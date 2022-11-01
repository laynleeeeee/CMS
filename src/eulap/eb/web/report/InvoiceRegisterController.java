package eulap.eb.web.report;

import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.InvoiceTypeService;
import eulap.eb.service.TermService;
import eulap.eb.service.report.InvoiceRegisterParam;
import eulap.eb.service.report.InvoiceRegisterServiceImpl;
import eulap.eb.web.dto.PaymentStatus;

/**
 * A controller class that handles the invoice register report generation. 

 *
 */
@Controller
@RequestMapping ("invoiceRegister")
public class InvoiceRegisterController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TermService termService;
	@Autowired
	private InvoiceTypeService invoiceTypeService;
	@Autowired
	private InvoiceRegisterServiceImpl registerServiceImpl;
	@Autowired
	private DivisionService divisionService;
	private final static String REPORT_TITLE = "INVOICE REGISTER";
	
	@RequestMapping (method = RequestMethod.GET)
	public String showInvoiceRegister (Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		// Handles all the necessary parameters for the initial loading.
		loadSelections(user, model);
		return "InvoiceRegisterReport.jsp";
	}

	private void loadSelections(User user, Model model) {
		//Invoice Types
		List<InvoiceType> invoiceTypes = invoiceTypeService.getAllInvoiceTypes(user);
		model.addAttribute("invoiceTypes", invoiceTypes);
		//Terms
		List<Term> terms = termService.getTerms(user);
		model.addAttribute("terms", terms);
		//Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		//Divisions
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		//Payment Status
		List<PaymentStatus> statuses = registerServiceImpl.paymentStatus();
		model.addAttribute("statuses", statuses);
		//Invoice Status
		List<FormStatus> invoiceStatuses = registerServiceImpl.getInvoiceRegisterStatuses(user);
		model.addAttribute("invoiceStatuses", invoiceStatuses);
	}
	
	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=false) int divisionId,
			@RequestParam (value="invoiceTypeId", required=false, defaultValue="-1") int invoiceTypeId,
			@RequestParam (value="supplierId", required=false, defaultValue="-1") int supplierId,
			@RequestParam (value="supplierAccountId", required=false, defaultValue="-1") int supplierAccountId,
			@RequestParam (value="termId", required=false, defaultValue="-1") int termId,
			@RequestParam (value="invoiceNumber", required=false) String invoiceNumber,
			@RequestParam (value="fromInvoiceDate", required=false) String fromInvoiceDate,
			@RequestParam (value="toInvoiceDate", required=false) String toInvoiceDate,
			@RequestParam (value="fromGLDate", required=false) String fromGLDate,
			@RequestParam (value="toGLDate", required=false) String toGLDate,
			@RequestParam (value="fromDueDate", required=false) String fromDueDate,
			@RequestParam (value="toDueDate", required=false) String toDueDate,
			@RequestParam (value="fromAmount", required=false) Double fromAmount,
			@RequestParam (value="toAmount", required=false) Double toAmount,
			@RequestParam (value="fromSequenceNumber", required=false) Integer fromSequenceNumber,
			@RequestParam (value="toSequenceNumber", required=false) Integer toSequenceNumber,
			@RequestParam (value="invoiceStatusId", required=false, defaultValue="-1") int invoiceStatusId,
			@RequestParam (value="paymentStatusId", required=false, defaultValue="-1") int paymentStatusId,
			@RequestParam (value="asOfDate", required=false) String asOfDate,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		InvoiceRegisterParam param = new InvoiceRegisterParam();
		param.setCompanyId(companyId);
		param.setDivisionId(divisionId);
		param.setInvoiceTypeId(invoiceTypeId);
		param.setSupplierId(supplierId);
		param.setSupplierAccountId(supplierAccountId);
		param.setTermId(termId);
		param.setInvoiceNumber(invoiceNumber);
		param.setFromInvoiceDate(DateUtil.parseDate(fromInvoiceDate));
		param.setToInvoiceDate(DateUtil.parseDate(toInvoiceDate));
		param.setFromGLDate(DateUtil.parseDate(fromGLDate));
		param.setToGLDate(DateUtil.parseDate(toGLDate));
		param.setFromDueDate(DateUtil.parseDate(fromDueDate));
		param.setToDueDate(DateUtil.parseDate(toDueDate));
		param.setFromAmount(fromAmount);
		param.setToAmount(toAmount);
		param.setFromSeqNumber(fromSequenceNumber);
		param.setToSeqNumber(toSequenceNumber);
		param.setInvoiceStatus(invoiceStatusId);
		param.setPaymentStatus(paymentStatusId);
		param.setAsOfDate(DateUtil.parseDate(asOfDate));
		JRDataSource dataSource = registerServiceImpl.generateInvoiceRegister(user, param);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("asOfDate" , "as of "+ asOfDate);
		ReportUtil.getPrintDetails(model, user, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "InvoiceRegister.jasper";
	}
}
