package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.TimeSheet;
import eulap.eb.service.TimeSheetAppPlugin;
import eulap.eb.service.fap.FormApprovalPlugin;

/**
 * Search controller for {@link TimeSheet}

 *
 */
@Controller
@RequestMapping("/timeSheetWorkflow")
public class TimeSheetAppCtrlr extends BaseWorkflowSearchController {
	@Autowired
	private TimeSheetAppPlugin plugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return plugin;
	}
}
