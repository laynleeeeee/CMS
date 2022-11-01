package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.User;
import eulap.eb.service.UserService;

/**
 * Get the users.

 *
 */
@Controller
@RequestMapping("/getUsers")
public class GetUsers {
	private final Logger logger = Logger.getLogger(GetUsers.class);
	@Autowired
	private UserService userService;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getUsers (@RequestParam(value="term", required=false) String name,
			HttpSession session) {
		List<User> users = userService.getUsersByName(name);
		for (User user : users) {
			user.setUserCompanies(userService.getUserCompaniesPerUser(user));
		}
		String[] exclude = {"userCompanies", "userGroup", "company",
				"accessRight", "serviceLeaseKey", "userAccessRights",
				"position", "userLoginStatus"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(users, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value="/byExactName")
	public @ResponseBody String getUser (@RequestParam(value="name", required=false) String name,
			HttpSession session) {
		List<User> users = userService.getUsersByName(name);
		if (users != null && !users.isEmpty()) {
			for (User user : users) {
				user.setUserCompanies(userService.getUserCompaniesPerUser(user));
			}
			String[] exclude = {"userCompanies", "userGroup", "company",
					"accessRight", "serviceLeaseKey", "userAccessRights",
					"position", "userLoginStatus"};
			JsonConfig jConfig = new JsonConfig();
			jConfig.setExcludes(exclude);
			JSONObject jsonObject = JSONObject.fromObject(users.iterator().next(), jConfig);

			return jsonObject.toString();
		}
		return "";
	}

	@RequestMapping(value="/position", method = RequestMethod.GET)
	public @ResponseBody String getUsers (
			@RequestParam (value="positionId") Integer positionId,
			@RequestParam (value="companyId", required=false) Integer companyId,
			HttpSession session) {
		logger.info("Retrieving users by position id : " + positionId);
		List<User> users = userService.getUsersByPosition(positionId, companyId);
		for (User user : users) {
			user.setUserCompanies(userService.getUserCompaniesPerUser(user));
		}
		String[] exclude = {"userCompanies", "userGroup", "company", 
				"accessRight", "serviceLeaseKey", "userAccessRights", 
				"position", "userLoginStatus"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(users, jConfig);
		return jsonArray.toString();
	}

}
