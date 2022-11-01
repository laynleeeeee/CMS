package eulap.eb.web;

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
import eulap.eb.service.report.StatementOfAccountParam;
import eulap.eb.service.report.StatementOfAccountService;
import net.sf.jasperreports.engine.JRException;

/**
 * Controller class for billing statement report of Eulap

 */

@Controller
@RequestMapping("/eulapBillingStatement")
public class EulapBillingStatmentCtrlr {
	private static final Logger logger =  Logger.getLogger(EulapBillingStatmentCtrlr.class);
	private final static String REPORT_TITLE = "BILLING STATEMENT";
	private final static String SIGNATORY = "Test T. Test";

	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArCustomerService customerService;
	@Autowired
	private ArCustomerAcctService acctService;
	@Autowired
	private StatementOfAccountService soaService;

	@RequestMapping(method = RequestMethod.GET)
	public String showStatementOfAccounts (Model model, HttpSession session) {
		model.addAttribute("companies", companyService.getCompanies(CurrentSessionHandler.getLoggedInUser(session)));
		return "StatementOfAccount.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generateStatementOfAcctPDF (@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="customerId") Integer customerId,
			@RequestParam (value="customerAcctId") Integer customerAcctId,
			@RequestParam (value="dateFrom") Date dateFrom,
			@RequestParam (value="dateTo") Date dateTo,
			@RequestParam (value="dueDate") Date dueDate,
			@RequestParam (value="formatType") String formatType,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating pdf version of statement of account");
		StatementOfAccountParam param = new StatementOfAccountParam();
		param.setCompanyId(companyId);
		param.setCustomerId(customerId);
		param.setCustomerAcctId(customerAcctId);
		param.setDateFrom(dateFrom);
		param.setDateTo(dateTo);
		model.addAttribute("datasource", soaService.generateSoaReport(param));
		model.addAttribute("format", formatType );

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("reportTitle" , REPORT_TITLE);

		ArCustomer customer = customerService.getCustomer(customerId);
		model.addAttribute("customerName", customer.getName());
		model.addAttribute("customerAddress", customer.getAddress());

		String customerAcctName = "ALL";
		String termName = "-";
		if(!customerAcctId.equals(-1)) {
			ArCustomerAccount customerAccount = acctService.getAccount(customerAcctId);
			customerAcctName = customerAccount.getName();
			termName = customerAccount.getTerm().getName();
		}
		model.addAttribute("customerAccount", customerAcctName);
		model.addAttribute("terms", termName);

		model.addAttribute("prevBalanceAmount", acctService.getPreviousBalanceAmount(param));
		model.addAttribute("dueDate", dueDate);

		String formattedDate = DateUtil.formatDate(dateFrom)  + " - " + DateUtil.formatDate(dateTo);
		model.addAttribute("dateRange", formattedDate);

		model.addAttribute("signatory", SIGNATORY);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("preparedByPosition",loggedUser.getPosition().getName());
		model.addAttribute("preparedDate", DateUtil.formatDate(new Date()));
		ReportUtil.getPrintDetails(model, loggedUser, false);
		return "StatementOfAccount.jasper";
	}
}
