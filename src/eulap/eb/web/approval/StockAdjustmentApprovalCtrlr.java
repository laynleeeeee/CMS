package eulap.eb.web.approval;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.SAFormPlugin;

/**
 * Controller class to retrieve the Stock Adjustment forms for approval.

 *
 */
@Controller
@RequestMapping ("/stockAdjustmentWorkflow/{typeId}")
public class StockAdjustmentApprovalCtrlr extends WSMultiFormController{
	private Logger logger = Logger.getLogger(StockAdjustmentApprovalCtrlr.class);
	@Autowired
	private SAFormPlugin saFormPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		logger.info("Retrieving the list of Stock Adjustment forms for approval.");
		return saFormPlugin;
	}
}
