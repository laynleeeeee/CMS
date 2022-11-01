package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.UserCustodianDao;
import eulap.eb.dao.UserCustodianLinesDao;
import eulap.eb.domain.hibernate.UserCustodianLines;

/**
 * Implementing class of {@link UserCustodianDao}

 *
 */
public class UserCustodianLinesDaoImpl extends BaseDao<UserCustodianLines> implements UserCustodianLinesDao{

	@Override
	protected Class<UserCustodianLines> getDomainClass() {
		return UserCustodianLines.class;
	}

	@Override
	public List<UserCustodianLines> getUserCustodianLines(Integer userCustodianId, int userId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(userCustodianId != null) {
			dc.add(Restrictions.eq(UserCustodianLines.FIELD.userCustodianId.name(), userCustodianId));
		}
		if(userId != 0) {
			dc.add(Restrictions.eq(UserCustodianLines.FIELD.userId.name(), userId));
		}
		return getAll(dc);
	}
}
