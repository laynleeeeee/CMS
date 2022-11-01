package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
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
import eulap.eb.domain.hibernate.LoanProceeds;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.LoanProceedsService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.TermService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.LoanProceedsValidator;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;

/**
 * A controller class for Loan Proceeds.

 */

@Controller
@RequestMapping ("/loanProceeds")
public class LoanProceedsController {
	private final Logger logger = Logger.getLogger(LoanProceedsController.class);
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private TermService termService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private WorkflowServiceHandler workflowService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private LoanProceedsService lpService;
	@Autowired
	private LoanProceedsValidator validator;
	@Autowired
	private DivisionService divisionService;

	private static final String FORM_TITLE = "LOAN PROCEEDS";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.GET)
	public String showForm (@PathVariable (value="typeId") Integer typeId,
			@RequestParam (value="pId", required=false) Integer pId,
			@RequestParam (value="isEdit", defaultValue="true", required=false) boolean isEdit,
			Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		LoanProceeds loanProceeds = new LoanProceeds();
		if (pId != null) {
			loanProceeds = lpService.getLoanProceeds(pId);
			loanProceeds.setLoanProceedsTypeId(typeId);
			loanProceeds.setlPlines(lpService.getLPLine(loanProceeds));
		} else {
			Date currentDate = new Date();
			loanProceeds.setLoanProceedsTypeId(typeId);
			loanProceeds.setDate(currentDate);
			loanProceeds.setGlDate(currentDate);
			int divisionId = lpService.getDivisionByLoanProceedsType(typeId);
			loanProceeds.setDivisionId(divisionId);
			loanProceeds.setDivision(divisionService.getDivision(divisionId));
		}
		return loadForm(loanProceeds, user, model);
	}

	private String loadForm(LoanProceeds loanProceeds, User user, Model model) {
		loanProceeds.serializeReferenceDocuments();
		loanProceeds.serializeLpLines();
		List<Company> companies = (List<Company>) companyService.getActiveCompanies(user, null, null, null);
		model.addAttribute("currencies", currencyService.getActiveCurrencies(loanProceeds.getCurrencyId()));
		model.addAttribute("companies", companies);
		model.addAttribute("terms", termService.getTerms(loanProceeds.getTermId()));
		model.addAttribute("loanProceeds", loanProceeds);
		model.addAttribute("title", FORM_TITLE);
		return "LoanProceedsForm.jsp";
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable (value="typeId") Integer typeId,
			@ModelAttribute ("loanProceeds") LoanProceeds loanProceeds, BindingResult result,
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException,
			ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loanProceeds.deserializeLpLines();
		loanProceeds.deserializeReferenceDocuments();
		synchronized (this) {
			loanProceeds.setlPlines(lpService.processLPLines(loanProceeds.getlPlines()));
			validator.validate(loanProceeds, result);
			if (result.hasErrors()) {
				return loadForm(loanProceeds, user, model);
			}
			lpService.setLpLineCombination(loanProceeds);
			ebFormServiceHandler.saveForm(loanProceeds, user);
		}
		model.addAttribute("ebObjectId", loanProceeds.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value = "{typeId}/form/viewForm", method = RequestMethod.GET)
	public String showForm (@PathVariable (value="typeId") Integer typeId,
			@RequestParam (value="pId", required = false)Integer pId,
			Model model, HttpSession session) throws ConfigurationException, IOException {
		logger.info("Retreiving Loan Proceeds form");
		LoanProceeds loanProceeds = null;
		boolean hasReference = true;
		if (pId != null){
			User user = CurrentSessionHandler.getLoggedInUser(session);
			logger.debug("Retreive Loan Proceeds of form id " + pId);
			loanProceeds = lpService.getLoanProceeds(pId);
			logger.debug("Retreiving LP Lines");
			loanProceeds.setlPlines(lpService.getLPLine(loanProceeds));
			if(loanProceeds.getReferenceDocuments().isEmpty()) {
				hasReference = false;
			}
			model.addAttribute("hasReference", hasReference);
			if (loanProceeds.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
				logger.info("Checking if has edit access.");
				model.addAttribute("hasEditAccess", hasEditAccess(pId, loanProceeds.getWorkflowName(), user));
			}
		} else {
			logger.error("Loan Proceeds form id is required.");
			throw new RuntimeException("AP invoice form id is required.");
		}
		model.addAttribute("loanProceeds", loanProceeds);
		logger.info("Successfully loaded Loan Proceeds form");
		return "LoanProceedsView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, lpService.getFormWorkflow(pId));
	}

	@RequestMapping (value="/pdf", method = RequestMethod.GET)
	private String showFormPdf (@RequestParam (value="pId", required = true)Integer pId,
			Model model, HttpSession session) throws Exception{
		LoanProceeds loanProceeds = lpService.getLoanProceedsPdf(pId);
		JRDataSource dataSource = new JRBeanCollectionDataSource(loanProceeds.getApLineDtos());
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		model.addAttribute("title", FORM_TITLE);
		model.addAttribute("date", loanProceeds.getDate());
		model.addAttribute("glDate", loanProceeds.getGlDate());
		model.addAttribute("description", loanProceeds.getDescription());
		model.addAttribute("sequenceNumber", loanProceeds.getSequenceNumber());
		model.addAttribute("currency", loanProceeds.getCurrency().getName());
		model.addAttribute("division", loanProceeds.getDivision().getName());

		Supplier supplier = loanProceeds.getSupplier();
		model.addAttribute("supplier", supplier.getName());
		model.addAttribute("supplierAccount", lpService.getSupplierAccount(loanProceeds.getSupplierAccountId()).getName());
		model.addAttribute("address", supplierService.processSupplierAddress(supplier));
		// Set company info
		Company company = companyService.getCompany(loanProceeds.getCompanyId());
		String companyLogo = company.getLogo();
		model.addAttribute("companyLogo", companyLogo);
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());

		if (loanProceeds.getCreatedDate() != null) {
			model.addAttribute("preparedDate", DateUtil.formatDate(loanProceeds.getCreatedDate()));
		}
		FormWorkflow formWorkflow = loanProceeds.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			String name = log.getCreated().getUserFullName();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("preparedBy", name);
				model.addAttribute("preparedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.REVIEWED_ID) {
				model.addAttribute("reviewedBy", name);
				model.addAttribute("reviewedPosition", position.getName());
				model.addAttribute("reviewedDate", DateUtil.formatDate(log.getCreatedDate()));
			}else if (log.getFormStatusId() == FormStatus.RECEIVED_ID) {
				model.addAttribute("receivedBy", name);
				model.addAttribute("receivedPosition", position.getName());
				model.addAttribute("receivedDate", DateUtil.formatDate(log.getCreatedDate()));
			}
		}
		return "LoanProceeds.jasper";
	}

	@RequestMapping(value="/{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchLoanProceeds(@PathVariable(value="typeId") int typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = lpService.searchLoanProceeds(typeId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}