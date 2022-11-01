package eulap.eb.web.report;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.BmsTrackerReportService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Controller class that will handle requests for BMS tracker report

 */

@Controller
@RequestMapping("/bmsTracker")
public class BmsTrackerReportController {
	private static Logger logger = Logger.getLogger(BmsTrackerReportController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private BmsTrackerReportService bmsTrackerService;
	private final static String REPORT_TITLE = "BMS TRACKER";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session) throws ConfigurationException {
		logger.info("Loading the search page of the Bms Tracker.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("formStatuses", bmsTrackerService.getFormStatuses(user));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		logger.info("Successfully loaded the search page.");
		return "BmsTrackerReport.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="typeId", required=false) Integer typeId,
			@RequestParam (value="bmsNo", required=false) String bmsNo,
			@RequestParam (value="poDateFrom", required=false) Date poDateFrom,
			@RequestParam (value="poDateTo", required=false) Date poDateTo,
			@RequestParam (value="invoiceDateFrom", required=false) Date invoiceDateFrom,
			@RequestParam (value="invoiceDateTo", required=false) Date invoiceDateTo,
			@RequestParam (value="statusId", required=false) Integer statusId,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		logger.info("Generating the BMS Tracker report.");
		logger.debug("Generating the datasource.");
		JRDataSource dataSource = bmsTrackerService.generateBmsTracker(companyId, divisionId, typeId,
				bmsNo, poDateFrom, poDateTo, invoiceDateFrom, invoiceDateTo, statusId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		company = null;
		model.addAttribute("reportTitle" , REPORT_TITLE);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		logger.info("Successfully generated the BMS Register report in "+formatType+" format.");
		return "BmsTrackerReport.jasper";
	}
}
