package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.service.fap.ArInvoiceAppPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Form approval controller for {@link ArInvoice}

 */

@Controller
@RequestMapping("/arInvoiceWorkflow/{typeId}")
public class ArInvoiceApprovalController extends WSMultiFormController {
	@Autowired
	private ArInvoiceAppPlugin plugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return plugin;
	}
}
