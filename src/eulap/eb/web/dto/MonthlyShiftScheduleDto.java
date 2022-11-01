package eulap.eb.web.dto;

import java.util.List;

import com.google.gson.annotations.Expose;

import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.MonthlyShiftSchedule;

/**
 * Data Transfer Object for {@link MonthlyShiftSchedule}

 *
 */
public class MonthlyShiftScheduleDto {
	@Expose
	private Employee employee;
	@Expose
	private Integer employeeId;
	@Expose
	private String employeeName;
	@Expose
	private List<EmployeeShift> employeeShifts;
	@Expose
	private List<DSSLineDto> dssLineDtos;

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

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public List<EmployeeShift> getEmployeeShifts() {
		return employeeShifts;
	}

	public void setEmployeeShifts(List<EmployeeShift> employeeShifts) {
		this.employeeShifts = employeeShifts;
	}

	public List<DSSLineDto> getDssLineDtos() {
		return dssLineDtos;
	}

	public void setDssLineDtos(List<DSSLineDto> dssLineDtos) {
		this.dssLineDtos = dssLineDtos;
	}

	@Override
	public String toString() {
		return "MonthlyShiftScheduleDto [employee=" + employee + ", employeeId=" + employeeId + ", employeeName="
				+ employeeName + ", employeeShifts=" + employeeShifts + ", dssLineDtos=" + dssLineDtos + "]";
	}

}
