package eulap.eb.web.report;

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
 * A controller class that controls the generation of different reports

 *
 */
@Controller
@RequestMapping ("reports")
public class ReportController {
	private final Logger logger = Logger.getLogger(ReportController.class);

	@Autowired
	private String CMSFormPath;
	
	@RequestMapping (method=RequestMethod.GET)
	public String showReports (HttpSession session, Model model) throws ConfigurationException{
		logger.info("processing reports...");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Set<ModuleConf> reports = new TreeSet<ModuleConf>(new EBModuleGenerator.FormConfComparator ());
		String formPath = CMSFormPath;
		for (UserGroupAccessRight uGAR : user.getUserGroup().getUserGroupAccessRights()) {
			int productKey = uGAR.getProductKey();
			int moduleKey = uGAR.getModuleKey();
			reports.addAll(EBModuleGenerator.getModules(formPath, EBModuleGenerator.REPORTS, productKey, moduleKey));
		}
		
		model.addAttribute("reports", reports);
		return "showReports";
	}
}
