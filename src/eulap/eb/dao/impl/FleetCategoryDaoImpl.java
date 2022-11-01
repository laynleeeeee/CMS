package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetCategoryDao;
import eulap.eb.domain.hibernate.FleetCategory;

/**
 * DAO Implementation of {@link FleetCategoryDao}

 *
 */
public class FleetCategoryDaoImpl extends BaseDao<FleetCategory> implements FleetCategoryDao {

	@Override
	protected Class<FleetCategory> getDomainClass() {
		return FleetCategory.class;
	}
}
