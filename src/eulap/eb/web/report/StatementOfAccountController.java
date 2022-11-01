package eulap.eb.web.report;

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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.StatementOfAccountParam;
import eulap.eb.service.report.StatementOfAccountService;
import net.sf.jasperreports.engine.JRException;

/**
 * Statement of account report controller

 */
@Controller
@RequestMapping("/statementOfAccount")
public class StatementOfAccountController {
	private static Logger logger = Logger.getLogger(StatementOfAccountController.class);
	private final static String REPORT_TITLE = "STATEMENT OF ACCOUNT";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArCustomerService customerService;
	@Autowired
	private ArCustomerAcctService acctService;
	@Autowired
	private StatementOfAccountService soaService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;

	@RequestMapping(method = RequestMethod.GET)
	public String showStatementOfAccounts (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(null));
		return "StatementOfAccount.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generateStatementOfAcctPDF (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="customerId", required=true) Integer customerId,
			@RequestParam (value="customerAcctId", required=true) Integer customerAcctId,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			@RequestParam (value="currencyId", required = true) Integer currencyId,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating pdf version of statement of account");
		StatementOfAccountParam param = new StatementOfAccountParam();
		logger.debug("Company ID: " + companyId);
		param.setCompanyId(companyId);
		logger.debug("Division ID: " + divisionId);
		param.setDivisionId(divisionId);
		logger.debug("Set customer id" + customerId);
		param.setCustomerId(customerId);
		logger.debug("Set customer account id" + customerAcctId);
		param.setCustomerAcctId(customerAcctId);
		logger.debug("Set date from " + dateFrom);
		param.setDateFrom(dateFrom);
		logger.debug("Set set date to " + dateTo);
		param.setDateTo(dateTo);
		param.setCurrencyId(currencyId);
		logger.debug("Retreiving the list of SOA");
		model.addAttribute("datasource", soaService.generateBillingStatementReport(param));
		model.addAttribute("format", formatType );
		getCommon(companyId, dateFrom, dateTo, model, customerId,
				customerAcctId, session, param, isFirstNameFirst,
				formatType);
		return "StatementOfAccount.jasper";
	}

	@RequestMapping (value="/generateSoaWithAcRef", method = RequestMethod.GET)
	public String generateSoaWithAcRef (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="customerId", required=true) Integer customerId,
			@RequestParam (value="customerAcctId", required=true) Integer customerAcctId,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating pdf version of statement of account");
		StatementOfAccountParam param = new StatementOfAccountParam();
		logger.debug("Company ID: " + companyId);
		param.setCompanyId(companyId);
		logger.debug("Set customer id" + customerId);
		param.setCustomerId(customerId);
		logger.debug("Set customer account id" + customerAcctId);
		param.setCustomerAcctId(customerAcctId);
		logger.debug("Set date from " + dateFrom);
		param.setDateFrom(dateFrom);
		logger.debug("Set set date to " + dateTo);
		param.setDateTo(dateTo);
		logger.debug("Retreiving the list of SOA");
		model.addAttribute("datasource", soaService.generateSoaReportWithAcRef(param));
		model.addAttribute("format", formatType );
		getCommon(companyId, dateFrom, dateTo, model, customerId,
				customerAcctId, session, param, isFirstNameFirst,
				formatType);
		return "StatementOfAccount.jasper";
	}

	private void getCommon(Integer companyId, Date dateFrom, Date dateTo, Model model,
			Integer customerId, Integer customerAcctId, HttpSession session,
			StatementOfAccountParam param, Boolean isFirstNameFirst, String formatType){
		logger.info("Generating pdf version of statement of account");
		Company company = null;
		ArCustomer customer = null;
		ArCustomerAccount customerAccount = null;
		String strCustomerAcct = "ALL";
		if (companyId != null && customerId != null && 
				customerAcctId != null && dateFrom != null && dateTo != null) {
			company = companyService.getCompany(companyId);
			customer = customerService.getCustomer(customerId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , REPORT_TITLE);
			if (customerAcctId < 0)
				customerAccount = acctService.getArCustomerAcct(companyId, customer.getId());
			else{
				customerAccount = acctService.getAccount(customerAcctId);
				strCustomerAcct = customerAccount.getName();
			}
			model.addAttribute("customerName", customer.getName());
			model.addAttribute("customerAddress", customer.getAddress());
			model.addAttribute("customerAccount", strCustomerAcct);
			String termName = customerAccount.getTerm().getName();
			model.addAttribute("terms", termName);
			String formattedDate = DateUtil.formatDate(dateFrom)  +" - "+ DateUtil.formatDate(dateTo);
			model.addAttribute("dateRange", formattedDate);
			User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
			ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		} else {
			logger.error("Company and time period are required.");
			throw new RuntimeException("Company, ar customer, ar customer account, and "
					+ "date range are required.");
		}
		logger.info("Sucessfully loaded the statement of accounts.");
	}
}
