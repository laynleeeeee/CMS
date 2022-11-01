package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeTypeDao;
import eulap.eb.domain.hibernate.EmployeeType;

/**
 * DAO implementation class for {@link EmployeeTypeDaoDao}

 *
 */
public class EmployeeTypeDaoImpl extends BaseDao<EmployeeType> implements EmployeeTypeDao {

	@Override
	protected Class<EmployeeType> getDomainClass() {
		return EmployeeType.class;
	}

}
