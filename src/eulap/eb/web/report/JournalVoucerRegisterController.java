package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.UserService;
import eulap.eb.service.report.JournalVoucherRegisterService;
import eulap.eb.web.dto.DivisionDto;
import eulap.eb.web.dto.JournalVoucherRegisterDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * A controller class that handles the journal voucher register report.
 * 

 */
@Controller
@RequestMapping("journalVoucherRegister")
public class JournalVoucerRegisterController {
	private static Logger logger = Logger.getLogger(JournalVoucerRegisterController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private UserService userService;
	@Autowired
	private JournalVoucherRegisterService jvrService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	private void loadSelections(User user, Model model) throws ConfigurationException {
		// Get Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		// Get User
		List<User> users = userService.getAllUsers();
		model.addAttribute("users", users);
		// Workflow Statuses
		model.addAttribute("formStatuses", jvrService.getFormStatuses(user));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showJournalVoucherRegister(Model model, HttpSession session) throws ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user, model);
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		logger.info("Successfully loaded the search parameters to generate the Journal Voucher Register.");
		return "JournalVoucherRegisterReport.jsp";
	}

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public String generateReport(@RequestParam(value = "companyId", required = false) Integer companyId,
			@RequestParam(value = "divisionId", required = false) Integer divisionId,
			@RequestParam(value = "fromGLDate", required = false) String strFromGlDate,
			@RequestParam(value = "toGLDate", required = false) String strToGlDate,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "createdBy", required = false) Integer createdBy,
			@RequestParam(value = "updatedBy", required = false) Integer updatedBy,
			@RequestParam(value = "formatType") String formatType,
			@RequestParam(value = "isFirstNameFirst", required = false) Boolean isFirstNameFirst, HttpSession session,
			Model model) {
		logger.info("Generating the Journal Voucher Register Report.");
		JRDataSource datasource = jvrService.generateJVRegisterDatasource(companyId, divisionId, strFromGlDate,
				strToGlDate, status, createdBy, updatedBy);
		model.addAttribute("datasource", datasource);
		model.addAttribute("format", formatType);

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());

		if (divisionId != -1) {
			Division division = divisionService.getDivision(divisionId);
			model.addAttribute("divisionName", division.getName());
		}

		model.addAttribute("reportTitle", "JOURNAL VOUCHER REGISTER");
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "JournalVoucherRegisterCancellationRemarks.jasper";
	}

	@RequestMapping(value = "/generateDataTable", method = RequestMethod.GET)
	public @ResponseBody String generateDataTable(
			@RequestParam(value = "companyId", required = false) Integer companyId,
			@RequestParam(value = "fromGLDate", required = false) String strFromGLDate,
			@RequestParam(value = "toGLDate", required = false) String strToGLDate,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "createdBy", required = false) Integer createdBy,
			@RequestParam(value = "updatedBy", required = false) Integer updatedBy,
			@RequestParam(value = "sSearch", required = false) String sSearch,
			@RequestParam(value = "sEcho", required = false) int sEcho,
			@RequestParam(value = "iDisplayLength", required = false) int iDisplayLength,
			@RequestParam(value = "iDisplayStart", required = false) int iDisplayStart,
			@RequestParam(value = "iSortingCols", required = false) int iSortingCols, HttpSession session) {
		// TODO: Deprecated, changed to Jasper Report.
		int pageNumber = (iDisplayStart / iDisplayLength) + 1;
		PageSetting pageSetting = new PageSetting(pageNumber, iDisplayLength);
		Page<JournalVoucherRegisterDto> journalVoucherRegister = jvrService.generateReport(companyId,
				DivisionDto.DIV_ALL, strFromGLDate, strToGLDate, status, createdBy, updatedBy, pageSetting);
		journalVoucherRegister.setsEcho(sEcho);
		return journalVoucherRegister.toJSONObject();
	}
}
