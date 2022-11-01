package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.CashSaleReturnFormPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Controller class for cash sale return aprroval

 */
@Controller
@RequestMapping("/cashSaleReturnWorkflow/{typeId}")
public class CashSaleReturnApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private CashSaleReturnFormPlugin multiPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return multiPlugin;
	}
}
