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
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.ArLineSetupService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.validator.ArLineSetupValidator;
/**
 * Controller class that will handle requests of AR Line Setup.

 */
@Controller
@RequestMapping("/admin/arLineSetup")
public class ArLineSetupController {
	@Autowired
	private ArLineSetupService lineSetupService;
	@Autowired
	private AccountCombinationService acService;
	@Autowired
	private ArLineSetupValidator lineSetupValidator;
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
		loadCompanies(user, 0, model);
		return "ArLineSetup.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		AccountCombination acctCombi = new AccountCombination();
		AccountCombination discAC = new AccountCombination();
		ArLineSetup arLineSetup = new ArLineSetup();
		arLineSetup.setActive(true);
		int companyId = 0;
		if(pId != null) {
			arLineSetup = lineSetupService.getLineSetup(pId);
			acctCombi = loadAcctCombi(arLineSetup);
			discAC = arLineSetup.getDiscAccountCombination();
			companyId = acctCombi.getCompanyId();
			setEditValues(companyId, acctCombi.getDivisionId(), acctCombi.getAccountId(), discAC, model);
		} else {
			loadCompanies(user, companyId, model);
		}
		model.addAttribute("acctCombi", acctCombi);
		model.addAttribute("discAC", discAC);
		model.addAttribute("arLine", arLineSetup);
		return "ArLineSetupForm.jsp";
	}

	private void setEditValues(Integer companyId, Integer divisionId, Integer accountId,
			AccountCombination discAC, Model model) {
		String companyNumberAndName = companyService.getCompany(companyId).getNumberAndName();
		String divisionName = divisionService.getDivision(divisionId).getNumberAndName();
		Account account = accountService.getAccount(accountId);
		String accountNumber = account.getNumber();
		String accountName = account.getAccountName();
		model.addAttribute("companyNumberAndName", companyNumberAndName);
		model.addAttribute("divisionName", divisionName);
		model.addAttribute("accountName", accountNumber + " - " + accountName);
		if (discAC != null) {
			model.addAttribute("discAccName", discAC.getAccount().getNumberAndName());
		}
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchSuppliers(@RequestParam(value="setupName", required=false) String setupName,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") Integer pageNumber, HttpSession session, Model model) {
		Page<ArLineSetup> arLineSetups = 
				lineSetupService.searchArLineSetup(setupName, companyId, status, pageNumber);
		model.addAttribute("arLines", arLineSetups);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "ArLineSetupTable.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveArLine(@ModelAttribute("arLine") ArLineSetup arLine,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		lineSetupValidator.validate(arLine, result);
		if(result.hasErrors()) {
			if(arLine.getCompanyId() != null) {
				loadCompanies(user, arLine.getCompanyId(), model);
			}
			if(arLine.getId() != 0) {
				AccountCombination acctCombi = acService.getAccountCombination(arLine.getAccountCombinationId());
				AccountCombination discAC = acService.getAccountCombination(arLine.getDiscountACId());
				setEditValues(acctCombi.getCompanyId(), acctCombi.getDivisionId(), acctCombi.getAccountId(), discAC, model);
			}
			model.addAttribute("arLine", arLine);
			return "ArLineSetupForm.jsp";
		}
		lineSetupService.saveArLineSetup(user, arLine);
		return "successfullySaved";
	}

	private AccountCombination loadAcctCombi(ArLineSetup lineSetup) {
		int aCId = lineSetup.getAccountCombinationId();
		AccountCombination acctCombi = acService.getAccountCombination(aCId);
		return acctCombi;
	}

	private void loadCompanies(User user, int companyId, Model model) {
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, companyId);
		model.addAttribute("companies", companies);
	}

	@RequestMapping(value="/loadDivisions", method=RequestMethod.GET)
	public @ResponseBody String loadDivisions(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId) {
		Collection<Division> divisions = divisionService.getDivisions(companyId, divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/loadAccounts", method=RequestMethod.GET)
	public @ResponseBody String loadAccounts(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId") int divisionId,
			@RequestParam(value="accountId", required=false) Integer accountId) {
		Collection<Account> accounts = accountService.getAccounts(companyId, divisionId, accountId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(accounts, jConfig);
		return jsonArray.toString();
	}
}
