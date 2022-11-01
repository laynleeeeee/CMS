package eulap.eb.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import eulap.eb.domain.hibernate.BiometricModel;
import eulap.eb.payroll.ExcelParserUtil.DateAndIndex;

/**
 * The parser for Generic biometric.

 *
 */
public class GenericParser implements PayrollParser{
	private static final String TIME_SEPARATOR = "-";
	private static final String CELL_SEPARATOR = ";";

	@Override
	public void init() {
	}

	@Override
	public int getBiometricModelId() {
		return BiometricModel.GENERIC;
	}

	@Override
	public void parseData(Date dateFrom, Date dateTo, InputStream in, PayrollDataHandler dataHandler) throws IOException, ParseException {
		try {
			Workbook workBook = WorkbookFactory.create(in);
			Sheet sheet = workBook.getSheetAt(0); // The first tab.
			Iterator<Row> rowIterator = sheet.iterator();
			String cellValue = null;
			String newLine = System.getProperty("line.separator");
			Date dtrDate = null;
			String number = null;
			Integer biometricId = null;
			String employeeName = null;
			List<DateAndIndex> dateAndIndex = new ArrayList<>();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				String rowValues = "";
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					cellValue = ExcelParserUtil.getCellValue(cell);
					if (row.getRowNum() == 0 && cell.getColumnIndex() == 0) { 
						// First row and column, contains the date range.
						String firstRow = cellValue.replace("GLog table ", "").split(" -- ")[0].trim();
						if (!firstRow.isEmpty()) {
							dtrDate = ExcelParserUtil.convStr2Date(firstRow);
						}
					} else if (row.getRowNum() >= 2){ // Discard the second header.
						if (row.getRowNum() == 2) { // Process the date header.
							if (cell.getColumnIndex() > 3 && dtrDate != null) { // Start of the date.
								Double dblNum = Double.parseDouble(cellValue);
								dtrDate = ExcelParserUtil.setDay(dtrDate, dblNum.intValue());
								boolean isWithinDate = (dtrDate.equals(dateFrom) || dtrDate.after(dateFrom)) && 
										(dtrDate.equals(dateTo) || dtrDate.before(dateTo));
								if (!isWithinDate) {
									continue;
								}
								dateAndIndex.add(new DateAndIndex(cell.getColumnIndex(), dtrDate));
							}
							continue;
						}
						if (cell.getColumnIndex() == 0) { // Get the biometric id.
							biometricId = Integer.valueOf(cellValue.trim());
						} if (cell.getColumnIndex() == 1) { // Get the employee name.
							employeeName = cellValue.trim();
						}
						String times = "";
						if (cellValue.contains(newLine)) { // Time
							String strTimes[] = cellValue.split(newLine);
							for (int i=0; i<strTimes.length; i++) {
								times += strTimes[i] + (i < strTimes.length - 1 ? TIME_SEPARATOR : "") ;
							}
						}
						rowValues += (times.isEmpty() ? cellValue.trim() + 
								(cellValue.trim().isEmpty() ? " " : "") : times) 
								+ CELL_SEPARATOR;
					}
				}
				if (!rowValues.trim().isEmpty() && !dateAndIndex.isEmpty()) {
					String strArr[] = rowValues.split(CELL_SEPARATOR);
					for(DateAndIndex di : dateAndIndex) {
						String strTimes[] = strArr[di.getIndex()].split(TIME_SEPARATOR);
						if (strTimes.length > 0) {
							for (int i=0; i<strTimes.length; i++) {
								if (!strTimes[i].trim().isEmpty()) {
									String timeArr[] = strTimes[i].split(":");
									int hour = Integer.valueOf(timeArr[0]);
									int minute = Integer.valueOf(timeArr[1]);
									Date date = eulap.common.util.DateUtil.setTimeOfDate(di.getDate(), hour, minute, 0);
									EmployeeDtr dtr = EmployeeDtr.getInstanceOf(number, biometricId, employeeName, date);
									dataHandler.handleParsedData(dtr);
								}
							}
						} 
					}
				}
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}



	public static void main (String args[]) {
		File file = new File ("../eb-fa/src/eulap/eb/payroll/Report_201607_002_revised.xls");
		GenericParser parser = new GenericParser();
		SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate;
		try {
			startDate = sdFormat.parse("07/01/2016");
			Date endDate = sdFormat.parse("07/31/2016");
			PayrollDataHandler dataHandler = new PayrollDataHandler();
			InputStream is = new FileInputStream(file);
			parser.parseData(startDate, endDate, is, dataHandler);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
