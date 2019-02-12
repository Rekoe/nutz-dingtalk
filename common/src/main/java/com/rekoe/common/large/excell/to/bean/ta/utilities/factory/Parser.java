package com.rekoe.common.large.excell.to.bean.ta.utilities.factory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.rekoe.common.large.excell.to.bean.ta.utilities.StreamingReader;
import com.rekoe.common.large.excell.to.bean.ta.utilities.annotations.ExcelBean;
import com.rekoe.common.large.excell.to.bean.ta.utilities.annotations.ExcelColumnHeader;
import com.rekoe.common.large.excell.to.bean.ta.utilities.annotations.ExcelColumnIndex;
import com.rekoe.common.large.excell.to.bean.ta.utilities.bean.ResponseBean;
import com.rekoe.common.large.excell.to.bean.ta.utilities.enums.CoreMessages;
import com.rekoe.common.large.excell.to.bean.ta.utilities.enums.ExcelFactoryType;

/**
 * Builds the excel factory on the specified class, The specified class must
 * have annotation type ExcelBean.<br>
 * Dependencies are latest version of apache POI<br>
 */
/**
 * 
 * @author Taleh Algayev Jun 1, 2018
 * @param <T>
 */
public class Parser<T extends ResponseBean> {
	final static Log log = Logs.get();
	StringBuilder builder = new StringBuilder();

	/**
	 * Will hold the reference to the annotated class which is being populated.
	 **/
	private Class<T> clazz;

	/**
	 * Excel factory type decides whether extraction is going to be column based or
	 * index based.
	 */
	private ExcelFactoryType excelFactoryType;

	/**
	 * Only applicable for column based extraction.<br>
	 * If set to true then first row in the excel sheet will be neglected.
	 **/
	private boolean skipHeader;

	/** Will store the reference to all the fields of the annotated class. **/
	private Map<String, Field> fieldsMap;

	/**
	 * Will stop the row processing whenever an empty row is encountered, be default
	 * it is set to true in the constructor.
	 **/
	private boolean breakAfterEmptyRow;

	// private XSSFWorkbook invoiceWorkbook;

	/**
	 * Initialize the excel parser.<br>
	 * This constructor will also save the annotated class fields in to a map for
	 * future use.
	 *
	 * @param clazz
	 * @param excelFactoryType
	 * @throws Exception
	 */
	public Parser(Class<T> clazz, ExcelFactoryType excelFactoryType, boolean breakAfterEmptyRow) throws Exception {

		this.clazz = clazz;
		this.excelFactoryType = excelFactoryType;
		this.breakAfterEmptyRow = breakAfterEmptyRow;

		/*
		 * Check whether class has ExcelBean annotation present, If not present then
		 * throw exception
		 */
		if (clazz.isAnnotationPresent(ExcelBean.class)) {

			/*
			 * Initialize the fields map as empty hash map, this will used to store the
			 * reference to the class fields
			 */
			this.fieldsMap = new HashMap<String, Field>();

			/* Get all declared fields for the annotated class */
			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {

				/*
				 * Based on excel factory type prepare the java reflection field map
				 */
				switch (this.excelFactoryType) {
				case COLUMN_INDEX_BASED_EXTRACTION:
					this.prepareColumnIndexBasedFieldMap(field);
					break;
				case COLUMN_NAME_BASED_EXTRACTION:
					this.prepareColumnHeaderBasedFieldMap(field);
					break;
				}
			}

		} else {
			throw new Exception("Provided class is not annotated with ExcelBean");
		}
	}

	/**
	 * Preapares the field Map based on the column index
	 * 
	 * @param field
	 */
	private void prepareColumnIndexBasedFieldMap(Field field) {
		if (field.isAnnotationPresent(ExcelColumnIndex.class)) {

			/*
			 * Make the field accessible and save it into the fields map
			 */
			field.setAccessible(true);
			ExcelColumnIndex column = field.getAnnotation(ExcelColumnIndex.class);
			String key = String.valueOf(column.columnIndex());
			this.fieldsMap.put(key, field);

		}
	}

	/**
	 * Prepares the field Map based on the column header
	 * 
	 * @param field
	 */
	private void prepareColumnHeaderBasedFieldMap(Field field) {
		if (field.isAnnotationPresent(ExcelColumnHeader.class)) {
			field.setAccessible(true);
			ExcelColumnHeader column = field.getAnnotation(ExcelColumnHeader.class);
			String key = column.columnHeader();
			this.fieldsMap.put(key, field);
		}
	}

