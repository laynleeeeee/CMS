package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;

/**
 * A class that handles the header for Quarterly Alphalist of Payees Final report data.

 */

public class QAPHeaderDtoFinal implements AlphalistHeader{
	private static String alphaType = "HQAP";
	private static String fTypeCode = "H1601FQ";
	private final String tin;
	private final String branchCode;
	private final String registeredName;
	private final String returnPeriod;
	private static String rdoCode = "111";
	private final String fileName;
	private List<AlphalistSchedule> controls;

	private QAPHeaderDtoFinal (String tin, String branchCode, String registeredName, String returnPeriod, String fileName, List<AlphalistSchedule> controls) {
		this.tin = tin;
		this.branchCode = branchCode;
		this.registeredName = registeredName;
		this.returnPeriod = returnPeriod;
		this.fileName = fileName;
		this.controls = controls;
	}

	public static QAPHeaderDtoFinal getInstance (String tin, String branchCode, String registeredName, String returnPeriod, String fileName, List<AlphalistSchedule> controls) {
		return new QAPHeaderDtoFinal(tin, branchCode, registeredName, returnPeriod, fileName, controls);
	}

	@Override
	public String convertHeaderToCSV() {
		return alphaType+ ","+ fTypeCode+ ","+ tin+ ","+ branchCode+ ","+registeredName+ ","+ returnPeriod+ ","+rdoCode;
	}

	public String getTin() {
		return tin;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public String getRegisteredName() {
		return registeredName;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public List<AlphalistSchedule> getSchedules() {
		return controls;
	}

	@Override
	public String toString() {
		return "QAPHeaderDto [tin=" + tin + ", branchCode=" + branchCode + ", registeredName=" + registeredName
				+ ", returnPeriod=" + returnPeriod + ", fileName=" + fileName + ", controls=" + controls + "]";
	}
}

