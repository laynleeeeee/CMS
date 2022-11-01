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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.SupplierAdvPaymentDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.domain.hibernate.SupplierAdvancePaymentLine;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.report.SupplierAdvancePaymentAgingParam;
import eulap.eb.web.dto.PurchaseOrderDto;
import eulap.eb.web.dto.SupplierAdvPaymentRegstrDto;
import eulap.eb.web.dto.SupplierAdvancePaymentAgingDto;


/**
 * DAO implementation class for {@link SupplierAdvPaymentDao}

 */

public class SupplierAdvPaymentDaoImpl extends BaseDao<SupplierAdvancePayment> implements SupplierAdvPaymentDao {

	@Override
	protected Class<SupplierAdvancePayment> getDomainClass() {
		return SupplierAdvancePayment.class;
	}

	@Override
	public Page<PurchaseOrderDto> getPurchaseOrders(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAcctId, Integer poNumber, String bmsNumber, Date dateFrom, Date dateTo,
			PageSetting pageSetting) {
		String sql = "SELECT R_PURCHASE_ORDER_ID, PO_DATE, PO_NUMBER, BMS_NUMBER, SUPPLIER_NAME, SUPPLIER_ACCT_NAME, "
				+ "SUM(PO_AMOUNT) AS TOTAL_PO_AMOUNT, SUM(ADVANCE_PAYMENT) AS TOTAL_ADV_PAYMENT FROM ( "
				+ "SELECT PO.R_PURCHASE_ORDER_ID, PO.PO_DATE, PO.PO_NUMBER, PO.BMS_NUMBER, "
				+ "S.NAME AS SUPPLIER_NAME, SA.NAME AS SUPPLIER_ACCT_NAME, "
				+ "SUM(POI.QUANTITY * COALESCE(POI.UNIT_COST, 0) + COALESCE(POI.VAT_AMOUNT,0)) AS PO_AMOUNT, "
				+ "0 AS ADVANCE_PAYMENT "
				+ "FROM R_PURCHASE_ORDER_ITEM POI "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PO.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = PO.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND PO.COMPANY_ID = ? "
				+ "AND PO.DIVISION_ID = ? "
				+ (supplierId != null ? "AND PO.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND PO.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (poNumber != null ? "AND PO.PO_NUMBER = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND PO.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND PO.PO_DATE BETWEEN ? AND ? " : "")
				+ "GROUP BY PO.R_PURCHASE_ORDER_ID "
				+ "UNION ALL "
				+ "SELECT PO.R_PURCHASE_ORDER_ID, PO.PO_DATE, PO.PO_NUMBER, PO.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, SUM(POL.UP_AMOUNT * COALESCE(POL.QUANTITY, 0)) AS PO_AMOUNT, 0 AS ADVANCE_PAYMENT "
				+ "FROM PURCHASE_ORDER_LINE POL "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POL.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PO.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = PO.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND PO.COMPANY_ID = ? "
				+ "AND PO.DIVISION_ID = ? "
				+ (supplierId != null ? "AND PO.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND PO.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (poNumber != null ? "AND PO.PO_NUMBER = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND PO.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND PO.PO_DATE BETWEEN ? AND ? " : "")
				+ "GROUP BY PO.R_PURCHASE_ORDER_ID "
				+ "UNION ALL "
				+ "SELECT SAP.R_PURCHASE_ORDER_ID, PO.PO_DATE, PO.PO_NUMBER, PO.BMS_NUMBER, "
				+ "S.NAME AS SUPPLIER_NAME, SA.NAME AS SUPPLIER_ACCT_NAME, 0 AS PO_AMOUNT, "
				+ "SUM(SAP.AMOUNT) AS ADVANCE_PAYMENT "
				+ "FROM SUPPLIER_ADVANCE_PAYMENT SAP "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = SAP.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = SAP.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = SAP.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SAP.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND PO.COMPANY_ID = ? "
				+ "AND PO.DIVISION_ID = ? "
				+ (supplierId != null ? "AND PO.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND PO.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (poNumber != null ? "AND PO.PO_NUMBER = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND PO.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND PO.PO_DATE BETWEEN ? AND ? " : "")
				+ "GROUP BY SAP.R_PURCHASE_ORDER_ID "
				+ ") AS TBL GROUP BY R_PURCHASE_ORDER_ID "
				+ "HAVING ROUND((TOTAL_PO_AMOUNT - TOTAL_ADV_PAYMENT), 2) > 0 "
				+ "ORDER BY PO_DATE DESC, PO_NUMBER DESC";
		return getAllAsPage(sql, pageSetting, new PoAdvPaymentFormHandler(companyId, divisionId, supplierId,
				supplierAcctId, poNumber, bmsNumber, dateFrom, dateTo));
	}

	private static class PoAdvPaymentFormHandler implements QueryResultHandler<PurchaseOrderDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer supplierId;
		private Integer supplierAcctId;
		private Integer poNumber;
		private String bmsNumber;
		private Date dateFrom;
		private Date dateTo;

		private PoAdvPaymentFormHandler(Integer companyId, Integer divisionId, Integer supplierId,
				Integer supplierAcctId, Integer poNumber, String bmsNumber, Date dateFrom, Date dateTo) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.supplierId = supplierId;
			this.supplierAcctId = supplierAcctId;
			this.poNumber = poNumber;
			this.bmsNumber = bmsNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public List<PurchaseOrderDto> convert(List<Object[]> queryResult) {
			List<PurchaseOrderDto> poDtos = new ArrayList<PurchaseOrderDto>();
			PurchaseOrderDto dto = null;
			for (Object[] row : queryResult) {
				int index = 0;
				dto = new PurchaseOrderDto();
				dto.setPurchaseOrderId((Integer) row[index++]);
				dto.setPoDate((Date) row[index++]);
				dto.setPoNumber((Integer) row[index++]);
				dto.setBmsNumber((String) row[index++]);
				dto.setSupplierName((String) row[index++]);
				dto.setSupplierAcctName((String) row[index++]);
				poDtos.add(dto);

				// free up memory
				dto = null;
			}
			return poDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			Integer index = 0;
			int totalNoOfTables = 3;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, companyId);
				query.setParameter(++index, divisionId);
				if (supplierId != null) {
					query.setParameter(++index, supplierId);
				}
				if (supplierAcctId != null) {
					query.setParameter(++index, supplierAcctId);
				}
				if (poNumber != null) {
					query.setParameter(++index, poNumber);
				}
				if (bmsNumber != null && !bmsNumber.isEmpty()) {
					query.setParameter(++index, bmsNumber);
				}
				if (dateFrom != null && dateTo != null) {
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				}
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("R_PURCHASE_ORDER_ID", Hibernate.INTEGER);
			query.addScalar("PO_DATE", Hibernate.DATE);
			query.addScalar("PO_NUMBER", Hibernate.INTEGER);
			query.addScalar("BMS_NUMBER", Hibernate.STRING);
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCT_NAME", Hibernate.STRING);
		}
	}

	@Override
	public List<SupplierAdvancePayment> getPoAdvPayments(Integer advPaymentId, Integer rpurchaseOrderId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SupplierAdvancePayment.FIELD.rpurchaseOrderId.name(), rpurchaseOrderId));
		if (advPaymentId != null) {
			dc.add(Restrictions.ne(SupplierAdvancePayment.FIELD.id.name(), advPaymentId));
		}
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(SupplierAdvancePayment.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(dc);
	}

