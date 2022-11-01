package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.service.fap.DeliveryReceiptAppPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Form approval controller for {@link DeliveryReceipt}

 */

@Controller
@RequestMapping("/drWorkflow/{typeId}")
public class DeliveryReceiptApprovalController extends WSMultiFormController {
	@Autowired
	private DeliveryReceiptAppPlugin plugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return plugin;
	}
}
