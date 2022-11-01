package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.service.PettyCashReplenishmentPlugin;
import eulap.eb.service.PettyCashVoucherApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * A class that retrieves the approved and for approval of {@link APInvoice}.

 */
@Controller
@RequestMapping("pcrWorkflow/{typeId}")
public class PettyCashReplenishmentApprovalController extends WSMultiFormController{
	@Autowired
	private PettyCashReplenishmentPlugin pcrAppPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return pcrAppPlugin;
	}
}
