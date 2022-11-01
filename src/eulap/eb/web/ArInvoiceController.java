package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArInvoiceService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * Controller for {@link ArInvoice}

 */

@Controller
@RequestMapping("/arInvoice")
public class ArInvoiceController {
	@Autowired
	private ArInvoiceService arInvoiceService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ArCustomerService arCustomerService;

	private static final String AR_INVOICE_ATTRIB_NAME = "arInvoice";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/{typeId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ArInvoice arInvoice = null;
		if(pId == null) {
			arInvoice = new ArInvoice();
			arInvoice.setDate(new Date());
		} else {
			arInvoice = arInvoiceService.getArInvoice(pId, true, true);
			arInvoiceService.setArInvoiceRefDoc(arInvoice);
		}
		int divisionId = arInvoiceService.getDivisionIdByAriType(typeId);
		arInvoice.setDivisionId(divisionId);
		arInvoice.setDivision(divisionService.getDivision(divisionId));
		arInvoice.setArInvoiceTypeId(typeId);
		return loadForm(arInvoice, model, user, typeId);
	}

	private String loadForm(ArInvoice arInvoice, Model model, User user, Integer typeId) {
		arInvoice.serializeNonSerialItems();
		arInvoice.serializeSerialItems();
		arInvoice.serializeARILines();
		arInvoice.serializeArInvoiceTruckingLines();
		arInvoice.serializeArInvoiceEquipmentLines();
		arInvoice.serializeReferenceDocuments();
		model.addAttribute(AR_INVOICE_ATTRIB_NAME, arInvoice);
		return "ArInvoiceNsbForm.jsp";
	}

	@RequestMapping(value="/showReferenceForm", method=RequestMethod.GET)
	public String showReferenceForm(@RequestParam (value="arInvoiceTypeId") Integer typeId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = (List<Company>) companyService.getActiveCompanies(user, null, null, null);
		model.addAttribute("companies", companies);
		int divisionId = arInvoiceService.getDivisionIdByAriType(typeId);
		model.addAttribute("divisionId", divisionId);
		loadReferences((companies != null ? companies.iterator().next().getId() : null), divisionId, null, null, null,
				SalesOrder.STATUS_UNUSED, null, null, null, PageSetting.START_PAGE, typeId, user, model);
		return "DRReference.jsp";
	}

