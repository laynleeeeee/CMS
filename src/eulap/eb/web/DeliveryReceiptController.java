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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.DeliveryReceiptType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SOType;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.AuthorityToWithdrawService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DeliveryReceiptService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.SalesOrderService;
import eulap.eb.service.TermService;
import eulap.eb.web.dto.AtwItemDto;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * Controller for {@link DeliveryReceipt}

 */

@Controller
@RequestMapping("/deliveryReceipt")
public class DeliveryReceiptController {
	@Autowired
	private DeliveryReceiptService deliveryReceiptService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private AuthorityToWithdrawService atwService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private TermService termService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private SalesOrderService salesOrderService;

	private static final String DELIVERY_RECEIPT_ATTRIB_NAME = "deliveryReceipt";
	private static final String DELIVERY_RECEIPT_REPORT_NAME = "DELIVERY RECEIPT - GOODS";
	private static final String WAYBILL_REPORT_NAME = "WAYBILL";
	private static final String EQUIPMENT_UTILIZATION_REPORT_NAME = "EQUIPMENT UTILIZATION";
	private static final String DELIVERY_RECEIPT_SERVICE_REPORT_NAME = "DELIVERY RECEIPT - SERVICE";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/{typeId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		DeliveryReceipt deliveryReceipt = null;
		if (pId == null) {
			deliveryReceipt = new DeliveryReceipt();
			if(typeId >= DeliveryReceiptType.DR_CENTRAL_TYPE_ID) {
				Integer divisionId = deliveryReceiptService.getDivisionByDrTypeId(typeId);
				deliveryReceipt.setDivisionId(divisionId);
				deliveryReceipt.setDivision(divisionService.getDivision(divisionId));
			}
			deliveryReceipt.setDate(new Date());
		} else {
			deliveryReceipt = deliveryReceiptService.getDeliveryReceipt(pId, true, true, typeId);
			deliveryReceiptService.processRefDoc(deliveryReceipt);
		}
		deliveryReceipt.setDeliveryReceiptTypeId(typeId);
		return loadForm(deliveryReceipt, model, user, typeId);
	}

	private String loadForm(DeliveryReceipt deliveryReceipt, Model model, User user, Integer typeId) {
		deliveryReceipt.serializeSerialDRItems();
		deliveryReceipt.serializeNonSerialDRItems();
		deliveryReceipt.serializeDRLines();
		deliveryReceipt.serializeReferenceDocuments();
		model.addAttribute(DELIVERY_RECEIPT_ATTRIB_NAME, deliveryReceipt);
		model.addAttribute("typeId", typeId);
		model.addAttribute("isStockIn", false);
		String form = "";
		if (typeId == DeliveryReceiptType.WAYBILL_DR_TYPE_ID) {
			deliveryReceipt.serializeWBLines();
			form = "WaybillForm.jsp";
		} else if (typeId == DeliveryReceiptType.DR_TYPE_ID) {
			form = "DeliveryReceiptForm.jsp";
		} else if (typeId == DeliveryReceiptType.EQ_UTIL_DR_TYPE_ID) {
			deliveryReceipt.serializeEULines();
			form = "EquipmentUtilizationForm.jsp";
		} else if (typeId == DeliveryReceiptType.DR_SERVICE_TYPE_ID) {
			form = "DeliveryReceiptServiceForm.jsp";
		} else if(typeId >= DeliveryReceiptType.DR_CENTRAL_TYPE_ID) {
			form = "DeliveryReceiptNsbForm.jsp";
		}
		return form;
	}

	@RequestMapping(value="/showReferenceForm", method=RequestMethod.GET)
	public String showReferenceForm(@RequestParam(value="drTypeId", required=false) Integer drTypeId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = (List<Company>) companyService.getActiveCompanies(user, null, null, null);
		model.addAttribute("companies", companies);
		model.addAttribute("divisionId", deliveryReceiptService.getDivisionByDrTypeId(drTypeId));
		loadReferences((companies != null ? companies.iterator().next().getId() : null), null, null, null,
				SalesOrder.STATUS_UNUSED, null, null, PageSetting.START_PAGE, user, model, drTypeId, null, divisionId);
		return "DrSalesOrderReference.jsp";
	}

