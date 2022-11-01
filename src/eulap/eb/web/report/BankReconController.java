package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

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
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.BankReconService;
import eulap.eb.web.dto.BankRecon;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller class for generating the Bank Reconciliation Report.

 *
 */
@Controller
@RequestMapping("/bankReconPDF")
public class BankReconController {
	@Autowired
	private BankReconService brService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private BankAccountService bankAccountService;
	private static Logger logger = Logger.getLogger(BankReconController.class);

	@RequestMapping(value="showParams", method=RequestMethod.GET)
	public String showBankReconFilters (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		logger.info("Showing the search parameters form Bank Reconciliation Report.");
		return "BankReconciliationReport.jsp";
	}

	@RequestMapping(method=RequestMethod.GET)
	public String generateReport(@RequestParam (value="companyId") Integer companyId,
			@RequestParam(value="bankAcctId") Integer bankAcctId,
			@RequestParam(value="amount") Double bankBalance,
			@RequestParam(value="asOfDate") String asOfDate,
			@RequestParam(value="bankDate") String bankDate,
			@RequestParam(value="divisionId", required=true) Integer divisionId,
			Model model, HttpSession session) {
		logger.info("Generating the Bank Reconciliation Report.");
		model.addAttribute("format", "pdf");
		model.addAttribute("bankBalance", bankBalance);
		model.addAttribute("bankDate", bankDate);
		model.addAttribute("asOfDate", "As of " + asOfDate);
		BankAccount ba = bankAccountService.getBankAccount(bankAcctId);
		model.addAttribute("bankAcctName", ba.getName());

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		company = null;

		String divisionName = "ALL";
		if (divisionId > 0) {
			divisionName = divisionService.getDivision(divisionId).getName();
		}
		model.addAttribute("divisionName", divisionName);

		Date parsedAsOfDate = DateUtil.parseDate(asOfDate);
		double bookBal = brService.getBookBalance(companyId, ba.getCashInBankAcctId(), parsedAsOfDate);
		model.addAttribute("bookBalance", bookBal);

		// Generate the data source
		List<BankRecon> reportData = brService.generateReport(bankAcctId, parsedAsOfDate, divisionId);
		JRDataSource dataSource = new JRBeanCollectionDataSource(reportData);
		if (reportData != null) {
			BankRecon br = reportData.iterator().next();
			double totalDITAmount = br.getTotalDITAmount();
			double totalOCAmount = br.getTotalOCAmount();
			double adjustedBalance = bankBalance + totalDITAmount - totalOCAmount;
			model.addAttribute("adjustedBalance", adjustedBalance);
			model.addAttribute("variance", bookBal - adjustedBalance);
		} else {
			logger.warn("Bank Reconciliation datasource is null.");
		}
		model.addAttribute("dataSource", dataSource);
		logger.debug("Successfully added the generated Report to the data source.");

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, false);
		return "BankReconReport.jasper";
	}
}
