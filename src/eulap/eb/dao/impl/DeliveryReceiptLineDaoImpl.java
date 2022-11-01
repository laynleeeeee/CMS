package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.DeliveryReceiptLineDao;
import eulap.eb.domain.hibernate.DeliveryReceiptLine;

/**
 * Implementation class for {@link DeliveryReceiptLineDao}

 *
 */
public class DeliveryReceiptLineDaoImpl extends BaseDao<DeliveryReceiptLine> implements DeliveryReceiptLineDao  {

	@Override
	protected Class<DeliveryReceiptLine> getDomainClass() {
		return DeliveryReceiptLine.class;
	}

	@Override
	public Double getRemainingQty(Integer drId, Integer referenceObjectId) {
		String sql = "SELECT EB_OBJECT_ID, ROUND(SUM(SOL_QUANTITY) - SUM(COALESCE(DRL_QUANTITY, 0)), 2) AS REMAINING_QTY FROM ( "
				+ "SELECT SOL.EB_OBJECT_ID, SOL.QUANTITY AS SOL_QUANTITY, 0 AS DRL_QUANTITY "
				+ "FROM SALES_ORDER_LINE SOL "
				+ "WHERE SOL.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT OTO.FROM_OBJECT_ID AS EB_OBJECT_ID, 0 AS SOL_QUANTITY, DRL.QUANTITY AS DRL_QUANTITY "
				+ "FROM DELIVERY_RECEIPT_LINE DRL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = DRL.EB_OBJECT_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRL.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE OTO.OR_TYPE_ID = 12005 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.FROM_OBJECT_ID = ? ";
		if (drId != null) {
			sql += "AND DR.DELIVERY_RECEIPT_ID != ? ";
		}
		sql += ") AS TBL";
		Session session = null;
		Double quantity = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, referenceObjectId);
			query.setParameter(1, referenceObjectId);
			if (drId != null) {
				query.setParameter(2, drId);
			}
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("REMAINING_QTY", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if(list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					quantity = (Double) row[1];
					break; // expecting single row only
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