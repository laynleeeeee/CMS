package eulap.eb.web.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.Date;

import javax.naming.ConfigurationException;
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
import eulap.eb.service.SummaryLSPService;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.jasperreports.engine.JRException;

/**
 * Class that will handle requests for generating summary list of sales and purchases report.

 */

@Controller
@RequestMapping("summaryLSPReport")
public class SummaryLSPController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private SummaryLSPService summaryLSPService;

	private static final String REPORT_TITLE = "summary list of sales and purchases";
	private static final Logger logger = Logger.getLogger(SummaryLSPController.class);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showReportMain (HttpSession session, Model model) {
		Date date = new Date();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		model.addAttribute("currentYear", DateUtil.getYear(date));
		model.addAttribute("months", TimePeriodMonth.getMonths());
		logger.info("Loaded the search page for summary list of sales and purchases.");
		return "SummaryLSPReport.jsp";
	}

	@RequestMapping(value="/generate", method = RequestMethod.GET)
	public String generateReport (
			@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="divisionId") Integer divisionId,
			@RequestParam (value="year") Integer year,
			@RequestParam (value="month") Integer month,
			@RequestParam (value="taxType", required=true) Integer taxType,
			@RequestParam(value="formatType") String formatType,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating the summary list of sales and purchases report.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, user);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("reportTitle", REPORT_TITLE);
		model.addAttribute("proprietor", "BARCELONA, NOEL S.");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", StringFormatUtil.processBirTinTo13Digits(company.getTin()));
		model.addAttribute("companyAddress", company.getAddress());
		if (taxType == SummaryLSPService.SALES_TAX_TYPE) {
			model.addAttribute("datasource", summaryLSPService.slSalesSvc(companyId, divisionId, year, month));
			logger.info("Successfully generated the sales summary report.");
			return "SummaryLSPSales.jasper";
		} else if (taxType == SummaryLSPService.PURCHASES_TAX_TYPE) {
			model.addAttribute("datasource", summaryLSPService.slPurchasesSvc(companyId, divisionId, year, month));
			logger.info("Successfully generated the purchases summary report.");
			return "SummaryLSPPurchases.jasper";
		}
		model.addAttribute("datasource", summaryLSPService.slImportsSvc(companyId, divisionId, year, month));
		logger.info("Successfully generated the importation summary report.");
		return "SummaryLSPImports.jasper";
	}

	@RequestMapping (value="/generateDAT", method = RequestMethod.GET)
	public void generateDAT(
			@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="divisionId") Integer divisionId,
			@RequestParam (value="month") Integer month,
			@RequestParam (value="year") Integer year,
			@RequestParam (value="taxType") Integer taxType,
			Model model, HttpSession session, HttpServletResponse response) throws FileNotFoundException {
		Path path = summaryLSPService.generateBIRDatFile(divisionId, taxType, month, year, false, companyId);
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", "attachment; filename="+path.getFileName());
		try {
			Files.copy(path, response.getOutputStream());
			 response.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
