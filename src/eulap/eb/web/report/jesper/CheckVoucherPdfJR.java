package eulap.eb.web.report.jesper;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ApPaymentService;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.SupplierService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A class the generate PDF file format for check voucher form.

 */
@Controller
@RequestMapping ("checkVoucher")
public class CheckVoucherPdfJR {
	@Autowired
	private ApPaymentService apPaymentService;
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private CompanyService companyService;

	@RequestMapping (value="{divisionId}/pdf", method = RequestMethod.GET)
	private String showFormPdf (@PathVariable(value="divisionId") int divisionId,
			@RequestParam (value="pId", required = true)Integer pId, Model model, HttpSession session){
		getCommon(model, pId, "pdf", session);
		return "CheckVoucher.jasper";
	}

	@RequestMapping (value="html", method = RequestMethod.GET)
	private String showFormHTML (@RequestParam (value="pId", required = true)Integer pId,
			Model model, HttpSession session){
		getCommon(model, pId, "html", session);
		return "CheckVoucherHTML.jasper";
	}

	private void getCommon (Model model, Integer pId, String format, HttpSession session){
		ApPayment payment = apPaymentService.getApPaymentPrintoutData(pId);
		JRDataSource dataSource = new JRBeanCollectionDataSource(payment.getApPaymentLineDtos());
		model.addAttribute("format", format);
		model.addAttribute("datasource", dataSource);
		Supplier supplier = supplierService.getSupplier(payment.getSupplierId());
		model.addAttribute("payee", supplier.getName());
		model.addAttribute("address", supplierService.processSupplierAddress(supplier));
		model.addAttribute("tin", StringFormatUtil.processBirTinTo13Digits(supplier.getTin()));
		model.addAttribute("voucherNo", payment.getVoucherNumber().toString());
		model.addAttribute("paymentDate", DateUtil.formatDate(payment.getPaymentDate()));
		model.addAttribute("currency", payment.getCurrency().getName());
		model.addAttribute("clearingDate", payment.getDateCleared());
		String decStr = NumberFormatUtil.amountToWordsWithDecimals(payment.getAmount())
				+ " ("+ NumberFormatUtil.format(payment.getAmount()).toString()+")";
		model.addAttribute("amountWords", 
				payment.getCurrencyId() != Currency.DEFUALT_PHP_ID ? apPaymentService.removePesos(decStr) :decStr);
		model.addAttribute("bankAccount", bankAccountService.getBankAccount(payment.getBankAccountId()).getName());
		model.addAttribute("checkNo", payment.getCheckNumber().toString());
		model.addAttribute("checkDate", DateUtil.formatDate(payment.getCheckDate()));
		//Company information
		Company company = companyService.getCompany(payment.getCompanyId());
		if(format == "pdf") {
			model.addAttribute("companyLogo", company.getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ company.getLogo());
		}
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		String companyTin = company.getTin();
		if(companyTin != null) {
			if(!companyTin.trim().isEmpty())
				companyTin = companyService.getTin(companyTin);
		}
		model.addAttribute("companyTin", companyTin);

		FormWorkflow pWorkflow = payment.getFormWorkflow();
		List<FormWorkflowLog> pLogs = pWorkflow.getFormWorkflowLogs();
		if(pWorkflow.getCurrentStatusId() == 4 ){
			model.addAttribute("cancelled", "cancelled.png");
		}
		for (FormWorkflowLog log : pLogs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			String prependStatus = "";
			switch (log.getFormStatusId()) {
			case FormStatus.CREATED_ID:
				prependStatus = "created";
				break;
			case FormStatus.REVIEWED_ID:
				prependStatus = "reviewed";
				break;
			case FormStatus.ISSUED_ID:
				prependStatus = "issued";
				break;
			case FormStatus.CLEARED_ID:
				prependStatus = "cleared";
				break;
			default:
				break;
			}
			model.addAttribute(prependStatus + "By", user.getUserFullName());
			model.addAttribute(prependStatus + "Position", position.getName());
		}
	}
}
