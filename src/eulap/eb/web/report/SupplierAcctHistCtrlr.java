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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.SupplierAccountService;
import eulap.eb.web.dto.SupplierAccountSummaryDto;
import eulap.eb.web.dto.SupplierAcctHistDto;

/**
 * Controller class for Supplier Account History Report.

 *
 */
@Controller
@RequestMapping("/supplierAcctHistPDF")
public class SupplierAcctHistCtrlr {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private SupplierAccountService supplierAcctService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;

	private static final Logger LOGGER = Logger.getLogger(SupplierAcctHistCtrlr.class);

	@RequestMapping(value="/showParams", method=RequestMethod.GET)
	public String showParams(HttpSession session, Model model) {
		LOGGER.info("Show the parameters of the report.");
		model.addAttribute("companies", companyService.getCompanies(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("divisions", divisionService.getActiveDivsions(CurrentSessionHandler.getLoggedInUser(session), 0));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(0));
		model.addAttribute("supplierAccts", supplierAcctService.getSupplierAccnts(0, CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		return "SupplierAcctHistory.jsp";
	}

	@RequestMapping(method=RequestMethod.GET)
	public String generateReport(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="supplierAcctId") int supplierAcctId,
			@RequestParam(value="divisionId") int divisionId,
			@RequestParam(value="currencyId") int currencyId,
			@RequestParam (value="asOfDate", required=true) String asOfDate,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			HttpSession session, Model model) {
		LOGGER.info("Generating the Supplier Account History Report.");
		//Data source
		LOGGER.debug("Generating the data source.");
		List<SupplierAcctHistDto> supplierHistRpt =
				supplierAcctService.generateReport(supplierAcctId, asOfDate, divisionId, currencyId);
		JRDataSource dataSource = new JRBeanCollectionDataSource(supplierHistRpt);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);

		//Company details
		Company company = companyService.getCompany(companyId);
		LOGGER.debug("Selected Company: "+company.getNumberAndName());
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());

		//Supplier Account details
		SupplierAccount supplierAcct = supplierAcctService.getSupplierAcct(supplierAcctId);
		String supplierName = supplierAcct.getSupplier().getName();
		LOGGER.debug("Selected Supplier: "+supplierName);
		model.addAttribute("supplierName", supplierName);
		model.addAttribute("supplierAcctName", supplierAcct.getName());
		model.addAttribute("date", DateUtil.setUpDate(asOfDate, null));

		Currency currency = currencyService.getCurency(currencyId);
		model.addAttribute("currency", currency.getName());
		SupplierAccountSummaryDto summaryDto = supplierAcctService.getSummaryHeader(supplierAcctId,
				DateUtil.parseDate(asOfDate), divisionId, currencyId);
		model.addAttribute("totalInvoices", summaryDto.getInvoiceAmount());
		model.addAttribute("totalPayments", summaryDto.getPaymentAmount());
		model.addAttribute("totalAdvancePayment", summaryDto.getSapAmount());
		model.addAttribute("outstandingBalance", summaryDto.getOutstandingBalance());
		summaryDto = null;

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		LOGGER.info("Successfully generated the report");
		if (currencyId == 1) {
			return "SupplierAcctHistory.jasper";
		} else {
			return "SupplierAcctHistoryUSD.jasper";
		}
	}
}
