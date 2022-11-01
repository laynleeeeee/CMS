package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.APInvoiceApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * A class that retrieves the approved and for approval of AP Invoices.

 */
@Controller
@RequestMapping("apInvoiceWorkflow/{typeId}")
public class APInvoiceApprovalController extends WSMultiFormController{
	@Autowired
	private APInvoiceApprovalPlugin apInvoiceApprovalPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return apInvoiceApprovalPlugin;
	}
}
