package eulap.eb.web.report;

import java.util.Collection;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ReportSourcesHandler;
import eulap.eb.service.report.ArLineAnalysisReportParam;
import eulap.eb.service.report.ArLineAnalysisReportServiceImpl;

/**
 * A controller class the handles the AR Line Analysis report generation.

 */
@Controller
@RequestMapping("arLineAnalysisReport")
public class ArLineAnalysisReportController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArLineAnalysisReportServiceImpl analysisReportServiceImpl;
	@Autowired
	private ReportSourcesHandler sourcesHandler;
	@Autowired
	private DivisionService divisionService;
	private final static String REPORT_TITLE = "AR Line Analysis";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showArLineAnalysisReport(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		//Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		//Division
		Collection<Division> divisions = divisionService.getActiveDivisions(0);
		model.addAttribute("divisions", divisions);
		//Sources
		model.addAttribute("sources", sourcesHandler.getAllARLineAnalysisSources());
		return "ArLineAnalysisReport.jsp";
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReportPDFexcel (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="sourceId", required=false) Integer sourceId,
			@RequestParam (value="divisionId", required=false) int divisionId,
			@RequestParam (value="serviceId", required=true) Integer serviceId,
			@RequestParam (value="unitOfMeasureId", required=false, defaultValue="-1") Integer unitOfMeasureId,
			@RequestParam (value="transactionDateFrom", required=false) Date transactionDateFrom,
			@RequestParam (value="transactionDateTo", required=false) Date transactionDateTo,
			@RequestParam (value="glDateFrom", required=false) Date glDateFrom,
			@RequestParam (value="glDateTo", required=false) Date glDateTo,
			@RequestParam (value="customerId", required=false, defaultValue="-1") Integer customerId,
			@RequestParam (value="customerAcctId", required=false, defaultValue="-1") Integer customerAcctId,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		ArLineAnalysisReportParam param = new ArLineAnalysisReportParam();
		param.setSourceId(sourceId);
		param.setDivisionId(divisionId);
		param.setCompanyId(companyId);
		param.setServiceId(serviceId);
		param.setUnitOfMeasureId(unitOfMeasureId);
		param.setTransactionDateFrom(transactionDateFrom);
		param.setTransactionDateTo(transactionDateTo);
		param.setGlDateFrom(glDateFrom);
		param.setGlDateTo(glDateTo);
		param.setCustomerId(customerId);
		param.setCustomerAcctId(customerAcctId);

		JRDataSource dataSource = analysisReportServiceImpl.generateReport(param);
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
		return "ARLineAnalysisReport.jasper";
	}
}
