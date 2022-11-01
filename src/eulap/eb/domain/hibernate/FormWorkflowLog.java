package eulap.eb.domain.hibernate;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represent the FORM_WORK_FLOW_LOG in the database.

 *
 */
@Entity
@Table (name = "FORM_WORKFLOW_LOG")
public class FormWorkflowLog extends BaseDomain {
	private int formStatusId;
	private int formWorkflowId;
	private FormWorkflow formWorkflow;
	private FormStatus formStatus;
	private User created;
	private String comment;
	// Use as message holder when saving the status of the workflow.
	private String workflowMessage;
	
	public enum FIELD {
		id, formStatusId, formWorkflowId, createdBy, createdDate
	}
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "FORM_WORKFLOW_LOG_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}
	
	@Column (name="FORM_STATUS_ID", columnDefinition ="INT(10)")
	public int getFormStatusId() {
		return formStatusId;
	}

	public void setFormStatusId(int formStatusId) {
		this.formStatusId = formStatusId;
	}

	@Column (name="FORM_WORKFLOW_ID", columnDefinition="INT(10)")
	public int getFormWorkflowId() {
		return formWorkflowId;
	}

	public void setFormWorkflowId(int formWorkflowId) {
		this.formWorkflowId = formWorkflowId;
	}
	
	@ManyToOne
	@JoinColumn (name = "FORM_WORKFLOW_ID", insertable=false, updatable=false)
	public FormWorkflow getFormWorkflow() {
		return formWorkflow;
	}

	public void setFormWorkflow(FormWorkflow formWorkflow) {
		this.formWorkflow = formWorkflow;
	}
	
	public void setFormWorkFlowId(int formWorkFlowId) {
		this.formWorkflowId = formWorkFlowId;
	}
	
	@ManyToOne
	@JoinColumn (name = "FORM_STATUS_ID", insertable=false, updatable=false)
	public FormStatus getFormStatus() {
		return formStatus;
	}

	public void setFormStatus(FormStatus formStatus) {
		this.formStatus = formStatus;
	}

	@Column (name="CREATED_BY", columnDefinition="INT(10)")
	@Override
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Column (name="CREATED_DATE", columnDefinition="DATE")
	@Override
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}
	
	@Column (name="COMMENT", columnDefinition="DATE")
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@ManyToOne
	@JoinColumn (name = "CREATED_BY", insertable=false, updatable=false)
	public User getCreated() {
		return created;
	}
	
	public void setCreated(User created) {
		this.created = created;
	}

	@Transient
	public String getWorkflowMessage() {
		return workflowMessage;
	}
	
	public void setWorkflowMessage(String workflowMessage) {
		this.workflowMessage = workflowMessage;
	}

	/**
	 * Sort the forms bys status date.
	 * @param fws The list to be sorted.
	 */
	public static void sortByFormStatusDate (List<FormWorkflowLog> fws) {
		Collections.sort(fws, new Comparator<FormWorkflowLog>() {
			@Override
			public int compare(FormWorkflowLog fwls1, FormWorkflowLog fwls2) {
				Date createdDate1 = fwls1.getCreatedDate();
				Date createdDate2 = fwls2.getCreatedDate();
				if (createdDate1 == null && createdDate2 == null)
					return 0;
				if (createdDate1 == null)
					return 1;
				if (createdDate2 == null)
					return -1;
				
				if (createdDate1.after(createdDate2))
					return 1;

				return -1;
			}
		});
	}
}
