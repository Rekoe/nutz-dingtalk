package com.rekoe.common.large.excell.to.bean.ta.utilities.factory;

import java.io.File;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.nutz.lang.Files;

import com.rekoe.common.large.excell.to.bean.ta.utilities.StreamingReader;

public class Test {

	public static void main(String[] args) {
		File file = Files.findFile("/Users/kouxian/Desktop/20180904.jason.xlsx");
		Workbook wb = StreamingReader.builder().rowCacheSize(5000).bufferSize(4096).open(file);
		int sheetNumber = wb.getNumberOfSheets();
		System.out.println(sheetNumber);
		for (int i = 0; i < sheetNumber; i++) {
			//Sheet sheet = wb.getSheetAt(i);
			//String sheetName = sheet.getSheetName();
			//System.out.println(sheetName);
			for (Sheet sheet : wb){
				for (Row row : sheet) {
					int rowNumber = row.getRowNum();
					if(rowNumber==0) {
						int cellNumber = row.getPhysicalNumberOfCells();
						for(int j=0;j<cellNumber;j++) {
							Cell cell = row.getCell(j);
							System.out.println(cell.getStringCellValue());
						}
						System.out.println();
						break;
					}
				}
				}
			/*Row row = sheet.getRow(0);
			int lastCellNumber = row.getLastCellNum();
			StringBuffer table = new StringBuffer();
			table.append("CREATE TABLE `").append(sheetName).append("` (").append("\n");
			for (int j = 0; j <= lastCellNumber; j++) {
				Cell cell = row.getCell(j);
				table.append("`").append(cell.getStringCellValue()).append("` varchar(225) DEFAULT NULL").append(',').append("\n");
			}
			table.append("PRIMARY KEY (`user_pseudo_id`)").append("\n");
			table.append(" KEY `").append(sheetName).append("_index` (`user_first_touch_timestamp`)").append("\n");
			table.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8").append("\n");
			System.out.println(table.toString());*/
		}
	}

}
