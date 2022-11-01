package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.APLine;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.APInvoiceService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.InvoiceClassificationService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.TermService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.APInvoiceValidator;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.LoanProceedsDto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * A controller class for adding and editing of accounts payable invoices.


 */

@Controller
@RequestMapping ("/aPInvoiceForm")
public class APInvoiceFormController {
	private final Logger logger = Logger.getLogger(APInvoiceFormController.class);
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private TermService termService;
	@Autowired
	private APInvoiceValidator invoiceValidator;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private WorkflowServiceHandler workflowService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private InvoiceClassificationService invoiceClassificationService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{typeId}/{divisionId}/form/new", method = RequestMethod.GET)
	public String showForm (@PathVariable (value="typeId") int typeId,
			@PathVariable (value="divisionId") int divisionId,
			@RequestParam (value="pId", required=false) Integer pId,
			@RequestParam (value="isEdit", defaultValue="true", required=false) boolean isEdit,
			Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		APInvoice aPInvoice = new APInvoice();
		if (pId != null) {
			aPInvoice = apInvoiceService.getInvoice(pId);
			aPInvoice.setaPlines(apInvoiceService.getAPLine(aPInvoice));
			Integer ebObjectId = aPInvoice.getEbObjectId();
			if (ebObjectId != null) {
				logger.info("Retrieving the list of reference documents.");
				aPInvoice.setReferenceDocuments(refDocumentService.processReferenceDocs(ebObjectId));
			}
			model.addAttribute("pId", pId);
			model.addAttribute("sequenceNo", aPInvoice.getSequenceNumber());
		} else {
			Date currentDate = new Date();
			aPInvoice.setInvoiceDate(currentDate);
			aPInvoice.setGlDate(currentDate);
			aPInvoice.setaPlines(new ArrayList<APLine>());
		}
		aPInvoice.setDivisionId(divisionId);
		aPInvoice.setDivision(divisionService.getDivision(divisionId));
		aPInvoice.setInvoiceTypeId(typeId);
		String title = apInvoiceService.getInvoiceHeaderName(typeId);
		return loadForm(aPInvoice, user, model, title);
	}

	private String loadForm(APInvoice aPInvoice, User user, Model model, String title) {
		ValueRange nonPORange = ValueRange.of(InvoiceType.API_NON_PO_CENTRAL, InvoiceType.API_NON_PO_NSB8A);
		boolean isNonPO = nonPORange.isValidValue(aPInvoice.getInvoiceTypeId());
		ValueRange importationRange = ValueRange.of(InvoiceType.API_IMPORT_CENTRAL, InvoiceType.API_IMPORT_NSB8A);
		boolean isImportation = importationRange.isValidValue(aPInvoice.getInvoiceTypeId());
		ValueRange apLoanRange = ValueRange.of(InvoiceType.AP_LOAN_CENTRAL, InvoiceType.AP_LOAN_NSB8A);
		boolean isLoan = apLoanRange.isValidValue(aPInvoice.getInvoiceTypeId());
		aPInvoice.serializeApLines();
		aPInvoice.serializeReferenceDocuments();
		List<Company> companies = (List<Company>) companyService.getActiveCompanies(user, null, null, null);
		model.addAttribute("currencies", currencyService.getActiveCurrencies(aPInvoice.getCurrencyId()));
		model.addAttribute("companies", companies);
		model.addAttribute("suppliers", supplierService.getSuppliers(user, aPInvoice));
		model.addAttribute("terms", termService.getTerms(aPInvoice.getTermId()));
		model.addAttribute("invoiceClassifications", invoiceClassificationService.getAllInvoiceClassifications(user));
		model.addAttribute("aPInvoice", aPInvoice);
		model.addAttribute("title", title);
		model.addAttribute("isNonPO", isNonPO);
		if (isImportation) {
			return "APInvoiceImportationForm.jsp";
		} else if (isLoan) {
			return "ApLoanForm.jsp";
		}
		return "APInvoiceNonPOForm.jsp";
	}

	@RequestMapping(value = "/getAccountCombination", method = RequestMethod.GET)
	public @ResponseBody String getDefaultDebitAcct (@RequestParam (value="supplierAccountId") Integer supplierAccountId) {
		AccountCombination ac = apInvoiceService.getAccountCombinationId(supplierAccountId);
		JSONObject jsonObject = JSONObject.fromObject(ac);
		return jsonObject.toString();
	}

	@RequestMapping(value = "{typeId}/{divisionId}/form/new", method = RequestMethod.POST)
	public String submit (@PathVariable (value="typeId") int typeId,
			@PathVariable (value="divisionId") int divisionId,
			@ModelAttribute ("aPInvoice") APInvoice aPInvoice, BindingResult result,
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException,
			ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		aPInvoice.deserializeApLines();
		aPInvoice.deserializeReferenceDocuments();
		synchronized (this) {
			aPInvoice.setaPlines(apInvoiceService.processAPLines(aPInvoice.getaPlines()));
			invoiceValidator.validate(aPInvoice, result, user, "", true, false, true);
			if (result.hasErrors()) {
				String title = apInvoiceService.getInvoiceHeaderName(typeId);
				return loadForm(aPInvoice, user, model, title);
			}
			setApLineCombination(aPInvoice);
			ebFormServiceHandler.saveForm(aPInvoice, user);
		}
		model.addAttribute("ebObjectId", aPInvoice.getEbObjectId());
		return "successfullySaved";
	}

	private void setApLineCombination(APInvoice aPInvoice) {
		Collection<APLine> apLines = aPInvoice.getaPlines();
		for (APLine apl : apLines) {
			if (apl.getAccountNumber() != null || apl.getCompanyNumber() != null || apl.getDivisionNumber() != null) {
				AccountCombination ac = apInvoiceService.getAccountCombination(apl);
				apl.setAccountCombinationId(ac.getId());
			}
		}
	}

