package eulap.eb.web.form.inv;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Date;

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
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.APInvoiceService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.RPurchaseOrderService;
import eulap.eb.service.RReceivingReportService;
import eulap.eb.service.TermService;
import eulap.eb.validator.inv.RReceivingReportValidator;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The entry point of retail receiving report form. 

 */
@Controller
@RequestMapping(value = "/retailReceivingReport")
public class RReceivingReportController {
	private static final Logger logger = Logger.getLogger(RReceivingReportController.class);
	private static final String RR_ATTRIBUTE_NAME = "apInvoice";
	@Autowired
	private RReceivingReportService receivingReportService;
	@Autowired
	private RReceivingReportValidator validator;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private APInvoiceService invoiceService;
	@Autowired
	private TermService termService;
	@Autowired
	private RPurchaseOrderService purchaseOderService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private DivisionService divisionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (value="{divisionId}/form", method=RequestMethod.GET)
	public String showRRForm (@PathVariable(value="divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId, 
			HttpSession session, Model model) throws IOException{
		User user = CurrentSessionHandler.getLoggedInUser(session);
		APInvoice apInvoice = new APInvoice();
		if (pId != null){
			logger.debug("Retrieving receiving report form by id "+pId);
			apInvoice = receivingReportService.getRrForEditing(pId);
		} else {
			apInvoice.setGlDate(new Date());
			RReceivingReport receivingReport = new RReceivingReport();
			receivingReport.setDivision(divisionService.getDivision(divisionId));
			receivingReport.setDivisionId(divisionId);
			apInvoice.setReceivingReport(receivingReport);
		}
		apInvoice.getReceivingReport().serializeSerialItems();
		apInvoice.serializeReferenceDocuments();
		apInvoice.serializeRrItems();
		apInvoice.serializeInvoiceLines();
		return loadRRForm(apInvoice, user, model);
	}

	private String loadRRForm (APInvoice apInvoice, User user, Model model) {
		logger.info("Loading retail receiving report form");
		//Companies
		apInvoice.setInvoiceTypeId(receivingReportService.getRrApInvoiceType(apInvoice.getReceivingReport().getDivisionId()));
		loadCompanyList(user, model);
		model.addAttribute("terms", termService.getTerms(user));
		model.addAttribute(RR_ATTRIBUTE_NAME, apInvoice);
		if(apInvoice.getReceivingReport().getDivisionId() == null) {
			return "R_ReceivingReportForm.jsp";
		}
		return "ReceivingReportForm.jsp";
	}

	@RequestMapping(value="{divisionId}/form", method=RequestMethod.POST)
	public String saveReceivingReport (@PathVariable(value="divisionId") int divisionId,
			@ModelAttribute("apInvoice") APInvoice apInvoice, BindingResult result, Model model,
			HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Saving retail receiving report form");
		apInvoice.deserializeRrItems();
		apInvoice.deserializeInvoiceLines();
		apInvoice.getReceivingReport().deserializeSerialItems();
		apInvoice.deserializeReferenceDocuments();
		synchronized (this) {
			validator.validate(apInvoice, result);
			if (result.hasErrors()) {
				//Reload form
				RReceivingReport rr = apInvoice.getReceivingReport();
				if (rr.getCompanyId() != null) {
					rr.setApInvoice(apInvoice);
					rr.setCompany(companyService.getCompany(rr.getCompanyId()));
					apInvoice.setReceivingReport(rr);
				}
				if (apInvoice.getFormWorkflowId() != null) {
					apInvoice.setFormWorkflow(invoiceService.getFormWorkflow(apInvoice.getId()));
				}
				return loadRRForm(apInvoice, user, model);
			}
			if (apInvoice.getId() == 0) {
				apInvoice.setFormWorkflow(null);
			}
			ebFormServiceHandler.saveForm(apInvoice, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", apInvoice.getSequenceNumber());
		model.addAttribute("formId", apInvoice.getId());
		logger.info("Successfully save receiving report details");
		model.addAttribute("ebObjectId", apInvoice.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value = "{divisionId}/form/viewForm", method = RequestMethod.GET)
	public String showForm (@PathVariable(value="divisionId") int divisionId,
			@RequestParam (value="pId", required = false)Integer pId, Model model) throws IOException {
		logger.info("Retreiving receiving report form");
		APInvoice apInvoice = null;
		if (pId != null){
			logger.debug("Retreive receiving report of form id " + pId);
			apInvoice = invoiceService.processReceivingReportAndItems(pId);
		} else {
			logger.error("Retail receiving report form id is required.");
			throw new RuntimeException("Retail receiving report form id is required.");
		}
		model.addAttribute(RR_ATTRIBUTE_NAME, apInvoice);
		logger.info("Successfully loaded retail receiving report form");
		if(divisionId != 0) {
			return "ReceivingReportFormView.jsp";
		}
		return "R_ReceivingReportFormView.jsp";
	}

	@RequestMapping(value = "/getLatestUc", method = RequestMethod.GET)
	public @ResponseBody String getLatestUC(@RequestParam(value="itemId") int itemId,
			@RequestParam(value="warehouseId") int warehouseId,
			@RequestParam(value="supplierAcctId", required=false) Integer supplierAcctId,
			@RequestParam(value="isSerial", required=false, defaultValue = "false") Boolean isSerial) {
		logger.info("Retrieving the latest unit cost of item id "+itemId+" under supplier account: "+supplierAcctId);
		double unitCost = supplierAcctId != null ? receivingReportService.getLatestUcPerSupplierAcct(itemId, warehouseId, supplierAcctId, isSerial)
				: receivingReportService.getLatestUcPerWarehouse(itemId, warehouseId, isSerial);
		return NumberFormatUtil.formatNumber(unitCost, 10);
	}

	@RequestMapping(value="{divisionId}/poReference", method=RequestMethod.GET)
	public String showPOReference(@PathVariable(value="divisionId") int divisionId, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadCompanyList(user, model);
		loadPOReference(model, user, PageSetting.START_PAGE, null, divisionId, null, null, null,  null, null, RReceivingReport.STATUS_UNUSED);
		return "POReference.jsp";
	}

	private void loadPOReference(Model model, User user, int pageNumber, Integer companyId, Integer divisionId,
			Integer supplierId, String poNumber, String bmsNumber, Date dateFrom, Date dateTo, Integer status) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("rPurchaseOrders", purchaseOderService.getPurchaseOrders(user, companyId, divisionId, supplierId, poNumber, bmsNumber, dateFrom, dateTo, status, pageNumber));
	}

	private void loadCompanyList(User user, Model model){
		model.addAttribute("companies", companyService.getCompanies(user));
	}

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String populateReturnToSupplier (@RequestParam (value="poId", required=true) Integer poId,
			Model model, HttpSession session) throws IOException {
		// Populating return to supplier
		logger.info("populating receiving report");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		RReceivingReport rr = receivingReportService.getAndConvertPO(poId, user);
		String[] excludes = {"itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices",
				"buyingAddOns", "buyingDiscounts", "rItemDetails", "accountSetups", "formattedRRNumber"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(rr, jsonConfig);
		return rr == null ? "No RR found" : jsonObject.toString();
	}

	@RequestMapping(value = "/loadPos", method = RequestMethod.GET)
	public String loadPurchaseOrder(@RequestParam(value="poNumber") String poNumber,
			@RequestParam(value="bmsNumber", required=false) String bmsNumber,
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam(value="supplierId") Integer supplierId,
			@RequestParam(value="dateFrom") Date dateFrom,
			@RequestParam(value="dateTo") Date dateTo,
			@RequestParam(value="status") Integer status,
			@RequestParam(value="pageNumber") int pageNumber,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadPOReference(model, user, pageNumber, companyId, divisionId, supplierId, poNumber, bmsNumber, dateFrom, dateTo, status);
		return "POReferenceTable.jsp";
	}
}
