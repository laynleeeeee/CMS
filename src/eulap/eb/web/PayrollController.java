package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.dao.TimeSheetDao;
import eulap.eb.domain.hibernate.BiometricModel;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EmployeeType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.TimeSheet;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.EmployeeService;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.service.TimeSheetService;
import eulap.eb.service.payroll.PayrollService;
import eulap.eb.validator.PayrollValidator;
import eulap.eb.web.dto.EmployeeDeductionDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.PayrollDto;
import eulap.eb.web.dto.TimePeriodMonth;
import eulap.eb.web.dto.TimesheetFormDto;
import eulap.eb.web.dto.webservice.EmployeeSalaryDTO;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller for Payroll form.

 */
@Controller
@RequestMapping(value = "payroll")
public class PayrollController{
	private static final Logger logger =  Logger.getLogger(PayrollController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private PayrollValidator payrollValidator;
	@Autowired
	private PayrollTimePeriodService payrollTimePeriodService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private PayrollService payrollService;
	@Autowired
	private TimeSheetDao timeSheetDao;
	@Autowired
	private TimeSheetService timeSheetService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String showPaymentForm(@RequestParam (value="pId", required = false)Integer pId,
			@RequestParam (value="isEdit", defaultValue="true", required=false) boolean isEdit,
			Model model, HttpSession session) {
		PayrollDto payrollDto = new PayrollDto();
		Payroll payroll = new Payroll();
		if (pId != null) {
			payroll = payrollService.getPayroll(pId, true);
			model.addAttribute("salaryDtos", payrollService.convertEmployeeSalaryToDto(payroll.getId()));
			List<EmployeeDeductionDto> employeeDeductionDtos = payrollService.getEmployeeDeductions(pId);
			loadDeductions(employeeDeductionDtos, model);
		} else {
			payroll.setEmployeeTypeId(EmployeeType.TYPE_REGULAR);
			Date currentDate = new Date();
			payroll.setDate(currentDate);
		}
		payrollDto.setPayroll(payroll);
		return loadForm(payrollDto, session, model);
	}

	public String loadForm(PayrollDto payrollDto, HttpSession session, Model model) {
		Payroll payroll = payrollDto.getPayroll();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<EmployeeType> employeeTypes = employeeService.getAllActiveEmployeeTypes();
		model.addAttribute("employeeTypes", employeeTypes);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		Collection<Division> divisions = divisionService.getActiveDivisions(
				payroll.getDivisionId() != null ? payroll.getDivisionId() : 0);
		model.addAttribute("divisions", divisions);
		List<BiometricModel> biometricModels = payrollService.getBiometricModels();
		model.addAttribute("biometricModels", biometricModels);
		payroll.serializePayrollEmployeeSalary();
		model.addAttribute("payrollDto", payrollDto);
		loadMonthAndYear(model);

		if (payroll.getId() == 0) {
			setDefaultPeriod(model);
		}
		return "PayrollHTMLForm.jsp";
	}

	private void setDefaultPeriod (Model model) {
		Date currentDate = DateUtil.removeTimeFromDate(new Date());
		model.addAttribute("month", DateUtil.getMonth(currentDate) + 1);
		model.addAttribute("year", DateUtil.getYear(currentDate));
	}

	@RequestMapping(value = "/reloadTimesheet", method = RequestMethod.GET)
	public String reloadTimeSheet() {
		return "PayrollEmployeeTimeSheet.jsp";
	}

	@RequestMapping(value = "/reloadSalary", method = RequestMethod.GET)
	public String reloadSalary() {
		return "PayrollEmployeeSalary.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String submit (@ModelAttribute ("payrollDto") PayrollDto payrollDto, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		payrollDto.derializeEmployeeDeductions();
		Payroll payroll = payrollDto.getPayroll();
		payroll.deSerializeEmployeeSalaryDTO();
		payroll.deSerializeTimeSheetDto();
		payroll.setEmployeeDeductions(payrollDto.getEmployeeDeductions());
		synchronized (this) {
			payrollValidator.validate(payroll, result);
			if (result.hasErrors()) {
				return loadForm(payrollDto, session, model);
			}
			ebFormServiceHandler.saveForm(payroll, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", payroll.getSequenceNumber());
		model.addAttribute("payrollDate", payroll.getDate());
		model.addAttribute("formId", payroll.getId());
		model.addAttribute("ebObjectId", payroll.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping (method=RequestMethod.GET, value="/parseSalary")
	public String parseAndProcessSalary(
			@RequestParam (value="timeSheetId", required=false) Integer timeSheetId,
			@RequestParam (value="payrollId", required=false) Integer payrollId,
			@RequestParam (value="payrollTimePeriodId", required=false) Integer payrollTimePeriodId,
			@RequestParam (value="payrollTimePeriodScheduleId", required=false) Integer payrollTimePeriodScheduleId,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="totalDeductions", required=false) String totalDeductions,
			Model model, HttpSession session) throws IOException, ParseException {
		List<EmployeeSalaryDTO> salaryDtos =  payrollService.computeEmployeesSalary(payrollId, payrollTimePeriodId, 
				payrollTimePeriodScheduleId, timeSheetId, companyId, totalDeductions, false);
		model.addAttribute("salaryDtos", salaryDtos);
		return "PayrollEmployeeSalary.jsp";
	}

	@RequestMapping (method=RequestMethod.POST, value="/computePayroll")
	public String computePayrolls(@ModelAttribute ("payroll") Payroll payroll,
			HttpSession session,
			Model model){
		return "PayrollEmployeeSalary.jsp";
	}

	@RequestMapping(value = "/viewForm", method = RequestMethod.GET)
	public String showView (@RequestParam (value="pId", required = false) Integer pId, Model model) {
		PayrollDto payrollDto = payrollService.getPayrollForView(pId);
		model.addAttribute("payrollDto", payrollDto);
		//Set timesheet table
		TimesheetFormDto timesheetFormDto = timeSheetService.getTimeSheetView(payrollDto.getPayroll().getTimeSheetId());
		model.addAttribute("timesheetFormDto", timesheetFormDto);
		//Set deduction table
		model.addAttribute("employeeDeductionDtos", payrollService.getEmployeeDeductions(pId));
		logger.info("Loading the view form.");
		return "PayrollView.jsp";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String searchPayrolls(@RequestParam(required=true, value="criteria", defaultValue="") String criteria,
			HttpSession session,
			Model model){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = payrollService.searchPayrolls(criteria, user);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/getTPSchedules")
	public @ResponseBody String getSchedules (@RequestParam(value="month") Integer month,
			@RequestParam(value="year") Integer year) {
		List<PayrollTimePeriodSchedule> schedules = payrollTimePeriodService.getTimePeriodSchedules(month, year);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(schedules, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/getDates")
	public @ResponseBody String getDates (@RequestParam(
				value="payrollTimePeriodScheduleId") Integer payrollTimePeriodScheduleId) {
		PayrollTimePeriodSchedule schedule = payrollTimePeriodService.getPayrollTimePeriodSchedule(payrollTimePeriodScheduleId);
		String dates = DateUtil.formatDate(schedule.getDateFrom()) + "-" +  DateUtil.formatDate(schedule.getDateTo());
		return dates;
	}

	private void loadMonthAndYear(Model model) {
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", payrollTimePeriodService.initYears());
	}

	@RequestMapping(value="loadTimesheet",method=RequestMethod.GET)
	public @ResponseBody String setPayroll (@RequestParam (value="timeSheetId", required=true) Integer timeSheetId,
			Model model, HttpSession session) {
		logger.info("setting cash sale return.");
		TimeSheet timeSheet = timeSheetDao.get(timeSheetId);
		Payroll payroll = payrollService.convertTSDtoPayroll(timeSheet);
		String [] exclude = {"ebObject","referenceDocument","fileData", "payrollEmployeeSalaries", "employeeTimeSheets", 
				 "employeeType", "biometricModel"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(payroll, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value="generateDeduction",method=RequestMethod.GET)
	public String generateDeduction (@RequestParam (value="timeSheetId", required=true) Integer timeSheetId,
			Model model, HttpSession session) {
		List<EmployeeDeductionDto> employeeDeductionDtos = payrollService.initEmployeeDeductions(timeSheetId);
		loadDeductions(employeeDeductionDtos, model);
		return "PayrollEmployeeDeduction.jsp";
	}

	public void loadDeductions(List<EmployeeDeductionDto> employeeDeductionDtos, Model model) {
		model.addAttribute("employeeDeductionDtos", employeeDeductionDtos);

		List<DeductionType> deductionTypes = payrollService.getAllActive();
		model.addAttribute("deductionTypes", deductionTypes);
	}

	@RequestMapping (value="/payslip/pdf", method=RequestMethod.GET)
	public String generatePayslip (@RequestParam (value="payrollId", required=true) int payrollId,
			Model model, HttpSession session) {
		getPayslipParams(payrollId, model, "pdf", session);
		return "PayslipPrintOut.jasper";
	}

	@RequestMapping (value="/payrollReport/pdf/{isExcludeFields}", method=RequestMethod.GET)
	public String generatePayroll (@PathVariable(value="isExcludeFields") boolean isExcludeFields,
			@RequestParam (value="pId", required=true) int payrollId,
			@RequestParam (value="format", required=false) String format,
			Model model, HttpSession session) {
		getPayrollParams(payrollId, model, session, "pdf", isExcludeFields, false);
		return "PayrollPrintOut.jasper";
	}

	/**
	 * Set parameters for payroll printout.
	 * @param payrollId The payroll id.
	 * @param model The model.
	 * @param session The session.
	 * @param format The printout format.
	 * @param isExcludeFields True if specified fields are excluded, otherwise false.
	 * @param hasCustomSalaryRec True if there is a custom salary record, otherwise false.
	 */
	public void getPayrollParams(Integer payrollId, Model model, HttpSession session, String format, boolean isExcludeFields, boolean hasCustomSalaryRec) {
		List<Payroll> payrolls = new ArrayList<>();
		Payroll payroll = payrollService.getPayrollWithDetails(payrollId);
		payrolls.add(payroll);
		JRDataSource dataSource = new JRBeanCollectionDataSource(payrolls);
		model.addAttribute("datasource", dataSource);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("format", format);
		Company company = companyService.getCompany(payroll.getCompanyId());
		model.addAttribute("companyLogo", format.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		Division division = divisionService.getDivision(payroll.getDivisionId());
		model.addAttribute("divisionName", division.getName());
		model.addAttribute("dateFrom", payroll.getDateFrom());
		model.addAttribute("dateTo", payroll.getDateTo());
		if(!hasCustomSalaryRec) {
			model.addAttribute("payrollEmplSalaryRecordDto",  payrollService.getPayrollEmplRecords(payrollId, isExcludeFields));
		}
		ReportUtil.getPrintDetails(model, loggedUser);
		FormWorkflow formWorkflow = payroll.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy",
						user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("createdPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.NOTED_ID) {
				model.addAttribute("notedBy", user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("notedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.VERIFIED_ID){
				model.addAttribute("verifiedBy", user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("verifiedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy", user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("approvedPosition", position.getName());
			}
		}
	}

	@RequestMapping (value="/payrollReport/lx", method=RequestMethod.GET)
	public String generateHTMLPayroll (@RequestParam (value="pId", required=true) int payrollId,
			@RequestParam (value="format", required=false) String format,
			Model model, HttpSession session) {
		getPayrollParams(payrollId, model, session, "pdf", false, false);
		return "PayrollPrintOutLX.jasper";
	}

	public void getPayslipParams(Integer payrollId, Model model, String format, HttpSession session) {
		Payroll payroll = payrollService.getPayroll(payrollId, true);
		JRDataSource dataSource = new JRBeanCollectionDataSource(payrollService.getPayrollEmplSalaryRecordDto(payrollId));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format);
		Company company = payroll.getCompany();
		if (format == "pdf") {
			model.addAttribute("companyLogo", company.getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ company.getLogo());
		}
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		PayrollTimePeriodSchedule schedule = payroll.getPayrollTimePeriodSchedule();
		model.addAttribute("period", payrollService.formatTimePeriodSched(schedule.getDateFrom(), schedule.getDateTo()));
	}

	@RequestMapping (value="/payslip/html", method=RequestMethod.GET)
	public String generateHTMLPayslip (@RequestParam (value="payrollId", required=true) int payrollId,
			Model model, HttpSession session) {
		getPayslipParams(payrollId, model, "html", session);
		return "PayslipPrintOutHTML.jasper";
	}
}