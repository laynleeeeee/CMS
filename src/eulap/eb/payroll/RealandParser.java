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

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.BiometricModel;

/**
 * The parser for Realand biometric.

 *
 */
public class RealandParser implements PayrollParser{

	@Override
	public void init() {
	}

	@Override
	public int getBiometricModelId() {
		return BiometricModel.REALAND;
	}

	@Override
	public void parseData(Date dateFrom, Date dateTo, InputStream in, PayrollDataHandler dataHandler) throws IOException, ParseException {
		try {
			List<Date> dates = new ArrayList<>();

			Workbook workBook = WorkbookFactory.create(in);
			Sheet sheet = workBook.getSheetAt(0); // The first tab.
			Iterator<Row> rowIterator = sheet.iterator();
			String cellValue = null;
			String number = null;
			Integer biometricId = null;
			String employeeName = null;
			Date date = null;
			int colIndexDateDiff = 4;
			String tmpTimes[] = {};
			int hour = 0;
			int minute = 0;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					cellValue = ExcelParserUtil.getCellValue(cell);
					if (row.getRowNum() == 0 && cell.getColumnIndex() >= colIndexDateDiff && !cellValue.trim().isEmpty()) {
						date = DateUtil.parseDate(cellValue.split(" ")[0]);
						if (date == null) {
							date = DateUtil.parseDate(cellValue.split(" ")[0] + "/" + processYear(dateFrom, dateTo, cellValue.split(" ")[0]));
						}
						dates.add(date);
					} else if (row.getRowNum() >= 1){
						if (cell.getColumnIndex() == 1) { // User / Biometric ID
							biometricId = Integer.parseInt(cellValue);
						} else if (cell.getColumnIndex() == 2) { // Employee Name
							employeeName = cellValue;
						} else if (cell.getColumnIndex() >= colIndexDateDiff) { // Date & Time
							tmpTimes = cellValue.trim().split("\\s+");
							if (tmpTimes.length > 1) {
								for (int i=0; i<tmpTimes.length; i++) {
									hour = Integer.parseInt(tmpTimes[i].split(":")[0]);
									minute = Integer.parseInt(tmpTimes[i].split(":")[1]);
									date = DateUtil.setTimeOfDate(dates.get(cell.getColumnIndex()-colIndexDateDiff), hour, minute, 0);
									EmployeeDtr dtr = EmployeeDtr.getInstanceOf(number, biometricId, employeeName, date);
									dataHandler.handleParsedData(dtr);
								}
							}
							tmpTimes = null;
						}
					}
				}
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	private int processYear(Date dateFrom, Date dateTo, String strMonthDay) {
		int yearFrom = DateUtil.getYear(dateFrom);
		int yearTo = DateUtil.getYear(dateTo);
		Date tmpDate = DateUtil.parseDate(strMonthDay + "/" + yearTo);
		if (yearFrom == yearTo || tmpDate.after(dateTo)) {
			return yearFrom;
		}
		return yearTo;
	}


	public static void main (String args[]) {
		File file = new File ("../CMS/src/eulap/eb/payroll/gvch_dtr_edited.xls");
		RealandParser parser = new RealandParser();
		SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate;
		try {
			startDate = sdFormat.parse("01/01/2017");
			Date endDate = sdFormat.parse("05/30/2018");
			PayrollDataHandler dataHandler = new PayrollDataHandler();
			InputStream is = new FileInputStream(file);
			parser.parseData(startDate, endDate, is, dataHandler);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
}
