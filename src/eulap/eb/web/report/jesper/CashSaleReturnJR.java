package eulap.eb.web.report.jesper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnArLine;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.CashSaleReturnIsService;
import eulap.eb.service.CashSaleReturnItemService;
import eulap.eb.service.CashSaleReturnService;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.SaleItemUtil;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A class the generate PDF file format of retail Cash Sales Return.

 */
@Controller
@RequestMapping("cashSaleReturnPDF")
public class CashSaleReturnJR {
	private static Logger logger =  Logger.getLogger(CashSaleReturnJR.class);
	private final static String REPORT_TITLE = "RETURN/EXCHANGE";
	@Autowired
	private CashSaleReturnService cashSaleReturnService;
	@Autowired
	private CashSaleReturnIsService cashSaleReturnIsService;
	@Autowired
	private CashSaleReturnItemService cashSaleReturnItemService;
	@Autowired
	private CashSaleService cashSaleService;

	@RequestMapping(value="{typeId}/pdf", method = RequestMethod.GET)
	private String showReport (@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId, Model model){
		if(pId != null) {
			logger.info("Loading the cash sales return pdf format.");
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(pId, typeId));
			List<CashSaleReturnArLine> arLines = cashSaleReturnService.getCsrArLine(pId);
			List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
			otherCharges.addAll(arLines);
			List<AROtherCharge> processedList = cashSaleService.getDetailOtherCharges(otherCharges);
			List<CashSaleReturnArLine> processedArLines = new ArrayList<CashSaleReturnArLine>();
			Double totalArLine = 0.00;
			for (AROtherCharge oc : processedList) {
				processedArLines.add((CashSaleReturnArLine) oc);
				totalArLine += oc.getAmount();
			}
			model.addAttribute("totalArLine", totalArLine);
			model.addAttribute("arLines", processedArLines);
			getCommonParams(model, pId, null, dataSource, "pdf");
		} else {
			logger.error("Cash sale return id required.");
			throw new RuntimeException("Cash sale return id is required.");
		}
		logger.info("Sucessfully loaded the cash sales return printout.");
		if(typeId == CashSaleType.INDIV_SELECTION) {
			return "CashSaleReturnISForm.jasper";
		}
		return "CashSaleReturnForm.jasper";
	}

	private List<CashSaleReturnItem> getDataSource(int pId, int typeId) {
		logger.debug("Loading cash sales return with an id " + pId);
		List<CashSaleReturnItem> csrItems = null;
		if(typeId == CashSaleType.INDIV_SELECTION) {
			csrItems = cashSaleReturnIsService.getCsrItems(pId);
			SaleItemUtil<CashSaleReturnItem> saleItemUtil = new SaleItemUtil<>();
			csrItems = saleItemUtil.generateSaleItemPrintout(csrItems);
		} else {
			csrItems = cashSaleReturnItemService.getCSRItemsPrintOut(pId);
		}
		return csrItems;
	}

	private void getCommonParams(Model model, int pId, Integer fontSize, JRDataSource dataSource, String format) {
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format);

		CashSaleReturn cashSaleReturn =   cashSaleReturnService.getCashSaleReturn(pId);
		model.addAttribute("companyLogo", cashSaleReturn.getCompany().getLogo());
		model.addAttribute("companyName", cashSaleReturn.getCompany().getName());
		model.addAttribute("companyAddress", cashSaleReturn.getCompany().getAddress());
		model.addAttribute("reportTitle" , REPORT_TITLE);

		model.addAttribute("customerName", cashSaleReturn.getArCustomer().getName());
		model.addAttribute("customerAcct", cashSaleReturn.getArCustomerAccount().getName());
		model.addAttribute("address", cashSaleReturn.getArCustomer().getAddress());
		model.addAttribute("salesInvoiceNo", cashSaleReturn.getSalesInvoiceNo());
		model.addAttribute("csrNumber", cashSaleReturn.getFormattedCSRNumber());
		model.addAttribute("date", DateUtil.removeTimeFromDate(cashSaleReturn.getDate()));
		model.addAttribute("term", cashSaleReturn.getArCustomerAccount().getTerm().getName());
		model.addAttribute("total", cashSaleReturn.getTotalAmount());
		SaleItemUtil.getFontSize(fontSize, model);
		String formLabel =  cashSaleReturnService.getCsrLabel(cashSaleReturn.getCashSaleTypeId());
		model.addAttribute("formLabel", formLabel+"No.");
		String formattedCsrNo = formLabel + cashSaleReturn.getFormattedCSRNumber();
		model.addAttribute("sequenceNo", formattedCsrNo);
		double subTotal = cashSaleReturn.getTotalAmount();
		model.addAttribute("total", subTotal);
		double totalItemVat = cashSaleReturnService.getTotalItemVat(pId);
		model.addAttribute("totalVat", totalItemVat);

		double totalAmountDue = subTotal + totalItemVat;
		WithholdingTaxAcctSetting wtax = cashSaleReturn.getWtAcctSetting();
		if (wtax != null) {
			double wtAmount = cashSaleReturn.getWtAmount() != null ? cashSaleReturn.getWtAmount() : 0;
			totalAmountDue -= wtAmount;
			model.addAttribute("wtAmount", wtAmount);
			model.addAttribute("wtAcctSetting", wtax.getName());
		}
		model.addAttribute("totalAmountDue", totalAmountDue);

		boolean isReturnOfReturn = cashSaleReturn.getRefCashSaleReturnId() != null;
		CashSaleReturn saleReturnRef = new CashSaleReturn();
		if(isReturnOfReturn){
			saleReturnRef = cashSaleReturnService.getCashSaleReturn(cashSaleReturn.getRefCashSaleReturnId());
		}
		model.addAttribute("referenceNo", saleReturnRef.getFormattedCSRNumber() == null ? cashSaleReturnService
				.getReferenceCs(cashSaleReturn.getCashSaleId()) : formLabel + saleReturnRef.getFormattedCSRNumber());

		FormWorkflow formWorkflow = cashSaleReturn.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("preparedBy",
						user.getLastName() + " " + user.getFirstName());
				model.addAttribute("preparedPosition", position.getName());
			}
			if(log.getFormStatusId() == FormStatus.RECEIVED_ID) {
				model.addAttribute("receivedBy",
						user.getLastName() + " " + user.getFirstName());
				model.addAttribute("receivedPosition", position.getName());
			}
		}
	}

	@RequestMapping(value="{typeId}/html", method = RequestMethod.GET)
	private String showHTMLReport (@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId, Model model){
		if(pId != null) {
			logger.info("Loading the cash sales return pdf format.");
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(pId, typeId));
			List<CashSaleReturnArLine> arLines = cashSaleReturnService.getCsrArLine(pId);
			List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
			otherCharges.addAll(arLines);
			List<AROtherCharge> processedList = cashSaleService.getDetailOtherCharges(otherCharges);
			List<CashSaleReturnArLine> processedArLines = new ArrayList<CashSaleReturnArLine>();
			Double totalArLine = 0.00;
			for (AROtherCharge oc : processedList) {
				processedArLines.add((CashSaleReturnArLine) oc);
				totalArLine += oc.getAmount();
			}
			model.addAttribute("totalArLine", totalArLine);
			model.addAttribute("arLines", processedArLines);
			getCommonParams(model, pId, null, dataSource, "html");
		} else {
			logger.error("Cash sale return id required.");
			throw new RuntimeException("Cash sale return id is required.");
		}
		logger.info("Sucessfully loaded the cash sales return printout.");
		return "CashSaleReturnFormHTML.jasper";
	}
}
