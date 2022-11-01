package eulap.eb.payroll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.BiometricModel;

/**
 * The parser for Fortress biometric.

 *
 */
public class FortressParser implements PayrollParser{

	@Override
	public void init() {
	}

	@Override
	public int getBiometricModelId() {
		return BiometricModel.FORTRESS;
	}

	@Override
	public void parseData(Date dateFrom, Date dateTo, InputStream in, PayrollDataHandler dataHandler) throws IOException, ParseException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			int cnt = 0;
			while ((line = reader.readLine()) != null) {
				if (cnt > 0) { // Ignore the header.
					String arr[] = line.split("\\s+");
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = formatter.parse(arr[6] + " " + arr[7]);
					Date dtNoTime = DateUtil.removeTimeFromDate(date);
					if ((dtNoTime.equals(dateFrom) || dtNoTime.after(dateFrom)) && 
							(dtNoTime.equals(dateTo) || dtNoTime.before(dateTo))) {
						dtNoTime = null;
						Integer biometricId = Integer.parseInt(arr[2]);
						String number = arr[0];
						String employeeName = arr[3];
						EmployeeDtr dtr = EmployeeDtr.getInstanceOf(number, biometricId, employeeName, date);
						dataHandler.handleParsedData(dtr);
						date = null;
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
}
