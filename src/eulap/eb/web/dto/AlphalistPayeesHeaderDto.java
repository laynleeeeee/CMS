package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.service.bir.AlphaListControl;
import eulap.eb.service.bir.AlphalistDetail;
import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;

/**
 * A class that handles the header for Alpha List Payees report data.

 */

public class AlphalistPayeesHeaderDto implements AlphalistHeader{
	private static  String fTypeCode = "H1604F";
	private final String tin;
	private final String branchCode;
	private final String retrnPeriod;
	private final String fileName;
	private List<AlphalistSchedule> schedule;
	private AlphalistPayeesHeaderDto (String tin, String branchCode, String retrnPeriod, String fileName, List<AlphalistSchedule> schedule) {
		this.tin = tin;
		this.branchCode = branchCode;
		this.retrnPeriod = retrnPeriod;
		this.fileName = fileName;
		this.schedule = schedule;
	}

	public static AlphalistPayeesHeaderDto getInstance (String tin, String branchCode, String retrnPeriod, String fileName, List<AlphalistSchedule> schedule) {
		return new AlphalistPayeesHeaderDto(tin, branchCode, retrnPeriod, fileName, schedule);
	}

	/**
	 *
	 * @return BIR 1604F Alphalist Payees Withholding Tax Details
	 */
	@Override
	public String convertHeaderToCSV() {
		return fTypeCode+"," +tin +"," + branchCode + ","+ retrnPeriod;
	}

	public String getTin() {
		return tin;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public String getRetrnPeriod() {
		return retrnPeriod;
	}

	@Override
	public String toString() {
		return "AlphaListPayeesHeaderDto [tin=" + tin + ", branchCode=" + branchCode + ", retrnPeriod=" + retrnPeriod
				+ "]";
	}

	@Override
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public List<AlphalistSchedule> getSchedules() {
		return schedule;
	}
}

