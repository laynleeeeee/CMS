package eulap.eb.web.report.jesper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArReceiptService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.SaleItemUtil;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A class that generate PDF file format of AR Receipt.

 */
@Controller
@RequestMapping("arReceiptPrint")
public class ArReceiptJR {
	@Autowired
	private ArReceiptService arReceiptService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArCustomerService arCustomerService;

	@RequestMapping (value="pdf", method = RequestMethod.GET)
	private String showReport (@RequestParam (value="pId", required = true)Integer pId, 
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session){
		ArReceipt arReceipt = arReceiptService.getArReceiptWithArReceiptLines(pId);
		getCommon (model, pId, fontSize, arReceipt, "pdf", session);
		return "ArReceipt.jasper";
	}

	@RequestMapping (value="html", method = RequestMethod.GET)
	private String showReporthtml (@RequestParam (value="pId", required = true)Integer pId, 
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session){
		ArReceipt arReceipt = arReceiptService.getArReceipt(pId);
		getCommon (model, pId, fontSize, arReceipt, "html", session);
		return "ArReceiptHTML.jasper";
	}

	private void getCommon (Model model, Integer pId, Integer fontSize, ArReceipt arReceipt,
			String format, HttpSession session) {
		List<ArReceipt> receipts = new ArrayList<ArReceipt>();
		receipts.add(arReceipt);
		JRDataSource dataSource = new JRBeanCollectionDataSource(receipts);
		model.addAttribute("format", format);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("receiptDate", DateUtil.formatDate(arReceipt.getReceiptDate()));
		model.addAttribute("receiptNumber", arReceipt.getReceiptNumber());
		String amountStr = NumberFormatUtil.format(arReceipt.getAmount());
		String decStr = "";
		String sumAmountStr = "";
		Double amountAbs = Math.abs(arReceipt.getAmount());
		if(!amountStr.contains(".0")){
			decStr = " and "+amountStr.substring((amountStr.length())-2, (amountStr.length()))+"/100";
		}
		sumAmountStr = NumberFormatUtil.numbersToWords(amountAbs)+decStr+" Only."
				+" ("+ NumberFormatUtil.format(amountAbs).toString()+")";
		model.addAttribute("seqNumber", arReceipt.getSequenceNo());
		model.addAttribute("amount", sumAmountStr);
		model.addAttribute("arCustomer", arReceipt.getArCustomer().getName());
		model.addAttribute("address", arCustomerService.getArCustomerFullAddress(arReceipt.getArCustomer()));
		model.addAttribute("tin", StringFormatUtil.processBirTinTo13Digits(arReceipt.getArCustomer().getTin()));
		model.addAttribute("total", arReceipt.getAmount());
		model.addAttribute("divisionName", arReceipt.getDivision().getName());
		model.addAttribute("currency", arReceipt.getCurrency().getName());
		model.addAttribute("maturityDate", arReceipt.getMaturityDate());
		model.addAttribute("checkNo", arReceipt.getRefNumber());
		model.addAttribute("totalAdvPayment", arReceiptService.getTotalAdvPayment(arReceipt.getArReceiptLines()));
		model.addAttribute("subTotal", arReceiptService.getSubTotal(arReceipt.getArReceiptLines()));
		model.addAttribute("recoupment", arReceiptService.negateAmount(arReceipt.getRecoupment()));
		model.addAttribute("retention", arReceiptService.negateAmount(arReceipt.getRetention()));
		BankAccount bankAccount = arReceipt.getReceiptMethod().getBankAccount();
		String bankName = "";
		if(bankAccount != null) {
			bankName = bankAccount.getBank().getName();
		}
		model.addAttribute("bank", bankName);

		//Company information
		Company company = companyService.getCompany(arReceipt.getArCustomerAccount().getCompanyId());
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
		SaleItemUtil.getFontSize(fontSize, model);

		FormWorkflow wf = arReceipt.getFormWorkflow();
		List<FormWorkflowLog> logs = wf.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			String prependStatus = "";
			switch (log.getFormStatusId()) {
			case FormStatus.RECEIVED_ID:
				prependStatus = "receiver";
				break;
			case FormStatus.CLEARED_ID:
				prependStatus = "negotiator";
				break;
			default:
				break;
			}
			model.addAttribute(prependStatus, user.getUserFullName());
			model.addAttribute(prependStatus + "Position", position.getName());
		}
	}
}
