package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphalistDetail;

/**
 * Annual Alphalist withhold.

 */

public class AnnualAlphalsitWTEDetailsDto  implements AlphalistDetail {
	private int sequenceNo;
	private String tin;
	private String payeesTin;
	private String branchCode;
	private String registName;
	private String lastName;
	private String firstName;
	private String middleName;
	private String atcCode;
	private Integer taxTypeId;
	private Double incomePayment;
	private Double taxRate;
	private Double amountTaxheld;

	public String getPayeesTin() {
		return payeesTin;
	}

	public void setPayeesTin(String payeesTin) {
		this.payeesTin = payeesTin;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public String getRegistName() {
		return registName;
	}

	public void setRegistName(String registName) {
		this.registName = registName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getAtcCode() {
		return atcCode;
	}

	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}

	public double getIncomePayment() {
		return incomePayment;
	}

	public void setIncomePayment(Double incomePayment) {
		this.incomePayment = incomePayment;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public double getAmountTaxheld() {
		return amountTaxheld;
	}

	public void setAmountTaxheld(Double amountTaxheld) {
		this.amountTaxheld = amountTaxheld;
	}

	public Integer getTaxTypeId() {
		return taxTypeId;
	}

	public void setTaxTypeId(Integer taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AnnualAlphalsitWTEDetailsDto [sequenceNo=").append(sequenceNo).append(", tin=").append(tin)
				.append(", payeesTin=").append(payeesTin).append(", branchCode=").append(branchCode)
				.append(", registName=").append(registName).append(", lastName=").append(lastName)
				.append(", firstName=").append(firstName).append(", middleName=").append(middleName)
				.append(", atcCode=").append(atcCode).append(", taxTypeId=").append(taxTypeId)
				.append(", incomePayment=").append(incomePayment).append(", taxRate=").append(taxRate)
				.append(", amountTaxheld=").append(amountTaxheld).append("]");
		return builder.toString();
	}

	@Override
	public String convertDetailToCSV() {
		// TODO Auto-generated method stub
		return null;
	}


}