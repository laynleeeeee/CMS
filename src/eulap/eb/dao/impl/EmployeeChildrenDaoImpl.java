package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeChildrenDao;
import eulap.eb.domain.hibernate.EmployeeChildren;

/**
 * Data access object of {@link EmployeeChildren}

 *
 */
public class EmployeeChildrenDaoImpl extends BaseDao<EmployeeChildren> implements EmployeeChildrenDao{

	@Override
	protected Class<EmployeeChildren> getDomainClass() {
		return EmployeeChildren.class;
	}

}
