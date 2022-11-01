package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphalistDetail;

/**
 * A class that handles the Quarterly Alphalist of Payees Taxes Final report data.

 */
public class QAPDetailsD3DtoFinal implements AlphalistDetail{
	public static String scheduleNumber = "D3";
	public static String fTypeCode = "1601FQ";
	private String tin;
	private String branchCode;
	private String registeredName;
	private String lastName;
	private String firstName;
	private String middleName;
	private String retrnPeriod;
	private Integer seqNum;
	private String statusCode;
	private String atcCode;
	private String incomePayment;

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
		return scheduleNumber+","+fTypeCode+","+tin+","+branchCode+","+dATRegName+","+dATLastName+","+dATFirstName+","+dATMiddleName+","+retrnPeriod
				+","+seqNum +","+statusCode+","+atcCode+","+incomePayment;
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

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getIncomePayment() {
		return incomePayment;
	}

	public void setIncomePayment(String incomePayment) {
		this.incomePayment = incomePayment;
	}

	@Override
	public String toString() {
		return "QAPDetailsD3DtoFinal [tin=" + tin + ", branchCode=" + branchCode + ", registeredName=" + registeredName
				+ ", lastName=" + lastName + ", firstName=" + firstName + ", middleName=" + middleName
				+ ", retrnPeriod=" + retrnPeriod + ", seqNum=" + seqNum + ", statusCode=" + statusCode + ", atcCode="
				+ atcCode + ", incomePayment=" + incomePayment + "]";
	}
}
