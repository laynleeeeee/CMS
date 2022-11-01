package eulap.eb.web;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.concurrent.ConcurrentException;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EmployeeLeaveCredit;
import eulap.eb.domain.hibernate.EmployeeLeaveCreditLine;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.EmployeeLeaveCreditService;
import eulap.eb.service.EmployeeRequestService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.EmployeeLeaveCreditDto;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.json.JSONArray;

/**
 * Controller class for Employee Leave Credit Form.

 *
 */
@Controller
@RequestMapping("/employeeLeaveCredit")
public class EmployeeLeaveCreditCtrlr {

	@Autowired
	private CompanyService companyService;
	@Autowired
	private EmployeeLeaveCreditService employeeLeaveCreditService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private EmployeeRequestService empRequestService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private WorkflowServiceHandler workflowService;
	private static final String ELC_ATTRIB_NAME = "employeeLeaveCredit";

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	private String loadForm(EmployeeLeaveCredit employeeLeaveCredit, User user, Model model){
		boolean isNew = employeeLeaveCredit.getId() == 0;
		Integer leaveTypeId = null;
		if(isNew) {
			model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		} else {
			leaveTypeId = employeeLeaveCredit.getTypeOfLeaveId();
			employeeLeaveCredit.setFormWorkflow(employeeLeaveCreditService.getFormWorkflow(employeeLeaveCredit.getId()));
			model.addAttribute("companies", companyService.getCompaniesWithInactives(user, employeeLeaveCredit.getCompanyId()));
		}
		model.addAttribute("typeOfLeaves", empRequestService.getTypeOfLeaves(leaveTypeId));
		model.addAttribute(ELC_ATTRIB_NAME, employeeLeaveCredit);
		model.addAttribute("divisions", divisionService.getActiveDivisions(employeeLeaveCredit.getDivisionId() == null ? 0 : employeeLeaveCredit.getDivisionId()));
		return "EmployeeLeaveCreditForm.jsp";
	}

