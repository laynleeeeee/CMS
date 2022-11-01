package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeShiftAdditionalPayDao;
import eulap.eb.domain.hibernate.EmployeeShiftAdditionalPay;

/**
 * DAO Implementation of {@link EmployeeShiftAdditionalPayDao}

 *
 */
public class EmployeeShiftAdditionalPayDaoImpl extends BaseDao<EmployeeShiftAdditionalPay> implements EmployeeShiftAdditionalPayDao{

	@Override
	protected Class<EmployeeShiftAdditionalPay> getDomainClass() {
		return EmployeeShiftAdditionalPay.class;
	}

	@Override
	public EmployeeShiftAdditionalPay getByShift(int employeeShiftId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeShiftAdditionalPay.FIELD.employeeShiftId.name(), 
				employeeShiftId));
		return get(dc);
	}
}
