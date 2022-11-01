package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.ArTransactionRegApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * A class that retrieves the approved and for approval of regular AR Transactions.

 *
 */
@Controller
@RequestMapping("arTransactionRegWorkflow/{typeId}")
public class ArTransactionRegApprovalController extends WSMultiFormController{
	@Autowired
	private ArTransactionRegApprovalPlugin arTransactionRegApprovalPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return arTransactionRegApprovalPlugin;
	}
}
