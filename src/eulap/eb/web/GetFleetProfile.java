package eulap.eb.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.service.FleetProfileService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
/**
 * A controller class that will get the list of fleet profiles.

 */
@Controller
@RequestMapping ("getFleetProfile")
public class GetFleetProfile {
	private static Logger logger = Logger.getLogger(GetFleetProfile.class);
	@Autowired
	private FleetProfileService profileService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getFleetProfiles(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="codeVesselName", required=false) String codeVesselName,
			@RequestParam(value="isExact", required=false) Boolean isExact){
		logger.info("Retrieving the list of Fleets");
		logger.debug("Retrieving list of Fleet Profiles by company id " + companyId);
		List<FleetProfile> fleets = profileService.getFleetProfilesByCompanyId(codeVesselName, companyId, isExact);
		if(fleets.isEmpty()) {
			logger.warn("No Fleet found");
		} else {
			logger.debug("Successfully retrieved "+fleets.size()+" Fleets.");
		}
		if(isExact != null && isExact){
			if(fleets != null && !fleets.isEmpty()){
				JSONObject jsonObject = JSONObject.fromObject(fleets.iterator().next());
				return fleets.iterator().next()  == null ? "No Fleet found" : jsonObject.toString();
			}
			return "No Fleet found";
		} else {
			JsonConfig jConfig = new JsonConfig();
			JSONArray jsonArray = JSONArray.fromObject(fleets, jConfig);
			return jsonArray.toString();
		}
	}

	@RequestMapping (method = RequestMethod.GET, value="byPlateNo")
	public @ResponseBody String getFleetByPlateNo(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="plateNo", required=false) String plateNo,
			@RequestParam(value="isExact", required=false, defaultValue ="false") Boolean isExact){
		logger.info("Retrieving the list of Fleets");
		List<FleetProfile> fleets = profileService.getFleetsByPlateNo(companyId, plateNo, isExact);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(fleets, jConfig);
		return jsonArray.toString();
	}
}
