package eulap.eb.web.report;

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
import eulap.eb.service.CompanyService;
import eulap.eb.service.report.BalanceSheetService;
import eulap.eb.service.report.ConsolidatedBalanceSheetService;
import eulap.eb.service.report.bs.BalanceSheet;
import eulap.eb.web.dto.BalanceSheetDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


/**
 * Controller class that will handle the requests for Balance Sheet.

 */
@Controller
@RequestMapping("/balanceSheet")
public class BalanceSheetController {
	private final Logger logger = Logger.getLogger(BalanceSheetController.class);
	private final static String REPORT_TITLE = "Statement of Financial Position";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private BalanceSheetService balanceSheetService;
	@Autowired
	private ConsolidatedBalanceSheetService cbsService;

	@RequestMapping(method = RequestMethod.GET)
	public String showMainForm(Model model, HttpSession session) {
		loadSelections(model, session);
		return "BalanceSheet.jsp";
	}

	@RequestMapping(value="generatePdf", method=RequestMethod.GET)
	public String generateReportPdf(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="asOfDate") String asOfDate,
			@RequestParam(value="formatType") String formatType,
			@RequestParam(value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws ConfigurationException {
		logger.info("Generating "+formatType+" version of balance sheet");
		logger.debug("Company ID: " + companyId + " As of date: " + asOfDate);
		logger.debug("Getting the list of balance sheets");
		List<BalanceSheet> rows = balanceSheetService.processBalanceSheet(companyId, DateUtil.parseDate(asOfDate), false);
		JRDataSource dataSource = new JRBeanCollectionDataSource(rows);
		model.addAttribute("datasource", dataSource);
		setCommonParam(formatType, asOfDate, isFirstNameFirst, model, session);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		company = null;
		return "BalanceSheet.jasper";
	}

	private void setCommonParam(String formatType, String asOfDate, Boolean isFirstNameFirst,
			Model model, HttpSession session) {
		model.addAttribute("format", formatType);
		model.addAttribute("reportTitle", REPORT_TITLE);
		model.addAttribute("asOfDate", "As of " + DateUtil.formatDate(DateUtil.parseDate(asOfDate)));
		ReportUtil.getPrintDetails(model, CurrentSessionHandler.getLoggedInUser(session),
				(isFirstNameFirst != null ? isFirstNameFirst : false));
	}

	private void loadSelections(Model model, HttpSession session) {
		model.addAttribute("companies", companyService.getCompanies(CurrentSessionHandler.getLoggedInUser(session)));
	}

	@RequestMapping(value="/consolidated", method = RequestMethod.GET)
	public String showMainFormV2(Model model, HttpSession session) {
		loadSelections(model, session);
		return "ConsolidatedBalanceSheet.jsp";
	}

	@RequestMapping(value="/generateReport", method=RequestMethod.GET)
	public String generateReportPdf(@RequestParam(value="strCompanyIds") String strCompanyIds,
			@RequestParam(value="asOfDate") String asOfDate,
			@RequestParam(value="formatType") String formatType,
			Model model, HttpSession session) throws ConfigurationException, CloneNotSupportedException {
		logger.info("Generating "+formatType+" version of balance sheet");
		logger.debug("Getting the list of balance sheets");
		List<BalanceSheetDto> bsDtos = cbsService.getBalanceSheetData(strCompanyIds, DateUtil.parseDate(asOfDate));
		JRDataSource dataSource = new JRBeanCollectionDataSource(bsDtos);
		model.addAttribute("datasource", dataSource);
		setCommonParam(formatType, asOfDate, false, model, session);
		model.addAttribute("assetTotalLb", "TOTAL ASSET");
		model.addAttribute("liabilitiesTotalLb", "TOTAL LIABILITIES");
		model.addAttribute("liabilitiesMemberEqTotalLb", "TOTAL LIABILITIES AND EQUITY");
		return "ConsolidatedBalanceSheet.jasper";
	}
}
