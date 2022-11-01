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
import eulap.eb.domain.hibernate.FleetType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.FleetTypeService;

/**
 * Controller for fleet type

 *
 */
@Controller
@RequestMapping ("/admin/fleetType")
public class FleetTypeController {
	@Autowired
	private FleetTypeService fleetTypeService;
	@Autowired
	private CompanyService companyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm (Model model,HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, 0));
		model.addAttribute("categories", fleetTypeService.getAllFleetCategories());
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		searchFleetType(model, null, "All", 1, -1, -1);
		return "FleetType.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		FleetType fleetType = new FleetType();
		Integer companyId = 0;
		if(pId != null){
			fleetType = fleetTypeService.getFleetTypeById(pId);
			fleetType.setCompanyId(fleetTypeService.getCompanyByRefObjectId(fleetType.getEbObjectId()));
			companyId = fleetType.getCompanyId();
		} else {
			fleetType.setActive(true);
		}
		return showFleetTypeForm(fleetType, model, session, companyId);
	}

	private String showFleetTypeForm (FleetType fleetType, Model model, HttpSession session, Integer companyId) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		model.addAttribute("fleetCategories", fleetTypeService.getAllFleetCategories());
		model.addAttribute("fleetType", fleetType);
		return "FleetTypeForm.jsp";
	}

	@RequestMapping (value = "/form", method = RequestMethod.POST)
	public String saveAccountType (@ModelAttribute("fleetType") FleetType fleetType, BindingResult result, Model model,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetTypeService.validateFleetTypes(fleetType, result);
		if (result.hasErrors())
			return  showFleetTypeForm(fleetType, model, session, fleetType.getCompanyId());
		fleetTypeService.saveFleetType(fleetType, user);
		model.addAttribute("message", "Sucessfully saved.");
		return "successfullySaved";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchFleetType(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="fleetCategoryId", required=false) Integer fleetCategoryId,
			@RequestParam(value="pageNumber") Integer pageNumber, HttpSession session, Model model) {
		searchFleetType(model, name, status, pageNumber, companyId, fleetCategoryId);
		return "FleetTypeTable.jsp";
	}

	private void searchFleetType(Model model, String name, String status, Integer pageNumber, Integer companyId, 
			Integer fleetCategoryId){
		Page<FleetType> fleetTypes = 
				fleetTypeService.searchFleetTypes(name, status, pageNumber, companyId, fleetCategoryId);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("fleetTypes", fleetTypes);
	}

}
