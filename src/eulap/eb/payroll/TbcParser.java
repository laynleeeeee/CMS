package eulap.eb.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import eulap.eb.domain.hibernate.BiometricModel;

/**
 * The parser for Generic biometric.

 *
 */
public class TbcParser implements PayrollParser{

	@Override
	public void init() {
	}

	@Override
	public int getBiometricModelId() {
		return BiometricModel.TBC;
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
			int month = 0;
			int startRow = 4;
			boolean isUserRow = false;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getRowNum() != 2 && row.getRowNum() < startRow) {
					continue;
				}
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					cellValue = ExcelParserUtil.getCellValue(cell);
					if (row.getRowNum() == 2 && cell.getColumnIndex() == 25) {
						String strDateRow = cellValue.replace("Attendance date:", "").split(" ~")[0].trim();;
						dtrDate = ExcelParserUtil.convStr2Date(strDateRow);
						month = ExcelParserUtil.getMonth(dtrDate);
						break;
					}
					if (cellValue.contains("UserID:")) {
						isUserRow = true;
					}
					if (isUserRow) {
						if (cell.getColumnIndex() == 3) { // Biometric ID
							biometricId = Integer.valueOf(cellValue.trim());
						} else if (cell.getColumnIndex() == 11) { // Employee Name
							employeeName = cellValue.trim();
						}
					} else {
						if (row.getRowNum() != 2) { // Time
							dtrDate = ExcelParserUtil.setDay(dtrDate, month, cell.getColumnIndex());
							boolean isWithinDate = (dtrDate.equals(dateFrom) || dtrDate.after(dateFrom)) && 
									(dtrDate.equals(dateTo) || dtrDate.before(dateTo));
							if (!isWithinDate) {
								continue;
							}

							String strTimes[] = cellValue.split(newLine);
							for (int i=0; i<strTimes.length; i++) {
								if (strTimes[i].contains(":")) {
									String timeArr[] = strTimes[i].split(":");
									if (timeArr.length > 0 && !timeArr[0].trim().isEmpty()) {
										int hour = Integer.valueOf(timeArr[0]);
										int minute = Integer.valueOf(timeArr[1]);
										Date date = eulap.common.util.DateUtil.setTimeOfDate(dtrDate, hour, minute, 0);
										EmployeeDtr dtr = EmployeeDtr.getInstanceOf(number, biometricId, employeeName, date);
										System.out.println(dtr);
										dataHandler.handleParsedData(dtr);
									}
								}
							}
						}
					}
				}
				isUserRow = false;
			}

		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	public static void main (String args[]) {
		File file = new File ("../eb-payroll/src/eulap/eb/payroll/AttendRecord-2.xls");
		TbcParser parser = new TbcParser();
		SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate;
		try {
			startDate = sdFormat.parse("11/16/2016");
			Date endDate = sdFormat.parse("11/30/2016");
			PayrollDataHandler dataHandler = new PayrollDataHandler();
			InputStream is = new FileInputStream(file);
			parser.parseData(startDate, endDate, is, dataHandler);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
