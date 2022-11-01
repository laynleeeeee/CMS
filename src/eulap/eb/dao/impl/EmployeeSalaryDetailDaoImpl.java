package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeSalaryDetailDao;
import eulap.eb.domain.hibernate.EmployeeSalaryDetail;

public class EmployeeSalaryDetailDaoImpl extends BaseDao<EmployeeSalaryDetail> implements EmployeeSalaryDetailDao{

	@Override
	protected Class<EmployeeSalaryDetail> getDomainClass() {
		return EmployeeSalaryDetail.class;
	}

	@Override
	public EmployeeSalaryDetail getSalaryDetailByEmployeeId(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeSalaryDetail.FIELD.employeeId.name(), employeeId));
		return get(dc);
	}
}
