package eulap.eb.web.report.jesper;

import java.time.temporal.ValueRange;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceImportationDetails;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.APInvoiceService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.SupplierService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A class the generate file format for ap invoice form.

 */
@Controller
@RequestMapping ("apInvoiceJR")
public class ApInvoiceJR {
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private SupplierService supplierService;
	
	@RequestMapping (value="/pdf", method = RequestMethod.GET)
	private String showFormPdf (@RequestParam (value="pId", required = true)Integer pId,
			Model model, HttpSession session) throws Exception{
		commonGeneration(model, pId, "pdf", session);
		return "ApInvoicePdf.jasper";
	}

	@RequestMapping (value="/html", method = RequestMethod.GET)
	private String showFormHtml (@RequestParam (value="pId", required = true)Integer pId,
			Model model, HttpSession session) throws Exception{
		commonGeneration(model, pId, "html", session);
		return "ApInvoiceHTML.jasper";
	}

	private void commonGeneration(Model model, Integer pId, String format, HttpSession session) throws Exception{
		APInvoice apInvoice = apInvoiceService.getInvoice(pId);
		InvoiceImportationDetails importationDetails = apInvoice.getInvoiceImportationDetails();
		int invoiceType = apInvoice.getInvoiceTypeId();
		String title = "PAYMENT VOUCHER - NON PO";
		ValueRange rangeConf = ValueRange.of(InvoiceType.API_CONF_CENTRAL, InvoiceType.API_CONF_NSB8A);
		if (rangeConf.isValidValue(invoiceType)) {
			title = "PAYMENT VOUCHER  - CONFIDENTIAL";
		}
		ValueRange importationRange = ValueRange.of(InvoiceType.API_IMPORT_CENTRAL, InvoiceType.API_IMPORT_NSB8A);
		boolean isImportation = importationRange.isValidIntValue(invoiceType);
		if (isImportation) {
			title = "PAYMENT VOUCHER  - IMPORTATION";
		}
		ValueRange loanRange = ValueRange.of(InvoiceType.AP_LOAN_CENTRAL, InvoiceType.AP_LOAN_NSB8A);
		boolean isLoan = loanRange.isValidIntValue(invoiceType);
		if (isLoan) {
			title = "AP LOAN";
		}
		JRDataSource dataSource = new JRBeanCollectionDataSource(apInvoiceService.getInvoicePrintoutDtos(pId, invoiceType));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format);
		// Set supplier info
		Supplier supplier = apInvoice.getSupplier();
		String name = "";
		if(supplier.getBussinessClassificationId().equals(1)) {
			if(!supplier.getLastName().isEmpty() && !supplier.getFirstName().isEmpty()) {
				name = supplier.getLastName()+", "+ supplier.getFirstName();
				if(!supplier.getMiddleName().isEmpty()) {
					name = supplier.getLastName()+", "+ supplier.getFirstName() +" "+ supplier.getMiddleName();
				}
			}
		} else {
			name = supplier.getName();
		}
		model.addAttribute("supplier", supplier.getName());
		model.addAttribute("supplierFullname", name);
		model.addAttribute("address", supplierService.processSupplierAddress(supplier));
		model.addAttribute("tin", StringFormatUtil.processTin(supplier.getTin()));
		model.addAttribute("description", apInvoice.getDescription());
		model.addAttribute("invoiceNo", apInvoice.getInvoiceNumber());
		model.addAttribute("sequenceNumber", apInvoice.getSequenceNumber());
		model.addAttribute("glDate", DateUtil.formatDate(apInvoice.getGlDate()));
		model.addAttribute("birLogo", "bir_logo.png");
		model.addAttribute("currency", apInvoice.getCurrency().getName());
		model.addAttribute("division", apInvoice.getDivision().getName());
		model.addAttribute("referenceNo", apInvoice.getReferenceNo());
		model.addAttribute("bmsNumber", apInvoice.getBmsNumber());
		model.addAttribute("amount", apInvoice.getAmount());
		model.addAttribute("title", title);
		model.addAttribute("isImportation", isImportation);
		model.addAttribute("isLoan", isLoan);
		model.addAttribute("principalLoan", apInvoice.getPrincipalLoan());
		model.addAttribute("principalPayment", apInvoice.getPrincipalPayment());
		model.addAttribute("supplierAccount" , apInvoice.getSupplierAccount().getName());
		if(!isLoan) {//These data are not in ap loan.
			model.addAttribute("invoiceDate", DateUtil.formatDate(apInvoice.getInvoiceDate()));
			model.addAttribute("dueDate", DateUtil.formatDate(apInvoice.getDueDate()));
		}
		if (importationDetails != null) {
			model.addAttribute("importEntryNo", importationDetails.getImportEntryNo());
			model.addAttribute("assessmentReleaseDate", importationDetails.getAssessmentReleaseDate() != null
					? DateUtil.formatDate(importationDetails.getAssessmentReleaseDate()) : null);
			model.addAttribute("registeredName", importationDetails.getRegisteredName());
			model.addAttribute("importationDate", importationDetails.getImportationDate() != null
					? DateUtil.formatDate(importationDetails.getImportationDate()) : null);
			model.addAttribute("countryOfOrigin", importationDetails.getCountryOfOrigin());
			model.addAttribute("totalLandedCost", importationDetails.getTotalLandedCost());
			model.addAttribute("dutiableValue", importationDetails.getDutiableValue());
			model.addAttribute("chargesFromCustom", importationDetails.getChargesFromCustom());
			model.addAttribute("taxableImport", importationDetails.getTaxableImport());
			model.addAttribute("exemptImport", importationDetails.getExemptImport());
			model.addAttribute("orNumber", importationDetails.getOrNumber());
			model.addAttribute("paymentDate", importationDetails.getPaymentDate() != null
					? DateUtil.formatDate(importationDetails.getPaymentDate()) : null);
		}
		String supplierTin = supplier.getTin();
		model.addAttribute("tin", supplierTin != null && !supplierTin.isEmpty()
				? StringFormatUtil.processBirTinTo13Digits(supplierTin) : "");

		// Set company info
		Company company = companyService.getCompany(apInvoice.getCompanyId());
		String companyLogo = company.getLogo();
		if (format == "xls") {
			companyLogo = session.getServletContext().getContextPath() + "/images/logo/" + company.getLogo();
		}
		model.addAttribute("companyLogo", companyLogo);
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		String tin = company.getTin();
		model.addAttribute("companyTin", tin != null && !tin.isEmpty()
				? StringFormatUtil.processBirTinTo13Digits(tin) : "");

		if (apInvoice.getCreatedDate() != null) {
			model.addAttribute("preparedDate", DateUtil.formatDate(apInvoice.getCreatedDate()));
		}
		FormWorkflow formWorkflow = apInvoice.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("preparedBy",  
						user.getUserFullName());
				model.addAttribute("preparedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.REVIEWED_ID) {
				model.addAttribute("approvedBy",  
						user.getUserFullName());
				model.addAttribute("approvedPosition", position.getName());
				model.addAttribute("approvedDate", DateUtil.formatDate(log.getCreatedDate()));
			}
		}
	}
}
