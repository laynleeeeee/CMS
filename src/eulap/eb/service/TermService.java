package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.TermDao;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;

/**
 * A class that handles the business logic of Term

 *
 */
@Service
public class TermService {
	@Autowired
	private TermDao termDao;

	public List<Term> getTerms (User user) {
		return termDao.getAllTerms(user.getServiceLeaseKeyId());
	}

	/**
	 * Get term by supplier account.
	 * @param supplierAccountId The supplier account id.
	 * @param user The logged user.
	 * @return The term.
	 */
	public Term getTermBySupplierAccount (int supplierAccountId, User user) {
		return termDao.getTermBySupplierAccount(supplierAccountId, user.getServiceLeaseKeyId());
	}

	/**
	 * Get term by customer account.
	 * @param customerAccountId The customer account id.
	 * @param user The current logged user.
	 * @return The term.
	 */
	public Term getTermByCustomerAccount (int customerAccountId, User user){
		return termDao.getTermByCustomerAccount(customerAccountId, user.getServiceLeaseKeyId());
	}

	/**
	 * Get the term object
	 * @param termId The term id.
	 * @return The term object.
	 */
	public Term getTerm(int termId) {
		return termDao.get(termId);
	}

	/**
	 * Check if the term is unique.
	 * @param term The object.
	 * @return True if unique, otherwise false.
	 */
	public boolean isUniqueTerm(Term term) {
		if(term.getId() == 0)
			return termDao.isUnique(term);
		Term oldTerm = termDao.get(term.getId());
		if(term.getName().trim().equalsIgnoreCase(oldTerm.getName().trim()))
			return true;
		return termDao.isUnique(term);
	}

	/**
	 * Save the term.
	 * @param term The term object.
	 * @param user The logged user.
	 */
	public void saveTerm(Term term, User user) {
		boolean isNewRecord = term.getId() == 0 ? true : false;
		term.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		AuditUtil.addAudit(term, new Audit(user.getId(), isNewRecord, new Date ()));
		term.setName(term.getName().trim());
		term.setDays(term.getDays());
		termDao.saveOrUpdate(term);
	}

	/**
	 * Get  filtered term.
	 * @param name The term name.
	 * @param days The term days.
	 * @param status Active, otherwise inactive.
	 * @param pageNumber The page number.
	 * @return The page result.
	 */
	public Page<Term> getTerm(String name, Integer days, int status, int pageNumber){
		return termDao.searchTerm(name, days, status, new PageSetting(pageNumber, 25));
	}

	/**
	 * Get the Term using the name.
	 * @param name The name of the term.
	 * @return The {@link Term}
	 */
	public Term getTermByName(String name) {
		if(name == null) {
			return null;
		}
		return termDao.getTermByName(name.trim());
	}

	/**
	 * Get the Term.
	 * @return The {@link Term}
	 */
	public List<Term> getAllTerms() {
		return termDao.getAllTerms();
	}

	/**
	 * Get the list of terms.
	 * @param termId The unique id of term.
	 * @return List of terms.
	 */
	public List<Term> getTerms ( Integer termId) {
		return termDao.getTerms(termId);
	}
}
