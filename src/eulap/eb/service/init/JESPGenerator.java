package eulap.eb.service.init;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.dao.Dao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.service.workflow.WorkflowPropertyGen;

/**
 * A class that will auto generate the stored procedure of the journal entries of the forms

 *
 */
@Service
public class JESPGenerator {
	@Autowired
	private CompanyDao dao;
	@Autowired
	private String CMSFormPath;

	/**
	 * Create the journal entries stored procedures.
	 * @throws ConfigurationException 
	 */
	@PostConstruct
	public void createJESPs () throws ConfigurationException {
		String formPath = CMSFormPath;
		AcctFormPropHandler handler = new AcctFormPropHandler();
		WorkflowPropertyGen.getAllFormProperties(formPath, null, handler);
		// Journal entries SP
		createJournalEntries(handler);

		// Account balances
		createAccountBalance(handler);

		// Account Analysis
		createAccountAnalysis(handler);

		// General Ledger Listings
		createGeneralLedgerListings(handler);

		// Income Statement By Division
		createIncomeStatement(handler);

		// Income Statement By Division and month
		createIncomeStatementByDivMonth(handler);

		// Balance Sheet By Division
		createBalanceSheet(handler);
	}

	public static void createStoredProcedure (Dao<?> dao, String spName, String selectStatement, String body, String condition) {
		if (body == null | body.isEmpty())
			return;
		dao.executeSQL("DROP PROCEDURE IF EXISTS " + spName +";");
		String sql = selectStatement +" "+ body + " "+ condition;
		dao.executeSQL(sql);
	}
	private void createJournalEntries (AcctFormPropHandler handler) {
		String selectStatement = "CREATE PROCEDURE GET_JOURNAL_ENTRIES (IN IN_COMPANY_ID INT, IN DATE_FROM DATE, "
				+ "IN DATE_TO DATE, IN IN_SOURCE VARCHAR(50), IN IN_REFERENCE_NUMBER VARCHAR(50), IN IN_IS_POSTED INT,"
				+ "IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT) "
				+ "SELECT GL_DATE, SOURCE, REFERENCE_NUMBER, ACCT_NO, ACCOUNT_NAME, DESCRIPTION ,"
				+ "ROUND(SUM(DEBIT),2) AS DEBIT, ROUND(SUM(CREDIT),2) AS CREDIT, IS_POSTED, (CASE WHEN DEBIT != 0 THEN 1 ELSE 0 END) AS IS_DEBIT, "
				+ "DIVISION_NAME, CUSTOMER_PO_NO FROM ( ";
		String condition = ") as JOURNAL_ENTRIES GROUP BY SOURCE, ID, ACCOUNT_ID, DESCRIPTION, IS_DEBIT, DIVISION_NAME "
				+ "ORDER BY GL_DATE, REFERENCE_NUMBER, IS_DEBIT DESC;";
		JESPGenerator.createStoredProcedure((Dao<?>)dao, "GET_JOURNAL_ENTRIES", selectStatement, handler.getJournalEntriesSQL(), condition);
	}

	private void createAccountBalance (AcctFormPropHandler handler) {
		String spName = "GET_ACCOUNT_BALANCE";
		String select = "CREATE procedure GET_ACCOUNT_BALANCE (IN IN_COMPANY_ID INT, IN IN_DIVISION_ID INT, IN IN_ACCOUNT_ID INT, " +
				"IN DATE_FROM DATE, IN DATE_TO DATE, IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT) " +
				"BEGIN " + 
				"SELECT ACCOUNT_ID, SUM(ROUND(DEBIT, 2)) AS DEBIT, SUM(ROUND(CREDIT, 2)) AS CREDIT, ACCT_NO, ACCOUNT_TYPE_ID FROM ( ";
		String condition = ") as ACCOUNT_BALANCES GROUP BY ACCOUNT_ID ORDER BY ACCOUNT_TYPE_ID, ACCT_NO; "+
				"END;";

		JESPGenerator.createStoredProcedure(dao, spName, select, handler.getAccountBalancesSQL(), condition);

		spName = "GET_ACCOUNT_BEGINNING_BALANCE";
		select = "CREATE procedure GET_ACCOUNT_BEGINNING_BALANCE (IN IN_COMPANY_ID INT, IN IN_ACCOUNT_ID INT, "+
				"IN IN_FROM_DIVISION VARCHAR(5), IN IN_TO_DIVISION VARCHAR(5), IN DATE_FROM DATE, IN DATE_TO DATE, IN IN_DESCRIPTION VARCHAR(50)) "+
				"BEGIN "+
				"SELECT SUM(ROUND(DEBIT, 2)) as TOTAL_DEBIT, SUM(ROUND(CREDIT, 2)) as TOTAL_CREDIT FROM (";
		condition = ") as BEGINNING_BALANCE; "+
				"END;";
		// Account Analysis - Beginning balance
		JESPGenerator.createStoredProcedure(dao, spName, select, handler.getAccountAnalysisSQL(), condition);
	}

