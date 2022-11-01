package eulap.eb.web.dto;

/**
 * Container class for holding the values needed to generate
 * the Quarterly Alphalist of Payees

 */

public class QuarterlyAlphaListOfPayeesDATDto {
	private String tin;
	private String corporateName;
	private String individualName;
	private String atcCode;
	private String natureOfPayment;
	private String firstAmount;
	private String firstTaxRate;
	private String firstTaxWithheld;
	private String totalAmount;
	private String totalTaxWithheld;
	private String lastName;
	private String firstName;
	private String middleName;
	private String branchCode;

	public String getTin() {
		return tin;
	}
	public void setTin(String tin) {
		this.tin = tin;
	}

	public String getCorporateName() {
		return corporateName;
	}
	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}

	public String getIndividualName() {
		return individualName;
	}
	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}

	public String getAtcCode() {
		return atcCode;
	}
	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}

	public String getNatureOfPayment() {
		return natureOfPayment;
	}
	public void setNatureOfPayment(String natureOfPayment) {
		this.natureOfPayment = natureOfPayment;
	}

	public String getFirstAmount() {
		return firstAmount;
	}
	public void setFirstAmount(String firstAmount) {
		this.firstAmount = firstAmount;
	}

	public String getFirstTaxRate() {
		return firstTaxRate;
	}
	public void setFirstTaxRate(String firstTaxRate) {
		this.firstTaxRate = firstTaxRate;
	}

	public String getFirstTaxWithheld() {
		return firstTaxWithheld;
	}
	public void setFirstTaxWithheld(String firstTaxWithheld) {
		this.firstTaxWithheld = firstTaxWithheld;
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

	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getTotalTaxWithheld() {
		return totalTaxWithheld;
	}
	public void setTotalTaxWithheld(String totalTaxWithheld) {
		this.totalTaxWithheld = totalTaxWithheld;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	@Override
	public String toString() {
		return "QuarterlyAlphaListOfPayeesDATDto [tin=" + tin + ", corporateName=" + corporateName + ", individualName="
				+ individualName + ", atcCode=" + atcCode + ", natureOfPayment=" + natureOfPayment + ", firstAmount="
				+ firstAmount + ", firstTaxRate=" + firstTaxRate + ", firstTaxWithheld=" + firstTaxWithheld
				+ ", totalAmount=" + totalAmount + ", totalTaxWithheld=" + totalTaxWithheld + ", lastName=" + lastName
				+ ", firstName=" + firstName + ", middleName=" + middleName + "]";
	}
}
