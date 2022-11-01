package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.DeliveryReceiptType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.SalesQuotation;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.SalesDeliveryEfficiencyDto;
import eulap.eb.web.dto.SalesOrderRegisterDto;
import eulap.eb.web.dto.SalesPoMonitoringDto;
import eulap.eb.web.dto.SalesReportDto;

/**
 * Implementation class for {@link SalesOrder}

 *
 */
public class SalesOrderDaoImpl extends BaseDao<SalesOrder> implements SalesOrderDao {

	@Override
	protected Class<SalesOrder> getDomainClass() {
		return SalesOrder.class;
	}

	@Override
	public Integer generateSequenceNo(int companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(SalesOrder.FIELD.sequenceNumber.name()));
		dc.add(Restrictions.eq(SalesOrder.FIELD.companyId.name(), companyId));
		if(divisionId != null) {
			dc.add(Restrictions.eq(SalesOrder.FIELD.divisionId.name(), divisionId));
		}
		return generateSeqNo(dc);
	}

	@Override
	public Page<SalesOrder> getSalesOrders(int typeId, ApprovalSearchParam searchParam, List<Integer> formStatusIds,
			PageSetting pageSetting) {
		HibernateCallback<Page<SalesOrder>> hibernateCallback = new HibernateCallback<Page<SalesOrder>>() {
			@Override
			public Page<SalesOrder> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(SalesOrder.class);
				if (typeId != 0) {
					dc.add(Restrictions.eq(SalesOrder.FIELD.divisionId.name(), typeId));
				}
				SearchCommonUtil.searchCommonParams(dc, null, SalesOrder.FIELD.companyId.name(),
						SalesOrder.FIELD.date.name(), SalesOrder.FIELD.date.name(),
						SalesOrder.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				String strCriteria = searchParam.getSearchCriteria();
				if (strCriteria != null && !strCriteria.trim().isEmpty()) {
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ", strCriteria.trim(), Hibernate.STRING));
				}
				// Workflow status
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0) {
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(),formStatusIds);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(SalesOrder.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(SalesOrder.FIELD.date.name()));
				dc.addOrder(Order.desc(SalesOrder.FIELD.id.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<SalesOrder> getSaleOrderReferences(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT SALES_ORDER_ID, SEQUENCE_NO, COMPANY_ID, AR_CUSTOMER_ID, "
				+ "AR_CUSTOMER_ACCOUNT_ID, SHIP_TO, SUM(SO_QTY) AS TOTAL_SO_QTY, SUM(ATW_QTY) AS TOTAL_ATW_QTY FROM ( "
				+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
				+ "SOI.QUANTITY AS SO_QTY, 0 AS ATW_QTY "
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.DEPOSIT = 0 "
				+ "AND SO.COMPANY_ID = ? "
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND SOI.SALES_ORDER_ID IN (SELECT SOL.SALES_ORDER_ID FROM SALES_ORDER_LINE SOL "
				+ "WHERE SOL.SALES_ORDER_ID IN (SELECT WO.SALES_ORDER_ID FROM WORK_ORDER WO "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1)) "
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
				+ "SOI.QUANTITY AS SO_QTY, 0 AS ATW_QTY "
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.DEPOSIT = 1 "
				+ "AND SO.COMPANY_ID = ? "
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND SO.SALES_ORDER_ID IN (SELECT CAP.SALES_ORDER_ID FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4) "
				+ "AND SOI.SALES_ORDER_ID IN (SELECT SOL.SALES_ORDER_ID FROM SALES_ORDER_LINE SOL "
				+ "WHERE SOL.SALES_ORDER_ID IN (SELECT WO.SALES_ORDER_ID FROM WORK_ORDER WO "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1)) "
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
				+ "SOI.QUANTITY AS SO_QTY, 0 AS ATW_QTY "
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.DEPOSIT = 0 "
				+ "AND SO.COMPANY_ID = ? "
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND SOI.SALES_ORDER_ID NOT IN (SELECT SOL.SALES_ORDER_ID FROM SALES_ORDER_LINE SOL "
				+ "WHERE SOL.SALES_ORDER_ID NOT IN (SELECT WO.SALES_ORDER_ID FROM WORK_ORDER WO "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1)) "
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
				+ "SOI.QUANTITY AS SO_QTY, 0 AS ATW_QTY "
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.DEPOSIT = 1 "
				+ "AND SO.COMPANY_ID = ? "
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND SO.SALES_ORDER_ID IN (SELECT CAP.SALES_ORDER_ID FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4) "
				+ "AND SOI.SALES_ORDER_ID NOT IN (SELECT SOL.SALES_ORDER_ID FROM SALES_ORDER_LINE SOL "
				+ "WHERE SOL.SALES_ORDER_ID NOT IN (SELECT WO.SALES_ORDER_ID FROM WORK_ORDER WO "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1)) "
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
				+ "0 AS SO_QTY, ATWI.QUANTITY AS ATW_QTY "
				+ "FROM AUTHORITY_TO_WITHDRAW_ITEM ATWI "
				+ "INNER JOIN AUTHORITY_TO_WITHDRAW ATW ON ATW.AUTHORITY_TO_WITHDRAW_ID = ATWI.AUTHORITY_TO_WITHDRAW_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = ATW.SALES_ORDER_ID "
				+ "INNER JOIN SALES_ORDER_ITEM SOI ON SOI.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = SOI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATW.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.TO_OBJECT_ID = ATWI.EB_OBJECT_ID "
				+ "AND OTO.OR_TYPE_ID = 12001 "
				+ "AND ATW.COMPANY_ID = ? "
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND ATW.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND ATW.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
				+ "0 AS SO_QTY, SI.QUANTITY AS ATW_QTY "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AUTHORITY_TO_WITHDRAW ATW ON ATW.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = ATW.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATW.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 12000 "
				+ "AND ATW.COMPANY_ID = ? "
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND ATW.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND ATW.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
				+ "0 AS SO_QTY, 0 AS ATW_QTY  "
				+ "FROM SALES_ORDER_LINE SOL "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOL.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.COMPANY_ID = ? "
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND SO.SALES_ORDER_ID IN (SELECT WO.SALES_ORDER_ID FROM WORK_ORDER WO "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1) "
				+ "AND SO.SALES_ORDER_ID NOT IN (SELECT ATW.SALES_ORDER_ID FROM AUTHORITY_TO_WITHDRAW ATW "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATW.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4) "
				+ ") AS REF_TABLE "
				+ "GROUP BY SALES_ORDER_ID ");
		if (statusId == SalesOrder.STATUS_UNUSED) {
			sql.append("HAVING TOTAL_ATW_QTY = 0 ");
		} else if(statusId == SalesOrder.STATUS_USED) {
			sql.append("HAVING TOTAL_ATW_QTY != 0 && ((TOTAL_SO_QTY - TOTAL_ATW_QTY) > 0) ");
		} else {
			sql.append("HAVING TOTAL_ATW_QTY = 0 || ((TOTAL_SO_QTY - TOTAL_ATW_QTY) > 0) ");
		}
		return getAllAsPage(sql.toString(), pageSetting, new SalesOrderRefHandler(companyId, soNumber, arCustomerId, arCustomerAcctId, 7));
	}

	private static class SalesOrderRefHandler implements QueryResultHandler<SalesOrder> {
		private Integer companyId;
		private Integer soNumber;
		private Integer arCustomerId;
		private Integer arCustomerAcctId;
		private Integer numberOfTbls;

		private SalesOrderRefHandler(Integer companyId, Integer soNumber, Integer arCustomerId,
				Integer arCustomerAcctId, Integer numberOfTbls) {
			this.companyId = companyId;
			this.soNumber = soNumber;
			this.arCustomerId = arCustomerId;
			this.arCustomerAcctId = arCustomerAcctId;
			this.numberOfTbls = numberOfTbls;
		}

		@Override
		public List<SalesOrder> convert(List<Object[]> queryResult) {
			List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
			SalesOrder salesOrder = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				salesOrder = new SalesOrder();
				salesOrder.setId((Integer) row[index]);
				salesOrder.setSequenceNumber((Integer) row[++index]);
				salesOrder.setCompanyId((Integer) row[++index]);
				salesOrder.setArCustomerId((Integer) row[++index]);
				salesOrder.setArCustomerAcctId((Integer) row[++index]);
				salesOrder.setShipTo((String) row[++index]);
				salesOrders.add(salesOrder);
			}
			return salesOrders;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0; i < numberOfTbls; i++) {
				query.setParameter(index, companyId);
				if (soNumber != null) {
					query.setParameter(++index, soNumber);
				}
				if (arCustomerId != null) {
					query.setParameter(++index, arCustomerId);
				}
				if (arCustomerAcctId != null) {
					query.setParameter(++index, arCustomerAcctId);
				}
				if (i < (numberOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SALES_ORDER_ID", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("SHIP_TO", Hibernate.STRING);
		}
	}

	@Override
	public Page<SalesOrder> getSOForms(int typeId, String searchCriteria, PageSetting pageSetting) {
		HibernateCallback<Page<SalesOrder>> sqhCallBack = new HibernateCallback<Page<SalesOrder>>() {
			@Override
			public Page<SalesOrder> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(SalesOrder.class);
				if (!searchCriteria.isEmpty()) {
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ", searchCriteria.trim(), Hibernate.STRING));
				}
				criteria.add(Restrictions.eq(SalesOrder.FIELD.divisionId.name(), typeId));
				return getAll(criteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(sqhCallBack);
	}

	@Override
	public Page<SalesOrder> showDepositSOForms(String seqNo, Integer companyId, PageSetting pageSetting) {
		String sql = "SELECT * FROM (SELECT SALES_ORDER_ID, SEQUENCE_NO, COMPANY_ID, AR_CUSTOMER_ID, "
				+ "AR_CUSTOMER_ACCOUNT_ID, SUM(SO_ADV_PYMNT_BALANCE) AS ADV_PYMNT_BALANCE FROM ( "
				+ "SELECT SO.SALES_ORDER_ID AS SALES_ORDER_ID, SO.SEQUENCE_NO AS SEQUENCE_NO, SO.COMPANY_ID AS COMPANY_ID, "
				+ "SO.AR_CUSTOMER_ID AS AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID AS AR_CUSTOMER_ACCOUNT_ID, "
				+ "SO.AMOUNT AS SO_ADV_PYMNT_BALANCE "
				+ "FROM SALES_ORDER SO "
				+ "INNER JOIN COMPANY C ON C.COMPANY_ID = SO.COMPANY_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.DEPOSIT = 1 "
				+ "AND SO.COMPANY_ID = ? "
				+ "AND SO.SEQUENCE_NO LIKE ? "
				+ "AND SO.SALES_ORDER_ID NOT IN ("
				+ "SELECT DR.SALES_ORDER_ID FROM DELIVERY_RECEIPT DR "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4) "
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID AS SALES_ORDER_ID, SO.SEQUENCE_NO AS SEQUENCE_NO, SO.COMPANY_ID AS COMPANY_ID, "
				+ "SO.AR_CUSTOMER_ID AS AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID AS AR_CUSTOMER_ACCOUNT_ID, "
				+ "-CAP.CASH AS SO_ADV_PYMNT_BALANCE "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = CAP.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND CAP.COMPANY_ID = ? "
				+ "AND SO.SEQUENCE_NO LIKE ? "
				+ "AND SO.SALES_ORDER_ID NOT IN ("
				+ "SELECT DR.SALES_ORDER_ID FROM DELIVERY_RECEIPT DR "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4) "
				+ ") AS INNER_TBL GROUP BY SALES_ORDER_ID "
				+ ") AS SO_REF_TBL WHERE ADV_PYMNT_BALANCE > 0 ";
		DepositSalesOrderHandler handler = new DepositSalesOrderHandler(seqNo, companyId, 2);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class DepositSalesOrderHandler implements QueryResultHandler<SalesOrder> {
		private Integer companyId;
		private String seqNo;
		private Integer noOfTbls;

		private DepositSalesOrderHandler(String seqNo, Integer companyId, Integer noOfTbls) {
			this.companyId = companyId;
			this.seqNo = seqNo;
			this.noOfTbls = noOfTbls;
		}

		@Override
		public List<SalesOrder> convert(List<Object[]> queryResult) {
			List<SalesOrder> depositSOs = new ArrayList<>();
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				SalesOrder depositSO = new SalesOrder();
				depositSO.setId((Integer) rowResult[colNum++]);
				depositSO.setSequenceNumber((Integer) rowResult[colNum++]);
				depositSO.setCompanyId((Integer) rowResult[colNum++]);
				depositSO.setArCustomerId((Integer) rowResult[colNum++]);
				depositSO.setArCustomerAcctId((Integer) rowResult[colNum++]);
				depositSOs.add(depositSO);
			}
			return depositSOs;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, companyId);
				query.setParameter(++index, "%"+seqNo+"%");
				if (i < (noOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SALES_ORDER_ID", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ACCOUNT_ID", Hibernate.INTEGER);
		}

	}

	@Override
	public SalesOrder getBySeqNo(Integer seqNo, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SalesOrder.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(SalesOrder.FIELD.sequenceNumber.name(), seqNo));
		return get(dc);
	}

	@Override
	public boolean isUsedByCAP(Integer soId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		DetachedCriteria capDc = DetachedCriteria.forClass(CustomerAdvancePayment.class);
		capDc.setProjection(Projections.property(CustomerAdvancePayment.FIELD.salesOrderId.name()));
		capDc.add(Restrictions.eq(CustomerAdvancePayment.FIELD.salesOrderId.name(), soId));
		capDc.add(Subqueries.propertyIn(CustomerAdvancePayment.FIELD.formWorkflowId.name(), fwDc));

		dc.add(Subqueries.propertyIn(SalesOrder.FIELD.id.name(), capDc));
		return getAll(dc).size() > 0;
	}

	@Override
	public boolean isUsedByWo(Integer soId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		DetachedCriteria woDc = DetachedCriteria.forClass(WorkOrder.class);
		woDc.setProjection(Projections.property(WorkOrder.FIELD.salesOrderId.name()));
		woDc.add(Restrictions.eq(WorkOrder.FIELD.salesOrderId.name(), soId));
		woDc.add(Subqueries.propertyIn(WorkOrder.FIELD.formWorkflowId.name(), fwDc));

		dc.add(Subqueries.propertyIn(SalesOrder.FIELD.id.name(), woDc));
		return getAll(dc).size() > 0;
	}

	@Override
	public List<SalesOrder> getSOsBySalesOrderId(Integer sqId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		dc.add(Subqueries.propertyIn(SalesOrder.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Restrictions.eq(SalesOrder.FIELD.salesQuotationId.name(), sqId));
		return getAll(dc);
	}

	@Override
	public Page<SalesOrder> getSOServiceReferences(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, "
			+ "SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO "
			+ "FROM SALES_ORDER_LINE SOL "
			+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOL.SALES_ORDER_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID  = SO.FORM_WORKFLOW_ID "
			+ "WHERE FW.IS_COMPLETE = 1 "
			+ "AND SO.DEPOSIT = 0 "
			+ "AND SO.COMPANY_ID = ? "
			+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
			+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
			+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
			+ "AND SO.SALES_ORDER_ID NOT IN (SELECT WO.SALES_ORDER_ID FROM WORK_ORDER WO "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
			+ "WHERE FW.CURRENT_STATUS_ID != 4) "
			+ "GROUP BY SO.SALES_ORDER_ID "
			+ "UNION ALL "
			+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, "
			+ "SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO "
			+ "FROM SALES_ORDER_LINE SOL "
			+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOL.SALES_ORDER_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID  = SO.FORM_WORKFLOW_ID "
			+ "WHERE FW.IS_COMPLETE = 1 "
			+ "AND SO.DEPOSIT = 1 "
			+ "AND SO.COMPANY_ID = ? "
			+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
			+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
			+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
			+ "AND SO.SALES_ORDER_ID IN (SELECT CAP.SALES_ORDER_ID FROM CUSTOMER_ADVANCE_PAYMENT CAP  "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID  "
			+ "WHERE FW.CURRENT_STATUS_ID != 4)  "
			+ "AND SO.SALES_ORDER_ID NOT IN (SELECT WO.SALES_ORDER_ID FROM WORK_ORDER WO "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
			+ "WHERE FW.CURRENT_STATUS_ID != 4) "
			+ "GROUP BY SO.SALES_ORDER_ID ");
		return getAllAsPage(sql.toString(), pageSetting, new SalesOrderRefHandler(companyId, soNumber, arCustomerId, arCustomerAcctId, 2));
	}

	@Override
	public Page<SalesOrder> getSOTruckingReferences(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, PageSetting pageSetting, Integer typeId) {
		String formTbl = typeId.equals(DeliveryReceiptType.WAYBILL_DR_TYPE_ID)
				? "SALES_ORDER_TRUCKING_LINE" : "SALES_ORDER_EQUIPMENT_LINE";
		StringBuilder sql = new StringBuilder("SELECT * FROM ( "
				+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, "
				+ "SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO "
				+ "FROM "+ formTbl +" SOTL "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOTL.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID  = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.DEPOSIT = 0 "
				+ "AND SO.COMPANY_ID = ? "
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND SO.SALES_ORDER_ID NOT IN (SELECT DR.SALES_ORDER_ID FROM DELIVERY_RECEIPT DR "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND DR.DELIVERY_RECEIPT_TYPE_ID = "+typeId+") "
				+ "GROUP BY SO.SALES_ORDER_ID "
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, "
				+ "SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO "
				+ "FROM "+ formTbl +" SOTL "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOTL.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID  = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.DEPOSIT = 1 "
				+ "AND SO.COMPANY_ID = ? "
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ "AND SO.SALES_ORDER_ID IN (SELECT CAP.SALES_ORDER_ID FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4) "
				+ "AND SO.SALES_ORDER_ID NOT IN (SELECT DR.SALES_ORDER_ID FROM DELIVERY_RECEIPT DR "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND DR.DELIVERY_RECEIPT_TYPE_ID = "+typeId+") "
				+ "GROUP BY SO.SALES_ORDER_ID "
				+ ") AS TBL ORDER BY SEQUENCE_NO DESC ");
		return getAllAsPage(sql.toString(), pageSetting, new SalesOrderRefHandler(companyId, soNumber, arCustomerId, arCustomerAcctId, 2));
	}

	@Override
	public boolean isUsedByDr(Integer soId, Integer drTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		DetachedCriteria wbDc = DetachedCriteria.forClass(DeliveryReceipt.class);
		wbDc.setProjection(Projections.property(DeliveryReceipt.FIELD.salesOrderId.name()));
		wbDc.add(Restrictions.eq(DeliveryReceipt.FIELD.salesOrderId.name(), soId));
		wbDc.add(Restrictions.eq(DeliveryReceipt.FIELD.deliveryReceiptTypeId.name(), drTypeId));
		wbDc.add(Subqueries.propertyIn(CustomerAdvancePayment.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Subqueries.propertyIn(SalesOrder.FIELD.id.name(), wbDc));
		return getAll(dc).size() > 0;
	}

	@Override
	public String getCustomerShipTo(Integer arCustomerId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria wdc = DetachedCriteria.forClass(FormWorkflow.class);
		wdc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		wdc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(SalesQuotation.FIELD.formWorkflowId.name(), wdc));
		dc.add(Restrictions.eq(SalesQuotation.FIELD.arCustomerId.name(), arCustomerId));
		dc.addOrder(Order.desc(SalesQuotation.FIELD.createdDate.name()));
		dc.getExecutableCriteria(getSession()).setMaxResults(1);
		return get(dc) != null ? get(dc).getShipTo() : null;
	}

	@Override
	public Page<SalesOrder> getDrSalesOrders(Integer companyId, Integer arCustomerId, Integer arCustomerAcctId,
			Integer soNumber, Integer statusId, Date dateFrom, Date dateTo, PageSetting pageSetting,
			Integer drTypeId, String poNumber, Integer divisionId) {
		StringBuilder sql = new StringBuilder("SELECT SALES_ORDER_ID, SEQUENCE_NO, COMPANY_ID, AR_CUSTOMER_ID, "
				+ "AR_CUSTOMER_ACCOUNT_ID, SHIP_TO, SUM(SO_QTY) AS TOTAL_SO_QTY, SUM(DR_QTY) AS TOTAL_DR_QTY, "
				+ "PO_NUMBER, DATE, REMARKS FROM ( ");
		int noOfTbls = 0;
		if (drTypeId >= DeliveryReceiptType.DR_CENTRAL_TYPE_ID) {
			sql.append("SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
					+ "SOI.QUANTITY AS SO_QTY, 0 AS DR_QTY, SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS DATE, SO.REMARKS AS REMARKS "
					+ "FROM SALES_ORDER_ITEM SOI "
					+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
					+ "WHERE FW.IS_COMPLETE = 1 "
					+ "AND SO.COMPANY_ID = ? "
					+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
					+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
					+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
					+ (dateFrom != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
					+ (poNumber != null ? "AND SO.PO_NUMBER LIKE ? " : "")
					+ (divisionId != null ? "AND SO.DIVISION_ID = ? " : "")
					+ "UNION ALL "
					+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
					+ "0 AS SO_QTY, DRI.QUANTITY AS DR_QTY, SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS DATE, SO.REMARKS AS REMARKS "
					+ "FROM DELIVERY_RECEIPT_ITEM DRI "
					+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRI.DELIVERY_RECEIPT_ID "
					+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
					+ "INNER JOIN SALES_ORDER_ITEM SOI ON SOI.SALES_ORDER_ID = SO.SALES_ORDER_ID "
					+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = SOI.EB_OBJECT_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 "
					+ "AND OTO.TO_OBJECT_ID = DRI.EB_OBJECT_ID "
					+ "AND OTO.OR_TYPE_ID = 12005 "
					+ "AND DR.COMPANY_ID = ? "
					+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
					+ (arCustomerId != null ? "AND DR.AR_CUSTOMER_ID = ? " : "")
					+ (arCustomerAcctId != null ? "AND DR.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
					+ (dateFrom != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
					+ (poNumber != null ? "AND SO.PO_NUMBER LIKE ? " : "")
					+ (divisionId != null ? "AND DR.DIVISION_ID = ? " : "")
					+ "UNION ALL "
					+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
					+ "0 AS SO_QTY, SI.QUANTITY AS DR_QTY, SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS DATE, SO.REMARKS AS REMARKS "
					+ "FROM SERIAL_ITEM SI "
					+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
					+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
					+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 "
					+ "AND OTO.OR_TYPE_ID = 12004 "
					+ "AND DR.COMPANY_ID = ? "
					+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
					+ (arCustomerId != null ? "AND DR.AR_CUSTOMER_ID = ? " : "")
					+ (arCustomerAcctId != null ? "AND DR.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
					+ (dateFrom != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
					+ (poNumber != null ? "AND SO.PO_NUMBER LIKE ? " : "")
					+ (divisionId != null ? "AND DR.DIVISION_ID = ? " : "")
					+ "UNION ALL "
					+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, "
					+ "SO.SHIP_TO, SOL.QUANTITY AS SO_QTY, 0 AS DR_QTY, SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS DATE, SO.REMARKS AS REMARKS "
					+ "FROM SALES_ORDER_LINE SOL  "
					+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOL.SALES_ORDER_ID  "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID  "
					+ "WHERE FW.IS_COMPLETE = 1 AND SO.COMPANY_ID = 1 "
					+ "AND SO.SALES_ORDER_ID NOT IN ( "
					+ "SELECT DR.SALES_ORDER_ID  "
					+ "FROM DELIVERY_RECEIPT DR  "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID  "
					+ "WHERE FW.CURRENT_STATUS_ID != 4  "
					+ "AND DR.DELIVERY_RECEIPT_TYPE_ID = 4 ) "
					+ "AND SO.COMPANY_ID = ? "
					+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
					+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
					+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
					+ (dateFrom != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
					+ (poNumber != null ? "AND SO.PO_NUMBER LIKE ? " : "")
					+ (divisionId != null ? "AND SO.DIVISION_ID = ? " : "")
					+ "UNION ALL "
					+ "SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, "
					+ "SO.SHIP_TO, 0 AS SO_QTY, DRL.QUANTITY AS DR_QTY, SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS DATE, SO.REMARKS AS REMARKS "
					+ "FROM DELIVERY_RECEIPT_LINE DRL "
					+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRL.DELIVERY_RECEIPT_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
					+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 "
					+ "AND DR.COMPANY_ID = ? "
					+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
					+ (arCustomerId != null ? "AND DR.AR_CUSTOMER_ID = ? " : "")
					+ (arCustomerAcctId != null ? "AND DR.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
					+ (dateFrom != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
					+ (poNumber != null ? "AND SO.PO_NUMBER LIKE ? " : "")
					+ (divisionId != null ? "AND DR.DIVISION_ID = ? " : "")
					+ ") AS REF_TABLE GROUP BY SALES_ORDER_ID ");
			if (statusId == SalesOrder.STATUS_UNUSED) {
				sql.append("HAVING TOTAL_DR_QTY = 0 ");
			} else if(statusId == SalesOrder.STATUS_USED) {
				sql.append("HAVING TOTAL_DR_QTY != 0 && ((TOTAL_SO_QTY - TOTAL_DR_QTY) > 0) ");
			} else {
				sql.append("HAVING TOTAL_DR_QTY = 0 || ((TOTAL_SO_QTY - TOTAL_DR_QTY) > 0) ");
			}
			//Filter only SO, if SO requries deposit, then it should have a valid CAP transaction.
			sql.append("AND SALES_ORDER_ID NOT IN ( "
					+ "SELECT SO.SALES_ORDER_ID FROM SALES_ORDER SO "
					+ "WHERE SO.DEPOSIT = 1 "
					+ "AND SO.SALES_ORDER_ID NOT IN ( "
					+ "SELECT CAP.SALES_ORDER_ID FROM CUSTOMER_ADVANCE_PAYMENT CAP "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4)) ");
			noOfTbls = 5;
		} else {
			sql.append("SELECT SO.SALES_ORDER_ID, SO.SEQUENCE_NO, SO.COMPANY_ID, SO.AR_CUSTOMER_ID, SO.AR_CUSTOMER_ACCOUNT_ID, SO.SHIP_TO, "
					+ "0 AS SO_QTY, 0 AS DR_QTY  "
					+ "FROM SALES_ORDER_LINE SOL "
					+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOL.SALES_ORDER_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
					+ "WHERE FW.IS_COMPLETE = 1 "
					+ "AND SO.COMPANY_ID = ? "
					+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
					+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
					+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
					+ "AND SO.SALES_ORDER_ID NOT IN (SELECT DR.SALES_ORDER_ID FROM DELIVERY_RECEIPT DR "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 "
					+ "AND DR.DELIVERY_RECEIPT_TYPE_ID = 4) "
					+ ") AS REF_TABLE GROUP BY SALES_ORDER_ID ");
			noOfTbls = 1;
		}
		sql.append("ORDER BY DATE DESC, SEQUENCE_NO DESC ");
		return getAllAsPage(sql.toString(), pageSetting, new SalesOrderReferenceHandler(companyId, soNumber, arCustomerId,
				arCustomerAcctId, dateFrom, dateTo, poNumber, divisionId, noOfTbls));
	}

	private static class SalesOrderReferenceHandler implements QueryResultHandler<SalesOrder> {
		private Integer companyId;
		private Integer soNumber;
		private Integer arCustomerId;
		private Integer arCustomerAcctId;
		private Integer numberOfTbls;
		private Date dateFrom;
		private Date dateTo;
		private String poNumber;
		private Integer divisionId;

		private SalesOrderReferenceHandler(Integer companyId, Integer soNumber, Integer arCustomerId,
				Integer arCustomerAcctId, Date dateFrom, Date dateTo, String poNumber, Integer divisionId, Integer numberOfTbls) {
			this.companyId = companyId;
			this.soNumber = soNumber;
			this.arCustomerId = arCustomerId;
			this.arCustomerAcctId = arCustomerAcctId;
			this.numberOfTbls = numberOfTbls;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.poNumber = poNumber;
			this.divisionId = divisionId;
		}

		@Override
		public List<SalesOrder> convert(List<Object[]> queryResult) {
			List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
			SalesOrder salesOrder = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				salesOrder = new SalesOrder();
				salesOrder.setId((Integer) row[index]);
				salesOrder.setSequenceNumber((Integer) row[++index]);
				salesOrder.setCompanyId((Integer) row[++index]);
				salesOrder.setArCustomerId((Integer) row[++index]);
				salesOrder.setArCustomerAcctId((Integer) row[++index]);
				salesOrder.setShipTo((String) row[++index]);
				salesOrder.setPoNumber((String) row[++index]);
				salesOrder.setDate((Date) row[++index]);
				salesOrder.setRemarks((String) row[++index]);
				salesOrders.add(salesOrder);
			}
			return salesOrders;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0; i < numberOfTbls; i++) {
				query.setParameter(index, companyId);
				if (soNumber != null) {
					query.setParameter(++index, soNumber);
				}
				if (arCustomerId != null) {
					query.setParameter(++index, arCustomerId);
				}
				if (arCustomerAcctId != null) {
					query.setParameter(++index, arCustomerAcctId);
				}
				if(dateFrom != null && dateTo != null) {
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				}
				if(poNumber != null) {
					query.setParameter(++index, StringFormatUtil.appendWildCard(poNumber));
				}
				if(divisionId != null) {
					query.setParameter(++index, divisionId);
				}
				if (i < (numberOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SALES_ORDER_ID", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("SHIP_TO", Hibernate.STRING);
			query.addScalar("PO_NUMBER", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public SalesOrder getSoByDr(Integer drId) {
		DetachedCriteria dc = getDetachedCriteria();
		//Delivery receipt.
		DetachedCriteria drDc = DetachedCriteria.forClass(DeliveryReceipt.class);
		drDc.add(Restrictions.eq(DeliveryReceipt.FIELD.id.name(), drId));
		drDc.setProjection(Projections.property(DeliveryReceipt.FIELD.salesOrderId.name()));
		dc.add(Subqueries.propertyIn(SalesOrder.FIELD.id.name(), drDc));
		return get(dc);
	}

	@Override
	public Page<SalesOrder> getCapSalesOrders(Integer companyId, Integer arCustomerId, Integer arCustomerAcctId,
			Integer soNumber, Integer statusId, Date dateFrom, Date dateTo, PageSetting pageSetting,
			String poNumber, Integer divisionId) {
		StringBuilder sql = new StringBuilder("SELECT SALES_ORDER_ID, EB_OBJECT_ID, DATE, SEQUENCE_NO, PO_NUMBER, "
				+ "AR_CUSTOMER_ID, REMARKS, SUM(SO_AMOUNT) AS TOTAL_SO_AMOUNT, SUM(CAP_AMOUNT) AS TOTAL_CAP_AMOUNT "
				+ "FROM ("
				+ "SELECT SO.SALES_ORDER_ID, SO.EB_OBJECT_ID, "
				+ "SO.DATE, SO.SEQUENCE_NO, SO.PO_NUMBER, "
				+ "SO.AR_CUSTOMER_ID, SO.REMARKS, SO.ADVANCE_PAYMENT AS SO_AMOUNT, 0 AS CAP_AMOUNT "
				+ "FROM SALES_ORDER SO "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.DEPOSIT = ? "
				+ (companyId != null ? "AND SO.COMPANY_ID = ? " : "")
				+ (divisionId != null ? "AND SO.DIVISION_ID = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (poNumber != null && poNumber != "" ? "AND SO.PO_NUMBER = ? " : "")
				+ (dateTo != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID, SO.EB_OBJECT_ID, SO.DATE, SO.SEQUENCE_NO, SO.PO_NUMBER, "
				+ "SO.AR_CUSTOMER_ID, SO.REMARKS, 0 AS SO_AMOUNT, SUM(COALESCE(CAP.AMOUNT, 0)) CAP_AMOUNT "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = CAP.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SO.DEPOSIT = ? "
				+ (companyId != null ? "AND SO.COMPANY_ID = ? " : "")
				+ (divisionId != null ? "AND SO.DIVISION_ID = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (poNumber != null && poNumber != "" ? "AND SO.PO_NUMBER = ? " : "")
				+ (dateTo != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
				+ "GROUP BY SO.SALES_ORDER_ID "
				+ ") AS TBL "
				+ "WHERE SALES_ORDER_ID NOT IN ( "
				+ "SELECT SO.SALES_ORDER_ID "
				+ "FROM SALES_ORDER SO "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ARI.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.EB_OBJECT_ID =  OTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "GROUP BY SO.SALES_ORDER_ID "
				+ "HAVING SUM(SO.AMOUNT) = SUM(ARL.AMOUNT)) "
				+ "GROUP BY SALES_ORDER_ID ");
				if(statusId == CustomerAdvancePayment.STATUS_UNUSED) {
					sql.append("HAVING TOTAL_CAP_AMOUNT = 0 ");
				} else if (statusId == CustomerAdvancePayment.STATUS_USED) {
					sql.append("HAVING TOTAL_SO_AMOUNT - TOTAL_CAP_AMOUNT > 0 AND TOTAL_CAP_AMOUNT > 0 ");
				} else {
					sql.append("HAVING TOTAL_SO_AMOUNT - TOTAL_CAP_AMOUNT > 0 ");
				}
				sql.append("ORDER BY DATE DESC, SEQUENCE_NO DESC ");
		Integer numberOfTbls = 2;
		return getAllAsPage(sql.toString(), pageSetting, new SoCapReferenceHandler(companyId, arCustomerId, 
				arCustomerAcctId, soNumber, statusId, dateFrom, dateTo, numberOfTbls, poNumber, divisionId));
	}

	private static class SoCapReferenceHandler implements QueryResultHandler<SalesOrder> {
		private Integer companyId;
		private Integer soNumber;
		private Integer arCustomerId;
		private Integer arCustomerAcctId;
		private Integer numberOfTbls;
		private Date dateFrom;
		private Date dateTo;
		private String poNumber;
		private Integer divisionId;

		private SoCapReferenceHandler(Integer companyId, Integer arCustomerId, Integer arCustomerAcctId,
				Integer soNumber, Integer statusId, Date dateFrom, Date dateTo, Integer numberOfTbls,
				String poNumber, Integer divisionId) {
			this.companyId = companyId;
			this.soNumber = soNumber;
			this.arCustomerId = arCustomerId;
			this.arCustomerAcctId = arCustomerAcctId;
			this.numberOfTbls = numberOfTbls;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.poNumber = poNumber;
			this.divisionId = divisionId;
		}

		@Override
		public List<SalesOrder> convert(List<Object[]> queryResult) {
			List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
			SalesOrder salesOrder = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				salesOrder = new SalesOrder();
				if(row[index] != null) {
					salesOrder.setId((Integer) row[index]);
					salesOrder.setEbObjectId((Integer) row[++index]);
					salesOrder.setDate((Date) row[++index]);
					salesOrder.setSequenceNumber((Integer) row[++index]);
					salesOrder.setPoNumber((String) row[++index]);
					salesOrder.setArCustomerId((Integer) row[++index]);
					salesOrder.setRemarks((String) row[++index]);
					salesOrders.add(salesOrder);
				}
			}
			return salesOrders;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0; i < numberOfTbls; i++) {
				query.setParameter(index, 1);
				if (companyId != null) {
					query.setParameter(++index, companyId);
				}
				if(divisionId != null) {
					query.setParameter(++index, divisionId);
				}
				if (arCustomerId != null) {
					query.setParameter(++index, arCustomerId);
				}
				if (arCustomerAcctId != null) {
					query.setParameter(++index, arCustomerAcctId);
				}
				if (soNumber != null) {
					query.setParameter(++index, soNumber);
				}
				if(poNumber != null  && poNumber != "") {
					query.setParameter(++index, poNumber);
				}
				if(dateFrom != null && dateTo != null) {
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				}
				if (i < (numberOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SALES_ORDER_ID", Hibernate.INTEGER);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("PO_NUMBER", Hibernate.STRING);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public Page<SalesOrder> getProjectRetentionSalesOrders(Integer companyId, Integer divisionId, Integer arCustomerId,
			Integer arCustomerAcctId, Integer soNumber, String poNumber, Date dateFrom, Date dateTo, Integer statusId,
			PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT SALES_ORDER_ID, OBJECT_ID, DATE, SEQUENCE_NO, PO_NUMBER, AR_CUSTOMER_ID,REMARKS, "
				+ "SUM(RETENTION) AS TOTAL_RETENTION, SUM(PAID_RET) AS TOTAL_PAID  FROM ( "
				//AR INVOICE
				+ "SELECT SO.SALES_ORDER_ID, SO.EB_OBJECT_ID AS OBJECT_ID, SO.DATE, SO.SEQUENCE_NO, SO.PO_NUMBER,  "
				+ "SO.AR_CUSTOMER_ID, SO.REMARKS, ARI.RETENTION AS RETENTION, 0 AS PAID_RET "
				+ "FROM SALES_ORDER SO "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.COMPANY_ID = ? "
				+ (divisionId != null ? "AND SO.DIVISION_ID = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (poNumber != null  && poNumber != "" ? "AND SO.PO_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				//AR RECEIPT
				+ "SELECT SO.SALES_ORDER_ID, SO.EB_OBJECT_ID AS OBJECT_ID, SO.DATE, SO.SEQUENCE_NO, SO.PO_NUMBER,  "
				+ "SO.AR_CUSTOMER_ID, SO.REMARKS, ARR.RETENTION AS RETENTION, 0 AS PAID_RET "
				+ "FROM SALES_ORDER SO "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ARI.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SO.COMPANY_ID = ? "
				+ (divisionId != null ? "AND SO.DIVISION_ID = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (poNumber != null  && poNumber != "" ? "AND SO.PO_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				// PRL AR INVOICE
				+ "SELECT SO.SALES_ORDER_ID, SO.EB_OBJECT_ID AS OBJECT_ID, SO.DATE, SO.SEQUENCE_NO, SO.PO_NUMBER,  "
				+ "SO.AR_CUSTOMER_ID, SO.REMARKS, 0 AS RETENTION, PRL.UP_AMOUNT AS PAID_RET "
				+ "FROM PROJECT_RETENTION_LINE PRL "
				+ "INNER JOIN PROJECT_RETENTION PR ON PR.PROJECT_RETENTION_ID = PRL.PROJECT_RETENTION_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = PRL.EB_OBJECT_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = ARI.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SO.COMPANY_ID = ? "
				+ (divisionId != null ? "AND SO.DIVISION_ID = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (poNumber != null  && poNumber != "" ? "AND SO.PO_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				// PRL AR RECEIPT
				+ "SELECT SO.SALES_ORDER_ID, SO.EB_OBJECT_ID AS OBJECT_ID, SO.DATE, SO.SEQUENCE_NO, SO.PO_NUMBER,  "
				+ "SO.AR_CUSTOMER_ID, SO.REMARKS, 0 AS RETENTION, PRL.UP_AMOUNT AS PAID_RET "
				+ "FROM PROJECT_RETENTION_LINE PRL "
				+ "INNER JOIN PROJECT_RETENTION PR ON PR.PROJECT_RETENTION_ID = PRL.PROJECT_RETENTION_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = PRL.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.AR_RECEIPT_ID = ARR.AR_RECEIPT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT ARL_OTO ON ARL_OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.EB_OBJECT_ID = ARL_OTO.FROM_OBJECT_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = ARI.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SO.COMPANY_ID = ? "
				+ (divisionId != null ? "AND SO.DIVISION_ID = ? " : "")
				+ (arCustomerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (soNumber != null ? "AND SO.SEQUENCE_NO = ? " : "")
				+ (poNumber != null  && poNumber != "" ? "AND SO.PO_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND SO.DATE BETWEEN ? AND ? " : "")
				+ ") AS TBL "
				+ "GROUP BY SALES_ORDER_ID ");
		if(statusId == SalesOrder.STATUS_UNUSED) {
			sql.append("HAVING TOTAL_PAID = 0 AND TOTAL_RETENTION > 0 ");
		} else if (statusId == SalesOrder.STATUS_USED) {
			sql.append("HAVING TOTAL_RETENTION - TOTAL_PAID > 0 AND TOTAL_PAID > 0 ");
		} else {
			sql.append("HAVING TOTAL_RETENTION - TOTAL_PAID > 0 ");
		}
		sql.append("ORDER BY DATE DESC, SEQUENCE_NO DESC ");
		Integer numberOfTbls = 4;
		return getAllAsPage(sql.toString(), pageSetting, new SoPrReferenceHandler(companyId, arCustomerId, 
				arCustomerAcctId, soNumber, statusId, dateFrom, dateTo, numberOfTbls, poNumber, divisionId));
	}

	private static class SoPrReferenceHandler implements QueryResultHandler<SalesOrder> {
		private Integer companyId;
		private Integer soNumber;
		private Integer arCustomerId;
		private Integer arCustomerAcctId;
		private Integer numberOfTbls;
		private Date dateFrom;
		private Date dateTo;
		private String poNumber;
		private Integer divisionId;

		private SoPrReferenceHandler(Integer companyId, Integer arCustomerId, Integer arCustomerAcctId,
				Integer soNumber, Integer statusId, Date dateFrom, Date dateTo, Integer numberOfTbls,
				String poNumber, Integer divisionId) {
			this.companyId = companyId;
			this.soNumber = soNumber;
			this.arCustomerId = arCustomerId;
			this.arCustomerAcctId = arCustomerAcctId;
			this.numberOfTbls = numberOfTbls;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.poNumber = poNumber;
			this.divisionId = divisionId;
		}

		@Override
		public List<SalesOrder> convert(List<Object[]> queryResult) {
			List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
			SalesOrder salesOrder = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				salesOrder = new SalesOrder();
				if(row[index] != null) {
					salesOrder.setId((Integer) row[index]);
					salesOrder.setEbObjectId((Integer) row[++index]);
					salesOrder.setDate((Date) row[++index]);
					salesOrder.setSequenceNumber((Integer) row[++index]);
					salesOrder.setPoNumber((String) row[++index]);
					salesOrder.setArCustomerId((Integer) row[++index]);
					salesOrder.setRemarks((String) row[++index]);
					salesOrders.add(salesOrder);
				}
			}
			return salesOrders;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0; i < numberOfTbls; i++) {
				query.setParameter(index, companyId);
				if(divisionId != null) {
					query.setParameter(++index, divisionId);
				}
				if (arCustomerId != null) {
					query.setParameter(++index, arCustomerId);
				}
				if (arCustomerAcctId != null) {
					query.setParameter(++index, arCustomerAcctId);
				}
				if (soNumber != null) {
					query.setParameter(++index, soNumber);
				}
				if(poNumber != null  && poNumber != "") {
					query.setParameter(++index, poNumber);
				}
				if(dateFrom != null && dateTo != null) {
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				}
				if (i < (numberOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SALES_ORDER_ID", Hibernate.INTEGER);
			query.addScalar("OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("PO_NUMBER", Hibernate.STRING);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public Page<SalesReportDto> generateSalesReport(Integer companyId, Integer divisionId, Integer salesPersonnelId,
			Date dateFrom, Date dateTo, Integer currencyId, PageSetting pageSetting) {
		SalesReportHandler handler = new SalesReportHandler(companyId, divisionId, salesPersonnelId,
				dateFrom, dateTo, currencyId);
		return executePagedSP("GET_SALES_REPORT", pageSetting, handler, companyId, divisionId, (salesPersonnelId != null ? salesPersonnelId : -1),
				dateFrom, dateTo, currencyId);
	}

	private static class SalesReportHandler implements QueryResultHandler<SalesReportDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer salesPersonnelId;
		private Date dateFrom;
		private Date dateTo;
		private Integer currencyId;

		private SalesReportHandler (Integer companyId, Integer divisionId, Integer salesPersonnelId,
				Date dateFrom, Date dateTo, Integer currencyId) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.salesPersonnelId = salesPersonnelId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.currencyId = currencyId;
		}

		@Override
		public List<SalesReportDto> convert(List<Object[]> queryResult) {
			List<SalesReportDto> dtos = new ArrayList<SalesReportDto>();
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				Integer soId = (Integer) row[index];
				Date soDate = (Date) row[++index];
				Integer soNumber = (Integer) row[++index];
				String poNumber = (String) row[++index];
				Double soAmount = (Double) row[++index];
				String term = (String) row[++index];
				String customerAcct = (String) row[++index];
				String customer = (String) row[++index];
				String salesPersonnel = (String) row[++index];
				String drNumber = (String) row[++index];
				String drRefNo = (String) row[++index];
				String drDate = (String) row[++index];
				String drDateReceived = (String) row[++index];
				String drReceiver = (String) row[++index];
				String drStatus = (String) row[++index];
				String ariDate = (String) row[++index];
				String ariDateReceived = (String) row[++index];
				String ariReceiver = (String) row[++index];
				String ariNumber = (String) row[++index];
				Double netSales = (Double) row[++index];
				Double vat = (Double) row[++index];
				Double grossSales = (Double) row[++index];
				Double wtAmount = (Double) row[++index];
				String currency = (String) row[++index];
				String arReceiptNo = (String) row[++index];
				Double arReceiptAmt = (Double) row[++index];
				Double balance = (Double) row[++index];
				String arReceiptDate = (String) row[++index];
				dtos.add(new SalesReportDto(soId, soDate, soNumber, poNumber, soAmount, term, customerAcct, 
						customer, salesPersonnel, drNumber, drRefNo, drDate, drDateReceived, drReceiver, 
						drStatus, ariDate, ariDateReceived, ariReceiver, ariNumber, netSales, vat, grossSales, 
						wtAmount, currency, arReceiptNo, arReceiptDate, arReceiptAmt, balance));
			}
			return dtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			if(divisionId != -1) {
				query.setParameter(++index, divisionId);
			}
			if(salesPersonnelId != null) {
				query.setParameter(++index, salesPersonnelId);
			}
			if(dateFrom != null && dateTo != null) {
				query.setParameter(++index, dateFrom);
				query.setParameter(++index, dateTo);
			}
			if(currencyId != -1) {
				query.setParameter(++index, currencyId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {}
	}

	public List<SalesDeliveryEfficiencyDto> getSalesDeliveryEfficiency(Integer companyId, Integer divisionId,
			Integer customerId, Date dateFrom, Date dateTo) {
		List<SalesDeliveryEfficiencyDto> sdeDtos = new ArrayList<SalesDeliveryEfficiencyDto>();
		List<Object> objects = executeSP("GET_SALES_DELIVERY_EFFICIENCY", companyId, divisionId, customerId, dateFrom, dateTo);
		if (objects != null && !objects.isEmpty()) {
			SalesDeliveryEfficiencyDto dto = null;
			Calendar cal = Calendar.getInstance();
			Object[] row = null;
			for (Object obj: objects) {
				row = (Object[]) obj;
				dto = new SalesDeliveryEfficiencyDto();
				dto.setDivisionId((Integer) row[0]);
				dto.setDivisionName((String) row[1]);
				dto.setCustomerId((Integer) row[2]);
				dto.setCustomerName((String) row[3]);
				dto.setStockCode((String) row[4]);
				dto.setDescription((String) row[5]);
				dto.setPoNumber((String) row[6]);
				dto.setRefNumber((String) row[7]);
				dto.setQuantity((Double) row[8]);
				dto.setDeliveryStatus((String) row[9]);
				dto.setDeliveryDate((Date) row[10]);
				dto.setDateReceived((Date) row[11]);
				dto.setDeliveryReceiptId((Integer) row[12]);
				cal.setTime(dto.getDeliveryDate());
				dto.setMonth(cal.get(Calendar.MONTH));
				dto.setYear(cal.get(Calendar.YEAR));
				sdeDtos.add(dto);
				// free up memory
				row = null;
				dto = null;
			}
		}
		return sdeDtos;
	}

	@Override
	public Page<SalesOrderRegisterDto> getSoRegisterData(Integer companyId, Integer divisionId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer soType, Integer soFrom, Integer soTo, String popcrNo, Date dateFrom, Date dateTo,
			Integer statusId, PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT ID, DIVISION, SO_TYPE, SEQ_NO, DATE, DELIVERY_DATE, CUSTOMER_NAME, CUSTOMER_ACCOUNT,  TERM, PO_NUMBER, "
				+ "AMOUNT, STATUS,REMARKS, CANCELLATION_REMARKS, STATUS_ID FROM( "
				+ "SELECT ID, DIVISION, SO_TYPE, SEQ_NO, DATE, DELIVERY_DATE, CUSTOMER_NAME, CUSTOMER_ACCOUNT,  TERM, PO_NUMBER, "
				+ "SUM(ORDER_AMOUNT) AS AMOUNT,  "
				+ "IF(SUM(COALESCE(DELIVERED_AMOUNT, 0)) = 0, IF(FW_STATUS_ID = 4, 'Cancelled', 'Unserved'), "
				+ "IF(SUM(COALESCE(DELIVERED_AMOUNT, 0)) < SUM(ORDER_AMOUNT), 'Partially Served', 'Fully Served')) AS STATUS ,  "
				+ "REMARKS, CANCELLATION_REMARKS,  "
				+ "IF(SUM(COALESCE(DELIVERED_AMOUNT, 0)) = 0, IF(FW_STATUS_ID = 4, 4, 3), "
				+ "IF(SUM(COALESCE(DELIVERED_AMOUNT, 0)) < SUM(ORDER_AMOUNT), 1, 2)) AS STATUS_ID FROM ( "
				+ "SELECT SO.SALES_ORDER_ID AS ID,  D.NAME AS DIVISION, ST.NAME AS SO_TYPE, SO.SEQUENCE_NO AS SEQ_NO, SO.DATE AS DATE, SO.DELIVERY_DATE AS DELIVERY_DATE,  "
				+ "AC.NAME AS CUSTOMER_NAME, ACA.NAME AS CUSTOMER_ACCOUNT, T.NAME AS TERM, SO.PO_NUMBER AS PO_NUMBER, "
				+ "IF(FW1.CURRENT_STATUS_ID = 4, 0, SUM((S.UP_AMOUNT * S.QUANTITY) - COALESCE(S.DISCOUNT, 0))) AS ORDER_AMOUNT,  "
				+ "(SELECT IF(FW1.CURRENT_STATUS_ID = 4, 0, SUM((DRL.UP_AMOUNT * DRL.QUANTITY) - COALESCE(DRL.DISCOUNT, 0))) AS DELIVERED_AMOUNT "
				+ "FROM DELIVERY_RECEIPT_LINE DRL "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRL.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN SALES_ORDER DR_SO ON DR_SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE DR_SO.SALES_ORDER_ID=SO.SALES_ORDER_ID AND FW.IS_COMPLETE=1) AS DELIVERED_AMOUNT, "
				+ "'' AS STATUS, SO.REMARKS AS REMARKS, IF(FW1.CURRENT_STATUS_ID = 4, FWL1.COMMENT, '') AS CANCELLATION_REMARKS,  "
				+ "FW1.CURRENT_STATUS_ID AS FW_STATUS_ID "
				+ "FROM SALES_ORDER_LINE S "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = S.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID  "
				+ "INNER JOIN FORM_WORKFLOW_LOG FWL1 ON (FWL1.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID AND FWL1.FORM_STATUS_ID = FW1.CURRENT_STATUS_ID)  "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = SO.DIVISION_ID  "
				+ "INNER JOIN SO_TYPE ST ON ST.SO_TYPE_ID = SO.SO_TYPE_ID  "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON ACA.AR_CUSTOMER_ACCOUNT_ID = SO.AR_CUSTOMER_ACCOUNT_ID  "
				+ "INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = ACA.AR_CUSTOMER_ID  "
				+ "INNER JOIN TERM T ON T.TERM_ID = ACA.TERM_ID "
				+ "WHERE SO.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND SO.DIVISION_ID = ? "  : "")
				+ (arCustomerId != -1 ? "AND SO.AR_CUSTOMER_ID = ? "  : "")
				+ (arCustomerAccountId > 0 ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (soType != -1 ? "AND SO.SO_TYPE_ID = ? " : "")
				+ (soFrom != null  && soTo != null ? "AND SO.SEQUENCE_NO BETWEEN ? AND ? " : "")
				+ (popcrNo != "" ? "AND SO.PO_NUMBER LIKE ? " : "")
				+ (dateFrom != null && dateTo != null ? " AND SO.DATE BETWEEN ? AND ? " : "")
				+ "GROUP BY S.SALES_ORDER_ID "
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID AS ID,  D.NAME AS DIVISION, ST.NAME AS SO_TYPE, SO.SEQUENCE_NO AS SEQ_NO, SO.DATE AS DATE, SO.DELIVERY_DATE AS DELIVERY_DATE,  "
				+ "AC.NAME AS CUSTOMER_NAME, ACA.NAME AS CUSTOMER_ACCOUNT, T.NAME AS TERM, SO.PO_NUMBER AS PO_NUMBER, "
				+ "IF(FW1.CURRENT_STATUS_ID = 4, 0, SUM((SOI.GROSS_AMOUNT * SOI.QUANTITY)-COALESCE(SOI.DISCOUNT, 0))-COALESCE(SO.WT_AMOUNT, 0)) AS ORDER_AMOUNT, "
				+ "(SELECT IF(FW.CURRENT_STATUS_ID = 4, 0, SUM((DRI.SRP * DRI.QUANTITY)-COALESCE(DRI.DISCOUNT, 0))-COALESCE(DR.WT_AMOUNT, 0)) AS DELIVERED_AMOUNT "
				+ "FROM DELIVERY_RECEIPT_ITEM DRI "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRI.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN SALES_ORDER DR_SO ON DR_SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE DR_SO.SALES_ORDER_ID=SO.SALES_ORDER_ID AND FW.IS_COMPLETE = 1) AS DELIVERED_AMOUNT, "
				+ "'' AS STATUS, SO.REMARKS AS REMARKS, IF(FW1.CURRENT_STATUS_ID = 4, FWL1.COMMENT, '') AS CANCELLATION_REMARKS,  "
				+ "FW1.CURRENT_STATUS_ID AS FW_STATUS_ID "
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID  "
				+ "INNER JOIN FORM_WORKFLOW_LOG FWL1 ON (FWL1.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID AND FWL1.FORM_STATUS_ID = FW1.CURRENT_STATUS_ID)  "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = SO.DIVISION_ID  "
				+ "INNER JOIN SO_TYPE ST ON ST.SO_TYPE_ID = SO.SO_TYPE_ID  "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON ACA.AR_CUSTOMER_ACCOUNT_ID = SO.AR_CUSTOMER_ACCOUNT_ID  "
				+ "INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = ACA.AR_CUSTOMER_ID  "
				+ "INNER JOIN TERM T ON T.TERM_ID = ACA.TERM_ID "
				+ "WHERE SO.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND SO.DIVISION_ID = ? "  : "")
				+ (arCustomerId != -1 ? "AND SO.AR_CUSTOMER_ID = ? "  : "")
				+ (arCustomerAccountId > 0 ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (soType != -1 ? "AND SO.SO_TYPE_ID = ? " : "")
				+ (soFrom != null  && soTo != null ? "AND SO.SEQUENCE_NO BETWEEN ? AND ? " : "")
				+ (popcrNo != "" ? "AND SO.PO_NUMBER LIKE ? " : "")
				+ (dateFrom != null && dateTo != null ? " AND SO.DATE BETWEEN ? AND ? " : "")
				+ "GROUP BY SOI.SALES_ORDER_ID "
				+ ")AS INNERTBL GROUP BY ID"
				+ ") AS FTBL"
				+ (statusId != -1 ? " WHERE STATUS_ID = ? "  : " ")
				+"  ");
		return getAllAsPage(sql.toString(),pageSetting, new SoRegisterHandler( companyId,  divisionId,  arCustomerId,  arCustomerAccountId,
				 soType,  soFrom,  soTo,  popcrNo,  dateFrom,  dateTo,
				 statusId));
	}

	private static class SoRegisterHandler implements QueryResultHandler<SalesOrderRegisterDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer arCustomerId;
		private Integer arCustomerAccountId;
		private Integer soType;
		private Integer soFrom;
		private Integer soTo;
		private String popcrNo;
		private Date dateFrom;
		private Date dateTo;
		private Integer statusId;

		private SoRegisterHandler(Integer companyId, Integer divisionId, Integer arCustomerId, Integer arCustomerAccountId,
				Integer soType, Integer soFrom, Integer soTo, String popcrNo, Date dateFrom, Date dateTo,
				Integer statusId) {
			this.companyId = companyId;
			this.divisionId=divisionId;
			this.arCustomerId = arCustomerId;
			this.arCustomerAccountId = arCustomerAccountId;
			this.soType= soType;
			this.soFrom=soFrom;
			this.soTo=soTo;
			this.popcrNo=popcrNo;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.statusId = statusId;
		}

		@Override
		public List<SalesOrderRegisterDto> convert(List<Object[]> queryResult) {
			List<SalesOrderRegisterDto> rdto = new ArrayList<SalesOrderRegisterDto>();
			SalesOrderRegisterDto so = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				so = new SalesOrderRegisterDto();
					so.setId((Integer) row[index]);
					so.setDivision((String) row[++index]);
					so.setSoType((String) row[++index]);
					so.setSequenceNumber((Integer) row[++index]);
					so.setDate((Date) row[++index]);
					so.setDeliveryDate((Date) row[++index]);
					so.setCustomerName((String) row[++index]);
					so.setCustomerAcctName((String) row[++index]);
					so.setTerm((String) row[++index]);
					so.setPoNumber((String) row[++index]);
					so.setAmount((Double) row[++index]);
					so.setStatus((String) row[++index]);
					so.setRemarks((String) row[++index]);
					so.setCancellationRemarks((String) row[++index]);
					so.setStatusId((Integer) row[++index]);
					rdto.add(so);
				}
			return rdto;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int numOfTables = 2;
			for (int i=0; i<numOfTables; i++) {
				query.setParameter(index, companyId);
				if(divisionId != -1) {
					query.setParameter(++index, divisionId);
				}
				if (arCustomerId != -1) {
					query.setParameter(++index, arCustomerId);
				}
				if (arCustomerAccountId > 0) {
					query.setParameter(++index, arCustomerAccountId);
				}
				if (soType != -1) {
					query.setParameter(++index, soType);
				}
				if(soFrom != null  && soTo != null) {
					query.setParameter(++index, soFrom);
					query.setParameter(++index, soTo);
				}
				if (popcrNo != "") {
					query.setParameter(++index, StringFormatUtil.appendWildCard(popcrNo));
				}
				if(dateFrom != null && dateTo != null) {
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				}
				if (i < (numOfTables-1)) {
					++index;
					}
			}
			if (statusId != -1) {
				query.setParameter(++index, statusId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("SO_TYPE", Hibernate.STRING);
			query.addScalar("SEQ_NO", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("DELIVERY_DATE", Hibernate.DATE);
			query.addScalar("CUSTOMER_NAME", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCOUNT", Hibernate.STRING);
			query.addScalar("TERM", Hibernate.STRING);
			query.addScalar("PO_NUMBER", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("STATUS", Hibernate.STRING);
			query.addScalar("REMARKS", Hibernate.STRING);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
			query.addScalar("STATUS_ID", Hibernate.INTEGER);
		}

	}

		public List<SalesPoMonitoringDto> getSalesOutputReportData(Integer companyId, Integer divisionId,
			Integer customerId, Integer customerAcctId, Integer salesPersonnelId, String poNumber, Date soDateFrom, Date soDateTo,
			Date drDateFrom, Date drDateTo, Date ariDateFrom, Date ariDateTo) {
		List<SalesPoMonitoringDto> dtos = new ArrayList<SalesPoMonitoringDto>();
		List<Object> objects = executeSP("GET_SALES_OUTPUT", companyId, divisionId, customerId, customerAcctId, salesPersonnelId, poNumber,
				soDateFrom, soDateTo, drDateFrom, drDateTo, ariDateFrom, ariDateTo);
		if (objects != null && !objects.isEmpty()) {
			SalesPoMonitoringDto dto = null;
			for(Object obj : objects) {
				int index = 0;
				Object[] row = (Object[]) obj;
				dto = new SalesPoMonitoringDto();
				dto.setSoId((Integer) row[index]);
				dto.setLineId((Integer) row[++index]);
				dto.setMonth((String) row[++index]);
				dto.setRequestor((String) row[++index]);
				dto.setPoNumber((String) row[++index]);
				dto.setSoDate((Date) row[++index]);
				dto.setEstimatedDelivery((Date) row[++index]);
				dto.setStockCode((String) row[++index]);
				dto.setDescription((String) row[++index]);
				dto.setQty((Double) row[++index]);
				dto.setUom((String) row[++index]);
				dto.setUnitPrice((Double) row[++index]);
				dto.setSoAmount((Double) row[++index]);
				dto.setDrDate((Date) row[++index]);
				dto.setDrRef((String) row[++index]);
				dto.setDrQuantity((Double) row[++index]);
				dto.setAriDate((Date) row[++index]);
				dto.setAriNumber((Integer) row[++index]);
				dto.setAriQty((Double) row[++index]);
				dto.setAriAmount((Double) row[++index]);
				dto.setSalesPersonnelId((Integer) row[++index]);
				dto.setCustomerId((Integer) row[++index]);
				dto.setCustomerName((String) row[++index]);
				dto.setDeliveryReceiptId((Integer) row[++index]);
				dtos.add(dto);
			}
		}
		return dtos;
	}
}
