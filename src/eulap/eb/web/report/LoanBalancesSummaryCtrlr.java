package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import eulap.eb.service.report.LoanAcctHistoryService;
import eulap.eb.web.dto.LoanAcctHistoryDto;

/**
 * Controller class that will handle request for loan summary report generation

 */

@Controller
@RequestMapping("loanBalancesSummary")
public class LoanBalancesSummaryCtrlr {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private LoanAcctHistoryService loanAcctHistoryService;

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session) throws ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		return "LoanBalancesSummary.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=true) int divisionId,
			@RequestParam (value="balanceOption", required=true) int balanceOption,
			@RequestParam (value="asOfDate", required=false) Date asOfDate,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		List<LoanAcctHistoryDto> loanAcctHistoryDtos =
				loanAcctHistoryService.getLoanBalancesSummaryData(companyId, divisionId, balanceOption, asOfDate);
		model.addAttribute("datasource", loanAcctHistoryDtos);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("division", divisionId != -1 ? divisionService.getDivision(divisionId).getName() : "ALL");
		model.addAttribute("asOfDate", "As of date " + DateUtil.formatDate(asOfDate));
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		return "LoanBalancesSummary.jasper";
	}
}
