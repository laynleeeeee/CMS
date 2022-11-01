package eulap.eb.web.approval;

import java.util.List;
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

/**
 * Controls the retrieval of different forms and its status

 *
 */
@Controller
@RequestMapping ("/adminModules")
public class AdminController {
	private final Logger logger = Logger.getLogger(AdminController.class);
	// User group access right product key.
	private static final int UGAR_PROD_KEY = 183;
	@Autowired
	private String CMSFormPath;
	
	// TODO: Copy paste in ApprovalController. 
	@RequestMapping (method=RequestMethod.GET)
	public String showPage (HttpSession session, Model model) throws ConfigurationException {
		logger.info("showing admin modules...");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Set<ModuleConf> adminModules = new TreeSet<ModuleConf>(new EBModuleGenerator.FormConfComparator ()); 
		String formPath = CMSFormPath;
		for (UserGroupAccessRight uGAR : user.getUserGroup().getUserGroupAccessRights()) {
			int productKey = uGAR.getProductKey();
			int moduleKey = uGAR.getModuleKey();
			List<ModuleConf> moduleConfs = EBModuleGenerator.getModules(formPath, EBModuleGenerator.ADMIN, productKey, moduleKey); 
			adminModules.addAll(moduleConfs);
		}
		// If admin force add the user group access right menu.
		if (user.isAdmin()) {
			List<ModuleConf> modConfs = EBModuleGenerator.getModules(formPath, EBModuleGenerator.ADMIN, UGAR_PROD_KEY, 1);
			for (ModuleConf mc : modConfs) {
				if (!adminModules.contains(mc)) {
					adminModules.add(mc);
				}
			}
		}
		model.addAttribute("adminModules",adminModules);
		return "showAdmin";
	}
}
