package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RepackingTypeDao;
import eulap.eb.domain.hibernate.RepackingType;

/**
 * DAO implementation class for {@link RepackingTypeDao}

 */

public class RepackingTypeDaoImpl extends BaseDao<RepackingType> implements RepackingTypeDao {

	@Override
	protected Class<RepackingType> getDomainClass() {
		return RepackingType.class;
	}
}
