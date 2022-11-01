package eulap.eb.service;

import java.io.InvalidClassException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OODomain;
import eulap.eb.service.oo.OOServiceHandler;
import eulap.eb.service.oo.ObjectDomainService;
import eulap.eb.service.workflow.WorkflowService;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * Elasticbooks form service handler. This will handle the saving for form. 
 * This will process the workflow and the object to object relations of the forms.  

 *
 */
@Service
public class EBFormServiceHandler {

	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private OOServiceHandler o2oServiceHandler;

	/**
	 * Handle the saving for the form. 
	 * @param ebForm
	 * @throws ClassNotFoundException 
	 * @throws InvalidClassException 
	 */
	public void saveForm (BaseFormWorkflow form, User user) throws InvalidClassException, ClassNotFoundException {
		String workflowName = form.getWorkflowName();
		WorkflowService formService = workflowServiceHandler.getFormServiceHandler(workflowName, user);
		// pre processing from form service layer
		formService.preFormSaving(form, workflowName, user);
		if (form.getId() == 0 ) { // create the workflow for new forms only.
			workflowServiceHandler.processFormWorkflow(workflowName, user, form);
		}
		if (form instanceof OODomain && formService instanceof ObjectDomainService) {
			ObjectDomainService objectDomainService = (ObjectDomainService) formService;
			o2oServiceHandler.createAndLinkObject((OODomain) form, workflowName, user, workflowServiceHandler, objectDomainService, formService.isDeleteOOReference());
		}

		formService.saveForm(form, workflowName, user);
	}
}
