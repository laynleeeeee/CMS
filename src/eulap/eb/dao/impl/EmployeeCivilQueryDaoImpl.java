package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeCivilQueryDao;
import eulap.eb.domain.hibernate.EmployeeCivilQuery;

/**
 * Implementing class of {@link EmployeeCivilQueryDao}

 *
 */
public class EmployeeCivilQueryDaoImpl extends BaseDao<EmployeeCivilQuery> implements EmployeeCivilQueryDao{

	@Override
	protected Class<EmployeeCivilQuery> getDomainClass() {
		return EmployeeCivilQuery.class;
	}

	@Override
	public EmployeeCivilQuery getByEmployee(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeCivilQuery.FIELD.employeeId.name(), employeeId));
		return get(dc);
	}

}
