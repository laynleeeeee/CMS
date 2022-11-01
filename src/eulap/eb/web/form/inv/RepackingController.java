package eulap.eb.web.form.inv;

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
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.domain.hibernate.RepackingType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.RepackingService;
import eulap.eb.service.RpItemConversionService;
import eulap.eb.service.fsp.RepackingSearcher;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.json.JSONArray;

/**
 * Controller class that will handle requests for {@link Repacking} object.

 */

@Controller
@RequestMapping("/repacking")
public class RepackingController {
	private static Logger logger = Logger.getLogger(RepackingController.class);
	@Autowired
	private RepackingService repackingService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private RpItemConversionService itemConversionService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private RepackingSearcher rpSearcher;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private WorkflowServiceHandler workflowService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/{divisionId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable(value="typeId") int typeId,
			@PathVariable (value="divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) throws IOException {
		logger.info("Loading the repacking form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Repacking repacking = new Repacking();
		repacking.setDivisionId(divisionId);
		repacking.setDivision(divisionService.getDivision(divisionId));
		if (pId != null) {
			logger.debug("Editing the repacking form: "+pId);
			repacking = repackingService.getRepacking(pId, typeId);
			repacking.setReferenceDocuments(refDocumentService.processReferenceDocs(repacking.getEbObjectId()));
		} else {
			repacking.setrDate(new Date());
		}
		return loadForm(typeId, (repacking.getCompanyId() != null ? repacking.getCompanyId() : 0),
				repacking, user, model);
	}

	private String loadForm(int typeId, int companyId, Repacking repacking, User user, Model model) {
		repacking.setRepackingTypeId(typeId);
		repacking.serializeItems();
		repacking.serializeRawMaterials();
		repacking.serializeFinishedGoods();
		repacking.serializeReferenceDocuments();
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		model.addAttribute("repacking", repacking);
		if (typeId == RepackingType.TYPE_REPACKING) {
			if (repacking.getDivisionId() != null) {
				return "ReclassForm.jsp";
			}
			return "RepackingForm.jsp";
		}
		return "ItemConversionForm.jsp";
	}

	@RequestMapping(value="{typeId}/{divisionId}/form", method=RequestMethod.POST)
	public String saveRepackingForm(@PathVariable("typeId") int typeId,
			@PathVariable (value="divisionId") int divisionId,
			@ModelAttribute("repacking") Repacking repacking, BindingResult result, HttpSession session,
			Model model) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		logger.info("Saving the repacking form.");
		repacking.setRepackingTypeId(typeId);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		repacking.deserializeItems();
		repacking.deserializeReferenceDocuments();
		repacking.deserializeRawMaterials();
		repacking.deserializeFinishedGoods();
		synchronized (this) {
			itemConversionService.validateForm(repacking, result);
			if (result.hasErrors()) {
				logger.info("Form has error/s. Reloading the form.");
				return loadForm(typeId, repacking.getCompanyId(), repacking, user, model);
			}
			ebFormServiceHandler.saveForm(repacking, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("ebObjectId", repacking.getEbObjectId());
		model.addAttribute("formId", repacking.getId());
		model.addAttribute("formNumber", repacking.getrNumber());
		return "successfullySaved";
	}

	@RequestMapping(value="{typeId}/viewForm", method=RequestMethod.GET)
	public String showViewForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId") Integer repackingId, Model model, HttpSession session) throws ConfigurationException, IOException {
		logger.info("Showing the view form of repacking form: "+repackingId);
		Repacking repacking = repackingService.getRepacking(repackingId, typeId);
		repacking.setReferenceDocuments(refDocumentService.processReferenceDocs(repacking.getEbObjectId()));
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (repacking.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(repackingId, repacking.getWorkflowName(), user));
		}
		model.addAttribute("repacking", repacking);
		if (typeId == RepackingType.TYPE_REPACKING) {
			if (repacking.getDivisionId() != null) {
				return "ReclassView.jsp";
			}
			return "RepackingView.jsp";
		}
		return "ItemConversionView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, repackingService.getFormWorkflow(pId));
	}

	@RequestMapping(value="{typeId}/{divisionId}/search", method=RequestMethod.GET)
	public @ResponseBody String searchRepackingForms(@PathVariable("typeId") int typeId,
			@PathVariable("divisionId") int divisionId,
			@RequestParam(value="criteria", defaultValue="")
			String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = rpSearcher.search(typeId, divisionId, user, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
