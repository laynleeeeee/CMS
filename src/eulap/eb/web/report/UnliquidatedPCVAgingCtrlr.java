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
import eulap.eb.service.DivisionService;
import eulap.eb.service.UnliquidatedPCVRegstrService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * The entry point of unliquidated petty cash register aging form.

 */
@Controller
@RequestMapping(value="/unliquidatedPCVAging")
public class UnliquidatedPCVAgingCtrlr {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private UnliquidatedPCVRegstrService unliquidatedPCVRegstrService;

	private static final Logger LOGGER = Logger.getLogger(UnliquidatedPCVAgingCtrlr.class);

	@RequestMapping (method = RequestMethod.GET)
	public String showParams(HttpSession session, Model model) {
		LOGGER.info("Show the parameters of the report.");
		model.addAttribute("companies", companyService.getCompanies(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("divisions", divisionService.getActiveDivsions(CurrentSessionHandler.getLoggedInUser(session), 0));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		return "UnliquidatedPCVAging.jsp";
	}

	@RequestMapping(value = "/generatePDF", method=RequestMethod.GET)
	public String generatePDF(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId") int divisionId,
			@RequestParam(value="custodianId") int custodianId,
			@RequestParam(value="requestor") String requestor,
			@RequestParam (value="asOfDate", required=true) Date asOfDate,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			HttpSession session, Model model) {
		LOGGER.info("Generating the Petty Cash Voucher Register Report.");
		//Data source
		LOGGER.debug("Generating the data source.");
		JRDataSource dataSource = unliquidatedPCVRegstrService.generateUnliquidatedPCVAging(companyId,
				divisionId, custodianId, requestor, asOfDate);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		model.addAttribute("asOfDate", DateUtil.formatDate(asOfDate));

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		LOGGER.debug("Searching transactions for Company: "+company.getName());

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		LOGGER.info("Successfully generated the report");
		return "UnliquidatedPettyCashVoucherAging.jasper";
	}
}
