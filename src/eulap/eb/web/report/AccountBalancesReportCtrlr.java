package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.AccountBalancesService;
import net.sf.jasperreports.engine.JRDataSource;


/**
 * Controller for Account Balances report.

 */

@Controller
@RequestMapping("/accountBalancesRpt")
public class AccountBalancesReportCtrlr {
	private final Logger logger = Logger.getLogger(AccountBalancesReportCtrlr.class);
	@Autowired
	private AccountBalancesService acctBalancesService;
	@Autowired
	private CompanyService companyService;
	private final static String REPORT_TITLE = "ACCOUNT BALANCES";
	@Autowired
	private DivisionService divisionService;

	@RequestMapping(method = RequestMethod.GET)
	public String showAccountBalancesReport(Model model,HttpSession session){
		logger.info("Loading account balances");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		return "AccountBalancesReport.jsp";
	}

	@RequestMapping(value="/generateAcctBalancesReport", method = RequestMethod.GET)
	public String generateReportPDFexcel(
			@RequestParam(value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=true) int divisionId,
			@RequestParam (value="strAsOfDate", required=true) Date asOfDate,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session){
		logger.info("Generate report for account balances."+ companyId + divisionId + asOfDate + formatType);
		JRDataSource dataSource = acctBalancesService.getAccntBalancesReport(companyId, divisionId, asOfDate);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("divisions", divisionId != -1? divisionService.getDivision(divisionId).getName():"ALL");
		String azOfDate = DateUtil.formatDate(asOfDate);
		model.addAttribute("asOfDate" , "as of date "+ azOfDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "AccountBalancesReport.jasper";
	}
}