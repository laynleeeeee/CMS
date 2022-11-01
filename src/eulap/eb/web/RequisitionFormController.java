package eulap.eb.web;

import java.io.InvalidClassException;
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
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RequisitionClassification;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.RequisitionType;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.RequisitionFormService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.RequisitionFormDto;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * Controller class for {@code RequisitionForm}

 *
 */
@Controller
@RequestMapping(value="requisitionForm")
public class RequisitionFormController {
	private final Logger logger = Logger.getLogger(RequisitionFormController.class);
	private static final String MODEL_ATTRIB_NAME = "requisitionFormDto";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private RequisitionFormService requisitionFormService;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.GET)
	public String showRequisitionForm (@PathVariable("typeId") int typeId,
			@RequestParam (value="pId", required = false) Integer pId, Model model, HttpSession session) throws ConfigurationException {
		logger.info("Loading the Requisition form of type: "+typeId);
		RequisitionFormDto requisitionFormDto = new RequisitionFormDto();
		RequisitionForm requisitionForm = new RequisitionForm();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isEdit = false;
		if (pId == null) {
			Date currentDate = new Date();
			requisitionForm.setDate(currentDate);
			requisitionForm.setRequestedDate(currentDate);
		} else {
			logger.debug("Retrieving requisition form by id "+pId);
			requisitionForm = requisitionFormService.getById(pId, true, true);
			isEdit = workflowServiceHandler.isAllowedToEdit(RequisitionForm.class.getSimpleName()+typeId,
					user, requisitionForm.getFormWorkflow());
		}
		model.addAttribute("isEdit", isEdit);
		getTypeName(typeId, model);
		requisitionForm.setRequisitionTypeId(typeId); //Set the Requisition Type id.
		requisitionFormDto.setRequisitionForm(requisitionForm);
		return loadRequisitionForm(requisitionFormDto, typeId, model, user);
	}

	public void getTypeName(int typeId, Model model) {
		model.addAttribute("requisitionTypeName", requisitionFormService.getRequisitionFormTypeName(typeId, false));
	}

	public String loadRequisitionForm(RequisitionFormDto requisitionFormDto, Integer typeId, Model model, User user) {
		logger.info("Retrieving active companies.");
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		logger.info("Retrieving requisition form types.");
		model.addAttribute("requisitionTypes", requisitionFormService.getAllActiveRequisitionTypes());
		logger.info("Retrieving requisition form classifications.");
		model.addAttribute("requisitionClassifications", requisitionFormService.getAllActiveReqClassifications(false));
		if (typeId == RequisitionType.RT_FUEL) {
			logger.info("Retrieving ratios.");
			model.addAttribute("ratios", requisitionFormService.getAllActiveRatios());
		}
		logger.info("Putting requisition form in a model.");
		model.addAttribute(MODEL_ATTRIB_NAME, requisitionFormDto);
		requisitionFormDto.serializeRfItems();
		requisitionFormDto.serializeOclItems();
		requisitionFormDto.serializeRefDocs();
		return "RequisitionForm.jsp";
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable("typeId") int typeId,
			@ModelAttribute ("requisitionFormDto") RequisitionFormDto requisitionFormDto, BindingResult result, 
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		RequisitionForm requisitionForm = requisitionFormDto.getRequisitionForm();
		requisitionFormDto.deserializeRfItems();
		requisitionFormDto.deserializeRefDocs();
		requisitionFormService.validateRequisitionForm(requisitionFormDto, result);
		if (result.hasErrors()) {
			getTypeName(typeId, model);
			return loadRequisitionForm(requisitionFormDto, typeId, model, user);
		}
		ebFormServiceHandler.saveForm(requisitionForm, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", requisitionForm.getSequenceNumber());
		model.addAttribute("date", requisitionForm.getDate());
		model.addAttribute("formId", requisitionForm.getId());
		model.addAttribute("ebObjectId", requisitionForm.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/{typeId}/view", method=RequestMethod.GET)
	public String viewRequisitionForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model) {
		RequisitionFormDto requisitionFormDto = requisitionFormService.getRequisitionFormDto(pId, true, true);
		logger.info("Loading the view form of Requisition Form.");
		model.addAttribute(MODEL_ATTRIB_NAME, requisitionFormDto);
		getTypeName(typeId, model);
		return "RequisitionFormView.jsp";
	}

	@RequestMapping(value="/{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchRequisitionForm(@PathVariable("typeId") int typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = requisitionFormService.searchRequisitionForms(criteria, typeId, false);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="{typeId}/pdf", method = RequestMethod.GET)
	private String generatePrintout(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true)Integer pId,
			Model model, HttpSession session) {
		RequisitionForm requisitionForm = requisitionFormService.getById(pId, true, true);
		List<RequisitionForm> requisitionForms = new ArrayList<>();
		requisitionForms.add(requisitionForm);
		model.addAttribute("datasource", new JRBeanCollectionDataSource(requisitionForms));
		setCommonParams(requisitionForm, typeId, model, session);
		return "RequisitionForm.jasper";
	}

	public void setCommonParams(RequisitionForm requisitionForm, int typeId, Model model, HttpSession session) {
		model.addAttribute("format", "pdf");
		Integer rfClassificationId = requisitionForm.getRequisitionClassificationId();
		boolean isPurchaseRequest = false;
		if (rfClassificationId != null) {
			isPurchaseRequest = rfClassificationId == RequisitionClassification.RC_PURCHASE_REQUISITION;
		}
		model.addAttribute("reportTitle" , requisitionFormService.getRequisitionFormTypeName(typeId, isPurchaseRequest));
		model.addAttribute("formDate", requisitionForm.getDate());
		model.addAttribute("sequenceNumber", ""+requisitionForm.getSequenceNumber());

		// Set company details
		model.addAttribute("companyName", requisitionForm.getCompany().getName());
		model.addAttribute("companyAddress", requisitionForm.getCompany().getAddress());
		model.addAttribute("companyTin", requisitionForm.getCompany().getTin());
		model.addAttribute("companyContactNo", requisitionForm.getCompany().getContactNumber());
		model.addAttribute("companyLogo", requisitionForm.getCompany().getLogo());
		model.addAttribute("sequenceNo", requisitionForm.getSequenceNumber());
		model.addAttribute("woNumber", requisitionForm.getWoNumber());

		FleetProfile fleetProfile = requisitionForm.getFleetProfile();
		String fleetName = fleetProfile != null ? fleetProfile.getCodeVesselName() : "";

		ArCustomer arCustomer = requisitionForm.getArCustomer();
		String projectName = arCustomer != null ? arCustomer.getName() : "";

		String fleetProject = !fleetName.isEmpty() && !projectName.isEmpty() ? fleetName + " / " + projectName
			: (fleetName.isEmpty() ? (!projectName.isEmpty() ? projectName : "") : fleetName);
		model.addAttribute("fleetProject", fleetProject);

		model.addAttribute("requestedBy", requisitionForm.getRequestedBy());
		model.addAttribute("requestedDate", requisitionForm.getRequestedDate());
		model.addAttribute("remarks", requisitionForm.getRemarks());

		FormWorkflow workflowLog = requisitionForm.getFormWorkflow();
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", log.getCreated().getLastName() + ", " + log.getCreated().getFirstName());
				model.addAttribute("createdDate", log.getCreatedDate());
				model.addAttribute("creatorPosition", log.getCreated().getPosition().getName());
			} else if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy", log.getCreated().getLastName() + ", " + log.getCreated().getFirstName());
				model.addAttribute("approvedDate", log.getCreatedDate());
				model.addAttribute("approverPosition", log.getCreated().getPosition().getName());
			}
		}
	}

	@RequestMapping(value="/showWorkOrderRefForm", method=RequestMethod.GET)
	public String showWorkOrderRefForm(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, 0);
		model.addAttribute("companies", companies);
		loadWOReferenceForms((companies != null ? companies.iterator().next().getId() : null), null, null,
				null, SalesOrder.STATUS_UNUSED, PageSetting.START_PAGE, model);
		return "RfWoReference.jsp";
	}

	private void loadWOReferenceForms(Integer companyId, Integer woNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, Integer pageNumber, Model model) {
		Page<WorkOrder> workOrders = requisitionFormService.getWorkOrderReferences(companyId, woNumber, arCustomerId,
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
		return "RfWoReferenceTable.jsp";
	}

	@RequestMapping(value="/loadRefWorkOrder",method=RequestMethod.GET)
	public @ResponseBody String loadRefWorkOrder(@RequestParam (value="refWorkOrderId", required=true) Integer refWorkOrderId,
			Model model, HttpSession session) {
		RequisitionForm rf = requisitionFormService.convertWoToRf(refWorkOrderId);
		JsonConfig jsonConfig = new JsonConfig();
		String [] excludes = {"glDate", "itemCategory", "itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices",
				"buyingAddOns", "buyingDiscounts", "rItemDetails", "soItems", "soLines", "formWorkflow"};
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(rf, jsonConfig);
		return jsonObject.toString();
	}
}