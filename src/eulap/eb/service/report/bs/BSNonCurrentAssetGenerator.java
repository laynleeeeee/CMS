package eulap.eb.service.report.bs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.APLineDao;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.AccountTypeDao;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.ApPaymentInvoiceDao;
import eulap.eb.dao.GlEntryDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountClass;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.User;
/**
* Generates the non-current asset report for balance sheet.

*/
@Service
public class BSNonCurrentAssetGenerator implements BalanceSheetReporter{
	private static String NON_CURRENT_ASSETS = "Non-Current Assets";
	private static String TOTAL_NON_CURRENT_ASSET = "Total Non-Current Assets";
	@Autowired
	private AccountTypeDao accountTypeDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private GlEntryDao glEntryDao;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private ApPaymentDao apPaymentDao;
	@Autowired
	private APLineDao apLineDao;
	@Autowired
	private ApPaymentInvoiceDao apPaymentInvoiceDao;

	@Override
	public List<BalanceSheetRowData> generateReport(User user, int companyId, Date asOfDate) {
		List<BalanceSheetRowData> result = new ArrayList<BalanceSheetRowData>();
		BalanceSheetRowData nonCurrentAssetTitle = new BalanceSheetRowData();
		nonCurrentAssetTitle.setTitle(NON_CURRENT_ASSETS);
		result.add(nonCurrentAssetTitle);

		// ACCOUNT TYPE WHERE ACCOUNT CLASS = Real - Non-Current Assets IS NOT a CONTRA ACCOUNT
		List<AccountType> realNonCurrentAssets =
				accountTypeDao.getAccountTypes(AccountClass.R_NON_CURRENT_ASSETS, false, user.getServiceLeaseKeyId(), companyId);
		double totalNonCurrentAsset = 0;
		for(AccountType accountType : realNonCurrentAssets) {
			BalanceSheetRowData accountTypeRowData = new BalanceSheetRowData();
			accountTypeRowData.setTitle(accountType.getName());
			result.add(accountTypeRowData);

			// Get all accounts  under this account type.
			double totalPerAccountType = 0;
			List<Account> accounts = accountDao.getAccounts(companyId, accountType.getId(), user.getServiceLeaseKeyId());
			for (Account account : accounts) {
				boolean isSingleAccount = accounts.size() == 1 ? true : false;
				BalanceSheetRowData currentBS = new BalanceSheetRowData();
				BalanceSheetRowData accountBS = new BalanceSheetRowData();
				currentBS = accountBS;
				accountBS.setSubTitle(account.getAccountName());
				Account contraAccount = accountDao.getContraAcctByRelatedAcct(account.getId());
				if (contraAccount == null) { // Normal account
					// Get the total amount of an account
					double totalPerAccount = getTotalDebit(companyId, account.getId(), asOfDate)
							- getTotalCredit(companyId, account.getId(), asOfDate);
					if (isSingleAccount){
						accountBS.setAmountColumn3(totalPerAccount);
						accountBS.setaC3Underlined(true);
					} else 
						accountBS.setAmountColumn2(totalPerAccount);
					totalPerAccountType += totalPerAccount;
					result.add(accountBS);

				} else {
					// Contra account
					double totalRelAccount = getTotalDebit(companyId, account.getId(), asOfDate)
							- getTotalCredit(companyId, account.getId(), asOfDate);
					accountBS.setAmountColumn1(totalRelAccount);
					totalPerAccountType += totalRelAccount;
					result.add(accountBS);
					BalanceSheetRowData contraAccountBS = new BalanceSheetRowData();
					contraAccountBS.setSubTitle(contraAccount.getAccountName());
					double contraAccountAmount = - (getTotalCredit(companyId, contraAccount.getId(), asOfDate)
							- getTotalDebit(companyId, contraAccount.getId(), asOfDate));
					contraAccountBS.setAmountColumn1(contraAccountAmount);
					contraAccountBS.setaC1Underlined(true);
					contraAccountBS.setAmountColumn2(contraAccountAmount + totalRelAccount);
					contraAccountBS.setaC2Underlined(true);
					result.add(contraAccountBS);
					totalPerAccountType += contraAccountAmount;
					currentBS = contraAccountBS;
				}
				if (accounts.get(accounts.size() -1) == account) { // Last account, should include the total per account type.
					currentBS.setaC2Underlined(true);
					currentBS.setAmountColumn3(totalPerAccountType);
				}
			}
			totalNonCurrentAsset +=totalPerAccountType;
			result.add(BalanceSheetRowData.EMPTY);
		}
		//Total Non-Current assets
		BalanceSheetRowData totalNonCurrentAssetBS = new BalanceSheetRowData();
		totalNonCurrentAssetBS.setTitle(TOTAL_NON_CURRENT_ASSET);
		totalNonCurrentAssetBS.setAmountColumn3(totalNonCurrentAsset);
		totalNonCurrentAssetBS.setaC3Underlined(true);
		result.add(BalanceSheetRowData.EMPTY);
		result.add(totalNonCurrentAssetBS);
		return result;
	}

	/**
	 * Compute the total amount per account
	 * <br>Amounts are from GL Entry, AP Line and AP Payment Invoice table
	 * @param companyId The Id of the company selected.
	 * @param accountId The Id of the account.
	 * @param asOfDate The end date of the date range.
	 * @return The total amount per account.
	 */
	private double getTotalDebit(int companyId, int accountId, Date asOfDate) {
		//Get total debit from GL Entry
		double totalDebit = glEntryDao.getTotalDebit(companyId, asOfDate, accountId);

		//Get total from AP Lines
		totalDebit += apLineDao.getTotalDebit(companyId, asOfDate, accountId);
		totalDebit += apLineDao.getTotalCredit(companyId, asOfDate, accountId);

		//Get total from AP Payment Invoice
		totalDebit += apPaymentInvoiceDao.getTotalCredit(companyId, asOfDate, accountId);
		totalDebit += apPaymentInvoiceDao.getTotalDebit(companyId, asOfDate, accountId);
		return totalDebit;
	}

	/**
	 * Compute the total amount per account
	 * <br>Amounts are from GL Entry, AP Invoice and AP Payment table
	 * @param companyId The Id of the company selected.
	 * @param accountId The Id of the account.
	 * @param asOfDate The end date of the date range.
	 * @return The total amount per account.
	 */
	private double getTotalCredit(int companyId, int accountId, Date asOfDate) {
		//Get total credit from GL Entry
		double totalCredit = glEntryDao.getTotalCredit(companyId, asOfDate, accountId);

		//Get the total credit from AP Invoice
		totalCredit += apInvoiceDao.getTotalDebit(companyId, asOfDate, accountId);
		totalCredit += apInvoiceDao.getTotalCredit(companyId, asOfDate, accountId);

		//Get the total credit from AP Payment
		totalCredit += apPaymentDao.totalPerAccount(companyId, asOfDate, accountId);
		return totalCredit;
	}
}
