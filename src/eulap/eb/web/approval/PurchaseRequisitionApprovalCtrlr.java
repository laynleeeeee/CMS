package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.PurchaseRequisitionPlugin;

/**
 * Controller class for {@code RequisitionForm} approval menu

 *
 */
@Controller
@RequestMapping("purchaseRequisitionWorkflow/{typeId}")
public class PurchaseRequisitionApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private PurchaseRequisitionPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}
}