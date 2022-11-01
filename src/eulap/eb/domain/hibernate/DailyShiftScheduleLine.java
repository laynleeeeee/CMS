package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain class for DAILY_SHIFT_SCHEDULE_LINE

 *
 */
@Entity
@Table(name="DAILY_SHIFT_SCHEDULE_LINE")
public class DailyShiftScheduleLine extends BaseDomain {
	private Integer dailyShiftScheduleId;
	private Integer employeeId;
	private Integer employeeShiftId;
	private Date date;
	private Employee employee;
	private EmployeeShift employeeShift;
	private String employeeName;
	private String shiftName;

	public enum FIELD {
		id, dailyShiftScheduleId, employeeId, employeeShiftId, date
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "DAILY_SHIFT_SCHEDULE_LINE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "DAILY_SHIFT_SCHEDULE_ID", columnDefinition="int(10)")
	public Integer getDailyShiftScheduleId() {
		return dailyShiftScheduleId;
	}

	public void setDailyShiftScheduleId(Integer dailyShiftScheduleId) {
		this.dailyShiftScheduleId = dailyShiftScheduleId;
	}

	@Column(name = "EMPLOYEE_ID", columnDefinition="int(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "EMPLOYEE_SHIFT_ID", columnDefinition="int(10)")
	public Integer getEmployeeShiftId() {
		return employeeShiftId;
	}

	public void setEmployeeShiftId(Integer employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}

	@Column(name = "DATE", columnDefinition="date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Transient
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Transient
	public EmployeeShift getEmployeeShift() {
		return employeeShift;
	}

	public void setEmployeeShift(EmployeeShift employeeShift) {
		this.employeeShift = employeeShift;
	}

	@Transient
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Transient
	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DailyShiftScheduleLine [dailyShiftScheduleId=")
				.append(dailyShiftScheduleId).append(", employeeId=")
				.append(employeeId).append(", employeeShiftId=")
				.append(employeeShiftId).append(", date=").append(date)
				.append("]");
		return builder.toString();
	}
}