	private void createIncomeStatement (AcctFormPropHandler handler) {
		String spName = "GET_IS_BY_DIVISION";
		String select = "CREATE PROCEDURE GET_IS_BY_DIVISION (IN IN_COMPANY_ID INT, IN IN_ACCOUNT_ID INT, IN IN_DIVISION_ID INT, "
				+ "IN IN_DATE_FROM DATE, IN IN_DATE_TO DATE) "
				+ "BEGIN "
				+ "SELECT COMPANY_ID, DIVISION_NAME, DIVISION_ID, ACCOUNT_NAME, ACCOUNT_ID, SUM(ROUND(DEBIT, 2)) AS DEBIT, "
				+ "SUM(ROUND(CREDIT, 2)) AS CREDIT, ACCOUNT_TYPE_ID, PARENT_ACCOUNT_ID, ACCT_NO FROM ( ";
		String condition = ") AS IS_TBL "
				+ "WHERE (CASE WHEN IN_ACCOUNT_ID = -1 THEN ACCOUNT_ID = ACCOUNT_ID ELSE ACCOUNT_ID = IN_ACCOUNT_ID END) "
				+ "AND (CASE WHEN IN_DIVISION_ID = -1 THEN DIVISION_ID = DIVISION_ID ELSE DIVISION_ID = IN_DIVISION_ID END) "
				+ "GROUP BY DIVISION_ID, ACCOUNT_ID ORDER BY ACCT_NO; "
				+ "END;";
		JESPGenerator.createStoredProcedure(dao, spName, select, handler.getIncomeStatementSQL(), condition);
	}

	private void createIncomeStatementByDivMonth (AcctFormPropHandler handler) {
		String spName = "GET_IS_BY_DIVISION_AND_MONTH";
		String select = "CREATE PROCEDURE GET_IS_BY_DIVISION_AND_MONTH (IN IN_COMPANY_ID INT, IN IN_ACCOUNT_ID INT, IN IN_DIVISION_ID INT, "
				+ "IN IN_DATE_FROM DATE, IN IN_DATE_TO DATE) "
				+ "BEGIN "
				+ "SELECT COMPANY_ID, DIVISION_NAME, DIVISION_ID, ACCOUNT_NAME, ACCOUNT_ID, SUM(ROUND(DEBIT, 2)) AS DEBIT, "
				+ "SUM(ROUND(CREDIT, 2)) AS CREDIT, ACCOUNT_TYPE_ID, PARENT_ACCOUNT_ID, ACCT_NO, MONTH(DATE) AS MONTH, ACCT_NO FROM ( ";
		String condition = ") AS IS_TBL "
				+ "WHERE (CASE WHEN IN_ACCOUNT_ID = -1 THEN ACCOUNT_ID = ACCOUNT_ID ELSE ACCOUNT_ID = IN_ACCOUNT_ID END) "
				+ "AND (CASE WHEN IN_DIVISION_ID = -1 THEN DIVISION_ID = DIVISION_ID ELSE DIVISION_ID = IN_DIVISION_ID END) "
				+ "GROUP BY DIVISION_ID, ACCOUNT_ID, MONTH ORDER BY ACCT_NO; "
				+ "END;";
		JESPGenerator.createStoredProcedure(dao, spName, select, handler.getIncomeStatementSQL(), condition);
	}