	/**
	 * Returns the dataType specified in the field
	 * 
	 * @param field
	 * @return String dataType
	 */
	private String getDataTypeFor(Field field) {
		String dataType = null;
		switch (this.excelFactoryType) {
		case COLUMN_INDEX_BASED_EXTRACTION:
			ExcelColumnIndex indexColumn = field.getAnnotation(ExcelColumnIndex.class);
			dataType = indexColumn.dataType();
			break;
		case COLUMN_NAME_BASED_EXTRACTION:
			ExcelColumnHeader headerColumn = field.getAnnotation(ExcelColumnHeader.class);
			dataType = headerColumn.dataType();
			break;
		}
		return dataType;
	}

	/**
	 * Returns the default value specified in the field
	 * 
	 * @param field
	 * @return String dataType
	 */
	private String getDefaultValueFor(Field field) {
		String defaultValue = null;
		switch (this.excelFactoryType) {
		case COLUMN_INDEX_BASED_EXTRACTION:
			ExcelColumnIndex indexColumn = field.getAnnotation(ExcelColumnIndex.class);
			defaultValue = indexColumn.defaultValue();
			break;
		case COLUMN_NAME_BASED_EXTRACTION:
			ExcelColumnHeader headerColumn = field.getAnnotation(ExcelColumnHeader.class);
			defaultValue = headerColumn.defaultValue();
			break;
		}
		return defaultValue;
	}

	/**
	 * Reads and convert valid excel file into required format<br>
	 * Will only process the first sheet, split multiple sheets into multiple files
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws ParseException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public List<T> parse(String filePath) throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, ParseException {

		List<T> result = new ArrayList<T>();
		T errorBean = this.clazz.newInstance();
		try {
			InputStream is = new FileInputStream(filePath);
			Workbook workbook = StreamingReader.builder().rowCacheSize(1000) // number of rows to keep in memory (defaults to 10)
					.bufferSize(4096) // buffer size to use when reading InputStream to file (defaults to 1024)
					.open(is);
			int i = 0;
			for (Sheet sheet : workbook) {
				i++;
				if (i != 1) {
					errorBean.setErrorCode(CoreMessages.INVALID_EXCELL_FORM);
					result.add(errorBean);
					return result;
				}
				for (Row row : sheet) {
					if (Objects.nonNull(row)) {
						if (isEquals(excelFactoryType, ExcelFactoryType.COLUMN_INDEX_BASED_EXTRACTION))
							if (isEquals(row.getRowNum(), 0) && skipHeader)
								continue;
						// Process all non empty rows

						if (!isEmptyRow(row)) {
							T beanObj = this.getBeanForARow(row);
							beanObj.setRowId(row.getRowNum() + 1);
							result.add(beanObj);
						} else {
							// If empty row found and user has opted to break whenever empty
							// row encountered then break the loop
							if (this.breakAfterEmptyRow)
								break;
						}
					} else
						break;
				}
			}
			return result;
		} catch (NotOfficeXmlFileException e) {
			log.error("**** INVALID EXCELL FORM ******");
			e.printStackTrace();

			errorBean.setErrorCode(CoreMessages.INVALID_EXCELL_FORM);
			result.add(errorBean);
			return result;
		} catch (Exception e) {
			log.error("**** ERROR WHEN PARSE ******");
			e.printStackTrace();

			errorBean.setErrorCode(CoreMessages.PARSE_ERROR);
			result.add(errorBean);
			return result;
		} catch (Throwable e) {
			log.error("**** ERROR WHEN PARSE ******");
			e.printStackTrace();
			errorBean.setErrorCode(CoreMessages.PARSE_ERROR);
			result.add(errorBean);
			return result;
		}
	}

	/**
	 * Reads and convert valid excel file into required format<br>
	 * Will only process the first sheet, split multiple sheets into multiple files
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws ParseException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public List<T> parse(byte[] file) throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, ParseException {

		List<T> result = new ArrayList<T>();
		T errorBean = this.clazz.newInstance();
		int i = 0;
		try {
			InputStream is = new ByteArrayInputStream(file);
			Workbook workbook = StreamingReader.builder().rowCacheSize(5000).bufferSize(4096).open(is);
			for (Sheet sheet : workbook) {
				for (Row row : sheet) {
					i++;
					if (Objects.nonNull(row)) {
						if (isEquals(excelFactoryType, ExcelFactoryType.COLUMN_INDEX_BASED_EXTRACTION))
							if (isEquals(row.getRowNum(), 0) && skipHeader)
								continue;
						if (!isEmptyRow(row)) {
							T beanObj = this.getBeanForARow(row);
							beanObj.setRowId(row.getRowNum() + 1);
							result.add(beanObj);
						} else {
							if (this.breakAfterEmptyRow)
								break;
						}
					} else
						break;
				}
			}
			return result;
		} catch (NotOfficeXmlFileException e) {
			log.errorf("**** INVALID EXCELL FORM ****** row number %s", i);
			e.printStackTrace();
			errorBean.setErrorCode(CoreMessages.INVALID_EXCELL_FORM);
			result.add(errorBean);
			return result;
		} catch (Exception e) {
			log.errorf("**** ERROR WHEN PARSE ****** row number %s", i);
			e.printStackTrace();
			errorBean.setErrorCode(CoreMessages.PARSE_ERROR);
			result.add(errorBean);
			return result;
		} catch (Throwable e) {
			log.errorf("**** ERROR WHEN PARSE ****** row number %s", i);
			e.printStackTrace();
			errorBean.setErrorCode(CoreMessages.PARSE_ERROR);
			result.add(errorBean);
			return result;
		}
	}

	public boolean isEquals(Object o1, Object o2) {
		return Objects.equals(o1, o2);
	}

	public Parser() {
		super();
	}

	/**
	 * Fetches the cell details from the each row and sets its values based on the
	 * instance variable defined by the annotation
	 * 
	 * @param row
	 * @return Clazz object
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ParseException
	 * @throws IllegalArgumentException
	 */
	public T getBeanForARow(Row row) throws InstantiationException, IllegalAccessException, IllegalArgumentException, ParseException {

		final T classObj = this.clazz.newInstance();
		for (int i = 0; i <= row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			if (cell != null) {
				DataFormatter df = new DataFormatter();
				// String value = df.formatCellValue(cell);
				// cell.setCellType(CellType.STRING);
				String value = df.formatCellValue(cell) == null ? null : df.formatCellValue(cell);
				this.setCellValueBasedOnDesiredExcelFactoryType(classObj, value, i);
			} else
				this.setCellValueBasedOnDesiredExcelFactoryType(classObj, null, i);
		}
		return classObj;
	}

