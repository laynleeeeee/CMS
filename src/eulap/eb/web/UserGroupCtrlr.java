package eulap.eb.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.UserGroupService;
import eulap.eb.validator.UserGroupValidator;
/**
 * A controller class of user group.

 */
@Controller
@RequestMapping("/admin/userGroup")
public class UserGroupCtrlr {
	private static final String USER_GROUP_ATTRIB_NAME = "userGroups";
	@Autowired
	private UserGroupService groupService;
	@Autowired
	private UserGroupValidator validator;

	@RequestMapping(method = RequestMethod.GET)
	public String showUserGroups(Model model) {
		model.addAttribute(USER_GROUP_ATTRIB_NAME, groupService.getUserGroups("", "", -1, 1));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		return "UserGroupMain.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, params={"groupName", "description", "status", "pageNumber"})
	public String getUserGroups(@RequestParam String groupName, @RequestParam String description,
			@RequestParam int status, @RequestParam int pageNumber,Model model) {
		model.addAttribute(USER_GROUP_ATTRIB_NAME, groupService.getUserGroups(groupName, description, status, pageNumber));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "UserGroupTable.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form")
	public String addUserGroup(Model model) {
		UserGroup userGroup = new UserGroup();
		userGroup.setActive(true);
		return showUserGroupForm(model, userGroup);
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form", params={"userGroupId"})
	public String editUserGroup(@RequestParam int userGroupId, Model model) {
		UserGroup userGroup = groupService.getUserGroup(userGroupId);
		return showUserGroupForm(model, userGroup);
	}

	private String showUserGroupForm(Model model, UserGroup userGroup) {
		model.addAttribute(userGroup);
		return "UserGroupForm.jsp";
	}

	@RequestMapping (method = RequestMethod.POST, value = "/form")
	public String saveUserGroup(@ModelAttribute("userGroup") UserGroup userGroup,
			BindingResult result, Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		validator.validate(userGroup, result);
		if(result.hasErrors())
			return showUserGroupForm(model, userGroup);
		groupService.save(userGroup, user);
		return "successfullySaved";
	}
}
