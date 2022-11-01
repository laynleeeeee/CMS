package eulap.eb.web.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data transfer object for Monthly schedule.

 *
 */
public class DTRMonthlyScheduleDto implements Serializable{
	private static final long serialVersionUID = -5000369681107382754L;
	private Integer employeeId;
	private Integer employeeShiftId;
	private Integer timePeriodSchedId;
	private Date dateFrom;
	private Date dateTo;
	private Date updatedDate;
	private Date updatedTimePeriodDate;

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getEmployeeShiftId() {
		return employeeShiftId;
	}

	public void setEmployeeShiftId(Integer employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getUpdatedTimePeriodDate() {
		return updatedTimePeriodDate;
	}

	public void setUpdatedTimePeriodDate(Date updatedTimePeriodDate) {
		this.updatedTimePeriodDate = updatedTimePeriodDate;
	}

	public Integer getTimePeriodSchedId() {
		return timePeriodSchedId;
	}

	public void setTimePeriodSchedId(Integer timePeriodSchedId) {
		this.timePeriodSchedId = timePeriodSchedId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DTRMonthlyShiftScheduleDto [employeeId=").append(employeeId).append(", employeeShiftId=")
				.append(employeeShiftId).append(", dateFrom=").append(dateFrom).append(", dateTo=").append(dateTo)
				.append(", updatedDate=").append(updatedDate).append("]");
		return builder.toString();
	}
}
