package bp.web.ar;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Logout controller. 

 *
 */
@Controller
@RequestMapping ("/logout")
public class LogoutController {
	@RequestMapping (method = RequestMethod.GET)
	public String logOut (Model model, HttpSession session){
		CurrentSessionHandler.clearLoggedUser(session);
		return "logoutPage";
	}
}
