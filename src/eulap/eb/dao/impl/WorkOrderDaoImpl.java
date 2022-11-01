package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.WorkOrderDao;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * DAO implementation class for {@link WorkOrderDao}

 */

public class WorkOrderDaoImpl extends BaseDao<WorkOrder> implements WorkOrderDao {

	@Override
	protected Class<WorkOrder> getDomainClass() {
		return WorkOrder.class;
	}

	@Override
	public Integer generateSequenceNo(int companyId) {
		return generateSequenceNumber(SalesOrder.FIELD.sequenceNumber.name(),
				SalesOrder.FIELD.companyId.name(), companyId);
	}

	@Override
	public Page<WorkOrder> getWorkOrders(ApprovalSearchParam searchParam, List<Integer> formStatusIds,
			PageSetting pageSetting) {
		HibernateCallback<Page<WorkOrder>> hibernateCallback = new HibernateCallback<Page<WorkOrder>>() {
			@Override
			public Page<WorkOrder> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(WorkOrder.class);
				SearchCommonUtil.searchCommonParams(dc, null, WorkOrder.FIELD.companyId.name(),
						WorkOrder.FIELD.date.name(), WorkOrder.FIELD.date.name(),
						WorkOrder.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				String strCriteria = searchParam.getSearchCriteria();
				if (strCriteria != null && !strCriteria.trim().isEmpty()) {
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ", strCriteria.trim(), Hibernate.STRING));
				}
				// Workflow status
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0) {
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(WorkOrder.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(WorkOrder.FIELD.id.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<WorkOrder> retrieveWorkOrders(String searchCriteria, PageSetting pageSetting) {
		HibernateCallback<Page<WorkOrder>> atwCallBack = new HibernateCallback<Page<WorkOrder>>() {
			@Override
			public Page<WorkOrder> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(WorkOrder.class);
				if (!searchCriteria.isEmpty()) {
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchCriteria.trim(), Hibernate.STRING));
				} 
				return getAll(criteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(atwCallBack);
	}

	@Override
	public Page<WorkOrder> getWorkOrderReferences(Integer companyId, Integer woNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT WORK_ORDER_ID, COMPANY_ID, SEQUENCE_NO, AR_CUSTOMER_ID, "
				+ "AR_CUSTOMER_ACCOUNT_ID, TARGET_END_DATE, WORK_DESCRIPTION FROM WORK_ORDER WO "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID = "+FormStatus.APPROVED_ID+" "
				+ "AND WO.COMPANY_ID = ? "
				+ (woNumber != null ? "AND WO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND WO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND WO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND WO.REFERENCE_WORK_ORDER_ID IS NULL ");
		return getAllAsPage(sql.toString(), pageSetting, new WorkOrderRefHandler(companyId, woNumber, arCustomerId, arCustomerAcctId, 1));
	}

	private static class WorkOrderRefHandler implements QueryResultHandler<WorkOrder> {
		private Integer companyId;
		private Integer woNumber;
		private Integer arCustomerId;
		private Integer arCustomerAcctId;
		private Integer tblCount;

		private WorkOrderRefHandler(Integer companyId, Integer woNumber, Integer arCustomerId,
				Integer arCustomerAcctId, Integer tblCount) {
			this.companyId = companyId;
			this.woNumber = woNumber;
			this.arCustomerId = arCustomerId;
			this.arCustomerAcctId = arCustomerAcctId;
			this.tblCount = tblCount;
		}
	
		@Override
		public List<WorkOrder> convert(List<Object[]> queryResult) {
			List<WorkOrder> salesOrders = new ArrayList<WorkOrder>();
			WorkOrder salesOrder = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				salesOrder = new WorkOrder();
				salesOrder.setId((Integer) row[index]);
				salesOrder.setCompanyId((Integer) row[++index]);
				salesOrder.setSequenceNumber((Integer) row[++index]);
				salesOrder.setArCustomerId((Integer) row[++index]);
				salesOrder.setArCustomerAcctId((Integer) row[++index]);
				salesOrder.setTargetEndDate((Date) row[++index]);
				salesOrder.setWorkDescription((String) row[++index]);
				salesOrders.add(salesOrder);
			}
			return salesOrders;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for(int i=0; i < tblCount; i++) {
				query.setParameter(index, companyId);
				if (woNumber != null) {
					query.setParameter(++index, woNumber);
				}
				if (arCustomerId != null) {
					query.setParameter(++index, arCustomerId);
				}
				if (arCustomerAcctId != null) {
					query.setParameter(++index, arCustomerAcctId);
				}
				if (i < (tblCount-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("WORK_ORDER_ID", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("TARGET_END_DATE", Hibernate.DATE);
			query.addScalar("WORK_DESCRIPTION", Hibernate.STRING);
		}
	}

	@Override
	public List<WorkOrder> getSubWorkOrders(int refWorkOrderId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.isNotNull(WorkOrder.FIELD.refWorkOrderId.name()));
		dc.add(Restrictions.eq(WorkOrder.FIELD.refWorkOrderId.name(), refWorkOrderId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(WorkOrder.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(dc);
	}

	@Override
	public Double getSerialItemQty(Integer soId, Integer itemId) {
		String sql = "SELECT SO.SALES_ORDER_ID, SUM(SOI.QUANTITY) - (SELECT COALESCE(SUM(SI.QUANTITY), 0) "
				+ "FROM WORK_ORDER WO "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = WO.EB_OBJECT_ID "
				+ "INNER JOIN SERIAL_ITEM SI ON SI.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN SALES_ORDER SO1 ON SO1.SALES_ORDER_ID = WO.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = SO1.SALES_ORDER_ID "
				+ "WHERE SO1.SALES_ORDER_ID = ? "
				+ "AND OTO.OR_TYPE_ID = 12011 "
				+ "AND FW1.CURRENT_STATUS_ID != 4 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND SI.ITEM_ID = ?) AS QTY "
				+ "FROM SALES_ORDER SO "
				+ "INNER JOIN SALES_ORDER_ITEM SOI ON SOI.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE SO.SALES_ORDER_ID = ? "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND SOI.ITEM_ID = ? ";
		Session session = null;
		Double quantity = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, soId);
			query.setParameter(1, itemId);
			query.setParameter(2, soId);
			query.setParameter(3, itemId);
			List<Object[]> list = query.list();
			if(list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					quantity = (Double) row[1];
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return quantity;
	}

	@Override
	public Double getPurchasedItemsQty(Integer soId, Integer itemId) {
		String sql = "SELECT SO.SALES_ORDER_ID, SOI.QUANTITY - "
				+ "(SELECT COALESCE(SUM(WOPI.QUANTITY), 0) FROM WORK_ORDER WO "
				+ "INNER JOIN WORK_ORDER_PURCHASED_ITEM WOPI ON WOPI.WORK_ORDER_ID = WO.WORK_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
				+ "INNER JOIN SALES_ORDER SO1 ON SO1.SALES_ORDER_ID = WO.SALES_ORDER_ID "
				+ "WHERE FW1.CURRENT_STATUS_ID != 4 "
				+ "AND SO1.SALES_ORDER_ID = ? "
				+ "AND WOPI.ITEM_ID = ?)  "
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND SO.SALES_ORDER_ID = ? "
				+ "AND SOI.ITEM_ID = ? ";
		Session session = null;
		Double quantity = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, soId);
			query.setParameter(1, itemId);
			query.setParameter(2, soId);
			query.setParameter(3, itemId);
			List<Object[]> list = query.list();
			if(list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					quantity = (Double) row[1];
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return quantity;
	}

	@Override
	public List<WorkOrder> getWoBySalesOrderId(Integer soId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		dc.add(Subqueries.propertyIn(WorkOrder.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Restrictions.eq(WorkOrder.FIELD.salesOrderId.name(), soId));
		return getAll(dc);
	}

	@Override
	public Page<WorkOrder> getMrWoReferences(Integer companyId, Integer woNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT *, SUM(WOI_QTY) AS TOTAL_WOI, SUM(RFI_QTY) AS TOTAL_RFI FROM ( "
				+ "SELECT WO.WORK_ORDER_ID, WO.COMPANY_ID, WO.SEQUENCE_NO, WO.AR_CUSTOMER_ID, "
				+ "WO.AR_CUSTOMER_ACCOUNT_ID, WO.TARGET_END_DATE, WO.WORK_DESCRIPTION, WOI.QUANTITY AS WOI_QTY, 0 AS RFI_QTY "
				+ "FROM WORK_ORDER WO "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
				+ "INNER JOIN WORK_ORDER_ITEM WOI ON WOI.WORK_ORDER_ID = WO.WORK_ORDER_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND FW.CURRENT_STATUS_ID != 1 "
				+ "AND WO.COMPANY_ID = ? "
				+ (woNumber != null ? "AND WO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND WO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND WO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT WO.WORK_ORDER_ID, WO.COMPANY_ID, WO.SEQUENCE_NO, WO.AR_CUSTOMER_ID,  "
				+ "WO.AR_CUSTOMER_ACCOUNT_ID, WO.TARGET_END_DATE, WO.WORK_DESCRIPTION, 0 AS WOI_QTY, RFI.QUANTITY AS RFI_QTY "
				+ "FROM REQUISITION_FORM RF "
				+ "INNER JOIN WORK_ORDER WO ON WO.WORK_ORDER_ID = RF.WORK_ORDER_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = RF.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM_ITEM RFI ON RFI.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = RF.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND RF.REQUISITION_TYPE_ID = 6 "
				+ "AND WO.COMPANY_ID = ? "
				+ (woNumber != null ? "AND WO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND WO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND WO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ ") AS TBL GROUP BY WORK_ORDER_ID ");
			if (statusId == SalesOrder.STATUS_UNUSED) {
				sql.append("HAVING TOTAL_RFI = 0 ");
			} else if (statusId == SalesOrder.STATUS_USED) {
				sql.append("HAVING TOTAL_RFI != 0 && ((TOTAL_WOI - TOTAL_RFI) > 0) ");
			} else {
				sql.append("HAVING TOTAL_RFI = 0 || ((TOTAL_WOI - TOTAL_RFI) > 0) ");
			}
		return getAllAsPage(sql.toString(), pageSetting, new WorkOrderRefHandler(companyId, woNumber, arCustomerId, arCustomerAcctId, 2));
	}

	@Override
	public boolean isUsedByRf(Integer workOrderId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		DetachedCriteria rfDc = DetachedCriteria.forClass(RequisitionForm.class);
		rfDc.setProjection(Projections.property(RequisitionForm.FIELD.workOrderId.name()));
		rfDc.add(Restrictions.eq(RequisitionForm.FIELD.workOrderId.name(), workOrderId));
		rfDc.add(Subqueries.propertyIn(CustomerAdvancePayment.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Subqueries.propertyIn(SalesOrder.FIELD.id.name(), rfDc));
		return getAll(dc).size() > 0;
	}
}