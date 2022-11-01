package eulap.eb.web;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

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
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.TermService;
import eulap.eb.validator.ArCustomerAcctValidator;

/**
 * Controller class that will handle the requests for AR Customer Account.

 *
 */
@Controller
@RequestMapping("/admin/arCustomerAccount")
public class ArCustomerAcctController {
	@Autowired
	private TermService termService;
	@Autowired
	private AccountCombinationService acService;
	@Autowired
	private ArCustomerAcctService customerAcctService;
	@Autowired
	private ArCustomerAcctValidator customerAcctValidator;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm (Model model,HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		loadSelections(user, model, null, false, 0);
		return "ArCustomerAcct.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ArCustomerAccount arCustomerAcct = new ArCustomerAccount();
		arCustomerAcct.setActive(true);
		boolean hasAcctCombi = false;
		int companyId = 0;
		if(pId != null) {
			hasAcctCombi = true;
			arCustomerAcct = customerAcctService.getAccount(pId);
			companyId = arCustomerAcct.getCompanyId();
		}
		loadSelections(user, model, arCustomerAcct, hasAcctCombi, companyId);
		model.addAttribute("arCustomerAcct", arCustomerAcct);
		return "ArCustomerAcctForm.jsp";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchCustomers(@RequestParam(value="customerName", required=false) String customerName,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="customerAcctName", required=false) String customerAcctName,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="termId", required=false) Integer termId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") Integer pageNumber, HttpSession session, Model model) {
		Page<ArCustomerAccount> arCustomerAccts =
				customerAcctService.searchCustomerAccts(divisionId, customerName, customerAcctName, companyId, termId, status, pageNumber);
		model.addAttribute("arCustomerAccts", arCustomerAccts);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "ArCustomerAccountTable.jsp";
	}

	private void loadSelections (User user, Model model, ArCustomerAccount account, boolean hasAcctCombi, int companyId) {
		int customerId = 0;
		Integer transLineId = 0;
		AccountCombination acctCombi = new AccountCombination();
		if(hasAcctCombi) {
			Integer aCId = account.getDefaultDebitACId();
			if(aCId == null) {
				//Get the saved account combination Id.
				account = customerAcctService.getAccount(account.getId());
				aCId = account.getDefaultDebitACId();
			}
			acctCombi = acService.getAccountCombination(aCId);
			customerId = account.getArCustomerId();
			transLineId = account.getDefaultTransactionLineId();
			if(account.getDefaultWithdrawalSlipACId() != null) {
				AccountCombination withdrawalSlipAC = acService.getAccountCombination(account.getDefaultWithdrawalSlipACId());
				account.setWithdrawalSlipDivisionId(withdrawalSlipAC.getDivisionId());
				account.setWithdrawalAccountId(withdrawalSlipAC.getAccountId());
			}
		}
	

		model.addAttribute("transLineId", transLineId);
		model.addAttribute("acctCombi", acctCombi);
		//AR Customers
		Collection<ArCustomer> arCustomers = customerAcctService.arCustomers(user, customerId);
		model.addAttribute("arCustomers", arCustomers);
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

	@RequestMapping(value="loadArLines", method=RequestMethod.GET)
	public @ResponseBody String loadArLines(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="arLineId", required=false) Integer arLineId, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection<ArLineSetup> arLines = customerAcctService.getArLines(companyId, arLineId, user);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arLines, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveAccount(@ModelAttribute("arCustomerAcct") ArCustomerAccount account,
			BindingResult result, Model model, HttpSession session) {
		boolean hasAcctCombi = true;
		User user = CurrentSessionHandler.getLoggedInUser(session);
		customerAcctValidator.validate(account, result);
		if(result.hasErrors()) {
			if(account.getId() == 0) {
				hasAcctCombi = false;
			}
			loadSelections(user, model, account, hasAcctCombi, account.getCompanyId());
			return "ArCustomerAcctForm.jsp";
		}
		customerAcctService.saveCustomerAcct(account, user);
		return "successfullySaved";
	}
}
