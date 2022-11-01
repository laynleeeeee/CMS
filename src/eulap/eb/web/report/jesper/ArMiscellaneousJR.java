package eulap.eb.web.report.jesper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.Date;

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
import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArMiscellaneousService;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;

/**
 * A class that generates file format of AR Miscellaneous.

 */
@Controller
@RequestMapping("arMiscellaneous")
public class ArMiscellaneousJR {
	@Autowired
	private ArMiscellaneousService miscellaneousService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private BankAccountService bankAccountService;
	
	@RequestMapping (value="pdf", method = RequestMethod.GET)
	private String showReport (@RequestParam (value="pId", required = true)Integer pId,
			Model model, HttpSession session){
		ArMiscellaneous arm = miscellaneousService.getArMiscellaneous(pId);
		getCommonParam(pId, model, "pdf", session);
		if(arm.getDivisionId() != null) {
			return "NSBArMiscellaneous.jasper";
		}
		return "ArMiscellaneous.jasper";
	}

	@RequestMapping (value="html", method = RequestMethod.GET)
	private String showReportHtml (@RequestParam (value="pId", required = true)Integer pId,
			Model model, HttpSession session){
		getCommonParam(pId, model, "html", session);
		return "ArMiscellaneousHTML.jasper";
	}

	private void getCommonParam(Integer pId, Model model, String format, HttpSession session){
		ArMiscellaneous miscellaneous = miscellaneousService.getArMiscellaneous(pId);
		JRDataSource dataSource = new JRBeanCollectionDataSource(miscellaneous.getArMiscellaneousLines());
		model.addAttribute("format", format);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("arCustomer", miscellaneous.getArCustomer().getName());
		model.addAttribute("address", arCustomerService.getArCustomerFullAddress(miscellaneous.getArCustomer()));
		model.addAttribute("tin", StringFormatUtil.processBirTinTo13Digits(miscellaneous.getArCustomer().getTin()));
		model.addAttribute("receiptDate", DateUtil.formatDate(miscellaneous.getReceiptDate()));
		model.addAttribute("maturityDate", DateUtil.formatDate(miscellaneous.getMaturityDate()));
		model.addAttribute("receiptNumber", miscellaneous.getReceiptNumber());
		model.addAttribute("currencyName", miscellaneous.getCurrency().getName());
		model.addAttribute("divisionName", miscellaneous.getDivision().getName());
		model.addAttribute("sequenceNo", miscellaneous.getSequenceNo());
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		model.addAttribute("withholdingVAT", miscellaneous.getWtVatAmount() != null ? miscellaneous.getWtVatAmount() : 0.0);
		Integer bankAccountId = miscellaneous.getReceiptMethod().getBankAccountId() != null ? miscellaneous.getReceiptMethod().getBankAccountId() : null;
		String bankName = null;
		BankAccount ba = null;
		if(bankAccountId != null) {
			ba = bankAccountService.getBankAccount(bankAccountId);
			bankName = ba.getBank().getName();
		}
		model.addAttribute("bankName", bankName);
		model.addAttribute("description", miscellaneous.getDescription());
		String decStr = NumberFormatUtil.amountToWordsWithDecimals(miscellaneous.getAmount())
				+ " ("+ NumberFormatUtil.format(miscellaneous.getAmount()).toString()+")";
		model.addAttribute("amount", decStr);

		//Company information
		Company company = companyService.getCompany(miscellaneous.getCompanyId());
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
		WithholdingTaxAcctSetting wtax = miscellaneous.getWtAcctSetting();
		if (wtax != null) {
			double wtAmount = miscellaneous.getWtAmount() != null ? miscellaneous.getWtAmount() : 0;
			model.addAttribute("wtAmount", wtAmount);
			model.addAttribute("wtAcctSetting", wtax.getName());
		}

		FormWorkflow workflowLog = miscellaneous.getFormWorkflow();
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			String name = log.getCreated().getUserFullName();
			if(log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", name);
				model.addAttribute("creatorPosition", log.getCreated().getPosition().getName());
			}
			if(log.getFormStatusId() == FormStatus.REVIEWED_ID) {
				model.addAttribute("reviewedBy", name);
				model.addAttribute("reviewerPosition", log.getCreated().getPosition().getName());
			}
			if(log.getFormStatusId() == FormStatus.CLEARED_ID) {
				model.addAttribute("clearedBy", name);
				model.addAttribute("clearerPosition", log.getCreated().getPosition().getName());
			}
		}
	}
}
