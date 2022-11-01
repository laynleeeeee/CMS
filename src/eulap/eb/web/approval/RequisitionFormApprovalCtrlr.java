package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.RequisitionFormPlugin;

/**
 * Controller class for {@code RequisitionForm} approval menu

 */
@Controller
@RequestMapping("requisitionFormWorkflow/{typeId}")
public class RequisitionFormApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private RequisitionFormPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}
}