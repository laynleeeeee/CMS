package eulap.eb.web.dto;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;

import eulap.eb.domain.hibernate.Employee;


/**
 * Daily Shift Schedule Line DTO.

 */
public class DailyShiftScheduleDto {
	@Expose
	private Employee employee;
	@Expose
	private Integer employeeId;
	@Expose
	private String employeeName;
	@Expose
	private Integer date;
	@Expose
	private Integer employeeSheet;
	@Expose
	private List<DSSLineDto> dssLineDtos;
	@Expose
	private Date shiftDate;

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public List<DSSLineDto> getDssLineDtos() {
		return dssLineDtos;
	}

	public void setDssLineDtos(List<DSSLineDto> dssLineDtos) {
		this.dssLineDtos = dssLineDtos;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Date getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(Date shiftDate) {
		this.shiftDate = shiftDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DailyShiftScheduleDto [employee=").append(employee)
				.append(", employeeId=").append(employeeId)
				.append(", dssLineDtos=").append(dssLineDtos).append("]");
		return builder.toString();
	}
}
