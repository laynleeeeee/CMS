package eulap.eb.dao.impl;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountTypeDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.AccountType;

/**
 * Implementation class of {@link AccountTypeDao}

 *
 */
public class AccountTypeDaoImpl extends BaseDao<AccountType> implements AccountTypeDao{
	@Override
	protected Class<AccountType> getDomainClass() {
		return AccountType.class;
	}

	@Override
	public Page<AccountType> getAccountTypes(int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountType.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.addOrder(Order.asc(AccountType.FIELD.name.name()));
		return getAll(dc, new PageSetting(1));
	}

	@Override
	public Page<AccountType> searchAccountTypes(String accountTypeName, int normalBalanceId, int serviceLeaseKeyId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (accountTypeName != null && !accountTypeName.isEmpty())
			dc.add(Restrictions.like(AccountType.FIELD.name.name(), "%" + accountTypeName + "%"));
		if (normalBalanceId != -1)
			dc.add(Restrictions.eq(AccountType.FIELD.normalBalanceId.name(), normalBalanceId));
		dc.add(Restrictions.eq(AccountType.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.addOrder(Order.asc(AccountType.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueAccountType(String accountTypeName, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountType.FIELD.name.name(), accountTypeName));
		dc.add(Restrictions.eq(AccountType.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		return getAll(dc).size() < 1;
	}

	@Override
	public Collection<AccountType> getActiveAccountTypes(int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountType.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.add(Restrictions.eq(AccountType.FIELD.active.name(), true));
		dc.addOrder(Order.asc(AccountType.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<AccountType> getAccountTypes(int accountClassId,
			boolean isContraAccount, int serviceLeaseKeyId) {
		DetachedCriteria acctTypeCriteria = DetachedCriteria.forClass(AccountType.class);
		acctTypeCriteria.add(Restrictions.eq(AccountType.FIELD.contraAccount.name(), isContraAccount));
		acctTypeCriteria.add(Restrictions.eq(AccountType.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		return getAll(acctTypeCriteria);
	}

	@Override
	public List<AccountType> getAccountTypes(int accountClassId,
			boolean isContraAccount, int serviceLeaseKeyId, int companyId) {
		DetachedCriteria acctTypeCriteria = DetachedCriteria.forClass(AccountType.class);
		acctTypeCriteria.add(Restrictions.eq(AccountType.FIELD.contraAccount.name(), isContraAccount));
		acctTypeCriteria.add(Restrictions.eq(AccountType.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		DetachedCriteria accountCombi = DetachedCriteria.forClass(AccountCombination.class);
		accountCombi.setProjection((Projections.property(AccountCombination.FIELD.accountId.name())));
		accountCombi.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		DetachedCriteria accountCriteria = DetachedCriteria.forClass(Account.class);
		accountCriteria.add(Subqueries.propertyIn(Account.FIELD.id.name(), accountCombi));
		accountCriteria.setProjection((Projections.property(Account.FIELD.accountTypeId.name())));
		acctTypeCriteria.add(Subqueries.propertyIn(AccountType.FIELD.id.name(), accountCriteria));
		return getAll(acctTypeCriteria);
	}

	@Override
	public List<AccountType> getAllActiveAccountTypes() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountType.FIELD.active.name(), true));
		dc.addOrder(Order.asc(AccountType.FIELD.id.name()));
		return getAll(dc);
	}
}
