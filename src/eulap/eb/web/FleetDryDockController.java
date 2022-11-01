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
import eulap.eb.domain.hibernate.FleetDryDock;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.FleetDryDockService;
import eulap.eb.web.dto.FleetDryDockDto;
import net.sf.json.JSONArray;

/**
 * Controller class for {@link FleetDryDock}

 *
 */
@Controller
@RequestMapping("dryDock")
public class FleetDryDockController {

	@Autowired
	private FleetDryDockService dryDockingService;

	private static final String DRY_DOCKING_ATTRIB_NAME = "dryDock";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showDryDockingForm(@RequestParam(value="refObjectId", required=false) Integer refObjectId,
			Model model, HttpSession session) {
		FleetDryDockDto dryDockingDto = dryDockingService.getDryDocking(refObjectId);
		return loadDryDocking(dryDockingDto, model);
	}

	private String loadDryDocking(FleetDryDockDto dryDockingDto, Model model) {
		dryDockingDto.serializeFleetDryDocking();
		dryDockingDto.serializeReferenceDocuments();
		model.addAttribute(DRY_DOCKING_ATTRIB_NAME, dryDockingDto);
		return "FleetDryDockForm.jsp";
	}

	@RequestMapping(value="/getFleetDryDocking", method=RequestMethod.GET)
	public @ResponseBody String getFleetDryDockings(@RequestParam(value="refObjectId") Integer refObjectId) {
		List<FleetDryDock> fleetDryDockings = dryDockingService.getFleetDryDockings(refObjectId);
		JSONArray jsonArray = JSONArray.fromObject(fleetDryDockings);
		return jsonArray.toString();
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveForm(@ModelAttribute(DRY_DOCKING_ATTRIB_NAME) FleetDryDockDto fleetDryDockingDto,
			BindingResult result, Model model, HttpSession session ) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetDryDockingDto.deserializeFleetDryDocking();
		fleetDryDockingDto.deserializeReferenceDocuments();
		dryDockingService.validate(fleetDryDockingDto, result);
		if(result.hasErrors()) {
			return loadDryDocking(fleetDryDockingDto, model);
		}
		dryDockingService.saveDryDocking(fleetDryDockingDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", fleetDryDockingDto.getFpEbObjectId());
		return "FormSuccess.jsp";
	}
}
