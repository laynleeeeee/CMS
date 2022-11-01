package eulap.eb.web;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.FleetIncidentService;
import eulap.eb.web.dto.FleetIncidentDto;

@Controller
@RequestMapping("fleetIncident")
public class FleetIncidentController {

	Logger logger =  Logger.getLogger(FleetIncidentController.class);

	@Autowired
	private FleetIncidentService fleetIncidentService;
	private static final String INCIDENT_ATTRIB_NAME = "fleetIncident";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showMain (@RequestParam (value="refObjectId", required = false)Integer refObjectId,
			Model model, HttpSession session) {
		FleetIncidentDto fleetIncident = null;
		if(refObjectId == null) {
			fleetIncident = new FleetIncidentDto();
		} else {
			fleetIncident = fleetIncidentService.getFleetIncidentDto(refObjectId, true);
		}
		logger.info("Showing Fleet Incident Tab");
		return loadForm(fleetIncident, model);
	}

	private String loadForm(FleetIncidentDto fleetIncident, Model model) {
		fleetIncident.serializeFleetIncidents();
		fleetIncident.serializeReferenceDocuments();
		model.addAttribute(INCIDENT_ATTRIB_NAME, fleetIncident);
		return "FleetIncidentForm.jsp";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveFleetIncidents(@ModelAttribute(INCIDENT_ATTRIB_NAME) FleetIncidentDto fleetIncidentDto,
			BindingResult result, Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetIncidentDto.deserializeFleetIncidents();
		fleetIncidentDto.deserializeReferenceDocuments();
		fleetIncidentService.validateFleeIncident(fleetIncidentDto, result);
		if (result.hasErrors()) {
			return	loadForm(fleetIncidentDto, model);
		}
		fleetIncidentService.saveFleetIncident(fleetIncidentDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", fleetIncidentDto.getFpEbObjectId());
		return "FormSuccess.jsp";
	}
}
