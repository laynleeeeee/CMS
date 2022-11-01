package eulap.eb.web.report;

import java.util.Date;

import javax.naming.ConfigurationException;
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
import eulap.eb.service.report.IncomeStatementByDivService;
import eulap.eb.web.dto.IncomeStatementNSBDto;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.jasperreports.engine.JRDataSource;

/** 
 * Income Statement by Division Service controller.

 *
 */
@Controller
@RequestMapping ("incomeStatementByDivNSB")
public class IncomeStatementByDivisionReportCtrlr {
	private static Logger logger = Logger.getLogger(IncomeStatementByDivisionReportCtrlr.class);
	public static final String REPORT_TITLE = "CONSOLIDATED STATEMENT OF FINANCIAL PERFORMANCE";
	@Autowired
	private IncomeStatementByDivService incomeStatementByDivService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CompanyService companyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/showReport", method = RequestMethod.GET)
	public String showReport (HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getDivisions(null, null, true, null));
		model.addAttribute("currentMonth", DateUtil.getMonth(new Date())+1);
		model.addAttribute("currentYear", DateUtil.getYear(new Date()));
		loadMonthAndYear(model);
		return "IncomeStatementByDivisionNSB.jsp";
	}

	private void loadMonthAndYear(Model model) {
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", DateUtil.initYears(2015));
	}

	@RequestMapping(value="/generate", method = RequestMethod.GET)
	public String generateReport (@RequestParam(value="formatType") String formatType,
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam(value="hasZeroBalance") Integer hasZeroBalance,
			@RequestParam(value="fromMonth") Integer fromMonth,
			@RequestParam(value="toMonth") Integer toMonth,
			@RequestParam(value="year") Integer year,
			Model model, HttpSession session) throws ConfigurationException, CloneNotSupportedException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Generating income statement by division report.");
		Date dateFrom = DateUtil.createDate(year, fromMonth-1, 1);
		Date dateTo = DateUtil.getEndDayOfMonth(DateUtil.createDate(year, toMonth, 0));
		JRDataSource dataSource = incomeStatementByDivService.getNSBIncomeStatementDatasource(companyId, divisionId, dateFrom, dateTo, hasZeroBalance);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		model.addAttribute("dateTo", dateTo);
		model.addAttribute("dateRange", DateUtil.formatToFullDate(dateFrom) + " - " + DateUtil.formatToFullDate(dateTo));
		model.addAttribute("currYear", year);
		model.addAttribute("prevYear", --year);
		model.addAttribute("percentToSalesId", IncomeStatementNSBDto.PERCENT_TO_SALES_ID);
		model.addAttribute("reportTitle", REPORT_TITLE);
		Company company = companyService.getCompany(companyId);
		if (company != null) {
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("companyTin", company.getTin());
		}
		String division = null;
		if(divisionId != null && divisionId != -1) {
			division = divisionService.getDivision(divisionId).getName();
		}
		model.addAttribute("division", division);
		ReportUtil.getPrintDetails(model, user);
		return "IncomeStatementByDivisionNSBA4.jasper";
	}
}
