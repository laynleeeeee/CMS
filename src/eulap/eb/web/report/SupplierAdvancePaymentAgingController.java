package eulap.eb.web.report;

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
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.report.SupplierAdvancePaymentAgingParam;
import eulap.eb.service.report.SupplierAdvancePaymentAgingService;
import net.sf.jasperreports.engine.JRDataSource;

/**
* A controller class that handles Supplier Advance Payment Aging

*/

@Controller
@RequestMapping("supplierAdvancePaymentAging")
public class SupplierAdvancePaymentAgingController{
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private SupplierAdvancePaymentAgingService serviceImpl;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private SupplierAdvancePaymentAgingService supplierAdvancePaymentService;
	private final static String REPORT_TITLE = "SUPPLIER ADVANCE PAYMENT AGING";


	@RequestMapping (method = RequestMethod.GET)
	public String showSupplierAdvancePaymentAging(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivisions(0));
		model.addAttribute("suppliers",supplierService.getSuppliers(user));
		model.addAttribute("formStatuses", supplierAdvancePaymentService.getFormStatuses(user));
		return "SupplierAdvancePaymentAgingReport.jsp";
	}

	@RequestMapping(value = "/generateReport",method = RequestMethod.GET)
	public String generateReportPDFexced (@RequestParam(value="companyId", required=true) Integer companyId,
			@RequestParam(value="divisionId",required=false,defaultValue="-1") Integer divisionId,
			@RequestParam(value="supplierId",required=false,defaultValue="-1") Integer supplierId,
			@RequestParam(value="supplierAcctId",required=false,defaultValue="-1") Integer supplierAcctId,
			@RequestParam(value="bmsNumber",required=false,defaultValue="-1") String bmsNumber,
			@RequestParam(value="dateTo",required=false) Date dateTo,
			@RequestParam(value="ageBasis",required=false,defaultValue="1") int ageBasis,
			@RequestParam(value="statusId",required=false,defaultValue="-1") Integer statusId,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) {
		SupplierAdvancePaymentAgingParam param = new SupplierAdvancePaymentAgingParam();
		param.setCompanyId(companyId);
		param.setDivisionId(divisionId);
		param.setSupplierId(supplierId);
		param.setSupplierAcctId(supplierAcctId);
		param.setBmsNumber(bmsNumber);
		param.setDateTo(dateTo);
		param.setAgeBasis(ageBasis);
		param.setStatusId(statusId);
		JRDataSource dataSource = serviceImpl.generateSupplierAdvancePaymentAging(param);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		company = null;
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("dateTo" , DateUtil.formatDate(dateTo));
		model.addAttribute("ageBasis", param.getAgeBasisDescription());
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "SupplierAdvancePaymentAging.jasper";
	}
}

