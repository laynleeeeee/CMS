package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.PayrollEmployeeSalary;
import eulap.eb.web.dto.webservice.EmployeeSalaryDTO;

/**
 * DAO (Data-Access-Object) of {@link PayrollEmployeeSalary}

 */
public interface PayrollEmployeeSalaryDao extends Dao<PayrollEmployeeSalary>{

	/**
	 * Get the list of payroll employee salary order by last name.
	 * @param payrollId The payroll id.
	 * @return The list of payroll employee salary.
	 */
	List<PayrollEmployeeSalary> getPayrollESDOrderByLastName(int payrollId);

	/**
	 * Get the list of payroll employee salary.
	 * @param payrollId The payroll id.
	 * @param isFirstNameFirst True if ordering is by first name first, otherwise false.
	 * @return The list of payroll employee salary.
	 */
	List<PayrollEmployeeSalary> getPayrollESD(int payrollId, boolean isFirstNameFirst);

	/**
	 * Get contributions summary.
	 * @param employeeId The employee filter.
	 * @param payrollTimePeriodId The time period filter.
	 * @return The contribuitions object.
	 */
	EmployeeSalaryDTO getContributionsSummary(int employeeId, int payrollTimePeriodId);

	/**
	 * Get the total salary per branch.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param statusId The employee status id.
	 * @param dateFrom The started.
	 * @param dateTo The date to.
	 * @param isLastNameFirst true if last name first, else false
	 * @param pageSetting The page settings.
	 * @return The list of payroll employee salary per branch.
	 */
	Page<PayrollEmployeeSalary> getTotalSalaryPerBranch(Integer companyId, Integer divisionId, Integer statusId,
			Date dateFrom, Date dateTo, Boolean isLastNameFirst, PageSetting pageSetting);

	/**
	 * Get the previous balance of the employee.
	 * @param employeeId The employee filter.
	 * @param payrollId The payroll filter.
	 * @return The previous balance.
	 */
	double getPrevBalance(int employeeId, int payrollId);

	/**
	 * Get the payroll employee salaries
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @return The list of payroll employee salaries
	 */
	List<PayrollEmployeeSalary> getEmployeeSalaries(Integer companyId, Integer divisionId, Date dateFrom, Date dateTo);

	/**
	 * Get the payroll employee salary object by employee id and payroll id.
	 * @param employeeId The employee id.
	 * @param payrollId The payroll id.
	 * @return The payroll employee salary object.
	 */
	PayrollEmployeeSalary getByEmplAndPayrollId(Integer employeeId, Integer payrollId);
}
