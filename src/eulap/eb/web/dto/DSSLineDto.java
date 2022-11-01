package eulap.eb.web.dto;

import java.util.Date;

import com.google.gson.annotations.Expose;

import eulap.eb.domain.hibernate.DailyShiftScheduleLine;

/**
 * A data transfer object class for {@link DailyShiftScheduleLine}

 */

public class DSSLineDto {
	@Expose
	private Date date;
	@Expose
	private Integer employeeShiftId;
	@Expose
	private String shiftName;
	@Expose
	private boolean active;
	@Expose
	private Integer origEmployeeShiftId;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getEmployeeShiftId() {
		return employeeShiftId;
	}

	public void setEmployeeShiftId(Integer employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getOrigEmployeeShiftId() {
		return origEmployeeShiftId;
	}

	public void setOrigEmployeeShiftId(Integer origEmployeeShiftId) {
		this.origEmployeeShiftId = origEmployeeShiftId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DSSLineDto [date=").append(date).append(", employeeShiftId=").append(employeeShiftId)
				.append(", shiftName=").append(shiftName).append(", active=").append(active)
				.append(", origEmployeeShiftId=").append(origEmployeeShiftId).append("]");
		return builder.toString();
	}
}