	@RequestMapping (value="/form", method = RequestMethod.GET)
	public String showEmployeeLeaveCreditForm(@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws ConfigurationException{
		User user = CurrentSessionHandler.getLoggedInUser(session);
		EmployeeLeaveCredit employeeLeaveCredit = new EmployeeLeaveCredit();
		boolean isEdit = false;
		if(pId == null){
			employeeLeaveCredit.setDate(new Date());
		} else {
			employeeLeaveCredit = employeeLeaveCreditService.getEmployeeLeaveCredit(pId);
			model.addAttribute("pId", pId);
			model.addAttribute("SO Number", employeeLeaveCredit.getSequenceNumber());
			if(employeeLeaveCredit.getEbObjectId() != null){
				List<EmployeeLeaveCreditLine> leaves = employeeLeaveCreditService.
						getEmployeeLeaveCreditLineByEbObject(employeeLeaveCredit.getTypeOfLeaveId(), employeeLeaveCredit.getEbObjectId(), null);
				for(EmployeeLeaveCreditLine leave : leaves){
					leave.setEmployeeName(leave.getEmployee().getFullName());
				}
				employeeLeaveCredit.setElcLines(leaves);
				List<ReferenceDocument> refDocs = refDocumentService.getReferenceDocuments(employeeLeaveCredit.getEbObjectId());
				employeeLeaveCredit.setReferenceDocuments(refDocs);
			}
			isEdit =  workflowServiceHandler.isAllowedToEdit(EmployeeLeaveCredit.class.getSimpleName(),
					user, employeeLeaveCredit.getFormWorkflow());
		}
		model.addAttribute("isEdit", isEdit);
		employeeLeaveCredit.serializeLeavesLines();
		employeeLeaveCredit.serializeReferenceDocuments();
		return loadForm(employeeLeaveCredit, user, model);
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String saveForm(@ModelAttribute ("employeeLeaveCredit") EmployeeLeaveCredit employeeLeaveCredit,
			BindingResult result, Model model, HttpSession session)throws InvalidClassException, ClassNotFoundException{
		User user = CurrentSessionHandler.getLoggedInUser(session);
		employeeLeaveCredit.deserializeLeavesLines();
		employeeLeaveCredit.deserializeReferenceDocuments();
		employeeLeaveCredit.setBindingResult(result);
		ebFormServiceHandler.saveForm(employeeLeaveCredit, user);
		if(employeeLeaveCredit.getBindingResult().hasErrors()){
			return loadForm(employeeLeaveCredit, user, model);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", employeeLeaveCredit.getSequenceNumber());
		model.addAttribute("ebObjectId", employeeLeaveCredit.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping (value="/form/view", method = RequestMethod.GET)
	public String viewEmployeeLeaveCredit(
			@RequestParam(value = "pId", required = false) Integer pId,
			@RequestParam(value = "employeeId", required = false) Integer employeeId,
			Model model, HttpSession session) throws ConcurrentException, ConfigurationException{
		EmployeeLeaveCredit employeeLeaveCredit = employeeLeaveCreditService.getEmployeeLeaveCredit(pId);
		List<EmployeeLeaveCreditLine> leaves = null;
		List<ReferenceDocument> referenceDocuments = null;
		if(employeeLeaveCredit.getEbObjectId() != null){
			leaves = employeeLeaveCreditService.getEmployeeLeaveCreditLineByEbObject(employeeLeaveCredit.getTypeOfLeaveId(),
					employeeLeaveCredit.getEbObjectId(), employeeId);
			referenceDocuments = refDocumentService.getReferenceDocuments(employeeLeaveCredit.getEbObjectId());
			for(EmployeeLeaveCreditLine leave : leaves){
				leave.setEmployeeName(leave.getEmployee().getFullName());
			}
			employeeLeaveCredit.setElcLines(leaves);
			employeeLeaveCredit.setReferenceDocuments(referenceDocuments);
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(employeeLeaveCredit.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(pId, "EmployeeLeaveCredit", user));
		}
		model.addAttribute(ELC_ATTRIB_NAME, employeeLeaveCredit);
		return "EmployeeLeaveCreditView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, employeeLeaveCreditService.getFormWorkflow(pId));
	}


	@RequestMapping(value="/search", method=RequestMethod.GET)
	public @ResponseBody String search(@RequestParam(required = true, value="criteria", defaultValue = "") String criteria,
			HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> results = employeeLeaveCreditService.search(criteria, user);
		JSONArray jsonArray = JSONArray.fromObject(results);
		return jsonArray.toString();
	}

	@RequestMapping(value="/pdf", method = RequestMethod.GET)
	private String showPrintOut(@RequestParam(value="pId", required=true) Integer pId,
			Model model, HttpSession session){
		EmployeeLeaveCredit employeeLeaveCredit = employeeLeaveCreditService.getEmployeeLeaveCredit(pId);
		setParams(employeeLeaveCredit, model, "pdf", session);
		return "EmployeeLeaveCredit.jasper";
	}

	public void setParams(EmployeeLeaveCredit employeeLeaveCredit, Model model, String format, HttpSession session) {
		Company company = companyService.getCompany(employeeLeaveCredit.getCompanyId());
		List<EmployeeLeaveCredit> employeeLeaveCredits = new ArrayList<>();
		employeeLeaveCredits.add(employeeLeaveCredit);
		List<EmployeeLeaveCreditDto> dataSource = employeeLeaveCreditService.convertToDto(employeeLeaveCredit);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format);
		String title = "EMPLOYEE LEAVE CREDIT";
		model.addAttribute("reportTitle", title);
		model.addAttribute("companyLogo", employeeLeaveCredit.getCompany().getLogo());
		if(format == "pdf") {
			model.addAttribute("companyLogo", employeeLeaveCredit.getCompany().getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ employeeLeaveCredit.getCompany().getLogo());
		}
		model.addAttribute("companyName", employeeLeaveCredit.getCompany().getName());
		model.addAttribute("companyAddress", employeeLeaveCredit.getCompany().getAddress());
		model.addAttribute("date", employeeLeaveCredit.getDate());
		model.addAttribute("leaveType", employeeLeaveCredit.getTypeOfLeave().getName());
		model.addAttribute("sequenceNo", employeeLeaveCredit.getSequenceNumber());
		model.addAttribute("remarks", employeeLeaveCredit.getRemarks());
		model.addAttribute("createdDate", DateUtil.formatDate(employeeLeaveCredit.getCreatedDate()));
		String companyTin = company.getTin();
		if(companyTin != null) {
			if(!companyTin.trim().isEmpty())
				companyTin = companyService.getTin(companyTin);
		}
		model.addAttribute("companyTin", companyTin);

		FormWorkflow formWorkflow = employeeLeaveCredit.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("receivedBy",
						user.getLastName() + ", " + user.getFirstName());
			} else if (log.getFormStatusId() == FormStatus.NOTED_ID) {
				model.addAttribute("notedBy", user.getLastName() + ", " + user.getFirstName());
			} else if (log.getFormStatusId() == FormStatus.VERIFIED_ID){
				model.addAttribute("verifiedBy", user.getLastName() + ", " + user.getFirstName());
			} else if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy", user.getLastName() + ", " + user.getFirstName());
			}
		}
	}

	@RequestMapping(value="/html", method = RequestMethod.GET)
	private String showHTMLPrintOut(@RequestParam(value="pId", required=true) Integer pId,
			Model model, HttpSession session){
		EmployeeLeaveCredit employeeLeaveCredit = employeeLeaveCreditService.getEmployeeLeaveCredit(pId);
		setParams(employeeLeaveCredit, model, "html", session);
		return "EmployeeLeaveCreditHTML.jasper";
	}
}
