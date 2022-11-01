package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.math.BigDecimal;
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
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.domain.hibernate.PaymentType;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ApPaymentService;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CheckbookService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.validator.ApPaymentValidator;
import eulap.eb.web.dto.ApInvoiceDto;
import eulap.eb.web.dto.ApPaymentLineDto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * Controller for AP Payment form.

 */
@Controller
@RequestMapping(value = "apPayment")
public class ApPaymentFormController {
	Logger logger =  Logger.getLogger(ApPaymentFormController.class);
	@Autowired
	private CheckbookService checkbookService;
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private ApPaymentService paymentService;
	@Autowired
	private ApPaymentValidator paymentValidator;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private DivisionService divisionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{divisionId}/form", method = RequestMethod.GET)
	public String showPaymentForm(@PathVariable(value="divisionId") int divisionId,
			@RequestParam (value="pId", required = false)Integer pId,
			@RequestParam (value="isEdit", defaultValue="true", required=false) boolean isEdit,
			Model model, HttpSession session) throws IOException {
		model.addAttribute("isEdit", isEdit);
		ApPayment apPayment = new ApPayment();
		Date currentDate = new Date();
		apPayment.setPaymentDate(currentDate);
		apPayment.setCheckDate(currentDate);
		apPayment.setDivisionId(divisionId);
		apPayment.setDivision(divisionService.getDivision(divisionId));
		if (pId != null){
			apPayment = paymentService.getApPayment(pId);
			paymentService.processPaymentRefDoc(apPayment);
			processApInvoiceDto(apPayment);
			model.addAttribute("pId", pId);
			model.addAttribute("voucherNo", apPayment.getVoucherNumber());
		}else{
			apPayment.setApInvoices(new ArrayList<ApInvoiceDto>());
		}
		return loadForm(model, apPayment);
	}

	private String loadForm(Model model, ApPayment apPayment) {
		apPayment.serializeReferenceDocuments();
		apPayment.serializeApPaymentLineDtos();
		model.addAttribute("apPayment", apPayment);
		model.addAttribute("currencies", currencyService.getActiveCurrencies(apPayment.getCurrencyId()));
		return "ApPaymentForm.jsp";
	}

	private void processApInvoiceDto(ApPayment apPayment) {
		List<ApInvoiceDto> dto = paymentService.getAPInvoices(apPayment);
		apPayment.setApInvoices(dto);
	}

