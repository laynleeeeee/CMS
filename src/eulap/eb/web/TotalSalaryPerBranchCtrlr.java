package eulap.eb.web;

import java.util.Date;

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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EmployeeService;
import eulap.eb.service.TotalSalaryPerBranchService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller class for Total Salary Per Branch report.

 *
 */
@Controller
@RequestMapping("totalSalaryPerBranch")
public class TotalSalaryPerBranchCtrlr {
	private static Logger logger = Logger.getLogger(TotalSalaryPerBranchCtrlr.class);
	private final static String REPORT_TITLE = "TOTAL SALARY PER BRANCH";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private TotalSalaryPerBranchService totalSalaryService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showReportForm(Model model, HttpSession session) {
		logger.info("Loading total salary per branch form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions",  divisionService.getActiveDivisions(0));
		model.addAttribute("statuses", employeeService.getAllActiveEmployeeStatus());
		return "TotalSalaryPerBranch.jsp";
	}

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public String generateReport(@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId")Integer divisionId,
			@RequestParam(value="statusId", required=false) Integer statusId,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam(value="format", required=false) String format,
			@RequestParam(value="isOrderByLastName", required=false, defaultValue="false") Boolean isOrderByLastName,
			Model model, HttpSession session) {
		JRDataSource dataSource = new JRBeanCollectionDataSource(totalSalaryService.getSalaryPerBranches(companyId, divisionId,
				statusId, dateFrom, dateTo, isOrderByLastName));
		model.addAttribute("dataSource", dataSource);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle", REPORT_TITLE);
		model.addAttribute("dateFrom", dateFrom);
		model.addAttribute("dateTo", dateTo);
		model.addAttribute("format", format);
		return "TotalSalaryPerBranch.jasper";
	}
}
