package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ApInvoiceItemDao;
import eulap.eb.domain.hibernate.ApInvoiceItem;

/**
 * DAO implementation class for {@link ApInvoiceItemDao}

 */

public class ApInvoiceItemDaoImpl extends BaseDao<ApInvoiceItem> implements ApInvoiceItemDao {

	@Override
	protected Class<ApInvoiceItem> getDomainClass() {
		return ApInvoiceItem.class;
	}

}
