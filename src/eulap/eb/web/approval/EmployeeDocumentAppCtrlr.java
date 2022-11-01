package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.EmployeeDocument;
import eulap.eb.service.EmployeeDocumentAppPlugin;
import eulap.eb.service.fap.FormApprovalPlugin;

/**
 * Search controller for {@link EmployeeDocument}

 *
 */
@Controller
@RequestMapping("/employeeDocumentWorkflow")
public class EmployeeDocumentAppCtrlr extends BaseWorkflowSearchController{
	@Autowired
	private EmployeeDocumentAppPlugin plugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return plugin;
	}
}
