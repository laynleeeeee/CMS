package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.User;

public interface DeductionTypeDao extends Dao<DeductionType>{
	/**
	 * Searches for deduction types.
	 * @param user The user currently logged in.
	 * @param name The deduction type name.
	 * @param status The search status.
	 * @param setting The page setting.
	 * @return A paged list of deduction type based on the search criteria.
	 */
	Page<DeductionType> searchDeductionType(User user, String name, SearchStatus status, PageSetting setting);

	/**
	 * Check if the deduction type name is a duplicate.
	 * @param deductionType The deduction type object.
	 * @return True if the name is a duplicate, otherwise false.
	 */
	boolean isDuplicate(DeductionType deductionType);

	/**
	 * Get the list of deduction types.
	 * @param deductionTypeId The deduction Type Id.
	 * @return The list of deduction types.
	 */
	List<DeductionType> getDeductionTypes(Integer deductionTypeId);

	/**
	 * Get the list of deduction types.
	 * @param payrollId The payroll Id.
	 * @return The list of deduction types.
	 */
	List<DeductionType> getDeductionTypesByPayrollId(Integer payrollId);

	/**
	 * Get the deduction types.
	 * @param payrollId The payroll filter.
	 * @param employeeId The employee filter.
	 * @return The list of deduction types.
	 */
	List<DeductionType> getDeductionTypes(Integer payrollId, Integer employeeId);

	/**
	 * Get the list of deduction type from filters.
	 * @param employeeId The employee id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param timePeriodScheduleId The payroll time period schedule id.
	 * @return {@link List<DeductionType>}
	 */
	List<DeductionType> getDeductionTypes(Integer employeeId, Integer companyId, Integer divisionId, Integer timePeriodScheduleId);
}
