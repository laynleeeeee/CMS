package eulap.eb.domain.hibernate;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import eulap.eb.service.oo.OOParent;

/**
 * An interface thats defines the basic workflow form. 

 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class BaseFormWorkflow  extends OOBaseDomain implements OOParent{
	private Integer formWorkflowId;
	private FormWorkflow formWorkflow;
	
	@Column (name="FORM_WORKFLOW_ID", columnDefinition="INT(10)", nullable=true)
	public Integer getFormWorkflowId() {
		return formWorkflowId;
	}

	public void setFormWorkflowId(Integer formWorkflowId) {
		this.formWorkflowId = formWorkflowId;
	}
	
	@OneToOne
	@JoinColumn(name = "FORM_WORKFLOW_ID", insertable = false, updatable = false)
	public FormWorkflow getFormWorkflow() {
		return formWorkflow;
	}

	public void setFormWorkflow(FormWorkflow formWorkflow) {
		this.formWorkflow = formWorkflow;
	}

	/**
	 * Get the workflow name of this form.
	 * Sub class can extend this method to handle special cases like
	 * ap invoice and ar transaction which have types.
	 */
	@Transient
	public String getWorkflowName () {
		return this.getClass().getSimpleName();
	}

	/** 
	 * @return The short description of this object. 
	 */
	@Transient
	public String getShortDescription () {
		return toString();
	}
	
	/**
	 * get the transaction data of this form.
	 */
	@Transient
	public Date getGLDate() {
		throw new RuntimeException("sub class must implement this method");
	}
	
	/**
	 * Get the company id of this form. 
	 */
	@Transient
	public Integer getCompanyId() {
		throw new RuntimeException("sub class must implement this method");
	}
}
