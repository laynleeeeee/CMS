package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Domain class for PAYROLL_EMPLOYEE_SALARY

 *
 */
@Entity
@Table(name="PAYROLL_EMPLOYEE_SALARY")
public class PayrollEmployeeSalary extends BaseFormLine {
	@Expose
	private Integer payrollId;
	@Expose
	private Integer employeeId;
	@Expose
	private Double basicPay;
	@Expose
	private Double overtime;
	@Expose
	private Double paidLeave;
	@Expose
	private Double deMinimis;
	@Expose
	private Double cola;
	@Expose
	private Double bonus;
	@Expose
	private Double deduction;
	@Expose
	private Double sss;
	@Expose
	private Double sssLoan;
	@Expose
	private Double sssEr;
	@Expose
	private Double sssEc;
	@Expose
	private Double philHealth;
	@Expose
	private Double philHealthEr;
	@Expose
	private Double pagibig;
	@Expose
	private Double pagibigLoan;
	@Expose
	private Double pagibigEr;
	@Expose
	private Double withholdingTax;
	@Expose
	private Double nightDifferential;
	@Expose
	private String employeeName;
	@Expose
	private Double grossPay;
	@Expose
	private Double prevBalance;
	@Expose
	private Double netPay;
	@Expose
	private Double lateAbsent;
	private Employee employee;
	@Expose
	private Double sundayHolidayPay;
	@Expose
	private Double breakageWastage;
	@Expose
	private Double cashAdvance;
	@Expose
	private Double totalHoursWorked;
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
	private Double otherDeductions;
	@Expose
	private Double cashAdvBalances;
	@Expose
	private Double otherBalances;

	public enum FIELD {
		id, payrollId, employeeId, basicPay, overtime, paidLeave, deMinimis,
		cola, bonus, deduction, sss, philHealth, pagibig, witholdingTax, lateAndAbsent,
		adjustment
	}

