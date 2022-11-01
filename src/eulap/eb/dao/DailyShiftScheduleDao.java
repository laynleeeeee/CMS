package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.DailyShiftSchedule;
import eulap.eb.domain.hibernate.User;

public interface DailyShiftScheduleDao extends Dao<DailyShiftSchedule> {

	/**
	 * Search Daily shift schedule.
	 * @param companyId The company id.
	 * @param month The month.
	 * @param year The year.
	 * @param user The user.
	 * @param pageSetting The page settings.
	 * @return The list of daily shift schedule.
	 */
	Page<DailyShiftSchedule> getDailyShiftScheduleLines(Integer companyId, Integer month,
			Integer year, User user, PageSetting pageSetting, boolean isAddOrder);

	/**
	 * Check if the daily schedule is unique.
	 * @return True if unique, otherwise false.
	 */
	boolean isUniqueSchedule(DailyShiftSchedule shiftSchedule);
}
