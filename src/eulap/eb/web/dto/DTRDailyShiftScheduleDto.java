package eulap.eb.web.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data transfer object for Available leaves.

 *
 */
public class DTRDailyShiftScheduleDto implements Serializable{
	private static final long serialVersionUID = -3252136071092557849L;
	private Integer employeeId;
	private Integer employeeShiftId;
	private Date startShift;
	private Date endShift;
	private Date updatedDate;

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Date getStartShift() {
		return startShift;
	}

	public void setStartShift(Date startShift) {
		this.startShift = startShift;
	}

	public Date getEndShift() {
		return endShift;
	}

	public void setEndShift(Date endShift) {
		this.endShift = endShift;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getEmployeeShiftId() {
		return employeeShiftId;
	}

	public void setEmployeeShiftId(Integer employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}

	@Override
	public String toString() {
		return "DTRDailyShiftScheduleDto [employeeId=" + employeeId + ", employeeShiftId=" + employeeShiftId
				+ ", startShift=" + startShift + ", endShift=" + endShift + ", updatedDate=" + updatedDate + "]";
	}
}
