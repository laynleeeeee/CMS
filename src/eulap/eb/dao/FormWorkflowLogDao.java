package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FormWorkflowLog;

/**
 * An interface class that defines the data access mode of FormWorkflowLog

 *
 */
public interface FormWorkflowLogDao extends Dao<FormWorkflowLog>{
	/**
	 * Get the form work flow based on the form work flow and form status.
	 * @param formWorkflowId The unique id of form work flow.
	 * @param formStatusId The unique id of form status.
	 * @return The form work flow log.
	 */
	FormWorkflowLog getFormWorkflowLog (Integer formWorkflowId, Integer formStatusId);
	
	/**
	 * Get the list of form workflow logs by status id.
	 * @param formWorkflowId The id of the form workflow.
	 * @param statusId The id of the selected status.
	 * @return The list of form workflow logs.
	 */
	List<FormWorkflowLog> getWorkflowLogsByStatusId (int formWorkflowId, int statusId);

}
