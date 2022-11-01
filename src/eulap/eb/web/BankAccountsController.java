package eulap.eb.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.validator.BankAccountValidator;

/**
 * A controller for bank accounts.

 */

@Controller
@RequestMapping("/admin/bankAccounts")
public class BankAccountsController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private BankAccountValidator bankAccountValidator;

	@RequestMapping(method = RequestMethod.GET)
	public String showBankAccounts (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user, model, 0);
		loadBankAccounts("", -1, -1, 1, model);
		return "BankAccount.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String addBankAccountForm (Model model, HttpSession session) {
		BankAccount bankAccount = new BankAccount();
		bankAccount.setActive(true);
		return loadBankAccountForm(bankAccount, model, session, 0);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET, params={"bankAccountId"})
	public String editBankAccountForm (
			@RequestParam("bankAccountId") Integer bankAccountId,
			Model model, HttpSession session) {
		BankAccount bankAccount = bankAccountService.getBankAccount(bankAccountId);
		int companyId = bankAccount.getCompanyId();
		return loadBankAccountForm(bankAccount, model, session, companyId);
	}

	@RequestMapping(method = RequestMethod.GET, params={"name", "companyId", "status", "pageNumber"})
	public String searchBankAccounts (@RequestParam("name") String name,
			@RequestParam("companyId") int companyId,
			@RequestParam("status") int status,
			@RequestParam("pageNumber") int pageNumber,
			Model model) {
		loadBankAccounts(name, companyId, status, pageNumber, model);
		return "BankAccountTable.jsp";
	}

	private String loadBankAccountForm (BankAccount bankAccount, Model model, HttpSession session, int companyId ) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user, model, companyId);
		model.addAttribute("banks", bankAccountService.getBanks(bankAccount.getBankId()));
		model.addAttribute("bankAccount", bankAccount);
		return "BankAccountForm.jsp";
	}

	private void loadBankAccounts (String name, int companyId, int status, int pageNumber, Model model) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("bankAccounts", bankAccountService.getBankAccounts(name, companyId, status, pageNumber));
	}

	private void loadSelections (User user, Model model, int companyId) {
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String submit (@ModelAttribute ("bankAccount") BankAccount bankAccount, BindingResult result,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		bankAccountValidator.validate(bankAccount, result, bankAccount.getId());
		if (result.hasErrors()) {
			return loadBankAccountForm(bankAccount, model, session, bankAccount.getCompanyId());
		}
		bankAccountService.saveBankAccount(bankAccount, user);
		return "successfullySaved";
	}
}