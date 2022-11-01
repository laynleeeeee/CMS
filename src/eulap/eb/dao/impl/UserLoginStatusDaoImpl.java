package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.UserLoginStatusDao;
import eulap.eb.domain.hibernate.UserLoginStatus;

import java.util.List;

/**
 * Implements the data access layer of the {@link UserLoginStatusDao}

 *
 */
public class UserLoginStatusDaoImpl extends BaseDao<UserLoginStatus> implements UserLoginStatusDao{

	@Override
	public boolean isExisting(int userId) {
		String sql = "{call isExistingUser(?)}";
		Session session = null;
		boolean isExisting = false;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, userId);
			List<?> list = query.list();
			isExisting = list.get(0) != null ? (Boolean) list.get(0) : false;  
		} finally {
			if (session != null)
				session.close();
		}
		return isExisting;
	}

	@Override
	protected Class<UserLoginStatus> getDomainClass() {
		return UserLoginStatus.class;
	}

	@Override
	public int getFailedLoginAttempts(int userId) {
		String sql = "{call getFailedLoginAttempts(?)}";
		Session session = null;
		int failedLoginAttempt = 0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, userId);
			List<?> list = query.list();
			failedLoginAttempt = list.get(0) != null ? (Integer) list.get(0) : 0;  
		} finally {
			if (session != null)
				session.close();
		}
		return failedLoginAttempt;
	}

	@Override
	public UserLoginStatus getUserLoginStatus(int userId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq("userId", userId));
		return get(dc);
	}
}
