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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ServiceSettingService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
/**
 * Controller class that will handle requests of Service settings.

 */
@Controller
@RequestMapping("/admin/service")
public class ServiceSettingController {
	@Autowired
	private AccountCombinationService acService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ServiceSettingService serviceSettingService;
	@Autowired
	private AccountCombinationService acctCombiService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm (Model model,HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadSelections(user, 0, 0, model);
		return "Service.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		AccountCombination acctCombi = new AccountCombination();
		ServiceSetting service = new ServiceSetting();
		int companyId = 0;
		if(pId != null) {
			service = serviceSettingService.getServiceSetting(pId);
			acctCombi = acctCombiService.getAccountCombination(service.getAccountCombinationId());
			companyId = acctCombi.getCompanyId();
			setEditValues(companyId, acctCombi.getDivisionId(), acctCombi.getAccountId(), model);
		} else {
			loadSelections(user, companyId, 0, model);
			service.setActive(true);
		}
		model.addAttribute("acctCombi", acctCombi);
		model.addAttribute("service", service);
		return "ServiceForm.jsp";
	}

	private void setEditValues(Integer companyId, Integer divisionId, Integer accountId, Model model) {
		String companyNumberAndName = companyService.getCompany(companyId).getNumberAndName();
		String divisionName = divisionService.getDivision(divisionId).getNumberAndName();
		Account account = accountService.getAccount(accountId);
		String accountNumber = account.getNumber();
		String accountName = account.getAccountName();
		model.addAttribute("companyNumberAndName", companyNumberAndName);
		model.addAttribute("divisionName", divisionName);
		model.addAttribute("accountName", accountNumber + " - " + accountName);
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchSuppliers(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") Integer pageNumber, HttpSession session, Model model) {
		Page<ServiceSetting> services =  serviceSettingService.searchServiceSettings(name, 
					companyId, divisionId, status, pageNumber);
		model.addAttribute("services", services);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "ServiceTable.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveArLine(@ModelAttribute("service") ServiceSetting service,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		serviceSettingService.validate(service, result);
		if(result.hasErrors()) {
			if(service.getCompanyId() != null) {
				loadSelections(user, service.getCompanyId(), service.getDivisionId(), model);
			}
			if(service.getId() != 0) {
				AccountCombination acctCombi = acService.getAccountCombination(service.getAccountCombinationId());
				setEditValues(acctCombi.getCompanyId(), acctCombi.getDivisionId(), acctCombi.getAccountId(), model);
			}
			return "ServiceForm.jsp";
		}
		serviceSettingService.save(user, service);
		return "successfullySaved";
	}

	private void loadSelections(User user, int companyId, int divisionId, Model model) {
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, companyId);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, divisionId));
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
