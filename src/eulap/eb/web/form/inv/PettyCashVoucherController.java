package eulap.eb.web.form.inv;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.PettyCashVoucherService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.UserCustodianService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;

/**
 * Controller class that will handle requests for {@link PettyCashVoucher} object.

 */

@Controller
@RequestMapping("/pettyCashVoucher")
public class PettyCashVoucherController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private WorkflowServiceHandler workflowService;
	@Autowired
	private PettyCashVoucherService pcvService;
	@Autowired
	private UserCustodianService userCustodianService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{divisionId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable (value="divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		PettyCashVoucher pcv = new PettyCashVoucher();
		Integer companyId = 0;
		pcv.setDivisionId(divisionId);
		pcv.setDivision(divisionService.getDivision(divisionId));
		if (pId != null) {
			pcv = pcvService.getPettyCashVoucher(pId);
			companyId = pcv.getCompanyId();
			Integer ebObjectId = pcv.getEbObjectId();
			if(ebObjectId != null) {
				pcv.setReferenceDocuments(refDocumentService.processReferenceDocs(ebObjectId));
			}
		} else {
			pcv.setPcvDate(new Date());
		}
		return loadForm(companyId, pcv, user, model);
	}

	private String loadForm(Integer companyId, PettyCashVoucher pcv, User user, Model model) {
		pcv.serializeReferenceDocuments();
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		model.addAttribute("userCustodians", userCustodianService.getUserCustodiansByUser(pcv.getCompanyId(), pcv.getDivisionId(), user, pcv.getUserCustodianId()));
		model.addAttribute("pcv", pcv);
		return "PettyCashVoucherForm.jsp";
	}

	@RequestMapping(value="{divisionId}/form", method=RequestMethod.POST)
	public String saveForm(@PathVariable (value="divisionId") int divisionId,
			@ModelAttribute("pcv") PettyCashVoucher pcv, BindingResult result, HttpSession session,
			Model model) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		pcv.deserializeReferenceDocuments();
		synchronized (this) {
			pcvService.validate(pcv, result);
			if (result.hasErrors()) {
				return loadForm(pcv.getCompanyId(), pcv, user, model);
			}
			ebFormServiceHandler.saveForm(pcv, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("ebObjectId", pcv.getEbObjectId());
		model.addAttribute("formId", pcv.getId());
		return "successfullySaved";
	}

	@RequestMapping(value="/viewForm", method=RequestMethod.GET)
	public String showViewForm(@RequestParam(value="pId") Integer pId, Model model, HttpSession session) throws ConfigurationException, IOException {
		PettyCashVoucher pcv = pcvService.getPettyCashVoucher(pId);
		boolean hasReference = true;
		if(pId != null) {
			if(pcv.getEbObjectId() != null) {
				pcv.setReferenceDocuments(refDocumentService.processReferenceDocs(pcv.getEbObjectId()));
			}
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(pcv.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(pId, pcv.getWorkflowName(), user));
		}
		if(pcv.getReferenceDocuments().isEmpty()){
			hasReference = false;
		}
		model.addAttribute("hasReference", hasReference);
		model.addAttribute("pcv", pcv);
		return "PettyCashVoucherView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, pcvService.getFormWorkflow(pId));
	}

	@RequestMapping(value="{divisionId}/search", method=RequestMethod.GET)
	public @ResponseBody String searchRepackingForms(@PathVariable("divisionId") int divisionId,
			@RequestParam(value="criteria", defaultValue="")
			String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = pcvService.searchPettyCashVouchers(divisionId, criteria, user);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/pdf", method=RequestMethod.GET)
	public String generateReport(@RequestParam(value="pId") int pId, Model model) {
		PettyCashVoucher pcv = pcvService.getPettyCashVoucher(pId);
		List<PettyCashVoucher> pcvs = new ArrayList<PettyCashVoucher>();
		pcvs.add(pcv);
		JRDataSource dataSource = new JRBeanCollectionDataSource(pcvs);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		model.addAttribute("reportTitle", "PETTY CASH VOUCHER");
		Company company = pcv.getCompany();
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());

		FormWorkflow workflowLog = pcv.getFormWorkflow();
		for (FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy",
						user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("creatorPosition", position.getName());
			}
		}
		return "PettyCashVoucher.jasper";
	}
}
