package eulap.eb.web.report;

import java.util.Date;

import javax.servlet.http.HttpSession;

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
import eulap.eb.service.report.DailyPettyCashFundReportParam;
import eulap.eb.service.report.DailyPettyCashFundReportService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * A controller class that handles the Daily Petty Cash Fund Report generation.

 */

@Controller
@RequestMapping("/dailyPettyCashFundReport")
public class DailyPettyCashFundReportController{
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private DailyPettyCashFundReportService dailyPettyCashFundReportService;

	private final static String REPORT_TITLE = "DAILY PETTY CASH FUND REPORT";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showDailyPettyCashFundReport( Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("division", divisionService.getActiveDivisions(0));
		model.addAttribute("formStatuses", dailyPettyCashFundReportService.getFormStatuses(user));
		return "DailyPettyCashFundReport.jsp";
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=false) int divisionId,
			@RequestParam (value="custodianId", required=false,defaultValue="-1") int custodianId,
			@RequestParam (value="transactionStatusId", required=true,defaultValue="1") int transactionStatusId,
			@RequestParam (value="date", required=false) Date date,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		DailyPettyCashFundReportParam param = new DailyPettyCashFundReportParam();
		param.setCompanyId(companyId);
		param.setDivisionId(divisionId);
		param.setCustodianId(custodianId);
		param.setTransactionStatusId(transactionStatusId);
		param.setDate(date);
		JRDataSource dataSource = dailyPettyCashFundReportService.generateDailyPettyCashFundReport(param);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle", REPORT_TITLE);
		model.addAttribute("asOfDate", DateUtil.formatDate(date));
		company = null;
		double cashFundCustodian = dailyPettyCashFundReportService.getCashFundCustodian(param);
		model.addAttribute("cashFundCustodian",cashFundCustodian);
		model.addAttribute("totalLiquidation", dailyPettyCashFundReportService.getTotalPettyCashLiquidationPerDay(
				companyId, divisionId, custodianId, date));
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, false);
		return "DailyPettyCashFundReport.jasper";
	}
}
