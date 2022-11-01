package eulap.eb.web.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;
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
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.AnnualAlphalistWTExpandedService;

/**
 * Annual Alpha List of Withholding Taxes Expanded Controller.

 *
 */
@Controller
@RequestMapping("/annualAlphaListWTExpanded")
public class AnnualAlphaListWTExpandedCtrlr {
	private static Logger logger = Logger.getLogger(AnnualAlphaListWTExpandedCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AnnualAlphalistWTExpandedService annualalphalistservice;

	private final static String REPORT_TITLE = "ALPHALIST OF PAYEES SUBJECT TO EXPANDED WITHHOLDING TAX";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showAnnualList(Model model, HttpSession session) {
		logger.info("Loading the search page of Report.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		logger.info("Successfully loaded the search page.");
		return "AnnualAlphaListWTExpanded.jsp";
	}

	@RequestMapping(value = "/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam(value = "companyId", required = true) int companyId,
			@RequestParam(value = "divisionId", required = true) Integer divisionId,
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "formatType", required = true) String formatType, Model model, HttpSession session) {
		logger.info("Generating the Annual alpha list report.");
		logger.debug("Generating the datasource.");
		model.addAttribute("format", formatType);
		model.addAttribute("dataSource", annualalphalistservice.generateAnnualALphalistWTExpanded(companyId, divisionId, year));
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName().toUpperCase());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin() + '-' +StringFormatUtil.parseBranchCode(company.getTin()));
		model.addAttribute("reportTitle", REPORT_TITLE);
		model.addAttribute("scheduleType", "3");
		String strDate = "DECEMBER 31, " + year.toString();
		model.addAttribute("yearEnd", strDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		logger.info("Successfully generated the report in " + formatType + " format.");
		return "AnnualAlphaListWTExpanded.jasper";
	}

	@RequestMapping (value="/generateDAT", method = RequestMethod.GET)
	public void generateDAT(@RequestParam (value="companyId", required=true) Integer companyId,
	@RequestParam (value="divisionId", required=true) Integer divisionId,
	@RequestParam (value="year", required=false) Integer year, Model model,
	HttpSession session, HttpServletResponse response) throws
	FileNotFoundException { String rtrnDate = "12/31/" + year.toString() ;
	model.addAttribute("yearEnd", rtrnDate); logger.info("Creating BIR 1604E");
	Path path = annualalphalistservice.generateBIRDatFile(companyId, divisionId, year, rtrnDate); response.setContentType("application/octet-stream"); 
	try {
	Files.copy(path, response.getOutputStream());
	response.getOutputStream().flush();
	} catch (IOException e) {
		e.printStackTrace();
	}
	logger.info("loading file : " + path);
	}
}
