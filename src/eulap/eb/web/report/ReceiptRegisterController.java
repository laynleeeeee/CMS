package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;

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
import eulap.eb.domain.hibernate.ArReceiptType;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArReceiptTypeService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ReportSourcesHandler;
import eulap.eb.service.report.ReceiptRegisterParam;
import eulap.eb.service.report.ReceiptRegisterServiceImpl;

/**
 * A controller class the handles the receipt register report generation.

 */
@Controller
@RequestMapping("receiptRegister")
public class ReceiptRegisterController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArReceiptTypeService arReceiptTypeService;
	@Autowired
	private ReceiptRegisterServiceImpl registerServiceImpl;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ReportSourcesHandler sourcesHandler;

	private final static String REPORT_TITLE = "RECEIPT REGISTER";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showReceiptRegister(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		loadSelections(user, model);
		return "ReceiptRegisterReport.jsp";
	}
	

	private void loadSelections(User user, Model model) {
		//Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		// Receipt Type
		List<ArReceiptType> arReceiptTypes = arReceiptTypeService.getArReceiptTypes();
		model.addAttribute("arReceiptTypes", arReceiptTypes);
		//Sources
		List<String> sources = sourcesHandler.getAllArReceiptRegisterSources();
		model.addAttribute("sources", sources);
		//Workflow Statuses
		List<FormStatus> formStatuses = registerServiceImpl.getFormStatuses(user);
		model.addAttribute("formStatuses", formStatuses);
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPdfExcel (@RequestParam (value="companyId", required=true) int companyId,
			@RequestParam (value="source", required=false, defaultValue="-1") String source,
			@RequestParam (value="receiptTypeId", required=false, defaultValue="-1") int receiptTypeId,
			@RequestParam (value="receiptMethodId", required=false, defaultValue="-1") int receiptMethodId,
			@RequestParam (value="customerId", required=false, defaultValue="-1") int customerId,
			@RequestParam (value="customerAcctId", required=false, defaultValue="-1") int customerAcctId,
			@RequestParam (value="receiptNo", required=false) String receiptNo,
			@RequestParam (value="divisionId", required=false, defaultValue="-1") int divisionId,
			@RequestParam (value="receiptDateFrom", required=false) Date receiptDateFrom,
			@RequestParam (value="receiptDateTo", required=false) Date receiptDateTo,
			@RequestParam (value="maturityDateFrom", required=false) Date maturityDateFrom,
			@RequestParam (value="maturityDateTo", required=false) Date maturityDateTo,
			@RequestParam (value="amountFrom", required=false) Double amountFrom,
			@RequestParam (value="amountTo", required=false) Double amountTo,
			@RequestParam (value="wfStatusId", required=false, defaultValue="-1") int wfStatusId,
			@RequestParam (value="appliedStatusId", required=false, defaultValue="-1") int appliedStatusId,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		ReceiptRegisterParam param = new ReceiptRegisterParam();
		int sourceId = sourcesHandler.getArReceiptRegisterSourceId(source.trim());
		param.setSourceId(sourceId);
		param.setCompanyId(companyId);
		param.setReceiptTypeId(receiptTypeId);
		param.setReceiptMethodId(receiptMethodId);
		param.setCustomerId(customerId);
		param.setCustomerAcctId(customerAcctId);
		param.setReceiptNo(receiptNo);
		param.setDivisionId(divisionId);
		param.setReceiptDateFrom(receiptDateFrom);
		param.setReceiptDateTo(receiptDateTo);
		param.setMaturityDateFrom(maturityDateFrom);
		param.setMaturityDateTo(maturityDateTo);
		param.setAmountFrom(amountFrom);
		param.setAmountTo(amountTo);
		param.setWfStatusId(wfStatusId);
		param.setAppliedStatusId(appliedStatusId);

		JRDataSource dataSource = registerServiceImpl.generateReceiptRegister(param);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		return "ArReceiptRegister.jasper";
	}
}
