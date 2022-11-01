package eulap.eb.web.report;

import javax.servlet.http.HttpSession;

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
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.ArTransactionRegisterServiceImpl;
import eulap.eb.service.report.TransactionAgingParam;
import eulap.eb.service.report.TransactionAgingService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * A controller class that handles the transaction aging report generation.

 */
@Controller
@RequestMapping("transactionAging")
public class TransactionAgingController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ArTransactionService transTypeService;
	@Autowired
	private ArTransactionRegisterServiceImpl transactionRegisterServiceImpl;
	@Autowired
	private TransactionAgingService serviceImpl;

	private final static String REPORT_TITLE = "AR TRANSACTION AGING";

	@RequestMapping (method = RequestMethod.GET)
	public String showInvoiceAging(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("invoiceTypes", transTypeService.getAllTransactionTypes(user));
		model.addAttribute("division", divisionService.getActiveDivisions(0));
		model.addAttribute("transactionClassifications", transactionRegisterServiceImpl.showTransactionClassification(user));
		model.addAttribute("companies", companyService.getCompanies(user));
		return "ArTransactionAgingReport.jsp";
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="typeId", required=true) int typeId,
			@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) int divisionId,
			@RequestParam (value="transactionClassificationId", required=false) int transactionClassificationId,
			@RequestParam (value="transTypeId", required=false, defaultValue="-1") Integer transTypeId,
			@RequestParam (value="customerId", required=false, defaultValue="-1") Integer customerId,
			@RequestParam (value="customerAcctId", required=false, defaultValue="-1") Integer customerAcctId,
			@RequestParam (value="showTrans", required=false, defaultValue="true") boolean showTrans,
			@RequestParam (value="ageBasis", required=false, defaultValue="1") int ageBasis,
			@RequestParam (value="asOfDate", required=false) String asOfDate,
			@RequestParam (value="classificationId", required=false) String classification,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session){
		TransactionAgingParam param = new TransactionAgingParam();
		param.setDivisionId(divisionId);
		param.setTransactionClassificationId(transactionClassificationId);
		param.setCustomerId(customerId);
		param.setCustomerAcctId(customerAcctId);
		param.setCompanyId(companyId);
		param.setTransTypeID(transTypeId);
		param.setAgeBasis(ageBasis);
		param.setShowTrans(showTrans);
		param.setTypeId(typeId);
		param.setClassification(classification);
		if (typeId == 2) {
			param.setAsOfDate(DateUtil.parseDate(asOfDate));
		}
		JRDataSource dataSource = serviceImpl.generateTransactionAging(param);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		company = null;
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("asOfDate", asOfDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		if (showTrans) {
			return "TransactionAgingReport.jasper";
		}
		return "TransactionAgingWithOutTransactionNumber.jasper";
	}
}
