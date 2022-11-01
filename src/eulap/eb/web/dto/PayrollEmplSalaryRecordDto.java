package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.EmployeeDeduction;

/**
 * Data transfer object for payroll employee salary record.

 */

public class PayrollEmplSalaryRecordDto {
	private Double basicPay;
	private Double overtime;
	private Double paidLeave;
	private Double deMinimis;
	private Double cola;
	private Double bonus;
	private Double deduction;
	private Double sss;
	private Double philHealth;
	private Double pagibig;
	private Double withholdingTax;
	private String employeeName;
	private Double grossPay;
	private Double netPay;
	private Double lateAbsent;
	private Double sundayHolidayPay;
	private Double nightDiff;
	private List<EmployeeDeduction> employeeDeductions;
	private Double totalHoursWorked;
	private Double totalLateHours;
	private Double totalDeductions;
	private Double regHolidayPay;
	private Double nonWorkHolidayPay;
	private Double dailyRate;
	private Double adjustment;
	private Double workingDays;
	private Double cashAdvance;
	private Double sssLoan;
	private Double hdmfLoan;
	private Double otherDeductions;
	private Double cashAdvBalances;
	private Double otherBalances;

	public Double getBasicPay() {
		return basicPay;
	}

	public void setBasicPay(Double basicPay) {
		this.basicPay = basicPay;
	}

	public Double getOvertime() {
		return overtime;
	}

	public void setOvertime(Double overtime) {
		this.overtime = overtime;
	}

	public Double getPaidLeave() {
		return paidLeave;
	}

	public void setPaidLeave(Double paidLeave) {
		this.paidLeave = paidLeave;
	}

	public Double getDeMinimis() {
		return deMinimis;
	}

	public void setDeMinimis(Double deMinimis) {
		this.deMinimis = deMinimis;
	}

	public Double getCola() {
		return cola;
	}

	public void setCola(Double cola) {
		this.cola = cola;
	}

	public Double getBonus() {
		return bonus;
	}
	public void setBonus(Double bonus) {
		this.bonus = bonus;
	}

	public Double getDeduction() {
		return deduction;
	}

	public void setDeduction(Double deduction) {
		this.deduction = deduction;
	}

	public Double getSss() {
		return sss;
	}

	public void setSss(Double sss) {
		this.sss = sss;
	}

	public Double getPhilHealth() {
		return philHealth;
	}

	public void setPhilHealth(Double philHealth) {
		this.philHealth = philHealth;
	}

	public Double getPagibig() {
		return pagibig;
	}

	public void setPagibig(Double pagibig) {
		this.pagibig = pagibig;
	}

	public Double getWithholdingTax() {
		return withholdingTax;
	}

	public void setWithholdingTax(Double withholdingTax) {
		this.withholdingTax = withholdingTax;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Double getGrossPay() {
		return grossPay;
	}

	public void setGrossPay(Double grossPay) {
		this.grossPay = grossPay;
	}

	public Double getNetPay() {
		return netPay;
	}

	public void setNetPay(Double netPay) {
		this.netPay = netPay;
	}

	public Double getLateAbsent() {
		return lateAbsent;
	}

	public void setLateAbsent(Double lateAbsent) {
		this.lateAbsent = lateAbsent;
	}
	

	public Double getSundayHolidayPay() {
		return sundayHolidayPay;
	}

	public void setSundayHolidayPay(Double sundayHolidayPay) {
		this.sundayHolidayPay = sundayHolidayPay;
	}

	public Double getNightDiff() {
		return nightDiff;
	}

	public void setNightDiff(Double nightDiff) {
		this.nightDiff = nightDiff;
	}

	public List<EmployeeDeduction> getEmployeeDeductions() {
		return employeeDeductions;
	}

	public void setEmployeeDeductions(List<EmployeeDeduction> employeeDeductions) {
		this.employeeDeductions = employeeDeductions;
	}

	public Double getTotalHoursWorked() {
		return totalHoursWorked;
	}

	public void setTotalHoursWorked(Double totalHoursWorked) {
		this.totalHoursWorked = totalHoursWorked;
	}

	public Double getTotalLateHours() {
		return totalLateHours;
	}

	public void setTotalLateHours(Double totalLateHours) {
		this.totalLateHours = totalLateHours;
	}

	public Double getTotalDeductions() {
		return totalDeductions;
	}

	public void setTotalDeductions(Double totalDeductions) {
		this.totalDeductions = totalDeductions;
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

	public Double getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(Double dailyRate) {
		this.dailyRate = dailyRate;
	}

	public Double getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(Double adjustment) {
		this.adjustment = adjustment;
	}

	public Double getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(Double workingDays) {
		this.workingDays = workingDays;
	}

	public Double getCashAdvance() {
		return cashAdvance;
	}

	public void setCashAdvance(Double cashAdvance) {
		this.cashAdvance = cashAdvance;
	}

	public Double getSssLoan() {
		return sssLoan;
	}

	public void setSssLoan(Double sssLoan) {
		this.sssLoan = sssLoan;
	}

	public Double getHdmfLoan() {
		return hdmfLoan;
	}

	public void setHdmfLoan(Double hdmfLoan) {
		this.hdmfLoan = hdmfLoan;
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
		builder.append("PayrollEmplSalaryRecordDto [basicPay=").append(basicPay).append(", overtime=").append(overtime)
				.append(", paidLeave=").append(paidLeave).append(", deMinimis=").append(deMinimis).append(", cola=")
				.append(cola).append(", bonus=").append(bonus).append(", deduction=").append(deduction).append(", sss=")
				.append(sss).append(", philHealth=").append(philHealth).append(", pagibig=").append(pagibig)
				.append(", withholdingTax=").append(withholdingTax).append(", employeeName=").append(employeeName)
				.append(", grossPay=").append(grossPay).append(", netPay=").append(netPay).append(", lateAbsent=")
				.append(lateAbsent).append(", sundayHolidayPay=").append(sundayHolidayPay).append(", nightDiff=")
				.append(nightDiff).append(", employeeDeductions=").append(employeeDeductions)
				.append(", totalHoursWorked=").append(totalHoursWorked).append(", totalLateHours=")
				.append(totalLateHours).append(", totalDeductions=").append(totalDeductions).append(", regHolidayPay=")
				.append(regHolidayPay).append(", nonWorkHolidayPay=").append(nonWorkHolidayPay).append(", dailyRate=")
				.append(dailyRate).append(", adjustment=").append(adjustment).append(", workingDays=")
				.append(workingDays).append(", cashAdvance=").append(cashAdvance).append(", sssLoan=").append(sssLoan)
				.append(", hdmfLoan=").append(hdmfLoan).append(", otherDeductions=").append(otherDeductions)
				.append(", cashAdvBalances=").append(cashAdvBalances).append(", otherBalances=").append(otherBalances)
				.append("]");
		return builder.toString();
	}
}
