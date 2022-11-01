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

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.service.ArLineSetupService;

/**
 * Controller that will retrieve the AR Line Setups.

 *
 */
@Controller
@RequestMapping ("getArLineSetups")
public class GetArLineSetups {
	private static Logger logger = Logger.getLogger(GetArLineSetups.class);
	@Autowired
	private ArLineSetupService arLineSetupService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getArLines(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="arCustAcctId", required=false) Integer arCustAcctId,
			@RequestParam(value="id", required=false) Integer id,
			@RequestParam(value="noLimit", required=false) Boolean noLimit,
			@RequestParam(value="isExact", required=false) Boolean isExact,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			HttpSession session) {
		List<ArLineSetup> arLineSetups = arLineSetupService.getArLineSetUps(name, arCustAcctId, id,
				noLimit, isExact, CurrentSessionHandler.getLoggedInUser(session), divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arLineSetups, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/byCompany", method = RequestMethod.GET)
	public @ResponseBody String getArLinesByCompany(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="name", required=false) String name, HttpSession session) {
		List<ArLineSetup> arLineSetups = arLineSetupService.getArLineSetups(companyId, name, null,
				CurrentSessionHandler.getLoggedInUser(session));
		String [] exclude = {"accountCombination"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(arLineSetups, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/getALSetup", method = RequestMethod.GET)
	public @ResponseBody String getArLine(@RequestParam(value="id", required=false) Integer arLineSetupId,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="name", required=false) String name) {
		ArLineSetup arLineSetup = new ArLineSetup();
		if (arLineSetupId != null) {
			logger.info("Retrieving AR Line Setup with id: "+arLineSetupId);
			arLineSetup = arLineSetupService.getLineSetup(arLineSetupId);
		} else if(companyId != null && name != null) {
			logger.info("Retrieving AR Line Setup using the company id: "
					+companyId+" and name: "+name);
			arLineSetup = arLineSetupService.getALSetupByNameAndCompany(name, companyId, divisionId);
		}
		String [] exclude = {"accountCombination"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(arLineSetup, jsonConfig);
		if(arLineSetup == null) {
			String message = "No AR Line Setup found";
			logger.info(message);
			return message;
		}
		logger.info("Successfully retrieved AR Line Setup: "+arLineSetup.getName());
		return jsonObject.toString();
	}
}
