package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.ArLineSetupDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.report.StatementOfAccountParam;

/**
 * Service class that will handle the business logic for AR Customer Account.

 *
 */
@Service
public class ArCustomerAcctService {
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private ArCustomerDao customerDao;
	@Autowired
	private ArLineSetupDao lineSetupDao;
	@Autowired
	private ArCustomerAcctDao customerAcctDao;
	@Autowired
	private AccountCombinationDao acctCombiDao;
	@Autowired
	private EBObjectDao ebObjectDao;

	/**
	 * Get the AR Customers based on the service lease key of the logged user.
	 * @return Collection of AR Customers.
	 */
	public Collection<ArCustomer> arCustomers(User user, int customerId) {
		Collection<ArCustomer> customers = customerDao.getArCustomers(user.getServiceLeaseKeyId());
		if(customerId != 0) {
			Collection<Integer> customerIds = new ArrayList<Integer>();
			for (ArCustomer customer : customers) {
				customerIds.add(customer.getId());
			}
			if(!customerIds.contains(customerId))
				customers.add(customerDao.get(customerId));
		}
		return customers;
	}

	/**
	 * Save the AR Customer Account.
	 */
	public void saveCustomerAcct(ArCustomerAccount account, User user) {
		//Set the Default Debit Account Combination
		AccountCombination debitAC = getAcctCombi(account.getCompanyId(), account.getDebitDivisionId(),
				account.getDebitAccountId());
		account.setDefaultDebitACId(debitAC.getId());
		Integer transLineId = account.getDefaultTransactionLineId() == 0 ? null : account.getDefaultTransactionLineId();
		account.setDefaultTransactionLineId(transLineId);
		account.setName(account.getName().trim());
		if(account.getWithdrawalAccountId() != null) {
			AccountCombination withdrawalSlipAC = getAcctCombi(account.getCompanyId(),
					account.getWithdrawalSlipDivisionId(), account.getWithdrawalAccountId());
			account.setDefaultWithdrawalSlipACId(withdrawalSlipAC.getId());
		}
		boolean isNewRecord = account.getId() == 0 ? true : false;
		if(isNewRecord){
			EBObject eb = new EBObject();
			AuditUtil.addAudit(eb, new Audit(user.getId(), true, new Date()));
			eb.setObjectTypeId(ArCustomerAccount.OBJECT_TYPE_ID);
			ebObjectDao.save(eb);
			account.setEbObjectId(eb.getId());
		}
		AuditUtil.addAudit(account, new Audit(user.getId(), isNewRecord, new Date()));
		customerAcctDao.saveOrUpdate(account);
	}

	/**
	 * Get the account combination.
	 * @return The account combination, otherwise null.
	 */
	public AccountCombination getAcctCombi(Integer companyId, Integer divisionId, Integer accountId) {
		if(companyId != null && divisionId != null && accountId != null) {
			AccountCombination acctCombi = acctCombiDao.getAccountCombination(companyId, divisionId, accountId);
			return acctCombi;
		}
		return null;
	}

	/**
	 * Get the Account combination using the Account Combination Id from AR Line.
	 * @param aCId The Id of the Account combination
	 * @return The account combination object.
	 */
	public AccountCombination accountCombi(int aCId) {
		return acctCombiDao.getACFromArLine(aCId);
	}

	/**
	 * Get the AR Lines under the service lease of the logged user and the selected AR Line if inactive.
	 * @return The collection of AR Lines.
	 */
	public Collection<ArLineSetup> getArLines(int companyId, Integer arLineId, User user) {
		Collection<ArLineSetup> arLines = lineSetupDao.getArLines(companyId, user.getServiceLeaseKeyId());
		if(arLineId != null && arLineId != 0) {
			Collection<Integer> arLineIds = new ArrayList<Integer>();
			for (ArLineSetup arLine : arLines) {
				arLineIds.add(arLine.getId());
			}
			if(!arLineIds.contains(arLineId))
				arLines.add(lineSetupDao.get(arLineId));
		}
		return arLines;
	}

	/**
	 * Verify if company, division and account is active.
	 * <br>1 = Company, 2 = Division, 3 = Account
	 * @return True if active, false if inactive.
	 */
	public boolean isActiveAcComponent(ArCustomerAccount customerAcct, int accountField) {
		AccountCombination ac = getAcctCombi(customerAcct.getCompanyId(),
				customerAcct.getDebitDivisionId(), customerAcct.getDebitAccountId());
		if(accountField == 1) {
			Company company = companyDao.get(ac.getCompanyId());
			if(company.isActive())
				return true;
		}
		if(accountField == 2) {
			Division division = divisionDao.get(ac.getDivisionId());
			if(division.isActive())
				return true;
		}
		if(accountField == 3) {
			Account account = accountService.getAccount(ac.getAccountId());
			if(account.isActive())
				return true;
		}
		return false;
	}

