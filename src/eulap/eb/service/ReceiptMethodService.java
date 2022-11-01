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
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.ReceiptMethodDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.domain.hibernate.User;

/**
 * A class that handles the business logic of Receipt Method

 */
@Service
public class ReceiptMethodService {
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private ReceiptMethodDao receiptMethodDao;
	@Autowired
	private AccountCombinationDao accountCombinationDao;

	/**
	 * Get the collection of companies and the selected company if inactive.
	 * @return The collection of companies filtered by the service lease key of the user.
	 */
	public Collection<Company> getCompanies (User user, int companyId) {
		Collection<Company> companies = companyDao.getActiveCompanies(user, null, null, null);
		if(companyId != 0) {
			Collection<Integer> companyIds = new ArrayList<Integer>();
			for (Company company : companies) {
				companyIds.add(company.getId());
			}
			if(!companyIds.contains(companyId))
				companies.add(companyDao.get(companyId));
		}
		return companies;
	}

	/**
	 * Get the receipt methods
	 * @param receiptId The unique id of the receipt
	 * @return The receipt methods
	 */
	public ReceiptMethod getReceiptMethod (int receiptId) {
		ReceiptMethod receiptMethod = receiptMethodDao.get(receiptId);
		if (receiptMethod == null)
			return null;
		//Set the debit account default company, division and account
		if (receiptMethod.getDebitAcctCombinationId() != null) {
			AccountCombination debitAccount = 
					accountCombinationDao.get(receiptMethod.getDebitAcctCombinationId());
			receiptMethod.setDbACAccountId(debitAccount.getAccountId());
			receiptMethod.setDbACDivisionId(debitAccount.getDivisionId());
		}
		//Set the credit account default company, division and account
		AccountCombination creditAcctCombination = 
				accountCombinationDao.get(receiptMethod.getCreditAcctCombinationId());
		receiptMethod.setCrACAccountId(creditAcctCombination.getAccountId());
		receiptMethod.setCrACDivisionId(creditAcctCombination.getDivisionId());
		return receiptMethod;
	}

	/**
	 * Evaluates if the receipt method name is unique
	 * @param receiptMethod The receipt method to be evaluated
	 * @param receiptId The receipt id of the receipt method
	 * @return True if receipt method is unique, otherwise false
	 */
	public boolean isUnique (ReceiptMethod receiptMethod) {
		if (receiptMethod.getId() == 0) {
			return receiptMethodDao.isUniqueReceiptMethod(receiptMethod);
		}
		ReceiptMethod oldReceiptMethod = receiptMethodDao.get(receiptMethod.getId());
		// If user did not change the name
		if (receiptMethod.getName().trim().equalsIgnoreCase(oldReceiptMethod.getName().trim())) {
			return true;
		}
		return receiptMethodDao.isUniqueReceiptMethod(receiptMethod);
	}

	/**
	 * Save the receipt method
	 * @param receiptMethod The receipt method to be saved
	 * @param user The logged user
	 */
	public void saveReceiptMethod (ReceiptMethod receiptMethod, User user) {
		if (receiptMethod.getBankAccountId() == Integer.valueOf(-1)) {
			receiptMethod.setBankAccountId(null);
			// Debit account
			if(receiptMethod.getDbACDivisionId() != 0){
				AccountCombination debitAccount = accountCombinationDao.getAccountCombination(receiptMethod.getCompanyId(), 
						receiptMethod.getDbACDivisionId(), receiptMethod.getDbACAccountId());
				receiptMethod.setDebitAcctCombinationId(debitAccount.getId());
			}
		} else {
			// If there is bank account, remove value for debit account combination.
			receiptMethod.setDebitAcctCombinationId(null);
		}

		// Credit account
		if(receiptMethod.getCrACDivisionId() != 0){
			AccountCombination creditAccount = accountCombinationDao.getAccountCombination(receiptMethod.getCompanyId(), 
					receiptMethod.getCrACDivisionId(), receiptMethod.getCrACAccountId());
			receiptMethod.setCreditAcctCombinationId(creditAccount.getId());
		}
		boolean isNewRecord = receiptMethod.getId() == 0;
		AuditUtil.addAudit(receiptMethod, new Audit(user.getId(), isNewRecord, new Date()));
		receiptMethod.setName(StringFormatUtil.removeExtraWhiteSpaces(receiptMethod.getName()));
		receiptMethodDao.saveOrUpdate(receiptMethod);
	}

