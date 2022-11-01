package eulap.eb.service.init;

import org.apache.commons.configuration2.Configuration;

import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.FormPropertyHandler;

/**
 * Account form property handler. This will
 * handle the parsing of accounting related procedure and building sql script.  
 *

 *
 */
public class AcctFormPropHandler implements FormPropertyHandler{
	private static final String SQL = "sql";
	private static final String SQL_ENTRIES = "sql.entries";
	private static final String SQL_JE = "je";
	private static final String SQL_JE_JERR = "sql.je.jerr";
	private static final String SQL_JE_AB = "sql.je.ab";
	private static final String SQL_JE_AA = "sql.je.aa";
	private static final String SQL_JE_GLL = "sql.je.gll";
	private static final String SQL_JE_ISBS = "isbs";
	private static final String SQL_IS = "sql.is";
	private static final String SQL_BS = "sql.bs";

	private static final String SQL_UNION_ALL = " UNION ALL ";
	private StringBuilder accountAnalysis = new StringBuilder ();
	private StringBuilder accountBalances = new StringBuilder ();
	private StringBuilder journalEntries = new StringBuilder ();
	private StringBuilder generalLedgerListings = new StringBuilder ();
	private StringBuilder isBs = new StringBuilder();
	private StringBuilder incomeStatement = new StringBuilder();

	@Override
	public void handleProperties(FormProperty formProperty, String propertyName, Configuration config) {
		String sqlEntries = config.getString(propertyName + "."+SQL_ENTRIES);
		if (sqlEntries == null || sqlEntries.length() <= 0) {
			return;
		}
		String jeJerrSql = config.getString(propertyName + "."+SQL_JE_JERR, "");
		String jeAbSql = config.getString(propertyName + "."+SQL_JE_AB, "");
		String jeAaSql = config.getString(propertyName + "."+SQL_JE_AA, "");
		String jeGllSql = config.getString(propertyName + "."+SQL_JE_GLL, "");
		String isSql = config.getString(propertyName + "."+SQL_IS, "");
		String bsSql = config.getString(propertyName + "."+SQL_BS, "");

		for (String entryNumber : sqlEntries.split(";")) {
			String jeSql = config.getString(propertyName + "."+ SQL +"."+entryNumber+"."+SQL_JE);
			//Journal Entries
			if (journalEntries.length() != 0) {
				journalEntries.append(SQL_UNION_ALL);
			}
			journalEntries.append(jeSql);
			journalEntries.append(" " + jeJerrSql);

			// Account balances	
			if (accountBalances.length() != 0) {
				accountBalances.append(SQL_UNION_ALL);
			}

			accountBalances.append(jeSql);
			accountBalances.append(" " + jeAbSql);

			// Account analysis
			if (accountAnalysis.length() != 0) {
				accountAnalysis.append(SQL_UNION_ALL);
			}
			accountAnalysis.append(jeSql);
			accountAnalysis.append(" " + jeAaSql);

			// General Ledger Listings
			if (generalLedgerListings.length() != 0) {
				generalLedgerListings.append(SQL_UNION_ALL);
			}
			generalLedgerListings.append(jeSql);
			generalLedgerListings.append(" " + jeGllSql);

			String jeIsBsSql = config.getString(propertyName+"."+SQL+"."+entryNumber+"."+SQL_JE_ISBS);
			// Income Statement / Balance Sheet
			if (isBs.length() != 0 && jeIsBsSql != null && !jeIsBsSql.trim().isEmpty()) {
				isBs.append(SQL_UNION_ALL);
			}

			if (incomeStatement.length() != 0 && jeIsBsSql != null && !jeIsBsSql.trim().isEmpty()) {
				incomeStatement.append(SQL_UNION_ALL);
			}

			if (jeIsBsSql != null && !jeIsBsSql.trim().isEmpty()) {
				isBs.append(jeIsBsSql);
				isBs.append(" "+bsSql); // bs only

				incomeStatement.append(jeIsBsSql);
				incomeStatement.append(" "+isSql); // is only
			}
		}
	}

	/**
	 * Get the account balances sql based in the form-workflow.properties configuration.
	 */
	protected String getAccountBalancesSQL () {
		return accountBalances.toString();
	}

	/**
	 * Get the account analysis sql parsed in the form-workflow.properties configuration.
	 */
	protected String getAccountAnalysisSQL () {
		return accountAnalysis.toString();
	}

	/**
	 * Get the journal entries sql parsed in the form-workflow.properteis configuration.
	 */
	protected String getJournalEntriesSQL() {
		return journalEntries.toString();
	}

	/**
	 * Get the general ledger listing sql parsed in the form-workflow.properties configuration.
	 */
	protected String getGeneralLedgerListingsSQL () {
		return generalLedgerListings.toString();
	}

	/**
	 * Get the balance sheet sql parsed in the form-workflow.properties configuration.
	 */
	protected String getBalanceSheetSQL() {
		return isBs.toString();
	}

	/**
	 * Get the income statement sql parsed in the form-workflow.properties configuration.
	 */
	protected String getIncomeStatementSQL() {
		return incomeStatement.toString();
	}
}