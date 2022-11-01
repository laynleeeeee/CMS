package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptTransaction;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.report.ArTransactionRegisterParam;
import eulap.eb.service.report.TransactionAgingParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.ArTransactionAgingDto;
import eulap.eb.web.dto.ArTransactionRegisterDto;
import eulap.eb.web.dto.PaymentStatus;
import eulap.eb.web.dto.VATDeclarationDto;

/**
 * DAO implementation class of {@link ArTransactionDao}.

 */
public class ArTransactionDaoImpl extends BaseDao<ArTransaction> implements ArTransactionDao{
	@Override
	protected Class<ArTransaction> getDomainClass() {
		return ArTransaction.class;
	}
	
	@Override
	public Collection<ArTransaction> getTransactionsByCustomerAcct(
			int customerAcctId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), customerAcctId));
		return getAll(dc);
	}

	@Override
	public List<ArTransaction> getARTransactions(int serviceLeaseKeyId, Integer arCustomerAccountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), arCustomerAccountId));
		return getAll(dc);
	}

	@Override
	public Page<ArTransaction> searchARTransactions(final Integer arTransactionTypeId, 
			final String searchCriteria, final PageSetting pageSetting) {
		return searchARTransactions(arTransactionTypeId, null, searchCriteria, pageSetting);
	}

	@Override
	public Page<ArTransaction> searchARTransactions(final Integer arTransactionTypeId, final Integer divisionId,
			final String searchCriteria, final PageSetting pageSetting) {
		HibernateCallback<Page<ArTransaction>> arTransactionCallback = 
				new HibernateCallback<Page<ArTransaction>>() {
			@Override
			public Page<ArTransaction> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(ArTransaction.class);
				if (arTransactionTypeId != null) {
					criteria.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), 
							arTransactionTypeId));
				} else {
					criteria.add(Restrictions.ne(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_ACCOUNT_SALE));
					criteria.add(Restrictions.ne(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_SALE_RETURN));
					criteria.add(Restrictions.ne(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_SALES_ORDER));
				}
				if(divisionId != null) {
					criteria.add(Restrictions.eq(ArTransaction.FIELD.divisionId.name(), 
							divisionId));
				}
				if (!searchCriteria.isEmpty()) {
					Criterion tNumberCrit = Restrictions.like(ArTransaction.FIELD.transactionNumber.name(), 
							"%" + searchCriteria.trim() + "%");
					Criterion descCrit = Restrictions.like(ArTransaction.FIELD.description.name(), 
							"%" + searchCriteria.trim() + "%");
					
					if (StringFormatUtil.isNumeric(searchCriteria)) {
						Criterion tNumOrDescCrit = Restrictions.or(tNumberCrit, descCrit);
						criteria.add(Restrictions.or(tNumOrDescCrit,
								Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchCriteria.trim(), Hibernate.STRING)));
					} else {
						criteria.add(Restrictions.or(tNumberCrit, descCrit));
					}
				} 

				Page<ArTransaction> arTransactions = getAll(criteria, pageSetting);
				for (ArTransaction transaction : arTransactions.getData()) {
					getHibernateTemplate().initialize(transaction.getArCustomer());
					getHibernateTemplate().initialize(transaction.getArCustomerAccount());
					getHibernateTemplate().initialize(transaction.getArTransactionType());
					getHibernateTemplate().initialize(transaction.getTerm());
				}
				return arTransactions;
			}
		};
		return getHibernateTemplate().execute(arTransactionCallback);
	}

	@Override
	public Page<ArTransaction> getAllArTransaction(final int arTransactionTypeId,
			final ApprovalSearchParam searchParam, final List<Integer> formStatusIds, 
			final PageSetting pageSetting){
		return getAllArTransaction(arTransactionTypeId, null, searchParam, formStatusIds, pageSetting);
	}

	@Override
	public Page<ArTransaction> getAllArTransaction(final int arTransactionTypeId, Integer divisionId,
			final ApprovalSearchParam searchParam, final List<Integer> formStatusIds, 
			final PageSetting pageSetting) {
		HibernateCallback<Page<ArTransaction>> hibernateCallback = 
				new HibernateCallback<Page<ArTransaction>>() {

			@Override
			public Page<ArTransaction> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria arTCriteria = session.createCriteria(ArTransaction.class);

				if (arTransactionTypeId > ArTransactionType.TYPE_CREDIT_MEMO) {
					arTCriteria.add(Restrictions.eq(
							ArTransaction.FIELD.transactionTypeId.name(), arTransactionTypeId));
				} else {
					List<Integer> transactionTypeIds = new ArrayList<Integer>();
					transactionTypeIds = Arrays.asList(ArTransactionType.TYPE_REGULAR_TRANSACTION,
							ArTransactionType.TYPE_DEBIT_MEMO, ArTransactionType.TYPE_CREDIT_MEMO);
					addAsOrInCritiria(arTCriteria, ArTransaction.FIELD.transactionTypeId.name(), transactionTypeIds);
				}
				if(divisionId != null) {
					arTCriteria.add(Restrictions.eq(ArTransaction.FIELD.divisionId.name(), divisionId));
				}

				//AR Customer Account Criteria
				DetachedCriteria arCusAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);

				//Search for company number, date and amount
				SearchCommonUtil.searchCommonParams(arTCriteria, arCusAcctCriteria, ArTransaction.FIELD.customerAcctId.name(),
						ArTransaction.FIELD.transactionDate.name(), ArTransaction.FIELD.dueDate.name(),
						ArTransaction.FIELD.glDate.name(), searchParam.getUser().getCompanyIds(), searchParam);

				//Search for transaction number and/or description
				String criteria = searchParam.getSearchCriteria();
				if (!criteria.isEmpty()) {
					Criterion tNumberCrit = Restrictions.like(ArTransaction.FIELD.transactionNumber.name(), 
							"%" + criteria.trim() + "%");
					Criterion descCrit = Restrictions.like(ArTransaction.FIELD.description.name(), 
							"%" + criteria.trim() + "%");
					if (arTransactionTypeId != ArTransactionType.TYPE_ACCOUNT_SALE 
							&& arTransactionTypeId != ArTransactionType.TYPE_SALE_RETURN) {
						
						if (StringFormatUtil.isNumeric(criteria)) {
							//Search for transaction number and description and sequence number
							Criterion tNumOrDescCrit = Restrictions.or(tNumberCrit, descCrit);
							arTCriteria.add(Restrictions.or(tNumOrDescCrit,
									Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", criteria.trim(), Hibernate.STRING)));
						} else {
							// Search for transaction number and description
							arTCriteria.add(Restrictions.or(tNumberCrit, descCrit));
						}
					} else {
						if (StringFormatUtil.isNumeric(criteria)) {
							//Search for account sales invoice number and sequence number.
							arTCriteria.add(Restrictions.or(descCrit,
									Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", criteria.trim(), Hibernate.STRING)));
						} else {
							arTCriteria.add(descCrit);
						}
					}
				}
				// Workflow status
				DetachedCriteria dcWorkFlow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkFlow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				dcWorkFlow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				arTCriteria.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkFlow));
				arTCriteria.addOrder(Order.desc(ArTransaction.FIELD.createdDate.name()));
				arTCriteria.addOrder(Order.desc(ArTransaction.FIELD.transactionDate.name()));
				arTCriteria.addOrder(Order.asc(ArTransaction.FIELD.dueDate.name()));
				Page<ArTransaction> ret = getAll(arTCriteria, pageSetting);
				for (ArTransaction transaction : ret.getData()) {
					getHibernateTemplate().initialize(transaction.getArCustomer());
					getHibernateTemplate().initialize(transaction.getArCustomerAccount());
					getHibernateTemplate().initialize(transaction.getArTransactionType());
					getHibernateTemplate().initialize(transaction.getTerm());
				}
				return ret;
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public boolean isUniqueTransactionNo(ArTransaction arTransaction, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();

		// AR Customer Account criteria.
		DetachedCriteria arCustAcctDc = DetachedCriteria.forClass(ArCustomerAccount.class);
		arCustAcctDc.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
		arCustAcctDc.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.customerAcctId.name(), arCustAcctDc));
		if(arTransaction.getCompanyId() != null) {
			dc.add(Restrictions.eq(ArTransaction.FIELD.companyId.name(), arTransaction.getCompanyId()));
		}
		if(arTransaction.getDivisionId() != null) {
			dc.add(Restrictions.eq(ArTransaction.FIELD.divisionId.name(), arTransaction.getDivisionId()));
		}
		dc.add(Restrictions.eq(ArTransaction.FIELD.transactionNumber.name(), arTransaction.getTransactionNumber().trim()));
		dc.add(Restrictions.ne(ArTransaction.FIELD.id.name(), arTransaction.getId()));

		return getAll(dc).size() == 0;
	}

	@Override
	public List<ArTransaction> getARTransactions(String criteria,
			Integer arCustAcctId, List<String> transactionsNums, Integer id, Boolean noLimit, Boolean isExact, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		
		if (criteria != null && !criteria.trim().isEmpty()) {
			dc.add(Restrictions.like(
					ArTransaction.FIELD.transactionNumber.name(), "%" + criteria.trim() + "%"));
		}

		if (arCustAcctId != null) {
			// AR Customer Account criteria
			DetachedCriteria arCustomerAccountDc = DetachedCriteria.forClass(ArCustomerAccount.class);
			arCustomerAccountDc.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
			arCustomerAccountDc.add(Restrictions.eq(ArCustomerAccount.FIELD.id.name(), arCustAcctId));
			
			dc.add(Subqueries.propertyIn(ArTransaction.FIELD.customerAcctId.name(), arCustomerAccountDc));
		}

		for (String tNum : transactionsNums) {
			dc.add(Restrictions.ne(ArTransaction.FIELD.transactionNumber.name(), tNum.trim()));
		}

		// Workflow criteria.
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));

		dc.addOrder(Order.asc(ArTransaction.FIELD.sequenceNumber.name()));
		if (noLimit == null)
			dc.getExecutableCriteria(getSession()).setMaxResults(10);
		return getAll(dc);
	}

	@Override
	public ArTransaction getArTransactionByWorkflow(Integer formWorkflowId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.formWorkflowId.name(), formWorkflowId));
		return get(dc);
	}
	
	@Override
	public boolean isExistingInArReceipt(Integer arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		
		// AR Receipt Transaction
		DetachedCriteria arRTransactionDc = DetachedCriteria.forClass(ArReceiptTransaction.class);
		arRTransactionDc.setProjection(Projections.property(ArReceiptTransaction.FIELD.arTransactionId.name()));
		arRTransactionDc.add(Restrictions.eq(ArReceiptTransaction.FIELD.arTransactionId.name(), arTransactionId));
		
		// AR Receipt Workflow
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		// AR Receipt
		DetachedCriteria arReceiptDc = DetachedCriteria.forClass(ArReceipt.class);
		arReceiptDc.setProjection(Projections.property(ArReceipt.FIELD.id.name()));
		arReceiptDc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), dcWorkflow));
		
		arRTransactionDc.add(Subqueries.propertyIn(ArReceiptTransaction.FIELD.arReceiptId.name(), arReceiptDc));
		
		DetachedCriteria arRTransaction2Dc =  DetachedCriteria.forClass(ArReceiptTransaction.class);
		arRTransaction2Dc.setProjection(Projections.property(ArReceiptTransaction.FIELD.arTransactionId.name()));
		arRTransaction2Dc.add(Restrictions.eq(ArReceiptTransaction.FIELD.arTransactionId.name(), arTransactionId));
		
		dc.add(Restrictions.and(Subqueries.propertyIn(ArTransaction.FIELD.id.name(), arRTransactionDc), 
				Subqueries.propertyIn(ArTransaction.FIELD.id.name(), arRTransaction2Dc)) );		
	
		return getAll(dc).size() > 0;
	}

	@Override
	public ArTransaction getTransactionByNumber(String transactionNumber) {
		return get(getArTransaction(transactionNumber, null));
	}

	@Override
	public ArTransaction getArTransaction(Integer arCustomerAcctId, String criteria) {
		DetachedCriteria dc = getDetachedCriteria();
		if (arCustomerAcctId != null)
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), arCustomerAcctId));
		if (criteria != null && !criteria.trim().isEmpty()) 
			dc.add(Restrictions.eq(ArTransaction.FIELD.transactionNumber.name(), criteria.trim()));				
		return get(dc);
	}

	@Override
	public ArTransaction getCompletedTransaction(Integer arCustomerAcctId, String criteria) {
		DetachedCriteria dc = getDetachedCriteria();
		if (arCustomerAcctId != null)
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), arCustomerAcctId));
		if (criteria != null && !criteria.trim().isEmpty()) 
			dc.add(Restrictions.eq(ArTransaction.FIELD.transactionNumber.name(), criteria.trim()));

		// Workflow criteria.
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		return get(dc);
	}

	private DetachedCriteria getArTransaction(String transactionNo, Integer arCustomerAcctId) {
		DetachedCriteria arTransactionCrit = getDetachedCriteria();
		if(arCustomerAcctId != null)
			arTransactionCrit.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), arCustomerAcctId));
		arTransactionCrit.add(Restrictions.eq(ArTransaction.FIELD.transactionNumber.name(), transactionNo.trim()));
		return arTransactionCrit;
	}

	@Override
	public Page<ArTransactionRegisterDto> searchTransaction(ArTransactionRegisterParam param, PageSetting pageSetting) {
		int paymentStatus = param.getPaymentStatusId();
		String paymentStatusCondition = "";
		switch (paymentStatus) {
		case PaymentStatus.FULLY_PAID:
			paymentStatusCondition = "AND (AMOUNT - PAID_AMOUNT) = 0 ";
			break;
		case PaymentStatus.PARTIALL_PAID:
			paymentStatusCondition = "AND (AMOUNT - PAID_AMOUNT) != 0 AND (AMOUNT - PAID_AMOUNT) != AMOUNT ";
			break;
		case PaymentStatus.UNPAID:
			paymentStatusCondition = "AND (AMOUNT - PAID_AMOUNT) = AMOUNT ";
			break;
		case PaymentStatus.ALL:
			break;
		default:
			throw new RuntimeException("Unknown payment");
		}
		String sql = "SELECT *, (CASE WHEN CURRENT_STATUS_ID = 4 THEN 0 ELSE AMOUNT END) AS AMNT "
				+ "FROM V_TRANSACTION_HISTORY "
				+ "WHERE COMPANY_ID = ? "
				+ (param.getDivisionId() != -1 ? "AND DIVISION_ID = ? " : "")
				+ (param.getTransactionClassificationId() != -1 ? "AND TRANSACTION_CLASSIFICATION_ID = ? " : "")
				+ (param.getCustomerId() != -1 ? "AND AR_CUSTOMER_ID = ? " : " ")
				+ (param.getCustomerAcctId() != -1 ? "AND AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (!param.getTransactionNumber().trim().isEmpty() ? "AND TRANS_NUMBER LIKE ? " : "")
				+ (param.getTransactionDateFrom() != null && param.getTransactionDateTo() != null
						? "AND TRANSACTION_DATE BETWEEN ? AND ? " : "")
				+ (param.getGlDateFrom() != null && param.getGlDateTo() != null ? "AND GL_DATE BETWEEN ? AND ? " : "")
				+ (param.getDueDateFrom() != null && param.getDueDateTo() != null ? "AND DUE_DATE BETWEEN ? AND ? " : "")
				+ (param.getAmountFrom() != null && param.getAmountTo() != null ? "AND AMOUNT BETWEEN ? AND ? " : "")
				+ (param.getSequenceNoFrom() != null && param.getSequenceNoTo() != null
						? "AND SEQUENCE_NO BETWEEN ? AND ? " : "")
				+ (param.getTransactionStatusId() != -1 ? "AND CURRENT_STATUS_ID = ? " : "")
				+ "AND GL_DATE <= ? " + paymentStatusCondition
				+ "ORDER BY GL_DATE, TRANS_NUMBER, CUSTOMER_NAME, CUSTOMER_ACCOUNT";

		TransactionRegisterHandler handler = new TransactionRegisterHandler(param);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class TransactionRegisterHandler implements QueryResultHandler<ArTransactionRegisterDto> {
		private ArTransactionRegisterParam param;

		private TransactionRegisterHandler(ArTransactionRegisterParam param) {
			this.param = param;
		}

		@Override
		public List<ArTransactionRegisterDto> convert(List<Object[]> queryResult) {
			List<ArTransactionRegisterDto> transactionRegister = new ArrayList<ArTransactionRegisterDto>();
			ArTransactionRegisterDto transRegisterDto = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				transRegisterDto = new ArTransactionRegisterDto();
				transRegisterDto.setDivision((String) rowResult[colNum]);
				transRegisterDto.setTransactionClassification((String) rowResult[++colNum]);
				transRegisterDto.setTransactionDate((Date) rowResult[++colNum]);
				transRegisterDto.setTransactionNumber((String) rowResult[++colNum]);
				transRegisterDto.setCustomerName((String) rowResult[++colNum]);
				transRegisterDto.setCustomerAcctName((String) rowResult[++colNum]);
				transRegisterDto.setTermName((String) rowResult[++colNum]);
				transRegisterDto.setGlDate((Date) rowResult[++colNum]);
				transRegisterDto.setDueDate((Date) rowResult[++colNum]);
				Double amount = NumberFormatUtil.convertBigDecimalToDouble(rowResult[++colNum]);
				transRegisterDto.setAmount(amount);
				Double totalPayment = NumberFormatUtil.convertBigDecimalToDouble(rowResult[++colNum]);
				Double balance = amount - totalPayment;
				transRegisterDto.setBalance(balance);
				transRegisterDto.setStatus((String) rowResult[++colNum]);
				transRegisterDto.setSequenceNumber((Integer) rowResult[++colNum]);
				transRegisterDto.setCancellationRemarks((String) rowResult[++colNum]);
				transRegisterDto.setFormattedSequenceNo((String) rowResult[++colNum]);
				transactionRegister.add(transRegisterDto);
			}
			return transactionRegister;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, param.getCompanyId());

			if (param.getDivisionId() != -1) {
				query.setParameter(++index, param.getDivisionId());
			}

			if (param.getTransactionClassificationId() != -1) {
				query.setParameter(++index, param.getTransactionClassificationId());
			}
			if (param.getCustomerId() != -1) {
				query.setParameter(++index, param.getCustomerId());
			}
			if (param.getCustomerAcctId() != -1) {
				query.setParameter(++index, param.getCustomerAcctId());
			}
			if (!param.getTransactionNumber().isEmpty()) {
				query.setParameter(++index, param.getTransactionNumber());
			}
			if (param.getTransactionDateFrom() != null && param.getTransactionDateTo() != null) {
				query.setParameter(++index, param.getTransactionDateFrom());
				query.setParameter(++index, param.getTransactionDateTo());
			}
			if (param.getGlDateFrom() != null && param.getGlDateTo() != null) {
				query.setParameter(++index, param.getGlDateFrom());
				query.setParameter(++index, param.getGlDateTo());
			}
			if (param.getDueDateFrom() != null && param.getDueDateTo() != null) {
				query.setParameter(++index, param.getDueDateFrom());
				query.setParameter(++index, param.getDueDateTo());
			}
			if (param.getAmountFrom() != null && param.getAmountTo() != null) {
				query.setParameter(++index, param.getAmountFrom());
				query.setParameter(++index, param.getAmountTo());
			}
			if (param.getSequenceNoFrom() != null && param.getSequenceNoTo() != null) {
				query.setParameter(++index, param.getSequenceNoFrom());
				query.setParameter(++index, param.getSequenceNoTo());
			}
			if (param.getTransactionStatusId() != -1) {
				query.setParameter(++index, param.getTransactionStatusId());
			}
			query.setParameter(++index, param.getAsOfDate());
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("TYPE", Hibernate.STRING);
			query.addScalar("TRANSACTION_DATE", Hibernate.DATE);
			query.addScalar("TRANS_NUMBER", Hibernate.STRING);
			query.addScalar("CUSTOMER_NAME", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCOUNT", Hibernate.STRING);
			query.addScalar("TERM", Hibernate.STRING);
			query.addScalar("GL_DATE", Hibernate.DATE);
			query.addScalar("DUE_DATE", Hibernate.DATE);
			query.addScalar("AMNT", Hibernate.DOUBLE);
			query.addScalar("PAID_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("STATUS", Hibernate.STRING);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
			query.addScalar("FORMATTED_SEQUENCE_NO", Hibernate.STRING);
		}
	}

	@Override
	public List<ArTransaction> getTransactionsByCustAcct (Integer arCustomerAcctId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), arCustomerAcctId));
		
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		
		return getAll(dc);
	}

	@Override
	public ArTransaction getArTransactionByNumber (String transactionNumber) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.transactionNumber.name(), transactionNumber.trim()));
		//TODO: Need to change this similar to getArTransactions
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.VALIDATED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		return get(dc);
	}

	@Override
	public Page<ArTransaction> getArTransactions(Integer companyId, Integer arLineSetupId, Integer unitOfMeasureId, Date transactionDateFrom,
			Date transactionDateTo, Date glDateFrom, Date glDateTo,	Integer customerId, Integer customerAcctId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria customerAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
		//Company
		if(companyId != -1 || companyId != null){
			customerAcctCriteria.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
			customerAcctCriteria.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
			dc.add(Subqueries.propertyIn(ArTransaction.FIELD.customerAcctId.name(), customerAcctCriteria));
		}
		//AR Line
		DetachedCriteria arLineCriteria = DetachedCriteria.forClass(ArLine.class);
		arLineCriteria.add(Restrictions.eq(ArLine.FIELD.arLineSetupId.name(), arLineSetupId));
		if (unitOfMeasureId != 0) // If unit of measure not equal to None
			arLineCriteria.add(Restrictions.eq(ArLine.FIELD.unitOfMeasurementId.name(), unitOfMeasureId));
		else
			arLineCriteria.add(Restrictions.isNull(ArLine.FIELD.unitOfMeasurementId.name()));

		arLineCriteria.setProjection(Projections.property(ArLine.FIELD.aRTransactionId.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.id.name(), arLineCriteria));

		//Transaction Date
		processDate(dc, ArTransaction.FIELD.transactionDate.name(), transactionDateFrom, transactionDateTo);
		//GL Date
		processDate(dc, ArTransaction.FIELD.glDate.name(), glDateFrom, glDateTo);
		//Customer
		if(customerId != -1)
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerId.name(), customerId));
		//Customer Account
		if(customerAcctId != -1)
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), customerAcctId));

		//Workflow status criteria
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		//Workflow log criteria
		DetachedCriteria wfLogCriteria = DetachedCriteria.forClass(FormWorkflowLog.class);
		wfLogCriteria.setProjection(Projections.property(FormWorkflowLog.FIELD.formWorkflowId.name()));
		wfLogCriteria.add(Restrictions.eq(FormWorkflowLog.FIELD.formStatusId.name(), FormStatus.VALIDATED_ID));
		dcWorkflow.add(Subqueries.propertyIn(FormWorkflow.FIELD.id.name(), wfLogCriteria));

		dc.addOrder(Order.asc(ArTransaction.FIELD.transactionDate.name()));
		dc.addOrder(Order.asc(ArTransaction.FIELD.transactionNumber.name()));
		return getAll(dc, pageSetting);
	}

	private void processDate (DetachedCriteria dc, String fieldName, Date fromDate,Date toDate) {
		if(fromDate != null && toDate != null)
			dc.add(Restrictions.between(fieldName, fromDate, toDate));
		else if(fromDate != null)
			dc.add(Restrictions.eq(fieldName, fromDate));
		else if(toDate != null)
			dc.add(Restrictions.eq(fieldName, toDate));
	}

	@Override
	public Double getCustomerAcctTotalTransaction(Integer customerAcctId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), customerAcctId));
		return getTotalTransactionAmt(dc);
	}

	@Override
	public double getTotalTransactionAmtOfCustomer(int arCustomerId, int arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.customerId.name(), arCustomerId));
		if(arTransactionId != 0) {
			dc.add(Restrictions.ne(ArTransaction.FIELD.id.name(), arTransactionId));
		}
		return getTotalTransactionAmt(dc);
	}

	double getTotalTransactionAmt(DetachedCriteria dc) {
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.setProjection(Projections.sum(ArTransaction.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public List<ArTransaction> getTranactions(Integer companyId, Integer customerId,
			Integer customerAcctId, Date transactionDateFrom, Date transactionDateTo) {
		DetachedCriteria dc = getDetachedCriteria();
		//Company
		if(companyId != -1 || companyId != null){
			DetachedCriteria customerAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
			customerAcctCriteria.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
			customerAcctCriteria.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
			dc.add(Subqueries.propertyIn(ArTransaction.FIELD.customerAcctId.name(), customerAcctCriteria));
		}
		//Customer
		if(customerId != -1 && customerId != null)
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerId.name(), customerId));
		//Customer Account
		if(customerAcctId != -1 && customerAcctId != null)
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), customerAcctId));
		//Transaction Date
		processDate(dc, ArTransaction.FIELD.transactionDate.name(), transactionDateFrom, transactionDateTo);
		//Workflow
		processWorkflow(dc, true);
		return getAll(dc);
	}

	private void processWorkflow(DetachedCriteria dc, boolean isComplete) {
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if(isComplete) {
			dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		}
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
	}

	@Override
	public Double getCustomerAcctTotalTransaction(Integer companyId, Integer customerAcctId,
			Date asOfDate) {
		DetachedCriteria arTransactionDc = getCustAcctTransactions(companyId, customerAcctId, asOfDate, false);
		return getBySumProjection(arTransactionDc);
	}

	@Override
	public Double getCustAcctTotalSales(Integer companyId, Integer customerAcctId, Date asOfDate) {
		DetachedCriteria acctSalesDc = getCustAcctTransactions(companyId, customerAcctId, asOfDate, true);
		return getBySumProjection(acctSalesDc);
	}

	private DetachedCriteria getCustAcctTransactions(Integer companyId, Integer customerAcctId,
			Date asOfDate, boolean isAccountSales) {
		DetachedCriteria arTransactionDC = getDetachedCriteria();
		if(isAccountSales) {
			//Only Account Sales and Account Sales Return
			arTransactionDC.add(Restrictions.or(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_ACCOUNT_SALE),
					Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_SALE_RETURN)));
			arTransactionDC.add(Restrictions.le(ArTransaction.FIELD.transactionDate.name(), asOfDate));
		} else {
			//AR Transaction only.
			arTransactionDC.add(Restrictions.ne(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_ACCOUNT_SALE));
			arTransactionDC.add(Restrictions.ne(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_SALE_RETURN));
			arTransactionDC.add(Restrictions.le(ArTransaction.FIELD.glDate.name(), asOfDate));
		}

		if(customerAcctId != null) {
			arTransactionDC.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), customerAcctId));
		}

		if(companyId != null) {
			arTransactionDC.createAlias("arCustomerAccount", "ca");
			arTransactionDC.add(Restrictions.eq("ca.companyId", companyId));
		}

		//Get only COMPLETED workflows.
		arTransactionDC.createAlias("formWorkflow", "fw");
		arTransactionDC.add(Restrictions.eq("fw.complete", true));
		arTransactionDC.setProjection(Projections.sum(ArTransaction.FIELD.amount.name()));
		return arTransactionDC;
	}

	@Override
	public Integer genSequenceNo(Integer companyId, Integer arTransactionTypeId) {
		return genSequenceNo(companyId, arTransactionTypeId, null);
	}
	@Override
	public Integer genSequenceNo(Integer companyId, Integer arTransactionTypeId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null) {
			//Set to null if AR Transaction, otherwise Account Sales or Account Sales Return
			dc.add(Restrictions.eq(ArTransaction.FIELD.companyId.name(), companyId));
		}
		if(divisionId != null) {
			dc.add(Restrictions.eq(ArTransaction.FIELD.divisionId.name(), divisionId));
		}
		dc.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), 
				arTransactionTypeId));
		dc.setProjection(Projections.max(ArTransaction.FIELD.sequenceNumber.name()));
		List<Object> result = getByProjection(dc);
		if (result == null)
			return 1;
		Object obj = result.iterator().next();
		if (obj == null)
			return 1;
		return ((Integer) obj) + 1;
	}

	@Override
	public ArTransaction getArTByCustAcctAndTNum(Integer arCustomerAcctId,
			String transactionNumber) {
		DetachedCriteria dc = getDetachedCriteria();
		if (arCustomerAcctId != null)
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), arCustomerAcctId));
		dc.add(Restrictions.eq(ArTransaction.FIELD.transactionNumber.name(), transactionNumber.trim()));
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		return get(dc);
	}

	@Override
	public Page<ArTransaction> getAccountSales(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer sequenceNo, Date dateFrom, Date dateTo, Integer status, Integer transactionTypeId,
			PageSetting pageSetting, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		if(transactionTypeId.equals(ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS)) {
			dc.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(),
					ArTransactionType.TYPE_ACCOUNT_SALES_IS));
		} else {
			List<Integer> arTransactionTypes = new ArrayList<>();
			arTransactionTypes.add(ArTransactionType.TYPE_SALE_RETURN);
			arTransactionTypes.add(ArTransactionType.TYPE_ACCOUNT_SALE);
			dc.add(Restrictions.in(ArTransaction.FIELD.transactionTypeId.name(), arTransactionTypes));
		}

		if (companyId != null)
			dc.add(Restrictions.eq(ArTransaction.FIELD.companyId.name(), companyId));
		if (arCustomerId != null)
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerId.name(), arCustomerId));
		if (arCustomerAccountId != null)
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), arCustomerAccountId));
		if (sequenceNo != null)
			dc.add(Restrictions.eq(ArTransaction.FIELD.sequenceNumber.name(), sequenceNo));
		if (dateFrom != null && dateTo != null)
			dc.add(Restrictions.between(ArTransaction.FIELD.transactionDate.name(), dateFrom, dateTo));
		else if (dateFrom != null)
			dc.add(Restrictions.ge(ArTransaction.FIELD.transactionDate.name(), dateFrom));
		else if (dateTo != null)
			dc.add(Restrictions.le(ArTransaction.FIELD.transactionDate.name(), dateTo));

		// If ASR is used as reference, excluded the forms that has returned quantities only
		DetachedCriteria asItemDc = DetachedCriteria.forClass(AccountSaleItem.class);
		asItemDc.setProjection(Projections.property(AccountSaleItem.FIELD.arTransactionId.name()));
		asItemDc.add(Restrictions.gt(AccountSaleItem.FIELD.quantity.name(), 0.0));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.id.name(), asItemDc));

		// Account sale Workflow
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		if (status != ArTransaction.STATUS_ALL) {
			// Account sale return subquery
			DetachedCriteria asrDc = DetachedCriteria.forClass(ArTransaction.class);
			asrDc.setProjection(Projections.property(ArTransaction.FIELD.accountSaleId.name()));
			asrDc.add(Restrictions.or(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_SALE_RETURN),
					Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS)));
			asrDc.add(Restrictions.isNotNull(ArTransaction.FIELD.accountSaleId.name()));

			//Account sale return Workflow - Add account sale that are used by cancelled account sale return.
			DetachedCriteria asrWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
			asrWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			asrWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), 
					FormStatus.CANCELLED_ID));
			asrDc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), asrWorkflow));

			if (status == ArTransaction.STATUS_USED)
				dc.add(Subqueries.propertyIn(ArTransaction.FIELD.id.name(), asrDc));
			else if (status == ArTransaction.STATUS_UNUSED)
				dc.add(Subqueries.propertyNotIn(ArTransaction.FIELD.id.name(), asrDc));
		}
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));

		dc.addOrder(Order.desc(ArTransaction.FIELD.transactionDate.name()));
		dc.addOrder(Order.desc(ArTransaction.FIELD.sequenceNumber.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public double getTotalTransactionAmount(Integer arCustomerAccountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), arCustomerAccountId));
		dc.setProjection(Projections.sum(ArTransaction.FIELD.amount.name()));

		// Ar Transaction Workflow
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		return getBySumProjection(dc);
	}

	@Override
	public Page<ArTransaction> getTransactions(int itemId, int warehouseId,
			Date date, PageSetting pageSetting) {
		DetachedCriteria transactionDc = getDetachedCriteria();
		transactionDc.createAlias("formWorkflow", "fw");
		transactionDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		transactionDc.add(Restrictions.eq(ArTransaction.FIELD.transactionDate.name(), date));
		restrictByWarehouseAndItem(transactionDc, itemId, warehouseId);
		return getAll(transactionDc, pageSetting);
	}

	private void restrictByWarehouseAndItem (DetachedCriteria transactionDc, int itemId, int warehouseId) {
		// only the selected item and warehouse.
		DetachedCriteria asItemDc = DetachedCriteria.forClass(AccountSaleItem.class);
		asItemDc.setProjection(Projections.property(AccountSaleItem.FIELD.arTransactionId.name()));
		asItemDc.add(Restrictions.eq(AccountSaleItem.FIELD.itemId.name(), itemId));
		asItemDc.add(Restrictions.eq(AccountSaleItem.FIELD.warehouseId.name(), warehouseId));
		transactionDc.add(Subqueries.propertyIn(ArTransaction.FIELD.id.name(), asItemDc));
	}

	@Override
	public int getASSize() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_ACCOUNT_SALE));
		dc.setProjection(Projections.count(ArTransaction.FIELD.id.name()));

		// Account sale workflow subquery. 
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		
		List<Object> ret = getByProjection(dc);
		if (ret != null && ret.size() > 0) {
			Object retObj = ret.iterator().next();
			if (retObj == null)
				return 0;
			return (Integer) retObj;
		}
		return 0;
	}

	@Override
	public Page<ArTransaction> getAccountSales(PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_ACCOUNT_SALE));

		// Account sale workflow subquery. 
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		
		return getAll(dc, pageSetting);
	}

	@Override
	public ArTransaction getTransactionBySeqNoAndType(Integer sequenceNo, Integer transactionTypeId) {
		DetachedCriteria transactionDc = getDetachedCriteria();
		transactionDc.add(Restrictions.eq(ArTransaction.FIELD.sequenceNumber.name(), sequenceNo));
		transactionDc.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), transactionTypeId));
		return get(transactionDc);
	}

	@Override
	public List<ArTransaction> getTransactions(int arTransactionId, int transactionTypeId) {
		DetachedCriteria transactionDc = getDetachedCriteria();
		transactionDc.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), transactionTypeId));
		transactionDc.add(Restrictions.gt(ArTransaction.FIELD.id.name(), arTransactionId));
		return getAll(transactionDc);
	}

	@Override
	public boolean isExistingInAccountSaleReturn(int arTransactionId) {
		DetachedCriteria arRTransactionDcAccountRet = DetachedCriteria.forClass(ArTransaction.class);

		arRTransactionDcAccountRet.setProjection(Projections.property(ArTransaction.FIELD.id.name()));
		arRTransactionDcAccountRet.add(Restrictions.eq(ArTransaction.FIELD.accountSaleId.name(), arTransactionId));
		
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		arRTransactionDcAccountRet.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));

		return getAll(arRTransactionDcAccountRet).size() > 0;
	}

	private String getAgeBasisColumn (int ageBasis) {
		String ageBasisColumn = "";
		switch (ageBasis) {
		case TransactionAgingParam.DUE_DATE_AGE_BASIS:
			ageBasisColumn = "DUE_DATE";
			break;
		case TransactionAgingParam.GL_DATE_AGE_BASIS:
			ageBasisColumn = "GL_DATE";
			break;
		case TransactionAgingParam.TRANSACTION_DATE_AGE_BASIS:
			ageBasisColumn = "TRANSACTION_DATE";
			break;
		default:
			throw new RuntimeException("Unable to find age basis : " + ageBasisColumn);
		}
		return ageBasisColumn;
	}
	@Override
	public Page<ArTransactionAgingDto> genareteTransactionAging(TransactionAgingParam param, PageSetting pageSetting) {
		if(param.getTypeId() == 1) {
			String ageBasisColumn = getAgeBasisColumn(param.getAgeBasis());
			String sqlGroupBy = " ";
			String sqlSelect = "";
			if (param.isShowTrans()){
				sqlSelect = "SELECT *, (AMOUNT-TOTAL_PAYMENT) AS BALANCE, IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 0 AND 30, (AMOUNT - TOTAL_PAYMENT), 0) AS 1_30_DAYS, " 
						+ "IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 31 AND 60, (AMOUNT - TOTAL_PAYMENT), 0) AS 31_60_DAYS, "
						+ "IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 61 AND 90, (AMOUNT - TOTAL_PAYMENT), 0) AS 61_90_DAYS, "
						+ "IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 91 AND 120, (AMOUNT - TOTAL_PAYMENT), 0) AS 91_120_DAYS, "
						+ "IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 121 AND 150, (AMOUNT - TOTAL_PAYMENT), 0) AS 121_150_DAYS, "
						+ "IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") >= 151, (AMOUNT - TOTAL_PAYMENT), 0) AS 151_UP ";
			} else { // Group the result by TRANSACTION_TYPE_ID and CUSTOMER_ACCOUNT_ID.
				// Add all the transactions of the customer account.
				sqlSelect = "SELECT *, SUM(AMOUNT-TOTAL_PAYMENT) AS BALANCE, SUM(IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 0 AND 30, (AMOUNT - TOTAL_PAYMENT), 0)) AS 1_30_DAYS, " 
						+ "SUM(IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 31 AND 60, (AMOUNT - TOTAL_PAYMENT), 0)) AS 31_60_DAYS, "
						+ "SUM(IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 61 AND 90, (AMOUNT - TOTAL_PAYMENT), 0)) AS 61_90_DAYS, "
						+ "SUM(IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 91 AND 120, (AMOUNT - TOTAL_PAYMENT), 0)) AS 91_120_DAYS, "
						+ "SUM(IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 121 AND 150, (AMOUNT - TOTAL_PAYMENT), 0)) AS 121_150_DAYS, "
						+ "SUM(IF(DATEDIFF(CURDATE(), "+ ageBasisColumn+") >= 151, (AMOUNT - TOTAL_PAYMENT), 0)) AS 151_UP ";
				sqlGroupBy = "GROUP BY AR_TRANSACTION_TYPE_ID, AR_CUSTOMER_ACCOUNT_ID ";
			}
			String sql = sqlSelect
					+ "FROM V_TRANSACTION_HISTORY WHERE COMPANY_ID = ? "
					+ (param.getTransTypeID() != - 1 ? "AND AR_TRANSACTION_TYPE_ID = ? " : " ")
					+ (param.getCustomerId() != - 1 ? "AND AR_CUSTOMER_ID = ? " : " ")
					+ (param.getCustomerAcctId() != - 1 ? "AND AR_CUSTOMER_ACCOUNT_ID = ? " : " ")
					+ "AND CURRENT_STATUS_ID != 4 "
					+ "AND (AMOUNT - TOTAL_PAYMENT) != 0 "
					+ "AND (AMOUNT - TOTAL_PAYMENT) NOT BETWEEN -0.009 AND 0.009 "
					+ "AND CURDATE() > " + ageBasisColumn + " "
					+ sqlGroupBy
					+ "ORDER BY AR_TRANSACTION_TYPE_ID, CUSTOMER_NAME, CUSTOMER_ACCOUNT ";
			TransactionAgingHandler handler = new TransactionAgingHandler(param);
			return getAllAsPage(sql, pageSetting, handler);
		} else {
			// Different generation of report for AR Aging Report with as of date parameter.
			ArAgingWithAsOfDateHandler handler = new ArAgingWithAsOfDateHandler(param);
			// Set the grouping of data for the report, if isShowTrans is true, then the value for
			// groupByOption is 1, otherwise value is 2.
			int groupByOption = param.isShowTrans() ? 1 : 2;
			return executePagedSP("GET_TRANSACTION_AGING", pageSetting, handler, param.getCompanyId(), param.getDivisionId(),
					param.getTransactionClassificationId(), param.getCustomerId(), param.getCustomerAcctId(), groupByOption,
					param.getAgeBasis(), param.getAsOfDate(), param.getAccountId());
		}
	}

	private static class TransactionAgingHandler implements QueryResultHandler<ArTransactionAgingDto> {
		private TransactionAgingParam param;

		private TransactionAgingHandler(TransactionAgingParam param) {
			this.param = param;
		}

		@Override
		public List<ArTransactionAgingDto> convert(List<Object[]> queryResult) {
			List<ArTransactionAgingDto> transactionAging = 
					new ArrayList<ArTransactionAgingDto>();
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				String companyName = (String) rowResult[colNum++];
				String type = (String) rowResult[colNum++];
				String customer = (String) rowResult[colNum++];
				String customerAccount = (String) rowResult[colNum++];
				String transNumber = (String) rowResult[colNum++];
				Double amount = (Double) rowResult[colNum++];
				String term = (String) rowResult[colNum++];
				Date dueDate = (Date) rowResult[colNum++];
				Date transDate = (Date) rowResult[colNum++];
				Date glDate = (Date) rowResult[colNum++];
				Integer termId = (Integer) rowResult[colNum++];
				Integer transactionId = (Integer) rowResult[colNum++];
				Integer transactionTypeId = (Integer) rowResult[colNum++];
				Integer customerId = (Integer) rowResult[colNum++];
				Integer customerAcctId = (Integer) rowResult[colNum++];
				Double totalPayment = (Double) rowResult[colNum++];
				String division = (String) rowResult[colNum++];
				String transactionClassification = (String) rowResult[colNum++];
				Double balance = (Double) rowResult[colNum++];
				Double range1To31 = (Double) rowResult[colNum++];
				Double range31To60 = (Double) rowResult[colNum++];
				Double range61To90 = (Double) rowResult[colNum++];
				Double range91To120 = (Double) rowResult[colNum++];
				Double range121To150 = (Double) rowResult[colNum++];
				Double range151Up = (Double) rowResult[colNum];

				ArTransactionAgingDto agingDto = ArTransactionAgingDto.getInstanceOf(companyName, type, customer,
						customerAccount, term, transNumber, amount, dueDate, transDate, glDate, termId, transactionId,
						customerId, customerAcctId, transactionTypeId, totalPayment,division,transactionClassification);
				agingDto.setBalance(balance);
				agingDto.setRange1To30(range1To31);
				agingDto.setRange31To60(range31To60);
				agingDto.setRange61To90(range61To90);
				agingDto.setRange91To120(range91To120);
				agingDto.setRange121To150(range121To150);
				agingDto.setRange151ToUp(range151Up);
				transactionAging.add(agingDto);
			}
			return transactionAging;
		}

		@Override
		public int setParamater(SQLQuery query) {

			int index = 0;
			query.setParameter(index, param.getCompanyId());
			if (param.getTransTypeID() != - 1) {
				query.setParameter(++index, param.getTransTypeID());
			}
			if (param.getCustomerId() != - 1) {
				query.setParameter(++index, param.getCustomerId());
			}
			if (param.getCustomerAcctId() != - 1) {
				query.setParameter(++index, param.getCustomerAcctId());	
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("COMPANY_NAME", Hibernate.STRING);
			query.addScalar("TYPE", Hibernate.STRING);
			query.addScalar("CUSTOMER_NAME", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCOUNT", Hibernate.STRING);
			query.addScalar("TRANSACTION_NUMBER", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("TERM", Hibernate.STRING);
			query.addScalar("DUE_DATE", Hibernate.DATE);
			query.addScalar("TRANSACTION_DATE", Hibernate.DATE);
			query.addScalar("GL_DATE", Hibernate.DATE);
			query.addScalar("TERM_ID", Hibernate.INTEGER);
			query.addScalar("AR_TRANSACTION_ID", Hibernate.INTEGER);
			query.addScalar("AR_TRANSACTION_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("TOTAL_PAYMENT", Hibernate.DOUBLE);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			query.addScalar("1_30_DAYS", Hibernate.DOUBLE);
			query.addScalar("31_60_DAYS", Hibernate.DOUBLE);
			query.addScalar("61_90_DAYS", Hibernate.DOUBLE);
			query.addScalar("91_120_DAYS", Hibernate.DOUBLE);
			query.addScalar("121_150_DAYS", Hibernate.DOUBLE);
			query.addScalar("151_UP", Hibernate.DOUBLE);
		}
	}

	private static class ArAgingWithAsOfDateHandler extends TransactionAgingHandler {
		private ArAgingWithAsOfDateHandler(TransactionAgingParam param) {
			super(param);
		}

		@Override
		public List<ArTransactionAgingDto> convert(List<Object[]> queryResult) {
			ArTransactionAgingDto agingDto = null;
			List<ArTransactionAgingDto> transactionAging =
					new ArrayList<ArTransactionAgingDto>();
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				agingDto = new ArTransactionAgingDto();
				agingDto.setDivision((String) rowResult[colNum++]);
				agingDto.setType((String) rowResult[colNum++]);
				agingDto.setTransactionClassification((String) rowResult[colNum++]);
				agingDto.setTransactionId((Integer) rowResult[colNum++]);
				agingDto.setCustomer((String) rowResult[colNum++]);
				agingDto.setCustomerAccount((String) rowResult[colNum++]);
				agingDto.setTransNumber((String) rowResult[colNum++]);
				agingDto.setTransDate((Date) rowResult[colNum++]);
				agingDto.setTerm((String) rowResult[colNum++]);
				agingDto.setAmount(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setTotalPayment(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setBalance(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange0(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange1To30(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange31To60(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange61To90(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange91To120(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange121To150(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange151ToUp(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				transactionAging.add(agingDto);
			}
			return transactionAging;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("TYPE", Hibernate.STRING);
			query.addScalar("TRANSACTION_CLASSIFICATION", Hibernate.STRING);
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("CUSTOMER_NAME", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCOUNT", Hibernate.STRING);
			query.addScalar("TRANSACTION_NUMBER", Hibernate.STRING);
			query.addScalar("TRANSACTION_DATE", Hibernate.DATE);
			query.addScalar("TERM", Hibernate.STRING);
			query.addScalar("TOTAL_TRANSACTION", Hibernate.DOUBLE);
			query.addScalar("TOTAL_PAYMENT", Hibernate.DOUBLE);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			query.addScalar("0_DAY", Hibernate.DOUBLE);
			query.addScalar("1_30_DAYS", Hibernate.DOUBLE);
			query.addScalar("31_60_DAYS", Hibernate.DOUBLE);
			query.addScalar("61_90_DAYS", Hibernate.DOUBLE);
			query.addScalar("91_120_DAYS", Hibernate.DOUBLE);
			query.addScalar("121_150_DAYS", Hibernate.DOUBLE);
			query.addScalar("151_UP", Hibernate.DOUBLE);
		}
	}

	@Override
	public Page<ArTransactionRegisterDto> getArTransactions(Integer arCustAcctId, Integer accountCollectionId,
			String tNumbers, String criteria, Boolean isShow, Boolean isExact, PageSetting pageSetting) {
		if(criteria != null && !criteria.trim().isEmpty()) {
			criteria = criteria.replace("'", "''");
		}
		String sql = "SELECT AR_TRANSACTION_ID, AR_CUSTOMER_ID, TRANS_NUMBER, CUSTOMER_NAME,"
				+ "AMOUNT, TOTAL_PAYMENT, SEQUENCE_NO, ROUND((AMOUNT - TOTAL_PAYMENT), 2) AS BALANCE FROM (SELECT "
				+ "COM.COMPANY_ID, COM.NAME AS COMPANY_NAME, ( "
				+ "CASE WHEN T.AR_TRANSACTION_TYPE_ID = 1 THEN 'REG' "
				+ "WHEN T.AR_TRANSACTION_TYPE_ID = 2 THEN 'DM' "
				+ "WHEN T.AR_TRANSACTION_TYPE_ID = 3 THEN 'CM' "
				+ "WHEN T.AR_TRANSACTION_TYPE_ID = 4 THEN 'AS' "
				+ "WHEN T.AR_TRANSACTION_TYPE_ID = 5 THEN 'ASR' "
				+ "WHEN T.AR_TRANSACTION_TYPE_ID = 16 THEN 'ARI' END) AS TYPE, "
				+ "C.NAME AS CUSTOMER_NAME, CA.NAME AS CUSTOMER_ACCOUNT, "
				+ "(CASE WHEN T.AR_TRANSACTION_TYPE_ID = 4 THEN "
				+ "(CASE WHEN T.DESCRIPTION IS NULL OR T.DESCRIPTION='' THEN T.TRANSACTION_NUMBER "
				+ "ELSE CONCAT(T.TRANSACTION_NUMBER, ', ', T.DESCRIPTION) END) "
				+ "WHEN T.AR_TRANSACTION_TYPE_ID = 5 THEN "
				+ "(CASE WHEN T.DESCRIPTION IS NULL OR T.DESCRIPTION='' THEN T.TRANSACTION_NUMBER "
				+ "ELSE CONCAT(T.TRANSACTION_NUMBER, ', ', T.DESCRIPTION) END) "
				+ "WHEN T.AR_TRANSACTION_TYPE_ID = 7 THEN "
				+ "(CASE WHEN T.DESCRIPTION IS NULL OR T.DESCRIPTION='' THEN T.TRANSACTION_NUMBER "
				+ "ELSE CONCAT(T.TRANSACTION_NUMBER, ',', T.DESCRIPTION) END) "
				+ "ELSE T.TRANSACTION_NUMBER END) AS TRANSACTION_NUMBER, "
				+ "T.TRANSACTION_NUMBER AS TRANS_NUMBER, T.SEQUENCE_NO, "
				+ "(CASE WHEN T.AR_TRANSACTION_TYPE_ID = 7 THEN -1*T.AMOUNT ELSE T.AMOUNT END) AMOUNT, "
				+ "TRM.NAME AS TERM, (CASE WHEN T.DUE_DATE IS NULL THEN T.TRANSACTION_DATE ELSE T.DUE_DATE END) "
				+ "AS DUE_DATE, T.TRANSACTION_DATE, (CASE WHEN T.GL_DATE IS NULL THEN T.TRANSACTION_DATE ELSE T.GL_DATE END) "
				+ "AS GL_DATE, TRM.TERM_ID AS TERM_ID, T.AR_TRANSACTION_ID AS AR_TRANSACTION_ID, "
				+ "T.AR_TRANSACTION_TYPE_ID AS AR_TRANSACTION_TYPE_ID, CA.AR_CUSTOMER_ACCOUNT_ID "
				+ "AS AR_CUSTOMER_ACCOUNT_ID, C.AR_CUSTOMER_ID AS AR_CUSTOMER_ID, "
				+ "COALESCE((SELECT SUM(ARRT.AMOUNT) FROM AR_RECEIPT_TRANSACTION ARRT "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARRT.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID=ARR.FORM_WORKFLOW_ID "
				+ "WHERE ARRT.AR_TRANSACTION_ID = T.AR_TRANSACTION_ID "
				+ "AND FW.CURRENT_STATUS_ID != 4), 0) AS TOTAL_PAYMENT, FW.CURRENT_STATUS_ID, "
				+ "FS.DESCRIPTION AS STATUS, FW.IS_COMPLETE AS IS_POSTED "
				+ "FROM AR_TRANSACTION T "
				+ "INNER JOIN AR_CUSTOMER C ON C.AR_CUSTOMER_ID=T.CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT CA ON CA.AR_CUSTOMER_ACCOUNT_ID=T.CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN TERM TRM ON TRM.TERM_ID=T.TERM_ID "
				+ "INNER JOIN COMPANY COM ON COM.COMPANY_ID=T.COMPANY_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID=T.FORM_WORKFLOW_ID "
				+ "INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID=FW.CURRENT_STATUS_ID "
				+ "WHERE AR_CUSTOMER_ACCOUNT_ID = ? "
				+ "AND CURRENT_STATUS_ID != 4 "
				+ "AND FW.IS_COMPLETE = 1) AS tmpTbl "
				+ "WHERE ROUND((AMOUNT - TOTAL_PAYMENT), 2) != 0 "
				+ (criteria != null && !criteria.trim().isEmpty() ? "AND TRANS_NUMBER LIKE '%" + criteria.trim() + "%' " : "");
		if(isShow!= null && isShow){
			if (tNumbers != null && !tNumbers.isEmpty()) {
				String tmpNumbers[] = tNumbers.split(";");
				for (String tmpNum : tmpNumbers) {
					tmpNum = tmpNum.replace("'", "''");
					if(!tmpNum.isEmpty() && !tmpNum.trim().isEmpty()){
						sql += " AND TRANS_NUMBER != '" + tmpNum.trim() + "'";
					}
				}
			}
		}

		TransactionRegisterDtoHandler handler = new TransactionRegisterDtoHandler(arCustAcctId);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class TransactionRegisterDtoHandler implements QueryResultHandler<ArTransactionRegisterDto> {
		private Integer arCustAcctId;

		private TransactionRegisterDtoHandler(Integer arCustAcctId) {
			this.arCustAcctId = arCustAcctId;
		}

		@Override
		public List<ArTransactionRegisterDto> convert(List<Object[]> queryResult) {
			List<ArTransactionRegisterDto> transactionRegister =
					new ArrayList<ArTransactionRegisterDto>();
			for (Object[] rowResult : queryResult) {
				ArTransactionRegisterDto transRegisterDto = new ArTransactionRegisterDto();
				int colNum = 0;
				transRegisterDto.setId((Integer) rowResult[colNum]);
				transRegisterDto.setCustomerId((Integer) rowResult[++colNum]);
				transRegisterDto.setTransactionNumber((String) rowResult[++colNum]);
				transRegisterDto.setCustomerName((String) rowResult[++colNum]);
				transRegisterDto.setTotalPayment((Double) rowResult[++colNum]);
				transRegisterDto.setAmount((Double) rowResult[++colNum]);
				transRegisterDto.setSequenceNumber((Integer) rowResult[++colNum]);
				transactionRegister.add(transRegisterDto);
			}
			return transactionRegister;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			if (arCustAcctId != -1) {
				query.setParameter(index, arCustAcctId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("AR_TRANSACTION_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("TRANS_NUMBER", Hibernate.STRING);
			query.addScalar("CUSTOMER_NAME", Hibernate.STRING);
			query.addScalar("TOTAL_PAYMENT", Hibernate.DOUBLE);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
		}
	}

	@Override
	public List<ArTransaction> getTransactionUsedInReturns(Integer arTransactionId) {
		DetachedCriteria artDc = getDetachedCriteria();
		DetachedCriteria asiDc = DetachedCriteria.forClass(AccountSaleItem.class);
		asiDc.setProjection(Projections.property(AccountSaleItem.FIELD.arTransactionId.name()));
		artDc.add(Subqueries.propertyIn(ArTransaction.FIELD.id.name(), asiDc));
		artDc.add(Restrictions.eq(ArTransaction.FIELD.accountSaleId.name(), arTransactionId));
		artDc.createAlias("formWorkflow", "rfw");
		artDc.add(Restrictions.ne("rfw.currentStatusId", FormStatus.CANCELLED_ID));
		return getAll(artDc);
	}

	@Override
	public Page<ArTransaction> getArTransactions(FormPluginParam formPluginParam) {

		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, formPluginParam.getUser());
		dc.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_SALES_ORDER));
		if (!formPluginParam.getSearchCriteria().trim().isEmpty()) {
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", formPluginParam.getSearchCriteria().trim(), Hibernate.STRING));
		}

		// Workflow status
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if (formPluginParam.getStatuses().size() > 0)
			addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formPluginParam.getStatuses());
		dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.addOrder(Order.desc(ArTransaction.FIELD.transactionDate.name()));
		dc.addOrder(Order.desc(ArTransaction.FIELD.createdDate.name()));
		return getAll(dc, formPluginParam.getPageSetting());
	}

	@Override
	public Page<ArTransaction> getArTransactions(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer soNumber, Date dateFrom, Date dateTo, User user, PageSetting pageSetting) {
		
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		if (companyId != null) {
			dc.add(Restrictions.eq(ArTransaction.FIELD.companyId.name(), companyId));
		}
		if (arCustomerId != null) {
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerId.name(), arCustomerId));
		}
		if (arCustomerAccountId != null) {
			dc.add(Restrictions.eq(ArTransaction.FIELD.customerAcctId.name(), arCustomerAccountId));
		}
		if (soNumber != null) {
			dc.add(Restrictions.eq(ArTransaction.FIELD.sequenceNumber.name(), soNumber));
		}
		if (dateFrom != null && dateTo != null) {
			dc.add(Restrictions.between(ArTransaction.FIELD.transactionDate.name(), dateFrom, dateTo));
		} else if (dateFrom != null)  {
			dc.add(Restrictions.ge(ArTransaction.FIELD.transactionDate.name(), dateFrom));
		} else if (dateTo != null) {
			dc.add(Restrictions.le(ArTransaction.FIELD.transactionDate.name(), dateTo));
		}

		dc.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_SALES_ORDER));

		//Special order Workflow - Add Special order that are used by cancelled Special order.
		DetachedCriteria wipSOWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		wipSOWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		wipSOWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		// Sales Order Workflow
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.addOrder(Order.desc(ArTransaction.FIELD.transactionDate.name()));
		dc.addOrder(Order.desc(ArTransaction.FIELD.sequenceNumber.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public double computeAvailableBalance(Integer arCustomerId) {
		List<Object> objects = executeSP("GET_CUSTOMER_AVAILABE_BALANCE", arCustomerId);
		if (!objects.isEmpty()) {
			return (Double) objects.iterator().next();
		}
		return 0;
	}

	@Override
	public List<VATDeclarationDto> getOutputVatDeclaration(Integer companyId, Integer divisionId, Integer year,
			Integer quarter, Integer month) {
		String sql = "SELECT TAX_TYPE_ID, TAX_TYPE, SUM(TAX_BASE) AS TAX_BASE, "
				+ "SUM(VAT_AMOUNT) AS VAT_AMOUNT FROM ( "
				+ "SELECT ASL.TAX_TYPE_ID, (CASE WHEN ASL.TAX_TYPE_ID = 2 THEN 'Sales - Exempt' "
				+ "WHEN ASL.TAX_TYPE_ID = 3 THEN 'Sales - Zero Rated' "
				+ "WHEN ASL.TAX_TYPE_ID = 8 THEN 'Sales - Private' "
				+ "WHEN ASL.TAX_TYPE_ID = 9 THEN 'Sales - Government' END) AS TAX_TYPE, "
				+ "ASL.AMOUNT AS TAX_BASE, COALESCE(ASL.VAT_AMOUNT, 0) AS VAT_AMOUNT "
				+ "FROM AR_SERVICE_LINE ASL "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASL.AR_TRANSACTION_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND ASL.TAX_TYPE_ID IS NOT NULL "
				+ "AND AT.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND AT.DIVISION_ID = ? " : "")
				+ (month != -1 ? "AND MONTH(AT.GL_DATE) = ? " : "")
				+ (year != -1 ? "AND YEAR(AT.GL_DATE) = ? " : "")
				+ (quarter != -1 ? "AND QUARTER(AT.GL_DATE)= ? " : "")
				+ "UNION ALL "
				+ "SELECT ARIL.TAX_TYPE_ID, (CASE WHEN ARIL.TAX_TYPE_ID = 2 THEN 'Sales - Exempt' "
				+ "WHEN ARIL.TAX_TYPE_ID = 3 THEN 'Sales - Zero Rated' "
				+ "WHEN ARIL.TAX_TYPE_ID = 8 THEN 'Sales - Private' "
				+ "WHEN ARIL.TAX_TYPE_ID = 9 THEN 'Sales - Government' END) AS TAX_TYPE, "
				+ "ARIL.AMOUNT AS TAX_BASE, COALESCE(ARIL.VAT_AMOUNT, 0) AS VAT_AMOUNT "
				+ "FROM AR_INVOICE_LINE ARIL "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.AR_INVOICE_ID = ARIL.AR_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND ARIL.TAX_TYPE_ID IS NOT NULL "
				+ "AND ARI.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND ARI.DIVISION_ID = ? " : "")
				+ (month != -1 ? "AND MONTH(ARI.DATE_RECEIVED) = ? " : "")
				+ (year != -1 ? "AND YEAR(ARI.DATE_RECEIVED) = ? " : "")
				+ (quarter != -1 ? "AND QUARTER(ARI.DATE_RECEIVED)= ? " : "")
				+ "UNION ALL "
				+ "SELECT ARII.TAX_TYPE_ID, (CASE WHEN ARII.TAX_TYPE_ID = 2 THEN 'Sales - Exempt' "
				+ "WHEN ARII.TAX_TYPE_ID = 3 THEN 'Sales - Zero Rated' "
				+ "WHEN ARII.TAX_TYPE_ID = 8 THEN 'Sales - Private' "
				+ "WHEN ARII.TAX_TYPE_ID = 9 THEN 'Sales - Government' END) AS TAX_TYPE, "
				+ "ARII.AMOUNT AS TAX_BASE, COALESCE(ARII.VAT_AMOUNT, 0) AS VAT_AMOUNT "
				+ "FROM AR_INVOICE_ITEM ARII "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.AR_INVOICE_ID = ARII.AR_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND ARII.TAX_TYPE_ID IS NOT NULL "
				+ "AND ARI.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND ARI.DIVISION_ID = ? " : "")
				+ (month != -1 ? "AND MONTH(ARI.DATE_RECEIVED) = ? " : "")
				+ (year != -1 ? "AND YEAR(ARI.DATE_RECEIVED) = ? " : "")
				+ (quarter != -1 ? "AND QUARTER(ARI.DATE_RECEIVED)= ? " : "")
				+ "UNION ALL "
				+ "SELECT SI.TAX_TYPE_ID, (CASE WHEN SI.TAX_TYPE_ID = 2 THEN 'Sales - Exempt' "
				+ "WHEN SI.TAX_TYPE_ID = 3 THEN 'Sales - Zero Rated' "
				+ "WHEN SI.TAX_TYPE_ID = 8 THEN 'Sales - Private' "
				+ "WHEN SI.TAX_TYPE_ID = 9 THEN 'Sales - Government' END) AS TAX_TYPE, "
				+ "SI.AMOUNT AS TAX_BASE, COALESCE(SI.VAT_AMOUNT, 0) AS VAT_AMOUNT "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND OTO.OR_TYPE_ID = 12006 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND SI.TAX_TYPE_ID IS NOT NULL "
				+ "AND ARI.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND ARI.DIVISION_ID = ? " : "")
				+ (month != -1 ? "AND MONTH(ARI.DATE_RECEIVED) = ? " : "")
				+ (year != -1 ? "AND YEAR(ARI.DATE_RECEIVED) = ? " : "")
				+ (quarter != -1 ? "AND QUARTER(ARI.DATE_RECEIVED)= ? " : "")
				+ "UNION ALL "
				+ "SELECT ARML.TAX_TYPE_ID, (CASE WHEN ARML.TAX_TYPE_ID = 2 THEN 'Sales - Exempt' "
				+ "WHEN ARML.TAX_TYPE_ID = 3 THEN 'Sales - Zero Rated' "
				+ "WHEN ARML.TAX_TYPE_ID = 8 THEN 'Sales - Private' "
				+ "WHEN ARML.TAX_TYPE_ID = 9 THEN 'Sales - Government' END) AS TAX_TYPE, "
				+ "ARML.AMOUNT AS TAX_BASE, COALESCE(ARML.VAT_AMOUNT, 0) AS VAT_AMOUNT "
				+ "FROM AR_MISCELLANEOUS_LINE ARML "
				+ "INNER JOIN AR_MISCELLANEOUS ARM ON ARM.AR_MISCELLANEOUS_ID = ARML.AR_MISCELLANEOUS_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARM.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND ARML.TAX_TYPE_ID IS NOT NULL "
				+ "AND ARM.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND ARM.DIVISION_ID = ? " : "")
				+ (month != -1 ? "AND MONTH(ARM.MATURITY_DATE) = ? " : "")
				+ (year != -1 ? "AND YEAR(ARM.MATURITY_DATE) = ? " : "")
				+ (quarter != -1 ? "AND QUARTER(ARM.MATURITY_DATE)= ? " : "")
				+ "UNION ALL "
				+ "SELECT TT.TAX_TYPE_ID, (CASE WHEN TT.TAX_TYPE_ID = 2 THEN 'Sales - Exempt' "
				+ "WHEN TT.TAX_TYPE_ID = 3 THEN 'Sales - Zero Rated' "
				+ "WHEN TT.TAX_TYPE_ID = 8 THEN 'Sales - Private' "
				+ "WHEN TT.TAX_TYPE_ID = 9 THEN 'Sales - Government' END) AS TAX_TYPE, 0 AS TAX_BASE, 0 AS VAT_AMOUNT "
				+ "FROM TAX_TYPE TT "
				+ "WHERE TAX_TYPE_ID IN (2, 3, 9, 8) "
				+ ") TBL GROUP BY TAX_TYPE_ID "
				+ "ORDER BY TAX_TYPE";
		return (List<VATDeclarationDto>) get(sql, new getOutputVatDeclarationHandler(companyId,
				divisionId, month, year, quarter, 5));
	}

	private static class getOutputVatDeclarationHandler implements QueryResultHandler<VATDeclarationDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer month;
		private Integer year;
		private Integer quarter;
		private int noOfTbls;

		private getOutputVatDeclarationHandler(Integer companyId, Integer divisionId, Integer month,
				Integer year, Integer quarter, int noOfTbls) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.month = month;
			this.year = year;
			this.quarter = quarter;
			this.noOfTbls = noOfTbls;
		}

		@Override
		public List<VATDeclarationDto> convert(List<Object[]> queryResult) {
			List<VATDeclarationDto> outputVat = new ArrayList<VATDeclarationDto>();
			VATDeclarationDto dto = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				dto = new VATDeclarationDto();
				dto.setName((String) rowResult[colNum]);
				dto.setTaxBase(NumberFormatUtil.roundOffTo2DecPlaces((Double) rowResult[++colNum]));
				dto.setVat(NumberFormatUtil.roundOffTo2DecPlaces((Double) rowResult[++colNum]));
				outputVat.add(dto);
			}
			return outputVat;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, companyId);
				if (divisionId != -1) {
					query.setParameter(++index, divisionId);
				}
				if (month != -1) {
					query.setParameter(++index, month);
				}
				if (year != -1) {
					query.setParameter(++index, year);
				}
				if (quarter != -1 ) {
					query.setParameter(++index, quarter);
				}
				if (i < (noOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("TAX_TYPE", Hibernate.STRING);
			query.addScalar("TAX_BASE", Hibernate.DOUBLE);
			query.addScalar("VAT_AMOUNT", Hibernate.DOUBLE);
		}
	}
}
