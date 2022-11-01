package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collection;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ProjectRetention;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.ProjectRetentionService;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller for {@link ProjectRetention}

 */

@Controller
@RequestMapping("/projectRetention")
public class ProjectRetentionController {
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private ProjectRetentionService projectRetentionService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private ArCustomerService arCustomerService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/{typeId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ProjectRetention projectRetention = null;
		int divisionId = projectRetentionService.getDivisionIdByPrTypeId(typeId);
		if (pId == null) {
			projectRetention = new ProjectRetention();
			projectRetention.setProjectRetentionTypeId(typeId);
			projectRetention.setDivisionId(divisionId);
			projectRetention.setDivision(projectRetentionService.getDivision(divisionId));
			projectRetention.setDate(new Date());
		} else {
			projectRetention = projectRetentionService.getProjectRetention(pId);
			projectRetentionService.processRefDoc(projectRetention);
		}
		return loadForm(projectRetention, model, user);
	}

	private String loadForm(ProjectRetention projectRetention, Model model, User user) {
		projectRetention.serializeReferenceDocuments();
		projectRetention.serializeProjectRetentionLines();
		model.addAttribute("currencies", currencyService.getActiveCurrencies(projectRetention.getCurrencyId()));
		model.addAttribute("projectRetention", projectRetention);
		return "ProjectRetentionForm.jsp";
	}

	@RequestMapping(value="/{typeId}/form", method = RequestMethod.POST)
	public String submitForm(@PathVariable(value="typeId") int typeId,
			@ModelAttribute ("projectRetention") ProjectRetention projectRetention, BindingResult result,
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		projectRetention.deserializeProjectRetentionLines();
		projectRetention.deserializeReferenceDocuments();
		synchronized (this) {
			projectRetentionService.validate(projectRetention, result);
			if (result.hasErrors()) {
				return loadForm(projectRetention, model, user);
			}
			ebFormServiceHandler.saveForm(projectRetention, user);
		}
		model.addAttribute("ebObjectId", projectRetention.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping (value="/{typeId}/viewForm", method = RequestMethod.GET)
	public String viewProjectRetention(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		ProjectRetention projectRetention = projectRetentionService.getProjectRetention(pId);
		projectRetentionService.processRefDoc(projectRetention);
		model.addAttribute("projectRetention", projectRetention);
		return "ProjectRetentionView.jsp";
	}

	private void loadSoReferenceForms(Integer companyId, Integer divisionId, Integer arCustomerId, 
			Integer arCustomerAcctId, Integer soNumber, String poNumber, Date dateFrom, Date dateTo,
			Integer statusId, Integer pageNumber, Model model) {
		Page<SalesOrder> salesOrders = projectRetentionService.getProjectRetentionSalesOrders(companyId, 
				divisionId, arCustomerId, arCustomerAcctId, soNumber, poNumber, dateFrom, dateTo, statusId, pageNumber);
		model.addAttribute("salesOrders", salesOrders);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="{typeId}/showSoReferences", method=RequestMethod.GET)
	public String showSoReferenceForm(@PathVariable(value="typeId") int typeId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		int divisionId = projectRetentionService.getDivisionIdByPrTypeId(typeId);
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, 0);
		model.addAttribute("companies", companies);
		model.addAttribute("divisionId", divisionId);
		Integer companyId = companies != null ? companies.iterator().next().getId() : null;
		loadSoReferenceForms(companyId, divisionId, null, null, null, "", null, null, ProjectRetention.STATUS_UNUSED,
				PageSetting.START_PAGE, model);
		return "PrSoReferenceForm.jsp";
	}

	@RequestMapping(value="/getSoReferences", method = RequestMethod.GET)
	public String getReferenceForms(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="arCustomerId", required=false) Integer arCustomerId,
			@RequestParam(value="arCustomerAcctId", required=false) Integer arCustomerAcctId,
			@RequestParam(value="soNumber", required=false) Integer soNumber,
			@RequestParam(value="poNumber", required=false) String poNumber,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam(value="pageNumber", required=false) int pageNumber,
			@RequestParam(value="statusId", required=false) Integer status,
			Model model, HttpSession session) {
		loadSoReferenceForms(companyId, divisionId, arCustomerId, arCustomerAcctId, soNumber, poNumber, 
				dateFrom, dateTo, status, pageNumber, model);
		return "PrSoReferenceTable.jsp";
	}

