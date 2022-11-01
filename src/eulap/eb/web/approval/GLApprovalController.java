package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.GLApprovalPlugin;


/**
 * A controller class that handles the retrieval of approved and for approval GL entries.

 *
 */
@Controller
@RequestMapping ("glWorkflow")
public class GLApprovalController extends BaseWorkflowSearchController {

	@Autowired
	private GLApprovalPlugin glApprovalPlugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return glApprovalPlugin;
	}
}
