package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.AccountDao;
import eulap.eb.dao.BankAccountDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.web.dto.AccountAnalysisReport;
import eulap.eb.web.dto.AccountBalancesDto;
import eulap.eb.web.dto.BankRecon;
import eulap.eb.web.dto.BankReconItem;
import eulap.eb.web.dto.BankReconSummaryDto;

/**
 * Business logic for generating Bank Reconciliation Report.


 */
@Service
public class BankReconService {
	@Autowired
	private BankAccountService baService;
	@Autowired
	private AccountCombinationService acctCombiService;
	@Autowired
	private AccountBalancesService acctBalService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private BankAccountDao brDao;
	@Autowired
	private AccountDao accountDao;

	private static int DIT_FIELD = 1;
	private final Logger logger = Logger.getLogger(BankReconService.class);

	/**
	 * Get the list of Bank Reconciliation Items.
	 * @param bankAcctId The unique id of the bank account.
	 * @param asOfDate End date of the date range.
	 * @return The list of Reconciling Items.
	 */
	public List<BankReconItem> getBrItems(int bankAcctId, Date asOfDate, Integer divisionId) {
		logger.info("Retrieving the list of reconciling items.");
		List<BankReconItem> data = brDao.getBankReconItems(bankAcctId, asOfDate, divisionId);
		logger.debug("Successfully retrieved "+data.size()+" reconciling items.");
		return data;
	}

	/**
	 * Generate the data source for the report.
	 * @param bankAcctId The id of the bank account selected.
	 * @param asOfDate The end date of the date range.
	 * @return The generated Bank Reconciliation data.
	 */
	public List<BankRecon> generateReport(int bankAcctId, Date asOfDate, Integer divisionId) {
		logger.info("Generating the report.");
		logger.debug("For bank account: "+bankAcctId+" as of: "+asOfDate);
		List<BankReconItem> ditReconItems = new ArrayList<BankReconItem>();
		List<BankReconItem> ocReconItems = new ArrayList<BankReconItem>();
		double totalDITAmt = 0;
		double totalOCAmt = 0;

		List<BankReconItem> brItems = getBrItems(bankAcctId, asOfDate, divisionId);
		if (!brItems.isEmpty()) {
			for (BankReconItem bankReconItem : brItems) {
				if (bankReconItem.getCheckNo() == null) {
					logger.trace("Setting the value of check no to N/A.");
					bankReconItem.setCheckNo("N/A");
				}
				if (bankReconItem.getBrField().equals(DIT_FIELD)) {
					totalDITAmt += bankReconItem.getAmount();
					ditReconItems.add(bankReconItem);
				} else {
					totalOCAmt += bankReconItem.getAmount();
					ocReconItems.add(bankReconItem);
				}
			}
		}

		List<BankRecon> bankReconRpt = new ArrayList<BankRecon>();
		logger.trace(ditReconItems.size()+" reconciling items added to Deposits in Transit");
		logger.trace(ocReconItems.size()+" reconciling items added to Outstanding Checks");
		bankReconRpt.add(BankRecon.getInstanceOf(ditReconItems, ocReconItems, totalDITAmt, totalOCAmt));
		return bankReconRpt;
	}

	/**
	 * Get PCF Amount
	 * @param companyId The company id
	 * @param userCustodianId The user custodian id
	 * @param asOfDate The as of date
	 * @return The PCF Amount
	 */
	public Double getBookBalance(Integer companyId, Integer bankAcctAcId, Date asOfDate) {
		AccountCombination ac = acctCombiService.getAccountCombination(bankAcctAcId);
		AccountAnalysisReport bookBalance = accountDao.getAccountBalance(companyId,
				ac.getAccount(), ac.getDivision().getNumber(), ac.getDivision().getNumber(),
				asOfDate, null);
		if (bookBalance != null) {
			return bookBalance.getBalance();
		}
		return 0.0;
	}


	/**
	 * Get the list of bank reconciliation summary report.
	 * @param bankAcctIdAndBalances The String that contains list of bank account ids and bank balances.
	 * @param asOfDate The as of date.
	 * @param bankDate The bank date.
	 * @return The list of bank reconciliation summary report dto.
	 */
	public List<BankReconSummaryDto> genBankReconSumReport(String bankAcctIdAndBalances, Date asOfDate,
			Date bankDate) {
		String bankAcctIdAndBals[] = bankAcctIdAndBalances.split(";");
		String strBankAcctIdAndBal[] = null;
		int bankAcctId = 0;
		double bankBalance = 0;
		List<BankRecon> bankRecons = null;
		List<BankReconSummaryDto> reconSummaryDtos = new ArrayList<BankReconSummaryDto>();
		BankReconSummaryDto bankReconSummaryDto = null;
		for (String bankAcctIdAndBal : bankAcctIdAndBals) {
			bankRecons = new ArrayList<BankRecon>();
			bankReconSummaryDto = new BankReconSummaryDto();
			strBankAcctIdAndBal = bankAcctIdAndBal.split(",");
			bankAcctId = Integer.parseInt(strBankAcctIdAndBal[0]);
			bankBalance = Double.parseDouble(strBankAcctIdAndBal[1]);
			bankRecons.addAll(generateReport(bankAcctId, asOfDate, -1));
			BankAccount bankAcct = baService.getBankAcct(bankAcctId);
			AccountCombination acctCombi = acctCombiService.getAccountCombination(bankAcct.getCashInBankAcctId());
			Double bookBal = getAcctBalance(bankAcctId, bankAcct.getCompanyId(), 
					acctCombi.getAccountId(), asOfDate);
			//computations
			if(!bankRecons.isEmpty()) {
				double totalDITAmount = bankRecons.get(0).getTotalDITAmount();
				double totalOCAmount = bankRecons.get(0).getTotalOCAmount();
				double adjustedBalance = bankBalance+totalDITAmount-totalOCAmount;
				bankReconSummaryDto.setAdjustedBalance(adjustedBalance);
				bankReconSummaryDto.setVariance(bookBal-adjustedBalance);
			}
			bankReconSummaryDto.setBankRecons(bankRecons);
			bankReconSummaryDto.setBookBalance(bookBal);
			bankReconSummaryDto.setBankBalance(bankBalance);
			bankReconSummaryDto.setBankAcctName(bankAcct.getName());
			//Company details
			Company company = companyService.getCompany(bankAcct.getCompanyId());
			bankReconSummaryDto.setCompanyName(company.getName());
			reconSummaryDtos.add(bankReconSummaryDto);
		}
		return reconSummaryDtos;
	}

	/**
	 * Get the Book Balance.
	 */
	private Double getAcctBalance(int bankAcctId, int companyId, int accountId, Date asOfDate) {
		AccountBalancesDto acctBal = acctBalService.getAcctBalance(bankAcctId, companyId, accountId, asOfDate);
		if(acctBal == null)
			return 0.0;
		logger.trace("Computed Book Balance: "+acctBal.getBalance());
		return acctBal.getBalance();
	}
}
