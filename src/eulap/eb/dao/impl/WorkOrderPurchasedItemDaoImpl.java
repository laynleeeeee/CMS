package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.WorkOrderPurchasedItemDao;
import eulap.eb.domain.hibernate.WorkOrderPurchasedItem;

/**
 * DAO Implementation class of {@link WorkOrderPurchasedItemDao}

 *
 */
public class WorkOrderPurchasedItemDaoImpl extends BaseDao<WorkOrderPurchasedItem> implements WorkOrderPurchasedItemDao{

	@Override
	protected Class<WorkOrderPurchasedItem> getDomainClass() {
		return WorkOrderPurchasedItem.class;
	}
}
