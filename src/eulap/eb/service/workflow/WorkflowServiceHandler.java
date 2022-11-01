package eulap.eb.service.workflow;


import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.FormWorkflowDao;
import eulap.eb.dao.FormWorkflowLogDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.EBModuleGenerator;
import eulap.eb.service.common.EBApplicationContextUtil;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.web.dto.ModuleConf;

/**
 * A class that handles all the workflow services. 

 *
 */
@Service
public class WorkflowServiceHandler {
	@Autowired
	private FormWorkflowDao formWorkflowDao;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private FormWorkflowLogDao formWorkflowLogDao;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired 
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private String CMSFormPath; 
	
	/**
	 * Get the form property.
	 * @param propertyName The property name
	 * @param user The current login user. 
	 * @return The form property.
	 */
	public FormProperty getProperty(String propertyName, User user) {
		String formPath = CMSFormPath;
		return WorkflowPropertyGen.getFormProperty(formPath, propertyName, user, null);
	}

	private List<FormProperty> getPropertiesByType (int acctTransactionTypeId, User user) {
		String formPath = CMSFormPath;
		List<FormProperty> formProperties;
		try {
			formProperties = WorkflowPropertyGen.getAllFormProperties(formPath, user);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
		List<FormProperty> ret = new ArrayList<FormProperty>();
		for (FormProperty formProperty : formProperties) {
			if (formProperty.getAcctTransactionTypes().isEmpty())
				continue;
			for (Integer formAcctTransactionTypeId : formProperty.getAcctTransactionTypes()) {
				if (acctTransactionTypeId == formAcctTransactionTypeId) {
					ret.add(formProperty);
					break;
				}
			}
		}
		return ret;
	}

	public interface WorkflowPostProcessor {
		/**
		 * Post processing after saving the workflow.
		 * @param workflow Saved workflow
		 * @param log Saved workflow log.
		 * @param user the log user. 
		 */
		void doPost (FormWorkflow workflow, FormWorkflowLog log, User user);
	}

	/**
	 * Process the saving of workflow statuses. The workflow is already saved in the database, otherwise the hibernate
	 * will complain about it.
	 * @param propertyName The configured property name of the form.
	 * @param user The current log user.
	 * @param form The workflow form
	 * 
	 */
	public void processFormWorkflow (String workflowName, User user, BaseFormWorkflow form) {
		processFormWorkflow(workflowName, user, form, null);
	}

	/**
	 * Process the saving of workflow statuses. The workflow is already saved in the database, otherwise the hibernate
	 * will complain about it.
	 * @param propertyName The configured property name of the form.
	 * @param user The current log user.
	 * @param form The workflow form
	 * 
	 */
	public void processFormWorkflow (String workflowName, User user, BaseFormWorkflow form, WorkflowPostProcessor postProcess) {
		FormWorkflow workflow = form.getFormWorkflow();
		FormProperty property = getProperty(workflowName, user);
		boolean isNew = false;
		if (workflow == null) {
			if (property == null) // Throw if no property was found.
				throw new RuntimeException("invalid property name or user has no access right");
			// The first configured status
			workflow = new FormWorkflow();
			//Update the workflow
			isNew = true;
		}
		FormStatusProp nextStatus = getNextStatus(form.getFormWorkflow(), isNew, property);
		workflow.setCurrentStatusId(nextStatus.getStatusId());
		if (nextStatus.getNextId() == 0)
			workflow.setComplete(true);
		formWorkflowDao.save(workflow);
		form.setFormWorkflowId(workflow.getId());

		FormWorkflowLog log = new FormWorkflowLog();
		log.setFormWorkflowId(workflow.getId());
		log.setFormStatusId(nextStatus.getStatusId());
		log.setCreatedBy(user.getId());
		log.setCreatedDate(new Date());
		formWorkflowDao.save(log);
		if (postProcess != null)
			postProcess.doPost(workflow, log, user);
	}

	private FormStatusProp getNextStatus (FormWorkflow workflow, boolean isNew, FormProperty property) {
		if (isNew)
			return property.getFormStatuses().iterator().next();
		FormStatus formStatus = workflow.getCurrentStatus();
		//From the list traverse up to the current status.
		int index = 0;
		for (FormStatusProp prop : property.getFormStatuses()) {
			index++;
			if (prop.getStatusId() == formStatus.getId())
				break;
		}
		return property.getFormStatuses().get(index+1);
	}

	/**
	 * Get the workflow of the form. 
	 * @param propertyName The property name of that was configured in the form-workflow
	 * @param user The current logged user.
	 * @param id The unique id of the form. 
	 * @return The workflow object.
	 * @throws InvalidClassException 
	 * @throws ClassNotFoundException 
	 */
	public FormWorkflow getWorkflow (String propertyName, User user, int id) throws InvalidClassException, ClassNotFoundException {
		return getFormServiceHandler(propertyName, user).getFormWorkflow(id);
		
	}

	/**
	 * Get the Form service handler.
	 */
	public WorkflowService getFormServiceHandler (String propertyName, User user)
			throws InvalidClassException, ClassNotFoundException {
		FormProperty prop = getProperty(propertyName, user);
		String serviceClass = prop.getServiceClass();
		Object object = EBApplicationContextUtil.getInstanceof(applicationContext, serviceClass);
		return (WorkflowService) object;
	}

	/** Get the available status of the form.
	 * @param propertyName The configured property file.
	 * @param user The current log user.
	 * @param workflow The workflow that holds the workflow status of the form.
	 * @return The list of available forms.
	 */
	public List<FormStatus> getAvailabeStatus (boolean hasCancelledAccess, String propertyName, User user, FormWorkflow workflow) {
		List<FormStatus> ret = new ArrayList<FormStatus>();
		FormProperty prop = getProperty(propertyName, user);
		FormWorkflowLog currentWorkflowLog = workflow.getCurrentLogStatus();
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			FormStatus status = new FormStatus();
			status.setId(FormStatus.CANCELLED_ID);
			status.setDescription(FormStatus.CANCELLED_LABEL);
			ret.add(status);
			return ret;
		}
		// Identify the next status and add the existing status.
		FormStatusProp currentStatus = getStatusByStatusId(currentWorkflowLog.getFormStatusId(), prop);

		// The next status
		for (Integer nextId : currentStatus.getNextIds()) {
			if (nextId != 0) {
				FormStatusProp nextPropStatus = getStatusByPropId(nextId, prop);
				if (nextPropStatus.getStatusId() == currentStatus.getStatusId()) {
					int savedStatus = 0;
					for (FormWorkflowLog log : workflow.getFormWorkflowLogs()) {
						if (log.getFormStatusId() == currentStatus.getStatusId()) {
							savedStatus++;
						}
					}
					int similar = countSimilarNextStatus(1, currentStatus, prop);
					if (similar == savedStatus){
						FormStatusProp last = getLastSimilarStatId(nextPropStatus, prop);
						if (last.getNextId() != 0)
							addIfhasAccess(user, getStatusByPropId(last.getNextId(), prop), ret);
					} else {
						ret.add(formStatusDao.get(currentStatus.getStatusId()));
					}
				} else { // Completed process
					addIfhasAccess(user, nextPropStatus, ret);
				}
			}
		}

		if (hasCancelledAccess) {
			// Adding cancel
			FormStatus status = new FormStatus();
			status.setId(FormStatus.CANCELLED_ID);
			status.setDescription(FormStatus.CANCELLED_LABEL);
			ret.add( status);
		}
		return ret;
	}

