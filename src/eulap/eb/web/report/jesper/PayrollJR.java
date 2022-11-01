package eulap.eb.web.report.jesper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.payroll.PayrollService;

/**
 * A controller class the handles the Payroll print out.

 */
@Controller
@RequestMapping("payrollPrint")
public class PayrollJR {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PayrollService payrollService;

	@RequestMapping (value="/payslip", method=RequestMethod.GET)
	public String generatePayslip (@RequestParam (value="payrollId", required=true) int payrollId,
			Model model, HttpSession session) {
		Payroll payroll = payrollService.getPayroll(payrollId);
		JRDataSource dataSource = new JRBeanCollectionDataSource(payrollService.getPayrollEmployeeSalaryDetails(payrollId));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		Company company = companyService.getCompany(payroll.getCompanyId());
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("dateFrom", payroll.getDateFrom());
		model.addAttribute("dateTo", payroll.getDateTo());
		return "PayslipPrintOut.jasper";
	}

	@RequestMapping (value="/payrollReport", method=RequestMethod.GET)
	public String generatePayroll (@RequestParam (value="pId", required=true) int payrollId,
			Model model, HttpSession session) {
		List<Payroll> payrolls = new ArrayList<>();
		Payroll payroll = payrollService.getPayrollWithDetails(payrollId);
		payrolls.add(payroll);
		JRDataSource dataSource = new JRBeanCollectionDataSource(payrolls);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		Company company = companyService.getCompany(payroll.getCompanyId());
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("dateFrom", payroll.getDateFrom());
		model.addAttribute("dateTo", payroll.getDateTo());
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		return "PayrollPrintOut.jasper";
	}
}
