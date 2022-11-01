package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.APPaymentApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;


/**
 * A class that retrieves the approved and for approval of AP Payments.

 *
 */
@Controller
@RequestMapping("apPaymentWorkflow/{typeId}")
public class APPaymentApprovalController extends WSMultiFormController{
	@Autowired
	private APPaymentApprovalPlugin apPaymentApprovalPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return apPaymentApprovalPlugin;
	}
}
