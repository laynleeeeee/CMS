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
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.MonthlyShiftSchedule;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.MonthlyShiftScheduleService;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.web.dto.MonthlyShiftScheduleDto;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controller class for Monthly Shift Schedule

 *
 */
@Controller
@RequestMapping("/admin/monthlyShiftSchedule")
public class MonthlyShiftScheduleController {
	private static final Logger logger = Logger.getLogger(MonthlyShiftScheduleController.class);
	@Autowired
	private PayrollTimePeriodService payrollTimePeriodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private MonthlyShiftScheduleService monthlyShiftScheduleService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	/**
	 * Load the daily shift schedule main page.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showMainForm(Model model, HttpSession session){
		logger.info("Loading daily shift schedule main page");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		Collection<Company> companies = companyService.getCompanies(user);
		model.addAttribute("status", searchStatus);
		model.addAttribute("companies", companies);
		loadMonthAndYear(model);
		loadMonthlyShiftScheduleTable(model, -1, -1, -1, "All", user, 1);
		return "MonthlyShiftSchedule.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		MonthlyShiftSchedule schedule = new MonthlyShiftSchedule();
		if(pId != null){
			schedule = monthlyShiftScheduleService.getMonthlySchedule(pId);
		} else {
			schedule.setActive(true);
		}
		loadSelections(model, user, schedule);
		return loadForm(schedule, model);
	}

	private String loadForm(MonthlyShiftSchedule schedule, Model model) {
		model.addAttribute("monthlyShiftSchedule", schedule);
		return "MonthlyShiftScheduleForm.jsp";
	}

	private void loadMonthAndYear(Model model) {
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", payrollTimePeriodService.initYears());
	}

	private void loadSelections(Model model, User user, MonthlyShiftSchedule schedule) {
		loadMonthAndYear(model);
		Integer companyId = schedule.getCompanyId() != null ? schedule.getCompanyId() : 0;

		//Company
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, companyId);
		model.addAttribute("companies", companies);
	}

	private void loadMonthlyShiftScheduleTable(Model model, Integer companyId, Integer month, Integer year, String status,
			User user, Integer pageNumber) {
		Page<MonthlyShiftSchedule> monthlyShiftSchedules = 
				monthlyShiftScheduleService.getMonthlyShiftScheduleLines(companyId, month, year, status, user, pageNumber);
		model.addAttribute("monthlyShiftSchedules", monthlyShiftSchedules);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping (method = RequestMethod.GET, value="/getTPSchedules")
	public @ResponseBody String getSchedules (@RequestParam(value="month") Integer month,
			@RequestParam(value="year") Integer year) {
		List<PayrollTimePeriodSchedule> schedules = payrollTimePeriodService.getTimePeriodSchedules(month, year);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(schedules, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(method=RequestMethod.GET, value="/generateEmployeeSheet")
	public String generateEmployeeSheet(@RequestParam(value="payrollTimePeriodScheduleId", required=false)
			Integer payrollTimePeriodScheduleId,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="isReload", required=false) Boolean isReload,
			Model model,
			HttpSession session) {
		if(isReload){
			return "EmployeeShiftSchedule.jsp";
		}
		List<MonthlyShiftScheduleDto> monthlyShiftScheduleDtos = monthlyShiftScheduleService.initEmployeeSchedSheet(companyId);
		model.addAttribute("monthlyShiftScheduleDtos", monthlyShiftScheduleDtos);
		return "EmployeeShiftSchedule.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveMonthlyShiftSched(@ModelAttribute("monthlyShiftSchedule") MonthlyShiftSchedule shiftSchedule,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		shiftSchedule.deSerializeMonthlySchedDTO();
		if(shiftSchedule.getPayrollTimePeriodScheduleId() != null) {
			PayrollTimePeriodSchedule timePeriod = payrollTimePeriodService.getPayrollTimePeriodSchedule(shiftSchedule.getPayrollTimePeriodScheduleId());
			shiftSchedule.setPayrollTimePeriodId(timePeriod.getPayrollTimePeriodId());
		}
		monthlyShiftScheduleService.validate(shiftSchedule, result);
		if(result.hasErrors()) {
			logger.debug("Reloading the Daily shift schedule Form.");
			loadSelections(model, user, shiftSchedule);
			if(!shiftSchedule.getMonthlyShiftScheduleDtos().isEmpty()) {
				List<MonthlyShiftScheduleDto> monthlyShiftScheduleDtos = monthlyShiftScheduleService.initEmployeeSchedSheet(shiftSchedule.getCompanyId());
				shiftSchedule.setMonthlyShiftScheduleDtos(monthlyShiftScheduleDtos);
			}
			return loadForm(shiftSchedule, model);
		}
		monthlyShiftScheduleService.saveMonthlyShiftSched(user, shiftSchedule);
		logger.info("Successfully saved the Employee.");
		return "successfullySaved";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchEmployee(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="month", required=false) Integer month,
			@RequestParam(value="year") Integer year,
			@RequestParam(value="status", required = false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadMonthlyShiftScheduleTable(model, companyId, month, year, status, user, pageNumber);
		return "MonthlyShiftScheduleTable.jsp";
	}
}
