package eulap.eb.dao.impl;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.CurrencyRateDao;
import eulap.eb.domain.hibernate.CurrencyRate;

/**
 * DAO Implementation of {@link CurrencyRateDao}

 *
 */

public class CurrencyRateDaoImpl extends BaseDao<CurrencyRate> implements CurrencyRateDao{

	@Override
	protected Class<CurrencyRate> getDomainClass() {
		return CurrencyRate.class;
	}

	@Override
	public Page<CurrencyRate> searchCurrencyRate(Date dateFrom, Date dateTo, Integer currencyId, SearchStatus status,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(dateFrom != null && dateTo != null) {
			dc.add(Restrictions.between(CurrencyRate.FIELD.date.name(), dateFrom, dateTo));
		}
		if(currencyId > 0) {
			dc.add(Restrictions.eq(CurrencyRate.FIELD.currencyId.name(), currencyId));
		}
		dc = DaoUtil.setSearchStatus(dc, CurrencyRate.FIELD.active.name(), status);
		dc.addOrder(Order.desc(CurrencyRate.FIELD.date.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUnique(Integer currencyRateId, Date date, Integer currencyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CurrencyRate.FIELD.date.name(), date));
		dc.add(Restrictions.eq(CurrencyRate.FIELD.currencyId.name(), currencyId));
		if(currencyRateId != 0) {
			dc.add(Restrictions.ne(CurrencyRate.FIELD.id.name(), currencyRateId));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public CurrencyRate getLatestRate(Integer currencyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CurrencyRate.FIELD.currencyId.name(), currencyId));
		dc.addOrder(Order.desc(CurrencyRate.FIELD.date.name()));
		dc.getExecutableCriteria(getSession()).setMaxResults(1);
		return get(dc);
	}
}
