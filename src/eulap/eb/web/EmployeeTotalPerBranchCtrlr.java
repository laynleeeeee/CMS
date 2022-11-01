package eulap.eb.web;

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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EmployeePerBranchReportService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Controller class for Total Employee Per Branch

 *
 */

@Controller
@RequestMapping("employeeTotalPerBranch")
public class EmployeeTotalPerBranchCtrlr {
	private final static String REPORT_TITLE = "TOTAL EMPLOYEE PER BRANCH";
	@Autowired
	private EmployeePerBranchReportService reportService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session){
		Collection<Company> companies = getStatusCompanies(session);
		model.addAttribute("companies", companies);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		Collection<Division> divisions = divisionService.getActiveDivisions(0);
		model.addAttribute("divisions", divisions);
		return "TotalEmployeePerBranch.jsp";
	}

	private Collection<Company> getStatusCompanies	(HttpSession session) {
		return companyService.getActiveCompanies(
				CurrentSessionHandler.getLoggedInUser(session), null, null, null);
	}

	@RequestMapping (value="/generateReport", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="strStatus", required=true) String strStatus,
			@RequestParam (value="asOfDate", required=true) Date asOfDate,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isOrderByLastName", required=false,  defaultValue="false") Boolean isOrderByLastName,
			Model model, HttpSession session) {
		JRDataSource dataSource =
				reportService.generateEmployeePerBranch(companyId, divisionId, strStatus, asOfDate, isOrderByLastName);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		Company company = null;
		company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("asOfDate", asOfDate);
		model.addAttribute("reportTitle" , REPORT_TITLE);
		return "TotalEmployeePerBranch.jasper";
	}

}
