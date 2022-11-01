package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.validator.AccountCombinationValidator;

/**
 * Controller for Account Combination module.

 */
@Controller
@RequestMapping("/admin/accountCombinations")
public class AccountCombinationController {

	@Autowired
	private AccountCombinationService accountCombinationService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountCombinationValidator accountCombinationValidator;

	private final Logger logger = Logger.getLogger(AccountCombinationController.class);
	private static final String ACCOUNT_COMBINATION_ATTRIBUTE_NAME = "accountCombinations";
	private static final String DIVISION_ATTRIBUTE_NAME = "divisions";
	private static final String COMPANY_ATTRIBUTE_NAME = "companies";
	private static final String ACCT_ATTRIB_NAME = "accounts";

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method =  RequestMethod.GET)
	public String showAcctCombination(Model model, HttpSession session){
		model.addAttribute(ACCOUNT_COMBINATION_ATTRIBUTE_NAME,
				accountCombinationService.getAllAccountCombinations(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		return "showAccountCombination";
	}

	@ResponseBody
	@RequestMapping(method =  RequestMethod.GET, value="/loadAccountCombination", params = {"companyId", "divisionId", "accountId"})
	public String loadAccountCombination (@RequestParam int companyId, @RequestParam int divisionId,
			@RequestParam int accountId){
		return accountCombinationService.generateAccountCombination(companyId, divisionId, accountId);
	}

	@RequestMapping(method =  RequestMethod.GET, params = {"companyNumber", "divisionNumber", "accountNumber",
			"companyName", "divisionName", "accountName", "pageNumber"})
	public String searchAcctCombination(@RequestParam String companyNumber, @RequestParam String divisionNumber,
			@RequestParam String accountNumber, @RequestParam String companyName, @RequestParam String divisionName,
			@RequestParam String accountName, @RequestParam int pageNumber,
			@RequestParam(value="status", required=false, defaultValue ="All") String status, Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute(ACCOUNT_COMBINATION_ATTRIBUTE_NAME, accountCombinationService.searchAccountCombinations(
				companyNumber, divisionNumber, accountNumber, companyName, divisionName, accountName,
				CurrentSessionHandler.getLoggedInUser(session).getServiceLeaseKeyId(), pageNumber, user, status));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "showAccountCombinationTable";
	}

	@RequestMapping(value = "/form", method=RequestMethod.GET)
	public String addAccountCombination(Model model, HttpSession session){
		logger.info("Add account combination");
		AccountCombination ac = new AccountCombination();
		ac.setActive(true);
		return showAccountCombinationForm(ac, model, session );
	}

	@RequestMapping(value = "/form", method=RequestMethod.GET, params = {"accountCombinationId"})
	public String editAccountCombination(@RequestParam int accountCombinationId, Model model,
			 HttpSession session){
		logger.info("Edit account combination");
		AccountCombination ac = accountCombinationService.getAccountCombination(accountCombinationId);
		return showAccountCombinationForm(ac, model, session);
	}

	@RequestMapping(value = "/form", method=RequestMethod.POST)
	public String saveAccountCombination(@ModelAttribute AccountCombination accountCombination,
			BindingResult result, HttpSession session, Model model){
		logger.info("Edit account combination");
		accountCombinationValidator.validate(accountCombination, result);
		if (result.hasErrors())
			return showAccountCombinationForm(accountCombination, model, session);
		accountCombinationService.saveAccountCombination(accountCombination,
				CurrentSessionHandler.getLoggedInUser(session) );
		return "successfullySaved";
	}

	private String showAccountCombinationForm(AccountCombination accountCombination, Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		int companyId = accountCombination.getCompanyId() == -1 ? 0 : accountCombination.getCompanyId();
		int divisionId = accountCombination.getDivisionId() == -1 ? 0 : accountCombination.getDivisionId();
		int accountId = accountCombination.getAccountId() == -1 ? 0 : accountCombination.getAccountId();
		model.addAttribute(COMPANY_ATTRIBUTE_NAME, companyService.getCompaniesWithInactives(user, companyId));
		model.addAttribute(DIVISION_ATTRIBUTE_NAME, divisionService.getLastLevelDivisions(divisionId));
		model.addAttribute(ACCT_ATTRIB_NAME, accountService.getLastLevelAccounts(accountId));
		model.addAttribute(accountCombination);
		return "showAccountCombinationForm";
	}
}