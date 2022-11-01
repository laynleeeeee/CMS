package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.PersonnelActionNotice;
import eulap.eb.service.PersonnelActionNoticeAppPlugin;
import eulap.eb.service.fap.FormApprovalPlugin;

/**
 * Search controller for {@link PersonnelActionNotice}

 *
 */
@Controller
@RequestMapping("/personnelActionNoticeWorkflow")
public class PersonnelActionNoticeAppCtrlr extends BaseWorkflowSearchController {
	@Autowired
	private PersonnelActionNoticeAppPlugin plugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return plugin;
	}

}
