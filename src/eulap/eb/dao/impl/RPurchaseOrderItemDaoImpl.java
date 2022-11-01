package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RPurchaseOrderItemDao;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RPurchaseOrderItem;

/**
 * Data Access Object {@link PurchaseOrderItem}

 */
public class RPurchaseOrderItemDaoImpl extends BaseDao<RPurchaseOrderItem> implements RPurchaseOrderItemDao{
	@Override
	protected Class<RPurchaseOrderItem> getDomainClass() {
		return RPurchaseOrderItem.class;
	}

	@Override
	public List<RPurchaseOrderItem> getPOItems(int rPurchaseOrderId) {
		DetachedCriteria poItemDc = getDetachedCriteria();
		poItemDc.add(Restrictions.eq(RPurchaseOrderItem.FIELD.rPurchaseOrderId.name(), rPurchaseOrderId));
		return getAll(poItemDc);
	}

	@Override
	public double getRemainingQty(Integer refenceObjectId, Integer itemId) {
		String sql = "SELECT SUM(PO_QTY - RRI_OTY) AS REMAINING_QTY, REF_OBJECT_ID FROM ( "
				+ "SELECT POI.EB_OBJECT_ID AS REF_OBJECT_ID, POI.QUANTITY AS PO_QTY, 0 AS RRI_OTY "
				+ "FROM R_PURCHASE_ORDER_ITEM POI "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND POI.EB_OBJECT_ID = ? "
				+ "AND POI.ITEM_ID = ? "
				+ "UNION ALL "
				+ "SELECT OO.FROM_OBJECT_ID AS REF_OBJECT_ID, 0 AS PO_QTY, SUM(RRI.QUANTITY) AS RRI_OTY "
				+ "FROM R_RECEIVING_REPORT_ITEM RRI "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = RRI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = API.AP_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (13, 14, 15, 16, 17, 18) "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.FROM_OBJECT_ID = ? "
				+ "AND RRI.ITEM_ID = ? ) TBL_REMAINING";
		Collection<Double> remainingQty = get(sql, new RemainingBalance(refenceObjectId, itemId));
		if(!remainingQty.isEmpty()) {
			return remainingQty.iterator().next();
		}
		return 0;
	}

	private static class RemainingBalance implements QueryResultHandler<Double> {
		private Integer refenceObjectId;
		private Integer itemId;

		private RemainingBalance (Integer refenceObjectId, Integer itemId) {
			this.refenceObjectId = refenceObjectId;
			this.itemId = itemId;
		}

		@Override
		public List<Double> convert(List<Object[]> queryResult) {
			List<Double> ret = new ArrayList<Double>();
			for (Object[] row : queryResult) {
				Double remainingQty = (Double) row[0];
				if (remainingQty == null) {
					ret.add(0.0);
					break;
				}
				ret.add(remainingQty);
				break; // Expecting one row only.
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			Integer index = 0;
			int noOfTbls = 2;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(i > 0 ? ++index : index, refenceObjectId);
				query.setParameter(++index, itemId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("REMAINING_QTY", Hibernate.DOUBLE);
			query.addScalar("REF_OBJECT_ID", Hibernate.STRING);
		}
	}

	@Override
	public double getNotReceivedPoiQty(Integer itemId, Integer poiId) {
		String sql = "SELECT POI.R_PURCHASE_ORDER_ITEM_ID AS POI_ID , SUM(COALESCE(POI.QUANTITY, 0)) AS TOTAL_QTY "
				+ "FROM R_PURCHASE_ORDER_ITEM POI  "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND POI.ITEM_ID = ? "
				+ "AND POI.R_PURCHASE_ORDER_ITEM_ID NOT IN ( "
				+ "SELECT POI.R_PURCHASE_ORDER_ITEM_ID FROM R_PURCHASE_ORDER_ITEM POI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = POI.EB_OBJECT_ID "
				+ "INNER JOIN R_RECEIVING_REPORT_ITEM RRI ON RRI.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW API_FW ON API_FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE API_FW.CURRENT_STATUS_ID != 4 "
				+ "AND POI.ITEM_ID = ? "
				+ "AND OTO.OR_TYPE_ID = 13 "
				+ ") "
				+ (poiId != 0 ? "AND POI.R_PURCHASE_ORDER_ITEM_ID != ? " : "");
		Session session = null;
		double qty = 0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			int index = 0;
			query.setParameter(index, itemId);
			query.setParameter(++index, itemId);
			if(poiId != 0) {
				query.setParameter(++index, poiId);
			}
			query.addScalar("POI_ID", Hibernate.INTEGER);
			query.addScalar("TOTAL_QTY", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					if((Double) row[1] != null) {
						qty = (Double) row[1];
					}
					break; // expecting 1 row only
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return qty;
	}

	@Override
	public RPurchaseOrderItem getByChildObjectId(Integer objectId) {
		DetachedCriteria dc = getDetachedCriteria();
		//Object to object
		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.fromObjectId.name()));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.toObjectId.name(), objectId));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), 13));
		dc.add(Subqueries.propertyIn(RPurchaseOrderItem.FIELD.ebObjectId.name(), otoDc));
		return get(dc);
	}
}
