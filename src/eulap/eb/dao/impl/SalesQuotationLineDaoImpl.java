package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SalesQuotationItemDao;
import eulap.eb.dao.SalesQuotationLineDao;
import eulap.eb.domain.hibernate.SalesQuotationLine;

/**
 * DAO implementation class for {@link SalesQuotationItemDao}

 */

public class SalesQuotationLineDaoImpl extends BaseDao<SalesQuotationLine> implements SalesQuotationLineDao {

	@Override
	protected Class<SalesQuotationLine> getDomainClass() {
		return SalesQuotationLine.class;
	}

}
