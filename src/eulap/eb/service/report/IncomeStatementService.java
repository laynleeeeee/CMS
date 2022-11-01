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
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.service.is.ISAccountClass;
import eulap.eb.service.is.ISAccountType;
import eulap.eb.service.is.IncomeStatement;
import eulap.eb.service.is.IncomeStatementConfParser;
import eulap.eb.service.is.SubStatement;
import eulap.eb.web.dto.AccountBalancesDto;
import eulap.eb.web.dto.ISAccountDto;

/**
 * Income statement service.

 *
 */
@Service
public class IncomeStatementService {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountTypeDao accountTypeDao;
	
	private static Logger logger = Logger.getLogger(IncomeStatement.class);
	
	/**
	 * Compute and process the income statement of the company.
	 * @param companyId The company id
	 * @param timePeriodId The selected time period.
	 * @param isIncludeZeroAmount true to include zero account, otherwise false.
	 * @return Income statement for rendition of rendition in the view.
	 * @throws ConfigurationException if there are any configuration error found. 
	 */
	public List<IncomeStatement> processIncomeStatement (final int companyId, final Date dateFrom, final Date dateTo, final boolean isIncludeZeroAmount) throws ConfigurationException {
		logger.info("show account with zero amount : " + isIncludeZeroAmount);
		logger.debug("Getting instance of income statement based on the configuration file ");
		IncomeStatement is = IncomeStatementConfParser.parseIncomeStatement(companyId);
		
		logger.info("processing and computing the sub-statements(income and expenses).");
		IncomeStatementTotal ist = new IncomeStatementTotal();
		PageSetting pageSetting = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		List<AccountBalancesDto> accountBalances =  getAccountBalances(pageSetting, companyId, dateFrom, dateTo);
		
		List<AccountBalancesDto> accountAsOfBalances = 
				new ArrayList<AccountBalancesDto>(accountDao.getAccountBalances(companyId,
						dateTo, pageSetting).getData());
		for (SubStatement subStatement : is.getSubStatements()) {
			ist.accountClassesTotal = 0;
			logger.debug("processing "+subStatement.getTitle()+" account classes");
			ist = computeAndProcess(companyId, dateFrom, dateTo,
					isIncludeZeroAmount, accountBalances, accountAsOfBalances, subStatement.getAccountClasses(), ist);
			logger.debug(subStatement.getTotalLabel() + "=============================" + ist.accountClassesTotal);
			subStatement.setAmount(ist.accountClassesTotal);
		}
		is.setAmount(ist.netIncome);
		
		logger.debug("Put income statement to list to prepare for jasper datasource.");
		List<IncomeStatement> incomeStatements = new ArrayList<IncomeStatement>();
		incomeStatements.add(is);
		
		logger.info("successfuly processed the income statement");
		return incomeStatements;
	}
	
	private List<AccountBalancesDto> getAccountBalances (PageSetting pageSetting, final int companyId, final Date dateFrom, final Date dateTo) {
		List<AccountBalancesDto> ret = new ArrayList<>();
		List<AccountBalancesDto> startAccountBalances = 
				new ArrayList<AccountBalancesDto>(accountDao.getAccountBalances(companyId,
						DateUtil.deductDaysToDate(dateFrom, 1), pageSetting).getData());
		List<AccountBalancesDto> endAccountBalances = 
				new ArrayList<AccountBalancesDto>(accountDao.getAccountBalances(companyId,
						dateTo, pageSetting).getData());
		List<AccountBalancesDto> unProcessed = new ArrayList<>();
		for (AccountBalancesDto eAb : endAccountBalances) {
			boolean isExistedInSAb = false; // We need to check if there are ending accounts that are not existing in the beginning accounts. 
			for (AccountBalancesDto sAb : startAccountBalances) {
				if (sAb.getAccount().getId() == eAb.getAccount().getId()) {
					isExistedInSAb = true;
					double debit = eAb.getDebit() - sAb.getDebit();
					double credit = eAb.getCredit() - sAb.getCredit();
					AccountBalancesDto abDto = AccountBalancesDto.getInstance(sAb.getAccount(), debit, credit, null);
					ret.add(abDto);
				}
			}
			if (!isExistedInSAb) {
				unProcessed.add(eAb);
			}
		}
		
		ret.addAll(unProcessed);
		return ret;
	}
	
