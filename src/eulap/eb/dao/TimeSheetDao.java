package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.TimeSheet;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.TimeSheetTemplateDto;

/**
 * Data access of {@link TimeSheet}

 *
 */
public interface TimeSheetDao extends Dao<TimeSheet>{

	/**
	 * Get the paged list of requests per type.
	 * @param searchParam The search parameter.
	 * @param statuses The form status ids.
	 * @param pageSetting The page settings.
	 * @return The paged list of request per type.
	 */
	Page<TimeSheet> getTimeSheets(ApprovalSearchParam searchParam, List<Integer> statuses, PageSetting pageSetting);

	/**
	 * Get the list of time sheets.
	 * @param searchCriteria The Search criteria.
	 * @param user The logged user/
	 * @return The list of time sheets.
	 */
	List<TimeSheet> searchTimeSheets(String searchCriteria, User user);

	/**
	 * Get the paged list of time sheets as reference for payroll.
	 * @param companyId The company filter.
	 * @param divisionId The division filter.
	 * @param month The month filter.
	 * @param year The year filter.
	 * @param payrollTimePeriodScheduleId The time period schedule filter.
	 * @param pageSetting The page setting.
	 * @return The paged list of time sheets.
	 */
	Page<TimeSheet> getTimeSheets(Integer companyId, Integer divisionId, Integer month, 
			Integer year, Integer payrollTimePeriodScheduleId, User user, PageSetting pageSetting);

	/**
	 * Checks if the payroll has duplicate time period and time period schedule.
	 * @param timePeriodId The time period id.
	 * @param timePeriodScheduleId The time period schedule id.
	 * @return True if there is already existing, otherwise false.
	 */
	boolean hasExistingTimeSheet (int timeSheetId, int timePeriodId, int timePeriodScheduleId, int divisionId, int companyId);

	/**
	 * Get the id of the time sheet the specified date is a part of.
	 * @param date The date.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The time sheet id.
	 */
	Integer getTimeSheetIdByDate (Date date, int companyId, int divisionId);

	/**
	 * Get time sheet template.
	 * @param payrollTimePeriodId The time period id.
	 * @param payrollTimePeriodScheduleId The time period schedule id.
	 * @param divisionId The division id.
	 * @param companyId The company id.
	 * @return The time sheet template.
	 */
	Page<TimeSheetTemplateDto> getTimesheetTemplateDetails(Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId,
			Integer divisionId, Integer companyId, PageSetting pageSetting);
}
