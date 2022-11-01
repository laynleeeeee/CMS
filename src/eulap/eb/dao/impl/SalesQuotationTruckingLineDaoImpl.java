package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SalesQuotationTruckingLineDao;
import eulap.eb.domain.hibernate.SalesQuotationTruckingLine;

/**
 * DAO implementation class for {@link SalesQuotationTruckingLineDao}

 */

public class SalesQuotationTruckingLineDaoImpl extends BaseDao<SalesQuotationTruckingLine> implements SalesQuotationTruckingLineDao {

	@Override
	protected Class<SalesQuotationTruckingLine> getDomainClass() {
		return SalesQuotationTruckingLine.class;
	}

}
