package eulap.eb.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.domain.hibernate.ApInvoiceLine;

/**
 * Implementation class of {@link ApInvoiceLineDao}

 *
 */
public class ApInvoiceLineDaoImpl extends BaseDao<ApInvoiceLine> implements ApInvoiceLineDao {

	@Override
	protected Class<ApInvoiceLine> getDomainClass() {
		return ApInvoiceLine.class;
	}

	@Override
	public List<ApInvoiceLine> getApInvoiceLineByInvoiceId(int invoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ApInvoiceLine.FIELD.apInvoiceId.name(), invoiceId));
		return getAll(dc);
	}

	@Override
	public Double getRemainingQty(Integer referenceObjectId, Integer apLineSetupId, Integer invoiceId) {
		String sql = "SELECT SUM(POL_QTY) AS FROM_QTY, SUM(APL_QTY) AS TO_QTY, REF_OBJECT_ID FROM ( "
				+ "SELECT 0 AS POL_QTY, SUM(COALESCE(APL.QUANTITY, 0)) AS APL_QTY, OTO.FROM_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM AP_INVOICE_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = APL.AP_INVOICE_ID "
				+ "INNER JOIN AP_INVOICE AP ON AP.AP_INVOICE_ID = APL.AP_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID "
				+ "WHERE AP.INVOICE_TYPE_ID IN (13, 14, 15, 16, 17, 18) "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.FROM_OBJECT_ID = ? "
				+ "AND APL.AP_LINE_SETUP_ID = ? "
				+ (invoiceId != null && invoiceId != 0 ? "AND APL.AP_INVOICE_ID != "+ invoiceId +" " : "")
				+ "UNION ALL "
				+ "SELECT COALESCE(POL.QUANTITY, 0) AS POL_QTY, 0 AS APL_QTY, POL.EB_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM PURCHASE_ORDER_LINE POL "
				+ "INNER JOIN R_PURCHASE_ORDER RPO ON RPO.R_PURCHASE_ORDER_ID = POL.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = RPO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND POL.EB_OBJECT_ID = ? "
				+ "AND POL.AP_LINE_SETUP_ID = ? "
				+ ") AS TBL";
		Collection<Double> remainingQty = get(sql, new RemainingBalance(referenceObjectId, apLineSetupId));
		if (!remainingQty.isEmpty()) {
			return remainingQty.iterator().next();
		}
		return 0.00;
	}

	private static class RemainingBalance implements QueryResultHandler<Double> {
		private Integer referenceObjectId;
		private Integer apLineSetupId;

		private RemainingBalance (Integer referenceObjectId, Integer apLineSetupId) {
			this.referenceObjectId = referenceObjectId;
			this.apLineSetupId = apLineSetupId;
		}

		@Override
		public List<Double> convert(List<Object[]> queryResult) {
			List<Double> ret = new ArrayList<Double>();
			for (Object[] row : queryResult) {
				Double polQty = (Double) row[0];
				Double aplQty = (Double) row[1];
				BigDecimal minuend = BigDecimal.valueOf(polQty != null ? polQty : 0);
				BigDecimal subtrahend = BigDecimal.valueOf(aplQty != null ? aplQty : 0);
				ret.add(minuend.subtract(subtrahend).doubleValue());
				break;
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			Integer index = 0;
			int noOfTbls = 2;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, referenceObjectId);
				query.setParameter(++index, apLineSetupId);
				if (i < (noOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("FROM_QTY", Hibernate.DOUBLE);
			query.addScalar("TO_QTY", Hibernate.DOUBLE);
			query.addScalar("REF_OBJECT_ID", Hibernate.STRING);
		}
	}

	@Override
	public double getRemainingRrLineQty(Integer invoiceId, Integer referenceObjId, Integer apLineSetupId) {
		String sql = "SELECT SUM(RR_QUANTITY) AS FROM_QTY, SUM(IG_QUANTITY) AS TO_QTY, REF_OBJECT_ID FROM ( "
				+ "SELECT RRI.EB_OBJECT_ID AS REF_OBJECT_ID, COALESCE(RRI.QUANTITY, 0) AS RR_QUANTITY, 0 AS IG_QUANTITY "
				+ "FROM AP_INVOICE_LINE RRI "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND RRI.EB_OBJECT_ID = ? "
				+ "AND RRI.AP_LINE_SETUP_ID = ? "
				+ "UNION ALL "
				+ "SELECT OTO.FROM_OBJECT_ID AS REF_OBJECT_ID, 0 AS RR_QUANTITY, COALESCE(IG.QUANTITY, 0) AS IG_QUANTITY "
				+ "FROM AP_INVOICE_LINE IG "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = IG.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = IG.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24003 "
				+ "AND OTO.FROM_OBJECT_ID = ? "
				+ "AND IG.AP_LINE_SETUP_ID = ? "
				+ ((invoiceId != null && invoiceId != 0) ? "AND API.AP_INVOICE_ID != " + invoiceId + " " : "")
				+ ") AS TBL GROUP BY REF_OBJECT_ID ";
		Collection<Double> remainingQty = get(sql, new RemainingBalance(referenceObjId, apLineSetupId));
		if (!remainingQty.isEmpty()) {
			return remainingQty.iterator().next();
		}
		return 0.00;
	}

	@Override
	public double getRemainingInvGsLineQty(Integer invoiceId, Integer referenceObjId, Integer apLineSetupId) {
		String sql = "SELECT SUM(IG_QUANTITY) AS FROM_QTY, SUM(RTS_QUANTITY) AS TO_QTY, REF_OBJECT_ID FROM ( "
				+ "SELECT API.EB_OBJECT_ID AS REF_OBJECT_ID, COALESCE(IG.QUANTITY, 0) AS IG_QUANTITY, 0 AS RTS_QUANTITY "
				+ "FROM AP_INVOICE_LINE IG "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = IG.AP_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND API.EB_OBJECT_ID = ? "
				+ "AND IG.AP_LINE_SETUP_ID = ? "
				+ "UNION ALL "
				+ "SELECT OTO.FROM_OBJECT_ID AS REF_OBJECT_ID, 0 AS IG_QUANTITY, COALESCE(RTSI.QUANTITY, 0) AS RTS_QUANTITY "
				+ "FROM AP_INVOICE_LINE RTSI "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RTSI.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24005 "
				+ "AND OTO.FROM_OBJECT_ID = ? "
				+ "AND RTSI.AP_LINE_SETUP_ID = ? "
				+ ((invoiceId != null && invoiceId != 0) ? "AND API.AP_INVOICE_ID != " + invoiceId + " " : "")
				+ ") AS TBL";
		Collection<Double> remainingQty = get(sql, new RemainingBalance(referenceObjId, apLineSetupId));
		if (!remainingQty.isEmpty()) {
			return remainingQty.iterator().next();
		}
		return 0.00;
	}
}
