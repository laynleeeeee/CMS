package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.service.report.CustomerAccountHistoryParam;
import eulap.eb.service.report.StatementOfAccountParam;
import eulap.eb.web.dto.CustomerAccountHistoryDto;
import eulap.eb.web.dto.CustomerBalancesSummaryDto;
import eulap.eb.web.dto.StatementOfAccountDto;

/**
 * DAO Implementation of {@link ArCustomerAcctDao}

 *
 */
public class ArCustomerAcctDaoImpl extends BaseDao<ArCustomerAccount> implements ArCustomerAcctDao {
	private static final String CUSTOMER_BEGINNING_BALANCE = "SELECT AR_CUSTOMER_ACCOUNT_ID, DATE, "
			+ "COALESCE(sum(TRANSACTION_AMOUNT), 0) as TOTAL_TRANSACTION, COALESCE(sum(RECEIPT_AMOUNT), 0) as TOTAL_RECEIPT FROM ( "+
				"SELECT 'T' as SOURCE, AT.AR_TRANSACTION_ID as ID, AT.CUSTOMER_ACCOUNT_ID AS AR_CUSTOMER_ACCOUNT_ID, AT.GL_DATE AS DATE, AT.TRANSACTION_NUMBER as REFERENCE_NUMBER, "+
					"	AT.AMOUNT as TRANSACTION_AMOUNT, 0 as RECEIPT_AMOUNT "+
					"FROM AR_TRANSACTION AT "+
					"INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID " +
					"WHERE F.CURRENT_STATUS_ID !="+ FormStatus.CANCELLED_ID +" "+
					"AND AT.AR_TRANSACTION_TYPE_ID !="+ArTransactionType.TYPE_SALE_RETURN+" "+
					"AND AT.AR_TRANSACTION_TYPE_ID !="+ArTransactionType.TYPE_ACCOUNT_SALE+" "+
				"UNION ALL "+
				"SELECT 'S' as SOURCE, AT.AR_TRANSACTION_ID as ID, AT.CUSTOMER_ACCOUNT_ID, AT.TRANSACTION_DATE, AT.TRANSACTION_NUMBER as REFERENCE_NUMBER, "+
					"	AT.AMOUNT as TRANSACTION_AMOUNT, 0 as RECEIPT_AMOUNT "+
					"FROM AR_TRANSACTION AT "+
					"INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID " +
					"WHERE AT.AR_TRANSACTION_TYPE_ID ="+ArTransactionType.TYPE_ACCOUNT_SALE+" "+
					"AND F.CURRENT_STATUS_ID !="+ FormStatus.CANCELLED_ID +" "+
				"UNION ALL "+
				// Negate the amount of the Account Sales Return
				"SELECT 'S' as SOURCE, AT.AR_TRANSACTION_ID as ID, AT.CUSTOMER_ACCOUNT_ID, AT.TRANSACTION_DATE, AT.TRANSACTION_NUMBER as REFERENCE_NUMBER, "+
				"	SUM(ASI.AMOUNT) as TRANSACTION_AMOUNT, 0 as RECEIPT_AMOUNT "+
				"FROM AR_TRANSACTION AT "+
				"INNER JOIN ACCOUNT_SALE_ITEM ASI ON ASI.AR_TRANSACTION_ID = AT.AR_TRANSACTION_ID "+
				"INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID " +
				"WHERE AT.AR_TRANSACTION_TYPE_ID ="+ArTransactionType.TYPE_SALE_RETURN+" "+
				"AND F.CURRENT_STATUS_ID !="+ FormStatus.CANCELLED_ID +" "+
				"GROUP BY AT.AR_TRANSACTION_ID "+
				"UNION ALL "+
				"SELECT 'R' AS SOURCE, AR.AR_RECEIPT_ID as ID, AR.AR_CUSTOMER_ACCOUNT_ID, AR.MATURITY_DATE as GL_DATE, AR.RECEIPT_NUMBER as REFERENCE_NUMBER, "+
					"	0 as TRANSACTION_AMOUNT, AR.AMOUNT as RECEIPT_AMOUNT "+
					"FROM AR_RECEIPT AR "+
					"INNER JOIN FORM_WORKFLOW F ON AR.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "+
					"WHERE F.CURRENT_STATUS_ID !="+ FormStatus.CANCELLED_ID+
				") AS T";
	private static final String ACCOUNT_HISTORY_SQL = "SELECT * FROM ( "+
			"SELECT 'T' as SOURCE, AT.AR_TRANSACTION_ID as ID, AT.GL_DATE, AT.TRANSACTION_NUMBER as REFERENCE_NUMBER, "+
				"DESCRIPTION AS INVOICE_NUMBER, AT.AMOUNT as TRANSACTION_AMOUNT, 0 as RECEIPT_AMOUNT, " +
				"AT.CREATED_DATE FROM AR_TRANSACTION AT "+
				"INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "+
				"WHERE AT.CUSTOMER_ACCOUNT_ID=? "+
				"AND AT.GL_DATE BETWEEN ? AND ? "+
				"AND F.CURRENT_STATUS_ID !="+FormStatus.CANCELLED_ID+" "+
			"UNION ALL "+
			"SELECT 'S' as SOURCE, AT.AR_TRANSACTION_ID as ID, AT.TRANSACTION_DATE, AT.TRANSACTION_NUMBER as REFERENCE_NUMBER, "+
				"DESCRIPTION AS INVOICE_NUMBER, AT.AMOUNT as TRANSACTION_AMOUNT, 0 as RECEIPT_AMOUNT, AT.CREATED_DATE "+
				"FROM AR_TRANSACTION AT "+
				"INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "+
				"WHERE AT.CUSTOMER_ACCOUNT_ID=? "+
				"AND AT.TRANSACTION_DATE BETWEEN ? AND ? "+
				"AND AT.AR_TRANSACTION_TYPE_ID ="+ArTransactionType.TYPE_ACCOUNT_SALE+" "+
				"AND F.CURRENT_STATUS_ID !="+FormStatus.CANCELLED_ID+" "+
			"UNION ALL "+
			// Negate the amount of the Account Sales Return
			"SELECT 'S' as SOURCE, AT.AR_TRANSACTION_ID as ID, AT.TRANSACTION_DATE, AT.TRANSACTION_NUMBER as REFERENCE_NUMBER, "+
				"DESCRIPTION AS INVOICE_NUMBER, SUM(ASI.AMOUNT) as TRANSACTION_AMOUNT, 0 as RECEIPT_AMOUNT, AT.CREATED_DATE "+
				"FROM AR_TRANSACTION AT "+
				"INNER JOIN ACCOUNT_SALE_ITEM ASI ON ASI.AR_TRANSACTION_ID = AT.AR_TRANSACTION_ID "+
				"INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "+
				"WHERE AT.CUSTOMER_ACCOUNT_ID=? "+
				"AND AT.TRANSACTION_DATE BETWEEN ? AND ? "+
				"AND AT.AR_TRANSACTION_TYPE_ID ="+ArTransactionType.TYPE_SALE_RETURN+" "+
				"AND F.CURRENT_STATUS_ID !="+FormStatus.CANCELLED_ID+" "+
				"GROUP BY AT.AR_TRANSACTION_ID "+
			"UNION ALL "+
			"SELECT 'R' AS SOURCE, AR.AR_RECEIPT_ID as ID, "+
				"AR.MATURITY_DATE as GL_DATE, CONCAT('AC-',AR.SEQUENCE_NO,', ',AR.RECEIPT_NUMBER) as REFERENCE_NUMBER, "+
				"'' AS INVOICE_NUMBER, 0 as TRANSACTION_AMOUNT, AR.AMOUNT as RECEIPT_AMOUNT, AR.CREATED_DATE "+
				"FROM AR_RECEIPT AR "+
				"INNER JOIN FORM_WORKFLOW F ON AR.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "+
				"WHERE AR.AR_CUSTOMER_ACCOUNT_ID=? "+
				"AND AR.MATURITY_DATE BETWEEN ? AND ? " +
				"AND F.CURRENT_STATUS_ID !="+FormStatus.CANCELLED_ID+
			") AS T "+
			"ORDER BY GL_DATE, CREATED_DATE";
	@Override
	protected Class<ArCustomerAccount> getDomainClass() {
		return ArCustomerAccount.class;
	}

