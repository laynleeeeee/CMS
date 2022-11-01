package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.ARMMultiApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * A class that retrieves the approved and for approval of AR Miscellaneous.

 *
 */
@Controller
@RequestMapping("ARMWorkflow/{typeId}")
public class ARMMultiApprovalController extends WSMultiFormController{
	@Autowired
	private ARMMultiApprovalPlugin plugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return plugin;
	}
}
