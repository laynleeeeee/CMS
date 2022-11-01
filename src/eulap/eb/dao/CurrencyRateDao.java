package eulap.eb.dao;

import java.util.Date;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.CurrencyRate;

/**
 * Data Access Object of {@link CurrencyRate}

 *
 */

public interface CurrencyRateDao extends Dao<CurrencyRate>{

	/**
	 * Get the list of {@link CurrencyRate} based on the parameter.
	 * @param dateFrom The date from.
	 * @param dateTo The date to.
	 * @param currencyId The {@link Currency} id.
	 * @param status The {@link CurrencyRate} status.
	 * @param pageSetting The {@link PageSetting}.
	 * @return The list of {@link CurrencyRate} in page format.
	 */
	Page<CurrencyRate> searchCurrencyRate(Date dateFrom, Date dateTo, Integer currencyId,
			SearchStatus status, PageSetting pageSetting);

	/**
	 * Determine if {@link CurrencyRate} is unique.
	 * @param currencyRateId The {@link CurrencyRate}.
	 * @param date The date.
	 * @param currencyId The {@link Currency} id.
	 * @return True if {@link CurrencyRate} is unique, otherwise false.
	 */
	boolean isUnique(Integer currencyRateId, Date date, Integer currencyId);

	/**
	 * Get the latest currency rate object
	 * @param currencyId The currency id
	 * @return The latest currency rate object
	 */
	CurrencyRate getLatestRate(Integer currencyId);
}
