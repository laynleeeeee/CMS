package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
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
import eulap.eb.service.COCTaxService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import net.sf.jasperreports.engine.JRException;

/**
 * Controller for Certificate of Creditable Tax Withheld at Source Report.

 *
 */
@Controller
@RequestMapping("/cocTaxReport")
public class COCTaxController {
	private static Logger logger = Logger.getLogger(COCTaxController.class);
	private final static String REPORT_TITLE = "Certificate of Creditable Tax Withheld at Source";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private COCTaxService cocTaxService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String generateReport (Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user,model);
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		return "COCTaxReport.jsp";
	}

	private void loadSelections(User user, Model model) {
		// Get Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		// Get Active Divisions
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
	}

	@RequestMapping (value="/generate", method = RequestMethod.GET)
	public String generatePDF(
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="formatType", required = false) String formatType,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating " + formatType + " version of Certificate of Creditable Tax Withheld at Source Report.");
		model.addAttribute("datasource", cocTaxService.generateCOCTaxData(companyId, divisionId, dateFrom, dateTo));
		model.addAttribute("format", formatType );

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("dateRange", DateUtil.formatToFullDate(dateTo));
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		logger.info("Sucessfully loaded the Certificate of Creditable Tax Withheld at Source Report.");
		return "COCTax.jasper";
	}
}