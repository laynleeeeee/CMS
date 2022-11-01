package eulap.eb.service.is;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import eulap.common.util.PropertyLoader;

/**
 * Income statement configuration parser. 

 *
 */
public class IncomeStatementConfParser {
	private static final String CONFIGURATION_PATH = "/eulap/eb/conf/report/is/";
	private static final String CONFIGURATION_FILE_EXTENSION = ".incomestatement";
	
	private static final String DELIMITER = ".";
	private static final String TYPE_SEPERATOR = ";";
	
	private static final String INCOME_TITLE = "income_title";
	private static final String INCOME_TOTAL_LABEL = "income_total_label";
	private static final String EXPENSES_TITLE = "expense_title";
	private static final String EXPENSES_TOTAL_LABEL = "expense_total_label";
	private static final String ACCOUNT_CLASSES = "account_classes";
	
	// Account classes
	private static final String AS_LABEL = "label";
	private static final String AS_IS_REVENUE = "is_positive";
	private static final String AS_IS_INCOME = "is_income";
	private static final String AS_SEQUENCE_NUMBER = "sequence_number";
	private static final String AS_ACCOUNT_TYPES = "account_types";
	
	// Account types
	private static final String AT_DB_ACCOUNT_TYPE_ID = "db_account_type_id";
	private static final String AT_IS_POSITIVE = "is_positive";
	private static final String AT_AS_OF_BALANCE = "as_of_balance";
	private static final String AT_SEQUENCE_NUMBER = "sequence_number";

	private static final int DEFAULT_COMPANY = 1;

	private static Logger logger = Logger.getLogger(IncomeStatementConfParser.class);
	/**
	 * Parse the configured different classes and type based on the configuration file.
	 * @param companyId The configured company id.
	 * @return The income statement with parsed configuration 
	 * @throws ConfigurationException Throw when there are configuration problem.
	 */
	public static IncomeStatement parseIncomeStatement (int companyId) throws ConfigurationException {
		String confPath = CONFIGURATION_PATH + DEFAULT_COMPANY + CONFIGURATION_FILE_EXTENSION;
		logger.debug("configuration path : " + confPath);
		
		Properties conf = null;
		try {
			conf = PropertyLoader.getProperties(confPath);
			logger.info("parsing the configured data for company : " + companyId);
			logger.debug("Parsing the titles and labels");
			String incomeTitle = conf.getProperty(INCOME_TITLE);
			if (incomeTitle == null){
				logger.error("Error while parsing the income title for "+ companyId);
				throw new ConfigurationException("Error in parsing the income title for company id " + companyId);
			}
			String incomeTotalLabel = conf.getProperty(INCOME_TOTAL_LABEL);
			if (incomeTotalLabel == null)
				throw new ConfigurationException("Error in parsing the income total label for company id " + companyId);
			String expensesTitle = conf.getProperty(EXPENSES_TITLE);
			if (expensesTitle == null)
				throw new ConfigurationException("Error in parsing the expenses title for company id " + companyId);
			String expensesTotalLabel = conf.getProperty(EXPENSES_TOTAL_LABEL);
			if (expensesTotalLabel == null)
				throw new ConfigurationException("Error in parsing the expenses total title label for company id " + companyId);
			
			List<ISAccountClass> incomeClasses = new ArrayList<ISAccountClass>();
			List<ISAccountClass> expensesClasses = new ArrayList<ISAccountClass>();
			List<ISAccountClass> accountClasses = getAccountClasses(companyId, conf);
			logger.debug("seperating the income and expenses classes");
			for (ISAccountClass accountClass : accountClasses) {
				if (accountClass.isIncome()) 
					incomeClasses.add(accountClass);
				else 
					expensesClasses.add(accountClass);
			}
			Comparator<ISAccountClass> accountClassComparator = new Comparator<ISAccountClass>() {
				@Override
				public int compare(ISAccountClass o1, ISAccountClass o2) {
					return o2.getSequenceOrder() - o1.getSequenceOrder();
				}
			};
			logger.debug("sorting the classes order by its sequence number");
			Collections.sort(incomeClasses, accountClassComparator);
			Collections.sort(expensesClasses, accountClassComparator);
			
			logger.debug("Creating sub-statement for income");
			SubStatement incomeStatement = SubStatement.getInstanceBy(incomeTitle, incomeTotalLabel, 
					true, incomeClasses);
			logger.debug("Creating sub-statement for expenses");
			SubStatement expensesStatement = SubStatement.getInstanceBy(expensesTitle, expensesTotalLabel, 
					false, expensesClasses);
			logger.debug("creating the incomeStatement object");				
			List<SubStatement> subStatements = new ArrayList<SubStatement>();
			subStatements.add(incomeStatement);
			subStatements.add(expensesStatement);
			
			IncomeStatement is = IncomeStatement.getInstanceOf(subStatements);
			logger.info("successfully parsed the income statement configuration for company " + companyId);
			return is;
		} finally {
			if (conf!=null)
				conf.clear();
		}
	}
	
