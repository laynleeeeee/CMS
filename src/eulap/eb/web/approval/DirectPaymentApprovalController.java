package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.DirectPaymentApprovalPlugin;
import eulap.eb.service.fap.FormApprovalPlugin;

/**
 * A class that retrieves the approved and for approval of Direct Payments.

 *
 */
@Controller
@RequestMapping("directPaymentWorkflow")
public class DirectPaymentApprovalController extends BaseWorkflowSearchController{

	@Autowired
	private DirectPaymentApprovalPlugin directPaymentApprovalPlugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return directPaymentApprovalPlugin;
	}

}
