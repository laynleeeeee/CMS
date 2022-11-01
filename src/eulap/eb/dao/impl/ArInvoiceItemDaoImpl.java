package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArInvoiceItemDao;
import eulap.eb.domain.hibernate.ArInvoiceItem;

/**
 * Implementing class of {@link ArInvoiceItemDao}

 */

public class ArInvoiceItemDaoImpl extends BaseDao<ArInvoiceItem> implements ArInvoiceItemDao {

	@Override
	protected Class<ArInvoiceItem> getDomainClass() {
		return ArInvoiceItem.class;
	}

}
