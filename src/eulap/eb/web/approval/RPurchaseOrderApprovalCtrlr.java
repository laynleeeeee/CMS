package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.RPOFormPlugin;

/**
 * Controller class for Retail - Purchase Order approval.

 *
 */
@Controller
@RequestMapping ("/rPurchaseOrderWorkflow")
public class RPurchaseOrderApprovalCtrlr extends BaseWorkflowSearchController {
	@Autowired
	private RPOFormPlugin poFormPlugin;

	@Override
	FormApprovalPlugin getFormPlugin() {
		return poFormPlugin;
	}
}
