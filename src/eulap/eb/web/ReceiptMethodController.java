package eulap.eb.web;

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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ReceiptMethodService;
import eulap.eb.validator.ReceiptMethodValidator;

/**
 * Controller for receipt methods

 */
@Controller
@RequestMapping ("/admin/receiptMethods")
public class ReceiptMethodController {
	@Autowired
	private ReceiptMethodService receiptMethodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private ReceiptMethodValidator receiptMethodValidator;

	@RequestMapping(method = RequestMethod.GET)
	public String showForm (Model model,HttpSession session) {
		loadSelections(CurrentSessionHandler.getLoggedInUser(session), model, 0);
		model.addAttribute("status", SearchStatus.getSearchStatus());
		return "ReceiptMethod.jsp";
	}

	@RequestMapping (value = "/form", method = RequestMethod.GET)
	public String showReceiptMethodForm (@RequestParam (value="pId", required = false)
			Integer pId, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ReceiptMethod receipt = new ReceiptMethod();
		receipt.setActive(true);
		int companyId = 0;
		if (pId != null) {
			receipt = receiptMethodService.getReceiptMethod(pId);
			companyId = receipt.getCompanyId();
		}
		loadSelections(user, model, companyId);
		model.addAttribute("receiptMethod", receipt);
		return "ReceiptMethodForm.jsp";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchReceiptMethod(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="bankAccountId", required=false) Integer bankAccountId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") Integer pageNumber, HttpSession session, Model model) {
		Page<ReceiptMethod> receiptMethods = receiptMethodService.searchReceiptMethod(name,
				companyId, bankAccountId, status, pageNumber);
		model.addAttribute("receiptMethods", receiptMethods);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "ReceiptMethodTable.jsp";
	}

	private void loadSelections (User user, Model model, int companyId) {
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		model.addAttribute("bankAccounts", bankAccountService.getBankAccountsByUser(user, true));
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String saveReceipt (@ModelAttribute ("receiptMethod") ReceiptMethod receiptMethod, BindingResult result,
				Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		synchronized (this) {
			receiptMethodValidator.validate(receiptMethod, result, receiptMethod.getId());
			if (result.hasErrors()) {
				loadSelections(user, model, receiptMethod.getCompanyId());
				return "ReceiptMethodForm.jsp";
			}
			receiptMethodService.saveReceiptMethod(receiptMethod, user);
		}
		return "successfullySaved";
	}
}
