package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ApLineSetupDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApLineSetup;
import eulap.eb.domain.hibernate.User;

/**
 * Business logic to process the requests for AP Line Setup.

 *
 */
@Service
public class ApLineSetupService {
	private Logger logger = Logger.getLogger(ApLineSetupService.class);
	@Autowired
	private ApLineSetupDao apLineSetupDao;
	@Autowired
	private AccountCombinationService acctCombiService;

	/**
	 * Get the AP Line Setup object using the unique id.
	 * @param apLineSetupId The id of the AP Line Setup.
	 * @return The {@link ApLineSetup}
	 */
	public ApLineSetup getApLineSetup(int apLineSetupId) {
		ApLineSetup apLineSetup = apLineSetupDao.get(apLineSetupId);
		if(apLineSetup == null) {
			return null;
		}
		AccountCombination ac = apLineSetup.getAccountCombination();
		apLineSetup.setCompanyId(ac.getCompanyId());
		apLineSetup.setDivisionId(ac.getDivisionId());
		apLineSetup.setAccountId(ac.getAccountId());
		return apLineSetup;
	}

	/**
	 * Validate the name of the AP Line Setup if unique.
	 * @param apLineSetupId The id of the ap line setup.
	 * @param name The name of the ap line setup.
	 * @param companyId The id of the company.
	 * @param divisionId The division id
	 * @return True if the name is unique, otherwise false.
	 */
	public boolean isUniqueName(Integer apLineSetupId, String name, Integer companyId, Integer divisionId) {
		return apLineSetupDao.isUniqueName(apLineSetupId, name.trim(), companyId, divisionId);
	}

	/**
	 * Search AP Line Setups.
	 * @param companyId The id of the company of the account combination.
	 * @param name The unique name of the AP Line Setup.
	 * @param status The status of the AP Line Setup. {Active, Inactive, All}
	 * @param pageNumber The current page.
	 * @return The paged result.
	 */
	public Page<ApLineSetup> searchApLineSetups(Integer companyId, String name, String status, Integer pageNumber) {
		logger.info("Searching AP Line Setups on page: "+pageNumber);
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return apLineSetupDao.searchApLineSetups(companyId, name, searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Save the AP Line Setup.
	 */
	public void saveApLineSetup(User user, ApLineSetup apLineSetup) {
		AccountCombination acctCombi = acctCombiService.getAccountCombination(apLineSetup.getCompanyId(),
				apLineSetup.getDivisionId(), apLineSetup.getAccountId());
		if(acctCombi != null) {
			logger.info("Processing the AP Line Setup to be saved.");
			boolean isNewRecord = apLineSetup.getId() == 0 ? true : false;
			AuditUtil.addAudit(apLineSetup, new Audit(user.getId(), isNewRecord, new Date()));
			apLineSetup.setAccountCombinationId(acctCombi.getId());
			apLineSetup.setName(apLineSetup.getName().trim());
			apLineSetupDao.saveOrUpdate(apLineSetup);
			logger.info("Saved AP Line Setup with name: "+apLineSetup.getName());
		}
	}

	/**
	 * Get the list of AP Line Setups of the company.
	 * @param companyId The id of the company.
	 * @param divisionId The division id
	 * @param name The name of the AP Line setup.
	 * @return The list of {@link ApLineSetup}
	 */
	public List<ApLineSetup> getApLineSetups(Integer companyId, Integer divisionId, String name) {
		return apLineSetupDao.getApLineSetups(companyId, divisionId, name);
	}

	/**
	 * Get the AP Line Setups of the company.
	 * @param companyId The id of the company.
	 * @param divisionId The division id
	 * @param name The name of the AP Line setup.
	 * @return The {@link ApLineSetup}
	 */
	public ApLineSetup getApLineSetup(Integer companyId, Integer divisionId, String name) {
		return apLineSetupDao.getApLineSetup(companyId, divisionId, name);
	}
}
