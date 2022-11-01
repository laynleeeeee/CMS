package eulap.eb.web.approval;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.ReclassFormPlugin;

/**
 * Controller class to retrieve the reclass forms for approval.

 *
 */
@Controller
@RequestMapping ("/reclassWorkflow/{typeId}")
public class ReclassApprovalCtrlr extends WSMultiFormController {
	private static Logger logger = Logger.getLogger(ReclassApprovalCtrlr.class);
	@Autowired
	private ReclassFormPlugin rpFormPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		logger.info("Retrieving the list of Reclass forms for approval.");
		return rpFormPlugin;
	}
}