	private void createBalanceSheet (AcctFormPropHandler handler) {
		String spName = "GET_ISBS_BY_DIVISION";
		String select = "CREATE PROCEDURE GET_ISBS_BY_DIVISION (IN IN_COMPANY_ID INT, IN IN_ACCOUNT_ID INT, IN IN_DIVISION_ID INT, "
				+ "IN IN_AS_OF_DATE DATE) "
				+ "BEGIN "
				+ "SELECT COMPANY_ID, DIVISION_NAME, DIVISION_ID, ACCOUNT_NAME, ACCOUNT_ID, SUM(ROUND(DEBIT, 2)) AS DEBIT, "
				+ "SUM(ROUND(CREDIT, 2)) AS CREDIT, ACCOUNT_TYPE_ID, PARENT_ACCOUNT_ID, ACCT_NO FROM ( ";
		String condition = ") AS BS_TBL "
				+ "WHERE (CASE WHEN IN_ACCOUNT_ID = -1 THEN ACCOUNT_ID = ACCOUNT_ID ELSE ACCOUNT_ID = IN_ACCOUNT_ID END) "
				+ "AND (CASE WHEN IN_DIVISION_ID = -1 THEN DIVISION_ID = DIVISION_ID ELSE DIVISION_ID = IN_DIVISION_ID END) "
				+ "GROUP BY DIVISION_ID, ACCOUNT_ID ORDER BY ACCOUNT_TYPE_ID, ACCT_NO; "
				+ "END;";
		JESPGenerator.createStoredProcedure(dao, spName, select, handler.getBalanceSheetSQL(), condition);
	}

	private void createAccountAnalysis(AcctFormPropHandler handler) {
		String spName = "GET_ACCOUNT_ANALYSIS";
		String select = "CREATE PROCEDURE GET_ACCOUNT_ANALYSIS(IN IN_COMPANY_ID INT, IN IN_ACCOUNT_ID INT, IN IN_FROM_DIVISION VARCHAR(5), "+ 
				"IN IN_TO_DIVISION VARCHAR(5), IN DATE_FROM DATE, IN DATE_TO DATE, IN IN_DESCRIPTION VARCHAR(50), IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT) "+
				"BEGIN " +
				"SELECT SOURCE, ID, COMPANY_ID, DIVISION_ID, ACCOUNT_ID, DIVISION_NO, DIVISION_NAME, ACCT_NO, ACCOUNT_NAME, GL_DATE, REFERENCE_NUMBER, DESCRIPTION, "+
				"SUM(ROUND(DEBIT, 2)) AS DEBIT, SUM(ROUND(CREDIT, 2)) AS CREDIT, FORM_WORKFLOW_ID, CURRENT_STATUS_ID, IS_COMPLETE, IS_POSTED FROM ( ";
		String condition = ") AS ACCOUNT_ANALYSIS GROUP BY REFERENCE_NUMBER, ID, SOURCE, DESCRIPTION ORDER BY GL_DATE, REFERENCE_NUMBER, DESCRIPTION; "+
				"END;";
		JESPGenerator.createStoredProcedure(dao, spName, select, handler.getAccountAnalysisSQL(), condition);
	}

	private void createGeneralLedgerListings(AcctFormPropHandler handler) {
		String spName = "GET_GENERAL_LEDGER_LISTING";
		String select = "CREATE PROCEDURE GET_GENERAL_LEDGER_LISTING(IN IN_COMPANY_ID INT, IN IN_ACCOUNT_ID INT, "+ 
				"IN DATE_FROM DATE, IN DATE_TO DATE, IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT) "+
				"BEGIN " +
				"SELECT GL_DATE, SOURCE, REFERENCE_NUMBER, ACCT_NO, ACCOUNT_NAME, DESCRIPTION ,"
				+ "SUM(DEBIT) AS DEBIT, SUM(CREDIT) AS CREDIT, IS_POSTED, "
				+ "(CASE WHEN DEBIT != 0 THEN 1 ELSE 0 END) AS IS_DEBIT, DIVISION_NAME FROM (  ";
		String condition = ") as GENERAL_LEDGER_LISTING GROUP BY ID, ACCOUNT_ID, DESCRIPTION, IS_DEBIT "
				+ "ORDER BY ACCT_NO, GL_DATE, REFERENCE_NUMBER, IS_DEBIT DESC; "
				+ "END;";
		JESPGenerator.createStoredProcedure(dao, spName, select, handler.getGeneralLedgerListingsSQL(), condition);
	}
}
