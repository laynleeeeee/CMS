package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SalesOrderLineDao;
import eulap.eb.domain.hibernate.SalesOrderLine;

/**
 * Implementation class for {@link SalesOrderLineDao}

 *
 */
public class SalesOrderLineDaoImpl extends BaseDao<SalesOrderLine> implements SalesOrderLineDao  {

	@Override
	protected Class<SalesOrderLine> getDomainClass() {
		return SalesOrderLine.class;
	}

}
