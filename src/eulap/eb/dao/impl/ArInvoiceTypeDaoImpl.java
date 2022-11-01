package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArInvoiceTypeDao;
import eulap.eb.domain.hibernate.ArInvoiceType;

/**
 * DAO implementation class for {@Link ArInvoiceTypeDao}

 */

public class ArInvoiceTypeDaoImpl extends BaseDao<ArInvoiceType> implements ArInvoiceTypeDao {

	@Override
	protected Class<ArInvoiceType> getDomainClass() {
		return ArInvoiceType.class;
	}

}
