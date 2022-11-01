package eulap.eb.web;

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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.FleetManningRequirement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.FleetManningRequirementService;
import eulap.eb.web.dto.FleetManningRequirementDto;

/**
 * Controller for fleet manning requirements.

 *
 */
@Controller
@RequestMapping ("/manningRequirements")
public class FleetManningRequirementCtrlr {
	@Autowired
	private FleetManningRequirementService manningRequirementService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showFleetManningRequirement (@RequestParam(value="refObjectId", required=false) Integer refObjectId,
			Model model, HttpSession session) {
		model.addAttribute("departments", FleetManningRequirement.DEPARTMENTS);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		model.addAttribute("refObjectId", refObjectId);
		searchManningRequirement(model, null, null, null, null, null, "All", 1, refObjectId);
		return "FleetManningRequiremet.jsp";
	}

	@RequestMapping (method = RequestMethod.POST)
	public String saveManningRequirements (@ModelAttribute("manningRequirementDto") FleetManningRequirementDto manningRequirementDto,
			BindingResult result, Model model,
			HttpSession session) {
		manningRequirementDto.deserializeReferenceDocuments();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		manningRequirementService.validate(manningRequirementDto, result);
		if(result.hasErrors()){
			return loadManningRequirement(manningRequirementDto, model);
		}
		manningRequirementService.saveFleetManningRequirement(manningRequirementDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", manningRequirementDto.getFpEbObjectId());
		model.addAttribute("formId", manningRequirementDto.getFpEbObjectId());
		return "FormSuccess.jsp";
	}

	private void searchManningRequirement(Model model, String position, String license, String number, String remarks, String department,
			String status, Integer pageNumber, Integer refObjectId){
		Page<FleetManningRequirement> manningRequirements = 
				manningRequirementService.searchManningRequirements(position, license, number, remarks, department, status, pageNumber, refObjectId);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("manningRequirements", manningRequirements);
	}

	private String loadManningRequirement(FleetManningRequirementDto manningRequirementDto, Model model){
		manningRequirementDto.serializeReferenceDocuments();
		model.addAttribute("manningRequirementDto", manningRequirementDto);
		model.addAttribute("departments", FleetManningRequirement.DEPARTMENTS);
		return "FleetManningRequiremetForm.jsp";
	}

	@RequestMapping(value="form",method = RequestMethod.GET)
	public String showForm (@RequestParam(value="refObjectId", required=false) Integer refObjectId,
			@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		FleetManningRequirementDto manningRequirementDto = new FleetManningRequirementDto();
		FleetManningRequirement manningRequirement = new FleetManningRequirement();
		manningRequirementDto.setFpEbObjectId(refObjectId);
		if(pId != null){
			manningRequirement = manningRequirementService.getManningRequirementsById(pId, refObjectId);
		} else {
			manningRequirement.setActive(true);
		}
		manningRequirementDto.setManningRequirement(manningRequirement);
		if(manningRequirement.getEbObjectId() != null){
			manningRequirementDto.setReferenceDocuments(manningRequirementService.getReferenceDocumentByRefObjectId(manningRequirement.getEbObjectId()));
		}
		return loadManningRequirement(manningRequirementDto, model);
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchManningRequirements(@RequestParam(value="license", required=false) String license,
			@RequestParam(value="position", required=false) String position,
			@RequestParam(value="number", required=false) String number,
			@RequestParam(value="remarks", required=false) String remarks,
			@RequestParam(value="department", required=false) String department,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="refObjectId", required=false) Integer refObjectId,
			@RequestParam(value="pageNumber") Integer pageNumber, HttpSession session, Model model) {
		searchManningRequirement(model, position, license, number, remarks, department, status, pageNumber, refObjectId);
		return "FleetManningRequiremetTable.jsp";
	}
}
