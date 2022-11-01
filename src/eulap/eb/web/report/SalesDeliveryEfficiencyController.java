package eulap.eb.web.report;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.service.report.SalesDeliveryEfficiencyService;
import eulap.eb.web.dto.SDEMainDto;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.jasperreports.engine.JRException;

/**
 * A controller class that handles the Sales Delivery Efficiency report generation. 

 *
 */
@Controller
@RequestMapping ("salesDeliveryEfficiency")
public class SalesDeliveryEfficiencyController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private SalesDeliveryEfficiencyService sdeService;
	@Autowired
	private PayrollTimePeriodService payrollTimePeriodService;

	private final static String REPORT_TITLE = "SALES DELIVERY EFFICIENCY";

	@RequestMapping (method = RequestMethod.GET)
	public String showSalesDeliveryEfficiency (Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", payrollTimePeriodService.initYears());
		LocalDate today = LocalDate.now();
		model.addAttribute("currentMonth", today.getMonthValue());
		model.addAttribute("currentYear", today.getYear());
		return "SalesDeliveryEfficiencyReport.jsp";
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="customerId", required=false) Integer customerId,
			@RequestParam (value="monthFrom", required=false) int monthFrom,
			@RequestParam (value="yearFrom", required=false) int yearFrom,
			@RequestParam (value="monthTo", required=false) int monthTo,
			@RequestParam (value="yearTo", required=false) int yearTo,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) throws JRException, ConfigurationException, ParseException {
		List<SDEMainDto> sdeMainDto = sdeService.getSalesDeliveryEfficiency(companyId, divisionId, customerId, monthFrom, yearFrom, monthTo, yearTo);
		model.addAttribute("datasource", sdeMainDto);
		model.addAttribute("format", formatType);
		model.addAttribute("divisionName", divisionId != -1 ? divisionService.getDivision(divisionId).getName() : "ALL");
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("dateRange" , sdeService.getDateRangeByMonthAndYear(monthFrom, yearFrom, monthTo, yearTo));
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, true);
		return "SalesDeliveryEfficiencyReport.jasper";
	}
}