	@Override
	public boolean isUniqueName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArCustomerAccount.FIELD.name.name(), name));
		return getAll(dc).size() < 1;
	}

	@Override
	public Page<ArCustomerAccount> searchAccounts(String searchCriteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(ArCustomerAccount.FIELD.name.name(), "%"+searchCriteria.trim()+"%"));
		dc.addOrder(Order.asc(ArCustomerAccount.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<ArCustomerAccount> getArCustomerAccounts(Integer arCustomerId, Integer companyId, Integer divisionId) {
		return getArCustomerAccounts(arCustomerId, companyId, divisionId, true);
	}

	@Override
	public List<ArCustomerAccount> getArCustomerAccounts(Integer arCustomerId, Integer companyId, Integer divisionId, boolean activeOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArCustomerAccount.FIELD.arCustomerId.name(), arCustomerId));
		if (companyId != null) {
			dc.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
 			acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			if (divisionId != null) {
				acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			}
			dc.add(Subqueries.propertyIn(ArCustomerAccount.FIELD.defaultDebitACId.name(), acDc));
		}
		if(activeOnly) {
			dc.add(Restrictions.eq(ArCustomerAccount.FIELD.active.name(), true));
		}
		dc.addOrder(Order.asc(ArCustomerAccount.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<CustomerAccountHistoryDto> getCustomerAccountHistory(final CustomerAccountHistoryParam param) {
		Collection<CustomerAccountHistoryDto> ret =
				get(ACCOUNT_HISTORY_SQL, new AHQueryResultHandler(param, this));
		return (List<CustomerAccountHistoryDto>) ret;
	}

	private static class AHQueryResultHandler implements QueryResultHandler<CustomerAccountHistoryDto> {
		private final Logger logger =  Logger.getLogger(CustomerAccountHistoryDto.class);
		private CustomerAccountHistoryParam param;
		private ArCustomerAcctDaoImpl daoImpl;
		private AHQueryResultHandler (CustomerAccountHistoryParam param, ArCustomerAcctDaoImpl daoImpl) {
			this.param = param;
			this.daoImpl = daoImpl;
		}

		@Override
		public List<CustomerAccountHistoryDto> convert(
				List<Object[]> queryResult) {
			List<CustomerAccountHistoryDto> ret = new ArrayList<CustomerAccountHistoryDto>();
			double balance = 0;
			if (param.getDateFrom() != null) {
				logger.info("Get the beginning balance of the customer account");
				balance = getBeginningBalance();
				CustomerAccountHistoryDto beginningBalanceDto = new CustomerAccountHistoryDto();
				beginningBalanceDto.setDescription("Beginning Balance");
				beginningBalanceDto.setBalance(balance);
				beginningBalanceDto.setReceiptAmount(0.0);
				beginningBalanceDto.setTransactionAmount(0.0);
				ret.add(beginningBalanceDto);
			}
			CustomerAccountHistoryDto dto = null;
			for (Object[] row : queryResult) {
				int index = 0;
				dto = new CustomerAccountHistoryDto();
				String source = (String) row[index++];
				int id = (Integer) row[index++];
				String description = getDescription(source, id);
				dto.setDescription(description);
				Date glDate = (Date) row[index++];
				dto.setDate(glDate);
				String referenceNumber = (String) row[index++];
				dto.setReferenceNumber(referenceNumber);
				dto.setInvoiceNumber((String) row[index++]);
				double transactionAmount =(Double)row[index++];
				dto.setTransactionAmount(transactionAmount);
				double receiptAmount = (Double) row[index++];
				dto.setReceiptAmount(receiptAmount);
				double currentBalance = transactionAmount-receiptAmount;
				balance += currentBalance;
				dto.setBalance(balance);
				ret.add(dto);
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int numOfTables = 4;
			for (int i = 1; i <= numOfTables; i++) {
				query.setParameter(index++, param.getCustomerAcctId());
				query.setParameter(index++, param.getDateFrom());
				query.setParameter(index, param.getDateTo());
				if(i < numOfTables) {
					index++;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			// No scalars to be set
		}

		private double getBeginningBalance() {
			String whereClause = " WHERE AR_CUSTOMER_ACCOUNT_ID = ? AND DATE < ? ";
			List<Double> transactionsAndReceipts = daoImpl.getTotalTransactionsAndReceipts(CUSTOMER_BEGINNING_BALANCE
					+whereClause, param.getCustomerAcctId(), param.getDateFrom());
			Double totalTransactions = transactionsAndReceipts.get(0);
			Double totalReceipts = transactionsAndReceipts.get(1);
			return totalTransactions - totalReceipts;
		}

		private String getDescription (String source, final int id) {
			String description = "";
			String sql = "SELECT AR_TRANSACTION_ID as ID, CONCAT('AC-',AR.SEQUENCE_NO,', ',RECEIPT_NUMBER) AS REFERENCE_NUMBER "+
						"FROM AR_RECEIPT AR "+
						"INNER JOIN AR_RECEIPT_TRANSACTION ART ON AR.AR_RECEIPT_ID = ART.AR_RECEIPT_ID "+
						"INNER JOIN FORM_WORKFLOW F ON AR.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID " +
						"WHERE AR_TRANSACTION_ID=? " +
						"AND F.CURRENT_STATUS_ID !=" + FormStatus.CANCELLED_ID;
			if (source.equals("R")) {// Get the description of the transaction or receipt.
				sql = "SELECT AR_RECEIPT_TRANSACTION_ID as ID, TRANSACTION_NUMBER AS REFERENCE_NUMBER FROM AR_TRANSACTION AT "+
						"INNER JOIN AR_RECEIPT_TRANSACTION ART ON AT.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID "+
						"INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID " +
						"WHERE ART.AR_RECEIPT_ID=? " +
						"AND F.CURRENT_STATUS_ID !=" + FormStatus.CANCELLED_ID;
			}
			Collection<String> refNumbers = daoImpl.get(sql, new QueryResultHandler<String>() {
				@Override
				public List<String> convert(List<Object[]> queryResult) {
					List<String> ret = new ArrayList<String>();
					for (Object[] row : queryResult)
						ret.add(row[1].toString()); // reference number
					return ret;
				}
				@Override
				public int setParamater(SQLQuery query) {
					int index = 0;
					query.setParameter(index, id);
					return index;
				}
				@Override
				public void setScalars(SQLQuery query) {
					query.addScalar("ID", Hibernate.INTEGER);
					query.addScalar("REFERENCE_NUMBER", Hibernate.STRING);
				}
			});
			for (String refNumber : refNumbers) {
				if (description.isEmpty()) {
					description += refNumber;
				} else {
					description += " - " + refNumber;
				}
			}
			return description;
		}
	}

	@Override
	public CustomerAccountHistoryDto getTotalTransactionsAndReceipts(int customerAccountId) {
		String whereClause = " WHERE AR_CUSTOMER_ACCOUNT_ID = ? ";
		List<Double> totalTransAndReceipts = getTotalTransactionsAndReceipts(CUSTOMER_BEGINNING_BALANCE+whereClause, customerAccountId, null);
		CustomerAccountHistoryDto ret = new CustomerAccountHistoryDto();
		ret.setTransactionAmount(totalTransAndReceipts.get(0));
		ret.setReceiptAmount(totalTransAndReceipts.get(1));
		return ret;
	}

	/**
	 * Get the total transactions and total receipts of the AR Customer Account.
	 * <br> Index 0 - Total Transactions<br>Index 1 = Total Receipts
	 * @param sql The sql select script.
	 * @param customerAcctId The id of the AR Customer Account.
	 * @param asOfDate As of date, optional.
	 * @return The total transactions and total receipts in {@link List}.
	 */
	private List<Double> getTotalTransactionsAndReceipts(String sql, final int customerAcctId, final Date asOfDate) {
		Collection<Double> begBalance = get(sql, new QueryResultHandler<Double>() {

			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {

					Double transaction = (Double) row[2];
					ret.add(transaction);
					logger.debug("TOTAL TRANSACTIONS: "+transaction);
					Double receipt = (Double) row[3];
					logger.debug("TOTAL RECEIPTS: "+receipt);
					ret.add(receipt);
					break; // Expecting one row only.
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				int index = 0;
				query.setParameter(index, customerAcctId);
				if(asOfDate != null) {
					query.setParameter(++index, asOfDate);
				}
				return index;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("AR_CUSTOMER_ACCOUNT_ID", Hibernate.INTEGER);
				query.addScalar("DATE", Hibernate.DATE);
				query.addScalar("TOTAL_TRANSACTION", Hibernate.DOUBLE);
				query.addScalar("TOTAL_RECEIPT", Hibernate.DOUBLE);
			}
		});

		return (List<Double>) begBalance;
	}

	@Override
	public Page<ArCustomerAccount> searchCustomerAccounts(Integer divisionId, String customerName, String customerAcctName, Integer companyId,
			Integer termId, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		//Account combination
		if(divisionId != null && divisionId != -1) {
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
			acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			dc.add(Subqueries.propertyIn(ArCustomerAccount.FIELD.defaultDebitACId.name(), acDc));
		}
		if(!customerName.isEmpty()) {
			dc.createAlias("arCustomer", "customer");
			dc.add(Restrictions.like("customer.name", "%" + customerName.trim() + "%"));
		}
		if(!customerAcctName.isEmpty())
			dc.add(Restrictions.like(ArCustomerAccount.FIELD.name.name(), "%" + customerAcctName.trim() +"%"));
		if(companyId != -1)
			dc.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
		if(termId != -1)
			dc.add(Restrictions.eq(ArCustomerAccount.FIELD.termId.name(), termId));
		dc = DaoUtil.setSearchStatus(dc, ArCustomerAccount.FIELD.active.name(), status);
		dc.addOrder(Order.asc(ArCustomerAccount.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Double getPreviousBalanceAmount(final StatementOfAccountParam param) {
		StringBuilder sql = new StringBuilder("SELECT SUM(TRANSACTION_AMOUNT) AS TOTAL_TRANSACTION, "
				+ "SUM(RECEIPT_AMOUNT) AS TOTAL_RECEIPT, SUM(PAID_ADVANCES) AS TOTAL_ADVANCES "
				+ "FROM V_STATEMENT_OF_ACCOUNT "
				+ "WHERE COMPANY_ID = ? AND CUSTOMER_ID = ? "
				+ "AND DATE < ? ");
		if (param.getCustomerAcctId() > 0) {
			sql.append("AND CUSTOMER_ACCOUNT_ID = ? ");
		}
		sql.append("AND IS_COMPLETE = 1 ");
		Collection<Double> prevBalance = get(sql.toString(), new  QueryResultHandler<Double>() {
			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {
					Double transaction = (Double) row[0];
					if (transaction == null) {
						ret.add(0.0); // If no data set beginning balance to zero.
						break;
					}
					double receipt = (Double) row[1];
					double advances = (Double) row[2];
					double totalPaidAmt = receipt + advances;
					ret.add(transaction - totalPaidAmt);
					break; // Expecting one row only.
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				int index = 0;
				query.setParameter(index++, param.getCompanyId());
				query.setParameter(index++, param.getCustomerId());
				query.setParameter(index, param.getDateFrom());
				if (param.getCustomerAcctId() > 0) {
					query.setParameter(++index, param.getCustomerAcctId());
				}
				return index;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("TOTAL_TRANSACTION", Hibernate.DOUBLE);
				query.addScalar("TOTAL_RECEIPT", Hibernate.DOUBLE);
				query.addScalar("TOTAL_ADVANCES", Hibernate.DOUBLE);
			}
		});
		return prevBalance.iterator().next();
	}

	@Override
	public ArCustomerAccount getArCustomerAccount(Integer companyId, Integer arCustomerId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null)
			dc.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
		if(arCustomerId != null)
			dc.add(Restrictions.eq(ArCustomerAccount.FIELD.arCustomerId.name(), arCustomerId));
		dc.add(Restrictions.eq(ArCustomerAccount.FIELD.active.name(), true));
		dc.addOrder(Order.asc(ArCustomerAccount.FIELD.name.name()));
		return get(dc);
	}

	@Override
	public Page<CustomerAccountHistoryDto> getCustomerAccountHistoryReport(
			CustomerAccountHistoryParam param, PageSetting pageSetting) {
		int currencyId = param.getCurrencyId();
		int divisionId = param.getDivisionId();
		String sql = "SELECT COMPANY_ID, DIVISION, CURRENCY_ID, CUSTOMER_ACCOUNT_ID, GL_DATE, REFERENCE_NUMBER, AR_OR_REF_NO, "
				+ "PO_PCR_NO, SOURCE, DESCRIPTION, TRANSACTION_AMOUNT, RECEIPT_AMOUNT, GAIN_LOSS, CREATED_DATE FROM ( "
				+ "SELECT COMPANY_ID, DIVISION, CURRENCY_ID, CUSTOMER_ACCOUNT_ID, GL_DATE, REFERENCE_NUMBER, AR_OR_REF_NO, PO_PCR_NO, SOURCE, DESCRIPTION, "
				+ "SUM(TRANSACTION_AMOUNT) AS TRANSACTION_AMOUNT, SUM(RECEIPT_AMOUNT) AS RECEIPT_AMOUNT, GAIN_LOSS, CREATED_DATE FROM ( "
				+ "SELECT COMPANY_ID, '' DIVISION, CURRENCY_ID, CUSTOMER_ACCOUNT_ID, NULL AS GL_DATE, '' AS REFERENCE_NUMBER, '' AS AR_OR_REF_NO, '' AS PO_PCR_NO, "
				+ "'Beginning Balance' AS SOURCE, 'Beginning Balance' AS DESCRIPTION, ";
		if (currencyId == 1) {
			sql += "TRANSACTION_AMOUNT, RECEIPT_AMOUNT, GAIN_LOSS, ";
		} else {
			sql += "(TRANSACTION_AMOUNT/CURRENCY_RATE_VALUE) AS TRANSACTION_AMOUNT, (CASE WHEN SOURCE != 'Account Collection' THEN (RECEIPT_AMOUNT/CURRENCY_RATE_VALUE) "
				+ "ELSE (SELECT (AC.AMOUNT/AC.CURRENCY_RATE_VALUE) FROM AR_RECEIPT AC WHERE AC.AR_RECEIPT_ID = ID) END) AS RECEIPT_AMOUNT, 0 AS GAIN_LOSS, ";
		}
		sql += "NULL AS CREATED_DATE FROM V_CUSTOMER_ACCT_HISTORY WHERE COMPANY_ID = ? ";
		if (divisionId != -1) {
			sql += "AND DIVISION_ID = ? ";
		}
		sql += "AND CUSTOMER_ACCOUNT_ID = ? AND GL_DATE < ? ";
		if (currencyId != 1) {
			sql += "AND CURRENCY_ID = ? ";
		}
		sql += ") AS BEG_BAL_TBL "
			+ "UNION ALL "
			+ "SELECT COMPANY_ID, DIVISION, CURRENCY_ID, CUSTOMER_ACCOUNT_ID, GL_DATE, REFERENCE_NUMBER, AR_OR_REF_NO, "
			+ "PO_PCR_NO, SOURCE, DESCRIPTION, ";
		if (currencyId == 1) {
			sql += "TRANSACTION_AMOUNT, RECEIPT_AMOUNT, GAIN_LOSS, ";
		} else {
			sql += "(TRANSACTION_AMOUNT/CURRENCY_RATE_VALUE) AS TRANSACTION_AMOUNT, CASE WHEN SOURCE != 'Account Collection' THEN (RECEIPT_AMOUNT/CURRENCY_RATE_VALUE) "
				+ "ELSE (SELECT (AC.AMOUNT/COALESCE(AC.CURRENCY_RATE_VALUE, 0)) FROM AR_RECEIPT AC WHERE AC.AR_RECEIPT_ID = ID) END AS RECEIPT_AMOUNT, 0 AS GAIN_LOSS, ";
		}
		sql += "CREATED_DATE FROM V_CUSTOMER_ACCT_HISTORY "
			+ "WHERE COMPANY_ID = ? ";
		if (divisionId != -1) {
			sql += "AND DIVISION_ID = ? ";
		}
		sql += "AND CUSTOMER_ACCOUNT_ID = ? AND GL_DATE BETWEEN ? AND ? ";
		if (currencyId != 1) {
			sql += "AND CURRENCY_ID = ? ";
		}
		sql += ") AS TBL ORDER BY GL_DATE, CREATED_DATE";
		CustomerAccountHistoryHandler handler = new CustomerAccountHistoryHandler(param, this);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class CustomerAccountHistoryHandler implements QueryResultHandler<CustomerAccountHistoryDto> {
		private CustomerAccountHistoryParam param;
		private CustomerAccountHistoryHandler (CustomerAccountHistoryParam param, ArCustomerAcctDaoImpl daoImpl) {
			this.param = param;
		}

		@Override
		public List<CustomerAccountHistoryDto> convert(List<Object[]> queryResult) {
			List<CustomerAccountHistoryDto> dto = new ArrayList<CustomerAccountHistoryDto>();
			double balance = 0;
			for (Object[] rowResult : queryResult) {
				int index = 0;
				String source = (String) rowResult[index++];
				String division = (String) rowResult[index++];
				Date glDate = (Date) rowResult[index++];
				String referenceNumber = (String) rowResult[index++];
				String arOrNO = (String) rowResult[index++];
				String poNo = (String) rowResult[index++];
				String description = (String) rowResult[index++];
				double transactionAmount = NumberFormatUtil.convertBigDecimalToDouble(rowResult[index++]);
				double receiptAmount = NumberFormatUtil.convertBigDecimalToDouble(rowResult[index++]);
				Integer currencyId = (Integer) rowResult[index++];
				double gainLoss = NumberFormatUtil.convertBigDecimalToDouble(rowResult[index++]);
				if (source.equalsIgnoreCase("Beginning Balance")) {
					balance = transactionAmount - (receiptAmount + gainLoss);
					transactionAmount = 0;
					receiptAmount = 0;
					gainLoss = 0;
				}
				dto.add(CustomerAccountHistoryDto.getInstanceOf(glDate, referenceNumber, "", description, arOrNO, poNo,
						transactionAmount, receiptAmount, gainLoss, balance, division, currencyId));
			}
			return dto;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int companyId = param.getCompanyId();
			int divisionId = param.getDivisionId();
			int currencyId = param.getCurrencyId();
			int csAcctId = param.getCustomerAcctId();
			Date dateFrom = param.getDateFrom();
			// set parameters
			query.setParameter(index, companyId);
			if (divisionId != -1) {
				query.setParameter(++index, divisionId);
			}
			query.setParameter(++index, csAcctId);
			query.setParameter(++index, dateFrom);
			if (currencyId != 1) {
				query.setParameter(++index, currencyId);
			}
			query.setParameter(++index, companyId);
			if (divisionId != -1) {
				query.setParameter(++index, divisionId);
			}
			query.setParameter(++index, csAcctId);
			query.setParameter(++index, dateFrom);
			query.setParameter(++index, param.getDateTo());
			if (currencyId != 1) {
				query.setParameter(++index, currencyId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SOURCE", Hibernate.STRING);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("GL_DATE", Hibernate.DATE);
			query.addScalar("REFERENCE_NUMBER", Hibernate.STRING);
			query.addScalar("AR_OR_REF_NO", Hibernate.STRING);
			query.addScalar("PO_PCR_NO", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("TRANSACTION_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("RECEIPT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("CURRENCY_ID", Hibernate.INTEGER);
			query.addScalar("GAIN_LOSS", Hibernate.DOUBLE);
		}
	}

	@Override
	public Page<CustomerBalancesSummaryDto> getCustomerBalancesSummaryReport(Integer customerAcctId, Integer companyId, Integer divisionId,
			Integer balanceOption, Date asOfDate, Integer currencyId, Integer accountId, PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT AR_CUSTOMER_ID, NAME, ROUND(SUM(TRANSACTION_AMOUNT), 2) AS TRANSACTION_AMOUNT, "
				+ "ROUND(SUM(RECEIPT_AMOUNT), 2) AS RECEIPT_AMOUNT, ROUND(SUM(ADVANCE_PAYMENT), 2) AS ADVANCE_PAYMENT, "
				+ "ROUND(SUM(GAIN_LOSS), 2) AS GAIN_LOSS FROM ( "
				+ "SELECT AT.AR_TRANSACTION_ID AS ID, C.AR_CUSTOMER_ID, C.NAME, "
				+ (currencyId != 1 ? "(AT.AMOUNT / AT.CURRENCY_RATE_VALUE) " : "AT.AMOUNT ")
				+ "AS TRANSACTION_AMOUNT, 0 AS RECEIPT_AMOUNT, 0 AS ADVANCE_PAYMENT, 0 AS GAIN_LOSS "
				+ "FROM AR_TRANSACTION AT "
				+ "INNER JOIN AR_CUSTOMER C ON C.AR_CUSTOMER_ID = AT.CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT CA ON CA.AR_CUSTOMER_ACCOUNT_ID = AT.CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = CA.DEFAULT_DEBIT_AC_ID "
				+ "INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "
				+ "WHERE F.IS_COMPLETE = 1 "
				+ "AND AT.AR_TRANSACTION_TYPE_ID IN (17, 18, 19, 20, 21, 22) "
				+ "AND AT.COMPANY_ID = ? "
				+ (accountId != null && accountId != -1 ? "AND AC.ACCOUNT_ID = ? " : "")
				+ (divisionId > 0 ? "AND AT.DIVISION_ID = ? "  : "")
				+ "AND AT.GL_DATE <= ? "
				+ (currencyId != 1 ? "AND AT.CURRENCY_ID = ? "  : "")
				+ (customerAcctId > 0 ? "AND AT.CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT AI.AR_INVOICE_ID AS ID, C.AR_CUSTOMER_ID, C.NAME, "
				+ (currencyId != 1 ? "(AI.AMOUNT / AI.CURRENCY_RATE_VALUE) " : "AI.AMOUNT ")
				+ "AS TRANSACTION_AMOUNT, 0 AS RECEIPT_AMOUNT, 0 AS ADVANCE_PAYMENT, 0 AS GAIN_LOSS "
				+ "FROM AR_INVOICE AI "
				+ "INNER JOIN AR_CUSTOMER C ON C.AR_CUSTOMER_ID = AI.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT CA ON CA.AR_CUSTOMER_ACCOUNT_ID = AI.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = CA.DEFAULT_DEBIT_AC_ID "
				+ "INNER JOIN FORM_WORKFLOW F ON AI.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "
				+ "WHERE AI.AMOUNT != 0 "
				+ "AND F.IS_COMPLETE = 1 "
				+ "AND AI.COMPANY_ID = ? "
				+ (accountId != null && accountId != -1 ? "AND AC.ACCOUNT_ID = ? " : "")
				+ (divisionId > 0 ? "AND AI.DIVISION_ID = ? "  : "")
				+ "AND AI.DATE_RECEIVED <= ? "
				+ (currencyId != 1 ? "AND AI.CURRENCY_ID = ? "  : "")
				+ (customerAcctId > 0 ? "AND AI.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT CAP.CUSTOMER_ADVANCE_PAYMENT_ID, CAP.AR_CUSTOMER_ID, C.NAME, 0 AS TRANSACTION_AMOUNT, "
				+ "0 AS RECEIPT_AMOUNT, CAP.AMOUNT AS ADVANCE_PAYMENT, 0 AS GAIN_LOSS "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN AR_CUSTOMER C ON C.AR_CUSTOMER_ID = CAP.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT CA ON CA.AR_CUSTOMER_ACCOUNT_ID = CAP.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = CA.DEFAULT_DEBIT_AC_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND CAP.COMPANY_ID = ? "
				+ (accountId != null && accountId != -1 ? "AND AC.ACCOUNT_ID = ? " : "")
				+ (divisionId > 0 ? "AND CAP.DIVISION_ID = ? "  : "")
				+ "AND CAP.MATURITY_DATE <= ? "
				+ (currencyId != 1 ? "AND CAP.CURRENCY_ID = ? "  : "")
				+ (customerAcctId > 0 ? "AND CAP.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT PR.PROJECT_RETENTION_ID AS ID, C.AR_CUSTOMER_ID, C.NAME, "
				+ (currencyId != 1 ? "(-PR.AMOUNT / PR.CURRENCY_RATE_VALUE) " : "-PR.AMOUNT ")
				+ "AS TRANSACTION_AMOUNT, 0 AS RECEIPT_AMOUNT, 0 AS ADVANCE_PAYMENT, 0 AS GAIN_LOSS "
				+ "FROM PROJECT_RETENTION PR "
				+ "INNER JOIN AR_CUSTOMER C ON C.AR_CUSTOMER_ID = PR.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT CA ON CA.AR_CUSTOMER_ACCOUNT_ID = PR.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = CA.DEFAULT_DEBIT_AC_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND PR.COMPANY_ID = ? "
				+ (accountId != null && accountId != -1 ? "AND AC.ACCOUNT_ID = ? " : "")
				+ (divisionId > 0 ? "AND PR.DIVISION_ID = ? "  : "")
				+ "AND PR.DATE <= ? "
				+ (currencyId != 1 ? "AND PR.CURRENCY_ID = ? "  : "")
				+ (customerAcctId > 0 ? "AND PR.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT AC.AR_RECEIPT_ID AS ID, C.AR_CUSTOMER_ID, C.NAME, 0 AS TRANSACTION_AMOUNT, "
				+ (currencyId != 1 ? "(AC.AMOUNT / COALESCE(AC.CURRENCY_RATE_VALUE, 0)) AS RECEIPT_AMOUNT, 0 AS ADVANCE_PAYMENT, 0 AS GAIN_LOSS "
					: "COALESCE((SELECT SUM(ROUND(ARRL.AMOUNT, 2)) FROM AR_RECEIPT_LINE ARRL WHERE ARRL.AR_RECEIPT_ID = AC.AR_RECEIPT_ID), 0) AS RECEIPT_AMOUNT, "
				+ "0 AS ADVANCE_PAYMENT, (AC.AMOUNT + AC.RECOUPMENT + AC.RETENTION) - COALESCE((SELECT SUM(ROUND(ARRL.AMOUNT, 2)) "
				+ "FROM AR_RECEIPT_LINE ARRL WHERE ARRL.AR_RECEIPT_ID = AC.AR_RECEIPT_ID), 0) AS GAIN_LOSS ")
				+ "FROM AR_RECEIPT AC "
				+ "INNER JOIN AR_CUSTOMER C ON C.AR_CUSTOMER_ID = AC.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT CA ON CA.AR_CUSTOMER_ACCOUNT_ID = AC.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION ACM ON ACM.ACCOUNT_COMBINATION_ID = CA.DEFAULT_DEBIT_AC_ID "
				+ "INNER JOIN FORM_WORKFLOW F ON AC.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "
				+ "WHERE F.CURRENT_STATUS_ID != 4 "
				+ "AND AC.COMPANY_ID = ? "
				+ (accountId != null && accountId != -1 ? "AND ACM.ACCOUNT_ID = ? " : "")
				+ (divisionId > 0 ? "AND AC.DIVISION_ID = ? "  : "")
				+ "AND AC.MATURITY_DATE <= ? "
				+ (currencyId != 1 ? "AND AC.CURRENCY_ID = ? "  : "")
				+ (customerAcctId > 0 ? "AND AC.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ ") AS TBL GROUP BY AR_CUSTOMER_ID ORDER BY NAME");
		CustomerBalaancesSummaryHandler handler = new CustomerBalaancesSummaryHandler(customerAcctId, divisionId,
				companyId, asOfDate, currencyId, accountId, balanceOption);
		return getAllAsPage(sql.toString(), pageSetting, handler);
	}

	private static class CustomerBalaancesSummaryHandler implements QueryResultHandler<CustomerBalancesSummaryDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer customerAcctId;
		private Date asOfDate;
		private Integer currencyId;
		private Integer accountId;
		private Integer balanceOption;

		private CustomerBalaancesSummaryHandler (Integer customerAcctId, Integer divisionId,
				Integer companyId, Date asOfDate, Integer currencyId, Integer accountId, Integer balanceOption) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.customerAcctId = customerAcctId;
			this.asOfDate = asOfDate;
			this.currencyId = currencyId;
			this.accountId = accountId;
			this.balanceOption = balanceOption;
		}

		@Override
		public List<CustomerBalancesSummaryDto> convert(List<Object[]> queryResult) {
			List<CustomerBalancesSummaryDto> dto = new ArrayList<CustomerBalancesSummaryDto>();
			for (Object[] rowResult : queryResult) {
				String name = (String) rowResult[0];
				double transactionAmount = (Double) rowResult[1] != null ? (Double) rowResult[1] : 0;
				double receiptAmount = (Double) rowResult[2] != null ? (Double) rowResult[2] : 0;
				double advances = (Double) rowResult[3] != null ? (Double) rowResult[3] : 0;
				double gainLoss = (Double) rowResult[4] != null ? (Double) rowResult[4] : 0;
				double balance = (transactionAmount + advances) - receiptAmount;
				if ((balanceOption == 2 && balance < 0) || (balanceOption == 1 && balance == 0)) {
					continue;
				}
				dto.add(CustomerBalancesSummaryDto.getInstancesOf(name, transactionAmount,
						(receiptAmount - advances), gainLoss, balance));
			}
			return dto;
		}
		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int noOfTbls = 5;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, companyId);
				if (accountId != null && accountId != -1) {
					query.setParameter(++index, accountId);
				}
				if (divisionId > 0) {
					query.setParameter(++index, divisionId);
				}
				query.setParameter(++index, asOfDate);
				if (currencyId != 1) {
					query.setParameter(++index, currencyId);
				}
				if (customerAcctId > 0) {
					query.setParameter(++index, customerAcctId);
				}
				if (i < (noOfTbls-1)) {
					++index;
				}
			}
			return index;
		}
		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("NAME", Hibernate.STRING);
			query.addScalar("TRANSACTION_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("RECEIPT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("ADVANCE_PAYMENT", Hibernate.DOUBLE);
			query.addScalar("GAIN_LOSS", Hibernate.DOUBLE);
		}
	}

	@Override
	public Page<StatementOfAccountDto> getSoaData(StatementOfAccountParam param, PageSetting pageSetting) {
		String sql = "SELECT ID, REFERENCE_NUMBER, INVOICE_NUMBER, TRANSACTION_AMOUNT, RECEIPT_AMOUNT, DATE, TERM_ID, "
				+ "TERM_DAYS, DUE_DATE, DR_REFERENCE_IDS, PAID_ADVANCES FROM ( "
				+ "SELECT AT.AR_TRANSACTION_ID AS ID, AT.TRANSACTION_NUMBER AS REFERENCE_NUMBER, AT.DESCRIPTION AS INVOICE_NUMBER, "
				+ "AT.AMOUNT AS TRANSACTION_AMOUNT, COALESCE((SELECT SUM(RT.AMOUNT) FROM AR_RECEIPT_TRANSACTION RT "
				+ "INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = RT.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW F ON AR.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "
				+ "WHERE RT.AR_TRANSACTION_ID = AT.AR_TRANSACTION_ID AND F.CURRENT_STATUS_ID != 4), 0) AS RECEIPT_AMOUNT, "
				+ "AT.TRANSACTION_DATE AS DATE, T.TERM_ID, T.DAYS AS TERM_DAYS, AT.DUE_DATE, '' AS DR_REFERENCE_IDS, 0 AS PAID_ADVANCES "
				+ "FROM AR_TRANSACTION AT "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON ACA.AR_CUSTOMER_ACCOUNT_ID = AT.CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN TERM T ON ACA.TERM_ID = T.TERM_ID "
				+ "INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "
				+ "WHERE F.IS_COMPLETE = 1 "
				+ "AND AT.AR_TRANSACTION_TYPE_ID IN (1, 2, 3, 4, 5) "
				+ "AND AT.COMPANY_ID = ? "
				+ "AND AT.CUSTOMER_ID = ? "
				+ (param.getDateFrom() != null && param.getDateTo() != null ? "AND AT.TRANSACTION_DATE BETWEEN ? AND ? " : "")
				+ (param.getCustomerAcctId() > 0 ? "AND AT.CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT AC.AR_RECEIPT_ID AS ID, CONCAT('AC-', AC.SEQUENCE_NO) AS REFERENCE_NUMBER, "
				+ "IF(AC.REF_NUMBER != '', CONCAT(AC.RECEIPT_NUMBER, ', ', AC.REF_NUMBER), AC.RECEIPT_NUMBER) AS INVOICE_NUMBER, "
				+ "0 AS TRANSACTION_AMOUNT, AC.AMOUNT AS RECEIPT_AMOUNT, AC.MATURITY_DATE AS DATE, 1 AS TERM_ID, 0 AS TERM_DAYS, "
				+ "NULL AS DUE_DATE, '' AS DR_REFERENCE_IDS, 0 AS PAID_ADVANCES "
				+ "FROM AR_RECEIPT AC "
				+ "INNER JOIN FORM_WORKFLOW F ON AC.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "
				+ "INNER JOIN AR_CUSTOMER C ON C.AR_CUSTOMER_ID = AC.AR_CUSTOMER_ID "
				+ "WHERE F.CURRENT_STATUS_ID != 4 "
				+ "AND AC.COMPANY_ID = ? "
				+ "AND AC.AR_CUSTOMER_ID = ? "
				+ (param.getDateFrom() != null && param.getDateTo() != null ? "AND AC.MATURITY_DATE BETWEEN ? AND ? " : "")
				+ (param.getCustomerAcctId() > 0 ? "AND AC.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND AC.AR_RECEIPT_ID NOT IN ("
				+ "SELECT ART.AR_RECEIPT_ID FROM AR_RECEIPT_TRANSACTION ART "
				+ "INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = ART.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW F ON AR.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "
				+ "WHERE F.CURRENT_STATUS_ID != 4) "
				+ "UNION ALL "
				+ "SELECT AT.AR_TRANSACTION_ID AS ID, '' AS REFERENCE_NUMBER, AT.TRANSACTION_NUMBER AS INVOICE_NUMBER, "
				+ "AT.AMOUNT AS TRANSACTION_AMOUNT, COALESCE((SELECT SUM(RT.AMOUNT) FROM AR_RECEIPT_TRANSACTION RT "
				+ "INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = RT.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW F ON AR.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "
				+ "WHERE RT.AR_TRANSACTION_ID=AT.AR_TRANSACTION_ID  AND F.CURRENT_STATUS_ID != 4), 0) AS RECEIPT_AMOUNT, "
				+ "AT.TRANSACTION_DATE AS DATE, T.TERM_ID, T.DAYS AS TERM_DAYS, AT.DUE_DATE, ARI.DR_REFERENCE_IDS, "
				+ "0 AS PAID_ADVANCES "
				+ "FROM AR_TRANSACTION AT "
				+ "INNER JOIN AR_INVOICE_TRANSACTION ARIT ON ARIT.AR_TRANSACTION_ID = AT.AR_TRANSACTION_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.AR_INVOICE_ID = ARIT.AR_INVOICE_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON ACA.AR_CUSTOMER_ACCOUNT_ID = AT.CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN TERM T ON ACA.TERM_ID = T.TERM_ID "
				+ "INNER JOIN FORM_WORKFLOW F ON AT.FORM_WORKFLOW_ID = F.FORM_WORKFLOW_ID "
				+ "WHERE F.IS_COMPLETE = 1 "
				+ "AND AT.AR_TRANSACTION_TYPE_ID = 16 "
				+ "AND AT.COMPANY_ID = ? "
				+ "AND AT.CUSTOMER_ID = ? "
				+ (param.getDateFrom() != null && param.getDateTo() != null ? "AND AT.TRANSACTION_DATE BETWEEN ? AND ? " : "")
				+ (param.getCustomerAcctId() > 0 ? "AND AT.CUSTOMER_ACCOUNT_ID = ? " : "")
				+ ") AS TBL ORDER BY DATE, REFERENCE_NUMBER";
		StatementOfAcctHandler handler = new StatementOfAcctHandler(param);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class StatementOfAcctHandler implements QueryResultHandler<StatementOfAccountDto> {
		private StatementOfAccountParam param;

		private StatementOfAcctHandler (StatementOfAccountParam param) {
			this.param = param;
		}

		@Override
		public List<StatementOfAccountDto> convert(List<Object[]> queryResult) {
			List<StatementOfAccountDto> soaData = new ArrayList<StatementOfAccountDto>();
			StatementOfAccountDto soaDto = null;
			for (Object[] rowResult : queryResult) {
				soaDto = new StatementOfAccountDto();
				soaDto.setId((Integer) rowResult[0]);
				soaDto.setTransactionNumber((String) rowResult[1]);
				soaDto.setInvoiceNumber((String) rowResult[2]);
				soaDto.setTransactionAmount((Double)rowResult[3]);
				soaDto.setCollectionAmount((Double)rowResult[4]);
				soaDto.setTransactionDate((Date) rowResult[5]);
				soaDto.setTermId((Integer) rowResult[6]);
				soaDto.setTermDays((Integer) rowResult[7]);
				Object objDate = rowResult[8];
				if (objDate != null) {
					soaDto.setDueDate(DateUtil.formatDate((Date) objDate));
				}
				soaDto.setDrReferenceIds((String) rowResult[9]);
				soaDto.setCollectedAdvanceAmt((Double) rowResult[10]);
				soaData.add(soaDto);
			}
			return soaData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int noOfTbls = 3;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, param.getCompanyId());
				query.setParameter(++index, param.getCustomerId());
				if (param.getDateFrom() != null && param.getDateTo() != null) {
					query.setParameter(++index, param.getDateFrom());
					query.setParameter(++index, param.getDateTo());
				}
				if (param.getCustomerAcctId() > 0) {
					query.setParameter(++index, param.getCustomerAcctId());
				}
				if (i < (noOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("REFERENCE_NUMBER", Hibernate.STRING);
			query.addScalar("INVOICE_NUMBER", Hibernate.STRING);
			query.addScalar("TRANSACTION_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("RECEIPT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("TERM_ID", Hibernate.INTEGER);
			query.addScalar("TERM_DAYS", Hibernate.INTEGER);
			query.addScalar("DUE_DATE", Hibernate.DATE);
			query.addScalar("DR_REFERENCE_IDS", Hibernate.STRING);
			query.addScalar("PAID_ADVANCES", Hibernate.DOUBLE);
		}
	}

	@Override
	public Page<StatementOfAccountDto> getBillingStatement(StatementOfAccountParam param, PageSetting pageSetting){
		String sql = "SELECT ID, DIVISION, SI_DATE, INVOICE_NUMBER, DR_REF_NUMBER, QUANTITY, UOM, DESCRIPTION, UNIT_PRICE, POPCR_NUMBER, AMOUNT, DUE_DATE FROM ( "
				+ "SELECT ID, DIVISION, SI_DATE, INVOICE_NUMBER, DR_REF_NUMBER, QUANTITY, UOM, DESCRIPTION, UNIT_PRICE, POPCR_NUMBER, "
				+ "(AMOUNT - ROUND(((AMOUNT/TR_AMOUNT) * PAYMENT), 6)) AS AMOUNT, "
				+ "DUE_DATE, PAYMENT, INVOICE_AMOUNT, EB_OBJECT_ID FROM ("
				+ "SELECT ARI.AR_INVOICE_ID AS ID, D.NAME AS DIVISION, AI.DATE AS SI_DATE, "
				+ "CONCAT('ARI','-',AI.SEQUENCE_NO) AS INVOICE_NUMBER, DR.DR_REF_NUMBER AS DR_REF_NUMBER, "
				+ "ARI.QUANTITY, COALESCE(UM.NAME, '') AS UOM, I.DESCRIPTION, ROUND((ARI.SRP/AI.CURRENCY_RATE_VALUE), 6) AS UNIT_PRICE, "
				+ "DR.PO_NUMBER AS POPCR_NUMBER, ROUND(((ARI.SRP * ARI.QUANTITY)/AI.CURRENCY_RATE_VALUE), 6) AS AMOUNT, AI.DUE_DATE, "
				+ "ROUND(((COALESCE("
				+ "(SELECT SUM(ARL.AMOUNT) AS PAID_AMOUNT "
				+ "FROM AR_RECEIPT ARR "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.AR_RECEIPT_ID = ARR.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.FROM_OBJECT_ID = AI.EB_OBJECT_ID)"
				+ ", 0) + COALESCE(AI.RETENTION, 0))/AI.CURRENCY_RATE_VALUE), 6) AS PAYMENT, "
				+ "ROUND(((AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) + COALESCE(AI.WT_VAT_AMOUNT, 0) "
				+ "+ COALESCE(AI.RETENTION, 0))/AI.CURRENCY_RATE_VALUE), 6) AS TR_AMOUNT, "
				+ "ROUND((COALESCE(AI.WT_AMOUNT, 0)/AI.CURRENCY_RATE_VALUE), 6) AS WT_AMOUNT, "
				+ "ROUND((COALESCE(AI.WT_VAT_AMOUNT, 0)/AI.CURRENCY_RATE_VALUE), 6) AS WT_VAT_AMOUNT, "
				+ "AI.AMOUNT AS INVOICE_AMOUNT, AI.EB_OBJECT_ID AS EB_OBJECT_ID "
				+ "FROM AR_INVOICE_ITEM ARI "
				+ "INNER JOIN AR_INVOICE AI ON AI.AR_INVOICE_ID = ARI.AR_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FWF ON FWF.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID= AI.DIVISION_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON FIND_IN_SET(DR.DELIVERY_RECEIPT_ID, (REPLACE(AI.DR_REFERENCE_IDS, ' ', ''))) "
				+ "LEFT JOIN ITEM I ON I.ITEM_ID = ARI.ITEM_ID "
				+ "LEFT JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE AI.COMPANY_ID = ? "
				+ "AND AI.AR_CUSTOMER_ID = ? "
				+ (param.getDivisionId() != -1 ? "AND AI.DIVISION_ID = ? " : "")
				+ (param.getDateFrom() != null && param.getDateTo() != null ? "AND AI.DATE BETWEEN ? AND ? " : "")
				+ (param.getCustomerAcctId() > 0 ? "AND AI.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND AI.CURRENCY_ID = ? "
				+ "AND FWF.IS_COMPLETE=1 "
				+ "UNION ALL "
				+ "SELECT AI.AR_INVOICE_ID AS ID, D.NAME AS DIVISION, AI.DATE AS SI_DATE, "
				+ "CONCAT('ARI','-',AI.SEQUENCE_NO) AS INVOICE_NUMBER,  DR.DR_REF_NUMBER, SI.QUANTITY, COALESCE(UM.NAME, '') AS UOM, "
				+ "CONCAT(I.DESCRIPTION,' ',SI.SERIAL_NUMBER) AS DESCRIPTION, ROUND((SI.SRP/AI.CURRENCY_RATE_VALUE), 6) AS UNIT_PRICE, "
				+ "DR.PO_NUMBER AS POPCR_NUMBER, ROUND(((SI.SRP * SI.QUANTITY)/AI.CURRENCY_RATE_VALUE), 6) AS AMOUNT, AI.DUE_DATE, "
				+ "ROUND(((COALESCE("
				+ "(SELECT SUM(ARL.AMOUNT) AS PAID_AMOUNT "
				+ "FROM AR_RECEIPT ARR "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.AR_RECEIPT_ID = ARR.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.FROM_OBJECT_ID = AI.EB_OBJECT_ID)"
				+ ", 0) + COALESCE(AI.RETENTION, 0))/AI.CURRENCY_RATE_VALUE), 6) AS PAYMENT, "
				+ "ROUND(((AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) + COALESCE(AI.WT_VAT_AMOUNT, 0) "
				+ "+ COALESCE(AI.RETENTION, 0))/AI.CURRENCY_RATE_VALUE), 6) AS TR_AMOUNT, "
				+ "ROUND((COALESCE(AI.WT_AMOUNT, 0)/AI.CURRENCY_RATE_VALUE), 6) AS WT_AMOUNT, "
				+ "ROUND((COALESCE(AI.WT_VAT_AMOUNT, 0)/AI.CURRENCY_RATE_VALUE), 6) AS WT_VAT_AMOUNT, "
				+ "AI.AMOUNT AS INVOICE_AMOUNT, AI.EB_OBJECT_ID "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AR_INVOICE AI ON AI.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FWF ON FWF.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID= AI.DIVISION_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON FIND_IN_SET(DR.DELIVERY_RECEIPT_ID, (REPLACE(AI.DR_REFERENCE_IDS, ' ', ''))) "
				+ "LEFT JOIN ITEM I ON I.ITEM_ID = SI.ITEM_ID "
				+ "LEFT JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE AI.COMPANY_ID = ? "
				+ "AND AI.AR_CUSTOMER_ID = ? "
				+ (param.getDivisionId() != -1 ? "AND AI.DIVISION_ID = ? " : "")
				+ (param.getDateFrom() != null && param.getDateTo() != null ? "AND AI.DATE BETWEEN ? AND ? " : "")
				+ (param.getCustomerAcctId() > 0 ? "AND AI.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND SI.ACTIVE=1 AND FWF.IS_COMPLETE=1 AND OTO.OR_TYPE_ID = 12006 "
				+ "AND AI.CURRENCY_ID = ? "
				+ "UNION ALL "
				+ "SELECT AIL.AR_INVOICE_ID AS ID, D.NAME AS DIVISION, AI.DATE AS SI_DATE, "
				+ "CONCAT('ARI','-',AI.SEQUENCE_NO) AS INVOICE_NUMBER, DR.DR_REF_NUMBER, "
				+ "AIL.QUANTITY, COALESCE(UM.NAME, '') AS UOM, DRL.DESCRIPTION AS DESCRIPTION, "
				+ "ROUND((AIL.UP_AMOUNT/AI.CURRENCY_RATE_VALUE), 6) AS UNIT_PRICE, DR.PO_NUMBER AS POPCR_NUMBER, "
				+ "ROUND(((AIL.UP_AMOUNT * AIL.QUANTITY)/AI.CURRENCY_RATE_VALUE), 6) AS AMOUNT, "
				+ "AI.DUE_DATE AS DUE_DATE, "
				+ "ROUND(((COALESCE("
				+ "(SELECT SUM(ARL.AMOUNT) AS PAID_AMOUNT "
				+ "FROM AR_RECEIPT ARR "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.AR_RECEIPT_ID = ARR.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.FROM_OBJECT_ID = AI.EB_OBJECT_ID)"
				+ ", 0) + COALESCE(AI.RETENTION, 0))/AI.CURRENCY_RATE_VALUE), 6) AS PAYMENT, "
				+ "ROUND(((AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) + COALESCE(AI.WT_VAT_AMOUNT, 0) "
				+ "+ COALESCE(AI.RETENTION, 0))/AI.CURRENCY_RATE_VALUE), 6) AS TR_AMOUNT, "
				+ "ROUND((COALESCE(AI.WT_AMOUNT, 0)/AI.CURRENCY_RATE_VALUE), 6) AS WT_AMOUNT, "
				+ "ROUND((COALESCE(AI.WT_VAT_AMOUNT, 0)/AI.CURRENCY_RATE_VALUE), 6) AS WT_VAT_AMOUNT, "
				+ "AI.AMOUNT AS INVOICE_AMOUNT, AI.EB_OBJECT_ID "
				+ "FROM AR_INVOICE_LINE AIL "
				+ "INNER JOIN AR_INVOICE AI ON AI.AR_INVOICE_ID = AIL.AR_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FWF ON FWF.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID= AI.DIVISION_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON FIND_IN_SET(DR.DELIVERY_RECEIPT_ID, (REPLACE(AI.DR_REFERENCE_IDS, ' ', ''))) "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = AIL.EB_OBJECT_ID "
				+ "INNER JOIN DELIVERY_RECEIPT_LINE DRL ON DRL.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN SERVICE_SETTING SS ON SS.SERVICE_SETTING_ID = AIL.SERVICE_SETTING_ID "
				+ "LEFT JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = AIL.UNITOFMEASUREMENT_ID "
				+ "WHERE AI.COMPANY_ID = ? "
				+ "AND AIL.SERVICE_SETTING_ID = DRL.SERVICE_SETTING_ID "
				+ "AND OTO.OR_TYPE_ID = 12007 "
				+ "AND AI.AR_CUSTOMER_ID = ? "
				+ (param.getDivisionId() != -1 ? "AND AI.DIVISION_ID = ? " : "")
				+ (param.getDateFrom() != null && param.getDateTo() != null ? "AND AI.DATE BETWEEN ? AND ? " : "")
				+ (param.getCustomerAcctId() > 0 ? "AND AI.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND SS.ACTIVE = 1 AND FWF.IS_COMPLETE=1 "
				+ "AND AI.CURRENCY_ID = ? "
				+ "UNION ALL "
				+ "SELECT AR.AR_TRANSACTION_ID AS ID, D.NAME AS DIVISION, AR.GL_DATE AS SI_DATE, "
				+ "CONCAT('ART','-',AR.SEQUENCE_NO) AS INVOICE_NUMBER, '' AS DR_REF_NUMBER, AL.QUANTITY, "
				+ "COALESCE(UM.NAME, '') AS UOM, COALESCE(SS.NAME) AS DESCRIPTION, "
				+ "ROUND((AL.UP_AMOUNT/AR.CURRENCY_RATE_VALUE), 6) AS UNIT_PRICE, '' AS POPCR_NUMBER, "
				+ "ROUND(((AL.UP_AMOUNT * AL.QUANTITY)/AR.CURRENCY_RATE_VALUE), 6) AS AMOUNT, "
				+ "AR.DUE_DATE AS DUE_DATE, "
				+ "ROUND((COALESCE("
				+ "(SELECT SUM(ARL.AMOUNT) AS PAID_AMOUNT "
				+ "FROM AR_RECEIPT ARR "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.AR_RECEIPT_ID = ARR.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.FROM_OBJECT_ID = AR.EB_OBJECT_ID)"
				+ ", 0)/AR.CURRENCY_RATE_VALUE), 6) AS PAYMENT, ROUND(((AR.AMOUNT + COALESCE(AR.WT_AMOUNT, 0) "
				+ "+ COALESCE(AR.WT_VAT_AMOUNT, 0))/AR.CURRENCY_RATE_VALUE), 6) AS TR_AMOUNT, "
				+ "ROUND((COALESCE(AR.WT_AMOUNT, 0)/AR.CURRENCY_RATE_VALUE), 6) AS WT_AMOUNT, "
				+ "ROUND((COALESCE(AR.WT_VAT_AMOUNT, 0)/AR.CURRENCY_RATE_VALUE), 6) AS WT_VAT_AMOUNT, "
				+ "AR.AMOUNT AS INVOICE_AMOUNT, AR.EB_OBJECT_ID "
				+ "FROM AR_TRANSACTION AR "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID= AR.DIVISION_ID "
				+ "INNER JOIN FORM_WORKFLOW F ON F.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID "
				+ "INNER JOIN AR_SERVICE_LINE AL ON AL.AR_TRANSACTION_ID = AR.AR_TRANSACTION_ID "
				+ "LEFT JOIN SERVICE_SETTING SS ON SS.SERVICE_SETTING_ID = AL.SERVICE_SETTING_ID "
				+ "LEFT JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = AL.UNITOFMEASUREMENT_ID "
				+ "WHERE F.IS_COMPLETE=1 "
				+ "AND AR.COMPANY_ID = ? "
				+ "AND AR.CUSTOMER_ID = ? "
				+ (param.getDivisionId() != -1 ? "AND AR.DIVISION_ID = ? " : "")
				+ (param.getDateFrom() != null && param.getDateTo() != null ? "AND AR.GL_DATE BETWEEN ? AND ? " : "")
				+ (param.getCustomerAcctId() > 0 ? "AND AR.CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND AR.CURRENCY_ID = ? "
				+ ") AS TBLE "
				+ "HAVING EB_OBJECT_ID NOT IN ( "//Exclude fully paid invoices and transactions
				+ "SELECT OBJECT_ID FROM ( "
				+ "SELECT OTO.FROM_OBJECT_ID AS OBJECT_ID, SUM(ARL.AMOUNT) AS PAID_AMOUNT FROM AR_RECEIPT ARR "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.AR_RECEIPT_ID = ARR.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "GROUP BY OTO.FROM_OBJECT_ID "
				+ ") AS ITBL "
				+ "WHERE PAID_AMOUNT = INVOICE_AMOUNT "
				+ "AND OBJECT_ID = EB_OBJECT_ID) "
				+ ") AS TBL ORDER BY SI_DATE ASC, DUE_DATE ASC, INVOICE_NUMBER ASC  ";//Final table
		BillingStatementtHandler handler = new BillingStatementtHandler(param);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class BillingStatementtHandler implements QueryResultHandler<StatementOfAccountDto> {
		private StatementOfAccountParam param;

		private BillingStatementtHandler (StatementOfAccountParam param) {
			this.param = param;
		}

		@Override
		public List<StatementOfAccountDto> convert(List<Object[]> queryResult) {
			List<StatementOfAccountDto> soaData = new ArrayList<StatementOfAccountDto>();
			StatementOfAccountDto soaDto = null;
			for (Object[] rowResult : queryResult) {
				soaDto = new StatementOfAccountDto();
				soaDto.setDivision((String) rowResult[0]);
				soaDto.setTransactionDate((Date) rowResult[1]);
				soaDto.setInvoiceNumber((String) rowResult[2]);
				soaDto.setDrRefNumber((String) rowResult[3]);
				soaDto.setQuantity((Integer) rowResult[4]);
				soaDto.setUnitMeasurement((String) rowResult[5]);
				soaDto.setArDescription((String) rowResult[6]);
				soaDto.setUnitPrice((Double)rowResult[7]);
				soaDto.setPoNumber((String) rowResult[8]);
				soaDto.setArAmount((Double)rowResult[9]);
				Object objDate = rowResult[10];
				if (objDate != null) {
					soaDto.setDueDate(DateUtil.formatDate((Date) objDate));
				}
				soaData.add(soaDto);
			}
			return soaData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int noOfTbls = 4;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, param.getCompanyId());
				query.setParameter(++index, param.getCustomerId());
				if (param.getDivisionId() != -1 ) {
					query.setParameter(++index, param.getDivisionId());
				}
				if (param.getDateFrom() != null && param.getDateTo() != null) {
					query.setParameter(++index, param.getDateFrom());
					query.setParameter(++index, param.getDateTo());
				}
				if (param.getCustomerAcctId() > 0) {
					query.setParameter(++index, param.getCustomerAcctId());
				}
				query.setParameter(++index, param.getCurrencyId());
				if (i < (noOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("SI_DATE", Hibernate.DATE);
			query.addScalar("INVOICE_NUMBER", Hibernate.STRING);
			query.addScalar("DR_REF_NUMBER", Hibernate.STRING);
			query.addScalar("QUANTITY", Hibernate.INTEGER);
			query.addScalar("UOM", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("UNIT_PRICE", Hibernate.DOUBLE);
			query.addScalar("POPCR_NUMBER", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("DUE_DATE", Hibernate.DATE);
		}
	}

	@Override
	public Page<CustomerBalancesSummaryDto> getCustomerBalSummaryFromDateRange(Integer companyId,
			Integer balanceOption, Date dateFrom, Date dateTo, PageSetting pageSetting) {
		StringBuffer sql = new StringBuffer("SELECT NAME, TRANSACTION_AMOUNT, RECEIPT_AMOUNT, ADVANCE_PAYMENT, BALANCE FROM ( "
				+ "SELECT NAME, SUM(TRANSACTION_AMOUNT) AS TRANSACTION_AMOUNT, "
				+ "SUM(RECEIPT_AMOUNT) AS RECEIPT_AMOUNT, "
				+ "SUM(ADVANCE_PAYMENT) AS ADVANCE_PAYMENT, "
				+ "ROUND((SUM(TRANSACTION_AMOUNT) - SUM(RECEIPT_AMOUNT) - SUM(ADVANCE_PAYMENT)), 2) AS BALANCE "
				+ "FROM V_CUSTOMER_ACCT_HISTORY "
				+ "WHERE COMPANY_ID = ? "
				+ "AND GL_DATE BETWEEN ? AND ? "
				+ "GROUP BY CUSTOMER_ID ) AS CUST_BALANCES ");
		if(balanceOption.equals(0)) {
			//Select only unpaid and partially paid transactions.
			sql.append("WHERE BALANCE != 0 ");
		}
		sql.append("ORDER BY NAME");
		return getAllAsPage(sql.toString(), pageSetting,
				new CustomerBalSummaryFromDateRange(companyId, dateFrom, dateTo));
	}

	private static class CustomerBalSummaryFromDateRange implements QueryResultHandler<CustomerBalancesSummaryDto> {
		private Integer companyId;
		private Date dateFrom;
		private Date dateTo;

		private CustomerBalSummaryFromDateRange (Integer companyId,Date dateFrom, Date dateTo) {
			this.companyId = companyId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public List<CustomerBalancesSummaryDto> convert(List<Object[]> queryResult) {
			List<CustomerBalancesSummaryDto> dto = new ArrayList<CustomerBalancesSummaryDto>();
			for (Object[] rowResult : queryResult) {
				int index = 0;
				String name = (String) rowResult[index++];
				double transactionAmount =(Double)rowResult[index++];
				double receiptAmount = (Double) rowResult[index++];
				double totalAdvances = (Double) rowResult[index++];
				Double balance = (Double) rowResult[index++];
				dto.add(CustomerBalancesSummaryDto.getInstanceOf(name, transactionAmount,
						receiptAmount, balance, totalAdvances));
			}
			return dto;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index++, companyId);
			query.setParameter(index++, dateFrom);
			query.setParameter(index, dateTo);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("NAME", Hibernate.STRING);
			query.addScalar("TRANSACTION_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("RECEIPT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("ADVANCE_PAYMENT", Hibernate.DOUBLE);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
		}
	}

	@Override
	public Page<StatementOfAccountDto> getSoaDataWithAcReference(StatementOfAccountParam param,
			PageSetting pageSetting) {
		String sql = "SELECT ID, REFERENCE_NUMBER, INVOICE_NUMBER, TRANSACTION_AMOUNT, RECEIPT_AMOUNT, "
				+ "DATE, DUE_DATE, TERM_ID, TERM_DAYS, AC_REF_NUMBER, SOURCE "
				+ "FROM V_STATEMENT_OF_ACCOUNT "
				+ "WHERE COMPANY_ID = ? AND CUSTOMER_ID = ? "
				+ (param.getDateFrom() != null && param.getDateTo() != null ? "AND DATE BETWEEN ? AND ? " : "") 
				+ "AND IS_COMPLETE = 1 "
				+ "AND TRANSACTION_AMOUNT != RECEIPT_AMOUNT";
		if(param.getCustomerAcctId() != -1) {
			sql += " AND CUSTOMER_ACCOUNT_ID = ? ";
		}
		sql += " ORDER BY DATE, REFERENCE_NUMBER ";
		SoaAcctHandler handler = new SoaAcctHandler(param);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class SoaAcctHandler implements QueryResultHandler<StatementOfAccountDto> {
		private StatementOfAccountParam param;

		private SoaAcctHandler (StatementOfAccountParam param) {
			this.param = param;
		}

		@Override
		public List<StatementOfAccountDto> convert(List<Object[]> queryResult) {
			List<StatementOfAccountDto> soaData = new ArrayList<StatementOfAccountDto>();
			StatementOfAccountDto soaDto = null;
			for (Object[] rowResult : queryResult) {
				int index = 0;
				soaDto = new StatementOfAccountDto();
				soaDto.setId((Integer) rowResult[index++]);
				soaDto.setTransactionNumber((String) rowResult[index++]);
				soaDto.setInvoiceNumber((String) rowResult[index++]);
				soaDto.setTransactionAmount((Double)rowResult[index++]);
				soaDto.setCollectionAmount((Double)rowResult[index++]);
				soaDto.setTransactionDate((Date) rowResult[index++]);
				soaDto.setTermId((Integer) rowResult[index++]);
				soaDto.setTermDays((Integer) rowResult[index++]);
				Object objDate = rowResult[index++];
				if (objDate != null) {
					soaDto.setDueDate(DateUtil.formatDate((Date) objDate));
				}
				soaDto.setAcSequenceNo((String) rowResult[index++]);
				soaData.add(soaDto);
			}
			return soaData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, param.getCompanyId());
			query.setParameter(++index, param.getCustomerId());
			if (param.getDateFrom() != null && param.getDateTo() != null) {
				query.setParameter(++index, param.getDateFrom());
				query.setParameter(++index, param.getDateTo());
			}
			if(param.getCustomerAcctId() != -1) {
				query.setParameter(++index, param.getCustomerAcctId());
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("REFERENCE_NUMBER", Hibernate.STRING);
			query.addScalar("INVOICE_NUMBER", Hibernate.STRING);
			query.addScalar("TRANSACTION_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("RECEIPT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("TERM_ID", Hibernate.INTEGER);
			query.addScalar("TERM_DAYS", Hibernate.INTEGER);
			query.addScalar("DUE_DATE", Hibernate.DATE);
			query.addScalar("AC_REF_NUMBER", Hibernate.STRING);
			query.addScalar("SOURCE", Hibernate.STRING);
		}
	}

	@Override
	public List<ArCustomerAccount> getArCustomerAccts(Integer companyId, Integer projectId) {
		DetachedCriteria dc = DetachedCriteria.forClass(ArCustomerAccount.class);
		dc.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(ArCustomerAccount.FIELD.arCustomerId.name(), projectId));
		return getHibernateTemplate().findByCriteria(dc);
	}

	@Override
	public double getPaidAdvances(String drReferenceIds) {
		double totalPaidAdvances = 0;
		String processedIds = processStrIds(drReferenceIds);
		if (!processedIds.isEmpty()) {
			String sql = "SELECT AR_CUSTOMER_ID, SUM(CASH) AS PAID_ADVANCES FROM ( "
					+ "SELECT CAP.CUSTOMER_ADVANCE_PAYMENT_ID, CAP.AR_CUSTOMER_ID, COALESCE(CAP.CASH, 0) AS CASH "
					+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
					+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = CAP.SALES_ORDER_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 "
					+ "AND DR.DELIVERY_RECEIPT_ID IN ("+processedIds+") "
					+ "GROUP BY CAP.CUSTOMER_ADVANCE_PAYMENT_ID "
					+ ") AS TBL";
			Session session = null;
			try {
				session = getSession();
				SQLQuery query = session.createSQLQuery(sql);
				query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
				query.addScalar("PAID_ADVANCES", Hibernate.DOUBLE);
				List<Object[]> list = query.list();
				if (list != null && !list.isEmpty()) {
					for (Object[] row : list) {
						totalPaidAdvances = (Double) row[1] != null ? (Double) row[1] : 0;
						break; // expecting 1 row only
					}
				}
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
		return totalPaidAdvances;
	}


	private String processStrIds(String strIds) {
		String processedIds = "";
		String tmpIds[] = strIds.split(";");
		int row = 0;
		for (String tmp : tmpIds) {
			if (tmp != null && !tmp.isEmpty()) {
				if (row == 0) {
					processedIds += tmp.trim();
				} else {
					processedIds += ", " + tmp.trim();
				}
			}
			row++;
		}
		return removeExtraCommas(processedIds);
	}

	private String removeExtraCommas(String strIds) {
		String processedIds = "";
		String tmpIds[] = strIds.split(",");
		int row = 0;
		for (String tmp : tmpIds) {
			tmp = tmp.trim();
			if (!tmp.isEmpty()) {
				if (row == 0) {
					processedIds += tmp.trim();
				} else {
					processedIds += ", " + tmp.trim();
				}
				row++;
			}
		}
		return processedIds;
	}
}
