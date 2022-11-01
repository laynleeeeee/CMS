package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.domain.hibernate.ArMiscellaneousLine;
import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArMiscellaneousLineService;
import eulap.eb.service.ArMiscellaneousService;
import eulap.eb.service.ArMiscellaneousTypeService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.ReceiptMethodService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.validator.ArMiscellaneousValidator;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Ar Miscellaneous controller.

 *
 */
@Controller
@RequestMapping ("/arMiscellaneous")
public class ArMiscellaneousController {
	private final Logger logger = Logger.getLogger(ArMiscellaneousController.class);
	@Autowired
	private ArMiscellaneousTypeService arMiscellaneousTypeService;
	@Autowired
	private ArMiscellaneousService arMiscellaneousService;
	@Autowired
	private ReceiptMethodService receiptMethodService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private ArMiscellaneousLineService arMiscellaneousLineService;
	@Autowired
	private ArMiscellaneousValidator arMiscellaneousValidator;
	@Autowired
	private EBFormServiceHandler eBFormServiceHandler;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private CurrencyService currencyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{divisionId}/form/new", method = RequestMethod.GET)
	public String showArReceiptForm (@PathVariable(value="divisionId") int divisionId,
			@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ArMiscellaneous arMiscellaneous = new ArMiscellaneous();
		arMiscellaneous.setDivisionId(divisionId);
		arMiscellaneous.setDivision(divisionService.getDivision(divisionId));
		if (pId != null){
			arMiscellaneous = arMiscellaneousService.getArMiscellaneous(pId);
			Integer ebObjectId = arMiscellaneous.getEbObjectId();
			if (ebObjectId != null) {
				arMiscellaneous.setReferenceDocuments(refDocumentService.processReferenceDocs(ebObjectId));
			}
			loadAttributes(arMiscellaneous, pId, model);
		} else {
			Date currentDate = new Date();
			arMiscellaneous.setReceiptDate(currentDate);
			arMiscellaneous.setMaturityDate(currentDate);
			arMiscellaneous.setArMiscellaneousLines(new ArrayList<ArMiscellaneousLine>());
		}
		loadSelections(user, arMiscellaneous, model);
		model.addAttribute("arMiscellaneous", arMiscellaneous);
		if (arMiscellaneous.getDivisionId() != null) {
			return "NSBArMiscellaneousForm.jsp";
		}
		return "ArMiscellaneousFormV2.jsp";
	}
	
	private void loadAttributes (ArMiscellaneous arMiscellaneous, int pId, Model model){
		arMiscellaneous.setArMiscellaneousLines(arMiscellaneousLineService.processArLines(
				arMiscellaneous.getArMiscellaneousLines(), arMiscellaneous.getCompanyId()));
		model.addAttribute("pId", pId);
		model.addAttribute("sequenceNo", arMiscellaneous.getSequenceNo());
		model.addAttribute("receiptNo", arMiscellaneous.getReceiptNumber());
	}

