package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.is.IncomeStatement;
import eulap.eb.service.report.ConsolidatedIncomeStatementService;
import eulap.eb.service.report.IncomeStatementService;
import eulap.eb.web.dto.IncomeStatementDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Income statement controller.

 */

@Controller
@RequestMapping ("/incomeStatement")
public class IncomeStatementController {
	private final Logger logger = Logger.getLogger(IncomeStatementController.class);
	private final static String REPORT_TITLE = "Statement of Financial Performance";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private IncomeStatementService incomeStatementService;
	@Autowired
	private ConsolidatedIncomeStatementService cisService;

	@RequestMapping(method = RequestMethod.GET)
	public String showMainForm(Model model, HttpSession session) {
		loadSelections(model, session);
		return "IncomeStatement.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generateIncomeStatementPDF (@RequestParam (value="companyId") Integer companyId,
			@RequestParam (value="dateFrom") Date dateFrom,
			@RequestParam (value="dateTo") Date dateTo,
			@RequestParam (value="formatType") String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating "+formatType+" version of income statement");
		logger.debug("Getting the list of income statement");
		List<IncomeStatement> rows = incomeStatementService.processIncomeStatement(companyId, dateFrom, dateTo, false);
		JRDataSource dataSource = new JRBeanCollectionDataSource(rows);
		model.addAttribute("amount", rows.iterator().next().getAmount());
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		Company company = null;
		if (companyId != null) {
			company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , REPORT_TITLE);
			model.addAttribute("dateFromAndTo", DateUtil.setUpDate(DateUtil.formatDate(dateFrom),
					DateUtil.formatDate(dateTo)));
		} else {
			logger.error("Company and time period are required.");
			throw new RuntimeException("Company and time period are required.");
		}
		logger.info("Sucessfully loaded the income statement.");
		return "IncomeStatement.jasper";
	}

	private void loadSelections(Model model, HttpSession session) {
		model.addAttribute("companies", companyService.getCompanies(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("timePeriods", timePeriodService.getCurrentTimePeriods());
	}

	@RequestMapping(value="/consolidated", method = RequestMethod.GET)
	public String showMainFormV2(Model model, HttpSession session) {
		loadSelections(model, session);
		return "ConsolidatedIncomeStatement.jsp";
	}

	@RequestMapping (value="/generateReport", method = RequestMethod.GET)
	public String generateIncomeStatementPDF (@RequestParam (value="strCompanyIds") String strCompanyIds,
			@RequestParam (value="dateFrom") Date dateFrom,
			@RequestParam (value="dateTo") Date dateTo,
			@RequestParam (value="formatType") String formatType,
			Model model, HttpSession session) throws JRException, ConfigurationException, CloneNotSupportedException {
		logger.info("Generating "+formatType+" version of income statement");
		List<IncomeStatementDto> isDtos = cisService.getIncomeStatementData(strCompanyIds, dateFrom, dateTo);
		JRDataSource dataSource = new JRBeanCollectionDataSource(isDtos);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		model.addAttribute("reportTitle", REPORT_TITLE);
		model.addAttribute("format", formatType);
		model.addAttribute("grossProfitLb", "GROSS PROFIT");
		model.addAttribute("operatingIncomeLb", "OPERATING INCOME");
		model.addAttribute("netIncomeLb", "NET INCOME");
		model.addAttribute("dateFromAndTo", DateUtil.setUpDate(DateUtil.formatDate(dateFrom),
				DateUtil.formatDate(dateTo)));
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, false);
		logger.info("Successfully generated consolidated income statement report.");
		return "ConsolidatedIncomeStatement.jasper";
	}
}
