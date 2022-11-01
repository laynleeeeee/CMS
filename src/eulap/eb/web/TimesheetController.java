package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
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
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EmployeeDtr;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.TimeSheet;
import eulap.eb.domain.hibernate.User;
import eulap.eb.exception.SuppressableStacktraceException;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.service.TimeSheetService;
import eulap.eb.service.payroll.PayrollService;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.TimePeriodMonth;
import eulap.eb.web.dto.TimesheetFormDto;
import eulap.eb.web.dto.webservice.TimeSheetDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;

/**
 * Controller for {@link TimeSheet}

 *
 */
@Controller
@RequestMapping("/timesheet")
public class TimesheetController {
	private static final Logger logger =  Logger.getLogger(TimesheetController.class);
	@Autowired
	private TimeSheetService timeSheetService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private PayrollTimePeriodService payrollTimePeriodService;
	@Autowired
	private PayrollService payrollService;

	@InitBinder
	public void init(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		TimesheetFormDto timesheetFormDto = new TimesheetFormDto();
		TimeSheet timeSheet = new TimeSheet();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isNew = pId == null;
		if(isNew) {
			timeSheet.setDate(new Date());
			setDefaultPeriod(model);
		} else {
			timesheetFormDto = timeSheetService.geTimeSheetDetails(pId);
			timeSheet = timesheetFormDto.getTimeSheet();
			setDate(timeSheet.getDateFrom(), timeSheet.getDateTo(), model);
			model.addAttribute("employeeDtrsJson",
					timeSheetService.getSerializedEmployeeDtrs(timesheetFormDto.getEmployeeDtrs()));
			model.addAttribute("timeSheetDtos", timeSheet.getTimeSheetDtos());
			ReferenceDocument referenceDocument = timesheetFormDto.getReferenceDocument();
			if(referenceDocument != null) {
				model.addAttribute("fileName", referenceDocument.getFileName());
				model.addAttribute("file", referenceDocument.getFile());
				model.addAttribute("fileSize", referenceDocument.getFileSize());
				model.addAttribute("description", referenceDocument.getDescription());
			}
		}
		timesheetFormDto.setTimeSheet(timeSheet);
		return loadForm(timesheetFormDto, user, model);
	}

