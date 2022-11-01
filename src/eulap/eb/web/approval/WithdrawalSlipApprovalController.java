package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.WithdrawalSlipormPlugin;

/**
 * Controller class for withdrawal slip aprroval

 */
@Controller
@RequestMapping("/withdwalSlipWorkflow")
public class WithdrawalSlipApprovalController extends BaseWorkflowSearchController {
	@Autowired
	private WithdrawalSlipormPlugin formPlugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return formPlugin;
	}
}
