package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CurrencyDao;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Service class that will handle business logic for {@link Currency}

 */

@Service
public class CurrencyService {
	private static final Logger logger = Logger.getLogger(CurrencyService.class);
	@Autowired
	private CurrencyDao currencyDao;

	/**
	 * Get the {@link Currency} object by id.
	 * @param currencyId The currency id.
	 * @return The {@link Currency} object.
	 */
	public Currency getCurency(Integer currencyId) {
		return currencyDao.get(currencyId);
	}

	/**
	 * Save {@link Currency}.
	 * @param user The logged-in{@link User}
	 * @param currency The {@link Currency} object.
	 */
	public void saveCurrency(User user, Currency currency) {
		logger.debug("Saving the currency.");
		boolean isNewRecord = currency.getId() == 0 ? true : false;
		AuditUtil.addAudit(currency, new Audit(user.getId(), isNewRecord, new Date()));
		currency.setName(StringFormatUtil.removeExtraWhiteSpaces(currency.getName()));
		currency.setDescription(StringFormatUtil.removeExtraWhiteSpaces(currency.getDescription()));
		currency.setSign(StringFormatUtil.removeExtraWhiteSpaces(currency.getSign()));
		currencyDao.saveOrUpdate(currency);
		logger.info("Successfully saved the currency.");
	}

	/**
	 * Check for incorrect data for {@link Currency}.
	 * @param currency The {@link Currency}.
	 * @param errors The {@link Error}.
	 */
	public void validateForm(Currency currency, Errors errors) {
		if(currency.getName() == null || currency.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("CurrencyService.0"));
		} else if(currency.getName().length() > Currency.MAX_CHARACTER_NAME) {
			errors.rejectValue("name", null, null,
				String.format(ValidatorMessages.getString("CurrencyService.4"),
						Currency.MAX_CHARACTER_NAME));
		} else if(!currencyDao.isUnique(currency)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("CurrencyService.3"));
		}

		if(currency.getDescription() == null || currency.getDescription().trim().isEmpty()){
			errors.rejectValue("description", null, null, ValidatorMessages.getString("CurrencyService.1"));
		} else if(currency.getDescription().length() > Currency.MAX_CHARACTER_DESCRIPTION) {
			errors.rejectValue("description", null, null,
				String.format(ValidatorMessages.getString("CurrencyService.5"),
						Currency.MAX_CHARACTER_DESCRIPTION));
		}

		if(currency.getSign() == null || currency.getSign().trim().isEmpty()){
			errors.rejectValue("sign", null, null, ValidatorMessages.getString("CurrencyService.2"));
		} else if(currency.getSign().length() > Currency.MAX_CHARACTER_SIGN) {
			errors.rejectValue("sign", null, null,
				String.format(ValidatorMessages.getString("CurrencyService.6"),
						Currency.MAX_CHARACTER_SIGN));
		}
	}

	/**
	 * Get the list of {@link Currency} in {@link Page} format.
	 * @param name The currency name.
	 * @param description The currency description
	 * @param sign The currency sign.
	 * @param status The currency status.
	 * @param pageNumber The pagenumber.
	 * @return The list of {@link Currency} in {@link Page} format.
	 */
	public Page<Currency> searchCurrency(String name, String description, String sign,
			String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return currencyDao.searchCurrency(name, description, sign,
				searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the list of active currencies. 
	 * Add the inactive currency based on the currencyId parameter.
	 * @param currencyId The currency id.
	 * @return The list of active {@link Currency}.
	 */
	public List<Currency> getActiveCurrencies(Integer currencyId) {
		return getActiveCurrencies(currencyId, false);
	}

	/**
	 * Get the list of active currencies. 
	 * Add the inactive currency based on the currencyId parameter.
	 * @param currencyId The currency id.
	 * @param isExcludePhp True if exclude PHP value, otherwise false
	 * @return The list of active {@link Currency}.
	 */
	public List<Currency> getActiveCurrencies(Integer currencyId, boolean isExcludePhp) {
		return currencyDao.getActiveCurrencies(currencyId, isExcludePhp);
	}

	/**
	 * Check if the currency is PHP.
	 * @param currencyId The currency id.
	 * @return True if the currency id is equal to the default PHP id, otherwise false.
	 */
	public boolean isPhp(int currencyId) {
		if(currencyId == Currency.DEFUALT_PHP_ID) {
			return true;
		}
		return false;
	}
}
