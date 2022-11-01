package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Class representation of EMPLOYEE_SHIFT_WORKING_DAY table from the database.

 */
@Entity
@Table(name="EMPLOYEE_SHIFT_WORKING_DAY")
public class EmployeeShiftWorkingDay extends BaseDomain{
	private Integer employeeShiftId;
	private Integer dayOfTheWeek;
	public enum FIELD {id, employeeShiftId, dayOfTheWeek}

	@Override
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EMPLOYEE_SHIFT_WORKING_DAY_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EMPLOYEE_SHIFT_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeShiftId() {
		return employeeShiftId;
	}

	public void setEmployeeShiftId(Integer employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}

	@Column(name = "DAY_OF_THE_WEEK", columnDefinition = "TINYINT(1)")
	public Integer getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public void setDayOfTheWeek(Integer dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeShiftWorkingDay [employeeShiftId=").append(employeeShiftId).append(", dayOfTheWeek=")
				.append(dayOfTheWeek).append(", getId()=").append(getId()).append("]");
		return builder.toString();
	}
}
