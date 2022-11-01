package eulap.eb.web.form.inv;

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
import eulap.eb.domain.hibernate.InventoryAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.InventoryAccountService;
import eulap.eb.validator.inv.InventoryAccountValidator;

/**
 * Controller class for Inventory Account.

 *
 */
@Controller
@RequestMapping("/admin/inventoryAccount")
public class InventoryAccountController {
	private static Logger logger = Logger.getLogger(InventoryAccountController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private InventoryAccountService iaService;
	@Autowired
	private InventoryAccountValidator iaValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method=RequestMethod.GET)
	public String loadAllInvAccts(HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadCompanies(model, user, 0);
		loadInvAccts(-1, -1, 1, model);
		logger.info("Loading the main page of Inventory Forms Account Setup.");
		return "InventoryAccount.jsp";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchInvAccts(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="statusId") int statusId,
			@RequestParam(value="page") int pageNumber,
			Model model) {
		loadInvAccts(companyId, statusId, pageNumber, model);
		logger.info("Loading the table of Inventory Forms Account Setup.");
		return "InventoryAccountTable.jsp";
	}

	private void loadInvAccts(int companyId, int statusId, int pageNumber, Model model) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		Page<InventoryAccount> invAccts = iaService.searchInvAccts(companyId, statusId, pageNumber);
		model.addAttribute("inventoryAccts", invAccts);
		logger.debug("Retrieved "+invAccts.getTotalRecords()+" Inventory Accounts.");
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showIAForm(@RequestParam(value="inventoryAcctId", required=false) Integer inventoryAcctId,
			HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		InventoryAccount inventoryAccount = new InventoryAccount();
		int companyId = 0;
		if(inventoryAcctId != null) {
			logger.info("Editing the Inventory Account form with id: "+inventoryAcctId);
			inventoryAccount = iaService.getInventoryAcct(inventoryAcctId);
			companyId = inventoryAccount.getCompanyId();
		} else {
			inventoryAccount.setActive(true);
		}
		loadCompanies(model, user, companyId);
		logger.info("Loading the Inventory Forms Account Setup.");
		model.addAttribute("inventoryAccount", inventoryAccount);
		return "InventoryAccountForm.jsp";
	}

	private void loadCompanies(Model model, User user, int companyId) {
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveInventoryAccount(@ModelAttribute("inventoryAccount") InventoryAccount inventoryAccount,
			BindingResult result, HttpSession session, Model model) {
		logger.info("Saving the Inventory Forms Account Setup.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		iaValidator.validate(inventoryAccount, result);
		if(result.hasErrors()) {
			loadCompanies(model, user, inventoryAccount.getCompanyId());
			//Reset account combination
			model.addAttribute("inventoryAccount", inventoryAccount);
			return "InventoryAccountForm.jsp";
		}
		iaService.saveInventoryAccount(inventoryAccount, user);
		logger.info("Successfully saved the inventory account id: "+inventoryAccount.getId());
		return "successfullySaved";
	}
}