	private void addIfhasAccess (User user, FormStatusProp currentStatus, List<FormStatus> ret) {
		if (user.hasAccess(currentStatus.getProductCode(), currentStatus.getModuleCode())) {
			FormStatus status = formStatusDao.get(currentStatus.getStatusId());
			if (!ret.contains(status))
				ret.add(status);
		}
	}
	
	private FormStatusProp getLastSimilarStatId (FormStatusProp currentStatus, FormProperty prop) {
		if (currentStatus.getNextId() == 0)
			return currentStatus;
		FormStatusProp next = getStatusByPropId(currentStatus.getNextId(), prop);
		if (currentStatus.getStatusId() == next.getStatusId())
			return getLastSimilarStatId(next, prop);
		return currentStatus;
	}
	
	private int countSimilarNextStatus (int count, FormStatusProp currentStatus, FormProperty prop) {
		if (currentStatus.getNextId() == 0) 
			return count;
		FormStatusProp nextPropStatus = getStatusByPropId(currentStatus.getNextId(), prop);
		if (nextPropStatus.getStatusId() == currentStatus.getStatusId()) 
			countSimilarNextStatus(count++, nextPropStatus, prop);
		return count;
	}
	
	
	private FormStatusProp getStatusByPropId (int fspId, FormProperty prop) {
		for (FormStatusProp status : prop.getFormStatuses()) {
			if (status.getId() == fspId) {
				return status;
			}
		}
		throw new RuntimeException("Unable to find status");
	}

