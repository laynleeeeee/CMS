package eulap.eb.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.BankAccountDao;
import eulap.eb.dao.CheckbookDao;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.domain.hibernate.User;

/**
 * Handles the business logic of {@link Checkbook}

 */
@Service
public class CheckbookService {

	@Autowired
	private CheckbookDao checkbookDao;
	@Autowired
	private BankAccountDao bankAccountDao;
	private static Logger logger = Logger.getLogger(CheckbookService.class);

	/**
	 * Evaluates if the checkbook is unique.
	 * @param checkbook The checkbook to be evaluated.
	 * @return True if unique otherwise, false.
	 */
	public boolean isUnique(Checkbook checkbook, int bankAccountId) {
		if(checkbook.getId() == 0)
			return checkbookDao.isUniqueCheckbook(checkbook, bankAccountId);
		Checkbook oldCheckbook = checkbookDao.get(checkbook.getId());
		// If user did not change the name.
		if (checkbook.getName().trim().equalsIgnoreCase(oldCheckbook.getName().trim()))
			return true;
		return checkbookDao.isUniqueCheckbook(checkbook, bankAccountId);
	}

	/**
	 * Save the checkbook.
	 * @param checkbook The checkbook to be saved
	 * @param user The logged user.
	 */
	public void saveCheckbook(Checkbook checkbook, User user) {
		boolean isNewRecord = checkbook.getId() == 0 ? true : false;
		BankAccount bankAccount =
				bankAccountDao.getBankAccountById(checkbook.getBankAccountId());
		checkbook.setBankAccountId(bankAccount.getId());
		checkbook.setName(checkbook.getName().trim());
		AuditUtil.addAudit(checkbook, new Audit(user.getId(), isNewRecord, new Date ()));
		logger.info("Successfully saved/updated the checkbook "+checkbook.getName());
		checkbookDao.saveOrUpdate(checkbook);
	}

	/**
	 * Validate the checkbook name and number series.
	 * @param checkbook The checkbook to be evaluated.
	 * @return Return true if valid, otherwise false.
	 */
	public boolean isValidSeries(Checkbook checkbook) {
		return checkbookDao.isValidSeries(checkbook);
	}

	/**
	 * Get the checkbooks.
	 * @param checkbookId The unique id of the checkbook.
	 * @return The checkbooks.
	 */
	public Checkbook getCheckbook(int checkbookId) {
		return checkbookDao.get(checkbookId);
	}

	/**
	 * Get all the checkbooks under a bank account.
	 * @param bankAccountId The Id of the bank account.
	 * @return The list of checkbooks.
	 */
	public List<Checkbook> getCheckbooks (User user, int bankAccountId) {
		return checkbookDao.getCheckbooks(user, bankAccountId);
	}

	/**
	 * The paged list of checkbooks.
	 * @param bankAccountName The bank account name.
	 * @param name The name of checkbook.
	 * @param checkNo The checkbook number.
	 * @param status Active or inactive.
	 * @param pageNumber The page number.
	 * @return The paged list of checkbooks.
	 */
	public Page<Checkbook> getCheckbooks (User user, Integer companyId, String bankAccountName, String name, String checkNo, int status, int pageNumber) {
		logger.info("Searching for checkbooks");
		BigDecimal checkNumber = null;
		if(!checkNo.trim().isEmpty() && StringFormatUtil.isNumeric(checkNo.trim())) {
			checkNumber = new BigDecimal(checkNo.trim());
			logger.trace("Successfully converted the check number to integer: "+checkNumber);
		}
		Page<Checkbook> checkbooks = checkbookDao.getCheckbooks(user, companyId, bankAccountName.trim(), name.trim(),
				checkNumber, status, new PageSetting(pageNumber));
		logger.debug("Retrieved "+checkbooks.getDataSize()+" checkbooks.");
		return checkbooks;
	}

	/**
	 * Get the checkbook by parameters.
	 * @param user The user current logged.
	 * @param bankAccountId The unique id of the bank account.
	 * @param checkBookName The checkbook name.
	 * @return The checkbook.
	 */
	public Checkbook getCheckBook(User user, Integer bankAccountId, String checkBookName) {
		return checkbookDao.getCheckBook(user, bankAccountId, checkBookName);
	}
}