package eulap.eb.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.ApLineSetup;
import eulap.eb.service.ApLineSetupService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class to retrieve the list of AP Line Setups.

 *
 */
@Controller
@RequestMapping(value="/getApLineSetups")
public class GetApLineSetups {
	private static Logger logger = Logger.getLogger(GetApLineSetups.class);
	@Autowired
	private ApLineSetupService apLineSetupService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getApLineSetups(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="name", required=false) String name) {
		List<ApLineSetup> apLineSetups = apLineSetupService.getApLineSetups(companyId, divisionId, name);
		if(apLineSetups.isEmpty()) {
			logger.debug("No AP Line Setups configured for search keyword: "
					+name+" and company id: "+companyId);
			return "No AP Line Setups found.";
		}
		logger.info("Retrieved "+apLineSetups.size()+" AP Line Setups under the company: "+companyId);
		String [] exclude = {"accountCombination"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(apLineSetups, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/byExactName",method = RequestMethod.GET)
	public @ResponseBody String getApLineSetup(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="name", required=false) String name) {
		logger.info("Retrieving AP Line Setup with comapnyId=" + companyId + "and AP Line Name=" + name);
		ApLineSetup apLineSetup = apLineSetupService.getApLineSetup(companyId, divisionId, name);
		String [] exclude = {"accountCombination"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONObject jsonObj = JSONObject.fromObject(apLineSetup, jConfig);
		return jsonObj.toString();
	}
}
