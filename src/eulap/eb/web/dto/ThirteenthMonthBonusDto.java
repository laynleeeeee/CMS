package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer object for Thirteen Month Bonus report

 */

public class ThirteenthMonthBonusDto {
	private String employeeId;
	private String employeeName;
	private String employeePosition;
	private Integer monthId;
	private String month;
	private Date firstCutOffDate;
	private Date secondCutOffDate;
	private Double firstCutOffSalary;
	private Double secondCutOffSalary;
	private String department;
	private String orderName;

	public ThirteenthMonthBonusDto (String employeeId, String employeeName, String employeePosition,
			Integer monthId, String month, Date firstCutOffDate, Date secondCutOffDate,
			Double firstCutOffSalary, Double secondCutOffSalary, String department, String orderName) {
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.employeePosition = employeePosition;
		this.monthId = monthId;
		this.month = month;
		this.firstCutOffDate = firstCutOffDate;
		this.secondCutOffDate = secondCutOffDate;
		this.firstCutOffSalary = firstCutOffSalary;
		this.secondCutOffSalary = secondCutOffSalary;
		this.department = department;
		this.orderName = orderName;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeePosition() {
		return employeePosition;
	}

	public void setEmployeePosition(String employeePosition) {
		this.employeePosition = employeePosition;
	}

	public Integer getMonthId() {
		return monthId;
	}

	public void setMonthId(Integer monthId) {
		this.monthId = monthId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Date getFirstCutOffDate() {
		return firstCutOffDate;
	}

	public void setFirstCutOffDate(Date firstCutOffDate) {
		this.firstCutOffDate = firstCutOffDate;
	}

	public Date getSecondCutOffDate() {
		return secondCutOffDate;
	}

	public void setSecondCutOffDate(Date secondCutOffDate) {
		this.secondCutOffDate = secondCutOffDate;
	}

	public Double getFirstCutOffSalary() {
		return firstCutOffSalary;
	}

	public void setFirstCutOffSalary(Double firstCutOffSalary) {
		this.firstCutOffSalary = firstCutOffSalary;
	}

	public Double getSecondCutOffSalary() {
		return secondCutOffSalary;
	}

	public void setSecondCutOffSalary(Double secondCutOffSalary) {
		this.secondCutOffSalary = secondCutOffSalary;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThirteenthMonthBonusDto [employeeId=").append(employeeId).append(", employeeName=")
				.append(employeeName).append(", employeePosition=").append(employeePosition).append(", monthId=")
				.append(monthId).append(", month=").append(month).append(", firstCutOffDate=").append(firstCutOffDate)
				.append(", secondCutOffDate=").append(secondCutOffDate).append(", firstCutOffSalary=")
				.append(firstCutOffSalary).append(", secondCutOffSalary=").append(secondCutOffSalary)
				.append(", department=").append(department).append(", orderName=").append(orderName).append("]");
		return builder.toString();
	}
}
