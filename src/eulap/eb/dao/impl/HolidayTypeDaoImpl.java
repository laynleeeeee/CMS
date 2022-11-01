package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.HolidayTypeDao;
import eulap.eb.domain.hibernate.HolidayType;

/**
 * Implementation class of {@link HolidayTypeDao}

 */
public class HolidayTypeDaoImpl extends BaseDao<HolidayType> implements HolidayTypeDao{

	@Override
	protected Class<HolidayType> getDomainClass() {
		return HolidayType.class;
	}
}
