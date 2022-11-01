package eulap.eb.web;

import java.io.IOException;
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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ApInvoiceGsService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.SupplierService;
import eulap.eb.web.dto.ApInvoiceDto;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * Controller class for AP invoice goods/service form

 */

@Controller
@RequestMapping("apInvoiceGS")
public class ApInvoiceGSController {
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private ApInvoiceGsService apInvoiceGsService;
	@Autowired
	private SupplierService supplierService;

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/{divisionId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable("typeId") int typeId,
			@PathVariable("divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId, Model model,
			HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		APInvoice apInvoice = new APInvoice();
		apInvoice.setDivisionId(divisionId);
		apInvoice.setDivision(apInvoiceGsService.getDivision(divisionId));
		apInvoice.setInvoiceTypeId(typeId);
		if (pId != null) {
			apInvoice = apInvoiceGsService.getApInvoiceGs(pId);
		} else {
			Date currentDate = new Date();
			apInvoice.setInvoiceDate(currentDate);
			apInvoice.setGlDate(currentDate);
			apInvoice.setDueDate(currentDate);
		}
		return loadForm(apInvoice, user, model, typeId);
	}

	private String loadForm(APInvoice apInvoice, User user, Model model, int typeId) {
		apInvoice.serializeSerialItems();
		apInvoice.serializeApInvoiceGoods();
		apInvoice.serializeInvoiceLines();
		apInvoice.serializeReferenceDocuments();
		model.addAttribute("terms",  apInvoiceGsService.getTerms(apInvoice.getTermId()));
		model.addAttribute("currencies", apInvoiceGsService.getActiveCurrencies(apInvoice.getCurrencyId()));
		model.addAttribute("apInvoiceGs", apInvoice);
		return "ApInvoiceGoodsServicesForm.jsp";
	}

	@RequestMapping(value="{typeId}/{divisionId}/form", method=RequestMethod.POST)
	public String submitForm(@PathVariable("typeId") int typeId,
			@PathVariable("divisionId") int divisionId,
			@ModelAttribute("apInvoiceGs") APInvoice apInvoice, BindingResult result,
			Model model, HttpSession session) throws ClassNotFoundException, IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		apInvoice.deserializeSerialItems();
		apInvoice.deserializeApInvoiceGoods();
		apInvoice.deserializeInvoiceLines();
		apInvoice.deserializeReferenceDocuments();
		synchronized (this) {
			apInvoiceGsService.validateForm(apInvoice, result);
			if (result.hasErrors()) {
				return loadForm(apInvoice, user, model, typeId);
			}
			ebFormServiceHandler.saveForm(apInvoice, user);
		}
		model.addAttribute("ebObjectId", apInvoice.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping (value="/view", method = RequestMethod.GET)
	public String viewForm(@RequestParam(value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		APInvoice apInvoiceGs = apInvoiceGsService.getApInvoiceGs(pId);
		apInvoiceGs.setDivision(apInvoiceGsService.getDivision(apInvoiceGs.getDivisionId()));
		model.addAttribute("apInvoiceGs", apInvoiceGs);
		return "ApInvoiceGoodsServicesView.jsp";
	}

	@RequestMapping(value="/print", method = RequestMethod.GET)
	private String generatePrintout(@RequestParam(value="pId", required=true) Integer pId,
			Model model, HttpSession session) throws IOException {
		APInvoice apInvoice = apInvoiceGsService.getApInvoiceGs(pId);
		apInvoice.setDivision(apInvoiceGsService.getDivision(apInvoice.getDivisionId()));
		model.addAttribute("datasource", new JRBeanCollectionDataSource(apInvoiceGsService.getInvoicePrintoutDtos(pId)));
		setCommonParams(apInvoice, model, session);
		return "ApInvoiceGSMain.jasper";
	}

	public void setCommonParams(APInvoice apInvoice, Model model, HttpSession session) {
		model.addAttribute("format", "pdf");
		model.addAttribute("formName" , "Payment Voucher - PO");
		model.addAttribute("sequenceNumber", ""+apInvoice.getSequenceNumber());
		model.addAttribute("description", apInvoice.getDescription());
		model.addAttribute("invoiceNumber", apInvoice.getInvoiceNumber());
		model.addAttribute("glDate", apInvoice.getGLDate());
		model.addAttribute("invoiceDate", apInvoice.getInvoiceDate());
		model.addAttribute("dueDate", apInvoice.getDueDate());
		model.addAttribute("birLogo", "bir_logo.png");
		model.addAttribute("divisionName", apInvoice.getDivision().getName());
		model.addAttribute("bmsNumber", apInvoice.getBmsNumber());
		model.addAttribute("amount", apInvoice.getAmount());
		model.addAttribute("poNumber", apInvoice.getPoNumber());
		model.addAttribute("currencyName", apInvoiceGsService.getCurrency(apInvoice.getCurrencyId()).getName());

		setCompanyParams(apInvoice.getSupplierAccount().getCompany(), model);
		setSupplierParams(apInvoice.getSupplier(), model);
		setWorkflowParams(apInvoice.getFormWorkflow(), model);
	}

	private void setCompanyParams(Company company, Model model) {
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		String tin = company.getTin();
		model.addAttribute("companyTin", tin != null && !tin.isEmpty()
				? StringFormatUtil.processBirTinTo13Digits(tin) : "");
		model.addAttribute("companyContactNo", company.getContactNumber());
		model.addAttribute("companyLogo", company.getLogo());
	}

	private void setSupplierParams(Supplier supplier, Model model) {
		String name = "";
		if(supplier.getBussinessClassificationId().equals(1)) {
			if(!supplier.getLastName().isEmpty() && !supplier.getFirstName().isEmpty()) {
				name = supplier.getLastName()+", "+ supplier.getFirstName();
				if(!supplier.getMiddleName().isEmpty()) {
					name = supplier.getLastName()+", "+ supplier.getFirstName() +" "+ supplier.getMiddleName();
				}
			}
		} else {
			name = supplier.getName();
		}
		model.addAttribute("supplier", supplier.getName());
		model.addAttribute("supplierFullname", name);
		model.addAttribute("supplierAddress", supplierService.processSupplierAddress(supplier));
		String tin = supplier.getTin();
		model.addAttribute("supplierTin", tin != null && !tin.isEmpty()
				? StringFormatUtil.processBirTinTo13Digits(tin) : "");
	}

	private void setWorkflowParams(FormWorkflow formWorkflow, Model model) {
		for (FormWorkflowLog log : formWorkflow.getFormWorkflowLogs()) {
			String name = log.getCreated().getUserFullName();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", name);
				model.addAttribute("createdDate", log.getCreatedDate());
				model.addAttribute("creatorPosition", log.getCreated().getPosition().getName());
			} else if (log.getFormStatusId() == FormStatus.REVIEWED_ID) {
				model.addAttribute("verifiedBy", name);
				model.addAttribute("verifiedDate", log.getCreatedDate());
				model.addAttribute("verifierPosition", log.getCreated().getPosition().getName());
			}
		}
	}

	@RequestMapping(value="{divisionId}/showRrReferences", method=RequestMethod.GET)
	public String showRrReferenceForm(@PathVariable(value="divisionId") int divisionId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("divisionId", divisionId);
		List<Company> companies = apInvoiceGsService.getCompanies(user, 0);
		model.addAttribute("companies", companies);
		Integer companyId = companies != null ? companies.iterator().next().getId() : null;
		loadRrReferenceForms(companyId, divisionId, null, null, null, null, null, APInvoice.STATUS_UNUSED,
				PageSetting.START_PAGE, model);
		return "RrReference.jsp";
	}

	private void loadRrReferenceForms(Integer companyId, Integer divisionId, Integer supplierId,
			Integer rrNumber, String bmsNumber, Date dateFrom, Date dateTo, Integer status,
			Integer pageNumber, Model model) {
		Page<ApInvoiceDto> receivingReports = apInvoiceGsService.getRrReferences(companyId, divisionId,
				supplierId, rrNumber, bmsNumber, dateFrom, dateTo, status, pageNumber);
		model.addAttribute("receivingReports", receivingReports);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getReceivingReports", method=RequestMethod.GET)
	public String getReceivingReports(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="supplierId", required=false) Integer supplierId,
			@RequestParam (value="rrNumber", required=false) Integer rrNumber,
			@RequestParam (value="bmsNumber", required=false) String bmsNumber,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="status", required=false) Integer status,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		loadRrReferenceForms(companyId, divisionId, supplierId, rrNumber, bmsNumber,
				dateFrom, dateTo, status, pageNumber, model);
		return "RrReferenceTable.jsp";
	}

	@RequestMapping(value="/loadRrReference",method=RequestMethod.GET)
	public @ResponseBody String loadRrRefence(@RequestParam (value="rrReferenceId", required=true) Integer rrReferenceId,
			Model model, HttpSession session) throws IOException {
		APInvoice apInvoice = apInvoiceGsService.convertRrToInvoice(rrReferenceId);
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

	@RequestMapping(value="{typeId}/{divisionId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchInvoices(@PathVariable(value="typeId") int typeId,
			@PathVariable(value="divisionId") int divisionId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = apInvoiceGsService.searchInvoices(typeId, divisionId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
