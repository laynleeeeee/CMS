package eulap.eb.web.dto;

/**
 * A class that handles data for generating report in pdf or excel format for Alphalist Payees report data.
 *
 *

 */
public class AnnualAlphalistPayeesDetailDto{

	private String scheduleNum; //D4 FOR SCHEDULE 4, D5 SCHDEULE 5, D6 SCHEDULE 6
	private String fTypeCode;
	private String tinEmpyr;
	private String branchCodeEmplyr;
	private String retrnPeriod;
	private Integer seqNum =0;
	private String tin;
	private String branchCode;
	private String registeredName;
	private String lastName;
	private String firstName;
	private String middleName;
	private String statusCode;
	private String atcCode;
	private String payeesAddress;
	private String natureOfIncomePayment;
	private Double taxRate;
	private Double incomePayment;
	private Double actualAmtWthld;
	private String employeeName;

	public void setfTypeCode(String fTypeCode) {
		this.fTypeCode = fTypeCode;
	}

	public String getfTypeCode() {
		return fTypeCode;
	}

	public String getTinEmpyr() {
		return tinEmpyr;
	}

	public void setTinEmpyr(String tinEmpyr) {
		this.tinEmpyr = tinEmpyr;
	}

	public String getBranchCodeEmplyr() {
		return branchCodeEmplyr;
	}

	public void setBranchCodeEmplyr(String branchCodeEmplyr) {
		this.branchCodeEmplyr = branchCodeEmplyr;
	}

	public String getRetrnPeriod() {
		return retrnPeriod;
	}

	public void setRetrnPeriod(String retrnPeriod) {
		this.retrnPeriod = retrnPeriod;
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

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getAtcCode() {
		return atcCode;
	}

	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}

	public Double getIncomePayment() {
		return incomePayment;
	}

	public void setIncomePayment(Double incomePayment) {
		this.incomePayment = incomePayment;
	}

	public Double getActualAmtWthld() {
		return actualAmtWthld;
	}

	public void setActualAmtWthld(Double actualAmtWthld) {
		this.actualAmtWthld = actualAmtWthld;
	}

	public String getPayeesAddress() {
		return payeesAddress;
	}

	public void setPayeesAddress(String payeesAddress) {
		this.payeesAddress = payeesAddress;
	}

	public String getNatureOfIncomePayment() {
		return natureOfIncomePayment;
	}

	public void setNatureOfIncomePayment(String natureOfIncomePayment) {
		this.natureOfIncomePayment = natureOfIncomePayment;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getScheduleNum() {
		return scheduleNum;
	}

	public void setScheduleNum(String scheduleNum) {
		this.scheduleNum = scheduleNum;
	}
}
