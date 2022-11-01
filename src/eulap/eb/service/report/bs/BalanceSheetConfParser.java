package eulap.eb.service.report.bs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import eulap.common.util.PropertyLoader;
import eulap.eb.service.is.ISAccountType;

/**
 * Balance sheet configuration parser. 

 *
 */
public class BalanceSheetConfParser {
	private static final String CONFIGURATION_PATH = "/eulap/eb/conf/report/bs/";
	private static final String CONFIGURATION_FILE_EXTENSION = ".balancesheet";

	private static final String DELIMITER = ".";
	private static final String TYPE_SEPERATOR = ";";
	
	private static final String ASSET_TITLE = "asset_title";
	private static final String ASSET_TOTAL_LABEL = "asset_total_label";
	private static final String LIABILITIES_AND_EQUITY_TITLE = "liabilities_and_equity_title";
	private static final String LIABILITIES_AND_EQUITY_TOTAL_LABEL = "liabilities_and_equity_total_label";
	private static final String ACCOUNT_CLASSES = "account_classes";
	
	// Account classes
	private static final String AS_LABEL = "label";
	private static final String AS_IS_ASSET = "is_asset";
	private static final String AS_IS_CURRENT_ASSET = "is_current_asset";
	private static final String AS_IS_LIABILITY = "is_liability";
	private static final String AS_TOTAL_LABEL = "total_label";
	private static final String AS_SEQUENCE_NUMBER = "sequence_number";
	private static final String AS_ACCOUNT_TYPES = "account_types";
	
	// Account types
	private static final String AT_DB_ACCOUNT_TYPE_ID = "db_account_type_id";
	private static final String AT_IS_POSITIVE = "is_positive";
	private static final String AT_SEQUENCE_NUMBER = "sequence_number";

	private static final int DEFAULT_COMPANY = 1;

	private static Logger logger = Logger.getLogger(BalanceSheetConfParser.class);
	/**
	 * Parse the configured different classes and type based on the configuration file.
	 * @param companyId The configured company id.
	 * @return The income statement with parsed configuration 
	 * @throws ConfigurationException Throw when there are configuration problem.
	 */
	public static List<BalanceSheet> parseBalanceSheet (int companyId) throws ConfigurationException {
		String confPath = CONFIGURATION_PATH + DEFAULT_COMPANY + CONFIGURATION_FILE_EXTENSION;
		Properties conf = null;
		try {
			conf = PropertyLoader.getProperties(confPath);
			logger.debug("configuration path : " + confPath);
			logger.info("parsing the configured data for company : " + companyId);
			logger.debug("Parsing the titles and labels");
			String assetTitle = conf.getProperty(ASSET_TITLE);
			if (assetTitle == null){
				logger.error("Error while parsing the asset title for "+ companyId);
				throw new ConfigurationException("Error in parsing the asset title for company id " + companyId);
			}
			String assetTotalLabel = conf.getProperty(ASSET_TOTAL_LABEL);
			if (assetTotalLabel == null)
				throw new ConfigurationException("Error in parsing the asset total label for company id " + companyId);
			String liabilitiesTitle = conf.getProperty(LIABILITIES_AND_EQUITY_TITLE);
			if (liabilitiesTitle == null)
				throw new ConfigurationException("Error in parsing the expenses title for company id " + companyId);
			String liabilitiesTotalLabel = conf.getProperty(LIABILITIES_AND_EQUITY_TOTAL_LABEL);
			if (liabilitiesTotalLabel == null)
				throw new ConfigurationException("Error in parsing the expenses total title label for company id " + companyId);
			
			// Current asset account classes
			List<BSAccountClass> cAssetClasses = new ArrayList<BSAccountClass>();
			// Non current asset account classes
			List<BSAccountClass> nonCAssetClasses = new ArrayList<BSAccountClass>();
			// Liability account classes
			List<BSAccountClass> liabilityClasses = new ArrayList<BSAccountClass>();
			// Equity account classes
			List<BSAccountClass> equityClasses = new ArrayList<BSAccountClass>();
			List<BSAccountClass> accountClasses = getAccountClasses(companyId, conf);
			logger.debug("seperating the assets and liabilities");
			for (BSAccountClass accountClass : accountClasses) {
				if (accountClass.isAsset()) {
					logger.debug("separating current asset and non current asset");
					if (accountClass.isCurrentAsset())
						cAssetClasses.add(accountClass);
					else
						nonCAssetClasses.add(accountClass);
				} else {
					logger.debug("separating liability and equity");
					if (accountClass.isLiability())
						liabilityClasses.add(accountClass);
					else
						equityClasses.add(accountClass);
				}
			}
			Comparator<BSAccountClass> accountClassComparator = new Comparator<BSAccountClass>() {
				@Override
				public int compare(BSAccountClass o1, BSAccountClass o2) {
					return o2.getSequenceOrder() - o1.getSequenceOrder();
				}
			};
			logger.debug("sorting the classes order by its sequence number");
			Collections.sort(cAssetClasses, accountClassComparator);
			Collections.sort(nonCAssetClasses, accountClassComparator);
			Collections.sort(liabilityClasses, accountClassComparator);
			Collections.sort(equityClasses, accountClassComparator);
			
			logger.debug("merging the current and non current asset in one list.");
			List<BSAccountClass> assets = new ArrayList<BSAccountClass>(cAssetClasses);
			assets.addAll(nonCAssetClasses);
			
			logger.debug("creating a balance sheet list.");
			List<BalanceSheet> balanceSheets = new ArrayList<BalanceSheet>();
			
			logger.debug("creating a balance sheet object that will contain the assets");
			BalanceSheet bsAssets = BalanceSheet.getInstanceBy(assetTitle, assetTotalLabel, assets );
			logger.debug("adding the asset balance sheet to the list of balance sheets");
			balanceSheets.add(bsAssets);
			
			logger.debug("merging the liabilities and equity in one list.");
			List<BSAccountClass> equityAndLiabilities = new ArrayList<BSAccountClass>(liabilityClasses);
			equityAndLiabilities.addAll(equityClasses);
			
			logger.debug("creating a balance sheet object that will contain the liabilities and equities");
			BalanceSheet bsEAndLiabilities = BalanceSheet.getInstanceBy(liabilitiesTitle, 
					liabilitiesTotalLabel, equityAndLiabilities);
			logger.debug("adding the asset balance sheet to the list of balance sheets");
			balanceSheets.add(bsEAndLiabilities);
					
			logger.info("successfully parsed the balance sheet configuration for company " + companyId);
			return balanceSheets;
		} finally {
			if (conf != null)
				conf.clear();
		}
	}
	
