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
import eulap.eb.service.report.BalanceSheetByDivisionService;
import eulap.eb.web.dto.BalanceSheetDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Balance Sheet by Division Service controller.

 *
 */
@Controller
@RequestMapping ("balanceSheetByDiv")
public class BalanceSheetReportCtrlr {
	private static Logger logger = Logger.getLogger(BalanceSheetReportCtrlr.class);
	public static final String REPORT_TITLE = "Statement of Financial Position";
	@Autowired
	private BalanceSheetByDivisionService bsByDivisionService;
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
		return "BalanceSheetByDivision.jsp";
	}

	@RequestMapping(value="/generate", method = RequestMethod.GET)
	public String generateReport (@RequestParam(value="formatType") String formatType,
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam(value="subDivision", required=false) Integer subDivision,
			@RequestParam(value="accountLevelId") Integer accountLevelId,
			@RequestParam(value="date", required=false) Date date,
			Model model, HttpSession session) throws ConfigurationException, CloneNotSupportedException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Generating Balance Sheet by division report.");
		BalanceSheetDto is = bsByDivisionService.generateBS(companyId, divisionId, subDivision, accountLevelId, date, user);
		List<BalanceSheetDto> bsDTOs = new ArrayList<>();
		bsDTOs.add(is);
		JRDataSource dataSource = new JRBeanCollectionDataSource(bsDTOs);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		model.addAttribute("assetTotalLb", "TOTAL ASSET");
		model.addAttribute("liabilitiesTotalLb", "TOTAL LIABILITIES");
		model.addAttribute("liabilitiesMemberEqTotalLb", "TOTAL LIABILITIES AND EQUITY");
		model.addAttribute("reportTitle", REPORT_TITLE);
		Company company = companyService.getCompany(companyId);
		if (company != null) {
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
		}
		model.addAttribute("date", DateUtil.formatDate(date));
		ReportUtil.getPrintDetails(model, user);
		return "BalanceSheetByDivision.jasper";
	}
}
