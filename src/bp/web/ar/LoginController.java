package bp.web.ar;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

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

import eulap.common.dto.LoginCredential;
import eulap.common.util.DateUtil;
import eulap.common.util.SimpleCryptoUtil;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserLoginStatus;
import eulap.eb.service.UserGroupAccessRightService;
import eulap.eb.service.UserLoginStatusService;
import eulap.eb.service.UserService;
import eulap.eb.validator.ChangePasswordValidator;
import eulap.eb.validator.LoginValidator;

/**
 * Login controller. 

 *
 */
@Controller
@RequestMapping ("/login")
public class LoginController {
	@Autowired
	private UserService userService;
	@Autowired
	private LoginValidator loginValidator;
	@Autowired
	private UserLoginStatusService userLoginStatusService;
	@Autowired
	private ChangePasswordValidator changePasswordValidator;
	@Autowired
	private UserGroupAccessRightService ugAccessRightService;
	private final Logger logger = Logger.getLogger(LoginController.class);

	@InitBinder
	public void initBindier(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showLoginPage (Model model, HttpSession currentSession){
		logger.info("Showing the Login page");
		LoginCredential loginCredential = new LoginCredential();
		model.addAttribute(loginCredential);
		return "loginPage";
	}

	@RequestMapping (method = RequestMethod.POST)
	public String validateUser (@ModelAttribute ("loginCredential") LoginCredential credential,
			 BindingResult result, Model model, HttpSession currentSession) {
		loginValidator.validate(credential, result);

		if (result.hasErrors()){
			CurrentSessionHandler.clearLoggedUser(currentSession);
			if (!userService.isUniqueUserName(credential.getUserName())) {
				int userId = userService.getUserIdByUsername(credential.getUserName()); 
				UserLoginStatus userLoginStatus = userLoginStatusService.getUserLoginStatus(userId);
				if (userLoginStatus.getFailedLoginAttempt() < 3) {
					userLoginStatus.setFailedLoginAttempt(userLoginStatus.getFailedLoginAttempt() + 1);
					userLoginStatusService.save(userLoginStatus);
					return "loginPage";
				} else {
					userLoginStatus.setBlockUser(true);
					userLoginStatusService.save(userLoginStatus);
					return "loginFail";
				}
			}
			return "loginPage";
		}
		int userId = userService.getUserIdByUsername(credential.getUserName()); 
		UserLoginStatus userLoginStatus = userLoginStatusService.getUserLoginStatus(userId);
		if (userLoginStatus == null || userLoginStatus.getSuccessfulLoginAttempt() == 0){
			model.addAttribute("userName", credential.getUserName());
			CurrentSessionHandler.saveUser(currentSession, userService, ugAccessRightService, credential);
			return "changePassword";
		} else if (userLoginStatus.isBlockUser()) {
			CurrentSessionHandler.clearLoggedUser(currentSession);
			return "loginFail";
		} else {
			userLoginStatus.setSuccessfulLoginAttempt(userLoginStatus.getSuccessfulLoginAttempt() + 1);
			userLoginStatus.setFailedLoginAttempt(0);
			userLoginStatus.setLastLogin(new Date());
			userLoginStatusService.save(userLoginStatus);
			CurrentSessionHandler.saveUser(currentSession, userService, ugAccessRightService, credential);
			return "redirectToMain";
		}
	}

	@RequestMapping (value = "/changePasswordForm", method = RequestMethod.GET)
	public String showChangePasswordPage (Model model, HttpSession currentSession){
		logger.info("Showing the Change Password form");
		LoginCredential loginCredential = new LoginCredential();
		model.addAttribute(loginCredential);
		return "changePasswordForm";
	}

	@RequestMapping (value = "/changePasswordForm", method = RequestMethod.POST)
	public String saveNewPassword (@ModelAttribute ("loginCredential") LoginCredential credential,
			 BindingResult result, Model model, HttpSession currentSession) throws NoSuchAlgorithmException {
		changePasswordValidator.validate(credential, result);
		if (result.hasErrors()){
			return "changePasswordForm";
		}
		int userId = userService.getUserIdByUsername(credential.getUserName()); 
		User user = userService.getUser(userId);
		user.setPassword(SimpleCryptoUtil.convertToSHA1(credential.getNewPassword()));
		userService.save(user, user, false);
		UserLoginStatus userLoginStatus = userLoginStatusService.getUserLoginStatus(userId);
		userLoginStatus.setSuccessfulLoginAttempt(1);
		userLoginStatus.setFailedLoginAttempt(0);
		userLoginStatus.setLastLogin(new Date());
		userLoginStatusService.save(userLoginStatus);

		logger.info("Successfully changed password of user name: "+user.getUsername());
		return "redirectToMain";
	}
}