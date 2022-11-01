package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CustomerAdvancePaymentItemDao;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentItem;

/**
 * Implementing class of {@link CustomerAdvancePaymentItemDao}

 *
 */
public class CustomerAdvancePaymentItemDaoImpl extends BaseDao<CustomerAdvancePaymentItem> implements
	CustomerAdvancePaymentItemDao{

	@Override
	protected Class<CustomerAdvancePaymentItem> getDomainClass() {
		return CustomerAdvancePaymentItem.class;
	}

	@Override
	public List<CustomerAdvancePaymentItem> getCAPItems(Integer capId,
			Integer itemId, Integer warehouseId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CustomerAdvancePaymentItem.FIELD.customerAdvancePaymentId.name(), capId));
		if(itemId != null) {
			dc.add(Restrictions.eq(CustomerAdvancePaymentItem.FIELD.itemId.name(), itemId));
		}
		if(warehouseId != null) {
			dc.add(Restrictions.eq(CustomerAdvancePaymentItem.FIELD.warehouseId.name(), warehouseId));
		}
		return getAll(dc);
	}

	@Override
	public double getTotalCAPAmount(Integer capId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CustomerAdvancePaymentItem.FIELD.customerAdvancePaymentId.name(), capId));
		dc.setProjection(Projections.sum(CustomerAdvancePaymentItem.FIELD.amount.name()));
		return getBySumProjection(dc);
	}
}
