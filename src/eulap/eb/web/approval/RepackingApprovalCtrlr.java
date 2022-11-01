package eulap.eb.web.approval;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.RepackingFormPlugin;

/**
 * Controller class to retrieve the Stock Adjustment forms for approval.

 *
 */
@Controller
@RequestMapping ("/repackingWorkflow/{typeId}")
public class RepackingApprovalCtrlr extends WSMultiFormController {
	private static Logger logger = Logger.getLogger(RepackingApprovalCtrlr.class);
	@Autowired
	private RepackingFormPlugin rpFormPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		logger.info("Retrieving the list of Repacking forms for approval.");
		return rpFormPlugin;
	}
}