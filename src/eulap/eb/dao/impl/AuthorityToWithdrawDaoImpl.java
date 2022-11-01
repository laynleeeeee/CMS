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
import eulap.eb.dao.AuthorityToWithdrawDao;
import eulap.eb.domain.hibernate.AuthorityToWithdraw;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.DeliveryReceiptType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * DAO implementation class for {@link AuthorityToWithdrawDao}

 */

public class AuthorityToWithdrawDaoImpl extends BaseDao<AuthorityToWithdraw> implements AuthorityToWithdrawDao {

	@Override
	protected Class<AuthorityToWithdraw> getDomainClass() {
		return AuthorityToWithdraw.class;
	}

	@Override
	public Integer generateSequenceNo(int companyId) {
		return generateSequenceNumber(AuthorityToWithdraw.FIELD.sequenceNumber.name(),
				AuthorityToWithdraw.FIELD.companyId.name(), companyId);
	}

	@Override
	public Page<AuthorityToWithdraw> getAuthorityToWithdraws(ApprovalSearchParam searchParam,
			List<Integer> formStatusIds, PageSetting pageSetting) {
		HibernateCallback<Page<AuthorityToWithdraw>> hibernateCallback = new HibernateCallback<Page<AuthorityToWithdraw>>() {
			@Override
			public Page<AuthorityToWithdraw> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(AuthorityToWithdraw.class);
				SearchCommonUtil.searchCommonParams(dc, null, AuthorityToWithdraw.FIELD.companyId.name(),
						AuthorityToWithdraw.FIELD.date.name(), AuthorityToWithdraw.FIELD.date.name(),
						AuthorityToWithdraw.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
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
				dc.add(Subqueries.propertyIn(AuthorityToWithdraw.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(AuthorityToWithdraw.FIELD.id.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<AuthorityToWithdraw> retrieveAuthorityToWithdraws(String searchCriteria, PageSetting pageSetting) {
		HibernateCallback<Page<AuthorityToWithdraw>> atwCallBack = new HibernateCallback<Page<AuthorityToWithdraw>>() {
			@Override
			public Page<AuthorityToWithdraw> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(AuthorityToWithdraw.class);
				if (!searchCriteria.isEmpty()) {
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchCriteria.trim(), Hibernate.STRING));
				} 
				return getAll(criteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(atwCallBack);
	}

	@Override
	public Page<AuthorityToWithdraw> getATWReferences(Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer atwNumber, Integer statusId, Date dateFrom,
			Date dateTo, PageSetting pageSetting, Integer drTypeId) {
		Integer noOfTbls = 1;
		StringBuilder sql = new StringBuilder("SELECT *, SUM(ATW_QTY) AS TOTAL_ATW_QTY, SUM(DR_QTY) AS TOTAL_DR_QTY FROM ( ");
		if (drTypeId.equals(DeliveryReceiptType.DR_TYPE_ID)) {
			sql.append("SELECT ATW.AUTHORITY_TO_WITHDRAW_ID, ATW.SEQUENCE_NO, ATW.COMPANY_ID, ATW.AR_CUSTOMER_ID, "
					+ "ATW.AR_CUSTOMER_ACCOUNT_ID, ATW.REMARKS, ATWI.QUANTITY AS ATW_QTY, 0 AS DR_QTY "
					+ "FROM AUTHORITY_TO_WITHDRAW_ITEM ATWI "
					+ "INNER JOIN AUTHORITY_TO_WITHDRAW ATW ON ATW.AUTHORITY_TO_WITHDRAW_ID =  ATWI.AUTHORITY_TO_WITHDRAW_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATW.FORM_WORKFLOW_ID "
					+ "WHERE FW.IS_COMPLETE = 1 "
					+ "AND ATW.COMPANY_ID = ? "
					+ (atwNumber != null ? "AND ATW.SEQUENCE_NO = ? " : "")
					+ (arCustomerId != null ? "AND ATW.AR_CUSTOMER_ID = ? " : "")
					+ (arCustomerAccountId != null ? "AND ATW.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
					+ ((dateFrom != null && dateTo != null) ? "AND ATW.DATE BETWEEN ? AND ? " : "")
					+ "AND ATW.AUTHORITY_TO_WITHDRAW_ID NOT IN ( "
					+ "SELECT AUTHORITY_TO_WITHDRAW_ID FROM DELIVERY_RECEIPT DR "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 "
					+ "AND DR.DELIVERY_RECEIPT_TYPE_ID = 1) "
					+ "UNION ALL "
					+ "SELECT ATW.AUTHORITY_TO_WITHDRAW_ID, ATW.SEQUENCE_NO, ATW.COMPANY_ID, ATW.AR_CUSTOMER_ID, "
					+ "ATW.AR_CUSTOMER_ACCOUNT_ID, ATW.REMARKS, ATWI.QUANTITY AS ATW_QTY, 0 AS DR_QTY "
					+ "FROM SERIAL_ITEM ATWI "
					+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ATWI.EB_OBJECT_ID "
					+ "INNER JOIN AUTHORITY_TO_WITHDRAW ATW ON ATW.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATW.FORM_WORKFLOW_ID "
					+ "WHERE FW.IS_COMPLETE = 1 "
					+ "AND OTO.OR_TYPE_ID = 12000 "
					+ "AND ATW.COMPANY_ID = ? "
					+ (atwNumber != null ? "AND ATW.SEQUENCE_NO = ? " : "")
					+ (arCustomerId != null ? "AND ATW.AR_CUSTOMER_ID = ? " : "")
					+ (arCustomerAccountId != null ? "AND ATW.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
					+ ((dateFrom != null && dateTo != null) ? "AND ATW.DATE BETWEEN ? AND ? " : "")
					+ "AND ATW.AUTHORITY_TO_WITHDRAW_ID NOT IN ( "
					+ "SELECT AUTHORITY_TO_WITHDRAW_ID FROM DELIVERY_RECEIPT DR "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 "
					+ "AND DR.DELIVERY_RECEIPT_TYPE_ID = 1) "
					+ ") AS TBL GROUP BY AUTHORITY_TO_WITHDRAW_ID ");
			if (statusId == SalesOrder.STATUS_UNUSED) {
				sql.append("HAVING TOTAL_DR_QTY = 0 ");
			} else if (statusId == SalesOrder.STATUS_USED) {
				sql.append("HAVING TOTAL_DR_QTY != 0 && ((TOTAL_ATW_QTY - TOTAL_DR_QTY) > 0) ");
			} else {
				sql.append("HAVING TOTAL_DR_QTY = 0 || ((TOTAL_ATW_QTY - TOTAL_DR_QTY) > 0) ");
			}
			noOfTbls = 2;
		} else {
			sql.append("SELECT ATW.AUTHORITY_TO_WITHDRAW_ID, ATW.SEQUENCE_NO, ATW.COMPANY_ID, "
					+ "ATW.AR_CUSTOMER_ID, ATW.AR_CUSTOMER_ACCOUNT_ID, ATW.REMARKS, ATWL.QUANTITY AS ATW_QTY, 0 AS DR_QTY  "
					+ "FROM AUTHORITY_TO_WITHDRAW_LINE ATWL "
					+ "INNER JOIN AUTHORITY_TO_WITHDRAW ATW ON ATW.AUTHORITY_TO_WITHDRAW_ID =  ATWL.AUTHORITY_TO_WITHDRAW_ID  "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATW.FORM_WORKFLOW_ID  "
					+ "WHERE FW.IS_COMPLETE = 1 "
					+ "AND ATW.COMPANY_ID = ? "
					+ (atwNumber != null ? "AND ATW.SEQUENCE_NO = ? " : "")
					+ (arCustomerId != null ? "AND ATW.AR_CUSTOMER_ID = ? " : "")
					+ (arCustomerAccountId != null ? "AND ATW.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
					+ ((dateFrom != null && dateTo != null) ? "AND ATW.DATE BETWEEN ? AND ? " : "")
					+ "AND ATW.AUTHORITY_TO_WITHDRAW_ID NOT IN ( "
					+ "SELECT DR.AUTHORITY_TO_WITHDRAW_ID FROM DELIVERY_RECEIPT DR "
					+ "INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
					+ "WHERE FW1.CURRENT_STATUS_ID != 4 "
					+ "AND DR.DELIVERY_RECEIPT_TYPE_ID = 4) "
					+ ") AS TBL GROUP BY AUTHORITY_TO_WITHDRAW_ID ");
		}
		return getAllAsPage(sql.toString(), pageSetting, new AtwReferenceHandler(companyId, arCustomerId,
				arCustomerAccountId, atwNumber, dateFrom, dateTo, noOfTbls));
	}

	private static class AtwReferenceHandler implements QueryResultHandler<AuthorityToWithdraw> {
		private Integer companyId;
		private Integer arCustomerId;
		private Integer arCustomerAccountId;
		private Integer atwNumber;
		private Date dateFrom;
		private Date dateTo;
		private Integer noOfTbls;

		private AtwReferenceHandler(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
				Integer atwNumber, Date dateFrom, Date dateTo, Integer noOfTbls) {
			this.companyId = companyId;
			this.arCustomerId = arCustomerId;
			this.arCustomerAccountId = arCustomerAccountId;
			this.atwNumber = atwNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.noOfTbls = noOfTbls;
		}

		@Override
		public List<AuthorityToWithdraw> convert(List<Object[]> queryResult) {
			List<AuthorityToWithdraw> atws = new ArrayList<AuthorityToWithdraw>();
			AuthorityToWithdraw atw = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				atw = new AuthorityToWithdraw();
				atw.setId((Integer) row[index]);
				atw.setSequenceNumber((Integer) row[++index]);
				atw.setCompanyId((Integer) row[++index]);
				atw.setArCustomerId((Integer) row[++index]);
				atw.setArCustomerAcctId((Integer) row[++index]);
				atw.setRemarks((String) row[++index]);
				atws.add(atw);
			}
			return atws;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, companyId);
				if (atwNumber != null) {
					query.setParameter(++index, atwNumber);
				}
				if (arCustomerId != null) {
					query.setParameter(++index, arCustomerId);
				}
				if (arCustomerAccountId != null) {
					query.setParameter(++index, arCustomerAccountId);
				}
				if (dateFrom != null && dateTo != null) {
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				}
				if (i < (noOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("AUTHORITY_TO_WITHDRAW_ID", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public boolean isUsedByDR(Integer atwId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		DetachedCriteria drDc = DetachedCriteria.forClass(DeliveryReceipt.class);
		drDc.setProjection(Projections.property(DeliveryReceipt.FIELD.authorityToWithdrawId.name()));
		drDc.add(Restrictions.eq(DeliveryReceipt.FIELD.authorityToWithdrawId.name(), atwId));
		drDc.add(Subqueries.propertyIn(DeliveryReceipt.FIELD.formWorkflowId.name(), fwDc));

		dc.add(Subqueries.propertyIn(AuthorityToWithdraw.FIELD.id.name(), drDc));
		return getAll(dc).size() > 0;
	}

	@Override
	public Double getRemainingAtwItemQty(Integer soId, Integer itemId) {
		String sql = "SELECT SO.SALES_ORDER_ID, SUM(SOI.QUANTITY) - ( "
				+ "SELECT COALESCE(SUM(ATWI.QUANTITY),0) FROM AUTHORITY_TO_WITHDRAW_ITEM ATWI "
				+ "INNER JOIN AUTHORITY_TO_WITHDRAW ATW ON ATW.AUTHORITY_TO_WITHDRAW_ID = ATWI.AUTHORITY_TO_WITHDRAW_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATW.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ATWI.EB_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 12001 "
				+ "AND ATW.SALES_ORDER_ID = ? "
				+ "AND ATWI.ITEM_ID = ?) AS QTY "
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND FW.IS_COMPLETE = 1 "
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
	public Double getRemainingSiQty(Integer soId, Integer itemId) {
		String sql = "SELECT SO.SALES_ORDER_ID, SUM(SOI.QUANTITY) - ( "
				+ "SELECT COALESCE(SUM(SI.QUANTITY),0) FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AUTHORITY_TO_WITHDRAW ATW ON OTO.FROM_OBJECT_ID = ATW.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATW.FORM_WORKFLOW_ID "
				+ "WHERE SI.ACTIVE = 1 "
				+ "AND OTO.OR_TYPE_ID = 12000 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND ATW.SALES_ORDER_ID = ? "
				+ "AND SI.ITEM_ID = ?) AS QTY "
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND SO.SALES_ORDER_ID = ? "
				+ "AND SOI.ITEM_ID = ?";
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
}
