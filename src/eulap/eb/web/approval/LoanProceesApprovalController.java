package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.LoanProceedsApprovalPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * A class that retrieves the approved and for approval of Loan Proceeds.

 *
 */
@Controller
@RequestMapping("loanProceedsWorkflow/{typeId}")
public class LoanProceesApprovalController extends WSMultiFormController{

	@Autowired
	private LoanProceedsApprovalPlugin loanProceedsApprovalPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return loanProceedsApprovalPlugin;
	}

}
