package eulap.eb.webservice;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.service.UserService;

/**
 * A web-service class that handles the user credential. 

 *
 */
@Controller
@RequestMapping("/webservice/logIn")
public class WebServiceLoginController {
	private static Logger logger = Logger.getLogger(WebServiceLoginController.class);
	@Autowired
	private UserService userService;

	/**
	 * A web-service entry point that will check the login credential.
	 * The request input stream should have the following object in order.
	 * 1. WebCredential
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws ParseException
	 */
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody String logIn (HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) 
			throws IOException, ClassNotFoundException, ParseException {
		logger.info("Checking user credential.");
		userService.processCredential(request, response);
		return "Done";
	}
}