	private void loadSelections (User user, ArMiscellaneous arMiscellaneous, Model model) {
		arMiscellaneous.serializeArMiscellaneousLines();
		arMiscellaneous.serializeReferenceDocuments();
		model.addAttribute("arMiscellaneousTypes", arMiscellaneousTypeService.getArMiscellaneousTypes());
		int companyId = arMiscellaneous.getCompanyId() != null ? arMiscellaneous.getCompanyId() : 0;
		model.addAttribute("receiptMethods", receiptMethodService.getReceiptMethodByCompanyAndDivision(
				companyId, arMiscellaneous.getDivisionId()));
		model.addAttribute("arCustomers", arCustomerService.getArCustomers(user));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(arMiscellaneous.getCurrencyId()));
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
	}

	@RequestMapping (value = "/getArCustomerAccounts", method = RequestMethod.GET)
	public @ResponseBody String getArCustomerAccounts (
			@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="divisionId") Integer divisionId,
			@RequestParam (value="arCustomerId") Integer arCustomerId,
			@RequestParam (value="receiptMethodId") Integer receiptMethodId,
			@RequestParam (value="arCustomerAccountId", required=false) Integer arCustomerAccountId){
		ReceiptMethod receiptMethod = receiptMethodService.getReceiptMethod(receiptMethodId);
		if (divisionId == null) {
			companyId = receiptMethod != null ? receiptMethod.getCompanyId() : null;
		}
		List<ArCustomerAccount> arCustomerAccounts = arCustomerAcctService.getArCustomerAccounts(arCustomerId,
				companyId, divisionId, arCustomerAccountId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arCustomerAccounts, jConfig);
		return jsonArray.toString();
	}

	private String loadArMiscellaneous(ArMiscellaneous arMiscellaneous, User user, Model model){
		loadSelections(user, arMiscellaneous, model);
		arMiscellaneous.serializeArMiscellaneousLines();
		arMiscellaneous.serializeReferenceDocuments();
		if (arMiscellaneous.getDivisionId() != null) {
			return "NSBArMiscellaneousForm.jsp";
		}
		return "ArMiscellaneousFormV2.jsp";
	}

	@RequestMapping (method = RequestMethod.POST)
	public String saveArMiscellaneous (@ModelAttribute ("arMiscellaneous") ArMiscellaneous arMiscellaneous, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		arMiscellaneous.deserializeArMiscellaneousLines();
		arMiscellaneous.deserializeReferenceDocuments();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Integer companyId = null;
		if (arMiscellaneous.getDivisionId() != null) {
			companyId = arMiscellaneous.getCompanyId();
		} else {
			companyId = arMiscellaneous.getReceiptMethodId() != null ?
					receiptMethodService.getReceiptMethod(arMiscellaneous.getReceiptMethodId()).getCompanyId() : null ;
		}
		arMiscellaneous.setArMiscellaneousLines(arMiscellaneousLineService.processArLines(
				arMiscellaneous.getArMiscellaneousLines(), companyId));
		synchronized (this) {
			arMiscellaneousValidator.validate(arMiscellaneous, result);
			if (result.hasErrors()) {
				return loadArMiscellaneous(arMiscellaneous, user, model);
			}
			eBFormServiceHandler.saveForm(arMiscellaneous, user);
		}
		model.addAttribute("ebObjectId", arMiscellaneous.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value = "/form/viewForm", method = RequestMethod.GET)
	public String showForm (@RequestParam (value="pId", required = false)Integer pId, Model model) throws IOException {
		logger.info("Retreiving AR Miscellaneous form");
		ArMiscellaneous arMiscellaneous = null;
		boolean hasReference = true;
		if (pId != null) {
			logger.debug("Retreive AR Miscellaneous form id " + pId);
			arMiscellaneous = arMiscellaneousService.getArMiscellaneous(pId);
			arMiscellaneous.setArMiscellaneousLines(arMiscellaneousLineService.processArLines(
					arMiscellaneous.getArMiscellaneousLines(), arMiscellaneous.getCompanyId()));
			Integer ebObjectId = arMiscellaneous.getEbObjectId();
			if (ebObjectId != null) {
				arMiscellaneous.setReferenceDocuments(refDocumentService.processReferenceDocs(ebObjectId));
			}
		} else {
			logger.error("AR Miscellaneous form id is required.");
			throw new RuntimeException("AR Miscellaneous form id is required.");
		}
		if (arMiscellaneous.getReferenceDocuments().isEmpty()) {
			hasReference = false;
		}
		model.addAttribute("hasReference", hasReference);
		model.addAttribute("arMiscellaneous", arMiscellaneous);
		logger.info("Successfully loaded AR Miscellaneous form");
		if (arMiscellaneous.getDivisionId() != null) {
			return "NSBArMiscellaneousView.jsp";
		}
		return "ArMiscellaneousFormView.jsp";
	}

	@RequestMapping(value="/{divisionId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchAPInvoicesWithDiv(@PathVariable(value="divisionId") int divisionId,
			@RequestParam(required=true, value="criteria", defaultValue="")
	String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = arMiscellaneousService.search(user, criteria, divisionId);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
