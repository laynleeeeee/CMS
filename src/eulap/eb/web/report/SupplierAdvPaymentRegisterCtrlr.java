package eulap.eb.web.report;

import java.util.Date;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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
import eulap.eb.service.CompanyService;
import eulap.eb.service.SupplierAdvPaymentRegisterService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

/**
 * Controller for Supplier advance payment register
 * The entry point of Supplier Advance Payment Register.

 */

@Controller
@RequestMapping(value="/supplierAdvPaymentRgstr")
public class SupplierAdvPaymentRegisterCtrlr {
	private static Logger logger =  Logger.getLogger(SupplierAdvPaymentRegisterCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private SupplierAdvPaymentRegisterService supplierAdvPaymentService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (value="/showParams",method = RequestMethod.GET)
	public String showItemUnitCostHistoryPerSupplierPage (Model model, HttpSession session){
		logger.info("Loading the main page of Item unit cost history per supplier.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("sapRegisterStatuses", supplierAdvPaymentService.getTransactionStatuses(CurrentSessionHandler.getLoggedInUser(session)));
		logger.info("Successfully loaded the Item unit cost history per supplier.");
		return "SupplierAdvancePaymentRegister.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generateReport(
			@RequestParam (value="companyId") int companyId,
			@RequestParam (value="divisionId") int divisionId,
			@RequestParam (value="supplierId") int supplierId,
			@RequestParam (value="supplierAcctId") int supplierAcctId,
			@RequestParam (value="bmsNumber") String bmsNumber,
			@RequestParam (value="dateFrom") Date dateFrom,
			@RequestParam (value="dateTo") Date dateTo,
			@RequestParam (value="status") int status,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating " + formatType + " version of Supplier Advance Payment Register.");
		JRDataSource dataSource = supplierAdvPaymentService.generateSupplierAdvancePaymentRegister(companyId,
				divisionId, supplierId, supplierAcctId, bmsNumber, dateFrom, dateTo, status);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		// Set company attributes.
		Company company = companyService.getCompany(companyId);
		if (company != null) {
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
		}
		// Set date from and date to.
		model.addAttribute("dateRange", DateUtil.formatDate(dateFrom) + " To " + DateUtil.formatDate(dateTo));
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("preparedBy", user.getFirstName() + " " + user.getLastName());
		ReportUtil.getPrintDetails(model, user, isFirstNameFirst != null ? isFirstNameFirst : false);
		logger.info("Sucessfully loaded the Supplier advance payment register report.");
		return "SupplierAdvPaymentRgstr.jasper";
	}
}

