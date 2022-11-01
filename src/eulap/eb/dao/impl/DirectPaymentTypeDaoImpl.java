package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.DirectPaymentTypeDao;
import eulap.eb.domain.hibernate.DirectPaymentType;

/**
 * DAO Implementation class of {@link DirectPaymentTypeDao}

 *
 */
public class DirectPaymentTypeDaoImpl extends BaseDao<DirectPaymentType> implements DirectPaymentTypeDao{

	@Override
	protected Class<DirectPaymentType> getDomainClass() {
		return DirectPaymentType.class;
	}

}
