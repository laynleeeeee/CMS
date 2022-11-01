package eulap.eb.web.report;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.SupplierAccountService;
import eulap.eb.service.report.InvoiceRegisterServiceImpl;
import eulap.eb.service.report.RTSRegisterService;
import eulap.eb.web.dto.PaymentStatus;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
/**
 * Return To Supplier Register Controller.

 *
 */
@Controller
@RequestMapping("/rtsReportRegister")
public class RTSRegisterReportController {
	private final static String REPORT_TITLE = "RETURN TO SUPPLIER REGISTER";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private RTSRegisterService registerService;
	@Autowired
	private InvoiceRegisterServiceImpl registerServiceImpl;
	@Autowired
	private DivisionService divisionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", getStatusCompanies(session));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		List<PaymentStatus> statuses = registerServiceImpl.paymentStatus();
		model.addAttribute("statuses", statuses);
		List <FormStatus> rtsStatuses = registerService.getRtsStatuses(user);
		model.addAttribute("rtsStatuses", rtsStatuses);
		return "RTSRegisterReport.jsp";
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
			@RequestParam (value="rrDateFrom", required=false) Date rrDateFrom,
			@RequestParam (value="rrDateTo", required=false) Date rrDateTo,
			@RequestParam (value="rtsDateFrom", required=true) Date rtsDateFrom,
			@RequestParam (value="rtsDateTo", required=true) Date rtsDateTo,
			@RequestParam (value="amountFrom", required=false) Double amountFrom,
			@RequestParam (value="amountTo", required=false) Double amountTo,
			@RequestParam (value="statusId", required=true) Integer statusId,
			@RequestParam (value="paymentStatId", required=true) Integer paymentStatId,
			@RequestParam (value="formatType", required = true) String formatType, 
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		JRDataSource dataSource = registerService.getReturnToSupplierRegisterData( companyId, divisionId, warehouseId,
				supplierId, supplierAcctId, rtsDateFrom, rtsDateTo, rrDateFrom, rrDateTo,
				amountFrom, amountTo, statusId, paymentStatId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		String startDate = DateUtil.formatDate(rtsDateFrom);
		String endDate = DateUtil.formatDate(rtsDateTo);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		String strDate = DateUtil.setUpDate(startDate, endDate);
		model.addAttribute("dateFromAndTo", strDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "RTSRegister.jasper";
	}
}
