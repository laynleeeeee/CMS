package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain class for MONTHLY_SHIFT_SCHEDULE_LINE

 *
 */
@Entity
@Table(name="MONTHLY_SHIFT_SCHEDULE_LINE")
public class MonthlyShiftScheduleLine extends BaseDomain {
	private Integer monthlyShiftScheduleId;
	private Integer employeeId;
	private Integer employeeShiftId;
	private Employee employee;
	private EmployeeShift employeeShift;

	public enum FIELD {
		id, monthlyShiftScheduleId, employeeId, employeeShiftId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "MONTHLY_SHIFT_SCHEDULE_LINE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "MONTHLY_SHIFT_SCHEDULE_ID", columnDefinition="int(10)")
	public Integer getMonthlyShiftScheduleId() {
		return monthlyShiftScheduleId;
	}
	
	public void setMonthlyShiftScheduleId(Integer monthlyShiftScheduleId) {
		this.monthlyShiftScheduleId = monthlyShiftScheduleId;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MonthlyShiftScheduleLine [monthlyShiftScheduleId=").append(monthlyShiftScheduleId)
				.append(", employeeId=").append(employeeId).append(", employeeShiftId=").append(employeeShiftId)
				.append("]");
		return builder.toString();
	}
}
