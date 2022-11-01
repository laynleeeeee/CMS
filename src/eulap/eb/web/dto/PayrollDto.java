package eulap.eb.web.dto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.EmployeeDeduction;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.web.dto.webservice.EmployeeSalaryDTO;

/**
 * Data transfer object for Payroll.

 *
 */
public class PayrollDto {
	private Payroll payroll;
	private String employeeDeductionJson;
	private List<EmployeeDeduction> employeeDeductions;
	private String employeeSalaryDtoJson;
	private List<EmployeeSalaryDTO> employeeSalaryDtos;

	public Payroll getPayroll() {
		return payroll;
	}

	public void setPayroll(Payroll payroll) {
		this.payroll = payroll;
	}

	public String getEmployeeDeductionJson() {
		return employeeDeductionJson;
	}

	public void setEmployeeDeductionJson(String employeeDeductionJson) {
		this.employeeDeductionJson = employeeDeductionJson;
	}

	public void serializeEmployeeDeductions() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		employeeDeductionJson = gson.toJson(employeeDeductions);
	}

	public void derializeEmployeeDeductions() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EmployeeDeduction>>(){}.getType();
		employeeDeductions = gson.fromJson(employeeDeductionJson, type);
	}

	public List<EmployeeDeduction> getEmployeeDeductions() {
		return employeeDeductions;
	}

	public void setEmployeeDeductions(List<EmployeeDeduction> employeeDeductions) {
		this.employeeDeductions = employeeDeductions;
	}

	public void serializeGvchEmployeeSalaryDtos() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		employeeSalaryDtoJson = gson.toJson(employeeSalaryDtos);
	}

	public void derializeGvchEmployeeSalaryDtos() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<EmployeeSalaryDTO>>(){}.getType();
		employeeSalaryDtos = gson.fromJson(employeeSalaryDtoJson, type);
	}

	public String getEmployeeSalaryDtoJson() {
		return employeeSalaryDtoJson;
	}

	public void setEmployeeSalaryDtoJson(String employeeSalaryDtoJson) {
		this.employeeSalaryDtoJson = employeeSalaryDtoJson;
	}

	public List<EmployeeSalaryDTO> getEmployeeSalaryDtos() {
		return employeeSalaryDtos;
	}

	public void setEmployeeSalaryDtos(List<EmployeeSalaryDTO> employeeSalaryDtos) {
		this.employeeSalaryDtos = employeeSalaryDtos;
	}
}
