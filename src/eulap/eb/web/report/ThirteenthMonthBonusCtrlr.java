package eulap.eb.web.report;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.ThirteenthMonthBonusService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * A controller class the handles Thirteenth Month Bonus report generation.

 */
@Controller
@RequestMapping("thirteenthMonBonus")
public class ThirteenthMonthBonusCtrlr {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ThirteenthMonthBonusService thirteenthMonthBonusService;

	private final static String REPORT_TITLE = "13TH MONTH BONUS";
	
	@RequestMapping(method=RequestMethod.GET)
	public String showArLineAnalysisReport(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		return "ThirteenthMonthBonusRprt.jsp";
	}

	@RequestMapping (method=RequestMethod.GET, value="/loadDivisions")
	public @ResponseBody String loadDivisions (
			@RequestParam (value="companyId", required=false) Integer companyId, HttpSession session) {
		Collection<Division> divisions = divisionService.getDivisions(companyId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(divisions, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("format", formatType);

		JRDataSource dataSource = new JRBeanCollectionDataSource(
				thirteenthMonthBonusService.getThirteenthMonthBonusDtos(companyId, divisionId, dateFrom, dateTo));
		model.addAttribute("datasource", dataSource);

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());

		model.addAttribute("dateFrom", dateFrom);
		model.addAttribute("dateTo", dateTo);

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, true);
		return "ThirteenthMonthBonusRprt.jasper";
	}

}
