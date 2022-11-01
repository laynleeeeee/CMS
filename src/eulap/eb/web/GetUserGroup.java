package eulap.eb.web;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.UserGroupService;

/**
 * Controller for getting User Group object.

 */
@Controller
@RequestMapping(value="getUserGroup")
public class GetUserGroup {
	@Autowired
	private UserGroupService userGroupService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String loadUserGroup(@RequestParam(value="name",required=false)String name){
		UserGroup userGroup = userGroupService.getUserGroupByName(name.trim());
		JSONObject jsonObject = JSONObject.fromObject(userGroup);
		return jsonObject.toString();
	}

}
