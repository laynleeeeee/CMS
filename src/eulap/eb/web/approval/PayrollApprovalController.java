package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.PayrollApprovalPlugin;


/**
 * A controller class that handles the retrieval of payroll entries.

 *
 */
@Controller
@RequestMapping ("payrollWorkflow")
public class PayrollApprovalController extends BaseWorkflowSearchController {

	@Autowired
	private PayrollApprovalPlugin formPlugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return formPlugin;
	}
}
