package eulap.eb.web;

import java.io.InvalidClassException;
import java.util.Date;

import javax.servlet.http.HttpSession;

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
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.RPurchaseOrderService;
import eulap.eb.service.WithdrawalSlipService;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller for withdrawal slip

 *
 */
@Controller
@RequestMapping ("/withdrawalSlip")
public class WithdrawalSlipController {
	private Logger logger = Logger.getLogger(WithdrawalSlipController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WithdrawalSlipService withdrawalSlipService;
	@Autowired
	private RPurchaseOrderService rPurchaseOrderService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		WithdrawalSlip withdrawalSlip = new WithdrawalSlip();
		if(pId != null){
			withdrawalSlip = withdrawalSlipService.getWithdrwalSlipForViewingEdit(pId, false);
		} else {
			withdrawalSlip.setDate(new Date());
		}
		return loadWithdrawalSlipForm(withdrawalSlip, user, model);
	}

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String populateWithdrawalSlip (@RequestParam (value="poId", required=true) Integer poId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		WithdrawalSlip withdrawalSlip = withdrawalSlipService.getAndConvertPO(poId, user);
		String [] exclude = {"item"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(withdrawalSlip, jsonConfig);
		return withdrawalSlip == null ? "No Withdrawal Slip found" : jsonObject.toString();
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveReceivingReport (@ModelAttribute("withdrawalSlip") WithdrawalSlip withdrawalSlip,
			BindingResult result, Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		withdrawalSlip.deserializeWithdrawalSlipItems();
		synchronized (this) {
			withdrawalSlipService.validate(withdrawalSlip, result, false);
			if(result.hasErrors()) {
				if(withdrawalSlip.getFormWorkflowId() != null) {
					withdrawalSlip.setFormWorkflow(withdrawalSlipService.getFormWorkflow(withdrawalSlip.getId()));
				}
				return loadWithdrawalSlipForm(withdrawalSlip, user, model);
			}
			ebFormServiceHandler.saveForm(withdrawalSlip, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", withdrawalSlip.getWsNumber());
		model.addAttribute("formId", withdrawalSlip.getId());
		model.addAttribute("ebObjectId", withdrawalSlip.getEbObjectId());
		logger.info("Successfully save withdrawal slip details");
		return "successfullySaved";
	}

	private String loadWithdrawalSlipForm(WithdrawalSlip withdrawalSlip, User user, Model model) {
		loadSelection(withdrawalSlip, model, user);
		return "WithdrawalSlipForm.jsp";
	}

	private void loadSelection(WithdrawalSlip withdrawalSlip, Model model, User user){
		withdrawalSlip.serializeWithdrawalSlipItems();
		model.addAttribute("withdrawalSlip", withdrawalSlip);
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, withdrawalSlip.getCompanyId() != null ? withdrawalSlip.getCompanyId() : 0));
	}

	@RequestMapping(value = "/viewForm", method = RequestMethod.GET)
	public String showForm (@RequestParam (value="pId", required = false)Integer pId, Model model) {
		WithdrawalSlip withdrawalSlip = new WithdrawalSlip();
		withdrawalSlip = withdrawalSlipService.getWithdrwalSlipForViewingEdit(pId, false);
		model.addAttribute("withdrawalSlip", withdrawalSlip);
		return "WithdrawalSlipView.jsp";
	}

	@RequestMapping(value="/poReference", method=RequestMethod.GET)
	public String showPOReference(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, 0));
		loadPOReference(model, user, PageSetting.START_PAGE, null, null, null,  null, null, WithdrawalSlip.STATUS_UNUSED);
		return "POWSReference.jsp";
	}

	@RequestMapping(value = "/loadPos", method = RequestMethod.GET)
	public String loadPurchaseOrder(@RequestParam(value="poNumber", required=false) String poNumber,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="supplierId", required=false) Integer supplierId,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam(value="status", required=false) Integer status,
			@RequestParam(value="pageNumber", required=false) int pageNumber,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadPOReference(model, user, pageNumber, companyId, supplierId, poNumber, dateFrom, dateTo, status);
		return "POWSReferenceTable.jsp";
	}

	private void loadPOReference(Model model, User user, int pageNumber, Integer companyId,
			Integer supplierId, String poNumber, Date dateFrom, Date dateTo, Integer status) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("rPurchaseOrders", rPurchaseOrderService.getPurchaseOrders(user, companyId, supplierId, poNumber, dateFrom, dateTo, status, pageNumber));
	}
}
