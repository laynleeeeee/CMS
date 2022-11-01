package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.FormStatusDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.EBModuleGenerator;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.FormStatusProp;
import eulap.eb.service.workflow.WorkflowPropertyGen;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ModuleConf;
import eulap.eb.web.dto.UGAccessRightFormDto;

/**
 * Business logic for generating form status.

 */
@Service
public class FormStatusService {
	private final Logger logger = Logger.getLogger(FormStatusService.class);
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private String CMSFormPath; 

	/**
	 * Get form statuses by accounting transaction type.
	 * @param acctTransactionType accounting transaction type.
	 * @param user the current login user.
	 * @param isSetCreatedToNew True to change new to created.
	 * @param hasCancelledStatus true to add cancel status.
	 * @return The form statuses for the selected accounting transaction type. 
	 */
	public List<FormStatus> getFormStatuses (int acctTransactionType, User user, boolean isSetCreatedToNew, boolean hasCancelledStatus) {
		Set<FormStatus> formStatuses = new HashSet<FormStatus> (workflowHandler.getAllStatuses(acctTransactionType, user, false));

		if(isSetCreatedToNew) {
			changeCreatedToNew(formStatuses);
		}

		if(hasCancelledStatus) {
			addCancelledStatus(formStatuses);
		}

		logger.info("Successfully retrieved " +formStatuses.size() + " statuses");
		return new ArrayList<FormStatus>(formStatuses);
	}

	private void changeCreatedToNew (Collection<FormStatus> statusList) {
		//Set CREATED description to NEW
		for(FormStatus stat : statusList) {
			if(stat.getId() == FormStatus.CREATED_ID) {
				stat.setDescription("NEW");
			}
		}
	}

	private void addCancelledStatus (Collection<FormStatus> statusList) {
		//Add CANCELLED Status to the list
		FormStatus cancelledStatus = formStatusDao.get(FormStatus.CANCELLED_ID);
		statusList.add(cancelledStatus);
	}

	/**
	 * Get the list of statuses of the form by product code.
	 * The statuses is based on the configuration in form-workflow.properties
	 * @param productCode The product code
	 * @param isIncludeCancelled True to include cancelled status
	 * @return The list of form statuses.
	 * @throws ConfigurationException 
	 */
	public List<FormStatus> getFormStatusesByPC (int productCode, boolean isIncludeCancelled) throws ConfigurationException {
		List<FormStatus> formStatuses = new ArrayList<FormStatus>();
		String formPath = CMSFormPath;
		List<ModuleConf> workflows = EBModuleGenerator.getModules(formPath, EBModuleGenerator.APPROVAL, productCode, 
				UGAccessRightFormDto.APPROVAL);
		if (workflows == null) {
			throw new RuntimeException("No workflow found.");
		}
		FormProperty formProperty = WorkflowPropertyGen.getFormProperty(formPath, workflows.iterator().next().getWorkflow(), null, null);
		for (FormStatusProp fsp : formProperty.getFormStatuses()) {
			formStatuses.add(formStatusDao.get(fsp.getStatusId()));
		}
		if (isIncludeCancelled) {
			formStatuses.add(formStatusDao.get(FormStatus.CANCELLED_ID));
		}
		return formStatuses;
	}

	/**
	 * Get {@link FormStatus} object by id.
	 * @param formStatusId The form Status id.
	 * @return The {@link FormStatus}.
	 */
	public FormStatus getFormStatus(Integer formStatusId) {
		return formStatusDao.get(formStatusId);
	}

	/**
	 * Get the list of workflow statuses of the form
	 * @param user The logged in user.
	 * @param propertyName The form property name
	 * @return The list of workflow statuses of the form
	 */
	public List<FormStatus> getFormStatuses(User user, String propertyName) {
		List<FormStatus> formStatuses = workflowHandler.getAllStatuses(propertyName, user, false);
		// Set is a unique collection.
		Set<FormStatus> statuses = new HashSet<FormStatus>();
		statuses.addAll(formStatuses);
		// Append cancelled status
		FormStatus cancelled = new FormStatus();
		cancelled.setDescription(FormStatus.CANCELLED_LABEL);
		cancelled.setId(FormStatus.CANCELLED_ID);
		cancelled.setSelected(true);
		statuses.add(cancelled);
		return new ArrayList<FormStatus>(statuses);
	}
}
