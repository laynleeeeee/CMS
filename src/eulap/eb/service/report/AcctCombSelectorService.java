package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountService;

/**
 * A class that handles the business logic of account combination selections.

 *
 */
@Service
public class AcctCombSelectorService {
	@Autowired
	private AccountCombinationDao acctCombinationDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountService accountService;

	public List<Company> getCompanies (User user) {
		return new ArrayList<Company>(acctCombinationDao.getCompanyByAcctCombination(true, user));
	}

	public List<Division> getDivisions (Company company) {
		return new ArrayList<Division>(divisionDao.getDivisionByAcctCombination(company.getId()));
	}

	public List<Account> getAccounts (Company company, Division division) {
		return accountService.getAccounts(company.getId(), division.getId());
	}
}
