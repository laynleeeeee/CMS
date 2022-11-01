package eulap.eb.payroll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the data formatting.
 * 

 *
 */
public class PayrollDataHandler {
	private Map<Integer, List<EmployeeDtr>> bioId2Dtrs = new HashMap<Integer, List<EmployeeDtr>>();

	/**
	 * This will be called by the parser.
	 * @param employeeDtr
	 */
	public void handleParsedData(EmployeeDtr employeeDtr) {
		if (employeeDtr.getDate() == null)
			return;
		Integer keyId = employeeDtr.getBiometricId() != null ? employeeDtr.getBiometricId() : employeeDtr.getEmployeeId();
		List<EmployeeDtr> dtrs = bioId2Dtrs.get(keyId);
		if (dtrs == null) {
			dtrs = new ArrayList<EmployeeDtr>();
		}
		dtrs.add(employeeDtr);
		bioId2Dtrs.put(keyId, dtrs);
	}

	public Map<Integer, List<EmployeeDtr>> getParseData () {
		return bioId2Dtrs;
	}
}
