package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.DivisionProjectDao;
import eulap.eb.domain.hibernate.DivisionProject;

/**
 * DAO implementation class of {@link DivisionProjectDao}

 */

public class DivisionProjectDaoImpl extends BaseDao<DivisionProject> implements DivisionProjectDao {

	@Override
	protected Class<DivisionProject> getDomainClass() {
		return DivisionProject.class;
	}

	@Override
	public boolean hasExistingDivProject(Integer customerId) {
		DetachedCriteria dc = getDcDivisionProject(customerId);
		return getAll(dc).size() > 0;
	}

	private DetachedCriteria getDcDivisionProject(Integer customerId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(DivisionProject.FIELD.projectId.name(), customerId));
		return dc;
	}

	@Override
	public DivisionProject getDivisionProject(Integer customerId) {
		DetachedCriteria dc = getDcDivisionProject(customerId);
		return get(dc);
	}

}
