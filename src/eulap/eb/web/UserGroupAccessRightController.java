package eulap.eb.web;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.UserGroupAccessRightService;
import eulap.eb.service.UserGroupService;
import eulap.eb.web.dto.UGAccessRightDto;

/**
 * User group access right controller.

 *
 */
@Controller
@RequestMapping("/admin/userGroupAccessRight")
public class UserGroupAccessRightController {
	@Autowired
	private UserGroupAccessRightService userGroupAccessRightService;
	@Autowired
	private UserGroupService userGroupService;

	@RequestMapping (method = RequestMethod.GET)
	public String showUserGroupAccessRights (Model model) throws ConfigurationException {
		model.addAttribute("userGroups", userGroupService.getActiveUserGroups());
		model.addAttribute("uGAccessRightDto", new UGAccessRightDto());
		model.addAttribute("uGAccessRightDto", setUGAccessRightDto(1));
		return "UserGroupAccessRight.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form", params={"userGroupId"})
	public String addUserGroupAccessRight(@RequestParam Integer userGroupId, Model model) throws ConfigurationException {
		return showUserGroupAccessRightForm(model, userGroupId, new UGAccessRightDto());
	}

	private UGAccessRightDto setUGAccessRightDto (Integer userGroupId) throws ConfigurationException {
		return userGroupAccessRightService.getUGAccessRightDto(userGroupId);
	}

	private String showUserGroupAccessRightForm (Model model, Integer userGroupId,
			UGAccessRightDto uGAccessRightDto) throws ConfigurationException {
		model.addAttribute("uGAccessRightDto", setUGAccessRightDto(userGroupId));
		return "UserGroupAccessRightTable.jsp";
	}

	@RequestMapping (method = RequestMethod.POST)
	public String saveUGAccessRight (@ModelAttribute("uGAccessRightDto") UGAccessRightDto uGAccessRightDto, 
			BindingResult result, Model model,
			HttpSession session) {
		User user =  CurrentSessionHandler.getLoggedInUser(session);
		userGroupAccessRightService.saveUGAccessRight(uGAccessRightDto, user);
		return "successfullySaved";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/hasAdminAccess", params={"userGroupId"})
	public @ResponseBody String getHasAdminAccess (@RequestParam int userGroupId) throws ConfigurationException {
		return userGroupAccessRightService.hasAdminModule(userGroupId) + "";
	}
}
