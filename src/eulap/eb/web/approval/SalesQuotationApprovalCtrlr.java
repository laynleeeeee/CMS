package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.SalesQuotation;
import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.SalesQuotationAppPlugin;

/**
 * Form approval controller for {@link SalesQuotation}

 */

@Controller
@RequestMapping("/salesQuotationWorkflow")
public class SalesQuotationApprovalCtrlr extends BaseWorkflowSearchController {
	@Autowired
	private SalesQuotationAppPlugin plugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return plugin;
	}
}
