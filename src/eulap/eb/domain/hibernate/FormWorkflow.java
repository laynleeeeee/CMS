package eulap.eb.domain.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represent the FORM_WORK_FLOW table in the database.

 *
 */
@Entity
@Table (name = "FORM_WORKFLOW")
public class FormWorkflow extends BaseDomain{
	private List<FormWorkflowLog> formWorkflowLogs;
	private int currentStatusId;
	private FormStatus currentFormStatus;
	private boolean complete;
	
	public enum FIELD {id, currentStatusId, complete};
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "FORM_WORKFLOW_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn (name = "FORM_WORKFLOW_ID", insertable=false, updatable=false)
	public List<FormWorkflowLog> getFormWorkflowLogs() {
		return formWorkflowLogs;
	}

	public void setFormWorkflowLogs(List<FormWorkflowLog> formWorkflowLogs) {
		this.formWorkflowLogs = formWorkflowLogs;
	}
	
	@Column (name="CURRENT_STATUS_ID" , columnDefinition="INT(10)")
	public int getCurrentStatusId() {
		return currentStatusId;
	}
	
	public void setCurrentStatusId(int currentStatusId) {
		this.currentStatusId = currentStatusId;
	}
	
	@ManyToOne
	@JoinColumn (name = "CURRENT_STATUS_ID", insertable=false, updatable=false)
	public FormStatus getCurrentFormStatus() {
		return currentFormStatus;
	}
	
	public void setCurrentFormStatus(FormStatus currentFormStatus) {
		this.currentFormStatus = currentFormStatus;
	}

	@Column (name="IS_COMPLETE" , columnDefinition="TINYINT(1)")
	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	/**
	 * The current status of the form
	 * @return The current status of the form. 
	 */
	@Transient
	public FormStatus getCurrentStatus() {
		return getCurrentLogStatus().getFormStatus();
	}
	
	/**
	 * Get the current logged workflow
	 */
	@Transient
	public FormWorkflowLog getCurrentLogStatus () {
		List<FormWorkflowLog> logs = getFormWorkflowLogs();
		FormWorkflowLog.sortByFormStatusDate(logs);
		return logs.get(logs.size() - 1);
	}
	/**
	 * List of completed process done. 
	 * @return The processes that was already done. 
	 */
	@Transient
	public List<FormStatus> getCompletedProcess() {
		List<FormWorkflowLog> logs = getFormWorkflowLogs();
		FormWorkflowLog.sortByFormStatusDate(logs);
		List<FormStatus> completedProcess = new ArrayList<FormStatus>();
		for (FormWorkflowLog log : logs)
			completedProcess.add(log.getFormStatus());
		return completedProcess;
	}
}