	@RequestMapping(value = "/form/{typeId}/viewForm", method = RequestMethod.GET)
	public String showForm (@PathVariable("typeId") int typeId,
			@RequestParam (value="pId", required = false)Integer pId,
			Model model, HttpSession session) throws ConfigurationException, IOException {
		logger.info("Retreiving AP Invoice form");
		APInvoice apInvoice = null;
		boolean hasReference = false;
		boolean isNonPO = false;
		boolean isImportation = false;
		boolean isLoan = false;
		if (pId != null){
			logger.debug("Retreive AP Invoice of form id " + pId);
			apInvoice = apInvoiceService.getInvoice(pId);
			ValueRange nonPORange = ValueRange.of(InvoiceType.API_NON_PO_CENTRAL, InvoiceType.API_NON_PO_NSB8A);
			isNonPO = nonPORange.isValidValue(apInvoice.getInvoiceTypeId());
			ValueRange importationRange = ValueRange.of(InvoiceType.API_IMPORT_CENTRAL, InvoiceType.API_IMPORT_NSB8A);
			isImportation = importationRange.isValidValue(apInvoice.getInvoiceTypeId());
			ValueRange apLoanRange = ValueRange.of(InvoiceType.AP_LOAN_CENTRAL, InvoiceType.AP_LOAN_NSB8A);
			isLoan = apLoanRange.isValidValue(apInvoice.getInvoiceTypeId());
			logger.debug("Retreiving AP Lines");
			apInvoice.setaPlines(apInvoiceService.getAPLine(apInvoice));
			if (apInvoice.getEbObjectId() != null) {
				List<ReferenceDocument> referenceDocuments = refDocumentService.processReferenceDocs(apInvoice.getEbObjectId());
				if (referenceDocuments != null && !referenceDocuments.isEmpty()) {
					apInvoice.setReferenceDocuments(referenceDocuments);
					hasReference = true;
				}
			}
			model.addAttribute("hasReference", hasReference);
			if (apInvoice.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
				logger.info("Checking if has edit access.");
				User user = CurrentSessionHandler.getLoggedInUser(session);
				model.addAttribute("hasEditAccess", hasEditAccess(pId, APInvoice.class.getSimpleName() + typeId, user));
			}
		} else {
			logger.error("AP invoice form id is required.");
			throw new RuntimeException("AP invoice form id is required.");
		}
		String title = apInvoiceService.getInvoiceHeaderName(typeId);
		model.addAttribute("apInvoice", apInvoice);
		model.addAttribute("title", title);
		model.addAttribute("isNonPO", isNonPO);
		model.addAttribute("isEmptyIID", apInvoiceService.isInvoiceImportationDetailsEmpty(apInvoice.getInvoiceImportationDetails()));
		logger.info("Successfully loaded AP Invoice form");
		if (isImportation) {
			return "APInvoiceImportationView.jsp";
		} else if (isLoan) {
			return "ApLoanView.jsp";
		}
		return "APInvoiceNonPOView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, apInvoiceService.getFormWorkflow(pId));
	}

	@RequestMapping(value="/{divisionId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchAPInvoicesWithDiv(@PathVariable(value="divisionId") int divisionId,
			@RequestParam(required=true, value="criteria", defaultValue="")
	String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = apInvoiceService.search(user, criteria, divisionId);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="{divisionId}/showLpReferences", method=RequestMethod.GET)
	public String showLpReferenceForms(@PathVariable(value="divisionId") int divisionId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("divisionId", divisionId);
		List<Company> companies = (List<Company>) companyService.getCompaniesWithInactives(user, 0);
		model.addAttribute("companies", companies);
		model.addAttribute("divisionId", divisionId);
		Integer companyId = companies != null ? companies.iterator().next().getId() : null;
		loadLpReferenceForms(companyId, divisionId, null, null, null, null, APInvoice.STATUS_UNUSED, null,
				PageSetting.START_PAGE, model);
		return "ApLoanLpReferenceForm.jsp";
	}

	private void loadLpReferenceForms(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAcctId, Date dateFrom, Date dateTo, Integer statusId, Integer lpNumber,
			Integer pageNumber, Model model) {
		Page<LoanProceedsDto> lps = apInvoiceService.getReferenceLoanProceeds(companyId, divisionId, supplierId, 
				supplierAcctId, dateFrom, dateTo, statusId, lpNumber, pageNumber);
		model.addAttribute("lps", lps);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getLoanProceeds", method=RequestMethod.GET)
	public String getLoanProceeds(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="supplierId", required=false) Integer supplierId,
			@RequestParam (value="lpNumber", required=false) Integer lpNumber,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="statusId", required=false) Integer statusId,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		loadLpReferenceForms(companyId, divisionId, supplierId, null, dateFrom, dateTo, statusId, lpNumber, pageNumber, model);
		return "ApLoanLpReferenceTable.jsp";
	}

	@RequestMapping(value="/loadLpReference",method=RequestMethod.GET)
	public @ResponseBody String loadLpReference(@RequestParam (value="lpId", required=true) Integer lpId,
			Model model, HttpSession session) throws IOException {
		APInvoice apInvoice = apInvoiceService.convertLpToApLoan(lpId);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		apInvoice.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		String[] excludes = {"rrItems", "rtsItems", "itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices",
				"buyingAddOns", "buyingDiscounts", "rItemDetails", "formWorkflow"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(apInvoice, new JsonConfig());
		return jsonObject.toString();
	}
}