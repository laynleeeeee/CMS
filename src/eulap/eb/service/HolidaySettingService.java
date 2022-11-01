package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.HolidaySettingDao;
import eulap.eb.dao.HolidayTypeDao;
import eulap.eb.domain.hibernate.HolidaySetting;
import eulap.eb.domain.hibernate.HolidayType;
import eulap.eb.domain.hibernate.User;

/**
 * Service class that handles the business logic of Holiday Setting module

 *
 */
@Service
public class HolidaySettingService {
	private static final Logger logger = Logger.getLogger(HolidaySettingService.class);

	@Autowired
	private HolidaySettingDao holidaySettingDao;
	@Autowired
	private HolidayTypeDao holidayTypeDao;

	/**
	 * Save the Holiday Setting
	 * @param user The current user
	 * @param holidaySetting The Holiday Setting Object
	 */
	public void saveHolidaySetting(User user, HolidaySetting holidaySetting){
		logger.debug("Saving Holiday Setting.");
		Integer holidaySettingId = holidaySetting.getId();
		boolean isNewRecord = holidaySettingId == 0;
		AuditUtil.addAudit(holidaySetting, new Audit(user.getId(), isNewRecord, new Date()));
		if(!isNewRecord){
			HolidaySetting hSetting = getHolidaySetting(holidaySettingId);
			DateUtil.setCreatedDate(holidaySetting, hSetting.getCreatedDate());
		}
		holidaySetting.setName((StringFormatUtil.removeExtraWhiteSpaces(
				holidaySetting.getName().trim(), null)));
		holidaySetting.setCompanyId(holidaySetting.getCompanyId());
		holidaySetting.setDate(holidaySetting.getDate());
		holidaySettingDao.saveOrUpdate(holidaySetting);
		logger.debug("Holiday Setting Successfully saved.");
	}

	/**
	 * Get the list of Holiday Setting objects based on the given parameters.
	 * @param companyId The id of the company.
	 * @param name The name of the Holiday Setting.
	 * @param holidayTypeId The holiday type id.
	 * @param date The date of the Holiday.
	 * @param status ALL, ACTIVE, INACTIVE.
	 * @param pageNumber The page number.
	 * @return The list of holiday settings.
	 */
	public Page<HolidaySetting> getHolidaySettings(Integer companyId, String name,
			Integer holidayTypeId, Date date, String status, int pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<HolidaySetting> result = holidaySettingDao.getHolidaySettings(companyId, name, holidayTypeId, date, searchStatus,
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		return result;

	}

	/**
	 * The Holiday Setting name.
	 * @param holidaySetting The Holiday Setting object.
	 * @return True if the Holiday Setting name is unique, else false.
	 */
	public boolean isUniqueName(HolidaySetting holidaySetting){
		return holidaySettingDao.isUniqueName(holidaySetting);
	}

	/**
	 * Get the Holiday Setting object by id.
	 * @param holidaySettingId The unique id of {@link HolidaySetting} object.
	 * @return The Holiday Setting object.
	 */
	public HolidaySetting getHolidaySetting(Integer holidaySettingId){
		return holidaySettingDao.get(holidaySettingId);
	}

	/**
	 * Check if the date is a holiday.
	 * @param date The date to be checked.
	 * @param companyId The company id
	 * @return True if holiday, otherwise false.
	 */
	public boolean isHoliday(Date date, Integer companyId){
		return holidaySettingDao.isHoliday(date, companyId);
	}

	/**
	 * Get all active holiday type.
	 * @return The list of holiday type.
	 */
	public List<HolidayType> getAllActiveHolidayType(){
		return holidayTypeDao.getAllActive();
	}
}
