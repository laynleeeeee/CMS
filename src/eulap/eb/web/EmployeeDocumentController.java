package eulap.eb.web;

import java.io.InvalidClassException;
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
import eulap.eb.domain.hibernate.DocumentType;
import eulap.eb.domain.hibernate.EmployeeDocument;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.EmployeeDocumentService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.json.JSONArray;

/**
 * Controller class for {@link EmployeeDocument}

 *
 */
@Controller
@RequestMapping(value="/employeeDocument")
public class EmployeeDocumentController{
	@Autowired
	private EmployeeDocumentService employeeDocumentService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WorkflowServiceHandler workflowService;

	private static final String ED_ATTRIB_NAME = "employeeDocument";

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (value="/form", method = RequestMethod.GET)
	public String showClinicalCosmeticRecordForm(@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session){
		EmployeeDocument employeeDocument = new EmployeeDocument();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (pId == null) {
			employeeDocument.setDate(new Date());
		} else {
			employeeDocument = employeeDocumentService.getById(pId, true);
		}
		employeeDocument.serializeReferenceDocuments();
		return loadForm(employeeDocument, model, user);
	}
	
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String saveForm(@ModelAttribute ("employeeDocument") EmployeeDocument employeeDocument,
			BindingResult result, Model model, HttpSession session)throws InvalidClassException, ClassNotFoundException{
		User user = CurrentSessionHandler.getLoggedInUser(session);

		employeeDocument.deserializeReferenceDocuments();
		employeeDocument.setResult(result);
		ebFormServiceHandler.saveForm(employeeDocument, user);
		if(employeeDocument.getResult().hasErrors()){
			return loadForm(employeeDocument, model, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", employeeDocument.getSequenceNo());
		model.addAttribute("formId", employeeDocument.getId());
	    model.addAttribute("ebObjectId", employeeDocument.getEbObjectId());
		return "successfullySaved";
	}

	private String loadForm(EmployeeDocument employeeDocument, Model model, User user){
		List<DocumentType> documentTypes = employeeDocumentService.getActiveDocTypes();
		if(employeeDocument.getId() == 0) {
			model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		} else {
			model.addAttribute("companies", companyService.getCompaniesWithInactives(user, employeeDocument.getCompanyId()));
		}
		model.addAttribute("documentTypes", documentTypes);
		model.addAttribute(ED_ATTRIB_NAME, employeeDocument);
		return "EmployeeDocumentForm.jsp";
	}

	@RequestMapping(value = "/form/view", method=RequestMethod.GET)
	public String viewEmployeeDocumentRecord(@RequestParam(value = "pId", required = false) Integer pId,
			Model model, HttpSession session) throws ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		EmployeeDocument employeeDocument = employeeDocumentService.getById(pId, true);
		model.addAttribute(ED_ATTRIB_NAME, employeeDocument);
		if(employeeDocument.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(pId, "EmployeeDocument", user));
		}
		return "EmployeeDocumentView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, employeeDocumentService.getFormWorkflow(pId));
	}

	@RequestMapping(value="/search", method = RequestMethod.GET)
	public @ResponseBody String search(@RequestParam(required = true, value="criteria", defaultValue = "") String criteria,
			HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> results = employeeDocumentService.searchEmployeeDocument(criteria, user);
		JSONArray jsonArray = JSONArray.fromObject(results);
		return jsonArray.toString();
	}
}
