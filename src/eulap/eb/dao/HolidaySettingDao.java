package eulap.eb.dao;

import java.util.Date;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.HolidaySetting;

/**
 *  Data access object of {@link HolidaySetting}

 *
 */
public interface HolidaySettingDao extends Dao<HolidaySetting>{

	/**
	 * Check if the name is unique
	 * @return True if name is unique, otherwise false.
	 */
	boolean isUniqueName(HolidaySetting holidaySetting);

	/**
	 * Get the list of holiday settings.
	 * @param companyId The Company Id
	 * @param name The name of Holiday
	 * @param holidayTypeId The holiday type id.
	 * @param date The date of Holiday
	 * @param status The status of Holiday Setting, ACTIVE, INACTIVE
	 * @param pageSetting The page setting.
	 * @return The list of Holiday Settings.
	 */
	Page<HolidaySetting> getHolidaySettings(Integer companyId ,String name,
			Integer holidayTypeId, Date date, SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the holiday setting.
	 * @param date the date.
	 * @return The holiday setting.
	 */
	HolidaySetting getByDate (Date date);

	/**
	 * Check if the date is a holiday.
	 * @param date The date to be checked.
	 * @param companyId The company id of the holiday.
	 * @return True if holiday, otherwise false.
	 */
	boolean isHoliday(Date date, Integer companyId);
}
