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
 * Generates the Liabilities report for Balance Sheet.

 */
@Service
public class BSLiabilitiesGenerator implements BalanceSheetReporter{
	private static String LIABILITIES = "Liabilities";
	@Autowired
	private AccountTypeDao accountTypeDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private GlEntryDao glEntryDao;
	@Autowired
	private ApPaymentInvoiceDao paymentInvoiceDao;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private ApPaymentDao apPaymentDao;
	@Autowired
	private APLineDao apLineDao;

	@Override
	public List<BalanceSheetRowData> generateReport(User user, int companyId, Date asOfDate) {
		List<BalanceSheetRowData> balSheetRowData = new ArrayList<BalanceSheetRowData>();
		//Constant title
		BalanceSheetRowData liabilitiesTitle = new BalanceSheetRowData();
		liabilitiesTitle.setTitle(LIABILITIES);
		balSheetRowData.add(liabilitiesTitle);
		List<AccountType> liabilityAcctTypes = new ArrayList<AccountType>();
		//Real - Current Liabilities & ACCOUNT TYPE IS NOT a CONTRA ACCOUNT
		List<AccountType> realCurrentLiabilities =
				accountTypeDao.getAccountTypes(AccountClass.R_CURRENT_LIABILITIES, false, user.getServiceLeaseKeyId(), companyId);
		for (AccountType accountType : realCurrentLiabilities) {
			liabilityAcctTypes.add(accountType);
		}
		//Real - Non-current Liabilities & ACCOUNT TYPE IS NOT a CONTRA ACCOUNT
		List<AccountType> realNonCurrentLiabilities =
				accountTypeDao.getAccountTypes(AccountClass.R_NON_CURRENT_LIABILITIES, false, user.getServiceLeaseKeyId(), companyId);
		for (AccountType accountType : realNonCurrentLiabilities) {
			liabilityAcctTypes.add(accountType);
		}
		double totalLiabilities = 0;
		for (AccountType accountType : liabilityAcctTypes) {
			//If Account class = Account Type, no need for additional labelling
			if(!accountType.getName().equalsIgnoreCase(LIABILITIES)) {
				BalanceSheetRowData accountTypeBS = new BalanceSheetRowData();
				accountTypeBS.setTitle(accountType.getName());
				balSheetRowData.add(accountTypeBS);
			}
			// Get all accounts  under this account type.
			double totalPerAccountType = 0;
			List<Account> accounts = accountDao.getAccounts(companyId, accountType.getId(), user.getServiceLeaseKeyId());
			for (Account account : accounts) {
				BalanceSheetRowData accountBS = new BalanceSheetRowData();
				double totalPerAccount = 0;

				//Check if there is a contra account
				boolean hasContraAcct = false;
				Account contraAcct = accountDao.getContraAcctByRelatedAcct(account.getId());
				if(contraAcct != null) {
					double totalRelAcct = (getTotalCredit(companyId, account.getId(), asOfDate)
							- getTotalDebit(companyId, account.getId(), asOfDate));
					if(totalRelAcct != 0.0) {
						accountBS.setSubTitle(account.getAccountName());
						accountBS.setAmountColumn1(totalRelAcct);
						balSheetRowData.add(accountBS);
					}
					BalanceSheetRowData contraAcctBS = new BalanceSheetRowData();
					double totalContraAcct = (getTotalDebit(companyId, account.getId(), asOfDate)
							- getTotalCredit(companyId, account.getId(), asOfDate));
					if(totalContraAcct != 0.0) {
						contraAcctBS.setaC1Underlined(true);
						contraAcctBS.setAmountColumn1(-totalContraAcct);
						contraAcctBS.setSubTitle(contraAcct.getAccountName());
						// Deduct CONTRA-ACCOUNT to it's RELATED ACCOUNT if included above
						totalPerAccount = totalRelAcct - totalContraAcct;
						contraAcctBS.setAmountColumn2(totalPerAccount);
						balSheetRowData.add(contraAcctBS);
						hasContraAcct = true;
					}
				} else {
					totalPerAccount = (getTotalCredit(companyId, account.getId(), asOfDate)
							- getTotalDebit(companyId, account.getId(), asOfDate));
					//Do not include those with zero amounts only
					if(totalPerAccount == 0.0)
						continue;
					accountBS.setSubTitle(account.getAccountName());
					//Set amount to column 2 if the size of accounts is more than 1
					if(accounts.size() > 1)
						accountBS.setAmountColumn2(totalPerAccount);
				}
				totalPerAccountType += totalPerAccount;
				if (accounts.lastIndexOf(account) == (accounts.size() - 1)){
					accountBS.setAmountColumn3(totalPerAccountType);
					//Will not underline column 2 if there is only 1 account retrieved
					if(accounts.size() > 1)
						accountBS.setaC2Underlined(true);
					//Last Account of the last Account type should be underlined
					if(liabilityAcctTypes.lastIndexOf(accountType) == (liabilityAcctTypes.size() - 1))
						accountBS.setaC3Underlined(true);
				}
				//If there is a contra account, it will not save the accountBS since it was already saved before the contra account
				if(!hasContraAcct)
					balSheetRowData.add(accountBS);
			}
			totalLiabilities += totalPerAccountType;
			balSheetRowData.add(BalanceSheetRowData.EMPTY);
		}
		//Total Amount for Liabilities
		BalanceSheetRowData totalAmountLiabilities = new BalanceSheetRowData();
		totalAmountLiabilities.setTitle("Total Liabilities");
		totalAmountLiabilities.setAmountColumn3(totalLiabilities);
		totalAmountLiabilities.setaC3Underlined(true);
		balSheetRowData.add(totalAmountLiabilities);
		balSheetRowData.add(BalanceSheetRowData.EMPTY);
		return balSheetRowData;
	}

	/**
	 * Compute the total amount per account
	 * <br>Amounts are from GL Entry, AP Line and AP Payment Invoice table.
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
		totalDebit += paymentInvoiceDao.getTotalCredit(companyId, asOfDate, accountId);
		totalDebit += paymentInvoiceDao.getTotalDebit(companyId, asOfDate, accountId);
		return totalDebit;
	}

	/**
	 * Compute the total amount per account
	 * <br>Amounts are from GL Entry,  AP Invoice and AP Payment table.
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
