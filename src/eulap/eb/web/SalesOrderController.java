package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.SOTypeService;
import eulap.eb.service.SalesOrderService;
import eulap.eb.service.SalesQuotationService;
import eulap.eb.service.TermService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class for {@link SalesOrder}

 *
 */
@Controller
@RequestMapping(value="/salesOrder")
public class SalesOrderController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private SalesQuotationService salesQuotationService;
	@Autowired
	private SalesOrderService salesOrderService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private TermService termService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SOTypeService soTypeService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private WorkflowServiceHandler workflowService;
	@Autowired
	private ArCustomerService arCustomerService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{divisionId}/form", method = RequestMethod.GET)
	public String showForm(@PathVariable(value="divisionId") int divisionId,
			@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDivision(divisionService.getDivision(divisionId));
		salesOrder.setDivisionId(divisionId);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (pId != null) {
			salesOrder = salesOrderService.getSalesOrder(pId, true);
			Integer ebObjectId = salesOrder.getEbObjectId();
			if(ebObjectId != null) {
				salesOrder.setReferenceDocuments(refDocumentService.processReferenceDocs(ebObjectId));
			}
		} else {
			salesOrder.setDate(new Date());
			salesOrder.setDeliveryDate(new Date());
		}
		return loadForm(salesOrder, user, model);
	}

	public String loadForm(SalesOrder salesOrder, User user, Model model) {
		int companyId = salesOrder.getCompanyId() != null ? salesOrder.getCompanyId() : 0;
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		salesOrder.serializeSOItems();
		salesOrder.serializeSOLines();
		salesOrder.serializeSOTLines();
		salesOrder.serializeSOELines();
		salesOrder.serializeReferenceDocuments();
		model.addAttribute("terms", termService.getTerms(salesOrder.getTermId()));
		model.addAttribute("soTypes", soTypeService.getActiveSOTypes(salesOrder.getSoTypeId()));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(salesOrder.getCurrencyId()));
		model.addAttribute("salesOrder", salesOrder);
		if(salesOrder.getDivisionId() == null) {
			return "SalesOrderForm.jsp";
		}
		return "NSBSalesOrderForm.jsp";
	}

	@RequestMapping(value = "{divisionId}/form", method = RequestMethod.POST)
	public String saveForm(@PathVariable(value="divisionId") int divisionId,
			@ModelAttribute ("salesOrder") SalesOrder salesOrder, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		salesOrder.deserializeSOItems();
		salesOrder.deserializeSOLines();
		salesOrder.deserializeSOTLines();
		salesOrder.deserializeSOELines();
		salesOrder.deserializeReferenceDocuments();
		synchronized (this) {
			salesOrderService.validateForm(salesOrder, result);
			if (result.hasErrors()) {
				return loadForm(salesOrder, user, model);
			}
			ebFormServiceHandler.saveForm(salesOrder, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formId", salesOrder.getId());
		model.addAttribute("ebObjectId", salesOrder.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/sqReference", method=RequestMethod.GET)
	public String showSQReference(@RequestParam (value="companyId", required=false) Integer companyId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSQReferences(model, PageSetting.START_PAGE, companyId, null, null, null, null, null, user);
		return "SQReference.jsp";
	}

	@RequestMapping(value="/sqReference/reload", method=RequestMethod.GET)
	public String showSQReferenceTable (
			@RequestParam (value="companyId") Integer companyId, 
			@RequestParam (value="arCustomerId", required=false) Integer arCustomerId, 
			@RequestParam (value="arCustomerAccountId", required=false) Integer arCustomerAccountId,
			@RequestParam (value="sqNumber", required=false) Integer sqNumber, 
			@RequestParam (value="dateFrom", required=false) String dateFrom, 
			@RequestParam (value="dateTo", required=false) String dateTo, 
			@RequestParam (value="pageNumber") Integer pageNumber, 
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSQReferences(model, pageNumber, companyId, arCustomerId,
				arCustomerAccountId, sqNumber, dateFrom, dateTo, user);
		return "SQReferenceTable.jsp";
	}

	private void loadSQReferences(Model model, Integer pageNumber, Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer asNumber, String dateFrom, String dateTo, User user) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("salesQuotations", salesQuotationService.getSalesQuotations(companyId, arCustomerId,
				arCustomerAccountId, asNumber, DateUtil.parseDate(dateFrom),DateUtil.parseDate(dateTo), pageNumber, user));
	}

	@RequestMapping(value="/loadSQReference", method=RequestMethod.GET)
	public @ResponseBody String setSalesOrder (@RequestParam (value="sqId", required=true) Integer sqId,
			Model model, HttpSession session) {
		SalesOrder salesOrder = salesOrderService.conv2SalesOrder(sqId);
		String [] exclude = {"item"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(salesOrder, jsonConfig);
		return salesOrder == null ? "No transaction found" : jsonObject.toString();
	}

	@RequestMapping(value = "/viewForm", method = RequestMethod.GET)
	public String showViewForm (@RequestParam(value="pId", required = false) Integer pId, Model model, HttpSession session) throws ConfigurationException, IOException {
		SalesOrder salesOrder = salesOrderService.getSalesOrder(pId, true);
		if(pId != null) {
			if(salesOrder.getEbObjectId() != null) {
				salesOrder.setReferenceDocuments(refDocumentService.processReferenceDocs(salesOrder.getEbObjectId()));
			}
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(salesOrder.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(pId, SalesOrder.class.getSimpleName()+salesOrder.getDivisionId(), user));
		}
		model.addAttribute("salesOrder", salesOrder);
		if(salesOrder.getDivisionId() == null) {
			return "SalesOrderView.jsp";
		}
		return "NSBSalesOrderView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, salesOrderService.getFormWorkflow(pId));
	}

	@RequestMapping(value="/printout", method = RequestMethod.GET)
	public String generatePrintout(@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session) {
		SalesOrder salesOrder = salesOrderService.getSalesOrder(pId, true);
		List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
		salesOrders.add(salesOrder);
		JRDataSource dataSource = new JRBeanCollectionDataSource(salesOrders);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		model.addAttribute("reportTitle", "SALES ORDER");
		model.addAttribute("companyAddress", salesOrder.getCompany().getAddress());
		model.addAttribute("companyLogo", salesOrder.getCompany().getLogo());
		model.addAttribute("sequenceNumber", salesOrder.getSequenceNumber());
		model.addAttribute("date", salesOrder.getDate());
		model.addAttribute("customerName", salesOrder.getArCustomer().getName());
		model.addAttribute("customerAccount", salesOrder.getArCustomerAccount().getName());
		model.addAttribute("divisionName", salesOrder.getDivision().getName());
		model.addAttribute("address", arCustomerService.getArCustomerFullAddress(salesOrder.getArCustomer()));
		model.addAttribute("tin", StringFormatUtil.processBirTinTo13Digits(salesOrder.getArCustomer().getTin()));
		model.addAttribute("remarks", salesOrder.getRemarks());
		model.addAttribute("currencyName", salesOrder.getCurrency().getName());
		model.addAttribute("deliveryDate", salesOrder.getDeliveryDate());
		model.addAttribute("soType", salesOrder.getSoType().getName());
		model.addAttribute("poNumber", salesOrder.getPoNumber());
		model.addAttribute("term", salesOrder.getTerm().getName());
		double totalVAT = salesOrderService.getConvertedTotalVat(pId);
		model.addAttribute("totalVAT", totalVAT);
		model.addAttribute("subTotal", salesOrderService.getConvertedSubtotal(pId));
		String wtAcctName = salesOrder.getWtAcctSetting() != null ? salesOrder.getWtAcctSetting().getName() : "";
		model.addAttribute("wtAccount", wtAcctName);
		double wtAmount = salesOrder.getWtAmount() != null ? salesOrder.getWtAmount() : 0.0;
		model.addAttribute("wtVatAmount", salesOrder.getWtVatAmount() != null ? salesOrder.getWtVatAmount() : 0.0);
		model.addAttribute("wtAmount", wtAmount);
		model.addAttribute("total", salesOrder.getAmount());

		FormWorkflow workflowLog = salesOrder.getFormWorkflow();
		for (FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", log.getCreated().getUserFullName());
				model.addAttribute("createdByPosition", log.getCreated().getPosition().getName());
			}
			if(log.getFormStatusId() == FormStatus.REVIEWED_ID) {
				model.addAttribute("approvedBy", log.getCreated().getUserFullName());
				model.addAttribute("approvedByPosition", log.getCreated().getPosition().getName());
			}
		}
		model.addAttribute("fromName", "Nonito M. Cabais");
		model.addAttribute("fromPosition", "President / General Manager");
		if(salesOrder.getDivisionId() == null) {
			return "SalesOrderForm.jasper";
		}
		return "NSBSalesOrderForm.jasper";
	}

	@RequestMapping(value = "/search/{typeId}", method = RequestMethod.GET)
	public @ResponseBody String searchForms(@PathVariable("typeId") int typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String searchCriteria,
			HttpSession session) {
		List<FormSearchResult> result = salesOrderService.getSOForms(typeId, searchCriteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/getCustomerShipTo", method = RequestMethod.GET)
	public @ResponseBody String getCustomerShipTo(@RequestParam(value="arCustomerId", required=false) Integer arCustomerId,
			HttpSession session) {
		String shipTo = salesOrderService.getCustomerShipTo(arCustomerId);
		return shipTo != null ? shipTo : "";
	}
}