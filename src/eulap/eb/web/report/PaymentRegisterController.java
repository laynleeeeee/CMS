package eulap.eb.web.report;

import java.math.BigDecimal;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.PaymentRegisterParam;
import eulap.eb.service.report.PaymentRegisterServiceImpl;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A controller class that handles the payment register report generation.

 */
@Controller
@RequestMapping("paymentRegister")
public class PaymentRegisterController {
	@Autowired
	private PaymentRegisterServiceImpl registerServiceImpl;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private DivisionService divisionService;

	private final static String REPORT_TITLE = "PAYMENT REGISTER";

	@RequestMapping(method = RequestMethod.GET)
	public String showPaymentRegister( Model model, HttpSession session) {
		loadSection(model, session);
		return "PaymentRegisterReport.jsp";
	}

	private void loadSection(Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		//Suppliers
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		//Bank Accounts
		List<BankAccount> bankAccounts = bankAccountService.getAllBankAccounts(user);
		model.addAttribute("bankAccounts", bankAccounts);
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPdfExcel (@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="bankAccountId", required=false) Integer bankAccountId,
			@RequestParam (value="supplierId", required=false) Integer supplierId,
			@RequestParam (value="supplierAccountId", required=false) Integer supplierAccountId,
			@RequestParam (value="paymentDateFrom", required=false) String paymentDateFrom,
			@RequestParam (value="paymentDateTo", required=false) String paymentDateTo,
			@RequestParam (value="checkDateFrom", required=false) String checkDateFrom,
			@RequestParam (value="checkDateTo", required=false) String checkDateTo,
			@RequestParam (value="amountFrom", required=false) Double amountFrom,
			@RequestParam (value="amountTo", required=false) Double amountTo,
			@RequestParam (value="voucherNoFrom", required=false) Integer voucherNoFrom,
			@RequestParam (value="voucherNoTo", required=false) Integer voucherNoTo,
			@RequestParam (value="checkNoFrom", required=false) String checkNoFrom,
			@RequestParam (value="checkNoTo", required=false) String checkNoTo,
			@RequestParam (value="paymentStatusId", required=false) Integer paymentStatusId,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		PaymentRegisterParam param = new PaymentRegisterParam();
		param.setCompanyId(companyId);
		param.setDivisionId(divisionId);
		param.setBankAccountId(bankAccountId);
		param.setSupplierId(supplierId);
		param.setSupplierAccountId(supplierAccountId);
		param.setPaymentDateFrom(DateUtil.parseDate(paymentDateFrom));
		param.setPaymentDateTo(DateUtil.parseDate(paymentDateTo));
		param.setCheckDateFrom(DateUtil.parseDate(checkDateFrom));
		param.setCheckDateTo(DateUtil.parseDate(checkDateTo));
		param.setAmountFrom(amountFrom);
		param.setAmountTo(amountTo);
		param.setVoucherNoFrom(voucherNoFrom);
		param.setVoucherNoTo(voucherNoTo);
		if (StringFormatUtil.isNumeric(checkNoFrom.trim()) && !checkNoFrom.trim().isEmpty()) {
			param.setCheckNoFrom(new BigDecimal(checkNoFrom.trim()));
		}
		if (StringFormatUtil.isNumeric(checkNoTo.trim()) && !checkNoTo.trim().isEmpty()) {
			param.setCheckNoTo(new BigDecimal(checkNoTo.trim()));
		}
		param.setPaymentStatusId(paymentStatusId);
		JRDataSource dataSource = new JRBeanCollectionDataSource(registerServiceImpl.generateReport(user, param));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		company = null;
		model.addAttribute("reportTitle" , REPORT_TITLE);
		ReportUtil.getPrintDetails(model, user, isFirstNameFirst != null ? isFirstNameFirst : false);
		if (divisionId != -1) {
			Division division = divisionService.getDivision(divisionId);
			model.addAttribute("divisionName", division.getName());
		}
		// If status is ALL or CANCELLED, show the reports with CANCELLED Status
		if (paymentStatusId == -1 || paymentStatusId.equals(FormStatus.CANCELLED_ID)) {
			return "PaymentRegisterCancellationRemarks.jasper";
		}
		return "PaymentRegister.jasper";
	}
}