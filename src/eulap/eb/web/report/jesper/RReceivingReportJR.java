package eulap.eb.web.report.jesper;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.service.APInvoiceService;
import eulap.eb.service.RReceivingReportService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A class the generate PDF file format of retail receiving report.

 */
@Controller
@RequestMapping("retailReceivingReportPDF")
public class RReceivingReportJR {
	private final static String SLIP_TITLE = "RECEIVING REPORT - IN SLIP";
	private static Logger logger = Logger.getLogger(RReceivingReportJR.class);
	private final static String REPORT_TITLE = "Receiving Report";
	@Autowired
	private RReceivingReportService reportService;
	@Autowired
	private APInvoiceService invoiceService;

	@RequestMapping(value="{typeId}/pdf", method = RequestMethod.GET)
	private String showReport (@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId,
			Model model, HttpSession session) throws IOException{
		getParams(pId, model, "pdf", session);
		return "R_ReceivingReportForm.jasper";
	}

	private void getCommonParams(Model model, RReceivingReport receivingReport, APInvoice invoice,
			String format, HttpSession session) {
		model.addAttribute("format", format);
		model.addAttribute("slipTitle", SLIP_TITLE);
		String companyLogo = receivingReport.getCompany().getLogo();
		if (format == "xls") {
			companyLogo = session.getServletContext().getContextPath() + "/images/logo/" + receivingReport.getCompany().getLogo();
		}
		model.addAttribute("companyLogo", companyLogo);
		model.addAttribute("companyName", receivingReport.getCompany().getName());
		model.addAttribute("companyAddress", receivingReport.getCompany().getAddress());
		model.addAttribute("companyTin", receivingReport.getCompany().getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("rrNumber", invoice.getSequenceNumber());
		model.addAttribute("invoiceDate", invoice.getInvoiceDate());
		model.addAttribute("rrDate", invoice.getGlDate());
		model.addAttribute("supplier", invoice.getSupplier().getName());
		model.addAttribute("supplierAddress", invoice.getSupplier().getAddress());
		model.addAttribute("supplierTin", invoice.getSupplier().getTin());
		model.addAttribute("supplierInvoiceNo", invoice.getInvoiceNumber());
		model.addAttribute("deliveryReceiptNo", receivingReport.getDeliveryReceiptNo());
		model.addAttribute("division", invoice.getDivision().getName());
		model.addAttribute("bmsNumber", receivingReport.getBmsNumber());
		model.addAttribute("remarks", receivingReport.getRemarks());
		model.addAttribute("warehouse", receivingReport.getWarehouse().getName());

		FormWorkflow workflowLog = invoice.getFormWorkflow();
		for (FormWorkflowLog log: workflowLog.getFormWorkflowLogs()) {
			if (log.getFormStatusId() == FormStatus.CREATED_ID){
				String name = log.getCreated().getUserFullName();
				String position = log.getCreated().getPosition().getName();
				model.addAttribute("createdBy", name);
				model.addAttribute("creatorPosition", position);
			}
			if (log.getFormStatusId() == FormStatus.CHECKED_ID) {
				String name = log.getCreated().getUserFullName();
				String position = log.getCreated().getPosition().getName();
				model.addAttribute("checkedBy", name);
				model.addAttribute("checkerPosition", position);
			}
		}
		logger.debug("Successfully generated the parameters for the report.");
	}

	private void getParams(Integer pId, Model model, String format, HttpSession session) throws IOException {
		if (pId != null) {
			logger.info("Loading receiving report pdf format");
			logger.debug("Loading receiving report with an id " + pId);
			APInvoice invoice = invoiceService.processReceivingReportAndItems(pId);
			JRDataSource dataSource = new JRBeanCollectionDataSource(reportService.getInvoicePrintoutDtos(invoice));
			logger.debug("Generated the dataSource.");
			model.addAttribute("datasource", dataSource);
			logger.debug("Retreiving items of receiving report id " + pId );
			List<RReceivingReportItem> receivingReportItems = invoice.getRrItems();
			List<ApInvoiceLine> apInvoiceLines = invoice.getApInvoiceLines();
			List<SerialItem> serialItems = invoice.getReceivingReport().getSerialItems();
			model.addAttribute("subTotal", reportService.getSubTotal(receivingReportItems, apInvoiceLines, serialItems));
			model.addAttribute("totalVat", reportService.getTotalVat(receivingReportItems, apInvoiceLines, serialItems));
			getCommonParams(model, reportService.getRrByInvoiceId(pId), invoice, format, session);
		} else {
			logger.error("Receiving report id required.");
			throw new RuntimeException("Receiving report id is required.");
		}
		logger.info("Sucessfully loaded the receiving report.");
	}

	@RequestMapping(value="/html", method = RequestMethod.GET)
	private String showHTMLReport (@RequestParam(value="pId", required=true) Integer pId,
			Model model, HttpSession session) throws IOException{
		getParams(pId, model, "html", session);
		return "ReceivingReportFormHTML.jasper";
	}
}
