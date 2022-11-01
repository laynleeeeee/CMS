package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PurchaseRequisitionItemDao;
import eulap.eb.domain.hibernate.PurchaseRequisitionItem;

/**
 * DAO implementation class for {@link PurchaseRequisitionItemDao}

 */

public class PurchaseRequisitionItemDaoImpl extends BaseDao<PurchaseRequisitionItem> implements PurchaseRequisitionItemDao {

	@Override
	protected Class<PurchaseRequisitionItem> getDomainClass() {
		return PurchaseRequisitionItem.class;
	}

}
