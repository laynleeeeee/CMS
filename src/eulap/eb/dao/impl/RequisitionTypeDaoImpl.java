package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RequisitionTypeDao;
import eulap.eb.domain.hibernate.RequisitionType;

/**
 * Implementation class for {@link RequisitionTypeDao}

 */
public class RequisitionTypeDaoImpl extends BaseDao<RequisitionType> implements RequisitionTypeDao {

	@Override
	protected Class<RequisitionType> getDomainClass() {
		return RequisitionType.class;
	}

	@Override
	public List<RequisitionType> getRequisitionType() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(RequisitionType.FIELD.active.name(), true));
		dc.addOrder(Order.asc(RequisitionType.FIELD.name.name()));
		return getAll(dc);
	}
}
