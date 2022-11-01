package eulap.eb.web.form.inv;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collection;
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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidation;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.PettyCashVoucherLiquidationService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class that will handle requests for {@link PettyCashVoucherLiquidation} object.

 */

@Controller
@RequestMapping("/pettyCashVoucherLiquidation")
public class PettyCashVoucherLiquidationController {
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private WorkflowServiceHandler workflowService;
	@Autowired
	private PettyCashVoucherLiquidationService pcvlService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{divisionId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable (value="divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		PettyCashVoucherLiquidation pcvl = new PettyCashVoucherLiquidation();
		Integer companyId = 0;
		pcvl.setDivisionId(divisionId);
		pcvl.setDivision(divisionService.getDivision(divisionId));
		if (pId != null) {
			pcvl = pcvlService.getPettyCashVoucherLiquidation(pId);
			companyId = pcvl.getCompanyId();
			Integer ebObjectId = pcvl.getEbObjectId();
			if(ebObjectId != null) {
				pcvl.setReferenceDocuments(refDocumentService.processReferenceDocs(ebObjectId));
			}
		} else {
			pcvl.setPcvlDate(new Date());
		}
		return loadForm(companyId, pcvl, user, model);
	}

	private String loadForm(Integer companyId, PettyCashVoucherLiquidation pcvl, User user, Model model) {
		pcvl.serializePcvlLines();
		pcvl.serializeReferenceDocuments();
		model.addAttribute("pcvl", pcvl);
		return "PettyCashVoucherLiquidationForm.jsp";
	}

	@RequestMapping(value="{divisionId}/showPCVReferences", method=RequestMethod.GET)
	public String showPCVReferenceForm(@PathVariable(value="divisionId") int divisionId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, 0);
		model.addAttribute("companies", companies);
		model.addAttribute("divisionId", divisionId);
		Integer companyId = companies != null ? companies.iterator().next().getId() : null;
		loadPCVReferences(companyId, divisionId, null, "", null, null, null, PageSetting.START_PAGE, model);
		return "PCVReferenceForm.jsp";
	}

	private void loadPCVReferences(Integer companyId, Integer divisionId, Integer userCustodianId,
			String requestor, Integer pcvNumber, Date dateFrom, Date dateTo, Integer pageNumber, Model model) {
		Page<PettyCashVoucher> pcvs = pcvlService.getPCVLiquidations(companyId, divisionId, userCustodianId, requestor, pcvNumber, dateFrom, dateTo, pageNumber);
		model.addAttribute("pcvs", pcvs);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getPCVReferences", method=RequestMethod.GET)
	public String getPCVRefs(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="userCustodianId", required=false) Integer userCustodianId,
			@RequestParam (value="requestor", required=false) String requestor,
			@RequestParam (value="pcvNumber", required=false) Integer pcvNumber,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		loadPCVReferences(companyId, divisionId, userCustodianId, requestor, pcvNumber, dateFrom, dateTo, pageNumber, model);
		return "PCVReferenceTable.jsp";
	}

	@RequestMapping(value = "/getPCVById", method = RequestMethod.GET)
	public @ResponseBody String getById (@RequestParam(value="pettyCashVoucherId", required=true) Integer pcvId,
		@RequestParam(value="pcvlId", required=false) Integer pcvlId,
		HttpSession session) {
		PettyCashVoucherLiquidation pcvl = pcvlService.convPCVtoPCVL(pcvId);
		String [] exclude = {"userCustodian", "referenceDocuments", "formWorkflow"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(pcvl, jsonConfig);
		pcvl = null; //Freeing up memory
		return jsonObject.toString();
	}

	@RequestMapping(value="{divisionId}/form", method=RequestMethod.POST)
	public String saveForm(@PathVariable (value="divisionId") int divisionId,
			@ModelAttribute("pcvl") PettyCashVoucherLiquidation pcvl, BindingResult result, HttpSession session,
			Model model) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		pcvl.deserializePcvlLines();
		pcvl.deserializeReferenceDocuments();
		synchronized (this) {
			pcvlService.validate(pcvl, result);
			if (result.hasErrors()) {
				return loadForm(pcvl.getCompanyId(), pcvl, user, model);
			}
			ebFormServiceHandler.saveForm(pcvl, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("ebObjectId", pcvl.getEbObjectId());
		model.addAttribute("formId", pcvl.getId());
		return "successfullySaved";
	}

	@RequestMapping(value="/viewForm", method=RequestMethod.GET)
	public String showViewForm(@RequestParam(value="pId") Integer pId, Model model, HttpSession session) throws ConfigurationException, IOException {
		PettyCashVoucherLiquidation pcvl = pcvlService.getPettyCashVoucherLiquidation(pId);
		if(pId != null) {
			if(pcvl.getEbObjectId() != null) {
				pcvl.setReferenceDocuments(refDocumentService.processReferenceDocs(pcvl.getEbObjectId()));
			}
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(pcvl.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(pId, pcvl.getWorkflowName(), user));
		}
		model.addAttribute("pcvl", pcvl);
		return "PettyCashVoucherLiquidationView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, pcvlService.getFormWorkflow(pId));
	}

	@RequestMapping(value="{divisionId}/search", method=RequestMethod.GET)
	public @ResponseBody String searchRepackingForms(@PathVariable("divisionId") int divisionId,
			@RequestParam(value="criteria", defaultValue="")
			String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = pcvlService.searchPettyCashVoucherLiquidations(divisionId, criteria, user);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/pdf", method=RequestMethod.GET)
	public String generateReport(@RequestParam(value="pId") int pId, Model model) {
		PettyCashVoucherLiquidation pcvl = pcvlService.getPettyCashVoucherLiquidation(pId);
		List<PettyCashVoucherLiquidation> pcvs = new ArrayList<PettyCashVoucherLiquidation>();
		pcvs.add(pcvl);
		JRDataSource dataSource = new JRBeanCollectionDataSource(pcvs);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		model.addAttribute("reportTitle", "PETTY CASH VOUCHER LIQUIDATION");
		Company company = pcvl.getCompany();
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());

		FormWorkflow workflowLog = pcvl.getFormWorkflow();
		for (FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy",log.getCreated().getUserFullName());
				model.addAttribute("creatorPosition", position.getName());
			}
		}
		return "PettyCashVoucherLiquidation.jasper";
	}
}
