package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArReceiptTypeDao;
import eulap.eb.domain.hibernate.ArReceiptType;

/**
 * Implementing class of {@link ArReceiptTypeDao}

 *
 */
public class ArReceiptTypeDaoImpl extends BaseDao<ArReceiptType> implements ArReceiptTypeDao {

	@Override
	protected Class<ArReceiptType> getDomainClass() {
		return ArReceiptType.class;
	}

}
