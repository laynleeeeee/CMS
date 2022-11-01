package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.DailyShiftScheduleLine;
import eulap.eb.web.dto.MaxDayPerScheduleLine;

/**
 * Data access object of {@link DailyShiftScheduleLine}}

 *
 */
public interface DailyShiftScheduleLineDao extends Dao<DailyShiftScheduleLine> {

	/**
	 * Get daily shift schedule details
	 * @param employeeId The employee id
	 * @param date The date
	 * @return The daily shift schedule details
	 */
	DailyShiftScheduleLine getDailyShiftSchedLine(Integer employeeId, Date date);

	/**
	 * Get the list of daily shift schedule line.
	 * @param dailyShiftScheduleId The daily shift schedule id.
	 * @return The list of daily shift schedule line.
	 */
	List<DailyShiftScheduleLine> getAllDailyShiftShedule(int dailyShiftScheduleId, boolean isFirstNameFirst);

	/**
	 * Get the max day per schedule line.
	 * @param payrollTimePeriodScheduleLineId The payroll time period schedule line id.
	 * @return The max day per schedule.
	 */
	MaxDayPerScheduleLine getMaxDayPerLine(int payrollTimePeriodScheduleLineId);
}