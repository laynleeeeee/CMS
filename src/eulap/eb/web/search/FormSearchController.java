package eulap.eb.web.search;

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
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroupAccessRight;
import eulap.eb.service.EBModuleGenerator;
import eulap.eb.web.dto.ModuleConf;

/**
 * A controller class that handles the searching of different forms 

 *
 */
@Controller
@RequestMapping ("search")
public class FormSearchController {
	private final Logger logger = Logger.getLogger(FormSearchController.class);
	@Autowired
	private String CMSFormPath;
	
	@RequestMapping (method = RequestMethod.GET)
	public String showSearchForm (@RequestParam (required=true) String criteria, HttpSession session, Model model) throws ConfigurationException {
		logger.info("showing search form...");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Set<ModuleConf> searchPlugins = new TreeSet<ModuleConf>(new EBModuleGenerator.FormConfComparator ());
		String formPath = CMSFormPath;
		for (UserGroupAccessRight uGAR : user.getUserGroup().getUserGroupAccessRights()) {
			int productKey = uGAR.getProductKey();
			int moduleKey = uGAR.getModuleKey();
			searchPlugins.addAll(EBModuleGenerator.getModules(formPath, EBModuleGenerator.FORM_SEARCH, productKey, moduleKey));
		}
		model.addAttribute("formSearch",searchPlugins);
		model.addAttribute("criteria", criteria);
		return "showSearchForm";
	}
}
