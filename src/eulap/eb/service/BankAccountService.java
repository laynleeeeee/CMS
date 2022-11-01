package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.BankAccountDao;
import eulap.eb.dao.BankDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Bank;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.User;
/**
 * A class that handles the business logic of Bank Account

 */
@Service
public class BankAccountService {
	@Autowired
	private BankAccountDao bankAccountDao;
	@Autowired
	private AccountCombinationDao accountCombinationDao;
	@Autowired
	private BankDao bankDao;

	/**
	 * Get the bank accounts.
	 * @param bankAccountId The unique id of bank account.
	 * @return The bank accounts.
	 */
	public BankAccount getBankAccount(int bankAccountId) {
		BankAccount bankAccount =  bankAccountDao.get(bankAccountId);
		if (bankAccount == null)
			return null;
		//Set the cash in bank default division and account.
		AccountCombination cashInBankAC =
				accountCombinationDao.get(bankAccount.getCashInBankAcctId());
		bankAccount.setInBAAccountId(cashInBankAC.getAccountId());
		bankAccount.setInBADivisionId(cashInBankAC.getDivisionId());
		//Set the cash receipt default division and account.
		AccountCombination cashReceiptAC =
				accountCombinationDao.get(bankAccount.getCashReceiptClearingAcctId());
		bankAccount.setcRCAAccountId(cashReceiptAC.getAccountId());
		bankAccount.setcRCADivisionId(cashReceiptAC.getDivisionId());
		//Set the cash payment default division and account.
		AccountCombination cashPaymentAC =
				accountCombinationDao.get(bankAccount.getCashPaymentClearingAcctId());
		bankAccount.setcPCAAccountId(cashPaymentAC.getAccountId());
		bankAccount.setcPCADivisionId(cashPaymentAC.getDivisionId());
		//Set the cash receipt post dated check default division and account.
		if (bankAccount.getCashReceiptsPdcAcctId() != null) {
			AccountCombination cashReceiptPdcAC =
					accountCombinationDao.get(bankAccount.getCashReceiptsPdcAcctId());
			bankAccount.setcRPdcAccountId(cashReceiptPdcAC.getAccountId());
			bankAccount.setcRPdcDivisionId(cashReceiptPdcAC.getDivisionId());
		}
		//Set the cash payment post dated check default division and account.
		if (bankAccount.getCashPaymentsPdcAcctId() != null) {
			AccountCombination cashPaymentPdcAC =
					accountCombinationDao.get(bankAccount.getCashPaymentsPdcAcctId());
			bankAccount.setcPPdcAccountId(cashPaymentPdcAC.getAccountId());
			bankAccount.setcPPdcDivisionId(cashPaymentPdcAC.getDivisionId());
		}
		return bankAccount;
	}

	/**
	 * Get the bank account object.
	 * @param bankAcctId The unique id of the bank account.
	 * @return The bank account object.
	 */
	public BankAccount getBankAcct(int bankAcctId) {
		return bankAccountDao.get(bankAcctId);
	}

	/**
	 * Evaluates if the bank account name is unique.
	 * @param bankAccount The bank account to be evaluated.
	 * @return True if the bank account in unique, otherwise false.
	 */
	public boolean isUnique (BankAccount bankAccount) {
		if (bankAccount.getId() == 0) {
			return isUniqueField(bankAccount, false);
		}
		BankAccount oldBankAccount = bankAccountDao.get(bankAccount.getId());
		// If user did not change the name.
		if (bankAccount.getName().trim().equalsIgnoreCase(oldBankAccount.getName().trim())) {
			return true;
		}
		return isUniqueField(bankAccount, false);
	}

	/**
	 * Evaluates if the bank account field is unique.
	 * @param bankAccount The bank account to be evaluated.
	 * @return True if the bank account field in unique, otherwise false.
	 */
	public boolean isUniqueField(BankAccount bankAccount, boolean isNumber) {
		return bankAccountDao.isUniqueBankAccount(bankAccount, false);
	}

	/**
	 * Get the bank accounts of the logged user.
	 * @param user The logged user.
	 * @return The list of bank accounts.
	 */
	public List<BankAccount> getBankAccouts(User user) {
		return bankAccountDao.getBankAccounts(user.getServiceLeaseKeyId());
	}

