package eulap.eb.web.report;

import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.MonthlyValueAddedTaxDecParam;
import eulap.eb.service.report.MonthlyValueAddedTaxDecService;
import eulap.eb.web.dto.TimePeriodMonth;
import eulap.eb.web.dto.ValueAddedTaxSummaryDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A controller class that handles the Monthly Value Added Tax Declaration.

 */

@Controller
@RequestMapping("/monthlyValueAddedTaxDec")
public class MonthlyValueAddedTaxDecController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private MonthlyValueAddedTaxDecService monthlyValueAddedTaxDecService;
	private final static String REPORT_TITLE = "MONTHLY VALUE ADDED TAX DECLARATION";

	@RequestMapping(method = RequestMethod.GET)
	public String showMonthlyValueAddedTaxDec( Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivisions(0));
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		model.addAttribute("year", DateUtil.rangeYears(1999, Year.now().getValue()));
		return "MonthlyValueAddedTaxDec.jsp";
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="monthId", required=false) Integer month,
			@RequestParam (value="year", required=false) Integer year,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst, Model model, HttpSession session) {
		MonthlyValueAddedTaxDecParam param = new MonthlyValueAddedTaxDecParam();
		param.setMonth(month);
		List<ValueAddedTaxSummaryDto> valueAddedTaxSummary = new ArrayList<ValueAddedTaxSummaryDto>();
		valueAddedTaxSummary.add(monthlyValueAddedTaxDecService.getMonthlyValueAddedTax(companyId,
				divisionId, year, -1, param.getMonth()));
		JRDataSource dataSource = new JRBeanCollectionDataSource(valueAddedTaxSummary);
		String months = param.getMonthName();
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("month", months);
		model.addAttribute("year", year);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		return "MonthlyValueAddedTaxDec.jasper";
	}
}
