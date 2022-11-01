package eulap.eb.web.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
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
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.QuarterlyAlphalistOfPayeesService;
import eulap.eb.web.dto.QuarterlyAlphaListOfPayeesDto;

/**
 * The entry point of quarterly alphalist of payees form.

 */

@Controller
@RequestMapping(value="/quarterlyAlphaListOfPayees")
public class QuarterlyAlphaListOfPayeesCtrlr {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private QuarterlyAlphalistOfPayeesService qaopService;

	private static final Logger LOGGER = Logger.getLogger(UnliquidatedPCVAgingCtrlr.class);
	@RequestMapping (method = RequestMethod.GET)
	public String showParams(HttpSession session, Model model) {
		LOGGER.info("Show the parameters of the report.");
		model.addAttribute("companies", companyService.getCompanies(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("divisions", divisionService.getActiveDivsions(CurrentSessionHandler.getLoggedInUser(session), 0));
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		return "QuarterlyAlphalistOfPayees.jsp";
	}

	@RequestMapping(value = "/generatePDF", method=RequestMethod.GET)
	public String generatePDF(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId") int divisionId,
			@RequestParam (value="month", required=true) int month,
			@RequestParam (value="year", required=true) int year,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			HttpSession session, Model model) {
		LOGGER.info("Generating the Quarterly Alphalist Of Payees.");
		List<QuarterlyAlphaListOfPayeesDto> datasource = qaopService.getQuarterlyAlphaListOfPayees(companyId, divisionId, month, year, true);
		model.addAttribute("datasource", datasource);
		LOGGER.debug("Generating the data source.");
		Company company = companyService.getCompany(companyId);
		model.addAttribute("format", formatType);
		model.addAttribute("endingDate", DateUtil.getMonthName(month+1).toUpperCase() + ", " + year);
		model.addAttribute("tin", StringFormatUtil.processBirTinTo13Digits(company.getTin()));
		model.addAttribute("agentName", company.getName());
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		LOGGER.info("Successfully generated the report");
		return "QuarterlyAlphalistOfPayees.jasper";
	}

	@RequestMapping (value="/generateDAT", method = RequestMethod.GET)
	public void generateDAT(
			@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="divisionId") Integer divisionId,
			@RequestParam (value="month") Integer month,
			@RequestParam (value="year", required=true) int year,
			Model model, HttpSession session, HttpServletResponse response) throws FileNotFoundException {
		String rtrnDate = month+ "/"+ year ;
		model.addAttribute("yearEnd", rtrnDate);
		Path path = qaopService.generateBIRDatFile(companyId, divisionId, month, year);
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
