package eulap.eb.web.report;

import java.util.Date;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.report.PaidInAdvanceDeliveryService;
import bp.web.ar.CurrentSessionHandler;

/**
 * Controller class for Paid in Advance Delivery Register report.

 *
 */
@Controller
@RequestMapping("paidInAdvanceDeliveryRegister")
public class PaidInAdvanceDeliveryRegisterCtrlr {
	private static Logger logger = Logger.getLogger(CustomerAdvancePaymentRegisterController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private PaidInAdvanceDeliveryService piadService;
	private final static int PK_PIAD = 161;

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session) throws ConfigurationException {
		logger.info("Loading the search page of the CAP Register Report.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		logger.info("Successfully loaded the search page.");
		/** Generates java.util.NoSuchElementException if Paid in Advance Delivery - IS is configured
		 * but the PIAD (FIFO) is not. **/
		model.addAttribute("statuses", formStatusService.getFormStatusesByPC(PK_PIAD, true));
		return "PaidInAdvanceDeliveryRegister.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="arCustomerId", required=true) Integer arCustomerId,
			@RequestParam (value="arCustomerAccountId", required=true) Integer arCustomerAccountId,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="statusId", required=true) Integer statusId,
			@RequestParam (value="formatType", required = true) String formatType, 
			Model model, HttpSession session) {
		logger.info("Generating the PIAD Register report.");
		logger.debug("Generating the datasource.");
		JRDataSource dataSource = piadService.generatePAIDRegister(companyId, arCustomerId, arCustomerAccountId, 
				dateFrom, dateTo, statusId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		String strDate = DateUtil.setUpDate(DateUtil.formatDate(dateFrom), DateUtil.formatDate(dateTo));
		model.addAttribute("dateFromAndTo", strDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		logger.info("Successfully generated the PIAD Register report in "+formatType+" format.");
		return "PaidInAdvanceDeliveryRegister.jasper";
	}

}