	private FormStatusProp getStatusByStatusId (int statusId, FormProperty prop) {
		for (FormStatusProp status : prop.getFormStatuses()) {
			if (status.getStatusId() == statusId) {
				return status;
			}
		}
		throw new RuntimeException("Unable to find status");
	}

	/**
	 * Check if the user has access to the next workflow process. If the process is complete 
	 * this will return false.  
	 * @return True if user has access otherwise false.
	 */
	public boolean hasAccessRighToNextWF (String propertyName, FormWorkflow workflow, User user) {
		if(workflow.getCurrentStatusId() == FormStatus.CANCELLED_ID)
			return false;
		FormProperty fp = getProperty(propertyName, user);
		FormStatusProp currentStatus = getStatusByStatusId(workflow.getCurrentStatusId(), fp);
		FormStatusProp nextStatus = getLastSimilarStatId(currentStatus, fp);
		if (nextStatus.getNextId() == 0)
			return false;
		return user.hasAccess(nextStatus.getProductCode(), nextStatus.getModuleCode());
	}
	
	/**
	 * Save the status of the form. 
	 * @param selectedStatus The selected status.
	 * @param user The current log user. 
	 * @throws InvalidClassException 
	 * @throws ClassNotFoundException 
	 */
	public void saveWorkflowLog (String propertyName, int formId, FormWorkflowLog selectedStatus, User user, BindingResult bindingResult) throws InvalidClassException, ClassNotFoundException {
		WorkflowService serviceHandler = getFormServiceHandler(propertyName, user);
		selectedStatus.setCreatedBy(user.getId());
		serviceHandler.validate(selectedStatus, bindingResult, formWorkflowDao, formWorkflowLogDao, this, propertyName, user);
		if (bindingResult.hasErrors()) {
			return;
		}
		serviceHandler.doBeforeSaving(selectedStatus, bindingResult);
		if (bindingResult.hasErrors()) {
			return;
		}
		//If no changes, do not allow updates.
		FormProperty prop = getProperty(propertyName, user);
		if (selectedStatus.getId() != 0 && selectedStatus.getFormStatusId() != FormStatus.CANCELLED_ID
				&& selectedStatus.getFormStatusId() != FormStatus.STALED_ID) {
			FormWorkflowLog log = formWorkflowLogDao.get(selectedStatus.getId());
			FormStatusProp statusProp = getStatusByStatusId(log.getFormStatusId(), prop);
			FormStatusProp nextProp = getStatusByPropId(statusProp.getNextId(), prop);
			if (nextProp.getStatusId() != log.getFormStatusId() &&
					log.getFormStatusId() == selectedStatus.getFormStatusId())
				return;
		}

		//Update the workflow status 
		FormWorkflow workflow = formWorkflowDao.get(selectedStatus.getFormWorkflowId());
		workflow.setCurrentStatusId(selectedStatus.getFormStatusId());
		workflow.setComplete(isCompleted(prop, selectedStatus, workflow, user));
		formWorkflowDao.saveOrUpdate(workflow);
		// Log the status.
		selectedStatus.setId(0);
		selectedStatus.setCreatedDate(new Date());
		formWorkflowDao.save(selectedStatus);
		serviceHandler.processAfterSaving(applicationContext, ooLinkHelper, serviceHandler, selectedStatus);
	}
	
	private boolean isCompleted (FormProperty prop, FormWorkflowLog selectedStatus, FormWorkflow workflow, User user){
		if (selectedStatus.getFormStatusId() == FormStatus.CANCELLED_ID
				|| selectedStatus.getFormStatusId() == FormStatus.STALED_ID)
			return false;
		FormStatusProp statusProp = getStatusByStatusId(selectedStatus.getFormStatusId(), prop);
		if (statusProp.getNextId() == 0)
			return true;
		FormStatusProp nextProp = getStatusByPropId(statusProp.getNextId(), prop);
		if (statusProp.getStatusId() == nextProp.getStatusId()) {
			for (FormWorkflowLog log : workflow.getFormWorkflowLogs()) {
				if (log.getFormStatusId() == selectedStatus.getFormStatusId())
					return nextProp.getNextId() == 0;
			}
		}
		return statusProp.getNextId() == 0;
	}
	
