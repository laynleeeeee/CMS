package eulap.eb.web.dto.webservice;

import java.io.Serializable;

import com.google.gson.annotations.Expose;


/**
 * A class that defines the employee salary. 

 *
 */
public class EmployeeSalaryDTO implements Serializable {
	private static final long serialVersionUID = -4521060830068286104L;
	@Expose
	private Integer id;
	@Expose
	private Integer payrollId;
	@Expose
	private String employeeNo;
	@Expose
	private double basicPay;
	@Expose
	private double overtime;
	@Expose
	private double paidLeave;
	@Expose
	private double deMinimis;
	@Expose
	private double cola;
	@Expose
	private double bonus;
	@Expose
	private double deduction;
	@Expose
	private double sss;
	@Expose
	private double sssLoan;
	@Expose
	private double sssEr;
	@Expose
	private double sssEc;
	@Expose
	private double philHealth;
	@Expose
	private double philHealthEr;
	@Expose
	private double pagibig;
	@Expose
	private double pagibigLoan;
	@Expose
	private double pagibigEr;
	@Expose
	private double withholdingTax;
	@Expose
	private String employeeName;
	@Expose
	private String employeeStatus;
	@Expose
	private double grossPay;
	@Expose
	private double netPay;
	@Expose
	private double prevBalance;
	@Expose
	private double lateAbsent;
	@Expose
	private double sundayHolidayPay;
	@Expose
	private double breakageWastage;
	@Expose
	private double cashAdvance;
	@Expose
	private Double nightDiff;
	@Expose
	private Double salaryRate;
	@Expose
	private Double regHolidayPay;
	@Expose
	private Double nonWorkHolidayPay;
	@Expose
	private Double adjustment;
	@Expose
	private Double dailyRate;
	@Expose
	private Double workingDays;
	@Expose
	private Integer employeeId;
	@Expose
	private Double otherDeductions;
	@Expose
	private Double cashAdvBalances;
	@Expose
	private Double otherBalances;

	public enum FIELD {
		id, payrollId, employeeId, basicPay, overtime, paidLeave, deMinimis,
		cola, bonus, deduction, sss, philHealth, pagibig, witholdingTax, lateAbsent, employeeStatus
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPayrollId() {
		return payrollId;
	}

