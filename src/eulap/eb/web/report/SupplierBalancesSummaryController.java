package eulap.eb.web.report;

import java.util.Collection;
import java.util.Date;

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
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.SupplierBalancesSummaryServiceImpl;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
/**
 * Supplier balances summary report controller

 */
@Controller
@RequestMapping("/supplierBalancesSummary")
public class SupplierBalancesSummaryController {
	private static Logger logger = Logger.getLogger(SupplierBalancesSummaryController.class);
	private final static String REPORT_TITLE = "SUPPLIER BALANCES SUMMARY";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SupplierBalancesSummaryServiceImpl serviceImpl;

	@RequestMapping(method = RequestMethod.GET)
	public String showSupplierBalancesMainPage (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(0));
		logger.info("Loading supplier balances summary main page.");
		return "SupplierBalancesSummaryReport.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="balanceOption", required=true) Integer balanceOption,
			@RequestParam (value="asOfDate", required=true) Date asOfDate,
			@RequestParam (value="currencyId") int currencyId,
			@RequestParam (value="classificationId", required=false) String classification,
			@RequestParam (value="formatType", required=true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Generating pdf version of supplier balances summary");
		logger.debug("Retreiving supplier balances summary from the database");
		JRDataSource dataSource = serviceImpl.getSupplierBalancesData(companyId, divisionId, asOfDate,
				currencyId, balanceOption, classification);
		return generateReportPrintout(companyId,divisionId, asOfDate, currencyId, formatType, isFirstNameFirst,
				user, dataSource, model, REPORT_TITLE, "SupplierBalancesSummary.jasper");
	}

	/**
	 * Generates the Supplier Balances Report printout.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param asOfDate The as of date.
	 * @param currencyId The currency id.
	 * @param formatType The printout format.
	 * @param isFirstNameFirst True if print detail user name format starts with the first name, otherwise false.
	 * @param user The user currently logged in.
	 * @param dataSource The datasource object.
	 * @param model The model object.
	 * @param reportTitle The report title.
	 * @param reportMapping The report mapping.
	 */
	public String generateReportPrintout(Integer companyId, Integer divisionId, Date asOfDate, Integer currencyId, String formatType, Boolean isFirstNameFirst,
			User user, JRDataSource dataSource, Model model, String reportTitle, String reportMapping) {
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		ReportUtil.getPrintDetails(model, user, isFirstNameFirst != null ? isFirstNameFirst : false);
		Company company = null;
		if (companyId != null && asOfDate != null) {
			company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , reportTitle);
			model.addAttribute("asOfDate", "As of date " + DateUtil.formatDate(asOfDate));
			String divisionName = "ALL";
			if (divisionId != -1) {
				Division division = divisionService.getDivision(divisionId);
				divisionName = division.getName();
				division = null;
			}
			model.addAttribute("division", divisionName);
			Currency currency = currencyService.getCurency(currencyId);
			model.addAttribute("currency", currency.getName());
			currency = null;
		} else {
			logger.error("Company and as of date are required");
			throw new RuntimeException("Company and as of date are required");
		}
		logger.info("Sucessfully loaded the supplier balances summary");
		return reportMapping;
	}

	@RequestMapping(value="/getSupplierAccounts", method = RequestMethod.GET)
	public @ResponseBody String getArAccounts(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			Model model, HttpSession session) {
		Collection<String> arAccounts = serviceImpl.getSupplierAccountNames(companyId, divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arAccounts, jConfig);
		return jsonArray.toString();
	}
}
