package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RepackingFinishedGoodDao;
import eulap.eb.domain.hibernate.RepackingFinishedGood;

/**
 * DAO implementation class for {@link RepackingFinishedGoodDao}

 */

public class RepackingFinishedGoodDaoImpl extends BaseDao<RepackingFinishedGood> implements RepackingFinishedGoodDao {

	@Override
	protected Class<RepackingFinishedGood> getDomainClass() {
		return RepackingFinishedGood.class;
	}

}
