package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

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
import eulap.eb.service.report.AccountBalancesService;
import net.sf.jasperreports.engine.JRDataSource;
/**
 * Controller for trial balance report.

 *
 */
@Controller
@RequestMapping("/trialBalance")
public class TrialBalanceReportController {
	private final static String REPORT_TITLE = "TRIAL BALANCE";
	private final Logger logger = Logger.getLogger(TrialBalanceReportController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private AccountBalancesService balancesService;
	@Autowired
	private DivisionService divisionService;
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showTrialBalancesReport(Model model,HttpSession session){
		logger.info("Loading trial balance");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		return "TrialBalanceReport.jsp";
	}

	@RequestMapping (value="/generate", method = RequestMethod.GET)
	public String generateTrialBalances(
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam (value="dateFrom") Date dateFrom,
			@RequestParam (value="dateTo") Date dateTo,
			@RequestParam(value="formatType") String formatType,
			@RequestParam(value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session){
		logger.info("Generating " + formatType + " trial balance report.");
		JRDataSource dataSource = balancesService.generateTrialBalDataSource(companyId ,divisionId, dateFrom, dateTo);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		model.addAttribute("reportTitle", REPORT_TITLE);
		if (companyId != null) {
			Company company = companyService.getCompany(companyId);
			if (company != null) {
				model.addAttribute("companyName", company.getName());
				model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
				model.addAttribute("companyAddress", company.getAddress());
			}
		}
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("division", divisionId == -1 ? "All" : divisionService.getDivision(divisionId).getName());
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		logger.info("Successfully generated " + formatType + " trial balance report.");
		return "TrialBalanceReport.jasper";
	}
}
