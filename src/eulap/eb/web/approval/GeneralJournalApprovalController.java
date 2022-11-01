package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.GeneralJournalFormPlugin;
import eulap.eb.service.fap.MultiFormPlugin;


/**
 * A controller class that handles the retrieval of approved and for approval GL entries per division.

 *
 */
@Controller
@RequestMapping ("generalJournalWorkflow/{typeId}")
public class GeneralJournalApprovalController extends WSMultiFormController {

	@Autowired
	private GeneralJournalFormPlugin glApprovalPlugin;

	@Override
		MultiFormPlugin getMultiFormPlugin() {
			return glApprovalPlugin;
		}
}
