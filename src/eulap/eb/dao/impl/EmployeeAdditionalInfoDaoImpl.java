package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeAdditionalInfoDao;
import eulap.eb.domain.hibernate.EmployeeAdditionalInfo;

/**
 * Implementing class of {@link EmployeeAdditionalInfoDao}

 *
 */
public class EmployeeAdditionalInfoDaoImpl extends BaseDao<EmployeeAdditionalInfo> implements EmployeeAdditionalInfoDao {

	@Override
	protected Class<EmployeeAdditionalInfo> getDomainClass() {
		return EmployeeAdditionalInfo.class;
	}

}
