package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.FormDeductionApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Controller class to retrieve the Form Deduction forms for approval.

 *
 */
@Controller
@RequestMapping ("/formDeductionWorkflow/{typeId}")
public class FormDeductionAppCtrlr extends WSMultiFormController{

	@Autowired
	private FormDeductionApprovalPlugin fdaPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return fdaPlugin;
	}

}