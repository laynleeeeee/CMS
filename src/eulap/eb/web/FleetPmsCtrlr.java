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
import eulap.eb.domain.hibernate.FleetPms;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.FleetPmsService;
import eulap.eb.service.FleetTypeService;
import eulap.eb.web.dto.FleetPmsDto;
import net.sf.json.JSONArray;

/**
 * Controller for fleet PMS.

 *
 */
@Controller
@RequestMapping ("/fleetPms")
public class FleetPmsCtrlr {
	@Autowired
	private FleetTypeService fleetTypeService;
	@Autowired
	private FleetPmsService fleetPmsService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showNewForm (@RequestParam(value="refObjectId", required=false) Integer refObjectId,
			Model model, HttpSession session) {
		FleetPmsDto fleetPmsDto = new FleetPmsDto();
		if(refObjectId != null){
			fleetPmsDto = fleetPmsService.getFleetPmsDto(refObjectId);
			fleetPmsDto.setFpEbObjectId(refObjectId);
			fleetPmsDto.setFleetTypeId(fleetTypeService.getFleetTypeByReferenceObjectId(refObjectId).getFleetCategoryId());
		}
		return loadPMSForm(fleetPmsDto, model);
	}

	private String loadPMSForm(FleetPmsDto fleetPmsDto, Model model){
		fleetPmsDto.serializeFleetPms();
		fleetPmsDto.serializeReferenceDocuments();
		model.addAttribute("fleetPmsDto", fleetPmsDto);
		return "FleetPmsForm.jsp";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String savePmsForm(@ModelAttribute("fleetPmsDto") FleetPmsDto fleetPmsDto,
			BindingResult result, Model model, HttpSession session ) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetPmsDto.deserializeFleetPms();
		fleetPmsDto.deserializeReferenceDocuments();
		fleetPmsService.validateFleetPms(fleetPmsDto, result);
		if(result.hasErrors()) {
			return loadPMSForm(fleetPmsDto, model);
		}
		fleetPmsService.saveFleetPms(fleetPmsDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", fleetPmsDto.getFpEbObjectId());
		return "FormSuccess.jsp";
	}

	@RequestMapping(value="/getFleetPms", method=RequestMethod.GET)
	public @ResponseBody String getFleetPms(@RequestParam(value="refObjectId") Integer refObjectId) {
		List<FleetPms> fleetPms = fleetPmsService.getFleetPms(refObjectId);
		JSONArray jsonArray = JSONArray.fromObject(fleetPms);
		return jsonArray.toString();
	}
}
