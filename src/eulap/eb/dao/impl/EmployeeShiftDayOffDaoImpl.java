package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeShiftDayOffDao;
import eulap.eb.domain.hibernate.EmployeeShiftDayOff;
/**
 * DAO implementation of {@link EmployeeShiftDayOffDao0}

 */
public class EmployeeShiftDayOffDaoImpl extends BaseDao<EmployeeShiftDayOff> implements EmployeeShiftDayOffDao{

	@Override
	protected Class<EmployeeShiftDayOff> getDomainClass() {
		return EmployeeShiftDayOff.class;
	}
}