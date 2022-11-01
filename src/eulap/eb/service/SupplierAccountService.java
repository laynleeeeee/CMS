package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.CustodianAccountDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.domain.hibernate.CustodianAccountSupplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.SupplierAccountSummaryDto;
import eulap.eb.web.dto.SupplierAcctHistDto;

/**
 * A class that handles the business logic of supplier's account. 

 *
 */
@Service
public class SupplierAccountService {
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	@Autowired
	private AccountCombinationDao accountCombinationDao;
	@Autowired
	private CustodianAccountSupplierService casService;
	@Autowired
	private CustodianAccountDao custodianAccountDao;

	private static Logger LOGGER = Logger.getLogger(SupplierAccountService.class);

	/**
	 * Get the supplier's account. 
	 * @param supplierAccountId The unique id of the supplier's account. 
	 * @return The Supplier's account. 
	 */
	public SupplierAccount getSupplierAccount (int supplierAccountId) {
		SupplierAccount supplierAcct = getSupplierAcct(supplierAccountId);
		if(supplierAcct == null)
			return null;
		if(supplierAcct.getDefaultDebitACId() != null){
			//Set the default debit account for division and account.
			AccountCombination debitAC =
					accountCombinationDao.get(supplierAcct.getDefaultDebitACId());
			supplierAcct.setDebitAccountId(debitAC.getAccountId());
			supplierAcct.setDebitDivisionId(debitAC.getDivisionId());
		}
		//Set the default credit account for division and account.
		AccountCombination creditAC =
				accountCombinationDao.get(supplierAcct.getDefaultCreditACId());
		supplierAcct.setCreditAccountId(creditAC.getAccountId());
		supplierAcct.setCreditDivisionId(creditAC.getDivisionId());
		return supplierAcct;
	}

	/**
	 * Evaluates if the supplier's account is unique. 
	 * @param supplierAccount The supplier's account
	 * @return True if the account is unique otherwise false.
	 */
	public boolean isUnique (SupplierAccount supplierAccount) {
		return supplierAccountDao.isUniqueSupplierAccount(supplierAccount);
	}

	/**
	 * Save the supplier's account
	 * @param supplierAccount The account that will be saved.
	 * @param user The current logged user.
	 */
	public void saveSupplierAccount (SupplierAccount supplierAccount, User user) {
		// Setting the default credit Account combination.
		AccountCombination creditAc =  accountCombinationDao.getAccountCombination(supplierAccount.getCompanyId(),
								supplierAccount.getCreditDivisionId(), supplierAccount.getCreditAccountId());
		supplierAccount.setDefaultCreditACId(creditAc.getId());
		if (supplierAccount.getDebitDivisionId() != 0 && supplierAccount.getDebitAccountId() != 0) {
			AccountCombination debitAc =  accountCombinationDao.getAccountCombination(supplierAccount.getCompanyId(),
					supplierAccount.getDebitDivisionId(), supplierAccount.getDebitAccountId());
			supplierAccount.setDefaultDebitACId(debitAc.getId());
		} else {
			supplierAccount.setDefaultDebitACId(null);
		}
		supplierAccount.setName(StringFormatUtil.removeExtraWhiteSpaces(supplierAccount.getName().trim(), null));
		AuditUtil.addAudit(supplierAccount, user, new Date());
		supplierAccountDao.saveOrUpdate(supplierAccount);

		if (supplierAccount.getId() != 0) {
			CustodianAccountSupplier cas = casService.getCAS(null, null, supplierAccount.getId());
			if (cas != null) {
				CustodianAccount ca = custodianAccountDao.get(cas.getCustodianAccountId());
				ca.setCustodianAccountName(StringFormatUtil.removeExtraWhiteSpaces(supplierAccount.getName()));
				ca.setCdAccountCombinationId(creditAc.getId());
				custodianAccountDao.update(ca);
			}
		}
	}

	public List<SupplierAccount> getSupplierAccnts (int supplierId, User user) {
		return supplierAccountDao.getSupplierAccounts(supplierId, user);
	}

	/**
	 * Get the list of supplier's account.
	 * @param supplierId The supplier id.
	 * @param divisionId The division id.
	 * @return The list of supplier's account.
	 */
	public List<SupplierAccount> getSupplierAccnts (int supplierId, User user, Integer divisionId, Integer supplierAccountId) {
		List<SupplierAccount> supplierAccounts = supplierAccountDao.getSupplierAccounts(supplierId, user, divisionId);
		return appendInactiveSupplierAcct(supplierAccounts, supplierAccountId);
	}

