package eulap.eb.web;

import java.io.InvalidClassException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.WorkOrderService;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * A controller class that will handle request for {@link WorkOrder}

 */

@Controller
@RequestMapping(value="/workOrder")
public class WorkOrderController {
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private WorkOrderService workOrderService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String showForm(@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) {
		WorkOrder workOrder = new WorkOrder();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (pId != null) {
			workOrder = workOrderService.getWorkOrder(pId, false, false);
		} else {
			Date currentDate = new Date();
			workOrder.setDate(currentDate);
			workOrder.setTargetEndDate(currentDate);
		}
		return loadForm(workOrder, user, model);
	}

	public String loadForm(WorkOrder workOrder, User user, Model model) {
		int companyId = workOrder.getCompanyId() != null ? workOrder.getCompanyId() : 0;
		model.addAttribute("companies", workOrderService.getCompanies(user, companyId));
		workOrder.serializeWoInstructions();
		workOrder.serializeWoItems();
		workOrder.serializeWoLines();
		workOrder.serializeReferenceDocuments();
		workOrder.serializeWoPurchasedItems();
		model.addAttribute("isStockIn", false);
		model.addAttribute("workOrder", workOrder);
		return "WorkOrderForm.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String saveForm(@ModelAttribute ("workOrder") WorkOrder workOrder, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		workOrder.deserializeWoInstructions();
		workOrder.deserializeWoItems();
		workOrder.deserializeWoLines();
		workOrder.deserializeReferenceDocuments();
		workOrder.deserializeWoPurchasedItems();
		workOrderService.validateForm(workOrder, result);
		if (result.hasErrors()) {
			return loadForm(workOrder, user, model);
		}
		ebFormServiceHandler.saveForm(workOrder, user);
		model.addAttribute("success", true);
		model.addAttribute("formId", workOrder.getId());
		model.addAttribute("ebObjectId", workOrder.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/showReferenceForm", method=RequestMethod.GET)
	public String showReferenceForm(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = workOrderService.getCompanies(user, 0);
		model.addAttribute("companies", companies);
		loadReferenceForms((companies != null ? companies.iterator().next().getId() : null), null, null,
				null, SalesOrder.STATUS_UNUSED, PageSetting.START_PAGE, model);
		return "WOSOReference.jsp";
	}

	private void loadReferenceForms(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, Integer pageNumber, Model model) {
		Page<SalesOrder> salesOrders = workOrderService.getSaleOrderForms(companyId, soNumber, arCustomerId,
				arCustomerAcctId, statusId, pageNumber);
		model.addAttribute("salesOrders", salesOrders);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getSaleOrderForms", method=RequestMethod.GET)
	public String getSaleOrderForms(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="soNumber", required=false) Integer soNumber,
			@RequestParam (value="arCustomerId", required=false) Integer arCustomerId,
			@RequestParam (value="arCustomerAcctId", required=false) Integer arCustomerAcctId,
			@RequestParam (value="status", required=false) Integer statusId,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		loadReferenceForms(companyId, soNumber, arCustomerId, arCustomerAcctId, statusId, pageNumber, model);
		return "WOSOReferenceTable.jsp";
	}

	@RequestMapping(value="/loadSOReferenceForm",method=RequestMethod.GET)
	public @ResponseBody String loadSOReferenceForm(@RequestParam (value="woSoRefId", required=true) Integer woSoRefId,
			Model model, HttpSession session) {
		WorkOrder workOrder = workOrderService.convertSOtoWO(woSoRefId);
		JsonConfig jsonConfig = new JsonConfig();
		String [] excludes = {"glDate", "itemCategory", "itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices",
				"buyingAddOns", "buyingDiscounts", "rItemDetails", "soItems", "soLines", "formWorkflow"};
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(workOrder, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value = "/viewForm", method = RequestMethod.GET)
	public String showViewForm (@RequestParam(value="pId", required = false) Integer pId, Model model) {
		WorkOrder workOrder = workOrderService.getWorkOrder(pId, false, true);
		model.addAttribute("workOrder", workOrder);
		return "WorkOrderView.jsp";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String searchForms(@RequestParam(required=true, value="criteria", defaultValue="") String searchCriteria,
			HttpSession session) {
		List<FormSearchResult> result = workOrderService.retrieveForms(searchCriteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/printout", method = RequestMethod.GET)
	public String generatePrintout(@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session) {
		JRDataSource dataSource = new JRBeanCollectionDataSource(workOrderService.getWorkOrderDtos(pId));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		// Set print detail
		WorkOrder workOrder = workOrderService.getWorkOrder(pId, true, false);
		String reportTitle = (workOrder.getRefWorkOrderId() != null ? "SUB-" : "") + "WORK ORDER";
		model.addAttribute("reportTitle", reportTitle);
		model.addAttribute("companyAddress", workOrder.getCompany().getAddress());
		model.addAttribute("companyLogo", workOrder.getCompany().getLogo());
		model.addAttribute("customerName", workOrder.getArCustomer().getName());
		model.addAttribute("customerAddress", workOrder.getArCustomer().getAddress());
		model.addAttribute("customerAcct", workOrder.getArCustomerAccount().getName());
		FormWorkflow workflowLog = workOrder.getFormWorkflow();
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			String name = log.getCreated().getLastName() + ", " + log.getCreated().getFirstName();
			String position = log.getCreated().getPosition().getName();
			Date createdDate = log.getCreatedDate();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", name);
				model.addAttribute("creatorPosition", position);
				model.addAttribute("createdDate", createdDate);
			} else if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy", name);
				model.addAttribute("approverPosition", position);
				model.addAttribute("approvedDate", createdDate);
			}
		}
		return "WorkOrderMainPdf.jasper";
	}

	@RequestMapping(value="/showWorkOrderRefForm", method=RequestMethod.GET)
	public String showWorkOrderRefForm(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = workOrderService.getCompanies(user, 0);
		model.addAttribute("companies", companies);
		loadWOReferenceForms((companies != null ? companies.iterator().next().getId() : null), null, null,
				null, SalesOrder.STATUS_UNUSED, PageSetting.START_PAGE, model);
		return "SubWOReference.jsp";
	}

	private void loadWOReferenceForms(Integer companyId, Integer woNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, Integer pageNumber, Model model) {
		Page<WorkOrder> workOrders = workOrderService.getWorkOrderReferences(companyId, woNumber, arCustomerId,
				arCustomerAcctId, statusId, pageNumber);
		model.addAttribute("workOrders", workOrders);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getWorkOrderForms", method=RequestMethod.GET)
	public String getWorkOrderForms(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="woNumber", required=false) Integer woNumber,
			@RequestParam (value="arCustomerId", required=false) Integer arCustomerId,
			@RequestParam (value="arCustomerAcctId", required=false) Integer arCustomerAcctId,
			@RequestParam (value="status", required=false) Integer statusId,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		loadWOReferenceForms(companyId, woNumber, arCustomerId, arCustomerAcctId, statusId, pageNumber, model);
		return "SubWOReferenceTable.jsp";
	}

	@RequestMapping(value="/loadRefWorkOrder",method=RequestMethod.GET)
	public @ResponseBody String loadRefWorkOrder(@RequestParam (value="refWorkOrderId", required=true) Integer refWorkOrderId,
			Model model, HttpSession session) {
		WorkOrder workOrder = workOrderService.setSubWorkOrder(refWorkOrderId);
		JsonConfig jsonConfig = new JsonConfig();
		String [] excludes = {"glDate", "itemCategory", "itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices",
				"buyingAddOns", "buyingDiscounts", "rItemDetails", "soItems", "soLines", "formWorkflow"};
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(workOrder, jsonConfig);
		return jsonObject.toString();
	}
}
