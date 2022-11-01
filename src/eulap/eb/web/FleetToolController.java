package eulap.eb.web;

import java.util.Date;

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
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.FleetProfileService;
import eulap.eb.service.FleetToolService;
import eulap.eb.web.dto.FleetToolDto;

/**
 * Controller class for {@link FleetTool}

 *
 */
@Controller
@RequestMapping("fleetTool")
public class FleetToolController {
	@Autowired
	private FleetToolService fleetToolService;
	@Autowired
	private FleetProfileService fleetProfileService;

	private static final String FLEET_TOOL_ATTRIB_NAME = "fleetToolDto";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(@RequestParam (value="refObjectId", required = false)Integer refObjectId,
			@RequestParam (value="asOfDate", required = false) Date asOfDate,
			Model model, HttpSession session) {
		FleetToolDto fleetToolDto = null;
		Integer divisionId =  fleetProfileService.getDivisionIdByFleet(refObjectId);
		if(refObjectId != null) {
			fleetToolDto = fleetToolService.getFleetToolDto(refObjectId, divisionId, 
					PageSetting.START_PAGE, asOfDate);
		} else {
			fleetToolDto = new FleetToolDto();
		}
		model.addAttribute("fpDivisionId", divisionId);
		fleetToolDto.serializeReferenceDocuments();
		model.addAttribute(FLEET_TOOL_ATTRIB_NAME, fleetToolDto);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, PageSetting.START_PAGE);
		return "FleetTool.jsp";
	}

	@RequestMapping(method = RequestMethod.GET, value="/tools")
	public String showItemsConsumed (@RequestParam (value="divisionId", required = false) Integer divisionId, 
			@RequestParam (value="asOfDate", required = false) Date asOfDate,
			@RequestParam (value="pageNumber", required = false) int pageNumber,
			Model model, HttpSession session) {
		FleetToolDto fleetToolDto = fleetToolService.getFleetToolDto(null, divisionId,  pageNumber, asOfDate);
		model.addAttribute(FLEET_TOOL_ATTRIB_NAME, fleetToolDto);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "FleetToolItem.jsp";
	}

	private String loadForm(FleetToolDto fleetToolDto, Model model) {
		fleetToolDto.serializeReferenceDocuments();
		model.addAttribute(FLEET_TOOL_ATTRIB_NAME, fleetToolDto);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, fleetToolDto.getPageNumber());
		return "FleetToolForm.jsp";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save (@ModelAttribute("fleetToolDto") FleetToolDto fleetToolDto,
			BindingResult result, Model model, HttpSession session) {
		fleetToolDto.deserializeReferenceDocuments();
		fleetToolDto.deserializeFleetToolConditions();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetToolService.validate(fleetToolDto, result);
		if (result.hasErrors()) {
			return loadForm(fleetToolDto, model);
		}
		fleetToolService.saveFleetTool(fleetToolDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", fleetToolDto.getFpEbObjectId());
		model.addAttribute("formId", fleetToolDto.getFpEbObjectId());
		return "FormSuccess.jsp";
	}
}
