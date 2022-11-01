package eulap.eb.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.UgMAccessRightDao;
import eulap.eb.domain.hibernate.UgMAccessRight;
import eulap.eb.domain.hibernate.User;

/**
 * Implementation class of {@link UgMAcessRightDao} interface.

 *
 */
public class UgMAccessRightDaoImpl extends BaseDao<UgMAccessRight> implements UgMAccessRightDao {
	
	@Override
	protected Class<UgMAccessRight> getDomainClass() {
		return UgMAccessRight.class;
	}

	@Override
	public Collection<UgMAccessRight> getUgMAccessRight(User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UgMAccessRightField.userGroupId.name(), user.getUserGroupId()));
		return getAll(dc);
	}
}
