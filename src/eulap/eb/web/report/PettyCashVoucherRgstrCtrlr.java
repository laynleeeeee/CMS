package eulap.eb.web.report;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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
import eulap.eb.service.CustodianAccountService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.PettyCashVoucherRegisterService;
import eulap.eb.service.PettyCashVoucherService;

/**
 * The entry point of petty cash register form.

 */
@Controller
@RequestMapping(value="/pettyCashVoucherRgstr")
public class PettyCashVoucherRgstrCtrlr {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CustodianAccountService custodianAccountService;
	@Autowired
	private PettyCashVoucherService pettyCashVoucherService;
	@Autowired
	private PettyCashVoucherRegisterService pettyCashVoucherRegisterService;


	private static final Logger LOGGER = Logger.getLogger(PettyCashVoucherRgstrCtrlr.class);

	@RequestMapping (method = RequestMethod.GET)
	public String showParams(HttpSession session, Model model) {
		LOGGER.info("Show the parameters of the report.");
		model.addAttribute("companies", companyService.getCompanies(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("divisions", divisionService.getActiveDivsions(CurrentSessionHandler.getLoggedInUser(session), 0));
		model.addAttribute("custodians", custodianAccountService.getCustodianAccount(0));
		model.addAttribute("pcvrStatuses", pettyCashVoucherService.getTransactionStatuses(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));

		return "PettyCashVoucherRegister.jsp";
	}

	@RequestMapping(value = "/generatePDF", method=RequestMethod.GET)
	public String generatePDF(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId") int divisionId,
			@RequestParam(value="custodianId") int custodianId,
			@RequestParam(value="requestor") String requestor,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="statusId") int statusId,
			@RequestParam (value="formatType", required = true) String formatType,
			HttpSession session, Model model) {
		LOGGER.info("Generating the Petty Cash Voucher Register Report.");
		LOGGER.debug("Generating the data source.");
		model.addAttribute("datasource", pettyCashVoucherRegisterService.generateReport(companyId, divisionId, custodianId, requestor, dateFrom, dateTo, statusId));
		model.addAttribute("format", formatType);
		model.addAttribute("dateRange", DateUtil.formatDate(dateFrom)
				+ " to " + DateUtil.formatDate(dateTo));
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		LOGGER.debug("Searching transactions for Company: "+company.getName());
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, false);
		LOGGER.info("Successfully generated the report");
		return "PettyCashVoucherRegister.jasper";
	}

}
