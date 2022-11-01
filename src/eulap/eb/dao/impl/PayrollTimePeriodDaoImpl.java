package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.PayrollTimePeriodDao;
import eulap.eb.domain.hibernate.PayrollTimePeriod;

/**
 * Implementation class of {@link PayrollTimePeriodDao}

 *
 */
public class PayrollTimePeriodDaoImpl extends BaseDao<PayrollTimePeriod> implements PayrollTimePeriodDao{

	@Override
	protected Class<PayrollTimePeriod> getDomainClass() {
		return PayrollTimePeriod.class;
	}

	@Override
	public Page<PayrollTimePeriod> getPayrollTimePeriods(String name, Integer month, Integer year,
			SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(name != null && !name.trim().isEmpty() ){
			dc.add(Restrictions.like(PayrollTimePeriod.FIELD.name.name(),"%"+name+"%"));
		}
		if(!month.equals(-1)) {
			dc.add(Restrictions.eq(PayrollTimePeriod.FIELD.month.name(), month));
		}
		if(!year.equals(-1)) {
			dc.add(Restrictions.eq(PayrollTimePeriod.FIELD.year.name(), year));
		}
		dc = DaoUtil.setSearchStatus(dc, PayrollTimePeriod.FIELD.active.name(), status);
		dc.addOrder(Order.desc(PayrollTimePeriod.FIELD.year.name()));
		dc.addOrder(Order.desc(PayrollTimePeriod.FIELD.month.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueName(PayrollTimePeriod payrollTimePeriod) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollTimePeriod.FIELD.name.name(), payrollTimePeriod.getName().trim()));
		if(payrollTimePeriod.getId() != 0) {
			dc.add(Restrictions.ne(PayrollTimePeriod.FIELD.id.name(), payrollTimePeriod.getId()));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public boolean isUniqueMonthAndYearCombi(PayrollTimePeriod payrollTimePeriod) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.and(Restrictions.eq(PayrollTimePeriod.FIELD.month.name(), payrollTimePeriod.getMonth()),
				Restrictions.eq(PayrollTimePeriod.FIELD.year.name(), payrollTimePeriod.getYear())));
		dc.add(Restrictions.ne(PayrollTimePeriod.FIELD.id.name(), payrollTimePeriod.getId()));
		return getAll(dc).isEmpty();
	}
}
