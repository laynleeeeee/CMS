package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Collection;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.GlEntrySource;
import eulap.eb.domain.hibernate.GlStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.GeneralLedgerService;
import eulap.eb.service.GlEntryService;
import eulap.eb.validator.GlEntryValidator;
import eulap.eb.web.dto.GeneralLedgerDto;

/**
 * General ledger entry controller.

 *
 */
@Controller
@RequestMapping ("/gl")
public class GlEntryController {
	private final Logger logger = Logger.getLogger(GlEntryController.class);
	private static final String JOURNAL_ENTRY_ATTRIB_NAME = "journalEntries";
	@Autowired
	private AccountService accountService;
	@Autowired
	private GlEntryService glEntryService;
	@Autowired
	private AccountCombinationService accountCombinationService;
	@Autowired
	private GeneralLedgerService generalLedgerService;
	@Autowired
	private GlEntryValidator glEntryValidator;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showGlEntries (Model model) {
		model.addAttribute(JOURNAL_ENTRY_ATTRIB_NAME, null);
		return "GlEntryForm.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, value="/loadCompanies")
	public @ResponseBody String loadCompanies (HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection<Company> companies = companyService.getActiveCompanies(user, null, null, null);
		return getJSONString(companies);
	}

	@RequestMapping(value = "/getCompaniesByNameAndNumber", method = RequestMethod.GET)
	public @ResponseBody String getCompanies(
			@RequestParam(value="companyName", required=false) String companyName,
			@RequestParam(value="isActive", required=false) boolean isActive,
			@RequestParam(value="limit", required=false) Integer limit, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection<Company> companyList = companyService.getActiveCompanies(user, companyName, null, null);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(companyList, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/loadDivisions", params = {"companyId"})
	public @ResponseBody String loadDivisions (@RequestParam int companyId) {
		Collection<Division> divisions = glEntryService.getDivisions(companyId);
		return getJSONString(divisions);
	}

	@RequestMapping (method = RequestMethod.GET, value="/loadAccounts", params = {"companyId", "divisionId"})
	public @ResponseBody String loadAccounts (@RequestParam int companyId, @RequestParam int divisionId) {
		Collection<Account> accounts =  glEntryService.getAccounts(companyId, divisionId);
		return getJSONString(accounts, "relatedAccount", "accountType");
	}

	private String getJSONString (Object object, String ... exclude) {
		JsonConfig jConfig = new JsonConfig();
		String [] excludes = null;
		if (exclude.length > 0) {
			excludes = new String[exclude.length];
			int i = 0;
			for (String str : exclude)
				excludes[i++] = str;
		}
		if (excludes != null)
			jConfig.setExcludes(excludes);
		JSONArray jsonArray = JSONArray.fromObject(object, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/generateCompanyName", params = {"companyNumber"})
	public @ResponseBody String generateCompanyName (@RequestParam String companyNumber, HttpSession session) {
		if (companyNumber.trim().isEmpty()) {
			return "No company found";
		}
		AccountCombination accountCombination = accountCombinationService.getAccountCombination(companyNumber, "", "");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(accountCombination != null){
			Company activeCompany = companyService.getCompanyByNumber(companyNumber, user);
			if(activeCompany == null)
				return "No company found";
		}
		return accountCombination == null ? "No company found" :
			JSONObject.fromObject(accountCombination.getCompany()).toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/generateDivisionName", params = {"companyNumber",
			"divisionNumber"})
	public @ResponseBody String generateDivisionName (@RequestParam String companyNumber,
			@RequestParam String divisionNumber, HttpSession session) {
		if (divisionNumber.trim().isEmpty())
			return "No division found";
		AccountCombination accountCombination = accountCombinationService.getAccountCombination(companyNumber,
				divisionNumber, "");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(accountCombination != null){
			Division activeDivision = divisionService.getDivisionByDivNumber(divisionNumber, user);
			if (activeDivision == null) {
				return "No division found";
			}
		}
		return accountCombination == null ? "No division found" :
			JSONObject.fromObject(accountCombination.getDivision()).toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/generateAccountName", params = {"companyNumber",
		"divisionNumber", "accountNumber"})
	public @ResponseBody String generateAccountName (@RequestParam String companyNumber, 
			@RequestParam String divisionNumber, @RequestParam String accountNumber, HttpSession session) {
		if (accountNumber.trim().isEmpty())
			return "No account found";
		AccountCombination accountCombination = accountCombinationService.getAccountCombination(companyNumber,
				divisionNumber, accountNumber);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(accountCombination != null){
			Account activeAccout = accountService.getAccountByNumber(accountNumber, user);
			if (activeAccout == null)
				return "No account found";
		}
		return accountCombination == null ? "No account found" :
		JSONObject.fromObject(accountCombination.getAccount()).toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/loadAccountCombinationId", params = {"companyId", "divisionId", "accountId"})
	public @ResponseBody String loadAccountCombinationId (@RequestParam int companyId, @RequestParam int divisionId, @RequestParam int accountId) {
		return accountCombinationService.getAccountCombination(companyId, divisionId, accountId).getId() + "";
	}

	@RequestMapping (method = RequestMethod.GET, value="/getAccount", params = {"accountId"})
	public @ResponseBody String getAccount (@RequestParam int accountId) {
		JSONObject jsonObject = JSONObject.fromObject(accountService.getAccount(accountId));
		return jsonObject.toString();
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form/new")
	public String addJournalEntry (@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) throws IOException {
		glEntryService.processPrevSavedGLEntries();
		String returnMessage = "";
		GlEntry glEntry = new GlEntry();
		GeneralLedgerDto generalLedgerDto = new GeneralLedgerDto(new GeneralLedger(), glEntry);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(pId != null) {
			GeneralLedger gl = generalLedgerService.getGeneralLedger(pId);
			model.addAttribute("pId", pId);
			model.addAttribute("sequenceNo", gl.getSequenceNo());
			Collection<GlEntry> glEntries = gl.getGlEntries();
			generalLedgerDto = new GeneralLedgerDto(gl, glEntries);
			generalLedgerDto.setJsonData(glEntryService.convertToJsonString(glEntries));
			returnMessage = showGlEntryForm(user, model, gl.getGlStatusId(), gl.getGlEntrySourceId(),
					generalLedgerDto, true);
		} else {
			model.addAttribute("jvNumber", generalLedgerService.generateSequenceNo(generalLedgerDto.getDivisionId()));
			glEntry.setDebit(true);
			returnMessage = showGlEntryForm(user, model, 0, GlEntrySource.SOURCE_GENERAL_LEDGER, 
					generalLedgerDto, false);
		}
		return returnMessage;
	}

	@RequestMapping (method = RequestMethod.POST,  value = "/form/new")
	public String saveGlEntry ( @ModelAttribute ("generalLedgerDto") GeneralLedgerDto generalLedgerDto,
			BindingResult result,  Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		Collection<GlEntry> glEntries = glEntryService.convertToGlEntry(JSONArray.fromObject(generalLedgerDto.getJsonData()));
		generalLedgerDto.setGlEntries(glEntries);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		synchronized (this) {
			glEntryValidator.validate(generalLedgerDto, result);
			if (result.hasErrors()) {
				int glStatus = 0;
				if (generalLedgerDto.getGeneralLedger().getId() != 0 )
					glStatus = GlStatus.STATUS_NEW;
				return showGlEntryForm(user, model, glStatus, GlEntrySource.SOURCE_GENERAL_LEDGER, generalLedgerDto,
						generalLedgerDto.getGeneralLedger().getId() != 0);
			}

			GeneralLedger gl = generalLedgerDto.getGeneralLedger();
			gl.setGlEntries(generalLedgerDto.getGlEntries());
			ebFormServiceHandler.saveForm(gl, user);
		}
		model.addAttribute("ebObjectId", generalLedgerDto.getGeneralLedger().getEbObjectId());
		return "successfullySaved";
	}

	private String showGlEntryForm(User user, Model model, int glStatusId, int glEntrySourceId,
			GeneralLedgerDto generalLedgerDto, boolean isEdit) {
		model.addAttribute("accounts", accountService.getActiveAccounts(user, 0));
		model.addAttribute("glStatusId", glStatusId);
		model.addAttribute("glEntrySourceId", glEntrySourceId);
		model.addAttribute("generalLedgerDto", generalLedgerDto);
		if (generalLedgerDto.getGlEntries() != null)
			model.addAttribute("glEntryRows", generalLedgerDto.getGlEntries().size());
		model.addAttribute("pId", generalLedgerDto.getGeneralLedger().getId());
		model.addAttribute("isEdit", isEdit);
		if (isEdit)
			model.addAttribute("flag", "edit");
		return "GlEntryForm.jsp";
	}

	/**
	 * Get the general ledger object using the general ledger id.
	 * @param pId The general ledger id. 
	 * @param model The model
	 * @return The gl entry form.
	 */
	@RequestMapping (method = RequestMethod.GET, params = {"pId"})
	public String approvalGlEntry (@RequestParam int pId, @RequestParam (value="isEdit", defaultValue="true", required=false) boolean isEdit, Model model, HttpSession session) throws IOException {
		model.addAttribute("isEdit", isEdit);
		GeneralLedger generalLedger = generalLedgerService.getGeneralLedger(pId);
		Collection<GlEntry> glEntries = generalLedger.getGlEntries();
		GeneralLedgerDto generalLedgerDto = new GeneralLedgerDto(generalLedger, glEntries );
		generalLedgerDto.setJsonData(glEntryService.convertToJsonString(glEntries));
		generalLedgerDto.setPosting(true);
		model.addAttribute("pId", pId);
		model.addAttribute("sequenceNo", generalLedger.getSequenceNo());
		User user = CurrentSessionHandler.getLoggedInUser(session);
		return showGlEntryForm(user, model, generalLedger.getGlStatusId(),
				GlEntrySource.SOURCE_GENERAL_LEDGER, generalLedgerDto, false);
	}

	@RequestMapping(value = "/form/viewForm", method = RequestMethod.GET)
	public String showForm (@RequestParam (value="pId", required = false) Integer pId, Model model) throws IOException {
		logger.info("Retreiving GL Entry form");
		GeneralLedger ledger = null;
		GeneralLedgerDto generalLedgerDto =null;
		if(pId != null){
			ledger = generalLedgerService.getGeneralLedger(pId);
			Collection<GlEntry> glEntries = ledger.getGlEntries();
			generalLedgerDto = new GeneralLedgerDto(ledger, glEntries);
			model.addAttribute("generalLedgerDto", generalLedgerDto);
		}else{
			logger.error("GL Entry form id is required");
			throw new RuntimeException("GL Entry form id is required");
		}
		model.addAttribute("legder", ledger);
		logger.info("Successfully loaded GL Entry form");
		return "GlEntryFormView.jsp";
	}
}
