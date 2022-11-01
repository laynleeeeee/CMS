package eulap.eb.web;

import java.io.IOException;

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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.FleetCaptainMdm;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.FleetCaptainMdmService;
import eulap.eb.web.dto.FleetCaptainMdmDto;

/**
 * Controller class for {@link FleetCaptainMdm}

 *
 */
@Controller
@RequestMapping("captainMdm")
public class FleetCaptainMdmController {

	@Autowired
	private FleetCaptainMdmService fleetCaptainMdmService;

	private static final String CAPTAIN_MDM_ATTRIB_NAME = "fleetCaptainMdm";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showMain (@RequestParam (value="refObjectId", required = false)Integer refObjectId,
			Model model, HttpSession session) {
		FleetCaptainMdmDto fleetCaptainMdmDto = null;
		if(refObjectId == null) {
			fleetCaptainMdmDto = new FleetCaptainMdmDto();
		} else {
			fleetCaptainMdmDto = fleetCaptainMdmService.getFleetCaptainMdmDto(refObjectId, true);
		}
		return loadForm(fleetCaptainMdmDto, model);
	}

	private String loadForm(FleetCaptainMdmDto fleetCaptainMdmDto, Model model) {
		fleetCaptainMdmDto.serializeFleetCaptains();
		fleetCaptainMdmDto.serializeReferenceDocuments();
		model.addAttribute(CAPTAIN_MDM_ATTRIB_NAME, fleetCaptainMdmDto);
		return "FleetCaptainMdmForm.jsp";
	}

	@RequestMapping(method = RequestMethod.POST)
	private String saveFleetCaptainMdm(@ModelAttribute(CAPTAIN_MDM_ATTRIB_NAME) FleetCaptainMdmDto fleetCaptainMdmDto,
			BindingResult result, Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetCaptainMdmDto.deserializeFleetCaptains();
		fleetCaptainMdmDto.deserializeReferenceDocuments();
		fleetCaptainMdmService.validateFleetCaptainMdms(fleetCaptainMdmDto, result);
		if(result.hasErrors()) {
			return loadForm(fleetCaptainMdmDto, model);
		}
		fleetCaptainMdmService.saveFleetCaptainMdm(fleetCaptainMdmDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", fleetCaptainMdmDto.getFpEbObjectId());
		model.addAttribute("formId", fleetCaptainMdmDto.getFpEbObjectId());
		return "FormSuccess.jsp";
	}
}
