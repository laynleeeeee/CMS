
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
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.ArTransactionTypeService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.TermService;
import eulap.eb.service.TransactionClassificationService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ArTransactionValidator;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class for Ar Transactions.

 */
@Controller
@RequestMapping("/arTransactionForm")
public class ArTransactionController {
	private final Logger logger = Logger.getLogger(ArTransactionController.class);
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private TermService termService;
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private ArTransactionTypeService arTransactionTypeService;
	@Autowired
	private ArTransactionValidator arTransactionValidator;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private EBFormServiceHandler eBFormServiceHandler;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private WorkflowServiceHandler workflowService;
	@Autowired
	private TransactionClassificationService transactionClassificationService;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{divisionId}/form", method = RequestMethod.GET)
	public String showForm (@PathVariable(value="divisionId") int divisionId,
			@RequestParam (value="pId", required = false) Integer pId,
			HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ArTransaction arTransaction = new ArTransaction();
		arTransaction.setDivisionId(divisionId);
		arTransaction.setDivision(divisionService.getDivision(divisionId));
		if (pId != null) {
			logger.info("Editing the AR Transaction form with id: "+pId);
			arTransaction = arTransactionService.getARTransaction(pId);
			loadAttributes(arTransaction, pId, model);
			Integer ebObjectId = arTransaction.getEbObjectId();
			if (ebObjectId != null) {
				logger.info("Retrieving the list of reference documents.");
				arTransaction.setReferenceDocuments(refDocumentService.getReferenceDocuments(ebObjectId));
			}
		} else {
			Date currentDate = new Date();
			arTransaction.setTransactionDate(currentDate);
			arTransaction.setGlDate(currentDate);
			logger.info("Showing the AR Transaction form.");
		}
		return loadArTransactionForm(arTransaction, user, model);
	}

	private void loadAttributes (ArTransaction arTransaction, int pId, Model model){
		arTransaction.setArServiceLines(arTransactionService.getArServiceLine(arTransaction));
		arTransaction.setTotalAmount(arTransactionService.getTotalAmount(arTransaction));
		model.addAttribute("pId", pId);
		model.addAttribute("sequenceNo", arTransaction.getSequenceNumber());
	}

	private void loadSelections (User user, ArTransaction arTransaction, Model model) {
		model.addAttribute("terms", termService.getTerms(arTransaction.getTermId()));
		model.addAttribute("transactionTypes", arTransactionTypeService.getTransactionTypes(user));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(arTransaction.getCurrencyId()));
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("transactionClassifications", transactionClassificationService.getAllTransactionClassifications(user));
	}

	@RequestMapping(value = "/getCustomerAccount", method = RequestMethod.GET)
	public @ResponseBody String getCustomersAccount (@RequestParam (value="customerId") int customerId,
			@RequestParam (value="customerAcctId", required=false) Integer customerAcctId){
		List<ArCustomerAccount> customerAccounts = arCustomerAcctService.getCustomerAccounts(customerId, customerAcctId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(customerAccounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/getTerm", method = RequestMethod.GET)
	public @ResponseBody String getTerm (@RequestParam (value="customerAccountId") Integer customerAccountId,
			HttpSession session) {
		Term term = termService.getTermByCustomerAccount(customerAccountId, CurrentSessionHandler.getLoggedInUser(session));
		JSONObject jsonObject = JSONObject.fromObject(term);
		return jsonObject.toString();
	}

	@RequestMapping(value = "/getTotalTrAmount", method = RequestMethod.GET)
	public @ResponseBody String getTotalTransactionAmount (@RequestParam (value = "arCustomerAcctId") Integer arCustomerAcctId){
		return arTransactionService.getTotalTransactionAmount(arCustomerAcctId).toString();
	}

	@RequestMapping (value = "{divisionId}/form", method = RequestMethod.POST)
	public String saveNewTransaction (@PathVariable(value="divisionId") int divisionId,
			@ModelAttribute ("arTransaction") ArTransaction arTransaction, 
			BindingResult result, Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException  {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		arTransaction.deserializeArLines();
		arTransaction.deserializeArServiceLines();
		arTransaction.deserializeReferenceDocuments();
		arTransaction.setTransactionTypeId(arTransactionService.setTransactionType(divisionId));
		logger.info("Validating the values of the AR Transaction before saving.");
		synchronized (this) {
			arTransactionValidator.validate(arTransaction, result);
			if (result.hasErrors()) {
				logger.info("Form has error/s. Reloading  the form.");
				return loadArTransactionForm(arTransaction, user, model);
			}
			logger.info("Saving the AR Transaction.");
			eBFormServiceHandler.saveForm(arTransaction, user);
		}
		model.addAttribute("ebObjectId", arTransaction.getEbObjectId());
		return "successfullySaved";
	}

	private String loadArTransactionForm (ArTransaction arTransaction, User user, Model model) {
		loadSelections(user, arTransaction, model);
		arTransaction.serializeArLines();
		arTransaction.serializeArServiceLines();
		arTransaction.serializeReferenceDocuments();
		model.addAttribute("arTransaction", arTransaction);
		if(arTransaction.getDivisionId() != null) {
			return "NSBArTransactionForm.jsp";
		}
		return "ArTransactionFormV2.jsp";
	}

	@RequestMapping(value = "/form/viewForm", method = RequestMethod.GET)
	public String showForm (@RequestParam (value="pId", required = false)Integer pId, Model model, HttpSession session) throws ConfigurationException, IOException {
		logger.info("Loading the AR Transaction view form.");
		ArTransaction arTransaction = null;
		boolean hasReference = true;
		if (pId != null){
			logger.debug("Retreive AR Transaction form id " + pId);
			arTransaction = arTransactionService.getARTransaction(pId);
			arTransaction.setArServiceLines(arTransactionService.getArServiceLine(arTransaction));
			if(arTransaction.getEbObjectId() != null) {
				arTransaction.setReferenceDocuments(refDocumentService.processReferenceDocs(arTransaction.getEbObjectId()));
			}
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(arTransaction.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(pId, arTransaction.getWorkflowName(), user));
		}
		if(arTransaction.getReferenceDocuments().isEmpty()){
			hasReference = false;
		}
		model.addAttribute("hasReference", hasReference);
		model.addAttribute("arTransaction", arTransaction);
		logger.info("Successfully loaded AR Transaction form");
		if(arTransaction.getDivisionId() != null) {
			return "NSBArTransactionView.jsp";
		}
		return "ArTransactionView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, arTransactionService.getFormWorkflow(pId));
	}

	@RequestMapping(value="/{divisionId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchARTransWithDiv(@PathVariable(value="divisionId") int divisionId,
			@RequestParam(required=true, value="criteria", defaultValue="")
	String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = arTransactionService.search(user, criteria, divisionId);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
