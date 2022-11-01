package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.RReceivingReportDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.PaymentStatus;
import eulap.eb.web.dto.RrRegisterDto;

/**
 * Implementation class of {@link RReceivingReportDao}

 */

public class RReceivingReportDaoImpl extends BaseDao<RReceivingReport> implements RReceivingReportDao {

	@Override
	protected Class<RReceivingReport> getDomainClass() {
		return RReceivingReport.class;
	}

	@Override
	public Integer generateMaxRRNumber(Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria apiDc = DetachedCriteria.forClass(APInvoice.class);
		apiDc.setProjection(Projections.property(APInvoice.FIELD.id.name()));
		apiDc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RR_TYPE_ID));
		dc.add(Restrictions.eq(RReceivingReport.FIELD.companyId.name(), companyId));
		apiDc.setProjection(Projections.max(APInvoice.FIELD.sequenceNumber.name()));
		dc.add(Subqueries.propertyIn(RReceivingReport.FIELD.apInvoiceId.name(), apiDc));
		List<Object> ret = getByProjection(dc);
		if (ret != null && ret.size() > 0) {
			Object retObj = ret.iterator().next();
			if (retObj != null)
				return ((Integer) retObj) + 1;
		}
		return 1;
	}

	private void getSequenceNo(DetachedCriteria apiDc, int typeId, String criteria) {
		apiDc.setProjection(Projections.property(APInvoice.FIELD.id.name()));
		apiDc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), typeId));
		if(!criteria.trim().isEmpty()) {
			if(StringFormatUtil.isNumeric(criteria)) {
				apiDc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ",
						criteria.trim(), Hibernate.STRING));
			}
		}
	}

	@Override
	public Page<RReceivingReport> searchReceivingReport(String searchCriteria,
			int typeId, PageSetting pageSetting) {
		DetachedCriteria rrDc = getDetachedCriteria();
		DetachedCriteria apiDc = DetachedCriteria.forClass(APInvoice.class);
		getSequenceNo(apiDc, typeId, searchCriteria);
		apiDc.addOrder(Order.asc(APInvoice.FIELD.sequenceNumber.name()));
		rrDc.add(Subqueries.propertyIn(RReceivingReport.FIELD.apInvoiceId.name(), apiDc));
		return getAll(rrDc, pageSetting);
	}

	@Override
	public Page<RReceivingReport> getReceivingReports(int typeId, FormPluginParam param) {
		DetachedCriteria rrDc = getDetachedCriteria();
		rrDc.createAlias("apInvoice", "ap");
		addUserCompany(rrDc, param.getUser());
		DetachedCriteria apiDc = DetachedCriteria.forClass(APInvoice.class);
		String searchCriteria = param.getSearchCriteria();
		if (typeId == InvoiceType.RR_TYPE_ID) {
			getSequenceNo(apiDc, typeId, searchCriteria);
		} else {
			apiDc.setProjection(Projections.property(APInvoice.FIELD.id.name()));
			rrDc.add(Restrictions.eq("ap.invoiceTypeId", typeId));
			if (!searchCriteria.trim().isEmpty()) {
				Criterion invoiceNoCrit = Restrictions.or(Restrictions.eq("ap.invoiceNumber", searchCriteria.trim()),
						 Restrictions.eq(RReceivingReport.FIELD.deliveryReceiptNo.name(), searchCriteria.trim()));
				if (StringFormatUtil.isNumeric(searchCriteria)) {
					Criterion seqNoCrit = Restrictions.eq("ap.sequenceNumber", Integer.valueOf(searchCriteria.trim()));
					rrDc.add(Restrictions.or(seqNoCrit, invoiceNoCrit));
				} else {
					rrDc.add(invoiceNoCrit);
				}
			}
		}
		// Workflow status
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if (param.getStatuses().size() > 0)
			addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), param.getStatuses());
		dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		apiDc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		rrDc.add(Subqueries.propertyIn(RReceivingReport.FIELD.apInvoiceId.name(), apiDc));
		//Order the list of RR by GL Date and Sequence No.
		rrDc.addOrder(Order.desc("ap.glDate"));
		rrDc.addOrder(Order.desc("ap.sequenceNumber"));
		return getAll(rrDc, param.getPageSetting());
	}

	@Override
	public Page<RReceivingReport> getReceivingReports(User user, Integer companyId,
			Integer warehouseId, Integer supplierId, String rrNumber,
			Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting) {
		DetachedCriteria rrDc = getDetachedCriteria();
		addUserCompany(rrDc, user);
		rrDc.createAlias("apInvoice", "ap");
		rrDc.add(Restrictions.eq("ap.invoiceTypeId", InvoiceType.RR_TYPE_ID));
		if(companyId != null)
			rrDc.add(Restrictions.eq("companyId", companyId));
		if(warehouseId != null) {
			if(warehouseId != 0)
				rrDc.add(Restrictions.eq("warehouseId", warehouseId));
		}
		if(supplierId != null)
			rrDc.add(Restrictions.eq("ap.supplierId", supplierId));
		if (dateFrom != null && dateTo != null)
			rrDc.add(Restrictions.between("ap.glDate", dateFrom, dateTo));
		else if (dateFrom != null)
			rrDc.add(Restrictions.ge("ap.glDate", dateFrom));
		else if (dateTo != null)
			rrDc.add(Restrictions.le("ap.glDate", dateTo));
		if(rrNumber != null) {
			if(StringFormatUtil.isNumeric(rrNumber.trim()))
				rrDc.add(Restrictions.like("ap.sequenceNumber", Integer.valueOf(rrNumber.trim())));
		}

		if (status != RReceivingReport.STATUS_ALL) {
			// Return to supplier item subquery.
			DetachedCriteria rtsItemDc = DetachedCriteria.forClass(RReturnToSupplierItem.class);
			rtsItemDc.setProjection(Projections.property(RReturnToSupplierItem.FIELD.rReceivingReportItemId.name()));
			rtsItemDc.add(Restrictions.isNotNull(RReturnToSupplierItem.FIELD.rReceivingReportItemId.name()));
			rtsItemDc.createAlias("apInvoice", "api");
			rtsItemDc.createAlias("api.formWorkflow", "fw");
			rtsItemDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
			
			// Receiving report item subquery.
			DetachedCriteria rrItemDc = DetachedCriteria.forClass(RReceivingReportItem.class);
			rrItemDc.setProjection(Projections.property(RReceivingReportItem.FIELD.apInvoiceId.name()));
			rrItemDc.add(Subqueries.propertyIn(RReceivingReportItem.FIELD.id.name(), rtsItemDc));
			
			if (status == RReceivingReport.STATUS_USED) {
				rrDc.add(Subqueries.propertyIn(RReceivingReport.FIELD.apInvoiceId.name(), rrItemDc));
			} else if (status == RReceivingReport.STATUS_UNUSED) {
				rrDc.add(Subqueries.propertyNotIn(RReceivingReport.FIELD.apInvoiceId.name(), rrItemDc));
			}
		}

		rrDc.createAlias("ap.formWorkflow", "fw");
		rrDc.add(Restrictions.eq("fw.complete", true));
		rrDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		rrDc.addOrder(Order.desc("ap.glDate"));
		rrDc.addOrder(Order.desc("ap.sequenceNumber"));
		return getAll(rrDc, pageSetting);
	}

	@Override
	public RReceivingReport getRrByInvoiceId(int apInvoiceId) {
		DetachedCriteria rrDc = getDetachedCriteria();
		rrDc.add(Restrictions.eq(RReceivingReport.FIELD.apInvoiceId.name(), apInvoiceId));
		return get(rrDc);
	}

	@Override
	public Page<RrRegisterDto> getRrRegisterData(int companyId, int divisionId, int warehouseId, int supplierId,
			int supplierAcctId, Date dateFrom, Date dateTo, int termId, Double amountFrom, Double amountTo,
			int statusId, Integer paymentStatId, PageSetting pageSetting) {
		String sql = "SELECT DIVISION, RR_NO, GL_DATE, WAREHOUSE, PO_NO, BMS_NUMBER, SUPPLIER, SUPPLIER_ACCT, "
				+ "TERM, PO_COST, INVOICE_AMT, BALANCE, STATUS, REMARKS, GS_AMOUNT FROM ( "
				+ "SELECT D.NAME AS DIVISION, CONCAT('RR ', API.SEQUENCE_NO) AS RR_NO, API.GL_DATE, W.NAME AS WAREHOUSE, "
				+ "CONCAT('PO ', RR.PO_NUMBER) AS PO_NO, RR.BMS_NUMBER, S.NAME AS SUPPLIER, SA.NAME AS SUPPLIER_ACCT, "
				+ "COALESCE((SELECT SUM((COALESCE(RRI.UNIT_COST, 0) * RRI.QUANTITY) + COALESCE(RRI.VAT_AMOUNT, 0)) "
				+ "AS PO_COST FROM R_RECEIVING_REPORT_ITEM RRI "
				+ "WHERE RRI.AP_INVOICE_ID = RR.AP_INVOICE_ID), 0) + "
				+ "COALESCE((SELECT SUM((COALESCE(APL.AMOUNT,0) + COALESCE(APL.VAT_AMOUNT, 0)) ) "
				+ "FROM AP_INVOICE_LINE APL WHERE APL.AP_INVOICE_ID = RR.AP_INVOICE_ID), 0) AS PO_COST, "
				+ "T.NAME AS TERM, COALESCE(API_GS.AMOUNT, 0) AS INVOICE_AMT,"
				+ "ROUND(COALESCE(API_GS.AMOUNT, 0) - COALESCE((SELECT SUM(APPL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APPL "
				+ "INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APPL.AP_PAYMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW_APP ON FW_APP.FORM_WORKFLOW_ID = APP.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO2 ON OTO2.TO_OBJECT_ID = APPL.EB_OBJECT_ID "
				+ "WHERE FW_APP.CURRENT_STATUS_ID NOT IN(4, 1, 32) AND OTO2.FROM_OBJECT_ID = API_GS.EB_OBJECT_ID), 0), 2) AS BALANCE, "
				+ "FS.DESCRIPTION AS STATUS, IF(FW.CURRENT_STATUS_ID = 4, (SELECT FWL.COMMENT FROM FORM_WORKFLOW_LOG FWL "
				+ "WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID AND FWL.FORM_STATUS_ID = FW.CURRENT_STATUS_ID), '') AS REMARKS, "
				+ "COALESCE(API_GS.AMOUNT, 0) AS GS_AMOUNT "
				+ "FROM R_RECEIVING_REPORT RR "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RR.AP_INVOICE_ID "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = API.DIVISION_ID "
				+ "INNER JOIN WAREHOUSE W ON W.WAREHOUSE_ID = RR.WAREHOUSE_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN TERM T ON T.TERM_ID = SA.TERM_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
				+ "LEFT JOIN OBJECT_TO_OBJECT OTO1 ON (OTO1.FROM_OBJECT_ID = API.EB_OBJECT_ID AND OTO1.OR_TYPE_ID = 24002) "
				+ "LEFT JOIN AP_INVOICE API_GS ON API_GS.EB_OBJECT_ID = OTO1.TO_OBJECT_ID "
				+ "LEFT JOIN FORM_WORKFLOW FW_GS ON (FW_GS.FORM_WORKFLOW_ID = API_GS.FORM_WORKFLOW_ID AND FW_GS.CURRENT_STATUS_ID != 4) "
				+ "WHERE RR.COMPANY_ID = ? ";
		if (divisionId > 0) {
			sql += "AND RR.DIVISION_ID = ? ";
		}
		if (warehouseId > 0) {
			sql += "AND RR.WAREHOUSE_ID = ? ";
		}
		if (supplierId > 0) {
			sql += "AND API.SUPPLIER_ID = ? ";
		}
		if (supplierAcctId > 0) {
			sql += "AND API.SUPPLIER_ACCOUNT_ID = ? ";
		}
		if (termId > 0) {
			sql += "AND SA.TERM_ID = ? ";
		}
		if (statusId > 0) {
			sql += "AND FW.CURRENT_STATUS_ID = ? ";
		}
		if (amountTo != 0) {
			sql += "AND COALESCE(API_GS.AMOUNT, 0) BETWEEN ? AND ? ";
		}
		if (dateFrom != null && dateTo != null) {
			sql += "AND API.GL_DATE BETWEEN ? AND ? ";
		}
		sql += ") AS RR_REGISTER_TBL ";
		if (paymentStatId > 0) {
			if (paymentStatId == PaymentStatus.FULLY_PAID) {
				sql += "WHERE BALANCE = 0 AND GS_AMOUNT != 0";
			} else if (paymentStatId == PaymentStatus.PARTIALL_PAID) {
				sql += "WHERE (INVOICE_AMT != BALANCE AND BALANCE > 0) ";
			} else if (paymentStatId == PaymentStatus.UNPAID) {
				sql += "WHERE INVOICE_AMT = BALANCE ";
			}
		}
		return getAllAsPage(sql, pageSetting, new RrRegisterHandler(companyId, divisionId, warehouseId,
				supplierId, supplierAcctId, dateFrom, dateTo, termId, amountFrom, amountTo, statusId));
	}

	private static class RrRegisterHandler implements QueryResultHandler<RrRegisterDto> {
		private int companyId;
		private int divisionId;
		private int warehouseId;
		private int supplierId;
		private int supplierAcctId;
		private Date dateFrom;
		private Date dateTo;
		private int termId;
		private Double amountFrom;
		private Double amountTo;
		private int statusId;

		private RrRegisterHandler(int companyId, int divisionId, int warehouseId, int supplierId,
				int supplierAcctId, Date dateFrom, Date dateTo, int termId, Double amountFrom,
				Double amountTo, int statusId) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.warehouseId = warehouseId;
			this.supplierId = supplierId;
			this.supplierAcctId = supplierAcctId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.termId = termId;
			this.amountFrom = amountFrom;
			this.amountTo = amountTo;
			this.statusId = statusId;
		}

		@Override
		public List<RrRegisterDto> convert(List<Object[]> queryResult) {
			List<RrRegisterDto> rrRegisterDtos = new ArrayList<RrRegisterDto>();
			RrRegisterDto dto = null;
			for (Object[] row : queryResult) {
				dto = new RrRegisterDto();
				dto.setDivision((String) row[0]);
				dto.setRrNo((String) row[1]);
				dto.setGlDate((Date) row[2]);
				dto.setWarehouse((String) row[3]);
				dto.setPoNo((String) row[4]);
				dto.setBmsNo((String) row[5]);
				dto.setSupplier((String) row[6]);
				dto.setSupplierAcct((String) row[7]);
				dto.setTerm((String) row[8]);
				dto.setPoCost(NumberFormatUtil.convertBigDecimalToDouble(row[9]));
				dto.setInvoiceAmt(NumberFormatUtil.convertBigDecimalToDouble(row[10]));
				dto.setBalance(NumberFormatUtil.convertBigDecimalToDouble(row[11]));
				dto.setStatus((String) row[12]);
				dto.setRemarks((String) row[13]);
				rrRegisterDtos.add(dto);
			}
			return rrRegisterDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			if (divisionId > 0) {
				query.setParameter(++index, divisionId);
			}
			if (warehouseId > 0) {
				query.setParameter(++index, warehouseId);
			}
			if (supplierId > 0) {
				query.setParameter(++index, supplierId);
			}
			if (supplierAcctId > 0) {
				query.setParameter(++index, supplierAcctId);
			}
			if (termId > 0) {
				query.setParameter(++index, termId);
			}
			if (statusId > 0) {
				query.setParameter(++index, statusId);
			}
			if (amountTo != 0) {
				query.setParameter(++index, amountFrom);
				query.setParameter(++index, amountTo);
			}
			if (dateFrom != null && dateTo != null) {
				query.setParameter(++index, DateUtil.formatToSqlDate(dateFrom));
				query.setParameter(++index, DateUtil.formatToSqlDate(dateTo));
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("RR_NO", Hibernate.STRING);
			query.addScalar("GL_DATE", Hibernate.DATE);
			query.addScalar("WAREHOUSE", Hibernate.STRING);
			query.addScalar("PO_NO", Hibernate.STRING);
			query.addScalar("BMS_NUMBER", Hibernate.STRING);
			query.addScalar("SUPPLIER", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCT", Hibernate.STRING);
			query.addScalar("TERM", Hibernate.STRING);
			query.addScalar("PO_COST", Hibernate.DOUBLE);
			query.addScalar("INVOICE_AMT", Hibernate.DOUBLE);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			query.addScalar("STATUS", Hibernate.STRING);
			query.addScalar("REMARKS", Hibernate.STRING);
		}
	}
}
