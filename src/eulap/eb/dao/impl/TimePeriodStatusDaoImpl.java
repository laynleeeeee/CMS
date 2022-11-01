package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.TimePeriodStatusDao;
import eulap.eb.domain.hibernate.TimePeriodStatus;

/**
 * DAO implementation of {@link TimePeriodStatusDao}

 */
public class TimePeriodStatusDaoImpl extends BaseDao<TimePeriodStatus> implements TimePeriodStatusDao{

	@Override
	protected Class<TimePeriodStatus> getDomainClass() {
		return TimePeriodStatus.class;
	}
}