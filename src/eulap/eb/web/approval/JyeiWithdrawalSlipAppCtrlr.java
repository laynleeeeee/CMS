package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.JyeiWithdrawalSlipFormPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Controller class for JYEI Withdrawal Slip Approval.

 */
@Controller
@RequestMapping("/jyeiWithdwalSlipWorkflow/{typeId}")
public class JyeiWithdrawalSlipAppCtrlr extends WSMultiFormController {
	@Autowired
	private JyeiWithdrawalSlipFormPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}

}
