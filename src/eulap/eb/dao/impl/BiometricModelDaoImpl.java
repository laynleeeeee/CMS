package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.BiometricModelDao;
import eulap.eb.domain.hibernate.BiometricModel;

/**
 * Implementation class of {@link BiometricModelDao}

 */
public class BiometricModelDaoImpl extends BaseDao<BiometricModel> implements BiometricModelDao{

	@Override
	protected Class<BiometricModel> getDomainClass() {
		return BiometricModel.class;
	}
}