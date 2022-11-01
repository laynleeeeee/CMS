package eulap.eb.web;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EmployeeForRegularizationService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Controller class for Employee for Regularization Report

 *
 */
@Controller
@RequestMapping("employeeForRegularization")
public class EmployeeForRegularizationCtrlr {
	private final static String REPORT_TITLE = "EMPLOYEE FOR REGULARIZATION";
	@Autowired
	private EmployeeForRegularizationService reportService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;

	@RequestMapping (method = RequestMethod.GET)
	public String showReport(Model model, HttpSession session){
		Collection<Company> companies = getStatusCompanies(session);
		model.addAttribute("companies", companies);
		model.addAttribute("divisions",  divisionService.getActiveDivisions(0));
		return "EmployeeForRegularization.jsp";
	}

	private Collection<Company> getStatusCompanies	(HttpSession session) {
		return companyService.getActiveCompanies(
				CurrentSessionHandler.getLoggedInUser(session), null, null, null);
	}

	@RequestMapping (value="/generateReport", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="renewalDate", required=true) Date renewalDate,
			@RequestParam (value="daysBeforeRenewal", required=true) Integer daysBeforeRenewal,
			@RequestParam (value="formatType", required = true) String formatType, 
			@RequestParam (value="isOrderByLastName", required=false, defaultValue="false") Boolean isOrderByLastName,
			Model model, HttpSession session) {
		Date dateHired = DateUtil.deductMonthsToDate(renewalDate, 6);
		Date beforeRenewalDate = DateUtil.deductDaysToDate(dateHired, daysBeforeRenewal);
		JRDataSource dataSource =
				reportService.generateEmployeeForRegtularization(companyId, divisionId, beforeRenewalDate, dateHired, isOrderByLastName);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		Company company = null;
		company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		return "EmployeeForRegularization.jasper";
	}

	@RequestMapping (value="/generateReportAsOfDate", method = RequestMethod.GET)
	public String generatePDFAsOfDate(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="asOfDate", required=true) Date asOfDate,
			@RequestParam (value="formatType", required = true) String formatType, 
			@RequestParam (value="isOrderByLastName", required=false, defaultValue="false") Boolean isOrderByLastName,
			Model model, HttpSession session) {
		Date dateHired = DateUtil.deductMonthsToDate(asOfDate, 6);
		JRDataSource dataSource =
				reportService.generateEmployeeForRegularizationAsOfDate(companyId, divisionId, dateHired, isOrderByLastName);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		Company company = null;
		company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		return "EmployeeForRegularization.jasper";
	}
}
