package eulap.eb.web.workflow;

import java.io.InvalidClassException;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * A class that controls the workflow of the form

 *
 */
@Controller
@RequestMapping ("/workflow/{workflowName}")
public class FormWorkflowController {
	@Autowired
	private WorkflowServiceHandler workflowService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String showForm (@PathVariable (value="workflowName") String workflowName, 		
			@RequestParam (value="pId") int formId, Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException, ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		FormProperty prop = workflowService.getProperty(workflowName, user);
		model.addAttribute("formProperty", prop);
		// Adding 
		FormWorkflow workflow = workflowService.getWorkflow(workflowName, user, formId);
		FormWorkflowLog workflowLog = new FormWorkflowLog ();
		workflowLog.setId(workflow.getCurrentLogStatus().getId());
		workflowLog.setFormWorkflowId(workflow.getId());
		model.addAttribute("workflowlog", workflowLog);
		setUpModel(workflow, user, workflowName, formId, model);
		return "MainFormWorkflow.jsp";
	}
	
	@RequestMapping (method = RequestMethod.POST)
	public String saveForm (@PathVariable (value="workflowName") String workflowName,
			@RequestParam (value="pId") int formId,
			@ModelAttribute (value="formWorkflowLog") FormWorkflowLog formWorkflowLog,
			BindingResult bindingResult, 
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException, ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		workflowService.saveWorkflowLog(workflowName, formId, formWorkflowLog, user, bindingResult);
		if (bindingResult.hasErrors()) {
			FormWorkflow workflow = workflowService.getWorkflow(workflowName, user, formId);
			formWorkflowLog.setFormWorkflowId(workflow.getId());
			setUpModel(workflow, user, workflowName, formId, model);
			model.addAttribute("workflowlog", formWorkflowLog);
			return "MainFormWorkflow.jsp";
		}
		return "successfullySaved";
	}
	
	private void setUpModel (FormWorkflow workflow, User user, String workflowName, int formId, Model model) throws ConfigurationException {
		model.addAttribute("workflow", workflow);
		model.addAttribute("formId", formId);
		boolean hasEditAccess = workflowService.isAllowedToEdit(workflowName, user, workflow);
		model.addAttribute("isEditable", hasEditAccess);
		boolean hasCancelAccess = workflowService.isAllowedToCancel(workflowName, user, workflow);
		model.addAttribute("statuses", workflowService.getAvailabeStatus(hasCancelAccess, workflowName, user, workflow));
		model.addAttribute("hasAccess", workflowService.hasAccessToWorkflow(workflowName, user, workflow));
		model.addAttribute("isCancelled", workflowService.isFormCancelled(workflow));
		model.addAttribute("process", workflowService.getAllWorkflow(workflowName, user, workflow, false));
	}
}