	public List<SupplierAccount> getSupplierAccounts(int supplierId, Integer supplierAccountId) {
		List<SupplierAccount> suppAccounts = getSupplierAccnts(supplierId, null);
		if(supplierAccountId != null) {
			boolean hasSelectedSupplierAcct = false;
			for(SupplierAccount sa : suppAccounts) {
				if (sa.getId() == supplierAccountId) {
					hasSelectedSupplierAcct = true;
					break;
				}
			}
			if(!hasSelectedSupplierAcct)
				suppAccounts.add(supplierAccountDao.get(supplierAccountId));
		}
		return suppAccounts;
	}

	/**
	 * Get the list of supplier accounts filtered by the company and service lease key.
	 * @return The list of supplier accounts.
	 */
	public List<SupplierAccount> getSupplierAccts(int supplierId, int companyId) {
		return getSupplierAccts(supplierId, companyId, null, true);
	}

	/**
	 * Get the list of supplier accounts filtered by the company and service lease key.
	 * @return The list of supplier accounts.
	 */
	public List<SupplierAccount> getSupplierAccts(int supplierId, int companyId, Integer divisionId, boolean activeOnly) {
		return getSupplierAccts(supplierId,null, companyId, divisionId, activeOnly);
	}

	/**
	 * Get the list of supplier accounts filtered by the company and service lease key.
	 * @return The list of supplier accounts.
	 */
	public List<SupplierAccount> getSupplierAccts(int supplierId,Integer supplierAccountId, int companyId, Integer divisionId, boolean activeOnly) {
		List<SupplierAccount> supplierAccounts = supplierAccountDao.getSupplierAccounts(supplierId, companyId, divisionId, activeOnly);
		return appendInactiveSupplierAcct(supplierAccounts, supplierAccountId);
	}

	private List<SupplierAccount> appendInactiveSupplierAcct(List<SupplierAccount> supplierAccounts, Integer supplierAccountId) {
		if(supplierAccountId != null) {
			//Append inactive supplier account
			Collection<Integer> supAcctIds = new ArrayList<Integer>();
			for(SupplierAccount supAcct : supplierAccounts) {
				supAcctIds.add(supAcct.getId());
			}
			if(!supAcctIds.contains(supplierAccountId)) {
				supplierAccounts.add(supplierAccountDao.get(supplierAccountId));
			}
		}
		return supplierAccounts;
	}

	/**
	 * Search for supplier accounts.
	 * @param suppierName The supplier name.
	 * @param supplierAcctNamr The supplier account name.
	 * @param companyId The company id.
	 * @param termId The term id.
	 * @param status The status of the supplier account.
	 * @param pageNumber The page number.
	 * @return The page result.
	 */
	public Page<SupplierAccount> searchSupplierAccounts(String supplierName, String SupplierAcctName, Integer companyId, 
			Integer termId, String status,Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return supplierAccountDao.searchSupplierAccounts(supplierName, SupplierAcctName, companyId, termId, searchStatus, new PageSetting(pageNumber, 25));
	}

	/**
	 * Get the Supplier Account object.
	 * @param supplierAccountId The unique id of the supplier account.
	 * @return The supplier account otherwise, null.
	 */
	public SupplierAccount getSupplierAcct (int supplierAccountId) {
		return supplierAccountDao.get(supplierAccountId);
	}

	/**
	 * Generate the Supplier Account History Report.
	 * @param supplierAcctId The unique id of the supplier account.
	 * @param strAsOfDate as of date of the date range.
	 * @param divisionId the unique id of the division.
	 * @param currencyId the unique id of the currency.
	 * @return List of generated data to be used in the report.
	 */
	public List<SupplierAcctHistDto> generateReport(int supplierAcctId, String strAsOfDate, int divisionId, int currencyId) {
		LOGGER.info("Generating the Supplier Account History Report");
		LOGGER.debug("Selected supplier account id: "+supplierAcctId+" and as of date : "+strAsOfDate );
		List<SupplierAcctHistDto> reportDatas = new ArrayList<SupplierAcctHistDto>();
		Date asOfDate = DateUtil.parseDate(strAsOfDate);
		int currentPage = 1;
		Double runningBalance = 0.0;
		Page<SupplierAcctHistDto> supplierAcctData = supplierAccountDao.getSupplierAcctHistoryData(supplierAcctId,
				asOfDate, divisionId, currencyId, new PageSetting(currentPage));
		while(currentPage <= supplierAcctData.getLastPage()) {
			LOGGER.debug("Retrieving the list of data on page: "+currentPage);
			if (currentPage > 1) { //If current page is greater than 1, get the data for next page.
				supplierAcctData = supplierAccountDao.getSupplierAcctHistoryData(
						supplierAcctId, asOfDate, divisionId, currencyId, new PageSetting(currentPage));
			}
			double invoiceAmonut = 0.00;
			for (SupplierAcctHistDto historyDto : supplierAcctData.getData()) {
				invoiceAmonut = historyDto.getInvoiceAmount() != null ? historyDto.getInvoiceAmount() : 0.00;
				runningBalance += (invoiceAmonut - historyDto.getPaymentAmount());
				if (currencyId == 1) {
					if (invoiceAmonut > historyDto.getPaymentAmount()) {
						runningBalance += historyDto.getGainLoss();
					} else {
						runningBalance -= historyDto.getGainLoss();
					}
				}
				//Sets gain loss based on selected currency
				if (currencyId != 1 ) {
					historyDto.setGainLoss(0.0);
				}
				LOGGER.debug("Running Balance: "+runningBalance);
				historyDto.setBalance(NumberFormatUtil.roundOffTo2DecPlaces(runningBalance));
				reportDatas.add(historyDto);
			}
			currentPage++;
			LOGGER.debug("Retrieved "+supplierAcctData.getDataSize()+" records.");
		}
		LOGGER.info("Retrieved a total of "+reportDatas.size()+" records for the report.");
		return reportDatas;
	}