	@Override
	public Integer generateSequenceNo(Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(SupplierAdvancePayment.FIELD.sequenceNumber.name()));
		dc.add(Restrictions.eq(SupplierAdvancePayment.FIELD.companyId.name(), companyId));
		if (divisionId != null) {
			dc.add(Restrictions.eq(SupplierAdvancePayment.FIELD.divisionId.name(), divisionId));
		}
		return generateSeqNo(dc);
	}

	@Override
	public Page<SupplierAdvancePayment> getSupplierAdvPayments(int divisionId, FormPluginParam param) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, param.getUser());
		if (!param.getSearchCriteria().trim().isEmpty()) {
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", param.getSearchCriteria(), Hibernate.STRING));
		}
		dc.add(Restrictions.eq(SupplierAdvancePayment.FIELD.divisionId.name(), divisionId));
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if (param.getStatuses().size() > 0) {
			addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), param.getStatuses());
		}
		dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(SupplierAdvancePayment.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.addOrder(Order.desc(SupplierAdvancePayment.FIELD.sequenceNumber.name()));
		dc.addOrder(Order.desc(SupplierAdvancePayment.FIELD.date.name()));
		return getAll(dc, param.getPageSetting());
	}

	@Override
	public Page<SupplierAdvancePayment> searchSupplierAdvancePayments(int divisionId, String searchCriteria,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SupplierAdvancePayment.FIELD.divisionId.name(), divisionId));
		if(StringFormatUtil.isNumeric(searchCriteria)) {
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchCriteria.trim(), Hibernate.STRING));
		}
		dc.addOrder(Order.desc(SupplierAdvancePayment.FIELD.sequenceNumber.name()));
		dc.addOrder(Order.desc(SupplierAdvancePayment.FIELD.date.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public SupplierAdvancePayment getByChildEbObject(int childEbObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		//Object to object
		DetachedCriteria dcOto = DetachedCriteria.forClass(ObjectToObject.class);
		dcOto.setProjection(Projections.property(ObjectToObject.FIELDS.fromObjectId.name()));
		dcOto.add(Restrictions.eq(ObjectToObject.FIELDS.toObjectId.name(), childEbObjectId));
		dc.add(Subqueries.propertyIn(SupplierAdvancePayment.FIELD.ebObjectId.name(), dcOto));
		return get(dc);
	}

	@Override
	public List<SupplierAdvancePayment> getSapBySapObjectId(Integer objectId) {
		DetachedCriteria dc = getDetachedCriteria();
		//SAP Line
		DetachedCriteria sapLineDc = DetachedCriteria.forClass(SupplierAdvancePaymentLine.class);
		sapLineDc.setProjection(Projections.property(SupplierAdvancePaymentLine.FIELD.supplierAdvPaymentId.name()));
		dc.add(Subqueries.propertyIn(SupplierAdvancePayment.FIELD.id.name(), sapLineDc));
		//Object to object
		DetachedCriteria dcOto = DetachedCriteria.forClass(ObjectToObject.class);
		dcOto.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		dcOto.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), objectId));
		dcOto.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), 24000));
		sapLineDc.add(Subqueries.propertyIn(SupplierAdvancePaymentLine.FIELD.ebObjectId.name(), dcOto));
		//Form Workflow
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(SupplierAdvancePayment.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc);
	}

	@Override
	public Page<SupplierAdvPaymentRegstrDto> getSupplierAdvPaymentRgstr(int companyId, int divisionId,
			int supplierId, int supplierAcctId, String bmsNumber, Date dateFrom, Date dateTo, int status,
			PageSetting pageSetting) {
		String sql = "SELECT D.NAME AS DIVISION, SAP.DATE AS DATE, S.NAME AS SUPPLIER, SA.NAME AS SUPPLIER_ACCOUNT, "
				+ "CONCAT('SAP ', SAP.SEQUENCE_NO) AS REFERENCE_NUMBER, "
				+ "SAP.BMS_NUMBER AS BMS_NUMBER, SAP.AMOUNT AS AMOUNT, "
				+ "SAP.AMOUNT - COALESCE((SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID "
				+ "WHERE APL.PAID_AMOUNT > 0 "
				+ "AND FW.CURRENT_STATUS_ID = " + FormStatus.CLEARED_ID + " "
				+ "AND OTO.OR_TYPE_ID = 24004 "
				+ "AND OTO.FROM_OBJECT_ID = SAP.EB_OBJECT_ID), 0) AS BALANCE, "
				+ "FS.DESCRIPTION AS STATUS, "
				+ "(SELECT FWL.COMMENT FROM FORM_WORKFLOW_LOG FWL WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "AND FWL.FORM_STATUS_ID = 4) AS CANCELLATION_REMARKS "
				+ "FROM SUPPLIER_ADVANCE_PAYMENT SAP "
				+ "INNER JOIN FORM_WORKFLOW FW ON SAP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
				+ "INNER JOIN COMPANY C ON SAP.COMPANY_ID = C.COMPANY_ID "
				+ "INNER JOIN DIVISION D ON SAP.DIVISION_ID = D.DIVISION_ID "
				+ "INNER JOIN SUPPLIER S ON SAP.SUPPLIER_ID = S.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SAP.SUPPLIER_ACCOUNT_ID = SA.SUPPLIER_ACCOUNT_ID "
				+ "WHERE SAP.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND SAP.DIVISION_ID = ? " : "" )
				+ (supplierId != -1 ? "AND SAP.SUPPLIER_ID = ? " : "" )
				+ (supplierAcctId != -1 ? "AND SAP.SUPPLIER_ACCOUNT_ID = ? " : "" )
				+ (bmsNumber != null ? "AND SAP.BMS_NUMBER LIKE ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND SAP.DATE BETWEEN ? AND ? " : "" )
				+ (status != -1 ? "AND FS.FORM_STATUS_ID = ? " : "" );

		SAPRegisterHandler sapRegHandler = new SAPRegisterHandler(companyId, divisionId, supplierId,
				supplierAcctId, StringFormatUtil.appendWildCard(bmsNumber), dateFrom, dateTo, status);
		return getAllAsPage(sql.toString(), pageSetting, sapRegHandler);
	}
	private static class SAPRegisterHandler implements QueryResultHandler<SupplierAdvPaymentRegstrDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer supplierId;
		private Integer supplierAcctId;
		private String bmsNumber;
		private Date dateFrom;
		private Date dateTo;
		private Integer status;

		private SAPRegisterHandler(Integer companyId, Integer divisionId, Integer supplierId,
				Integer supplierAcctId, String bmsNumber, Date dateFrom, Date dateTo, Integer status) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.supplierId = supplierId;
			this.supplierAcctId = supplierAcctId;
			this.bmsNumber = bmsNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.status = status;
		}

		@Override
		public List<SupplierAdvPaymentRegstrDto> convert(List<Object[]> queryResult) {
			List<SupplierAdvPaymentRegstrDto> saprDto = new ArrayList<SupplierAdvPaymentRegstrDto>();
			SupplierAdvPaymentRegstrDto dto = null;
			for (Object[] row : queryResult) {
				int index = 0;
				dto = new SupplierAdvPaymentRegstrDto();
				dto.setDivisionName((String) row[index++]);
				dto.setDate((Date) row[index++]);
				dto.setSupplier((String) row[index++]);
				dto.setSupplierAcct((String) row[index++]);
				dto.setReferenceNo((String) row[index++]);
				dto.setBmsNumber((String) row[index++]);
				dto.setAmount((Double) row[index++]);
				dto.setBalanceAmount((Double) row[index++]);
				dto.setStatus((String) row[index++]);
				dto.setCancellationRemarks((String) row[index++]);
				saprDto.add(dto);

				// free up memory
				dto = null;
			}
			return saprDto;
		}
		@Override
		public int setParamater(SQLQuery query) {
			Integer index = 0;
			query.setParameter(index, companyId);
			if (divisionId !=-1) {
				query.setParameter(++index, divisionId);
			}
			if (supplierId != -1) {
				query.setParameter(++index, supplierId);
			}
			if (supplierAcctId != -1) {
				query.setParameter(++index, supplierAcctId);
			}
			if (bmsNumber != null) {
				query.setParameter(++index, bmsNumber);
			}
			if (dateFrom != null && dateTo != null) {
				query.setParameter(++index, dateFrom);
				query.setParameter(++index, dateTo);
			}
			if (status != -1) {
				query.setParameter(++index, status);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("SUPPLIER", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCOUNT", Hibernate.STRING);
			query.addScalar("REFERENCE_NUMBER", Hibernate.STRING);
			query.addScalar("BMS_NUMBER", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			query.addScalar("STATUS", Hibernate.STRING);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
		}
	}
	@Override
	public Page<SupplierAdvancePaymentAgingDto> generateSupplierAdvancePaymentAging(SupplierAdvancePaymentAgingParam param, PageSetting pageSetting) {
		SupplierAdvPaymentHandler handler = new SupplierAdvPaymentHandler(param);
		return executePagedSP("GET_SUPPLIER_ADVANCE_PAYMENT_AGING", pageSetting, handler, param.getCompanyId(), param.getDivisionId(), param.getSupplierId(),
				param.getSupplierAcctId(), param.getBmsNumber(), param.getDateTo(), param.getAgeBasis(), param.getStatusId());
	}

	private static class SupplierAdvPaymentHandler implements QueryResultHandler<SupplierAdvancePaymentAgingDto> {
		private SupplierAdvPaymentHandler(SupplierAdvancePaymentAgingParam param) {
		}
		@Override
		public List<SupplierAdvancePaymentAgingDto> convert(List<Object[]> queryResult) {
			List<SupplierAdvancePaymentAgingDto> supplierAdvancePaymentAging = new ArrayList<SupplierAdvancePaymentAgingDto>();
			SupplierAdvancePaymentAgingDto supplierAgingDto = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				supplierAgingDto = new SupplierAdvancePaymentAgingDto();
				supplierAgingDto.setCompanyName((String) rowResult[colNum++]);
				supplierAgingDto.setDivision((String) rowResult[colNum++]);
				supplierAgingDto.setDate((Date) rowResult[colNum++]);
				supplierAgingDto.setSupplier((String) rowResult[colNum++]);
				supplierAgingDto.setSupplierAccount((String) rowResult[colNum++]);
				supplierAgingDto.setInitialAmount((double) rowResult[colNum++]);
				supplierAgingDto.setRequestorName((String) rowResult[colNum++]);
				supplierAgingDto.setRefNumber((String) rowResult[colNum++]);
				supplierAgingDto.setBmsNumber((String) rowResult[colNum++]);
				supplierAgingDto.setPoNumber((Integer) rowResult[colNum++]);
				supplierAgingDto.setAmount((double) rowResult[colNum++]);
				supplierAgingDto.setStatus((String) rowResult[colNum++]);
				supplierAgingDto.setRange1To30((double) rowResult[colNum++]);
				supplierAgingDto.setRange31To60((double) rowResult[colNum++]);
				supplierAgingDto.setRange61To90((double) rowResult[colNum++]);
				supplierAgingDto.setRange91To120((double) rowResult[colNum++]);
				supplierAgingDto.setRange120ToUp((double) rowResult[colNum++]);
				supplierAgingDto.setCancellationRemarks((String) rowResult[colNum++]);
				supplierAdvancePaymentAging.add(supplierAgingDto);
			}
			return supplierAdvancePaymentAging;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("COMPANY", Hibernate.STRING);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("SUPPLIER", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCOUNT", Hibernate.STRING);
			query.addScalar("AMOUNT_INITIALLY", Hibernate.DOUBLE);
			query.addScalar("REQUESTOR", Hibernate.STRING);
			query.addScalar("REFERENCE_NO", Hibernate.STRING);
			query.addScalar("BMS_NUMBER", Hibernate.STRING);
			query.addScalar("PO_NUMBER", Hibernate.INTEGER);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("STATUS", Hibernate.STRING);
			query.addScalar("1_30_DAYS", Hibernate.DOUBLE);
			query.addScalar("31_60_DAYS", Hibernate.DOUBLE);
			query.addScalar("61_90_DAYS", Hibernate.DOUBLE);
			query.addScalar("91_120_DAYS", Hibernate.DOUBLE);
			query.addScalar("120_UP", Hibernate.DOUBLE);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public boolean isUsedBySAP(Integer poId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		dc.add(Subqueries.propertyIn(SupplierAdvancePayment.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Restrictions.eq(SupplierAdvancePayment.FIELD.rpurchaseOrderId.name(), poId));
		return getAll(dc).size() > 0;
	}
}