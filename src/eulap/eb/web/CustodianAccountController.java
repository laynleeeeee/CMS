package eulap.eb.web;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CustodianAccountService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.TermService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controller class that will handle the requests for Custodian Account.

 *
 */
@Controller
@RequestMapping("/admin/custodianAccount")
public class CustodianAccountController {
	@Autowired
	private TermService termService;
	@Autowired
	private AccountCombinationService acService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private CustodianAccountService custodianAccountService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm (Model model,HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadSelections(user, model, null, false, 0);
		return "CustodianAccount.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		CustodianAccount custodianAccount = new CustodianAccount();
		custodianAccount.setActive(true);
		boolean hasAcctCombi = false;
		int companyId = 0;
		if(pId != null) {
			hasAcctCombi = true;
			custodianAccount = custodianAccountService.getCustodianAccount(pId);
			companyId = custodianAccount.getCompanyId();
		}
		loadSelections(user, model, custodianAccount, hasAcctCombi, companyId);
		model.addAttribute("custodianAccount", custodianAccount);
		return "CustodianAccountForm.jsp";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchCustomers(@RequestParam(value="custodianName", required=false) String custodianName,
			@RequestParam(value="custodianAccountName", required=false) String custodianAccountName,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="termId", required=false) Integer termId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") Integer pageNumber, HttpSession session, Model model) {
		Page<CustodianAccount> custodianAccounts =
				custodianAccountService.searchCustodianAccounts(custodianName, custodianAccountName, companyId, termId, status, pageNumber);
		model.addAttribute("custodianAccounts", custodianAccounts);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "CustodianAccountTable.jsp";
	}

	private void loadSelections (User user, Model model, CustodianAccount custodianAccount, boolean hasAcctCombi, int companyId) {
		AccountCombination acctCombi = new AccountCombination();
		if(hasAcctCombi) {
			Integer aCId = custodianAccount.getCdAccountCombinationId();
			if(aCId == null) {
				custodianAccount = custodianAccountService.getCustodianAccount(custodianAccount.getId());
			}
			acctCombi = acService.getAccountCombination(aCId);
			if(custodianAccount.getFdAccountCombinationId() != null) {
				AccountCombination fdACId = acService.getAccountCombination(custodianAccount.getFdAccountCombinationId());
				custodianAccount.setFdDivisionId(fdACId.getDivisionId());
				custodianAccount.setFdAccountId(fdACId.getAccountId());
			}
		}
		model.addAttribute("acctCombi", acctCombi);
		//Companies
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		//Term
		List<Term> terms = termService.getTerms(user);
		model.addAttribute("terms", terms);
	}

	@RequestMapping(value="/loadDivisions", method=RequestMethod.GET)
	public @ResponseBody String loadDivisions(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId") int divisionId) {
		Collection<Division> divisions = divisionService.getDivisions(companyId, divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/loadAccounts", method=RequestMethod.GET)
	public @ResponseBody String loadAccounts(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId") int divisionId, @RequestParam(value="accountId") int accountId) {
		Collection<Account> accounts = accountService.getAccounts(companyId, divisionId, accountId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(accounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveAccount(@ModelAttribute("custodianAccount") CustodianAccount custodianAccount,
			BindingResult result, Model model, HttpSession session) {
		boolean hasAcctCombi = true;
		User user = CurrentSessionHandler.getLoggedInUser(session);
		custodianAccountService.validate(custodianAccount, result);
		if(result.hasErrors()) {
			if(custodianAccount.getId() == 0) {
				hasAcctCombi = false;
			}
			loadSelections(user, model, custodianAccount, hasAcctCombi, custodianAccount.getCompanyId());
			return "CustodianAccountForm.jsp";
		}
		custodianAccountService.saveCustodianAccount(custodianAccount, user);
		return "successfullySaved";
	}
}
