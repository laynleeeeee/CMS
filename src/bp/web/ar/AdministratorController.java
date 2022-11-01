package bp.web.ar;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for administrator module. 

 *
 */
@Controller
@RequestMapping ("/admin")
public class AdministratorController {
	private final Logger logger = Logger.getLogger(AdministratorController.class);

	@RequestMapping (method = RequestMethod.GET)
	public String showAdministrator (Model model, HttpSession currentSession) {
		List<String> options = new ArrayList<String>();
		options.add("Users");
		options.add("Companies");
		options.add("User Groups");

		logger.info("GET: Show Administrator");
		model.addAttribute("options", options);
		return "showAdministrator";
	}
}
