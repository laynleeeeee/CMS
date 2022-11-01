package eulap.eb.web;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.PositionService;
import eulap.eb.service.UserGroupService;
import eulap.eb.service.UserLoginStatusService;
import eulap.eb.service.UserService;
import eulap.eb.validator.UserValidator;

/**
 * YBL User Forms Controller 

 */
@Controller
@RequestMapping ("/admin/userForms")
public class UserFormController {
	private static final String USER_ATTRIB_NAME = "user";
	@Autowired
	private PositionService posService;
	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserValidator userValidator;
	@Autowired
	private UserLoginStatusService loginStatusService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}
	
	@RequestMapping (method = RequestMethod.GET)
	public String showUserForms (Model model){
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY,1);
		model.addAttribute("users", userService.searchUser("", "", "", -1, -1, -1, -1, 1));
		loadSelections(model);
		return "showUserForms";
	}

	@RequestMapping(method = RequestMethod.GET, params = {"username","firstName","lastName","userGroupId","positionId","loginStatus","status","pageNumber"})
	public String searchUser(@RequestParam String username,
			@RequestParam String firstName, 
			@RequestParam String lastName,
			@RequestParam Integer userGroupId, 
			@RequestParam Integer positionId,
			@RequestParam Integer loginStatus,
			@RequestParam Integer status,
			@RequestParam Integer pageNumber,
			Model model){
		Page<User> users = userService.searchUser(username.trim(), firstName.trim(), lastName.trim(), userGroupId, positionId, loginStatus, status, pageNumber);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("users", users);
		return "UserTable.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String addUserForm(Model model){
		User user = new User();
		user.setActive(true);
		return showForm(user,model);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET, params = {"userId"})
	public String editUserForm(@RequestParam Integer userId, Model model){
		User user = userService.getUser(userId);
		user.setUserLoginStatus(loginStatusService.getUserLoginStatus(user.getId()));
		return showForm(user,model);
	}
	
	private String showForm(User user, Model model){
		model.addAttribute("userId",user.getId());
		model.addAttribute(USER_ATTRIB_NAME, user);
		return "UserForm.jsp";
	}

	@RequestMapping (value="/form", method = RequestMethod.POST)
	public String saveUser(@ModelAttribute("user") User user, BindingResult result,
			Model model, HttpSession session) throws NoSuchAlgorithmException{
		User loggedInUser = CurrentSessionHandler.getLoggedInUser(session);
		userValidator.validate(user, result);
		if(result.hasErrors()){
			return showForm(user,model);
		}
		userService.saveUser(user, loggedInUser);
		return "successfullySaved";
	}

	public void loadSelections(Model model){
		// Positions
		List<Position> positions = posService.getAllPositions();
		model.addAttribute("positions",positions);
		// User Groups
		List<UserGroup> userGroups = userGroupService.getAll();
		model.addAttribute("userGroups", userGroups);
	}
}