	/**
	 * Parse the cell values to their specified dataType and sets into the java
	 * class field
	 * 
	 * @param classObj
	 * @param columnValue
	 * @param columnIndex
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws ParseException
	 */
	private void setCellValueBasedOnDesiredExcelFactoryType(T classObj, String columnValue, int columnIndex) throws IllegalArgumentException, IllegalAccessException, ParseException {

		Field field = this.fieldsMap.get(String.valueOf(columnIndex));
		if (field != null) {
			// If column value is null or empty then try to put the default value
			if (columnValue == null || columnValue.trim().isEmpty()) {
				columnValue = this.getDefaultValueFor(field);
			}

			/**
			 * Based on the dataType Specified convert it to primitive value<br>
			 * But make sure that columnvalue is not null or empty
			 **/
			if (columnValue != null && !columnValue.trim().isEmpty()) {
				String dataType = this.getDataTypeFor(field);
				switch (dataType) {
				case "int":
					field.set(classObj, Integer.parseInt(columnValue));
					break;
				case "long":
					field.set(classObj, Long.parseLong(columnValue));
					break;
				case "bool":
					field.set(classObj, Boolean.parseBoolean(columnValue));
					break;
				case "double":
					Double data = Double.parseDouble(columnValue);
					field.set(classObj, data);
					break;
				case "date":
					field.set(classObj, this.dateParser(columnValue));
					break;
				default:
					field.set(classObj, columnValue);
					break;
				}
			}
		}

	}

	/**
	 * Parses the date columns in dd-MM-YYYY format. Customize the format in case.
	 * 
	 * @param value
	 * @return
	 */
	private Date dateParser(String value) {
		if (value != null && !value.isEmpty()) {
			String[] formats = new String[] { "dd-MM-yyyy" };
			java.util.Date date;
			try {
				date = DateUtils.parseDate(value, formats);
				return new Date(date.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		} else
			return null;
	}

	/**
	 * Checks whether an encountered row is empty or not.
	 * 
	 * @param row
	 * @return
	 */
	@SuppressWarnings("deprecation")
	boolean isEmptyRow(Row row) {
		boolean isEmptyRow = true;
		for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
			Cell cell = row.getCell(cellNum);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && org.apache.commons.lang3.StringUtils.isNotBlank(cell.toString())) {
				isEmptyRow = false;
			}
		}
		return isEmptyRow;
	}

	public boolean isBreakAfterEmptyRow() {
		return breakAfterEmptyRow;
	}

	public void setBreakAfterEmptyRow(boolean breakAfterEmptyRow) {
		this.breakAfterEmptyRow = breakAfterEmptyRow;
	}

	public boolean isSkipHeader() {
		return skipHeader;
	}

	public void setSkipHeader(boolean skipHeader) {
		this.skipHeader = skipHeader;
	}
}
