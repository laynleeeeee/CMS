package eulap.eb.web.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.MonthlyCashflowRprtService;
import eulap.eb.web.dto.MonthlyCashflowReportDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Class that will handle requests for generating monthly cash flow report

 */

@Controller
@RequestMapping("monthlyCashFlowReport")
public class MonthlyCashflowReportCtrlr {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private MonthlyCashflowRprtService monthlyCashflowRprtService;
	private static final String REPORT_TITLE = "MONTHLY CASH FLOW";

	private static Logger logger = Logger.getLogger(MonthlyCashflowReportCtrlr.class);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showReportMain (HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("defaultMonth", DateUtil.getMonth(new Date()));
		logger.info("Loaded the search page for Monthly Cashflow report.");
		model.addAttribute("months", Arrays.asList("January", "February", "March", "April", "May", "June", "July", 
				"August", "September", "October", "November", "December"));
		return "MonthlyCashflowReport.jsp";
	}

	@RequestMapping(value="/generate", method = RequestMethod.GET)
	public String generateReport (@RequestParam (value="companyId") int companyId,
			@RequestParam (value="divisionId") int divisionId,
			@RequestParam (value="currentMonth", required=false) Integer currentMonth,
			@RequestParam (value="prevMonths", required=false) Integer prevMonths,
			@RequestParam(value="formatType") String formatType,
			Model model, HttpSession session) {
		logger.info("Generating the Monthly Cash Flow Report.");
		List<MonthlyCashflowReportDto> cashflowReportDtos = new ArrayList<MonthlyCashflowReportDto>();
		MonthlyCashflowReportDto cashflowReportDto = monthlyCashflowRprtService.genMonthlyCashFlow(companyId, divisionId, currentMonth, prevMonths);
		cashflowReportDtos.add(cashflowReportDto);
		JRDataSource dataSource = new JRBeanCollectionDataSource(cashflowReportDtos);
		model.addAttribute("format", formatType);
		model.addAttribute("datasource", dataSource);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, user);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("reportTitle", REPORT_TITLE);
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		Division division = divisionService.getDivision(divisionId); 
		model.addAttribute("division", division.getName());
		return "MonthlyCashflowReport.jasper";
	}

}
