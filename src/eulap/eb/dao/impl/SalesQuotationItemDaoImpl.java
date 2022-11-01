package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SalesQuotationItemDao;
import eulap.eb.domain.hibernate.SalesQuotationItem;

/**
 * DAO implementation class for {@link SalesQuotationItemDao}

 */

public class SalesQuotationItemDaoImpl extends BaseDao<SalesQuotationItem> implements SalesQuotationItemDao {

	@Override
	protected Class<SalesQuotationItem> getDomainClass() {
		return SalesQuotationItem.class;
	}

}
