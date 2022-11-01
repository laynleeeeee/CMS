package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.CashSaleFormPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Controller class for cash sale aprroval

 */
@Controller
@RequestMapping("/cashSaleWorkflow/{typeId}")
public class CashSaleApprovalController extends WSMultiFormController {
	@Autowired
	private CashSaleFormPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}
}