	private static List<ISAccountClass> getAccountClasses (int companyId, Properties conf) throws ConfigurationException {
		logger.info("parsing account classes");
		String delimitedAccountClasses = conf.getProperty(ACCOUNT_CLASSES);
		if (delimitedAccountClasses == null || delimitedAccountClasses.isEmpty()) 
			throw new ConfigurationException("Error in parsing the "+ACCOUNT_CLASSES+" for company" + companyId);

		List<ISAccountClass> accountClasses = new ArrayList<ISAccountClass>();
		Comparator<ISAccountType> accountTypeComparator = new Comparator<ISAccountType>() {
			
			@Override
			public int compare(ISAccountType o1, ISAccountType o2) {
				return o2.getSequenceOrder() - o1.getSequenceOrder();
			}
		};
		
		logger.debug("parsing the configuration of account classes");
		for (String strAccountClass : delimitedAccountClasses.split(TYPE_SEPERATOR)) {
			String label = conf.getProperty(strAccountClass + DELIMITER+AS_LABEL);
			if (label == null) {
				logger.error("missing configuration for " + strAccountClass + DELIMITER+AS_LABEL);
				throw new ConfigurationException("Error in parsing the "+AS_LABEL+" for account class " + strAccountClass);
			}
			String strSsRev = conf.getProperty(strAccountClass+DELIMITER+AS_IS_REVENUE);
			if (strSsRev == null) {
				logger.error("missing configuration for + " + strAccountClass + DELIMITER+AS_IS_REVENUE);
				throw new ConfigurationException("Error in parsing the "+AS_IS_REVENUE+" for account class " + strAccountClass);
			}
			boolean isRevenue = Boolean.valueOf(strSsRev);
			
			String strIsGrossProfit = conf.getProperty(strAccountClass+DELIMITER+AS_IS_INCOME);
			if (strIsGrossProfit == null) {
				logger.error("missing configuration for + " + strAccountClass + DELIMITER+AS_IS_INCOME);
				throw new ConfigurationException("Error in parsing the "+AS_IS_INCOME+" for account class " + strAccountClass);
			}
			boolean isGrossProfit = Boolean.valueOf(strIsGrossProfit);
			
			String strSequenceNumber = conf.getProperty(strAccountClass+DELIMITER+AS_SEQUENCE_NUMBER);
			if (strSequenceNumber == null) {
				logger.error("missing configuration for + " + strAccountClass + DELIMITER+AS_SEQUENCE_NUMBER);
				throw new ConfigurationException("Error in parsing the "+AS_SEQUENCE_NUMBER+" for account class " + strAccountClass);
			}
			int sequenceNumber = Integer.valueOf(strSequenceNumber);
			ISAccountClass accountClass =
					ISAccountClass.getInstanceOf(label, isRevenue, isGrossProfit, sequenceNumber);
			List<ISAccountType> accountTypes = getAccountTypes(strAccountClass, conf);
			
			Collections.sort(accountTypes, accountTypeComparator);
			accountClass.setAccountTypes(accountTypes);
			accountClasses.add(accountClass);
		}
		logger.info("successfully parsed the account classess");
		return accountClasses;
	}
	
	private static List<ISAccountType> getAccountTypes (String accountClass, Properties conf) throws ConfigurationException {
		logger.info("creating account types for " + accountClass);
		List<ISAccountType> accountTypes = new ArrayList<ISAccountType>();
		String delimitedAccountTypes = conf.getProperty(accountClass+DELIMITER+AS_ACCOUNT_TYPES);
		if (delimitedAccountTypes == null || delimitedAccountTypes.isEmpty()) { 
			logger.error("Missing configuration for " + accountClass+DELIMITER+AS_ACCOUNT_TYPES);
			throw new  ConfigurationException("Error in parsing the "+AS_ACCOUNT_TYPES+" for account class " + accountClass);
		}
		for (String strAccountType : delimitedAccountTypes.split(TYPE_SEPERATOR)) {
			String strAccountTypeId = conf.getProperty(strAccountType+DELIMITER+AT_DB_ACCOUNT_TYPE_ID);
			if (strAccountTypeId == null) {
				logger.error("Missing configuration for " + strAccountType+DELIMITER+AT_DB_ACCOUNT_TYPE_ID);
				throw new ConfigurationException("Error in parsing the "+AT_DB_ACCOUNT_TYPE_ID+" for account type " + strAccountType);
			}
			int accountTypeId = Integer.valueOf(strAccountTypeId);
			
			String strIsPositive = conf.getProperty(strAccountType + DELIMITER + AT_IS_POSITIVE);
			if (strIsPositive == null) { 
				logger.error("Missing configuration for " + strAccountType+DELIMITER+AT_IS_POSITIVE);
				throw new ConfigurationException("Error in parsing the "+AT_IS_POSITIVE+" for account type " + strAccountType);
			}
			boolean isPositive = Boolean.valueOf(strIsPositive);
			
			String strAsOfBalance = conf.getProperty(strAccountType + DELIMITER + AT_AS_OF_BALANCE);
			if (strAsOfBalance == null) { 
				logger.error("Missing configuration for " + strAccountType+DELIMITER+AT_AS_OF_BALANCE);
				throw new ConfigurationException("Error in parsing the "+AT_AS_OF_BALANCE+" for account type " + strAccountType);
			}
			boolean asOfBalance = Boolean.valueOf(strAsOfBalance);
			
			String strSequenceNumber = conf.getProperty(strAccountType+DELIMITER+AT_SEQUENCE_NUMBER);
			if (strSequenceNumber == null) {
				logger.error("Missing configuration for " + strAccountType+DELIMITER+AT_SEQUENCE_NUMBER);
				throw new ConfigurationException("Error in parsing the "+AT_SEQUENCE_NUMBER+" for account type " + strAccountType);
			}
			int sequenceNumber = Integer.valueOf(strSequenceNumber);
			ISAccountType accountType = ISAccountType.getInstanceOf(accountTypeId, isPositive, asOfBalance, sequenceNumber);
			accountTypes.add(accountType);
		}
		logger.info("successfully paresed all of the account types for " + accountClass);
		return accountTypes;
	}
}
