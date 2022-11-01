package eulap.eb.web;

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
import eulap.eb.domain.hibernate.FleetDriver;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.FleetDriverService;
import eulap.eb.web.dto.FleetDriverDto;

/**
 * Controller class for {@link FleetDriver}

 *
 */
@Controller
@RequestMapping("/fleetDriver")
public class FleetDriverController {
	@Autowired
	private FleetDriverService fleetDriverService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showMain (@RequestParam (value="refObjectId", required = false) Integer refObjectId, 
			Model model, HttpSession session) {
		FleetDriverDto driverDto = refObjectId == null ? new FleetDriverDto() : fleetDriverService.getFleetDriver(refObjectId);
		loadForm(driverDto, model);
		return "FleetDriverForm.jsp";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save (@ModelAttribute("fleetDriverDto") FleetDriverDto fleetDriverDto,
			BindingResult result, Model model, HttpSession session) {
		fleetDriverDto.deserializeReferenceDocuments();
		fleetDriverDto.deserializeFleetDrivers();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetDriverService.validate(fleetDriverDto, result, fleetDriverDto.getFpEbObjectId() == null);
		if (result.hasErrors()) {
			return loadForm(fleetDriverDto, model);
		}
		fleetDriverService.saveFleetDrivers(fleetDriverDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", fleetDriverDto.getFpEbObjectId());
		model.addAttribute("formId", fleetDriverDto.getFpEbObjectId());
		return "FormSuccess.jsp";
	}

	private String loadForm(FleetDriverDto driverDto, Model model) {
		driverDto.serializeFleetDrivers();
		driverDto.serializeReferenceDocuments();
		model.addAttribute("fleetDriverDto", driverDto);
		return "FleetDriverForm.jsp";
	}

}
