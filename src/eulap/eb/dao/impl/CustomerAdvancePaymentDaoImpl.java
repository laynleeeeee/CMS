package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentLine;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.ArTransactionRegisterDto;

/**
 * Implementing class of {@link CustomerAdvancePaymentDao}

 *
 */
public class CustomerAdvancePaymentDaoImpl extends BaseDao<CustomerAdvancePayment> implements CustomerAdvancePaymentDao{

	@Override
	protected Class<CustomerAdvancePayment> getDomainClass() {
		return CustomerAdvancePayment.class;
	}

	@Override
	public Integer generateCapNumber(Integer companyId, Integer typeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(CustomerAdvancePayment.FIELD.capNumber.name()));
		if(companyId != null) {
			dc.add(Restrictions.eq(CustomerAdvancePayment.FIELD.companyId.name(), companyId));
		}
		if(typeId != null) {
			dc.add(Restrictions.eq(CustomerAdvancePayment.FIELD.customerAdvancePaymentTypeId.name(), typeId));
		}
		return generateSeqNo(dc);
	}

	@Override
	public Page<CustomerAdvancePayment> searchCaps(String criteria, Integer typeId,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(StringFormatUtil.isNumeric(criteria)) {
			dc.add(Restrictions.sqlRestriction("CAP_NUMBER LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		dc.add(Restrictions.eq(CustomerAdvancePayment.FIELD.customerAdvancePaymentTypeId.name(), typeId));
		dc.addOrder(Order.asc(CustomerAdvancePayment.FIELD.capNumber.name()));
		return getAll(dc, pageSetting);
	}
	@Override
	public Page<CustomerAdvancePayment> getCapsForApproval(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final Integer typeId, final PageSetting pageSetting) {
		HibernateCallback<Page<CustomerAdvancePayment>> hibernateCallback = new HibernateCallback<Page<CustomerAdvancePayment>>() {
			@Override
			public Page<CustomerAdvancePayment> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(CustomerAdvancePayment.class);
				dc.add(Restrictions.eq(CustomerAdvancePayment.FIELD.customerAdvancePaymentTypeId.name(), typeId));
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						CustomerAdvancePayment.FIELD.receiptDate.name(), CustomerAdvancePayment.FIELD.receiptDate.name(),
						CustomerAdvancePayment.FIELD.receiptDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				String searchCriteria = searchParam.getSearchCriteria();
				if (searchCriteria != null && !searchCriteria.isEmpty()) {
					searchCriteria = StringFormatUtil.removeExtraWhiteSpaces(searchCriteria);
					Criterion sInvCrit = Restrictions.like(CustomerAdvancePayment.FIELD.salesInvoiceNo.name(),
							StringFormatUtil.appendWildCard(searchCriteria));
					if (StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
						dc.add(Restrictions.or(sInvCrit, Restrictions.sqlRestriction("CAP_NUMBER LIKE ?",
								searchParam.getSearchCriteria().trim(), Hibernate.STRING)));
					} else {
						dc.add(sInvCrit);
					}
				}
				// Workflow status
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(CustomerAdvancePayment.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(CustomerAdvancePayment.FIELD.receiptDate.name()));
				dc.addOrder(Order.desc(CustomerAdvancePayment.FIELD.createdDate.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<ArTransactionRegisterDto> getCAPRegisterData(int companyId, int divisionId,int arCustomerId, int arCustomerAccountId,
			Date dateFrom, Date dateTo, int statusId, PageSetting pageSetting) {
		return executePagedSP("GET_CAP_REGISTER", pageSetting, new CAPRegisterHandler(), companyId, divisionId, arCustomerId, 
				arCustomerAccountId, dateFrom, dateTo, statusId);
	}

	private static class CAPRegisterHandler implements QueryResultHandler<ArTransactionRegisterDto> {

		@Override
		public List<ArTransactionRegisterDto> convert(List<Object[]> queryResult) {
			List<ArTransactionRegisterDto> registerRptData = new ArrayList<ArTransactionRegisterDto>();
			ArTransactionRegisterDto cap = null;
			int index = 0;
			double amount = 0;
			double deliveredAmt = 0;
			for (Object[] row : queryResult) {
				index = 0;
				cap = new ArTransactionRegisterDto();
				cap.setId((Integer) row[index++]); //CAP Id
				cap.setSequenceNumber((Integer) row[index++]); //CAP No.
				cap.setDivision((String) row[index++]) ; //Division
				cap.setTransactionDate((Date) row[index++]); //Receipt Date
				cap.setCustomerName((String) row[index++]);
				cap.setCustomerAcctName((String) row[index++]);
				cap.setTransactionNumber((String) row[index++]); //Sales Invoice
				cap.setPoNumber((String) row[index++]); //PO No.
				amount = (Double) row[index++]; // CAP items + ar lines
				deliveredAmt = (Double) row[index++]; // CAP Delivery items + ar lines
				cap.setAmount(amount);
				cap.setBalance(amount - deliveredAmt);
				cap.setStatus((String) row[index++]); //Unserved, Fully and Partially Served
				cap.setCancellationRemarks((String) row[index++]);//Comment of CancellationRemark
				cap.setCollectionStatus((String) row[index++]);
				registerRptData.add(cap);
			}
			return registerRptData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("SEQ_NO", Hibernate.INTEGER);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("CUSTOMER", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCT", Hibernate.STRING);
			query.addScalar("REFERENCE_NO", Hibernate.STRING);
			query.addScalar("PO_NUMBER", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("TOTAL_DELIVERY", Hibernate.DOUBLE);
			query.addScalar("STATUS", Hibernate.STRING);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
			query.addScalar("COLLECTION_STATUS", Hibernate.STRING);
		}
	}

	private DetachedCriteria searchCAP(int companyId,  int divisionId,int arCustomerId, int arCustomerAccountId,
			 Date dateFrom, Date dateTo, DetachedCriteria capCrit) {
		if(capCrit != null) {
			if(companyId != -1){
				capCrit.add(Restrictions.eq(CustomerAdvancePayment.FIELD.companyId.name(), companyId));
			}
			if(divisionId != -1){
				capCrit.add(Restrictions.eq(CustomerAdvancePayment.FIELD.divisionId.name(), divisionId));
			}
			if(arCustomerId != -1){
				capCrit.add(Restrictions.eq(CustomerAdvancePayment.FIELD.arCustomerId.name(), arCustomerId));
			}
			if(arCustomerAccountId != -1){
				capCrit.add(Restrictions.eq(CustomerAdvancePayment.FIELD.arCustomerAccountId.name(), arCustomerAccountId));
			}
			if(dateFrom!=null && dateTo != null){
				capCrit.add(Restrictions.between(CustomerAdvancePayment.FIELD.receiptDate.name(), dateFrom, dateTo));
			}
		}
		return capCrit;
	}

	@Override
	public Page<CustomerAdvancePayment> getCAPReferences(int companyId,int typeId, int arCustomerId, int arCustomerAccountId,
			int capNo, Date dateFrom, Date dateTo, int statusId, PageSetting pageSetting, User user) {
		return getCAPReferences(companyId, null, typeId, arCustomerId, arCustomerAccountId, capNo, dateFrom, dateTo, statusId, pageSetting, user);
	}

	@Override
	public Double getTotalAdvPaymentsBySO(Integer soId, Integer capId) {
		String sql = "SELECT SUM(CASH) AS TOTAL_ADV_PYMNT_BALANCE "
				+ "FROM ( "
				+ "SELECT SO.SALES_ORDER_ID AS SALES_ORDER_ID, SO.SEQUENCE_NO AS SEQUENCE_NO, SO.COMPANY_ID AS COMPANY_ID, "
				+ "SO.AR_CUSTOMER_ID AS AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID AS AR_CUSTOMER_ACCOUNT_ID, COALESCE(CAP.CASH,0) AS CASH "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = CAP.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SO.SALES_ORDER_ID = ? ";
		if(capId != 0) {
			sql += "AND CAP.CUSTOMER_ADVANCE_PAYMENT_ID != ? ";
		}
		sql += ") AS TBL";
		PageSetting pageSetting = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		TotalAdvPymentHandler handler = new TotalAdvPymentHandler(soId, capId);
		Double totalAdvances = getAllAsPage(sql.toString(), pageSetting, handler).getData().iterator().next();
		return totalAdvances == null ? 0.0 : totalAdvances;
	}

	private static class TotalAdvPymentHandler implements QueryResultHandler<Double> {
		private Integer soId;
		private Integer capId;

		private TotalAdvPymentHandler (Integer soId, Integer capId) {
			this.soId = soId;
			this.capId = capId;
		}

		@Override
		public List<Double> convert(List<Object[]> queryResult) {
			List<Double> ret = new ArrayList<>();
			for (Object row : queryResult) {
				Double deliveredAmt = (Double)row;
				ret.add(deliveredAmt);
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, soId);
			if(capId != 0) {
				query.setParameter(++index, capId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("TOTAL_ADV_PYMNT_BALANCE", Hibernate.DOUBLE);
		}
	}

	private String getDrRefIds(String arTransactionIds) {
		String drRefIds = "";
		String trIds = processStrIds(arTransactionIds);
		String sql = "SELECT ART.AR_TRANSACTION_ID, COALESCE(ARI.DR_REFERENCE_IDS, '') AS DR_REFERENCE_IDS FROM AR_TRANSACTION ART "
				+ "INNER JOIN AR_INVOICE_TRANSACTION ARIT ON ARIT.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.AR_INVOICE_ID = ARIT.AR_INVOICE_ID "
				+ "WHERE ART.AR_TRANSACTION_ID IN ("+trIds+")";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					drRefIds += (String) row[1];
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return processStrIds(drRefIds);
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
		return processedIds;
	}

	@Override
	public List<CustomerAdvancePayment> getCustomerAdvancePayments(String arTransactionIds) {
		List<CustomerAdvancePayment> caps = new ArrayList<CustomerAdvancePayment>();
		String drRefIds = getDrRefIds(arTransactionIds);
		if (!drRefIds.isEmpty()) {
			String sql = "SELECT CAP.CUSTOMER_ADVANCE_PAYMENT_ID, CAP.CAP_NUMBER, CAP.CASH "
					+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
					+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = CAP.SALES_ORDER_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 "
					+ "AND DR.DELIVERY_RECEIPT_ID IN ("+drRefIds+") "
					+ "AND CAP.CUSTOMER_ADVANCE_PAYMENT_ID NOT IN ( "
					+ "SELECT ARRAP.CUSTOMER_ADVANCE_PAYMENT_ID FROM AR_RECEIPT_ADVANCE_PAYMENT ARRAP "
					+ "INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = ARRAP.AR_RECEIPT_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4) "
					+ "GROUP BY CAP.CUSTOMER_ADVANCE_PAYMENT_ID ";
			Session session = null;
			CustomerAdvancePayment cap = null;
			try {
				session = getSession();
				SQLQuery query = session.createSQLQuery(sql);
				List<Object[]> list = query.list();
				if (list != null && !list.isEmpty()) {
					for (Object[] row : list) {
						cap = new CustomerAdvancePayment();
						cap.setId((Integer) row[0]);
						cap.setCapNumber((Integer) row[1]);
						cap.setCash((Double) row[2]);
						caps.add(cap);
					}
				}
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
		return caps;
	}

	@Override
	public List<CustomerAdvancePayment> getCustomerAdvancePayments(Integer companyId, Integer customerId,
			Integer customerAcctId, Integer capNumber, boolean isExact, String toBeExcludedCapIds,
			Integer arReceiptId) {
		List<CustomerAdvancePayment> caps = new ArrayList<CustomerAdvancePayment>();
		String sql = "SELECT CAP.CUSTOMER_ADVANCE_PAYMENT_ID, CAP.CAP_NUMBER, CAP.CASH "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND CAP.CUSTOMER_ADVANCE_PAYMENT_ID NOT IN ( "
				+ "SELECT ARRAP.CUSTOMER_ADVANCE_PAYMENT_ID FROM AR_RECEIPT_ADVANCE_PAYMENT ARRAP "
				+ "INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = ARRAP.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 ";
		if (arReceiptId != null) {
			sql += "AND AR.AR_RECEIPT_ID != " + arReceiptId + " ";
		}
		sql += ") AND CAP.COMPANY_ID = ? "
			+ "AND CAP.AR_CUSTOMER_ID = ? "
			+ "AND CAP.AR_CUSTOMER_ACCOUNT_ID = ? ";
		if (capNumber != null) {
			if (isExact) {
				sql += "AND CAP.CAP_NUMBER = ? ";
			} else {
				sql += "AND CAP.CAP_NUMBER LIKE ? ";
			}
		}
		if (toBeExcludedCapIds != null) {
			String tmpIds[] = toBeExcludedCapIds.split(";");
			for (String tmp : tmpIds) {
				if (tmp != null && !tmp.isEmpty()) {
					sql += "AND CAP.CUSTOMER_ADVANCE_PAYMENT_ID != '" + tmp.trim() + "'";
				}
			}
		}
		Session session = null;
		CustomerAdvancePayment cap = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, companyId);
			query.setParameter(1, customerId);
			query.setParameter(2, customerAcctId);
			if (capNumber != null) {
				if (isExact) {
					query.setParameter(3, capNumber);
				} else {
					query.setParameter(3, "'%"+capNumber+"%'");
				}
			}

			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					cap = new CustomerAdvancePayment();
					cap.setId((Integer) row[0]);
					cap.setCapNumber((Integer) row[1]);
					cap.setCash((Double) row[2]);
					caps.add(cap);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return caps;
	}

	@Override
	public List<CustomerAdvancePayment> getCAPsBySalesOrderId(Integer soId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		dc.add(Subqueries.propertyIn(CustomerAdvancePayment.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Restrictions.eq(CustomerAdvancePayment.FIELD.salesOrderId.name(), soId));
		return getAll(dc);
	}

	@Override
	public String getSalesOrderRefNumber(String drReferenceIds) {
		String referenceNumber = null;
		String processedDrIds = processStrIds(drReferenceIds);
		String sql = "SELECT GROUP_CONCAT(' ', 'SO-', SO.SEQUENCE_NO) AS SO_NUMBER, "
				+ "COALESCE((SELECT GROUP_CONCAT('CAP-', CAP.CAP_NUMBER) FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE CAP.CUSTOMER_ADVANCE_PAYMENT_ID = SO.SALES_ORDER_ID), '') AS CAP_NUMBER "
				+ "FROM SALES_ORDER SO "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "WHERE DR.DELIVERY_RECEIPT_ID IN ("+processedDrIds+")";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.addScalar("SO_NUMBER", Hibernate.STRING);
			query.addScalar("CAP_NUMBER", Hibernate.STRING);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					referenceNumber = (String) row[0] + " - " + (String) row[1];
					break; // expecting 1 row only
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return StringFormatUtil.removeExtraWhiteSpaces(referenceNumber);
	}

	@Override
	public List<CustomerAdvancePayment> getSoAdvPayments(Integer advPaymentId, Integer refSoId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CustomerAdvancePayment.FIELD.salesOrderId.name(), refSoId));
		if (advPaymentId != null) {
			dc.add(Restrictions.ne(CustomerAdvancePayment.FIELD.id.name(), advPaymentId));
		}
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(CustomerAdvancePayment.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(dc);
	}

	@Override
	public List<CustomerAdvancePayment> getCapsByCapId(Integer capId) {
		DetachedCriteria dc = getDetachedCriteria();
		//Customer Advance Payment Line
		DetachedCriteria capLineDc = DetachedCriteria.forClass(CustomerAdvancePaymentLine.class);
		capLineDc.setProjection(Projections.property(CustomerAdvancePaymentLine.FIELD.customerAdvPaymentId.name()));
		dc.add(Subqueries.propertyIn(CustomerAdvancePayment.FIELD.id.name(), capLineDc));
		//Object to object
		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), 12003));
		capLineDc.add(Subqueries.propertyIn(CustomerAdvancePaymentLine.FIELD.ebObjectId.name(), otoDc));
		//Child Customer Advance Payment
		DetachedCriteria cCapDc = DetachedCriteria.forClass(CustomerAdvancePayment.class);
		cCapDc.setProjection(Projections.property(CustomerAdvancePayment.FIELD.ebObjectId.name()));
		cCapDc.add(Restrictions.eq(CustomerAdvancePayment.FIELD.id.name(), capId));
		otoDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), cCapDc));
		//Form Workflow
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(CustomerAdvancePayment.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc);
	}

	@Override
	public double getRemainingCapBalance(int salesOrderId, Integer arInvoiceId, Integer arReceiptId) {
		String sql = "SELECT SALES_ORDER_ID, SUM(COALESCE(CAP, 0)) AS BALANCE FROM ( "
				+ "SELECT CAP.SALES_ORDER_ID, CAP.AMOUNT / CAP.CURRENCY_RATE_VALUE AS CAP FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE CAP.SALES_ORDER_ID = ? "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "GROUP BY CAP.CUSTOMER_ADVANCE_PAYMENT_ID "
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, -ARI.RECOUPMENT / ARI.CURRENCY_RATE_VALUE AS CAP FROM SALES_ORDER SO "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID "
				+ "WHERE SO.SALES_ORDER_ID = ? ";
			if(arInvoiceId != null && arInvoiceId != 0) {
				sql += "AND ARI.AR_INVOICE_ID != ? ";
			}
			sql += "AND FW.CURRENT_STATUS_ID != 4 "
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, -ARR.RECOUPMENT / ARR.CURRENCY_RATE_VALUE AS CAP FROM SALES_ORDER SO "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ARI.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SO.SALES_ORDER_ID = ? ";
			if(arReceiptId != null && arReceiptId != 0) {
				sql += "AND ARR.AR_RECEIPT_ID != ? ";
			}
			sql += ") AS CAP_BALANCE";
		Session session = null;
		double balance = 0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			int index = 0;
			query.setParameter(index, salesOrderId);
			query.setParameter(++index, salesOrderId);
			if(arInvoiceId != null && arInvoiceId != 0) {
				query.setParameter(++index, arInvoiceId);
			}
			query.setParameter(++index, salesOrderId);
			if(arReceiptId != null && arReceiptId != 0) {
				query.setParameter(++index, arReceiptId);
			}
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					balance = (Double) row[1] != null ? (Double) row[1] : 0;
					break;//Expecting 1 result.
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return balance;
	}

	@Override
	public Page<CustomerAdvancePayment> getCAPReferences(int companyId, Integer divisionId, int typeId, int arCustomerId,
			int arCustomerAccountId, int capNo, Date dateFrom, Date dateTo, int statusId, PageSetting pageSetting,
			User user) {
		DetachedCriteria capCrit = getDetachedCriteria();
		capCrit = searchCAP(companyId,divisionId,arCustomerId, arCustomerAccountId, dateFrom, dateTo, capCrit);
		capCrit.add(Restrictions.eq(CustomerAdvancePayment.FIELD.customerAdvancePaymentTypeId.name(), typeId));
		addUserCompany(capCrit, user);
		if (capNo != -1) {
			capCrit.add(Restrictions.eq(CustomerAdvancePayment.FIELD.capNumber.name(), capNo));
		}
		capCrit.createAlias("formWorkflow", "cfw");

		if(statusId != SaleItemUtil.STATUS_ALL) {
			DetachedCriteria deliveryCrit = DetachedCriteria.forClass(CAPDelivery.class);
			deliveryCrit.setProjection(Projections.property(CAPDelivery.FIELD.customerAdvancePaymentId.name()));
			if(typeId != CustomerAdvancePaymentType.WIP_SPECIAL_ORDER) {
				deliveryCrit.add(Restrictions.isNotNull(CAPDelivery.FIELD.customerAdvancePaymentId.name()));
			}
			deliveryCrit.createAlias("formWorkflow", "dfw");
			deliveryCrit.add(Restrictions.ne("dfw.currentStatusId", FormStatus.CANCELLED_ID));
			if(statusId == SaleItemUtil.STATUS_USED) {
				capCrit.add(Subqueries.propertyIn(CustomerAdvancePayment.FIELD.id.name(), deliveryCrit));
			} else if(statusId == SaleItemUtil.STATUS_UNUSED) {
				capCrit.add(Subqueries.propertyNotIn(CustomerAdvancePayment.FIELD.id.name(), deliveryCrit));
			}
		}

		capCrit.add(Restrictions.eq("cfw.complete", true));
		capCrit.addOrder(Order.desc(CustomerAdvancePayment.FIELD.receiptDate.name()));
		capCrit.addOrder(Order.desc(CustomerAdvancePayment.FIELD.capNumber.name()));
		return getAll(capCrit, pageSetting);
	}
}
