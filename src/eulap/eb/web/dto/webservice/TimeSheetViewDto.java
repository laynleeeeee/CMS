package eulap.eb.web.dto.webservice;

import java.util.List;

/**
 * DTO for time sheet view.

 *
 */
public class TimeSheetViewDto {
	private Integer employeeId;
	private String employeeNo;
	private String employeeName;
	private String firstName;
	private List<Double> hoursWorked;
	private List<Double> adjustments;
	private List<Double> overtimes;
	private List<Double> empStats;
	private List<String> statusLabels;
	private Double daysWorked;

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public List<Double> getHoursWorked() {
		return hoursWorked;
	}

	public void setHoursWorked(List<Double> hoursWorked) {
		this.hoursWorked = hoursWorked;
	}

	public List<Double> getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(List<Double> adjustments) {
		this.adjustments = adjustments;
	}

	public List<Double> getOvertimes() {
		return overtimes;
	}

	public void setOvertimes(List<Double> overtimes) {
		this.overtimes = overtimes;
	}

	public List<Double> getEmpStats() {
		return empStats;
	}

	public void setEmpStats(List<Double> empStats) {
		this.empStats = empStats;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public List<String> getStatusLabels() {
		return statusLabels;
	}

	public void setStatusLabels(List<String> statusLabels) {
		this.statusLabels = statusLabels;
	}

	public Double getDaysWorked() {
		return daysWorked;
	}

	public void setDaysWorked(Double daysWorked) {
		this.daysWorked = daysWorked;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeSheetViewDto [employeeId=").append(employeeId).append(", employeeNo=").append(employeeNo)
				.append(", employeeName=").append(employeeName).append(", firstName=").append(firstName)
				.append(", hoursWorked=").append(hoursWorked).append(", adjustments=").append(adjustments)
				.append(", overtimes=").append(overtimes).append(", empStats=").append(empStats)
				.append(", statusLabels=").append(statusLabels).append(", daysWorked=").append(daysWorked).append("]");
		return builder.toString();
	}
}