	/**
	 * Validate if the user has access to edit the form. 
	 * @throws ConfigurationException 
	 */
	public boolean isAllowedToEdit (String propertyName, User user, FormWorkflow workflow) throws ConfigurationException {
		return isAllowedToEditOrCancel(propertyName, user, workflow, false);
	}

	/**
	 * Validate if the user has access to cancel the form.
	 * @throws ConfigurationException 
	 */
	public boolean isAllowedToCancel(String propertyName, User user, FormWorkflow workflow) throws ConfigurationException {
		return isAllowedToEditOrCancel(propertyName, user, workflow, true);
	}

	/**
	 * Validate if the user has access to edit or cancel the form.
	 * @throws ConfigurationException 
	 */
	private boolean isAllowedToEditOrCancel(String propertyName, User user, FormWorkflow workflow,
			boolean isCancel) throws ConfigurationException {
		if(isFormCancelled(workflow)) {
			return false;
		}
		FormProperty prop = getProperty(propertyName, user);
		FormStatusProp statusProp = getStatusByStatusId(workflow.getCurrentStatusId(), prop);
		// Check if user has access in the edit form.
		boolean hasAccessToWorkflow = hasAccessToWorkflow(propertyName, user, workflow);
			if (hasAccessToWorkflow || isCancel) {
				boolean hasEditAccess =  isAllowedToEditForm(prop, user, workflow);
				if(isCancel && hasEditAccess) {
					return true;
				} else {
					if (!hasEditAccess || (workflow.isComplete() && !statusProp.isEditable())) {
						return false;
					}
					return true;
				}
			}
			// If user has no access to the workflow but the current workflow was configured to be editable
			// set the form to be editable.
			return false;
	}

	/**
	 * Determine if the user is allowed to edit the form based on user access rights.
	 * @param propertyName The property name.
	 * @param user The user object.
	 * @param workflow The form workflow object.
	 * @return True if user is allowed to edit, otherwise false.
	 * @throws ConfigurationException
	 */
	public boolean hasEditAccess(String propertyName, User user, FormWorkflow workflow) throws ConfigurationException {
		return isAllowedToEditForm(getProperty(propertyName, user), user, workflow);
	}

	private boolean isAllowedToEditForm (FormProperty prop, User user, FormWorkflow workflow) throws ConfigurationException {
		String formPath = CMSFormPath;
		String editConf= prop.getEditConf();
		FormStatusProp statusProp = getStatusByStatusId(workflow.getCurrentStatusId(), prop);
		int productKey = statusProp.getProductCode();
		ModuleConf conf = EBModuleGenerator.getEditModuleConfig(formPath, editConf, productKey);
		return user.hasAccess(productKey, conf.getModuleCode());
	}

	/**
	 * Check if the user has access to the workflow.
	 * @param propertyName The configured property name
	 * @param user The current logged user.
	 * @param workflow The current workflow
	 * @return true if the user has access, otherwise false.
	 */
	public boolean hasAccessToWorkflow (String propertyName, User user, FormWorkflow workflow) {
		if(isFormCancelled(workflow)) {
			return false;
		}
		FormProperty prop = getProperty(propertyName, user);
		FormStatusProp statusProp = getStatusByStatusId(workflow.getCurrentStatusId(), prop);
		return user.hasAccess(statusProp.getProductCode(), statusProp.getModuleCode());
	}

	/**
	 * Check if the current status of the workflow is cancelled.
	 * @param workflow The current workflow.
	 * @return True if cancelled status, otherwise false.
	 */
	public boolean isFormCancelled(FormWorkflow workflow) {
		if (workflow.getCurrentStatusId() == FormStatus.CANCELLED_ID)
			return true;
		return false;
	}

