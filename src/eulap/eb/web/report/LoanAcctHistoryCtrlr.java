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
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.SupplierAccountService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.report.LoanAcctHistoryService;
import eulap.eb.web.dto.LoanAcctHistoryDto;

/**
 * Controller class that will handle request for loan account history report generation

 */

@Controller
@RequestMapping("loanAcctHistory")
public class LoanAcctHistoryCtrlr {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private LoanAcctHistoryService loanAcctHistoryService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private SupplierAccountService supplierAcctService;

	@RequestMapping (method = RequestMethod.GET)
	public String showStudentListing(Model model, HttpSession session) throws ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		return "LoanAccountHistory.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="divisionId", required=true) int divisionId,
			@RequestParam (value="supplierId", required=true) int supplierId,
			@RequestParam (value="supplierAcctId", required=true) Integer supplierAcctId,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		List<LoanAcctHistoryDto> loanAcctHistoryDtos = loanAcctHistoryService.getLoanAcctHistoryData(companyId,
				divisionId, supplierId, supplierAcctId, dateFrom, dateTo);
		model.addAttribute("datasource", loanAcctHistoryDtos);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		Supplier supplier = supplierService.getSupplier(supplierId);
		model.addAttribute("lender", supplier.getName());
		SupplierAccount supplierAccount = supplierAcctService.getSupplierAcct(supplierAcctId);
		model.addAttribute("lenderAccount", supplierAccount.getName());
		String strDate = DateUtil.setUpDate(DateUtil.formatDate(dateFrom), DateUtil.formatDate(dateTo));
		model.addAttribute("dateFromAndTo", strDate);
		LoanAcctHistoryDto loanAcctHistoryDto =
				loanAcctHistoryService.getTotalAmountValues(companyId, divisionId, supplierId, supplierAcctId);
		model.addAttribute("loanAmount", loanAcctHistoryDto.getLoanAmount());
		model.addAttribute("interestPayment", loanAcctHistoryDto.getInterest());
		model.addAttribute("principalPayment", loanAcctHistoryDto.getPrincipal());
		model.addAttribute("outstandingBalance", loanAcctHistoryDto.getBalance());
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		return "LoanAccountHistory.jasper";
	}
}
