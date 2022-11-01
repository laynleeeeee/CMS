package eulap.eb.web;

import java.util.Collection;
import java.util.List;

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
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.domain.hibernate.CheckbookTemplate;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CheckbookService;
import eulap.eb.service.CheckbookTemplateService;
import eulap.eb.service.CompanyService;
import eulap.eb.validator.CheckbookValidator;

/**
 * A controller class that handles the adding of checkbooks.

 */
@Controller
@RequestMapping("/admin/checkbook")
public class CheckbookController {
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private CheckbookValidator checkbookValidator;
	@Autowired
	private CheckbookService checkbookService;
	@Autowired
	private CheckbookTemplateService checkbookTemplateService;
	@Autowired
	private CompanyService companyService;

	@RequestMapping(method = RequestMethod.GET)
	public String showCheckbooks (Model model, HttpSession session) {
		loadCheckbooks(CurrentSessionHandler.getLoggedInUser(session), -1, "", "", "", -1, 1, model);
		return "Checkbook.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String addCheckbookForm(
			Model model, HttpSession session) {
		Checkbook checkbook = new Checkbook();
		checkbook.setActive(true);
		return loadCheckbookForm(checkbook, model, session);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET, params={"checkbookId"})
	public String editCheckbookForm(@RequestParam("checkbookId") int checkbookId,
			Model model, HttpSession session) {
		Checkbook checkbook = checkbookService.getCheckbook(checkbookId);
		return loadCheckbookForm(checkbook, model, session);
	}

	@RequestMapping(method = RequestMethod.GET, params={"bankAccountName",
			"name", "checkNo", "status", "pageNumber"})
	public String searchCheckbooks (@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam("bankAccountName") String bankAccountName,
			@RequestParam("name") String name,
			@RequestParam("checkNo") String checkNo,
			@RequestParam("status") int status,
			@RequestParam("pageNumber") int pageNumber,
			Model model,
			HttpSession session) {
		loadCheckbooks(CurrentSessionHandler.getLoggedInUser(session), companyId, 
				bankAccountName, name, checkNo, status, pageNumber, model);
		return "CheckbookTable.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String submit (@ModelAttribute ("checkbook") Checkbook checkbook, BindingResult result,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		checkbookValidator.validate(checkbook, result, checkbook.getBankAccountId());
		if(result.hasErrors())
			return loadCheckbookForm(checkbook, model, session);
		checkbookService.saveCheckbook(checkbook, user);
		return "successfullySaved";
	}

	private String loadCheckbookForm (Checkbook checkbook, Model model, HttpSession session ) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user, model);
		model.addAttribute(checkbook);
		return "CheckbookForm.jsp";
	}

	private void loadSelections (User user, Model model) {
		//Bank account
		List<BankAccount> bankAccounts = bankAccountService.getBankAccouts(user);
		Collection<CheckbookTemplate> checkbookTemplates = checkbookTemplateService.getAllCheckTemplate();
		model.addAttribute("bankAccounts", bankAccounts);
		model.addAttribute("checkbookTemplates", checkbookTemplates);
	}

	private void loadCheckbooks (User user, Integer companyId, String bankAccountName, String name,
			String checkNo, int status, int pageNumber, Model model) {
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("checkBooks", checkbookService.getCheckbooks(user, companyId, bankAccountName,
				name, checkNo, status, pageNumber));
	}
}