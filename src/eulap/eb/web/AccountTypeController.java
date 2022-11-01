package eulap.eb.web;

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

import bp.web.ar.CurrentSessionHandler;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountTypeService;
import eulap.eb.validator.AccountTypeValidator;

/**
 * Controller for account type

 *
 */
@Controller
@RequestMapping ("/admin/accountTypes")
public class AccountTypeController {
	private static final String ACCT_TYPE_ATTRIB_NAME = "accountTypes";
	@Autowired
	private AccountTypeService accountTypeService;
	@Autowired
	private AccountTypeValidator accountTypeValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showAccountTypes (Model model, HttpSession session) {
		model.addAttribute("normalBalances", accountTypeService.getNormalBalances());
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		model.addAttribute(ACCT_TYPE_ATTRIB_NAME,
				accountTypeService.getAccountTypes(CurrentSessionHandler.getLoggedInUser(session)));
		return "showAccountTypes";
	}

	@RequestMapping (method = RequestMethod.GET, params = {"accountTypeName", "normalBalanceId", "pageNumber"})
	public String searchAccountTypes (@RequestParam String accountTypeName, @RequestParam int normalBalanceId,
			@RequestParam int pageNumber, Model model, HttpSession session) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		Page<AccountType> accountTypes = accountTypeService.
				searchAccountTypes(accountTypeName, normalBalanceId, pageNumber,
						CurrentSessionHandler.getLoggedInUser(session));
		model.addAttribute(ACCT_TYPE_ATTRIB_NAME, accountTypes);
		return "showAccountTypeTable";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form")
	public String addAccountType (Model model, HttpSession session) {
		AccountType accountType = new AccountType();
		accountType.setActive(true);
		return showAccountTypeForm(accountType, model, session);
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form", params = {"accountTypeId"})
	public String editAccountType (@RequestParam int accountTypeId, Model model, HttpSession session) {
		AccountType accountType = accountTypeService.getAccountType(accountTypeId);
		return showAccountTypeForm(accountType, model, session);
	}

	private String showAccountTypeForm (AccountType accountType, Model model, HttpSession session) {
		model.addAttribute("normalBalances", accountTypeService.getNormalBalances());
		model.addAttribute(accountType);
		return "showAccountTypeForm";
	}

	@RequestMapping (value = "/form", method = RequestMethod.POST)
	public String saveAccountType (@ModelAttribute("accountType") AccountType accountType, BindingResult result, Model model,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		accountType.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		accountTypeValidator.validate(accountType, result, user);
		if (result.hasErrors())
			return  showAccountTypeForm(accountType, model, session);
		accountTypeService.saveAccountType(accountType, CurrentSessionHandler.getLoggedInUser(session));
		model.addAttribute("message", "Sucessfully saved.");
		return "successfullySaved";
	}
}
