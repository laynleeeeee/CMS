package eulap.eb.web.dto;

import java.util.Date;

/**
 * Employee Attendance Report dto.

 *
 */
public class EmployeeAttendanceReportDto {
	private Integer employeeId;
	private String employeeName;
	private Date timestamp;
	private String employeeNo;
	private Double hoursWorked;
	private Double adjustment;
	private String shiftName;
	private Date timeIn;
	private Date timeOut;

	public static EmployeeAttendanceReportDto getInstanceOf(Integer employeeId,
			String employeeName, Date timestamp, String employeeNo, Double hoursWorked, Double adjustment) {
		EmployeeAttendanceReportDto dto = new EmployeeAttendanceReportDto();
		dto.employeeId = employeeId;
		dto.employeeName = employeeName;
		dto.timestamp = timestamp;
		dto.employeeNo = employeeNo;
		dto.hoursWorked = hoursWorked;
		dto.adjustment = adjustment;
		return dto;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public Double getHoursWorked() {
		return hoursWorked;
	}

	public void setHoursWorked(Double hoursWorked) {
		this.hoursWorked = hoursWorked;
	}

	public Double getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(Double adjustment) {
		this.adjustment = adjustment;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public Date getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(Date timeIn) {
		this.timeIn = timeIn;
	}

	public Date getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Date timeOut) {
		this.timeOut = timeOut;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeAttendanceReportDto [employeeId=").append(employeeId).append(", employeeName=")
				.append(employeeName).append(", timestamp=").append(timestamp).append(", employeeNo=")
				.append(employeeNo).append(", hoursWorked=").append(hoursWorked).append(", shiftName=")
				.append(shiftName).append(", timeIn=").append(timeIn).append(", timeOut=").append(timeOut).append("]");
		return builder.toString();
	}
}
