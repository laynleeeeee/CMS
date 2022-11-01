package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArInvoiceTruckingLineDao;
import eulap.eb.domain.hibernate.ArInvoiceTruckingLine;

/**
 * DAO Implementation class of {@link ArInvoiceTruckingLineDao}

 *
 */
public class ArInvoiceTruckingLineDaoImpl extends BaseDao<ArInvoiceTruckingLine> implements ArInvoiceTruckingLineDao {

	@Override
	protected Class<ArInvoiceTruckingLine> getDomainClass() {
		return ArInvoiceTruckingLine.class;
	}
}
