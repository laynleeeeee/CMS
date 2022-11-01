package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SalesOrderTruckingLineDao;
import eulap.eb.domain.hibernate.SalesOrderTruckingLine;

/**
 * Implementation class for {@link SalesOrderTruckingLineDao}

 *
 */
public class SalesOrderTruckingLineDaoImpl extends BaseDao<SalesOrderTruckingLine> implements SalesOrderTruckingLineDao  {

	@Override
	protected Class<SalesOrderTruckingLine> getDomainClass() {
		return SalesOrderTruckingLine.class;
	}

}
