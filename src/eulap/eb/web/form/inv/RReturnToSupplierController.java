package eulap.eb.web.form.inv;

import java.io.IOException;
import java.io.InvalidClassException;
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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.RReceivingReportService;
import eulap.eb.service.RReturnToSupplierService;
import eulap.eb.validator.inv.RReturnToSupplierValidator;
import eulap.eb.web.dto.ApInvoiceDto;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * The entry point of retail return to supplier form.


 */

@Controller
@RequestMapping(value="/retailReturnToSupplier")
public class RReturnToSupplierController {
	private static final Logger logger = Logger.getLogger(RReturnToSupplierController.class);
	private static final String RRTS_ATTRIBUTE_NAME = "apInvoice";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private RReturnToSupplierValidator validator;
	@Autowired
	private RReturnToSupplierService rtsService;
	@Autowired
	private RReceivingReportService rrService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private DivisionService divisionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (value="{typeId}/{divisionId}/form", method=RequestMethod.GET)
	public String showRTSForm (@PathVariable (value="typeId") int typeId,
			@PathVariable (value="divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) {
		APInvoice apInvoice = new APInvoice();
		if (pId != null) {
			apInvoice = rtsService.getRtsApInvoice(pId);
		} else {
			apInvoice.setReturnToSupplier(new RReturnToSupplier());
			apInvoice.setGlDate(new Date());
			apInvoice.setInvoiceDate(new Date());
			apInvoice.setInvoiceTypeId(typeId);
			apInvoice.setDivisionId(divisionId);
			apInvoice.setDivision(divisionService.getDivision(divisionId));
		}
		return loadForm(apInvoice, model);
	}

	private String loadForm(APInvoice apInvoice, Model model) {
		apInvoice.serializeSerialItems();
		apInvoice.serializeRtsItems();
		apInvoice.serializeInvoiceLines();
		apInvoice.serializeReferenceDocuments();
		model.addAttribute(RRTS_ATTRIBUTE_NAME, apInvoice);
		if (apInvoice.getInvoiceTypeId() == InvoiceType.RTS_TYPE_ID) {
			return "ReturnToSupplierForm.jsp";
		}
		return "ReturnToSupplierFormV2.jsp";
	}

	@RequestMapping(value="{typeId}/{divisionId}/form", method=RequestMethod.POST)
	public String saveReturnToSupplier(@PathVariable (value="typeId") int typeId,
			@PathVariable (value="divisionId") int divisionId,
			@ModelAttribute("apInvoice") APInvoice apInvoice,
			BindingResult result, Model model, HttpSession session) throws CloneNotSupportedException,
			InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		apInvoice.deserializeRtsItems();
		apInvoice.deserializeSerialItems();
		apInvoice.deserializeInvoiceLines();
		apInvoice.deserializeReferenceDocuments();
		synchronized (this) {
			validator.validateForm(apInvoice, result);
			if (result.hasErrors()) {
				return loadForm(apInvoice, model);
			}
			ebFormServiceHandler.saveForm(apInvoice, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("ebObjectId", apInvoice.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value = "/viewForm", method = RequestMethod.GET)
	public String showForm (@RequestParam (value="pId", required = false) Integer pId, Model model) {
		logger.info("Retreiving return to supplier form");
		APInvoice apInvoice =  null;
		if (pId != null) {
			apInvoice = rtsService.getRtsApInvoice(pId);
		} else {
			logger.error("Retail return to supplier form id is required.");
			throw new RuntimeException("Retail return to supplier form id is required.");
		}
		model.addAttribute(RRTS_ATTRIBUTE_NAME, apInvoice);
		logger.info("Successfully loaded retail return to supplier form");
		if (apInvoice.getInvoiceTypeId() == InvoiceType.RTS_TYPE_ID) {
			return "ReturnToSupplierForm.jsp";
		}
		return "ReturnToSupplierViewV2.jsp";
	}

	@RequestMapping(value="/rtsReference", method=RequestMethod.GET)
	public String showRrReference(Model model, HttpSession session) {
		loadReferences(model, session, PageSetting.START_PAGE, null, null, null, null, null, null, RReceivingReport.STATUS_UNUSED);
		return "R_RRReference.jsp";
	}

	private void loadReferences(Model model, HttpSession session, int pageNumber, Integer companyId,
			Integer warehouseId, Integer supplierId, String rrNumber, Date dateFrom, Date dateTo, Integer status) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("receivingReports", rrService.getReceivingReports(user, companyId, warehouseId, 
				supplierId, rrNumber, dateFrom, dateTo, status, pageNumber));
	}

	@RequestMapping(value="/rtsReference", method=RequestMethod.GET, params={"companyId", "warehouseId", "supplierId",
			"rrNumber", "dateFrom", "dateTo", "status", "pageNumber"})
	public String showRrReferenceTable (@RequestParam Integer companyId, @RequestParam Integer warehouseId, @RequestParam Integer supplierId,
			@RequestParam String rrNumber, @RequestParam String dateFrom, @RequestParam String dateTo,
			@RequestParam Integer status, @RequestParam Integer pageNumber, Model model, HttpSession session) {
		loadReferences(model, session, pageNumber, companyId, warehouseId, supplierId, rrNumber,
				DateUtil.parseDate(dateFrom), DateUtil.parseDate(dateTo), status);
		return "R_RRReferenceTable.jsp";
	}

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String populateReturnToSupplier (@RequestParam (value="receivingReportId", required=true) Integer receivingReportId,
			Model model, HttpSession session) {
		// Populating return to supplier
		logger.info("populating return to supplier");
		RReturnToSupplier rts = rtsService.getAndConvertRR(receivingReportId);
		String [] exclude = {"item", "buyingDiscounts"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(rts, jsonConfig);
		return rts == null ? "No RTS found" : jsonObject.toString();
	}

	@RequestMapping(value="/showInvGsReferences/{divisionId}", method=RequestMethod.GET)
	public String showRrReferenceForm(@PathVariable(value="divisionId") int divisionId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = rtsService.getCompanies(user, 0);
		model.addAttribute("companies", companies);
		model.addAttribute("divisionId", divisionId);
		Integer companyId = companies != null ? companies.iterator().next().getId() : null;
		loadInvGsReferenceForms(companyId, divisionId, null, null, null, null, null, null, APInvoice.STATUS_UNUSED,
				PageSetting.START_PAGE, model);
		return "InvGsReference.jsp";
	}

	private void loadInvGsReferenceForms(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAcctId, Integer invGsNumber, String bmsNumber, Date dateFrom, Date dateTo, Integer status,
			Integer pageNumber, Model model) {
		Page<ApInvoiceDto> invoiceGs = rtsService.getInvGsReferences(companyId, divisionId,
				supplierId, supplierAcctId, invGsNumber, bmsNumber, dateFrom, dateTo, status, pageNumber);
		model.addAttribute("invoiceGs", invoiceGs);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getInvGsReferences", method=RequestMethod.GET)
	public String getReceivingReports(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="supplierId", required=false) Integer supplierId,
			@RequestParam (value="supplierAcctId", required=false) Integer supplierAcctId,
			@RequestParam (value="invGsNumber", required=false) Integer invGsNumber,
			@RequestParam (value="bmsNumber", required=false) String bmsNumber,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="status", required=false) Integer status,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		loadInvGsReferenceForms(companyId, divisionId, supplierId, supplierAcctId, invGsNumber, bmsNumber,
				dateFrom, dateTo, status, pageNumber, model);
		return "InvGsReferenceTable.jsp";
	}

	@RequestMapping(value="/loadInvGsReference",method=RequestMethod.GET)
	public @ResponseBody String loadRrRefence(@RequestParam (value="apInvoiceId", required=true) Integer apInvoiceId,
			Model model, HttpSession session) throws IOException {
		APInvoice apInvoice = rtsService.convertInvGsToRts(apInvoiceId);
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
