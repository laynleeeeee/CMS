package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.CustAdvancePaymentFormPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Controller class for customer advance payment aprroval

 */
@Controller
@RequestMapping("/customerAdvancePaymentWorkflow/{typeId}")
public class CustAdvancePaymentApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private CustAdvancePaymentFormPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}
}
