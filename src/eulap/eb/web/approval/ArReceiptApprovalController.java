package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.ArReceiptApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * A class that retrieves the approved and for approval of AR Receipts.

 *
 */
@Controller
@RequestMapping("arReceiptWorkflow/{typeId}")
public class ArReceiptApprovalController extends WSMultiFormController{
	@Autowired
	private ArReceiptApprovalPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}

}
