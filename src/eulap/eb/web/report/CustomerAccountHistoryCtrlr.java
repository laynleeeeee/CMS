package eulap.eb.web.report;

import java.util.Date;

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
import eulap.common.util.Page;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.CustomerAccountHistoryParam;
import eulap.eb.service.report.CustomerAcctHistoryServiceImpl;
import eulap.eb.web.dto.CustomerBalancesSummaryDto;
import net.sf.jasperreports.engine.JRDataSource;
/**
 * A controller class that handles the Customer Account History generation.

 */
@Controller
@RequestMapping("customerAccountHistory")
public class CustomerAccountHistoryCtrlr {
	private final static String REPORT_TITLE = "Customer Account History Report";
	private final Logger logger = Logger.getLogger(CustomerAccountHistoryCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArCustomerService customerService;
	@Autowired
	private ArCustomerAcctService customerAcctService;
	@Autowired
	private CustomerAcctHistoryServiceImpl historyServiceImpl;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showCustomerAcctHistory(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user, model);
		logger.info("Loading customer account history report");
		return "CustomerAccountHistoryReport.jsp";
	}

	private void loadSelections(User user, Model model) {
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("customers", customerService.getArCustomers(user));
		model.addAttribute("division", divisionService.getActiveDivisions(0));
		model.addAttribute("currency", currencyService.getActiveCurrencies(0));
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generateAccountHistoryPDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="customerAcctId", required=true) Integer customerAcctId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType, 
			@RequestParam (value="currencyId", required=true) Integer currencyId,
			HttpSession session, Model model) {
		logger.info("Generating " + formatType.toUpperCase() + " version of customer account history.");
		JRDataSource dataSource = historyServiceImpl.generateCustAcctHistReport(populateParam(companyId,
				customerAcctId, dateFrom, dateTo, divisionId, currencyId));
		return generateAccountHistory(companyId, customerAcctId, dateFrom, dateTo, divisionId, currencyId,
				formatType, model, dataSource, session, "CustomerAccountHistory.jasper", REPORT_TITLE);
	}

	/**
	 * Generates the report printout.
	 * @param companyId The company id.
	 * @param customerAcctId The customer account id.
	 * @param dateFrom The date from filter.
	 * @param dateTo The date to filter.
	 * @param formatType The format type.
	 * @param model The model.
	 * @param dataSource The datasource object.
	 * @param session The session.
	 * @param reportMapping The report views mapping.
	 * @param reportTitle The report title.
	 */
	public String generateAccountHistory(Integer companyId, Integer customerAcctId, Date dateFrom, Date dateTo,
			Integer divisionId, Integer currencyId, String formatType, Model model, JRDataSource dataSource,
			HttpSession session, String reportMapping, String reportTitle) {
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		if (companyId != null && customerAcctId != null ) {
			Company company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , reportTitle);
			if (customerAcctId != -1) {
				ArCustomerAccount customerAccount =  customerAcctService.getAccount(customerAcctId);
				model.addAttribute("customerAccountName", customerAccount.getName());
				model.addAttribute("customerName", customerAccount.getArCustomer().getName());
			} else {
				model.addAttribute("customerAccountName", "ALL");
				model.addAttribute("customerName", "ALL");
			}
			String formattedDate = DateUtil.formatDate(dateFrom)  +" to "+ DateUtil.formatDate(dateTo);
			model.addAttribute("dateRange", formattedDate);
			Currency currency = currencyService.getCurency(currencyId);
			model.addAttribute("currency", currency.getName());

			logger.debug("Get the total transaction amount of customer account id " + customerAcctId);
			Page<CustomerBalancesSummaryDto> summaryDto = historyServiceImpl.getTotalSummary(companyId, customerAcctId, divisionId, currencyId, dateTo);
			for (CustomerBalancesSummaryDto balancesSummaryDto : summaryDto.getData()) {
				model.addAttribute("totalTransactionAmount", balancesSummaryDto.getTotalTransaction());
				model.addAttribute("totalReceiptAmount", balancesSummaryDto.getTotalReceipt());
				model.addAttribute("totalGainLoss", balancesSummaryDto.getGainLoss());
				logger.debug("Get the total receipt amount of customer account id " + customerAcctId);
				break;
			}
			User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
			ReportUtil.getPrintDetails(model, loggedUser);
		} else {
			logger.error("Company and customer account are required.");
			throw new RuntimeException("Company and customer account are required.");
		}

		logger.info("Successfully loaded customer account history.");
		return reportMapping;
	}

	/**
	 * Populate the customer account history report parameters.
	 * @param companyId The company id.
	 * @param customerAcctId The customer account id.
	 * @param dateFrom The date from filter.
	 * @param dateTo The date to filter.
	 * @return The CustomerAccountHistoryParam object.
	 */
	public CustomerAccountHistoryParam populateParam(Integer companyId, Integer customerAcctId, Date dateFrom, Date dateTo, Integer divisionId , Integer currencyId) {
		CustomerAccountHistoryParam param = new CustomerAccountHistoryParam();
		logger.debug("Set company " + companyId);
		param.setCompanyId(companyId);
		logger.debug("Set division " + divisionId);
		param.setDivisionId(divisionId);
		logger.debug("Set currency " + currencyId);
		param.setCurrencyId(currencyId);
		logger.debug("Set customer " + customerAcctId);
		param.setCustomerAcctId(customerAcctId);
		logger.debug("Set date from " + dateFrom);
		param.setDateFrom(dateFrom);
		logger.debug("Set set date to " + dateTo);
		param.setDateTo(dateTo);

		// TODO : Investigate this code further because it appears to have no significance. 
		// We decided to leave it as it is for now to avoid unexpected problems/issues.
		historyServiceImpl.generate(param); 

		logger.info("Retreive the list of customer account history.");
		return param;
	}
}
