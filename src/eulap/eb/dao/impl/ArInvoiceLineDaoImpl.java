package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArInvoiceLineDao;
import eulap.eb.domain.hibernate.ArInvoiceLine;

/**
 * Implementing class of {@link ArInvoiceLineDao}

 */

public class ArInvoiceLineDaoImpl extends BaseDao<ArInvoiceLine> implements ArInvoiceLineDao {

	@Override
	protected Class<ArInvoiceLine> getDomainClass() {
		return ArInvoiceLine.class;
	}

}
