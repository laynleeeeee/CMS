package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.EmployeeDtr;
import eulap.eb.web.dto.DTRDailyShiftScheduleDto;
import eulap.eb.web.dto.DTRMonthlyScheduleDto;
import eulap.eb.web.dto.EmployeeAttendanceReportDto;

/**
 * Data access object for {@link EmployeeDtr}

 *
 */
public interface EmployeeDtrDao extends Dao<EmployeeDtr> {

	/**
	 * Get the log time in string format.
	 * @param employeeId The employee filter.
	 * @param date The date filter.
	 * @return The log time.
	 */
	List<String> getLogTimes (int employeeId, Date date);

	/**
	 * Get the paged list of employee attendance.
	 * @param companyId The company id filter.
	 * @param divisionId The division id filter.
	 * @param dateFrom The date range from.
	 * @param dateTo The date range to.
	 * @param isLastNameFirst true if last name first, else false
	 * @param pageSetting The page setting
	 * @return The paged list of employee attendance.
	 */
	Page<EmployeeAttendanceReportDto> getEmployeeAttendanceReport(Integer companyId, 
			Integer divisionId, Date dateFrom, Date dateTo, Boolean isLastNameFirst, PageSetting pageSetting);

	/**
	 * Get the list of employee dtrs by company, division, beginning date, and end date.
	 * @param companyId The company filter.
	 * @param divisionId The division filter.
	 * @param dateFrom The beginning date filter.
	 * @param dateTo The end date filter.
	 * @param timeSheetId The time sheet id.
	 * @return List of {@link EmployeeDtr}
	 */
	List<EmployeeDtr> getEmployeeDtrs (Integer companyId, Integer divisionId, Date dateFrom, Date dateTo, Integer timeSheetId);

	/**
	 * Get the list of the latest employees daily shift.
	 * @param updatedDate The last updated date.
	 * @return The list of daily shift schedule.
	 */
	List<DTRDailyShiftScheduleDto> getLatestDailyShifts(Date updatedDate);

	/** 
	 * Get the list of employee dtrs by company, division, beginning date, and end date.
	 * @param companyId The company filter.
	 * @param divisionId The division filter.
	 * @param dateFrom The beginning date filter.
	 * @param dateTo The end date filter.
	 * @return List of {@link EmployeeDtr}
	 */
	List<EmployeeDtr> getEmployeeDtrs(Integer companyId, Integer divisionId, Date dateFrom, Date dateTo, Boolean isActive);

	/**
	 * Get the list of latest monthly shift schedule.
	 * @param updateddate The updated date.
	 * @param updatedTimePeriodDate The payroll time period updated date.
	 * @return List of {@link DTRMonthlyScheduleDto}
	 */
	List<DTRMonthlyScheduleDto> getLatestMonthlySheds(Date updateddate, Date updatedTimePeriodDate);

	/**
	 * Check if there is already a log.
	 * @param employeeId The employee id.
	 * @param date The date.
	 * @return True or false.
	 */
	boolean isExistingLog(Integer employeeId, Date date);

	/**
	 * Get the list of employee DTR 
	 * @param timeSheetId The Time Sheet ID
	 * @return employee DTR
	 */
	List<EmployeeDtr> geEmployeeDtrsByTimeSheet(int timeSheetId);

	/**
	 * Get the list of employee DTR by employee ID
	 * @param companyId The company ID
	 * @param divisionId The Division ID
	 * @param dateFrom Start date
	 * @param dateTo end date
	 * @param employeeId the Employee ID
	 * @return list of employee DTR
	 */
	List<EmployeeDtr> getEmployeeDtrsByEmployee(Integer companyId, Integer divisionId, Date dateFrom, Date dateTo,
			Integer employeeId);
}
