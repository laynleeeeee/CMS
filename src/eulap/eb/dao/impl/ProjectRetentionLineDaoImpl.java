package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ProjectRetentionLineDao;
import eulap.eb.domain.hibernate.ProjectRetentionLine;

/**
 * Implementing class of {@link ProjectRetentionLineDao}

 */

public class ProjectRetentionLineDaoImpl extends BaseDao<ProjectRetentionLine> implements ProjectRetentionLineDao {

	@Override
	protected Class<ProjectRetentionLine> getDomainClass() {
		return ProjectRetentionLine.class;
	}

	@Override
	public List<ProjectRetentionLine> getTransactionsBySoId(Integer salesOrderId) {
		List<ProjectRetentionLine> prLines = new ArrayList<>();
		String sql = "SELECT OBJECT_ID, REF_NO, SUM(RETENTION) AS TOTAL_RET, SUM(PAID_RET) AS TOTAL_PAID_RET, RATE FROM ( "
				+ "SELECT ARI.EB_OBJECT_ID AS OBJECT_ID, CONCAT('ARI-', ARI.SEQUENCE_NO) AS REF_NO, ARI.RETENTION, 0 AS PAID_RET,"
				+ "ARI.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_INVOICE ARI "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = ARI.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SO.SALES_ORDER_ID = ? "
				+ "UNION ALL "
				+ "SELECT ARI.EB_OBJECT_ID AS OBJECT_ID, CONCAT('ARI-', ARI.SEQUENCE_NO) AS REF_NO, 0 AS RETENTION, PRL.UP_AMOUNT AS PAID_RET, "
				+ "ARI.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM PROJECT_RETENTION_LINE PRL "
				+ "INNER JOIN PROJECT_RETENTION PR ON PR.PROJECT_RETENTION_ID = PRL.PROJECT_RETENTION_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = PRL.EB_OBJECT_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = ARI.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24007 "
				+ "AND SO.SALES_ORDER_ID = ? "
				+ "UNION ALL "
				+ "SELECT ARR.EB_OBJECT_ID AS OBJECT_ID, CONCAT('AC-', ARR.SEQUENCE_NO) AS REF_NO, ARR.RETENTION, 0 AS PAID_RET, "
				+ "ARR.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_RECEIPT ARR "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.AR_RECEIPT_ID = ARR.AR_RECEIPT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = ARI.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SO.SALES_ORDER_ID = ? "
				+ "UNION ALL "
				+ "SELECT ARR.EB_OBJECT_ID AS OBJECT_ID, CONCAT('AC-', ARR.SEQUENCE_NO) AS REF_NO, 0 AS RETENTION, PRL.UP_AMOUNT AS PAID_RET, "
				+ "ARR.CURRENCY_RATE_VALUE AS RATE "
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
				+ "AND OTO.OR_TYPE_ID = 24007 "
				+ "AND SO.SALES_ORDER_ID = ? "
				+ ") AS TBL "
				+ "GROUP BY OBJECT_ID "
				+ "HAVING TOTAL_RET - TOTAL_PAID_RET > 0";
		Session session = null;
		ProjectRetentionLine prLine = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			int totalNoOfTables = 4;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, salesOrderId);
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			query.addScalar("OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("REF_NO", Hibernate.STRING);
			query.addScalar("TOTAL_RET", Hibernate.DOUBLE);
			query.addScalar("TOTAL_PAID_RET", Hibernate.DOUBLE);
			query.addScalar("RATE", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					prLine = new ProjectRetentionLine();
					prLine.setReferenceObjectId((Integer) row[0]);
					prLine.setReferenceNo((String) row[1]);
					double retention = (Double) row[2];
					double paidRetention = (Double) row[3];
					prLine.setCurrencyRateValue((Double) row[4]);
					prLine.setUpAmount(retention - paidRetention);
					prLines.add(prLine);
					// Free up memory
					prLine = null;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return prLines;
	}

	@Override
	public double getRemainingRetBal(Integer objectId, Integer prlId, boolean isInvoice) {
		StringBuffer sql = new StringBuffer("SELECT OBJECT_ID, SUM(RETENTION) AS BALANCE FROM ( "
				+ "SELECT OTO.FROM_OBJECT_ID AS OBJECT_ID, -PRL.UP_AMOUNT AS RETENTION "
				+ "FROM PROJECT_RETENTION_LINE PRL "
				+ "INNER JOIN PROJECT_RETENTION PR ON PR.PROJECT_RETENTION_ID = PRL.PROJECT_RETENTION_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = PRL.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24007 "
				+ "AND OTO.FROM_OBJECT_ID = ? "
				+ (prlId != null ? "AND PRL.PROJECT_RETENTION_LINE_ID != " + prlId : "")
				+ " UNION ALL ");
		if(isInvoice) {
			sql.append("SELECT ARI.EB_OBJECT_ID AS OBJECT_ID, ARI.RETENTION AS RETENTION FROM AR_INVOICE ARI WHERE ARI.EB_OBJECT_ID = ? ");
		} else {
			sql.append("SELECT ARR.EB_OBJECT_ID AS OBJECT_ID, ARR.RETENTION AS RETENTION FROM AR_RECEIPT ARR WHERE ARR.EB_OBJECT_ID = ? ");
		}
		sql.append(") AS TBL ");
		Session session = null;
		double balance = 0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql.toString());
			Integer index = 0;
			int totalNoOfTables = 2;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, objectId);
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			query.addScalar("OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					balance = (Double) row[1];
					break;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return balance;
	}
}
