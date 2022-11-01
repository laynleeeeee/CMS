package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeEducationalAttainmentDao;
import eulap.eb.domain.hibernate.EmployeeEducationalAttainment;

/**
 * Implementing class of {@link EmployeeEducationalAttainmentDao}

 *
 */
public class EmployeeEducationalAttainmentDaoImpl extends BaseDao<EmployeeEducationalAttainment> implements EmployeeEducationalAttainmentDao{

	@Override
	protected Class<EmployeeEducationalAttainment> getDomainClass() {
		return EmployeeEducationalAttainment.class;
	}

	@Override
	public EmployeeEducationalAttainment getByEmployee(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeEducationalAttainment.FIELD.employeeId.name(), employeeId));
		return get(dc);
	}

}
