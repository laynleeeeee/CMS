package eulap.eb.web;

import java.io.InvalidClassException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.dao.ActionNoticeDao;
import eulap.eb.domain.hibernate.ActionNotice;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PersonnelActionNotice;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.PersonnelActionNoticeService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.json.JSONArray;

/**
 * Controller class for {@link PersonnelActionNotice}

 *
 */
@Controller
@RequestMapping("/personnelActionNotice")
public class PersonnelActionNoticeCtrlr {
	@Autowired
	private PersonnelActionNoticeService personnelActionNoticeService;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ActionNoticeDao actionNoticeDao;
	@Autowired
	private WorkflowServiceHandler workflowService;

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		PersonnelActionNotice personnelActionNotice = new PersonnelActionNotice();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isNew = pId == null;
		if (isNew) {
			personnelActionNotice.setDate(new Date());
		} else {
			personnelActionNotice = personnelActionNoticeService.getPAN(pId);
		}

		if(personnelActionNotice.getEbObjectId() != null){
			List<ReferenceDocument> refDocs =
					referenceDocumentService.getReferenceDocuments(personnelActionNotice.getEbObjectId());
			personnelActionNotice.setReferenceDocuments(refDocs);
		}
		loadForm(personnelActionNotice, user, model, isNew);
		return "PersonnelActionNoticeForm.jsp";
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String viewFrom(@RequestParam(value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws ConfigurationException {
		PersonnelActionNotice personnelActionNotice =
				personnelActionNoticeService.getPAN(pId);

		List<ReferenceDocument> referenceDocuments = null;
		if (personnelActionNotice.getEbObjectId() != null) {
			referenceDocuments = referenceDocumentService.getReferenceDocuments(personnelActionNotice.getEbObjectId());
			personnelActionNotice.setReferenceDocuments(referenceDocuments);
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(personnelActionNotice.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(pId, "PersonnelActionNotice", user));
		}
		model.addAttribute("personnelActionNotice", personnelActionNotice);
		return "PersonnelActionNoticeView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, personnelActionNoticeService.getFormWorkflow(pId));
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveForm(@ModelAttribute ("personnelActionNotice") PersonnelActionNotice personnelActionNotice,
			BindingResult result, Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isNew = personnelActionNotice.getId() == 0;
		personnelActionNotice.deserializeReferenceDocuments();
		personnelActionNotice.setResult(result);
		ebFormServiceHandler.saveForm(personnelActionNotice, user);
		if(personnelActionNotice.getResult().hasErrors()) {
			loadForm(personnelActionNotice, user, model, isNew);
			return "PersonnelActionNoticeForm.jsp";
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", personnelActionNotice.getSequenceNo());
		model.addAttribute("formId", personnelActionNotice.getId());
		model.addAttribute("ebObjectId", personnelActionNotice.getEbObjectId());
		return "successfullySaved";
	}

	private void loadForm(PersonnelActionNotice personnelActionNotice, User user, Model model,boolean isNew) {
		Integer actionNoticeId = null;
		Collection<Company> companies = new ArrayList<Company>();
		if (!isNew) {
			companies = companyService.getCompaniesWithInactives(user, personnelActionNotice.getCompanyId());
			actionNoticeId = personnelActionNotice.getActionNoticeId();
		} else {
			companies = companyService.getActiveCompanies(user, null, null, null);
		}
		personnelActionNotice.serializeReferenceDocuments();
		model.addAttribute("actionNotices", personnelActionNoticeService.getActionNotices(actionNoticeId));
		model.addAttribute("companies", companies);
		model.addAttribute("personnelActionNotice", personnelActionNotice);
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public @ResponseBody String searchForm(
			@RequestParam(required = true, value="criteria", defaultValue = "") String criteria,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> results = personnelActionNoticeService.searchForm(criteria, user);
		JSONArray jsonArray = JSONArray.fromObject(results);
		return jsonArray.toString();
	}

	@RequestMapping(value="/pdf", method=RequestMethod.GET)
	private String showPrintout(@RequestParam(value="pId", required=true) Integer pId, Model model) {
		PersonnelActionNotice personnelActionNotice = personnelActionNoticeService.getPAN(pId);
		setParams(personnelActionNotice, model, "pdf");
		return "PersonnelActionNotice.jasper";
	}

	public void setParams(PersonnelActionNotice personnelActionNotice, Model model, String format) {
		model.addAttribute("format", format);
		model.addAttribute("reportTitle", "PERSONNEL ACTION NOTICE");

		Company company = companyService.getCompany(personnelActionNotice.getCompanyId());
		model.addAttribute("parentCompanyName", company.getName());
		model.addAttribute("companyCode", company.getCompanyCode());

		model.addAttribute("employeeName", personnelActionNotice.getEmployeeFullName());
		model.addAttribute("date", DateUtil.removeTimeFromDate(personnelActionNotice.getDate()));
		model.addAttribute("hiredDate", personnelActionNotice.getHiredDate());
		model.addAttribute("justification", personnelActionNotice.getJustification());

		ActionNotice actionNotice = actionNoticeDao.get(personnelActionNotice.getActionNoticeId());
		model.addAttribute("actionNotice", actionNotice.getName());

		FormWorkflow formWorkflow = personnelActionNotice.getFormWorkflow();
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

	@RequestMapping(value="/html", method=RequestMethod.GET)
	private String showHTMLPrintout(@RequestParam(value="pId", required=true) Integer pId, Model model) {
		PersonnelActionNotice personnelActionNotice = personnelActionNoticeService.getPAN(pId);
		setParams(personnelActionNotice, model, "html");
		return "PersonnelActionNoticeHTML.jasper";
	}
}
