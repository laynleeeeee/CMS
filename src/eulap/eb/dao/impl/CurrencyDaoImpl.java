package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CurrencyDao;
import eulap.eb.domain.hibernate.Currency;

/**
 * DAO Implementation of {@link CurrencyDao}

 *
 */

public class CurrencyDaoImpl extends BaseDao<Currency> implements CurrencyDao{

	@Override
	protected Class<Currency> getDomainClass() {
		return Currency.class;
	}

	@Override
	public boolean isUnique(Currency currency) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Currency.FIELD.name.name(), StringFormatUtil.removeExtraWhiteSpaces(currency.getName())));
		if(currency.getId() != 0) {
			dc.add(Restrictions.ne(Currency.FIELD.id.name(), currency.getId()));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public Page<Currency> searchCurrency(String name, String description, String sign, SearchStatus status,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!name.trim().isEmpty()) {
			dc.add(Restrictions.like(Currency.FIELD.name.name(), StringFormatUtil.appendWildCard(name.trim())));
		}
		if(!description.trim().isEmpty()) {
			dc.add(Restrictions.like(Currency.FIELD.description.name(), StringFormatUtil.appendWildCard(description.trim())));
		}
		if(!sign.trim().isEmpty()) {
			dc.add(Restrictions.like(Currency.FIELD.sign.name(), StringFormatUtil.appendWildCard(sign.trim())));
		}
		dc = DaoUtil.setSearchStatus(dc, Currency.FIELD.active.name(), status);
		dc.addOrder(Order.asc(Currency.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<Currency> getActiveCurrencies(Integer currencyId, boolean isExcludePhp) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(Currency.FIELD.active.name(), true);
		if (currencyId != null) {
			criterion = Restrictions.or(criterion, Restrictions.and(Restrictions.eq(Currency.FIELD.id.name(), currencyId),
					Restrictions.eq(Currency.FIELD.active.name(), false)));
		} else {
			if (isExcludePhp) {
				dc.add(Restrictions.ne(Currency.FIELD.id.name(), Currency.DEFUALT_PHP_ID)); // exclude PHP currency
			}
		}
		dc.add(criterion);
		return getAll(dc);
	}
}
