package eulap.eb.web;

import java.util.List;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.UserGroupService;

/**
 * Controller class for retreiving User groups.

 */
@Controller
@RequestMapping(value="getUserGroups")
public class GetUserGroups {
	@Autowired
	private UserGroupService userGroupService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String loadUserGroups(@RequestParam(value="term",required=false)String name){
		List<UserGroup> userGroups = userGroupService.getUserGroups(name.trim());
		JSONArray jsonArray = JSONArray.fromObject(userGroups);
		return jsonArray.toString();
	}

}
