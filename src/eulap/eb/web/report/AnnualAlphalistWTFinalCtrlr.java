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
import eulap.eb.service.report.AnnualAlphaListWTFinalService;

/**
 * Annual Alphalist of Withholding Taxes Final Controller.

 *
 */
@Controller
@RequestMapping("/annualAlphalistWTFinal")
public class AnnualAlphalistWTFinalCtrlr {
	private static Logger logger = Logger.getLogger(AnnualAlphalistWTFinalCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AnnualAlphaListWTFinalService annualWTFinalService;
	private final static String REPORT_TITLE = "ALPHALIST OF PAYEES SUBJECT OF FINAL WITHHOLDING TAX";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showAnnualList(Model model, HttpSession session) {
		logger.info("Loading the search page of Report.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		logger.info("Successfully loaded the search page.");
		return "AnnualAlphalistWTFinal.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="year", required=false) Integer year,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		logger.info("Generating the Annual alpha list report.");
		logger.debug("Generating the datasource.");
		model.addAttribute("datasource", annualWTFinalService.generateAnnualALphalistWTFinal(companyId, divisionId,year));
		model.addAttribute("format", formatType );
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName().toUpperCase());
		model.addAttribute("companyAddress", company.getAddress());
		String tin = company.getTin();
		model.addAttribute("companyTin", tin != null && !tin.isEmpty()
				? StringFormatUtil.processBirTinTo13Digits(tin) : "");
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("schedules", "4, 5, 6");// Schedules
		String strDate = "DECEMBER 31, " + year ;
		model.addAttribute("yearEnd", strDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		logger.info("Successfully generated the report in "+formatType+" format.");
		return "AnnualAlphalistWTFinal.jasper";
	}

	@RequestMapping (value="/generateDAT", method = RequestMethod.GET)
	public void generateDAT(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="year", required=false) Integer year,
			Model model, HttpSession session, HttpServletResponse response) throws FileNotFoundException {
		String rtrnDate = "12/31/" + year ;
		model.addAttribute("yearEnd", rtrnDate);
		logger.info("Creating BIR 1604F");
		Path path = annualWTFinalService.generateBIRDatFile(companyId, divisionId, year, rtrnDate);
		response.setContentType("application/octet-stream");
		try {
			Files.copy(path, response.getOutputStream());
			 response.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("loading file : " + path);
	}
}
