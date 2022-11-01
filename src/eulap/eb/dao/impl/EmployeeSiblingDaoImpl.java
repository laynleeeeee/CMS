package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeSiblingDao;
import eulap.eb.domain.hibernate.EmployeeSibling;

/**
 * Data access object of {@link EmployeeSiblingDao}

 *
 */
public class EmployeeSiblingDaoImpl extends BaseDao<EmployeeSibling> implements EmployeeSiblingDao{

	@Override
	protected Class<EmployeeSibling> getDomainClass() {
		return EmployeeSibling.class;
	}

}
