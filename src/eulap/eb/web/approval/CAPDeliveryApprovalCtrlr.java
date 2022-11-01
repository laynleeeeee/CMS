package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.CAPDeliveryPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Controller class for CAP Delivery Approval.

 *
 */
@Controller
@RequestMapping("/capDeliveryWorkflow/{typeId}")
public class CAPDeliveryApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private CAPDeliveryPlugin deliveryPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return deliveryPlugin;
	}

}
