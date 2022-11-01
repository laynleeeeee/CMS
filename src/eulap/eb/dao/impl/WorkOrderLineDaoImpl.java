package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.WorkOrderLineDao;
import eulap.eb.domain.hibernate.WorkOrderLine;

/**
 * DAO implementation class for {@link WorkOrderLineDao}

 */

public class WorkOrderLineDaoImpl extends BaseDao<WorkOrderLine> implements WorkOrderLineDao {

	@Override
	protected Class<WorkOrderLine> getDomainClass() {
		return WorkOrderLine.class;
	}

}