	/**
	 * Save the bank account
	 * @param bankAccount The bank account to be saved.
	 * @param user The logged user.
	 */
	public void saveBankAccount(BankAccount bankAccount, User user) {
		// Cash in bank account
		if(bankAccount.getInBADivisionId() != 0 ){
			AccountCombination cashInBankAcct = accountCombinationDao.getAccountCombination(bankAccount.getCompanyId(),
					bankAccount.getInBADivisionId(), bankAccount.getInBAAccountId());
			bankAccount.setCashInBankAcctId(cashInBankAcct.getId());
			bankAccount.setCashReceiptClearingAcctId(cashInBankAcct.getId());
			bankAccount.setCashPaymentClearingAcctId(cashInBankAcct.getId());
			bankAccount.setCashReceiptsPdcAcctId(cashInBankAcct.getId());
			bankAccount.setCashPaymentsPdcAcctId(cashInBankAcct.getId());
		}
		boolean isNewRecord = bankAccount.getId() == 0;
		bankAccount.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		AuditUtil.addAudit(bankAccount, new Audit(user.getId(), isNewRecord, new Date()));
		bankAccount.setName(bankAccount.getName().trim());
		bankAccountDao.saveOrUpdate(bankAccount);
	}

	/**
	 * Get all bank accounts (active/inactive).
	 * @param user The logged user.
	 * @return The list of all bank accounts.
	 */
	public List<BankAccount> getAllBankAccounts(User user) {
		return bankAccountDao.getAllBankAccounts(user.getServiceLeaseKeyId());
	}

	/**
	 * Get active bank accounts by company .
	 * @param companyId The company id.
	 * @return The list of bank accounts.
	 */
	public List<BankAccount> getBankAccounts(Integer companyId) {
		return bankAccountDao.getBankAccounts(companyId);
	}

	/**
	 * Get the paged list of bank accounts.
	 * @param name The name of bank account.
	 * @param pageNumber The page number.
	 * @return The paged list of bank accounts.
	 */
	public Page<BankAccount> getBankAccounts(String name, int companyId, int status, int pageNumber) {
		Page<BankAccount> bankAccounts = bankAccountDao.searchBankAccount(name, companyId, status,
				new PageSetting(pageNumber));
		for (BankAccount ba : bankAccounts.getData()) {
			if (ba.getBankId() != null) {
				ba.setBank(getBank(ba.getBankId()));
			}
		}
		return bankAccounts;
	}

	/**
	 * Get the list of the bank accounts by name
	 * @param name the bank account name
	 * @param user The current user logged
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return list of bank account
	 */
	public List<BankAccount> getBankAccountsByName (String name, Integer limit, User user, Integer companyId, Integer divisionId) {
		return bankAccountDao.getAllBankAccountsByName(name, limit, user, companyId, divisionId);
	}

	public BankAccount getBankAccoutByName(String name) {
		if (name != null && !name.trim().isEmpty()) {
			return bankAccountDao.getBankAccountByName(name);
		}
		return null;
	}

	/**
	 * Get the bank account by name, company id, and division id
	 * @param name The bank name
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @return The bank account object
	 */
	public BankAccount getBankAccoutByName(String name, Integer companyId, Integer divisionId) {
		if (name != null && !name.trim().isEmpty()) {
			return bankAccountDao.getBankAccountByName(name, companyId, divisionId);
		}
		return null;
	}


	/**
	 * Get active bank accounts by user company .
	 * @param companyId The company id.
	 * @return The list of bank accounts.
	 */
	public List<BankAccount> getBankAccountsByUser(User user) {
		return getBankAccountsByUser(user, false);
	}

	/**
	 * Get all bank accounts by user company.
	 * @param companyId The company id.
	 * @param isActiveOnly True if get all active bank accounts, otherwise all
	 * @return The list of bank accounts.
	 */
	public List<BankAccount> getBankAccountsByUser(User user, boolean isActiveOnly) {
		return bankAccountDao.getBankAccountsByUser(user, isActiveOnly);
	}


	/**
	 * Get the list of banks
	 * @param bankId The bank id
	 * @return The list of banks
	 */
	public List<Bank> getBanks(Integer bankId) {
		return bankDao.getBanks(bankId);
	}

	/**
	 * Get the bank object
	 * @param bankId The bank id
	 * @return The bank object
	 */
	public Bank getBank(Integer bankId) {
		return bankDao.get(bankId);
	}
}