	private void setDefaultPeriod (Model model) {
		Date currentDate = DateUtil.removeTimeFromDate(new Date());
		model.addAttribute("month", DateUtil.getMonth(currentDate) + 1);
		model.addAttribute("year", DateUtil.getYear(currentDate));
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveForm(@ModelAttribute ("timesheetFormDto") TimesheetFormDto timesheetFormDto,
			BindingResult result, Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		TimeSheet timeSheet = timesheetFormDto.getTimeSheet();
		timeSheet.deSerializeTimeSheetDto();
		List<EmployeeDtr> employeeDtrs = timeSheetService.getDeserializedDtrs(timesheetFormDto.getEmployeeDtrsJson());
		timesheetFormDto.setEmployeeDtrs(employeeDtrs);
		synchronized (this) {
			timeSheetService.validateTimeSheet(timeSheet, result);
			if(result.hasErrors()) {
				model.addAttribute("selectTPSched", timeSheet.getSelectTPSched());
				model.addAttribute("dateFrom", timeSheet.getDateFrom());
				model.addAttribute("dateTo", timeSheet.getDateTo());
				model.addAttribute("timeSheetDtos", timeSheet.getTimeSheetDtos());
				setDate(timeSheet.getDateFrom(), timeSheet.getDateTo(), model); 
				return loadForm(timesheetFormDto, user, model);
			}
			timeSheetService.saveTimesheetForm(timesheetFormDto, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", timeSheet.getSequenceNumber());
		model.addAttribute("formId", timeSheet.getId());
		model.addAttribute("ebObjectId", timeSheet.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping (method=RequestMethod.POST, value="/parseTimeSheet")
	public String parseAndProcessTimeSheet(@RequestParam (value="tId", required=false) Integer tId,
			@RequestParam (value="date", required=false) Date date,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="biometricModelId", required=false) Integer biometricModelId,
			@RequestParam (value="payrollTimePeriodId", required=false) Integer payrollTimePeriodId,
			@RequestParam (value="payrollTimePeriodScheduleId", required=false) Integer payrollTimePeriodScheduleId,
			@RequestParam (value="month", required=false) Integer month,
			@RequestParam (value="year", required=false) Integer year,
			@RequestParam (value="selectTPSched", required=false) String selectTPSched,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="typeId", required=false) Integer typeId,
			MultipartHttpServletRequest request, HttpServletResponse response, Model model) throws IOException,
			ParseException, SuppressableStacktraceException {
		MultipartFile mpf = request.getFile("file");
		Payroll payroll = new Payroll();
		logger.debug("FILE NAME: " +  mpf.getOriginalFilename());
		setDate(dateFrom, dateTo, model);
		model.addAttribute("file", timeSheetService.encodeFileToBase64Binary(mpf));
		model.addAttribute("fileName", mpf.getOriginalFilename());
		model.addAttribute("fileSize", Double.parseDouble(mpf.getSize() + ""));
		model.addAttribute("payrollTimePeriodId", payrollTimePeriodId);
		model.addAttribute("payrollTimePeriodScheduleId", payrollTimePeriodScheduleId);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
		model.addAttribute("selectTPSched", selectTPSched);
		model.addAttribute("divisionId", divisionId);
		if(dateFrom != null && dateTo != null) {
			List<TimeSheetDto> timeSheetDtos = timeSheetService.parseAndProcessDTRO(tId,
					companyId, divisionId, typeId, biometricModelId, payrollTimePeriodId,
					payrollTimePeriodScheduleId, dateFrom, dateTo, mpf);
			if (!timeSheetDtos.isEmpty()) {
				String employeeDtrsJson = timeSheetService.getSerializedDtrs(timeSheetDtos);
				model.addAttribute("employeeDtrsJson", employeeDtrsJson);
			}
			payroll.setTimeSheetDtos(timeSheetDtos);
			payroll.serializeTimeSheetDto();
			model.addAttribute("timeSheetDtos", timeSheetDtos);
		}
		return "PayrollEmployeeTimeSheet.jsp";
	}

	private String loadForm(TimesheetFormDto timesheetFormDto, User user, Model model) {
		TimeSheet timeSheet = timesheetFormDto.getTimeSheet();
		if(timeSheet.getId() != 0) {
			timeSheet.setFormWorkflow(timeSheetService.getFormWorkflow(timeSheet.getId()));
		}
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("biometricModels", payrollService.getBiometricModels());
		Collection<Division> divisions = divisionService.getActiveDivisions(
				timeSheet.getDivisionId() != null ? timeSheet.getDivisionId() : 0);
		model.addAttribute("divisions", divisions);
		model.addAttribute("timesheetFormDto", timesheetFormDto);
		loadMonthAndYear(model);
		timeSheet.serializeTimeSheetDto();
		return "TimeSheetForm.jsp";
	}

	private void loadMonthAndYear(Model model) {
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", payrollTimePeriodService.initYears());
	}

	private void setDate(Date dateFrom, Date dateTo, Model model){
		List<Date> dates = new ArrayList<>();
		if(dateFrom != null && dateTo != null){
			dates = payrollService.getPayrollDates(dateFrom, dateTo);
		}
		model.addAttribute("dates", dates);
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String showView (@RequestParam (value="pId", required = false)Integer pId,  Model model) {
		return loadTSView(pId, true, model);
	}

	@RequestMapping(value = "/tableView", method = RequestMethod.GET)
	public String showTableView (@RequestParam (value="timeSheetId")Integer timeSheetId,  Model model) {
		return loadTSView(timeSheetId, false, model);
	}

	private String loadTSView(Integer timeSheetId, boolean isWholeView, Model model) {
		TimesheetFormDto timesheetFormDto = timeSheetService.getTimeSheetView(timeSheetId);
		model.addAttribute("timesheetFormDto", timesheetFormDto);
		return isWholeView ? "TimeSheetView.jsp" : "TimeSheetTableView.jsp";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public @ResponseBody String searchForm(
			@RequestParam(required = true, value="criteria", defaultValue = "") String criteria,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> results = timeSheetService.searchTimeSheets(criteria, user);
		JSONArray jsonArray = JSONArray.fromObject(results);
		return jsonArray.toString();
	}

	@RequestMapping (value="/print", method=RequestMethod.GET)
	public String generatePayroll (@RequestParam (value="pId", required=true) int pId,
			Model model, HttpSession session) {
		List<TimeSheet> timeSheets = new ArrayList<>();
		TimeSheet timeSheet = timeSheetService.getTimeSheetWithDetails(pId);
		timeSheets.add(timeSheet);
		JRDataSource dataSource = new JRBeanCollectionDataSource(timeSheets);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		Company company = companyService.getCompany(timeSheet.getCompanyId());
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		Division division = divisionService.getDivision(timeSheet.getDivisionId());
		model.addAttribute("divisionName", division.getName());
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		TimesheetFormDto timesheetFormDto = timeSheetService.getTimeSheetView(pId);
		if(timesheetFormDto != null) {
			if (timesheetFormDto.getReferenceDocument() != null) {
				model.addAttribute("timeRecord", timesheetFormDto.getReferenceDocument().getFileName());
			}
			if (timesheetFormDto.getBiometricModel() != null) {
				model.addAttribute("biometricModel", timesheetFormDto.getBiometricModel().getModelName());
			}
		}
		FormWorkflow formWorkflow = timeSheet.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy",
						user.getLastName() + " " + user.getFirstName());
				model.addAttribute("createdPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.NOTED_ID) {
				model.addAttribute("notedBy", user.getLastName() + " " + user.getFirstName());
				model.addAttribute("notedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.VERIFIED_ID){
				model.addAttribute("verifiedBy", user.getLastName() + " " + user.getFirstName());
				model.addAttribute("verifiedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy", user.getLastName() + " " + user.getFirstName());
				model.addAttribute("approvedPosition", position.getName());
			}
		}
		ReportUtil.getPrintDetails(model, loggedUser);
		return "TimeSheetPrintout.jasper";
	}

	@RequestMapping (value="/reference", method=RequestMethod.GET)
	public String getTimeSheetReference (
		Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		Collection<Division> divisions = divisionService.getActiveDivisions(0);
		model.addAttribute("divisions", divisions);
		loadMonthAndYear(model);
		setDefaultPeriod(model);
		Date currentDate = new Date();
		loadReferences(model, null, null, DateUtil.getMonth(currentDate) + 1, DateUtil.getYear(currentDate), 
				null, user, PageSetting.START_PAGE);
		return "TSReference.jsp";
	}

	@RequestMapping (value="/reference/detail", method=RequestMethod.GET)
	public String getTimeSheetRefTable (@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="month") Integer month,
			@RequestParam (value="year") Integer year,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		loadReferences(model, companyId, divisionId, month, year, null, CurrentSessionHandler.getLoggedInUser(session), 
				pageNumber);
		return "TSReferenceTable.jsp";
	}

	private void loadReferences(Model model, Integer companyId, Integer divisionId, Integer month, Integer year, 
			Integer payrollTimePeriodScheduleId, User user, int pageNumber) {
		Page<TimeSheet> timeSheet = timeSheetService.getTimeSheets(companyId, divisionId, 
				month, year, payrollTimePeriodScheduleId, user, pageNumber);
		model.addAttribute("timeSheet", timeSheet);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/timeSheetTemplate.csv", method=RequestMethod.GET)
	public String downloadExcel(@RequestParam (value="payrollTimePeriodId", required=false) Integer payrollTimePeriodId,
			@RequestParam (value="payrollTimePeriodScheduleId", required=false) Integer payrollTimePeriodScheduleId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="companyId", required=false) Integer companyId, Model model)  {
		JRDataSource dataSource = timeSheetService.downTimesheetTemplate(payrollTimePeriodId,
				payrollTimePeriodScheduleId, divisionId, companyId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "csv");
		return "TimeSheetTemplate.jasper";
	}
}
