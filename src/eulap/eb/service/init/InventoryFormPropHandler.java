package eulap.eb.service.init;

import org.apache.commons.configuration2.Configuration;

import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.FormPropertyHandler;


/**
 * Inventory form property handler. This will 
 * handle the parsing of inventory stored procedure in the configuration file of the form 
 * 

 *
 */
public class InventoryFormPropHandler implements FormPropertyHandler{
	private static final String SQL = "sql";
	private static final String SQL_INV_ENTRIES = SQL + ".inv.entries";
	// formName.sql.inv.as (Available Stocks)
	private static final String SQL_AS = SQL + ".inv.as";
	// formName.sql.inv.rsao (Received Stocks-future)
	private static final String SQL_RSF = SQL + ".inv.rsf";
	// formName.sql.inv.il (Inventory Listing)
	private static final String SQL_IL = SQL + ".inv.il";
	// formName.sql.inv.sc (Stockcard)
	private static final String SQL_SC = SQL + ".inv.sc";
	// formName.sql.inv.ft (future transactions)
	private static final String SQL_FT = SQL + ".inv.ft";
	private static final String SQL_INV = "inv";
	private static final String SQL_UNION_ALL = " UNION ALL ";
	
	private StringBuilder availableStocks = new StringBuilder();
	private StringBuilder receivedStocksFuture = new StringBuilder();
	private StringBuilder inventoryListing = new StringBuilder();
	private StringBuilder stockcardperItem = new StringBuilder();
	private StringBuilder futureTransactions = new StringBuilder ();

	@Override
	public void handleProperties(FormProperty formProperty,
			String propertyName, Configuration config) {
		String sqlEntries = config.getString(propertyName + "."+SQL_INV_ENTRIES);
		if (sqlEntries == null || sqlEntries.length() <= 0) {
			return;
		}
		
		String availableStocksCriteria = config.getString(propertyName + "." + SQL_AS, "");
		String rsfCriteria = config.getString(propertyName + "." + SQL_RSF, "");
		String inventoryListCriteria = config.getString(propertyName + "." + SQL_IL, "");
		String stockCardCriteria = config.getString(propertyName + "." + SQL_SC, "");
		String futureTransactionCriteria = config.getString(propertyName + "." + SQL_FT, "");
		for (String entryNumber : sqlEntries.split(";")){
			String invSql = config.getString(propertyName +"."+SQL+ "."+entryNumber + "."+SQL_INV, "");

			// Weighted Average
			appendSQL(availableStocks, invSql, availableStocksCriteria);
						
			// Inventory List
			appendSQL(inventoryListing, invSql, inventoryListCriteria);
			
			//Stockcard per item
			appendSQL(stockcardperItem, invSql, stockCardCriteria);
			
			//Future Transactions
			appendSQL(futureTransactions, invSql, futureTransactionCriteria);
			
			//Received stocks as of
			appendSQL(receivedStocksFuture, invSql, rsfCriteria);
		}
	}
	
	private void appendSQL (StringBuilder builder, String invSql, String criteria) {
		if (builder.length() != 0) {
			builder.append(SQL_UNION_ALL);
		}

		builder.append(invSql);
		builder.append(" ").append(criteria);
	}
	
	

	/**
	 * Get the weighted average sql based on the parse value in the form configuration file. 
	 */
	protected String getAvailableStocksSQL () {
		return availableStocks.toString();
	}

	protected String getInventoryListSQL () {
		return inventoryListing.toString();
	}
	
	protected String getStockcardperItemSQL () {
		return stockcardperItem.toString();
	}
	
	protected String getFutureTransactonSQL () {
		return futureTransactions.toString();
	}
	
	protected String getReceivedStocksFutureSQL () {
		return receivedStocksFuture.toString();
	}
}
