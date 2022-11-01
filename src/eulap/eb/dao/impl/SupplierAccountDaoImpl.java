package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.Dao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.PaymentType;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.SupplierAcctHistDto;
import eulap.eb.web.dto.SupplierBalancesSummaryDto;

/**
 * Implementation class of {@link SupplierAccountDao}

 */

public class SupplierAccountDaoImpl extends BaseDao<SupplierAccount> implements SupplierAccountDao {
	private static final Logger LOGGER = Logger.getLogger(SupplierAccountDaoImpl.class);
	private static final double ZERO = 0.0;

	@Override
	protected Class<SupplierAccount> getDomainClass() {
		return SupplierAccount.class;
	}

	@Override
	public boolean isUniqueSupplierAccount(SupplierAccount supplierAcount) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(SupplierAccount.FIELD.name.name(), supplierAcount.getTrimmedName()));
		dc.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), supplierAcount.getCompanyId()));
		if (supplierAcount.getId() != 0) {
			dc.add(Restrictions.ne(SupplierAccount.FIELD.id.name(), supplierAcount.getId()));
		}
		DetachedCriteria supplierDc = DetachedCriteria.forClass(Supplier.class);
		supplierDc.setProjection(Projections.property(Supplier.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(SupplierAccount.FIELD.supplierId.name(), supplierDc));
		return getAll(dc).isEmpty();
	}

	@Override
	public List<SupplierAccount> getSupplierAccounts(int supplierId, User user) {
		return getAll(getSupAccounts(supplierId, user, null));
	}

	private DetachedCriteria getSupAccounts(int supplierId, User user, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		dc.add(Restrictions.eq(SupplierAccount.FIELD.supplierId.name(), supplierId));
		dc.add(Restrictions.eq(SupplierAccount.FIELD.active.name(), true));
		if (divisionId != null) {
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
			acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			dc.add(Subqueries.propertyIn(SupplierAccount.FIELD.defaultCreditACId.name(), acDc));
		}
		return dc;
	}

	public List<SupplierAccount> getSupplierAccounts(int supplierId, User user, Integer divisionId) {
		return getAll(getSupAccounts(supplierId, user, divisionId));
	}

	@Override
	public Page<SupplierAccount> searchSupplierAccount(String searchCriteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.createAlias("supplier", "supplier");
		dc.add(Restrictions.or(Restrictions.like(SupplierAccount.FIELD.name.name(), "%" + searchCriteria.trim() + "%"),
				Restrictions.like("supplier.name", "%" + searchCriteria.trim() + "%")));
		dc.addOrder(Order.asc(SupplierAccount.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<SupplierAccount> getSupplierAccounts(int supplierId, int companyId, Integer divisionId,
			boolean activeOnly) {
		return getSupplierAccounts(supplierId, null, companyId, divisionId, activeOnly);
	}

	@Override
	public List<SupplierAccount> getSupplierAccounts(int supplierId, Integer supplierAccountId, int companyId,
			Integer divisionId, boolean activeOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		if (activeOnly) {
			dc.add(Restrictions.eq(SupplierAccount.FIELD.active.name(), activeOnly));
		}
		if (supplierId > 0) {
			dc.add(Restrictions.eq(SupplierAccount.FIELD.supplierId.name(), supplierId));
		}
		if (supplierAccountId != null) {
			dc.add(Restrictions.eq(SupplierAccount.FIELD.id.name(), supplierAccountId));
		}
		if (divisionId != null) {
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
			acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			dc.add(Subqueries.propertyIn(SupplierAccount.FIELD.defaultCreditACId.name(), acDc));
		}
		return getAll(dc);
	}

	@Override
	public Page<SupplierAccount> searchSupplierAccounts(String supplierName, String supplierAcctName, Integer companyId,
			Integer termId, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (!supplierName.isEmpty()) {
			dc.createAlias("supplier", "supplier");
			dc.add(Restrictions.like("supplier.name", "%" + supplierName.trim() + "%"));
		}
		if (!supplierAcctName.isEmpty())
			dc.add(Restrictions.like(SupplierAccount.FIELD.name.name(), "%" + supplierAcctName.trim() + "%"));
		if (companyId != -1)
			dc.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		if (termId != -1)
			dc.add(Restrictions.eq(SupplierAccount.FIELD.termId.name(), termId));
		dc = DaoUtil.setSearchStatus(dc, SupplierAccount.FIELD.active.name(), status);
		dc.addOrder(Order.asc(SupplierAccount.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<SupplierAcctHistDto> getSupplierAcctHistoryData(int supplierAcctId, Date asOfDate, int divisionId,
			int currencyId, PageSetting pageSetting) {
		LOGGER.info("Retrieving the data needed to generate the Supplier Account History Report.");
		String sql = "SELECT ID, INV_TYPE_ID,SUPPLIER_ACCOUNT_ID,DIVISION_ID, DIVISION, DATE, CREATED_DATE, SOURCE, INV_NO, PO_NO, BMS_NO, REF_NO, "
				+ "DESCRIPTION, INVOICE_AMOUNT, PAYMENT_AMOUNT, TOTAL_LINE_AMOUNT, GAIN_LOSS, CURRENCY_ID, SUPPLIER_ID FROM ( "
				+ "SELECT AI.AP_INVOICE_ID AS ID, AI.INVOICE_TYPE_ID AS INV_TYPE_ID, AI.SUPPLIER_ACCOUNT_ID, "
				+ "D.DIVISION_ID, D.NAME AS DIVISION, AI.INVOICE_DATE AS DATE, AI.CREATED_DATE, "
				+ "'API' AS SOURCE, AI.INVOICE_NUMBER AS INV_NO, "
				+ "COALESCE((SELECT RRR.PO_NUMBER AS PO_NO FROM AP_INVOICE RR_API INNER JOIN R_RECEIVING_REPORT RRR ON RRR.AP_INVOICE_ID = RR_API.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RR_OTO ON RR_OTO.FROM_OBJECT_ID = RR_API.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE GS_API ON GS_API.EB_OBJECT_ID = RR_OTO.TO_OBJECT_ID "
				+ "WHERE GS_API.AP_INVOICE_ID = AI.AP_INVOICE_ID), '') AS PO_NO, AI.BMS_NUMBER AS BMS_NO, "
				+ "(CASE WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_NON_PO_CENTRAL + " AND " + InvoiceType.API_NON_PO_NSB8A + " THEN CONCAT('API NP-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_GS_CENTRAL + " AND " + InvoiceType.API_GS_NSB8A + " THEN CONCAT('API G/S-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.RTS_CENTRAL_TYPE_ID + " AND " + InvoiceType.RTS_NSB8A_TYPE_ID + " THEN CONCAT('RTS-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_CONF_CENTRAL + " AND " + InvoiceType.API_CONF_NSB8A + " THEN CONCAT('API C-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_IMPORT_CENTRAL + " AND " + InvoiceType.API_IMPORT_NSB8A + " THEN CONCAT('API I-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.AP_LOAN_CENTRAL + " AND " + InvoiceType.AP_LOAN_NSB8A + " THEN CONCAT('API L-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.PCR_CENTRAL + " AND " + InvoiceType.PCR_NSB8A + " THEN CONCAT('PCR-', AI.SEQUENCE_NO) "
				+ "END) AS REF_NO, AI.DESCRIPTION, ";
		if (currencyId == 1) {
			sql += "(CASE WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.RTS_CENTRAL_TYPE_ID + " AND " + InvoiceType.RTS_NSB8A_TYPE_ID + " "
				+ "THEN -AI.AMOUNT ELSE AI.AMOUNT END) AS INVOICE_AMOUNT, ";
		} else {
			sql += "(CASE WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.RTS_CENTRAL_TYPE_ID + " AND " + InvoiceType.RTS_NSB8A_TYPE_ID + " "
				+ "THEN -(AI.AMOUNT / AI.CURRENCY_RATE_VALUE) ELSE (AI.AMOUNT / AI.CURRENCY_RATE_VALUE) END) AS INVOICE_AMOUNT, ";
		}
		sql += "0.0 AS PAYMENT_AMOUNT, 0.0 AS TOTAL_LINE_AMOUNT, 0.0 AS GAIN_LOSS, AI.CURRENCY_ID AS CURRENCY_ID, AI.SUPPLIER_ID "
			+ "FROM AP_INVOICE AI "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
			+ "INNER JOIN DIVISION D ON D.DIVISION_ID = AI.DIVISION_ID "
			+ "WHERE AI.INVOICE_DATE <= ? "
			+ "AND FW.IS_COMPLETE = 1 "
			+ "AND AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_NON_PO_CENTRAL + " AND " + InvoiceType.PCR_NSB8A + " ";
		// AP payment
		sql += "UNION ALL "
			+ "SELECT AP.AP_PAYMENT_ID AS ID, 0 AS INV_TYPE_ID, AP.SUPPLIER_ACCOUNT_ID, D.DIVISION_ID, D.NAME AS DIVISION, "
			+ "AP.CHECK_DATE AS DATE, AP.CREATED_DATE, 'APP' AS SOURCE, AP.CHECK_NUMBER AS INV_NO, '' AS PO_NO, '' AS BMS_NO, "
			+ "CONCAT('APP-', AP.VOUCHER_NO) AS REF_NO, '' AS DESCRIPTION, 0.0 AS INVOICE_AMOUNT, ";
		if (currencyId == 1) {
			sql += "AP.AMOUNT AS PAYMENT_AMOUNT, COALESCE((SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL "
				+ "WHERE APL.AP_PAYMENT_ID = AP.AP_PAYMENT_ID), 0) AS TOTAL_LINE_AMOUNT, 0 AS GAIN_LOSS, ";
		} else {
			sql += "(AP.AMOUNT / AP.CURRENCY_RATE_VALUE) AS PAYMENT_AMOUNT, "
				+ "COALESCE((SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL "
				+ "WHERE APL.AP_PAYMENT_ID = AP.AP_PAYMENT_ID), 0) AS TOTAL_LINE_AMOUNT, (("
				+ "SELECT SUM(PAID_AMOUNT) FROM ("
				+ "SELECT APL.AP_PAYMENT_ID, (APL.PAID_AMOUNT/API_GS.CURRENCY_RATE_VALUE) AS PAID_AMOUNT "
				+ "FROM AP_PAYMENT_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O1 ON O2O1.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API_GS ON API_GS.EB_OBJECT_ID = O2O1.FROM_OBJECT_ID "
				+ "WHERE AP_PAYMENT_LINE_TYPE_ID = 1 "
				+ "UNION ALL "
				+ "SELECT APL.AP_PAYMENT_ID, (APL.PAID_AMOUNT/SAP.CURRENCY_RATE_VALUE) AS PAID_AMOUNT "
				+ "FROM AP_PAYMENT_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O1 ON O2O1.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN SUPPLIER_ADVANCE_PAYMENT SAP ON SAP.EB_OBJECT_ID = O2O1.FROM_OBJECT_ID "
				+ "WHERE AP_PAYMENT_LINE_TYPE_ID = 2) AS TBL "
				+ "WHERE AP_PAYMENT_ID = AP.AP_PAYMENT_ID) - (AP.AMOUNT / AP.CURRENCY_RATE_VALUE)) AS GAIN_LOSS, ";
		}
		sql += "AP.CURRENCY_ID, AP.SUPPLIER_ID "
			+ "FROM AP_PAYMENT AP "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID "
			+ "INNER JOIN DIVISION D ON D.DIVISION_ID = AP.DIVISION_ID "
			+ "WHERE AP.CHECK_DATE <= ? "
			+ "AND AP.PAYMENT_TYPE_ID = 1 "
			+ "AND FW.CURRENT_STATUS_ID NOT IN (1, 4, 32) "
			+ "UNION ALL "
			+ "SELECT DISTINCT SAP.SUPPLIER_ADVANCE_PAYMENT_ID AS ID, NULL AS INV_TYPE_ID, SAP.SUPPLIER_ACCOUNT_ID, D.DIVISION_ID, "
			+ "D.NAME AS DIVISION, SAP.DATE AS DATE, SAP.CREATED_DATE, 'SAP' AS SOURCE, NULL AS INV_NO, REFERENCE_NO AS PO_NO, "
			+ "SAP.BMS_NUMBER AS BMS_NO, CONCAT('SAP ', SAP.SEQUENCE_NO) AS REF_NO, NULL AS DESCRIPTION, ";
		if (currencyId == 1) {
			sql += "SAP.AMOUNT AS INVOICE_AMOUNT, ";
		} else {
			sql += "(SAP.AMOUNT / SAP.CURRENCY_RATE_VALUE) AS INVOICE_AMOUNT, ";
		}
		sql += "0.0 AS PAYMENT_AMOUNT, 0.0 AS TOTAL_LINE_AMOUNT, 0.0 AS GAIN_LOSS, SAP.CURRENCY_ID AS CURRENCY_ID, SAP.SUPPLIER_ID "
			+ "FROM SUPPLIER_ADVANCE_PAYMENT SAP "
			+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = SAP.EB_OBJECT_ID "
			+ "INNER JOIN AP_PAYMENT_LINE APL ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID "
			+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID "
			+ "INNER JOIN FORM_WORKFLOW APFW ON APFW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID "
			+ "AND APFW.CURRENT_STATUS_ID != 4 "
			+ "INNER JOIN FORM_WORKFLOW FW ON SAP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
			+ "INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
			+ "INNER JOIN COMPANY C ON SAP.COMPANY_ID = C.COMPANY_ID "
			+ "INNER JOIN DIVISION D ON SAP.DIVISION_ID = D.DIVISION_ID "
			+ "WHERE FW.IS_COMPLETE = 1 "
			+ "AND SAP.DATE <= ? ";
		sql += ") AS SUPPLIER_ACCT_HIST WHERE SUPPLIER_ACCOUNT_ID = " + supplierAcctId + " ";
		if (divisionId != -1) {
			sql += "AND DIVISION_ID = " + divisionId + " ";
		}
		if (currencyId != 1) {
			sql += "AND CURRENCY_ID = " + currencyId + " ";
		}
		sql += " ORDER BY DATE ASC, CREATED_DATE ASC";
		return getAllAsPage(sql, pageSetting, new SAHistoryReportHandler(asOfDate ,this));
	}

	private static class SAHistoryReportHandler implements QueryResultHandler<SupplierAcctHistDto> {
		private Logger logger = Logger.getLogger(SAHistoryReportHandler.class);
		private Dao<SupplierAccount> dao;
		private Date asOfDate;

		private SAHistoryReportHandler(Date asOfDate, Dao<SupplierAccount> dao) {
			this.dao = dao;
			this.asOfDate = asOfDate;
		}

		@Override
		public List<SupplierAcctHistDto> convert(List<Object[]> queryResult) {
			logger.info("Converting the retrieved data to list.");
			List<SupplierAcctHistDto> supplierAcctData = new ArrayList<SupplierAcctHistDto>();
			SupplierAcctHistDto dto = null;
			logger.debug("Looping through the retrieved data.");
			logger.trace("Retrieved " + queryResult.size() + " data.");
			for (Object[] rowResult : queryResult) {
				int counter = 0;
				dto = new SupplierAcctHistDto();
				Integer id = (Integer) rowResult[counter++];
				dto.setId(id);
				dto.setInvTypeId((Integer) rowResult[counter++]);
				dto.setSupplierAccountId((Integer) rowResult[counter++]);
				dto.setDivisionId((Integer) rowResult[counter++]);
				dto.setDivision((String) rowResult[counter++]);
				dto.setDate((Date) rowResult[counter++]);
				String source = (String) rowResult[counter++];
				dto.setSource(source);
				dto.setInvoiceNo((String) rowResult[counter++]);
				dto.setPoNo((String) rowResult[counter++]);
				dto.setBmsNo((String) rowResult[counter++]);
				dto.setReferenceNo((String) rowResult[counter++]);
				dto.setDescription((String) rowResult[counter++]);
				double invoiceAmount =(Double) rowResult[counter++];
				if (source.equalsIgnoreCase("SAP")) {
					dto.setSapAmount(invoiceAmount);
				} else {
					dto.setInvoiceAmount(invoiceAmount);
				}
				double headerAmount = NumberFormatUtil.roundOffTo2DecPlaces((Double) rowResult[counter++]);
				double totalLineAmt = NumberFormatUtil.roundOffTo2DecPlaces((Double) rowResult[counter++]);
				dto.setPaymentAmount(headerAmount);
				dto.setGainLoss(NumberFormatUtil.roundOffTo2DecPlaces(totalLineAmt - headerAmount));
				dto.setPaymentRef(getDescription(source, id));
				supplierAcctData.add(dto);
				// free up memory
				dto = null;
			}
			return supplierAcctData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int numberOfTables = 3;
			for (int i = 0; i < numberOfTables; i++) {
				query.setParameter(index, asOfDate);
				if (i < (numberOfTables-1))
					index++;
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			logger.info("Setting the scalars.");
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("INV_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("SUPPLIER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("DIVISION_ID", Hibernate.INTEGER);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("SOURCE", Hibernate.STRING);
			query.addScalar("INV_NO", Hibernate.STRING);
			query.addScalar("PO_NO", Hibernate.STRING);
			query.addScalar("BMS_NO", Hibernate.STRING);
			query.addScalar("REF_NO", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("INVOICE_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("PAYMENT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("TOTAL_LINE_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("GAIN_LOSS", Hibernate.DOUBLE);
		}

		private String getDescription(String source, final int id) {
			logger.info("Get the reference numbers.");
			String description = "";
			String sql = null;
			logger.debug("Source is from: " + source + " with ID: " + id);
			if (source.equalsIgnoreCase("API")) {
				sql = "SELECT APP.AP_PAYMENT_ID AS ID, APP.CHECK_NUMBER AS REF_NUMBER FROM AP_PAYMENT APP "
					+ "INNER JOIN AP_PAYMENT_LINE APPL ON APPL.AP_PAYMENT_ID = APP.AP_PAYMENT_ID "
					+ "INNER JOIN OBJECT_TO_OBJECT POTO ON POTO.TO_OBJECT_ID = APPL.EB_OBJECT_ID "
					+ "INNER JOIN AP_INVOICE GS_API ON GS_API.EB_OBJECT_ID = POTO.FROM_OBJECT_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON APP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != " + FormStatus.CANCELLED_ID + " AND GS_API.AP_INVOICE_ID = ?";
			} else if (source.equalsIgnoreCase("SAP")) {
				sql = "SELECT APP.AP_PAYMENT_ID AS ID, APP.CHECK_NUMBER AS REF_NUMBER FROM AP_PAYMENT APP "
					+ "INNER JOIN AP_PAYMENT_LINE APPL ON APPL.AP_PAYMENT_ID = APP.AP_PAYMENT_ID "
					+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APPL.EB_OBJECT_ID "
					+ "INNER JOIN SUPPLIER_ADVANCE_PAYMENT SAP ON SAP.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON APP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != " + FormStatus.CANCELLED_ID + " AND SAP.SUPPLIER_ADVANCE_PAYMENT_ID = ?";
			} else if (source.equalsIgnoreCase("APP")) {
				sql = "SELECT ID, TRIM(GROUP_CONCAT(DISTINCT ' ', REF_NUMBER)) AS REF_NUMBER FROM ("
					+ "SELECT APL.AP_PAYMENT_ID AS ID, "
					+ "(CASE WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_NON_PO_CENTRAL + " AND " + InvoiceType.API_NON_PO_NSB8A + " THEN CONCAT('API NP-', AI.SEQUENCE_NO) "
					+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_GS_CENTRAL + " AND " + InvoiceType.API_GS_NSB8A + " THEN CONCAT('API G/S-', AI.SEQUENCE_NO) "
					+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.RTS_CENTRAL_TYPE_ID + " AND " + InvoiceType.RTS_NSB8A_TYPE_ID + " THEN CONCAT('RTS-', AI.SEQUENCE_NO) "
					+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_CONF_CENTRAL + " AND " + InvoiceType.API_CONF_NSB8A + " THEN CONCAT('API C-', AI.SEQUENCE_NO) "
					+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_IMPORT_CENTRAL + " AND " + InvoiceType.API_IMPORT_NSB8A + " THEN CONCAT('API I-', AI.SEQUENCE_NO) "
					+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.AP_LOAN_CENTRAL + " AND " + InvoiceType.AP_LOAN_NSB8A + " THEN CONCAT('API L-', AI.SEQUENCE_NO) "
					+ "WHEN AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.PCR_CENTRAL + " AND " + InvoiceType.PCR_NSB8A + " THEN CONCAT('PCR-', AI.SEQUENCE_NO) "
					+ "END) AS REF_NUMBER "
					+ "FROM AP_PAYMENT_LINE APL "
					+ "INNER JOIN OBJECT_TO_OBJECT O2O1 ON O2O1.TO_OBJECT_ID = APL.EB_OBJECT_ID "
					+ "INNER JOIN AP_INVOICE AI ON AI.EB_OBJECT_ID = O2O1.FROM_OBJECT_ID "
					+ "WHERE APL.AP_PAYMENT_ID = ? "
					+ "AND AP_PAYMENT_LINE_TYPE_ID = 1 "
					+ "UNION ALL "
					+ "SELECT APL.AP_PAYMENT_ID AS ID, CONCAT('SAP-', SAP.SEQUENCE_NO) AS REF_NUMBER "
					+ "FROM AP_PAYMENT_LINE APL "
					+ "INNER JOIN OBJECT_TO_OBJECT O2O1 ON O2O1.TO_OBJECT_ID = APL.EB_OBJECT_ID "
					+ "INNER JOIN SUPPLIER_ADVANCE_PAYMENT SAP ON SAP.EB_OBJECT_ID = O2O1.FROM_OBJECT_ID "
					+ "WHERE APL.AP_PAYMENT_ID = ? "
					+ "AND AP_PAYMENT_LINE_TYPE_ID = 2) AS TBL ";
			} else {
				sql = "SELECT RTS_API.AP_INVOICE_ID AS ID, RTS_API.INVOICE_NUMBER AS REF_NUMBER FROM AP_INVOICE RTS_API  "
					+ "INNER JOIN FORM_WORKFLOW FW ON RTS_API.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
					+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = RTS_API.EB_OBJECT_ID "
					+ "INNER JOIN AP_INVOICE GS_API ON GS_API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
					+ "INNER JOIN OBJECT_TO_OBJECT RR_OTO ON RR_OTO.TO_OBJECT_ID = GS_API.EB_OBJECT_ID "
					+ "INNER JOIN AP_INVOICE RR_API ON RR_API.EB_OBJECT_ID = RR_OTO.FROM_OBJECT_ID "
					+ "INNER JOIN R_RETURN_TO_SUPPLIER RRTS ON RRTS.AP_INVOICE_ID = RTS_API.AP_INVOICE_ID "
					+ "INNER JOIN R_RECEIVING_REPORT RRR ON RRR.AP_INVOICE_ID = RR_API.AP_INVOICE_ID "
					+ "INNER JOIN OBJECT_TO_OBJECT POTO ON POTO.FROM_OBJECT_ID = RTS_API.EB_OBJECT_ID "
					+ "INNER JOIN AP_PAYMENT_LINE APPL ON APPL.EB_OBJECT_ID = POTO.TO_OBJECT_ID "
					+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APPL.AP_PAYMENT_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != " + FormStatus.CANCELLED_ID + " AND APP.AP_PAYMENT_ID = ?";
			}

			Collection<String> refNumbers = dao.get(sql, new QueryResultHandler<String>() {
				@Override
				public List<String> convert(List<Object[]> queryResult) {
					List<String> references = new ArrayList<String>();
					for (Object[] row : queryResult)
						references.add((String) row[1]);
					return references;
				}

				@Override
				public int setParamater(SQLQuery query) {
					int index = 0;
					query.setParameter(index, id);
					if (source.equalsIgnoreCase("APP")) {
						query.setParameter(++index, id);
					}
					return index;
				}

				@Override
				public void setScalars(SQLQuery query) {
					query.addScalar("ID", Hibernate.INTEGER);
					query.addScalar("REF_NUMBER", Hibernate.STRING);
				}
			});

			logger.info("Formatting " + refNumbers.size() + " reference numbers");
			for (String refNumber : refNumbers) {
				logger.debug("Formatting the reference number: " + refNumber);
				if (refNumber != null && !refNumber.trim().isEmpty()) {
					description += description == "" ? refNumber : " - " + refNumber;
				}
			}
			logger.debug("The description for the id: " + id + " is " + description);
			return description;
		}
	}

	@Override
	public double getBeginningBalance(Date asOfDate, Integer supplierAcctId, Integer divisionId, Integer currencyId) {
		LOGGER.info("Computing the beginning balance of supplier account: " + supplierAcctId + " as of: " + asOfDate);
		String supplierAcctBeginningBalSql = "SELECT SUM(INV_AMOUNT) AS TOTAL_INVOICE, SUM(RR_AMOUNT) AS TOTAL_RR, SUM(RTS_AMOUNT) AS TOTAL_RTS, "
				+ "SUM(PAY_AMOUNT) AS TOTAL_PAYMENT, DIVISION_ID, CURRENCY_ID, SUPPLIER_ACCOUNT_ID, SUPPLIER_ID FROM ("
				+ "SELECT AI.AMOUNT AS INV_AMOUNT, 0 AS RR_AMOUNT, 0 AS RTS_AMOUNT, 0 AS PAY_AMOUNT, AI.DIVISION_ID AS DIVISION_ID, AI.CURRENCY_ID AS CURRENCY_ID, "
				+ "AI.SUPPLIER_ACCOUNT_ID AS SUPPLIER_ACCOUNT_ID, AI.SUPPLIER_ID AS SUPPLIER_ID FROM AP_INVOICE AI "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "WHERE AI.INVOICE_DATE <= ? " + "AND FW.IS_COMPLETE  = 1 "
				+ "AND AI.INVOICE_TYPE_ID IN (1, 2, 3, 4, 10, 12, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30) "
				+ "UNION ALL "
				+ "SELECT 0 AS INV_AMOUNT, AI.AMOUNT AS RR_AMOUNT, 0 AS RTS_AMOUNT, 0 AS PAY_AMOUNT, AI.DIVISION_ID AS DIVISION_ID, AI.CURRENCY_ID AS CURRENCY_ID, "
				+ "AI.SUPPLIER_ACCOUNT_ID AS SUPPLIER_ACCOUNT_ID, AI.SUPPLIER_ID AS SUPPLIER_ID FROM AP_INVOICE AI "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "WHERE AI.INVOICE_DATE <= ? " + "AND FW.IS_COMPLETE  = 1 "
				+ "AND AI.INVOICE_TYPE_ID IN (5,8,9,13,14,15,16,17,18) " + "UNION ALL "
				+ "SELECT 0 AS INV_AMOUNT, 0 AS RR_AMOUNT, AI.AMOUNT AS RTS_AMOUNT, 0 AS PAY_AMOUNT, AI.DIVISION_ID AS DIVISION_ID, AI.CURRENCY_ID AS CURRENCY_ID, "
				+ "AI.SUPPLIER_ACCOUNT_ID AS SUPPLIER_ACCOUNT_ID, AI.SUPPLIER_ID AS SUPPLIER_ID FROM AP_INVOICE AI "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID " + "WHERE AI.GL_DATE <= ? "
				+ "AND FW.IS_COMPLETE  = 1 " + "AND (AI.INVOICE_TYPE_ID =" + InvoiceType.RTS_TYPE_ID + " "
				+ "OR AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.RTS_CENTRAL_TYPE_ID + " AND "
				+ InvoiceType.RTS_NSB8A_TYPE_ID + ") " + "UNION ALL "
				+ "SELECT 0 AS INV_AMOUNT, 0 AS RR_AMOUNT, AI.AMOUNT AS RTS_AMOUNT, 0 AS PAY_AMOUNT, AI.DIVISION_ID AS DIVISION_ID, AI.CURRENCY_ID AS CURRENCY_ID,"
				+ "AI.SUPPLIER_ACCOUNT_ID AS SUPPLIER_ACCOUNT_ID, AI.SUPPLIER_ID AS SUPPLIER_ID FROM AP_INVOICE AI "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID " + "WHERE AI.GL_DATE <= ? "
				+ "AND FW.IS_COMPLETE  = 1 " + "AND AI.INVOICE_TYPE_ID =" + InvoiceType.RTS_EB_TYPE_ID + " "
				+ "UNION ALL "
				+ "SELECT 0 AS INV_AMOUNT, 0 AS RR_AMOUNT, 0 AS RTS_AMOUNT, AP.AMOUNT AS PAY_AMOUNT, AP.DIVISION_ID AS DIVISION_ID, AP.CURRENCY_ID AS CURRENCY_ID, "
				+ "AP.SUPPLIER_ACCOUNT_ID AS SUPPLIER_ACCOUNT_ID, AP.SUPPLIER_ID AS SUPPLIER_ID FROM AP_PAYMENT AP "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID "
				+ "WHERE AP.CHECK_DATE <= ? " + "AND AP.PAYMENT_TYPE_ID = " + PaymentType.TYPE_AP_PAYMENT + " "
				+ "AND FW.CURRENT_STATUS_ID !=" + FormStatus.CANCELLED_ID + " "
				+ ") AS SUPPLIER_ACCT_BEG_BAL WHERE SUPPLIER_ACCOUNT_ID = " + supplierAcctId + " ";

		if (divisionId != -1) {
			supplierAcctBeginningBalSql += "AND DIVISION_ID = " + divisionId + " ";
		}
		Collection<Double> begBalance = get(supplierAcctBeginningBalSql, new BeginningBalanceHandler(asOfDate));
		return begBalance.iterator().next();
	}

	private static class BeginningBalanceHandler implements QueryResultHandler<Double> {
		private Logger handlerLogger = Logger.getLogger(BeginningBalanceHandler.class);
		private Date asOfDate;

		private BeginningBalanceHandler(Date asOfDate) {
			this.asOfDate = asOfDate;
		}

		@Override
		public List<Double> convert(List<Object[]> queryResult) {
			handlerLogger.info("Converting the retrieved data to list.");
			List<Double> begBalance = new ArrayList<Double>();
			for (Object[] row : queryResult) {
				int counter = 0;
				Double totalInvoices = (Double) row[counter++];
				handlerLogger.debug("Total Invoices: " + totalInvoices);
				if (totalInvoices == null) {
					handlerLogger.debug("Total invoices is null, sCURRENCY_et to zero.");
					begBalance.add(ZERO);
					break;
				}

				Double totalRRs = (Double) row[counter] == null ? ZERO : (Double) row[counter];
				handlerLogger.debug("Total RR: " + totalRRs);
				counter++;

				Double totalReturns = (Double) row[counter] == null ? ZERO : (Double) row[counter];
				handlerLogger.debug("Total RTS: " + totalReturns);
				counter++;

				Double totalPayments = (Double) row[counter] == null ? ZERO : (Double) row[counter];
				handlerLogger.debug("Total Payments: " + totalPayments);

				Double beginningBalance = totalInvoices + totalRRs - totalReturns - totalPayments;
				begBalance.add(beginningBalance);
				break; // Expecting one row only.
			}
			handlerLogger.info("Beginning balance is: " + begBalance);
			return begBalance;
		}

		@Override
		public int setParamater(SQLQuery query) {
			Integer index = 0;
			Integer numberOfTables = 5;

			for (int i = 0; i < numberOfTables; i++) {
				query.setParameter(index, asOfDate);
				if (i < (numberOfTables - 1))
					index++;
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("TOTAL_INVOICE", Hibernate.DOUBLE);
			query.addScalar("TOTAL_RR", Hibernate.DOUBLE);
			query.addScalar("TOTAL_RTS", Hibernate.DOUBLE);
			query.addScalar("TOTAL_PAYMENT", Hibernate.DOUBLE);
		}
	}

	@Override
	public SupplierAccount getSupplierAcctByNameAndCompany(Integer companyId, String supplierAcctName) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SupplierAccount.FIELD.name.name(),supplierAcctName.trim()));
		dc.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		return get(dc);
	}

	@Override
	public Page<SupplierBalancesSummaryDto> getSupplierAcctBalancesData(int companyId, int divisionId, int creditAccountId,
			int currencyId, Date asOfDate, int balanceOption, PageSetting pageSetting) {
		// AP invoice
		String sql = "SELECT SUPPLIER_NAME, SUM(INVOICE_AMOUNT) AS INVOICE_AMOUNT, SUM(PAYMENT_AMOUNT) AS PAYMENT_AMOUNT,"
				+ "SUM(GAIN_LOSS) AS GAIN_LOSS, COMPANY_ID, DIVISION_ID, SUPPLIER_ID FROM ( "
				+ "SELECT AI.AP_INVOICE_ID, AI.COMPANY_ID, AI.DIVISION_ID, AI.SUPPLIER_ID, S.NAME AS SUPPLIER_NAME, ";
		if (currencyId == Currency.DEFUALT_PHP_ID) {
			sql += "IF(AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.RTS_CENTRAL_TYPE_ID + " AND "
				+ InvoiceType.RTS_NSB8A_TYPE_ID + ", -AI.AMOUNT, AI.AMOUNT) ";
		} else {
			sql += "IF(AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.RTS_CENTRAL_TYPE_ID + " AND "
				+ InvoiceType.RTS_NSB8A_TYPE_ID + ", -(AI.AMOUNT / AI.CURRENCY_RATE_VALUE), (AI.AMOUNT / AI.CURRENCY_RATE_VALUE)) ";
		}
		sql += "AS INVOICE_AMOUNT, 0.0 AS PAYMENT_AMOUNT, 0.0 AS GAIN_LOSS "
			+ "FROM AP_INVOICE AI "
			+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = AI.SUPPLIER_ID "
			+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = AI.SUPPLIER_ACCOUNT_ID "
			+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = SA.DEFAULT_CREDIT_AC_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
			+ "WHERE FW.IS_COMPLETE = 1 "
			+ "AND AI.INVOICE_TYPE_ID BETWEEN " + InvoiceType.API_NON_PO_CENTRAL + " AND " + InvoiceType.PCR_NSB8A + " "
			+ "AND AI.COMPANY_ID = ? "
			+ (divisionId > 0 ? "AND AI.DIVISION_ID = ? " : "")
			+ (creditAccountId > 0 ? "AND AC.ACCOUNT_ID = ? " : "")
			+ "AND AI.INVOICE_DATE <= ? "
			+ (currencyId != Currency.DEFUALT_PHP_ID ? "AND AI.CURRENCY_ID = ? " : "");
		// AP payment
		sql += "UNION ALL "
			+ "SELECT AP.AP_PAYMENT_ID, AP.COMPANY_ID, AP.DIVISION_ID, AP.SUPPLIER_ID, S.NAME AS SUPPLIER_NAME, 0.0 AS INVOICE_AMOUNT, ";
		if (currencyId == Currency.DEFUALT_PHP_ID) {
			sql += "AP.AMOUNT AS PAYMENT_AMOUNT, (COALESCE((SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL "
				+ "WHERE APL.AP_PAYMENT_ID = AP.AP_PAYMENT_ID), 0) - AP.AMOUNT) AS GAIN_LOSS ";
		} else {
			sql += "(AP.AMOUNT / AP.CURRENCY_RATE_VALUE) AS PAYMENT_AMOUNT, ((SELECT SUM(PAID_AMOUNT) FROM ("
				+ "SELECT APL.AP_PAYMENT_ID, (APL.PAID_AMOUNT/API_GS.CURRENCY_RATE_VALUE) AS PAID_AMOUNT "
				+ "FROM AP_PAYMENT_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O1 ON O2O1.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API_GS ON API_GS.EB_OBJECT_ID = O2O1.FROM_OBJECT_ID "
				+ "WHERE AP_PAYMENT_LINE_TYPE_ID = 1 "
				+ "UNION ALL "
				+ "SELECT APL.AP_PAYMENT_ID, (APL.PAID_AMOUNT/SAP.CURRENCY_RATE_VALUE) AS PAID_AMOUNT "
				+ "FROM AP_PAYMENT_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O1 ON O2O1.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN SUPPLIER_ADVANCE_PAYMENT SAP ON SAP.EB_OBJECT_ID = O2O1.FROM_OBJECT_ID "
				+ "WHERE AP_PAYMENT_LINE_TYPE_ID = 2) AS TBL "
				+ "WHERE AP_PAYMENT_ID = AP.AP_PAYMENT_ID) - (AP.AMOUNT / AP.CURRENCY_RATE_VALUE)) AS GAIN_LOSS ";
		}
		sql += "FROM AP_PAYMENT AP "
			+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = AP.SUPPLIER_ID "
			+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = AP.SUPPLIER_ACCOUNT_ID "
			+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = SA.DEFAULT_CREDIT_AC_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID "
			+ "WHERE FW.CURRENT_STATUS_ID NOT IN (1, 4, 32) "
			+ "AND AP.PAYMENT_TYPE_ID = 1 "
			+ "AND AP.COMPANY_ID = ? "
			+ (divisionId > 0 ? "AND AP.DIVISION_ID = ? " : "")
			+ (creditAccountId > 0 ? "AND AC.ACCOUNT_ID = ? " : "")
			+ "AND AP.CHECK_DATE <= ? "
			+ (currencyId != Currency.DEFUALT_PHP_ID ? "AND AP.CURRENCY_ID = ? " : "")
			+ "UNION ALL "
			+ "SELECT DISTINCT SAP.SUPPLIER_ADVANCE_PAYMENT_ID, SAP.COMPANY_ID, SAP.DIVISION_ID, SAP.SUPPLIER_ID, S.NAME AS SUPPLIER_NAME, ";
		if (currencyId == Currency.DEFUALT_PHP_ID) {
			sql += "SAP.AMOUNT AS INVOICE_AMOUNT, ";
		} else {
			sql += "SAP.AMOUNT / SAP.CURRENCY_RATE_VALUE AS INVOICE_AMOUNT, ";
		}
		sql += "0.0 AS PAYMENT_AMOUNT, 0.0 AS GAIN_LOSS "
			+ "FROM SUPPLIER_ADVANCE_PAYMENT SAP "
			+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = SAP.SUPPLIER_ID "
			+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = SAP.SUPPLIER_ACCOUNT_ID "
			+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = SA.DEFAULT_CREDIT_AC_ID "
			+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = SAP.EB_OBJECT_ID "
			+ "INNER JOIN AP_PAYMENT_LINE APL ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID "
			+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID "
			+ "INNER JOIN FORM_WORKFLOW APFW ON (APFW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID AND APFW.CURRENT_STATUS_ID IN (4, 32)) "
			+ "INNER JOIN FORM_WORKFLOW FW ON SAP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
			+ "WHERE FW.IS_COMPLETE = 1 "
			+ "AND SAP.COMPANY_ID = ? "
			+ (divisionId > 0 ? "AND SAP.DIVISION_ID = ? " : "")
			+ (creditAccountId > 0 ? "AND AC.ACCOUNT_ID = ? " : "")
			+ "AND SAP.DATE <= ? "
			+ (currencyId != Currency.DEFUALT_PHP_ID ? "AND SAP.CURRENCY_ID = ? " : "")
			+ ") AS TBL GROUP BY SUPPLIER_ID ";
		if (balanceOption == 0) {
			sql += "HAVING ROUND((INVOICE_AMOUNT - (PAYMENT_AMOUNT + GAIN_LOSS)), 2) > 0 ";
		}
		sql += "ORDER BY SUPPLIER_NAME ";
		return getAllAsPage(sql, pageSetting, new SupplierAcctBalHandler(companyId, divisionId, creditAccountId, currencyId, asOfDate));
	}

	private static class SupplierAcctBalHandler implements QueryResultHandler<SupplierBalancesSummaryDto> {
		private int companyId;
		private int divisionId;
		private int creditAccountId;
		private int currencyId;
		private Date asOfDate;

		private SupplierAcctBalHandler(int companyId, int divisionId, int creditAccountId,
				int currencyId, Date asOfDate) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.creditAccountId = creditAccountId;
			this.currencyId = currencyId;
			this.asOfDate = asOfDate;
		}

		@Override
		public List<SupplierBalancesSummaryDto> convert(List<Object[]> queryResult) {
			List<SupplierBalancesSummaryDto> supplierBalancesDto = new ArrayList<SupplierBalancesSummaryDto>();
			for (Object[] rowResult : queryResult) {
				double totalInvoiceAmount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.convertBigDecimalToDouble((Double) rowResult[1]));
				double totalPaidAmount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.convertBigDecimalToDouble((Double) rowResult[2]));
				double totalGainLossAmount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.convertBigDecimalToDouble((Double) rowResult[3]));
				double balance = totalInvoiceAmount - (totalPaidAmount + totalGainLossAmount);
				supplierBalancesDto.add(SupplierBalancesSummaryDto.getInstanceOf((String) rowResult[0],
						totalInvoiceAmount, totalPaidAmount, totalGainLossAmount, balance));
			}
			return supplierBalancesDto;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int numberOfTables = 3;
			for (int i = 0; i < numberOfTables; i++) {
				query.setParameter(index, companyId);
				if (divisionId > 0) {
					query.setParameter(++index, divisionId);
				}
				if (creditAccountId > 0) {
					query.setParameter(++index, creditAccountId);
				}
				query.setParameter(++index, asOfDate);
				if (currencyId != 1) {
					query.setParameter(++index, currencyId);
				}
				if (i < (numberOfTables-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
			query.addScalar("INVOICE_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("PAYMENT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("GAIN_LOSS", Hibernate.DOUBLE);
		}
	}
}