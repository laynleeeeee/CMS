package eulap.eb.web.report;

import javax.servlet.http.HttpSession;

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
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.InvoiceTypeService;
import eulap.eb.service.report.ApInvoiceAgingParam;
import eulap.eb.service.report.ApInvoiceAgingService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * A controller class that handles the invoice aging report generation.

 */
@Controller
@RequestMapping("invoiceAging")
public class ApInvoiceAgingController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private InvoiceTypeService invoiceTypeService;
	@Autowired
	private ApInvoiceAgingService serviceImpl;
	@Autowired
	private DivisionService divisionService;
	private final static String REPORT_TITLE = "INVOICE AGING";

	@RequestMapping (method = RequestMethod.GET)
	public String showInvoiceAging(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user, model);
		return "ApInvoiceAgingReport.jsp";
	}

	private void loadSelections(User user, Model model) {
		model.addAttribute("invoiceTypes", invoiceTypeService.getAllInvoiceTypes(user));
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="typeId") int typeId,
			@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=false) int divisionId,
			@RequestParam (value="invoiceTypeId", required=false, defaultValue="-1") int invoiceTypeId,
			@RequestParam (value="supplierId", required=false, defaultValue="-1") int supplierId,
			@RequestParam (value="supplierAccountId", required=false, defaultValue="-1") int supplierAccountId,
			@RequestParam (value="showInvoices", required=false, defaultValue="true") boolean showInvoices,
			@RequestParam (value="ageBasis", required=false, defaultValue="1") int ageBasis,
			@RequestParam (value="asOfDate", required=false, defaultValue="1") String asOfDate,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session){
		ApInvoiceAgingParam param = new ApInvoiceAgingParam();
		param.setCompanyId(companyId);
		param.setDivisionId(divisionId);
		param.setInvoiceTypeId(invoiceTypeId);
		param.setSupplierId(supplierId);
		param.setSupplierAccountId(supplierAccountId);
		param.setAgeBasis(ageBasis);
		param.setShowInvoices(showInvoices);
		param.setTypeId(typeId);
		if(typeId == 2) {
			// Only the second version of Invoice Aging have As of Date
			param.setAsOfDate(DateUtil.parseDate(asOfDate));
		}
		JRDataSource dataSource = serviceImpl.generateInvoiceAging(param);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		if(showInvoices){
			return "InvoiceAgingWithInvoices.jasper";
		}else{
			return "InvoiceAgingWithOutInvoices.jasper";
		}
	}
}
