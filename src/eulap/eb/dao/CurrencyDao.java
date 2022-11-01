package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Currency;

/**
 * Data Access Object of {@link Currency}

 *
 */

public interface CurrencyDao extends Dao<Currency>{
	/**
	 * Determine if the currency name is unique.
	 * @param currency The {@link Currency}.
	 * @return True if unique, otherwise false.
	 */
	boolean isUnique(Currency currency);

	/**
	 * Get the list of {@link Currency} in {@link Page} format.
	 * @param name The currency name.
	 * @param description The currency description
	 * @param sign The currency sign.
	 * @param status The currency status.
	 * @param pageNumber The pagenumber.
	 * @return The list of {@link Currency} in {@link Page} format.
	 */
	Page<Currency> searchCurrency(String name, String description, String sign,
			SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the list of active currency objects
	 * @param currencyId The currency id
	 * @param isExcludePhp True if exclude PHP value, otherwise false
	 * @return The list of active currency objects
	 */
	List<Currency> getActiveCurrencies(Integer currencyId, boolean isExcludePhp);
}
