package eulap.eb.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DailyShiftSchedule;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DailyShiftScheduleService;
import eulap.eb.service.EmployeeShiftService;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.payroll.PayrollService;
import eulap.eb.validator.DailyShiftScheduleValidator;
import eulap.eb.web.dto.DailyShiftScheduleDto;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controller class for Daily Shift Schedule

 *
 */
@Controller
@RequestMapping("/admin/dailyShiftSchedule")
public class DailyShiftScheduleController {
	private static final Logger logger = Logger.getLogger(DailyShiftScheduleController.class);
	@Autowired
	private PayrollTimePeriodService payrollTimePeriodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PayrollService payrollService;
	@Autowired
	private EmployeeShiftService employeeShiftService;
	@Autowired
	private DailyShiftScheduleService dailyShiftScheduleService;
	@Autowired
	private DailyShiftScheduleValidator shiftScheduleValidator;
	@Autowired
	private ReferenceDocumentService documentService;

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
		Collection<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		loadMonthAndYear(model);
		loadDailyShiftScheduleTable(model, -1, -1, -1, user, 1);
		return "DailyShiftSchedule.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		DailyShiftSchedule schedule = new DailyShiftSchedule();
		if (pId != null) {
			schedule = dailyShiftScheduleService.getDaiyShiftSchedule(pId, true);
			Integer ebObjectId = schedule.getEbObjectId();
			if (ebObjectId != null) {
				List<ReferenceDocument> referenceDocuments = documentService.getReferenceDocuments(ebObjectId);
				if (referenceDocuments != null && !referenceDocuments.isEmpty()) {
					ReferenceDocument referenceDocument = referenceDocuments.iterator().next();
					if (referenceDocument != null){
						schedule.setReferenceDocument(referenceDocument);
						referenceDocument = null;
					}
					referenceDocuments = null;
				}
			}
			PayrollTimePeriodSchedule periodSchedule = payrollTimePeriodService.getPayrollTimePeriodSchedule(schedule.getPayrollTimePeriodScheduleId());
			setAndShiftsDates(model, periodSchedule.getDateFrom(), periodSchedule.getDateTo(), schedule.getCompanyId());
		}
		loadSelections(model, user, schedule);
		return loadForm(schedule, model);
	}
	
	private String loadForm(DailyShiftSchedule schedule, Model model) {
		model.addAttribute("dailyShiftSchedule", schedule);
		return "DailyShiftScheduleForm.jsp";
	}

	private void loadMonthAndYear(Model model) {
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", payrollTimePeriodService.initYears());
	}

	private void loadSelections(Model model, User user, DailyShiftSchedule schedule) {
		loadMonthAndYear(model);
		// Set company details
		Integer companyId = schedule.getCompanyId() != null ? schedule.getCompanyId() : 0;
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
	}

	private void loadDailyShiftScheduleTable(Model model, Integer companyId, Integer month,
			Integer year, User user, Integer pageNumber) {
		Page<DailyShiftSchedule> dailyTimeSheetSchedules = dailyShiftScheduleService.searchDailyShiftSchedule(
				companyId, month, year, user, pageNumber, false);
		model.addAttribute("dailyTimeSheetSchedules", dailyTimeSheetSchedules);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping (method = RequestMethod.GET, value="/getTPSchedules")
	public @ResponseBody String getSchedules (@RequestParam(value="month") Integer month,
			@RequestParam(value="year") Integer year,
			@RequestParam(value="payrollTimePeriodScheduleId", required=false) Integer payrollTimePeriodScheduleId) {
		List<PayrollTimePeriodSchedule> schedules = payrollTimePeriodService.getTimePeriodSchedules(month,
				year, payrollTimePeriodScheduleId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(schedules, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(method=RequestMethod.GET, value="/generateEmployeeSheet")
	public String generateEmployeeSheet(@RequestParam(value="payrollTimePeriodScheduleId", required=false) Integer ptpsId,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="isReload", required=false) Boolean isReload,
			Model model, HttpSession session) {
		if (isReload) {
			return "EmployeeDailyShiftSchedule.jsp";
		}
		PayrollTimePeriodSchedule schedule = payrollTimePeriodService.getPayrollTimePeriodSchedule(ptpsId);
		List<Date> dates = setAndShiftsDates(model, schedule.getDateFrom(), schedule.getDateTo(), companyId);
		List<DailyShiftScheduleDto> scheduleSheetDtos = dailyShiftScheduleService.initEmployeeScheduleSheet(companyId, dates, true);
		model.addAttribute("scheduleSheetDtos", scheduleSheetDtos);
		return "EmployeeDailyShiftSchedule.jsp";
	}

	@RequestMapping(method=RequestMethod.POST, value="/generateShiftSched")
	public String generateDailySchedule(@RequestParam(value="payrollTimePeriodScheduleId", required=false) Integer ptpsId,
			@RequestParam(value="companyId", required=false) Integer companyId,
			MultipartHttpServletRequest request, Model model, HttpSession session) {
		MultipartFile mpf = request.getFile("file");
		PayrollTimePeriodSchedule schedule = payrollTimePeriodService.getPayrollTimePeriodSchedule(ptpsId);
		List<Date> dates = setAndShiftsDates(model, schedule.getDateFrom(), schedule.getDateTo(), companyId);
		List<DailyShiftScheduleDto> scheduleSheetDtos = new ArrayList<DailyShiftScheduleDto>();
		try {
			scheduleSheetDtos = dailyShiftScheduleService.convJson2DailyShiftSchedule(mpf, companyId, dates);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		model.addAttribute("scheduleSheetDtos", scheduleSheetDtos);
		return "EmployeeDailyShiftSchedule.jsp";
	}

	@RequestMapping(method=RequestMethod.GET, value="/reloadTable")
	public String reloadEmployeeSheet() {
		return "EmployeeDailyShiftSchedule.jsp";
	}

	private List<Date> setAndShiftsDates (Model model, Date dateFrom, Date dateTo, Integer companyId){
		List<Date> dates = new ArrayList<>();
		List<EmployeeShift> employeeShifts = employeeShiftService.getEmployeeShifts(companyId, null);
		model.addAttribute("employeeShifts", employeeShifts);
		dates = payrollService.getPayrollDates(dateFrom, dateTo);
		model.addAttribute("dates", dates);
		return dates;
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveDailyShiftSchedule(@ModelAttribute("dailyShiftSchedule") DailyShiftSchedule shiftSchedule,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		shiftSchedule.deSerializeEmployeeTimeSchedDTO();
		PayrollTimePeriodSchedule timePeriod = payrollTimePeriodService.getPayrollTimePeriodSchedule(
				shiftSchedule.getPayrollTimePeriodScheduleId());
		shiftSchedule.setPayrollTimePeriodId(timePeriod.getPayrollTimePeriodId());
		shiftScheduleValidator.validate(shiftSchedule, result);
		if (result.hasErrors()) {
			logger.debug("Reloading the Daily shift schedule Form.");
			loadSelections(model, user, shiftSchedule);
			setAndShiftsDates(model, timePeriod.getDateFrom(), timePeriod.getDateTo(),
					shiftSchedule.getCompanyId());
			return loadForm(shiftSchedule, model);
		}
		dailyShiftScheduleService.saveDailyShiftSchedule(user, shiftSchedule);
		logger.info("Successfully saved the Employee.");
		return "successfullySaved";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchEmployee(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="month", required=false) Integer month,
			@RequestParam(value="year") Integer year,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadDailyShiftScheduleTable(model, companyId, month, year, user, pageNumber);
		return "DailyShiftScheduleTable.jsp";
	}

	@RequestMapping(value="/loadSchedules", method = RequestMethod.GET)
	public String showDetail (@RequestParam(value="dailyShiftScheduleId") int dailyShiftScheduleId,
			Model model) {
		model.addAttribute("lines", dailyShiftScheduleService.getScheduleLines(dailyShiftScheduleId, false));
		return "DailyShiftScheduleLineDetail.jsp";
	}

	@RequestMapping(value="/dailyShiftScheduleTemplate.csv", method=RequestMethod.GET)
	public String downloadExcel(@RequestParam(value="payrollTimePeriodScheduleId", required=false)
			Integer payrollTimePeriodScheduleId,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="isReload", required=false) Boolean isReload,
			Model model, HttpSession session) {
		PayrollTimePeriodSchedule schedule = payrollTimePeriodService.getPayrollTimePeriodSchedule(payrollTimePeriodScheduleId);
		List<Date> dates = setAndShiftsDates(model, schedule.getDateFrom(), schedule.getDateTo(), companyId);
		List<DailyShiftScheduleDto> scheduleSheetDtos = dailyShiftScheduleService.initEmployeeScheduleTemplate(companyId, divisionId, dates);
		JRDataSource dataSource = new JRBeanCollectionDataSource(scheduleSheetDtos);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "csv");
		return "DailyShiftScheduleTemplate.jasper";
	}
}