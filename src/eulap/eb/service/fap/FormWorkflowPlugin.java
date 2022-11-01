package eulap.eb.service.fap;

import java.util.List;

import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;

/**
 * A class that define the form based workflow that will be plugged in searching for 
 * the different forms. 

 *
 */
public interface FormWorkflowPlugin {
	
	/**
	 * Get the different status of the associated form.
	 * @param workflowName The name of the workflow that is configured in form-workflow.properties 
	 * @return All of the status
	 */
	List<FormStatus> getStatuses (String workflowName, User user);
}
