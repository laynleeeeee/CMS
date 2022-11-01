package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphalistDetail;

/**
 * Annual Alphalist withhold base details DTO

 */

public abstract class AnnualAlphaWTESchedBaseDetaisDto implements AlphalistDetail {
	private String tin;
	private String branchCode;
	private String returnPeriod;
	private int sequenceNumber;
	private String payeesTin;
	private String registName;
	private String lastName;
	private String firstName;
	private String middleName;
	private String atcCode;
	private String incomePayment;
	private String employersTin;
	private String employerBranchCode;

	protected String convertCommonToString() {
		StringBuilder csv = new StringBuilder();
		csv.append(',').append(employersTin);
		csv.append(',').append(employerBranchCode);
		csv.append(',').append(returnPeriod);
		csv.append(',').append(sequenceNumber);
		csv.append(',').append(tin);
		csv.append(',').append(branchCode);
		if(!registName.isEmpty()) {
			csv.append(',').append('"').append(registName).append('"');
		} else {
			csv.append(',');
		}
		if(!lastName.isEmpty()) {
			csv.append(',').append('"').append(lastName).append('"');
		} else {
			csv.append(',');
		}
		if(!firstName.isEmpty()) {
			csv.append(',').append('"').append(firstName).append('"');
		} else {
			csv.append(',');
		}
		if(!middleName.isEmpty()) {
			csv.append(',').append('"').append(middleName).append('"');
		} else {
			csv.append(',');
		}
		csv.append(',').append(atcCode);
		csv.append(',').append(incomePayment);
		return csv.toString();
	}

	public String getEmployersTin() {
		return employersTin;
	}

	public void setEmployersTin(String employersTin) {
		this.employersTin = employersTin;
	}

	public String getEmployerBranchCode() {
		return employerBranchCode;
	}

	public void setEmployerBranchCode(String employerBranchCode) {
		this.employerBranchCode = employerBranchCode;
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

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getPayeesTin() {
		return payeesTin;
	}

	public void setPayeesTin(String payeesTin) {
		this.payeesTin = payeesTin;
	}

	public String getRegistName() {
		return registName;
	}

	public void setRegistName(String registName) {
		this.registName = registName;
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

	public String getAtcCode() {
		return atcCode;
	}

	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}

	public String getIncomePayment() {
		return incomePayment;
	}

	public void setIncomePayment(String incomePayment) {
		this.incomePayment = incomePayment;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AnnualAlphaWTESchedFourDetaisDto [tin=").append(tin).append(", branchCode=").append(branchCode)
				.append(", returnPeriod=").append(returnPeriod).append(", sequenceNumber=").append(sequenceNumber)
				.append(", payeesTin=").append(payeesTin).append(", registName=").append(registName)
				.append(", lastName=").append(lastName).append(", firstName=").append(firstName).append(", middleName=")
				.append(middleName).append(", atcCode=").append(atcCode).append(", incomePayment=")
				.append(incomePayment).append("]");
		return builder.toString();
	}
}