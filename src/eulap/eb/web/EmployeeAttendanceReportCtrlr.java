package eulap.eb.web;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EmployeeAttendanceReportService;
import eulap.eb.web.dto.EmployeeAttendanceReportDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Controller class for {@link EmployeeAttendanceReportDto}

 *
 */
@Controller
@RequestMapping("employeeAttendanceReport")
public class EmployeeAttendanceReportCtrlr {
	private static Logger logger = Logger.getLogger(EmployeeAttendanceReportCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private EmployeeAttendanceReportService empAttendanceReportService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showReportForm (HttpSession session, Model model) {
		logger.info("Loading employee attendance report form");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivisions(0));
		return "EmployeeAttendanceReport.jsp";
	}

	@RequestMapping(value="/generate", method=RequestMethod.GET)
	public String generateReport(@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId")Integer divisionId,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam(value="isOrderByLastName", required=false, defaultValue="false") Boolean isOrderByLastName,
			Model model, HttpSession session) {

		JRDataSource dataSource = empAttendanceReportService.generateEmpAttendanceReport(companyId,
				divisionId, dateFrom, dateTo, isOrderByLastName);
		Company company = companyService.getCompany(companyId);
		if (company != null) {
			model.addAttribute("companyLogo", company.getLogo());
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
		}
		model.addAttribute("date", DateUtil.formatDate(dateFrom) + "-" + DateUtil.formatDate(dateTo));
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("format", "pdf" );
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, user);
		return "EmployeeAttendanceReport.jasper";
	}
}
