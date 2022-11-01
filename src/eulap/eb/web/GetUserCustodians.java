package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.UserCustodian;
import eulap.eb.service.UserCustodianService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controller that will retrieve the {@link UserCustodian}.

 *
 */
@Controller
@RequestMapping(value="getUserCustodians")
public class GetUserCustodians {
	@Autowired
	private UserCustodianService userCustodianService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String showCustodianAccounts (@RequestParam(value="name", required=false) String name,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="userCustodianId", required=false) Integer userCustodianId,
			@RequestParam(value="isExact", required=false, defaultValue="false") boolean isExact,
			HttpSession session) {
		List<UserCustodian> ucs = userCustodianService.getUserCustodianByName(companyId, divisionId, userCustodianId, name, isExact);
		String [] exclude = {"userCustodianLines"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(ucs, jConfig);
		ucs = null; //Freeing up memory
		return jsonArray.toString();
	}
}
