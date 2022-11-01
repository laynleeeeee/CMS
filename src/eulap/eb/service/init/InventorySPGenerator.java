package eulap.eb.service.init;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.CompanyDao;
import eulap.eb.service.workflow.WorkflowPropertyGen;

/**
 * A class that auto generate inventory
 * related stored procedure. 
 *  

 *
 */
@Service 
public class InventorySPGenerator {
	
	@Autowired
	private CompanyDao dao;
	@Autowired
	private String CMSFormPath; 

	@PostConstruct
	public void createSP () throws ConfigurationException {
		String formPath = CMSFormPath;
		InventoryFormPropHandler handler = new InventoryFormPropHandler();
		WorkflowPropertyGen.getAllFormProperties(formPath, null, handler);
		createSPAvailableBalance(handler);
		createSPReceivedStocksFuture(handler);
		createSPBeginningBalance(handler);
		createSPLInventoryListing(handler);
		createSPStockcardPerItem(handler);
		createSPFutureTransactions(handler);
	}

	private void createSPFutureTransactions (InventoryFormPropHandler handler) {
		String sql = handler.getFutureTransactonSQL();
		if (sql == null || sql.isEmpty()){
			return;
		}
		dao.executeSQL("DROP PROCEDURE IF EXISTS GET_ITEM_FUTURE_WITHDRAWAL_TRANSACTIONS"); 
		String futureTransactionSQL = "CREATE PROCEDURE GET_ITEM_FUTURE_WITHDRAWAL_TRANSACTIONS (IN IN_COMPANY_ID INT, "
				+ "IN IN_WAREHOUSE_ID INT,"
				+ "IN IN_ITEM_ID INT, "
				+ "IN IN_AS_OF_DATE DATE, "
				+ "IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT) "
				+ "SELECT ITEM_ID, DATE, CREATED_DATE, WAREHOUSE_ID, OUT_QUANTITY, "
				+ "(IN_TOTAL_COST + OUT_TOTAL_COST)/(IN_QUANTITY + OUT_QUANTITY) as UNIT_COST, PARENT_EB_OBJECT_ID, EB_OBJECT_ID FROM ( ";
		futureTransactionSQL += sql;
		futureTransactionSQL += ") AS FUTURE_TRANSACTION_TBLE ";
		futureTransactionSQL += "WHERE (ABS(OUT_QUANTITY) != 0) "
				+ "ORDER BY DATE, CREATED_DATE, REFERENCE_NUMBER";
		dao.executeSQL (futureTransactionSQL);
	}

	private void createSPStockcardPerItem (InventoryFormPropHandler handler) {
		String sql = handler.getStockcardperItemSQL();
		if (sql == null || sql.isEmpty()){
			return;
		}
		dao.executeSQL("DROP PROCEDURE IF EXISTS GET_STOCKARD_PER_ITEM");
		String stockcardPeritem = "CREATE PROCEDURE GET_STOCKARD_PER_ITEM (IN IN_COMPANY_ID INT, "
				+ "IN IN_ITEM_ID INT, "
				+ "IN IN_WAREHOUSE_ID INT,"
				+ "IN IN_DIVISION_ID INT,"
				+ "IN IN_START_DATE DATE, "
				+ "IN IN_END_DATE DATE, " 
				+ "IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT) "
				+ "SELECT ITEM_ID, DATE, REFERENCE_NUMBER, COALESCE(INVOICE_NUMBER, '') as INVOICE_NUMBER, "
				+ "TRANS_DESCRIPTION, IN_QUANTITY - OUT_QUANTITY as QUANTITY, "
				+ "(IN_TOTAL_COST + OUT_TOTAL_COST)/(IN_QUANTITY + OUT_QUANTITY) as UNIT_COST, "
				+ "CAST(SRP as DECIMAL(12,2)) as SRP, DIVISION_NAME, BMS_NUMBER FROM (";
		stockcardPeritem += sql;
		stockcardPeritem += ") as STOCKCARD_TABLE " + 
				"ORDER BY DATE, CREATED_DATE, REFERENCE_NUMBER";
		dao.executeSQL (stockcardPeritem);
	}

	private void createSPBeginningBalance (InventoryFormPropHandler handler) {
		String sql = handler.getAvailableStocksSQL();
		if (sql == null || sql.isEmpty()){
			return;
		}
		dao.executeSQL("DROP PROCEDURE IF EXISTS GET_BEGINNING_BALANCE");
		String beginningBalance = "CREATE PROCEDURE GET_BEGINNING_BALANCE (IN IN_COMPANY_ID INT, IN IN_ITEM_ID INT, IN IN_WAREHOUSE_ID INT, IN IN_DATE DATE) "+
				"SELECT SOURCE, WAREHOUSE_ID, ITEM_ID, (SUM(IN_QUANTITY)-SUM(OUT_QUANTITY)) as QUANTITY, (SUM(IN_TOTAL_COST)-SUM(OUT_TOTAL_COST))/(SUM(IN_QUANTITY)-SUM(OUT_QUANTITY)) as UNIT_COST FROM (";
		beginningBalance += sql;
		beginningBalance +=") as AVAILABLE_STOCKS " +
				"WHERE DATE <= IN_DATE " +
				"HAVING ROUND(QUANTITY, 2) != 0 ";
		dao.executeSQL (beginningBalance);
	}

