package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphalistDetail;

/**
 * A class that handles the Quarterly Alphalist of Payees Taxes report data.

 */
public class QAPDetailsD2Dto implements AlphalistDetail{
	public static String scheduleNumber = "D2";
	public static String fTypeCode = "1601EQ";
	private Integer seqNum;
	private String tin;
	private String branchCode;
	private String registeredName;
	private String lastName;
	private String firstName;
	private String middleName;
	private String retrnPeriod;
	private String atcCode;
	private String taxBase;

	@Override
	public String convertDetailToCSV() {
		String dATRegName = "";
		if (!registeredName.isEmpty()) {
			dATRegName = "\""+registeredName+"\"";
		}

		String dATLastName = "";
		if (!lastName.isEmpty()) {
			dATLastName = "\""+lastName+"\"";
		}

		String dATFirstName = "";
		if (!firstName.isEmpty()) {
			dATFirstName ="\""+firstName+"\"";
		}

		String dATMiddleName = "";
		if (!middleName.isEmpty()) {
			dATMiddleName = "\""+middleName+"\"";
		}
		return scheduleNumber+","+fTypeCode+","+seqNum +","+tin+","+branchCode+","+dATRegName+","+dATLastName+","+dATFirstName+","+dATMiddleName+","+retrnPeriod
				+","+atcCode+","+taxBase;
	}

	public Integer getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(Integer seqNum) {
		this.seqNum = seqNum;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getRegisteredName() {
		return registeredName;
	}

	public void setRegisteredName(String registeredName) {
		this.registeredName = registeredName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getRetrnPeriod() {
		return retrnPeriod;
	}

	public void setRetrnPeriod(String retrnPeriod) {
		this.retrnPeriod = retrnPeriod;
	}

	public String getAtcCode() {
		return atcCode;
	}

	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}

	public String getTaxBase() {
		return taxBase;
	}

	public void setTaxBase(String taxBase) {
		this.taxBase = taxBase;
	}

	@Override
	public String toString() {
		return "QAPDetailsD2Dto [seqNum=" + seqNum + ", tin=" + tin + ", branchCode=" + branchCode + ", registeredName="
				+ registeredName + ", lastName=" + lastName + ", firstName=" + firstName + ", middleName=" + middleName
				+ ", retrnPeriod=" + retrnPeriod + ", atcCode=" + atcCode + ", taxBase=" + taxBase + "]";
	}
}