	/**
	 * Evaluate the AR Customer Account name if unique.
	 * @return True if unique, false if there is already an entry of the evaluated name.
	 */
	public boolean isUniqueName(ArCustomerAccount account) {
		if(account.getId() != 0) {
			ArCustomerAccount existingAccount = customerAcctDao.get(account.getId());
			if(existingAccount.getName().trim().equalsIgnoreCase(account.getName().trim()))
				return true;
		}
		return customerAcctDao.isUniqueName(account.getName());
	}

	/**
	 * Get the AR Customer Account object using its Id.
	 * @return The AR Customer Account object.
	 */
	public ArCustomerAccount getAccount(int customerAcctId) {
		return customerAcctDao.get(customerAcctId);
	}

	/**
	 * Get the list of AR customer's account.
	 * @param customerId The customer id.
	 * @param customerAccountId The customer account id.
	 * @return The list of AR customer's account.
	 */
	public List<ArCustomerAccount> getCustomerAccounts (int customerId, Integer customerAccountId){
		List<ArCustomerAccount> customerAccounts = customerAcctDao.getArCustomerAccounts(customerId, null, null);
		if (customerAccountId != null){
			boolean hasSelectedCustomerAcct = false;
			for (ArCustomerAccount ArCsAcct : customerAccounts) {
				if (ArCsAcct.getId() == customerAccountId){
					hasSelectedCustomerAcct = true;
					break;
				}
			}
			ArCustomerAccount arCustAcct = customerAcctDao.get(customerAccountId);
			if(!hasSelectedCustomerAcct && arCustAcct != null)
				customerAccounts.add(arCustAcct);
		}
		return customerAccounts;
	}

	/**
	 * Get the AR customer accounts by customer and company.
	 * @param arCustomerId The AR customer id.
	 * @param companyId The company id.
	 * @param divisionId The division id
	 * @return The list of ar customer accounts.
	 */
	public List<ArCustomerAccount> getArCustomerAccounts (Integer arCustomerId, Integer companyId, Integer divisionId, Integer arCustomerAccountId) {
		List<ArCustomerAccount> arCustomerAccounts = customerAcctDao.getArCustomerAccounts(arCustomerId, companyId, divisionId);
		return appendInactiveCustomerAcct(arCustomerAccounts, arCustomerAccountId);
	}

	private List<ArCustomerAccount> appendInactiveCustomerAcct(List<ArCustomerAccount> arCustomerAccounts, Integer arCustomerAccountId) {
		if (arCustomerAccountId != null) {
			ArCustomerAccount arCustomerAcct = getAccount(arCustomerAccountId);
			if (arCustomerAcct != null && !arCustomerAcct.isActive()) {
				arCustomerAccounts.add(arCustomerAcct);
				arCustomerAcct = null;
			}
		}
		return arCustomerAccounts;
	}

	/**
	 * Get the AR customer accounts by customer and company.
	 * @param arCustomerId The AR customer id.
	 * @param companyId The company id.
	 * @param divisionId The division id
	 * @return The list of ar customer accounts.
	 */
	public List<ArCustomerAccount> getArCustomerAccounts (Integer arCustomerId, Integer companyId, Integer divisionId, boolean activeOnly) {
		return customerAcctDao.getArCustomerAccounts(arCustomerId, companyId, divisionId, activeOnly);
	}

	/**
	 * Checks if the customer account belongs to the customer.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAcctId The customer account id.
	 * @return True if the customer account belongs to the customer, otherwise false.
	 */
	public boolean belongsToCustomer (Integer arCustomerId, Integer arCustomerAcctId) {
		if (arCustomerId != null && arCustomerAcctId != null) {
			List<ArCustomerAccount> arCustomerAccounts = customerAcctDao.getArCustomerAccounts(arCustomerId, null, null);
			if (arCustomerAccounts != null && !arCustomerAccounts.isEmpty()) {
				for (ArCustomerAccount arCustomerAccount : arCustomerAccounts) {
					if (arCustomerAcctId == arCustomerAccount.getId())
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Search for customer accounts.
	 * @param customerName The customer name.
	 * @param customerAcctNamr The customer account name.
	 * @param companyId The company id.
	 * @param termId The term id.
	 * @param status The status of the customer account.
	 * @param pageNumber The page number.
	 * @return The page result.
	 */
	public Page<ArCustomerAccount> searchCustomerAccts(Integer divisionId, String customerName, String customerAcctName, Integer companyId,
			Integer termId, String status,Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return customerAcctDao.searchCustomerAccounts(divisionId, customerName, customerAcctName, companyId, termId, 
				searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the previous transaction balance amount.
	 * @param param The selected parameter.
	 * @return The previous balance amount.
	 */
	public Double getPreviousBalanceAmount(StatementOfAccountParam param) {
		return customerAcctDao.getPreviousBalanceAmount(param);
	}

	/**
	 * Get the AR Customer Account.
	 * @param companyId The selected company id.
	 * @param customerId The selected customer id.
	 * @return The AR Customer Account object.
	 */
	public ArCustomerAccount getArCustomerAcct(Integer companyId, Integer customerId) {
		return customerAcctDao.getArCustomerAccount(companyId, customerId);
	}
}
