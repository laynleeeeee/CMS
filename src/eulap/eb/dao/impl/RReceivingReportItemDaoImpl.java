package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.RReceivingReportItemDao;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.web.dto.ItemUnitCostHistoryPerSupplier;
/**
 * Implementation class of {@link RReceivingReportItemDao}

 */
public class RReceivingReportItemDaoImpl extends BaseDao<RReceivingReportItem> implements RReceivingReportItemDao{

	@Override
	protected Class<RReceivingReportItem> getDomainClass() {
		return RReceivingReportItem.class;
	}

	@Override
	public List<RReceivingReportItem> getRrItems(Integer apInvoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(RReceivingReportItem.FIELD.apInvoiceId.name(), apInvoiceId));
		return getAll(dc);
	}

	@Override
	public Page<ItemUnitCostHistoryPerSupplier> generateItemUCHistPerSupplier(
			int companyId, int divisionId, int itemId, int supplierId, int supplierAccountId,
			Date dateFrom, Date dateTo, PageSetting pageSetting) {
		String sql = "SELECT DISTINCT R_RECEIVING_REPORT_ITEM_ID AS ID, API.GL_DATE AS DATE, S.NAME AS SUPPLIER, "
				+ "CONCAT(CONCAT('RR-', API.SEQUENCE_NO), ', ', IF (API.INVOICE_NUMBER != '' , "
				+ "API.INVOICE_NUMBER, RR.DELIVERY_RECEIPT_NO)) AS RR_NUMBER, "
				+ "RRI.UNIT_COST, D.NAME AS DIVISION_NAME FROM R_RECEIVING_REPORT_ITEM RRI "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RRI.AP_INVOICE_ID = RR.AP_INVOICE_ID "
				+ "INNER JOIN DIVISION D ON RR.DIVISION_ID = D.DIVISION_ID "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ID = S.SUPPLIER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND RR.COMPANY_ID = ? "
				+ (divisionId !=-1 ? "AND RR.DIVISION_ID = ? " : "")
				+ "AND RRI.ITEM_ID = ? "
				+ (supplierId != -1 ? "AND API.SUPPLIER_ID = ? " : "")
				+ (supplierAccountId != -1 ? "AND API.SUPPLIER_ACCOUNT_ID = ? " : "") 
				+ "AND API.GL_DATE BETWEEN ? AND ? "
				+ "ORDER BY API.GL_DATE, S.NAME, API.SEQUENCE_NO";
		IUCHPSHandler handler = new IUCHPSHandler(companyId, divisionId, itemId, supplierId, supplierAccountId, dateFrom,
				dateTo);
		return getAllAsPage(sql, pageSetting, handler);
	}
	
	private static class IUCHPSHandler implements QueryResultHandler<ItemUnitCostHistoryPerSupplier> {
		private int companyId;
		private int divisionId;
		private int itemId;
		private int supplierId;
		private int supplierAccountId;
		private Date dateFrom;
		private Date dateTo;

		private IUCHPSHandler (int companyId, int divisionId, int itemId, int supplierId, int supplierAccountId,
				Date dateFrom, Date dateTo) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.itemId = itemId;
			this.supplierId = supplierId;
			this.supplierAccountId = supplierAccountId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public List<ItemUnitCostHistoryPerSupplier> convert(
				List<Object[]> queryResult) {
			List<ItemUnitCostHistoryPerSupplier> itemUnitCostHistoryPerSuppliers = 
					new ArrayList<ItemUnitCostHistoryPerSupplier>();
			for (Object[] rowResult : queryResult) {
				int colNum = 1;
				String divisionName = (String) rowResult[colNum++];
				Date date = (Date) rowResult[colNum++];
				String supplier = (String) rowResult[colNum++];
				String rrNumber = (String) rowResult[colNum++];
				Double unitCost = (Double) rowResult[colNum++];
				itemUnitCostHistoryPerSuppliers.add(
						ItemUnitCostHistoryPerSupplier.getInstanceOf(divisionName, date, supplier, rrNumber, unitCost));
			}
			return itemUnitCostHistoryPerSuppliers;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index++, companyId);
			if(divisionId !=-1) {
				query.setParameter(index++, divisionId);
			}
			query.setParameter(index++, itemId);
			if (supplierId != -1) {
				query.setParameter(index++, supplierId);
			}
			if (supplierAccountId != -1) {
				query.setParameter(index++, supplierAccountId);
			}
			query.setParameter(index++, dateFrom);
			query.setParameter(index, dateTo);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("DIVISION_NAME", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("SUPPLIER", Hibernate.STRING);
			query.addScalar("RR_NUMBER", Hibernate.STRING);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
		}
	}

	private String getLatestUcSql(boolean isBySupplier, boolean isSerial) {
		String nonSerialSql = "SELECT R_RECEIVING_REPORT_ITEM_ID AS ID, CONCAT('RR-', API.SEQUENCE_NO) AS RR_NUMBER, "
				+ "RRI.UNIT_COST AS UNIT_COST, API.CREATED_DATE "
				+ "FROM R_RECEIVING_REPORT_ITEM RRI "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RRI.AP_INVOICE_ID = RR.AP_INVOICE_ID "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID ";
		String serialSql = "SELECT RRI.SERIAL_ITEM_ID AS ID, CONCAT('RR-', API.SEQUENCE_NO) AS RR_NUMBER,  "
				+ "RRI.UNIT_COST AS UNIT_COST, API.CREATED_DATE  "
				+ "FROM SERIAL_ITEM RRI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = RRI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = API.AP_INVOICE_ID ";
		StringBuilder latestUcSql = new StringBuilder("SELECT RR_NUMBER, CREATED_DATE, UNIT_COST FROM ( "
				+ (isSerial ? serialSql : nonSerialSql)
				+ (isBySupplier ? "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID " : "")
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND RRI.ITEM_ID = ? "
				+ "AND RR.WAREHOUSE_ID = ? "
				+ (isBySupplier ? "AND API.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ ") AS LATEST_UC ORDER BY CREATED_DATE DESC LIMIT 1 ");
		return latestUcSql.toString();
	}

	private double getLatestUnitCost(int itemId, int warehouseId, int supplierAcctId, boolean isBySupplier, boolean isSerial) {
		String latestUcSql = getLatestUcSql(isBySupplier, isSerial);
		Collection<Double> latestUcPerSupplier = get(latestUcSql, new LatestUcHandler(itemId, warehouseId, supplierAcctId, isBySupplier));
		if(!latestUcPerSupplier.isEmpty()) {
			return latestUcPerSupplier.iterator().next();
		}
		return 0;
	}

	@Override
	public double getLatestUcPerSupplierAcct(int itemId, int warehouseId, int supplierAcctId, boolean isSerial) {
		return getLatestUnitCost(itemId, warehouseId, supplierAcctId, true, isSerial);
	}

	private static class LatestUcHandler implements QueryResultHandler<Double> {
		private int itemId;
		private int warehouseId;
		private int supplierAcctId;
		private boolean isBySupplier;

		private LatestUcHandler (int itemId, int warehouseId, int supplierAcctId, boolean isBySupplier) {
			this.itemId = itemId;
			this.warehouseId = warehouseId;
			this.supplierAcctId = supplierAcctId;
			this.isBySupplier = isBySupplier;
		}

		@Override
		public List<Double> convert(List<Object[]> queryResult) {
			List<Double> ret = new ArrayList<Double>();
			for (Object[] row : queryResult) {
				Double latestUc = (Double) row[2];
				if (latestUc == null) {
					ret.add(0.0);
					break;
				}

				ret.add(latestUc);
				break; // Expecting one row only.
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			Integer index = 0;
			query.setParameter(index, itemId);
			query.setParameter(++index, warehouseId);
			if (isBySupplier) {
				query.setParameter(++index, supplierAcctId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("RR_NUMBER", Hibernate.STRING);
			query.addScalar("CREATED_DATE", Hibernate.TIMESTAMP);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
		}
	}

	@Override
	public double getTotalItemQtyByPO(Integer itemId, String poNumber) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(RReceivingReportItem.FIELD.itemId.name(), itemId));

		DetachedCriteria rrDc = DetachedCriteria.forClass(RReceivingReport.class);
		rrDc.setProjection(Projections.property(RReceivingReport.FIELD.apInvoiceId.name()));
		rrDc.add(Restrictions.eq(RReceivingReport.FIELD.poNumber.name(), poNumber));

		dc.add(Subqueries.propertyIn(RReceivingReportItem.FIELD.apInvoiceId.name(), rrDc));
		dc.setProjection(Projections.sum(RReceivingReportItem.FIELD.quantity.name()));
		return getBySumProjection(dc);
	}

	@Override
	public double getLatestUcPerWarehouse(int itemId, int warehouseId, boolean isSerial) {
		return getLatestUnitCost(itemId, warehouseId, -1, false, isSerial);
	}
}
