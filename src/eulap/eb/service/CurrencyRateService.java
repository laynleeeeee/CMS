package eulap.eb.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.CurrencyRateDao;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.CurrencyRate;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Service class that will handle business logic for {@link CurrencyRate}

 */

@Service
public class CurrencyRateService {
	private static final Logger logger = Logger.getLogger(CurrencyRateService.class);
	@Autowired
	private CurrencyRateDao currencyRateDao;

	/**
	 * Get the {@link CurrencyRate} object by id.
	 * @param currencyRateId The currency rate id.
	 * @return The {@link CurrencyRate} object.
	 * @throws ParseException 
	 */
	public CurrencyRate getCurencyRate(Integer currencyRateId) throws ParseException {
		CurrencyRate currencyRate = currencyRateDao.get(currencyRateId);
		splitDateAndTime(currencyRate);
		return currencyRate;
	}

	/**
	 * Save {@link CurrencyRate}.
	 * @param user The logged-in{@link User}
	 * @param currency The {@link CurrencyRate} object.
	 */
	public void saveCurrencyRate(User user, CurrencyRate currencyRate) {
		logger.debug("Saving the currency rate.");
		boolean isNewRecord = currencyRate.getId() == 0 ? true : false;
		AuditUtil.addAudit(currencyRate, new Audit(user.getId(), isNewRecord, new Date()));
		if (!isNewRecord) {
			DateUtil.setCreatedDate(currencyRate, currencyRate.getCreatedDate());
		}
		currencyRate.setDate(appendTimeToDate(currencyRate.getDate(), currencyRate.getTime()));
		currencyRateDao.saveOrUpdate(currencyRate);
		logger.info("Successfully saved the currency rate.");
	}

	/**
	 * Add time data to date.
	 * @param date The date.
	 * @param time The time.
	 * @return The formatted date.
	 */
	private Date appendTimeToDate(Date date, String time) {
		if(date != null && !time.trim().isEmpty()) {
			date = DateUtil.appendTimeToDate(time, date);
		}
		return date;
	}

	private void splitDateAndTime(CurrencyRate currencyRate) throws ParseException {
		if(currencyRate.getDate() != null) {
			currencyRate.setTime(getMilitaryTime(currencyRate.getDate()));
			currencyRate.setDate(DateUtil.removeTimeFromDate(currencyRate.getDate()));
		}
	}

	/**
	 * Get the time from the date parameter and convert it in military time format.
	 * @param date The date.
	 * @return The time in military time format.
	 * @throws ParseException
	 */
	public String getMilitaryTime(Date date) throws ParseException {
		SimpleDateFormat militaryTimeFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
		date = parseFormat.parse(DateUtil.getTimeFromDate(date));
		return militaryTimeFormat.format(date);
	}


	/**
	 * Check for incorrect data for {@link CurrencyRate}.
	 * @param currencyRate The {@link CurrencyRate}.
	 * @param errors The {@link Error}.
	 */
	public void validateForm(CurrencyRate currencyRate, Errors errors) {
		if(currencyRate.getDate() == null) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("CurrencyRateService.0"));
		} else if(!currencyRate.getDate().equals(DateUtil.removeTimeFromDate(new Date()))) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("CurrencyRateService.7"));
		}

		if(currencyRate.getTime() == null || currencyRate.getTime().isEmpty()) {
			errors.rejectValue("time", null, null, ValidatorMessages.getString("CurrencyRateService.1"));
		}

		if(currencyRate.getCurrencyId() == null) {
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("CurrencyRateService.3"));
		}

		if(currencyRate.getRate() == null) {
			errors.rejectValue("rate", null, null, ValidatorMessages.getString("CurrencyRateService.4"));
		} else if(currencyRate.getRate() <= 0) {
			errors.rejectValue("rate", null, null, ValidatorMessages.getString("CurrencyRateService.6"));
		}

		if(currencyRate.getDate() != null && !currencyRate.getTime().isEmpty() && currencyRate.getCurrencyId() != null ) {
			Date date = appendTimeToDate(currencyRate.getDate(), currencyRate.getTime());
			if(!currencyRateDao.isUnique(currencyRate.getId(), date, currencyRate.getCurrencyId())) {
				errors.rejectValue("rate", null, null, ValidatorMessages.getString("CurrencyRateService.5"));
			}
		}
	}

	/**
	 * Get the list of {@link CurrencyRate} based on the parameter.
	 * @param dateFrom The date from.
	 * @param dateTo The date to.
	 * @param currencyId The {@link Currency} id.
	 * @param status The {@link CurrencyRate} status.
	 * @param pageSetting The {@link PageSetting}.
	 * @return The list of {@link CurrencyRate} in page format.
	 */
	public Page<CurrencyRate> searchCurrencyRate(Date dateFrom, Date dateTo, String timeFrom, String timeTo, Integer currencyId,
			String status, Integer pageNumber) {
		dateFrom = appendTimeToDate(dateFrom, timeFrom);
		timeTo = timeTo == null || timeTo.trim().isEmpty() ? "23:59" : timeTo;//set end time filter to 23:59 if empty.
		dateTo = appendTimeToDate(dateTo, timeTo);
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<CurrencyRate> currencyRates = currencyRateDao.searchCurrencyRate(dateFrom, dateTo, currencyId, searchStatus,
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		return currencyRates;
	}

	/**
	 * Get the latest currency rate object
	 * @param currencyId The currency id
	 * @return The latest currency rate object
	 */
	public CurrencyRate getLatestRate(Integer currencyId) {
		return currencyRateDao.getLatestRate(currencyId);
	}
}
