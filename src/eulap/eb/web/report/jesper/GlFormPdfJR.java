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

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.GeneralLedgerService;
import eulap.eb.service.GlEntryService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A class the generate PDF file format for general ledger form.

 */
@Controller
@RequestMapping ("glFormPDF")
public class GlFormPdfJR {
	@Autowired
	private GeneralLedgerService generalLedgerService;
	@Autowired
	private GlEntryService glEntryService;
	@Autowired
	private CompanyService companyService;

	@RequestMapping (value="/pdf", method = RequestMethod.GET)
	private String showFormPdf (@RequestParam (value="pId", required = true)Integer pId, 
			Model model, HttpSession session){
		getParams(pId, model, "pdf", session);
//		return "GlFormPdf.jasper";
		return "GeneralLedger.jasper";
	}

	private void getParams(Integer pId, Model model, String format, HttpSession session) {
		GeneralLedger gl = generalLedgerService.getGLPdf(pId);
		List<GeneralLedger> glDataSource = new ArrayList<GeneralLedger>();
		glDataSource.add(gl);

		JRDataSource dataSource = new JRBeanCollectionDataSource(glDataSource);
		model.addAttribute("format", format);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("description", gl.getComment());
		model.addAttribute("glDate", DateUtil.formatDate(gl.getGlDate()));
		model.addAttribute("sequenceNo", gl.getSequenceNo());
		model.addAttribute("divisionName", gl.getDivision().getName());
		model.addAttribute("totalDebit", glEntryService.getTotalAmt(gl.getGlEntries(), Boolean.TRUE));
		model.addAttribute("totalCredit", glEntryService.getTotalAmt(gl.getGlEntries(), Boolean.FALSE));
		model.addAttribute("currency", gl.getCurrency().getName());

		if (gl.getCreatedDate() != null)
			model.addAttribute("preparedDate", DateUtil.formatDate(gl.getCreatedDate()));
		FormWorkflow formWorkflow = gl.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", user.getUserFullName());
				model.addAttribute("creatorPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.REVIEWED_ID) {
				model.addAttribute("reviewedBy", user.getUserFullName());
				model.addAttribute("reviewerPosition", position.getName());
				model.addAttribute("postedDate", DateUtil.formatDate(gl.getCreatedDate()));
			}
		}
		//Company information
		int companyId = 0;
		for (GlEntry glEntry : gl.getGlEntries()) {
			companyId = glEntry.getAccountCombination().getCompanyId();
			break;
		}
		Company company = companyService.getCompany(companyId);
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
		model.addAttribute("reportTitle" , "JOURNAL VOUCHER");
	}

	@RequestMapping (value="/html", method = RequestMethod.GET)
	private String generateHTMLPrintout (@RequestParam (value="pId", required = true)Integer pId, 
			Model model, HttpSession session){
		getParams(pId, model, "html", session);
		return "GlFormPdfHTML.jasper";
	}
}
