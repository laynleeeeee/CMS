package eulap.eb.domain.hibernate;

import java.util.Date;

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
 * Domain class for PAYROLL_EMPLOYEE_TIMESHEET

 *
 */
@Entity
@Table(name="PAYROLL_EMPLOYEE_TIMESHEET")
public class PayrollEmployeeTimeSheet extends BaseFormLine {
	@Expose
	private Integer payrollId;
	@Expose
	private Integer timeSheetId;
	@Expose
	private Integer employeeId;
	@Expose
	private Date date;
	@Expose
	private double hoursWorked;
	@Expose
	private double late;
	@Expose
	private Employee employee;
	@Expose
	private double adjustment;
	@Expose
	private double overtime;
	@Expose
	private double status;
	@Expose
	private String statusLabel;
	@Expose
	private double undertime;
	@Expose
	private double daysWorked;

	public enum FIELD {
		id, payrollId, timeSheetId, date, modelName, active, employeeId
	}

	/**
	 * Payroll Employee Timesheet object type = 152.
	 */
	public static final int PET_OBJECT_TYPE = 152;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PAYROLL_EMPLOYEE_TIMESHEET_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "TIME_SHEET_ID", columnDefinition = "INT(10)")
	public Integer getTimeSheetId() {
		return timeSheetId;
	}

	public void setTimeSheetId(Integer timeSheetId) {
		this.timeSheetId = timeSheetId;
	}

	@Column(name = "EMPLOYEE_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "DATE", columnDefinition = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "HOURS_WORKED", columnDefinition = "double(19,4)")
	public double getHoursWorked() {
		return hoursWorked;
	}

	public void setHoursWorked(double hoursWorked) {
		this.hoursWorked = hoursWorked;
	}

	@Column(name = "LATE", columnDefinition = "double(19,4)")
	public double getLate() {
		return late;
	}

	public void setLate(double late) {
		this.late = late;
	}

	@OneToOne
	@JoinColumn(name = "EMPLOYEE_ID", insertable = false, updatable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Column(name = "ADJUSTMENT", columnDefinition = "double(19,4)")
	public Double getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(Double adjustment) {
		this.adjustment = adjustment;
	}

	@Column(name = "OVERTIME", columnDefinition = "double(19,4)")
	public double getOvertime() {
		return overtime;
	}

	public void setOvertime(double overtime) {
		this.overtime = overtime;
	}

	@Column(name = "STATUS", columnDefinition = "double(1, 0)")
	public double getStatus() {
		return status;
	}

	public void setStatus(double status) {
		this.status = status;
	}

	@Column(name = "STATUS_LABEL", columnDefinition = "VARCHAR(5)")
	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}

	@Column(name = "UNDERTIME", columnDefinition = "double(19,4)")
	public double getUndertime() {
		return undertime;
	}

	public void setUndertime(double undertime) {
		this.undertime = undertime;
	}

	@Column(name = "DAYS_WORKED", columnDefinition = "double(4,2)")
	public double getDaysWorked() {
		return daysWorked;
	}

	public void setDaysWorked(double daysWorked) {
		this.daysWorked = daysWorked;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return PET_OBJECT_TYPE;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PayrollEmployeeTimeSheet [payrollId=").append(payrollId).append(", timeSheetId=")
				.append(timeSheetId).append(", employeeId=").append(employeeId).append(", date=").append(date)
				.append(", hoursWorked=").append(hoursWorked).append(", late=").append(late).append(", employee=")
				.append(employee).append(", adjustment=").append(adjustment).append(", overtime=").append(overtime)
				.append(", status=").append(status).append(", statusLabel=").append(statusLabel).append(", undertime=")
				.append(undertime).append(", daysWorked=").append(daysWorked).append("]");
		return builder.toString();
	}
}
