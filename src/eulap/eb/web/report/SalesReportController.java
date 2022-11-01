package eulap.eb.web.report;

import java.util.Date;

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
import eulap.eb.service.report.SalesReportService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Controller class that will handle request for sales report generation.

 */

@Controller
@RequestMapping("salesReport")
public class SalesReportController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SalesReportService salesReportService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session) throws ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(null));
		return "SalesReport.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=true) int divisionId,
			@RequestParam (value="salesPersonnelId", required=false) Integer salesPersonnelId,
			@RequestParam (value="currencyId", required=false) Integer currencyId,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		JRDataSource dataSource =salesReportService.generateRetentionCostRprt(companyId, divisionId, 
				salesPersonnelId, dateFrom, dateTo, currencyId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("dateFrom", DateUtil.formatDate(dateFrom));
		model.addAttribute("dateTo", DateUtil.formatDate(dateTo));
		model.addAttribute("division", divisionId != -1 ? divisionService.getDivision(divisionId).getName() : "ALL");
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		return "SalesReport.jasper";
	}
}
