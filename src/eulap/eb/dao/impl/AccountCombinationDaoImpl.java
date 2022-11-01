package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;

/**
 * The implementation class of {@link AccountCombinationDao}

 *
 */
public class AccountCombinationDaoImpl extends BaseDao<AccountCombination> implements AccountCombinationDao{

	@Override
	protected Class<AccountCombination> getDomainClass() {
		return AccountCombination.class;
	}

	/**
	 * Get all account combinations.
	 * @return The page collection of account combinations.
	 */
	@Override
	public Page<AccountCombination> getAccountCombinations(User user) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		dc.add(Restrictions.eq(AccountCombination.FIELD.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		dc.createAlias("company", "c").addOrder(Order.asc("c.companyNumber"));
		dc.createAlias("division", "d").addOrder(Order.asc("d.number"));
		dc.createAlias("account", "a").addOrder(Order.asc("a.number"));
		return getAll(dc, new PageSetting(1));
	}

	/**
	 *  Search/filter account combinations based on the parameters.
	 *  @return Paged collection of account combination.
	 */
	@Override
	public Page<AccountCombination> searchAccountCombinations(
			String companyNumber, String divisionNumber, String accountNumber,
			String companyName, String divisionName, String accountName,
			int serviceLeaseKeyId, SearchStatus status, PageSetting pageSetting, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		dc.add(Restrictions.eq(AccountCombination.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.createAlias("company", "c");
		dc.createAlias("division", "d");
		dc.createAlias("account", "a");
		dc = DaoUtil.setSearchStatus(dc,Account.FIELD.active.name(),status);
		if (!companyNumber.isEmpty())
			dc.add(Restrictions.like("c.companyNumber", "%" + companyNumber + "%"));
		if (!divisionNumber.isEmpty())
			dc.add(Restrictions.like("d.number", "%" + divisionNumber + "%"));
		if (!accountNumber.isEmpty())
			dc.add(Restrictions.like("a.number", "%" + accountNumber + "%"));
		if (!companyName.isEmpty())
			dc.add(Restrictions.like("c.name", "%" + companyName + "%"));
		if (!divisionName.isEmpty())
			dc.add(Restrictions.like("d.name", "%" + divisionName + "%"));
		if (!accountName.isEmpty())
			dc.add(Restrictions.like("a.accountName", "%" + accountName + "%"));
		dc.addOrder(Order.asc("c.companyNumber"));
		dc.addOrder(Order.asc("d.number"));
		dc.addOrder(Order.asc("a.number"));
		return getAll(dc, pageSetting);
	}

	/**
	 * Check if the account combination is unique or not.
	 * @return True if the account combination number is unique, otherwise false.
	 */
	@Override
	public boolean isUniqueAccountCombination(AccountCombination accountCombination) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), accountCombination.getCompanyId()));
		dc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), accountCombination.getDivisionId()));
		dc.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountCombination.getAccountId()));
		return getAll(dc).size() < 1;
	}

	@Override
	public AccountCombination getAccountCombination(int companyId,
			int divisionId, int accountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		dc.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		return get(dc);
	}

	@Override
	public AccountCombination getAccountCombination(final String companyNumber,
			final String divisionNumber, final String accountNumber) {
		return getHibernateTemplate().execute(new HibernateCallback<AccountCombination>() {
			@Override
			public AccountCombination doInHibernate(Session session) 
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(AccountCombination.class);
				if (companyNumber != null && !companyNumber.isEmpty()) 
					criteria.createAlias("company", "c").add(Restrictions.eq("c.companyNumber", companyNumber));
				if (!divisionNumber.isEmpty())
					criteria.createAlias("division", "d").add(Restrictions.eq("d.number", divisionNumber));
				if (!accountNumber.isEmpty())
					criteria.createAlias("account", "a").add(Restrictions.eq("a.number", accountNumber));
				criteria.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
				AccountCombination accountCombination = get(criteria);
				if (accountCombination != null) {
					if (accountCombination.getAccount().getRelatedAccountId() != null)
						getHibernateTemplate().initialize(accountCombination.getAccount().getRelatedAccount());
					else
						accountCombination.getAccount().setRelatedAccount (null);
				}
				return accountCombination;
			}
		});
	}

	@Override
	public Collection<AccountCombination> getAccountCombinations(int companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		return getAll(dc);
	}

	@Override
	public AccountCombination getAcctCombiBySupplierAcctId(int supplierAccountId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria supplierAcctCriteria = DetachedCriteria.forClass(SupplierAccount.class);
		supplierAcctCriteria.add(Restrictions.eq(SupplierAccount.FIELD.id.name(), supplierAccountId));
		supplierAcctCriteria.setProjection(Projections.property(SupplierAccount.FIELD.defaultDebitACId.name()));
		dc.add(Subqueries.propertyIn(AccountCombination.FIELD.id.name(), supplierAcctCriteria));
		return get(dc);
	}

	@Override
	public AccountCombination getACFromArLine(int accountCombinationId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria arLineCriteria = DetachedCriteria.forClass(ArLineSetup.class);
		arLineCriteria.add(Restrictions.eq(ArLineSetup.FIELD.accountCombinationId.name(), accountCombinationId));
		arLineCriteria.setProjection(Projections.property(ArLineSetup.FIELD.accountCombinationId.name()));
		dc.add(Subqueries.propertyIn(AccountCombination.FIELD.id.name(), arLineCriteria));
		return get(dc);
	}

	@Override
	public AccountCombination getAcctCombiByCustomerAcctId(int customerAccountId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria customerAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
		customerAcctCriteria.add(Restrictions.eq(ArCustomerAccount.FIELD.id.name(), customerAccountId));
		customerAcctCriteria.setProjection(Projections.property(ArCustomerAccount.FIELD.defaultDebitACId.name()));
		dc.add(Subqueries.propertyIn(AccountCombination.FIELD.id.name(), customerAcctCriteria));
		return get(dc);
	}

	@Override
	public List<AccountCombination> getAccountCombinations(Integer companyId,
			Integer divisionId, String accountName, Integer limit) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		dc.createAlias("account", "a");
		dc.add(Restrictions.eq("a.active", true));
		if(accountName != null && !accountName.trim().isEmpty()){
			dc.add(Restrictions.or(Restrictions.like("a.accountName", "%"+accountName.trim()+"%"),
					Restrictions.like("a.number", "%"+accountName.trim()+"%")));
			dc.add(Restrictions.or(Restrictions.like("a.accountName", "%"+accountName.trim()+"%"),
							Restrictions.like("a.number", "%"+accountName.trim()+"%")));
		}
		if(limit != null){
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		dc.addOrder(Order.asc("a.number"));
		return getAll(dc);
	}

	@Override
	public Collection<Company> getCompanyByAcctCombination(
			boolean isActiveOnly, User user) {
		
		DetachedCriteria companyCriteria = DetachedCriteria.forClass(Company.class);
		// For account combination
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		acctCombinationCriteria.setProjection(Projections.property("companyId"));
		companyCriteria.add(Subqueries.propertyIn("id", acctCombinationCriteria));
		companyCriteria.add(Restrictions.eq(Company.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		if (isActiveOnly) {
			companyCriteria.add(Restrictions.eq(Company.Field.active.name(), true));
		}
		companyCriteria.addOrder(Order.asc(Company.Field.companyNumber.name()));
		return getHibernateTemplate().findByCriteria(companyCriteria);
	}
}
