package eulap.eb.web.report;

import java.time.Year;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.SummaryWithholdingTaxesService;
import bp.web.ar.CurrentSessionHandler;
/**
 * Summary of Withholding Taxes controller

 */
@Controller
@RequestMapping("/summaryWithholdingTaxes")
public class SummaryWithholdingTaxesController {
	private static Logger logger = Logger.getLogger(SummaryWithholdingTaxesController.class);
	private final static String REPORT_TITLE = "SUMMARY OF WITHHOLDING TAXES EXPANDED";
	private final static String REPORT_TITLE1 = "SUMMARY OF FINAL WITHHOLDING TAXES";
	private final static String WITHHOLDING_TAXES_EXPANDED = "1,2";
	private final static String FINAL_WITHHOLDING_TAXES = "3,4,5";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private SummaryWithholdingTaxesService summaryWT;
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/monthlySummary", method = RequestMethod.GET)
	public String showMonthlySummaryMainPage (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		logger.info("Loaded the search page for Monthly Summary.");
		model.addAttribute("months", Arrays.asList("January", "February", "March", "April", "May", "June", "July", 
				"August", "September", "October", "November", "December"));
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		model.addAttribute("defaultMonth", DateUtil.getMonth(new Date()));
		return "MonthlySummaryWithholdingTaxes.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="year", required=true) Integer year,
			@RequestParam (value="month", required=true) Integer month,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating pdf version of monthly summary");
		JRDataSource dataSource = summaryWT.generateMonthlySummaryWT(companyId, divisionId,  year, month, WITHHOLDING_TAXES_EXPANDED);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		if (companyId != null && month != null) {
			Company company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , REPORT_TITLE);
			String divisionName = divisionId != -1 ? divisionService.getDivision(divisionId).getName() : "ALL";
			model.addAttribute("divisionName", divisionName);
			String strMonth = "Month of " + DateUtil.getMonthName(month)+ " " + year;
			model.addAttribute("monthName",strMonth);
		} else {
			logger.error("Company and as of date are required");
			throw new RuntimeException("Company and as of date are required");
		}
		logger.info("Sucessfully loaded the summary");
		return "MonthlySummaryWT.jasper";
	}

	@RequestMapping(value="/quarterlySummary", method = RequestMethod.GET)
	public String showQuarterlyMainPage(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		logger.info("Loaded the search page for Quarterly Summary.");
		model.addAttribute("quarters", Arrays.asList("Jan - Mar ", "Apr - Jun ", "Jul - Sept ", "Oct- Dec "));
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		return "QuarterlySummaryWithholdingTaxes.jsp";
	}

	@RequestMapping (value="/generateReport", method = RequestMethod.GET)
	public String generateReport(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="year", required=true) Integer year,
			@RequestParam (value="quarter", required=true) Integer quarter,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating pdf version of customer balances summary");
		JRDataSource dataSource = summaryWT.generateQuarterlySummaryWT(companyId, divisionId, year, quarter);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		if (companyId != null && quarter != null) {
			Company company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , REPORT_TITLE);
			String divisionName = divisionId != -1 ? divisionService.getDivision(divisionId).getName() : "ALL";
			model.addAttribute("divisionName", divisionName);
			String strMonth = "Quarter " + DateUtil.getQuarterName(quarter) + " " + year;
			model.addAttribute("quarterName",strMonth);
		} else {
			logger.error("Company and as of date are required");
			throw new RuntimeException("Company and as of date are required");
		}
		logger.info("Sucessfully loaded the summary");
		return "QuarterlySummaryWT.jasper";
	}

	@RequestMapping(value="/monthlySummaryFinal", method = RequestMethod.GET)
	public String showMonthlySummaryFinalMainPage (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		logger.info("Loaded the search page for Monthly Summary.");
		model.addAttribute("months", Arrays.asList("January", "February", "March", "April", "May", "June", "July", 
				"August", "September", "October", "November", "December"));
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		return "MonthlySummaryFinalWithholdingTaxes.jsp";
	}
	@RequestMapping (value="/generateMonthlyFinal", method = RequestMethod.GET)
	public String generateMonthlyFinal(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="year", required=true) Integer year,
			@RequestParam (value="month", required=true) Integer month,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating pdf version of monthly summary");
		JRDataSource dataSource = summaryWT.generateMonthlySummaryWT(companyId, divisionId, year, month, FINAL_WITHHOLDING_TAXES);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		if (companyId != null && month != null) {
			Company company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , REPORT_TITLE1);
			String divisionName = divisionId != -1 ? divisionService.getDivision(divisionId).getName() : "ALL";
			model.addAttribute("divisionName", divisionName);
			String strMonth = "Month: " + DateUtil.getMonthName(month) + " " + year;
			model.addAttribute("monthName",strMonth);
		} else {
			logger.error("Company and as of date are required");
			throw new RuntimeException("Company and as of date are required");
		}
		logger.info("Sucessfully loaded the summary");
		return "MonthlySummaryFinalWT.jasper";
	}
}
