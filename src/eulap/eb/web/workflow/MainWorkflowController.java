package eulap.eb.web.workflow;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroupAccessRight;
import eulap.eb.service.EBModuleGenerator;
import eulap.eb.web.dto.ModuleConf;
import eulap.eb.web.dto.UGAccessRightFormDto;

/**
 * Controls the retrieval of different forms and its status

 *
 */
@Controller
@RequestMapping ("/workflows")
public class MainWorkflowController {
	private final Logger logger = Logger.getLogger(MainWorkflowController.class);
	@Autowired
	private String CMSFormPath;
	// TODO: Copy paste in ApprovalController. 
	@RequestMapping (method=RequestMethod.GET)
	public String showApprovalForms (HttpSession session, Model model) throws ConfigurationException {
		logger.info("showing forms...");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Set<ModuleConf> approvals = new TreeSet<ModuleConf>(new EBModuleGenerator.FormConfComparator ()); 
		String formPath = CMSFormPath;
		Date startTime = new Date();
		logger.info(formPath);
		for (UserGroupAccessRight uGAR : user.getUserGroup().getUserGroupAccessRights()) {
			int productKey = uGAR.getProductKey();
			int moduleKey = uGAR.getModuleKey();
			moduleKey = moduleKey | UGAccessRightFormDto.EDIT;
			approvals.addAll(EBModuleGenerator.getModules(formPath, EBModuleGenerator.APPROVAL, productKey, moduleKey));
		}
		model.addAttribute("approval",approvals);
		Date endTime = new Date();
		logger.info("total time : " + (endTime.getTime() - startTime.getTime()));
		return "showMainWorkflow";
	}
}