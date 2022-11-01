package eulap.eb.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.AccountClassDao;
import eulap.eb.domain.hibernate.AccountClass;
import eulap.eb.domain.hibernate.User;

/**
 * The implementation class of {@link AccountClassDao}

 *
 */
public class AccountClassDaoImpl extends BaseDao<AccountClass> implements AccountClassDao{
	@Override
	protected Class<AccountClass> getDomainClass() {
		return AccountClass.class;
	}

	@Override
	public Collection<AccountClass> getAccountClasses(User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountClass.FIELD.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		return getAll(dc);
	}
}