	private IncomeStatementTotal computeAndProcess(final int companyId, final Date dateFrom, final Date dateTo, 
			final boolean isIncludeZeroAmount, List<AccountBalancesDto> accountBalances, List<AccountBalancesDto> accountAsOfBalances,
			List<ISAccountClass> accountClasses, IncomeStatementTotal ist) {
		double totalAmount = 0;
		for (ISAccountClass accountClass : accountClasses) {
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
					if (!isAccountType.isAsOfBalance()) {
						ISAccountDto accountDto = ISAccountDto.getInstance (accountBalances, account.getId());
					
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
						
						logger.debug("checking if account has a contra account");
						// TODO: Temporary commenting this for now since this affects the speed performance in generating balance sheet report.
//						ISAccountDto contraDto = accountDao.getISOrBSAccounts(companyId, account.getId(), 
//								timePeriod.getDateFrom(), timePeriod.getDateTo(), true, true);
//						
//						if (isIncludeZeroAmount || contraDto != null) {
//							// Set the amount to negative
//							if (contraDto != null) {
//								logger.debug("processing account " + contraDto.getAccountName());
//								double amount = contraDto.getAmount(accountType);
//								contraDto.setAmount(amount);
//								logger.debug("contra account, "+accountDto.getAccountName() + " amount ===== " + amount);
//								accountTypeAmount += amount;
//								logger.debug(accountType.getName() + " amount =====" + accountTypeAmount);
//							}
//							
//							isAccounts.add(contraDto);
//						}
//						
//						if (accountDto != null)
//							logger.debug("successfully retrieved and added the account " + accountDto.getAccountName() +
//									" together with its contra account (if existing).");
					} else {
						ISAccountDto asOfBalanceDto = ISAccountDto.getInstance(accountAsOfBalances, account.getId());
						if (asOfBalanceDto != null) {
							logger.debug("processing account " + asOfBalanceDto.getAccountName());
							double amount = asOfBalanceDto.getAmount(accountType);
							asOfBalanceDto.setAmount(amount);
							logger.debug("contra account, "+asOfBalanceDto.getAccountName() + " amount ===== " + amount);
							isAccounts.add(asOfBalanceDto);
							accountTypeAmount += amount;
							logger.debug(accountType.getName() + " amount =====" + accountTypeAmount);
							logger.debug("successfully retrieved as of balance account " + asOfBalanceDto.getAccountName() +
									".");
						}
					}
				}
				
				logger.debug("total amount for " +accountType.getName() +" is " + accountTypeAmount);
				logger.debug(isAccountType.getAccountType().getName() + " is positive " + isAccountType.isPositive());
				if (!isAccountType.isPositive()) 
					accountTypeAmount *= -1; //Negate the value
				
				isAccountType.setAmount(accountTypeAmount);
				isAccountType.setAccounts(isAccounts);
				accountClassAmount += accountTypeAmount;
				
				logger.trace (accountClass.getName() + "amount ======= " + accountClassAmount);
			}
			
			logger.info("Successfully computed and processed the account class ");
			logger.trace (accountClass.getName() + " is revenue " + accountClass.isPositive());
			int revenueMultiplier = 1;
			if (!accountClass.isPositive()) 
				revenueMultiplier = -1;
			accountClassAmount *= revenueMultiplier;
			logger.trace(accountClass.getName() +"=============" + accountClassAmount);
			accountClass.setAmount(accountClassAmount);
			totalAmount += accountClassAmount;
			int incomeMultiplier = 1;
			if (!accountClass.isIncome()) 
				incomeMultiplier = -1;
			ist.netIncome += (accountClassAmount * incomeMultiplier);
		}
		ist.accountClassesTotal = totalAmount;
		return ist;
	}
	
	private static class IncomeStatementTotal {
		private double accountClassesTotal;
		private double netIncome;
	}
}