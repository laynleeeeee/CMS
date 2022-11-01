package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.report.BankReconService;
import eulap.eb.web.dto.BankReconSummaryDto;

/**
 * Controller class for generating the Bank Reconciliation Summary Report.

 *
 */
@Controller
@RequestMapping("/bankReconSummary")
public class BankReconSummaryController {
	private final static String REPORT_TITLE = "Bank Reconcilation Summary Report";
	@Autowired
	private BankAccountService baService;
	@Autowired
	private BankReconService brService;
	private static Logger logger = Logger.getLogger(BankReconSummaryController.class);

	@RequestMapping(method=RequestMethod.GET)
	public String showBankReconFilters (Model model, HttpSession session) {
		model.addAttribute("bankAccounts", baService.getBankAccountsByUser(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		logger.info("Showing the search parameters form Bank Reconciliation Summary Report.");
		return "BankReconciliationSummaryReport.jsp";
	}

	@RequestMapping(value = "generateReport",method=RequestMethod.GET)
	public String generateReport(@RequestParam(value="bankAcctIdAndBalances") String bankAcctIdAndBalances,
			@RequestParam(value="asOfDate") String asOfDate,
			@RequestParam(value="bankDate") String bankDate,
			Model model, HttpSession session) {
		logger.info("Generating the Bank Reconciliation Summary Report.");
		Date parsedAsOfDate = DateUtil.parseDate(asOfDate);
		Date parsedBankDate = DateUtil.parseDate(bankDate);
		List<BankReconSummaryDto> reconSummaryDtos =
				brService.genBankReconSumReport(bankAcctIdAndBalances, parsedAsOfDate, parsedBankDate);
		JRDataSource dataSource = new JRBeanCollectionDataSource(reconSummaryDtos);
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("format", "pdf");
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("reportTitle", REPORT_TITLE);
		model.addAttribute("bankDate", DateUtil.formatToText(parsedBankDate, true));
		model.addAttribute("asOfDate", "AS OF "+DateUtil.formatToText(parsedAsOfDate, true));
		ReportUtil.getPrintDetails(model, loggedUser);
		return "BankReconSummaryReport.jasper";
	}
}
