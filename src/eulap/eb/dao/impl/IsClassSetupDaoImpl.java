package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.IsClassSetupDao;
import eulap.eb.domain.hibernate.IsClassSetup;

/**
 * Implementing class of {@link IsClassSetupDao}

 *
 */
public class IsClassSetupDaoImpl extends BaseDao<IsClassSetup> implements IsClassSetupDao {

	@Override
	protected Class<IsClassSetup> getDomainClass() {
		return IsClassSetup.class;
	}

	@Override
	public List<IsClassSetup> getIsClassSetups() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.desc(IsClassSetup.FIELD.sequenceOrder.name()));
		return getAll(dc);
	}

}
