package eulap.eb.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.csvreader.CsvReader;

import eulap.common.util.DateUtil;
import eulap.common.util.TimeFormatUtil;

/**
 * The parser for Eulap biometric for sueno project

 *
 */
public class EulapV2Parser extends EulapParser {
	private final static String FORMAT_12_HR = "MM/dd/yyyy hh:mm:ss aa";
	private final static String FORMAT_24_HR = "MM/dd/yyyy HH:mm";
	private final static int MAX_COL = 7;

	@Override
	public void parseData(Date dateFrom, Date dateTo, InputStream in, PayrollDataHandler dataHandler) throws IOException, ParseException {
		CsvReader reader = null;
		dateTo = DateUtil.addDaysToDate(dateTo, 1);
		try {
			reader = new CsvReader(in, Charset.defaultCharset());
			int cnt = 0;
			while (reader.readRecord()) {
				if (cnt > 0) { // Ignore the header.
					boolean hasTimeIn = true;
					boolean hasTimeOut = true;
					String arr[] = reader.getRawRecord().split(",");
					Integer colCount = arr.length;

					if (colCount < MAX_COL || arr[5].trim().isEmpty() || arr[6].trim().isEmpty()) {
						hasTimeOut = false;
					}
		
					Date timeOut =   hasTimeOut ? processLogDate(arr[5], arr[6]) : null; // set end time to null

					if (colCount <= 4) {
						hasTimeIn = false;
					} else {
						hasTimeIn = !arr[3].trim().isEmpty() && !arr[4].trim().isEmpty();
					}

					Date timeIn = hasTimeIn ? processLogDate(arr[3], arr[4]) : null; // set initial time to null

					Date dtNoTime = null;
					if(timeIn != null) {
						dtNoTime = DateUtil.removeTimeFromDate(timeIn);
					}

					if (timeOut != null) {
						dtNoTime = DateUtil.removeTimeFromDate(timeOut);
					}

					if (timeIn == null && timeOut == null) {
						continue;
					}
					if ((dtNoTime.equals(dateFrom) || dtNoTime.after(dateFrom)) && 
							(dtNoTime.equals(dateTo) || dtNoTime.before(dateTo))) {
						dtNoTime = null;
						Integer biometricId = Integer.parseInt(arr[1]);
						String number = arr[0];
						String employeeName = arr[2];
						EmployeeDtr eeDtr = null;
						if (timeIn != null) {
							eeDtr = EmployeeDtr.getInstanceOf(number, biometricId, employeeName, timeIn);
							eeDtr.setRowNum(cnt);
							dataHandler.handleParsedData(eeDtr);
							timeIn = null;
						}
						if (timeOut != null) {
							eeDtr = EmployeeDtr.getInstanceOf(number, biometricId, employeeName, timeOut);
							eeDtr.setRowNum(cnt);
							dataHandler.handleParsedData(eeDtr);
							timeOut = null;
						}
					} else {
						continue;
					}
				}
				cnt++;
			}
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
	}

	private Date processLogDate(String strDate, String strTime) throws IOException, ParseException {
		SimpleDateFormat formatter = initDateFormatter(strTime);
		return formatter.parse(strDate + " " + strTime);
	}

	private SimpleDateFormat initDateFormatter(String toBeEvalTime) throws IOException  {
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_24_HR);
		if(!TimeFormatUtil.isMilitaryTime(toBeEvalTime)) {
			formatter = new SimpleDateFormat(FORMAT_12_HR);
		}
		return formatter;
	}

	public static void main (String args[]) {
		File file = new File ("../Downloads/timeSheetTemplate1.csv");
		EulapV2Parser parser = new EulapV2Parser();
		SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate;
		try {
			startDate = sdFormat.parse("11/26/2019");
			Date endDate = sdFormat.parse("12/10/2019");
			PayrollDataHandler dataHandler = new PayrollDataHandler();
			InputStream is = new FileInputStream(file);
			parser.parseData(startDate, endDate, is, dataHandler);
			System.out.println(dataHandler.getParseData());
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
}
