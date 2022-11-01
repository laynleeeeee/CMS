package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.service.PettyCashVoucherApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * A class that retrieves the approved and for approval of {@link PettyCashVoucher}.

 */
@Controller
@RequestMapping("pcvWorkflow/{typeId}")
public class PettyCashVoucherApprovalController extends WSMultiFormController{
	@Autowired
	private PettyCashVoucherApprovalPlugin pcvAppPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return pcvAppPlugin;
	}
}
