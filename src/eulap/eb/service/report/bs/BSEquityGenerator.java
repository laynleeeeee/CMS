package eulap.eb.service.report.bs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import eulap.eb.dao.AccountDao;
import eulap.eb.dao.AccountTypeDao;
import eulap.eb.dao.GlEntryDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountClass;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.User;

/**
 * Generates the equity report for balance sheet

 */
@Service
public class BSEquityGenerator implements BalanceSheetReporter{
	private static String EQUITY = "Equity";
	@Autowired
	private AccountTypeDao accountTypeDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private GlEntryDao glEntryDao;
	
	@Override
	public List<BalanceSheetRowData> generateReport(User user, int companyId,
			Date asOfDate) {
		List<BalanceSheetRowData> balSheetRowData = new ArrayList<BalanceSheetRowData>();
		// Constant Title
		BalanceSheetRowData equityTitle = new BalanceSheetRowData();
		equityTitle.setTitle(EQUITY);
		balSheetRowData.add(equityTitle);
		List<AccountType> equityAcctType = new ArrayList<AccountType>();
		// Real - Equity & ACCOUNT TYPE is not a CONTRA ACCOUNT
		List<AccountType> realEquities = 
			accountTypeDao.getAccountTypes(AccountClass.R_EQUITY, false, user.getServiceLeaseKeyId(), companyId);
		for (AccountType accountType : realEquities) {
			equityAcctType.add(accountType);
		}
		double totalEquity = 0;
		// Get related accounts of this Account type
		for (AccountType accountType : equityAcctType) {
			// If Account class = Account Type ( No more need for additional labeling )
			if (!accountType.getName().equalsIgnoreCase(EQUITY)){
				BalanceSheetRowData accountTypeBS = new BalanceSheetRowData();
				accountTypeBS.setTitle(accountType.getName());
				balSheetRowData.add(accountTypeBS);
			}
			// The Accounts under this account type
			double totalPerAccountType = 0;
			List<Account> accounts = accountDao.getAccounts(companyId, accountType.getId(), user.getServiceLeaseKeyId());
			for (Account account: accounts){
				BalanceSheetRowData accountBS = new BalanceSheetRowData();
				// Get Debit and Credit amounts of account 
				double debAcctAmount = glEntryDao.getTotalDebit(companyId, asOfDate, account.getId());
				double credAcctAmount = glEntryDao.getTotalCredit(companyId, asOfDate, account.getId());
				// Check for Contra account
				Account contraAcct = accountDao.getContraAcctByRelatedAcct(account.getId());
				// No Contra Account
				if (contraAcct == null){
					// If not the last record
					if (accounts.get(accounts.size() - 1) != account){
						// Add to Total amount the computed value of (credit - debit)
						totalPerAccountType += (credAcctAmount - debAcctAmount);
						accountBS.setSubTitle(account.getAccountName());
						accountBS.setAmountColumn2(credAcctAmount - debAcctAmount);
						balSheetRowData.add(accountBS);
					} else { // The Last record
						// Add to Total amount the computed value of (credit - debit)
						totalPerAccountType += (credAcctAmount - debAcctAmount);
						accountBS.setSubTitle(account.getAccountName());
						accountBS.setAmountColumn2(credAcctAmount - debAcctAmount);
						accountBS.setaC2Underlined(true);
						balSheetRowData.add(accountBS);
					}
				} else { // There is CONTRA account
					double contraAcctamount = glEntryDao.totalPerAccount(companyId, contraAcct.getId(), asOfDate);
					BalanceSheetRowData contraAcctBS = new BalanceSheetRowData();
					// If not the last record
					if (accounts.get(accounts.size() - 1) != account){
						// Add to Total amount the computed value of (credit - debit)
						totalPerAccountType += (credAcctAmount - debAcctAmount);
						accountBS.setSubTitle(account.getAccountName());
						accountBS.setAmountColumn2(credAcctAmount - debAcctAmount);
						balSheetRowData.add(accountBS);
						
						// Deduct Contra Account Amount to total amount
						totalPerAccountType -= contraAcctamount;
						contraAcctBS.setAmountColumn1(contraAcctamount * -1);
						contraAcctBS.setaC1Underlined(true);
						contraAcctBS.setAmountColumn2((credAcctAmount - debAcctAmount) - contraAcctamount);
						balSheetRowData.add(contraAcctBS);
						
					} else { // Last record
						// Add to Total amount the computed value of (credit - debit)
						totalPerAccountType += (credAcctAmount - debAcctAmount);
						accountBS.setSubTitle(account.getAccountName());
						accountBS.setAmountColumn2(credAcctAmount - debAcctAmount);
						balSheetRowData.add(accountBS);
						
						// Deduct Contra Account Amount to total amount
						totalPerAccountType -= contraAcctamount;
						contraAcctBS.setAmountColumn1(contraAcctamount * -1);
						contraAcctBS.setaC1Underlined(true);
						contraAcctBS.setAmountColumn2((credAcctAmount - debAcctAmount) - contraAcctamount);
						contraAcctBS.setaC2Underlined(true);
						balSheetRowData.add(contraAcctBS);
					}
				}
			}
			// Add space after last entry of the account
			totalEquity += totalPerAccountType;
			balSheetRowData.add(BalanceSheetRowData.EMPTY);
		}
		// Total Amount for Equity
		BalanceSheetRowData totalAmountEquity = new BalanceSheetRowData();
		totalAmountEquity.setTitle("Total Equity");
		totalAmountEquity.setAmountColumn3(totalEquity);
		totalAmountEquity.setaC3Underlined(true);
		balSheetRowData.add(totalAmountEquity);
		balSheetRowData.add(BalanceSheetRowData.EMPTY);

		return balSheetRowData;
	}
}
