package eulap.eb.web.report.jesper;

import java.util.ArrayList;
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

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.service.DivisionService;
import eulap.eb.service.RReturnToSupplierService;
import eulap.eb.service.SupplierService;
import eulap.eb.web.dto.ReturnToSupplierItemsDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
/**
 * A class the generate PDF file format of retail return to supplier.

 */
@Controller
@RequestMapping("retailReturnToSupplierPDF")
public class RReturnToSupplierJR {
	private static final Logger logger = Logger.getLogger(RReturnToSupplierJR.class);
	private static final String RRTS_REPORT_TITLE = "RETURN TO SUPPLIER";
	@Autowired
	private RReturnToSupplierService rtsService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private SupplierService supplierService;

	@RequestMapping(value="/pdf/{divisionId}", method = RequestMethod.GET)
	private String showReport (@RequestParam(value="pId", required=true)Integer pId,
			Model model, HttpSession session,
			@PathVariable (value="divisionId") int divisionId){
		getParams(pId, model, "pdf", session, divisionId);
		logger.info("Sucessfully loaded the return to supplier.");
		return "R_ReturnToSupplierForm.jasper";
	}

	private void getParams(Integer pId, Model model, String format, HttpSession session, Integer divisionId) {
		if (pId != null) {
			logger.info("Loading return to supplier pdf format");
			logger.debug("Loading return to supplier with an id " + pId);
			APInvoice apInvoice = rtsService.getRtsApInvoice(pId);
			logger.debug("Retreiving items of return to supplier id " + pId);
			List<ReturnToSupplierItemsDto> itemsDtos = new ArrayList<ReturnToSupplierItemsDto>();
			ReturnToSupplierItemsDto itemsDto = new ReturnToSupplierItemsDto();
			itemsDto.setApInvoiceLines(apInvoice.getApInvoiceLines());
			itemsDto.setRtsItems(apInvoice.getRtsItems());
			itemsDto.setSerialItems(apInvoice.getSerialItems());
			itemsDtos.add(itemsDto);
			JRDataSource dataSource = new JRBeanCollectionDataSource(itemsDtos);
			model.addAttribute("datasource", dataSource);
			model.addAttribute("format", format);
			logger.debug("Successfully generated the datasource.");

			RReturnToSupplier rts = rtsService.getRtsByInvoiceId(pId);
			String companyLogo = rts.getCompany().getLogo();
			if (format == "xls") {
				companyLogo = session.getServletContext().getContextPath() +"/images/logo/"+ rts.getCompany().getLogo();
			}
			setTaxDetails(model, itemsDto);
			model.addAttribute("companyLogo", companyLogo);
			model.addAttribute("companyName", rts.getCompany().getName());
			model.addAttribute("companyAddress", rts.getCompany().getAddress());
			model.addAttribute("reportTitle" , RRTS_REPORT_TITLE);
			model.addAttribute("amount", apInvoice.getAmount());
			model.addAttribute("rtsNumber", apInvoice.getSequenceNumber());
			model.addAttribute("rtsDate", apInvoice.getGlDate());
			model.addAttribute("invoiceDate", apInvoice.getInvoiceDate());
			model.addAttribute("supplierInvoiceNo", apInvoice.getInvoiceNumber());
			model.addAttribute("wtAcctSetting", apInvoice.getWtAcctSetting() != null
					? apInvoice.getWtAcctSetting().getName() : "");
			model.addAttribute("withholdingTax", apInvoice.getWtAmount());
			model.addAttribute("bmsNumber", apInvoice.getBmsNumber());
			model.addAttribute("poNumber", apInvoice.getPoNumber());
			model.addAttribute("strInvoiceDate", apInvoice.getStrInvoiceDate());
			Supplier supplier = apInvoice.getSupplier();
			if(supplier != null) {
				model.addAttribute("supplier", supplier.getName());
				model.addAttribute("supplierAddress", supplierService.processSupplierAddress(supplier));
				model.addAttribute("tin", StringFormatUtil.processBirTinTo13Digits(supplier.getTin()));
			}
			model.addAttribute("currency", apInvoice.getCurrency().getName());
			Division division = divisionService.getDivision(divisionId);
			model.addAttribute("division", division.getName());
			FormWorkflow workflowLog =  apInvoice.getFormWorkflow();
			for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
				if (log.getFormStatusId() == FormStatus.CREATED_ID){
					String name = log.getCreated().getUserFullName();
					String position = log.getCreated().getPosition().getName();
					model.addAttribute("createdBy", name);
					model.addAttribute("creatorPosition", position);
				}
				if (log.getFormStatusId() == FormStatus.REVIEWED_ID){
					String name = log.getCreated().getUserFullName();
					String position = log.getCreated().getPosition().getName();
					model.addAttribute("approvedBy", name);
					model.addAttribute("approverPosition", position);
				}
			}
			logger.debug("Successfully generated the parameters for the report.");
		}else{
			logger.error("Receiving report id required.");
			throw new RuntimeException("Return to supplier id is required.");
		}
	}

	private void setTaxDetails(Model model, ReturnToSupplierItemsDto itemsDto) {
		double subTotal = 0;
		double totalVat = 0;
		double amountDue = 0;
		for (RReturnToSupplierItem item : itemsDto.getRtsItems()) {
			double vatAmount = item.getVatAmount() != null ? NumberFormatUtil.roundOffTo2DecPlaces(item.getVatAmount()) : 0;
			double discount = item.getDiscount() != null ? NumberFormatUtil.roundOffTo2DecPlaces(item.getDiscount()) : 0;
			double sub = NumberFormatUtil.roundOffTo2DecPlaces(item.getUnitCost() * item.getQuantity() - (vatAmount)-discount);
			subTotal += sub;
			totalVat += vatAmount;
			amountDue += vatAmount+sub;
		}
		for (SerialItem item : itemsDto.getSerialItems()) {
			double vatAmount = item.getVatAmount() != null ? NumberFormatUtil.roundOffTo2DecPlaces(item.getVatAmount()) : 0;
			double discount = item.getDiscount() != null ? NumberFormatUtil.roundOffTo2DecPlaces(item.getDiscount()) : 0;
			double sub = NumberFormatUtil.roundOffTo2DecPlaces(item.getUnitCost() * item.getQuantity() - (vatAmount)-discount);
			subTotal += sub;
			totalVat += vatAmount;
			amountDue += vatAmount+sub;
		}
		for (ApInvoiceLine item : itemsDto.getApInvoiceLines()) {
			double vatAmount = item.getVatAmount() != null ? NumberFormatUtil.roundOffTo2DecPlaces(item.getVatAmount()) : 0;
			double discount = item.getDiscount() != null ? NumberFormatUtil.roundOffTo2DecPlaces(item.getDiscount()) : 0;
			double sub = NumberFormatUtil.roundOffTo2DecPlaces(item.getUpAmount() * item.getQuantity() - (vatAmount)-discount);
			subTotal += sub;
			totalVat += vatAmount;
			amountDue += vatAmount+sub;
		}
		model.addAttribute("subTotal", subTotal);
		model.addAttribute("totalVat", totalVat);
		model.addAttribute("amountDue", amountDue);
	}
}