	public void setPayrollId(Integer payrollId) {
		this.payrollId = payrollId;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public double getBasicPay() {
		return basicPay;
	}

	public void setBasicPay(double basicPay) {
		this.basicPay = basicPay;
	}

	public double getOvertime() {
		return overtime;
	}

	public void setOvertime(double overtime) {
		this.overtime = overtime;
	}

	public double getPaidLeave() {
		return paidLeave;
	}

	public void setPaidLeave(double paidLeave) {
		this.paidLeave = paidLeave;
	}

	public double getDeMinimis() {
		return deMinimis;
	}

	public void setDeMinimis(double deMinimis) {
		this.deMinimis = deMinimis;
	}

	public double getCola() {
		return cola;
	}

	public void setCola(double cola) {
		this.cola = cola;
	}

	public double getBonus() {
		return bonus;
	}

	public void setBonus(double bonus) {
		this.bonus = bonus;
	}

	public double getDeduction() {
		return deduction;
	}

	public void setDeduction(double deduction) {
		this.deduction = deduction;
	}

	public double getSss() {
		return sss;
	}

	public void setSss(double sss) {
		this.sss = sss;
	}

	public double getSssLoan() {
		return sssLoan;
	}

	public void setSssLoan(double sssLoan) {
		this.sssLoan = sssLoan;
	}

	public double getSssEr() {
		return sssEr;
	}

	public void setSssEr(double sssEr) {
		this.sssEr = sssEr;
	}

	public double getSssEc() {
		return sssEc;
	}

	public void setSssEc(double sssEc) {
		this.sssEc = sssEc;
	}

	public double getPhilHealth() {
		return philHealth;
	}

	public void setPhilHealth(double philHealth) {
		this.philHealth = philHealth;
	}

	public double getPhilHealthEr() {
		return philHealthEr;
	}

	public void setPhilHealthEr(double philHealthEr) {
		this.philHealthEr = philHealthEr;
	}

	public double getPagibig() {
		return pagibig;
	}

	public void setPagibig(double pagibig) {
		this.pagibig = pagibig;
	}

	public double getPagibigLoan() {
		return pagibigLoan;
	}

	public void setPagibigLoan(double pagibigLoan) {
		this.pagibigLoan = pagibigLoan;
	}

	public double getPagibigEr() {
		return pagibigEr;
	}

	public void setPagibigEr(double pagibigEr) {
		this.pagibigEr = pagibigEr;
	}

	public double getWithholdingTax() {
		return withholdingTax;
	}

	public void setWithholdingTax(double withholdingTax) {
		this.withholdingTax = withholdingTax;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public double getGrossPay() {
		return grossPay;
	}

	public void setGrossPay(double grossPay) {
		this.grossPay = grossPay;
	}

	public double getNetPay() {
		return netPay;
	}

	public void setNetPay(double netPay) {
		this.netPay = netPay;
	}

	public double getPrevBalance() {
		return prevBalance;
	}

	public void setPrevBalance(double prevBalance) {
		this.prevBalance = prevBalance;
	}

	public double getLateAbsent() {
		return lateAbsent;
	}

	public void setLateAbsent(double lateAbsent) {
		this.lateAbsent = lateAbsent;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public double getSundayHolidayPay() {
		return sundayHolidayPay;
	}

	public void setSundayHolidayPay(double sundayHolidayPay) {
		this.sundayHolidayPay = sundayHolidayPay;
	}

	public double getBreakageWastage() {
		return breakageWastage;
	}

	public void setBreakageWastage(double breakageWastage) {
		this.breakageWastage = breakageWastage;
	}

	public double getCashAdvance() {
		return cashAdvance;
	}

	public void setCashAdvance(double cashAdvance) {
		this.cashAdvance = cashAdvance;
	}

	public Double getNightDiff() {
		return nightDiff;
	}

	public void setNightDiff(Double nightDiff) {
		this.nightDiff = nightDiff;
	}

	public Double getSalaryRate() {
		return salaryRate;
	}

	public void setSalaryRate(Double salaryRate) {
		this.salaryRate = salaryRate;
	}

	public Double getRegHolidayPay() {
		return regHolidayPay;
	}

	public void setRegHolidayPay(Double regHolidayPay) {
		this.regHolidayPay = regHolidayPay;
	}

	public Double getNonWorkHolidayPay() {
		return nonWorkHolidayPay;
	}

	public void setNonWorkHolidayPay(Double nonWorkHolidayPay) {
		this.nonWorkHolidayPay = nonWorkHolidayPay;
	}

	public Double getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(Double adjustment) {
		this.adjustment = adjustment;
	}

	public Double getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(Double dailyRate) {
		this.dailyRate = dailyRate;
	}

	public Double getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(Double workingDays) {
		this.workingDays = workingDays;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Double getOtherDeductions() {
		return otherDeductions;
	}

	public void setOtherDeductions(Double otherDeductions) {
		this.otherDeductions = otherDeductions;
	}

	public Double getCashAdvBalances() {
		return cashAdvBalances;
	}

	public void setCashAdvBalances(Double cashAdvBalances) {
		this.cashAdvBalances = cashAdvBalances;
	}

	public Double getOtherBalances() {
		return otherBalances;
	}

	public void setOtherBalances(Double otherBalances) {
		this.otherBalances = otherBalances;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeSalaryDTO [id=").append(id).append(", payrollId=").append(payrollId)
				.append(", employeeNo=").append(employeeNo).append(", basicPay=").append(basicPay).append(", overtime=")
				.append(overtime).append(", paidLeave=").append(paidLeave).append(", deMinimis=").append(deMinimis)
				.append(", cola=").append(cola).append(", bonus=").append(bonus).append(", deduction=")
				.append(deduction).append(", sss=").append(sss).append(", sssLoan=").append(sssLoan).append(", sssEr=")
				.append(sssEr).append(", sssEc=").append(sssEc).append(", philHealth=").append(philHealth)
				.append(", philHealthEr=").append(philHealthEr).append(", pagibig=").append(pagibig)
				.append(", pagibigLoan=").append(pagibigLoan).append(", pagibigEr=").append(pagibigEr)
				.append(", withholdingTax=").append(withholdingTax).append(", employeeName=").append(employeeName)
				.append(", employeeStatus=").append(employeeStatus).append(", grossPay=").append(grossPay)
				.append(", netPay=").append(netPay).append(", prevBalance=").append(prevBalance).append(", lateAbsent=")
				.append(lateAbsent).append(", sundayHolidayPay=").append(sundayHolidayPay).append(", breakageWastage=")
				.append(breakageWastage).append(", cashAdvance=").append(cashAdvance).append(", nightDiff=")
				.append(nightDiff).append(", salaryRate=").append(salaryRate).append(", regHolidayPay=")
				.append(regHolidayPay).append(", nonWorkHolidayPay=").append(nonWorkHolidayPay).append(", adjustment=")
				.append(adjustment).append(", dailyRate=").append(dailyRate).append(", workingDays=")
				.append(workingDays).append(", employeeId=").append(employeeId).append(", otherDeductions=")
				.append(otherDeductions).append(", cashAdvBalances=").append(cashAdvBalances).append(", otherBalances=")
				.append(otherBalances).append("]");
		return builder.toString();
	}
}
