package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.ProjectRetention;
import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.ProjectRetentionFormPlugin;

/**
 * Controller class for {@link ProjectRetention} approval menu

 */

@Controller
@RequestMapping("projectRetentionWorkflow/{typeId}")
public class ProjectRetentionApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private ProjectRetentionFormPlugin formPlugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return formPlugin;
	}
}