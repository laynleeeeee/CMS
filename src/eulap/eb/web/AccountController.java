package eulap.eb.web;

import java.io.InvalidClassException;
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
import eulap.common.util.ReportUtil;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountService;
import eulap.eb.validator.AccountValidator;
import eulap.eb.web.dto.AccountDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Account controller.

 *
 */
@Controller
@RequestMapping ("/admin/accounts")
public class AccountController {
	private static final String ACCT_ATTRIB_NAME = "accounts";
	private static final String REPORT_TITLE = "CHART OF ACCOUNTS";
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountValidator accountValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showAccounts (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("accountTypes", accountService.getActiveAccountTypes(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		model.addAttribute(ACCT_ATTRIB_NAME, accountService.searchAccountWithSubs(-1, "", "", "All", user, PageSetting.START_PAGE));
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		return "Account.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, params = {"accountTypeId", "accountNumber", "accountName",  "pageNumber"})
	public String searchAccounts (@RequestParam int accountTypeId, @RequestParam String accountNumber,
			@RequestParam String accountName, @RequestParam int pageNumber,
			@RequestParam(value="status", required=false) String status, Model model, HttpSession session) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		Page<AccountDto> accounts = accountService.searchAccountWithSubs(accountTypeId, accountNumber, accountName, status, 
				CurrentSessionHandler.getLoggedInUser(session), pageNumber);
		model.addAttribute(ACCT_ATTRIB_NAME, accounts);
		return "AccountTable.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form")
	public String addAccount (Model model, HttpSession session) {
		Account account = new Account();
		account.setActive(true);
		return showAccountForm(account, model, session);
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form", params = {"accountId"})
	public String editAccount (@RequestParam int accountId, Model model, HttpSession session) {
		Account account = accountService.getAccount(accountId);
		return showAccountForm(account, model, session);
	}

	private String showAccountForm (Account account, Model model, HttpSession session) {
		Collection<AccountType> activeAccountTypes =
				accountService.getActiveAccountTypes(CurrentSessionHandler.getLoggedInUser(session));
		model.addAttribute("accountTypes", activeAccountTypes);
		model.addAttribute(account);
		return "AccountForm.jsp";
	}

	@ResponseBody
	@RequestMapping (method = RequestMethod.GET, params = {"accountTypeId", "accountId"})
	public String loadRelatedAccounts (@RequestParam int accountTypeId, @RequestParam int accountId,
			HttpSession session) {
		Collection<Account> accounts = accountService.getRelatedAccounts(accountTypeId, accountId,
				CurrentSessionHandler.getLoggedInUser(session));
		return getJSONString(accounts, "relatedAccount", "accountType");
	}

	private String getJSONString (Object object, String ... exclude) {
		JsonConfig jConfig = new JsonConfig();
		String [] excludes = null;
		if (exclude.length > 0) {
			excludes = new String[exclude.length];
			int i = 0;
			for (String str : exclude) {
				excludes[i++] = str;
			}
		}
		if (excludes != null)
			jConfig.setExcludes(excludes);
		JSONArray jsonArray = JSONArray.fromObject(object, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value = "/form", method = RequestMethod.POST)
	public String saveAccount (@ModelAttribute("account") Account account, BindingResult result, Model model,
			HttpSession session) {
		User user =  CurrentSessionHandler.getLoggedInUser(session);
		accountValidator.validate(account, result, user);
		if (result.hasErrors())
			return  showAccountForm(account, model, session);
		accountService.saveAccount(account, user);
		model.addAttribute("message", "Sucessfully saved.");
		return "successfullySaved";
	}

	@RequestMapping(value="/showChildren", method=RequestMethod.GET)
	public String addChildrenAccounts(@RequestParam(value="accountId") Integer accountId,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		List<AccountDto> accounts = accountService.getAllChildren(accountId);
		model.addAttribute("accounts", accounts);
		return "AddChildAccount.jsp";
	}

	@RequestMapping(value="/printChartOfAccounts", method=RequestMethod.GET)
	public String addChildrenAccounts(Model model, HttpSession session) {
		User user =  CurrentSessionHandler.getLoggedInUser(session);
		JRDataSource dataSource = new JRBeanCollectionDataSource(accountService.generateChartOfAccounts(user));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		model.addAttribute("reportTitle" , REPORT_TITLE);
		ReportUtil.getPrintDetails(model, user);
		return "ChartOfAccounts.jasper";
	}
}
