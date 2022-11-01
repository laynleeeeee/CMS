package eulap.eb.service.fap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * Base class for form workflow plugin

 *
 */
public abstract class BaseFormWorkflowPlugin implements FormWorkflowPlugin {
	@Autowired
	protected WorkflowServiceHandler workflowServiceHandler;

	@Override
	public List<FormStatus> getStatuses(String workflowName, User user) {
		List<FormStatus> ret = workflowServiceHandler.getAllStatuses(workflowName, null, false);
		FormStatus defaultStatus = workflowServiceHandler.getDefaultStatus(user, workflowName);
		for (FormStatus status : ret) {
			if  (defaultStatus.getId() == status.getId()){
				status.setSelected(true);
				break;
			}
		}
		FormStatus status = new FormStatus();
		status.setId(FormStatus.CANCELLED_ID);
		status.setDescription(FormStatus.CANCELLED_LABEL);
		ret.add(status);
		return ret;
	}
	
	public WorkflowServiceHandler getWorkflowServiceHandler() {
		return workflowServiceHandler;
	}
}
