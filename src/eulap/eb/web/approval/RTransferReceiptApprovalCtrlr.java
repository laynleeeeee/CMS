package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.RTRFormPlugin;

/**
 * Controller class to retrieve the forms for Retail - Transfer Receipt approval.

 *
 */
@Controller
@RequestMapping ("/rTransferReceiptWorkflow/{typeId}")
public class RTransferReceiptApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private RTRFormPlugin trFormPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return trFormPlugin;
	}
}
