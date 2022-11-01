package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphalistDetail;

/**
 * A class that handles the controls for Alphalist Payees report data.
 *
 *

 */
public abstract class AlphalistPayeesDetailBase implements AlphalistDetail{
	public static  String SCHEDULE_NUM_D4 = "D4";
	public static  String SCHEDULE_NUM_D5 = "D5";
	public static  String SCHEDULE_NUM_D6 = "D6";
	public static  String F_TYPE_CODE_1604F = "1604F";
	private String scheduleNum; //D4 FOR SCHEDULE 4, D5 SCHDEULE 5, D6 SCHEDULE 6
	private String fTypeCode;
	private String tinEmpyr;
	private String branchCodeEmplyr;
	private String retrnPeriod;
	private Integer seqNum =0;
	private String tin;
	private String branchCode;
	private String lastName;
	private String firstName;
	private String middleName;
	private String atcCode;

	private String payeesAddress;
	private String natureOfIncomePayment;
	private String employeeName;

	protected String convertCommonFieldsToCSV () {
		StringBuffer csv = new StringBuffer();
		csv.append(scheduleNum);
		prependComma(csv, fTypeCode);
		prependComma(csv, tinEmpyr);
		prependComma(csv, branchCodeEmplyr);
		prependComma(csv, retrnPeriod);
		prependComma(csv, seqNum);
		prependComma(csv, tin);
		prependComma(csv, branchCode);
		return csv.toString();
	}

	protected void prependComma (StringBuffer csv, Object obj) {
		csv.append(',').append(obj);
	}

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
