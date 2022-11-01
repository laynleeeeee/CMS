package eulap.eb.web;

import java.io.InvalidClassException;
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
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.JyeiWithdrawalSlip;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.service.CompanyService;
import eulap.eb.service.JyeiWithdrawalSlipService;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.WithdrawalSlipDto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller for withdrawal slip

 *
 */
@Controller
@RequestMapping ("/jyeiWithdrawalSlip")
public class JyeiWithdrawalSlipCtrlr {
	private Logger logger = Logger.getLogger(JyeiWithdrawalSlipCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private JyeiWithdrawalSlipService jyeiWSService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		WithdrawalSlipDto wsDto = new WithdrawalSlipDto();
		if(pId != null){
			wsDto = jyeiWSService.getWithdrawalSlipDtoForView(pId, false);
		} else {
			WithdrawalSlip withdrawalSlip = new WithdrawalSlip();
			JyeiWithdrawalSlip gemmaWithdrawalSlip = new JyeiWithdrawalSlip();
			withdrawalSlip.setDate(new Date());
			gemmaWithdrawalSlip.setRequisitionTypeId(typeId);
			wsDto.setWithdrawalSlip(withdrawalSlip);
			wsDto.setJyeiWithdrawalSlip(gemmaWithdrawalSlip);
		}
		
		return loadWithdrawalSlipForm(wsDto, user, model);
	}

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String populateWithdrawalSlip (@RequestParam (value="rfId", required=true) Integer rfId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		WithdrawalSlipDto withdrawalSlip = jyeiWSService.getAndConvertRf(rfId, user);
		String [] exclude = {"item"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(withdrawalSlip, jsonConfig);
		return withdrawalSlip == null ? "No Withdrawal Slip found" : jsonObject.toString();
	}

	@RequestMapping(value="/{typeId}/form", method=RequestMethod.POST)
	public String saveWithdrawalSlip (@PathVariable("typeId") int typeId,
			@ModelAttribute("wsDto") WithdrawalSlipDto wsDto, BindingResult result, Model model,
			HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		WithdrawalSlip withdrawalSlip = wsDto.getWithdrawalSlip();
		withdrawalSlip.setTypeId(typeId);
		withdrawalSlip.deserializeWithdrawalSlipItems();
		wsDto.deserializeReferenceDocuments();
		wsDto.deserrializeSerialItems();
		jyeiWSService.validateWSDto(wsDto, result, true);
		jyeiWSService.validateWSSerialItems(wsDto, result);
		if(result.hasErrors()) {
			if(withdrawalSlip.getFormWorkflowId() != null) {
				withdrawalSlip.setFormWorkflow(jyeiWSService.getFormWorkflow(withdrawalSlip.getId()));
			}
			return loadWithdrawalSlipForm(wsDto, user, model);
		}
		jyeiWSService.saveJyeiWithdrawalSlip(wsDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", withdrawalSlip.getWsNumber());
		model.addAttribute("formId", withdrawalSlip.getId());
		model.addAttribute("ebObjectId", withdrawalSlip.getEbObjectId());
		logger.info("Successfully save withdrawal slip details");
		return "successfullySaved";
	}

	private String loadWithdrawalSlipForm(WithdrawalSlipDto wsDto, User user, Model model) {
		WithdrawalSlip withdrawalSlip = wsDto.getWithdrawalSlip();
		loadSelection(withdrawalSlip, model, user);
		withdrawalSlip.serializeWithdrawalSlipItems();
		wsDto.serializeReferenceDocuments();
		wsDto.serializeSerialItems();
		wsDto.setWithdrawalSlip(withdrawalSlip);
		model.addAttribute("isStockIn", false);
		model.addAttribute("wsDto", wsDto);
		return "WithdrawalSlipForm.jsp";
	}

	private void loadSelection(WithdrawalSlip withdrawalSlip, Model model, User user){
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user,
				withdrawalSlip.getCompanyId() != null ? withdrawalSlip.getCompanyId() : 0));
	}

	@RequestMapping(value = "/{typeId}/viewForm", method = RequestMethod.GET)
	public String showForm (@PathVariable("typeId") int typeId,
			@RequestParam (value="pId", required = false)Integer pId, Model model) {
		WithdrawalSlipDto wsDto = jyeiWSService.getWithdrawalSlipDtoForView(pId, false);
		wsDto.getWithdrawalSlip().setTypeId(typeId);
		wsDto.setFleetPlateNo(jyeiWSService.getPlateNo(wsDto.getWithdrawalSlip().getFleetId()));
		model.addAttribute("wsDto", wsDto);
		return "WithdrawalSlipView.jsp";
	}

	@RequestMapping(value="/{typeId}/search", method=RequestMethod.GET)
	public @ResponseBody String searchRPurchaseOrder(@PathVariable("typeId") int typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = jyeiWSService.searchJyeiWithdrawalSlips(criteria, typeId);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="{typeId}/rfReference", method=RequestMethod.GET)
	public String showPOReference(@PathVariable("typeId") int typeId,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="isPakyawanSubconOnly", required=false) Boolean isPakyawanSubconOnly,
			@RequestParam(value="isExcludePrForms", required=false) Boolean isExcludePrForms,
			@RequestParam(value="currDate", required=false) Date currDate,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("reqTypeId", typeId);
		model.addAttribute("companyId", companyId);
		isPakyawanSubconOnly = isPakyawanSubconOnly != null ? isPakyawanSubconOnly : false;
		isExcludePrForms = isExcludePrForms != null ? isExcludePrForms : false;
		loadRFReference(model, user, PageSetting.START_PAGE, companyId, null, null, null,
				currDate, currDate, WithdrawalSlip.STATUS_UNUSED, typeId, isPakyawanSubconOnly,
				isExcludePrForms);
		return "ReqFormReference.jsp";
	}

	private void loadRFReference(Model model, User user, int pageNumber, Integer companyId, Integer fleetId,
			Integer projectId, Integer rfNumber, Date dateFrom, Date dateTo, Integer status, Integer reqTypeId,
			boolean isPakyawanSubconOnly, boolean isExcludePrForms) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("requisitionForms", jyeiWSService.getRequisitionForms(user,
				companyId, fleetId, projectId, rfNumber, dateFrom, dateTo, status,
				reqTypeId, pageNumber, isPakyawanSubconOnly, isExcludePrForms));
	}

	@RequestMapping(value = "{typeId}/loadRfs", method = RequestMethod.GET)
	public String loadReqFormTable(@PathVariable("typeId") int typeId,
			@RequestParam(value="rfNumber", required=false) Integer rfNumber,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="fleetId", required=false) Integer fleetId,
			@RequestParam(value="projectId", required=false) Integer projectId,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam(value="status", required=false) Integer status,
			@RequestParam(value="pageNumber", required=false) int pageNumber,
			@RequestParam(value="isPakyawanSubconOnly", required=false) Boolean isPakyawanSubconOnly,
			@RequestParam(value="isExcludePrForms", required=false) Boolean isExcludePrForms,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		isPakyawanSubconOnly = isPakyawanSubconOnly != null ? isPakyawanSubconOnly : false;
		isExcludePrForms = isExcludePrForms != null ? isExcludePrForms : false;
		loadRFReference(model, user, pageNumber, companyId, fleetId, projectId,
				rfNumber, dateFrom, dateTo, status, typeId, isPakyawanSubconOnly, isExcludePrForms);
		return "ReqFormReferenceTable.jsp";
	}
}
