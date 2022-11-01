package eulap.eb.service;

import java.util.Collections;
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
import eulap.eb.dao.ArLineSetupDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;

/**
 * Service class that will handle all the business logic

 *
 */
@Service
public class ArLineSetupService {
	@Autowired
	private AccountService accountService;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private ArLineSetupDao lineSetupDao;
	@Autowired
	private AccountCombinationDao acctCombiDao;

	/**
	 * Get the AR Line Setup object using the Id.
	 * @param id The unique Id of the AR Line object.
	 * @return The AR Line object.
	 */
	public ArLineSetup getLineSetup (int id) {
		return lineSetupDao.get(id);
	}

	/**
	 * Save the AR Line Setup.
	 */
	public void saveArLineSetup(User user, ArLineSetup arLine) {
		boolean isNewRecord = arLine.getId() == 0 ? true : false;
		AuditUtil.addAudit(arLine, new Audit(user.getId(), isNewRecord, new Date()));
		arLine.setAccountCombinationId(getAcctCombi(arLine).getId());
		arLine.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		arLine.setName(arLine.getName().trim());
		lineSetupDao.saveOrUpdate(arLine);
	}

	/**
	 * Get the Account Combination Id.
	 */
	public AccountCombination getAcctCombi(ArLineSetup arLine) {
		if(arLine.getCompanyId() != null && arLine.getDivisionId() != null && arLine.getAccountId() != null) {
			AccountCombination acctCombi = acctCombiDao.getAccountCombination(arLine.getCompanyId(),
					arLine.getDivisionId(), arLine.getAccountId());
			return acctCombi;
		}
		return null;
	}

	/**
	 * Verify if company, division and account is active.
	 * <br>1 = Company, 2 = Division, 3 = Account
	 * @return True if active, false if inactive.
	 */
	public boolean isActiveAcComponent(ArLineSetup arLine, int arLineField) {
		AccountCombination ac = getAcctCombi(arLine);
		if(arLineField == 1) {
			Company company = companyDao.get(ac.getCompanyId());
			if(company.isActive())
				return true;
		}
		if(arLineField == 2) {
			Division division = divisionDao.get(ac.getDivisionId());
			if(division.isActive())
				return true;
		}
		if(arLineField == 3) {
			Account account = accountService.getAccount(ac.getAccountId());
			if(account.isActive())
				return true;
		}
		return false;
	}

	/**
	 * Validate the account combination if it is active or inactive.
	 * @return True if active, otherwise false.
	 */
	public boolean isActiveAC(ArLineSetup arLine) {
		AccountCombination ac = getAcctCombi(arLine);
		if(ac.isActive())
			return true;
		return false;
	}

	/**
	 * Evaluate the AR Line Setup name and company if there is duplicate.
	 * @return True if has duplicate, false if there is no duplicate name and company.
	 */
	public boolean hasDuplicate(ArLineSetup arLineSetup) {
		return lineSetupDao.hasDuplicate(arLineSetup);
	}

	/**
	 * Get all active AR Line Accounts.
	 * @return The list of AR Line accounts.
	 */
	public List<ArLineSetup> getAllARLineAccts(){
		return lineSetupDao.getAllActiveARLineAccts();
	}

	/**
	 * Get the Ar line setup object by its number
	 * @param arLineNumber The Ar line setup number.
	 * @param user The current logged in user.
	 * @return The Ar line setup object.
	 */
	public ArLineSetup getArLineSetupByNumber (String arLineNumber, User user){
		return lineSetupDao.getArLineSetupByNumber(arLineNumber, user.getServiceLeaseKeyId());
	}

	/**
	 * Get the list of AR Line setups based on its name.
	 * @param name The name of the ar line setup.
	 * @param arCustAcctId The unique id of Ar Customer Account.
	 * @param id The unique id of Ar Line Setup.
	 * @param noLimit True if display all unit of measurements.
	 * @param isExact True if the name must be exact.
	 * @param user The logged user.
	 * @param divisionId The division id
	 * @return The list of AR Line setups.
	 */
	public List<ArLineSetup> getArLineSetUps (String name, Integer arCustAcctId, Integer id, Boolean noLimit,
			Boolean isExact, User user, Integer divisionId) {
		List<ArLineSetup> arLineSetups = lineSetupDao.getArLineSetUps(name, arCustAcctId, id, noLimit,
				isExact, user.getServiceLeaseKeyId(), divisionId);
		if (arLineSetups == null || arLineSetups.isEmpty()) {
			return Collections.emptyList();
		}
		return arLineSetups;
	}

	/**
	 * Get the list of AR Line setups based on its name.
	 * @param companyId The selected company id.
	 * @param name The name of the ar line setup.
	 * @param isExact True if the name must be exact.
	 * @param user The logged user.
	 * @return The list of AR Line setups.
	 */
	public List<ArLineSetup> getArLineSetups (Integer companyId, String name, Boolean isExact, User user) {
		List<ArLineSetup> arLineSetUps = lineSetupDao.getArLineSetUps(companyId, name, isExact, user.getServiceLeaseKeyId());
		if (arLineSetUps == null || arLineSetUps.isEmpty())
			return Collections.emptyList();
		return arLineSetUps;
	}

	/**
	 * Get the Ar Line setup object by name.
	 * @param name The ar line setup name.
	 * @param companyId The company id.
	 * @param divisionId The division id
	 * @return AR Line Setup object.
	 */
	public ArLineSetup getALSetupByNameAndCompany (String name, Integer companyId, Integer divisionId) {
		return lineSetupDao.getALSetupByNameAndCompany(name, companyId, divisionId);
	}

	/**
	 * Get the Ar Line setup object by name.
	 * @param arLineSetupName The ar line setup name.
	 * @return AR Line Setup object.
	 */
	public ArLineSetup getArLineSetupByName (String arLineSetupName) {
		return getALSetupByNameAndCompany(arLineSetupName, null, null);
	}

	/**
	 * Search the AR Line Setup.
	 * @param name The AR line setup name.
	 * @param companyId The company id.
	 * @param status The status of the AR line setup
	 * @param pageNumber The page number.
	 * @return The page result.
	 */
	public Page<ArLineSetup> searchArLineSetup(String name, Integer companyId, String status,Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return lineSetupDao.searchArLineSetup(name, companyId, searchStatus, new PageSetting(pageNumber, 25));
	}
}
