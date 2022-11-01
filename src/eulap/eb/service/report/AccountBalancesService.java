package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.domain.hibernate.NormalBalance;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.AccountBalancesDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Service for Account Balances Report

 */
@Service
public class AccountBalancesService {
	private static Logger logger = Logger.getLogger(AccountBalancesService.class);
	@Autowired
	private AccountDao accountDao;

	/**
	 * Get the account balances of a certain company.
	 * @return The acc: "+source);
		if(sourceount balances.
	 */
	public Page<AccountBalancesDto> getAccountBalances (int companyId,int divisionId, Date dateFrom,Date dateTo, PageSetting pageSetting) {
		logger.info("Generating the data for Account Balances Report of company "+companyId +" as of "
				+(dateFrom != null ? DateUtil.formatDate(dateFrom)+" until "+DateUtil.formatDate(dateTo) : DateUtil.formatDate(dateTo)));
		Page<AccountBalancesDto> accountBalancesData = accountDao.getAccountBalances(companyId, null, divisionId, dateFrom, dateTo, pageSetting);
		// Sort out accounts by order
		List<AccountBalancesDto> currAssets = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> nonCurrAssets = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> currLiabilities = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> nonCurrLiabilities = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> equities = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> revenues = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> others = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> directLabors = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> directMaterials = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> inventoryAdjs = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> mfgOverheads = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> mfgOverheadRms = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> sellings = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> genAdmins = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> deprecations = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> interestIncomes = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> otherIncomes = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> otherExpenses = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> supenseAccts = new ArrayList<AccountBalancesDto>();
		List<AccountBalancesDto> ret = new ArrayList<AccountBalancesDto>();
		for (AccountBalancesDto dto : accountBalancesData.getData()) {
			Integer accountTypeId = dto.getAccountTypeId();
			if (accountTypeId.equals(1)) {
				currAssets.add(dto);
			} else if (accountTypeId.equals(2) || accountTypeId.equals(10)
					|| accountTypeId.equals(11)) {
				nonCurrAssets.add(dto);
			} else if (accountTypeId.equals(3)) {
				currLiabilities.add(dto);
			} else if (accountTypeId.equals(4) || accountTypeId.equals(12)
					|| accountTypeId.equals(13)) {
				nonCurrLiabilities.add(dto);
			} else if (accountTypeId.equals(8) || accountTypeId.equals(14)
					|| accountTypeId.equals(15)) {
				equities.add(dto);
			} else if (accountTypeId.equals(5) || accountTypeId.equals(16)) {
				revenues.add(dto);
			} else if (accountTypeId.equals(18)) {
				directLabors.add(dto);
			} else if (accountTypeId.equals(17)) {
				directMaterials.add(dto);
			} else if (accountTypeId.equals(30)) {
				inventoryAdjs.add(dto);
			} else if (accountTypeId.equals(19)) {
				mfgOverheads.add(dto);
			} else if (accountTypeId.equals(20)) {
				mfgOverheadRms.add(dto);
			} else if (accountTypeId.equals(21)) {
				sellings.add(dto);
			} else if (accountTypeId.equals(22)) {
				genAdmins.add(dto);
			} else if (accountTypeId.equals(23)) {
				deprecations.add(dto);
			} else if (accountTypeId.equals(24)) {
				interestIncomes.add(dto);
			} else if (accountTypeId.equals(25)) {
				otherIncomes.add(dto);
			} else if (accountTypeId.equals(26)) {
				otherExpenses.add(dto);
			} else if (accountTypeId.equals(29)) {
				supenseAccts.add(dto);
			} else {
				others.add(dto);
			}
		}
		ret.addAll(currAssets);
		ret.addAll(nonCurrAssets);
		ret.addAll(currLiabilities);
		ret.addAll(nonCurrLiabilities);
		ret.addAll(equities);
		ret.addAll(revenues);
		ret.addAll(directLabors);
		ret.addAll(directMaterials);
		ret.addAll(inventoryAdjs);
		ret.addAll(mfgOverheads);
		ret.addAll(mfgOverheadRms);
		ret.addAll(sellings);
		ret.addAll(genAdmins);
		ret.addAll(deprecations);
		ret.addAll(interestIncomes);
		ret.addAll(otherIncomes);
		ret.addAll(otherExpenses);
		ret.addAll(supenseAccts);
		ret.addAll(others);
		logger.info("Retrieved "+accountBalancesData.getTotalRecords()+" data for Account Balances Report.");
		return new Page<AccountBalancesDto>(pageSetting, ret, accountBalancesData.getTotalRecords());
	}

	/**
	 * Get the account balance of the selected combination of company and account.
	 * @param bankAccountId The id of the selected bank account.
	 * @param companyId The id of the company.
	 * @param accountId The id of the account.
	 * @param asOfDate Compute the balance as of the specified date.
	 * @return The account balance.
	 */
	public AccountBalancesDto getAcctBalance(int bankAccountId, int companyId, int accountId, Date asOfDate) {
		return accountDao.getAcctBalance(bankAccountId, companyId, accountId, asOfDate);
	}

