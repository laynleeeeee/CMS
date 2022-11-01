package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.AccountTypeDao;
import eulap.eb.dao.IsAtSetupDao;
import eulap.eb.dao.IsClassSetupDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.service.is.ISAccountType;
import eulap.eb.service.report.bs.BSAccountClass;
import eulap.eb.service.report.bs.BalanceSheet;
import eulap.eb.service.report.bs.BalanceSheetConfParser;
import eulap.eb.web.dto.AccountBalancesDto;
import eulap.eb.web.dto.ISAccountDto;

/**
 * Class that handles the business logic of balance sheet.

 *
 */
@Service
public class BalanceSheetService {
	@Autowired
	private IsClassSetupDao isClassSetupDao;
	@Autowired
	private IsAtSetupDao isAtSetupDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountTypeDao accountTypeDao;
	
	private static Logger logger = Logger.getLogger(BalanceSheet.class);
	
	/**
	 * Compute and process the balance sheet of the company.
	 * @param companyId The company id
	 * @param timePeriodId The selected time period.
	 * @param isIncludeZeroAmount true to include zero account, otherwise false.
	 * @return Income statement for rendition of rendition in the view.
	 * @throws ConfigurationException if there are any configuration error found. 
	 */
	public List<BalanceSheet> processBalanceSheet (final int companyId, final Date asOfDate, final boolean isIncludeZeroAmount) throws ConfigurationException {
		logger.info("Start: generate income statement for company "+ companyId + " as of date " + 
				DateUtil.formatDate(asOfDate));
		logger.info("show account with zero amount : " + isIncludeZeroAmount);
		logger.debug("Getting instance of balance sheet based on the configuration file ");
		List<BalanceSheet> balanceSheets = BalanceSheetConfParser.parseBalanceSheet(companyId);
		PageSetting pageSetting = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		List<AccountBalancesDto> accountBalances = 
				new ArrayList<AccountBalancesDto>(accountDao.getAccountBalances(companyId, asOfDate, pageSetting).getData());
		for (BalanceSheet balanceSheet : balanceSheets) {
			logger.debug("processing "+balanceSheet.getTitle()+" account classes");
			balanceSheet.setAmount(computeAndProcess(companyId, asOfDate, false, balanceSheet.getAccountClasses(), accountBalances));
			logger.debug(balanceSheet.getTotalLabel() + "=====================" + balanceSheet.getAmount());
		}
			
		logger.info("successfuly processed the balance sheet");
		return balanceSheets;
	}
	
	private double computeAndProcess (final int companyId,  final Date asOfDate,
			final boolean isIncludeZeroAmount, List<BSAccountClass> accountClasses, List<AccountBalancesDto> accountBalances) {
		double totalAmount = 0;
		
		for (BSAccountClass accountClass : accountClasses) {
			double accountClassAmount = 0;
			logger.debug("processing account type for " + accountClass);
			for (ISAccountType isAccountType : accountClass.getAccountTypes()) {
				double accountTypeAmount = 0;
				logger.debug ("Setting the attribute/fields from the databased of the " + isAccountType);
				int accountTypeId = isAccountType.getAccountTypeId();
				AccountType accountType = accountTypeDao.get(accountTypeId);
				if (accountType == null) {
					logger.error("Unable to find account type id  " + accountTypeId + " in the database");
					throw new NullPointerException("unable to find account type id " + accountTypeId);
				}
				
				logger.debug("Successfully retrieved account type object ," + accountType);
				isAccountType.setAccountType(accountType);
				logger.debug("Retrieving all accounts under " + accountType.getName() +", " + accountTypeId);
				
				List<Account> accounts = accountDao.getAcctsByCompanyAndType(companyId, accountTypeId);
				if (accounts.isEmpty()) 
					logger.warn(".....No account found ........");
				
				List<ISAccountDto> isAccounts = new ArrayList<ISAccountDto>();
				
				for (Account account : accounts) {
					ISAccountDto accountDto = ISAccountDto.getInstance(accountBalances, account.getId());
					if (isIncludeZeroAmount || accountDto != null){
						logger.debug("processing account " + accountDto);
						if (accountDto != null) {
							double amount = accountDto.getAmount(accountType);
							accountDto.setAmount(amount);
							logger.trace(accountDto.getAccountName() + " amount ===== " + amount);
							accountTypeAmount += amount;
							logger.trace (accountType.getName() + " amount =====" + accountTypeAmount);
						}
						isAccounts.add(accountDto);
					}
					
					//Temporary commenting this for now since this affects the speed performance in generating balance sheet report. 
					/*logger.debug("checking if account has a contra account");
					
					ISAccountDto contraDto = accountDao.getISOrBSAccounts(companyId, account.getId(), 
							asOfDate, null, false, true);
					
					if (isIncludeZeroAmount || contraDto != null) {
						// Set the amount to negative
						if (contraDto != null) {
							logger.debug("processing account " + contraDto.getAccountName());
							double amount = contraDto.getAmount(accountType);
							contraDto.setAmount(amount);
							logger.debug("contra account, "+accountDto.getAccountName() + " amount ===== " + amount);
							accountTypeAmount += amount;
							logger.debug(accountType.getName() + " amount =====" + accountTypeAmount);
						}
						isAccounts.add(contraDto);
					}*/
					
					if (accountDto != null)
						logger.debug("successfully retrieved and added the account " + accountDto.getAccountName() +
							" together with its contra account (if existing).");
				}
				
				logger.debug("total amount for " +accountType.getName() +" is " + accountTypeAmount);
				logger.debug(isAccountType.getAccountType().getName() + " is positive " + isAccountType.isPositive());
				if (!isAccountType.isPositive()) 
					accountTypeAmount *= -1; //Negate the value
				
				isAccountType.setAmount(accountTypeAmount);
				isAccountType.setAccounts(isAccounts);
				accountClassAmount += accountTypeAmount;
				accountClass.setAmount(accountClassAmount);
				logger.trace (accountClass.getName() + "amount ======= " + accountClassAmount);
			}
			totalAmount += accountClassAmount;
		}
		return totalAmount;
	}
}
