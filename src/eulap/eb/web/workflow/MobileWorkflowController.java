package eulap.eb.web.workflow;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.CBSConstant;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroupAccessRight;
import eulap.eb.service.EBModuleGenerator;
import eulap.eb.web.dto.ModuleConf;

/**
 * Controls the retrieval of different forms and its statuses for mobile

 *
 */
@Controller
@RequestMapping ("/workflows/mobile")
public class MobileWorkflowController {
	private final Logger logger = Logger.getLogger(MobileWorkflowController.class);
	@Autowired
	private String CMSFormPath;
	
	@RequestMapping (method=RequestMethod.GET)
	public String showMobApprovalForms (HttpSession session, Model model) throws ConfigurationException {
		logger.info("showing forms...");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Set<ModuleConf> approvals = new TreeSet<ModuleConf>(new EBModuleGenerator.FormConfComparator ()); 
		String formPath = CMSFormPath;
		for (UserGroupAccessRight uGAR : user.getUserGroup().getUserGroupAccessRights()) {
			int productKey = uGAR.getProductKey();
			int moduleKey = uGAR.getModuleKey();
			approvals.addAll(EBModuleGenerator.getModules(formPath, EBModuleGenerator.APPROVAL, productKey, moduleKey));
		}
		model.addAttribute("serverDate", DateUtil.formatDate(new Date()));
		model.addAttribute("approval",approvals);
		return "showMobileMainWorkflow";
	}
}
