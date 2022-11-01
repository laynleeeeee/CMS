package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.ApInvoiceItemFormPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Controller class for AP Invoice item/service approval menu

 */

@Controller
@RequestMapping("apInvoiceItemWorkflow/{typeId}")
public class ApInvoiceItemApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private ApInvoiceItemFormPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}
}