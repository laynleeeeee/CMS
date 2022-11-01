package eulap.eb.web.report;

import java.util.Date;

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
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.DeliveryReceiptRegisterParam;
import eulap.eb.service.report.DeliveryReceiptRegisterServiceImpl;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A controller class that handles the Delivery Receipt Register report generation.

 */
@Controller
@RequestMapping("/deliveryReceiptRegister")
public class DeliveryReceiptRegisterController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionSerivce;
	@Autowired
	private ArCustomerService customerService;
	@Autowired
	private DeliveryReceiptRegisterServiceImpl deliveryReceiptRegisterServiceImpl;
	private final static String REPORT_TITLE = "DELIVERY RECEIPT REGISTER";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showDeliveryReceiptRegister(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionSerivce.getActiveDivisions(0));
		model.addAttribute("customers", customerService.getArCustomers(user));
		model.addAttribute("formStatuses", deliveryReceiptRegisterServiceImpl.getFormStatuses(user));
		return "DeliveryReceiptRegisterReport.jsp";
	}

	@RequestMapping(value = "/generateReport",method = RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam(value="divisionId",required=false ) Integer divisionId,
			@RequestParam(value="customerId",required=false, defaultValue="-1") Integer customerId,
			@RequestParam(value="customerAcctId",required=false,defaultValue="-1") Integer customerAcctId,
			@RequestParam(value="soNumber",required=false) String soNumber,
			@RequestParam(value="poPcrNumber",required=false) String poPcrNumber,
			@RequestParam(value="drNumberFrom",required=false) Integer drNumberFrom,
			@RequestParam(value="drNumberTo",required=false) Integer drNumberTo,
			@RequestParam(value="drDateFrom",required=false) Date drDateFrom,
			@RequestParam(value="drDateTo",required=false) Date drDateTo,
			@RequestParam(value="deliveryReceiptStatus",required=false) Integer deliveryReceiptStatus,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) {
		DeliveryReceiptRegisterParam param = new DeliveryReceiptRegisterParam();
		param.setCompanyId(companyId);
		param.setDivisionId(divisionId);
		param.setCustomerId(customerId);
		param.setCustomerAcctId(customerAcctId);
		param.setSoNumber(soNumber);
		param.setPoPcrNumber(poPcrNumber);
		param.setDrNumberFrom(drNumberFrom);
		param.setDrNumberTo(drNumberTo);
		param.setDrDateFrom(drDateFrom);
		param.setDrDateTo(drDateTo);
		param.setDeliveryReceiptStatus(deliveryReceiptStatus);
		String datefrom = DateUtil.formatDate(drDateFrom);
		String dateTo = DateUtil.formatDate(drDateTo);
		JRDataSource dataSource = new JRBeanCollectionDataSource(deliveryReceiptRegisterServiceImpl.generateDeliveryReceiptRegister(param));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("asOfDate" , datefrom+ " To " +dateTo );
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "DeliveryReceiptRegister.jasper";
	}
}
