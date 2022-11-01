package eulap.eb.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.web.dto.FormDetailsDto;

/**
 * Data access object of {@link TimePeriod}

 */
public interface TimePeriodDao extends Dao<TimePeriod>{

	/**
	 * Get all the time periods.
	 * @return Paged time periods.
	 */
	Page<TimePeriod> getAllTimePeriods();

	/**
	 * Get all the time periods arranged by 
	 * latest first.
	 * @return List of time periods.
	 */
	List<TimePeriod> getTimePeriods();

	/**
	 * Search for time periods.
	 * @param name The name of the time period.
	 * @param periodStatusId The time period status Id {Open, Closed, Never Opened}.
	 * @param dateFrom Start date of the time period date range.
	 * @param dateTo  End date of the time period date range.
	 * @param pageSetting The page setting.
	 * @return The paged search result.
	 */
	Page<TimePeriod> searchTimePeriods(String name, int periodStatusId, Date dateFrom,
			Date dateTo, PageSetting pageSetting);

	/**
	 * Evaluate if the name is unique.
	 * @param name The name of the time period.
	 * @return True if unique otherwise, false.
	 */
	boolean isUniqueName (String name);

	/**
	 * Evaluate if the time period is valid.
	 * <br>Time period should not be in-range or equal to an existing time period.
	 * @param timePeriod The time period to be evaluated.
	 * @return True if time period is valid, otherwise false.
	 */
	boolean isValidTimePeriod (TimePeriod timePeriod);

	/**
	 * Get the list of all open time periods
	 * @return Open time periods.
	 */
	Collection<TimePeriod> getOpenTimePeriods ();

	/**
	 * Get the current time periods and previous period order by the latest period.
	 * @param date The current date.
	 * @return The current time periods and previous period.
	 */
	List<TimePeriod> getCurrentTimePeriods(Date date);

	/**
	 * Get the list of forms that have not yet tagged
	 * as completed in {@link FormWorkflow}
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @return The list of form with incomplete status within date range
	 */
	List<FormDetailsDto> getUnpostedForms(Date dateFrom, Date dateTo);
}