package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.SupplierAdvPaymentAppPlugin;

/**
 * Form approval controller for {@link SupplierAdvancePayment}

 */

@Controller
@RequestMapping("/advPaymentWorkflow/{typeId}")
public class SupplierAdvPaymentAppCtrlr extends WSMultiFormController {
	@Autowired
	private SupplierAdvPaymentAppPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}

}
