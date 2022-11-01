package eulap.eb.web.report;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.AccountAnalysisService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Account analysis report controller.

 *
 */
@Controller
@RequestMapping ("/accountAnalysisReport")
public class AccountAnalysisController {
	private final Logger logger = Logger.getLogger(AccountAnalysisController.class);
	@Autowired
	private AccountAnalysisService accountAnalysisService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CompanyService companyService;
	private final static String REPORT_TITLE = "ACCOUNT ANALYSIS";

	@RequestMapping (method=RequestMethod.GET)
	public String showExpensesTypeReport (Model model, HttpSession session) {
		logger.info("showing account analysis report");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		return "AccountAnalysisReport.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, value="/loadAccounts")
	public @ResponseBody String loadAccounts (@RequestParam (value ="accountName", required=false) String accountName,
			@RequestParam (value ="accountNumber", required=false) String accountNumber,
			@RequestParam (value ="companyId", required=false) int companyId) {
		Collection<Account> accounts =  accountService.getAccounts(accountName, accountNumber, companyId);
		return getJSONString(accounts, "relatedAccount", "accountType");
	}

	@RequestMapping (method = RequestMethod.GET, value="/loadDivisions")
	public @ResponseBody String loadDivisions (@RequestParam (value="companyId") int companyId,
			@RequestParam (value="accountId") int accountId, 
			@RequestParam (value="selectedDivisionId", required=false) Integer selectedDivisionId,
			HttpSession session) {
		Collection<Division> divisions = divisionService.getDivisions(companyId,
				accountId, selectedDivisionId, true, CurrentSessionHandler.getLoggedInUser(session));
		return getJSONString(divisions);
	}

	private String getJSONString (Object object, String ... exclude) {
		JsonConfig jConfig = new JsonConfig();
		String [] excludes = null;
		if (exclude.length > 0) {
			excludes = new String[exclude.length];
			int i = 0;
			for (String str : exclude) 
				excludes[i++] = str;
		}
		if (excludes != null)
			jConfig.setExcludes(excludes);
		JSONArray jsonArray = JSONArray.fromObject(object, jConfig);
		return jsonArray.toString();
	}
	
	@RequestMapping (value="/generateReport", method = RequestMethod.GET)
	public String generateReportPdfExcel (
			@RequestParam (value="companyId") Integer companyId, 
			@RequestParam (value="accountId") Integer accountId,  
			@RequestParam (value="divisionIdFrom") Integer divisionIdFrom, 
			@RequestParam (value="divisionIdTo") Integer divisionIdTo, 
			@RequestParam (value="dateFrom", required=false) String dateFrom, 
			@RequestParam (value="dateTo", required=false) String dateTo,
			@RequestParam (value="description", required=false) String description,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) {
		JRDataSource dataSource = accountAnalysisService.getAccntAnalysisReport(companyId, accountId, divisionIdFrom, 
				divisionIdTo, dateFrom, dateTo, description);
		String strDate = DateUtil.setUpDate(dateFrom, dateTo);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		Account account = accountService.getAccount(accountId);
		Division division1 = divisionService.getDivision(divisionIdFrom);
		Division division2 = divisionService.getDivision(divisionIdTo);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("fromTodate" , strDate);
		model.addAttribute("account" , account.getAccountName());
		model.addAttribute("division" , division1.getName() + " to " + division2.getName());
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "AccountAnalysis.jasper";
	}
}
