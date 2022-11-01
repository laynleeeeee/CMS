package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.AccountSalesPlugin;
import eulap.eb.service.fap.FormApprovalPlugin;

/**
 * Controller class for Account Sales approval.

 *
 */
@Controller
@RequestMapping ("/accountSalesWorkflow")
public class AccountSalesApprovalCtrlr extends BaseWorkflowSearchController {
	@Autowired
	private AccountSalesPlugin asFormPlugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return asFormPlugin;
	}
}