	private static List<BSAccountClass> getAccountClasses (int companyId, Properties conf) throws ConfigurationException {
		logger.info("parsing account classes");
		String delimitedAccountClasses = conf.getProperty(ACCOUNT_CLASSES);
		if (delimitedAccountClasses == null || delimitedAccountClasses.isEmpty()) 
			throw new ConfigurationException("Error in parsing the "+ACCOUNT_CLASSES+" for company" + companyId);

		List<BSAccountClass> accountClasses = new ArrayList<BSAccountClass>();
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
			String strIsAsset = conf.getProperty(strAccountClass+DELIMITER+AS_IS_ASSET);
			if (strIsAsset == null) {
				logger.error("missing configuration for + " + strAccountClass + DELIMITER+AS_IS_ASSET);
				throw new ConfigurationException("Error in parsing the "+AS_IS_ASSET+" for account class " + strAccountClass);
			}
			boolean isAsset = Boolean.valueOf(strIsAsset);
			
			String strIsCurrentAsset = conf.getProperty(strAccountClass+DELIMITER+AS_IS_CURRENT_ASSET);
			if (strIsCurrentAsset == null) {
				logger.error("missing configuration for + " + strAccountClass + DELIMITER+AS_IS_ASSET);
				throw new ConfigurationException("Error in parsing the "+AS_IS_CURRENT_ASSET+" for account class " + strAccountClass);
			}
			boolean isCurrentAsset = Boolean.valueOf(strIsCurrentAsset);
			
			String strIsLiability = conf.getProperty(strAccountClass+DELIMITER+AS_IS_LIABILITY);
			if (strIsLiability == null) {
				logger.error("missing configuration for + " + strAccountClass + DELIMITER+AS_IS_LIABILITY);
				throw new ConfigurationException("Error in parsing the "+AS_IS_LIABILITY+" for account class " + strAccountClass);
			}
			boolean isLiability = Boolean.valueOf(strIsLiability);
			
			String strTotalLabel = conf.getProperty(strAccountClass+DELIMITER+AS_TOTAL_LABEL);
			if (strTotalLabel == null) {
				logger.error("missing configuration for + " + strAccountClass + DELIMITER+AS_TOTAL_LABEL);
				throw new ConfigurationException("Error in parsing the "+AS_TOTAL_LABEL+" for account class " + strAccountClass);
			}
			
			String strSequenceNumber = conf.getProperty(strAccountClass+DELIMITER+AS_SEQUENCE_NUMBER);
			if (strSequenceNumber == null) {
				logger.error("missing configuration for + " + strAccountClass + DELIMITER+AS_SEQUENCE_NUMBER);
				throw new ConfigurationException("Error in parsing the "+AS_SEQUENCE_NUMBER+" for account class " + strAccountClass);
			}
			int sequenceNumber = Integer.valueOf(strSequenceNumber);
			BSAccountClass accountClass =
					BSAccountClass.getInstanceOf(label, isAsset, isCurrentAsset, isLiability, strTotalLabel, sequenceNumber);
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
			
			String strSequenceNumber = conf.getProperty(strAccountType+DELIMITER+AT_SEQUENCE_NUMBER);
			if (strSequenceNumber == null) {
				logger.error("Missing configuration for " + strAccountType+DELIMITER+AT_SEQUENCE_NUMBER);
				throw new ConfigurationException("Error in parsing the "+AT_SEQUENCE_NUMBER+" for account type " + strAccountType);
			}
			int sequenceNumber = Integer.valueOf(strSequenceNumber);
			ISAccountType accountType = ISAccountType.getInstanceOf(accountTypeId, isPositive, false, sequenceNumber);
			accountTypes.add(accountType);
		}
		logger.info("successfully paresed all of the account types for " + accountClass);
		return accountTypes;
	}
}
