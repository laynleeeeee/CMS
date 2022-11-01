package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SalesOrderItemDao;
import eulap.eb.domain.hibernate.SalesOrderItem;

/**
 * Implementation class for {@link SalesOrderItemDaoImpl}

 *
 */
public class SalesOrderItemDaoImpl extends BaseDao<SalesOrderItem> implements SalesOrderItemDao {

	@Override
	protected Class<SalesOrderItem> getDomainClass() {
		return SalesOrderItem.class;
	}

}
