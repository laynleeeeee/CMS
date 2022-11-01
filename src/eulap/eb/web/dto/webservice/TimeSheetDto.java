package eulap.eb.web.dto.webservice;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

import eulap.eb.domain.hibernate.EmployeeDtr;

/**
 * Time sheet dto.

 */

public class TimeSheetDto implements Serializable{
	private static final long serialVersionUID = 725097072746635652L;
	@Expose
	private Integer employeeId;
	@Expose
	private String employeeNo;
	@Expose
	private String employeeName;
	@Expose
	private String firstName;
	@Expose
	private String employeeStatus;
	@Expose
	private List<TimeSheetDetailsDto> timeSheetDetailsDtos;
	@Expose
	private List<EmployeeDtr> employeeDtrs;

	public TimeSheetDto() {
	}

	public TimeSheetDto (String employeeNo, String employeeName, List<TimeSheetDetailsDto> timeSheetDetailsDtos, String employeeStatus) {
		this.employeeNo = employeeNo;
		this.employeeName = employeeName;
		this.timeSheetDetailsDtos = timeSheetDetailsDtos;
		this.employeeStatus = employeeStatus;
	}

	public TimeSheetDto (Integer employeeId, String employeeNo, String employeeName, List<TimeSheetDetailsDto> timeSheetDetailsDtos, String employeeStatus) {
		this.employeeId = employeeId;
		this.employeeNo = employeeNo;
		this.employeeName = employeeName;
		this.timeSheetDetailsDtos = timeSheetDetailsDtos;
		this.employeeStatus = employeeStatus;
	}

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

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public List<TimeSheetDetailsDto> getTimeSheetDetailsDtos() {
		return timeSheetDetailsDtos;
	}

	public void setTimeSheetDetailsDtos(
			List<TimeSheetDetailsDto> timeSheetDetailsDtos) {
		this.timeSheetDetailsDtos = timeSheetDetailsDtos;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<EmployeeDtr> getEmployeeDtrs() {
		return employeeDtrs;
	}

	public void setEmployeeDtrs(List<EmployeeDtr> employeeDtrs) {
		this.employeeDtrs = employeeDtrs;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeSheetDto [employeeId=").append(employeeId).append(", employeeNo=").append(employeeNo)
				.append(", employeeName=").append(employeeName).append(", employeeStatus=").append(employeeStatus)
				.append(", timeSheetDetailsDtos=").append(timeSheetDetailsDtos).append("]");
		return builder.toString();
	}
}
