package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.processing.fap.ProcessingReportPlugin;

/**
 * Controller class for processing report approval

 */
@Controller
@RequestMapping("/processingReportWorkflow/{typeId}")
public class ProcessingReportAppCtrlr extends WSMultiFormController {
	@Autowired
	private ProcessingReportPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}

}
