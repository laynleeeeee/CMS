package bp.web.ar;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.SimpleCryptoUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.domain.hibernate.UserLoginStatus;
import eulap.eb.service.CompanyService;
import eulap.eb.service.LabelName2FieldName;
import eulap.eb.service.UserGroupService;
import eulap.eb.service.UserLoginStatusService;
import eulap.eb.service.UserService;
import eulap.eb.validator.UserValidator;

@Controller
@RequestMapping ("/user")
public class UserController {
	public static final String USER_ATTRIBUTE_NAME = "users";
	private final UserService userService;
	private final UserValidator userValidator;
	private final CompanyService companyService;
	private final UserGroupService userGroupService;
	private final UserLoginStatusService userLoginStatusService;
	private final Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	public UserController (UserService userService, UserValidator userValidator, 
			CompanyService companyService, UserGroupService userGroupService,
			UserLoginStatusService userLoginStatusService){
		this.userService = userService;
		this.userValidator = userValidator;
		this.companyService = companyService;
		this.userGroupService = userGroupService;
		this.userLoginStatusService = userLoginStatusService;
	}

	@InitBinder
	public void initBindier(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showUserList (Model model) {	
		List<String> searchUserOptionCategory = LabelName2FieldName.getLabels();
		List<String> searchUserOptionStatus = SearchStatus.getSearchStatus();
		logger.info("GET: Show user list");

		Page<User> page = userService.getUsers("*", "username", 1);
		model.addAttribute("searchUserOptionCategory", searchUserOptionCategory);
		model.addAttribute("searchUserOptionStatus", searchUserOptionStatus);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		model.addAttribute(USER_ATTRIBUTE_NAME, page);
		return "showUserList";
	}

	@RequestMapping (method = RequestMethod.GET, params = {"searchText", "category", "status", "page"})
	public String searchUser (@RequestParam ("searchText") String searchText, 
			@RequestParam ("category") String category, 
			@RequestParam ("status") String status,
			@RequestParam ("page") int page, 
			Model model, HttpSession currentSession) {
		PageSetting pageSetting = new PageSetting(page);
		logger.info("GET: Search user at page: "+ pageSetting.getPageNumber());
		Page<User> pageUser = userService.searchUsers(searchText, category,
				status, pageSetting);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, page);
		model.addAttribute(USER_ATTRIBUTE_NAME, pageUser);
		return "showUserTable";
	}

	@RequestMapping (value = "/add", method = RequestMethod.GET)
	public String addUser (Model model, HttpSession session) {
		logger.info("Show user form");
		User user = new User ();
		setUpUserGroupAndCompany(model, false, user);
		model.addAttribute(user);
		return "addUser";
	}

	@RequestMapping (value = "/add", method = RequestMethod.POST)
	public String saveUser (@ModelAttribute ("user") User user,
			BindingResult result, Model model, HttpSession session) throws NoSuchAlgorithmException  {
		logger.info("Saving user");
		userValidator.validate(user, result);

		if (result.hasErrors()){
			model.addAttribute(user);
			setUpUserGroupAndCompany(model, false, user);
			return "addUser";
		}
		user.setPassword("root");
		if (user.getBirthDate() != null) user.setBirthDate(user.getBirthDate());
		saveUser(user, true, session) ;
		logger.info("Successfully saved user with username: "+user.getUsername());
		return "successfullySaved";
	}

	@RequestMapping (value = "/edit/{userId}", method = RequestMethod.GET)
	public String editUser (@PathVariable ("userId") String userId,
			Model model, HttpSession session) {
		logger.info("editing user: " + userId);
		User user = userService.getUser(Integer.valueOf(userId));
		setUpUserGroupAndCompany(model, true, user);
		model.addAttribute(user);
		return "editUser";
	}

	@RequestMapping (value = "/edit/{userId}", method = RequestMethod.POST)
	public String saveUser (@PathVariable ("userId") String userId, 
			@ModelAttribute ("user") User user, BindingResult result, 
			Model model, HttpSession session) throws NoSuchAlgorithmException  {
		userValidator.validate(user, result);
		if (result.hasErrors()){
			setUpUserGroupAndCompany(model, true, user);
			return "editUser";
		}
		user.setBirthDate(user.getBirthDate());
		saveUser(user, false, session) ;
		logger.info("Successfully saved user: "+user.getUsername());
		return "successfullySaved";
	}

	private void setUpUserGroupAndCompany(Model model, boolean isEdit, User user) {
		//Add the user groups to the model
		List<UserGroup> userGroups  = userGroupService.getAll();
		model.addAttribute("uUserGroups",userGroups);
		//add the companies to the model
		Collection<Company> companies = companyService.getAll();
		model.addAttribute ("uCompanies",companies);
	}

	@RequestMapping (value = "/{pageNumber}/print", method = RequestMethod.GET)
	public String printUser (@PathVariable ("pageNumber") int pageNumber, 
			Model model, HttpSession session) {
		logger.info("Printing...");
		Page<User> page = userService.getUsers("", "name", pageNumber);
		model.addAttribute(USER_ATTRIBUTE_NAME, page);
		return "printUser";
	}

	/**
	 * Factored save function fragment used both by add and edit functions for user module.
	 */
	private void saveUser (User user, boolean isNewRecord, HttpSession session) throws NoSuchAlgorithmException {
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		user.setPassword(SimpleCryptoUtil.convertToSHA1(user.getPassword()));
		userService.save(user, loggedUser, isNewRecord);
		UserLoginStatus userLoginStatus = new UserLoginStatus();
		userLoginStatus.setUserId(user.getId());
		userLoginStatus.setSuccessfulLoginAttempt(0);
		userLoginStatus.setFailedLoginAttempt(0);
		userLoginStatus.setBlockUser(false);
		userLoginStatusService.save(userLoginStatus);
	}
}