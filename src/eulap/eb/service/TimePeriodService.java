package eulap.eb.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.TimePeriodDao;
import eulap.eb.dao.TimePeriodStatusDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.domain.hibernate.TimePeriodStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormDetailsDto;

/**
 * The business logic of Time Period.

 */
@Service
public class TimePeriodService {
	@Autowired
	private TimePeriodDao timePeriodDao;
	@Autowired
	private TimePeriodStatusDao periodStatusDao;

	/**
	 * Get all time periods by descending date from.
	 * @return The list of time periods.
	 */
	public List<TimePeriod> getTimePeriods () {
		return (List<TimePeriod>) timePeriodDao.getTimePeriods();
	}

	/**
	 * Get all the time periods.
	 * @return Paged collection of time periods.
	 */
	public Page<TimePeriod> getAllTimePeriods() {
		return timePeriodDao.getAllTimePeriods();
	}

	/**
	 * Get all the time period statuses.
	 * @return Collection of statuses.
	 */
	public Collection<TimePeriodStatus> getAllPeriodStatuses() {
		return periodStatusDao.getAll();
	}

	/**
	 * Search the time periods.
	 * @param name The name of the time period.
	 * @param periodStatusId The statusId of the time period.
	 * @param startDate Start date of the date range.
	 * @param endDate End date of the date range.
	 * @param pageNumber The pageNumber.
	 * @return The paged result based on the search criteria.
	 */
	public Page<TimePeriod> searchTimePeriods(String name, int periodStatusId, String startDate,
			String endDate, int pageNumber) {
		Date dateFrom = DateUtil.isvalidDateFormat(startDate) ? DateUtil.parseDate(startDate): null;
		Date dateTo = DateUtil.isvalidDateFormat(endDate) ? DateUtil.parseDate(endDate): null;
		return timePeriodDao.searchTimePeriods(name.trim(), periodStatusId, dateFrom, dateTo, new PageSetting(pageNumber));
	}

	/**
	 * Get the time period by its Id.
	 * @param timePeriodId The Id of the time period.
	 * @return The time period object.
	 */
	public TimePeriod getPeriodById(int timePeriodId) {
		return timePeriodDao.get(timePeriodId);
	}

	/**
	 * Save the time period.
	 * @param timePeriod The time period object.
	 * @param user The logged user.
	 */
	public void saveTimePeriod(TimePeriod timePeriod, User user) {
		boolean isNewRecord = timePeriod.getId() == 0 ? true : false;
		AuditUtil.addAudit(timePeriod, new Audit(user.getId(), isNewRecord, new Date()));
		timePeriod.setName(timePeriod.getName().trim());
		timePeriodDao.saveOrUpdate(timePeriod);
	}

	/**
	 * Evaluate if the time period is a valid period.
	 * <br>Valid period:
	 * <pre>
	 *     {@link TimePeriod#getDateFrom()} and {@link TimePeriod#getDateTo()} are not part of any existing period or
	 *     are not included in the range of an existing period.
	 * </pre>
	 * @param timePeriod The period that will be evaluated.
	 * @return True for valid period, otherwise false.
	 */
	public boolean isValidPeriod (TimePeriod timePeriod) {
		return timePeriodDao.isValidTimePeriod(timePeriod);
	}

	/**
	 * Evaluate if the name is unique or not.
	 * @param name The name of the period
	 * @return true if unique otherwise, false.
	 */
	public boolean isUniqueName (TimePeriod timePeriod) {
		if(timePeriod.getId() != 0){
			TimePeriod existingPeriod = timePeriodDao.get(timePeriod.getId());
			if (timePeriod.getName().trim().equalsIgnoreCase(existingPeriod.getName()))
				return true;
		}
		return timePeriodDao.isUniqueName(timePeriod.getName());
	}

	/**
	 * Validate the GL Date if it is in open time period.
	 * @param glDate The date to be validated.
	 * @return True if it is in an open time period, otherwise false.
	 */
	public boolean isOpenDate (Date glDate) {
		Collection<TimePeriod> openTimePeriods = timePeriodDao.getOpenTimePeriods();
		for(TimePeriod tp : openTimePeriods) {
			Date dateBefore = DateUtil.sqlDateToJavaDate(tp.getDateFrom());
			Date dateAfter = DateUtil.sqlDateToJavaDate(tp.getDateTo());
			if((dateBefore.before(glDate) && dateAfter.after(glDate))
					|| dateBefore.equals(glDate) || dateAfter.equals(glDate))
				return true;
		}
		return false;
	}

	/**
	 * Get the current time periods and previous period order by the latest period.
	 * @return The current time periods and previous period.
	 */
	public List<TimePeriod> getCurrentTimePeriods() {
		Date curTimePeriod = new Date();
		return timePeriodDao.getCurrentTimePeriods(curTimePeriod);
	}

	/**
	 * Get the list of forms that have not yet tagged
	 * as completed in {@link FormWorkflow}
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @return The list of form with incomplete status within date range
	 */
	public List<FormDetailsDto> getUnpostedForms(Date dateFrom, Date dateTo) {
		return timePeriodDao.getUnpostedForms(dateFrom, dateTo);
	}
}