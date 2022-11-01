package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collection;
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
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptAdvancePayment;
import eulap.eb.domain.hibernate.ArReceiptLine;
import eulap.eb.domain.hibernate.ArReceiptTransaction;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArReceiptService;
import eulap.eb.service.ArReceiptTransactionService;
import eulap.eb.service.ArReceiptTypeService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.CustomerAdvancePaymentService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.validator.ArReceiptValidator;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * Ar Receipt controller.

 *
 */
@Controller
@RequestMapping ("/arReceipt")
public class ArReceiptController {
	private final Logger logger = Logger.getLogger(ArReceiptController.class);
	@Autowired
	private ArReceiptService arReceiptService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private ArReceiptValidator arReceiptValidator;
	@Autowired
	private ArReceiptTransactionService arReceiptTransactionService;
	@Autowired
	private ArReceiptTypeService arReceiptTypeService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler eBFormServiceHandler;
	@Autowired
	private CustomerAdvancePaymentService capService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private ReferenceDocumentService refDocumentService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{divisionId}/form/new", method = RequestMethod.GET)
	public String showArReceiptForm (@PathVariable("divisionId") int divisionId,
			@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ArReceipt arReceipt = new ArReceipt();
		Date currentDate = new Date();
		arReceipt.setReceiptDate(currentDate);
		arReceipt.setMaturityDate(currentDate);
		if (pId != null){
			arReceipt = arReceiptService.getArReceiptWithArReceiptLines(pId);
			arReceipt.setReferenceDocuments(refDocumentService.processReferenceDocs(arReceipt.getEbObjectId()));
			loadAttributes(arReceipt, pId, model);
		} else {
			arReceipt.setDivisionId(divisionId);
			arReceipt.setDivision(divisionService.getDivision(divisionId));
			arReceipt.setArRTransactions(new ArrayList<ArReceiptTransaction>());
		}
		return loadForm(arReceipt, user, model);
	}

	private String loadForm(ArReceipt arReceipt, User user, Model model) {
		loadSelections(user, arReceipt, model);
		arReceipt.serializeArReceiptLines();
		arReceipt.serializeReferenceDocuments();
		model.addAttribute("arReceipt", arReceipt);
		return "ArReceiptForm.jsp";
	}
	
	private void loadAttributes (ArReceipt arReceipt, int pId, Model model){
		processReceiptTransactions(arReceipt);
		model.addAttribute("pId", pId);
		model.addAttribute("sequenceNo", arReceipt.getSequenceNo());
		model.addAttribute("receiptNo", arReceipt.getReceiptNumber());
	}

	public void processReceiptTransactions(ArReceipt arReceipt) {
		logger.debug("Retreiving receipt transactions");
		arReceipt.setArRTransactions(arReceiptTransactionService.getArReceiptTransaction(arReceipt.getId()));
	}

