package eulap.eb.web;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.report.AcctCombSelectorService;

/**
 * A controller class for account combination selector.

 *
 */
@Controller
@RequestMapping ("accountCombinationSelector") 
public class AcctCombSelectorController {
	@Autowired
	private AcctCombSelectorService acctCombSelectorService;
	@Autowired
	private CompanyService companyService;

	@RequestMapping(method = RequestMethod.GET)
	public String showForm (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection <Company> companies = companyService.getActiveCompanies(user, null, null, null);
		if (!companies.isEmpty()) {
			Company company = companies.iterator().next();
			List<Division> divisions = acctCombSelectorService.getDivisions(company);
			if (!divisions.isEmpty()) {
				Division division = divisions.iterator().next();
				List<Account> accounts = acctCombSelectorService.getAccounts(company, division);
				model.addAttribute("accounts", accounts);
			}
			model.addAttribute("divisions", divisions);
		}
		model.addAttribute("companies", companies);
		return "AccountCombinationSelector.jsp";
	}
}
