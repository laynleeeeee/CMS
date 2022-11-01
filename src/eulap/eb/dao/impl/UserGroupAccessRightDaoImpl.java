package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.UserGroupAccessRightDao;
import eulap.eb.domain.hibernate.UserGroupAccessRight;

/**
 * Implementing class of {@link UserGroupAccessRightDao}

 *
 */
public class UserGroupAccessRightDaoImpl extends BaseDao<UserGroupAccessRight> implements UserGroupAccessRightDao {

	@Override
	protected Class<UserGroupAccessRight> getDomainClass() {
		return UserGroupAccessRight.class;
	}

	@Override
	public List<UserGroupAccessRight> getUserGroupAccessRights(
			Integer userGroupId) {
		DetachedCriteria dc = getUgarDc(userGroupId, null);
		return getAll(dc);
	}

	@Override
	public UserGroupAccessRight getUGARByProductKey(Integer productKey) {
		DetachedCriteria dc = getUgarDc(null, productKey);
		return get(dc);
	}

	@Override
	public UserGroupAccessRight getUGARByPKAndUG(Integer userGroupId, Integer productKey) {
		DetachedCriteria dc = getUgarDc(userGroupId, productKey);
		return get(dc);
	}

	private DetachedCriteria getUgarDc(Integer userGroupId, Integer productKey) {
		DetachedCriteria ugarDc = getDetachedCriteria();
		if(userGroupId != null) {
			ugarDc.add(Restrictions.eq(UserGroupAccessRight.FIELD.userGroupId.name(), userGroupId));
		}
		if(productKey != null) {
			ugarDc.add(Restrictions.eq(UserGroupAccessRight.FIELD.productKey.name(), productKey));
		}
		return ugarDc;
	}

	@Override
	public boolean isAdmin(int userGroupId) {
		DetachedCriteria dc = getUgarDc(userGroupId, 0);
		dc.add(Restrictions.eq(UserGroupAccessRight.FIELD.moduleKey.name(), 1));
		UserGroupAccessRight accessRight = get(dc);
		return accessRight != null;
	}

	@Override
	public boolean hasAdminModule(int userGroupId, List<Integer> adminProductKeys) {
		DetachedCriteria dc = getUgarDc(userGroupId, null);
		addAsOrInCritiria(dc, UserGroupAccessRight.FIELD.productKey.name(), adminProductKeys);
		return !getAll(dc).isEmpty();
	}
}
