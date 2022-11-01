package eulap.eb.web.dto;


import java.util.List;

import com.google.gson.annotations.Expose;

import eulap.eb.domain.hibernate.EmployeeShift;

/**
 * Time sheet dto.

 */

public class EmployeeScheduleSheetDto {
	@Expose
	private String employeeNo;
	@Expose
	private String employeeName;
	@Expose
	private String employeeStatus;
	@Expose
	private List<EmployeeShift> employeeShifts;

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

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public List<EmployeeShift> getEmployeeShifts() {
		return employeeShifts;
	}

	public void setEmployeeShifts(List<EmployeeShift> employeeShifts) {
		this.employeeShifts = employeeShifts;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeScheduleSheetDto [employeeNo=")
				.append(employeeNo).append(", employeeName=")
				.append(employeeName).append(", employeeStatus=")
				.append(employeeStatus).append(", employeeShifts=")
				.append(employeeShifts).append("]");
		return builder.toString();
	}
}
