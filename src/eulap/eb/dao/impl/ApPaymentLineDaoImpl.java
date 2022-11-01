package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ApPaymentLineDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.ApPaymentLine;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.web.dto.ApPaymentLineDto;

/**
 * Implementation class of {@link ApPaymentLineDao}.

 *
 */
public class ApPaymentLineDaoImpl extends BaseDao<ApPaymentLine> implements ApPaymentLineDao{

	private static final int OBJECT_TYPE_AP_PAYMENT_LINE = 24004;

	@Override
	protected Class<ApPaymentLine> getDomainClass() {
		return ApPaymentLine.class;
	}

	@Override
	public Page<ApPaymentLineDto> getUnpaidPaymentLines(Integer supplierAcctId, Integer divisionId,
			String invoiceNumber, String ebObjectIds, Integer currencyId, PageSetting pageSetting) {
		String rtsInvoiceTypeIds = "6, 31, 32, 33, 34, 35, 36";
		String rrInvoiceTypeIds = "5, 8, 9, 13, 14, 15, 16, 17, 18";
		String excludeIds = "";
		if (ebObjectIds != null && !ebObjectIds.trim().isEmpty()) {
			String tmpInvoiceIds[] = ebObjectIds.split(";");
			int index = 0;
			for(String tmp : tmpInvoiceIds) {
				excludeIds += index ==0 ? tmp.trim() : ","+tmp.trim();
				index++;
			}
		}
		String sql = "SELECT * FROM ( "
				+ "SELECT EB_OBJECT_ID, AP_INVOICE_ID, PAYMENT_LINE_TYPE, SEQUENCE_NO, "
				+ "INVOICE_TYPE_ID, INVOICE_NUMBER, AMOUNT, PAID_AMOUNT, CURRENCY_RATE_VALUE FROM ("
				// Select invoices with payments
				+ "SELECT AI.EB_OBJECT_ID, 1 AS PAYMENT_LINE_TYPE, AI.AP_INVOICE_ID, AI.SEQUENCE_NO, AI.INVOICE_TYPE_ID, "
				+ "CONCAT(CASE WHEN AI.INVOICE_TYPE_ID <= 4 THEN AI.INVOICE_NUMBER "
				+ "WHEN AI.INVOICE_TYPE_ID IN ("+rtsInvoiceTypeIds+") THEN CONCAT('RTS-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 19 AND AI.INVOICE_TYPE_ID <= 24 THEN CONCAT('API NPO -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 25 AND AI.INVOICE_TYPE_ID <= 30 THEN CONCAT('API G/S -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 37 AND AI.INVOICE_TYPE_ID <= 42 THEN CONCAT('API CONF -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 43 AND AI.INVOICE_TYPE_ID <= 48 THEN CONCAT('API I -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 49 AND AI.INVOICE_TYPE_ID <= 54 THEN CONCAT('APL -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 55 AND AI.INVOICE_TYPE_ID <= 60 THEN CONCAT('PCR -', AI.SEQUENCE_NO) "
				+ "ELSE CONCAT('AP INVOICE -', AI.SEQUENCE_NO) END) AS INVOICE_NUMBER, "
				// Negate amount for RTS and RTS - EB invoice type
				+ "IF(AI.INVOICE_TYPE_ID IN ("+rtsInvoiceTypeIds+"), -AI.AMOUNT, AI.AMOUNT) AS AMOUNT, "
				+ "COALESCE((SELECT SUM(APL.PAID_AMOUNT)  "
				+ "FROM AP_PAYMENT_LINE APL  "
				+ "INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID  "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)  "
				+ "AND API.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "AND OTO1.OR_TYPE_ID = 24004), 0 ) AS PAID_AMOUNT, AI.CURRENCY_RATE_VALUE "
				+ "FROM AP_INVOICE AI "
				+ "INNER JOIN INVOICE_TYPE IT ON IT.INVOICE_TYPE_ID = AI.INVOICE_TYPE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "LEFT JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				// Filter RR invoices
				+ "AND IT.INVOICE_TYPE_ID NOT IN ("+rrInvoiceTypeIds+") "
				+ "AND AI.SUPPLIER_ACCOUNT_ID = ? "
				+ "AND AI.DIVISION_ID = ? ";
			if(currencyId != null) {
				sql += "AND AI.CURRENCY_ID = ? ";
			}
			sql += "UNION ALL "
				// Select invoices without payments
				+ "SELECT AI.EB_OBJECT_ID, 1 AS PAYMENT_LINE_TYPE, AI.AP_INVOICE_ID, AI.SEQUENCE_NO, AI.INVOICE_TYPE_ID, "
				+ "CONCAT(CASE WHEN AI.INVOICE_TYPE_ID <= 4 THEN AI.INVOICE_NUMBER "
				+ "WHEN AI.INVOICE_TYPE_ID IN ("+rtsInvoiceTypeIds+") THEN CONCAT('RTS-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 19 AND AI.INVOICE_TYPE_ID <= 24 THEN CONCAT('API NPO -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 25 AND AI.INVOICE_TYPE_ID <= 30 THEN CONCAT('API G/S -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 37 AND AI.INVOICE_TYPE_ID <= 42 THEN CONCAT('API CONF -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 43 AND AI.INVOICE_TYPE_ID <= 48 THEN CONCAT('API I -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 49 AND AI.INVOICE_TYPE_ID <= 54 THEN CONCAT('APL -', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID >= 55 AND AI.INVOICE_TYPE_ID <= 60 THEN CONCAT('PCR -', AI.SEQUENCE_NO) "
				+ "ELSE CONCAT('AP INVOICE -', AI.SEQUENCE_NO) END) AS INVOICE_NUMBER, "
				// Negate amount for RTS and RTS - EB invoice type
				+ "IF(AI.INVOICE_TYPE_ID IN ("+rtsInvoiceTypeIds+"), -AI.AMOUNT, AI.AMOUNT) AS AMOUNT, "
				+ "0 AS PAID_AMOUNT, AI.CURRENCY_RATE_VALUE "
				+ "FROM AP_INVOICE AI "
				+ "INNER JOIN INVOICE_TYPE IT ON IT.INVOICE_TYPE_ID = AI.INVOICE_TYPE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "LEFT JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				// Filter RR invoices
				+ "AND IT.INVOICE_TYPE_ID NOT IN ("+rrInvoiceTypeIds+") "
				+ "AND AI.SUPPLIER_ACCOUNT_ID = ? "
				+ "AND AI.DIVISION_ID = ? ";
			if(currencyId != null) {
				sql += "AND AI.CURRENCY_ID = ? ";
			}
			sql += "AND AI.EB_OBJECT_ID NOT IN "
				+ "(SELECT OTO1.FROM_OBJECT_ID FROM AP_PAYMENT_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
				+ "WHERE OTO1.OR_TYPE_ID = 24004) "
				+ "UNION ALL "
				+ "SELECT SAP.EB_OBJECT_ID, 2 AS PAYMENT_LINE_TYPE, SAP.SUPPLIER_ADVANCE_PAYMENT_ID, SAP.SEQUENCE_NO, 0 AS INVOICE_TYPE_ID,  "
				+ "CONCAT('SAP-', SAP.SEQUENCE_NO) AS INVOICE_NUMBER, SAP.AMOUNT, 0 AS PAID_AMOUNT, SAP.CURRENCY_RATE_VALUE "
				+ "FROM SUPPLIER_ADVANCE_PAYMENT SAP "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SAP.EB_OBJECT_ID NOT IN ( "
				+ "SELECT OTO1.FROM_OBJECT_ID FROM AP_PAYMENT_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW AFW ON AFW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID "
				+ "WHERE OTO1.OR_TYPE_ID = 24004 "
				+ "AND AFW.CURRENT_STATUS_ID NOT IN (4, 32)) "
				+ "AND SAP.SUPPLIER_ACCOUNT_ID = ? "
				+ "AND SAP.DIVISION_ID = ? ";
			if(currencyId != null) {
				sql += "AND SAP.CURRENCY_ID = ? ";
			}
			sql +=")AS TBL WHERE ABS(AMOUNT) > ABS(PAID_AMOUNT) GROUP BY EB_OBJECT_ID "
				+ "UNION ALL "
				+ "SELECT EB_OBJECT_ID, AP_INVOICE_ID, PAYMENT_LINE_TYPE, SEQUENCE_NO, INVOICE_TYPE_ID, INVOICE_NUMBER, AMOUNT, PAID_AMOUNT, CURRENCY_RATE_VALUE FROM ( "
				//For SAP with positive payments.
				+ "SELECT SAP.EB_OBJECT_ID AS EB_OBJECT_ID, 2 AS PAYMENT_LINE_TYPE, SAP.SUPPLIER_ADVANCE_PAYMENT_ID AS AP_INVOICE_ID, SAP.SEQUENCE_NO, 0 AS INVOICE_TYPE_ID,  "
				+ "CONCAT('SAP-', SAP.SEQUENCE_NO, ' ', SAP.AMOUNT - SUM(APL.PAID_AMOUNT)) AS INVOICE_NUMBER, SAP.AMOUNT, SUM(IF(APL.PAID_AMOUNT > 0, APL.PAID_AMOUNT, 0)) AS PAID_AMOUNT, SAP.CURRENCY_RATE_VALUE   "
				+ "FROM AP_PAYMENT_LINE APL  "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID  "
				+ "INNER JOIN SUPPLIER_ADVANCE_PAYMENT SAP ON SAP.EB_OBJECT_ID = OTO.FROM_OBJECT_ID  "
				+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID  "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID  "
				+ "WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)  "
				+ "AND OTO.OR_TYPE_ID = 24004  "
				+ "AND SAP.SUPPLIER_ACCOUNT_ID = ? "
				+ "AND SAP.DIVISION_ID = ? "
				+ "AND SAP.CURRENCY_ID = ? "
				+ "GROUP BY SAP.EB_OBJECT_ID "
				+ "UNION ALL "
				//For SAP with negative payments.
				+ "SELECT SAP.EB_OBJECT_ID AS EB_OBJECT_ID, 2 AS PAYMENT_LINE_TYPE, SAP.SUPPLIER_ADVANCE_PAYMENT_ID AS AP_INVOICE_ID, SAP.SEQUENCE_NO, 0 AS INVOICE_TYPE_ID,  "
				+ "CONCAT('SAP-', SAP.SEQUENCE_NO, ' (',SUM(APL.PAID_AMOUNT),')') AS INVOICE_NUMBER, 0,  "
				+ "IF(SUM(IF(APL.PAID_AMOUNT < 0, ABS(APL.PAID_AMOUNT), 0)) < SAP.AMOUNT, SUM(APL.PAID_AMOUNT) , 0) AS PAID_AMOUNT, SAP.CURRENCY_RATE_VALUE   "
				+ "FROM AP_PAYMENT_LINE APL  "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID  "
				+ "INNER JOIN SUPPLIER_ADVANCE_PAYMENT SAP ON SAP.EB_OBJECT_ID = OTO.FROM_OBJECT_ID  "
				+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID  "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID  "
				+ "WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)  "
				+ "AND OTO.OR_TYPE_ID = 24004  "
				+ "AND SAP.SUPPLIER_ACCOUNT_ID = ? "
				+ "AND SAP.DIVISION_ID = ? ";
			if(currencyId != null) {
				sql += "AND SAP.CURRENCY_ID = ? ";
			}
			sql += "GROUP BY SAP.EB_OBJECT_ID "
				+ ") AS SAP_TBL WHERE AMOUNT != PAID_AMOUNT "
				+ ") AS MAIN_TBL "
				+ "WHERE EB_OBJECT_ID != -1 "
				+ (excludeIds.trim().isEmpty() ? "" : "AND EB_OBJECT_ID NOT IN ("+excludeIds+") ")
				+ (invoiceNumber.trim().isEmpty() ? "" : " AND INVOICE_NUMBER LIKE '"+StringFormatUtil.appendWildCard(invoiceNumber)+"' ");
			UnpaidPaymentLinesHandler handler = new UnpaidPaymentLinesHandler(supplierAcctId, divisionId, currencyId);
			return getAllAsPage(sql, pageSetting, handler);
	}

	private static class UnpaidPaymentLinesHandler implements QueryResultHandler<ApPaymentLineDto> {
		private Integer supplierAcctId;
		private Integer divisionId;
		private Integer currencyId;

		private UnpaidPaymentLinesHandler(Integer supplierAcctId, Integer divisionId, Integer currencyId) {
			this.supplierAcctId = supplierAcctId;
			this.divisionId = divisionId;
			this.currencyId = currencyId;
		}

		@Override
		public List<ApPaymentLineDto> convert(List<Object[]> queryResult) {
			List<ApPaymentLineDto> dtos = new ArrayList<ApPaymentLineDto>();
			for (Object[] rowResult : queryResult) {
				ApPaymentLineDto dto = new ApPaymentLineDto();
				int colNum = 0;
				Integer refenceObjectId = (Integer) rowResult[colNum++];
				Integer apPaymentLineType = (Integer) rowResult[colNum++];
				Integer sequenceNo = (Integer) rowResult[colNum++];
				String invoiceNumber = (String) rowResult[colNum++];
				Double amount = (Double)  rowResult[colNum++];
				Double paidAmount = (Double)  rowResult[colNum++];
				Double currencyRateValue = (Double)  rowResult[colNum++];

				dto.setRefenceObjectId(refenceObjectId);
				dto.setApPaymentLineTypeId(apPaymentLineType);
				dto.setSequenceNumber(sequenceNo);
				dto.setReferenceNumber(invoiceNumber);
				double balance = amount - paidAmount;
				dto.setAmount(balance);
				dto.setBalance(balance);
				dto.setCurrencyRateValue(currencyRateValue);
				dtos.add(dto);
			}
			return dtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int numberOfTbls = 5;
			for (int i = 0; i < numberOfTbls; i++) {
				query.setParameter(index, supplierAcctId);
				query.setParameter(++index, divisionId);
				if(currencyId != null) {
					query.setParameter(++index, currencyId);
				}
				if (i < (numberOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("PAYMENT_LINE_TYPE", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("INVOICE_NUMBER", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("PAID_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("CURRENCY_RATE_VALUE", Hibernate.DOUBLE);
		}
	}

	@Override
	public List<ApPaymentLine> getApPaymentLines(Integer parentObjectId, Integer apPaymentId) {
		DetachedCriteria dc = getDetachedCriteria();
		//AP Payment
		DetachedCriteria paymentCriteria =  DetachedCriteria.forClass(ApPayment.class);
		paymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));
		//Form Workflow
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		//Object to object
		DetachedCriteria dcOto = DetachedCriteria.forClass(ObjectToObject.class);
		dcOto.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));

		if(apPaymentId != null && apPaymentId != 0){
			paymentCriteria.add(Restrictions.ne(ApPayment.FIELD.id.name(), apPaymentId));
		}
		dcOto.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), parentObjectId));
		dcWorkflow.add(Restrictions.and(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID),
				Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.STALED_ID)));

		paymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ApPaymentLine.FIELD.ebObjectId.name(), dcOto));
		dc.add(Subqueries.propertyIn(ApPaymentLine.FIELD.apPaymentId.name(), paymentCriteria));
		return getAll(dc);
	}

	@Override
	public List<ApPaymentLine> getPaidInvoices(Integer invoiceId, Date asOfDate) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria paymentCriteria =  DetachedCriteria.forClass(ApPayment.class);
		paymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));
		if (asOfDate != null) {
			paymentCriteria.add(Restrictions.le(ApPayment.FIELD.checkDate.name(), asOfDate));
		}

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		Criterion criterion = Restrictions.and(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID),
				Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.STALED_ID));
		dcWorkflow.add(Restrictions.and(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CREATED_ID), criterion));
		paymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ApPaymentLine.FIELD.apPaymentId.name(), paymentCriteria));

		DetachedCriteria oto = DetachedCriteria.forClass(ObjectToObject.class);
		oto.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		oto.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), OBJECT_TYPE_AP_PAYMENT_LINE));

		DetachedCriteria invDc = DetachedCriteria.forClass(APInvoice.class);
		invDc.setProjection(Projections.property(APInvoice.FIELD.ebObjectId.name()));
		if (invoiceId != null) {
			invDc.add(Restrictions.eq(APInvoice.FIELD.id.name(), invoiceId));
		}
		oto.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), invDc));
		dc.add(Subqueries.propertyIn(ApPaymentLine.FIELD.ebObjectId.name(), oto));
		return getAll(dc);
	}

	@Override
	public List<ApPaymentLineDto> getSapRefTrans(Integer invoiceEbObject, String ebObjectIds, Integer currencyId) {
		String excludeIds = "";
		if(ebObjectIds != null && !ebObjectIds.trim().isEmpty()) {
			String ids[] = ebObjectIds.split(";");
			int index = 0;
			for(String id : ids) {
				excludeIds += index == 0 ? id : "," + id;
				index++;
			}
		}
		String sql = "SELECT * FROM ("
				+ "SELECT SAP.EB_OBJECT_ID, 2 AS PAYMENT_LINE_TYPE, CONCAT('SAP-', SAP.SEQUENCE_NO) AS SEQUENCE_NO, 0 AS AMOUNT,"
				+ "COALESCE((SELECT IF(SUM(IF(APL.PAID_AMOUNT < 0, ABS(APL.PAID_AMOUNT), 0)) < SAP.AMOUNT, SUM(APL.PAID_AMOUNT) , 0) "
				+ "FROM AP_PAYMENT_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW APP_FW ON APP_FW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID "
				+ "WHERE APP_FW.CURRENT_STATUS_ID NOT IN (4, 32) "
				+ "AND OTO.FROM_OBJECT_ID = SAP.EB_OBJECT_ID),0) AS PAID_AMOUNT, SAP.CURRENCY_RATE_VALUE "
				+ "FROM SUPPLIER_ADVANCE_PAYMENT SAP "
				+ "INNER JOIN FORM_WORKFLOW SAP_FW ON SAP_FW.FORM_WORKFLOW_ID = SAP.FORM_WORKFLOW_ID "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = SAP.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = PO.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT API_OTO ON API_OTO.FROM_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API_GS ON API_GS.EB_OBJECT_ID = API_OTO.TO_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API_GS.FORM_WORKFLOW_ID "
				+ "WHERE API_GS.INVOICE_TYPE_ID IN (25,26,27,28,29,30) "
				+ "AND API_OTO.OR_TYPE_ID = 24002 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND SAP_FW.IS_COMPLETE = 1 "
				+ "AND SAP.EB_OBJECT_ID NOT IN ("+excludeIds+") "
				+ "AND API_GS.EB_OBJECT_ID = ? ";
		if(currencyId != null) {
			sql += "AND SAP.CURRENCY_ID = ? ";
		}
		sql += ") AS TBL WHERE AMOUNT != PAID_AMOUNT ";
		Session session = null;
		List<ApPaymentLineDto> dtos = new ArrayList<ApPaymentLineDto>();
		ApPaymentLineDto dto = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			query.setParameter(index, invoiceEbObject);
			if(currencyId != null) {
				query.setParameter(++index, currencyId);
			}
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("PAYMENT_LINE_TYPE", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("PAID_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("CURRENCY_RATE_VALUE", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					dto = new ApPaymentLineDto();
					dto.setRefenceObjectId((Integer) row[0]);
					dto.setApPaymentLineTypeId((Integer) row[1]);
					dto.setReferenceNumber((String) row[2]);
					double balance = (Double) row[3] - (Double) row[4];
					dto.setAmount(balance);
					dto.setCurrencyRateValue((Double) row[5]);
					dtos.add(dto);
					// Free up memory
					dto = null;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return dtos;
	}
}