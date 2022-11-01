package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer object for time sheet template.

 *
 */
public class TimeSheetTemplateDto {
	private String employeeNo;
	private String employeeName;
	private Date date;
	private Integer biometricId;

	public static TimeSheetTemplateDto getInstanceOf(String employeeNo, Integer biometricId, String employeeName, Date date) {
		TimeSheetTemplateDto dto = new TimeSheetTemplateDto();
		dto.setDate(date);
		dto.setEmployeeName(employeeName);
		dto.setEmployeeNo(employeeNo);
		dto.setBiometricId(biometricId);
		return dto;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getBiometricId() {
		return biometricId;
	}

	public void setBiometricId(Integer biometricId) {
		this.biometricId = biometricId;
	}

	@Override
	public String toString() {
		return "TimeSheetTemplateDto [employeeNo=" + employeeNo + ", employeeName=" + employeeName + ", date=" + date
				+ ", biometricId=" + biometricId + "]";
	}
}
