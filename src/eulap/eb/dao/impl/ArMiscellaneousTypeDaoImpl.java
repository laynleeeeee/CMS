package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArMiscellaneousTypeDao;
import eulap.eb.domain.hibernate.ArMiscellaneousType;

/**
 * Implementing class of {@link ArMiscellaneousTypeDao}

 *
 */
public class ArMiscellaneousTypeDaoImpl extends BaseDao<ArMiscellaneousType> implements ArMiscellaneousTypeDao{

	@Override
	protected Class<ArMiscellaneousType> getDomainClass() {
		return ArMiscellaneousType.class;
	}

}
