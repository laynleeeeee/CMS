package eulap.eb.web.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Year;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import eulap.eb.service.report.SAWTReportService;
import eulap.eb.web.dto.SAWTDto;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.jasperreports.engine.JRException;

/**
 * Controller for summary alphalist of withholding taxes.
 * The entry point of summary alphalist of withholding taxes.

 */

@Controller
@RequestMapping(value="/SAWTReport")
public class SAWTReportController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private SAWTReportService sawtService;

	@RequestMapping (method = RequestMethod.GET)
	public String showItemUnitCostHistoryPerSupplierPage (Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("currentMonth", DateUtil.getMonth(new Date()));
		model.addAttribute("currentYear", DateUtil.getYear(new Date()));
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		return "SAWTReport.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generateReport(
			@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="divisionId") Integer divisionId,
			@RequestParam (value="fromMonth") Integer fromMonth,
			@RequestParam (value="toMonth") Integer toMonth,
			@RequestParam (value="year") Integer year,
			@RequestParam (value="birFormType") String birFormType,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException, ParseException {
		List<SAWTDto> datasource = sawtService.getSAWT(companyId, divisionId, -1, fromMonth, toMonth, year, true);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("datasource", datasource);
		model.addAttribute("format", formatType );
		model.addAttribute("birCode", "BIR FORM " + birFormType);
		model.addAttribute("title", "SUMMARY ALPHALIST OF WITHHOLDING TAXES (SAWT)");
		model.addAttribute("monthOf", "FOR THE MONTH OF "+(DateUtil.getMonthName(fromMonth-1)+" - "+DateUtil.getMonthName(toMonth-1)+", " + year)).toString().toUpperCase();
		if (company.getTin() != null) {
			model.addAttribute("tinNumber", company.getTin().substring(0,9));
		}
		model.addAttribute("payeeName", company.getName());
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("preparedBy", user.getFirstName() + " " + user.getLastName());
		ReportUtil.getPrintDetails(model, user, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "SAWTReport.jasper";
	}

	@RequestMapping (value="/generateDAT", method = RequestMethod.GET)
	public void generateDAT(
			@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="divisionId") Integer divisionId,
			@RequestParam (value="fromMonth") Integer fromMonth,
			@RequestParam (value="toMonth") Integer toMonth,
			@RequestParam (value="year") Integer year,
			@RequestParam (value="birFormType") String birFormType,
			Model model, HttpSession session, HttpServletResponse response) throws FileNotFoundException {
		String rtrnDate = toMonth+ "/"+ year ;
		model.addAttribute("yearEnd", rtrnDate);
		Path path = sawtService.generateBIRDatFile(divisionId, -1, fromMonth, toMonth, year, false, companyId, birFormType);
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
