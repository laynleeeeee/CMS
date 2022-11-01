package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
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
import eulap.eb.dao.ArReceiptDao;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptLine;
import eulap.eb.domain.hibernate.ArReceiptTransaction;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Implementing class of {@link ArReceiptDao}

 *
 */
public class ArReceiptDaoImpl extends BaseDao<ArReceipt> implements ArReceiptDao {
	@Override
	protected Class<ArReceipt> getDomainClass() {
		return ArReceipt.class;
	}

	@Override
	public Page<ArReceipt> searchArReceipts(final String searchCriteria,
			final PageSetting pageSetting) {
		return searchArReceipts(searchCriteria, null, pageSetting);
	}

	@Override
	public Page<ArReceipt> searchArReceipts(final String searchCriteria, Integer divisionId,
			final PageSetting pageSetting) {
		HibernateCallback<Page<ArReceipt>> arReceiptCallBack = new HibernateCallback<Page<ArReceipt>>() {
			@Override
			public Page<ArReceipt> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria =  session.createCriteria(ArReceipt.class);
				if (!searchCriteria.trim().isEmpty()) {
					criteria.add(Restrictions.sqlRestriction("RECEIPT_NUMBER LIKE ?",
							StringFormatUtil.appendWildCard(searchCriteria), Hibernate.STRING));
				}
				if(divisionId != null) {
					criteria.add(Restrictions.eq(ArReceipt.FIELD.divisionId.name(), divisionId));
				}
				Page<ArReceipt> arReceipts = getAll(criteria, pageSetting);
				for (ArReceipt arReceipt: arReceipts.getData()) {
					getHibernateTemplate().initialize(arReceipt.getArCustomer());
					getHibernateTemplate().initialize(arReceipt.getArCustomerAccount());
				}
				return arReceipts;
			}
		};
		return getHibernateTemplate().execute(arReceiptCallBack);
	}

	@Override
	public Page<ArReceipt> getAllArReceipts(final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final int divisionId, final PageSetting pageSetting) {
		HibernateCallback<Page<ArReceipt>> hibernateCallback = new HibernateCallback<Page<ArReceipt>>() {

			@Override
			public Page<ArReceipt> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria arReceiptCriteria = session.createCriteria(ArReceipt.class);
				DetachedCriteria arCusAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
				//Search for company number, date and amount
				SearchCommonUtil.searchCommonParams(arReceiptCriteria, arCusAcctCriteria, ArReceipt.FIELD.arCustomerAccountId.name(),
						ArReceipt.FIELD.maturityDate.name(), ArReceipt.FIELD.receiptDate.name(), null, searchParam.getUser().getCompanyIds(), searchParam);
				String criteria = StringFormatUtil.removeExtraWhiteSpaces(searchParam.getSearchCriteria());
				arReceiptCriteria.add(Restrictions.eq(ArReceipt.FIELD.divisionId.name(), divisionId));
				if (!criteria.isEmpty()) {
					Criterion receiptNumber = Restrictions.like(ArReceipt.FIELD.receiptNumber.name(),
							StringFormatUtil.appendWildCard(criteria));
					Criterion refNumber = Restrictions.like(ArReceipt.FIELD.refNumber.name(),
							StringFormatUtil.appendWildCard(criteria));
					if (StringFormatUtil.isNumeric(criteria)) {
						Criterion criterion = Restrictions.or(receiptNumber, Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ",
								StringFormatUtil.appendWildCard(criteria), Hibernate.STRING));
						arReceiptCriteria.add(Restrictions.or(criterion, refNumber));
					} else {
						arReceiptCriteria.add(Restrictions.or(receiptNumber, refNumber));
					}
				}
				// Workflow status
				DetachedCriteria dcWorkFlow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkFlow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				dcWorkFlow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				arReceiptCriteria.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), dcWorkFlow));
				arReceiptCriteria.addOrder(Order.desc(ArReceipt.FIELD.receiptDate.name()));
				arReceiptCriteria.addOrder(Order.desc(ArReceipt.FIELD.maturityDate.name()));
				arReceiptCriteria.addOrder(Order.desc(ArReceipt.FIELD.sequenceNo.name()));
				Page<ArReceipt> ret = getAll(arReceiptCriteria, pageSetting);
				for (ArReceipt transaction : ret.getData()) {
					getHibernateTemplate().initialize(transaction.getArCustomer());
					getHibernateTemplate().initialize(transaction.getArCustomerAccount());
				}
				return ret;
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public boolean isUniqueReceiptNo(ArReceipt arReceipt, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();

		// AR Customer Account criteria.
		DetachedCriteria arCustAcctDc = DetachedCriteria.forClass(ArCustomerAccount.class);
		arCustAcctDc.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
		arCustAcctDc.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.arCustomerAccountId.name(), arCustAcctDc));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), fwDc));

		dc.add(Restrictions.eq(ArReceipt.FIELD.receiptNumber.name(), arReceipt.getReceiptNumber().trim()));
		dc.add(Restrictions.ne(ArReceipt.FIELD.id.name(), arReceipt.getId()));
		return getAll(dc).size() == 0;
	}

	@Override
	public ArReceipt getArReceiptByWorkflow(Integer formWorkflowId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArReceipt.FIELD.formWorkflowId.name(), formWorkflowId));
		return get(dc);
	}

	@Override
	public Page<ArReceipt> searchArReceipts(int companyId, int customerId,
			int customerAcctId, Date receiptDateFrom, Date receiptDateTo,
			Date maturityDateFrom, Date maturityDateTo, Date asOfDate,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria customerAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
		//Company
		if(companyId != -1){
			customerAcctCriteria.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
			customerAcctCriteria.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
			dc.add(Subqueries.propertyIn(ArReceipt.FIELD.arCustomerAccountId.name(), customerAcctCriteria));
		}
		//Customer
		if(customerId != -1)
			dc.add(Restrictions.eq(ArReceipt.FIELD.arCustomerId.name(), customerId));
		//Customer Account
		if(customerAcctId != -1)
			dc.add(Restrictions.eq(ArReceipt.FIELD.arCustomerAccountId.name(), customerAcctId));
		//Receipt Date
		if(receiptDateFrom != null && receiptDateTo != null)
			dc.add(Restrictions.between(ArReceipt.FIELD.receiptDate.name(), receiptDateFrom, receiptDateTo));
		else if(receiptDateFrom != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.receiptDate.name(), receiptDateFrom));
		else if(receiptDateTo != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.receiptDate.name(), receiptDateTo));
		//Maturity Date
		if(maturityDateFrom != null && maturityDateTo != null)
			dc.add(Restrictions.between(ArReceipt.FIELD.maturityDate.name(), maturityDateFrom, maturityDateTo));
		else if(maturityDateFrom != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.maturityDate.name(), maturityDateFrom));
		else if(maturityDateTo != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.maturityDate.name(), maturityDateTo));
		//As of date
		if(asOfDate != null)
			dc.add(Restrictions.le(ArReceipt.FIELD.receiptDate.name(), asOfDate));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<ArReceipt> getArReceipts(int companyId, int receiptTypeId, int receiptMethodId, int customerId, int customerAcctId, String receiptNumber,
			Date receiptDateFrom, Date receiptDateTo, Date maturityDateFrom,Date maturityDateTo, Double amountFrom, Double amountTo, int wfStatusId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria customerAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
		//Company
		if(companyId != -1){
			customerAcctCriteria.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
			customerAcctCriteria.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
			dc.add(Subqueries.propertyIn(ArReceipt.FIELD.arCustomerAccountId.name(), customerAcctCriteria));
		}
		//Receipt Type
		if(receiptTypeId != -1)
			dc.add(Restrictions.eq(ArReceipt.FIELD.arReceiptTypeId.name(), receiptTypeId));
		//Receipt Method
		if(receiptMethodId != -1)
			dc.add(Restrictions.eq(ArReceipt.FIELD.receiptMethodId.name(), receiptMethodId));
		//Customer
		if(customerId != -1)
			dc.add(Restrictions.eq(ArReceipt.FIELD.arCustomerId.name(), customerId));
		//Customer Account
		if(customerAcctId != -1)
			dc.add(Restrictions.eq(ArReceipt.FIELD.arCustomerAccountId.name(), customerAcctId));
		//Receipt Number
		if(!receiptNumber.isEmpty() || receiptNumber != null)
			dc.add(Restrictions.like(ArReceipt.FIELD.receiptNumber.name(), "%"+receiptNumber.trim()+"%"));
		//Receipt Date
		if(receiptDateFrom != null && receiptDateTo != null)
			dc.add(Restrictions.between(ArReceipt.FIELD.receiptDate.name(), receiptDateFrom, receiptDateTo));
		else if(receiptDateFrom != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.receiptDate.name(), receiptDateFrom));
		else if(receiptDateTo != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.receiptDate.name(), receiptDateTo));
		//Maturity Date
		if(maturityDateFrom != null && maturityDateTo != null)
			dc.add(Restrictions.between(ArReceipt.FIELD.maturityDate.name(), maturityDateFrom, maturityDateTo));
		else if(maturityDateFrom != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.maturityDate.name(), maturityDateFrom));
		else if(maturityDateTo != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.maturityDate.name(), maturityDateTo));
		//Amount
		if(amountFrom != null && amountTo != null)
			dc.add(Restrictions.between(ArReceipt.FIELD.amount.name(), amountFrom, amountTo));
		else if(amountFrom != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.amount.name(), amountFrom));
		else if(amountTo != null)
			dc.add(Restrictions.eq(ArReceipt.FIELD.amount.name(), amountTo));
		//Receipt Status
		if(wfStatusId != -1) {
			DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
			dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.currentStatusId.name(), wfStatusId));
			dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			dc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), dcWorkflow));
		}
		dc.addOrder(Order.asc(ArReceipt.FIELD.receiptDate.name()));
		dc.addOrder(Order.asc(ArReceipt.FIELD.arReceiptTypeId.name()));
		dc.addOrder(Order.asc(ArReceipt.FIELD.receiptNumber.name()));
		dc.createAlias("arCustomer", "arCustomer").addOrder(Order.asc("arCustomer.name"));
		dc.createAlias("arCustomerAccount", "arCustomerAccount").addOrder(Order.asc("arCustomerAccount.name"));
		return getAll(dc, pageSetting);
	}

	@Override
	public Double getCustomerAcctTotalReceipt(Integer customerAcctId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArReceipt.FIELD.arCustomerAccountId.name(), customerAcctId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.setProjection(Projections.sum(ArReceipt.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public Double getCustomerAcctTotalReceipt(Integer companyId, Integer customerId, Date asOfDate) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArReceipt.FIELD.arCustomerAccountId.name(), customerId));
		dc.add(Restrictions.le(ArReceipt.FIELD.maturityDate.name(), asOfDate));

		DetachedCriteria dcCustAcct = DetachedCriteria.forClass(ArCustomerAccount.class);
		dcCustAcct.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
		dcCustAcct.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.arCustomerAccountId.name(), dcCustAcct));
		dc.setProjection(Projections.sum(ArReceipt.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public double getCustomerTotalReceipt(Integer arCustomerId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArReceipt.FIELD.arCustomerId.name(), arCustomerId));
		dc.add(Restrictions.le(ArReceipt.FIELD.maturityDate.name(), new Date()));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.setProjection(Projections.sum(ArReceipt.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public List<ArReceipt> getArReceipts(Integer pId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArReceipt.FIELD.id.name(),pId));
		return getAll(dc);
	}

	@Override
	public List<ArReceipt> getTransactionPayments(int arTransactionId) {
		DetachedCriteria arReceiptDc = getDetachedCriteria();
		DetachedCriteria receiptTransactionDc = DetachedCriteria.forClass(ArReceiptTransaction.class);
		receiptTransactionDc.setProjection(Projections.property(ArReceiptTransaction.FIELD.arReceiptId.name()));
		receiptTransactionDc.createAlias("arTransaction", "art");
		receiptTransactionDc.createAlias("art.formWorkflow", "tfw");
		receiptTransactionDc.add(Restrictions.eq("art.id", arTransactionId));
		arReceiptDc.add(Subqueries.propertyIn(ArReceipt.FIELD.id.name(), receiptTransactionDc));
		arReceiptDc.createAlias("formWorkflow", "rfw");
		arReceiptDc.add(Restrictions.ne("rfw.currentStatusId", FormStatus.CANCELLED_ID));
		return getAll(arReceiptDc);
	}

	@Override
	public Integer generateSequenceNo(Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null) {
			dc.add(Restrictions.eq(ArReceipt.FIELD.companyId.name(), companyId));
		}
		if(divisionId != null) {
			dc.add(Restrictions.eq(ArReceipt.FIELD.divisionId.name(), divisionId));
		}
		dc.setProjection(Projections.max(ArReceipt.FIELD.sequenceNo.name()));
		List<Object> result = getByProjection(dc);
		if (result == null)
			return 1;
		Object obj = result.iterator().next();
		if (obj == null)
			return 1;
		return ((Integer) obj) + 1;
	}

	@Override
	public List<ArReceipt> getArReceiptsByCapId(Integer capId) {
		DetachedCriteria dc = getDetachedCriteria();
		//AR Receipt Line
		DetachedCriteria arlDc = DetachedCriteria.forClass(ArReceiptLine.class);
		arlDc.setProjection(Projections.property(ArReceiptLine.FIELD.arReceiptId.name()));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.id.name(), arlDc));
		//Object to object
		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ArReceipt.CHILD_TO_CHILD_OR_TYPE_ID));
		arlDc.add(Subqueries.propertyIn(ArReceiptLine.FIELD.ebObjectId.name(), otoDc));
		//Customer advance payment
		DetachedCriteria capDc = DetachedCriteria.forClass(CustomerAdvancePayment.class);
		capDc.setProjection(Projections.property(CustomerAdvancePayment.FIELD.ebObjectId.name()));
		capDc.add(Restrictions.eq(CustomerAdvancePayment.FIELD.id.name(), capId));
		otoDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), capDc));
		//Form workflow
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc);
	}

	@Override
	public List<ArReceipt> geArReceipsByArInvoiceId(Integer arInvoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		//AR Receipt Line
		DetachedCriteria arlDc = DetachedCriteria.forClass(ArReceiptLine.class);
		arlDc.setProjection(Projections.property(ArReceiptLine.FIELD.arReceiptId.name()));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.id.name(), arlDc));
		//Object to object
		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ArReceipt.CHILD_TO_CHILD_OR_TYPE_ID));
		arlDc.add(Subqueries.propertyIn(ArReceiptLine.FIELD.ebObjectId.name(), otoDc));
		//AR Invoice
		DetachedCriteria ariDc = DetachedCriteria.forClass(ArInvoice.class);
		ariDc.setProjection(Projections.property(ArInvoice.FIELD.ebObjectId.name()));
		ariDc.add(Restrictions.eq(ArInvoice.FIELD.id.name(), arInvoiceId));
		otoDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), ariDc));
		//Form workflow
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc);
	}

	@Override
	public List<ArReceipt> getArReceiptsByTrId(Integer arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria arlDc = DetachedCriteria.forClass(ArReceiptLine.class);
		arlDc.setProjection(Projections.property(ArReceiptLine.FIELD.arReceiptId.name()));

		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ArReceipt.CHILD_TO_CHILD_OR_TYPE_ID));
		arlDc.add(Subqueries.propertyIn(ArReceiptLine.FIELD.ebObjectId.name(), otoDc));

		DetachedCriteria trDc = DetachedCriteria.forClass(ArTransaction.class);
		trDc.setProjection(Projections.property(ArTransaction.FIELD.ebObjectId.name()));
		trDc.add(Restrictions.eq(ArTransaction.FIELD.id.name(), arTransactionId));
		otoDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), trDc));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.id.name(), arlDc));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc);
	}
}