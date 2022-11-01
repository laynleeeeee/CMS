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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.ApLineSetup;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.ApLineSetupService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.validator.ApLineSetupValidator;

/**
 * Controller class that will handle requests of AP Line Setup.

 */

@Controller
@RequestMapping("/admin/apLineSetup")
public class ApLineSetupController {
	private Logger logger = Logger.getLogger(ApLineSetupController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ApLineSetupService apLineSetupService;
	@Autowired
	private ApLineSetupValidator apLineSetupValidator;
	@Autowired
	private AccountCombinationService acctCombiService;
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
		loadCompanies(user, model, 0);
		loadApLineSetups(-1, "", SearchStatus.All.name(), PageSetting.START_PAGE, model);
		return "ApLineSetup.jsp";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchApLineSetups(@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="name") String name,
			@RequestParam(value="status") String status,
			@RequestParam(value="pageNumber") Integer pageNumber,
			Model model) {
		loadApLineSetups(companyId, name, status, pageNumber, model);
		return "ApLineSetupTable.jsp";
	}

	private void loadApLineSetups(Integer companyId, String name, String status, Integer pageNumber, Model model) {
		Page<ApLineSetup> apLineSetups = apLineSetupService.searchApLineSetups(companyId, name, status, pageNumber);
		model.addAttribute("apLineSetups", apLineSetups);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	private void loadCompanies(User user, Model model, Integer companyId) {
		if(companyId != null) {
			model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		}
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="apLineSetupId", required=false) Integer apLineSetupId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ApLineSetup apLineSetup = new ApLineSetup();
		int companyId = 0;
		if(apLineSetupId != null) {
			logger.info("Editing the AP Line Setup with id: "+apLineSetupId);
			apLineSetup = apLineSetupService.getApLineSetup(apLineSetupId);
			companyId = apLineSetup.getCompanyId();
			setEditValues(companyId, apLineSetup.getDivisionId(), apLineSetup.getAccountId(), model);
		} else {
			logger.info("Showing the AP Line Setup form.");
			apLineSetup.setActive(true);
		}
		model.addAttribute("apLineSetup", apLineSetup);
		loadCompanies(user, model, companyId);
		return "ApLineSetupForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveArLine(@ModelAttribute("apLineSetup") ApLineSetup apLineSetup,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		synchronized (this) {
			apLineSetupValidator.validate(apLineSetup, result);
			Integer acctCombiId = apLineSetup.getAccountCombinationId();
			if (result.hasErrors()) {
				loadCompanies(user, model, apLineSetup.getCompanyId());
				if (acctCombiId != null) {
					apLineSetup.setAccountCombination(acctCombiService.getAccountCombination(acctCombiId));
					if (apLineSetup.getId() != 0) {
						setEditValues(apLineSetup.getCompanyId(), apLineSetup.getDivisionId(), apLineSetup.getAccountId(), model);
					}
				}
				return "ApLineSetupForm.jsp";
			}
			apLineSetupService.saveApLineSetup(user, apLineSetup);
		}
		return "successfullySaved";
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
	
}
