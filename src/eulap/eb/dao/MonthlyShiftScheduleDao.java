package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.MonthlyShiftSchedule;
import eulap.eb.domain.hibernate.User;

/**
 * DAO for {@link MonthlyShiftSchedule}

 *
 */
public interface MonthlyShiftScheduleDao extends Dao<MonthlyShiftSchedule> {

	/**
	 * Search Monthly shift schedule.
	 * @param companyId The company id.
	 * @param month The month.
	 * @param year The year.
	 * @param user The user.
	 * @param pageSetting The page settings.
	 * @return The list of daily shift schedule.
	 */
	Page<MonthlyShiftSchedule> getMonthlyShiftScheduleLines(Integer companyId, Integer month, Integer year, SearchStatus searchStatus, User user, PageSetting pageSetting);

	/**
	 * Check if the daily schedule is unique.
	 * @return True if unique, otherwise false.
	 */
	boolean isUniqueSchedule(MonthlyShiftSchedule shiftSchedule);
}
