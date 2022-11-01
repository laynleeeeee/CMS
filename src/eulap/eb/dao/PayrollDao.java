package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.EmployerEmployeeContributionDTO;

/**
 * DAO (Data-Access-Object) of {@link Payroll}

 */
public interface PayrollDao extends Dao<Payroll>{

	/**
	 * Get the paged of payrolls.
	 * @param searchParam The form plugin parameter filter object.
	 * @return Paged list of payrolls.
	 */
	Page<Payroll> getPayrolls(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Search payroll by sequence number.
	 * @param criteria The search criteria
	 * @param pageSetting The page setting
	 * @return
	 */
	Page<Payroll> searchPayrolls (String criteria, User user, PageSetting pageSetting);

	/**
	 * Get the employer-employee contribution report data
	 * @param companyId The company id
	 * @param month The month
	 * @param year The year
	 * @param timePeriodScheduleId The time period schedule id
	 * @param divisionId The division id
	 * @param pageSetting The page setting
	 * @return The employer-employee contribution report data
	 */
	Page<EmployerEmployeeContributionDTO> getEEContibutions(Integer companyId, Integer month, Integer year,
			Integer timePeriodScheduleId, Integer divisionId, PageSetting pageSetting, boolean isFirstNameFirst);

	/**
	 * Checks if the payroll has duplicate time period and time period schedule.
	 * @param timePeriodId The time period id.
	 * @param timePeriodScheduleId The time period schedule id.
	 * @param divisionId The division id.
	 * @param companyId The company id.
	 * @return True if there is already existing, otherwise false.
	 */
	boolean hasExistingPayroll (int payrollId, int timePeriodId, int timePeriodScheduleId, int divisionId, int companyId);

	/**
	 * Get the payroll by time sheet.
	 * @param timeSheetId The time sheet reference.
	 * @return
	 */
	Payroll getByTimeSheet(int timeSheetId, boolean isExcludeCancelled);

	/**
	 * Get the payroll object.
	 * @param ptpId The payroll time period id.
	 * @param ptpSchedId The payroll time period schedule id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The payroll object.
	 */
	Payroll getPayroll(Integer ptpId, Integer ptpSchedId, Integer companyId, Integer divisionId);

}
