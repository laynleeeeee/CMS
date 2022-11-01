package eulap.eb.web;

import java.io.InvalidClassException;
import java.util.List;

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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.DivisionService;
import eulap.eb.validator.DivisionValidator;
import eulap.eb.web.dto.DivisionDto;

/**
 * Controller for Division

 */
@Controller
@RequestMapping("/admin/division")
public class DivisionController {
	private final Logger logger = Logger.getLogger(DivisionController.class);
	private static final String DIVISION_ATTRIBUTE_NAME = "divisions";
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private DivisionValidator divisionValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showDivisions(Model model, HttpSession session) {
		Page<DivisionDto> searchDivisions = divisionService.searchDivisionWithSubs("", "",
				CurrentSessionHandler.getLoggedInUser(session), "All", PageSetting.START_PAGE);
		model.addAttribute(DIVISION_ATTRIBUTE_NAME, searchDivisions);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		logger.info("Show the all the divisions.");
		return "Division.jsp";
	}

	@RequestMapping(method = RequestMethod.GET, params = {"number", "name", "status", "pageNumber"})
	public String searchDivisions(@RequestParam String number, @RequestParam String name,
			@RequestParam(value="status", required=false) String status,
			@RequestParam int pageNumber, Model model, HttpSession session) {
		Page<DivisionDto> searchDivisions = divisionService.searchDivisionWithSubs(number, name,
				CurrentSessionHandler.getLoggedInUser(session), status, pageNumber);
		model.addAttribute(DIVISION_ATTRIBUTE_NAME, searchDivisions);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		logger.info("Search for divisions");
		return "DivisionTable.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String addDivision(Model model) {
		Division division = new Division();
		division.setActive(true);
		logger.info("Add a division.");
		return showDivisionForm(division, model);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET, params = {"divisionId"})
	public String editDivision(@RequestParam Integer divisionId, Model model, HttpSession session) {
		Division getDivision = divisionService.getDivisionById(divisionId, 
				CurrentSessionHandler.getLoggedInUser(session) );
		logger.info("Editing division: " + divisionId);
		return showDivisionForm(getDivision, model);
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String saveDivision(@ModelAttribute("division") Division division, BindingResult result,
			HttpSession session,  Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		divisionValidator.validate(division, result, CurrentSessionHandler.getLoggedInUser(session));
		if (result.hasErrors())
			return showDivisionForm(division, model);
		divisionService.saveDivision(division, user);
		model.addAttribute("message", "Successfully Saved.");
		logger.info("Saved division: " + division.getName());
		return "successfullySaved";
	}

	private String showDivisionForm(Division division, Model model) {
		model.addAttribute(division);
		return "DivisionForm.jsp";
	}

	@RequestMapping(value="/showChildren", method=RequestMethod.GET)
	public String addChildrenAccounts(@RequestParam(value="divisionId") Integer divisionId,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		List<DivisionDto> divisions = divisionService.getAllChildren(divisionId, false);
		model.addAttribute(DIVISION_ATTRIBUTE_NAME, divisions);
		return "AddChildDivision.jsp";
	}
}