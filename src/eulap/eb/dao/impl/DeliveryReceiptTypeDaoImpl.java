package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.DeliveryReceiptTypeDao;
import eulap.eb.domain.hibernate.DeliveryReceiptType;

/**
 * DAO implementation class for {@Link DeliveryReceiptTypeDao}

 */

public class DeliveryReceiptTypeDaoImpl extends BaseDao<DeliveryReceiptType> implements DeliveryReceiptTypeDao {

	@Override
	protected Class<DeliveryReceiptType> getDomainClass() {
		return DeliveryReceiptType.class;
	}

}
