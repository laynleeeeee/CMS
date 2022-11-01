package eulap.eb.web.report;

import java.util.ArrayList;
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
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.IncomeStatementByDivOptService;
import eulap.eb.web.dto.IncomeStatementDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/** 
 * Income Statement by Division Service controller.


 *
 */
@Controller
@RequestMapping ("incomeStatementByDiv")
public class IncomeStatementReportCtrlr {
	private static Logger logger = Logger.getLogger(IncomeStatementReportCtrlr.class);
	public static final String REPORT_TITLE = "Statement Of Profit Or Loss";
	@Autowired
	private IncomeStatementByDivOptService incomeStatementByDivisionService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CompanyService companyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/showReport", method = RequestMethod.GET)
	public String showReport (HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getDivisions(null, null, true, null));
		return "IncomeStatementByDivision.jsp";
	}

	@RequestMapping(value="/generate", method = RequestMethod.GET)
	public String generateReport (@RequestParam(value="formatType") String formatType,
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam(value="subDivision", required=false) Integer subDivision, 
			@RequestParam(value="accountLevelId") Integer accountLevelId,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			Model model, HttpSession session) throws ConfigurationException, CloneNotSupportedException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Generating income statement by division report.");
		IncomeStatementDto is = incomeStatementByDivisionService.genIncomStatement(companyId, divisionId, subDivision, 
				accountLevelId, dateFrom, dateTo, false, user, 1);
		List<IncomeStatementDto> incomeStatementMains = new ArrayList<>();
		incomeStatementMains.add(is);
		JRDataSource dataSource = new JRBeanCollectionDataSource(incomeStatementMains);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		model.addAttribute("grossProfitLb", "GROSS PROFIT");
		model.addAttribute("operatingIncomeLb", "OPERATING INCOME");
		model.addAttribute("netIncomeLb", "NET INCOME");
		model.addAttribute("reportTitle", REPORT_TITLE);
		Company company = companyService.getCompany(companyId);
		if (company != null) {
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("companyTin", company.getTin());
		}
		model.addAttribute("date", DateUtil.formatDate(dateFrom) + "-" + DateUtil.formatDate(dateTo));
		ReportUtil.getPrintDetails(model, user);
		return "IncomeStatementByDivision.jasper";
	}
}
