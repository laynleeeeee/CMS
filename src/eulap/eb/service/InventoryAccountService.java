package eulap.eb.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.InventoryAccountDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.InventoryAccount;
import eulap.eb.domain.hibernate.User;

/**
 * Business logic of Inventory Account.

 *
 */
@Service
public class InventoryAccountService {
	private static Logger logger = Logger.getLogger(InventoryAccountService.class);
	@Autowired
	private InventoryAccountDao inventoryAccountDao;
	@Autowired
	private AccountCombinationDao acctCombiDao;

	/**
	 * Get the Inventory Account object.
	 * @param id The unique id of Inventory Account.
	 * @return The Inventory Account object.
	 */
	public InventoryAccount getInventoryAcct(int id) {
		return inventoryAccountDao.get(id);
	}

	/**
	 * Save the Inventory Forms Account Setup.
	 */
	public void saveInventoryAccount(InventoryAccount ia, User user) {
		logger.info("Saving the Inventory Forms Account Setup.");
		boolean isNew = ia.getId() == 0 ? true : false;
		AuditUtil.addAudit(ia, new Audit(user.getId(), isNew, new Date()));

		if(!isNew) {
			//Set the created date of the Inventory Account.
			InventoryAccount savedIA = getInventoryAcct(ia.getId());
			DateUtil.setCreatedDate(ia, savedIA.getCreatedDate());
		}

		inventoryAccountDao.saveOrUpdate(ia);
		logger.info("Successfully saved Inventory Account id: "+ia.getId());

		logger.debug("=======>>>> Freeing up memory allocation.");
		ia = null;
	}

	/**
	 * Get the account combination.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param accountId The account id.
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
	 * Search Inventory Accounts.
	 * @param companyId The id of the company.
	 * @param statusId The status of the inventory account. {1=Active, 0=Inactive, -1=ALL}
	 * @param pageNumber The current page.
	 * @return List of inventory accounts in paged format.
	 */
	public Page<InventoryAccount> searchInvAccts(int companyId, int statusId, int pageNumber) {
		logger.debug("Searching inventory accounts.");
		logger.trace("Searching using the parameters: companyId="+companyId+" statusId="+statusId+" at page "+pageNumber);
		return inventoryAccountDao.searchInventoryAccts(companyId, statusId, new PageSetting(pageNumber));
	}

	/**
	 * Get the Inventory Forms Account Setup
	 * @param companyId The id of the company.
	 * @return The {@link InventoryAccount} object, otherwise null.
	 */
	public InventoryAccount getInvAcctByCompanyId(int companyId, int inventoryAcctId) {
		return inventoryAccountDao.getInvAcctByCompanyId(companyId, inventoryAcctId);
	}
}
