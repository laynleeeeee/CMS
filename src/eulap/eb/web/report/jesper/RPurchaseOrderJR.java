package eulap.eb.web.report.jesper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RPurchaseOrderItem;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.RPurchaseOrderService;

/**
 * Controller class for generating the printout of Retail - PO.

 *
 */
@Controller
@RequestMapping("/retailPurchaseOrderPDF")
public class RPurchaseOrderJR {
	private static Logger logger = Logger.getLogger(RPurchaseOrderJR.class);
	@Autowired
	private RPurchaseOrderService pOrderService;
	@Autowired
	private CurrencyService currencyService;

	@RequestMapping(value="/pdf", method=RequestMethod.GET)
	public String generatePrintout(@RequestParam(value="pId") int pId, Model model, HttpSession session) throws IOException {
		logger.info("Generating the printout of Retail - PO: "+pId);
		RPurchaseOrder rPurchaseOrder = pOrderService.getRpurchaseOrder(pId);
		getParams(rPurchaseOrder, model, "pdf", session);
		model.addAttribute("term", rPurchaseOrder.getTerm().getName());
		return "RPurchaseOrderPDF.jasper";
	}

	private void getParams(RPurchaseOrder rPurchaseOrder, Model model, String format, HttpSession session) {
		List<RPurchaseOrderItem> rPOItems = rPurchaseOrder.getrPoItems();
		JRDataSource dataSource = new JRBeanCollectionDataSource(rPOItems);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format);
		logger.debug("Successfully generated the datasource of the printout.");

		Company company = rPurchaseOrder.getCompany();
		if(format == "pdf") {
			model.addAttribute("companyLogo", company.getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ company.getLogo());
		}
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("supplier", rPurchaseOrder.getSupplier().getName());
		model.addAttribute("supplierAccount", rPurchaseOrder.getSupplierAccount().getName());
		model.addAttribute("poNumber", rPurchaseOrder.getFormattedPONumber());
		model.addAttribute("poDate", rPurchaseOrder.getPoDate());
		model.addAttribute("remarks", rPurchaseOrder.getRemarks());

		FormWorkflow workflowLog = rPurchaseOrder.getFormWorkflow();
		for (FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				String name = log.getCreated().getUserFullName();
				model.addAttribute("createdBy", name);
				model.addAttribute("createdByPosition", log.getCreated().getPosition().getName());
			}
		}
		logger.debug("Successfully generated the parameters for the printout.");
		logger.info("Successfully generated the printout.");
	}

	@RequestMapping(value="/html", method=RequestMethod.GET)
	public String generateHTMLPrintout(@RequestParam(value="pId") int pId, Model model, HttpSession session) throws IOException {
		logger.info("Generating the printout of Retail - PO: "+pId);
		RPurchaseOrder rPurchaseOrder = pOrderService.getRpurchaseOrder(pId);
		getParams(rPurchaseOrder, model, "html", session);
		return "RPurchaseOrderPDFHTML.jasper";
	}

	@RequestMapping(value="/nsbFormat", method=RequestMethod.GET)
	public String generateNSBPrintout(@RequestParam(value="pId") int pId, Model model, HttpSession session) throws IOException {
		RPurchaseOrder purchaseOrder = pOrderService.getPurchaseOrder(pId);
		List<RPurchaseOrder> purchaseOrders = new ArrayList<RPurchaseOrder>();
		purchaseOrders.add(purchaseOrder);
		JRDataSource dataSource = new JRBeanCollectionDataSource(purchaseOrders);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");

		// Set header fields
		model.addAttribute("poNumber", "PO #" + purchaseOrder.getPoNumber());
		Supplier supplier = purchaseOrder.getSupplier();
		model.addAttribute("supplier", supplier.getName());
		model.addAttribute("supplierAddr", supplier.getStreetBrgy() + ", " + supplier.getCityProvince());
		model.addAttribute("supplierTelNo", supplier.getContactNumber());
		model.addAttribute("contactPerson", supplier.getContactPerson());
		supplier = null;
		model.addAttribute("bmsNumber", purchaseOrder.getBmsNumber());
		model.addAttribute("deliveryDate", purchaseOrder.getEstDeliveryDate());
		model.addAttribute("remarks",purchaseOrder.getRemarks());
		model.addAttribute("poDate", purchaseOrder.getPoDate());
		model.addAttribute("requestor", purchaseOrder.getRequesterName());
		model.addAttribute("termName", purchaseOrder.getTerm().getName());
		model.addAttribute("totalNetOfVAT", purchaseOrder.getTotalNetOfVAT());
		model.addAttribute("totalVAT", purchaseOrder.getTotalVAT());
		//Only provide the currency name if the currency id is not equal to the default PHP id.
		if(!currencyService.isPhp(purchaseOrder.getCurrencyId())) {
			model.addAttribute("currency", purchaseOrder.getCurrency().getName());
		}

		FormWorkflow workflowLog = purchaseOrder.getFormWorkflow();
		for (FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			String name = log.getCreated().getUserFullName();
			String position = log.getCreated().getPosition().getName();
			String date = DateUtil.formatDate(log.getCreatedDate());
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", name);
				model.addAttribute("creatorPosition", position);
				model.addAttribute("createdDate", date);
			} else if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy", name);
				model.addAttribute("approverPosition", position);
				model.addAttribute("approvedDate", date);
			}
		}
		return "NSBPurchaseOrderPdfMatrix.jasper";
	}
}