	private void loadSelections (User user, ArReceipt arReceipt, Model model) {
		model.addAttribute("arReceiptTypes", arReceiptTypeService.getArReceiptTypes());
		model.addAttribute("arCustomers", arCustomerService.getArCustomers(user));
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(arReceipt.getCurrencyId()));
	}
	
	@RequestMapping (value = "/getArCustomerAccounts", method = RequestMethod.GET)
	public @ResponseBody String getArCustomerAccounts(@RequestParam (value="arCustomerId") Integer arCustomerId,
			@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="divisionId") Integer divisionId,
			@RequestParam (value="arCustomerAccountId", required=false) Integer arCustomerAccountId) {
		List<ArCustomerAccount> arCustomerAccounts = arCustomerAcctService.getArCustomerAccounts(arCustomerId, companyId, divisionId, arCustomerAccountId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arCustomerAccounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (method = RequestMethod.POST)
	public String saveArReceipt(@ModelAttribute ("arReceipt") ArReceipt arReceipt, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		arReceipt.deserializeArReceiptLines();
		arReceipt.deserializeReferenceDocuments();
		arReceipt.setArRTransactions(arReceiptTransactionService.processArRTransactions(arReceipt));
		synchronized (this) {
			arReceiptValidator.validate(arReceipt, result);
			if (result.hasErrors()) {
				arReceiptTransactionService.resetTransactions(arReceipt);
				return loadForm(arReceipt, user, model);
			}
			eBFormServiceHandler.saveForm(arReceipt, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", arReceipt.getSequenceNo());
		model.addAttribute("formId", arReceipt.getId());
		model.addAttribute("ebObjectId", arReceipt.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value = "{divisionId}/form/viewForm", method = RequestMethod.GET)
	public String showForm (@PathVariable("divisionId") int divisionId,
			@RequestParam (value="pId", required = false) Integer pId, Model model) throws IOException {
		logger.info("Retreiving AR Receipt form");
		ArReceipt arReceipt = null;
		if(pId != null){
			logger.debug("Retreiving form with an id " + pId);
			arReceipt = arReceiptService.getArReceiptWithArReceiptLines(pId);
			arReceipt.setReferenceDocuments(refDocumentService.processReferenceDocs(arReceipt.getEbObjectId()));
		}else{
			logger.error("AR Receipt form id is required");
			throw new RuntimeException("AR Receipt form id is required");
		}
		model.addAttribute("arReceipt", arReceipt);
		logger.info("Successfully loaded AR Receipt form");
		return "ArReceiptFormView.jsp";
	}

	@RequestMapping(value = "/loadCustomerAdvances", method = RequestMethod.GET)
	public @ResponseBody String loadCustomerAdvances(@RequestParam(value="arTransactionIds", required=false) String arTransactionIds, Model model) {
		List<ArReceiptAdvancePayment> caps = capService.getAdvancePayments(arTransactionIds);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(caps, jsonConfig);
		return caps != null && !caps.isEmpty() ? jsonArray.toString() : "No advance payments created.";
	}

	@RequestMapping(value = "/getAdvancePayments", method = RequestMethod.GET)
	public @ResponseBody String getAdvancePayments(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="customerId", required=false) Integer customerId,
			@RequestParam(value="customerAcctId", required=false) Integer customerAcctId,
			@RequestParam(value="capNumber", required=false) Integer capNumber,
			@RequestParam(value="isExact", required=false) boolean isExact,
			@RequestParam(value="toBeExcludedCapIds", required=false) String toBeExcludedCapIds,
			@RequestParam(value="arReceiptId", required=false) Integer arReceiptId,
			Model model) {
		List<CustomerAdvancePayment> caps = capService.getCustomerAdvancePayments(companyId, customerId, customerAcctId,
				capNumber, isExact, toBeExcludedCapIds, arReceiptId);
		String [] excludes = {"glDate", "itemCategory", "itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices", "buyingAddOns",
				"buyingDiscounts", "rItemDetails", "arCustomer", "arCustomerAccount", "formWorkflow", "capItems", "capArLines",
				"wtAcctSetting", "salesOrder", "receiptMethod", "objectTypeId", "customerAdvancePaymentTypeId"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONArray jsonArray = JSONArray.fromObject(caps, jsonConfig);
		return caps != null && !caps.isEmpty() ? jsonArray.toString() : "No advance payments created.";
	}

	@RequestMapping(value = "/getArReceiptLines", method = RequestMethod.GET)
	public @ResponseBody String getArReceiptLines(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="customerAcctId", required=false) Integer customerAcctId,
			@RequestParam(value="currencyId", required=false) Integer currencyId,
			@RequestParam(value="transNumber", required=false) String transNumber,
			@RequestParam(value="arReceiptId", required=false) Integer arReceiptId,
			@RequestParam(value="refObjIds", required=false) String refObjIds,
			Model model) {
		Collection<ArReceiptLine> arReceiptLines = arReceiptService.getArReceiptLines(companyId, divisionId, currencyId, customerAcctId, transNumber, refObjIds);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray = JSONArray.fromObject(arReceiptLines, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/getCapReferences", method = RequestMethod.GET)
	public @ResponseBody String getCapReferences(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="currencyId", required=false) Integer currencyId,
			@RequestParam(value="arInvoiceObjectId", required=false) Integer arInvoiceObjectId,
			@RequestParam(value="refObjIds", required=false) String refObjIds,
			Model model) {
		Collection<ArReceiptLine> arReceiptLines = arReceiptService.getCapReferences(arInvoiceObjectId, companyId, divisionId, currencyId, refObjIds);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray = JSONArray.fromObject(arReceiptLines, jsonConfig);
		return arReceiptLines != null && !arReceiptLines.isEmpty() ? jsonArray.toString() : "No advance payments created.";
	}

	@RequestMapping(value = "{divisionId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchAPInvoices(@PathVariable("divisionId") int divisionId,
			@RequestParam(required=true, value="criteria", defaultValue="")
			String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = arReceiptService.searchArReceipt(user, divisionId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/getRemainingCapBalance", method = RequestMethod.GET)
	public @ResponseBody String getRemainingCapBalance(
			@RequestParam( value="ariRefObjectId", required=false) Integer ariRefObjectId,
			@RequestParam( value="arReceiptId", required=false) Integer arReceiptId) {
		double balance = arReceiptService.getCapRemainingBalance(ariRefObjectId, arReceiptId);
		return String.valueOf(balance);
	}
}
