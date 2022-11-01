package eulap.eb.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.User;
import eulap.eb.service.UserService;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping("getUser")
public class GetUser {
	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String loadUser(@RequestParam(value="name",required=false)String name){
		User user = userService.getUserByName(name);
		JsonConfig jsonConfig = new JsonConfig();
		String[] excludes = {"userCompanies"};
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(user, jsonConfig);
		return jsonObject.toString();
	}
}
