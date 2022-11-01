package eulap.eb.web;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PettyCashReplenishmentLine;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.PettyCashReplenishmentService;
import eulap.eb.service.UserCustodianService;
import eulap.eb.web.dto.APLinesDto;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class for Petty Cash Replenishment form

 */

@Controller
@RequestMapping("pettyCashReplenishment")
public class PettyCashReplenishmentController {
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private PettyCashReplenishmentService pcrService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private UserCustodianService userCustodianService;
	@Autowired
	private CompanyService companyService;

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/{divisionId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable("typeId") int typeId,
			@PathVariable("divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId, Model model,
			HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		APInvoice apInvoice = new APInvoice();
		apInvoice.setDivisionId(divisionId);
		apInvoice.setDivision(divisionService.getDivision(divisionId));
		apInvoice.setInvoiceTypeId(typeId);
		Integer companyId = 0;
		if (pId != null) {
			apInvoice = pcrService.getApInvoicePcr(pId);
			companyId = apInvoice.getCompanyId();
		} else {
			Date currentDate = new Date();
			apInvoice.setGlDate(currentDate);
		}
		return loadForm(apInvoice, user, model, typeId, companyId);
	}

	private String loadForm(APInvoice apInvoice, User user, Model model, int typeId, Integer companyId) {
		apInvoice.serializePcrls();
		apInvoice.serializeApLines();
		apInvoice.serializeSummarizedApLines();
		apInvoice.serializeReferenceDocuments();
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		model.addAttribute("userCustodians", userCustodianService.getUserCustodiansByUser(apInvoice.getCompanyId(),
				apInvoice.getDivisionId(), user, apInvoice.getUserCustodianId()));
		model.addAttribute("apInvoice", apInvoice);
		return "PettyCashReplenishmentForm.jsp";
	}

	@RequestMapping(value="{typeId}/{divisionId}/form", method=RequestMethod.POST)
	public String submitForm(@PathVariable("typeId") int typeId,
			@PathVariable("divisionId") int divisionId,
			@ModelAttribute("apInvoice") APInvoice apInvoice, BindingResult result,
			Model model, HttpSession session) throws ClassNotFoundException, IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		apInvoice.deserializePcrls();
		apInvoice.deserializeApLines();
		apInvoice.deserializeSummarizedApLines();
		apInvoice.deserializeReferenceDocuments();
		synchronized (this) {
			pcrService.validateForm(apInvoice, result);
			if (result.hasErrors()) {
				return loadForm(apInvoice, user, model, typeId, apInvoice.getCompanyId());
			}
			ebFormServiceHandler.saveForm(apInvoice, user);
		}
		model.addAttribute("ebObjectId", apInvoice.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping (value="/populateTable",method = RequestMethod.GET)
	public @ResponseBody String getReplenishments(@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="userCustodianId", required=false) Integer userCustodianId,
			@RequestParam(value="apInvoiceId", required=false) Integer apInvoiceId,
			Model model, HttpSession session) throws IOException {
		List<PettyCashReplenishmentLine> pcrls = pcrService.getReplenishments(divisionId, userCustodianId, apInvoiceId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(pcrls, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/populateSummaryTable",method = RequestMethod.GET)
	public @ResponseBody String getSummaryOfAccounts(@RequestParam(value="accounts", required=false) String accounts,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			Model model, HttpSession session) {
		APLinesDto apLinesDto = pcrService.getApLinesAndSummarizedLines(accounts, companyId, divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(apLinesDto, jConfig);
		return jsonObject.toString();
	}

	@RequestMapping (value="/view", method = RequestMethod.GET)
	public String viewForm(@RequestParam(value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		APInvoice apInvoice = pcrService.getApInvoicePcr(pId);
		apInvoice.setDivision(divisionService.getDivision(apInvoice.getDivisionId()));
		model.addAttribute("apInvoice", apInvoice);
		model.addAttribute("pcfAmount", getPcfAmount(apInvoice.getCompanyId(), apInvoice.getUserCustodianId(), apInvoice.getGlDate()));
		return "PettyCashReplenishmentView.jsp";
	}

	@RequestMapping(value="/print", method = RequestMethod.GET)
	private String generatePrintout(@RequestParam(value="pId", required=true) Integer pId,
			Model model, HttpSession session) throws IOException {
		APInvoice apInvoice = pcrService.getApInvoicePcr(pId);
		apInvoice.setDivision(divisionService.getDivision(apInvoice.getDivisionId()));
		model.addAttribute("datasource", new JRBeanCollectionDataSource(apInvoice.getSummarizedApLines()));
		setCommonParams(apInvoice, model, session);
		return "PettyCashReplenishment.jasper";
	}

	public void setCommonParams(APInvoice apInvoice, Model model, HttpSession session) {
		model.addAttribute("format", "pdf");
		model.addAttribute("title" , "PETTY CASH REPLENISHMENT");
		model.addAttribute("sequenceNumber", apInvoice.getSequenceNumber());
		model.addAttribute("glDate", apInvoice.getGlDate());
		model.addAttribute("birLogo", "bir_logo.png");
		model.addAttribute("custodianName", apInvoice.getUserCustodian().getCustodianAccount().getCustodianName());
		model.addAttribute("pcfAmount", getPcfAmount(apInvoice.getCompanyId(), apInvoice.getUserCustodianId(), apInvoice.getGlDate()));

		setCompanyParams(apInvoice.getCompany(), model);
		setWorkflowParams(apInvoice.getFormWorkflow(), model);
	}

	private void setCompanyParams(Company company, Model model) {
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyContactNo", company.getContactNumber());
		model.addAttribute("companyLogo", company.getLogo());
	}

	private void setWorkflowParams(FormWorkflow formWorkflow, Model model) {
		for (FormWorkflowLog log : formWorkflow.getFormWorkflowLogs()) {
			String name = log.getCreated().getUserFullName();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", name);
				model.addAttribute("createdDate", log.getCreatedDate());
				model.addAttribute("creatorPosition", log.getCreated().getPosition().getName());
			} else if (log.getFormStatusId() == FormStatus.CHECKED_ID) {
				model.addAttribute("checkedBy", name);
				model.addAttribute("checkedDate", log.getCreatedDate());
				model.addAttribute("checkerPosition", log.getCreated().getPosition().getName());
			}
		}
	}

	@RequestMapping(value="{typeId}/{divisionId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchInvoices(@PathVariable(value="typeId") int typeId,
			@PathVariable(value="divisionId") int divisionId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = pcrService.searchInvoices(typeId, divisionId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	private Double getPcfAmount(Integer companyId, Integer userCustodianId, Date asOfDate) {
		return pcrService.getPCFAmount(companyId, userCustodianId, asOfDate);
	}

	@RequestMapping(value="/getPCFAmount", method = RequestMethod.GET)
	public @ResponseBody String getPCFAmount (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="userCustodianId", required=true) Integer userCustodianId,
			@RequestParam (value="pcrDate", required=true) String pcrDate,
			HttpSession session) {
		return getPcfAmount(companyId, userCustodianId, DateUtil.parseDate(pcrDate)).toString();
	}
}
