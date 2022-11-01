package eulap.eb.web.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.service.report.SalesOutputService;
import eulap.eb.web.dto.SalesOutputMainDto;
import eulap.eb.web.dto.TimePeriodMonth;

/**
 * Controller class that will handle request for sales output report generation.

 */

@Controller
@RequestMapping("salesOutput")
public class SalesOutputController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SalesOutputService salesOutputService;
	@Autowired
	private PayrollTimePeriodService payrollTimePeriodService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session) throws ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Date date = new Date();
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(null));
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", payrollTimePeriodService.initYears());
		model.addAttribute("currentMonth", DateUtil.getMonth(date));
		model.addAttribute("currentYear", DateUtil.getYear(date));
		return "SalesOutput.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=true) int divisionId,
			@RequestParam (value="customerId", required=false) Integer customerId,
			@RequestParam (value="customerAcctId", required=false) Integer customerAcctId,
			@RequestParam (value="salesPersonnelId", required=false) Integer salesPersonnelId,
			@RequestParam (value="poNumber", required=false) String poNumber,
			@RequestParam (value="soDateFrom", required=false) Date soDateFrom,
			@RequestParam (value="soDateTo", required=false) Date soDateTo,
			@RequestParam (value="drDateFrom", required=false) Date drDateFrom,
			@RequestParam (value="drDateTo", required=false) Date drDateTo,
			@RequestParam (value="ariDateFrom", required=false) Date ariDateFrom,
			@RequestParam (value="ariDateTo", required=false) Date ariDateTo,
			@RequestParam (value="monthFrom", required=false) Integer monthFrom,
			@RequestParam (value="yearFrom", required=false) Integer yearFrom,
			@RequestParam (value="monthTo", required=false) Integer monthTo,
			@RequestParam (value="yearTo", required=false) Integer yearTo,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		List<SalesOutputMainDto> datasource = new ArrayList<SalesOutputMainDto>();
		SalesOutputMainDto reportData = salesOutputService.processSalesOutputRprtData(companyId, divisionId, 
				customerId, customerAcctId, salesPersonnelId, poNumber, soDateFrom, soDateTo, 
				drDateFrom, drDateTo, ariDateFrom, ariDateTo, monthFrom, yearFrom, monthTo, yearTo);
		datasource.add(reportData);
		model.addAttribute("datasource", datasource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("division", divisionId != -1 ? divisionService.getDivision(divisionId).getName() : "ALL");
		model.addAttribute("dateRange", salesOutputService.formatReportRangeParam(monthFrom, yearFrom, monthTo, yearTo, 
				soDateFrom, soDateTo, drDateFrom, drDateTo, ariDateFrom, ariDateTo));
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		return "SalesOutputMainPdf.jasper";
	}
}
