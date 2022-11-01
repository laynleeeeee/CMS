package eulap.eb.web.report.jesper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CustomerAdvancePaymentService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A class the generate PDF file format of retail Customer Advance Payment.

 */
@Controller
@RequestMapping("customerAdvancePaymentPDF")
public class CustomerAdvancePaymentJR {
	private static Logger logger =  Logger.getLogger(CustomerAdvancePaymentJR.class);
	@Autowired
	private CustomerAdvancePaymentService capService;
	@Autowired
	private ArCustomerService arCustomerService;

	@RequestMapping(method = RequestMethod.GET)
	private String showReport (@RequestParam(value="pId", required=true) Integer pId, Model model){
		if(pId != null) {
			logger.info("Loading the customer advance payment pdf format.");
			List<CustomerAdvancePayment> caps = new ArrayList<>();
			CustomerAdvancePayment cap = capService.getCapPrint(pId);
			caps.add(cap);
			JRDataSource dataSource = new JRBeanCollectionDataSource(caps);
			model.addAttribute("datasource", dataSource);
			model.addAttribute("format", "pdf");
			model.addAttribute("companyLogo", cap.getCompany().getLogo());
			model.addAttribute("companyName", cap.getCompany().getName());
			model.addAttribute("companyAddress", cap.getCompany().getAddress());

			String reportTitle = "CUSTOMER ADVANCE PAYMENT";
			String snLabel = "CAP No.";
			model.addAttribute("reportTitle" , reportTitle);
			model.addAttribute("snLabel", snLabel);

			ArCustomer arCustomer = cap.getArCustomer();
			model.addAttribute("customerName", arCustomer.getName());
			model.addAttribute("address", arCustomerService.getArCustomerFullAddress(arCustomer));
			model.addAttribute("tin", StringFormatUtil.processTin(arCustomer.getTin()));
			model.addAttribute("customerAcctName", cap.getArCustomerAccount().getName());
			model.addAttribute("poNumber", cap.getPoNumber());
			model.addAttribute("salesInvoiceNo", cap.getSalesInvoiceNo());
			model.addAttribute("sequenceNo", cap.getCapNumber());
			model.addAttribute("date", DateUtil.removeTimeFromDate(cap.getReceiptDate()));
			double subTotal = capService.getTotalAmount(pId, false);
			model.addAttribute("subTotal", subTotal);
			double totalVAT = capService.getTotalAmount(pId, true);
			model.addAttribute("totalVAT", totalVAT);
			String wtAcctName = cap.getWtAcctSetting() != null ? cap.getWtAcctSetting().getName() : "";
			model.addAttribute("wtAccount", wtAcctName);
			double wtAmount = cap.getWtAmount() != null ? cap.getWtAmount() : 0.0;
			model.addAttribute("wtAmount", wtAmount);
			double totalAmountDue = (subTotal+totalVAT)-wtAmount;
			model.addAttribute("total", totalAmountDue);
			model.addAttribute("totalAmountDue", totalAmountDue);
			model.addAttribute("cash", cap.getCash());
			model.addAttribute("checkNo", cap.getRefNumber());
			model.addAttribute("typeId", cap.getCustomerAdvancePaymentTypeId());
			model.addAttribute("txtAmount", capService.convertNumberToWords(cap.getAmount(), cap.getCurrencyId()));
			model.addAttribute("amount", -cap.getAmount());
			model.addAttribute("balance", capService.getSoRemainingBalance(cap.getCapLines(), cap.getAmount()));
			model.addAttribute("currency", cap.getCurrency().getName());

			FormWorkflow capWorkflow = cap.getFormWorkflow();
			List<FormWorkflowLog> capLogs = capWorkflow.getFormWorkflowLogs();
			for (FormWorkflowLog log : capLogs) {
				User user = log.getCreated();
				Position position = user.getPosition();
				String prependStatus = "";
				switch (log.getFormStatusId()) {
				case FormStatus.CREATED_ID:
					prependStatus = "creator";
					break;
				case FormStatus.REVIEWED_ID:
					prependStatus = "reviewer";
					break;
				default:
					break;
				}
				model.addAttribute(prependStatus, user.getUserFullName());
				model.addAttribute(prependStatus + "Position", position.getName());
			}
		} else {
			logger.error("Customer advance payment id required.");
			throw new RuntimeException("Customer advance payment id required.");
		}
		logger.info("Sucessfully loaded the customer advance payments.");
		return "CustomerAdvancePaymentPdf.jasper";
	}
}
