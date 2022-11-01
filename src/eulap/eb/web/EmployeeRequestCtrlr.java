package eulap.eb.web;

import java.io.InvalidClassException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.LeaveDetail;
import eulap.eb.domain.hibernate.OvertimeDetail;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.RequestType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AdminTypeOfLeaveService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.EmployeeLeaveCreditService;
import eulap.eb.service.EmployeeRequestService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;

/**
 * Controller class for {@link EmployeeRequest}

 *
 */
@Controller
@RequestMapping("/employeeRequest")
public class EmployeeRequestCtrlr {
	@Autowired
	private EmployeeRequestService employeeRequestService;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private EmployeeLeaveCreditService leaveCreditService;
	@Autowired
	private AdminTypeOfLeaveService leaveTypeService;
	@Autowired
	private WorkflowServiceHandler workflowService;

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable("typeId") Integer typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model,
			HttpSession session) {
		EmployeeRequest employeeRequest = new EmployeeRequest();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isNew = pId == null;
		if (isNew) {
			employeeRequest.setDate(new Date());
		} else {
			if(typeId.equals(RequestType.REQUEST_FOR_LEAVE)) {
				employeeRequest = employeeRequestService.getEmployeeRequest(pId, typeId);
			} else if (typeId.equals(RequestType.REQUEST_FOR_OVERTIME)) {
				employeeRequest = employeeRequestService.getEmployeeRequest(pId, typeId);
			}

			//Set reference document
			if(employeeRequest.getEbObjectId() != null){
				List<ReferenceDocument> refDocs =
						referenceDocumentService.getReferenceDocuments(employeeRequest.getEbObjectId());
				employeeRequest.setReferenceDocuments(refDocs);
			}
		}
		loadForm(employeeRequest, user, model, isNew, typeId);
		return "EmployeeRequestForm.jsp";
	}

	@RequestMapping (value="{typeId}/view", method = RequestMethod.GET)
	public String viewForm(@PathVariable("typeId") Integer typeId,
			@RequestParam(value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws ConfigurationException {
		EmployeeRequest employeeRequest = new EmployeeRequest();
		if(typeId.equals(RequestType.REQUEST_FOR_LEAVE)) {
			employeeRequest = employeeRequestService.getEmployeeRequest(pId, typeId);
		}
		if(typeId.equals(RequestType.REQUEST_FOR_OVERTIME)) {
			employeeRequest = employeeRequestService.getEmployeeRequest(pId, typeId);
		}
		List<ReferenceDocument> referenceDocuments = null;
		if (employeeRequest.getEbObjectId() != null) {
			referenceDocuments = referenceDocumentService.getReferenceDocuments(employeeRequest.getEbObjectId());
			employeeRequest.setReferenceDocuments(referenceDocuments);
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(employeeRequest.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(pId, "EmployeeRequest"+typeId, user));
		}
		model.addAttribute("employeeRequest", employeeRequest);
		return "EmployeeRequestView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, employeeRequestService.getFormWorkflow(pId));
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String saveForm(@PathVariable("typeId") int typeId,
			@ModelAttribute ("employeeRequest") EmployeeRequest employeeRequest,
			BindingResult result, Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException{
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isNew = employeeRequest.getId() == 0;
		employeeRequest.deserializeReferenceDocuments();
		employeeRequest.setBindingResult(result);
		synchronized (this) {
			employeeRequestService.validate(employeeRequest, employeeRequest.getLeaveDetail(),
					employeeRequest.getOvertimeDetail(), employeeRequest.getBindingResult());
			if(employeeRequest.getBindingResult().hasErrors()){
				loadForm(employeeRequest, user, model, isNew, typeId);
				return "EmployeeRequestForm.jsp";
			}
			ebFormServiceHandler.saveForm(employeeRequest, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", employeeRequest.getSequenceNo());
		model.addAttribute("formId", employeeRequest.getId());
		model.addAttribute("ebObjectId", employeeRequest.getEbObjectId());
		return "successfullySaved";
	}

	private void loadForm(EmployeeRequest employeeRequest, User user, Model model, boolean isNew, Integer typeId) {
		employeeRequest.setRequestTypeId(typeId);
		Collection<Company> companies = new ArrayList<Company>();
		Integer leaveTypeId = null;
		if (!isNew) {
			companies = companyService.getCompaniesWithInactives(user, employeeRequest.getCompanyId());
			if (typeId.equals(RequestType.REQUEST_FOR_LEAVE)) {
				leaveTypeId = employeeRequest.getLeaveDetail().getTypeOfLeaveId();
			}
		} else {
			companies = companyService.getActiveCompanies(user, null, null, null);
		}
		employeeRequest.serializeReferenceDocuments();
		if (typeId.equals(RequestType.REQUEST_FOR_LEAVE)) {
			model.addAttribute("typeOfLeaves", employeeRequestService.getTypeOfLeaves(leaveTypeId));
		}
		model.addAttribute("requestType", typeId);
		model.addAttribute("companies", companies);
		model.addAttribute("employeeRequest", employeeRequest);
	}

	@RequestMapping(value="/{typeId}/search", method=RequestMethod.GET)
	public @ResponseBody String searchForm(@PathVariable("typeId") int typeId,
			@RequestParam(required = true, value="criteria", defaultValue = "") String criteria,
			HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> results = employeeRequestService.search(typeId, criteria, user);
		JSONArray jsonArray = JSONArray.fromObject(results);
		return jsonArray.toString();
	}

	@RequestMapping(value="/{typeId}/pdf", method = RequestMethod.GET)
	private String showPrintout(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId, Model model){
		EmployeeRequest employeeRequest = employeeRequestService.getEmployeeRequest(pId, typeId);
		setCommonParam(employeeRequest, typeId, model, "pdf");
		return "RequestForLeaveForm.jasper";
	}

	public void setCommonParam(EmployeeRequest employeeRequest, Integer typeId, Model model, String format) {
		model.addAttribute("format", format);

		String label = typeId == RequestType.REQUEST_FOR_LEAVE ? " LEAVE" : " OVERTIME";
		String reportTitle = "REQUEST FOR" + label;
		model.addAttribute("reportTitle" , reportTitle);

		Company company = companyService.getCompany(employeeRequest.getCompanyId());
		model.addAttribute("parentCompanyName", company.getName());
		model.addAttribute("companyCode", company.getCompanyCode());

		model.addAttribute("employeeName", employeeRequest.getEmployeeFullName());
		model.addAttribute("employeePosition", employeeRequest.getEmployeePosition());
		model.addAttribute("date", DateUtil.removeTimeFromDate(employeeRequest.getDate()));

		model.addAttribute("divisionName", employeeRequest.getEmployee().getDivision().getName());

		Integer employeeId = employeeRequest.getEmployeeId();
		if (typeId == RequestType.REQUEST_FOR_LEAVE) {
			LeaveDetail detail = employeeRequest.getLeaveDetail();
			Integer typeOfLeaveId = detail.getTypeOfLeaveId();
			model.addAttribute("leaveType", leaveTypeService.getTypeOfLeave(typeOfLeaveId).getName());
			model.addAttribute("dateFrom", detail.getDateFrom());
			model.addAttribute("dateTo", detail.getDateTo());
			model.addAttribute("leaveDays", detail.getLeaveDays());
			model.addAttribute("remarks", detail.getRemarks());
			model.addAttribute("leavesEarned",
					leaveCreditService.getAvailableLeaves(employeeId, typeOfLeaveId, false, true));
			model.addAttribute("leavesUsed", detail.getLeaveDays());
			model.addAttribute("leaveBalance",
					leaveCreditService.getAvailableLeaves(employeeId, typeOfLeaveId, true, false));
			getFormWorkFlowLog(employeeRequest, model);
		}
	}

	private void getFormWorkFlowLog(EmployeeRequest employeeRequest, Model model){
		FormWorkflow formWorkflow = employeeRequest.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy",
						user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("createdPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.NOTED_ID) {
				model.addAttribute("notedBy", user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("notedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.VERIFIED_ID){
				model.addAttribute("verifiedBy", user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("verifiedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy", user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("approvedPosition", position.getName());
			}
		}
	}

	@RequestMapping (value="{typeId}/requestForOT/pdf", method = RequestMethod.GET)
	public String generatePDF(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId, Model model) {
		EmployeeRequest employeeRequest = employeeRequestService.getEmployeeRequest(pId, typeId);
		setOTParams(typeId, employeeRequest, model, "pdf");
		return "RequestForOverTime.jasper";
	}

	/**
	 * Compute and get the hours difference
	 * @param startTime The start time
	 * @param endTime The end time
	 * @return The hours difference
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/computeNoOfHours")
	public @ResponseBody String getHoursDiff(@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "allowableBreak", required = false) Double allowableBreak) {
		Double hoursDiff;
		try {
			hoursDiff = DateUtil.getHoursDiff(startTime, endTime, allowableBreak);
		} catch (ParseException e) {
			return "Invalid Time Format.";
		}
		return hoursDiff.toString();
	}

	/**
	 * Compute and get the day difference
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @return The day difference
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/computeNoOfDays")
	public @ResponseBody String getDaysDiff(@RequestParam(value = "dateFrom", required = false) Date dateFrom,
			@RequestParam(value = "dateTo", required = false) Date dateTo) throws ParseException {
		Integer daysDiff = 0;
		if(dateFrom != null && dateTo != null) {
			daysDiff = (DateUtil.getDayDifference(dateFrom, dateTo) + 1);
		}
		return daysDiff.toString();
	}

	@RequestMapping(value="/{typeId}/html", method = RequestMethod.GET)
	private String showHTMLPrintout(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId, Model model){
		EmployeeRequest employeeRequest = employeeRequestService.getEmployeeRequest(pId, typeId);
		setCommonParam(employeeRequest, typeId, model, "html");
		return "RequestForLeaveFormHTML.jasper";
	}

	public void setOTParams(int typeId, EmployeeRequest employeeRequest, Model model, String format) {
		List<OvertimeDetail> overtimeDetails = new ArrayList<>();
		overtimeDetails.add(employeeRequest.getOvertimeDetail());
		JRDataSource dataSource = new JRBeanCollectionDataSource(overtimeDetails);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf" );
		Company company  = companyService.getCompany(employeeRequest.getCompanyId());
		model.addAttribute("parentCompany" , company.getName());
		model.addAttribute("companyCode", company.getCompanyCode());
		model.addAttribute("guard", "");
		model.addAttribute("reportTitle" , "REQUEST FOR OVERTIME");
		model.addAttribute("employeeName" , employeeRequest.getEmployee().getFullName());
		model.addAttribute("position" , employeeRequest.getEmployee().getPosition().getName());
		model.addAttribute("department" , employeeRequest.getEmployee().getDivision().getName());
		getFormWorkFlowLog(employeeRequest, model);
	}

	@RequestMapping (value="{typeId}/requestForOT/html", method = RequestMethod.GET)
	public String generateHTMLPrintout(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId, Model model) {
		EmployeeRequest employeeRequest = employeeRequestService.getEmployeeRequest(pId, typeId);
		setOTParams(typeId, employeeRequest, model, "html");
		return "RequestForOverTimeHTML.jasper";
	}
}
