package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EmployeeDeduction;

/**
 * Data Access Object interface of {@link EmployeeDeduction}

 *
 */
public interface EmployeeDeductionDao extends Dao<EmployeeDeduction>{

	/**
	 * Initialize employee deductions.
	 * @param employeeId The employee filter.
	 * @param dateFrom The beginning of date range filter.
	 * @param dateTo The end of date range filter.
	 * @return The list of employee deductions.
	 */
	List<EmployeeDeduction> initEmployeeDeductions (Integer employeeId, Date dateFrom, Date dateTo);

	/**
	 * Get the list of employee deductions.
	 * @param payrollId The payroll id.
	 * @param employeeId The employee id.
	 * @return The list of employee deductions.
	 */
	List<EmployeeDeduction> getEmployeeDeductions(Integer payrollId, Integer employeeId);

	/**
	 * Get the list of employee deductions.
	 * @param payrollId The payroll id.
	 * @param employeeId The employee id.
	 * @return The list of employee deductions
	 * @param deductionTypeId The deductionTypeId
	 * @return The list of employee deductions.
	 */
	List<EmployeeDeduction> getEmployeeDeductions(Integer payrollId, Integer employeeId, Integer deductionTypeId);

	/**
	 * Get the list of active employee deductions
	 * @param payrollId The payroll id
	 * @return The list of active employee deductions
	 */
	List<EmployeeDeduction> getActiveEmployeeDeductions (Integer payrollId);
}
