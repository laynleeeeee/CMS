package eulap.eb.service.fap;

import eulap.common.util.Page;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Handles the multiform in retrieving the forms. 

 *
 */
public abstract class MultiFormPlugin extends BaseFormWorkflowPlugin {
	/**
	 * Retrieved the forms give.
	 * @param typeId the unique type id of the form.
	 * @param param the parameter of form plugin
	 * @return The list of forms.
	 */
	public abstract Page<FormApprovalDto> retrieveForms (int typeId, FormPluginParam param);
}
