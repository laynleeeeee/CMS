package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeShiftWorkingDayDao;
import eulap.eb.domain.hibernate.EmployeeShiftWorkingDay;
/**
 * DAO implementation of {@link EmployeeShiftWorkingDayDao}

 */
public class EmployeeShiftWorkingDayDaoImpl extends BaseDao<EmployeeShiftWorkingDay> implements EmployeeShiftWorkingDayDao{

	@Override
	protected Class<EmployeeShiftWorkingDay> getDomainClass() {
		return EmployeeShiftWorkingDay.class;
	}
}