package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;

/**
 * A class that handles the header for Summary Alphalist of Withholding Taxes report data.

 */

public class SAWTHeaderDto implements AlphalistHeader{
	private static String alphaType = "HSAWT";
	private final String fTypeCode;
	private final String tin;
	private final String branchCode;
	private final String registeredName;
	private final String lastName;
	private final String firstName;
	private final String middleName;
	private final String returnPeriod;
	private static String rdoCode = "111";
	private final String fileName; 
	private List<AlphalistSchedule> controls;

	private SAWTHeaderDto (String fTypeCode, String tin, String branchCode, String registeredName, String lastName, String firstName, String middleName,
			String returnPeriod, String fileName, List<AlphalistSchedule> controls) {
		this.fTypeCode = fTypeCode;
		this.tin = tin; 
		this.branchCode = branchCode; 
		this.registeredName = registeredName;
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.returnPeriod = returnPeriod;
		this.fileName = fileName;
		this.controls = controls;
	}

	public static SAWTHeaderDto getInstance (String fTypeCode, String tin, String branchCode, String registeredName, String lastName, String firstName, String middleName,
			String returnPeriod, String fileName, List<AlphalistSchedule> controls) {
		return new SAWTHeaderDto(fTypeCode, tin, branchCode, registeredName, lastName, firstName, middleName, returnPeriod, fileName, controls);
	}

	@Override
	public String convertHeaderToCSV() {
		return alphaType+ ","+ fTypeCode+ ","+ tin+ ","+ branchCode+ ","+registeredName+ ","+lastName+ ","+firstName+ ","+middleName+ ","+ returnPeriod+ ","+rdoCode;
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

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
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
		return "SAWTHeaderDto [tin=" + tin + ", branchCode=" + branchCode + ", registeredName=" + registeredName
				+ ", lastName=" + lastName + ", firstName=" + firstName + ", middleName=" + middleName
				+ ", returnPeriod=" + returnPeriod + ", fileName=" + fileName + ", controls="
				+ controls + "]";
	}
}