	private void loadReferences(Integer companyId, Integer divisionId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer atwNumber, Integer statusId, Date dateFrom, Date dateTo, String drRefNumber, Integer pageNumber,
			 Integer typeId, User user, Model model) {
		Page<DeliveryReceipt> deliveryReceipts = arInvoiceService.getDeliveryReceipts(companyId, divisionId, arCustomerId,
				arCustomerAccountId, atwNumber, statusId, dateFrom, dateTo, drRefNumber, typeId, pageNumber);
		model.addAttribute("arInvoiceTypeId", typeId);
		model.addAttribute("deliveryReceipts", deliveryReceipts);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value = "/getDRReferenceForms", method = RequestMethod.GET)
	public String getATWReferenceForms(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="arCustomerId", required=false) Integer arCustomerId,
			@RequestParam(value="arCustomerAcctId", required=false) Integer arCustomerAccountId,
			@RequestParam(value="drNumber", required=false) Integer drNumber,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam (value="arInvoiceTypeId") Integer typeId,
			@RequestParam (value="drRefNumber") String drRefNumber,
			@RequestParam(value="status", required=false) Integer status,
			@RequestParam(value="pageNumber", required=false) int pageNumber,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadReferences(companyId, divisionId, arCustomerId, arCustomerAccountId, drNumber, status, dateFrom, dateTo, drRefNumber,
				pageNumber, typeId, user, model);
		return "DRReferenceTable.jsp";
	}

	@RequestMapping(value="/loadDRReferenceForm", method=RequestMethod.GET)
	public @ResponseBody String loadDRReferenceForm(@RequestParam (value="referenceDrIds") String referenceDrIds,
			Model model, HttpSession session) {
		ArInvoice arInvoice = arInvoiceService.convertDRtoARI(referenceDrIds);
		String [] excludes = {"glDate", "itemCategory", "itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices",
				"buyingAddOns", "buyingDiscounts", "rItemDetails", "formWorkflow"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(arInvoice, jsonConfig);
		return arInvoice == null ? "No DR reference found" : jsonObject.toString();
	}

	@RequestMapping(value = "/{typeId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable(value="typeId") int typeId,
			@ModelAttribute ("arInvoice") ArInvoice arInvoice, BindingResult result,
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		arInvoice.deserializeNonSerialItems();
		arInvoice.deserializeSerialItems();
		arInvoice.deserializeARILines();
		arInvoice.deserializeArInvoiceTruckingLines();
		arInvoice.deserializeArInvoiceEquipmentLines();
		arInvoice.deserializeReferenceDocuments();
		synchronized (this) {
			arInvoiceService.validate(arInvoice, result);
			if (result.hasErrors()) {
				return loadForm(arInvoice, model, user, typeId);
			}
			ebFormServiceHandler.saveForm(arInvoice, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", arInvoice.getSequenceNo());
		model.addAttribute("ebObjectId", arInvoice.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping (value="/{typeId}/view", method = RequestMethod.GET)
	public String viewForm(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		ArInvoice arInvoice = arInvoiceService.getArInvoice(pId, true, true);
		arInvoiceService.setArInvoiceRefDoc(arInvoice);
		model.addAttribute("companyName", companyService.getCompany(arInvoice.getCompanyId()).getName());
		model.addAttribute("arInvoice", arInvoice);
		model.addAttribute("capBalance", arInvoiceService.getCapRemainingBalance(arInvoice.getStrDrRefIds(), arInvoice.getId()));
		return "ArInvoiceNsbView.jsp";
	}

	@RequestMapping(value="/{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchOrderSlips(@PathVariable(value="typeId") int typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = arInvoiceService.searchARInvoice(criteria, typeId);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/{typeId}/pdf", method = RequestMethod.GET)
	public String generatePrintout(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="isSalesInvoice", required=false, defaultValue = "true") Boolean isSalesInvoice,
			@RequestParam(value="infoType", required=true, defaultValue = "1") Integer infoType,
			@RequestParam(value="hasDate", required=false, defaultValue = "true") Boolean hasDate,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			@RequestParam(value="format", required=false) String format,
			Model model, HttpSession session) {
		ArInvoice arInvoice = arInvoiceService.getArInvoice(pId, true, true);
		List<ArInvoice> arInvoices = new ArrayList<ArInvoice>();
		arInvoices.add(arInvoice);
		JRDataSource dataSource = new JRBeanCollectionDataSource(arInvoices);
		if (hasDate) {
			model.addAttribute("date", arInvoice.getDate());
			model.addAttribute("dueDate", arInvoice.getDueDate());
			model.addAttribute("term", arInvoice.getTerm().getName());
		}
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		model.addAttribute("remarks", arInvoice.getRemarks());
		model.addAttribute("sequenceNo", arInvoice.getSequenceNo());
		model.addAttribute("drNumber", arInvoice.getDrNumber());
		model.addAttribute("amount", arInvoiceService.getInvoiceGrossAmount(arInvoice));
		model.addAttribute("poNumber", arInvoiceService.getPoNumber(arInvoice));
		model.addAttribute("recoupment", NumberFormatUtil.negateAmount(arInvoice.getRecoupment()));
		model.addAttribute("retention", NumberFormatUtil.negateAmount(arInvoice.getRetention()));
		//Company
		Company company = arInvoice.getCompany();
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("customerAcct", arInvoice.getArCustomerAccount().getName());
		model.addAttribute("customerName", arInvoice.getArCustomer().getName());
		model.addAttribute("customerAddress", arCustomerService.getArCustomerFullAddress(arInvoice.getArCustomer()));
		model.addAttribute("tin", StringFormatUtil.processBirTinTo13Digits(arInvoice.getArCustomer().getTin()));
		model.addAttribute("attention", arInvoice.getRemarks());
		model.addAttribute("currency", arInvoice.getCurrency().getName());
		//WTax
		if (arInvoice.getWtAcctSetting() != null) {
			model.addAttribute("wtName", arInvoice.getWtAcctSetting().getName());
			model.addAttribute("wtAmount", arInvoice.getWtAmount());
		}
		DeliveryReceipt dr = arInvoiceService.getDrByAri(arInvoice);
		if (dr != null) {
			model.addAttribute("salesRepresentative", dr.getSalesPersonnelName());
			model.addAttribute("drRefNumber", dr.getDrRefNumber());
		}
		/**
		 * The printout contains:
		 * infoType = 1 : item stock code, description, and amount.
		 * infoType = 2 : item stock code, description, amount, UOM and quantity.
		 * infoType = 3 : item stock code, description, amount, UOM, quantity and percentage.
		 */

		model.addAttribute("infoType", infoType);
		model.addAttribute("taxTypeId", arInvoiceService.getTaxType(arInvoice));
		model.addAttribute("totalAmount", arInvoice.getAmount());
		model.addAttribute("totalVat", arInvoiceService.computeTotalVat(arInvoice));
		model.addAttribute("subTotal", arInvoiceService.getSubtotal(arInvoice));
		FormWorkflow workflowLog = arInvoice.getFormWorkflow();
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			String name = log.getCreated().getUserFullName();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", name);
			} else if (log.getFormStatusId() == FormStatus.REVIEWED_ID) {
				model.addAttribute("reviewedBy", name);
			}
		}
		model.addAttribute("receivedBy", arInvoice.getReceiver());
		model.addAttribute("receivedDate", arInvoice.getDateReceived());
		String printoutName = "";
		if(isSalesInvoice) {
			printoutName = "NSBAriSalesInvoiceMatrix.jasper";
		} else {
			printoutName = "NSBAriBillingStatementMatrix.jasper";
		}
		return printoutName;
	}

	@RequestMapping(value="receiveAri/{formId}", method=RequestMethod.GET)
	public String receiveAriForm(@PathVariable(value="formId") int formId,
			Model model, HttpSession session) throws IOException {
		ArInvoice arInvoice = new ArInvoice();
		arInvoice.setDateReceived(new Date());
		return loadReceiveAriForm(formId, arInvoice, model);
	}

	private String loadReceiveAriForm(int formId, ArInvoice arInvoice, Model model) {
		model.addAttribute("formId", formId);
		model.addAttribute("arInvoice", arInvoice);
		return "AriReceivingForm.jsp";
	}

	@RequestMapping (value="receiveAri/{formId}", method=RequestMethod.POST)
	public String saveCheckLog(@PathVariable(value="formId") int formId,
			@ModelAttribute("arInvoice") ArInvoice arInvoice, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		synchronized (this) {
			arInvoiceService.validateStatusLogs(arInvoice, result);
			if(result.hasErrors()) {
				return loadReceiveAriForm(formId, arInvoice, model);
			}
			arInvoiceService.saveAriReceivingDetails(arInvoice, user);
		}
		model.addAttribute("success", true);
		return "successfullySaved";
	}

	@RequestMapping(value="/getRemainingCapBalance", method = RequestMethod.GET)
	public @ResponseBody String getRemainingCapBalance(@RequestParam( value="deliveryReceiptIds", required=true) String deliveryReceiptIds,
			@RequestParam( value="arInvoiceId", required=false) Integer arInvoiceId) {
		double balance = arInvoiceService.getCapRemainingBalance(deliveryReceiptIds, arInvoiceId);
		return String.valueOf(balance);
	}
}
