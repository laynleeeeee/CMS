package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeStatusDao;
import eulap.eb.domain.hibernate.EmployeeStatus;

/**
 * Implementation class of {@link EmployeeStatusDao}

 */
public class EmployeeStatusDaoImpl extends BaseDao<EmployeeStatus> implements EmployeeStatusDao{

	@Override
	protected Class<EmployeeStatus> getDomainClass() {
		return EmployeeStatus.class;
	}
}