	private void loadReferences(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer atwNumber, Integer statusId, Date dateFrom, Date dateTo, Integer pageNumber,
			User user, Model model, Integer drTypeId, String poNumber, Integer divisionId) {
		Page<SalesOrder> salesOrders = deliveryReceiptService.getSalesOrderRefs(companyId,
				arCustomerId, arCustomerAccountId, atwNumber, statusId, dateFrom, dateTo,
				pageNumber, drTypeId, poNumber, divisionId);
		model.addAttribute("salesOrders", salesOrders);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getSaleOrderForms", method = RequestMethod.GET)
	public String getATWReferenceForms(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="arCustomerId", required=false) Integer arCustomerId,
			@RequestParam(value="arCustomerAcctId", required=false) Integer arCustomerAccountId,
			@RequestParam(value="soNumber", required=false) Integer soNumber,
			@RequestParam(value="status", required=false) Integer status,
			@RequestParam(value="drTypeId", required=false) Integer drTypeId,
			@RequestParam(value="pageNumber", required=false) int pageNumber,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam(value="poNumber", required=false) String poNumber,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadReferences(companyId, arCustomerId, arCustomerAccountId, soNumber, status, dateFrom, dateTo,
				pageNumber, user, model, drTypeId, poNumber, divisionId);
		return "DrSalesOrderReferenceTbl.jsp";
	}

