package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;

/**
 * Business logic for Account Combination object.

 */
@Service
public class AccountCombinationService {

	@Autowired
	private AccountCombinationDao accountCombinationDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountDao accountDao;

	/**
	 * Load all the account combinations.
	 * user The logged user.
	 * @return The page list of account combinations.
	 */
	public Page<AccountCombination> getAllAccountCombinations(User user){
		return accountCombinationDao.getAccountCombinations(user);
	}

	/**
	 * Get the account combination object.
	 * @param accountCombinationId The account combination id.
	 * @return The account combination object.
	 */
	public AccountCombination getAccountCombination(int accountCombinationId){
		return accountCombinationDao.get(accountCombinationId);
	}

	/**
	 * Search/filter account combinations based on the parameters.
	 * @return Paged collection of account combination.
	 */
	public Page<AccountCombination> searchAccountCombinations (String companyNumber,  String divisionNumber,
			 String accountNumber,  String companyName,  String divisionName, String accountName,
			 int serviceLeaseKeyId, int pageNumber, User user, String status) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return accountCombinationDao.searchAccountCombinations(companyNumber.trim(), divisionNumber.trim(),
				accountNumber.trim(), companyName.trim(), divisionName.trim(), accountName.trim(),
				serviceLeaseKeyId, searchStatus, new PageSetting(pageNumber), user);
	}

	/**
	 * Save the account combination object.
	 * @param accountCombination The account combination to be saved.
	 * @param user The logged in  user.
	 */
	public void saveAccountCombination(AccountCombination accountCombination, User user){
		boolean isNewRecord = accountCombination.getId() == 0 ? true : false;
		AuditUtil.addAudit(accountCombination, new Audit(user.getId(), isNewRecord, new Date()));
		accountCombination.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		accountCombinationDao.saveOrUpdate(accountCombination);
	}

	/**
	 * Validate/Check if the account combination is unique or not.
	 * @param accountCombination The account combination object to be evaluated.
	 * @return True if the account combination number is unique, otherwise false.
	 */
	public boolean isUniqueAccountCombination(AccountCombination accountCombination){
		if (accountCombination.getId() == 0)
			return accountCombinationDao.isUniqueAccountCombination(accountCombination);
		AccountCombination oldAccountCombination = accountCombinationDao.get(accountCombination.getId());
		if (accountCombination.getCompanyId() == oldAccountCombination.getCompanyId() && 
				accountCombination.getDivisionId() == oldAccountCombination.getDivisionId() &&
				accountCombination.getAccountId() == oldAccountCombination.getAccountId())
			return true;
		return accountCombinationDao.isUniqueAccountCombination(accountCombination);
	}

	/**
	 * Generate an account combination based on company number, division number, and account number.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param accountId The account id.
	 * @return The account combination number.
	 */
	public String generateAccountCombination (int companyId, int divisionId, int accountId) {
		String strCompanyNumber = companyId != -1 ? companyDao.get(companyId).getCompanyNumber() : "";
		String strDivisionNumber = divisionId != -1 ? divisionDao.get(divisionId).getNumber() : "";
		String strAccountNumber =  accountId != -1 ? accountDao.get(accountId).getNumber() : "";
		if (strCompanyNumber.isEmpty() && strDivisionNumber.isEmpty() && strAccountNumber.isEmpty())
			return "";
		return strCompanyNumber+"-"+strDivisionNumber+"-"+strAccountNumber;
	}

	/**
	 * Check if the combination is valid.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param accountId The account id.
	 * @return True if combination is valid, otherwise false.
	 */
	public boolean isValidCombination (int companyId, int divisionId, int accountId) {
		if (companyId != -1 && divisionId != -1 && accountId != -1)
			return true;
		return false;
	}

	/**
	 * Get the account combination object based on the company id, division id, and account id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param accountId The account id.
	 * @return The account combination object.
	 */
	public AccountCombination getAccountCombination(int companyId, int divisionId, int accountId) {
		return accountCombinationDao.getAccountCombination(companyId, divisionId, accountId);
	}

	/**
	 * Get the account combination object based on the company number, division number, and account number.
	 * @param companyNumber The company number.
	 * @param divisionNumber The division number.
	 * @param accountNumber The account number.
	 * @return The account combination object.
	 */
	public AccountCombination getAccountCombination (String companyNumber, String divisionNumber, String accountNumber) {
		return accountCombinationDao.getAccountCombination(companyNumber.trim(), divisionNumber.trim(),
				accountNumber.trim());
	}

	/**
	 * Get the list of account combination base on the companyId and divisionId.
	 * @param companyId The company Id.
	 * @param divisionId the division Id.
	 * @param limit the limit of items to be showed.
	 * @param accountName The account name.
	 * @return The account combination object.
	 */
	public List<AccountCombination> getAccountCombinations(Integer companyId,
			Integer divisionId, String accountName, Integer limit) {
		return accountCombinationDao.getAccountCombinations(companyId, divisionId, accountName, limit);
	}

	/**
	 * Create account combination based on the division id created
	 * @param divisionId The division id
	 * @param userId The current user logged id
	 * @param serviceLeaseKeyId The service lease key id
	 * @param currentDate The current date
	 */
	public void createAccountCombinations(int divisionId, int userId, int serviceLeaseKeyId,
			Date currentDate) {
		List<Account> defaultAccounts = accountDao.getAllActive();
		List<Company> companies = companyDao.getAllActive();
		List<Domain> toBeSavedAcctCombis = new ArrayList<>();
		AccountCombination acctCombi = null;
		if (!defaultAccounts.isEmpty()) {
			for (Account defAcct : defaultAccounts) {
				int accountId = defAcct.getId();
				for (Company c : companies) {
					// Check if there is already existing account combination when editing to avoid duplicate entry.
					int companyId = c.getId();
					if (hasAcctCombination(companyId, divisionId, accountId)) {
						continue;
					}
					acctCombi = new AccountCombination();
					acctCombi.setCompanyId(companyId);
					acctCombi.setDivisionId(divisionId);
					acctCombi.setAccountId(accountId);
					acctCombi.setActive(true);
					acctCombi.setServiceLeaseKeyId(serviceLeaseKeyId);
					AuditUtil.addAudit(acctCombi, new Audit(userId, true, currentDate));
					toBeSavedAcctCombis.add(acctCombi);
					acctCombi = null;
				}
			}

			if (!toBeSavedAcctCombis.isEmpty()) {
				accountCombinationDao.batchSave(toBeSavedAcctCombis);
			}
		}
	}

	private boolean hasAcctCombination(int companyId, int divisionId, int accountId) {
		return accountCombinationDao.getAccountCombination(companyId, divisionId, accountId) != null;
	}
}