package eulap.eb.web.report;

import java.util.Collection;
import java.util.Date;

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
import eulap.eb.service.CompanyService;
import eulap.eb.service.TermService;
import eulap.eb.service.report.InvoiceRegisterServiceImpl;
import eulap.eb.service.report.RRRegisterService;
import net.sf.jasperreports.engine.JRDataSource;
/**
 * Receiving Report Register Controller

 *
 */
@Controller
@RequestMapping("/rrReportRegister")
public class RRReportController {
	private final static String REPORT_TITLE = "RECEIVING REPORT REGISTER";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TermService termService;
	@Autowired
	private RRRegisterService registerService;
	@Autowired
	private InvoiceRegisterServiceImpl registerServiceImpl;

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session){
		model.addAttribute("companies", getStatusCompanies(session));
		model.addAttribute("statuses", registerServiceImpl.paymentStatus());
		model.addAttribute("terms", termService.getAllTerms());
		return "RRRegisterReport.jsp";
	}

	private Collection<Company> getStatusCompanies(HttpSession session) {
		return companyService.getActiveCompanies(
				CurrentSessionHandler.getLoggedInUser(session), null, null, null);
	}
	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="warehouseId", required=true) Integer warehouseId,
			@RequestParam (value="supplierId", required=true) Integer supplierId,
			@RequestParam (value="supplierAcctId", required=true) Integer supplierAcctId,
			@RequestParam (value="rrDateFrom", required=true) Date dateFrom,
			@RequestParam (value="rrDateTo", required=true) Date dateTo,
			@RequestParam (value="termId", required=true) Integer termId,
			@RequestParam (value="amountFrom", required=false) Double amountFrom,
			@RequestParam (value="amountTo", required=false) Double amountTo,
			@RequestParam (value="statusId", required=true) Integer statusId,
			@RequestParam (value="paymentStatId", required=true) Integer paymentStatId,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		JRDataSource dataSource = registerService.getRrRegisterData(companyId, divisionId, warehouseId,
				supplierId, supplierAcctId, dateFrom, dateTo, termId, amountFrom, amountTo, statusId, paymentStatId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		company = null;
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("dateFromAndTo", DateUtil.setUpDate(DateUtil.formatDate(dateFrom), DateUtil.formatDate(dateTo)));
		ReportUtil.getPrintDetails(model, CurrentSessionHandler.getLoggedInUser(session), false);
		return "ReceivingReportRegisterCancellationRemarks.jasper";
	}
}
