package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.DeliveryReceiptItemDao;
import eulap.eb.domain.hibernate.DeliveryReceiptItem;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * Implementing class of {@link DeliveryReceiptItemDao}

 */

public class DeliveryReceiptItemDaoImpl extends BaseDao<DeliveryReceiptItem> implements DeliveryReceiptItemDao{

	@Override
	protected Class<DeliveryReceiptItem> getDomainClass() {
		return DeliveryReceiptItem.class;
	}

	@Override
	public List<DeliveryReceiptItem> getDRItems(Integer refObjectId, Integer orTypeId) {
		return getAll(getDRDetachedCriteria(refObjectId, orTypeId, null));
	}

	private DetachedCriteria getDRDetachedCriteria(Integer refObjectId, Integer orTypeId, Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
		if(orTypeId != null) {
			ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), orTypeId));
		}
		dc.add(Subqueries.propertyIn(DeliveryReceiptItem.FIELD.ebObjectId.name(), ooDc));
		if(itemId != null) {
			dc.add(Restrictions.eq(DeliveryReceiptItem.FIELD.itemId.name(), itemId));
		}
		return dc;
	}

	@Override
	public Double getRemainingQty(List<Integer> refObjectIds, Integer itemId) {
		String refIds = processRefIds(refObjectIds);

		StringBuilder sqlBuilder = new StringBuilder("SELECT REMAINING_QTY FROM ( ");
		sqlBuilder.append("SELECT ITEM_ID, SUM(TOTAL_ORDERED_QTY-TOTAL_DELIVERED_QTY) AS REMAINING_QTY FROM( ");
		sqlBuilder.append("SELECT OSI.ITEM_ID, SUM(OSI.QUANTITY) AS TOTAL_ORDERED_QTY, 0 AS TOTAL_DELIVERED_QTY FROM ORDER_SLIP_ITEM OSI  ");
		sqlBuilder.append("INNER JOIN GVCH_ITEM GI ON GI.ITEM_ID = OSI.ITEM_ID ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = OSI.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN ORDER_SLIP OS ON OS.EB_OBJECT_ID = OO.FROM_OBJECT_ID  ");
		sqlBuilder.append("WHERE GI.SERIALIZED_ITEM = 0 ");
		sqlBuilder.append("AND OSI.ACTIVE = 1 ");
		sqlBuilder.append(refIds + ") GROUP BY OSI.ITEM_ID ");
		sqlBuilder.append("UNION ALL ");
		sqlBuilder.append("SELECT DRI.ITEM_ID, 0 AS TOTAL_ORDERED_QTY, SUM(DRI.QUANTITY) AS TOTAL_DELIVERED_QTY FROM DELIVERY_RECEIPT_ITEM DRI  ");
		sqlBuilder.append("INNER JOIN GVCH_ITEM GI ON GI.ITEM_ID = DRI.ITEM_ID ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = DRI.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN DELIVERY_RECEIPT DR ON DR.EB_OBJECT_ID = OO.FROM_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = DRI.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN ORDER_SLIP_ITEM OSI ON OSI.EB_OBJECT_ID = OTO.FROM_OBJECT_ID ");
		sqlBuilder.append("WHERE GI.SERIALIZED_ITEM = 0 ");
		sqlBuilder.append("AND DRI.ACTIVE = 1 ");
		sqlBuilder.append("AND FW.CURRENT_STATUS_ID != 4 ");
		sqlBuilder.append(refIds + ") GROUP BY OSI.ITEM_ID ");
		sqlBuilder.append(") AS TBL " + (itemId != null ? "WHERE ITEM_ID = ? " : "") + "HAVING REMAINING_QTY >= 0 ");
		sqlBuilder.append(") AS REM_QTY ");
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sqlBuilder.toString());
			int index = 0;
			if(itemId != null) {
				query.setParameter(index++, itemId);
			}
			if (query.list() != null && !query.list().isEmpty()) {
				return (Double) query.list().get(0);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return 0.0;
	}

	private String processRefIds(List<Integer> refObjectIds) {
		StringBuilder sqlBuilder = new StringBuilder(" ");
		for(int i = 0; i < refObjectIds.size(); i++) {
			sqlBuilder.append((i == 0 ? "AND OSI.EB_OBJECT_ID IN ( " : ", ") + refObjectIds.get(i));
		}
		return sqlBuilder.toString();
	}

	@Override
	public Boolean isSerialItemServed(Integer ebObjectId) {
		StringBuilder sqlBuilder = new StringBuilder("SELECT DISTINCT DRI_OTO.FROM_OBJECT_ID FROM DELIVERY_RECEIPT_ITEM DRI ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT DRI_OTO ON DRI_OTO.TO_OBJECT_ID = DRI.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN ORDER_SLIP_ITEM OSI ON OSI.EB_OBJECT_ID = DRI_OTO.FROM_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT DR_OTO ON DR_OTO.TO_OBJECT_ID = DRI.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN DELIVERY_RECEIPT DR ON DR.EB_OBJECT_ID = DR_OTO.FROM_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID ");
		sqlBuilder.append("AND FW.CURRENT_STATUS_ID != 4 ");
		sqlBuilder.append("AND DRI_OTO.FROM_OBJECT_ID = ? ");
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sqlBuilder.toString());
			query.setParameter(0, ebObjectId);
			return !query.list().isEmpty();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<DeliveryReceiptItem> getDRItems(Integer refObjectId, Integer orTypeId, Integer itemId) {
		return getAll(getDRDetachedCriteria(refObjectId, orTypeId, itemId));
	}

	@Override
	public Double getNonSerialItemRemainingQty(Integer soId, Integer itemId) {
		String sql = "SELECT SALES_ORDER_ID, SUM(SO_QTY) AS TOTAL_SO_QTY, SUM(DR_QTY) AS TOTAL_DR_QTY FROM ( " 
				+ "SELECT SO.SALES_ORDER_ID, COALESCE(SOI.QUANTITY, 0) AS SO_QTY, 0 AS DR_QTY " 
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND SO.SALES_ORDER_ID = ? "
				+ "AND SOI.ITEM_ID = ? "
				+ "UNION ALL "
				+ "SELECT DR.SALES_ORDER_ID, 0 AS SO_QTY, COALESCE(DRI.QUANTITY, 0) AS DR_QTY "
				+ "FROM DELIVERY_RECEIPT_ITEM DRI " 
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRI.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND DR.SALES_ORDER_ID = ? "
				+ "AND DRI.ITEM_ID = ? "
				+ ") AS REMAINING_QTY GROUP BY SALES_ORDER_ID ";
		Session session = null;
		Double quantity = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			int index = 0;
			int numberOfTbls = 2;
			for (int i = 0; i < numberOfTbls; i++) {
				query.setParameter(index, soId);
				query.setParameter(++index, itemId);
				if (i < (numberOfTbls-1)) {
					++index;
				}
			}
			query.addScalar("SALES_ORDER_ID", Hibernate.INTEGER);
			query.addScalar("TOTAL_SO_QTY", Hibernate.DOUBLE);
			query.addScalar("TOTAL_DR_QTY", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if(list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					double totalSOQty = (Double) row[1];
					double totalDRQty = (Double) row[2];
					quantity =  totalSOQty - totalDRQty;
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

	@Override
	public Double getSerialItemRemainingQty(Integer soId, Integer itemId) {
		String sql = "SELECT SALES_ORDER_ID, SUM(SO_QTY) AS TOTAL_SO_QTY, SUM(DR_QTY) AS TOTAL_DR_QTY FROM ( " 
				+ "SELECT SO.SALES_ORDER_ID, COALESCE(SOI.QUANTITY, 0) AS SO_QTY, 0 AS DR_QTY " 
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOI.SALES_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND SO.SALES_ORDER_ID = ? "
				+ "AND SOI.ITEM_ID = ? "
				+ "UNION ALL "
				+ "SELECT DR.SALES_ORDER_ID, 0 AS SO_QTY, DRI.QUANTITY AS DR_QTY "
				+ "FROM SERIAL_ITEM DRI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = DRI.EB_OBJECT_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE OTO.OR_TYPE_ID = 12004 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND DR.SALES_ORDER_ID = ? "
				+ "AND DRI.ITEM_ID = ? "
				+ ") AS REMAINING_QTY GROUP BY SALES_ORDER_ID ";
		Session session = null;
		Double quantity = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			int index = 0;
			int numberOfTbls = 2;
			for (int i = 0; i < numberOfTbls; i++) {
				query.setParameter(index, soId);
				query.setParameter(++index, itemId);
				if (i < (numberOfTbls-1)) {
					++index;
				}
			}
			query.addScalar("SALES_ORDER_ID", Hibernate.INTEGER);
			query.addScalar("TOTAL_SO_QTY", Hibernate.DOUBLE);
			query.addScalar("TOTAL_DR_QTY", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if(list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					double totalSOQty = (Double) row[1];
					double totalDRQty = (Double) row[2];
					quantity =  totalSOQty - totalDRQty;
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

	@Override
	public Double getRemainingRefItemQty(Integer drId, Integer refObjectId) {
		String sql = "SELECT ID, SUM(SOI_QTY - DRI_QTY) AS REMAINING_QTY FROM ( "
				+ "SELECT SOI.SALES_ORDER_ITEM_ID AS ID, SOI.QUANTITY AS SOI_QTY, 0 AS DRI_QTY "
				+ "FROM SALES_ORDER_ITEM SOI "
				+ "WHERE SOI.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT DRI.DELIVERY_RECEIPT_ITEM_ID AS ID, 0 AS SOI_QTY, SUM(COALESCE(DRI.QUANTITY, 0)) AS DRI_QTY "
				+ "FROM DELIVERY_RECEIPT_ITEM DRI "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRI.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = DRI.EB_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 12005 "
				+ "AND OTO.FROM_OBJECT_ID = ? ";
		if (drId != null && drId > 0) {
			sql += "AND DR.DELIVERY_RECEIPT_ID != ? ";
		}
		sql += ") AS TMP_TBL ";
		Session session = null;
		Double quantity = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, refObjectId);
			query.setParameter(1, refObjectId);
			if (drId != null && drId > 0) {
				query.setParameter(2, drId);
			}
			query.addScalar("ID", Hibernate.INTEGER);
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
		return quantity != null ? quantity : 0.0;
	}

}