	@RequestMapping(value = "/getCheckbooks", method = RequestMethod.GET)
	public @ResponseBody String getCheckbooks (@RequestParam(value="bankAccountId") Integer bankAccountId,
			@RequestParam(value="checkbookId", required=false) Integer checkBookId, 
			@RequestParam(value="checkBookName", required=false) String checkBookName,
			HttpSession session) {
		List<Checkbook> checkbooks = paymentService.getCheckBooks(CurrentSessionHandler.getLoggedInUser(session),
				bankAccountId, checkBookId, checkBookName, 10);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(checkbooks, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/getInvoice", method = RequestMethod.GET)
	public @ResponseBody String getInvoiceNumber(@RequestParam (value="supplierAccountId") int supplierAccountId,
			@RequestParam (value="invoiceNumber") String invoiceNumber, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		APInvoice invoice = paymentService.getInvoiceByInvNumber(supplierAccountId, invoiceNumber,
				user.getServiceLeaseKeyId());
		String [] excludes = {"aPlines", "supplier", "supplierAccount", "term", "apLineDtos", "rrItems", "rtsItems",
				"receivingReport", "returnToSupplier", "rrItemsJson", "rtsItemsJson", "serviceLeaseKeyId", "userAccessRights",
				"formWorkflow", "objectTypeId"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setIgnoreDefaultExcludes(false);
		jConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(invoice, jConfig);
		return invoice == null ? "No invoice found" : jsonObject.toString();
	}

	@RequestMapping(value = "/getCheckNumber", method = RequestMethod.GET)
	public @ResponseBody String getCheckNumbers(@RequestParam(value="checkbookId") Integer checkbookId) {
		Checkbook checkbook = checkbookService.getCheckbook(checkbookId);
		BigDecimal checkNumber = paymentService.generateCheckNumber(checkbook);
		return checkNumber == null ? "Exhausted checkbook" : String.valueOf(checkNumber);
	}

	@RequestMapping(value = "/loadInvoices", method = RequestMethod.GET)
	public String loadInvoices (@RequestParam(value="supplierAccountId") int supplierAccountId,
			@RequestParam(value="invoiceNumber", required=false, defaultValue="") String invoiceNumber,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="ebObjectIds") String ebObjectIds,
			@RequestParam(value="currencyId", required=false) Integer currencyId,
			@RequestParam(value="invoiceIds", required=false) String invoiceIds, Model model) {
		Collection<ApPaymentLineDto> paymentLines = paymentService.loadApPaymentLines(supplierAccountId, 
				divisionId, invoiceNumber, ebObjectIds, currencyId);
		model.addAttribute("paymentLines", paymentLines);
		return "ApInvoiceSelector.jsp";
	}

	@RequestMapping(value = "/loadPaymentLines", method = RequestMethod.GET)
	public  @ResponseBody String loadPaymentLines (@RequestParam(value="supplierAccountId") Integer supplierAccountId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="currencyId", required=false) Integer currencyId,
			@RequestParam(value="invoiceNumber", required=false, defaultValue="") String invoiceNumber,
			@RequestParam(value="ebObjectIds", required=false) String ebObjectIds, Model model) {
		Collection<ApPaymentLineDto> dtos = paymentService.loadApPaymentLines(supplierAccountId, 
				divisionId, invoiceNumber, ebObjectIds, currencyId);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray = JSONArray.fromObject(dtos, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/loadRefSap", method = RequestMethod.GET)
	public @ResponseBody String loadSapRefTransactions(
			@RequestParam(value="invoiceEbObject") Integer invoiceEbObject,
			@RequestParam(value="currencyId", required=false) Integer currencyId,
			@RequestParam(value="ebObjectIds") String ebObjectIds) {
		List<ApPaymentLineDto> apInvoices = paymentService.getSapRefTrans(invoiceEbObject, ebObjectIds, currencyId);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray = JSONArray.fromObject(apInvoices, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "loadSupplierAccounts", method = RequestMethod.GET)
	public @ResponseBody String loadSupplierAccounts (@RequestParam(value="supplierId") Integer supplierId,
			@RequestParam(value="bankAccountId") Integer bankAccountId) {
		BankAccount bankAccount = bankAccountService.getBankAccount(bankAccountId);
		List<SupplierAccount> supplierAccounts = paymentService.getSupplierAccounts(supplierId, bankAccount.getCompanyId());
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(supplierAccounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "{divisionId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable(value="divisionId") int divisionId,
			@ModelAttribute ("apPayment") ApPayment apPayment, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		apPayment.setPaymentTypeId(PaymentType.TYPE_AP_PAYMENT);
		apPayment.deserializeReferenceDocuments();
		apPayment.deserializeApPaymentLineDtos();
		synchronized (this) {
			paymentValidator.validate(apPayment, result, user);
			if (result.hasErrors()) {
				return loadForm(model, apPayment);
			}
			ebFormServiceHandler.saveForm(apPayment, user);
		}
		model.addAttribute("ebObjectId", apPayment.getEbObjectId());
		return "successfullySaved";
	}
	
	@RequestMapping(value = "{divisionId}/form/viewForm", method = RequestMethod.GET)
	public String showForm (@PathVariable(value="divisionId") int divisionId,
			@RequestParam (value="pId", required = false)Integer pId, Model model) throws IOException {
		logger.info("Retreiving AP Payment form");
		ApPayment apPayment = null;
		if (pId != null){
			logger.debug("Retreiving form with an id " + pId);
			apPayment = paymentService.getApPayment(pId);
			paymentService.processPaymentRefDoc(apPayment);
			logger.debug("Retreiving AP Invoices");
			processApInvoiceDto(apPayment);
		}else{
			logger.error("AP invoice form id is required");
			throw new RuntimeException("AP Payment form id is required");
		}
		model.addAttribute("apPayment", apPayment);
		logger.info("Successfully loaded AP Payment form");
		return "ApPaymentFormView.jsp";
	}

	@RequestMapping(value="clearPayment/{formId}", method=RequestMethod.GET)
	public String clearPaymentForm(@PathVariable(value="formId") int formId,
			Model model, HttpSession session) throws IOException {
		ApPayment apPayment = new ApPayment();
		apPayment.setDateCleared(new Date());
		return loadReceiveDrForm(formId, apPayment, model);
	}

	private String loadReceiveDrForm(int formId, ApPayment apPayment, Model model) {
		model.addAttribute("formId", formId);
		model.addAttribute("apPayment", apPayment);
		return "ApPaymentClearingForm.jsp";
	}

	@RequestMapping (value="clearPayment/{formId}", method=RequestMethod.POST)
	public String saveCheckLog(@PathVariable(value="formId") int formId,
			@ModelAttribute("apPayment") ApPayment apPayment, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		synchronized (this) {
			paymentValidator.validateStatusLogs(apPayment, result);
			if(result.hasErrors()) {
				return loadReceiveDrForm(formId, apPayment, model);
			}
			paymentService.savePaymentReceivingDetails(apPayment, user);
		}
		model.addAttribute("success", true);
		return "successfullySaved";
	}
}