package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphalistDetail;

/**
 * A class that handles the Quarterly Alphalist of Payees Taxes Final report data.

 */
public class QAPDetailsD2DtoFinal implements AlphalistDetail{
	public static String scheduleNumber = "D2";
	public static String fTypeCode = "1601FQ";
	private String tin;
	private String branchCode;
	private String lastName;
	private String firstName;
	private String middleName;
	private String retrnPeriod;
	private Integer seqNum;
	private String atcCode;
	private String fringeBenifit;
	private String monetaryValue;
	private String actualAMTWTHLD;

	@Override
	public String convertDetailToCSV() {
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

		return scheduleNumber+","+fTypeCode+","+tin+","+branchCode+","+dATLastName+","+dATFirstName+","+dATMiddleName+","+retrnPeriod
				+","+seqNum +","+atcCode+","+fringeBenifit+","+monetaryValue+","+actualAMTWTHLD;
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

	public String getFringeBenifit() {
		return fringeBenifit;
	}

	public void setFringeBenifit(String fringeBenifit) {
		this.fringeBenifit = fringeBenifit;
	}

	public String getMonetaryValue() {
		return monetaryValue;
	}

	public void setMonetaryValue(String monetaryValue) {
		this.monetaryValue = monetaryValue;
	}

	public String getActualAMTWTHLD() {
		return actualAMTWTHLD;
	}

	public void setActualAMTWTHLD(String actualAMTWTHLD) {
		this.actualAMTWTHLD = actualAMTWTHLD;
	}

	@Override
	public String toString() {
		return "QAPDetailsD2DtoFinal [tin=" + tin + ", branchCode=" + branchCode + ", lastName=" + lastName
				+ ", firstName=" + firstName + ", middleName=" + middleName + ", retrnPeriod=" + retrnPeriod
				+ ", seqNum=" + seqNum + ", atcCode=" + atcCode + ", fringeBenifit=" + fringeBenifit
				+ ", monetaryValue=" + monetaryValue + ", actualAMTWTHLD=" + actualAMTWTHLD + "]";
	}
}
