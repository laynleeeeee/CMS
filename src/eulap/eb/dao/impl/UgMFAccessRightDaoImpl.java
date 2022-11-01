package eulap.eb.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.UgMFAccessRightDao;
import eulap.eb.domain.hibernate.UgMFAccessRight;
import eulap.eb.domain.hibernate.User;

/**
 * Implementation class of {@link UgMFAcessRightDao} interface.

 *
 */
public class UgMFAccessRightDaoImpl extends BaseDao<UgMFAccessRight> implements UgMFAccessRightDao{
	
	@Override
	protected Class<UgMFAccessRight> getDomainClass() {
		return UgMFAccessRight.class;
	}

	@Override
	public Collection<UgMFAccessRight> getUGMFAccessRight(User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UgMFAccessRightField.userGroupId.name(), user.getUserGroupId()));
		return getAll(dc);
	}
}