	@RequestMapping(value = "/convSOToPr", method = RequestMethod.GET)
	public @ResponseBody String convRefTrans (@RequestParam(value="salesOrderId", required=true) int salesOrderId,
			@RequestParam(value="capId", required=false) Integer capId,
			@RequestParam(value="typeId", required=false) Integer typeId) throws IOException {
		ProjectRetention pr = projectRetentionService.convertSoToPr(salesOrderId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pr, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value="/pdf", method = RequestMethod.GET)
	public String generatePrintout(@RequestParam(value="pId", required=true) Integer pId,
			Model model, HttpSession session) throws IOException {
		ProjectRetention pr = projectRetentionService.getProjectRetention(pId);
		List<ProjectRetention> projectRetetions = new ArrayList<ProjectRetention>();
		projectRetetions.add(pr);
		model.addAttribute("datasource", new JRBeanCollectionDataSource(projectRetetions));
		setCommonParams(pr, model, session);
		return "ProjectRetentionPDF.jasper";
	}

	private void setCommonParams(ProjectRetention pr, Model model, HttpSession session) {
		model.addAttribute("format", "pdf");
		model.addAttribute("formName" , "Project Retention");
		model.addAttribute("sequenceNumber", ""+pr.getSequenceNo());
		model.addAttribute("remarks", pr.getRemarks());
		model.addAttribute("date", pr.getDate());
		model.addAttribute("dueDate", pr.getDueDate());
		model.addAttribute("divisionName", pr.getDivision().getName());
		model.addAttribute("poNumber", pr.getPoNumber());
		model.addAttribute("currencyName", pr.getCurrency().getName());
		model.addAttribute("wtAcctName", projectRetentionService.getWtAcctSettingName(pr.getWtAcctSettingId()));
		model.addAttribute("wtAmount", pr.getWtAmount());
		model.addAttribute("receiver", pr.getReceiver());
		model.addAttribute("receivedDate", pr.getDateReceived());

		setCompanyParams(pr.getCompanyId(), model);
		setCustomerParms(pr, model);
		setWorkflowParams(pr.getFormWorkflow(), model);
	}

	private void setCompanyParams(Integer companyId, Model model) {
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyContactNo", company.getContactNumber());
		model.addAttribute("companyLogo", company.getLogo());
	}

	private void setWorkflowParams(FormWorkflow formWorkflow, Model model) {
		for (FormWorkflowLog log : formWorkflow.getFormWorkflowLogs()) {
			String status = "";
			String name = log.getCreated().getLastName() + ", " + log.getCreated().getFirstName();
			switch (log.getFormStatusId()) {
			case FormStatus.CREATED_ID:
				status = "creator";
				break;
			case FormStatus.REVIEWED_ID:
				status = "reviewer";
				break;
			default:
				break;
			}
			model.addAttribute(status, name);
			model.addAttribute(status+"Position", log.getCreated().getPosition().getName());
		}
	}

	private void setCustomerParms(ProjectRetention pr, Model model) {
		ArCustomer customer = pr.getArCustomer();
		model.addAttribute("customer", customer.getName());
		model.addAttribute("customerAddress", arCustomerService.getArCustomerFullAddress(customer));
		model.addAttribute("customerTin", StringFormatUtil.processBirTinTo13Digits(customer.getTin()));
		model.addAttribute("customerAccount", pr.getArCustomerAccount().getName());
		customer = null;
	}

	@RequestMapping(value="/{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchOrderSlips(@PathVariable(value="typeId") int typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = projectRetentionService.searchProjectRetentions(typeId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="receivePr/{formId}", method=RequestMethod.GET)
	public String receivePrForm(@PathVariable(value="formId") int formId,
			Model model, HttpSession session) throws IOException {
		ProjectRetention projectRetention = new ProjectRetention();
		projectRetention.setDateReceived(new Date());
		return loadReceivePrForm(formId, projectRetention, model);
	}

	private String loadReceivePrForm(int formId, ProjectRetention projectRetention, Model model) {
		model.addAttribute("formId", formId);
		model.addAttribute("projectRetention", projectRetention);
		return "PrReceivingForm.jsp";
	}

	@RequestMapping (value="receivePr/{formId}", method=RequestMethod.POST)
	public String saveCheckLog(@PathVariable(value="formId") int formId,
			@ModelAttribute("projectRetention") ProjectRetention projectRetention, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		synchronized (this) {
			projectRetentionService.validateStatusLogs(projectRetention, result);
			if(result.hasErrors()) {
				return loadReceivePrForm(formId, projectRetention, model);
			}
			projectRetentionService.savePrReceivingDetails(projectRetention, user);
		}
		model.addAttribute("success", true);
		return "successfullySaved";
	}
}