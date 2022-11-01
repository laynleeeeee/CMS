package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;

/**
 * A class that handles the header for Alpha List Payees withholding taxes expanded 1604-E .

 */

public class AnnualAlphalistWTEHeaderDto implements AlphalistHeader{
	private static  String fTypeCode = "H1604E";
	private final String tin;
	private final String branchCode;
	private final String retrnPeriod;
	private final String fileName;
	private List<AlphalistSchedule> controls;
	private AnnualAlphalistWTEHeaderDto (String tin, String branchCode, String retrnPeriod, String fileName, List<AlphalistSchedule> controls) {
		this.tin = tin;
		this.branchCode = branchCode;
		this.retrnPeriod = retrnPeriod;
		this.fileName = fileName;
		this.controls = controls;
	}

	public static AnnualAlphalistWTEHeaderDto getInstance (String tin, String branchCode, String retrnPeriod, String fileName, List<AlphalistSchedule> controls) {
		return new AnnualAlphalistWTEHeaderDto(tin, branchCode, retrnPeriod, fileName, controls);
	}

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
		return controls;
	}
}

