package eulap.eb.web.form;

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

import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroupAccessRight;
import eulap.eb.service.EBModuleGenerator;
import eulap.eb.web.dto.ModuleConf;
import bp.web.ar.CurrentSessionHandler;

/**
 * A controller class that controls the generation of different forms. 

 *
 */
@Controller
@RequestMapping ("forms")
public class FormController {
	private final Logger logger = Logger.getLogger(FormController.class);
	@Autowired
	private String CMSFormPath;
	
	@RequestMapping (method=RequestMethod.GET)
	public String showForms (HttpSession session, Model model) throws ConfigurationException {
		logger.info("showing forms...");
		User user = CurrentSessionHandler.getLoggedInUser(session);

		Set<ModuleConf> forms = new TreeSet<ModuleConf>(new EBModuleGenerator.FormConfComparator ());
		// TODO : Changed the user to user group.
//		for (UserAccessRight userAccessRight : user.getUserAccessRights()) {
//			int productKey = userAccessRight.getProductKey();
//			int moduleKey = userAccessRight.getModuleKey();
//			forms.addAll(EBModuleGenerator.getModules(EBModuleGenerator.FORMS, productKey, moduleKey));
//		}

		String formPath = CMSFormPath;
		for (UserGroupAccessRight uGAR : user.getUserGroup().getUserGroupAccessRights()) {
			int productKey = uGAR.getProductKey();
			int moduleKey = uGAR.getModuleKey();
			forms.addAll(EBModuleGenerator.getModules(formPath, EBModuleGenerator.FORMS, productKey, moduleKey));
		}
		model.addAttribute("forms",forms);
		return "showForms";
	}
}
