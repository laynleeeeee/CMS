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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ProcessingReportType;
import eulap.eb.web.dto.AvblStocksAndBagsDto;

/**
 * Implementing class of {@code ItemBagQuantityDao}

 *
 */
public class ItemBagQuantityDaoImpl extends BaseDao<ItemBagQuantity> implements  ItemBagQuantityDao{

	@Override
	protected Class<ItemBagQuantity> getDomainClass() {
		return ItemBagQuantity.class;
	}

	@Override
	public ItemBagQuantity getByRefId(int refObjectId, int orTypeId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));

		dc.add(Subqueries.propertyIn(ItemBagQuantity.FIELD.ebObjectId.name(), obj2ObjDc));
		return get(dc);
	}

	@Override
	public Double getASIRemainingBagQty(Integer arTransactionId, Integer refAcctSaleItemId) {
		StringBuilder sqlBuilder = new StringBuilder("SELECT SUM(REMAINING_QTY) AS REMAINING_QTY FROM ( ");
		sqlBuilder.append("SELECT IBQ.QUANTITY AS ORIG_QTY, IBQ.QUANTITY AS REMAINING_QTY ");
		sqlBuilder.append("FROM ACCOUNT_SALE_ITEM ASI ");
		sqlBuilder.append("INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID ");
		sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ASI.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN ITEM_BAG_QUANTITY IBQ ON IBQ.EB_OBJECT_ID = OTO.TO_OBJECT_ID ");
		sqlBuilder.append("WHERE FW.IS_COMPLETE = 1 ");
		sqlBuilder.append("AND ASI.ACCOUNT_SALE_ITEM_ID = ? ");
		sqlBuilder.append("AND OTO.OR_TYPE_ID = ? ");
		sqlBuilder.append("UNION ALL ");
		sqlBuilder.append("SELECT 0, IBQ.QUANTITY FROM ACCOUNT_SALE_ITEM ASRI ");
		sqlBuilder.append("INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASRI.AR_TRANSACTION_ID ");
		sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ASRI.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN ITEM_BAG_QUANTITY IBQ ON IBQ.EB_OBJECT_ID = OTO.TO_OBJECT_ID ");
		sqlBuilder.append("WHERE FW.CURRENT_STATUS_ID != 4 ");
		sqlBuilder.append("AND ASRI.REF_ACCOUNT_SALE_ITEM_ID = ? ");
		sqlBuilder.append("AND AT.AR_TRANSACTION_ID != ? " );
		sqlBuilder.append("AND OTO.OR_TYPE_ID = ? ");
		sqlBuilder.append(") AS AS_REF_QTY ");
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sqlBuilder.toString());
			int index = 0;
			query.setParameter(index, refAcctSaleItemId);
			query.setParameter(++index, ItemBagQuantity.AS_IS_BAG_QTY);
			query.setParameter(++index, refAcctSaleItemId);
			query.setParameter(++index, arTransactionId);
			query.setParameter(++index, ItemBagQuantity.ASR_IS_BAG_QTY);
			if (query.list() != null && !query.list().isEmpty()) {
				return (Double) query.list().get(0);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return 0.0;
	}

	@Override
	public Double getCapIsRemainingBagQty(Integer capId, Integer itemId, Integer capdId) {
		StringBuilder sqlBuilder = new StringBuilder("SELECT SUM(CAP_IBQ_QTY) - SUM(CAPD_IBQ_QTY) AS REMAINING_BAG_QTY FROM ( ");
		sqlBuilder.append("SELECT IBQ.QUANTITY AS CAP_IBQ_QTY, 0 AS CAPD_IBQ_QTY ");
		sqlBuilder.append("FROM ITEM_BAG_QUANTITY IBQ ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT IBQ_OTO ON IBQ_OTO.TO_OBJECT_ID = IBQ.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN CUSTOMER_ADVANCE_PAYMENT_ITEM CAPI ON CAPI.EB_OBJECT_ID = IBQ_OTO.FROM_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT CAP_OTO ON CAP_OTO.TO_OBJECT_ID = CAPI.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN CUSTOMER_ADVANCE_PAYMENT CAP ON CAP.EB_OBJECT_ID = CAP_OTO.FROM_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID ");
		sqlBuilder.append("WHERE FW.IS_COMPLETE = 1 ");
		sqlBuilder.append("AND CAP.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 3 ");
		sqlBuilder.append("AND CAP.CUSTOMER_ADVANCE_PAYMENT_ID = ? ");
		sqlBuilder.append("AND IBQ_OTO.OR_TYPE_ID = ? ");
		sqlBuilder.append("AND IBQ.ITEM_ID = ? ");
		sqlBuilder.append("UNION ALL ");
		sqlBuilder.append("SELECT 0 AS CAP_IBQ_QTY, SUM(IBQ.QUANTITY) AS CAPD_IBQ_QTY ");
		sqlBuilder.append("FROM ITEM_BAG_QUANTITY IBQ ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT IBQ_OTO ON IBQ_OTO.TO_OBJECT_ID = IBQ.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN CAP_DELIVERY_ITEM CAP_DI ON CAP_DI.EB_OBJECT_ID = IBQ_OTO.FROM_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN OBJECT_TO_OBJECT CAPD_OTO ON CAPD_OTO.TO_OBJECT_ID = CAP_DI.EB_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN CAP_DELIVERY CAPD ON CAPD.EB_OBJECT_ID = CAPD_OTO.FROM_OBJECT_ID ");
		sqlBuilder.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID ");
		sqlBuilder.append("WHERE FW.CURRENT_STATUS_ID != 4 ");
		sqlBuilder.append("AND CAPD.CUSTOMER_ADVANCE_PAYMENT_ID = ? ");
		sqlBuilder.append("AND IBQ_OTO.OR_TYPE_ID = ? ");
		sqlBuilder.append("AND IBQ.ITEM_ID = ? ");
		if(capdId != null) {
			sqlBuilder.append("AND CAPD.CAP_DELIVERY_ID != ? ");
		}
		sqlBuilder.append(") AS REMAINING_BAG_QTY");
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sqlBuilder.toString());
			int index = 0;
			// Start of parameters for first select query
			query.setParameter(index, capId);
			query.setParameter(++index, ItemBagQuantity.CAP_IS_BAG_QTY);
			query.setParameter(++index, itemId);
			// End of parameters for first select query

			// Start of parameters for second select query
			query.setParameter(++index, capId);
			query.setParameter(++index, ItemBagQuantity.PIAD_IS_BAG_QTY);
			query.setParameter(++index, itemId);
			if(capdId != null) {
				query.setParameter(++index, capdId);
			}
			// End of parameters for second select query

			if (query.list() != null && !query.list().isEmpty()) {
				return (Double) query.list().get(0);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return null;
	}

	@Override
	public List<ItemBagQuantity> getAvailableItemBagQty(Integer refObjectId) {
		List<Object> availableItemBagQuantities = executeSP("GET_AVAILABLE_ITEM_BAG_QTY", refObjectId);
		List<ItemBagQuantity> availableItemBags = new ArrayList<ItemBagQuantity>();
		ItemBagQuantity ibq = null;
		for (Object obj : availableItemBagQuantities) {
			Object[] row = (Object[]) obj;
			int index = 0;
			ibq = new ItemBagQuantity();
			ibq.setSourceForm((String)row[index++]);
			ibq.setSourceObjectId((Integer)row[index++]);
			ibq.setQuantity((Double) row[index]);
			availableItemBags.add(ibq);
		}
		return availableItemBags;
	}

	private String availableBagsAndStocksSql(Integer companyId, Integer itemId, Integer warehouseId, Integer refSourceObjId, Integer itemObjectId,
			Integer itemCategoryId, String stockCode, Date asOfDate) {
		boolean hasCompanyId = companyId != null;
		boolean hasRefObj = refSourceObjId != null;
		boolean hasItemObjId = itemObjectId != null;
		boolean hasItemId = itemId != null;
		boolean hasWarehouseId = warehouseId != null;
		boolean hasItemCategoryId = itemCategoryId != null;
		boolean hasStockCode = (stockCode != null && (!stockCode.equals("")));
		boolean hasAsOfDate = asOfDate != null;

		StringBuilder sql = new StringBuilder("SELECT SOURCE, SOURCE_OBJECT_ID, EB_OBJECT_ID, SUM(BAG_QUANTITY) AS TOTAL_BAGS, "
				+ "SUM(ITEM_QUANTITY) AS TOTAL_QTY, UNIT_COST, ITEM_ID, STOCK_CODE, DESCRIPTION, UOM, DATE, CREATED_DATE FROM ( "
				+ "SELECT SOURCE, SOURCE_OBJECT_ID, EB_OBJECT_ID, SUM(BAG_QUANTITY) AS BAG_QUANTITY, SUM(ITEM_QUANTITY) AS ITEM_QUANTITY, "
				+ "UNIT_COST, ITEM_ID, STOCK_CODE, DESCRIPTION, UOM, DATE, CREATED_DATE FROM ( ");

		// 0 - Stock Adjustment In -IS
		sql.append("SELECT CONCAT('SAI-IS ', SA.SA_NUMBER) AS SOURCE, SAI.EB_OBJECT_ID AS SOURCE_OBJECT_ID, SAI.EB_OBJECT_ID, "
				+ "COALESCE(("+getItemBagQuantity("SAI.EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, SAI.QUANTITY AS ITEM_QUANTITY, SAI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, SA.SA_DATE AS DATE, SA.CREATED_DATE "
				+ "FROM STOCK_ADJUSTMENT_ITEM SAI "
				+ "INNER JOIN STOCK_ADJUSTMENT SA ON SA.STOCK_ADJUSTMENT_ID = SAI.STOCK_ADJUSTMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = SAI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND SA.STOCK_ADJUSTMENT_CLASSIFICATION_ID = 3 ");
		sql.append(hasCompanyId ? "AND SA.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND SA.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasRefObj ? "AND SAI.EB_OBJECT_ID = ? " : "");
		sql.append(hasAsOfDate ? "AND SA.SA_DATE <= ? " : "");

		// 1 - Stock Adjustment Out - IS
		sql.append("UNION ALL "
				+ "SELECT CONCAT('SA0-IS ', SA.SA_NUMBER) AS SOURCE, SOURCE_ITEM_OO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, SAI.EB_OBJECT_ID, "
				+ "COALESCE(("+getItemBagQuantity("SAI.EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, SAI.QUANTITY AS ITEM_QUANTITY, SAI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, SA.SA_DATE AS DATE, SA.CREATED_DATE "
				+ "FROM STOCK_ADJUSTMENT_ITEM SAI "
				+ "INNER JOIN OBJECT_TO_OBJECT SOURCE_ITEM_OO ON SOURCE_ITEM_OO.TO_OBJECT_ID = SAI.EB_OBJECT_ID "
				+ "INNER JOIN STOCK_ADJUSTMENT SA ON SA.STOCK_ADJUSTMENT_ID = SAI.STOCK_ADJUSTMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = SAI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SOURCE_ITEM_OO.OR_TYPE_ID = 3 "
				+ "AND SA.STOCK_ADJUSTMENT_CLASSIFICATION_ID = 4 ");
		sql.append(hasCompanyId ? "AND SA.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND SA.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND SA.SA_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND SOURCE_ITEM_OO.FROM_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND SAI.EB_OBJECT_ID != ? " : "");

		// 2 - CS - IS
		sql.append("UNION ALL "
				+ "SELECT CONCAT('CS-IS ', CS.CS_NUMBER), SOURCE_OO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, CSI.EB_OBJECT_ID, "
				+ "COALESCE(-("+getItemBagQuantity("CSI.EB_OBJECT_ID")+"), 0) AS BAG_QTY, COALESCE(-CSI.QUANTITY, 0) AS ITEM_QUANTITY, CSI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, CS.RECEIPT_DATE AS DATE, CS.CREATED_DATE "
				+ "FROM  CASH_SALE_ITEM CSI "
				+ "INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CSI.CASH_SALE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT SOURCE_OO ON SOURCE_OO.TO_OBJECT_ID = CSI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = CSI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SOURCE_OO.OR_TYPE_ID = 4 "
				+ "AND CS.CASH_SALE_TYPE_ID = 3 ");
		sql.append(hasCompanyId ? "AND CS.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND CSI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND CS.RECEIPT_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND SOURCE_OO.FROM_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND CSI.EB_OBJECT_ID != ? " : "");

		// 3 - CSR - IS Returns
		sql.append("UNION ALL "
				+ "SELECT SOURCE, SOURCE_OBJECT_ID, EB_OBJECT_ID, BAG_QUANTITY, ITEM_QUANTITY, UNIT_COST, ITEM_ID, STOCK_CODE, "
				+ "DESCRIPTION, UOM, DATE, CREATED_DATE FROM ("
				+ "SELECT IS_COMPLETE, COMPANY_ID, WAREHOUSE_ID, SOURCE, SOURCE_OBJECT_ID, EB_OBJECT_ID, SUM(-BAG_QUANTITY) AS BAG_QUANTITY, "
				+ "SUM(ITEM_QUANTITY) AS ITEM_QUANTITY, UNIT_COST, ITEM_ID, STOCK_CODE, DESCRIPTION, UOM, DATE, CREATED_DATE, ITEM_CATEGORY_ID FROM ( "
				+ "SELECT FW.IS_COMPLETE, CSR.COMPANY_ID, CSRI.WAREHOUSE_ID, CONCAT('CSR-IS ', CSR.CSR_NUMBER) AS SOURCE, "
				+ "CSRI.EB_OBJECT_ID AS SOURCE_OBJECT_ID, CSRI.EB_OBJECT_ID,  "
				+ "0 AS BAG_QUANTITY, COALESCE(-CSRI.QUANTITY, 0) AS ITEM_QUANTITY, CSRI.UNIT_COST,  "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, CSR.DATE AS DATE, CSR.CREATED_DATE, "
				+ "I.ITEM_CATEGORY_ID "
				+ "FROM CASH_SALE_RETURN_ITEM CSRI "
				+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = CSRI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND CSRI.QUANTITY < 0 "
				+ "AND CSR.CASH_SALE_TYPE_ID = 3 "
				+ "UNION ALL "
				+ "SELECT FW.IS_COMPLETE, CSR.COMPANY_ID, CSRI.WAREHOUSE_ID, CONCAT('CSR-IS ', CSR.CSR_NUMBER) AS SOURCE, "
				+ "CSRI.EB_OBJECT_ID AS SOURCE_OBJECT_ID, CSRI.EB_OBJECT_ID, "
				+ "IBQ.QUANTITY AS BAG_QUANTITY, 0 AS ITEM_QUANTITY, CSRI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION,'' AS UOM, CSR.DATE AS DATE, CSR.CREATED_DATE, "
				+ "0 AS ITEM_CATEGORY_ID "
				+ "FROM ITEM_BAG_QUANTITY IBQ "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = IBQ.EB_OBJECT_ID "
				+ "INNER JOIN CASH_SALE_RETURN_ITEM CSRI ON OTO.FROM_OBJECT_ID = CSRI.EB_OBJECT_ID "
				+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = CSRI.ITEM_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 " 
				+ "AND CSRI.QUANTITY < 0 "
				+ "AND CSR.CASH_SALE_TYPE_ID = 3 " 
				+ "AND OR_TYPE_ID = " + ItemBagQuantity.CSR_IS_BAG_QTY + " "
				+ ") AS CS_RETURN WHERE IS_COMPLETE = 1 ");
				sql.append(hasCompanyId ? "AND COMPANY_ID = ? " : "");
				sql.append(hasItemId ? "AND ITEM_ID = ? " : "");
				sql.append(hasWarehouseId ? "AND WAREHOUSE_ID = ? " : "");
				sql.append(hasItemCategoryId ? "AND ITEM_CATEGORY_ID = ? " : "");
				sql.append(hasStockCode ? "AND STOCK_CODE = ? " : "");
				sql.append(hasAsOfDate ? "AND DATE <= ? " : "");
				sql.append(hasRefObj ? "AND EB_OBJECT_ID = ? " : "");
				sql.append("GROUP BY SOURCE) AS CSR_AVL_BAGS ");

		// 4 - AS - IS
		sql.append("UNION ALL "
				+ "SELECT CONCAT('AS-IS ', ART.SEQUENCE_NO) AS SOURCE, SOURCE_ITEM_OO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, ASI.EB_OBJECT_ID, "
				+ "COALESCE(-("+getItemBagQuantity("ASI.EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, COALESCE(-ASI.QUANTITY, 0) AS ITEM_QUANTITY, ASI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, ART.TRANSACTION_DATE AS DATE, ART.CREATED_DATE "
				+ "FROM  ACCOUNT_SALE_ITEM ASI "
				+ "INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT SOURCE_ITEM_OO ON SOURCE_ITEM_OO.TO_OBJECT_ID = ASI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SOURCE_ITEM_OO.OR_TYPE_ID = 5 "
				+ "AND ART.AR_TRANSACTION_TYPE_ID = 10 ");
		sql.append(hasCompanyId ? "AND ART.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND ASI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND ART.TRANSACTION_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND SOURCE_ITEM_OO.FROM_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND ASI.EB_OBJECT_ID != ? " : "");

		// 5 - ASR - IS
		sql.append("UNION ALL "
				+ "SELECT CONCAT('ASR-IS ', ART.SEQUENCE_NO) AS SOURCE, ASI.EB_OBJECT_ID AS SOURCE_OBJECT_ID, ASI.EB_OBJECT_ID, "
				+ "COALESCE(-("+getItemBagQuantity("ASI.EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, COALESCE(-ASI.QUANTITY, 0) AS ITEM_QUANTITY, ASI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, ART.TRANSACTION_DATE AS DATE, ART.CREATED_DATE "
				+ "FROM ACCOUNT_SALE_ITEM ASI "
				+ "INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND ASI.QUANTITY < 0 "
				+ "AND ART.AR_TRANSACTION_TYPE_ID = 11 ");
		sql.append(hasCompanyId ? "AND ART.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND ASI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND ART.TRANSACTION_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND ASI.EB_OBJECT_ID = ? " : "");

		// 6 - PIAD - IS
		sql.append("UNION ALL "
				+ "SELECT CONCAT('PIAD-IS ', CAPD.CAPD_NUMBER) AS SOURCE, SOURCE_OO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, CAPI.EB_OBJECT_ID, "
				+ "COALESCE(-("+getItemBagQuantity("CAPI.EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, COALESCE(-CAPI.QUANTITY, 0) AS ITEM_QUANTITY, CAPI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, CAPD.DELIVERY_DATE AS DATE, CAPD.CREATED_DATE "
				+ "FROM  CAP_DELIVERY_ITEM CAPI "
				+ "INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = CAPI.CAP_DELIVERY_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT SOURCE_OO ON SOURCE_OO.TO_OBJECT_ID = CAPI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = CAPI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SOURCE_OO.OR_TYPE_ID = 12 "
				+ "AND CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 3 ");
		sql.append(hasCompanyId ? "AND CAPD.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND CAPI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND CAPD.DELIVERY_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND SOURCE_OO.FROM_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND CAPI.EB_OBJECT_ID != ? " : "");

		// 7 - TR OUT
		sql.append("UNION ALL "
				+ "SELECT CONCAT('TRO-IS ', TR.TR_NUMBER) AS SOURCE, SOURCE_ITEM_OO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, TRI.EB_OBJECT_ID, "
				+ "COALESCE(-("+getItemBagQuantity("TRI.EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, COALESCE(-TRI.QUANTITY, 0) AS ITEM_QUANTITY, TRI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, TR.TR_DATE AS DATE, TR.CREATED_DATE "
				+ "FROM R_TRANSFER_RECEIPT_ITEM TRI "
				+ "INNER JOIN R_TRANSFER_RECEIPT TR ON TR.R_TRANSFER_RECEIPT_ID = TRI.R_TRANSFER_RECEIPT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT SOURCE_ITEM_OO ON SOURCE_ITEM_OO.TO_OBJECT_ID = TRI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = TR.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = TRI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SOURCE_ITEM_OO.OR_TYPE_ID = 8 "
				+ "AND TR.TRANSFER_RECEIPT_TYPE_ID = 2 ");
		sql.append(hasCompanyId ? "AND TR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND TR.WAREHOUSE_FROM_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND TR.TR_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND SOURCE_ITEM_OO.FROM_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND TRI.EB_OBJECT_ID != ? " : "");

		// 8 - TR In - IS
		sql.append("UNION ALL "
				+ "SELECT CONCAT('TR-IS ', TR.TR_NUMBER) AS SOURCE, TRI.EB_OBJECT_ID AS SOURCE_OBJECT_ID, TRI.EB_OBJECT_ID, "
				+ "COALESCE(("+getItemBagQuantity("TRI.EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, COALESCE(TRI.QUANTITY, 0) AS ITEM_QUANTITY, TRI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, TR.TR_DATE AS DATE, TR.CREATED_DATE "
				+ "FROM R_TRANSFER_RECEIPT_ITEM TRI "
				+ "INNER JOIN R_TRANSFER_RECEIPT TR ON TR.R_TRANSFER_RECEIPT_ID = TRI.R_TRANSFER_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = TR.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = TRI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND TR.TRANSFER_RECEIPT_TYPE_ID = 2 ");
		sql.append(hasCompanyId ? "AND TR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND TR.WAREHOUSE_TO_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND TR.TR_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND TRI.EB_OBJECT_ID = ? " : "");

		// 9 - RR-RM Palay
		sql.append("UNION ALL "
				+ "SELECT CONCAT('RR-RM P ', AI.SEQUENCE_NO) AS SOURCE, RR_RM_RI.EB_OBJECT_ID AS SOURCE_OBJECT_ID, RR_RM_RI.EB_OBJECT_ID, "
				+ "SUM(RR_BQ.BAG_QUANTITY) AS BAG_QUANTITY, "
				+ "SUM(RR_BQ.NET_WEIGHT) - COALESCE((SELECT SUM(RR_BD.BAG_QUANTITY * RR_BD.DISCOUNT_QUANTITY) FROM RRI_BAG_DISCOUNT RR_BD "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = RR_BD.EB_OBJECT_ID "
				+ "WHERE OTO.FROM_OBJECT_ID = AI.EB_OBJECT_ID), 0) AS ITEM_QUANTITY, "
				+ "RR_RI.UNIT_COST, I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, AI.GL_DATE AS DATE, AI.CREATED_DATE "
				+ "FROM AP_INVOICE AI "
				+ "INNER JOIN R_RECEIVING_REPORT_ITEM RR_RI ON RR_RI.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "INNER JOIN R_RECEIVING_REPORT_RM_ITEM RR_RM_RI ON RR_RM_RI.R_RECEIVING_REPORT_ITEM_ID = RR_RI.R_RECEIVING_REPORT_ITEM_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = AI.EB_OBJECT_ID "
				+ "INNER JOIN RRI_BAG_QUANTITY RR_BQ ON RR_BQ.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "LEFT JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = RR_RI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE INVOICE_TYPE_ID = 9 "
				+ "AND FW.IS_COMPLETE = 1 ");
		sql.append(hasCompanyId ? "AND RR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND RR.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND AI.GL_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND RR_RM_RI.EB_OBJECT_ID = ? " : "");
		sql.append("GROUP BY RR_RM_RI.EB_OBJECT_ID ");

		// 10 - Processing Report - Milling Order Main Raw Materials
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.MILLING_ORDER,
				" CONCAT('PR', PR.PROCESSING_REPORT_TYPE_ID) ", "PR_RAW_MATERIALS_ITEM", "RMI", true));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND RMI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND RMI.EB_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND RMI.EB_OBJECT_ID != ? " : "");

		// 11 - Processing Report - Milling Order Other Raw Materials
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.MILLING_ORDER,
				" CONCAT('PR', PR.PROCESSING_REPORT_TYPE_ID) ", "PR_OTHER_MATERIALS_ITEM", "OMI", true));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND OMI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND OMI.EB_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND OMI.EB_OBJECT_ID != ? " : "");

		// 12 - Processing Report - Milling Order Main Product
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.MILLING_ORDER,
				" CONCAT('PR', PR.PROCESSING_REPORT_TYPE_ID) ", "PR_MAIN_PRODUCT", "MP", false));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND MP.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND MP.EB_OBJECT_ID = ? " : "");

		// 13 - Processing Report - Milling Order By Product
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.MILLING_ORDER,
				" CONCAT('PR', PR.PROCESSING_REPORT_TYPE_ID) ", "PR_BY_PRODUCT", "BP", false));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND BP.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND BP.EB_OBJECT_ID = ? " : "");
/**
		// 14 - Processing Report - Milling Report Main Raw Materials
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.MILLING_REPORT,
				"PR-MR ", "PR_RAW_MATERIALS_ITEM", "RMI", true));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND RMI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND RMI.EB_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND RMI.EB_OBJECT_ID != ? " : "");

		// 15 - Processing Report - Milling Report Other Raw Materials
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.MILLING_REPORT,
				"PR-MR ", "PR_OTHER_MATERIALS_ITEM", "OMI", true));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND OMI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND OMI.EB_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND OMI.EB_OBJECT_ID != ? " : "");

		// 16 - Processing Report - Milling Report Main Product
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.MILLING_REPORT,
				"PR-MR ", "PR_MAIN_PRODUCT", "MP", false));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND MP.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND MP.EB_OBJECT_ID = ? " : "");

		// 17 - Processing Report - Milling Report By Product
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.MILLING_REPORT,
				"PR-MR ", "PR_BY_PRODUCT", "BP", false));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND BP.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND BP.EB_OBJECT_ID = ? " : "");

		// 18 - Processing Report - Pass In Main Raw Materials
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.PASS_IN,
				"PR-IN ", "PR_RAW_MATERIALS_ITEM", "RMI", true));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND RMI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND RMI.EB_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND RMI.EB_OBJECT_ID != ? " : "");

		// 19 - Processing Report - Pass In Other Raw Materials
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.PASS_IN,
				"PR-IN ", "PR_OTHER_MATERIALS_ITEM", "OMI", true));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND OMI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND OMI.EB_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND OMI.EB_OBJECT_ID != ? " : "");

		// 20 - Processing Report - Pass In Main Product
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.PASS_IN,
				"PR-IN ", "PR_MAIN_PRODUCT", "MP", false));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND MP.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND MP.EB_OBJECT_ID = ? " : "");

		// 21 - Processing Report - Pass In By Product
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.PASS_IN,
				"PR-IN ", "PR_BY_PRODUCT", "BP", false));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND BP.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND BP.EB_OBJECT_ID = ? " : "");

		// 22 - Processing Report - Pass Out Main Raw Materials
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.PASS_OUT,
				"PR-OUT ", "PR_RAW_MATERIALS_ITEM", "RMI", true));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND RMI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND RMI.EB_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND RMI.EB_OBJECT_ID != ? " : "");

		// 23 - Processing Report - Pass Out Other Raw Materials
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.PASS_OUT,
				"PR-OUT ", "PR_OTHER_MATERIALS_ITEM", "OMI", true));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND OMI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND OMI.EB_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND OMI.EB_OBJECT_ID != ? " : "");

		// 24 - Processing Report - Pass Out Main Product
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.PASS_OUT,
				"PR-OUT ", "PR_MAIN_PRODUCT", "MP", false));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND MP.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND MP.EB_OBJECT_ID = ? " : "");

		// 25 - Processing Report - Pass Out By Product
		sql.append("UNION ALL " + processingRptAvblBagsSql(ProcessingReportType.PASS_OUT,
				"PR-OUT ", "PR_BY_PRODUCT", "BP", false));
		sql.append(hasCompanyId ? "AND PR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND BP.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND PR.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND BP.EB_OBJECT_ID = ? " : "");
*/
		// 26 - RR-RM IS
		sql.append("UNION ALL "
				+ "SELECT CONCAT('RR-RM IS ', AI.SEQUENCE_NO) AS SOURCE, RR_RM_RI.EB_OBJECT_ID AS SOURCE_OBJECT_ID, RR_RM_RI.EB_OBJECT_ID, "
				+ "0 AS BAG_QUANTITY, RR_RI.QUANTITY AS ITEM_QUANTITY, RR_RI.UNIT_COST, I.ITEM_ID, I.STOCK_CODE, "
				+ "I.DESCRIPTION, UOM.NAME AS UOM, AI.GL_DATE AS DATE, AI.CREATED_DATE "
				+ "FROM AP_INVOICE AI "
				+ "INNER JOIN R_RECEIVING_REPORT_ITEM RR_RI ON RR_RI.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "INNER JOIN R_RECEIVING_REPORT_RM_ITEM RR_RM_RI ON RR_RM_RI.R_RECEIVING_REPORT_ITEM_ID = RR_RI.R_RECEIVING_REPORT_ITEM_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = RR_RI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE INVOICE_TYPE_ID = 8 "
				+ "AND FW.IS_COMPLETE = 1 ");
		sql.append(hasCompanyId ? "AND RR.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND RR.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND AI.GL_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND RR_RM_RI.EB_OBJECT_ID = ? " : "");
		
		// 27 - ASR - IS Exchange
		sql.append("UNION ALL "
				+ "SELECT CONCAT('ASR-IS ', ART.SEQUENCE_NO) AS SOURCE, SOURCE_ITEM_OO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, ASI.EB_OBJECT_ID, "
				+ "COALESCE(-("+getItemBagQuantity("ASI.EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, COALESCE(-ASI.QUANTITY, 0) AS ITEM_QUANTITY, ASI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, ART.TRANSACTION_DATE AS DATE, ART.CREATED_DATE "
				+ "FROM  ACCOUNT_SALE_ITEM ASI "
				+ "INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT SOURCE_ITEM_OO ON SOURCE_ITEM_OO.TO_OBJECT_ID = ASI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SOURCE_ITEM_OO.OR_TYPE_ID = 7 "
				+ "AND ART.AR_TRANSACTION_TYPE_ID = 11 ");
		sql.append(hasCompanyId ? "AND ART.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND ASI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND ART.TRANSACTION_DATE <= ? " : "");
		sql.append(hasRefObj ? "AND SOURCE_ITEM_OO.FROM_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND ASI.EB_OBJECT_ID != ? " : "");

		// 28 - CSR - IS Exchange
		sql.append("UNION ALL "
				+ "SELECT CONCAT('CSR-IS ', CS.CSR_NUMBER), SOURCE_OO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, CSI.EB_OBJECT_ID, "
				+ "COALESCE(-("+getItemBagQuantity("CSI.EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, COALESCE(-CSI.QUANTITY, 0) AS ITEM_QUANTITY, CSI.UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, CS.DATE AS DATE, CS.CREATED_DATE "
				+ "FROM CASH_SALE_RETURN_ITEM CSI "
				+ "INNER JOIN CASH_SALE_RETURN CS ON CS.CASH_SALE_RETURN_ID = CSI.CASH_SALE_RETURN_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT SOURCE_OO ON SOURCE_OO.TO_OBJECT_ID = CSI.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = CSI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SOURCE_OO.OR_TYPE_ID = 6 "
				+ "AND CSI.QUANTITY > 0 "
				+ "AND CS.CASH_SALE_TYPE_ID = 3 ");
		sql.append(hasCompanyId ? "AND CS.COMPANY_ID = ? " : "");
		sql.append(hasItemId ? "AND I.ITEM_ID = ? " : "");
		sql.append(hasWarehouseId ? "AND CSI.WAREHOUSE_ID = ? " : "");
		sql.append(hasItemCategoryId ? "AND I.ITEM_CATEGORY_ID = ? " : "");
		sql.append(hasStockCode ? "AND I.STOCK_CODE = ? " : "");
		sql.append(hasAsOfDate ? "AND CS.DATE <= ? " : "");
		sql.append(hasRefObj ? "AND SOURCE_OO.FROM_OBJECT_ID = ? " : "");
		sql.append(hasItemObjId ? "AND CSI.EB_OBJECT_ID != ? " : "");

		sql.append(") AS INNER_TBL GROUP BY EB_OBJECT_ID "
				+ ") AS AVAILABLE_BAGS GROUP BY SOURCE_OBJECT_ID "
				+ "HAVING TOTAL_QTY > 0 ");
		return sql.toString();
	}

	private String getItemBagQuantity(String itemLineEbObjectId) {
		return "SELECT IBQ.QUANTITY FROM ITEM_BAG_QUANTITY IBQ "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = IBQ.EB_OBJECT_ID "
				+ "WHERE OTO.FROM_OBJECT_ID = " + itemLineEbObjectId + " ";
	}

	private String processingRptAvblBagsSql(Integer processingRptTypeId, String sourceLabel,
			String itemTableName, String itemTableAlias, boolean isRawMat) {
		String sql = "";
		if(isRawMat) {
			sql = "SELECT CONCAT('PR', PR.PROCESSING_REPORT_TYPE_ID,' ', PR.SEQUENCE_NO) AS SOURCE, SOURCE_ITEM_OTO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, "+itemTableAlias+".EB_OBJECT_ID, "
				+ "ROUND(COALESCE(-("+getItemBagQuantity(itemTableAlias+".EB_OBJECT_ID")+"), 0), 2) AS BAG_QUANTITY, COALESCE(-"+itemTableAlias+".QUANTITY, 0) AS ITEM_QUANTITY, "+itemTableAlias+".UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, PR.DATE AS DATE, PR.CREATED_DATE "
				+ "FROM "+itemTableName+" "+itemTableAlias+" "
				+ "INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = "+itemTableAlias+".PROCESSING_REPORT_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = "+itemTableAlias+".ITEM_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT SOURCE_ITEM_OTO ON SOURCE_ITEM_OTO.TO_OBJECT_ID = "+itemTableAlias+".EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				//+ "AND PR.PROCESSING_REPORT_TYPE_ID = " + processingRptTypeId +" "
				+ "AND SOURCE_ITEM_OTO.OR_TYPE_ID = 2 ";
		} else {
			sql = "SELECT CONCAT('PR', PR.PROCESSING_REPORT_TYPE_ID,' ', PR.SEQUENCE_NO) AS SOURCE, "+itemTableAlias+".EB_OBJECT_ID AS SOURCE_OBJECT_ID, "+itemTableAlias+".EB_OBJECT_ID, "
				+ "COALESCE(("+getItemBagQuantity(itemTableAlias+".EB_OBJECT_ID")+"), 0) AS BAG_QUANTITY, COALESCE("+itemTableAlias+".QUANTITY, 0) AS ITEM_QUANTITY, "+itemTableAlias+".UNIT_COST, "
				+ "I.ITEM_ID, I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, PR.DATE AS DATE, PR.CREATED_DATE "
				+ "FROM "+itemTableName+" "+itemTableAlias+" "
				+ "INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = "+itemTableAlias+".PROCESSING_REPORT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = "+itemTableAlias+".EB_OBJECT_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = "+itemTableAlias+".ITEM_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND OO.OR_TYPE_ID = 1 ";
				//+ "AND PR.PROCESSING_REPORT_TYPE_ID = "+processingRptTypeId+" ";
		}
		return sql;
	}

	@Override
	public Page<AvblStocksAndBagsDto> getAvailableBags(Integer companyId, Integer itemId, Integer warehouseId, Integer refBagQtyObjId, Integer itemObjectId) {
		PageSetting pageSetting = new PageSetting(1, PageSetting.MAX_RECORD_100);
		StringBuilder avblBagsAndStocksSql = new StringBuilder("SELECT SOURCE, SOURCE_OBJECT_ID, EB_OBJECT_ID, TOTAL_BAGS, "
				+ "TOTAL_QTY, UNIT_COST, ITEM_ID, STOCK_CODE, DESCRIPTION, UOM FROM ( ");
		avblBagsAndStocksSql.append(availableBagsAndStocksSql(companyId, itemId, warehouseId, refBagQtyObjId, itemObjectId, null, null, null));
		avblBagsAndStocksSql.append(" ) AS TOTAL_AVAIL_STOCKS WHERE TOTAL_QTY != 0.0 ");
		return getAllAsPage(availableBagsAndStocksSql(companyId, itemId, warehouseId, refBagQtyObjId, itemObjectId, null, null, null), pageSetting,
				new AvailableBagsHandler(companyId, itemId, warehouseId, refBagQtyObjId, itemObjectId, null, null, null));
	}

	private static class AvailableBagsHandler implements QueryResultHandler<AvblStocksAndBagsDto> {
		private final Integer itemId;
		private final Integer warehouseId;
		private final Integer refBagQtyObjId;
		private final Integer companyId;
		private final Integer itemObjectId;
		private final Integer itemCategoryId;
		private final String stockCode;
		private final Date asOfDate;

		private AvailableBagsHandler(Integer companyId, Integer itemId, Integer warehouseId, Integer refBagQtyObjId, Integer itemObjectId,
				Integer itemCategoryId, String stockCode, Date asOfDate) {
			this.companyId = companyId;
			this.itemId = itemId;
			this.warehouseId = warehouseId;
			this.refBagQtyObjId = refBagQtyObjId;
			this.itemObjectId = itemObjectId;
			this.itemCategoryId = itemCategoryId;
			this.stockCode = stockCode;
			this.asOfDate = asOfDate;
			
		}

		@Override
		public List<AvblStocksAndBagsDto> convert(List<Object[]> queryResult) {
			List<AvblStocksAndBagsDto> stocksAndBagsDto = new ArrayList<>();
			AvblStocksAndBagsDto avbleSBDto = null;
			for (Object obj : queryResult) {
				Object[] row = (Object[]) obj;
				int index = 0;
				avbleSBDto = new AvblStocksAndBagsDto();
				avbleSBDto.setSource((String)row[index++]);
				avbleSBDto.setSourceObjId((Integer)row[index++]);
				avbleSBDto.setEbObjectId((Integer) row[index++]);
				avbleSBDto.setTotalBags((Double) row[index++]);
				avbleSBDto.setTotalStocks((Double)row[index++]);
				avbleSBDto.setUnitCost((Double) row[index++]);
				avbleSBDto.setItemId((Integer) row[index++]);
				avbleSBDto.setStockCode((String) row[index++]);
				avbleSBDto.setDescription((String) row[index++]);
				avbleSBDto.setUnitOfMeasurement((String) row[index++]);
				stocksAndBagsDto.add(avbleSBDto);
			}
			return stocksAndBagsDto;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int totalNoOfTables = 17;
			for(int i=0; i < totalNoOfTables; i++) {
				query.setParameter(index, companyId);
				if(itemId != null) {
					query.setParameter(++index, itemId);
				}
				if(warehouseId != null) {
					query.setParameter(++index, warehouseId);
				}
				if(itemCategoryId != null) {
					query.setParameter(++index, itemCategoryId);
				}
				if(stockCode != null && !stockCode.trim().isEmpty()) {
					query.setParameter(++index, stockCode);
				}
				if(asOfDate != null) {
					query.setParameter(++index, asOfDate);
				}
				if(refBagQtyObjId != null) {
					query.setParameter(++index, refBagQtyObjId);
				}
				if(itemObjectId != null && (i == 1 || i == 2 || i == 4 || i == 6 || i == 7 || i == 10 || i == 11
						|| i == 14 || i == 15 || i == 18 || i == 19 || i == 22 || i == 23 || i==27 || i==28)) {
					query.setParameter(++index, itemObjectId);
				}
				if(i < (totalNoOfTables-1)) {
					index++;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SOURCE", Hibernate.STRING);
			query.addScalar("SOURCE_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("TOTAL_BAGS", Hibernate.DOUBLE);
			query.addScalar("TOTAL_QTY", Hibernate.DOUBLE);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
			query.addScalar("STOCK_CODE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("UOM", Hibernate.STRING);
		}
		
	}

	@Override
	public Page<AvblStocksAndBagsDto> getAvblBagsAndStocksRpt(Integer companyId, Integer itemId, Integer warehouseId,
			Integer refBagQtyObjId, Integer itemObjectId, Integer itemCategoryId, String stockCode, Date asOfDate,
			String orderBy, PageSetting pageSetting) {
		StringBuilder avblBagsAndStocksSql = new StringBuilder("SELECT SOURCE, SOURCE_OBJECT_ID, EB_OBJECT_ID, TOTAL_BAGS, "
				+ "TOTAL_QTY, UNIT_COST, ITEM_ID, STOCK_CODE, DESCRIPTION, UOM FROM ( ");
		avblBagsAndStocksSql.append(availableBagsAndStocksSql(companyId, itemId, warehouseId, refBagQtyObjId, itemObjectId, itemCategoryId, stockCode, asOfDate));
		avblBagsAndStocksSql.append(" ) AS TOTAL_AVAIL_STOCKS WHERE TOTAL_QTY != 0.0 ");
		String ordering = orderBy.equals("sc") ? " STOCK_CODE " : "DESCRIPTION ";
		avblBagsAndStocksSql.append("ORDER BY " + ordering);
		return getAllAsPage(avblBagsAndStocksSql.toString(), pageSetting,
				new AvailableBagsHandler(companyId, itemId, warehouseId, refBagQtyObjId, itemObjectId, itemCategoryId, stockCode, asOfDate));
	}

	@Override
	public AvblStocksAndBagsDto getRefAvailableBags(Integer companyId, Integer sourceObjectId, Integer itemId, Integer warehouseId) {
		AvblStocksAndBagsDto avblStocksAndBagsDto = null;
		StringBuilder avblBagsAndStocksSql = new StringBuilder("SELECT SOURCE, SOURCE_OBJECT_ID, EB_OBJECT_ID, TOTAL_BAGS, "
				+ "TOTAL_QTY, UNIT_COST, ITEM_ID, STOCK_CODE, DESCRIPTION, UOM FROM ( ");
		avblBagsAndStocksSql.append(availableBagsAndStocksSql(companyId, itemId, warehouseId, sourceObjectId, null, null, null, null));
		avblBagsAndStocksSql.append(" ) AS TOTAL_AVAIL_STOCKS ");
		AvailableBagsHandler handler = new AvailableBagsHandler(companyId, itemId, warehouseId, sourceObjectId, null, null, null, null);
		List<AvblStocksAndBagsDto> avblStocksAndBagsDtos=  new ArrayList<>(get(avblBagsAndStocksSql.toString(), handler));
		if(avblStocksAndBagsDtos != null && !avblStocksAndBagsDtos.isEmpty()) {
			avblStocksAndBagsDto = avblStocksAndBagsDtos.get(0); // Expecting only one row.
		}
		return avblStocksAndBagsDto;
	}
}
