package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.BusRegTypeDao;
import eulap.eb.domain.hibernate.BusinessRegistrationType;

public class BusRegTypeDaoImpl extends BaseDao<BusinessRegistrationType> implements BusRegTypeDao{

	@Override
	protected Class<BusinessRegistrationType> getDomainClass() {
		return BusinessRegistrationType.class;
	}

	@Override
	public List<BusinessRegistrationType> getBusRegType() {
		DetachedCriteria dc = getDetachedCriteria();
		return getAll(dc);
	}

	@Override
	public BusinessRegistrationType getBusRegType(Integer busRegTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(BusinessRegistrationType.FIELD.id.name(), busRegTypeId));
		dc.addOrder(Order.asc(BusinessRegistrationType.FIELD.name.name()));
		return get(dc);
	}
}
