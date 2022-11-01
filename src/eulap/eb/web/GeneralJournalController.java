package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.GlEntrySource;
import eulap.eb.domain.hibernate.GlStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.GeneralLedgerService;
import eulap.eb.service.GlEntryService;
import eulap.eb.validator.GlEntryValidator;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.GeneralLedgerDto;
import net.sf.json.JSONArray;

/**
 * General Journal Controller

 *
 */
@Controller
@RequestMapping ("/generalLedger/journalEntries")
public class GeneralJournalController {
	private final Logger logger = Logger.getLogger(GeneralJournalController.class);
	@Autowired
	private GeneralLedgerService generalLedgerService;
	@Autowired
	private GlEntryValidator glEntryValidator;
	@Autowired
	private GlEntryService glEntryService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private CurrencyService currencyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET, value = "{divisionId}/form/new")
	public String createGeneralJournal (@PathVariable(value="divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) throws IOException {
		logger.info("Showing the General Journal Form.");
		GlEntry glEntry = new GlEntry();
		GeneralLedger generalLedger = new GeneralLedger();
		generalLedger.setDivisionId(divisionId);
		generalLedger.setGlDate(new Date());
		GeneralLedgerDto generalLedgerDto = new GeneralLedgerDto(generalLedger, glEntry);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		int glStatusId = 0;
		int glEntrySource = GlEntrySource.SOURCE_GENERAL_LEDGER;
		boolean isEdit = pId != null;
		if (isEdit) {
			generalLedger = generalLedgerService.getGeneralLedger(pId);
			model.addAttribute("pId", pId);
			model.addAttribute("sequenceNo", generalLedger.getSequenceNo());
			generalLedgerDto = new GeneralLedgerDto(generalLedger, generalLedger.getGlEntries());
			generalLedgerDto.setCompanyId(generalLedger.getCompanyId());
			generalLedgerDto.setReferenceDocuments(generalLedger.getReferenceDocuments());
			glEntryService.loadGlLines(generalLedgerDto);
		} else {
			model.addAttribute("jvNumber", generalLedgerService.generateSequenceNo(divisionId));
		}
		return showGlEntryForm(user, model, glStatusId, glEntrySource, generalLedgerDto, isEdit, divisionId);
	}

	private String showGlEntryForm(User user, Model model, int glStatusId, int glEntrySourceId,
			GeneralLedgerDto generalLedgerDto, boolean isEdit, int divisionId) {
		generalLedgerDto.serializeGlLines();
		generalLedgerDto.serializeReferenceDocuments();
		generalLedgerDto.setDivisionId(divisionId);
		generalLedgerDto.setDivisionName(divisionService.getDivision(divisionId).getName());
		model.addAttribute("glStatusId", glStatusId);
		model.addAttribute("glEntrySourceId", glEntrySourceId);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("generalLedgerDto", generalLedgerDto);
		model.addAttribute("pId", generalLedgerDto.getGeneralLedger().getId());
		model.addAttribute("isEdit", isEdit);
		if (isEdit) {
			model.addAttribute("flag", "edit");
		}
		model.addAttribute("currencies", currencyService.getActiveCurrencies(generalLedgerDto.getGeneralLedger().getCurrencyId()));
		return "GlEntryForm.jsp";
	}

	@RequestMapping (method = RequestMethod.POST,  value = "{divisionId}/form/new")
	public String saveGlEntry (@PathVariable(value="divisionId") int divisionId,
			@ModelAttribute ("generalLedgerDto") GeneralLedgerDto generalLedgerDto,
			BindingResult result,  Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		generalLedgerDto.deserializeGlLines();
		generalLedgerDto.deserializeReferenceDocuments();
		synchronized (this) {
			generalLedgerService.processGL(generalLedgerDto);
			glEntryValidator.validate(generalLedgerDto, result);
			if (result.hasErrors()) {
				int glStatus = 0;
				if (generalLedgerDto.getGeneralLedger().getId() != 0)
					glStatus = GlStatus.STATUS_NEW;
				return showGlEntryForm(user, model, glStatus, GlEntrySource.SOURCE_GENERAL_LEDGER, generalLedgerDto,
						generalLedgerDto.getGeneralLedger().getId() != 0, divisionId);
			}
			ebFormServiceHandler.saveForm(generalLedgerDto.getGeneralLedger(), user);
		}
		GeneralLedger gl = generalLedgerDto.getGeneralLedger();
		model.addAttribute("ebObjectId", gl.getEbObjectId());
		model.addAttribute("formId", gl.getId());
		model.addAttribute("formNumber", gl.getSequenceNo());
		model.addAttribute("success", true);
		return "successfullySaved";
	}

	@RequestMapping(value = "/form/viewForm", method = RequestMethod.GET)
	public String showForm(@RequestParam(value = "pId", required = false) Integer pId, Model model) throws IOException {
		logger.info("Retreiving GL Entry form");
		GeneralLedger ledger = null;
		GeneralLedgerDto generalLedgerDto = null;
		if (pId != null) {
			ledger = generalLedgerService.getGeneralLedger(pId);
			Double rate = ledger.getCurrencyRateValue();
			Collection<GlEntry> glEntries = generalLedgerService.processGlLines(
					(List<GlEntry>) ledger.getGlEntries(), rate != null ? rate : 1.0);
			generalLedgerDto = new GeneralLedgerDto(ledger, glEntries);
			generalLedgerDto.setReferenceDocuments(ledger.getReferenceDocuments());
			model.addAttribute("generalLedgerDto", generalLedgerDto);
		} else {
			logger.error("GL Entry form id is required");
			throw new RuntimeException("GL Entry form id is required");
		}
		model.addAttribute("legder", ledger);
		logger.info("Successfully loaded GL Entry form");
		return "GlEntryFormView.jsp";
	}

	@RequestMapping(value = "/{divisionId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchGeneralJournals(@PathVariable(value = "divisionId") int divisionId,
			@RequestParam(required = true, value = "criteria", defaultValue = "") String criteria,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = generalLedgerService.search(user, criteria, divisionId);
		logger.info("Found " + result.size() + " GLs in Search");
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
