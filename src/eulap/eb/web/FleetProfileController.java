package eulap.eb.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.FleetProfileService;
import eulap.eb.service.FleetTypeService;
import eulap.eb.service.UserGroupAccessRightService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Fleet Profile Controller.

 *
 */
@Controller
@RequestMapping("/fleetProfile")
public class FleetProfileController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FleetTypeService fleetTypeService;
	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private UserGroupAccessRightService ugarService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showMain (Model model, HttpSession session) {
		return "FleetProfile.jsp";
	}

	@RequestMapping(value="/{fleetProfileId}", method=RequestMethod.GET)
	public String getFleetProfile(@PathVariable(value="fleetProfileId") int fleetProfileId,
			Model model, HttpSession session) throws IOException {
		model.addAttribute("fleetProfileId", fleetProfileId);
		FleetProfile fleetProfile = fleetProfileService.getFleetProfile(fleetProfileId);
		if (fleetProfile != null) {
			model.addAttribute("fpRefObjectId", fleetProfile.getEbObjectId());
			model.addAttribute("ftRefObjectId", fleetProfile.getRefObjectId());
		}
		model.addAttribute("fleetProfile", fleetProfile);
		return "FleetProfile.jsp";
	}

	@RequestMapping(value="/form", method = RequestMethod.GET)
	public String showNewForm (Model model, HttpSession session) {
		FleetProfile fleetProfile = new FleetProfile();
		return loadForm(fleetProfile, model, session);
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String addFleetProfile(@ModelAttribute("fleetProfile") FleetProfile fleetProfile,
			BindingResult result, Model model, HttpSession session) throws IOException {
		return saveFleetProfile(fleetProfile, result, model, session);
	}

	@RequestMapping(value="/{fleetProfileId}/form", method = RequestMethod.GET)
	public String showEditPatientProfileForm (@PathVariable(value="fleetProfileId") int fleetProfileId, 
			Model model, HttpSession session) {
		FleetProfile fleetProfile = fleetProfileService.getFleetProfile(fleetProfileId);
		return loadForm(fleetProfile, model, session);
	}

	@RequestMapping(value="/{fleetProfileId}/form", method=RequestMethod.POST)
	public String editFleetProfile(@ModelAttribute("fleetProfile") FleetProfile fleetProfile,
			BindingResult result, Model model, HttpSession session) throws IOException {
		return saveFleetProfile(fleetProfile, result, model, session);
	}

	private String loadForm(FleetProfile fleetProfile, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(fleetProfile.getId() == 0) {
			model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
			model.addAttribute("fleetTypes", fleetTypeService.getAllActiveFTypes());
		}  else {
			model.addAttribute("company", companyService.getCompany(fleetProfile.getCompanyId()));
			model.addAttribute("fleetType", fleetTypeService.getFleetTypeById(fleetProfile.getFleetTypeId()));
		}
		model.addAttribute("fleetProfile", fleetProfile);
		return "FleetProfileForm.jsp";
	}

	@RequestMapping(value="/searchFleet", method = RequestMethod.GET)
	public @ResponseBody String searchFleet (@RequestParam (value="codeVesselName") String codeVesselName,  
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FleetProfile> fleetProfiles = fleetProfileService.getFleetProfiles(codeVesselName, user);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(fleetProfiles, jConfig);
		return jsonArray.toString();
	}

	private String saveFleetProfile(@ModelAttribute("fleetProfile") FleetProfile fleetProfile,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetProfileService.validate(fleetProfile, result, fleetProfile.getId() == 0);
		if (result.hasErrors()){
			return loadForm(fleetProfile, model, session);
		}
		fleetProfileService.saveFleetProfile(user, fleetProfile);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", fleetProfile.getEbObjectId());
		model.addAttribute("formId", fleetProfile.getId());
		return "FormSuccess.jsp";
	}

	@RequestMapping(value="/hasAccessRight", method=RequestMethod.GET)
	public @ResponseBody String hasAccessRight(@RequestParam(value="userGroupId") int userGroupId) {
		boolean hasAccessRight = ugarService.hasAccessRight(userGroupId, FleetProfile.PRODUCT_KEY);
		return hasAccessRight ? "true" : "false";
	}
}
