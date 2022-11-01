package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeFamilyDao;
import eulap.eb.domain.hibernate.EmployeeFamily;

/**
 * Implementing class of {@link EmployeeFamilyDao}

 *
 */
public class EmployeeFamilyDaoImpl extends BaseDao<EmployeeFamily> implements EmployeeFamilyDao{

	@Override
	protected Class<EmployeeFamily> getDomainClass() {
		return EmployeeFamily.class;
	}

	@Override
	public EmployeeFamily getByEmployee(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeFamily.FIELD.employeeId.name(), employeeId));
		return get(dc);
	}

}
