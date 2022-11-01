package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.RReturnToSupplierDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.ReturnToSupplierRegisterDto;

/**
 * Implementation class of {@link RReturnToSupplierDao}

 */
public class RReturnToSupplierDaoImpl extends BaseDao<RReturnToSupplier> implements RReturnToSupplierDao{

	@Override
	protected Class<RReturnToSupplier> getDomainClass() {
		return RReturnToSupplier.class;
	}

	@Override
	public Integer generateMaxRtsNumber(Integer companyId) {
		return generateSequenceNumber("rtsNumber", "companyId", companyId);
	}

	@Override
	public Page<RReturnToSupplier> searchReturnToSupplier(int invoiceTypeId, String searchCriteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria apiDc = DetachedCriteria.forClass(APInvoice.class);
		apiDc.setProjection(Projections.property(APInvoice.FIELD.id.name()));
		apiDc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
		if (StringFormatUtil.isNumeric(searchCriteria)) {
			apiDc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
					searchCriteria.trim(), Hibernate.STRING));
		}
		apiDc.addOrder(Order.asc(APInvoice.FIELD.sequenceNumber.name()));
		dc.add(Subqueries.propertyIn(RReturnToSupplier.FIELD.apInvoiceId.name(), apiDc));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<RReturnToSupplier> getReturnToSuppliers(int invoiceTypeId, FormPluginParam param) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, param.getUser());
		DetachedCriteria apiDc = DetachedCriteria.forClass(APInvoice.class);
		apiDc.setProjection(Projections.property(APInvoice.FIELD.id.name()));
		apiDc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
		if (StringFormatUtil.isNumeric(param.getSearchCriteria())) {
			apiDc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
					param.getSearchCriteria().trim(), Hibernate.STRING));
		}
		// Workflow status
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if (param.getStatuses().size() > 0)
			addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), param.getStatuses());
		dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		apiDc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(RReturnToSupplier.FIELD.apInvoiceId.name(), apiDc));
		//Order the list of RTS by GL Date and Sequence No.
		dc.createAlias("apInvoice", "ap");
		dc.addOrder(Order.desc("ap.glDate"));
		dc.addOrder(Order.desc("ap.sequenceNumber"));
		return getAll(dc, param.getPageSetting());
	}

	@Override
	public RReturnToSupplier getRtsByInvoiceId(int apInvoiceId) {
		DetachedCriteria rtsDc = getDetachedCriteria();
		rtsDc.add(Restrictions.eq(RReturnToSupplier.FIELD.apInvoiceId.name(), apInvoiceId));
		return get(rtsDc);
	}

	@Override
	public Page<RReturnToSupplier> getRTSReportRegister(int companyId,
			int warehouseId, int supplierId, int supplierAcctId,
			String strRrDateFrom, String strRrDateTo, Date rtsDateFrom, Date rtsDateTo,
			Double amountFrom, Double amountTo, int statusId,
			Integer paymentStatId, PageSetting pageSetting) {

		Date rrDateTo = DateUtil.parseDate(strRrDateTo);
		Date rrDateFrom = DateUtil.parseDate(strRrDateFrom);
		DetachedCriteria apDc = DetachedCriteria.forClass(APInvoice.class);
		apDc.setProjection(Projections.property(APInvoice.FIELD.id.name()));
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId!=-1){
			dc.add(Restrictions.eq(RReturnToSupplier.FIELD.companyId.name(), companyId));
		}
		if(warehouseId !=-1){
			dc.add(Restrictions.eq(RReturnToSupplier.FIELD.warehouseId.name(), warehouseId));
		}
		if(supplierId !=-1){
			apDc.add(Restrictions.eq(APInvoice.FIELD.supplierId.name(), supplierId));
		}
		if(supplierAcctId !=-1){
			apDc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAcctId));
		}
		if(rrDateTo !=null && rrDateFrom != null){
			apDc.add(Restrictions.between(APInvoice.FIELD.invoiceDate.name(), rrDateFrom, rrDateTo));
		}
		if(rtsDateFrom !=null && rtsDateTo != null){
			apDc.add(Restrictions.between(APInvoice.FIELD.glDate.name(), rtsDateFrom, rtsDateTo));
			dc.add(Subqueries.propertyIn(RReturnToSupplier.FIELD.apInvoiceId.name(), apDc));
		}
		if(amountTo != 0 && amountFrom != 0){
			apDc.add(Restrictions.between(APInvoice.FIELD.amount.name(), amountFrom, amountTo));
		}
		else if(amountTo != 0){
			apDc.add(Restrictions.le(APInvoice.FIELD.amount.name(), amountTo));
		}
		else if(amountFrom != 0){
			apDc.add(Restrictions.ge(APInvoice.FIELD.amount.name(), amountFrom));
		}
		if(statusId != -1){
			DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
			workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			workflowCriteria.add(Restrictions.eq(FormWorkflow.FIELD.currentStatusId.name(), statusId));
			apDc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), workflowCriteria));
		}
		apDc.addOrder(Order.desc(APInvoice.FIELD.sequenceNumber.name()));
		dc.add(Subqueries.propertyIn(RReturnToSupplier.FIELD.apInvoiceId.name(), apDc));
		return getAll(dc, pageSetting);
	}

	public List<ReturnToSupplierRegisterDto> getReturnToSupplierRegisterData(Integer companyId, Integer divisionId, Integer warehouseId,
		    Integer supplierId, Integer supplierAcountId, Date rtsDateFrom, Date rtsDateTo,  Date rrDateFrom, Date rrDateTo,
		    double amountFrom, double amountTo, Integer rtsStatusId, Integer paymentStatId, PageSetting pageSetting) {
			StringBuilder sqlBuilder = new
			StringBuilder("SELECT * FROM(");
			sqlBuilder.append("SELECT DISTINCT(RTS_API.AP_INVOICE_ID) AS INVOICE_ID, RTS_API.COMPANY_ID AS COMPANY_ID, D.DIVISION_ID AS DIVISION_ID, D.NAME AS DIVISION, RTS_API.SEQUENCE_NO AS RTS_NO, ");
			sqlBuilder.append("RR_API.SEQUENCE_NO AS RR_NO, RRR.PO_NUMBER AS PO_NO, RTS_API.INVOICE_NUMBER AS SI_SOA_NO, WH.WAREHOUSE_ID AS WAREHOUSE_ID, ");
			sqlBuilder.append("WH.NAME AS WAREHOUSE, S.SUPPLIER_ID AS SUPPLIER_ID, S.NAME AS SUPPLIER, SA.SUPPLIER_ACCOUNT_ID AS SUPPLIER_ACCOUNT_ID, ");
			sqlBuilder.append("SA.NAME AS SUPPLIER_ACCOUNT,  ");
			sqlBuilder.append("IF (FW.CURRENT_STATUS_ID !=4 ,RTS_API.AMOUNT , 0.0) AS AMOUNT, ");
			sqlBuilder.append("RTS_API.GL_DATE AS RTS_DATE, ");
			sqlBuilder.append("RR_API.GL_DATE AS RR_DATE, FW.CURRENT_STATUS_ID AS RTS_STATUS_ID, ");
			sqlBuilder.append("IF (FW.CURRENT_STATUS_ID =4, (SELECT FWL.COMMENT FROM FORM_WORKFLOW_LOG FWL ");
			sqlBuilder.append("WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID AND FWL.FORM_STATUS_ID = 4), '') AS CANCELLATION_REMARKS, ");
			sqlBuilder.append("(SELECT FS.DESCRIPTION FROM FORM_STATUS FS WHERE FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID ) AS FORM_STATUS, ");
			sqlBuilder.append("SUM(APPL.PAID_AMOUNT)  AS PAID_AMOUNT  ");
			sqlBuilder.append("FROM AP_INVOICE RTS_API ");
			sqlBuilder.append("INNER JOIN DIVISION D ON D.DIVISION_ID = RTS_API.DIVISION_ID ");
			sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = RTS_API.EB_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN AP_INVOICE GS_API ON GS_API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT RR_OTO ON RR_OTO.TO_OBJECT_ID = GS_API.EB_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN AP_INVOICE RR_API ON RR_API.EB_OBJECT_ID = RR_OTO.FROM_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN R_RETURN_TO_SUPPLIER RRTS ON RRTS.AP_INVOICE_ID = RTS_API.AP_INVOICE_ID ");
			sqlBuilder.append("INNER JOIN R_RECEIVING_REPORT RRR ON RRR.AP_INVOICE_ID = RR_API.AP_INVOICE_ID ");
			sqlBuilder.append("INNER JOIN WAREHOUSE WH ON WH.WAREHOUSE_ID = RRTS.WAREHOUSE_ID ");
			sqlBuilder.append("INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = RTS_API.SUPPLIER_ID ");
			sqlBuilder.append("INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = RTS_API.SUPPLIER_ACCOUNT_ID ");
			sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = RTS_API.FORM_WORKFLOW_ID ");
			sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT POTO ON POTO.FROM_OBJECT_ID = RTS_API.EB_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN AP_PAYMENT_LINE APPL ON APPL.EB_OBJECT_ID = POTO.TO_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APPL.AP_PAYMENT_ID ");
			sqlBuilder.append("INNER JOIN FORM_WORKFLOW APFW ON APFW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID ");

			sqlBuilder.append("WHERE RTS_API.INVOICE_TYPE_ID IN (31,32,33,34,35,36) AND RR_API.INVOICE_TYPE_ID IN (13,14,15,16,17,18)"
					+ " AND APFW.CURRENT_STATUS_ID != 4 ");

			if(companyId != -1) {
				sqlBuilder.append("AND RTS_API.COMPANY_ID = ? ");
			}

			if(divisionId != -1) {
				sqlBuilder.append("AND RTS_API.DIVISION_ID = ? ");
			}

			if(warehouseId != -1) {
				sqlBuilder.append("AND WH.WAREHOUSE_ID = ? ");
			}

			if(supplierId != -1) {
				sqlBuilder.append("AND S.SUPPLIER_ID = ? ");
			}

			if(supplierAcountId != -1) {
				sqlBuilder.append("AND SA.SUPPLIER_ACCOUNT_ID = ? ");
			}

			sqlBuilder.append("AND (RTS_API.GL_DATE BETWEEN ? AND ?) ");

			if(rrDateFrom !=  null || rrDateTo != null) {
				sqlBuilder.append("AND (RR_API.GL_DATE BETWEEN ? AND ?) ");
			}
			if(amountFrom != 0.0 || amountTo != 0.0) {
				sqlBuilder.append("AND (RTS_API.AMOUNT BETWEEN ? AND ?) ");
			}
			if(rtsStatusId != -1) {
				sqlBuilder.append("AND FW.CURRENT_STATUS_ID = ? ");
			}
			sqlBuilder.append("GROUP BY RTS_API.AP_INVOICE_ID ");
			sqlBuilder.append("UNION ALL ");

			sqlBuilder.append("SELECT RTS_API.AP_INVOICE_ID AS INVOICE_ID, RTS_API.COMPANY_ID AS COMPANY_ID, D.DIVISION_ID AS DIVISION_ID, D.NAME AS DIVISION, RTS_API.SEQUENCE_NO AS RTS_NO, ");
			sqlBuilder.append("RR_API.SEQUENCE_NO AS RR_NO, RRR.PO_NUMBER AS PO_NO, RTS_API.INVOICE_NUMBER AS SI_SOA_NO, WH.WAREHOUSE_ID AS WAREHOUSE_ID, ");
			sqlBuilder.append("WH.NAME AS WAREHOUSE, S.SUPPLIER_ID AS SUPPLIER_ID, S.NAME AS SUPPLIER, SA.SUPPLIER_ACCOUNT_ID AS SUPPLIER_ACCOUNT_ID, ");
			sqlBuilder.append("SA.NAME AS SUPPLIER_ACCOUNT,  ");
			sqlBuilder.append("IF (FW.CURRENT_STATUS_ID !=4 ,RTS_API.AMOUNT , 0.0) AS AMOUNT, ");
			sqlBuilder.append("RTS_API.GL_DATE AS RTS_DATE, ");
			sqlBuilder.append("RR_API.GL_DATE AS RR_DATE, FW.CURRENT_STATUS_ID AS STATUS_ID, ");
			sqlBuilder.append("IF (FW.CURRENT_STATUS_ID =4, (SELECT FWL.COMMENT FROM FORM_WORKFLOW_LOG FWL ");
			sqlBuilder.append("WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID AND FWL.FORM_STATUS_ID = 4), '') AS CANCELLATION_REMARKS, ");
			sqlBuilder.append("(SELECT FS.DESCRIPTION FROM FORM_STATUS FS WHERE FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID ) AS FORM_STATUS, ");
			sqlBuilder.append("0.0 AS PAID_AMOUNT ");
			sqlBuilder.append("FROM AP_INVOICE RTS_API ");
			sqlBuilder.append("INNER JOIN DIVISION D ON D.DIVISION_ID = RTS_API.DIVISION_ID ");
			sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = RTS_API.EB_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN AP_INVOICE GS_API ON GS_API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT RR_OTO ON RR_OTO.TO_OBJECT_ID = GS_API.EB_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN AP_INVOICE RR_API ON RR_API.EB_OBJECT_ID = RR_OTO.FROM_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN R_RETURN_TO_SUPPLIER RRTS ON RRTS.AP_INVOICE_ID = RTS_API.AP_INVOICE_ID ");
			sqlBuilder.append("INNER JOIN R_RECEIVING_REPORT RRR ON RRR.AP_INVOICE_ID = RR_API.AP_INVOICE_ID ");
			sqlBuilder.append("INNER JOIN WAREHOUSE WH ON WH.WAREHOUSE_ID = RRTS.WAREHOUSE_ID ");
			sqlBuilder.append("INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = RTS_API.SUPPLIER_ID ");
			sqlBuilder.append("INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = RTS_API.SUPPLIER_ACCOUNT_ID ");
			sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = RTS_API.FORM_WORKFLOW_ID ");

			sqlBuilder.append("WHERE RTS_API.INVOICE_TYPE_ID IN (31,32,33,34,35,36) AND RR_API.INVOICE_TYPE_ID IN (13,14,15,16,17,18) ");
			sqlBuilder.append("AND RTS_API.EB_OBJECT_ID NOT IN (SELECT POTO.FROM_OBJECT_ID FROM AP_PAYMENT_LINE APPL INNER JOIN OBJECT_TO_OBJECT POTO ON POTO.TO_OBJECT_ID = APPL.EB_OBJECT_ID "
					+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APPL.AP_PAYMENT_ID "
					+ "INNER JOIN FORM_WORKFLOW APP_FW ON APP_FW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID "
					+ "WHERE APP_FW.CURRENT_STATUS_ID != 4 ) ");

			if(companyId != -1) {
				sqlBuilder.append("AND RTS_API.COMPANY_ID = ? ");
			}

			if(divisionId != -1) {
				sqlBuilder.append("AND RTS_API.DIVISION_ID = ? ");
			}

			if(warehouseId != -1) {
				sqlBuilder.append("AND WH.WAREHOUSE_ID = ? ");
			}

			if(supplierId != -1) {
				sqlBuilder.append("AND S.SUPPLIER_ID = ? ");
			}

			if(supplierAcountId != -1) {
				sqlBuilder.append("AND SA.SUPPLIER_ACCOUNT_ID = ? ");
			}

			sqlBuilder.append("AND (RTS_API.GL_DATE BETWEEN ? AND ?) ");

			if(rrDateFrom !=  null || rrDateTo != null) {
				sqlBuilder.append("AND (RR_API.GL_DATE BETWEEN ? AND ?) ");
			}
			if(amountFrom != 0.0 || amountTo != 0.0) {
				sqlBuilder.append("AND (RTS_API.AMOUNT BETWEEN ? AND ?) ");
			}
			if(rtsStatusId != -1) {
				sqlBuilder.append("AND FW.CURRENT_STATUS_ID = ? ");
			}

			sqlBuilder.append(") AS V_RTS_API ");

			if(paymentStatId != -1) {
				if (paymentStatId == 1 ) {
					sqlBuilder.append("WHERE AMOUNT != 0.0 AND PAID_AMOUNT != 0.0 AND ABS(PAID_AMOUNT) = ABS(AMOUNT) ");
				} else if (paymentStatId == 2 ) {
					sqlBuilder.append("WHERE AMOUNT != 0.0 AND PAID_AMOUNT != 0.0 AND PAID_AMOUNT != AMOUNT AND (ABS(PAID_AMOUNT) < ABS(AMOUNT)) ");
				} else if (paymentStatId == 3) {
					sqlBuilder.append("WHERE AMOUNT != 0.0 AND PAID_AMOUNT = 0 ");
				}
			}

			sqlBuilder.append("ORDER BY RTS_NO, RR_NO, PO_NO, RTS_DATE");

			return (List<ReturnToSupplierRegisterDto>) get(sqlBuilder.toString(),
					new ReturnToSupplierRegisterHandler(companyId, divisionId, warehouseId, supplierId, supplierAcountId, rtsDateFrom,rtsDateTo,
							rrDateFrom,rrDateTo, amountFrom, amountTo, rtsStatusId, paymentStatId));

	}

	private static class ReturnToSupplierRegisterHandler implements QueryResultHandler<ReturnToSupplierRegisterDto>{

		private final Integer companyId;
		private final Integer divisionId;
		private final Integer warehouseId;
		private final Integer supplierId;
		private final Integer supplierAcountId;
		private final Date rtsDateFrom;
		private final Date rtsDateTo;
		private final Date rrDateFrom;
		private final Date rrDateTo;
		private final double amountFrom;
		private final double amountTo;
		private final Integer rtsStatusId;

		private ReturnToSupplierRegisterHandler(final Integer companyId, final Integer divisionId, final Integer warehouseId,
				final Integer supplierId, final Integer supplierAcountId, final Date rtsDateFrom, final Date rtsDateTo, final Date rrDateFrom, final Date rrDateTo,
				final double amountFrom, final double amountTo, final Integer rtsStatusId, final Integer paymentStatId) {

			this.companyId = companyId;
			this.divisionId = divisionId;
			this.warehouseId = warehouseId;
			this.supplierId = supplierId;
			this.supplierAcountId = supplierAcountId;
			this.rtsDateFrom = rtsDateFrom;
			this.rtsDateTo = rtsDateTo;
			this.rrDateFrom = rrDateFrom;
			this.rrDateTo = rrDateTo;
			this.amountFrom = amountFrom;
			this.amountTo = amountTo;
			this.rtsStatusId = rtsStatusId;
		}

		@Override
		public List<ReturnToSupplierRegisterDto> convert(List<Object[]> queryResult) {
			List<ReturnToSupplierRegisterDto> returnToSupplierRegisterDtos = new ArrayList<>();
			ReturnToSupplierRegisterDto dto = null;
				for(Object[] row : queryResult) {
					int index = 0;
					dto = new ReturnToSupplierRegisterDto();
					dto.setApInvoiceId((Integer) row[index++]);
					dto.setDivisionId((Integer) row[index++]);
					dto.setDivision((String) row[index++]);
					dto.setRtsNo((Integer) row[index++]);
					dto.setRrNo((Integer) row[index++]);
					dto.setPoNo((Integer) row[index++]);
					dto.setSiSoaNo((String) row[index++]);
					dto.setwarehouseId((Integer) row[index++]);
					dto.setWarehouse((String) row[index++]);
					dto.setSupplierId((Integer) row[index++]);
					dto.setSupplier((String) row[index++]);
					dto.setSupplierAccountId((Integer) row[index++]);
					dto.setSupplierAccount((String) row[index++]);
					dto.setAmount((Double) row[index++]);
					dto.setRtsDate((Date) row[index++]);
					dto.setRrDate((Date) row[index++]);
					dto.setRtsStatusId((Integer) row[index++]);
					dto.setCancellationRemarks((String) row[index++]);
					dto.setFormStatus((String) row[index++]);
					dto.setPaidAmount((Double) row[index++]);
					returnToSupplierRegisterDtos.add(dto);
					// free up memory
					dto = null;
				}
			return returnToSupplierRegisterDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int totalNoOfTables = 2;

			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index++,companyId);
				if(divisionId != -1) { query.setParameter(index++, divisionId); }
				if(warehouseId != -1) { query.setParameter(index++, warehouseId); }
				if(supplierId != -1) { query.setParameter(index++, supplierId); }
				if(supplierAcountId != -1) { query.setParameter(index++, supplierAcountId); }
				query.setParameter(index++, rtsDateFrom);
				query.setParameter(index++, rtsDateTo);
				if(rrDateFrom !=  null || rrDateTo != null) {
					query.setParameter(index++, rrDateFrom);
					query.setParameter(index++, rrDateTo);
				}
				if(amountFrom != 0 || amountTo != 0) {
					query.setParameter(index++, amountFrom);
					query.setParameter(index++, amountTo);
				}
				if(rtsStatusId != -1) {query.setParameter(index++, rtsStatusId); }
			}

			return index;

		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("INVOICE_ID", Hibernate.INTEGER);
			query.addScalar("DIVISION_ID", Hibernate.INTEGER);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("RTS_NO", Hibernate.INTEGER);
			query.addScalar("RR_NO", Hibernate.INTEGER);
			query.addScalar("PO_NO", Hibernate.INTEGER);
			query.addScalar("SI_SOA_NO", Hibernate.STRING);
			query.addScalar("WAREHOUSE_ID", Hibernate.INTEGER);
			query.addScalar("WAREHOUSE", Hibernate.STRING);
			query.addScalar("SUPPLIER_ID", Hibernate.INTEGER);
			query.addScalar("SUPPLIER", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("SUPPLIER_ACCOUNT", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("RTS_DATE", Hibernate.DATE);
			query.addScalar("RR_DATE", Hibernate.DATE);
			query.addScalar("RTS_STATUS_ID", Hibernate.INTEGER);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
			query.addScalar("FORM_STATUS", Hibernate.STRING);
			query.addScalar("PAID_AMOUNT", Hibernate.DOUBLE);
		}
	}
}
