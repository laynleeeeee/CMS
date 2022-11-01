package eulap.eb.web.dto;

/**
 * Container class for holding the values needed to generate
 * the Quarterly Alphalist of Payees

 */
public class QuarterlyAlphaListOfPayeesDto {
	private String tin;
	private String corporateName;
	private String individualName;
	private String atcCode;
	private String natureOfPayment;
	private Double firstAmount;
	private Double firstTaxRate;
	private Double firstTaxWithheld;
	private Double secondAmount;
	private Double secondTaxRate;
	private Double secondTaxWithheld;
	private Double thirdAmount;
	private Double thirdTaxRate;
	private Double thirdTaxWithheld;
	private Double totalAmount;
	private Double totalTaxWithheld;
	private String lastName;
	private String firstName;
	private String middleName;

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

	public Double getFirstAmount() {
		return firstAmount;
	}
	public void setFirstAmount(Double firstAmount) {
		this.firstAmount = firstAmount;
	}

	public Double getFirstTaxRate() {
		return firstTaxRate;
	}
	public void setFirstTaxRate(Double firstTaxRate) {
		this.firstTaxRate = firstTaxRate;
	}

	public Double getFirstTaxWithheld() {
		return firstTaxWithheld;
	}
	public void setFirstTaxWithheld(Double firstTaxWithheld) {
		this.firstTaxWithheld = firstTaxWithheld;
	}

	public Double getSecondAmount() {
		return secondAmount;
	}
	public void setSecondAmount(Double secondAmount) {
		this.secondAmount = secondAmount;
	}

	public Double getSecondTaxRate() {
		return secondTaxRate;
	}
	public void setSecondTaxRate(Double secondTaxRate) {
		this.secondTaxRate = secondTaxRate;
	}

	public Double getSecondTaxWithheld() {
		return secondTaxWithheld;
	}
	public void setSecondTaxWithheld(Double secondTaxWithheld) {
		this.secondTaxWithheld = secondTaxWithheld;
	}

	public Double getThirdAmount() {
		return thirdAmount;
	}
	public void setThirdAmount(Double thirdAmount) {
		this.thirdAmount = thirdAmount;
	}

	public Double getThirdTaxRate() {
		return thirdTaxRate;
	}
	public void setThirdTaxRate(Double thirdTaxRate) {
		this.thirdTaxRate = thirdTaxRate;
	}

	public Double getThirdTaxWithheld() {
		return thirdTaxWithheld;
	}
	public void setThirdTaxWithheld(Double thirdTaxWithheld) {
		this.thirdTaxWithheld = thirdTaxWithheld;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getTotalTaxWithheld() {
		return totalTaxWithheld;
	}
	public void setTotalTaxWithheld(Double totalTaxWithheld) {
		this.totalTaxWithheld = totalTaxWithheld;
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

	@Override
	public String toString() {
		return "QuarterlyAlphaListOfPayeesDto [tin=" + tin + ", corporateName=" + corporateName + ", individualName="
				+ individualName + ", atcCode=" + atcCode + ", natureOfPayment=" + natureOfPayment + ", firstAmount="
				+ firstAmount + ", firstTaxRate=" + firstTaxRate + ", firstTaxWithheld=" + firstTaxWithheld
				+ ", secondAmount=" + secondAmount + ", secondTaxRate=" + secondTaxRate + ", secondTaxWithheld="
				+ secondTaxWithheld + ", thirdAmount=" + thirdAmount + ", thirdTaxRate=" + thirdTaxRate
				+ ", thirdTaxWithheld=" + thirdTaxWithheld + ", totalAmount=" + totalAmount + ", totalTaxWithheld="
				+ totalTaxWithheld + ", lastName=" + lastName + ", firstName=" + firstName + ", middleName="
				+ middleName + "]";
	}
}