	private void createSPAvailableBalance (InventoryFormPropHandler handler) {
		// Get Available Balance
		String sql = handler.getAvailableStocksSQL();
		if (sql == null || sql.isEmpty()){
			return;
		}
		dao.executeSQL("DROP PROCEDURE IF EXISTS GET_AVAILABLE_STOCKS");
		String availableStocks = "CREATE PROCEDURE GET_AVAILABLE_STOCKS (IN IN_COMPANY_ID INT, IN IN_ITEM_ID INT, IN IN_WAREHOUSE_ID INT, IN IN_DATE DATE) "+
				"SELECT WAREHOUSE_ID, ITEM_ID, (SUM(IN_QUANTITY)-SUM(OUT_QUANTITY)) as QUANTITY, (SUM(IN_TOTAL_COST)-SUM(OUT_TOTAL_COST))/(SUM(IN_QUANTITY)-SUM(OUT_QUANTITY)) as UNIT_COST FROM (";

		availableStocks += sql; 
		availableStocks +=") as WEIGHTED_AVERAGE";
		dao.executeSQL (availableStocks);
	}

	private void createSPReceivedStocksFuture (InventoryFormPropHandler handler) {
		String sql = handler.getReceivedStocksFutureSQL();
		if (sql == null || sql.isEmpty()){
			return;
		}
		// Get Available Balance
		dao.executeSQL("DROP PROCEDURE IF EXISTS GET_RECEIEVED_STOCKS_FUTURE");
		String receivedStocksFuture = "CREATE PROCEDURE GET_RECEIEVED_STOCKS_FUTURE (IN IN_COMPANY_ID INT, IN IN_ITEM_ID INT, IN IN_WAREHOUSE_ID INT, IN IN_DATE DATE) "+
				"SELECT WAREHOUSE_ID, ITEM_ID, (SUM(IN_QUANTITY)-SUM(OUT_QUANTITY)) as QUANTITY, (SUM(IN_TOTAL_COST)-SUM(OUT_TOTAL_COST))/(SUM(IN_QUANTITY)-SUM(OUT_QUANTITY)) as UNIT_COST FROM (";
		receivedStocksFuture += sql;
		receivedStocksFuture +=") as RECEIEVED_STOCKS_FUTURE "
				+ "WHERE IN_QUANTITY != 0";
		dao.executeSQL (receivedStocksFuture);
	}

	private void createSPLInventoryListing (InventoryFormPropHandler handler) {
		String sql = handler.getInventoryListSQL();
		if (sql == null || sql.isEmpty()){
			return;
		}
		dao.executeSQL("DROP PROCEDURE IF EXISTS GET_INVENTORY_LISTING");
		String invListing = "CREATE PROCEDURE GET_INVENTORY_LISTING ( "+
				"IN IN_COMPANY_ID INT, "+
					"IN IN_DATE DATE, " +
					"IN IN_ITEM_CATEGORY_ID INT, "+
					"IN IN_DIVISION_ID INT, "+
					"IN IN_WAREHOUSE_ID INT, "+
					"IN IN_IS_ACTIVE INT, " +
					"IN IN_STATUS INT, "+
					"IN IN_ORDER_BY INT, "+
					"IN IN_STOCK_OPTION INT, "+
					"IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT) "+
				"SELECT ITEM_ID, STOCK_CODE, DESCRIPTION, ITEM_CATEGORY_ID, UNIT_MEASUREMENT_ID, COALESCE(QUANTITY, 0) AS QUANTITY, "+
				"ROUND(COALESCE(UNIT_COST, 0), 6) AS UNIT_COST, DIVISION_NAME, WAREHOUSE_ID, DIVISION_ID, LATEST_SRP, UOM_NAME FROM ( "+
					"SELECT ITEM_ID, STOCK_CODE, DESCRIPTION, ITEM_CATEGORY_ID, UNIT_MEASUREMENT_ID, ROUND((SUM(IN_QUANTITY)-SUM(OUT_QUANTITY)), 6) as QUANTITY, " +
					"ROUND((SUM(IN_TOTAL_COST)-SUM(OUT_TOTAL_COST)),6)/ROUND((SUM(IN_QUANTITY)-SUM(OUT_QUANTITY)),6) as UNIT_COST, DIVISION_NAME, WAREHOUSE_ID, DIVISION_ID, " +
					"LATEST_SRP, UOM_NAME FROM ( " + sql + ") AS INNER_TBL "+
					"GROUP BY ITEM_ID, DIVISION_ID "+
				") AS INV_LISTING "+
				"WHERE IF(IN_STOCK_OPTION != -1, ROUND(COALESCE(QUANTITY, 0), 3) != 0, ITEM_ID IS NOT NULL) "+
				"ORDER BY IF(IN_ORDER_BY = 1, STOCK_CODE, DESCRIPTION)";
		dao.executeSQL (invListing);
	}
}