	@RequestMapping(value="/convertSOtoDR", method=RequestMethod.GET)
	public @ResponseBody String convertSOtoDR(@RequestParam (value="salesOrderId") int salesOrderId,
			@RequestParam(value="drTypeId", required=false) Integer drTypeId,
			Model model, HttpSession session) {
		DeliveryReceipt dr = deliveryReceiptService.convertSOtoDR(salesOrderId, drTypeId);
		String [] excludes = {"glDate", "itemCategory", "itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices",
				"buyingAddOns", "buyingDiscounts", "rItemDetails", "formWorkflow"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(dr, jsonConfig);
		return dr == null ? "No ATW reference found" : jsonObject.toString();
	}

	@RequestMapping(value="/getRefAtwItemDetails", method=RequestMethod.GET)
	public @ResponseBody String getSOGrossPrice(@RequestParam (value="itemRefObjectId") Integer itemRefObjectId,
			Model model, HttpSession session) {
		AtwItemDto dto = atwService.getAtwItemByRefItemObjectId(itemRefObjectId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(dto, jsonConfig);
		return dto == null ? "No ATW reference found" : jsonObject.toString();
	}

	@RequestMapping(value="/{typeId}/form", method = RequestMethod.POST)
	public String submitForm(@PathVariable(value="typeId") int typeId,
			@ModelAttribute ("deliveryReceipt") DeliveryReceipt deliveryReceipt, BindingResult result,
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		deliveryReceipt.deserializeSerialDRItems();
		deliveryReceipt.deserializeNonSerialDRItems();
		deliveryReceipt.deserializeDRLines();
		deliveryReceipt.deserializeWBLines();
		deliveryReceipt.deserializeEULines();
		deliveryReceipt.deserializeReferenceDocuments();
		synchronized (this) {
			deliveryReceiptService.validate(deliveryReceipt, result);
			if (result.hasErrors()) {
				return loadForm(deliveryReceipt, model, user, typeId);
			}
			ebFormServiceHandler.saveForm(deliveryReceipt, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", deliveryReceipt.getSequenceNo());
		model.addAttribute("formId", deliveryReceipt.getId());
		model.addAttribute("ebObjectId", deliveryReceipt.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping (value="/{typeId}/view", method = RequestMethod.GET)
	public String viewDeliveryReceipt(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		DeliveryReceipt deliveryReceipt = deliveryReceiptService.getDeliveryReceipt(pId, true, true, typeId);
		deliveryReceiptService.processRefDoc(deliveryReceipt);
		model.addAttribute("companyName", companyService.getCompany(deliveryReceipt.getCompanyId()).getName());
		model.addAttribute("deliveryReceipt", deliveryReceipt);
		String viewForm = "";
		if(DeliveryReceiptType.DR_TYPE_ID == typeId) {
			viewForm = "DeliveryReceiptView.jsp";
		} else if(DeliveryReceiptType.WAYBILL_DR_TYPE_ID == typeId) {
			viewForm ="WaybillView.jsp";
		} else if(DeliveryReceiptType.EQ_UTIL_DR_TYPE_ID == typeId) {
			viewForm ="EquipmentUtilizationView.jsp";
		} else if(DeliveryReceiptType.DR_SERVICE_TYPE_ID == typeId) {
			viewForm = "DeliveryReceiptServiceView.jsp";
		} else if(DeliveryReceiptType.DR_CENTRAL_TYPE_ID <= typeId) {
			viewForm = "DeliveryReceiptNsbView.jsp";
		}
		return viewForm;
	}

	@RequestMapping(value="/{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchOrderSlips(@PathVariable(value="typeId") int typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = deliveryReceiptService.searchDeliveryReceipts(typeId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/{typeId}/pdf", method = RequestMethod.GET)
	public String generatePrintout(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session) {
		DeliveryReceipt deliveryReceipt = deliveryReceiptService.getDeliveryReceipt(pId, true, true, typeId);
		List<DeliveryReceipt> deliveryReceipts = new ArrayList<DeliveryReceipt>();
		deliveryReceipts.add(deliveryReceipt);
		JRDataSource dataSource = new JRBeanCollectionDataSource(deliveryReceipts);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		model.addAttribute("remarks", deliveryReceipt.getRemarks());
		model.addAttribute("date", deliveryReceipt.getDate());
		model.addAttribute("dueDate", deliveryReceipt.getDueDate());
		model.addAttribute("sequenceNo", deliveryReceipt.getSequenceNo());

		SalesOrder salesOrder = salesOrderService.getSalesOrder(deliveryReceipt.getSalesOrderId());
		model.addAttribute("poNumber", (salesOrder.getSoTypeId().equals(SOType.PO_SO_TYPE_ID) ? deliveryReceipt.getPoNumber() : "" ));
		salesOrder = null;

		model.addAttribute("salesPersonnelName", deliveryReceipt.getSalesPersonnelName());
		model.addAttribute("shipTo", deliveryReceipt.getRemarks());

		//Company
		Company company = companyService.getCompany(deliveryReceipt.getCompanyId());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyLogo", company.getLogo());
		//Customer/Project
		ArCustomerAccount arCustomerAcct = arCustomerAcctService.getAccount(deliveryReceipt.getArCustomerAccountId());
		model.addAttribute("customerAcct", arCustomerAcct.getName());
		ArCustomer customer = arCustomerAcct.getArCustomer();
		model.addAttribute("customerName", customer.getName());
		model.addAttribute("customerAddress", customer.getAddress());
		model.addAttribute("tin", StringFormatUtil.processBirTinTo13Digits(customer.getTin()));
		model.addAttribute("customerAddress", customer.getStreetBrgy() + ", " + customer.getCityProvince());
		//Term
		model.addAttribute("term", termService.getTerm(deliveryReceipt.getTermId()).getName());
		FormWorkflow workflowLog = deliveryReceipt.getFormWorkflow();
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			String name = log.getCreated().getUserFullName();
			String attrName = "";
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				attrName = "created";
			} else if (log.getFormStatusId() == FormStatus.REVIEWED_ID) {
				attrName = "reviewed";
			}
			model.addAttribute(attrName + "By", name);
		}
		model.addAttribute("receivedBy", deliveryReceipt.getReceiver());
		model.addAttribute("receivedDate", deliveryReceipt.getDateReceived());
		String jasper = "";
		String reportTitle = "";
		if(DeliveryReceiptType.DR_TYPE_ID == typeId) {
			reportTitle = DELIVERY_RECEIPT_REPORT_NAME;
			jasper = "DeliveryReceiptPdf.jasper";
		} else if(DeliveryReceiptType.WAYBILL_DR_TYPE_ID == typeId) {
			reportTitle = WAYBILL_REPORT_NAME;
			jasper = "WaybillPdf.jasper";
		} else if(DeliveryReceiptType.EQ_UTIL_DR_TYPE_ID == typeId) {
			reportTitle = EQUIPMENT_UTILIZATION_REPORT_NAME;
			jasper = "WaybillPdf.jasper";
		} else if(DeliveryReceiptType.DR_SERVICE_TYPE_ID == typeId) {
			reportTitle = DELIVERY_RECEIPT_SERVICE_REPORT_NAME;
			jasper = "DeliveryReceiptPdf.jasper";
		} else {
			jasper = "DeliveryReceiptPdfV2.jasper";
		}
		model.addAttribute("reportTitle", reportTitle);
		return jasper;
	}

	@RequestMapping(value="/showSOTruckingRefsForm", method=RequestMethod.GET)
	public String showSOTruckingRefsForm(@RequestParam (value="typeId") Integer typeId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = (List<Company>) companyService.getActiveCompanies(user, null, null, null);
		model.addAttribute("companies", companies);
		model.addAttribute("typeId", typeId);
		loadSOTruckingReferences((companies != null ? companies.iterator().next().getId() : null), null, null, null,
				SalesOrder.STATUS_UNUSED, null, null, PageSetting.START_PAGE, user, model, typeId);
		return "WbSoReference.jsp";
	}

	private void loadSOTruckingReferences(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer soNumber, Integer statusId, Date dateFrom, Date dateTo, Integer pageNumber,
			User user, Model model, Integer typeId) {
		Page<SalesOrder> salesOrders = deliveryReceiptService.getSOTruckingReferences(companyId,
				soNumber, arCustomerId, arCustomerAccountId, statusId, pageNumber, typeId);
		model.addAttribute("salesOrders", salesOrders);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/loadSOTReferenceForm", method=RequestMethod.GET)
	public @ResponseBody String loadSOTReferenceForm(@RequestParam (value="sotRefId") Integer sotRefId,
			@RequestParam (value="typeId") Integer typeId,
			Model model, HttpSession session) {
		DeliveryReceipt dr = deliveryReceiptService.convertSO(sotRefId, typeId);
		String [] excludes = {"glDate", "itemCategory", "itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices",
				"buyingAddOns", "buyingDiscounts", "rItemDetails", "formWorkflow"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(dr, jsonConfig);
		return dr == null ? "No SO reference found" : jsonObject.toString();
	}

	@RequestMapping(value="/getSOReferenceForms", method = RequestMethod.GET)
	public String getSOReferenceForms(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="arCustomerId", required=false) Integer arCustomerId,
			@RequestParam(value="arCustomerAcctId", required=false) Integer arCustomerAccountId,
			@RequestParam(value="soNumber", required=false) Integer soNumber,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam(value="status", required=false) Integer status,
			@RequestParam(value="drTypeId", required=false) Integer drTypeId,
			@RequestParam(value="pageNumber", required=false) int pageNumber,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSOTruckingReferences(companyId, arCustomerId, arCustomerAccountId, soNumber, status, dateFrom, dateTo,
				pageNumber, user, model, drTypeId);
		return "WbSoReferenceTable.jsp";
	}

	@RequestMapping(value="receiveDr/{formId}", method=RequestMethod.GET)
	public String receiveDrForm(@PathVariable(value="formId") int formId,
			Model model, HttpSession session) throws IOException {
		DeliveryReceipt deliveryReceipt = new DeliveryReceipt();
		deliveryReceipt.setDateReceived(new Date());
		return loadReceiveDrForm(formId, deliveryReceipt, model);
	}

	private String loadReceiveDrForm(int formId, DeliveryReceipt deliveryReceipt, Model model) {
		model.addAttribute("formId", formId);
		model.addAttribute("deliveryReceipt", deliveryReceipt);
		return "DrReceivingForm.jsp";
	}

	@RequestMapping (value="receiveDr/{formId}", method=RequestMethod.POST)
	public String saveCheckLog(@PathVariable(value="formId") int formId,
			@ModelAttribute("deliveryReceipt") DeliveryReceipt deliveryReceipt, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		synchronized (this) {
			deliveryReceiptService.validateStatusLogs(deliveryReceipt, result);
			if(result.hasErrors()) {
				return loadReceiveDrForm(formId, deliveryReceipt, model);
			}
			deliveryReceiptService.saveDrReceivingDetails(deliveryReceipt, user);
		}
		model.addAttribute("success", true);
		return "successfullySaved";
	}
}
