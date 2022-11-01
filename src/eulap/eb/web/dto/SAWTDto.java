package eulap.eb.web.dto;

/**
 * Summary alphalist of withholding taxes dto.

 */
public class SAWTDto {
	private Integer sequenceNumber;
	private String tin;
	private String corporateName;
	private String individualName;
	private String birAtcCode;
	private String natureOfPayment;
	private Double amount;
	private Double taxRate;
	private Double taxWitheld;
	private String strAmount;
	private String strTaxRate;
	private String strTaxWitheld;
	private String lastName;
	private String firstName;
	private String middleName;

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

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

	public String getBirAtcCode() {
		return birAtcCode;
	}
	public void setBirAtcCode(String birAtcCode) {
		this.birAtcCode = birAtcCode;
	}

	public String getNatureOfPayment() {
		return natureOfPayment;
	}
	public void setNatureOfPayment(String natureOfPayment) {
		this.natureOfPayment = natureOfPayment;
	}

	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public Double getTaxWitheld() {
		return taxWitheld;
	}
	public void setTaxWitheld(Double taxWitheld) {
		this.taxWitheld = taxWitheld;
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

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getStrTaxRate() {
		return strTaxRate;
	}

	public void setStrTaxRate(String strTaxRate) {
		this.strTaxRate = strTaxRate;
	}

	public String getStrTaxWitheld() {
		return strTaxWitheld;
	}

	public void setStrTaxWitheld(String strTaxWitheld) {
		this.strTaxWitheld = strTaxWitheld;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SAWTDto [sequenceNumber=").append(sequenceNumber).append(", tin=").append(tin)
				.append(", corporateName=").append(corporateName).append(", individualName=").append(individualName)
				.append(", birAtcCode=").append(birAtcCode).append(", natureOfPayment=").append(natureOfPayment)
				.append(", amount=").append(amount).append(", taxRate=").append(taxRate).append(", taxWitheld=")
				.append(taxWitheld).append(", strAmount=").append(strAmount).append(", strTaxRate=").append(strTaxRate)
				.append(", strTaxWitheld=").append(strTaxWitheld).append(", lastName=").append(lastName)
				.append(", firstName=").append(firstName).append(", middleName=").append(middleName).append("]");
		return builder.toString();
	}
}
