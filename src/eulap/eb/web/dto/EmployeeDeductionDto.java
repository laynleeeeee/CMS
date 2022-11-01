package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.EmployeeDeduction;

/**
 * Employee deduction DTO.

 *
 */
public class EmployeeDeductionDto {
	private Integer employeeId;
	private String employeeNo;
	private String employeeName;
	private Double grossNet;
	private List<EmployeeDeduction> employeeDeductions;
	private List<DeductionType> deductionTypes;

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

	public Double getGrossNet() {
		return grossNet;
	}

	public void setGrossNet(Double grossNet) {
		this.grossNet = grossNet;
	}

	public List<EmployeeDeduction> getEmployeeDeductions() {
		return employeeDeductions;
	}

	public void setEmployeeDeductions(List<EmployeeDeduction> employeeDeductions) {
		this.employeeDeductions = employeeDeductions;
	}

	public List<DeductionType> getDeductionTypes() {
		return deductionTypes;
	}

	public void setDeductionTypes(List<DeductionType> deductionTypes) {
		this.deductionTypes = deductionTypes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeDeductionDto [employeeId=").append(employeeId).append(", employeeNo=")
				.append(employeeNo).append(", employeeName=").append(employeeName).append(", grossNet=")
				.append(grossNet).append(", employeeDeductions=").append(employeeDeductions).append("]");
		return builder.toString();
	}
}
