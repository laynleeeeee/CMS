package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.SupplierAccountService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.TermService;
import eulap.eb.validator.SupplierAccountValidator;

/**
 * A controller class that handles the adding and editing of supplier's account.

 *
 */
@Controller
@RequestMapping ("/admin/supplierAccount")
public class SupplierAccountFormController {
	private Logger logger = Logger.getLogger(SupplierAccountFormController.class);
	@Autowired
	private SupplierAccountService supplierAccountService;
	@Autowired
	private SupplierAccountValidator supplierAccountValidator;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TermService termService;

	@RequestMapping(method = RequestMethod.GET)
	public String showForm (Model model, HttpSession session) {
		logger.info("Showing the Supplier Account module.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user, model, null, 0);
		return "SupplierAccount.jsp";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchSuppliers(@RequestParam(value="supplierName", required=false) String supplierName,
			@RequestParam(value="supplierAcctName", required=false) String supplierAcctName,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="termId", required=false) Integer termId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") Integer pageNumber, HttpSession session, Model model) {
		logger.info("Searching for supplier accounts.");
		Page<SupplierAccount> suppliersAccounts = 
			supplierAccountService.searchSupplierAccounts(supplierName, supplierAcctName, companyId, termId, status, pageNumber);
		model.addAttribute("supplierAccounts", suppliersAccounts);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		logger.info("Retrieved "+suppliersAccounts.getTotalRecords()+" supplier accounts.");
		return "SupplierAccountTable.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String showForm (@RequestParam (value="pId", required = false)Integer pId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		SupplierAccount supplierAccount = new SupplierAccount();
		int companyId = 0;
		if (pId != null) {
			logger.info("Editing the Supplier Account with ID: "+pId);
			supplierAccount = supplierAccountService.getSupplierAccount(pId);
			companyId = supplierAccount.getCompanyId();
		} else {
			logger.info("Showing the Supplier Account form.");
			supplierAccount.setActive(true);
		}
		loadSelections(user, model, pId, companyId);
		model.addAttribute("supplierAccount", supplierAccount);
		return "SupplierAccountForm.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String submit (@ModelAttribute ("supplierAccount") SupplierAccount supplierAccount, BindingResult result, 
			Model model, HttpSession session) {
		logger.info("Saving the supplier account.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		String trimmedName = supplierAccount.getName() != null ? supplierAccount.getName().trim() : "";
		supplierAccount.setTrimmedName(StringFormatUtil.removeExtraWhiteSpaces(trimmedName, null));
		supplierAccountValidator.validate(supplierAccount, result);
		if (result.hasErrors()) {
			logger.info("Form has error/s. Reloading the supplier account form.");
			loadSelections(user, model, supplierAccount.getId(), supplierAccount.getCompanyId());
			return "SupplierAccountForm.jsp";
		}

		supplierAccountService.saveSupplierAccount(supplierAccount, user);
		logger.info("Successfully saved the supplier account.");
		return "successfullySaved";
	}

	private void loadSelections (User user, Model model, Integer supplierAcctId, Integer companyId) {
		//Suppliers
		List<Supplier> suppliers = supplierService.getSuppliers(user, supplierAcctId);
		model.addAttribute("suppliers", suppliers);
		//Companies
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		//Term
		List<Term> terms = termService.getTerms(user);
		model.addAttribute("terms", terms);
		//Status
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
	}
}
