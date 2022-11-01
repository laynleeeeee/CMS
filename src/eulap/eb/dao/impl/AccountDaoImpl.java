package eulap.eb.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.NormalBalance;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.AccountAnalysisReport;
import eulap.eb.web.dto.AccountBalancesDto;
import eulap.eb.web.dto.AccountDto;
import eulap.eb.web.dto.ISAccountDto;
import eulap.eb.web.dto.ISBSAccountDto;
import eulap.eb.web.dto.JournalEntriesRegisterDto;

/**
 * Implementation class of {@link AccountDao}

 *
 */
public class AccountDaoImpl extends BaseDao<Account> implements AccountDao{

	@Override
	protected Class<Account> getDomainClass() {
		return Account.class;
	}

	@Override
	public Page<Account> getAccounts(final User user) {
		return getHibernateTemplate().execute(new HibernateCallback<Page<Account>>() {
			@Override
			public Page<Account> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Account.class);
				criteria.add(Restrictions.eq(Account.FIELD.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
				criteria.addOrder(Order.asc(Account.FIELD.number.name()));
				Page<Account> result = getAll(criteria, new PageSetting(1));
				for (Account account : result.getData()) {
					if (account.getRelatedAccountId() != null)
						getHibernateTemplate().initialize(account.getRelatedAccount());
				}
				return result;
			}
		});
	}

	@Override
	public Page<Account> searchAccounts(int accountTypeId, String accountNumber, String accountName, User user,
			SearchStatus status, boolean isMainAccountOnly, PageSetting pageSetting) {
		DetachedCriteria criteria = getDetachedCriteria();
		if (accountTypeId != -1) {
			criteria.add(Restrictions.eq(Account.FIELD.accountTypeId.name(), accountTypeId));
		}
		if (isMainAccountOnly) {
			criteria.add(Restrictions.isNull(Account.FIELD.parentAccountId.name()));
		}
		if (!accountNumber.isEmpty()) {
			String tmpAccountNumber = accountNumber.replace("%", "\\%").replace("_", "\\_");
			criteria.add(Restrictions.like(Account.FIELD.number.name(), "%" + tmpAccountNumber + "%"));
		}
		if (!accountName.isEmpty()) {
			String tmpAccountName = accountName.replace("%", "\\%").replace("_", "\\_");
			criteria.add(Restrictions.like(Account.FIELD.accountName.name(), "%" + tmpAccountName + "%"));
		}
		criteria.add(Restrictions.eq(Account.FIELD.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		criteria = DaoUtil.setSearchStatus(criteria,Account.FIELD.active.name(),status);
		criteria.addOrder(Order.asc(Account.FIELD.number.name()));
		return getAll(criteria, pageSetting);
	}

	@Override
	public Collection<Account> getNonContraAccounts(int accountId, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.createAlias("accountType", "at").add(Restrictions.eq("at.contraAccount", false));
		if (accountId != 0)
			dc.add(Restrictions.ne(Account.FIELD.id.name(), accountId));
		dc.add(Restrictions.eq(Account.FIELD.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		return getAll(dc);
	}

	@Override
	public boolean isUniqueAccountName(String accountName, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Account.FIELD.accountName.name(), accountName));
		dc.add(Restrictions.eq(Account.FIELD.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		return getAll(dc).size() < 1;
	}

	@Override
	public boolean isUniqueAccountNumber(String accountNumber) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Account.FIELD.number.name(), accountNumber));
		return getAll(dc).size() < 1;
	}

	@Override
	public Collection<Account> getActiveAccounts(User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Account.FIELD.active.name(), true));
		dc.add(Restrictions.eq(Account.FIELD.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		dc.addOrder(Order.asc(Account.FIELD.accountName.name()));
		return getAll(dc);
	}

	@Override
	public List<Account> getAccountByAcctCombination(int companyId,
			int divisionId, boolean isOrderByNumber) {
		DetachedCriteria accountCriteria = getDetachedCriteria();
		// For account combination
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		// For account type
		DetachedCriteria acctTypeCriteria = DetachedCriteria.forClass(AccountType.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		acctCombinationCriteria.setProjection(Projections.property("accountId"));
		acctTypeCriteria.add(Restrictions.eq(AccountType.FIELD.active.name(), true));
		accountCriteria.add(Subqueries.propertyIn("id", acctCombinationCriteria));
		acctTypeCriteria.add(Subqueries.propertyIn("id", acctTypeCriteria));
		accountCriteria.add(Restrictions.eq(Account.FIELD.active.name(), true));
		String fieldOrder = isOrderByNumber ? Account.FIELD.number.name() : Account.FIELD.accountName.name();
		accountCriteria.addOrder(Order.asc(fieldOrder));
		return getAll(accountCriteria);
	}

	@Override
	public List<Account> getAccountsByCompany(String accountName, String accountNumber, int companyId) {
		DetachedCriteria accountCriteria = getDetachedCriteria();
		accountCriteria.add(Restrictions.eq(Account.FIELD.active.name(), true));
		if(accountName != null && !accountName.trim().isEmpty()){
			accountCriteria.add(Restrictions.or(Restrictions.like(Account.FIELD.accountName.name(), "%"+accountName.trim()+"%"),
					Restrictions.like(Account.FIELD.number.name(), "%"+accountName.trim()+"%")));
		}
		// For account combination
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		// For account type
		DetachedCriteria acctTypeCriteria = DetachedCriteria.forClass(AccountType.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.setProjection(Projections.property("accountId"));
		accountCriteria.add(Subqueries.propertyIn("id", acctCombinationCriteria));
		acctTypeCriteria.add(Subqueries.propertyIn("id", acctTypeCriteria));
		accountCriteria.addOrder(Order.asc(Account.FIELD.number.name()));
		return getAll(accountCriteria);
	}

	@Override
	public List<Account> getAccounts(int companyId, int accountTypeId, int serviceLeaseKeyId) {
		return getAll(getAccountCriteria(companyId, accountTypeId, serviceLeaseKeyId));
	}

	@Override
	public List<Account> getContraAccounts (final int accountId) {
		HibernateCallback<List<Account>> hc = new HibernateCallback<List<Account>>() {
			@Override
			public List<Account> doInHibernate(Session session) throws HibernateException,
					SQLException {
				Criteria criteria = session.createCriteria(Account.class);
				criteria.add(Restrictions.eq(Account.FIELD.relatedAccountId.name(), accountId));
				List<Account> accounts = getAllByCriteria(criteria);
				for (Account account : accounts) {
					getHibernateTemplate().initialize(account.getAccountType().getNormalBalance());
				}
				return accounts;
			}
		};
		return getHibernateTemplate().execute(hc);
	}

	@Override
	public Account getAccountByNumber(String accountNumber, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		dc.add(Restrictions.eq(Account.FIELD.number.name(), accountNumber));
		return get(dc);
	}

	public List<Account> getAccounts(int companyId, int serviceLeaseKeyId) {
		DetachedCriteria accountCriteria = DetachedCriteria.forClass(Account.class);
		accountCriteria.add(Restrictions.eq(Account.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));

		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.accountId.name()));

		accountCriteria.add(Subqueries.propertyIn(Account.FIELD.id.name(), acctCombinationCriteria));
		accountCriteria.addOrder(Order.asc(Account.FIELD.number.name()));

		return getAll(accountCriteria);
	}

	@Override
	public Account getAccount(int companyId, int accountTypeId,
			int serviceLeaseKeyId) {
		return get(getAccountCriteria(companyId, accountTypeId, serviceLeaseKeyId));
	}

	private DetachedCriteria getAccountCriteria (int companyId, int accountTypeId,
			int serviceLeaseKeyId) {
		DetachedCriteria accountCriteria = DetachedCriteria.forClass(Account.class);
		accountCriteria.add(Restrictions.eq(Account.FIELD.accountTypeId.name(), accountTypeId));
		accountCriteria.add(Restrictions.eq(Account.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));

		DetachedCriteria accountTypeCriteria = DetachedCriteria.forClass(AccountType.class);
		accountTypeCriteria.add(Restrictions.eq(AccountType.FIELD.contraAccount.name(), false));
		accountTypeCriteria.setProjection(Projections.property(AccountType.FIELD.id.name()));

		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.accountId.name()));

