package eulap.eb.web.report.jesper;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.ArServiceLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.SaleItemUtil;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A class that generates PDF file format of AR Transaction.

 */

@Controller
@RequestMapping("arTransactionPrint")
public class ArTransactionJR {
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArCustomerService arCustomerService;

	@RequestMapping (value="pdf", method = RequestMethod.GET)
	private String showReport (@RequestParam(value="pId", required = true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session) throws Exception {
		ArTransaction transaction = arTransactionService.getARTransaction(pId);
		getCommonParam(transaction, fontSize, model, "pdf", session);
		Integer trTypeId = transaction.getTransactionTypeId();
		if (trTypeId.equals(ArTransactionType.TYPE_CREDIT_MEMO) || trTypeId.equals(ArTransactionType.TYPE_DEBIT_MEMO)) {
			setMemoDetails(model, transaction);
			model.addAttribute("reportLable", trTypeId.equals(ArTransactionType.TYPE_CREDIT_MEMO) ? "CM #" : "DM #");
			model.addAttribute("reportTitle", trTypeId.equals(ArTransactionType.TYPE_CREDIT_MEMO) ? "CREDIT MEMO" : "DEBIT MEMO");
			return "CreditDebitMemoPDF.jasper";
		}
		setTrDetails(model, transaction);
		return "ArTransaction.jasper";
	}

	@RequestMapping (value="nsb/pdf", method = RequestMethod.GET)
	private String showReportNSB (@RequestParam(value="pId", required = true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session) throws Exception {
		ArTransaction transaction = arTransactionService.getARTransaction(pId);
		transaction.setArLines(arTransactionService.getARLine(transaction));
		getCommonParam(transaction, fontSize, model, "pdf", session);
		Integer trTypeId = transaction.getTransactionClassificationId();
		String reportTitle = "AR TRANSACTION";
		setMemoDetails(model, transaction);
		if (trTypeId.equals(ArTransactionType.TYPE_CREDIT_MEMO) || trTypeId.equals(ArTransactionType.TYPE_DEBIT_MEMO)) {
			model.addAttribute("reportLable", trTypeId.equals(ArTransactionType.TYPE_CREDIT_MEMO) ? "CM #" : "DM #");
			reportTitle = trTypeId.equals(ArTransactionType.TYPE_CREDIT_MEMO) ? reportTitle + " - CREDIT MEMO" : reportTitle + " - DEBIT MEMO";
		}
		model.addAttribute("reportTitle", reportTitle);
		setTrDetails(model, transaction);
		return "ArTransactionV2.jasper";
	}

	@RequestMapping (value="html", method = RequestMethod.GET)
	private String showReportHTML (@RequestParam(value="pId", required = true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session) throws Exception {
		ArTransaction transaction = arTransactionService.getARTransaction(pId);
		getCommonParam(transaction, fontSize, model, "html", session);
		Integer trTypeId = transaction.getTransactionTypeId();
		if (trTypeId.equals(ArTransactionType.TYPE_CREDIT_MEMO) || trTypeId.equals(ArTransactionType.TYPE_DEBIT_MEMO)) {
			setMemoDetails(model, transaction);
			return trTypeId.equals(ArTransactionType.TYPE_CREDIT_MEMO)
					? "CreditMemoHTML.jasper" : "DebitMemoHTML.jasper";
		}
		setTrDetails(model, transaction);
		return "ArTransactionHTML.jasper";
	}

	private void setTrDetails(Model model, ArTransaction transaction) {
		JRDataSource dataSource = new JRBeanCollectionDataSource(arTransactionService.getArServiceLine(transaction));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("sequenceNo", transaction.getSequenceNumber());
		model.addAttribute("transactionDate", DateUtil.formatDate(transaction.getTransactionDate()));
		model.addAttribute("transactionNumber", transaction.getTransactionNumber());
		model.addAttribute("trAmount", transaction.getAmount());
		model.addAttribute("currencyName", transaction.getCurrency().getName());
		model.addAttribute("divisionName", transaction.getDivision().getName());
		model.addAttribute("description", transaction.getDescription());
		WithholdingTaxAcctSetting wtax = transaction.getWtAcctSetting();
		model.addAttribute("wtVatAmount", transaction.getWtVatAmount() != null ? transaction.getWtVatAmount() : 0.0);
		if (wtax != null) {
			model.addAttribute("wtAmount", transaction.getWtAmount() != null ? transaction.getWtAmount() : 0.0);
			model.addAttribute("wtAcctSetting", wtax.getName());
		}
	}

	private void setMemoDetails(Model model, ArTransaction transaction) {
		model.addAttribute("amount", transaction.getAmount());
		model.addAttribute("transactionNo", transaction.getTransactionNumber());
		model.addAttribute("transactionDate", transaction.getTransactionDate());
		WithholdingTaxAcctSetting wtax = transaction.getWtAcctSetting();
		if (wtax != null) {
			model.addAttribute("wtAmount", transaction.getWtAmount() != null ? transaction.getWtAmount() : 0.0);
			model.addAttribute("wtAcctSetting", wtax.getName());
		}

		List<ArServiceLine> arServiceLines = transaction.getArServiceLines();
		double subTotal = arTransactionService.getTotalNetAmount(arServiceLines);
		model.addAttribute("subTotal", subTotal);
		double totalVat = arTransactionService.getTotalVatAmount(arServiceLines);
		model.addAttribute("totalVat", totalVat);
		double totalGross = subTotal + totalVat;
		model.addAttribute("totalGross", totalGross);
		arServiceLines = null;
	}

	private void getCommonParam(ArTransaction transaction, Integer fontSize, Model model, String format, HttpSession session) {
		SaleItemUtil.getFontSize(fontSize, model);

		model.addAttribute("format", format);
		model.addAttribute("arCustomer", transaction.getArCustomer().getName());
		model.addAttribute("address", arCustomerService.getArCustomerFullAddress(transaction.getArCustomer()));

		FormWorkflow workflow = transaction.getFormWorkflow();
		for (FormWorkflowLog log : workflow.getFormWorkflowLogs()) {
			String name = log.getCreated().getUserFullName();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("preparedBy", name);
				model.addAttribute("preparedPosition", log.getCreated().getPosition().getName());
				model.addAttribute("preparedDate",  DateUtil.formatDate(log.getCreatedDate()));
			} else if (log.getFormStatusId() == FormStatus.REVIEWED_ID){
				model.addAttribute("checkedBy", name);
				model.addAttribute("checkerPosition", log.getCreated().getPosition().getName());
				model.addAttribute("checkedDate",  DateUtil.formatDate(log.getCreatedDate()));
			} else if (log.getFormStatusId() == FormStatus.RECEIVED_ID){
				model.addAttribute("receivedBy", name);
				model.addAttribute("receiverPosition", log.getCreated().getPosition().getName());
				model.addAttribute("receiverDate",  DateUtil.formatDate(log.getCreatedDate()));
			}
		}

		// Company information
		Company company = companyService.getCompany(transaction.getArCustomerAccount().getCompanyId());
		String companyLogo = company.getLogo();
		if (format == "xls") {
			companyLogo = session.getServletContext().getContextPath() + "/images/logo/" + company.getLogo();
		}
		model.addAttribute("companyLogo", companyLogo);
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
	}
}
