package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.PettyCashVoucherLiquidation;
import eulap.eb.service.PCVLApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * A class that retrieves the approved and for approval of {@link PettyCashVoucherLiquidation}.

 */
@Controller
@RequestMapping("pcvlWorkflow/{typeId}")
public class PCVLApprovalController extends WSMultiFormController{
	@Autowired
	private PCVLApprovalPlugin pcvlAppPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return pcvlAppPlugin;
	}
}
