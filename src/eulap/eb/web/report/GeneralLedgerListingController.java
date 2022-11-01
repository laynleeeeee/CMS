package eulap.eb.web.report;

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
import eulap.eb.service.ReportSourcesHandler;
import eulap.eb.service.report.GeneralLedgerListingService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * General Ledger Listing report controller.

 *
 */
@Controller
@RequestMapping ("generalLedgerListing")
public class GeneralLedgerListingController {
	private static Logger logger = Logger.getLogger(GeneralLedgerListingController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private GeneralLedgerListingService ledgerListingService;
	@Autowired
	private ReportSourcesHandler sourcesHandler;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showReportForm (HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("sources", sourcesHandler.getAllJESources());
		logger.info("Loaded the search page for General Ledger Listing.");
		return "GeneralLedgerListing.jsp";
	}

	@RequestMapping(value="/generate", method = RequestMethod.GET)
	public String generateReport (@RequestParam (value="companyId") Integer companyId, 
			@RequestParam (value="strFromDate", required=false) String strFromDate,
			@RequestParam (value="strToDate", required=false) String strToDate,
			@RequestParam (value="accountId", defaultValue = "-1") Integer accountId, 
			@RequestParam(value="formatType") String formatType,
			Model model, HttpSession session) {
		logger.info("Generating the General Ledger Listing report.");
		JRDataSource dataSource = ledgerListingService.generateGLListingDatasource(companyId,
				strFromDate, strToDate, (accountId != null ? accountId : -1));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);

		logger.debug("Generating the parameters.");
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		logger.info("Successfully generated the report.");
		return "GeneralLedgerListing.jasper";
	}
}
