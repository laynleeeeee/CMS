package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArReceiptTransactionDao;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptTransaction;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;

/**
 * Implementing class of {@link ArReceiptTransactionDao}

 *
 */
public class ArReceiptTransactionDaoImpl extends BaseDao<ArReceiptTransaction> implements ArReceiptTransactionDao {
	@Override
	protected Class<ArReceiptTransaction> getDomainClass() {
		return ArReceiptTransaction.class;
	}

	@Override
	public List<ArReceiptTransaction> getArReceiptTransactions(Integer arReceiptId, Integer arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (arReceiptId != null)
			dc.add(Restrictions.eq(ArReceiptTransaction.FIELD.arReceiptId.name(), arReceiptId));
		if (arTransactionId != null)
			dc.add(Restrictions.eq(ArReceiptTransaction.FIELD.arTransactionId.name(), arTransactionId));
		return getAll(dc);
	}

	@Override
	public Double getTotalTransactionAmount (Integer arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArReceiptTransaction.FIELD.arTransactionId.name(), arTransactionId));

		// Receipt workflow.
		DetachedCriteria rWorkflowDc = DetachedCriteria.forClass(FormWorkflow.class);
		rWorkflowDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		rWorkflowDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		// Receipt
		DetachedCriteria receiptDc = DetachedCriteria.forClass(ArReceipt.class);
		receiptDc.setProjection(Projections.property(ArReceipt.FIELD.id.name()));
		receiptDc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), rWorkflowDc));

		dc.add(Subqueries.propertyIn(ArReceiptTransaction.FIELD.arReceiptId.name(), receiptDc));
		dc.setProjection(Projections.sum(ArReceiptTransaction.FIELD.amount.name()));
		List<Object> ret = getByProjection(dc);
		if (ret != null && !ret.isEmpty()) {
			Object retObj = ret.iterator().next();
			if (retObj != null)
				return (Double) retObj; 
		}
		return 0.0;
	}

	@Override
	public List<ArReceiptTransaction> getReceiptTransactions(
			Integer transactionId, Date asOfDate) {
		DetachedCriteria dc = getDetachedCriteria();
		//Subquery for AR Receipt
		DetachedCriteria receiptCriteria = DetachedCriteria.forClass(ArReceipt.class);
		if(asOfDate != null) {
			receiptCriteria.setProjection(Projections.property(ArReceipt.FIELD.id.name()));
			receiptCriteria.add(Restrictions.le(ArReceipt.FIELD.maturityDate.name(), asOfDate));
			dc.add(Subqueries.propertyIn(ArReceiptTransaction.FIELD.arReceiptId.name(), receiptCriteria));
		}
		if(transactionId != null)
			dc.add(Restrictions.eq(ArReceiptTransaction.FIELD.arTransactionId.name(), transactionId));
		return getAll(dc);
	}

	@Override
	public Double getReceiptTransactionAmount(Integer arTransactionId,
			Date trDateFrom, Date trDateTo) {
		DetachedCriteria dc = getDetachedCriteria();
		//AR Transaction
		DetachedCriteria trCriteria = DetachedCriteria.forClass(ArTransaction.class);
		trCriteria.setProjection(Projections.property(ArTransaction.FIELD.id.name()));
		dc.add(Restrictions.eq(ArReceiptTransaction.FIELD.arTransactionId.name(), arTransactionId));
		if(trDateFrom != null && trDateTo != null)
			trCriteria.add(Restrictions.between(ArTransaction.FIELD.transactionDate.name(), trDateFrom, trDateTo));
		else if(trDateFrom != null)
			trCriteria.add(Restrictions.eq(ArTransaction.FIELD.transactionDate.name(), trDateFrom));
		else if(trDateTo != null)
			trCriteria.add(Restrictions.eq(ArTransaction.FIELD.transactionDate.name(), trDateTo));
		// AR Receipt Workflow
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		//AR Receipt
		DetachedCriteria dcReceipt = DetachedCriteria.forClass(ArReceipt.class);
		dcReceipt.setProjection(Projections.property(ArReceipt.FIELD.id.name()));
		if(trDateFrom != null && trDateTo != null)
			dcReceipt.add(Restrictions.between(ArReceipt.FIELD.maturityDate.name(), trDateFrom, trDateTo));
		else if(trDateFrom != null)
			dcReceipt.add(Restrictions.ge(ArReceipt.FIELD.maturityDate.name(), trDateFrom));
		else if(trDateTo != null)
			dcReceipt.add(Restrictions.le(ArReceipt.FIELD.maturityDate.name(), trDateTo));
		dcReceipt.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), dcWorkflow));

		dc.add(Subqueries.propertyIn(ArReceiptTransaction.FIELD.arReceiptId.name(), dcReceipt));
		dc.add(Subqueries.propertyIn(ArReceiptTransaction.FIELD.arTransactionId.name(), trCriteria));
		dc.setProjection(Projections.sum(ArReceiptTransaction.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public double getTotalReceiptPayment(Integer arCustomerAccountId, boolean isEdit) {
		DetachedCriteria dc = getDetachedCriteria();

		// Subquery for receipt workflow.
		DetachedCriteria rWorkflowDc = DetachedCriteria.forClass(FormWorkflow.class);
		rWorkflowDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		rWorkflowDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		if(isEdit){
			rWorkflowDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.RECEIVED_ID));
		}

		// Subquery for receipt
		DetachedCriteria receiptDc = DetachedCriteria.forClass(ArReceipt.class);
		receiptDc.setProjection(Projections.property(ArReceipt.FIELD.id.name()));
		receiptDc.add(Restrictions.eq(ArReceipt.FIELD.arCustomerAccountId.name(), arCustomerAccountId));
		receiptDc.add(Subqueries.propertyIn(ArReceipt.FIELD.formWorkflowId.name(), rWorkflowDc));

		dc.add(Subqueries.propertyIn(ArReceiptTransaction.FIELD.arReceiptId.name(), receiptDc));
		dc.setProjection(Projections.sum(ArReceiptTransaction.FIELD.amount.name()));
		return getBySumProjection(dc);
	}
}
