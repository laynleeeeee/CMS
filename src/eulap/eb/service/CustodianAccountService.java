package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.CustodianAccountDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Service class that will handle the business logic for Custodian Account.

 *
 */
@Service
public class CustodianAccountService {
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private CustodianAccountDao custodianAccountDao;
	@Autowired
	private ArCustomerAcctService arCustomerAccountService;
	@Autowired
	private TermService termService;
	@Autowired
	private AccountCombinationService accountCombinationService;
	@Autowired
	private CustodianAccountSupplierService casService;

	/**
	 * Get the AR Customer Account object using its Id.
	 * @return The AR Customer Account object.
	 */
	public CustodianAccount getCustodianAccount(int custodianAccountId) {
		return custodianAccountDao.get(custodianAccountId);
	}

	/**
	 * Validate custodian account informations
	 * @param obj The Object
	 * @param errors The validation errors
	 */
	public void validate(Object obj, Errors errors) {
		CustodianAccount custodianAccount = (CustodianAccount) obj;
		//Name
		if(custodianAccount.getCustodianName() == null || custodianAccount.getCustodianName().trim().isEmpty()) {
			errors.rejectValue("custodianName", null, null, ValidatorMessages.getString("CustodianAccountValidator.0"));
		}
		if(custodianAccount.getCustodianName() != null) {
			custodianAccount.setCustodianName(removeExtraSpaces(custodianAccount.getCustodianName()));
		}
		if(custodianAccount.getCustodianName().length() > 100) {
			errors.rejectValue("custodianName", null, null, ValidatorMessages.getString("CustodianAccountValidator.1"));
		}
		if(!custodianAccountDao.isUniqueCustodianName(custodianAccount)) {
			errors.rejectValue("custodianName", null, null, ValidatorMessages.getString("CustodianAccountValidator.2"));
		}

		//Custodian Account
		if(custodianAccount.getCustodianAccountName() == null || custodianAccount.getCustodianAccountName().trim().isEmpty()) {
			errors.rejectValue("custodianAccountName", null, null, ValidatorMessages.getString("CustodianAccountValidator.3"));
		}
		if(custodianAccount.getCustodianAccountName() != null) {
			custodianAccount.setCustodianAccountName(removeExtraSpaces(custodianAccount.getCustodianAccountName()));
		}
		if(custodianAccount.getCustodianAccountName().length() > 100) {
			errors.rejectValue("custodianAccountName", null, null, ValidatorMessages.getString("CustodianAccountValidator.4"));
		}
		if(!custodianAccountDao.isUniqueCustodianAccountName(custodianAccount)) {
			errors.rejectValue("custodianAccountName", null, null, ValidatorMessages.getString("CustodianAccountValidator.5"));
		}

		//Account Combination
		AccountCombination cdAC = arCustomerAccountService.getAcctCombi(custodianAccount.getCompanyId(),
				custodianAccount.getCdDivisionId(), custodianAccount.getCdAccountId());
		if(cdAC == null)
			errors.rejectValue("cdAccountCombinationId", null, null, ValidatorMessages.getString("CustodianAccountValidator.6"));
		else {
			if(!cdAC.isActive())
				errors.rejectValue("cdAccountCombinationId", null, null, ValidatorMessages.getString("CustodianAccountValidator.7"));

			//Company
			if(!isActiveCAComponent(custodianAccount, 1))
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.7"));

			//Division
			if(!isActiveCAComponent(custodianAccount, 2))
				errors.rejectValue("cdDivisionId", null, null, ValidatorMessages.getString("CustodianAccountValidator.9"));

			//Account
			if(!isActiveCAComponent(custodianAccount, 3))
				errors.rejectValue("cdAccountId", null, null, ValidatorMessages.getString("CustodianAccountValidator.10"));
		}

		AccountCombination fdAC = arCustomerAccountService.getAcctCombi(custodianAccount.getCompanyId(),
				custodianAccount.getFdDivisionId(), custodianAccount.getFdAccountId());
		if(fdAC == null) {
			errors.rejectValue("fdAccountCombinationId", null, null, ValidatorMessages.getString("CustodianAccountValidator.6"));
		} else {
			if(!fdAC.isActive()) {
				errors.rejectValue("fdAccountCombinationId", null, null, ValidatorMessages.getString("CustodianAccountValidator.7"));
			}

			//Division
			if(!isActiveCAComponent(custodianAccount, 2)) {
				errors.rejectValue("fdDivisionId", null, null, ValidatorMessages.getString("CustodianAccountValidator.9"));
			}

			//Account
			if(!isActiveCAComponent(custodianAccount, 3)) {
				errors.rejectValue("fdAccountId", null, null, ValidatorMessages.getString("CustodianAccountValidator.10"));
			}
		}

		if (custodianAccount.getTermId() == null) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("CustodianAccountValidator.11"));
		} else {
			Term term = termService.getTerm(custodianAccount.getTermId());
			if (term.isActive() == false){
				errors.rejectValue("termId", null, null, ValidatorMessages.getString("CustodianAccountValidator.12"));
			}
		}
	}

	private String removeExtraSpaces(String name) {
		return StringFormatUtil.removeExtraWhiteSpaces(name).trim();
	}

	/**
	 * Check if custodian name is unique
	 * @param custodianAccount The custodian account object
	 * @return True or false
	 */
	public boolean isUniqueCustodianName(CustodianAccount custodianAccount) {
		return custodianAccountDao.isUniqueCustodianName(custodianAccount);
	}

	/**
	 * Check if custodian account name is unique
	 * @param custodianAccount The custodian account object
	 * @return True or false
	 */
	public boolean isUniqueCustodianAccountName(CustodianAccount custodianAccount) {
		return custodianAccountDao.isUniqueCustodianAccountName(custodianAccount);
	}

	/**
	 * Verify if company, division and account is active.
	 * <br>1 = Company, 2 = Division, 3 = Account
	 * @return True if active, false if inactive.
	 */
	public boolean isActiveCAComponent(CustodianAccount custodianAccount, int accountField) {
		AccountCombination ac = arCustomerAccountService.getAcctCombi(custodianAccount.getCompanyId(),
				custodianAccount.getCdDivisionId(), custodianAccount.getCdAccountId());
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
	 * Search for custodian accounts.
	 * @param custodianName The customer name.
	 * @param custodianAccountName The customer account name.
	 * @param companyId The company id.
	 * @param termId The term id.
	 * @param status The status of the customer account.
	 * @param pageNumber The page number.
	 * @return The page result.
	 */
	public Page<CustodianAccount> searchCustodianAccounts(String custodianName, String custodianAccountName, Integer companyId,
			Integer termId, String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return custodianAccountDao.searchCustodianAccounts(custodianName, custodianAccountName, companyId, termId, 
				searchStatus, new PageSetting(pageNumber, 25));
	}

	/**
	 * Save the Custodian Account.
	 */
	public void saveCustodianAccount(CustodianAccount custodianAccount, User user) {
		AccountCombination cdAC = arCustomerAccountService.getAcctCombi(custodianAccount.getCompanyId(), custodianAccount.getCdDivisionId(),
				custodianAccount.getCdAccountId());
		custodianAccount.setCdAccountCombinationId(cdAC.getId());
		AccountCombination fdAC = arCustomerAccountService.getAcctCombi(custodianAccount.getCompanyId(),
				custodianAccount.getFdDivisionId(), custodianAccount.getFdAccountId());
			custodianAccount.setFdAccountCombinationId(fdAC.getId());
		boolean isNewRecord = custodianAccount.getId() == 0 ? true : false;
		AuditUtil.addAudit(custodianAccount, new Audit(user.getId(), isNewRecord, new Date()));
		custodianAccountDao.saveOrUpdate(custodianAccount);
		casService.saveCustodianAccountSupplier(user, custodianAccount);
	}

	/**
	 *  Get the list of custodian account based on the company id and name
	 * @param companyId The selected company id.
	 * @param name The name of the custodian account.
	 * @param isExact True if it must be exact, otherwise false.
	 * @return The list of custodian account.
	 */
	public List<CustodianAccount> getCustodianAccounts (Integer companyId, Integer divisionId, String name, Boolean isExact) {
		List<CustodianAccount> custodianAccounts = custodianAccountDao.getCustodianAccounts(companyId, name, isExact);
		if (!isExact) {
			List<CustodianAccount> filteredCAs = new ArrayList<CustodianAccount>();
			for (CustodianAccount ca : custodianAccounts) {
				if (checkACbyDivison(ca.getCdAccountCombinationId(), ca.getFdAccountCombinationId(), divisionId)) {
					filteredCAs.add(ca);
				}
			}
			if (filteredCAs == null || filteredCAs.isEmpty())
				return Collections.emptyList();
			return filteredCAs;
		} else {
			if (custodianAccounts == null || custodianAccounts.isEmpty())
				return Collections.emptyList();
			return custodianAccounts;
		}
	}

	private boolean checkACbyDivison(Integer cdACId, Integer fcACId, Integer divisionId) {
		AccountCombination cdAC = accountCombinationService.getAccountCombination(cdACId);
		AccountCombination fdAC = accountCombinationService.getAccountCombination(fcACId);
		if(cdAC.getDivisionId() == divisionId) {
			return true;
		}
		if(fdAC.getDivisionId() == divisionId) {
			return true;
		} else {
			return false;
		}
	}
}
