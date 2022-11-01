package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.LocationDao;
import eulap.eb.domain.hibernate.Location;

/**
 * Implementation class for {@Link LocationDao}

 *
 */
public class LocationDaoImpl extends BaseDao<Location> implements LocationDao{

	@Override
	protected Class<Location> getDomainClass() {
		return Location.class;
	}
}
