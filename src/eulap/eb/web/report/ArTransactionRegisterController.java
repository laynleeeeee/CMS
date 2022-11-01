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
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.ArTransactionRegisterParam;
import eulap.eb.service.report.ArTransactionRegisterServiceImpl;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * A controller class that handles the AR transaction register report generation.

 */

@Controller
@RequestMapping("/transactionRegister")
public class ArTransactionRegisterController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ArTransactionRegisterServiceImpl transactionRegisterServiceImpl;
	@Autowired
	private ArCustomerService customerService;
	private final static String REPORT_TITLE = "TRANSACTION REGISTER";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showTransactionRegister( Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("division", divisionService.getActiveDivisions(0));
		model.addAttribute("transactionClassifications", transactionRegisterServiceImpl.showTransactionClassification(user));
		model.addAttribute("statuses", transactionRegisterServiceImpl.paymentStatus());
		model.addAttribute("customers", customerService.getArCustomers(user));
		model.addAttribute("formStatuses", transactionRegisterServiceImpl.getFormStatuses(user));
		return "ArTransactionRegisterReport.jsp";
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=false) int divisionId,
			@RequestParam (value="transactionClassificationId", required=false) int transactionClassificationId,
			@RequestParam (value="customerId", required=false, defaultValue="-1") int customerId,
			@RequestParam (value="customerAcctId", required=false, defaultValue="-1") int customerAcctId,
			@RequestParam (value="transactionNumber", required=false) String transactionNumber,
			@RequestParam (value="transactionDateFrom", required=false) Date transactionDateFrom,
			@RequestParam (value="transactionDateTo", required=false) Date transactionDateTo,
			@RequestParam (value="glDateFrom", required=false) Date glDateFrom,
			@RequestParam (value="glDateTo", required=false) Date glDateTo,
			@RequestParam (value="dueDateFrom", required=false) Date dueDateFrom,
			@RequestParam (value="dueDateTo", required=false) Date dueDateTo,
			@RequestParam (value="amountFrom", required=false) Double amountFrom,
			@RequestParam (value="amountTo", required=false) Double amountTo,
			@RequestParam (value="sequenceNoFrom", required=false) Integer sequenceNoFrom,
			@RequestParam (value="sequenceNoTo", required=false) Integer sequenceNoTo,
			@RequestParam (value="transactionStatusId", required=false, defaultValue="-1") int transactionStatusId,
			@RequestParam (value="paymentStatusId", required=false, defaultValue="-1") int paymentStatusId,
			@RequestParam (value="asOfDate", required=false) Date asOfDate,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) {
		ArTransactionRegisterParam param = new ArTransactionRegisterParam();
		param.setCompanyId(companyId);
		param.setDivisionId(divisionId);
		param.setTransactionClassificationId(transactionClassificationId);
		param.setCustomerId(customerId);
		param.setCustomerAcctId(customerAcctId);
		param.setTransactionNumber(transactionNumber);
		param.setTransactionDateFrom(transactionDateFrom);
		param.setTransactionDateTo(transactionDateTo);
		param.setGlDateFrom(glDateFrom);
		param.setGlDateTo(glDateTo);
		param.setDueDateFrom(dueDateFrom);
		param.setDueDateTo(dueDateTo);
		param.setAmountFrom(amountFrom);
		param.setAmountTo(amountTo);
		param.setSequenceNoFrom(sequenceNoFrom);
		param.setSequenceNoTo(sequenceNoTo);
		param.setTransactionStatusId(transactionStatusId);
		param.setPaymentStatusId(paymentStatusId);
		param.setAsOfDate(asOfDate);

		JRDataSource dataSource = transactionRegisterServiceImpl.generateTransactionRegister(param);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		company = null;

		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("asOfDate" , "as of " + DateUtil.formatDate(asOfDate));

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "TransactionRegisterwithCancellation.jasper";
	}
}
