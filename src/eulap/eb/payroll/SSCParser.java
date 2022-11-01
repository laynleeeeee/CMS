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
 * The parser for SSC Biometric.

 *
 */
public class SSCParser implements PayrollParser{
	private static final String DATE_FORMAT = "MM/dd/yyyy";

	@Override
	public void init() {
	}

	@Override
	public int getBiometricModelId() {
		return BiometricModel.SSC;
	}

	@Override
	public void parseData(Date dateFrom, Date dateTo, InputStream in, PayrollDataHandler dataHandler) throws IOException, ParseException {
		try {
			Workbook workBook = WorkbookFactory.create(in);
			Sheet sheet = workBook.getSheetAt(0); // The first tab.
			Iterator<Row> rowIterator = sheet.iterator();
			String cellValue = null;
			Date dtrDate = null;
			String number = null;
			Integer biometricId = null;
			String employeeName = null;
			List<String> timeList = null;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getRowNum() == 0) {
					continue;
				}
				Iterator<Cell> cellIterator = row.cellIterator();
				timeList = new ArrayList<>();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					cellValue = ExcelParserUtil.getCellValue(cell);
					if (cellValue.trim().isEmpty()) {
						continue;
					}
					if (cell.getColumnIndex() == 0) {
						dtrDate = ExcelParserUtil.convStr2Date(cellValue, DATE_FORMAT);
					} else if (cell.getColumnIndex() == 1) {
						biometricId = Integer.parseInt(cellValue);
					} else if (cell.getColumnIndex() == 3) {
						employeeName = cellValue;
					} else if (cell.getColumnIndex() >= 5) {
						if (cellValue.trim().isEmpty()) {
							continue;
						}
						timeList.add(cellValue);
					}
				}
				if (!timeList.isEmpty()) {
					int size = timeList.size();
					String baseTime = "";
					int cnt = 0;
					for (String tl : timeList) {
						if (size > 1) {
							baseTime = timeList.get(0);
						} 
						String timeArr[] = tl.split(":");
						int hour = Integer.valueOf(timeArr[0]);
						int minute = Integer.valueOf(timeArr[1]);
						if (cnt >= 1) {
							String prevTime[] = baseTime.split(":");
							int prevHour = Integer.valueOf(prevTime[0]);
							if (prevHour > hour) {
								dtrDate = DateUtil.addDaysToDate(dtrDate, 1);
							} else if (prevHour == hour){
								int prevMinute = Integer.valueOf(prevTime[1]);
								if (prevMinute > minute) {
									dtrDate = DateUtil.addDaysToDate(dtrDate, 1);
								}
							}
						}
						Date date = DateUtil.setTimeOfDate(dtrDate, hour, minute, 0);
						EmployeeDtr dtr = EmployeeDtr.getInstanceOf(number, biometricId, employeeName, date);
						dtr.setRowNum(row.getRowNum());
						dataHandler.handleParsedData(dtr);
						cnt++;
					}
				}
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	public static void main (String args[]) {
		File file = new File ("../CMS/src/eulap/eb/payroll/SSCReport2.xls");
		SSCParser parser = new SSCParser();
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate;
		try {
			startDate = sdFormat.parse("2019-01-01");
			Date endDate = sdFormat.parse("2019-04-31");
			PayrollDataHandler dataHandler = new PayrollDataHandler();
			InputStream is = new FileInputStream(file);
			parser.parseData(startDate, endDate, is, dataHandler);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
