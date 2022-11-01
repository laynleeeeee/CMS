package eulap.eb.service.workflow;

import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;

import eulap.eb.dao.FormWorkflowDao;
import eulap.eb.dao.FormWorkflowLogDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;

/**
 * A class that defines the standard workflow business logic.  

 *
 */
public interface WorkflowService {
	
	/**
	 * Get the workflow of the form. 
	 * @param id The unique id of the form
	 * @return The workflow obect of the form. 
	 */
	FormWorkflow getFormWorkflow (int id);

	/**
	 * Get the actual form.
	 * @param ebObjectId the object id of the form.
	 * @return
	 */
	BaseFormWorkflow getForm (Integer ebObjectId);

	/**
	 * Get the form by workflow id. 
	 * @param workflowId the workflow id. 
	 * @return
	 */
	BaseFormWorkflow getFormByWorkflow (Integer workflowId);
	/**
	 * Perform extra processing before the actual saving.
	 * @param form The form to be saved
	 * @param workflowName The workflow name.
	 * @param user The current log-in user.
	 */
	void preFormSaving (BaseFormWorkflow form, String workflowName, User user);

	/**
	 * Save the form after the workflow has been saved. 
	 * @param form The form to be saved. 
	 * @param workflowName The workflow name
	 * @param user The current log-in user. 
	 */
	void saveForm (BaseFormWorkflow form, String workflowName, User user);

	/**
	 * Check whether to delete or not the previous reference objects.
	 * @return true to delete the reference, otherwise false.
	 */
	boolean isDeleteOOReference ();

	/**
	 * Do the operation before saving
	 * @param currentWorkflowLog The current workflow log.
	 * @param bindingResult the binding result.
	 */
	void doBeforeSaving (FormWorkflowLog currentWorkflowLog, BindingResult bindingResult);
	
	/**
	 * Do the post operation after the workflow status was changed. 
	 * @param currentWorkflowLog The selected workflow status.
	 */
	void doAfterSaving (FormWorkflowLog currentWorkflowLog);
	
	/**
	 * This will be called after saving of the entity. 
	 * @param currentWorkflowLog the current workflow status
	 */
	void processAfterSaving (ApplicationContext ac, OOLinkHelper ooLinkHelper, WorkflowService serviceHandler, FormWorkflowLog currentWorkflowLog);
	/**
	 * Validate the workflow log before saving.
	 * @param currentWorkflowLog The current workflow log to be evaluated.
	 * @param bindingResult The binding result.
	 * @param workflowDao The DAO layer of {@link FormWorkflow}
	 * @param workflowLogDao The DAO layer of {@link FormWorkflowLog}
	 * @param workflowHandler The service handler for the business logic of FormWorkflow
	 * @param propertyName The property name of the form.
	 * @param user The current logged user.
	 */
	void validate(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult, FormWorkflowDao workflowDao,
			FormWorkflowLogDao workflowLogDao, WorkflowServiceHandler workflowHandler, String propertyName, User user);
}
