package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.UserCompanyHeadDao;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.domain.hibernate.UserCompanyHead;

/**
 * Implements the data access layer of the {@link UserCompanyHeadDao}

 *
 */
public class UserCompanyHeadDaoImpl extends BaseDao<UserCompanyHead> implements UserCompanyHeadDao{
	@Override
	protected Class<UserCompanyHead> getDomainClass() {
		return UserCompanyHead.class;
	}

	@Override
	public UserCompanyHead getUserCompanyHeadPerUser(Integer userId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria dcUserComp = DetachedCriteria.forClass(UserCompany.class);
		dcUserComp.setProjection(Projections.property(UserCompany.FIELD.userCompanyHeadId.name()));
		dcUserComp.add(Restrictions.eq(UserCompany.FIELD.userId.name(), userId));
		dc.add(Subqueries.propertyIn(UserCompanyHead.Field.id.name(), dcUserComp));
		return get(dc);
	}
}
