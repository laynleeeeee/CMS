package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeRelativeDao;
import eulap.eb.domain.hibernate.EmployeeRelative;

/**
 * Implementing class of {@link EmployeeRelativeDao}

 *
 */
public class EmployeeRelativeDaoImpl extends BaseDao<EmployeeRelative> implements EmployeeRelativeDao{

	@Override
	protected Class<EmployeeRelative> getDomainClass() {
		return EmployeeRelative.class;
	}

}