		accountCriteria.add(Subqueries.propertyIn(Account.FIELD.id.name(), acctCombinationCriteria));
		accountCriteria.add(Subqueries.propertyIn(Account.FIELD.accountTypeId.name(), accountTypeCriteria));
		accountCriteria.addOrder(Order.asc(Account.FIELD.number.name()));

		return accountCriteria;
	}

	@Override
	public Account getContraAcctByRelatedAcct(int accountId) {
		DetachedCriteria accountCriteria = DetachedCriteria.forClass(Account.class);
		accountCriteria.add(Restrictions.eq(Account.FIELD.relatedAccountId.name(), accountId));
		return get(accountCriteria);
	}

	@Override
	public Page<AccountBalancesDto> getAccountBalances(int companyId, Date asOfDate, PageSetting pageSetting) {
		//For Account Balances report
		AccountBalancesHandler handler = new  AccountBalancesHandler(companyId, -1, null, asOfDate, null, this);
		return executePagedSP("GET_ACCOUNT_BALANCE", pageSetting, handler, companyId, -1, -1, null, asOfDate);
	}

	@Override
	public Page<AccountBalancesDto> getAccountBalances(int companyId, Integer accountId, int divisionId, Date dateFrom, Date dateTo,
			PageSetting pageSetting) {
		//For Account Balances report
		AccountBalancesHandler handler = new  AccountBalancesHandler(companyId, divisionId, dateFrom, dateTo, accountId, this);
		return executePagedSP("GET_ACCOUNT_BALANCE", pageSetting, handler, companyId, divisionId, accountId, dateFrom, dateTo);
	}

	@Override
	public AccountBalancesDto getAcctBalance(int bankAccountId, int companyId, int accountId, Date asOfDate) {
		List<Object> data = executeSP("GET_BOOK_BALANCE", bankAccountId, 
				companyId, accountId, asOfDate);
		if(!data.isEmpty()) {
			for (Object object : data) {
				Object[] rowResult = (Object[]) object;
				Integer acctId = (Integer) rowResult[0];
				Double debit = (Double) rowResult[1];
				Double credit = (Double) rowResult[2];
				if(acctId != null && debit != null && credit != null) {
					return AccountBalancesDto.getInstance(this.get(acctId), debit, credit, null);
				}
			}
		}
		return null;
	}

	private static class AccountBalancesHandler implements QueryResultHandler<AccountBalancesDto> {
		private int companyId;
		private int divisionId;
		private Date startDate;
		private Date endDate;
		private Integer accountId;
		private AccountDaoImpl accountDaoImpl;

		public AccountBalancesHandler (int companyId, int divisionId, Date startDate, Date endDate, Integer accountId,
				AccountDaoImpl accountDaoImpl) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.startDate = startDate;
			if (this.startDate == null) {
				Calendar cal = Calendar.getInstance();
				cal.set(1900, 01, 01); // Start date will be January 1, 1900
				this.startDate = cal.getTime();
			}
			this.endDate = endDate;
			this.accountId = accountId;
			this.accountDaoImpl = accountDaoImpl;
		}

		@Override
		public List<AccountBalancesDto> convert(List<Object[]> queryResult) {
			Account account = null;
			List<AccountBalancesDto> acctBalancesData = new ArrayList<AccountBalancesDto>();
			int acctId = 0;
			int divId = 0;
			for (Object[] rowResult : queryResult) {
				acctId = (Integer) rowResult[0];
				double debit = NumberFormatUtil.convertBigDecimalToDouble(rowResult[1]);
				double credit = NumberFormatUtil.convertBigDecimalToDouble(rowResult[2]);
				Integer accountTypeId = (Integer) rowResult[4];
				if (accountId != null) {
					if (accountId.equals(acctId)) {
						account = accountDaoImpl.get(acctId);
						acctBalancesData.add(AccountBalancesDto.getInstance(account, divId,
								debit, credit, accountTypeId));
						if (divisionId != -1) {
							break;
						}
					}
				} else {
					account = accountDaoImpl.get(acctId);
					acctBalancesData.add(AccountBalancesDto.getInstance(account, divId,
							debit, credit, accountTypeId));
				}
			}
			return acctBalancesData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index++, companyId);
			if (divisionId != -1) {
				query.setParameter(index++, divisionId);
			}
			query.setParameter(index++, startDate);
			query.setParameter(index++, endDate);
			query.setParameter(index, 1);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("DEBIT", Hibernate.DOUBLE);
			query.addScalar("CREDIT", Hibernate.DOUBLE);
			query.addScalar("ACCT_NO", Hibernate.STRING);
		}
	}

	@Override
	public AccountAnalysisReport getAccountBalance(int companyId, Account account,
			String divisionNumberFrom, String divisionNumberTo, Date asOfDate, String description) {
		description = StringFormatUtil.appendWildCard(description);
		Calendar startDate = Calendar.getInstance();
		startDate.set(1990, 1, 1);

		// This is for JESPGenerator.
		List<Object> ret = executeSP("GET_ACCOUNT_BEGINNING_BALANCE", companyId, account.getId(), divisionNumberFrom,
				divisionNumberTo, startDate.getTime(),asOfDate, description);
		AccountAnalysisReport begBal = new AccountAnalysisReport();
		begBal.setSource("Beginning Balance");
		boolean isNormalBalanceDebit = isNormalBalanceDebit(account);
		double debit = 0;
		double credit = 0;
		boolean isBd = false;
		for (Object obj : ret) {
			// Type casting
			Object[] row = (Object[]) obj;
			Object numObj = (Object) row[0];
			if (numObj == null) {
				debit = 0;
			} else {
				isBd =  numObj instanceof BigDecimal;
				debit =  isBd ? ((BigDecimal) numObj).doubleValue() : (Double) numObj;
			}
			begBal.setDebitAmount(debit);

			numObj = (Object) row[1];
			if (numObj == null) {
				credit = 0;
			} else {
				isBd =  numObj instanceof BigDecimal;
				credit =  isBd ? ((BigDecimal) numObj).doubleValue() : (Double) numObj;
			}

			begBal.setCreditAmount(credit);
			double balance = 0;
			if (isNormalBalanceDebit) {
				balance = debit - credit;
			} else {
				balance = credit - debit;
			}
			begBal.setBalance(balance);
		}
		return begBal;
	}

	private static boolean isNormalBalanceDebit (Account account) {
		return NormalBalance.DEBIT == account.getAccountType().getNormalBalance().getId();
	}

	private static double getBalance (Account account, double debit, double credit) {
		double balance = 0;
		if (isNormalBalanceDebit(account)) {
			balance = debit - credit;
		} else { 
			balance = credit - debit;
		}
		return balance;
	}

	@Override
	public Page<AccountAnalysisReport> getAccountAnalysisReport(int companyId,
			Account account, String divisionNumberFrom, String divisionNumberTo,
			Date fromDate, Date toDate, String description, PageSetting pageSetting) {
		description = StringFormatUtil.appendWildCard(description);
		AccountAnalysisHandler handler = new AccountAnalysisHandler(companyId, account,
				divisionNumberFrom, divisionNumberTo, fromDate, toDate);
		return executePagedSP("GET_ACCOUNT_ANALYSIS", pageSetting, handler, companyId, account.getId(),
				divisionNumberFrom, divisionNumberTo, fromDate, toDate, description);
	}

	private static class AccountAnalysisHandler implements QueryResultHandler<AccountAnalysisReport> {
		private final int companyId;
		private final Account account;
		private final String divisionNumberFrom;
		private final String divisionNumberTo;
		private final Date fromDate; 
		private final Date toDate;

		private AccountAnalysisHandler (int companyId, Account account,  String divisionNumberFrom,
				String divisionNumberTo, Date fromDate, Date toDate) {
			this.companyId = companyId;
			this.account = account;
			this.divisionNumberFrom = divisionNumberFrom;
			this.divisionNumberTo = divisionNumberTo;
			this.fromDate = fromDate;
			this.toDate = toDate;
		}

		@Override
		public List<AccountAnalysisReport> convert(List<Object[]> queryResult) {
			List<AccountAnalysisReport> analysisReport = new ArrayList<AccountAnalysisReport>();
			for (Object[] rowResult : queryResult) {
				String source = (String) rowResult[0];
				String divisionName = (String) rowResult[6];
				Date glDate = (Date) rowResult[9];
				String referenceNo = (String) rowResult[10];
				String description = (String) rowResult[11];
				Double debit = 0.0;
				if(rowResult[12] instanceof BigDecimal) {
					BigDecimal bdDebit = (BigDecimal) rowResult[12];
					debit = bdDebit.doubleValue();
				} else {
					debit = (Double) rowResult[12];
				}
				Double credit = 0.0;
				if(rowResult[13] instanceof BigDecimal) {
					BigDecimal bdDebit = (BigDecimal) rowResult[13];
					credit = bdDebit.doubleValue();
				} else {
					credit = (Double) rowResult[13];
				}
				double balance = getBalance(account, debit, credit);
				analysisReport.add(AccountAnalysisReport.getInstanceOf(source, divisionName, glDate, 
						referenceNo, description, debit, credit, balance));
			}
			return analysisReport;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index++, companyId);
			query.setParameter(index++, account.getId());
			query.setParameter(index++, divisionNumberFrom);
			query.setParameter(index++, divisionNumberTo);
			query.setParameter(index++, DateUtil.deductDaysToDate(fromDate, 1));
			query.setParameter(index++, 1);
			query.setParameter(index++, companyId);
			query.setParameter(index++, account.getId());
			query.setParameter(index++, divisionNumberFrom);
			query.setParameter(index++, divisionNumberTo);
			query.setParameter(index++, fromDate);
			query.setParameter(index++, toDate);
			query.setParameter(index, 1);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SOURCE", Hibernate.STRING);
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("DIVISION_ID", Hibernate.INTEGER);
			query.addScalar("ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("DIVISION_NO", Hibernate.STRING);
			query.addScalar("DIVISION_NAME", Hibernate.STRING);
			query.addScalar("ACCT_NO", Hibernate.STRING);
			query.addScalar("ACCT_NAME", Hibernate.STRING);
			query.addScalar("GL_DATE", Hibernate.DATE);
			query.addScalar("REFERENCE_NUMBER", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("DEBIT", Hibernate.DOUBLE);
			query.addScalar("CREDIT", Hibernate.DOUBLE);
			query.addScalar("FORM_WORKFLOW_ID", Hibernate.INTEGER);
			query.addScalar("CURRENT_STATUS_ID", Hibernate.INTEGER);
			query.addScalar("IS_COMPLETE", Hibernate.BOOLEAN);
			query.addScalar("IS_POSTED", Hibernate.BOOLEAN);
		}
	}

	@Override
	public List<Account> getAcctsByCompanyAndType(Integer companyId, Integer accountTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Account.FIELD.accountTypeId.name(), accountTypeId));
		dc.add(Restrictions.eq(Account.FIELD.active.name(), true));
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.accountId.name()));
		dc.add(Subqueries.propertyIn(Account.FIELD.id.name(), acctCombinationCriteria));
		dc.addOrder(Order.asc(Account.FIELD.number.name()));
		return getAll(dc);
	}

	private ISAccountDto getISAccounts(String storedProc, Integer companyId,
			Integer accountId, Date ... dates) {
		int length = dates.length;
		List<Object> accounts = new ArrayList<Object>();
		if (length > 1)
			accounts = executeSP(storedProc, companyId, accountId, dates[0], dates[1]);
		else
			accounts = executeSP(storedProc, companyId, accountId, dates[0]);
		return getAccounts(accounts);
	}

	private ISAccountDto getAccounts (List<Object> accounts) {
		if (accounts != null && !accounts.isEmpty()) {
			for (Object obj : accounts) {
				// Type casting
				Object[] row = (Object[]) obj;
				int columnNumber = 0;
				Integer acctId = (Integer)row[columnNumber++]; //0
				String accountName = (String)row[columnNumber++]; //1
				Double debit = (Double)row[columnNumber++]; //2
				Double credit = (Double)row[columnNumber++]; //3
				ISAccountDto account = new ISAccountDto(acctId, accountName, debit, credit);
				return account;
			}
		}
		return null;
	}

	@Override
	public ISAccountDto getAsOfBalanceAccount(Integer companyId,
			Integer accountId, Date asOfDate) {
		return getISAccounts("GET_ACCOUNT_BALANCE", companyId, accountId,  asOfDate);
	}

	@Override
	public Account getAccountByName(String name, boolean isActiveOnly) {
		DetachedCriteria accountDc = getDetachedCriteria();
		accountDc.add(Restrictions.eq(Account.FIELD.accountName.name(), name.trim()));
		if (isActiveOnly) {
			accountDc.add(Restrictions.eq(Account.FIELD.active.name(), true));
		}
		return get(accountDc);
	}

	/**
	 * Get the sql statement for either Income Statement or Balance Sheet
	 * @param isIncomeStatement If true, generate sql statement for income statement, otherwise
	 * generate for balance sheet.
	 * @param isContraAccount If true, generate sql statement to get contra accounts, otherwise
	 * generate for normal accounts.
	 * @return The generated sql statement.
	 */
	private String getISOrBSSql (boolean isIncomeStatement, boolean isContraAccount) {
		String sql = "SELECT A.ACCOUNT_ID, A.ACCOUNT_NAME, SUM(DEBIT), SUM(CREDIT) "
				+ "FROM V_JOURNAL_ENTRY JE "
				+ "INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = JE.ACCOUNT_ID "
				+ "WHERE COMPANY_ID = ? "
				+ "AND A." + (isContraAccount ? "RELATED_" : "") + "ACCOUNT_ID = ? "
				+ "AND GL_DATE " + (isIncomeStatement ? "BETWEEN ? AND ?" : "<= ?") + " "
				+ "AND IS_POSTED = ? "
				+ "GROUP BY A.NUMBER ORDER BY A.NUMBER";
		return sql;
	}

	@Override
	public ISAccountDto getISOrBSAccounts( Integer companyId,
			Integer accountId, Date fromDate, Date toDate,
			boolean isIncomeStatement, boolean isContraAccount) {
		String sql = getISOrBSSql(isIncomeStatement, isContraAccount);
		ISOrBSAccountsHandler handler = new ISOrBSAccountsHandler(companyId, accountId, 
				fromDate, toDate, isIncomeStatement);
		Page<ISAccountDto> accounts = getAllAsPage(sql, 
				new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT), handler);
		if (accounts != null && accounts.getDataSize() > 0) {
			return accounts.getData().iterator().next();
		}
		return null;
	}

	private static class ISOrBSAccountsHandler implements QueryResultHandler<ISAccountDto> {
		private int companyId;
		private int accountId;
		private Date fromDate; 
		private Date toDate;
		private boolean isIncomeStatement; 

		private ISOrBSAccountsHandler (int companyId, int accountId, 
				Date fromDate, Date toDate, 
				boolean isIncomeStatement) {
			this.companyId = companyId;
			this.accountId = accountId;
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.isIncomeStatement = isIncomeStatement;
		}

		@Override
		public List<ISAccountDto> convert(List<Object[]> queryResult) {
			List<ISAccountDto> accounts = new ArrayList<ISAccountDto>();
			for (Object[] row : queryResult) {
				int columnNumber = 0;
				Integer acctId = (Integer)row[columnNumber++]; //0
				String accountName = (String)row[columnNumber++]; //1
				Double debit = (Double)row[columnNumber++]; //2
				Double credit = (Double)row[columnNumber++]; //3
				accounts.add(new ISAccountDto(acctId, accountName, debit, credit));
			}
			return accounts;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index++, companyId);
			query.setParameter(index++, accountId);
			query.setParameter(index++, fromDate);
			if (isIncomeStatement) {
				query.setParameter(index++, toDate);
			}
			query.setParameter(index, 1);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("A.ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("A.ACCOUNT_NAME", Hibernate.STRING);
			query.addScalar("SUM(DEBIT)", Hibernate.DOUBLE);
			query.addScalar("SUM(CREDIT)", Hibernate.DOUBLE);
		}
	}

	@Override
	public Page<JournalEntriesRegisterDto> getJournalEntriesRegister(
			Integer companyId, Date fromDate, Date toDate, Integer statusId,
			String source, String refNo, PageSetting pageSetting) {
		refNo = refNo.equals("") ? "-1": refNo;
		JournalEntriesRegisterHandler handler = new JournalEntriesRegisterHandler();
		return executePagedSP("GET_JOURNAL_ENTRIES", pageSetting, handler, companyId, fromDate, toDate, source, refNo, statusId);
	}

	private static class JournalEntriesRegisterHandler implements QueryResultHandler<JournalEntriesRegisterDto> {

		@Override
		public List<JournalEntriesRegisterDto> convert(List<Object[]> queryResult) {
			List<JournalEntriesRegisterDto> ret = new ArrayList<JournalEntriesRegisterDto>();
			JournalEntriesRegisterDto jer = null;
			for (Object[] row : queryResult) {
				// Type casting
				int columnNumber = 0;
				Date glDate = (Date)row[columnNumber++]; //0
				String source = (String)row[columnNumber++]; //1
				String refNo = (String)row[columnNumber++]; //2
				String accountNo = (String)row[columnNumber++]; //3
				String accountDesc = (String)row[columnNumber++]; //4
				String description = (String)row[columnNumber++]; //5
				Double debit = getDouble(row[columnNumber++]); //6
				Double credit = getDouble(row[columnNumber++]); //7
				Integer isPosted = getInteger(row[columnNumber++]); //8
				int status = isPosted.intValue();
				String division = (String)row[10];
				String customerPo = (String)row[11];
				jer = new JournalEntriesRegisterDto();
				jer.setGlDate(glDate);
				jer.setSource(source);
				jer.setRefNo(refNo);
				jer.setAccountNo(accountNo);
				jer.setAccountDesc(accountDesc);
				jer.setDescription(description);
				jer.setDebit(debit);
				jer.setCredit(credit);
				jer.setStatus(status == 1 ? "POSTED" : "UNPOSTED");
				jer.setDivision(division);
				jer.setCustomerPo(customerPo);
				if (debit != 0.0 || credit != 0.0) {
					ret.add(jer);
				}
			}
			return ret;
		}

		private Double getDouble (Object doubleValue) {
			Double ret = 0.0;
			if (doubleValue instanceof BigDecimal) {
				ret = ((BigDecimal) doubleValue).doubleValue();
			} else if (doubleValue instanceof Double) {
				ret = (Double) doubleValue;
			}
			return ret;
		}

		private Integer getInteger (Object intValue) {
			Integer ret = 0;
			if (intValue instanceof BigInteger) {
				ret = ((BigInteger) intValue).intValue();
			} else if (intValue instanceof Integer) {
				ret = (Integer) intValue;
			} else if(intValue instanceof Byte) {
				Byte val = (Byte) intValue;
				ret = val.intValue();
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			// Do nothing
		}
	}

	@Override
	public List<Account> getAccountsByName(String accountName, Integer companyId, Integer divisionId,
			Integer limit, boolean isOrderByName) {
		return getAll(getAccountDetachCriteria(accountName, companyId, divisionId, null, limit, isOrderByName, false));
	}

	private DetachedCriteria getAccountDetachCriteria(String accountName, Integer companyId, Integer divisionId,
			Integer accountId, Integer limit, boolean isOrderByName, boolean isExact) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Account.FIELD.active.name(), true));
		if (accountName != null && !accountName.trim().isEmpty()) {
			if(isExact) {
				dc.add(Restrictions.or(Restrictions.eq(Account.FIELD.accountName.name(), accountName.trim()),
						Restrictions.eq(Account.FIELD.number.name(), accountName.trim())));
			} else {
				dc.add(Restrictions.or(Restrictions.like(Account.FIELD.accountName.name(), "%"+accountName.trim()+"%"),
						Restrictions.like(Account.FIELD.number.name(), "%"+accountName.trim()+"%")));
			}
		}
		if (accountId != null) {
			dc.add(Restrictions.ne(Account.FIELD.id.name(), accountId));
		}
		DetachedCriteria accDc = DetachedCriteria.forClass(AccountCombination.class);
		accDc.setProjection(Projections.property("accountId"));
		accDc.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		accDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		accDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		dc.add(Subqueries.propertyIn("id", accDc));
		if (limit != null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		String fieldOrder = isOrderByName ? Account.FIELD.number.name() : Account.FIELD.accountName.name();
		dc.addOrder(Order.asc(fieldOrder));
		return dc;
	}

	@Override
	public Page<JournalEntriesRegisterDto> getGeneralLedgerListing(
			Integer companyId, Date fromDate, Date toDate, Integer accountId, PageSetting pageSetting) {
		JournalEntriesRegisterHandler handler = new JournalEntriesRegisterHandler();
		return executePagedSP("GET_GENERAL_LEDGER_LISTING", pageSetting, handler, companyId, accountId, fromDate, toDate);
	}

	public List<Account> getAccounts(Integer companyId) {
		DetachedCriteria accountCriteria = DetachedCriteria.forClass(Account.class);

		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.setProjection(Projections.property(AccountCombination.FIELD.accountId.name()));

		accountCriteria.add(Subqueries.propertyIn(Account.FIELD.id.name(), acctCombinationCriteria));
		accountCriteria.addOrder(Order.asc(Account.FIELD.number.name()));

		return getAll(accountCriteria);
	}

	@Override
	public List<Account> getAccounts(String numberOrName, Integer accountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Account.FIELD.active.name(), true));
		if (accountId != null) {
			dc.add(Restrictions.ne(Account.FIELD.id.name(), accountId));
		}
		if(numberOrName != null && !numberOrName.trim().isEmpty()) {
			dc.add(Restrictions.or(Restrictions.like(Account.FIELD.accountName.name(), "%"+numberOrName.trim()+"%"),
					Restrictions.like(Account.FIELD.number.name(), "%"+numberOrName.trim()+"%")));
		}

		DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
		acDc.setProjection(Projections.property(AccountCombination.FIELD.accountId.name()));
		acDc.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		dc.add(Subqueries.propertyNotIn(Account.FIELD.id.name(), acDc));

		return getAll(dc);
	}

	@Override
	public Page<AccountDto> searchAccountWithSubs(int accountTypeId, String accountNumber, String accountName,
			User user, SearchStatus status, boolean isMainAccountOnly, PageSetting pageSetting) {
		String sql = "SELECT A.ACCOUNT_ID, A.NUMBER, A.ACCOUNT_NAME AS ACCT_NAME, A.DESCRIPTION, "
				+ "AT.NAME AS AT_NAME, COALESCE(PA.ACCOUNT_NAME, '') AS PA_NAME, A.ACTIVE "
				+ "FROM ACCOUNT A "
				+ "LEFT JOIN ACCOUNT PA ON PA.ACCOUNT_ID = A.PARENT_ACCOUNT_ID "
				+ "INNER JOIN ACCOUNT_TYPE AT ON AT.ACCOUNT_TYPE_ID = A.ACCOUNT_TYPE_ID "
				+ "WHERE " + (status == SearchStatus.All ? "(A.ACTIVE = 0 OR A.ACTIVE = ?) " : "A.ACTIVE = ? ")
				+ (isMainAccountOnly ? "AND A.PARENT_ACCOUNT_ID IS NULL " : "");
		if (accountTypeId > 0) {
			sql += "AND A.ACCOUNT_TYPE_ID = ? ";
		}
		if (!accountNumber.isEmpty()) {
			String tmpAccountNumber = accountNumber.replace("%", "\\%").replace("_", "\\_");
			sql += "AND A.NUMBER LIKE '%" + tmpAccountNumber + "%' ";
		}
		if (!accountName.isEmpty()) {
			String tmpAccountName = accountName.replace("%", "\\%").replace("_", "\\_");
			sql += "AND A.ACCOUNT_NAME LIKE '%" + tmpAccountName + "%' ";
		}
		sql += "ORDER BY A.NUMBER";
		return getAllAsPage(sql, pageSetting, new AccountDtoHandler(accountTypeId, status, this));
	}

	private static class AccountDtoHandler implements QueryResultHandler<AccountDto> {
		private int accountTypeId;
		private SearchStatus status;
		private AccountDaoImpl accountDaoImpl;

		private AccountDtoHandler (int accountTypeId, SearchStatus status, AccountDaoImpl accountDaoImpl) {
			this.accountTypeId = accountTypeId;
			this.status = status;
			this.accountDaoImpl = accountDaoImpl;
		}

		@Override
		public List<AccountDto> convert(List<Object[]> queryResult) {
			List<AccountDto> mainAccounts = new ArrayList<AccountDto>();
			AccountDto dto = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				Integer id = (Integer) rowResult[colNum++];
				String number = (String) rowResult[colNum++];
				String accountName = (String) rowResult[colNum++];
				String description = (String) rowResult[colNum++];
				String atName = (String) rowResult[colNum++];
				String paName = (String) rowResult[colNum++];
				boolean active = (Boolean) rowResult[colNum++];
				dto = AccountDto.getInstanceOf(id, number, accountName, description, null,
						atName, paName, active, this.accountDaoImpl.isLastLevel(id));
				dto.setMainParent(true);
				mainAccounts.add(dto);
			}
			return mainAccounts;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int searchStatus = (status == SearchStatus.All ? 1 : (status == SearchStatus.Active ? 1 : 0));
			query.setParameter(index, searchStatus);
			if (accountTypeId > 0) {
				query.setParameter(++index, accountTypeId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("NUMBER", Hibernate.STRING);
			query.addScalar("ACCT_NAME", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("AT_NAME", Hibernate.STRING);
			query.addScalar("PA_NAME", Hibernate.STRING);
			query.addScalar("ACTIVE", Hibernate.BOOLEAN);
		}
	}

	@Override
	public List<AccountDto> getAllChildren(int accountId) {
		String sql = "SELECT A.ACCOUNT_ID, A.NUMBER, A.ACCOUNT_NAME AS ACCT_NAME, A.DESCRIPTION, "
				+ "AT.NAME AS AT_NAME, PA.ACCOUNT_NAME AS PA_NAME, A.ACTIVE FROM ACCOUNT A "
				+ "INNER JOIN ACCOUNT_TYPE AT ON AT.ACCOUNT_TYPE_ID = A.ACCOUNT_TYPE_ID "
				+ "INNER JOIN ACCOUNT PA ON PA.ACCOUNT_ID = A.PARENT_ACCOUNT_ID "
				+ "WHERE A.ACTIVE = ? AND PA.ACCOUNT_ID = " + accountId;
		return (List<AccountDto>) getAllAsPage(sql, new PageSetting(PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT), 
				new AccountDtoHandler(-1, SearchStatus.All, this)).getData();
	}

	@Override
	public boolean isLastLevel(int accountId) {
		String sql = "SELECT ACCOUNT_ID FROM ACCOUNT WHERE ACCOUNT_ID NOT IN "
				+ "(SELECT PARENT_ACCOUNT_ID FROM ACCOUNT "
				+ "WHERE PARENT_ACCOUNT_ID IS NOT NULL AND  ACTIVE = 1) "
				+ "AND ACTIVE = ? AND ACCOUNT_ID = ? ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, 1);
			query.setParameter(1, accountId);
			return !query.list().isEmpty();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<Account> getLastLevelAccounts() {
		List<Account> accounts = new ArrayList<>();
		String sql = "SELECT ACCOUNT_ID, NUMBER, ACCOUNT_NAME, DESCRIPTION, ACCOUNT_TYPE_ID, RELATED_ACCOUNT_ID, PARENT_ACCOUNT_ID, "
				+ "EB_OBJECT_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID FROM ACCOUNT "
				+ "WHERE ACCOUNT_ID NOT IN "
				+ "(SELECT PARENT_ACCOUNT_ID FROM ACCOUNT WHERE PARENT_ACCOUNT_ID IS NOT NULL AND ACTIVE = 1) AND ACTIVE = ? "
				+ "ORDER BY NUMBER";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, 1);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					int colNum = 0;
					Integer id = (Integer) row[colNum++];
					String number = (String) row[colNum++];
					String accountName = (String) row[colNum++];
					String description = (String) row[colNum++];
					Integer accountTypeId = (Integer) row[colNum++];
					Integer relatedAccountId = (Integer) row[colNum++];
					Integer parentAccountId = (Integer) row[colNum++];
					Integer ebObjectId = (Integer) row[colNum++];
					boolean active = (Boolean) row[colNum++];
					Integer createdBy = (Integer) row[colNum++];
					Date createdDate = (Date) row[colNum++];
					Integer updatedBy = (Integer) row[colNum++];
					Date updatedDate = (Date) row[colNum++];
					Integer serviceLeaseKeyId = (Integer) row[colNum++];
					accounts.add(Account.getInstanceOf(id, number, accountName, description, accountTypeId, 
							relatedAccountId, parentAccountId, ebObjectId, active, createdBy, createdDate, 
							updatedBy, updatedDate, serviceLeaseKeyId));
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return accounts;
	}

	@Override
	public List<Account> getAccountsByNameAndType(String accountName, Integer companyId, Integer divisionId,
			Integer limit, Integer accountTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(accountName != null && !accountName.trim().isEmpty()) {
			dc.add(Restrictions.or(Restrictions.like(Account.FIELD.accountName.name(), "%"+accountName.trim()+"%"),
					Restrictions.like(Account.FIELD.number.name(), "%"+accountName.trim()+"%")));
		}
		dc.add(Restrictions.eq(Account.FIELD.accountTypeId.name(), accountTypeId));
		DetachedCriteria accDc = DetachedCriteria.forClass(AccountCombination.class);
		accDc.setProjection(Projections.property("accountId"));
		accDc.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		accDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		accDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		if (limit != null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		dc.add(Subqueries.propertyIn("id", accDc));
		dc.add(Restrictions.eq(Account.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public Account getAccountByNameAndType(String accountName, Integer companyId, Integer divisionId,
			Integer accountTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(accountName != null && !accountName.trim().isEmpty()) {
			dc.add(Restrictions.or(Restrictions.eq(Account.FIELD.accountName.name(), accountName.trim()),
					Restrictions.eq(Account.FIELD.number.name(), accountName.trim())));
		}
		dc.add(Restrictions.eq(Account.FIELD.accountTypeId.name(), accountTypeId));
		DetachedCriteria accDc = DetachedCriteria.forClass(AccountCombination.class);
		accDc.setProjection(Projections.property("accountId"));
		accDc.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		accDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		accDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		dc.add(Subqueries.propertyIn("id", accDc));
		dc.add(Restrictions.eq(Account.FIELD.active.name(), true));
		return get(dc);
	}

	@Override
	public List<AccountDto> getByCombinationAndType(Integer companyId, Integer divisionId, Integer accountTypeId, String acctNameOrNumber) {
		List<AccountDto> accountDtos = new ArrayList<>();
		String sql = "SELECT DISTINCT A.ACCOUNT_ID, A.NUMBER, A.ACCOUNT_NAME, A.DESCRIPTION, "
			+ "COALESCE(A.PARENT_ACCOUNT_ID, 0) AS PA_ID, COALESCE(PA.ACCOUNT_NAME, '') AS PA_NAME, A.ACTIVE FROM ACCOUNT A "
			+ "LEFT JOIN ACCOUNT PA ON PA.ACCOUNT_ID = A.PARENT_ACCOUNT_ID "
			+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_ID = A.ACCOUNT_ID "
			+ "WHERE A.ACTIVE = 1 "
			+ "AND AC.ACTIVE = 1 " 
			+ "AND AC.COMPANY_ID = ? "
			+ (divisionId != null ? "AND AC.DIVISION_ID = ? " : "")
			+ (accountTypeId != null ? "AND A.ACCOUNT_TYPE_ID = ? " : "")
			+ (acctNameOrNumber != null ? "AND (A.ACCOUNT_NAME LIKE ? OR A.NUMBER LIKE ?) " : "");
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			int index = 0;
			query.setParameter(index++, companyId);
			if (divisionId != null && divisionId.intValue() != -1) {
				query.setParameter(index++, divisionId);
			}
			if (accountTypeId != null) {
				query.setParameter(index++, accountTypeId);
			}
			if(acctNameOrNumber != null) {
				query.setParameter(index++, "%"+acctNameOrNumber+"%");
				query.setParameter(index++, "%"+acctNameOrNumber+"%");
			}

			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					accountDtos.add(AccountDto.getInstanceOf((Integer) row[0], (String) row[1], 
							(String) row[2], (String) row[3], ((BigDecimal) row[4]).intValue(), "", (String) row[5], (Boolean) row[6], false));
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return accountDtos;
	}

	@Override
	public boolean isUsedAsParentAccount(Integer accountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Account.FIELD.parentAccountId.name(), accountId));
		return getAll(dc).size() > 0;
	}

	@Override
	public boolean isInCombination(int companyId, int accountId, int divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria aCombiDc = DetachedCriteria.forClass(AccountCombination.class);
		aCombiDc.setProjection(Projections.property(AccountCombination.FIELD.accountId.name()));
		aCombiDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		aCombiDc.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		if (divisionId != -1) {
			aCombiDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		}
		dc.add(Subqueries.propertyIn(Account.FIELD.id.name(), aCombiDc));
		return get(dc) != null;
	}

	@Override
	public List<Account> getAccountByDivisions(int companyId, int divisionFromId, int divisionToId) {
		DetachedCriteria dc = getDetachedCriteria();
		// For account combination
		DetachedCriteria acctCombinationCriteria = DetachedCriteria.forClass(AccountCombination.class);
		// For account type
		DetachedCriteria acctTypeCriteria = DetachedCriteria.forClass(AccountType.class);
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.active.name(), true));
		acctCombinationCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombinationCriteria.add(Restrictions.between(AccountCombination.FIELD.divisionId.name(), divisionFromId, divisionToId));
		acctCombinationCriteria.setProjection(Projections.property("accountId"));
		acctTypeCriteria.add(Restrictions.eq(AccountType.FIELD.active.name(), true));
		dc.add(Subqueries.propertyIn("id", acctCombinationCriteria));
		acctTypeCriteria.add(Subqueries.propertyIn("id", acctTypeCriteria));
		dc.add(Restrictions.eq(Account.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public List<Account> getAccountsAndExcludeId(String accountName, Integer companyId, Integer divisionId,
			Integer accountId, Integer limit, boolean isOrderByName, boolean isExact) {
		return getAll(getAccountDetachCriteria(accountName, companyId, divisionId, accountId, limit, isOrderByName, isExact));
	}



	@Override
	public List<ISBSAccountDto> getISBSAccounts(int companyId, int accountId, int divisionId,
			List<AccountType> accountTypes, Date dateFrom, Date dateTo, boolean isIS) {
		return getISBSAccounts(companyId, accountId, divisionId, accountTypes, dateFrom, dateTo, isIS, false);
	}

	@Override
	public List<ISBSAccountDto> getISBSAccounts(int companyId, int accountId, int divisionId,
			List<AccountType> accountTypes, Date dateFrom, Date dateTo, boolean isIS, boolean isByMonth) {
		List<ISBSAccountDto> accounts = new ArrayList<>();
		List<Object> objs = null;
		if (!isIS) {
			objs = executeSP("GET_ISBS_BY_DIVISION", companyId, accountId, divisionId, dateTo);
		} else {
			objs = executeSP(!isByMonth ? "GET_IS_BY_DIVISION" : "GET_IS_BY_DIVISION_AND_MONTH", companyId, accountId, divisionId, dateFrom, dateTo);
		}
		if(objs != null && !objs.isEmpty()) {
			ISBSAccountDto dto = null;
			for (Object obj : objs) {
				Object[] row = (Object[]) obj;
				int columnNumber = 0;
				dto = new ISBSAccountDto();
				dto.setCompanyId((Integer) row[columnNumber++]);
				dto.setDivisionName((String) row[columnNumber++]);
				dto.setDivisionId((Integer) row[columnNumber++]);
				dto.setAccountName((String) row[columnNumber++]);
				dto.setAccountId((Integer) row[columnNumber++]);
				Object numObj = row[columnNumber++];
				Double debit = NumberFormatUtil.convertBigDecimalToDouble(numObj);
				numObj = row[columnNumber++];
				Double credit = NumberFormatUtil.convertBigDecimalToDouble(numObj);
				dto.setAccountTypeId((Integer) row[columnNumber++]);
				dto.setAmount(getISAmount(accountTypes, debit != null ? debit : 0,
						credit != null ? credit : 0, dto.getAccountTypeId()));
				dto.setpAcctId((Integer) row[columnNumber++]);
				dto.setAcctNo((String) row[columnNumber++]);
				if (isByMonth) {
					if(dto.getAmount() == null || dto.getAmount().equals(0.00)) {
						continue;
					}
					dto.setMonth((Integer) row[columnNumber++]);
					dto.setMonthName(DateUtil.convertToStringMonth(dto.getMonth()));
					dto.setParentAccountId(dto.getAcctNo());
				}
				accounts.add(dto);
			}
		}
		return accounts;
	}

	private double getISAmount(List<AccountType> accountTypes, double debit, double credit, int accountTypeId) {
		for (AccountType accountType : accountTypes) {
			if (accountType.getId() == accountTypeId) {
				int normalBalanceId = accountType.getNormalBalanceId();
				if (normalBalanceId == NormalBalance.DEBIT) {
					return debit - credit;
				} else {
					return credit - debit;
				}
			}
		}
		return 0;
	}

	@Override
	public Collection<String> getArAccounts(int companyId, Integer divisionId) {
		// Get distinct Accounts from AR Customer Account
		// Get accounts from AR Customer Accounts and Inventory Account for Customer Advance Payment
		StringBuilder sql = new StringBuilder("SELECT DISTINCT A.ACCOUNT_ID, A.ACCOUNT_NAME AS ACCOUNT_NAME "
				+ "FROM ACCOUNT A "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_ID = A.ACCOUNT_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON AC.ACCOUNT_COMBINATION_ID = ACA.DEFAULT_DEBIT_AC_ID "
				+ "WHERE A.ACTIVE = 1 AND ACA.ACTIVE = 1 "
				+ "AND ACA.COMPANY_ID = ? "
				+ (divisionId != null && divisionId != -1 ? "AND AC.DIVISION_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT DISTINCT A.ACCOUNT_ID, A.ACCOUNT_NAME AS ACCOUNT_NAME "
				+ "FROM ACCOUNT A "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_ID = A.ACCOUNT_ID "
				+ "INNER JOIN RECEIPT_METHOD RM ON RM.CREDIT_ACCOUNT_COMBINATION_ID = AC.ACCOUNT_COMBINATION_ID "
				+ "INNER JOIN INVENTORY_ACCOUNT IA ON IA.CUSTOMER_ADV_PAYMENT_RM_ID = RM.RECEIPT_METHOD_ID "
				+ "WHERE IA.ACTIVE =1 AND RM.ACTIVE = 1 "
				+ "AND IA.COMPANY_ID = ? "
				+ (divisionId != null && divisionId != -1 ? "AND AC.DIVISION_ID = ? " : ""));
		PageSetting pageSetting = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		ArAccountsHandler handler = new ArAccountsHandler(companyId, divisionId, 2);
		return getAllAsPage(sql.toString(), pageSetting, handler).getData();
	}

	private static class ArAccountsHandler implements QueryResultHandler<String> {
		private int companyId;
		private Integer divisionId;
		private int rowCnt;

		private ArAccountsHandler (int companyId, Integer divisionId, int rowCnt) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.rowCnt = rowCnt;
		}

		@Override
		public List<String> convert(List<Object[]> queryResult) {
			List<String> ret = new ArrayList<>();
			for (Object[] row : queryResult) {
				String accountName = (String)row[1];
				ret.add(accountName);
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int row = 0; row < rowCnt; row++) {
				query.setParameter(row == 0 ? index : ++index, companyId);
				if(divisionId != null && divisionId != -1) {
					query.setParameter(++index, divisionId);
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("ACCOUNT_NAME", Hibernate.STRING);
		}
	}

	@Override
	public Collection<String> getSupplierAccountNames(int companyId, Integer divisionId) {
		// Get distinct Accounts from Supplier Account
		StringBuilder sql = new StringBuilder("SELECT DISTINCT A.ACCOUNT_ID, A.ACCOUNT_NAME AS ACCOUNT_NAME "
				+ "FROM ACCOUNT A "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_ID = A.ACCOUNT_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON AC.ACCOUNT_COMBINATION_ID = SA.DEFAULT_CREDIT_AC_ID "
				+ "WHERE A.ACTIVE = 1 AND SA.ACTIVE = 1 "
				+ "AND SA.COMPANY_ID = ? "
				+ (divisionId != null && divisionId != -1 ? "AND AC.DIVISION_ID = ? " : ""));
		PageSetting pageSetting = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		ArAccountsHandler handler = new ArAccountsHandler(companyId, divisionId, 1);
		return getAllAsPage(sql.toString(), pageSetting, handler).getData();
	}
}