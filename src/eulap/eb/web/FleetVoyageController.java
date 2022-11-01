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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.FleetVoyage;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.FleetVoyageService;
import eulap.eb.web.dto.FleetVoyageDto;
import net.sf.json.JSONArray;

/**
 * Controller class for {@link FleetVoyage}

 *
 */
@Controller
@RequestMapping("voyage")
public class FleetVoyageController {
	@Autowired
	private FleetVoyageService fleetVoyagesService;

	private static final String VOYAGE_ATTRIB_NAME = "voyage";
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showDryDockingForm(@RequestParam(value="refObjectId", required=false) Integer refObjectId,
			Model model, HttpSession session) {
		FleetVoyageDto fleetVoyagesDto = fleetVoyagesService.getFleetVoyageDto(refObjectId);
		return loadVoyage(fleetVoyagesDto, model);
	}

	private String loadVoyage(FleetVoyageDto fleetVoyagesDto, Model model) {
		fleetVoyagesDto.serializeFleetVoyages();
		fleetVoyagesDto.serializeReferenceDocuments();
		model.addAttribute(VOYAGE_ATTRIB_NAME, fleetVoyagesDto);
		return "FleetVoyageForm.jsp";
	}

	@RequestMapping(value="/getFleetVoyages", method=RequestMethod.GET)
	public @ResponseBody String getFleetDryDockings(@RequestParam(value="refObjectId") Integer refObjectId) {
		List<FleetVoyage> fleetVoyages = fleetVoyagesService.getFleetVoyagesByRefObjectId(refObjectId);
		JSONArray jsonArray = JSONArray.fromObject(fleetVoyages);
		return jsonArray.toString();
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveForm(@ModelAttribute(VOYAGE_ATTRIB_NAME) FleetVoyageDto fleetVoyagesDto,
			BindingResult result, Model model, HttpSession session ) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetVoyagesDto.deserializeFleetVoyages();
		fleetVoyagesDto.deserializeReferenceDocuments();
		fleetVoyagesService.validate(fleetVoyagesDto, result);
		if(result.hasErrors()) {
			return loadVoyage(fleetVoyagesDto, model);
		}
		fleetVoyagesService.saveFleetVoyage(fleetVoyagesDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", fleetVoyagesDto.getFpEbObjectId());
		return "FormSuccess.jsp";
	}
}
