package eulap.eb.web.report;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.CustomerBalancesSummaryServiceImpl;
import eulap.eb.web.dto.CustomerBalancesSummaryDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
/**
 * Customer balances summary report controller

 */
@Controller
@RequestMapping("/customerBalancesSummary")
public class CustomerBalancesSummaryController {
	private static Logger logger = Logger.getLogger(CustomerBalancesSummaryDto.class);
	private final static String REPORT_TITLE = "CUSTOMER BALANCES SUMMARY";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CustomerBalancesSummaryServiceImpl serviceImpl;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;

	@RequestMapping(method = RequestMethod.GET)
	public String showCustomerBalancesMainPage (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(0));
		return "CustomerBalancesSummaryReport.jsp";
	}

	@RequestMapping(value="/getArAccounts", method = RequestMethod.GET)
	public @ResponseBody String getArAccounts(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			Model model, HttpSession session) {
		logger.info("Retrieving the list of AR Accounts for Company ID : "+companyId);
		Collection<String> arAccounts = serviceImpl.getArAccounts(companyId, divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arAccounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="balanceOption", required=true) Integer balanceOption,
			@RequestParam (value="classificationId", required=false) String classification,
			@RequestParam (value="asOfDate", required=true) Date asOfDate,
			@RequestParam (value="currencyId", required=false) Integer currencyId,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating pdf version of customer balances summary");
		JRDataSource dataSource = serviceImpl.generateReport(companyId, divisionId, balanceOption,
				asOfDate,currencyId, classification);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		String strDate = "";
		if (companyId != null && asOfDate != null) {
			Company company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , REPORT_TITLE);
			strDate = "As of date " + DateUtil.formatDate(asOfDate);
			model.addAttribute("asOfDate", strDate);
			String divisionName = divisionId != -1 ? divisionService.getDivision(divisionId).getName() : null;
			model.addAttribute("divisionName", divisionName);
			Currency currency = currencyService.getCurency(currencyId);
			model.addAttribute("currencyName",currency.getName());
		} else {
			logger.error("Company and as of date are required");
			throw new RuntimeException("Company and as of date are required");
		}
		logger.info("Sucessfully loaded the customer balances summary");
		return "CustomerBalancesSummary.jasper";
	}

	@RequestMapping(value="/withDateRange", method = RequestMethod.GET)
	public String showMainPage(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		logger.info("Loading customer balances summary main page.");
		return "CustomerBalancesSummaryDateRange.jsp";
	}

	@RequestMapping (value="/generateReport", method = RequestMethod.GET)
	public String generateReport(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="balanceOption", required=true) Integer balanceOption,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating customer balances summary");
		JRDataSource dataSource = serviceImpl.generateCustomerBalSummryRprt(companyId,
				balanceOption, dateFrom, dateTo);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		String dateRange = DateUtil.formatDate(dateFrom) +" To "+ DateUtil.formatDate(dateTo);
		model.addAttribute("dateRange", dateRange);
		logger.info("Sucessfully loaded the customer balances summary report");
		return "CustomerBalancesSummaryDateRange.jasper";
	}
}
