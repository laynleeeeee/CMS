package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.PurchaseOrderDivFormPlugin;

/**
 * Controller class for retail - purchase order per division approval.

 */

@Controller
@RequestMapping ("/poByDivWorkflow/{typeId}")
public class PurchaseOrderDivApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private PurchaseOrderDivFormPlugin poFormPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return poFormPlugin;
	}
}
