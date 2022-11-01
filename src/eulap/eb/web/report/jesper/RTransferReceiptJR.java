package eulap.eb.web.report.jesper;

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

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.domain.hibernate.RTransferReceiptItem;
import eulap.eb.domain.hibernate.TransferReceiptType;
import eulap.eb.service.RTransferReceiptService;
import eulap.eb.service.TransferReceiptISService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller class for generating the printout ok Retail - TR.

 *
 */
@Controller
@RequestMapping("/retailTransferReceiptPDF")
public class RTransferReceiptJR {
	private static Logger logger = Logger.getLogger(RTransferReceiptJR.class);
	@Autowired
	private RTransferReceiptService trService;
	@Autowired
	private TransferReceiptISService trIsService;

	@RequestMapping(value="{typeId}/pdf", method=RequestMethod.GET)
	public String generatePrintout(@PathVariable("typeId") Integer typeId,
			@RequestParam(value="pId") int pId, Model model, HttpSession session) {
		getParams(typeId, pId, model, "pdf", session);
		if(typeId.equals(TransferReceiptType.INDIVIDUAL_SELECTION)) {
			return "RTransferReceiptIsPDF.jasper";
		}
		return "RTransferReceiptPDF.jasper";
	}

	private void getParams(Integer typeId, Integer pId, Model model, String format, HttpSession session) {
		logger.info("Generating the printout of transfer receipt: "+pId);
		RTransferReceipt rTransferReceipt = trService.getRTransferReceipt(pId);
		List<RTransferReceiptItem> rTRItems = rTransferReceipt.getrTrItems();
		rTRItems = typeId.equals(TransferReceiptType.INDIVIDUAL_SELECTION) ? trIsService.processTrItems(rTRItems) : rTRItems;
		JRDataSource dataSource = new JRBeanCollectionDataSource(rTRItems);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format);
		logger.debug("Successfully generated the datasource of the printout.");

		Company company = rTransferReceipt.getCompany();
		if(format == "pdf") {
			model.addAttribute("companyLogo", company.getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ company.getLogo());
		}

		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("warehouseFrom", rTransferReceipt.getWarehouseFrom().getName());
		model.addAttribute("warehouseTo", rTransferReceipt.getWarehouseTo().getName());
		model.addAttribute("drNumber", rTransferReceipt.getDrNumber());
		model.addAttribute("trNumber", rTransferReceipt.getFormattedTRNumber());
		model.addAttribute("trDate", DateUtil.formatDate(rTransferReceipt.getTrDate()));

		String name = null;
		String position = null;
		FormWorkflow workflow = rTransferReceipt.getFormWorkflow();
		for (FormWorkflowLog log : workflow.getFormWorkflowLogs()) {
			if(log.getFormStatusId() == FormStatus.CHECKED_ID || log.getFormStatusId() == FormStatus.CREATED_ID) {
				name = log.getCreated().getFirstName() + " " + log.getCreated().getLastName();
				position = log.getCreated().getPosition().getName();
				if(log.getFormStatusId() == FormStatus.CREATED_ID) {
					model.addAttribute("createdBy", name);
					model.addAttribute("createdByPosition", position);
					model.addAttribute("createdDate", DateUtil.formatDate(log.getCreatedDate()));
				} else if(log.getFormStatusId() == FormStatus.CHECKED_ID) {
					model.addAttribute("receivedBy", name);
					model.addAttribute("receivedByPosition", position);
					model.addAttribute("receivedDate", DateUtil.formatDate(log.getCreatedDate()));
				}
			}
		}

		logger.warn("Freeing up the memory allocation.");
		rTransferReceipt = null;
		rTRItems = null;
		company = null;
		name = null;
		position = null;
	}

	@RequestMapping(value="{typeId}/html", method=RequestMethod.GET)
	public String generateHTMLPrintout(@PathVariable("typeId") Integer typeId,
			@RequestParam(value="pId") int pId, Model model, HttpSession session) {
		getParams(typeId, pId, model, "html", session);
		return "RTransferReceiptPDFHTML.jasper";
	}
}
