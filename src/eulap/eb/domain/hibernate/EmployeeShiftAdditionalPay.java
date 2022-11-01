package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Class representation of EMPLOYEE_SHIFT_ADDITIONAL_PAY table from the database.

 *
 */
@Entity
@Table(name="EMPLOYEE_SHIFT_ADDITIONAL_PAY")
public class EmployeeShiftAdditionalPay extends BaseDomain{

	private int employeeShiftId;
	private Double weekendMultiplier;
	private Double holidayMultiplier;

	public enum FIELD {
		id, employeeShiftId, weekendMultiplier, holidayMultiplier
	}

	@Override
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EMPLOYEE_SHIFT_ADDITIONAL_PAY_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EMPLOYEE_SHIFT_ID", columnDefinition = "INT(10)")
	public int getEmployeeShiftId() {
		return employeeShiftId;
	}

	public void setEmployeeShiftId(int employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}

	@Column(name = "WEEKEND_MULTIPLIER", columnDefinition = "DOUBLE")
	public Double getWeekendMultiplier() {
		return weekendMultiplier;
	}

	public void setWeekendMultiplier(Double weekendMultiplier) {
		this.weekendMultiplier = weekendMultiplier;
	}

	@Column(name = "HOLIDAY_MULTIPLIER", columnDefinition = "DOUBLE")
	public Double getHolidayMultiplier() {
		return holidayMultiplier;
	}

	public void setHolidayMultiplier(Double holidayMultiplier) {
		this.holidayMultiplier = holidayMultiplier;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeShiftAdditionalPay [employeeShiftId=")
				.append(employeeShiftId).append(", weekendMultiplier=")
				.append(weekendMultiplier).append(", holidayMultiplier=")
				.append(holidayMultiplier).append("]");
		return builder.toString();
	}
}