	/**
	 * Get the supplier account by name and company.
	 * @param companyId The company id.
	 * @param supplierAcctName The supplier account name.
	 * @return {@link SupplierAccount}
	*/
	public SupplierAccount getSupplierAcctByNameAndCompany(Integer companyId, String supplierAcctName) {
		return supplierAccountDao.getSupplierAcctByNameAndCompany(companyId, supplierAcctName);
	}
	/**
	 * Generate the supplier account summary header.
	 * @param supplierAcctId the unique id of the supplier account.
	 * @param asOfDate As of date of the date range.
	 * @param divisionId the unique id of the division.
	 * @param currencyId the unique id of the currency.
	 * @return Computed total amounts for summary header.
	 */
	public SupplierAccountSummaryDto getSummaryHeader(int supplierAcctId, Date asOfDate, int divisionId, int currencyId) {
		SupplierAccountSummaryDto summaryDto = new SupplierAccountSummaryDto();
		double totalInvoices = 0;
		double totalPayments = 0;
		double totalAdvances = 0;
		double totalGainLoss = 0;
		double balance = 0;
		int currentPage = PageSetting.START_PAGE;
		Page<SupplierAcctHistDto> supplierAcctData = supplierAccountDao.getSupplierAcctHistoryData(supplierAcctId,
				asOfDate, divisionId, currencyId, new PageSetting(currentPage));
		while(currentPage <= supplierAcctData.getLastPage()) {
			LOGGER.debug("Retrieving the list of data on page: "+currentPage);
			if (currentPage > 1) { //If current page is greater than 1, get the data for next page.
				supplierAcctData = supplierAccountDao.getSupplierAcctHistoryData(supplierAcctId,
						asOfDate, divisionId, currencyId, new PageSetting(currentPage));
			}
			// Retreive amounts from SupplierAcctHistDto then store them to SupplierAccountSummaryDto
			for (SupplierAcctHistDto historyDto : supplierAcctData.getData()) {
				totalInvoices += NumberFormatUtil.roundOffTo2DecPlaces((historyDto.getInvoiceAmount() != null ? historyDto.getInvoiceAmount() : 0));
				totalAdvances += NumberFormatUtil.roundOffTo2DecPlaces((historyDto.getSapAmount() != null ? historyDto.getSapAmount() : 0));
				totalPayments += NumberFormatUtil.roundOffTo2DecPlaces(historyDto.getPaymentAmount());
				totalGainLoss += NumberFormatUtil.roundOffTo2DecPlaces(historyDto.getGainLoss());
			}
			currentPage++;
			LOGGER.debug("Retrieved "+supplierAcctData.getDataSize()+" records.");
		}
		LOGGER.debug("Gain/Loss Amount: "+totalGainLoss);
		LOGGER.debug("Outstanding Balance: "+balance);
		if (currencyId == Currency.DEFUALT_PHP_ID) {
			balance = ((totalInvoices - totalPayments) + Math.abs(totalGainLoss));
		} else {
			balance = (totalInvoices - totalPayments);
		}
		summaryDto.setSapAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalAdvances));
		summaryDto.setInvoiceAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalInvoices));
		summaryDto.setPaymentAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalPayments));
		summaryDto.setGainLoss(NumberFormatUtil.roundOffTo2DecPlaces(totalGainLoss));
		summaryDto.setOutstandingBalance(NumberFormatUtil.roundOffTo2DecPlaces(balance));
		return summaryDto;
	}
}