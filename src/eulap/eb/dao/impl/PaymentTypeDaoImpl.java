package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PaymentTypeDao;
import eulap.eb.domain.hibernate.PaymentType;

/**
 * Implementation class of {@link PaymentTypeDao}

 *
 */
public class PaymentTypeDaoImpl extends BaseDao<PaymentType> implements PaymentTypeDao{

	@Override
	protected Class<PaymentType> getDomainClass() {
		return PaymentType.class;
	}

}
