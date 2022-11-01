package eulap.eb.service.fap;

import eulap.common.util.Page;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * An interface that handles the retrieval of forms that will be approved and approved forms. 

 *
 */
public abstract class FormApprovalPlugin extends BaseFormWorkflowPlugin{

	/**
	 * Retrieved the forms give.
	 * @param param the parameter of form plugin
	 * @return The list of forms.
	 */
	public abstract Page<FormApprovalDto> retrieveForms (FormPluginParam param);
}
