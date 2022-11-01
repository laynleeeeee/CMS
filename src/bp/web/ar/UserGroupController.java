package bp.web.ar;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.domain.UserInfo;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.LabelName2FieldNameUG;
import eulap.eb.service.UserGroupService;
import eulap.eb.validator.UserGroupValidator;

import java.util.List;

/**
 * Controller for user group module. Handles the adding, updating and deleting of user group. 

 *
 */
@Controller
@RequestMapping ("/userGroup")
public class UserGroupController {
	public static final String USER_GROUP_ATTRIBUTE_NAME = "userGroups";
	private final UserGroupService userGroupService;
	private final UserGroupValidator userGroupValidator;
	private final Logger logger = Logger.getLogger(UserGroupController.class);

	@Autowired
	public UserGroupController (UserGroupService userGroupService, UserGroupValidator userGroupValidator){
		this.userGroupService = userGroupService;
		this.userGroupValidator = userGroupValidator;
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showUserGroupList (Model model, HttpSession currentSession) {		
		List<String> searchUserGroupOptionCategory =  LabelName2FieldNameUG.getLabels();
		List<String> searchUserGroupOptionStatus = SearchStatus.getSearchStatus();

		logger.info("GET: Show userGroupList");
		Page<UserGroup> page = userGroupService.getUserGroups("*", "name", 1);
		model.addAttribute("searchUserGroupOptionCategory", searchUserGroupOptionCategory);
		model.addAttribute("searchUserGroupOptionStatus", searchUserGroupOptionStatus);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		model.addAttribute(USER_GROUP_ATTRIBUTE_NAME, page);
		return "showUserGroupList";
	}

	@RequestMapping ( method = RequestMethod.GET, params = {"searchText", "category", "status", "page"})
	public String searchUserGroup (@RequestParam ("searchText") String searchText, 
			@RequestParam ("category") String category, 
			@RequestParam ("status") String status,
			@RequestParam ("page") int page, 
			Model model, HttpSession currentSession) {
		logger.info("GET: Search user group");
		PageSetting pageSetting = new PageSetting(page);
		Page<UserGroup> pageUserGroup = userGroupService.searchUserGroups(searchText, category,
				status, pageSetting);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, page);
		model.addAttribute(USER_GROUP_ATTRIBUTE_NAME, pageUserGroup);
		return "showUserGroupTable";
	}

	@RequestMapping (value = "/add", method = RequestMethod.GET)
	public String addUserGroup (Model model, HttpSession session) {
		logger.info("show user group form");
		UserGroup userGroup = new UserGroup ();
		model.addAttribute(userGroup);
		return "addUserGroup";
	}

	@RequestMapping (value = "/add", method = RequestMethod.POST)
	public String saveUserGroup (@ModelAttribute ("userGroup") UserGroup userGroup,
			BindingResult result, Model model, HttpSession session) {
		userGroupValidator.validate(userGroup, result);
		if (result.hasErrors())
			return "addUserGroup";
		saveUserGroup(userGroup);
		logger.info("Successfully saved the user group: "+userGroup.getName());
		return "successfullySaved";
	}

	@RequestMapping (value = "/edit/{userGroupId}", method = RequestMethod.GET)
	public String editUserGroup (@PathVariable ("userGroupId") String userGroupId,
			Model model, HttpSession session) {
		logger.info("editing user group : " + userGroupId);
		UserGroup userGroup = userGroupService.getUserGroup(Integer.valueOf(userGroupId));
		model.addAttribute(userGroup);
		return "editUserGroup";
	}

	@RequestMapping (value = "/edit/{userGroupId}", method = RequestMethod.POST)
	public String saveUserGroup (@PathVariable ("userGroupId") String userGroupId, 
			@ModelAttribute ("userGroup") UserGroup userGroup , BindingResult result, 
			Model model, HttpSession session) {
		userGroupValidator.validate(userGroup, result);
		if (result.hasErrors())
			return "editUserGroup";
		saveUserGroup(userGroup);
		logger.info("Successfully edit user group"+userGroup.getName());
		return "successfullySaved";
	}

	@RequestMapping (value = "/{pageNumber}/print", method = RequestMethod.GET)
	public String printUserGroup (@PathVariable ("pageNumber") int pageNumber, 
			Model model, HttpSession session) {
		logger.info("Printing...");
		Page<UserGroup> page = userGroupService.getUserGroups("", "name", pageNumber);
		model.addAttribute(USER_GROUP_ATTRIBUTE_NAME, page);
		return "printUserGroup";
	}

	/**
	 * Factored save function fragment used both by add and edit functions.
	 * @param ug The user group
	 */
	private void saveUserGroup (UserGroup ug) {
		//TODO: This should be retrieved from the session.
		UserInfo ui = new UserInfo (1, "user name", 1, "user group name");
		userGroupService.save(ug, true, ui);
	}
}