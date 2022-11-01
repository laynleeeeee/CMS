package eulap.eb.payroll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.BiometricModel;

/**
 * The parser for CHL.

 *
 */
public class ZkTeco628CAParser implements PayrollParser{
	@Override
	public void init() {
	}

	@Override
	public int getBiometricModelId() {
		return BiometricModel.ZKTECO628C;
	}

	@Override
	public void parseData(Date dateFrom, Date dateTo, InputStream in, PayrollDataHandler dataHandler) throws IOException, ParseException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = reader.readLine()) != null) {
				String arr[] = line.split("\\s+");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = formatter.parse(arr[1] + " " + arr[2]);
				Date dtNoTime = DateUtil.removeTimeFromDate(date);
				if ((dtNoTime.equals(dateFrom) || dtNoTime.after(dateFrom)) && 
						(dtNoTime.equals(dateTo) || dtNoTime.before(dateTo))) {
					dtNoTime = null;
					Integer biometricId = Integer.parseInt(arr[0]);
					EmployeeDtr dtr = EmployeeDtr.getInstanceOf(null, biometricId, null, date);
					dataHandler.handleParsedData(dtr);
					date = null;
				} else {
					continue;
				}
			}
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
	}

	public static void main (String args[]) {
		File file = new File ("../CMS/src/eulap/eb/payroll/bio.txt");
		ZkTeco628CAParser parser = new ZkTeco628CAParser();
		SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate;
		try {
			startDate = sdFormat.parse("01/01/2018");
			Date endDate = sdFormat.parse("02/28/2018");
			PayrollDataHandler dataHandler = new PayrollDataHandler();
			InputStream is = new FileInputStream(file);
			parser.parseData(startDate, endDate, is, dataHandler);
			System.out.println(dataHandler.getParseData());
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
