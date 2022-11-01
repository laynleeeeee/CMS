package eulap.eb.web;

import java.io.InvalidClassException;
import java.util.ArrayList;
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
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RequisitionClassification;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.PurchaseRequisitionService;
import eulap.eb.service.RequisitionFormService;
import eulap.eb.service.WarehouseService;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.RequisitionFormDto;
import eulap.eb.web.dto.Rf2PrDto;
import eulap.eb.web.dto.RfReferenceDto;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * Controller class for Purchase Requisition

 */

@Controller
@RequestMapping(value="purchaseRequisition")
public class PurchaseRequisitionController {
	private final Logger logger = Logger.getLogger(PurchaseRequisitionController.class);
	private static final String MODEL_ATTRIB_NAME = "requisitionFormDto";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private RequisitionFormService requisitionFormService;
	@Autowired
	private PurchaseRequisitionService purchaseRequisitionService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private WarehouseService warehouseService;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.GET)
	public String showRequisitionForm (@PathVariable("typeId") int typeId,
			@RequestParam (value="pId", required = false) Integer pId, Model model,
			HttpSession session) throws ConfigurationException {
		logger.info("Loading the Requisition form of type: "+typeId);
		RequisitionFormDto requisitionFormDto = new RequisitionFormDto();
		RequisitionForm requisitionForm = new RequisitionForm();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (pId == null) {
			Date currentDate = new Date();
			requisitionForm.setDate(currentDate);
			requisitionForm.setRequestedDate(currentDate);
		} else {
			logger.debug("Retrieving requisition form by id "+pId);
			requisitionForm = purchaseRequisitionService.getPurchaseRequisition(pId);
		}
		requisitionForm.setRequisitionTypeId(typeId);
		requisitionForm.setRequisitionClassificationId(RequisitionClassification.RC_PURCHASE_REQUISITION);
		requisitionFormDto.setRequisitionForm(requisitionForm);
		return loadRequisitionForm(requisitionFormDto, model, user, typeId);
	}

	public String loadRequisitionForm(RequisitionFormDto requisitionFormDto, Model model, User user, Integer typeId) {
		logger.info("Retrieving active companies.");
		RequisitionForm requisitionForm = requisitionFormDto.getRequisitionForm();
		if (requisitionForm.getId() == 0) {
			model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		} else {
			model.addAttribute("companies", companyService.getCompaniesWithInactives(user,
					requisitionForm.getCompanyId()));
		}
		logger.info("Retrieving requisition form types.");
		model.addAttribute("requisitionTypes", requisitionFormService.getAllActiveRequisitionTypes());
		logger.info("Retrieving requisition form classifications.");
		model.addAttribute("requisitionClassifications", requisitionFormService.getAllActiveReqClassifications(false));
		requisitionFormDto.serializeRfItems();
		requisitionFormDto.serializeOclItems();
		requisitionFormDto.serializeRefDocs();
		requisitionFormDto.serializePrItems();
		requisitionFormDto.setRequisitionTypeName(requisitionFormService.getRequisitionFormTypeName(typeId, true));
		if (requisitionForm.getWarehouseId() != null && requisitionForm.getWarehouseId() != -1) {
			requisitionFormDto.setWarehouseName(warehouseService.getWarehouse(
					requisitionForm.getWarehouseId()).getName());
		}
		logger.info("Putting requisition form in a model.");
		model.addAttribute(MODEL_ATTRIB_NAME, requisitionFormDto);
		return "PurchaseRequisitionForm.jsp";
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String saveRequisitionForm(@PathVariable("typeId") int typeId,
			@ModelAttribute ("requisitionFormDto") RequisitionFormDto requisitionFormDto, BindingResult result,
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		RequisitionForm requisitionForm = requisitionFormDto.getRequisitionForm();
		requisitionFormDto.deserializeRefDocs();
		requisitionFormDto.deserializePrItems();
		requisitionFormService.validateRequisitionForm(requisitionFormDto, result);
		if (result.hasErrors()) {
			return loadRequisitionForm(requisitionFormDto, model, user, typeId);
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
		logger.info("Loading the view form of Requisition Form.");
		RequisitionFormDto rfDto = new RequisitionFormDto();
		RequisitionForm requisitionForm = purchaseRequisitionService.getPurchaseRequisition(pId);
		if (requisitionForm.getWarehouseId() != null) {
			rfDto.setWarehouseName(warehouseService.getWarehouse(
					requisitionForm.getWarehouseId()).getName());
		}
		rfDto.setRequisitionForm(requisitionForm);
		rfDto.setRequisitionTypeName(requisitionFormService.getRequisitionFormTypeName(typeId, true));
		model.addAttribute(MODEL_ATTRIB_NAME, rfDto);
		return "PurchaseRequisitionView.jsp"; 
	}

	@RequestMapping(value="/{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchRequisitionForm(@PathVariable("typeId") int typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = requisitionFormService.searchRequisitionForms(criteria, typeId, true);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/{typeId}/rfReference", method=RequestMethod.GET)
	public String showRfReference(@PathVariable("typeId") int typeId,
			@RequestParam (value="companyId") int companyId,
			@RequestParam (value="pageNumber", required=false) Integer pageNumber,
			Model model, HttpSession session) {
		loadReferences(model, companyId, typeId, null, null, null , null, null, RequisitionForm.STATUS_UNUSED, 
				pageNumber != null ? pageNumber : PageSetting.START_PAGE);
		return "RFReference.jsp";
	}

	@RequestMapping(value="/{typeId}/rfReference/reload", method=RequestMethod.GET)
	public String showRfReferenceTable (@PathVariable("typeId") int typeId,
			@RequestParam (value="companyId") int companyId,
			@RequestParam (value="fleetProfileId", required=false) Integer fleetProfileId,
			@RequestParam (value="projectId", required=false) Integer projectId,
			@RequestParam (value="rfNumber", required=false)  Integer rfNumber,
			@RequestParam (value="strDateFrom", required=false) String strDateFrom,
			@RequestParam (value="strDateTo", required=false) String strDateTo,
			@RequestParam (value="status") Integer status,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		Date dateFrom = strDateFrom != null ? DateUtil.parseDate(strDateFrom) : null;
		Date dateTo = strDateTo != null ? DateUtil.parseDate(strDateTo) : null; 
		loadReferences(model, companyId, typeId, fleetProfileId, rfNumber, projectId,
				dateFrom, dateTo, status, pageNumber);
		return "RFReferenceTable.jsp";
	}

	private void loadReferences(Model model, int companyId, int requisitionTypeId, Integer fleetProfileId,
			Integer rfNumber, Integer projectId, Date dateFrom,  Date dateTo, Integer status,
			Integer pageNumber) {
		logger.info("Loading the requisition forms.");
		Page<RfReferenceDto> requisitionForms = purchaseRequisitionService.getRequisitionForms(companyId,
				fleetProfileId, projectId, rfNumber, dateFrom, dateTo, status, requisitionTypeId, pageNumber);
		model.addAttribute("requisitionForms", requisitionForms);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="loadRequisitionForm",method=RequestMethod.GET)
	public @ResponseBody String setRequisitionForm (@RequestParam (value="rfId", required=true) Integer rfId,
			Model model, HttpSession session) {
		logger.info("setting requisition form.");
		Rf2PrDto prDto = purchaseRequisitionService.convertRfToPr(rfId);
		String [] excludes = {"itemCategory", "unitMeasurement", "itemSrps", "itemDiscounts",
				"itemAddOns", "buyingPrices", "buyingAddOns", "buyingDiscounts", "rItemDetails"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(prDto, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value="{typeId}/pdf", method = RequestMethod.GET)
	private String generatePrintout(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true)Integer pId,
			Model model, HttpSession session) {
		RequisitionForm purchaseRequisition = purchaseRequisitionService.getPurchaseRequisition(pId);
		List<RequisitionForm> purchaseRequisitions = new ArrayList<RequisitionForm>();
		purchaseRequisitions.add(purchaseRequisition);
		model.addAttribute("datasource", new JRBeanCollectionDataSource(purchaseRequisitions));
		setCommonParams(purchaseRequisition, typeId, model, session);
		return "PurchaseRequisition.jasper";
	}

	public void setCommonParams(RequisitionForm purchaseRequisition, int typeId, Model model, HttpSession session) {
		model.addAttribute("format", "pdf");
		model.addAttribute("reportTitle" , requisitionFormService.getRequisitionFormTypeName(typeId, true));
		model.addAttribute("formDate", purchaseRequisition.getDate());
		model.addAttribute("sequenceNumber", ""+purchaseRequisition.getSequenceNumber());

		// Set company details
		model.addAttribute("companyName", purchaseRequisition.getCompany().getName());
		model.addAttribute("companyAddress", purchaseRequisition.getCompany().getAddress());
		model.addAttribute("companyTin", purchaseRequisition.getCompany().getTin());
		model.addAttribute("companyContactNo", purchaseRequisition.getCompany().getContactNumber());
		model.addAttribute("companyLogo", purchaseRequisition.getCompany().getLogo());

		FleetProfile fleetProfile = purchaseRequisition.getReqFormRef().getFleetProfile();
		String fleetName = fleetProfile != null ? fleetProfile.getCodeVesselName() : "";

		ArCustomer arCustomer = purchaseRequisition.getReqFormRef().getArCustomer();
		String projectName = arCustomer != null ? arCustomer.getName() : "";

		String fleetProject = !fleetName.isEmpty() && !projectName.isEmpty() ? fleetName + " / " + projectName
			: (fleetName.isEmpty() ? (!projectName.isEmpty() ? projectName : "") : fleetName);
		model.addAttribute("fleetProject", fleetProject);

		model.addAttribute("requestedBy", purchaseRequisition.getRequestedBy());
		model.addAttribute("requestedDate", purchaseRequisition.getRequestedDate());
		model.addAttribute("remarks", purchaseRequisition.getRemarks());

		FormWorkflow workflowLog = purchaseRequisition.getFormWorkflow();
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			if(log.getFormStatusId() == FormStatus.CREATED_ID) {
				String name = log.getCreated().getLastName() + ", " + log.getCreated().getFirstName();
				model.addAttribute("createdBy", name);
				model.addAttribute("createdDate", log.getCreatedDate());
				model.addAttribute("creatorPosition", log.getCreated().getPosition().getName());
			} else if(log.getFormStatusId() == FormStatus.APPROVED_ID) {
				String name = log.getCreated().getLastName() + ", " + log.getCreated().getFirstName();
				model.addAttribute("approvedBy", name);
				model.addAttribute("approvedDate", log.getCreatedDate());
				model.addAttribute("approverPosition", log.getCreated().getPosition().getName());
			}
		}
	}
}
