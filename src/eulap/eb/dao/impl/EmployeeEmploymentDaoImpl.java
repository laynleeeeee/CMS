package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeEmploymentDao;
import eulap.eb.domain.hibernate.EmployeeEmployment;

/**
 * Implementing class of {@link EmployeeEmploymentDao}

 *
 */
public class EmployeeEmploymentDaoImpl extends BaseDao<EmployeeEmployment> implements EmployeeEmploymentDao{

	@Override
	protected Class<EmployeeEmployment> getDomainClass() {
		return EmployeeEmployment.class;
	}

}
