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
import eulap.eb.domain.hibernate.BiometricModel;

/**
 * The parser for Eulap biometric.

 *
 */
public class EulapParser implements PayrollParser{
	private final static String FORMAT_12_HR = "MM/dd/yyyy hh:mm:ss aa";
	private final static String FORMAT_24_HR = "MM/dd/yyyy HH:mm";
	private final static Integer ROW_COUNT = 6;

	@Override
	public void init() {
	}

	@Override
	public int getBiometricModelId() {
		return BiometricModel.EULAP;
	}

	@Override
	public void parseData(Date dateFrom, Date dateTo, InputStream in, PayrollDataHandler dataHandler) throws IOException, ParseException {
		CsvReader reader = null;
		try {
			reader = new CsvReader(in, Charset.defaultCharset());
			int cnt = 0;
			SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_24_HR);
			boolean isDifferentFormat = false;
			while (reader.readRecord()) {
				if (cnt > 0) { // Ignore the header.
					String arr[] = reader.getRawRecord().split(",");
					Integer rowCount = arr.length;
					if(rowCount.equals(ROW_COUNT)) {
						if(!isDifferentFormat){
							formatter = initDateFormatter(arr[4]);
							isDifferentFormat = true;
						}
						Date timeIn = formatter.parse(arr[3] + " " + arr[4]);
						Date timeOut = formatter.parse(arr[3] + " " + arr[5]);
						Date dtNoTime = DateUtil.removeTimeFromDate(timeIn);
						if ((dtNoTime.equals(dateFrom) || dtNoTime.after(dateFrom)) && 
								(dtNoTime.equals(dateTo) || dtNoTime.before(dateTo))) {
							dtNoTime = null;
							Integer biometricId = Integer.parseInt(arr[1]);
							String number = arr[0];
							String employeeName = arr[2];
							dataHandler.handleParsedData(EmployeeDtr.getInstanceOf(number, biometricId, employeeName, timeIn));
							dataHandler.handleParsedData(EmployeeDtr.getInstanceOf(number, biometricId, employeeName, timeOut));
							timeIn = null;
						} else {
							continue;
						}
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

	private SimpleDateFormat initDateFormatter(String toBeEvalTime) throws IOException  {
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_24_HR);
		if(!TimeFormatUtil.isMilitaryTime(toBeEvalTime)) {
			formatter = new SimpleDateFormat(FORMAT_12_HR);
		}
		return formatter;
	}

	public static void main (String args[]) {
		File file = new File ("/home/joven/Downloads/downloadTimeSheetTemplate");
		EulapParser parser = new EulapParser();
		SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate;
		try {
			startDate = sdFormat.parse("03/01/2018");
			Date endDate = sdFormat.parse("03/31/2018");
			PayrollDataHandler dataHandler = new PayrollDataHandler();
			InputStream is = new FileInputStream(file);
			parser.parseData(startDate, endDate, is, dataHandler);
			System.out.println("");
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
}
