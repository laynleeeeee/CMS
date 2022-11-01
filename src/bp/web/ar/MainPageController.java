package bp.web.ar;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Redirect to the main page.

 *
 */
@Controller
@RequestMapping ("/main")
public class MainPageController {
	
	@RequestMapping (method = RequestMethod.GET)
	public String showMainPage (Model model) {
		return "mainPage";
	}
}
