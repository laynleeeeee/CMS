package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eulap.common.dao.BaseDao;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArReceiptLineDao;
import eulap.eb.domain.hibernate.ArReceiptLine;
import eulap.eb.domain.hibernate.ArReceiptLineType;
import eulap.eb.domain.hibernate.TransactionClassification;

/**
 * Implementing class of {@link ArReceiptLineDao}

 *
 */
public class ArReceiptLineDaoImpl extends BaseDao<ArReceiptLine> implements ArReceiptLineDao{

	@Override
	protected Class<ArReceiptLine> getDomainClass() {
		return ArReceiptLine.class;
	}

	@Override
	public Page<ArReceiptLine> getArReceiptLines(Integer companyId, Integer divisionId, Integer currencyId, 
			Integer arCustomerAcctId, String transNumber, String refObjIds, PageSetting pageSetting) {
		String sql = "SELECT * FROM ( "
				// Not collected AR invoice
				+ "SELECT CONCAT('ARI#' , ARI.SEQUENCE_NO, ' ' , ARI.DUE_DATE, ' ' , T.NAME) AS REFERENCE_NO,  "
				+ "ARI.AMOUNT, 0 AS PAID_AMOUNT, ARI.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, 1 AS RECEIPT_TYPE, "
				+ "0 AS ART_CLASSIFICATION, ARI.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_INVOICE ARI  "
				+ "INNER JOIN TERM T ON T.TERM_ID = ARI.TERM_ID  "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID  "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND ARI.EB_OBJECT_ID NOT IN ( "
				+ "SELECT OTO.FROM_OBJECT_ID FROM AR_RECEIPT_LINE ARL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24006 ) "
				+ "AND ARI.COMPANY_ID = ? "
				+ (divisionId != null ? "AND ARI.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND ARI.CURRENCY_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND ARI.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (transNumber.trim() != "" ? "AND CONCAT('ARI ', ARI.SEQUENCE_NO) LIKE ? " : "")
				+ "UNION ALL "
				// Partially collected AR invoice
				+ "SELECT CONCAT('ARI#' , ARI.SEQUENCE_NO, ' ' , ARI.DUE_DATE, ' ' , T.NAME) AS REFERENCE_NO,  "
				+ "ARI.AMOUNT, SUM(ARL.AMOUNT) AS PAID_AMOUNT, ARI.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, 1 AS RECEIPT_TYPE, "
				+ "0 AS ART_CLASSIFICATION, ARI.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_INVOICE ARI  "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ARI.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN TERM T ON T.TERM_ID = ARI.TERM_ID  "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID  "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND ARI.COMPANY_ID = ? "
				+ (divisionId != null ? "AND ARI.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND ARI.CURRENCY_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND ARI.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (transNumber.trim() != "" ? "AND CONCAT('ARI ', ARI.SEQUENCE_NO) LIKE ? " : "")
				+ "UNION ALL "
				// Not collected AR transaction
				+ "SELECT CONCAT('ART#' , ART.SEQUENCE_NO, ' ' , ART.DUE_DATE, ' ' , T.NAME) AS REFERENCE_NO, "
				+ "ABS(ART.AMOUNT), 0 AS PAID_AMOUNT, ART.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, 2 AS RECEIPT_TYPE, "
				+ "ART.TRANSACTION_CLASSIFICATION_ID AS ART_CLASSIFICATION, ART.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_TRANSACTION ART "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID "
				+ "INNER JOIN TERM T ON T.TERM_ID = ART.TERM_ID "
				+ "WHERE FW.IS_COMPLETE = 1  "
				+ "AND ART.AR_TRANSACTION_TYPE_ID >= 17 "
				+ "AND ART.AR_TRANSACTION_TYPE_ID <= 22 "
				+ "AND ART.EB_OBJECT_ID NOT IN ( "
				+ "SELECT OTO.FROM_OBJECT_ID FROM AR_RECEIPT_LINE ARL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24006 ) "
				+ "AND ART.COMPANY_ID = ? "
				+ (divisionId != null ? "AND ART.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND ART.CURRENCY_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND ART.CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (transNumber.trim() != "" ? "AND CONCAT('ART ', ART.SEQUENCE_NO) LIKE ? " : "")
				+ "GROUP BY ART.AR_TRANSACTION_ID "
				+ "UNION ALL "
				// Partially collected AR transaction
				+ "SELECT CONCAT('ART#' , ART.SEQUENCE_NO, ' ' , ART.DUE_DATE, ' ' , T.NAME) AS REFERENCE_NO, "
				+ "ABS(ART.AMOUNT), SUM(ABS(COALESCE(ARL.AMOUNT, 0))) AS PAID_AMOUNT, ART.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, 2 AS RECEIPT_TYPE, "
				+ "ART.TRANSACTION_CLASSIFICATION_ID AS ART_CLASSIFICATION, ART.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_TRANSACTION ART "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ART.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN TERM T ON T.TERM_ID = ART.TERM_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND ART.AR_TRANSACTION_TYPE_ID >= 17 "
				+ "AND ART.AR_TRANSACTION_TYPE_ID <= 22 "
				+ "AND ART.COMPANY_ID = ? "
				+ (divisionId != null ? "AND ART.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND ART.CURRENCY_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND ART.CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (transNumber.trim() != "" ? "AND CONCAT('ART ', ART.SEQUENCE_NO) LIKE ? " : "")
				+ "GROUP BY ART.AR_TRANSACTION_ID "
				+ "UNION ALL "
				// Not collected customer advance payment
				+ "SELECT CONCAT('CAP#' , CAP.CAP_NUMBER, ' ' , CAP.RECEIPT_DATE) AS REFERENCE_NO,  "
				+ "CAP.AMOUNT, 0 AS PAID_AMOUNT, CAP.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, 3 AS RECEIPT_TYPE, "
				+ "0 AS ART_CLASSIFICATION, CAP.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN FORM_WORKFLOW FW ON CAP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND CAP.EB_OBJECT_ID NOT IN ( "
				+ "SELECT OTO.FROM_OBJECT_ID FROM AR_RECEIPT_LINE ARL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24006) "
				+ "AND CAP.COMPANY_ID = ? "
				+ (divisionId != null ? "AND CAP.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND CAP.CURRENCY_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND CAP.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (transNumber.trim() != "" ? "AND CONCAT('CAP ', CAP.CAP_NUMBER) LIKE ? " : "")
				+ "UNION ALL "
				// Partially collected customer advance payment
				+ "SELECT CONCAT('CAP#' , CAP.CAP_NUMBER, ' ' , CAP.RECEIPT_DATE) AS REFERENCE_NO,  "
				+ "CAP.AMOUNT, SUM(ABS(COALESCE(ARL.AMOUNT, 0))) AS PAID_AMOUNT, CAP.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, "
				+ "3 AS RECEIPT_TYPE, 0 AS ART_CLASSIFICATION, CAP.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = CAP.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND CAP.COMPANY_ID = ? "
				+ (divisionId != null ? "AND CAP.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND CAP.CURRENCY_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND CAP.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (transNumber.trim() != "" ? "AND CONCAT('CAP ', CAP.CAP_NUMBER) LIKE ? " : "")
				+ "UNION ALL "
				// Not collected project retention
				+ "SELECT CONCAT('PR#' , PR.SEQUENCE_NO, ' ' , PR.DUE_DATE) AS REFERENCE_NO, "
				+ "PR.AMOUNT, 0 AS PAID_AMOUNT, PR.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, 4 AS RECEIPT_TYPE, "
				+ "0 AS ART_CLASSIFICATION, PR.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM PROJECT_RETENTION PR "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND PR.EB_OBJECT_ID NOT IN ( "
				+ "SELECT OTO.FROM_OBJECT_ID FROM AR_RECEIPT_LINE ARL  "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID  "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID  "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID  "
				+ "WHERE FW.CURRENT_STATUS_ID != 4  "
				+ "AND OTO.OR_TYPE_ID = 24006 ) "
				+ "AND PR.COMPANY_ID = ? "
				+ (divisionId != null ? "AND PR.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND PR.CURRENCY_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND PR.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (transNumber.trim() != "" ? "AND CONCAT('PR ', PR.SEQUENCE_NO) LIKE ? " : "")
				// Partially collected project retention
				+ "UNION ALL "
				+ "SELECT CONCAT('PR#' , PR.SEQUENCE_NO, ' ' , PR.DUE_DATE) AS REFERENCE_NO, " 
				+ "PR.AMOUNT, SUM(COALESCE(ARL.AMOUNT, 0)) AS PAID_AMOUNT, PR.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, "
				+ "ARL.AR_RECEIPT_LINE_TYPE_ID AS RECEIPT_TYPE, 0 AS ART_CLASSIFICATION, PR.CURRENCY_RATE_VALUE AS RATE " 
				+ "FROM PROJECT_RETENTION PR " 
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = PR.EB_OBJECT_ID  " 
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.EB_OBJECT_ID = OTO.TO_OBJECT_ID  " 
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID  " 
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID " 
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND PR.COMPANY_ID = ? "
				+ "AND ARL.AR_RECEIPT_LINE_TYPE_ID = 4 "
				+ (divisionId != null ? "AND PR.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND PR.CURRENCY_ID = ? " : "")
				+ (arCustomerAcctId != null ? "AND PR.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (transNumber.trim() != "" ? "AND CONCAT('PR ', PR.SEQUENCE_NO) LIKE ? " : "")
				+ "GROUP BY PR.PROJECT_RETENTION_ID "
				+ ") AS TBL WHERE ROUND(AMOUNT, 2) - ROUND(PAID_AMOUNT, 2) > 0 "
				+ (refObjIds.trim() != "" ? "AND REFERENCE_OBJECT_ID NOT IN (" + refObjIds + ")" : "");
		int noOfTbls = 8;
		return getAllAsPage(sql, pageSetting, new ArReceiptLinesHandler(companyId, divisionId, currencyId, 
				arCustomerAcctId, transNumber, noOfTbls));
	}

	private static class ArReceiptLinesHandler implements QueryResultHandler<ArReceiptLine> {
		private Integer companyId;
		private String transNumber;
		private Integer arCustAcctId;
		private Integer divisionId;
		private Integer currencyId;
		private int noOfTbls;

		private ArReceiptLinesHandler(Integer companyId, Integer divisionId, Integer currencyId, 
				Integer arCustAcctId, String transNumber, int noOfTbls) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.currencyId = currencyId;
			this.arCustAcctId = arCustAcctId;
			this.transNumber = transNumber;
			this.noOfTbls = noOfTbls;
		}

		@Override
		public List<ArReceiptLine> convert(List<Object[]> queryResult) {
			List<ArReceiptLine> arReceiptLines = new ArrayList<>();
			for (Object[] rowResult : queryResult) {
				ArReceiptLine arReceiptLine = new ArReceiptLine();
				int colNum = 0;
				arReceiptLine.setReferenceNo((String) rowResult[colNum]);
				int receiptTypeId = (Integer) rowResult[++colNum];
				int artClassificationId = (Integer) rowResult[++colNum];
				arReceiptLine.setArReceiptLineTypeId(receiptTypeId);
				arReceiptLine.setReferenceObjectId((Integer) rowResult[++colNum]);
				double amount = 0;
				double paidAmount = 0;
				if(receiptTypeId == 2 && artClassificationId == TransactionClassification.CREDIT_MEMO) {
					amount = NumberFormatUtil.negateAmount((Double) rowResult[++colNum]);
					paidAmount = NumberFormatUtil.negateAmount((Double) rowResult[++colNum]);
				} else {
					amount = (Double) rowResult[++colNum];
					paidAmount = (Double) rowResult[++colNum];
				}
				arReceiptLine.setAmount(amount - paidAmount);
				arReceiptLine.setCurrencyRateValue((Double) rowResult[++colNum]);
				arReceiptLines.add(arReceiptLine);
			}
			return arReceiptLines;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, companyId);
				if(divisionId != null) {
					query.setParameter(++index, divisionId);
				}
				if(currencyId != null) {
					query.setParameter(++index, currencyId);
				}
				if (arCustAcctId != -1) {
					query.setParameter(++index, arCustAcctId);
				}
				if(transNumber.trim() != "") {
					query.setParameter(++index, StringFormatUtil.appendWildCard(transNumber));
				}
				if (i < (noOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("REFERENCE_NO", Hibernate.STRING);
			query.addScalar("RECEIPT_TYPE", Hibernate.INTEGER);
			query.addScalar("ART_CLASSIFICATION", Hibernate.INTEGER);
			query.addScalar("REFERENCE_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("PAID_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("RATE", Hibernate.DOUBLE);
		}
	}

	@Override
	public List<ArReceiptLine> getArReceiptLines(Integer arReceiptId) {
		List<ArReceiptLine> arReceiptLines = new ArrayList<>();
		String sql = "SELECT * FROM ( "
				+ "SELECT  ARL.AR_RECEIPT_LINE_ID, ARL.AR_RECEIPT_ID,  ARL.EB_OBJECT_ID, "
				+ "ARL.AMOUNT AS AMOUNT, ARI.EB_OBJECT_ID AS REFERENCE_OBJECT_ID,  "
				+ "CONCAT('ARI# ', ARI.SEQUENCE_NO, ' ', ARI.DUE_DATE, ' ', T.NAME) AS REFERENCE_NO, "
				+ "ARI.REMARKS AS PARTICULAR, 1 AS RECEIPT_TYPE, ARL.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_RECEIPT_LINE ARL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN TERM T ON T.TERM_ID = ARI.TERM_ID "
				+ "WHERE OTO.OR_TYPE_ID = 24006 "
				+ "AND ARL.AR_RECEIPT_ID = ? "
				+ "UNION ALL "
				+ "SELECT ARL.AR_RECEIPT_LINE_ID, ARL.AR_RECEIPT_ID, ARL.EB_OBJECT_ID, ARL.AMOUNT AS AMOUNT, "
				+ "ART.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, "
				+ "CONCAT('ART# ', ART.SEQUENCE_NO, ' ', ART.DUE_DATE, ' ', T.NAME) AS REFERENCE_NO, "
				+ "ART.DESCRIPTION AS PARTICULAR, 2 AS RECEIPT_TYPE, ARL.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_RECEIPT_LINE ARL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN AR_TRANSACTION ART ON ART.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN TERM T ON T.TERM_ID = ART.TERM_ID "
				+ "WHERE OTO.OR_TYPE_ID = 24006 "
				+ "AND ART.AR_TRANSACTION_TYPE_ID >= 17 "
				+ "AND ART.AR_TRANSACTION_TYPE_ID <= 22 "
				+ "AND ARL.AR_RECEIPT_ID = ? "
				+ "GROUP BY ART.AR_TRANSACTION_ID "
				+ "UNION ALL "
				+ "SELECT ARL.AR_RECEIPT_LINE_ID, ARL.AR_RECEIPT_ID, ARL.EB_OBJECT_ID, ARL.AMOUNT AS AMOUNT, "
				+ "CAP.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, "
				+ "CONCAT('CAP# ', CAP.CAP_NUMBER, ' ', CAP.RECEIPT_DATE) AS REFERENCE_NO, "
				+ "'' AS PARTICULAR, 3 AS RECEIPT_TYPE, ARL.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_RECEIPT_LINE ARL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN CUSTOMER_ADVANCE_PAYMENT CAP ON CAP.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "WHERE OTO.OR_TYPE_ID = 24006 "
				+ "AND ARL.AR_RECEIPT_ID = ? "
				+ "UNION ALL "
				+ "SELECT ARL.AR_RECEIPT_LINE_ID, ARL.AR_RECEIPT_ID, ARL.EB_OBJECT_ID, ARL.AMOUNT AS AMOUNT, "
				+ "PR.EB_OBJECT_ID AS REFERENCE_OBJECT_ID,  "
				+ "CONCAT('PR# ', PR.SEQUENCE_NO, ' ', PR.DATE) AS REFERENCE_NO, '' AS PARTICULAR, 4 AS RECEIPT_TYPE, "
				+ "ARL.CURRENCY_RATE_VALUE AS RATE "
				+ "FROM AR_RECEIPT_LINE ARL  "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID  "
				+ "INNER JOIN PROJECT_RETENTION PR ON PR.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "WHERE OTO.OR_TYPE_ID = 24006 "
				+ "AND ARL.AR_RECEIPT_ID = ? "
				+ ") AS TBL";
		Session session = null;
		ArReceiptLine arReceiptLine = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			int totalNoOfTables = 4;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, arReceiptId);
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			query.addScalar("AR_RECEIPT_LINE_ID", Hibernate.INTEGER);
			query.addScalar("AR_RECEIPT_ID", Hibernate.INTEGER);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("REFERENCE_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("REFERENCE_NO", Hibernate.STRING);
			query.addScalar("PARTICULAR", Hibernate.STRING);
			query.addScalar("RECEIPT_TYPE", Hibernate.INTEGER);
			query.addScalar("RATE", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					int colNum = 0;
					arReceiptLine = new ArReceiptLine();
					arReceiptLine.setId((Integer) row[colNum]);
					arReceiptLine.setArReceiptId((Integer) row[++colNum]);
					arReceiptLine.setEbObjectId((Integer) row[++colNum]);
					arReceiptLine.setAmount((Double) row[++colNum]);
					arReceiptLine.setReferenceObjectId((Integer) row[++colNum]);
					arReceiptLine.setReferenceNo((String) row[++colNum]);
					arReceiptLine.setRemarks((String) row[++colNum]);
					arReceiptLine.setArReceiptLineTypeId((Integer) row[++colNum]);
					arReceiptLine.setCurrencyRateValue((Double) row[++colNum]);
					arReceiptLines.add(arReceiptLine);
					// Free up memory
					arReceiptLine = null;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return arReceiptLines;
	}

	@Override
	public List<ArReceiptLine> getCapReference(Integer arInvoiceObjectId, Integer companyId, Integer divisionId,
			Integer currencyId, String refObjIds) {
		List<ArReceiptLine> arReceiptLines = new ArrayList<>();
		String sql = "SELECT * FROM ( "
				+ "SELECT CONCAT('CAP#' , CAP.CAP_NUMBER, ' ' , CAP.RECEIPT_DATE, ' ', CAP.AMOUNT) AS REFERENCE_NO,  "
				+ "CAP.AMOUNT, 0 AS PAID_AMOUNT, CAP.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, 3 AS RECEIPT_TYPE   "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = CAP.SALES_ORDER_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND ARI.EB_OBJECT_ID = ? "
				+ "AND ARI.COMPANY_ID = ? "
				+ (divisionId != null ? "AND ARI.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND ARI.CURRENCY_ID = ? " : "")
				+ "AND CAP.EB_OBJECT_ID NOT IN ( "
				+ "SELECT OTO.FROM_OBJECT_ID FROM AR_RECEIPT_LINE ARL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24006 ) "
				+ "GROUP BY CAP.CUSTOMER_ADVANCE_PAYMENT_ID "
				+ "UNION ALL "
				+ "SELECT CONCAT('CAP#' , CAP.CAP_NUMBER, ' ' , CAP.RECEIPT_DATE, ' ', CAP.AMOUNT) AS REFERENCE_NO,  "
				+ "CAP.AMOUNT, SUM(ABS(COALESCE(ARL.AMOUNT, 0))) AS PAID_AMOUNT, CAP.EB_OBJECT_ID AS REFERENCE_OBJECT_ID, "
				+ "3 AS RECEIPT_TYPE "
				+ "FROM CUSTOMER_ADVANCE_PAYMENT CAP "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = CAP.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = CAP.SALES_ORDER_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND ARI.EB_OBJECT_ID = ? "
				+ "AND ARI.COMPANY_ID = ? "
				+ (divisionId != null ? "AND ARI.DIVISION_ID = ? " : "")
				+ (currencyId != null ? "AND ARI.CURRENCY_ID = ? " : "")
				+ "GROUP BY CAP.CUSTOMER_ADVANCE_PAYMENT_ID "
				+ ") AS TBL WHERE AMOUNT - PAID_AMOUNT > 0 "
				+ (refObjIds.trim() != "" ? "AND REFERENCE_OBJECT_ID NOT IN (" + refObjIds + ")" : "");
		Session session = null;
		ArReceiptLine arReceiptLine = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			int totalNoOfTables = 2;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, arInvoiceObjectId);
				query.setParameter(++index, companyId);
				if(divisionId != null) {
					query.setParameter(++index, divisionId);
				}
				if(currencyId != null) {
					query.setParameter(++index, currencyId);
				}
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			query.addScalar("REFERENCE_NO", Hibernate.STRING);
			query.addScalar("RECEIPT_TYPE", Hibernate.INTEGER);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("REFERENCE_OBJECT_ID", Hibernate.INTEGER);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					arReceiptLine = new ArReceiptLine();
					int colNum = 0;
					arReceiptLine.setReferenceNo((String) row[colNum]);
					arReceiptLine.setArReceiptLineTypeId((Integer) row[++colNum]);
					arReceiptLine.setAmount(NumberFormatUtil.negateAmount((Double) row[++colNum]));
					arReceiptLine.setReferenceObjectId((Integer) row[++colNum]);
					arReceiptLines.add(arReceiptLine);
					// Free up memory
					arReceiptLine = null;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return arReceiptLines;
	}

	@Override
	public double getRemainingBalance(Integer refObjecId, int arReceiptLineTypeId, Integer arReceiptId) {
		String refTable = "";
		if(ArReceiptLineType.AR_INVOICE == arReceiptLineTypeId) {
			refTable = "AR_INVOICE";
		} else if(ArReceiptLineType.AR_TRANSACTION == arReceiptLineTypeId) {
			refTable = "AR_TRANSACTION";
		} else if(ArReceiptLineType.CUSTOMER_ADVANCE_PAYMENT == arReceiptLineTypeId) {
			refTable = "CUSTOMER_ADVANCE_PAYMENT";
		} else if(ArReceiptLineType.PROJECT_RETENTION == arReceiptLineTypeId) {
			refTable = "PROJECT_RETENTION";
		}
		String sql = "SELECT OTO.FROM_OBJECT_ID, REF_TRAN.AMOUNT - SUM(ABS(COALESCE(ARL.AMOUNT, 0))) "
				+ "FROM AR_RECEIPT_LINE ARL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "INNER JOIN "+refTable+" REF_TRAN ON REF_TRAN.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24006 "
				+ "AND OTO.FROM_OBJECT_ID = ? "
				+ (arReceiptId != null ? "AND ARL.AR_RECEIPT_ID != ? " : "" )
				+ "GROUP BY OTO.FROM_OBJECT_ID "
				+ "UNION ALL "
				+ "SELECT REF_TRAN.EB_OBJECT_ID, REF_TRAN.AMOUNT AS BALANCE "
				+ "FROM "+refTable+" REF_TRAN "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = REF_TRAN.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND REF_TRAN.EB_OBJECT_ID = ? "
				+ "AND REF_TRAN.EB_OBJECT_ID NOT IN ( "
				+ "SELECT OTO.FROM_OBJECT_ID "
				+ "FROM AR_RECEIPT_LINE ARL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ARL.EB_OBJECT_ID  "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID  "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID  "
				+ "INNER JOIN AR_TRANSACTION REF_TRAN ON REF_TRAN.EB_OBJECT_ID = OTO.FROM_OBJECT_ID  "
				+ "WHERE FW.CURRENT_STATUS_ID != 4  "
				+ (arReceiptId != null ? "AND ARL.AR_RECEIPT_ID != ? " : "" )
				+ "AND OTO.OR_TYPE_ID = 24006 ) ";
				
		Session session = null;
		double balance = 0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			int totalNoOfTables = 2;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, refObjecId);
				if(arReceiptId != null) {
					query.setParameter(++index, arReceiptId);
				}
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					balance = (Double) row[1];
					break;//Expecting 1 row result.
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
