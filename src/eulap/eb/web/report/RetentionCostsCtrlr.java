package eulap.eb.web.report;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.RetentionCostsService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Controller class that will handle request for retention cost report generation

 */

@Controller
@RequestMapping("retentionCostRprt")
public class RetentionCostsCtrlr {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private RetentionCostsService retentionCostService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session) throws ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		return "RetentionCosts.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=true) int divisionId,
			@RequestParam (value="customerId", required=false) Integer customerId,
			@RequestParam (value="customerAcctId", required=false) Integer customerAcctId,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="asOfDate", required=false) Date asOfDate,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		JRDataSource dataSource =retentionCostService.generateRetentionCostRprt(companyId, divisionId,
				customerId, customerAcctId, dateFrom, dateTo, asOfDate);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("division", divisionService.getDivision(divisionId).getName());
		model.addAttribute("date", retentionCostService.processReportDateParam(dateFrom, dateTo, asOfDate));
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		return "RetentionCosts.jasper";
	}
}
