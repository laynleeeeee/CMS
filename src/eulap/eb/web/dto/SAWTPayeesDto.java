package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphalistDetail;

/**
 * A class that handles the Summary Alphalist of Withholding Taxes report data.

 */
public class SAWTPayeesDto implements AlphalistDetail{
	public static String alphaType = "DSAWT";
	private String fTypeCode;
	private Integer seqNum;
	private String tin;
	private String branchCode;
	private String registeredName;
	private String lastName;
	private String firstName;
	private String middleName;
	private String retrnPeriod;
	private String natureOfIncomePayment;
	private String atcCode;
	private Double taxRate;
	private Double taxBase;
	private Double actualAmtWthld;
	private String strTaxRate;
	private String strTaxBase;
	private String strActualAmtWthld;


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
		return alphaType+","+fTypeCode+","+seqNum +","+tin+","+branchCode+","+dATRegName+","+dATLastName+","+dATFirstName+","+dATMiddleName+","+retrnPeriod
				+","+natureOfIncomePayment+","+atcCode+","+strTaxRate+","+strTaxBase+","+strActualAmtWthld;
	}

	public String getfTypeCode() {
		return fTypeCode;
	}

	public void setfTypeCode(String fTypeCode) {
		this.fTypeCode = fTypeCode;
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

	public String getNatureOfIncomePayment() {
		return natureOfIncomePayment;
	}

	public void setNatureOfIncomePayment(String natureOfIncomePayment) {
		this.natureOfIncomePayment = natureOfIncomePayment;
	}

	public String getAtcCode() {
		return atcCode;
	}

	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public Double getTaxBase() {
		return taxBase;
	}

	public void setTaxBase(Double taxBase) {
		this.taxBase = taxBase;
	}

	public Double getActualAmtWthld() {
		return actualAmtWthld;
	}

	public void setActualAmtWthld(Double actualAmtWthld) {
		this.actualAmtWthld = actualAmtWthld;
	}

	public String getStrTaxRate() {
		return strTaxRate;
	}

	public void setStrTaxRate(String strTaxRate) {
		this.strTaxRate = strTaxRate;
	}

	public String getStrTaxBase() {
		return strTaxBase;
	}

	public void setStrTaxBase(String strTaxBase) {
		this.strTaxBase = strTaxBase;
	}

	public String getStrActualAmtWthld() {
		return strActualAmtWthld;
	}

	public void setStrActualAmtWthld(String strActualAmtWthld) {
		this.strActualAmtWthld = strActualAmtWthld;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SAWTPayeesDto [fTypeCode=").append(fTypeCode).append(", seqNum=").append(seqNum)
				.append(", tin=").append(tin).append(", branchCode=").append(branchCode).append(", registeredName=")
				.append(registeredName).append(", lastName=").append(lastName).append(", firstName=").append(firstName)
				.append(", middleName=").append(middleName).append(", retrnPeriod=").append(retrnPeriod)
				.append(", natureOfIncomePayment=").append(natureOfIncomePayment).append(", atcCode=").append(atcCode)
				.append(", taxRate=").append(taxRate).append(", taxBase=").append(taxBase).append(", actualAmtWthld=")
				.append(actualAmtWthld).append(", strTaxRate=").append(strTaxRate).append(", strTaxBase=")
				.append(strTaxBase).append(", strActualAmtWthld=").append(strActualAmtWthld).append("]");
		return builder.toString();
	}
}
