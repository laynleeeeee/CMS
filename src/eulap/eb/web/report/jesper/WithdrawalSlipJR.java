package eulap.eb.web.report.jesper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.service.CompanyService;
import eulap.eb.service.WithdrawalSlipService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A class that generate PDF file format of AR Receipt.

 */
@Controller
@RequestMapping("withdrawalSlipPDF")
public class WithdrawalSlipJR {
	@Autowired
	private WithdrawalSlipService withdrawalSlipService;
	@Autowired
	private CompanyService companyService;
	private final static String REPORT_TITLE = "WITHDRAWAL SLIP";

	@RequestMapping (value="/pdf", method = RequestMethod.GET)
	private String showReport (@RequestParam (value="pId", required = true)Integer pId,
			Model model, HttpSession session){
		getParams(pId, model, "pdf", session);
		return "WithdrawalSlipForm.jasper";
	}

	private void getParams(Integer pId, Model model, String format, HttpSession session) {
		WithdrawalSlip withdrawalSlip = withdrawalSlipService.getWithdrwalSlipForViewingEdit(pId, false);
		List<WithdrawalSlip> withdrawalSlips = new ArrayList<>();
		withdrawalSlips.add(withdrawalSlip);
		JRDataSource dataSource = new JRBeanCollectionDataSource(withdrawalSlips);
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("format", format);
		Company company = companyService.getCompany(withdrawalSlip.getCompanyId());
		model.addAttribute("companyLogo", company.getLogo());
		if(format == "pdf") {
			model.addAttribute("companyLogo", company.getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ company.getLogo());
		}
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		String companyTin = company.getTin();
		if(companyTin != null) {
			if(!companyTin.trim().isEmpty())
				companyTin = companyService.getTin(companyTin);
		}

		model.addAttribute("companyTin", companyTin);
		model.addAttribute("reportTitle" , REPORT_TITLE);
		String name = null;
		String position = null;
		FormWorkflow workflow =  withdrawalSlip.getFormWorkflow();
		for (FormWorkflowLog log : workflow.getFormWorkflowLogs()) {
			if(log.getFormStatusId() == FormStatus.CREATED_ID) {
				name = log.getCreated().getUserFullName().toUpperCase();
				position = StringFormatUtil.toTitleCase(log.getCreated().getPosition().getName());
				model.addAttribute("createdBy", name);
				model.addAttribute("createdByPosition", position);
				model.addAttribute("createdDate", log.getCreatedDate());
			}
			if(log.getFormStatusId() == FormStatus.APPROVED_ID) {
				name = log.getCreated().getUserFullName().toUpperCase();
				position = StringFormatUtil.toTitleCase(log.getCreated().getPosition().getName());
				model.addAttribute("approvedBy", name);
				model.addAttribute("approvedByPosition", position);
				model.addAttribute("approvedDate", log.getCreatedDate());
			}
		}
	}

	@RequestMapping (value="/html", method = RequestMethod.GET)
	private String showHTMLReport (@RequestParam (value="pId", required = true)Integer pId,
			Model model, HttpSession session){
		getParams(pId, model, "html", session);
		return "WithdrawalSlipFormHTML.jasper";
	}
}
