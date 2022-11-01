package eulap.eb.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
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
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.ApPaymentLine;
import eulap.eb.domain.hibernate.ApPaymentLineType;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.domain.hibernate.DirectPayment;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PaymentType;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.MCFlowSubDetailDto;
import eulap.eb.web.dto.PaymentRegisterDto;

/**
 * DAO implementation of {@link ApPaymentDao}

 */
public class ApPaymentDaoImpl extends BaseDao<ApPayment> implements ApPaymentDao {

	@Override
	protected Class<ApPayment> getDomainClass() {
		return ApPayment.class;
	}

	@Override
	public int generateVoucherNumber(int companyId, Integer paymentTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(ApPayment.FIELD.voucherNumber.name()));
		dc.add(Restrictions.eq(ApPayment.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(ApPayment.FIELD.paymentTypeId.name(), paymentTypeId));
		List<Object> result = getByProjection(dc);
		if (result == null)
			return 1;
		Object obj = result.iterator().next();
		if (obj == null)
			return 1;
		return ((Integer) obj) + 1;
	}

	@Override
	public boolean isUniqueCheckNumber(ApPayment apPayment, int bankAccountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ApPayment.FIELD.checkNumber.name(), apPayment.getCheckNumber()));
		dc.add(Restrictions.eq(ApPayment.FIELD.bankAccountId.name(), bankAccountId));
		return getAll(dc).size() < 1;
	}

	@Override
	public BigDecimal getMaxCheckNumber(Checkbook checkbook) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ApPayment.FIELD.checkbookId.name(), checkbook.getId()));
		dc.setProjection(Projections.max(ApPayment.FIELD.checkNumber.name()));
		List<Object> result = getByProjection(dc);
		if(result == null)
			return checkbook.getCheckbookNoFrom();
		Object obj = result.iterator().next();
		if (obj == null)
			return checkbook.getCheckbookNoFrom();
		BigDecimal maxCheckNo = ((BigDecimal) obj);
		return maxCheckNo.add(BigDecimal.ONE);
	}

	@Override
	public Page<ApPayment> searchPayment(final String searchCriteria,
			final PageSetting pageSetting, final int paymentTypeId) {
		HibernateCallback<Page<ApPayment>> paymentCallBack = new HibernateCallback<Page<ApPayment>>() {
			@Override
			public Page<ApPayment> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria =  session.createCriteria(ApPayment.class);
				criteria.add(Restrictions.eq(ApPayment.FIELD.paymentTypeId.name(), paymentTypeId));
				if(!searchCriteria.isEmpty()) {
					if(StringFormatUtil.isNumeric(searchCriteria)) {
						Criterion checkNoCrit = Restrictions.sqlRestriction("CHECK_NUMBER LIKE ?", searchCriteria.trim(), Hibernate.STRING);
						Criterion voucherNoCrit = Restrictions.sqlRestriction("VOUCHER_NO LIKE ?", searchCriteria.trim(), Hibernate.STRING);
						Criterion voucherAndCheckNumber = Restrictions.or(checkNoCrit, voucherNoCrit);

						Double amount = Double.valueOf(searchCriteria);
						criteria.add(Restrictions.or(voucherAndCheckNumber, Restrictions.eq(ApPayment.FIELD.amount.name(), amount)));
					} else {
						// Bank account search criteria
						criteria.createAlias("bankAccount", "b").add(Restrictions.like("b.name", "%" + searchCriteria.trim() + "%"));
					}
				}
				Page<ApPayment> payments = getAll(criteria, pageSetting);
				for (ApPayment payment: payments.getData()) {
					getHibernateTemplate().initialize(payment.getCompany());
					getHibernateTemplate().initialize(payment.getSupplier());
					getHibernateTemplate().initialize(payment.getSupplierAccount());
					getHibernateTemplate().initialize(payment.getBankAccount());
				}
				return payments;
			}
		};
		return getHibernateTemplate().execute(paymentCallBack);
	}

	@Override
	public List<PaymentRegisterDto> searchPayments(int companyId, int divisionId, int bankAccountId, int supplierId, int supplierAccountId,
			Date paymentDateFrom, Date paymentDateTo, Date checkDateFrom, Date checkDateTo, Double amountFrom, Double amountTo, Integer voucherNoFrom,
			Integer voucherNoTo, BigDecimal checkNoFrom, BigDecimal checkNoTo, int paymentStatusId) {
		StringBuilder sql = new StringBuilder("SELECT D.NAME AS DIVISION, APP.PAYMENT_DATE, BA.NAME AS BANK_ACCT, APP.CHECK_NUMBER, "
				+ "APP.CHECK_DATE, S.NAME AS SUPPLIER_NAME, SA.NAME AS SUPPLIER_ACCT, IF(FW.CURRENT_STATUS_ID != 4 OR FW.CURRENT_STATUS_ID != 32, APP.AMOUNT, 0) AS AMOUNT, "
				+ "CONCAT((CASE WHEN APP.PAYMENT_TYPE_ID = 1 THEN 'APP ' ELSE 'DP ' END), ' ', D.NAME, ' ', APP.VOUCHER_NO) AS VOUCHER_NO, "
				+ "FW.CURRENT_STATUS_ID, FS.DESCRIPTION AS FORM_STATUS, IF(FW.CURRENT_STATUS_ID = 16, APP.DATE_CLEARED, "
				+ "(SELECT FWL.CREATED_DATE FROM FORM_WORKFLOW_LOG FWL WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "AND FWL.FORM_STATUS_ID = FW.CURRENT_STATUS_ID)) AS DATE, "
				+ "IF(FW.CURRENT_STATUS_ID = 4, (SELECT FWL.COMMENT FROM FORM_WORKFLOW_LOG FWL WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "AND FWL.FORM_STATUS_ID = FW.CURRENT_STATUS_ID), '') AS CANCELLATION_REMARKS "
				+ "FROM AP_PAYMENT APP "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = APP.DIVISION_ID "
				+ "INNER JOIN BANK_ACCOUNT BA ON BA.BANK_ACCOUNT_ID = APP.BANK_ACCOUNT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = APP.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = APP.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID "
				+ "INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
				+ "WHERE APP.COMPANY_ID = " + companyId + " ");
		if (divisionId != -1) {
			sql.append("AND APP.DIVISION_ID = " + divisionId + " ");
		}
		if (bankAccountId !=-1) {
			sql.append("AND APP.BANK_ACCOUNT_ID = " + bankAccountId + " ");
		}
		if (supplierId != -1) {
			sql.append("AND APP.SUPPLIER_ID = " + supplierId + " ");
		}
		if (supplierAccountId != -1) {
			sql.append("AND APP.SUPPLIER_ACCOUNT_ID = " + supplierAccountId + " ");
		}
		if (paymentDateFrom != null && paymentDateTo != null) {
			sql.append("AND APP.PAYMENT_DATE BETWEEN '" + DateUtil.formatToSqlDate(paymentDateFrom)
				+ "' AND '" + DateUtil.formatToSqlDate(paymentDateTo) + "' ");
		} else if (paymentDateFrom != null) {
			sql.append("AND APP.PAYMENT_DATE = '" + DateUtil.formatToSqlDate(paymentDateFrom) + "' ");
		} else if (paymentDateTo != null) {
			sql.append("AND APP.PAYMENT_DATE = '" + DateUtil.formatToSqlDate(paymentDateTo) + "' ");
		}
		if (checkDateFrom != null && checkDateTo != null) {
			sql.append("AND APP.CHECK_DATE BETWEEN '" + DateUtil.formatToSqlDate(checkDateFrom)
				+ "' AND '" + DateUtil.formatToSqlDate(checkDateTo) + "' ");
		} else if (checkDateFrom != null) {
			sql.append("AND APP.CHECK_DATE = '" + DateUtil.formatToSqlDate(checkDateFrom) + "' ");
		} else if (checkDateTo != null) {
			sql.append("AND APP.CHECK_DATE = '" + DateUtil.formatToSqlDate(checkDateTo) + "' ");
		}
		if (amountFrom != null && amountTo != null) {
			sql.append("AND APP.AMOUNT BETWEEN " + amountFrom + " AND " + amountTo + " ");
		} else if (amountFrom != null) {
			sql.append("AND APP.AMOUNT = " + amountFrom + " ");
		} else if (amountTo != null) {
			sql.append("AND APP.AMOUNT = " + amountTo + " ");
		}
		if (voucherNoFrom != null && voucherNoTo != null) {
			sql.append("AND APP.VOUCHER_NO BETWEEN " + voucherNoFrom + " AND " + voucherNoTo + " ");
		} else if (voucherNoFrom != null) {
			sql.append("AND APP.VOUCHER_NO = " + voucherNoFrom + " ");
		} else if (voucherNoTo != null) {
			sql.append("AND APP.VOUCHER_NO = " + voucherNoTo + " ");
		}
		if (checkNoFrom != null && checkNoTo != null) {
			sql.append("AND APP.CHECK_NUMBER BETWEEN " + checkNoFrom + " AND " + checkNoTo + " ");
		} else if (checkNoFrom != null) {
			sql.append("AND APP.CHECK_NUMBER = " + checkNoFrom + " ");
		} else if (checkNoTo != null) {
			sql.append("AND APP.CHECK_NUMBER = " + checkNoTo + " ");
		}
		if (paymentStatusId != -1) {
			sql.append("AND FW.CURRENT_STATUS_ID = " + paymentStatusId + " ");
		}
		sql.append("ORDER BY APP.CHECK_DATE ASC");
		return (List<PaymentRegisterDto>) get(sql.toString(), new ApPaymentRegisterHandler()) ;
	}

	private static class ApPaymentRegisterHandler implements QueryResultHandler<PaymentRegisterDto> {
		@Override
		public List<PaymentRegisterDto> convert(List<Object[]> queryResult) {
			List<PaymentRegisterDto> paymentRegister = new ArrayList<PaymentRegisterDto>();
			PaymentRegisterDto dto = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				dto = new PaymentRegisterDto();
				// test
				dto.setDivision((String) rowResult[colNum++]);
				dto.setPaymentDate((Date) rowResult[colNum++]);
				dto.setBankAcct((String) rowResult[colNum++]);
				dto.setCheckNo((String) rowResult[colNum++]);
				dto.setCheckDate((Date) rowResult[colNum++]);
				dto.setSupplierName((String) rowResult[colNum++]);
				dto.setSupplierAcct((String) rowResult[colNum++]);
				dto.setAmount((Double) rowResult[colNum++]);
				dto.setVoucherNo((String) rowResult[colNum++]);
				dto.setFormStatusId((Integer) rowResult[colNum++]);
				dto.setFormStatus((String) rowResult[colNum++]);
				dto.setDate((Date) rowResult[colNum++]);
				dto.setCancellationRemarks((String) rowResult[colNum++]);
				paymentRegister.add(dto);
			}
			return paymentRegister;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return -1;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("PAYMENT_DATE", Hibernate.DATE);
			query.addScalar("BANK_ACCT", Hibernate.STRING);
			query.addScalar("CHECK_NUMBER", Hibernate.STRING);
			query.addScalar("CHECK_DATE", Hibernate.DATE);
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCT", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("VOUCHER_NO", Hibernate.STRING);
			query.addScalar("CURRENT_STATUS_ID", Hibernate.INTEGER);
			query.addScalar("FORM_STATUS", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public double totalPerAccount(int companyId, Date asOfDate, int accountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.le(ApPayment.FIELD.paymentDate.name(), asOfDate));

		// For AP Payment workflow status
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcWorkflow));

		//Subquery for Account Combination
		DetachedCriteria acctCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		//Subquery for Bank Account
		DetachedCriteria bankAcctCriteria = DetachedCriteria.forClass(BankAccount.class);
		bankAcctCriteria.add(Subqueries.propertyIn(BankAccount.FIELD.cashPaymentClearingAcctId.name(), acctCombiCriteria));
		bankAcctCriteria.setProjection(Projections.property(BankAccount.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ApPayment.FIELD.bankAccountId.name(), bankAcctCriteria));
		dc.setProjection(Projections.sum(ApPayment.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public Page<ApPayment> getAllAPPayment(final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting) {
		HibernateCallback<Page<ApPayment>> hibernateCallback = new HibernateCallback<Page<ApPayment>>() {
			@Override
			public Page<ApPayment> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria apCriteria = session.createCriteria(ApPayment.class);

				//Search for company number, date and amount
				SearchCommonUtil.searchCommonParams(apCriteria, null, ApPayment.FIELD.companyId.name(),
						ApPayment.FIELD.paymentDate.name(), ApPayment.FIELD.checkDate.name(), null, searchParam.getUser().getCompanyIds(), searchParam);

				String criteria = searchParam.getSearchCriteria();
				if(!criteria.isEmpty()) {
					if (StringFormatUtil.isNumeric(criteria)){
						apCriteria.add(Restrictions.or(
								Restrictions.sqlRestriction("VOUCHER_NO LIKE ?", "%" + criteria + "%", Hibernate.STRING),
								Restrictions.sqlRestriction("CHECK_NUMBER LIKE ? ", "%" + criteria +"%", Hibernate.STRING)));
					}
				}
				apCriteria.add(Restrictions.eq(ApPayment.FIELD.paymentTypeId.name(), PaymentType.TYPE_AP_PAYMENT));

				// Workflow status
				DetachedCriteria dcWorkFlow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkFlow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				dcWorkFlow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				apCriteria.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkFlow));
				apCriteria.addOrder(Order.desc(ApPayment.FIELD.paymentDate.name()));
				apCriteria.addOrder(Order.desc(ApPayment.FIELD.checkDate.name()));
				Page<ApPayment> ret = getAll(apCriteria, pageSetting);
				for (ApPayment ap : ret.getData()) {
					getHibernateTemplate().initialize(ap.getBankAccount());
					getHibernateTemplate().initialize(ap.getSupplier());
				}
				return ret;
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<ApPayment> getApPaymentWithNullWF() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.isNull(ApPayment.FIELD.formWorkflowId.name()));
		dc.getExecutableCriteria(getSession()).setMaxResults(100);
		return getAll(dc);
	}

	@Override
	public boolean hasNullFW() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.isNull(APInvoice.FIELD.formWorkflowId.name()));
		return getAll(dc).size() > 0;
	}

	@Override
	public ApPayment getApPaymentByWorkflow(Integer formWorkflowId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ApPayment.FIELD.formWorkflowId.name(), formWorkflowId));
		return get(dc);
	}

	@Override
	public double getTotalPaymentAmount(Integer companyId, Integer supplierAcctId, Date asOfDate) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ApPayment.FIELD.paymentTypeId.name(), PaymentType.TYPE_AP_PAYMENT));
		if(asOfDate != null) {
			dc.add(Restrictions.le(ApPayment.FIELD.checkDate.name(), asOfDate));
		}
		dc.add(Restrictions.eq(ApPayment.FIELD.supplierAccountId.name(), supplierAcctId));
		//Supplier account criteria
		DetachedCriteria dcSupplierAcct =  DetachedCriteria.forClass(SupplierAccount.class);
		dcSupplierAcct.setProjection(Projections.property(SupplierAccount.FIELD.id.name()));
		dcSupplierAcct.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		//Workflow criteria
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));

		dc.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ApPayment.FIELD.supplierAccountId.name(), dcSupplierAcct));
		dc.setProjection(Projections.sum(ApPayment.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public Integer generateDirectPaymentVC(Integer companyId, Integer directPaymentTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(ApPayment.FIELD.voucherNumber.name()));
		dc.add(Restrictions.eq(ApPayment.FIELD.companyId.name(), companyId));

		DetachedCriteria dcApPayment = DetachedCriteria.forClass(DirectPayment.class);
		dcApPayment.setProjection(Projections.property(DirectPayment.FIELD.apPaymentId.name()));
		dcApPayment.add(Restrictions.eq(DirectPayment.FIELD.directPaymentTypeId.name(), directPaymentTypeId));

		dc.add(Subqueries.propertyIn(ApPayment.FIELD.id.name(), dcApPayment));
		List<Object> result = getByProjection(dc);
		if (result == null)
			return 1;
		Object obj = result.iterator().next();
		if (obj == null)
			return 1;
		return ((Integer) obj) + 1;
	}

	@Override
	public Page<ApPayment> getAllApPaymentForms(final int divisionId, final ApprovalSearchParam searchParam,
			final  List<Integer> formStatusIds, final PageSetting pageSetting) {
		HibernateCallback<Page<ApPayment>> hibernateCallback = new HibernateCallback<Page<ApPayment>>() {
			@Override
			public Page<ApPayment> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria apCriteria = session.createCriteria(ApPayment.class);
				//Search for company number, date and amount
				SearchCommonUtil.searchCommonParams(apCriteria, null, ApPayment.FIELD.companyId.name(),
						ApPayment.FIELD.paymentDate.name(), ApPayment.FIELD.checkDate.name(), null,
						searchParam.getUser().getCompanyIds(), searchParam);
				String criteria = searchParam.getSearchCriteria();
				if (!criteria.isEmpty()) {
					if (StringFormatUtil.isNumeric(criteria)){
						apCriteria.add(Restrictions.or(
								Restrictions.sqlRestriction("VOUCHER_NO LIKE ?", "%" + criteria + "%", Hibernate.STRING),
								Restrictions.sqlRestriction("CHECK_NUMBER LIKE ? ", "%" + criteria +"%", Hibernate.STRING)));
					}
				}
				apCriteria.add(Restrictions.eq(ApPayment.FIELD.divisionId.name(), divisionId));
				// Workflow status
				DetachedCriteria dcWorkFlow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0) {
					addAsOrInCritiria(dcWorkFlow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				}
				dcWorkFlow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				apCriteria.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkFlow));
				apCriteria.addOrder(Order.desc(ApPayment.FIELD.voucherNumber.name()));
				apCriteria.addOrder(Order.desc(ApPayment.FIELD.paymentDate.name()));
				apCriteria.addOrder(Order.desc(ApPayment.FIELD.checkDate.name()));
				Page<ApPayment> ret = getAll(apCriteria, pageSetting);
				for (ApPayment ap : ret.getData()) {
					getHibernateTemplate().initialize(ap.getBankAccount());
					getHibernateTemplate().initialize(ap.getSupplier());
				}
				return ret;
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<ApPayment> getApPaymentBySap(Integer sapId) {
		DetachedCriteria dc = getDetachedCriteria();
		//AP Payment Line
		DetachedCriteria aplDc = DetachedCriteria.forClass(ApPaymentLine.class);
		aplDc.setProjection(Projections.property(ApPaymentLine.FIELD.apPaymentId.name()));
		dc.add(Subqueries.propertyIn(ApPayment.FIELD.id.name(), aplDc));
		//Object to Object
		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		aplDc.add(Subqueries.propertyIn(ApPaymentLine.FIELD.ebObjectId.name(), otoDc));
		//Supplier Advance Payment
		DetachedCriteria sapDc = DetachedCriteria.forClass(SupplierAdvancePayment.class);
		sapDc.setProjection(Projections.property(SupplierAdvancePayment.FIELD.ebObjectId.name()));
		sapDc.add(Restrictions.eq(SupplierAdvancePayment.FIELD.id.name(), sapId));
		otoDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), sapDc));
		//Form Workflow
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc);
	}

	@Override
	public List<ApPayment> getApPaymentsByObjectId(Integer objectId) {
		DetachedCriteria dc = getDetachedCriteria();
		//AP Payment Line
		DetachedCriteria aplDc = DetachedCriteria.forClass(ApPaymentLine.class);
		aplDc.setProjection(Projections.property(ApPaymentLine.FIELD.apPaymentId.name()));
		dc.add(Subqueries.propertyIn(ApPayment.FIELD.id.name(), aplDc));
		//Object to Object
		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), objectId));
		aplDc.add(Subqueries.propertyIn(ApPaymentLine.FIELD.ebObjectId.name(), otoDc));
		//Form Workflow
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.and(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID),
				Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.STALED_ID)));
		dc.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc);
	}


	@Override
	public List<ApPayment> getApPaymentsWithNegativeSap(Integer objectId, Integer paymentId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.ne(ApPayment.FIELD.id.name(), paymentId));
		//AP Payment Line
		DetachedCriteria aplDc = DetachedCriteria.forClass(ApPaymentLine.class);
		aplDc.setProjection(Projections.property(ApPaymentLine.FIELD.apPaymentId.name()));
		aplDc.add(Restrictions.eq(ApPaymentLine.FIELD.apPaymentLineTypeId.name(), ApPaymentLineType.SUPLIER_ADVANCE_PAYMENT));
		aplDc.add(Restrictions.lt(ApPaymentLine.FIELD.paidAmount.name(), 0.00));//Retrieve negative payment lines.
		dc.add(Subqueries.propertyIn(ApPayment.FIELD.id.name(), aplDc));
		//Object to Object
		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), objectId));
		aplDc.add(Subqueries.propertyIn(ApPaymentLine.FIELD.ebObjectId.name(), otoDc));
		//Form Workflow
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc);
	}

	@Override
	public List<MCFlowSubDetailDto> getMcFlowSubDetailDtos(int companyId, int divisionId, Date dateFrom, Date dateTo) {
		List<Object> objects = executeSP("GET_MONTHLY_CASH_FLOW", companyId, divisionId, dateFrom, dateTo);
		List<MCFlowSubDetailDto> subDetailDtos = new ArrayList<MCFlowSubDetailDto>();
		MCFlowSubDetailDto dto = null;
		if (objects != null && !objects.isEmpty()) {
			for (Object rowResult : objects) {
				Object[] row = (Object[]) rowResult;
				dto = new MCFlowSubDetailDto();
				dto.setSourceLabel((String) row[0]);
				dto.setAccountId((Integer) row[1]);
				dto.setAccountName((String) row[2]);
				dto.setAccountTypeId((Integer) row[3]);
				dto.setAmount(NumberFormatUtil.convertBigDecimalToDouble(row[4])); // line amount
				dto.setTrAmount(NumberFormatUtil.convertBigDecimalToDouble(row[5])); // header amount
				dto.setPaidAmount(NumberFormatUtil.convertBigDecimalToDouble(row[6])); // paid amount
				dto.setWtaxAmount(NumberFormatUtil.convertBigDecimalToDouble(row[7])); // withholding tax
				subDetailDtos.add(dto);
			}
		}
		return subDetailDtos;
	}
}
