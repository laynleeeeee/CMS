package eulap.eb.web.report;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.service.payroll.PayrollService;
import eulap.eb.web.dto.TimePeriodMonth;

/**
 * Controller class that handles Employer-Employee Contribution Report

 *
 */
@Controller
@RequestMapping("/erEeContributionRprt")
public class ErEeContributionReportCrtlr {
	private static final Logger LOGGER = Logger.getLogger(ErEeContributionReportCrtlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PayrollTimePeriodService payrollTimePeriodService;
	@Autowired
	private PayrollService payrollService;
	@Autowired
	private DivisionService divisionService;

	@RequestMapping(method = RequestMethod.GET)
	public String showForm (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		Collection<Division> divisions = divisionService.getActiveDivisions(0);
		model.addAttribute("divisions", divisions);
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", payrollTimePeriodService.initYears());
		return "ErEeContributionReport.jsp";
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReport (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="month", required=true) Integer month,
			@RequestParam (value="year", required=true) Integer year,
			@RequestParam (value="timePeriodSchedId", required=true) Integer timePeriodSchedId,
			@RequestParam (value="format", required=true) String format,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) {
		LOGGER.info("Generating Employee-Employer Contribution Report.");
		Company company = companyService.getCompany(companyId);
		model.addAttribute("division", divisionId == -1 ? "ALL" : divisionService.getDivision(divisionId).getName());
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		String monthName = month > 0 ? DateUtil.convertToStringMonth(month) : "";
		model.addAttribute("month", monthName);
		model.addAttribute("year", year);
		payrollService.getDateFromTimePeriodSchedule(model, timePeriodSchedId, month, year);
		model.addAttribute("format", format);
		JRDataSource dataSource = payrollService.generateContributionReport(companyId, month, year,
				timePeriodSchedId, divisionId, isFirstNameFirst);
		model.addAttribute("dataSource", dataSource);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, user, isFirstNameFirst != null ? isFirstNameFirst : false);
		LOGGER.info("Sucessfully generated Employee-Employer Contribution Report.");
		return "ErEeContributionReport.jasper";
	}
}
