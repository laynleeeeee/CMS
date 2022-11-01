package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.DivisionService;
import eulap.eb.web.dto.DivisionDto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * A controller class that retrieves the division based on the configured
 * account combination or its division number.

 *
 */
@Controller
@RequestMapping ("getDivisions")
public class GetDivision {
	@Autowired
	private DivisionService divisionService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getDivisions (@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="divisionNumber", required=false) String divisionNumber, HttpSession session) {
		String returnMessage = "";
		if(companyId != null) {
			List<Division> divisions = (divisionId != null ? (List<Division>) divisionService.getDivisions(companyId, divisionId) : 
				divisionService.getDivisions(companyId));
			JsonConfig jConfig = new JsonConfig();
			JSONArray jsonArray = JSONArray.fromObject(divisions, jConfig);
			returnMessage = jsonArray.toString();
		} else if (divisionNumber != null) {
			User user = CurrentSessionHandler.getLoggedInUser(session);
			Division division = divisionService.getDivisionByDivNumber(divisionNumber, user);
			JSONObject jsonObject = JSONObject.fromObject(division);
			returnMessage = division == null ? "No division found" : jsonObject.toString();
		}
		return returnMessage;
	}

	@RequestMapping (value="/new",method = RequestMethod.GET)
	public @ResponseBody String getNewDivisions (@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionNumber", required=false) String divisionNumber,
			@RequestParam(value="isActive", required=false) boolean isActive,
			@RequestParam(value="limit", required=false) Integer limit, HttpSession session) {
		String returnMessage = "";
		if(companyId != null && divisionNumber != null) {
			List<Division> divisions = divisionService.getDivisions(companyId, divisionNumber, isActive, limit);
			JsonConfig jConfig = new JsonConfig();
			JSONArray jsonArray = JSONArray.fromObject(divisions, jConfig);
			returnMessage = jsonArray.toString();
		}
		return returnMessage;
	}

	@RequestMapping (value="/specific",method = RequestMethod.GET)
	public @ResponseBody String getDivision (@RequestParam(value="divisionId", required=false) Integer divisionId,
			HttpSession session) {
		Division division = divisionService.getDivision(divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(division, jConfig);
		return jsonObject.toString();
	}

	@RequestMapping (value="/byName",method = RequestMethod.GET)
	public @ResponseBody String retrieveDivisions(@RequestParam(value="divisionName", required=false) String divisionName,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="isExact", required=false) Boolean isExact,
			@RequestParam(value="limit", required=false) Integer limit,
			HttpSession session) {
		List<Division> divisions = divisionService.retrieveDivisions(divisionName, divisionId, isExact, limit);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/getParentDivisions",method = RequestMethod.GET)
	public @ResponseBody String getParentDivisions (
			@RequestParam (value="numberOrName", required=false) String numberOrName,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="isMainLevelOnly") boolean isMainLevelOnly,
			HttpSession session) {
		List<Division> divisions = divisionService.getDivisions(numberOrName, divisionId, isMainLevelOnly);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/byExactName", method = RequestMethod.GET)
	public @ResponseBody String getDivisionByExactName (@RequestParam (value="divisionName", required=false) String divisionName,
			@RequestParam (value="excludeDivWithAcctCombi", required=false, defaultValue="false") boolean excludeDivWithAcctCombi,
			HttpSession session) {
		Division division = divisionService.getDivisionByName(divisionName, true, excludeDivWithAcctCombi);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(division, jsonConfig);
		return jsonObj.toString();
	}

	@RequestMapping (value="/getChildrenDivision",method = RequestMethod.GET)
	public @ResponseBody String getParentDivisions (
			@RequestParam (value="parentDivisionId", required=false) Integer parentDivisionId,
			HttpSession session) {
		List<DivisionDto> divisions = divisionService.getAllChildren(parentDivisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/byCompany", method=RequestMethod.GET)
	public @ResponseBody String getDivisionsByCompany (@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="isMainLevelOnly") boolean isMainLevelOnly,
			HttpSession session) {
		List<DivisionDto> divisions = divisionService.getDivisionsByCombination(companyId, isMainLevelOnly);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/lastLevelsByCompany", method=RequestMethod.GET)
	public @ResponseBody String getLastDivisionsByCompany (@RequestParam (value="companyId") Integer companyId,
			HttpSession session) {
		List<DivisionDto> divisions = divisionService.getLastDivsByCombi(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/byCombination", method=RequestMethod.GET)
	public @ResponseBody String getDivisionsByCompany (@RequestParam (value="companyId") Integer companyId,
			HttpSession session) {
		List<DivisionDto> divisions = divisionService.getDivsByCombi(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/getChildrenByName",method = RequestMethod.GET)
	public @ResponseBody String getChildrenByName (
			@RequestParam (value="parentDivisionId", required=false) Integer parentDivisionId,
			@RequestParam (value="name", required=false) String name,
			@RequestParam (value="isActive", required=false, defaultValue="true") boolean isActive,
			@RequestParam (value="isExact", required=false, defaultValue="false") boolean isExact,
			HttpSession session) {
		List<DivisionDto> divisions = divisionService.getAllChildrenAndParent(parentDivisionId, name, isActive, isExact);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jConfig);
		return jsonArray.toString();
	}
}
