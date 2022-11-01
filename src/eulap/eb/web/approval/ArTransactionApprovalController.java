package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.ArTransactionApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * A class that retrieves the approved and for approval of AR Transactions.

 *
 */
@Controller
@RequestMapping("arTransactionWorkflow/{typeId}")
public class ArTransactionApprovalController extends WSMultiFormController{
	@Autowired
	private ArTransactionApprovalPlugin arTransactionApprovalPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return arTransactionApprovalPlugin;
	}
}
