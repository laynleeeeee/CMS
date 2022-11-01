package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eulap.common.dao.BaseDao;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.ApInvoiceGoodsDao;
import eulap.eb.domain.hibernate.ApInvoiceGoods;

/**
 * DAO implementation class for AP invoice goods

 */

public class ApInvoiceGoodsDaoImpl extends BaseDao<ApInvoiceGoods> implements ApInvoiceGoodsDao {

	@Override
	protected Class<ApInvoiceGoods> getDomainClass() {
		return ApInvoiceGoods.class;
	}

	@Override
	public double getRrRemainingQty(Integer invoiceId, Integer referenceObjId, Integer itemId) {
		String sql = "SELECT REFERENCE_OBJECT_ID, SUM(RR_QUANTITY)-SUM(IG_QUANTITY) AS BALANCE FROM ( "
				+ "SELECT API.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, RRI.QUANTITY AS RR_QUANTITY, 0 AS IG_QUANTITY "
				+ "FROM R_RECEIVING_REPORT_ITEM RRI "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND RRI.ITEM_ID = ? "
				+ "AND API.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT OTO.FROM_OBJECT_ID AS REFERENCE_OBJECT_ID, 0 AS RR_QUANTITY, IG.QUANTITY AS IG_QUANTITY "
				+ "FROM AP_INVOICE_GOODS IG "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = IG.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24002 "
				+ "AND IG.ITEM_ID = ? "
				+ "AND OTO.FROM_OBJECT_ID = ? "
				+ ((invoiceId != null && invoiceId != 0) ? "AND API.AP_INVOICE_ID != " + invoiceId + " " : "")
				+ ") AS TBL GROUP BY REFERENCE_OBJECT_ID ";
		return getRemainingQuantity(sql, invoiceId, referenceObjId, itemId);
	}

	private double getRemainingQuantity(String sql, Integer invoiceId, Integer referenceObjId, Integer itemId) {
		Double remainingQty = 0.0;
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			int totalNoOfTables = 2;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, itemId);
				query.setParameter(++index, referenceObjId);
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			query.addScalar("REFERENCE_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					remainingQty = (Double) row[1];
					break;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return remainingQty != null ? NumberFormatUtil.roundOffTo2DecPlaces(remainingQty) : 0.0;
	}

	@Override
	public double getInvGsRemainingQty(Integer invoiceId, Integer referenceObjId, Integer itemId) {
		String sql = "SELECT REFERENCE_OBJECT_ID, SUM(IG_QUANTITY)-SUM(RTS_QUANTITY) AS BALANCE FROM ( "
				+ "SELECT API.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, IG.QUANTITY AS IG_QUANTITY, 0 AS RTS_QUANTITY "
				+ "FROM AP_INVOICE_GOODS IG "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = IG.AP_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND IG.ITEM_ID = ? "
				+ "AND API.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT OTO.FROM_OBJECT_ID AS REFERENCE_OBJECT_ID, 0 AS IG_QUANTITY, RTSI.QUANTITY AS RTS_QUANTITY "
				+ "FROM R_RETURN_TO_SUPPLIER_ITEM RTSI "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RTSI.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24005 "
				+ "AND RTSI.ITEM_ID = ? "
				+ "AND OTO.FROM_OBJECT_ID = ? "
				+ ((invoiceId != null && invoiceId != 0) ? "AND API.AP_INVOICE_ID != " + invoiceId + " " : "")
				+ ") AS TBL GROUP BY REFERENCE_OBJECT_ID ";
		return getRemainingQuantity(sql, invoiceId, referenceObjId, itemId);
	}
}