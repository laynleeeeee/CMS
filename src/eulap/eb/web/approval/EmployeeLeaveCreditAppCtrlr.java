package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.EmployeeLeaveCredit;
import eulap.eb.service.EmployeeLeaveCreditAppPlugin;
import eulap.eb.service.fap.FormApprovalPlugin;

/**
 * Search controller for {@link EmployeeLeaveCredit}

 *
 */
@Controller
@RequestMapping("/employeeLeaveCreditWorkflow")
public class EmployeeLeaveCreditAppCtrlr extends BaseWorkflowSearchController{

	@Autowired
	private EmployeeLeaveCreditAppPlugin plugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return plugin;
	}

}