	/**
	 * Payroll employee salary object type = 153.
	 */
	public static final int PES_OBJECT_TYPE = 153;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PAYROLL_EMPLOYEE_SALARY_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "PAYROLL_ID", columnDefinition = "INT(10)")
	public Integer getPayrollId() {
		return payrollId;
	}

	public void setPayrollId(Integer payrollId) {
		this.payrollId = payrollId;
	}

	@Column(name = "EMPLOYEE_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "BASIC_PAY", columnDefinition = "DOUBLE(12,2)")
	public Double getBasicPay() {
		return basicPay;
	}

	public void setBasicPay(Double basicPay) {
		this.basicPay = basicPay;
	}

	@Column(name = "OVERTIME", columnDefinition = "DOUBLE(12,2)")
	public Double getOvertime() {
		return overtime;
	}

	public void setOvertime(Double overtime) {
		this.overtime = overtime;
	}

	@Column(name = "PAID_LEAVE", columnDefinition = "DOUBLE(12,2)")
	public Double getPaidLeave() {
		return paidLeave;
	}

	public void setPaidLeave(Double paidLeave) {
		this.paidLeave = paidLeave;
	}

	@Column(name = "DE_MINIMIS", columnDefinition = "DOUBLE(12,2)")
	public Double getDeMinimis() {
		return deMinimis;
	}

	public void setDeMinimis(Double deMinimis) {
		this.deMinimis = deMinimis;
	}

	@Column(name = "COLA", columnDefinition = "DOUBLE(12,2)")
	public Double getCola() {
		return cola;
	}

	public void setCola(Double cola) {
		this.cola = cola;
	}

	@Column(name = "BONUS", columnDefinition = "DOUBLE(12,2)")
	public Double getBonus() {
		return bonus;
	}

	public void setBonus(Double bonus) {
		this.bonus = bonus;
	}

	@Column(name = "DEDUCTION", columnDefinition = "DOUBLE(12,2)")
	public Double getDeduction() {
		return deduction;
	}

	public void setDeduction(Double deduction) {
		this.deduction = deduction;
	}

	@Column(name = "SSS", columnDefinition = "DOUBLE(12,2)")
	public Double getSss() {
		return sss;
	}

	public void setSss(Double sss) {
		this.sss = sss;
	}

	@Column(name = "SSS_LOAN", columnDefinition = "DOUBLE(12,2)")
	public Double getSssLoan() {
		return sssLoan;
	}

	public void setSssLoan(Double sssLoan) {
		this.sssLoan = sssLoan;
	}

	@Column(name = "SSS_ER", columnDefinition = "DOUBLE(12,2)")
	public Double getSssEr() {
		return sssEr;
	}

	public void setSssEr(Double sssEr) {
		this.sssEr = sssEr;
	}

	@Column(name = "SSS_EC", columnDefinition = "DOUBLE(12,2)")
	public Double getSssEc() {
		return sssEc;
	}

	public void setSssEc(Double sssEc) {
		this.sssEc = sssEc;
	}

	@Column(name = "PHILHEALTH", columnDefinition = "DOUBLE(12,2)")
	public Double getPhilHealth() {
		return philHealth;
	}

	public void setPhilHealth(Double philHealth) {
		this.philHealth = philHealth;
	}

	@Column(name = "PHILHEALTH_ER", columnDefinition = "DOUBLE(12,2)")
	public Double getPhilHealthEr() {
		return philHealthEr;
	}

	public void setPhilHealthEr(Double philHealthEr) {
		this.philHealthEr = philHealthEr;
	}

	@Column(name = "PAGIBIG", columnDefinition = "DOUBLE(12,2)")
	public Double getPagibig() {
		return pagibig;
	}

	public void setPagibig(Double pagibig) {
		this.pagibig = pagibig;
	}

	@Column(name = "PAGIBIG_LOAN", columnDefinition = "DOUBLE(12,2)")
	public Double getPagibigLoan() {
		return pagibigLoan;
	}

	public void setPagibigLoan(Double pagibigLoan) {
		this.pagibigLoan = pagibigLoan;
	}

	@Column(name = "PAGIBIG_ER", columnDefinition = "DOUBLE(12,2)")
	public Double getPagibigEr() {
		return pagibigEr;
	}

	public void setPagibigEr(Double pagibigEr) {
		this.pagibigEr = pagibigEr;
	}

	@Column(name = "WITHHOLDING_TAX", columnDefinition = "DOUBLE(12,2)")
	public Double getWithholdingTax() {
		return withholdingTax;
	}

	public void setWithholdingTax(Double withholdingTax) {
		this.withholdingTax = withholdingTax;
	}

	@Column(name = "NIGHT_DIFFERENTIAL", columnDefinition = "DOUBLE(12,2)")
	public Double getNightDifferential() {
		return nightDifferential;
	}

	public void setNightDifferential(Double nightDifferential) {
		this.nightDifferential = nightDifferential;
	}

	@Transient
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Transient
	public Double getGrossPay() {
		return grossPay;
	}

	public void setGrossPay(Double grossPay) {
		this.grossPay = grossPay;
	}

	@Column(name = "NET_PAY", columnDefinition = "DOUBLE(12,2)")
	public Double getNetPay() {
		return netPay;
	}

	public void setNetPay(Double netPay) {
		this.netPay = netPay;
	}

	@Transient
	public Double getPrevBalance() {
		return prevBalance;
	}

	public void setPrevBalance(Double prevBalance) {
		this.prevBalance = prevBalance;
	}

	@OneToOne
	@JoinColumn(name = "EMPLOYEE_ID", insertable = false, updatable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Column(name = "LATE_ABSENT", columnDefinition = "DOUBLE(12,2)")
	public Double getLateAbsent() {
		return lateAbsent;
	}

	public void setLateAbsent(Double lateAbsent) {
		this.lateAbsent = lateAbsent;
	}

	@Column(name = "SUNDAY_HOLIDAY_PAY", columnDefinition = "DOUBLE(12,2)")
	public Double getSundayHolidayPay() {
		return sundayHolidayPay;
	}

	public void setSundayHolidayPay(Double sundayHolidayPay) {
		this.sundayHolidayPay = sundayHolidayPay;
	}

	@Column(name = "BREAKAGE_WASTAGE", columnDefinition = "DOUBLE(12,2)")
	public Double getBreakageWastage() {
		return breakageWastage;
	}

	public void setBreakageWastage(Double breakageWastage) {
		this.breakageWastage = breakageWastage;
	}

	@Column(name = "CASH_ADVANCE", columnDefinition = "DOUBLE(12,2)")
	public Double getCashAdvance() {
		return cashAdvance;
	}

	public void setCashAdvance(Double cashAdvance) {
		this.cashAdvance = cashAdvance;
	}

	@Transient
	public Double getTotalHoursWorked() {
		return totalHoursWorked;
	}

	public void setTotalHoursWorked(Double totalHoursWorked) {
		this.totalHoursWorked = totalHoursWorked;
	}

	@Column(name = "REGULAR_HOLIDAY_PAY", columnDefinition = "DOUBLE(12,2)")
	public Double getRegHolidayPay() {
		return regHolidayPay;
	}

	public void setRegHolidayPay(Double regHolidayPay) {
		this.regHolidayPay = regHolidayPay;
	}

	@Column(name = "NON_WORKING_HOLIDAY", columnDefinition = "DOUBLE(12,2)")
	public Double getNonWorkHolidayPay() {
		return nonWorkHolidayPay;
	}

	public void setNonWorkHolidayPay(Double nonWorkHolidayPay) {
		this.nonWorkHolidayPay = nonWorkHolidayPay;
	}

	@Column(name = "ADJUSTMENT", columnDefinition = "DOUBLE(12,2)")
	public Double getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(Double adjustment) {
		this.adjustment = adjustment;
	}

	@Column(name = "DAILY_SALARY", columnDefinition = "DOUBLE(12,2)")
	public Double getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(Double dailyRate) {
		this.dailyRate = dailyRate;
	}

	@Column(name = "DAYS", columnDefinition = "DOUBLE(12,2)")
	public Double getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(Double workingDays) {
		this.workingDays = workingDays;
	}

	@Column(name = "OTHER_DEDUCTION", columnDefinition = "DOUBLE(12,2)")
	public Double getOtherDeductions() {
		return otherDeductions;
	}

	public void setOtherDeductions(Double otherDeductions) {
		this.otherDeductions = otherDeductions;
	}

	@Column(name = "CA_BALANCE", columnDefinition = "DOUBLE(12,2)")
	public Double getCashAdvBalances() {
		return cashAdvBalances;
	}

	public void setCashAdvBalances(Double cashAdvBalances) {
		this.cashAdvBalances = cashAdvBalances;
	}

	@Column(name = "OTHER_BALANCE", columnDefinition = "DOUBLE(12,2)")
	public Double getOtherBalances() {
		return otherBalances;
	}

	public void setOtherBalances(Double otherBalances) {
		this.otherBalances = otherBalances;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return PES_OBJECT_TYPE;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PayrollEmployeeSalary [payrollId=").append(payrollId).append(", employeeId=").append(employeeId)
				.append(", basicPay=").append(basicPay).append(", overtime=").append(overtime).append(", paidLeave=")
				.append(paidLeave).append(", deMinimis=").append(deMinimis).append(", cola=").append(cola)
				.append(", bonus=").append(bonus).append(", deduction=").append(deduction).append(", sss=").append(sss)
				.append(", sssLoan=").append(sssLoan).append(", sssEr=").append(sssEr).append(", sssEc=").append(sssEc)
				.append(", philHealth=").append(philHealth).append(", philHealthEr=").append(philHealthEr)
				.append(", pagibig=").append(pagibig).append(", pagibigLoan=").append(pagibigLoan)
				.append(", pagibigEr=").append(pagibigEr).append(", withholdingTax=").append(withholdingTax)
				.append(", nightDifferential=").append(nightDifferential).append(", employeeName=").append(employeeName)
				.append(", grossPay=").append(grossPay).append(", prevBalance=").append(prevBalance).append(", netPay=")
				.append(netPay).append(", lateAbsent=").append(lateAbsent).append(", sundayHolidayPay=")
				.append(sundayHolidayPay).append(", breakageWastage=").append(breakageWastage).append(", cashAdvance=")
				.append(cashAdvance).append(", totalHoursWorked=").append(totalHoursWorked).append(", regHolidayPay=")
				.append(regHolidayPay).append(", nonWorkHolidayPay=").append(nonWorkHolidayPay).append(", adjustment=")
				.append(adjustment).append(", dailyRate=").append(dailyRate).append(", workingDays=")
				.append(workingDays).append(", otherDeductions=").append(otherDeductions).append(", cashAdvBalances=")
				.append(cashAdvBalances).append(", otherBalances=").append(otherBalances).append("]");
		return builder.toString();
	}
}
