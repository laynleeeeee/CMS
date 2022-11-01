package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.SerialItemDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.web.dto.StockAdjustmentDto;

/**
 * Implementation class of {@link SerialItemDao}

 *
 */

public class SerialItemDaoImpl extends BaseDao<SerialItem> implements SerialItemDao {

	@Override
	protected Class<SerialItem> getDomainClass() {
		return SerialItem.class;
	}

	@Override
	public List<SerialItem> getByReferenctObjectId(Integer refEbObjectId, Integer orTypeId, boolean isCancelled) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!isCancelled) {
			dc.add(Restrictions.eq(SerialItem.FIELD.active.name(), true));
		}
		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refEbObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), orTypeId));
		dc.add(Subqueries.propertyIn(SerialItem.FIELD.ebObjectId.name(), ooDc));
		return getAll(dc);
	}

	@Override
	public List<SerialItem> getItemSerialNumbers(String serialNumber, Integer warehouseId, Integer itemId,
			boolean isExact, Integer referenceObjectId) {
		String formattedSerialNumber = isExact ? serialNumber.trim() : "%" + serialNumber.trim() + "%";
		List<Object> serialItems = executeSP("GET_AVAILABLE_SERIAL_NUMBER", itemId, warehouseId,
				formattedSerialNumber, isExact, referenceObjectId);

		List<SerialItem> items = new ArrayList<SerialItem>();
		SerialItem si = null;
		for (Object obj : serialItems) {
			Object[] row = (Object[]) obj;
			int index = 0;
			si = new SerialItem();
			si.setSerialNumber((String)row[index++]);
			si.setUnitCost((double)row[index++]);
			si.setEbObjectId((Integer) row[index]);
			items.add(si);
		}
		return items;
		
	}

	@Override
	public boolean isExistingOrUsedSerialNumber(String serialNumber,
			Integer serializedItemId, Integer itemId, boolean isExistingOrUsed) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SerialItem.FIELD.active.name(), true));
		dc.add(Restrictions.eq(SerialItem.FIELD.serialNumber.name(), StringFormatUtil.removeExtraWhiteSpaces(serialNumber)));
		if(itemId != null) {
			dc.add(Restrictions.eq(SerialItem.FIELD.itemId.name(), itemId));
		}
		if(serializedItemId != null){
			dc.add(Restrictions.ne(SerialItem.FIELD.id.name(), serializedItemId));
		}
		if(!isExistingOrUsed) {
			return getAll(dc).size() > 1;
		}
		return !getAll(dc).isEmpty();
	}

	@Override
	public SerialItem getSerialItemByReference(Integer toObjectId, Integer orTypeId, boolean isActiveOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		if (isActiveOnly) {
			dc.add(Restrictions.eq(SerialItem.FIELD.active.name(), true));
		}

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.fromObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.toObjectId.name(), toObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), orTypeId));

		dc.add(Subqueries.propertyIn(SerialItem.FIELD.ebObjectId.name(), ooDc));
		return get(dc);
	}

	@Override
	public Page<SerialItem> getLatestUpdateSerialItemsByRef(Integer refEbObjectId) {
		String sql = "SELECT SI.* FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "WHERE OTO.FROM_OBJECT_ID = ? AND "
				+ "UPDATED_DATE = (SELECT MAX(UPDATED_DATE) FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "WHERE OTO.FROM_OBJECT_ID = ?)";
		return getAllAsPage(sql, new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT), new SerialItemsHandler(refEbObjectId));
	}

	private static class SerialItemsHandler implements QueryResultHandler<SerialItem> {
		private int refEbObjectId;

		private SerialItemsHandler(int refEbObjectId) {
			this.refEbObjectId = refEbObjectId;
		}

		@Override
		public List<SerialItem> convert(List<Object[]> queryResult) {
			List<SerialItem> stockAdjustments = new ArrayList<SerialItem>();
			SerialItem si = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				si = new SerialItem();
				si.setId((Integer)rowResult[colNum++]);
				si.setEbObjectId((Integer)rowResult[colNum++]);
				si.setItemId((Integer)rowResult[colNum++]);
				si.setQuantity((Double)rowResult[colNum++]);
				si.setUnitCost((Double)rowResult[colNum++]);
				si.setWarehouseId((Integer)rowResult[colNum++]);
				si.setSerialNumber((String)rowResult[colNum++]);
				si.setItemSrpId((Integer)rowResult[colNum++]);
				si.setSrp((Double)rowResult[colNum++]);
				si.setItemDiscountId((Integer)rowResult[colNum++]);
				si.setDiscount((Double)rowResult[colNum++]);
				si.setItemAddOnId((Integer)rowResult[colNum++]);
				si.setAmount((Double)rowResult[colNum++]);
				stockAdjustments.add(si);
			}
			return stockAdjustments;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, refEbObjectId);
			query.setParameter(++index, refEbObjectId);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SERIAL_ITEM_ID", Hibernate.INTEGER);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("QUANTITY", Hibernate.DOUBLE);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("WAREHOUSE_ID", Hibernate.INTEGER);
			query.addScalar("SERIAL_NUMBER", Hibernate.STRING);
			query.addScalar("ITEM_SRP_ID", Hibernate.INTEGER);
			query.addScalar("SRP", Hibernate.DOUBLE);
			query.addScalar("ITEM_DISCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("DISCOUNT", Hibernate.DOUBLE);
			query.addScalar("ITEM_ADD_ON_ID", Hibernate.INTEGER);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
		}
	}

	@Override
	public List<SerialItem> getFormByRefSerialNo(String serialNumber, Integer warehouseId, Integer serialItemId) {
		List<Object> objects = executeSP("GET_REF_FORM_BY_SERIAL_NUMBER", serialNumber, warehouseId, serialItemId);
		List<SerialItem> serialItems = new ArrayList<SerialItem>();
		SerialItem serialItem = null;
		for (Object object : objects) {
			int colNo = 0;
			Object[] rowResult = (Object[]) object;
			serialItem = new SerialItem();
			serialItem.setSource((String) rowResult[colNo]);
			serialItem.setSerialNumber((String) rowResult[++colNo]);
			serialItems.add(serialItem);
		}
		return serialItems;
	}

	public SerialItem getSerialItemByO2ORelationship(Integer refObjectId, String serialNumber) {
		SerialItem serialItem = null;
		String sql = "SELECT SI.SERIAL_ITEM_ID, SI.SERIAL_NUMBER, SI.EB_OBJECT_ID, SI.UNIT_COST FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID =  SI.EB_OBJECT_ID "
				+ "INNER JOIN ORDER_SLIP_ITEM OSI ON OSI.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO2 ON OTO2.TO_OBJECT_ID = OSI.EB_OBJECT_ID "
				+ "INNER JOIN ORDER_SLIP OS ON OS.EB_OBJECT_ID = OTO2.FROM_OBJECT_ID "
				+ "WHERE OS.EB_OBJECT_ID = ? "
				+ "AND SI.SERIAL_NUMBER = ? "
				+ "AND SI.ACTIVE = 1";
		ArrayList<SerialItem> serialItems = (ArrayList<SerialItem>) get(sql, new SerialItemByO2ORelationshipHandler(refObjectId, serialNumber));
		if(serialItems != null && !serialItems.isEmpty()) {
			serialItem = serialItems.get(0);
		}
		return serialItem;
	}


	private static class SerialItemByO2ORelationshipHandler implements QueryResultHandler<SerialItem>{

		private Integer refObjectId;
		private String serialNumber;

		private SerialItemByO2ORelationshipHandler(Integer refObjectId, String serialNumber) {
			this.refObjectId = refObjectId;
			this.serialNumber = serialNumber;
		}
		@Override
		public List<SerialItem> convert(List<Object[]> queryResult) {
			List<SerialItem> serialItems = new ArrayList<>();
			SerialItem serialItem = null;
			for(Object[] row : queryResult) {
				int index = 0;
				serialItem = new SerialItem();
				serialItem.setId((int) row[index++]);
				serialItem.setSerialNumber((String) row[index++]);
				serialItem.setEbObjectId((Integer) row[index++]);
				serialItem.setUnitCost((Double) row[index]);
				serialItems.add(serialItem);
			}
			return serialItems;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, refObjectId);
			query.setParameter(++index, serialNumber);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SERIAL_ITEM_ID", Hibernate.INTEGER);
			query.addScalar("SERIAL_NUMBER", Hibernate.STRING);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
		}
	}

	@Override
	public SerialItem getSerialItem(String serialNumber, Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SerialItem.FIELD.active.name(), true));
		dc.add(Restrictions.eq(SerialItem.FIELD.serialNumber.name(), StringFormatUtil.removeExtraWhiteSpaces(serialNumber)));
		dc.add(Restrictions.eq(SerialItem.FIELD.itemId.name(), itemId));
		return get(dc);
	}

	@Override
	public double getAvailableStockFromPo(int invoiceId, Integer itemId, Integer poId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SerialItem.FIELD.active.name(), true));
		dc.add(Restrictions.eq(SerialItem.FIELD.itemId.name(), itemId));

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));

		DetachedCriteria apiDc = DetachedCriteria.forClass(APInvoice.class);
		apiDc.setProjection(Projections.property(APInvoice.FIELD.ebObjectId.name()));
		if(invoiceId != 0){
			apiDc.add(Restrictions.eq(APInvoice.FIELD.id.name(), invoiceId));
		}

		DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
		workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		workflowCriteria.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		DetachedCriteria ooPoDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooPoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));

		DetachedCriteria poDc = DetachedCriteria.forClass(RPurchaseOrder.class);
		poDc.setProjection(Projections.property(RPurchaseOrder.FIELD.ebObjectId.name()));
		poDc.add(Restrictions.eq(RPurchaseOrder.FIELD.id.name(), poId));

		ooPoDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), poDc));
		apiDc.add(Subqueries.propertyIn(APInvoice.FIELD.ebObjectId.name(), ooPoDc));
		apiDc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), workflowCriteria));
		ooDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), apiDc));
		dc.add(Subqueries.propertyIn(SerialItem.FIELD.ebObjectId.name(), ooDc));
		dc.setProjection(Projections.sum(SerialItem.FIELD.quantity.name()));
		return getBySumProjection(dc);
	}

	@Override
	public List<StockAdjustmentDto> getStockAdjustmentRegisterData(Integer companyId, Integer warehouseId,Integer divisionId,
		    Integer stockAdjustmentTypeId, Date dateFrom, Date dateTo, Integer formStatusId) {
			StringBuilder sqlBuilder = new
			StringBuilder("SELECT DIVISION,STOCK_ADJUSTMENT_ID, DATE, SEQUENCE_NO, STOCK_CODE, DESCRIPTION, BMS_NUMBER, "
					+ "REMARKS, UOM, QUANTITY, UNIT_COST, CANCELLATION_REMARKS, FORM_STATUS FROM ( "
					+ "SELECT D.NAME AS DIVISION, SA.STOCK_ADJUSTMENT_ID, SA.SA_DATE AS DATE, "
					+ "CONCAT(IF(SA.STOCK_ADJUSTMENT_CLASSIFICATION_ID IN (1,3,5,6,7,8,9,10), 'SAI ', 'SAO '), SA.SA_NUMBER) AS SEQUENCE_NO, "
					+ "I.STOCK_CODE, I.DESCRIPTION, SA.BMS_NUMBER, SA.REMARKS, UOM.NAME AS UOM, SAI.QUANTITY, "
					+ "COALESCE(SAI.UNIT_COST, 0) AS UNIT_COST, ");
			sqlBuilder.append("IF(FW.CURRENT_STATUS_ID = 4, (SELECT FWL.COMMENT FROM FORM_WORKFLOW_LOG FWL "
					+ "WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID AND FWL.FORM_STATUS_ID = 4), '') AS CANCELLATION_REMARKS, ");
			sqlBuilder.append("FS.DESCRIPTION AS FORM_STATUS ");
			sqlBuilder.append("FROM STOCK_ADJUSTMENT_ITEM SAI ");
			sqlBuilder.append("INNER JOIN STOCK_ADJUSTMENT SA ON SA.STOCK_ADJUSTMENT_ID = SAI.STOCK_ADJUSTMENT_ID ");
			sqlBuilder.append("INNER JOIN ITEM I ON I.ITEM_ID = SAI.ITEM_ID ");
			sqlBuilder.append("INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID ");
			sqlBuilder.append("INNER JOIN STOCK_ADJUSTMENT_TYPE SAT ON SAT.STOCK_ADJUSTMENT_TYPE_ID = SA.STOCK_ADJUSTMENT_TYPE_ID ");
			sqlBuilder.append("INNER JOIN COMPANY C ON C.COMPANY_ID = SA.COMPANY_ID ");
			sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID ");
			sqlBuilder.append("INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID ");
			sqlBuilder.append("INNER JOIN DIVISION D ON D.DIVISION_ID = SA.DIVISION_ID ");
			sqlBuilder.append("WHERE SA.COMPANY_ID = ? ");
			if (warehouseId != -1) {
				sqlBuilder.append("AND SA.WAREHOUSE_ID = ? ");
			}
			if (divisionId != -1) {
				sqlBuilder.append("AND SA.DIVISION_ID = ? ");
			}
			if (stockAdjustmentTypeId !=- 1){
				sqlBuilder.append("AND SA.STOCK_ADJUSTMENT_TYPE_ID = ? ");
			}
			sqlBuilder.append("AND SA.SA_DATE BETWEEN ? AND ? ");
			if (formStatusId > 0) {
				sqlBuilder.append("AND FW.CURRENT_STATUS_ID = ? ");
			}
			sqlBuilder.append("UNION ALL ");
			sqlBuilder.append("SELECT D.NAME AS DIVISION, SA.STOCK_ADJUSTMENT_ID, SA.SA_DATE AS DATE, "
					+ "CONCAT(IF(SA.STOCK_ADJUSTMENT_CLASSIFICATION_ID IN (1,3,5,6,7,8,9,10), 'SAI ', 'SAO '), SA.SA_NUMBER) AS SEQUENCE_NO, ");
			sqlBuilder.append("I.STOCK_CODE, I.DESCRIPTION, SA.BMS_NUMBER, SA.REMARKS, UOM.NAME AS UOM, ");
			sqlBuilder.append("SUM(SI.QUANTITY) AS QUANTITY, SUM(COALESCE(SI.UNIT_COST, 0)) AS UNIT_COST, ");
			sqlBuilder.append("IF(FW.CURRENT_STATUS_ID = 4, (SELECT FWL.COMMENT FROM FORM_WORKFLOW_LOG "
					+ "FWL WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID AND FWL.FORM_STATUS_ID = 4), '') AS CANCELLATION_REMARKS, ");
			sqlBuilder.append("FS.DESCRIPTION AS FORM_STATUS ");
			sqlBuilder.append("FROM SERIAL_ITEM SI ");
			sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT SIOTO ON SIOTO.TO_OBJECT_ID = SI.EB_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN STOCK_ADJUSTMENT SA ON SA.EB_OBJECT_ID = SIOTO.FROM_OBJECT_ID ");
			sqlBuilder.append("INNER JOIN ITEM I ON I.ITEM_ID = SI.ITEM_ID ");
			sqlBuilder.append("INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID ");
			sqlBuilder.append("INNER JOIN STOCK_ADJUSTMENT_TYPE SAT ON SAT.STOCK_ADJUSTMENT_TYPE_ID = SA.STOCK_ADJUSTMENT_TYPE_ID ");
			sqlBuilder.append("INNER JOIN COMPANY C ON C.COMPANY_ID = SA.COMPANY_ID ");
			sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID ");
			sqlBuilder.append("INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID ");
			sqlBuilder.append("INNER JOIN DIVISION D ON D.DIVISION_ID = SA.DIVISION_ID ");
			sqlBuilder.append("WHERE SA.COMPANY_ID = ? ");
			if (warehouseId != -1) {
				sqlBuilder.append("AND SA.WAREHOUSE_ID = ? ");
			}
			if (divisionId != -1) {
				sqlBuilder.append("AND SA.DIVISION_ID = ? ");
			}
			if (stockAdjustmentTypeId !=- 1){
				sqlBuilder.append("AND SA.STOCK_ADJUSTMENT_TYPE_ID = ? ");
			}
			sqlBuilder.append("AND SA.SA_DATE BETWEEN ? AND ? ");
			if (formStatusId > 0) {
				sqlBuilder.append("AND FW.CURRENT_STATUS_ID = ? ");
			}
			sqlBuilder.append("GROUP BY SI.ITEM_ID, SA.STOCK_ADJUSTMENT_ID ");
			sqlBuilder.append(") AS TBL ORDER BY DATE, SEQUENCE_NO, STOCK_CODE");
		return (List<StockAdjustmentDto>) get(sqlBuilder.toString(), new StockAdjustmentRegisterHandler(companyId,
				warehouseId, divisionId, stockAdjustmentTypeId, dateFrom, dateTo, formStatusId));
	}

	private static class StockAdjustmentRegisterHandler implements QueryResultHandler<StockAdjustmentDto> {
		private final Integer companyId;
		private final Integer warehouseId;
		private final Integer divisionId;
		private final Integer stockAdjustmentTypeId;
		private final Date dateFrom;
		private final Date dateTo;
		private final Integer formStatusId;

		private StockAdjustmentRegisterHandler(final Integer companyId, final Integer warehouseId, final Integer divisionId,
				final Integer stockAdjustmentTypeId, final Date dateFrom, final Date dateTo, final Integer formStatusId) {
			this.companyId = companyId;
			this.warehouseId = warehouseId;
			this.divisionId = divisionId;
			this.stockAdjustmentTypeId = stockAdjustmentTypeId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.formStatusId = formStatusId;
		}

		@Override
		public List<StockAdjustmentDto> convert(List<Object[]> queryResult) {
			List<StockAdjustmentDto> stockAdjustmentDtos = new ArrayList<>();
			for(Object[] row : queryResult) {
				int index = 0;
				Integer formId = (Integer) row[index++];
				String division = (String) row[index++];
				Date date = (Date) row[index++];
				String number = (String) row[index++];
				String stockCode = (String) row[index++];
				String description = (String) row[index++];
				String bmsNumber = (String) row[index++];
				String remarks = (String) row[index++];
				String uom = (String) row[index++];
				Double quantity = (Double) row[index++];
				Double unitCost = (Double) row[index++];
				String cancellationRemarks = (String) row[index++];
				String formStatus = (String) row[index++];
				double amount = NumberFormatUtil.multiplyWFP(quantity, unitCost);
				stockAdjustmentDtos.add(StockAdjustmentDto.getInstanceOf(formId,division, date, number, stockCode,
						description,bmsNumber, remarks, uom, quantity, 0.0, unitCost, amount, cancellationRemarks,
						formStatus));
			}
			return stockAdjustmentDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int totalNoOfTables = 2;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index++, companyId);
				if(warehouseId != -1) {
					query.setParameter(index++, warehouseId);
				}
				if(divisionId != -1) {
					query.setParameter(index++, divisionId);
				}
				if(stockAdjustmentTypeId != -1) {
					query.setParameter(index++, stockAdjustmentTypeId);
				}
				query.setParameter(index++, dateFrom);
				query.setParameter(index++, dateTo);
				if (formStatusId > 0) {
					query.setParameter(index++, formStatusId);
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("STOCK_ADJUSTMENT_ID", Hibernate.INTEGER);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("SEQUENCE_NO", Hibernate.STRING);
			query.addScalar("STOCK_CODE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("BMS_NUMBER", Hibernate.STRING);
			query.addScalar("REMARKS", Hibernate.STRING);
			query.addScalar("UOM", Hibernate.STRING);
			query.addScalar("QUANTITY", Hibernate.DOUBLE);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
			query.addScalar("FORM_STATUS", Hibernate.STRING);
		}
	}

	@Override
	public List<SerialItem> getRrRemainingItems(Integer referenceObjId) {
		String sql = "SELECT SI.ITEM_ID, SI.UNIT_COST, SI.EB_OBJECT_ID, COALESCE(SI.VAT_AMOUNT, 0) AS VAT_AMOUNT, "
				+ "SI.TAX_TYPE_ID, SI.SERIAL_NUMBER, SI.DISCOUNT, SI.ITEM_DISCOUNT_ID, SI.ITEM_DISCOUNT_TYPE_ID, SI.DISCOUNT_VALUE "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND OTO.OR_TYPE_ID = 64 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND API.EB_OBJECT_ID = ? "
				+ "AND SI.SERIAL_NUMBER NOT IN (SELECT SI.SERIAL_NUMBER FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID  = API.EB_OBJECT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24001 "
				+ "AND OTO1.OR_TYPE_ID = 24002 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND OTO1.FROM_OBJECT_ID = ?"
				+ ")";
		return getRemainingSerialItems(sql, referenceObjId);
	}

	private List<SerialItem> getRemainingSerialItems(String sql, Integer referenceObjId) {
		List<SerialItem> serialItems = new ArrayList<SerialItem>();
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			Integer index = 0;
			int totalNoOfTables = 2;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, referenceObjId);
				if (i < (totalNoOfTables-1)) {
					++index;
				}
			}
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("VAT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("TAX_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("SERIAL_NUMBER", Hibernate.STRING);
			query.addScalar("DISCOUNT", Hibernate.DOUBLE);
			query.addScalar("ITEM_DISCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("ITEM_DISCOUNT_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("DISCOUNT_VALUE", Hibernate.DOUBLE);

			List<Object[]> list = query.list();
			SerialItem si = null;
			if (list != null && !list.isEmpty()) {
				int itemIndex = 0;
				for (Object[] row : list) {
					itemIndex = 0;
					si = new SerialItem();
					si.setItemId((Integer) row[itemIndex++]);
					si.setUnitCost((Double) row[itemIndex++]);
					si.setEbObjectId((Integer) row[itemIndex++]);
					si.setVatAmount((Double) row[itemIndex++]);
					si.setTaxTypeId((Integer) row[itemIndex++]);
					si.setSerialNumber((String) row[itemIndex++]);
					si.setDiscount((Double) row[itemIndex++]);
					si.setItemDiscountId((Integer) row[itemIndex++]);
					si.setItemDiscountTypeId((Integer) row[itemIndex++]);
					si.setDiscountValue((Double) row[itemIndex++]);
					serialItems.add(si);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
	return serialItems;
	}

	@Override
	public List<SerialItem> getInvGoodsRemainingItems(Integer referenceObjId) {
		String sql = "SELECT SI.ITEM_ID, SI.UNIT_COST, SI.EB_OBJECT_ID, COALESCE(SI.VAT_AMOUNT, 0) AS VAT_AMOUNT, "
				+ "SI.TAX_TYPE_ID, SI.SERIAL_NUMBER, SI.DISCOUNT, SI.ITEM_DISCOUNT_ID, SI.ITEM_DISCOUNT_TYPE_ID, SI.DISCOUNT_VALUE "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND OTO.OR_TYPE_ID = 24001 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND API.EB_OBJECT_ID = ? "
				+ "AND SI.SERIAL_NUMBER NOT IN (SELECT SI.SERIAL_NUMBER FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 105 "
				+ "AND OTO1.OR_TYPE_ID = 24005 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND OTO.FROM_OBJECT_ID = ? "
				+ ")";
		return getRemainingSerialItems(sql, referenceObjId);
	}

	@Override
	public double getPoRemainingQuantity(Integer referenceObjId, Integer itemId) {
		Double remainingQty = 0.0;
		String sql = "SELECT ITEM_ID, SUM(PO_QTY)-SUM(RR_QTY) AS BALANCE FROM ( "
				+ "SELECT POI.ITEM_ID, COALESCE(POI.QUANTITY, 0) AS PO_QTY, 0 AS RR_QTY  "
				+ "FROM R_PURCHASE_ORDER_ITEM POI "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "WHERE POI.ITEM_ID = ? "
				+ "AND PO.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT SI.ITEM_ID, 0 AS PO_QTY, COALESCE(SUM(SI.QUANTITY), 0) AS RR_QTY  "
				+ "FROM SERIAL_ITEM SI  "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID  "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = API.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID  = API.EB_OBJECT_ID  "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID  "
				+ "WHERE API.INVOICE_TYPE_ID IN (13, 14, 15, 16, 17, 18) "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND SI.ITEM_ID = ? "
				+ "AND OTO1.FROM_OBJECT_ID = ? "
				+ "AND SI.ACTIVE = 1 "
				+ ") AS TBL";
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
				query.addScalar("ITEM_ID", Hibernate.INTEGER);
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
	public boolean isAlreadyUsedSerialNumber(String serialNumber) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SerialItem.FIELD.active.name(), true));
		dc.add(Restrictions.eq(SerialItem.FIELD.serialNumber.name(), serialNumber.trim()));

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));

		DetachedCriteria apiDc = DetachedCriteria.forClass(APInvoice.class);
		apiDc.setProjection(Projections.property(APInvoice.FIELD.ebObjectId.name()));

		apiDc.add(Restrictions.between(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_CENTRAL_TYPE_ID, InvoiceType.RTS_NSB8A_TYPE_ID));

		DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
		workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		workflowCriteria.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		apiDc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), workflowCriteria));
		ooDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), apiDc));
		dc.add(Subqueries.propertyIn(SerialItem.FIELD.ebObjectId.name(), ooDc));
		return getAll(dc).size() != 0;
	}
}
