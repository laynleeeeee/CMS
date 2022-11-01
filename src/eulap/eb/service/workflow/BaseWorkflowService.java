package eulap.eb.service.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;

import eulap.eb.dao.FormWorkflowDao;
import eulap.eb.dao.FormWorkflowLogDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectDomainService;

/**
 * Base class for workflow service.

 *
 */
public abstract class BaseWorkflowService extends ObjectDomainService implements WorkflowService {
	private static Logger logger = Logger.getLogger(BaseWorkflowService.class);
	private static int FIRST_INDEX = 0;
	private static int REMARKS_MAX_CHAR = 180;

	@Override
	public boolean isDeleteOOReference() {
		return true;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		// Do nothing will be implemented on the subclasses. 
	}

	@Override
	public void processAfterSaving (ApplicationContext ac, OOLinkHelper ooLinkHelper, WorkflowService serviceHandler,
			FormWorkflowLog currentWorkflowLog) {
		doAfterSaving(currentWorkflowLog);
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		// Do nothing
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		// Needed to be implemented in the implementing class.
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName,
			User user) {
		// Do nothing. Implement this in the subclass if needed.
	}

	@Override
	public void validate(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult, FormWorkflowDao workflowDao,
			FormWorkflowLogDao workflowLogDao, WorkflowServiceHandler workflowHandler, String propertyName, User user) {
		logger.info("Validating the current form workflow log.");
		List<FormStatus> formStatuses = workflowHandler.getAllStatuses(propertyName, user, true);
		String errorMessage = null;
		FormWorkflow workflow = workflowDao.get(currentWorkflowLog.getFormWorkflowId());
		logger.info("The current status of the form: "+workflow.getCurrentStatusId());

		Integer currentWfLogStatusId = currentWorkflowLog.getFormStatusId();
		logger.info("The current status of the form is to be changed to: "+currentWfLogStatusId);

		List<Integer> statusProps = getStatusIds(workflowHandler, propertyName, user);
		//Check if the current status is in the list of configured list of statuses of the form.
		int frequencyOfCurStatId = Collections.frequency(statusProps, currentWfLogStatusId);
		logger.debug("Frequency of the current status is: "+frequencyOfCurStatId);

		if (frequencyOfCurStatId == 0 && currentWfLogStatusId != FormStatus.CANCELLED_ID) {
			logger.warn("Updating the form status to:"+currentWfLogStatusId+"that is not in the configured list of statuses.");
		}


		//Iterate through the list of configured statuses of the form.
		List<FormWorkflowLog> fwLogs = null;
		for (int index = FIRST_INDEX; index < formStatuses.size(); index++) {
			FormStatus currentFormStatus = formStatuses.get(index);
			if (workflow != null) {
				if (currentWfLogStatusId.equals(currentFormStatus.getId())) {
					//Add exception for the forms that were configured with duplicate status.
					fwLogs = workflowLogDao.getWorkflowLogsByStatusId(workflow.getId(), 0);
					for (FormWorkflowLog fwl : fwLogs) {
						if (currentWfLogStatusId == fwl.getFormStatusId()) {
							errorMessage = "Status "+currentFormStatus.getDescription()+" was already updated by other user.";
							break;
						}
					}
				} else {
					//Validate if the current status of the form is CANCELLED and is to be changed.
					if (workflow.getCurrentStatusId() == FormStatus.CANCELLED_ID) {
						errorMessage = "Form was already cancelled.";
					}
				}
			}

			String remarks = currentWorkflowLog.getComment();
			if (remarks != null && !remarks.trim().isEmpty()) {
				if (remarks.trim().length() > REMARKS_MAX_CHAR) {
					errorMessage = "Remarks must not exceed "+REMARKS_MAX_CHAR+" characters.";
				}
			} else {
				if (currentWfLogStatusId == FormStatus.CANCELLED_ID) {
					errorMessage = "Remarks is required when cancelling the form.";
				}
			}

			if(errorMessage != null) {
				//Set the error message to the current workflow log.
				bindingResult.reject("workflowMessage", errorMessage);
				currentWorkflowLog.setWorkflowMessage(errorMessage);
				logger.warn(errorMessage);
				errorMessage = null;
				return;
			}
		}

		logger.info("Free up the memory allocation");
		logger.warn("Freeing up the memory allocation for form statuses: "+formStatuses);
		logger.warn("Freeing up the memory allocation for workflow: "+workflow);
		formStatuses = null;
		workflow = null;
	}

	/**
	 * Get the list of the configured status ids of the form.
	 */
	private List<Integer> getStatusIds (WorkflowServiceHandler workflowHandler, String propertyName, User user) {
		logger.debug("Retrieving the list of status ids of the property: "+propertyName);
		List<Integer> statusProps = new ArrayList<Integer>();
		FormProperty formProperty = workflowHandler.getProperty(propertyName, user);
		for (FormStatusProp prop : formProperty.getFormStatuses()) {
			statusProps.add(prop.getStatusId());
		}
		logger.trace("Added "+statusProps.size()+" to the list.");
		return statusProps;
	}
	
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		throw new RuntimeException("Implement this to method to its subclass.");
	}
}