	/**
	 * Get all the different status of form work flows.
	 * @param acctTransactionType the accounting transaction type.
	 * @param user The current login user.
	 * @param allowDuplicate true to allow duplicate, otherwise false.
	 * @return The list of form statuses per accounting transaction type.
	 */
	public List<FormStatus> getAllStatuses (int acctTransactionType, User user, boolean allowDuplicate) {
		List<FormStatus> statuses = new ArrayList<FormStatus>();
		for (FormProperty formProperty : getPropertiesByType(acctTransactionType, user)) {
			statuses.addAll(getAllStatuses(formProperty, user, allowDuplicate));
		}
		return statuses;
	}
	/**
	 * Get all of the statuses of the form. 
	 * @param propertyName The configured name in form-workflow.properties. The class name of the form. 
	 * @param user The log user. 
	 * @return The list of statuses of the form. 
	 */
	public List<FormStatus> getAllStatuses(String propertyName, User user, boolean allowDuplicate) {
		FormProperty prop = getProperty(propertyName, user);
		return getAllStatuses(prop, user, allowDuplicate);
	}

	private List<FormStatus> getAllStatuses(FormProperty prop, User user, boolean allowDuplicate) {
		List<FormStatus> statuses = new ArrayList<FormStatus>();
		for (FormStatusProp formProp : prop.getFormStatuses()){
			boolean contains =false;
			for (FormStatus status : statuses) {
				if (status.getId() == formProp.getStatusId()){
					contains = true;
					break;
				}
			}
			if (!contains || allowDuplicate){
				FormStatus status = formStatusDao.get(formProp.getStatusId());
				statuses.add(status);
			}				
		}
		return statuses;
	}
	/**
	 * Get the default workflow status of the user. 
	 * @param user The current log user. 
	 * @param propertyName The workflow name
	 * @return The default status. 
	 */
	public FormStatus getDefaultStatus (User user, String propertyName){
		List<FormStatus> statuses = getAllStatuses(propertyName, user, false);
		// If the user has access to more than one flow, use the 2nd flow only as default status
		FormProperty formProp = getProperty(propertyName, user);
		// We need to start from the last flow.
		ListIterator<FormStatus> it = statuses.listIterator(statuses.size());
		FormStatus defaultStatus = null;
		while (it.hasPrevious()) {
			FormStatus status = it.previous();
			FormStatusProp statusProp = getStatusByStatusId(status.getId(), formProp);
			if (user.hasAccess(statusProp.getProductCode(), statusProp.getModuleCode())){ 
				if (defaultStatus != null) {
					defaultStatus = status;
					break;
				} 
				defaultStatus = status;
			}
		}
		// If the user has no access rights to all work flows, set 1st workflow as default status
		if (defaultStatus == null) {
			defaultStatus = statuses.get(0);
		}
		return defaultStatus;
	}

	/**
	 * Get workflow process and set the current status.
	 * @param appendCancelled Set to true if append CANCELLED status,
	 * otherwise if current status is CANCELLED, it will only show CANCELLED status.
	 */
	public List<FormStatus> getAllWorkflow (String propertyName, User user,FormWorkflow workflow, boolean appendCancelled) {
		List<FormStatus> statuses = new ArrayList<FormStatus>();
		int currentStatusId = workflow.getCurrentStatusId();
		boolean isCancelled = currentStatusId == FormStatus.CANCELLED_ID;
		boolean isStaled = currentStatusId == FormStatus.STALED_ID;
		if ((isCancelled || isStaled) && !appendCancelled) {
			FormStatus formStatus = new FormStatus();
			formStatus.setDescription(isCancelled ? FormStatus.CANCELLED_LABEL : FormStatus.STALED_LABEL);
			formStatus.setId(isCancelled ? FormStatus.CANCELLED_ID : FormStatus.STALED_ID);
			formStatus.setSelected(true);
			statuses.add(formStatus);
			return statuses;
		}
		statuses = getAllStatuses(propertyName, user, false);
		int similarStatus = 0;
		for (FormWorkflowLog log : workflow.getFormWorkflowLogs()) {
			if (log.getFormStatusId() == workflow.getCurrentStatusId())
				similarStatus++;
		}
		int index = 0;
		if (similarStatus > 0){
			for (FormStatus status : statuses){
				if (similarStatus == 0)
					break;
				if (status.getId() == workflow.getCurrentStatusId()){
					similarStatus--;
				}
				index++;
			}
			index--;// zero base
		} else
			index = statuses.lastIndexOf(workflow.getCurrentStatus());
		if (index == -1 || appendCancelled){// form was cancelled
			FormStatus status = new FormStatus();
			status.setId(FormStatus.CANCELLED_ID);
			status.setDescription(FormStatus.CANCELLED_LABEL);
			status.setSelected(true);
			statuses.add(status);
		} else {
			statuses.get(index).setSelected(true);
		}
		return statuses;
	}
}
