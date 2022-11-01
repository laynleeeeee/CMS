package eulap.eb.web.approval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

/**
 * A controller class that handles the entry point for approval

 *
 */
@Controller
@RequestMapping ("/approvals")
@Deprecated
public class ApprovalController {
	private final Logger logger = Logger.getLogger(ApprovalController.class); 
	@Autowired
	private String CMSFormPath;
	
	@RequestMapping (method=RequestMethod.GET)
	public String showApprovalForms (HttpSession session, Model model) throws ConfigurationException {
		logger.info("showing forms...");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<ModuleConf> approvals = new ArrayList<ModuleConf>();
		// TODO : Changed the user to user group.
//		for (UserAccessRight userAccessRight : user.getUserAccessRights()) {
//			int productKey = userAccessRight.getProductKey();
//			int moduleKey = userAccessRight.getModuleKey();
//			approvals.addAll(EBModuleGenerator.getModules(EBModuleGenerator.APPROVAL, productKey, moduleKey));
//		}
		String formPath = CMSFormPath;
		for (UserGroupAccessRight uGAR : user.getUserGroup().getUserGroupAccessRights()) {
			int productKey = uGAR.getProductKey();
			int moduleKey = uGAR.getModuleKey();
			
			approvals.addAll(EBModuleGenerator.getModules(formPath, EBModuleGenerator.APPROVAL, productKey, moduleKey));
		}
		Collections.sort(approvals, new EBModuleGenerator.FormConfComparator ());
		model.addAttribute("approval",approvals);
		return "showFormApproval";
	}
}
