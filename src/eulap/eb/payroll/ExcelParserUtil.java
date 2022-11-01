package eulap.eb.payroll;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Common functions for parsing excel biometric.

 *
 */
public class ExcelParserUtil {

	public static class DateAndIndex {
		private Integer index;
		private Date date;

		public DateAndIndex(Integer index, Date date) {
			this.index = index;
			this.date = date;
		}

		public Integer getIndex() {
			return index;
		}

		public Date getDate() {
			return date;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DateAndIndex [index=").append(index)
					.append(", date=").append(date).append("]");
			return builder.toString();
		}
	}

	public static Date convStr2Date(String strDate) throws ParseException {
		return  convStr2Date(strDate, "MM.dd.yyyy");
	}

	/**
	 * Convert the string date to date object with specific date format.
	 * @param strDate The date in string format.
	 * @param dateFormat The specific date format.
	 * @return The date object.
	 * @throws ParseException
	 */
	public static Date convStr2Date(String strDate, String dateFormat) throws ParseException {
		 DateFormat df = new SimpleDateFormat(dateFormat); 
		 return df.parse(strDate);
	}

	public static Date setDay(Date date, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		c.set(year,  month , day, 0, 0);
		return c.getTime();
	}

	public static Date setDay(Date date, int day) {
		return setDay(date, Calendar.MONTH, day);
	}

	public static String getCellValue(Cell cell) {
		String cellValue = "";
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				cellValue = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if(DateUtil.isCellDateFormatted(cell)) {
					cellValue = eulap.common.util
							.DateUtil.formatDate(cell.getDateCellValue());
				} else {
					Double numVal = cell.getNumericCellValue();
					BigDecimal bdNumeric = new BigDecimal(numVal.toString());
					cellValue = bdNumeric.toPlainString();
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				cellValue = cell.getBooleanCellValue() + "";
			case Cell.CELL_TYPE_BLANK:
				break;
		}
		return cellValue;
	}

	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		return month;
	}

}
