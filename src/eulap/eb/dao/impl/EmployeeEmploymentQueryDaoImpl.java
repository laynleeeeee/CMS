package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeEmploymentQueryDao;
import eulap.eb.domain.hibernate.EmployeeEmploymentQuery;

/**
 * Implementing class of {@link EmployeeEmploymentQueryDao}

 *
 */
public class EmployeeEmploymentQueryDaoImpl extends BaseDao<EmployeeEmploymentQuery> implements EmployeeEmploymentQueryDao{

	@Override
	protected Class<EmployeeEmploymentQuery> getDomainClass() {
		return EmployeeEmploymentQuery.class;
	}

	@Override
	public EmployeeEmploymentQuery getByEmployee(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeEmploymentQuery.FIELD.employeeId.name(), employeeId));
		return get(dc);
	}

}
