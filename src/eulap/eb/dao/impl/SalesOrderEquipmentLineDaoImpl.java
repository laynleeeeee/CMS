package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SalesOrderEquipmentLineDao;
import eulap.eb.domain.hibernate.SalesOrderEquipmentLine;

/**
 * Implementation class for {@link SalesOrderEquipmentLineDao}

 *
 */
public class SalesOrderEquipmentLineDaoImpl extends BaseDao<SalesOrderEquipmentLine> implements SalesOrderEquipmentLineDao  {

	@Override
	protected Class<SalesOrderEquipmentLine> getDomainClass() {
		return SalesOrderEquipmentLine.class;
	}

}
