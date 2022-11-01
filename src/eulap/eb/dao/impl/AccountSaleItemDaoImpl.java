package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.AccountSaleItemDao;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;

/**
 * Implementing class of {@link AccountSaleItemDao}

 *
 */
public class AccountSaleItemDaoImpl extends BaseDao<AccountSaleItem> implements AccountSaleItemDao {

	@Override
	protected Class<AccountSaleItem> getDomainClass() {
		return AccountSaleItem.class;
	}

	@Override
	public List<AccountSaleItem> getAccountSaleItems(Integer arTransactionId, Integer itemId, Integer warehouseId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc = getDc(arTransactionId, itemId, warehouseId, dc);
		return getAll(dc);
	}

	@Override
	public List<AccountSaleItem> getAccountSaleItems(Integer arTransactionId, Integer itemId,
			Integer warehouseId, boolean isASExchangedItem) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria arTransactionDC = DetachedCriteria.forClass(ArTransaction.class);
		arTransactionDC.setProjection(Projections.property(ArTransaction.FIELD.id.name()));

		if(!isASExchangedItem) {
			arTransactionDC.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(),
					ArTransactionType.TYPE_ACCOUNT_SALE));
		} else {
			//Get only the exchanged items.
			dc.add(Restrictions.gt(AccountSaleItem.FIELD.quantity.name(), 0.0));
			arTransactionDC.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(),
					ArTransactionType.TYPE_SALE_RETURN));
		}
		dc = getDc(arTransactionId, itemId, warehouseId, dc);
		dc.add(Subqueries.propertyIn(AccountSaleItem.FIELD.arTransactionId.name(), arTransactionDC));
		return getAll(dc);
	}

	private DetachedCriteria getDc(Integer arTransactionId, Integer itemId,
			Integer warehouseId, DetachedCriteria acctSaleItemDC) {
		acctSaleItemDC.add(Restrictions.eq(AccountSaleItem.FIELD.arTransactionId.name(), arTransactionId));
		if(itemId != null) {
			acctSaleItemDC.add(Restrictions.eq(AccountSaleItem.FIELD.itemId.name(), itemId));
		}
		if(warehouseId != null) {
			acctSaleItemDC.add(Restrictions.eq(AccountSaleItem.FIELD.warehouseId.name(), warehouseId));
		}
		return acctSaleItemDC;
	}

	@Override
	public double getTotalBalance(Integer arCustomerId, int arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		
		// Subquery for ar transaction.
		DetachedCriteria transactionCriteria = DetachedCriteria.forClass(ArTransaction.class);
		transactionCriteria.setProjection(Projections.property(ArTransaction.FIELD.id.name()));
		transactionCriteria.add(Restrictions.eq(ArTransaction.FIELD.customerId.name(), arCustomerId));
		transactionCriteria.add(Restrictions.le(ArTransaction.FIELD.transactionDate.name(), new Date()));
		if(arTransactionId != 0) {
			transactionCriteria.add(Restrictions.ne(ArTransaction.FIELD.id.name(), arTransactionId));
		}

		// Subquery for workflow.
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		transactionCriteria.add(
				Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		
		dc.add(Subqueries.propertyIn(AccountSaleItem.FIELD.arTransactionId.name(), transactionCriteria));
		dc.setProjection(Projections.sum(AccountSaleItem.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public double getTotalAcctSalesAndReturnItems(Integer companyId,	Integer customerAcctId, Date asOfDate) {
		DetachedCriteria dc = getDetachedCriteria();
		// Subquery for ar customer account.
		DetachedCriteria dcCustAcct = DetachedCriteria.forClass(ArCustomerAccount.class);
		dcCustAcct.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
		dcCustAcct.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
		dcCustAcct.add(Restrictions.eq(ArCustomerAccount.FIELD.id.name(), customerAcctId));
		// Subquery for ar transaction.
		DetachedCriteria transactionCriteria = DetachedCriteria.forClass(ArTransaction.class);
		transactionCriteria.setProjection(Projections.property(ArTransaction.FIELD.id.name()));
		transactionCriteria.add(Restrictions.le(ArTransaction.FIELD.transactionDate.name(), asOfDate));
		transactionCriteria.add(Subqueries.propertyIn(ArTransaction.FIELD.customerAcctId.name(), dcCustAcct));
		// Workflow criteria
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow = getWorkflowDC(dcWorkflow, true);

		transactionCriteria.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(AccountSaleItem.FIELD.arTransactionId.name(), transactionCriteria));
		dc.setProjection(Projections.sum(AccountSaleItem.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	private DetachedCriteria getWorkflowDC(DetachedCriteria workflowDC, boolean isComplete) {
		workflowDC.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		if(isComplete) {
			//Only completed workflows.
			workflowDC.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		}
		workflowDC.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		return workflowDC;
	}

	@Override
	public boolean isReferenceId(List<Integer> acctSaleItemIds) {
		DetachedCriteria acctSaleItemDc = getDetachedCriteria();
		DetachedCriteria arTransactionDC = DetachedCriteria.forClass(ArTransaction.class);
		arTransactionDC.setProjection(Projections.property(ArTransaction.FIELD.id.name()));
		DetachedCriteria workflowDC = DetachedCriteria.forClass(FormWorkflow.class);
		workflowDC = getWorkflowDC(workflowDC, false);
		for (Integer id : acctSaleItemIds) {
			acctSaleItemDc.add(Restrictions.eq(AccountSaleItem.FIELD.refAccountSaleItemId.name(), id));
		}
		arTransactionDC.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), workflowDC));
		acctSaleItemDc.add(Subqueries.propertyIn(AccountSaleItem.FIELD.arTransactionId.name(), arTransactionDC));
		return getAll(acctSaleItemDc).isEmpty() ? false : true;
	}

	@Override
	public List<AccountSaleItem> getSalesReturnItem(int referenceId) {
		DetachedCriteria salesReturnDc = getDetachedCriteria();
		DetachedCriteria arTransactionDC = DetachedCriteria.forClass(ArTransaction.class);
		arTransactionDC.setProjection(Projections.property(ArTransaction.FIELD.id.name()));
		salesReturnDc.add(Restrictions.eq(AccountSaleItem.FIELD.refAccountSaleItemId.name(), referenceId));
		DetachedCriteria workflowDC = DetachedCriteria.forClass(FormWorkflow.class);
		workflowDC = getWorkflowDC(workflowDC, false);
		arTransactionDC.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), workflowDC));
		salesReturnDc.add(Subqueries.propertyIn(AccountSaleItem.FIELD.arTransactionId.name(), arTransactionDC));
		return getAll(salesReturnDc);
	}

	@Override
	public List<AccountSaleItem> getASRItemsByReference(int refAccountSaleId) {
		DetachedCriteria dc = getDetachedCriteria();
		
		DetachedCriteria transDc = DetachedCriteria.forClass(ArTransaction.class);
		transDc.setProjection(Projections.property(CashSaleReturn.FIELD.id.name()));
		transDc.add(Restrictions.eq(ArTransaction.FIELD.transactionTypeId.name(), ArTransactionType.TYPE_SALE_RETURN));
		transDc.add(Restrictions.eq(ArTransaction.FIELD.accountSaleId.name(), refAccountSaleId));
		transDc.createAlias("formWorkflow", "fw");
		transDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		
		dc.add(Subqueries.propertyIn(AccountSaleItem.FIELD.arTransactionId.name(), transDc));
		return getAll(dc);
	}

	@Override
	public double getTotalAcctSaleItems(int arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AccountSaleItem.FIELD.arTransactionId.name(), arTransactionId));
		dc.setProjection(Projections.sum(AccountSaleItem.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public double getRemainingQty(int refAcctSaleItemId) {
		StringBuffer sql = new StringBuffer("SELECT SUM(ORIG_QTY) AS ORIG_QTY, SUM(REMAINING_QTY) AS REMAINING_QTY FROM ( "
				+ "SELECT ASI.QUANTITY AS ORIG_QTY, ASI.QUANTITY AS REMAINING_QTY "
				+ "FROM ACCOUNT_SALE_ITEM ASI "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 AND ASI.ACCOUNT_SALE_ITEM_ID = ? "
				+ "UNION ALL "
				+ "SELECT 0, ASRI.QUANTITY FROM ACCOUNT_SALE_ITEM ASRI "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASRI.AR_TRANSACTION_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 AND ASRI.REF_ACCOUNT_SALE_ITEM_ID = ? "
				+ ") AS AS_REF_QTY ");
		List<Double> remainingQties = getAsRemainingQty(sql.toString(), refAcctSaleItemId);
		return remainingQties.get(1); // Get the value from the REMAINING_QTY column
	}

	private List<Double> getAsRemainingQty(String sql, final int refAcctSaleItemId) {
		Collection<Double> acctSaleQuantities = get(sql, new QueryResultHandler<Double>() {

			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {

					Double origQty = (Double) row[0];
					ret.add(origQty);
					Double remainingQty = (Double) row[1];
					ret.add(remainingQty);
					break; // Expecting one row only.
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				int index = 0;
				query.setParameter(index, refAcctSaleItemId);
				query.setParameter(++index, refAcctSaleItemId);
				return index;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("ORIG_QTY", Hibernate.DOUBLE);
				query.addScalar("REMAINING_QTY", Hibernate.DOUBLE);
			}
		});

		return (List<Double>) acctSaleQuantities;
	}

	@Override
	public double getRemainingQty(Integer saleRefId, Integer itemId, Integer warehouseId) {
		StringBuffer sql = new StringBuffer("SELECT SUM(ORIG_QTY) AS ORIG_QTY, SUM(REMAINING_QTY) AS REMAINING_QTY FROM ( "
				+ "SELECT ASI.QUANTITY AS ORIG_QTY, ASI.QUANTITY AS REMAINING_QTY "
				+ "FROM ACCOUNT_SALE_ITEM ASI "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 AND ASI.AR_TRANSACTION_ID = "+saleRefId+" "
				+ "AND ASI.ITEM_ID = "+itemId+" "
				+ "AND ASI.WAREHOUSE_ID = "+warehouseId+" "
				+ "AND ASI.QUANTITY > 0 "
				+ "UNION ALL "
				+ "SELECT 0 AS ORIG_QTY, ASRI.QUANTITY AS REMAINING_QTY FROM ACCOUNT_SALE_ITEM ASRI "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASRI.AR_TRANSACTION_ID "
				+ "INNER JOIN ACCOUNT_SALE_ITEM ASI ON ASI.ACCOUNT_SALE_ITEM_ID = ASRI.REF_ACCOUNT_SALE_ITEM_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 AND ASI.AR_TRANSACTION_ID = "+saleRefId+" "
				+ "AND ASRI.ITEM_ID = "+itemId+" "
				+ "AND ASRI.WAREHOUSE_ID = "+warehouseId+" "
				+ ") AS AS_REF_QTY ");
		List<Double> remainingQties = getRemainingQty(sql.toString());
		return remainingQties.get(1); // Get the value from the REMAINING_QTY column
	}

	private List<Double> getRemainingQty(String sql) {
		Collection<Double> acctSaleQuantities = get(sql, new QueryResultHandler<Double>() {

			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {

					Double origQty = (Double) row[0];
					ret.add(origQty);
					Double remainingQty = (Double) row[1];
					ret.add(remainingQty);
					break; // Expecting one row only.
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				return -1;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("ORIG_QTY", Hibernate.DOUBLE);
				query.addScalar("REMAINING_QTY", Hibernate.DOUBLE);
			}
		});

		return (List<Double>) acctSaleQuantities;
	}
}