	/**
	 * Get the list of receipt methods based on the logged user.
	 * @param user The logged user.
	 * @return The list of receipt methods.
	 */
	public List<ReceiptMethod> getReceiptMethods (User user) {
		return receiptMethodDao.getReceiptMethods(user);
	}

	/**
	 * Checks if there is no bank account id and debit account combination id.
	 * @param bankAccountId The bank account id.
	 * @param dbACDivisionId The debit account division id.
	 * @param dbACAccountId The debit account account id.
	 * @return True if no bank account id and debit account combination id,
	 * otherwise false.
	 */
	public boolean noBAIdAndDDAC (Integer bankAccountId, int dbACDivisionId, 
			int dbACAccountId) {
		return (bankAccountId == null || bankAccountId == Integer.valueOf(-1)) && 
				(dbACDivisionId == 0 || dbACAccountId == 0);
	}

	/**
	 * Checks if there is both bank account id and debit account combination id.
	 * @param bankAccountId The bank account id.
	 * @param dbACDivisionId The debit account division id.
	 * @param dbACAccountId The debit account account id.
	 * @return True if there is account id and debit account combination id,
	 * otherwise false.
	 */
	public boolean hasBAIdAndDDAC (Integer bankAccountId, int dbACDivisionId, 
			int dbACAccountId) {
		return bankAccountId != null && bankAccountId != -1 && dbACDivisionId != 0 && dbACAccountId != 0;
	}

	/**
	 * Search the receipt method.
	 * @param name The receipt method name.
	 * @param companyId The id of the company.
	 * @param bankAccountId The id of bank account.
	 * @param status The status of the receipt method.
	 * @param pageNumber The page number.
	 * @return The page result.
	 */
	public Page<ReceiptMethod> searchReceiptMethod(String name, Integer companyId, Integer bankAccountId, String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return receiptMethodDao.searchReceiptMethod(name, companyId, bankAccountId, searchStatus, new PageSetting(pageNumber, 25));
	}

	/**
	 * Get the active receipt methods by company.
	 * @param companyId The company id.
	 * @return The receipt methods.
	 */
	public List<ReceiptMethod> getReceiptMethods (Integer companyId) {
		return receiptMethodDao.getReceiptMethods(companyId);
	}

	/**
	 * Get the list of active receipt methods.
	 * If the provided receipt method id corresponds with an inactive receipt method, it will be included in the list.
	 * @param companyId The company id.
	 * @param receiptMethodId The receuot method id.
	 * @return The list of receipt methods.
	 */
	public List<ReceiptMethod> getReceiptMethodsWithInactive (Integer companyId, Integer receiptMethodId) {
		return receiptMethodDao.getReceiptMethods(companyId, receiptMethodId);
	}

	/**
	 * Get the list of receipt methods by division.
	 * @param divisionId The division id.
	 * @return The list of receipt methods.
	 */
	public List<ReceiptMethod> getReceiptMethodsByDivision (Integer divisionId) {
		return receiptMethodDao.getReceiptMethodsByDivision(divisionId);
	}

	/**
	 * Get receipt method by company and division.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The list of {@link ReceiptMethod}
	 */
	public List<ReceiptMethod> getReceiptMethodByCompanyAndDivision(Integer companyId, Integer divisionId){
		List<ReceiptMethod> rms = getReceiptMethods(companyId);
		return getReceiptMethodByCompanyAndDivision(rms, divisionId);
	}

	/**
	 * Get receipt method by company and division, with inactive receipt method based on the provided receipt method id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param receiptMethodId The receipt method id.
	 * @return The list of {@link ReceiptMethod}
	 */
	public List<ReceiptMethod> getReceiptMethodByCompanyAndDivision(Integer companyId, Integer divisionId, Integer receiptMethodId){
		List<ReceiptMethod> rms = getReceiptMethodsWithInactive(companyId, receiptMethodId);
		return getReceiptMethodByCompanyAndDivision(rms, divisionId);
	}

	private List<ReceiptMethod> getReceiptMethodByCompanyAndDivision(List<ReceiptMethod> rms, Integer divisionId) {
		List<ReceiptMethod> filteredRms = new ArrayList<ReceiptMethod>();
		for(ReceiptMethod rm : rms) {
			if(divisionId != null) {
				if(rm.getDebitAcctCombinationId() != null) {
					if(rm.getDebitAcctCombination().getDivisionId() == divisionId || rm.getCreditAcctCombination().getDivisionId() == divisionId) {
						filteredRms.add(rm);
					}
				} else {
					if(rm.getCreditAcctCombination().getDivisionId() == divisionId) {
						filteredRms.add(rm);
					}
				}
			}
		}
		return filteredRms;
	}
}