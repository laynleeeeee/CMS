package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FrequencyReportDto;

/**
 * Data access object of {@link Employee}}

 *
 */
public interface EmployeeDao extends Dao<Employee>{

	/**
	 * Search employee
	 * @param name The name of the employee
	 * @param position The position of the employee
	 * @param divisionId The unique id of division.
	 * @param user The user currently log.
	 * @param status The status of the employee.
	 * @param pageSetting The page setting
	 * @return The list of employees in paged format
	 */
	Page<Employee> searchEmployees(String name, String position, int divisionId, User user, SearchStatus status, PageSetting pageSetting);

	/**
	 * Check if biometric id is unique.
	 * @param employee The employee.
	 * @return True if biometric id is unique, otherwise false.
	 */
	boolean isUniqueBiometricId(Employee employee);

	/**
	 * Get the employee by biometric id.
	 * @param biometricId The biometric id.
	 * @param companyId The company id.
	 * @param employeeTypeId The employeeType id
	 * @param divisionId The division id
	 * @return The employee object
	 */
	Employee getEmployeeByBiometricIdCompAndEmpType(Integer biometricId, Integer companyId,
			Integer employeeTypeId, Integer divisionId);

	/**
	 * Get the employee base on the employee no.
	 * @param employeeNo The employee no.
	 * @param companyId The company id.
	 * @return The employee.
	 */
	Employee getEmployeeByNo(String employeeNo, Integer companyId);

	/**
	 * Check the employee no if it's already use.
	 * @param employee The employee.
	 * @return True if the employee no is unique, otherwise false.
	 */
	boolean isUniqueEmployeeNo(Employee employee);

	/**
	 * Get the list of employees by company, division, and type.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param employeeTypeId The employee type id.
	 * @return The list of employee.
	 */
	List<Employee> getEmployees(Integer companyId, Integer divisionId, Integer employeeTypeId);

	/**
	 * Get list of employees by Payroll Id.
	 * @param payrollId The payroll id.
	 * @return
	 */
	List<Employee> getEmployeesByPayrollId(int payrollId);

	/**
	 * Get the list of employees by name and company
	 * @param companyId The Id of the company associated with the employee.
	 * @param name The name of the employee
	 * @return The list of employees.
	 */
	List<Employee> getEmployeesByName(Integer companyId, String name);

	/**
	 * Get the employee object by company and name.
	 * @param companyId The company id.
	 * @param name The employee name.
	 * @param isFirstNameFirst True if employee name format is fist name first, otherwise false.
	 * @return The employee object.
	 */
	List<Employee> getEmployeeByCompanyAndName(Integer companyId, String name, boolean isFirstNameFirst);

	/**
	 * Get employees daily shift schedule.
	 * @param companyId The company filter.
	 * @param scheduleId The time period schedule id.
	 */
	List<Employee> getNoMonthlySchedule (Integer companyId, Integer scheduleId);

	/**
	 * Get the list of employees by name and company
	 * @param companyId The Id of the company associated with the employee.
	 * @param divisionId The Id of the division.
	 * @param name The name of the employee
	 * @return The list of employees.
	 */
	List<Employee> getEmployeesByName(Integer companyId, Integer divisionId, User user, String name);

	/**
	 * Get the list of absent employees by date.
	 * @param date The date.
	 * @param status The status of the employee
	 * @param dayOfWeek The day of the week.
	 * @param isFirstNameFirst True if the employee name format is first name first, otherwise false.
	 * @param isOrderByLastName true if order by last name, else false
	 * @return The list of the employees.
	 */
	List<FrequencyReportDto> getAbsentEmployees(Integer companyId, Integer divisionId,
			Date date, Integer status, int dayOfWeek, boolean isFirstNameFirst, boolean isOrderByLastName);

	/**
	 * Get list of employees by Time sheet Id.
	 * @param timeSheetId The timeSheet id.
	 * @return The list of employee by time sheet
	 */
	List<Employee> getEmployeesByTimeSheetId(int timeSheetId);

	/**
	 * Evaluates whether the employee name is unique
	 * @param companyId The company Id.
	 * @param completeName The name of the employee.
	 * @return True if unique, otherwise false.
	 */
	Boolean isUniqueEmployeeName(Integer companyId, String completeName);

	/**
	 * Get employees daily shift schedule.
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param scheduleId The time period schedule id.
	 */
	List<Employee> getEmployeeBySchedule (Integer companyId, Integer divisionId,
			Integer scheduleId, boolean hasSchedule);
}
