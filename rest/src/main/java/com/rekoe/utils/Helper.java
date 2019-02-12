package com.rekoe.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.nutz.integration.json4excel.J4E;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.repo.Base64;

public class Helper {

	private final static Log log = Logs.get();
	private final static DateFormat DF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static {
		DF_DATE_TIME.setTimeZone(TimeZone.getTimeZone("GMT+3"));
	}

	public static Date getTimeZoneDate() {
		return getCalendarByTimeZone("GMT+8").getTime();
	}

	public static String sarTimeFormat(Date time) {
		return Times.format(DF_DATE_TIME, time);
	}

	public static Date getTimeZoneDate(Date date) {
		return getCalendarByTimeZone("GMT+8", date).getTime();
	}

	public static Date getTimeZoneDateBySaudi(Date date) {
		return getCalendarByTimeZone("GMT+3", date).getTime();
	}

	public static int getWeekDay() {
		return getCalendarByTimeZone("GMT+8").get(Calendar.DAY_OF_WEEK);
	}

	public static Calendar getCalendarByTimeZone(String timeZone, Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		calendar.setTime(time);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return calendar2;
	}

	public static Calendar getCalendarByTimeZone(String timeZone) {
		Date date = new Date(System.currentTimeMillis());
		return getCalendarByTimeZone(timeZone, date);
	}

	public static Date getTimeZoneDate2() {
		Date date = new Date(System.currentTimeMillis()); // 2014-1-31 21:20:50
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		calendar.setTime(date);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return calendar2.getTime();
	}

	/**
	 * 格式January 9, 2018 at 8:06pm
	 * 
	 * @param str
	 * @return January 9, 2018 at 8:06pm
	 */
	public static Date str2Date(String str) {
		int len = str.length();
		String amOrPm = StringUtils.substring(str, len - 2, len);
		String tim = StringUtils.substring(str, 0, len - 2);
		String time = tim + " " + amOrPm.toUpperCase();
		System.out.println(time);
		if (log.isDebugEnabled()) {
			log.debugf("from %s to %s ", str, time);
		}
		try {
			return Times.parse("MMMM d, yyyy 'at' h:m a", time);
		} catch (ParseException e) {
			log.error(e);
		}
		return Times.now();
	}

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String day = Times.format("yyyy-MM-dd", cal.getTime());
		System.out.println(day);
	}

	public static String getImageBinary(byte[] bytes) {
		return Base64.encodeToString(bytes, false).trim();
	}

	public static String getImageBinary(InputStream is) {
		try {
			BufferedImage bi = ImageIO.read(is);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi, "jpg", baos);
			byte[] bytes = baos.toByteArray();
			return Base64.encodeToString(bytes, false).trim();
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	public static String getImageBinary(File file) {
		try {
			BufferedImage bi = ImageIO.read(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi, "jpg", baos);
			byte[] bytes = baos.toByteArray();
			return Base64.encodeToString(bytes, false).trim();
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	static void base64StringToImage(String base64String, File toFile) {
		try {
			byte[] bytes1 = Base64.decodeFast(base64String);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
			BufferedImage bi1 = ImageIO.read(bais);
			ImageIO.write(bi1, "jpg", toFile);
		} catch (IOException e) {
			log.error(e);
		}
	}

	public static long getUserId() {
		long uid = (long) SecurityUtils.getSubject().getPrincipal();
		return uid;
	}

	public static boolean isPermitted(String permission) {
		return SecurityUtils.getSubject().isPermitted(permission);
	}

	public static Date SaudiTime(String timeStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
		Date date = Times.parseq(dateFormat, timeStr);
		return date;
	}

	public static boolean isZh(String str) {
		if (StringUtils.isBlank(str)) {
			// return false;
		}
		char[] nameChars = str.toCharArray();
		boolean needTranslate = false;
		for (char c : nameChars) {
			if (Strings.isChineseCharacter(c)) {
				needTranslate = true;
				break;
			}
		}
		return needTranslate;
	}

	public static boolean isContainsNotOnlyZh(String str) {
		boolean isNotZh = false;
		char[] nameChars = str.toCharArray();
		boolean needTranslate = false;
		for (char c : nameChars) {
			if (Strings.isChineseCharacter(c)) {
				needTranslate = true;
			} else {
				isNotZh = true;
			}
		}
		return needTranslate && isNotZh;
	}

	public static boolean isFilterWorld(String world, List<String> filterWorlds) {
		for (String filter : filterWorlds) {
			if (StringUtils.contains(world, filter)) {
				return true;
			}
		}
		return false;
	}

	public static void func2(String[] skuarr, int i, String[][] sku, List<String[]> arr) {
		for (int j = 0; j < sku[i].length; j++) {
			if (i < sku.length - 1) {
				skuarr[i] = sku[i][j];
				func2(skuarr, i + 1, sku, arr);
			} else {
				skuarr[i] = sku[i][j];
				String abc[] = new String[skuarr.length];
				for (int k = 0; k < skuarr.length; k++) {
					abc[k] = skuarr[k];
				}
				arr.add(abc);
			}
		}
	}

	/**
	 * 检查供应商的编码
	 * 
	 * @param providerCode
	 * @return
	 */
	String patternStr = "([A-Z]+)([0-9]+)([A-Z]+)[0-9]+";

	private static Pattern pattern = Pattern.compile("([A-Z]+)([0-9]+)([A-Z]+)");

	public static boolean providerCodeCheck(String providerCode) {
		Matcher m = pattern.matcher(providerCode);
		while (m.find()) {
			if (m.groupCount() == 3) {
				return true;
			}
		}
		return false;
	}

	public static List<String> providerCode(String providerCode) {
		Matcher m = pattern.matcher(providerCode);
		List<String> list = new ArrayList<>();
		while (m.find()) {
			if (m.groupCount() == 3) {
				list.add(m.group(1));
				list.add(m.group(2));
				list.add(m.group(3));
				break;
			}
		}
		return list;
	}

	public static String getCellValue(Cell cell) {
		String result = "";
		if (Lang.isEmpty(cell)) {
			return "";
		}
		try {
			switch (cell.getCellTypeEnum()) {
			case BLANK:
				result = cell.getStringCellValue();
				break;
			case BOOLEAN:
				result = String.valueOf(cell.getBooleanCellValue());
				break;
			case ERROR:
				result = String.valueOf(cell.getErrorCellValue());
				break;
			case FORMULA:
				throw Lang.makeThrow("unsput FORMULA type row[%s],cell[%s]", cell.getRowIndex(), cell.getColumnIndex());
			case NUMERIC:
				if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
					result = sdf.format(date);
				} else
					result = NumberToTextConverter.toText(cell.getNumericCellValue());
				break;
			case STRING:
				result = cell.getRichStringCellValue().getString();
				break;
			default:
				result = cell.getStringCellValue();
				break;
			}
		} catch (Exception e) {
			log.error(e);
		}
		return result;
	}

	public static Image getImage(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		return ImageIO.read(url.openStream());
	}

	public void moreSave(List<?> saveList, File outFile) {
		XSSFWorkbook wb = new XSSFWorkbook();
		OutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
			J4E.toExcel(wb, out, saveList, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			Streams.safeClose(wb);
			Streams.safeClose(out);
		}
	}

	public static float m1(float f) {
		BigDecimal bg = new BigDecimal(f);
		return bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}
}
