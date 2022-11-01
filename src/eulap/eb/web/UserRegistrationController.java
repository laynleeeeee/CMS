package eulap.eb.web;

import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.UserRegistrationService;
import eulap.eb.validator.UserRegistrationValidator;
import eulap.eb.web.dto.UserRegistration;

/**
 * Controller class for User Registration module.

 *
 */
@Controller
@RequestMapping ("/registerUser")
public class UserRegistrationController {
	private static final Logger logger = Logger.getLogger(UserRegistrationController.class);
	@Autowired
	private UserRegistrationValidator userRegistrationValidator;
	@Autowired
	private UserRegistrationService userRegistrationService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showForm(Model model) {
		UserRegistration userRegistration = new UserRegistration();
		userRegistration.setCompany(new Company());
		userRegistration.setUser(new User());
		model.addAttribute("userRegistration", userRegistration);
		logger.info("Showing the New Account Registration form.");
		return "registerUser";
	}

	@RequestMapping (method = RequestMethod.POST)
	public String saveForm(@ModelAttribute ("userRegistration") UserRegistration registrationForm,
			 BindingResult result, Model model, HttpSession currentSession) throws NoSuchAlgorithmException {
		userRegistrationValidator.validate(registrationForm, result);
		if(result.hasErrors()) {
			logger.info("Form has error/s. Reloading the form.");
			model.addAttribute("userRegistration", registrationForm);
			return "registerUser";
		}

		logger.info("Saving Registration form.");
		userRegistrationService.saveRegistrationForm(registrationForm);
		return "successfullySaved";
	}

}
