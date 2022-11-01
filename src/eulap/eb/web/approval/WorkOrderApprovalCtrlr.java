package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.WorkOrderAppPlugin;

/**
 * Form approval controller for {@link WorkOrder}

 */

@Controller
@RequestMapping("/workOrderWorkflow")
public class WorkOrderApprovalCtrlr extends BaseWorkflowSearchController {
	@Autowired
	private WorkOrderAppPlugin plugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return plugin;
	}
}
