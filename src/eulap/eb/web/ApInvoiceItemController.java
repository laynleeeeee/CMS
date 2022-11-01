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
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceItem;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.APInvoiceService;
import eulap.eb.service.ApInvoiceItemService;
import eulap.eb.service.SupplierAccountService;
import eulap.eb.service.TermService;
import eulap.eb.web.dto.ApInvoiceItemDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.InvoicePrintoutDto;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * Controller class for {@link ApInvoiceItem} form

 */

@Controller
@RequestMapping("apInvoiceItem")
public class ApInvoiceItemController {
	private final Logger logger = Logger.getLogger(ApInvoiceItemController.class);
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private ApInvoiceItemService apInvoiceItemService;
	@Autowired
	private TermService termService;
	@Autowired
	private SupplierAccountService accountService;

	private static final String APII_FORM_NAME = "AP INVOICE - GOODS";
	private static final String APIS_FORM_NAME = "REQUEST FOR PAYMENT";

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model,
			HttpSession session) throws IOException {
		logger.info("Showing the AP Invoice - Item form");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ApInvoiceItemDto apInvoiceItemDto = new ApInvoiceItemDto();
		APInvoice apInvoice = new APInvoice();
		boolean isNew = pId == null;
		apInvoice.setInvoiceTypeId(typeId);
		if (!isNew) {
			apInvoiceItemDto = apInvoiceItemService.setApInvoiceItemDto(pId);
		} else {
			Date currentDate = new Date();
			apInvoice.setInvoiceDate(currentDate);
			apInvoice.setGlDate(currentDate);
			apInvoice.setDueDate(currentDate);
			apInvoiceItemDto.setApInvoice(apInvoice);
		}
		return loadForm(apInvoiceItemDto, user, model, typeId);
	}

	private String loadForm(ApInvoiceItemDto apInvoiceItemDto, User user, Model model, int typeId) {
		APInvoice apInvoice = apInvoiceItemDto.getApInvoice();
		apInvoice.serializeApLines();
		apInvoice.serializeInvoiceLines();
		apInvoice.serializeReferenceDocuments();
		apInvoiceItemDto.setApInvoice(apInvoice);
		model.addAttribute("terms",  termService.getTerms(user));
		model.addAttribute("apInvoiceItemDto", apInvoiceItemDto);
		if (typeId == InvoiceType.INVOICE_ITEM_TYPE_ID) {
			return "ApInvoiceItemForm.jsp";
		}
		return "ApInvoiceServiceForm.jsp";
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.POST)
	public String submitForm(@PathVariable("typeId") int typeId,
			@ModelAttribute("apInvoiceItemDto") ApInvoiceItemDto apInvoiceItemDto, BindingResult result,
			Model model, HttpSession session) throws ClassNotFoundException, IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		APInvoice apInvoice = apInvoiceItemDto.getApInvoice();
		apInvoice.deserializeApLines();
		apInvoice.deserializeInvoiceLines();
		apInvoice.deserializeReferenceDocuments();
		if (typeId == InvoiceType.INVOICE_ITEM_TYPE_ID) {
			apInvoice.setApInvoiceItems(apInvoiceItemService.processToBeSavedInvoiceItems(apInvoice.getApInvoiceItems()));
		} else {
			apInvoice.setaPlines(apInvoiceService.processAPLines(apInvoice.getaPlines()));
			apInvoiceItemService.setApLineCombination(apInvoice);
		}
		apInvoiceItemService.validateForm(apInvoiceItemDto, user, result);
		if (result.hasErrors()) {
			return loadForm(apInvoiceItemDto, user, model, typeId);
		}
		apInvoiceItemService.saveApInvoiceItem(apInvoiceItemDto, user);
		model.addAttribute("success", true);
		model.addAttribute("ebObjectId", apInvoice.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping (value="{typeId}/view", method = RequestMethod.GET)
	public String viewForm(@RequestParam(value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		ApInvoiceItemDto invoiceItemDto = apInvoiceItemService.setApInvoiceItemDto(pId);
		model.addAttribute("apInvoiceItemDto", invoiceItemDto);
		int typeId = invoiceItemDto.getApInvoice().getInvoiceTypeId();
		if (typeId == InvoiceType.INVOICE_ITEM_TYPE_ID) {
			return "ApInvoiceItemView.jsp";
		}
		return "ApInvoiceServiceFormView.jsp";
	}

	@RequestMapping(value="/getReceivingReports", method = RequestMethod.GET)
	public @ResponseBody String getInvoicesBySupplierAccount(@RequestParam(value="supplierAccountId") Integer supplierAcctId,
			@RequestParam(value="invoiceNumber", required=false) String invoiceNumber,
			@RequestParam(value="invoiceIds", required=false) String invoiceIds,
			@RequestParam(value="formId", required=false) Integer formId,
			@RequestParam(value="isExact", required=false) boolean isExact) {
		Collection<APInvoice> invoices = apInvoiceItemService.getReceivingReports(formId, supplierAcctId, invoiceNumber, invoiceIds, isExact);
		String [] excludes = {"aPlines", "supplier", "supplierAccount", "term", "apLineDtos", "rrItems", "rtsItems",
				"receivingReport", "returnToSupplier", "rrItemsJson", "rtsItemsJson", "serviceLeaseKeyId",
				"companyId", "objectTypeId"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONArray jsonArray = JSONArray.fromObject(invoices, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/loadReceivingReports", method = RequestMethod.GET)
	public String loadInvoices (@RequestParam(value="supplierAccountId") Integer supplierAcctId,
			@RequestParam(value="formId", required=false) Integer formId, Model model) {
		Collection<APInvoice> apInvoices = apInvoiceItemService.getReceivingReports(formId, supplierAcctId, null, null, false);
		model.addAttribute("approvedInvoices", apInvoices);
		return "ApInvoiceSelector.jsp";
	}

	@RequestMapping(value="{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchInvoiceForms(@PathVariable("typeId") Integer typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = apInvoiceItemService.searchInvoiceForms(user, criteria, typeId);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping (value="/getSupplierAccounts", method = RequestMethod.GET)
	public @ResponseBody String getSuppliersAccount (@RequestParam (value="supplierId") Integer supplierId,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="supplierAccountId", required=false) Integer supplierAccountId, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<SupplierAccount> supplierAccunts = new ArrayList<>();
		if (companyId != null) {
			supplierAccunts = accountService.getSupplierAccts(supplierId, companyId);
		} else {
			supplierAccunts = accountService.getSupplierAccnts(supplierId, user);
		}
		if (supplierAccountId != null) {
			SupplierAccount supplierAccount = accountService.getSupplierAccount(supplierAccountId);
			if (!supplierAccount.isActive() && !supplierAccunts.contains(supplierAccount)) {
				supplierAccunts.add(supplierAccount);
			}
		}
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(supplierAccunts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="{typeId}/print", method = RequestMethod.GET)
	private String generatePrintout(@PathVariable("typeId") Integer typeId,
			@RequestParam(value="pId", required=true) Integer pId,
			Model model, HttpSession session) throws IOException {
		APInvoice apInvoice = apInvoiceService.getInvoice(pId);
		List<InvoicePrintoutDto> invoicePrintoutDtos = apInvoiceItemService.getInvoicePrintoutDtos(pId);
		model.addAttribute("datasource", new JRBeanCollectionDataSource(invoicePrintoutDtos));
		setCommonParams(apInvoice, typeId, model, session);
		if (typeId.equals(InvoiceType.INVOICE_ITEM_TYPE_ID)) {
			return "ApInvoiceItemPdf.jasper";
		}
		return "ApInvoiceServicePdf.jasper";
	}

	public void setCommonParams(APInvoice apInvoice, Integer invoiceTypeId,
			Model model, HttpSession session) {
		model.addAttribute("hasWtax", apInvoice.getWtAcctSettingId() != null);
		model.addAttribute("format", "pdf");
		String formName = "";
		if (invoiceTypeId.equals(InvoiceType.INVOICE_ITEM_TYPE_ID)) {
			formName = APII_FORM_NAME;
		} else {
			formName = APIS_FORM_NAME;
			model.addAttribute("printDate", DateUtil.formatToMediumDate(new Date()));
		}
		model.addAttribute("formDate", apInvoice.getGlDate());
		model.addAttribute("formName" , formName);
		model.addAttribute("sequenceNumber", ""+apInvoice.getSequenceNumber());
		model.addAttribute("description", apInvoice.getDescription());
		model.addAttribute("invoiceNumber", apInvoice.getInvoiceNumber());
		model.addAttribute("glDate", apInvoice.getGLDate());
		model.addAttribute("invoiceDate", apInvoice.getInvoiceDate());
		model.addAttribute("dueDate", apInvoice.getDueDate());
		model.addAttribute("birLogo", "bir_logo.png");

		setCompanyParams(apInvoice.getSupplierAccount().getCompany(), model);
		setSupplierParams(apInvoice.getSupplier(), model);
		setWorkflowParams(apInvoice.getFormWorkflow(), model);
	}

	private void setCompanyParams(Company company, Model model) {
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyContactNo", company.getContactNumber());
		model.addAttribute("companyLogo", company.getLogo());
	}

	private void setSupplierParams(Supplier supplier, Model model) {
		model.addAttribute("supplier", supplier.getName());
		model.addAttribute("supplierAddress", supplier.getAddress());
		model.addAttribute("supplierTin", supplier.getTin());
	}

	private void setWorkflowParams(FormWorkflow formWorkflow, Model model) {
		for (FormWorkflowLog log : formWorkflow.getFormWorkflowLogs()) {
			String name = log.getCreated().getUserFullName();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", name);
				model.addAttribute("createdDate", log.getCreatedDate());
				model.addAttribute("creatorPosition", log.getCreated().getPosition().getName());
			} else if (log.getFormStatusId() == FormStatus.VERIFIED_ID) {
				model.addAttribute("verifiedBy", name);
				model.addAttribute("verifiedDate", log.getCreatedDate());
				model.addAttribute("verifierPosition", log.getCreated().getPosition().getName());
			}
		}
	}
}
