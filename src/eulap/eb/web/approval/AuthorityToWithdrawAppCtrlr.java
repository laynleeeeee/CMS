package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.AuthorityToWithdraw;
import eulap.eb.service.fap.AuthorityToWithdrawAppPlugin;
import eulap.eb.service.fap.FormApprovalPlugin;

/**
 * Form approval controller for {@link AuthorityToWithdraw}

 */

@Controller
@RequestMapping("/atwWorkflow")
public class AuthorityToWithdrawAppCtrlr extends BaseWorkflowSearchController {
	@Autowired
	private AuthorityToWithdrawAppPlugin plugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return plugin;
	}
}