	/**
	 * Generates the data source of the Trial Balance Report.
	 * @param companyId The id of the company.
	 * @param asOfDate The as of date.
	 * @return The data source for the report.
	 */
	public JRDataSource generateTrialBalDataSource(int companyId, int divisionId, Date dateFrom, Date dateTo) {
		EBJRServiceHandler<AccountBalancesDto> trialBalHandler = new TrialBalanceHandler(companyId,divisionId,dateFrom,dateTo, this);
		return new EBDataSource<AccountBalancesDto>(trialBalHandler);
	}

	private static class TrialBalanceHandler implements EBJRServiceHandler<AccountBalancesDto> {
		private final int companyId;
		private final int divisionId;
		private final Date dateFrom;
		private final Date dateTo;
		private AccountBalancesService abService;
		private Page<AccountBalancesDto> currentPage;

		private TrialBalanceHandler(int companyId,Integer divisionId, Date dateFrom,Date dateTo,
				AccountBalancesService acctBalancesService) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.abService = acctBalancesService;
		}

		@Override
		public void close() throws IOException {
			currentPage = null;
		}

		@Override
		public Page<AccountBalancesDto> nextPage(PageSetting pageSetting) {
			logger.info("Generating the data for Trial Balances Report.");
			Page<AccountBalancesDto> accountBalances = abService.getAccountBalances(companyId, divisionId, dateFrom, dateTo, pageSetting);
			List<AccountBalancesDto> trialBalancesData = new ArrayList<AccountBalancesDto>();
			if(!accountBalances.getData().isEmpty()) {
				for (AccountBalancesDto abd : accountBalances.getData()) {
					double balance = abd.getBalance();
					if(NumberFormatUtil.roundOffTo2DecPlaces(balance) == 0) {
						continue;
					}
					logger.debug("Processing account: "+abd.getAccountName());
					logger.debug("Balance: "+balance);
					int normalBalanceId = abd.getAccount().getAccountType().getNormalBalanceId();
					boolean isDebit = normalBalanceId == NormalBalance.DEBIT;
					if(isDebit && balance > 0 || !isDebit && balance < 0) {
						//If Normal Balance is debit and value of balance is positive.
						//Or Normal Balance is credit and value of balance is negative.
						trialBalancesData.add(AccountBalancesDto.getInstance(abd.getAccount(),
								Math.abs(balance), 0.0, abd.getAccountTypeId()));
					} else if (!isDebit && balance > 0 || isDebit && balance < 0) {
						//If Normal Balance is credit and value of balance is positive.
						//Or Normal Balance is debit and value of balance is negative.
						trialBalancesData.add(AccountBalancesDto.getInstance(abd.getAccount(), 0.0,
								Math.abs(balance), abd.getAccountTypeId()));
					}
				}
				logger.debug("Successfully processed the data for Trial Balance.");
			}

			logger.info("Successfully retrieved and processed the data for Trial Balance report.");
			currentPage = new Page<AccountBalancesDto>(pageSetting, trialBalancesData, accountBalances.getTotalRecords());
			return currentPage;
		}
	}

	/**
	 * Generates the data source of the Account Balance Report.
	 * @param companyId The id of the company.
	 * @param asOfDate The as of date.
	 * @return The data source for the report.
	 */
	public JRDataSource getAccntBalancesReport(int companyId, int divisionId, Date asOfDate) {
		EBJRServiceHandler<AccountBalancesDto> handler = new JRAccountBalancesHandler(companyId, divisionId, asOfDate, this);
		return new EBDataSource<AccountBalancesDto>(handler);
	}

	private static class JRAccountBalancesHandler implements EBJRServiceHandler<AccountBalancesDto> {
		private int companyId;
		private int divisionId;
		private Date asOfDate;
		private AccountBalancesService balancesService;
		private Page<AccountBalancesDto> currentPage;

		private JRAccountBalancesHandler (Integer companyId, Integer divisionId,
				Date asOfDate, AccountBalancesService balancesService){
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.asOfDate = asOfDate;
			this.balancesService = balancesService;
		}

		@Override
		public void close() throws IOException {
			currentPage = null;
			balancesService = null;
		}

		@Override
		public Page<AccountBalancesDto> nextPage(PageSetting pageSetting) {
			logger.info("Generating the data for Account Balances Report of company "+companyId+" as of "+DateUtil.formatDate(asOfDate));
			currentPage = balancesService.getAccountBalances(companyId, divisionId, null, asOfDate, pageSetting);
			logger.info("Processing data on page "+pageSetting.getPageNumber()+".");
			return currentPage;
		}
	}
}
