package eulap.eb.web.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data transfer object for Employee Shift.

 *
 */
public class EmployeeShiftDto implements Serializable{
	private static final long serialVersionUID = 7116112597864219756L;
	private Integer employeeShiftId;
	private Integer companyId;
	private Integer userByUpdatedBy;
	private Integer userByCreatedBy;
	private Integer dayOff;
	private String startShift;
	private String endShift;
	private Boolean nightShift;
	private Double dailyWorkingHours;
	private Date createdDate;
	private Date updatedDate;
	private Boolean active;

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getEmployeeShiftId() {
		return employeeShiftId;
	}

	public void setEmployeeShiftId(Integer employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}

	public Integer getUserByUpdatedBy() {
		return userByUpdatedBy;
	}

	public void setUserByUpdatedBy(Integer userByUpdatedBy) {
		this.userByUpdatedBy = userByUpdatedBy;
	}

	public Integer getUserByCreatedBy() {
		return userByCreatedBy;
	}

	public void setUserByCreatedBy(Integer userByCreatedBy) {
		this.userByCreatedBy = userByCreatedBy;
	}

	public String getStartShift() {
		return startShift;
	}

	public void setStartShift(String startShift) {
		this.startShift = startShift;
	}

	public String getEndShift() {
		return endShift;
	}

	public void setEndShift(String endShift) {
		this.endShift = endShift;
	}

	public Boolean isNightShift() {
		return nightShift;
	}

	public void setNightShift(Boolean nightShift) {
		this.nightShift = nightShift;
	}

	public Double getDailyWorkingHours() {
		return dailyWorkingHours;
	}

	public void setDailyWorkingHours(Double dailyWorkingHours) {
		this.dailyWorkingHours = dailyWorkingHours;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Integer getDayOff() {
		return dayOff;
	}

	public void setDayOff(Integer dayOff) {
		this.dayOff = dayOff;
	}

	public Boolean getNightShift() {
		return nightShift;
	}

	@Override
	public String toString() {
		return "EmployeeShiftDto [employeeShiftId=" + employeeShiftId + ", companyId=" + companyId
				+ ", userByUpdatedBy=" + userByUpdatedBy + ", userByCreatedBy=" + userByCreatedBy + ", startShift="
				+ startShift + ", endShift=" + endShift + ", nightShift=" + nightShift + ", dailyWorkingHours="
				+ dailyWorkingHours + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + ", active="
				+ active + "]";
	}
}
