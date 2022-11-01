package eulap.eb.web;

import java.util.Collection;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EmployeeShiftService;
import eulap.eb.validator.EmployeeShiftValidator;
import eulap.eb.web.dto.DayOfTheWeek;

/**
 * Controller class that will handle request for {@link EmployeeShift}

 *
 */

@Controller
@RequestMapping("/admin/employeeShift")
public class EmployeeShiftController {
	private final Logger logger = Logger.getLogger(EmployeeShiftController.class);
	@Autowired
	private EmployeeShiftService employeeShiftService;
	@Autowired
	private EmployeeShiftValidator employeeShiftValidator;
	@Autowired
	private CompanyService companyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	/**
	 * Load the main page of CSC Employee Shift Form.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(Model model, HttpSession session){
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		loadEmployeeShifts(null, null, null, null, null, null, "All", 1, user, model);
		return "EmployeeShift.jsp";
	}

	/**
	 * Load the employee shift form for CSC.
	 */
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		EmployeeShift employeeShift = new EmployeeShift();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(pId != null) {
			logger.info("Loading employee shift with id: "+pId);
			employeeShift = employeeShiftService.getEmployeeShift(pId);
			employeeShift.setDayOffDto(employeeShiftService.getDayOffsByEmplyeeShiftId(pId));
		} else {
			employeeShift.setActive(true);
		}
		return loadSelections(model, user, employeeShift);
	}

	protected String loadSelections(Model model, User user, EmployeeShift employeeShift ) {
		Integer companyId = employeeShift.getCompanyId() != null ? employeeShift.getCompanyId() : 0;
		model.addAttribute("employeeShift", employeeShift);
		//Company
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, companyId);
		model.addAttribute("companies", companies);
		model.addAttribute("days", DayOfTheWeek.getDaysOfTheWeek());
		return "EmployeeShiftForm.jsp";
	}

	/**
	 * Validating and Saving the form.
	 */
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveEmployeeShift(@ModelAttribute("employeeShift") EmployeeShift employeeShift,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		employeeShiftValidator.validate(employeeShift, result);
		if(result.hasErrors()) {
			logger.debug("Reloading the employee shift Form");
			return loadSelections(model, user, employeeShift);
		}
		employeeShiftService.saveEmployeeShift(user, employeeShift);
		logger.info("Successfully saved the Employee Shift.");
		return "successfullySaved";
	}

	/**
	 * Get the paged list of CSC's employee shift objects.
	 * @param name The name of the shift.
	 * @param firstHalfShiftStart The first half start of the shift.
	 * @param firstHalfShiftEnd The first half end of the shift.
	 * @param secondHalfShiftStart The second half start of the shift.
	 * @param secondHalfShiftEnd The second half end of the shift.
	 * @param allowableBreak The allowable break time of the employee.
	 * @param lateMuliplier The multiplier to deduct the salary of the employee.
	 * @param weekendMultiplier The multiplier for additional pay for weekends.
	 * @param holidayMultiplier The multiplier for additional pay for holidays.
	 * @param status ALL, ACTIVE, or INACTIVE.
	 * @param user The user currently log.
	 * @param pageNumber The selected page number.
	 * @return The list of employee shifts of CSC.
	 */
	private void loadEmployeeShifts(String name, String firstHalfShiftStart, String firstHalfShiftEnd, String secondHalfShiftStart, 
			String secondHalfShiftEnd, Double dailyWorkingHours, String status, int pageNumber, User user, Model model){
		Page<EmployeeShift> cscEmployeeShifts = employeeShiftService.getCscEmployeeShifts(null, name, 
				firstHalfShiftStart, firstHalfShiftEnd, 
				secondHalfShiftStart, secondHalfShiftEnd, dailyWorkingHours, status, pageNumber, user);
		for (EmployeeShift employeeShift : cscEmployeeShifts.getData()) {
			employeeShift.setDayOffDto(employeeShiftService.getDayOffsByEmplyeeShiftId(employeeShift.getId()));
		}
		model.addAttribute("cscEmployeeShifts", cscEmployeeShifts);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	/**
	 * Get the paged list of employee shift objects based on the specified filter parameters.
	 * @param name The name of the shift.
	 * @param firstHalfShiftStart The start of the first half of shift.
	 * @param firstHalfShiftEnd The end of the first half of shift.
	 * @param secondHalfShiftStart The start of the second half of shift.
	 * @param secondHalfShiftEnd The end of the second half of shift.
	 * @param dailyWorkingHours The number of work hours per day.
	 * @param status ALL, ACTIVE, or INACTIVE.
	 * @param pageNumber The selected page number.
	 * @return The list of employee shifts.
	 */
	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchEmployeeShifts(
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="firstHalfShiftStart", required=false) String firstHalfShiftStart,
			@RequestParam(value="firstHalfShiftEnd", required=false) String firstHalfShiftEnd,
			@RequestParam(value="secondHalfShiftStart", required=false) String secondHalfShiftStart,
			@RequestParam(value="secondHalfShiftEnd", required=false) String secondHalfShiftEnd,
			@RequestParam(value="dailyWorkingHours", required=false) Double dailyWorkingHours,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadEmployeeShifts(name, firstHalfShiftStart, firstHalfShiftEnd, secondHalfShiftStart, secondHalfShiftEnd, dailyWorkingHours, status, pageNumber, user, model);
		return "EmployeeShiftTable.jsp";
	}
}
