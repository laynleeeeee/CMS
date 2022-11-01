package eulap.eb.web.form.inv;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
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
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.PurchaseOrderLine;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RPurchaseOrderItem;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.RPurchaseOrderService;
import eulap.eb.service.TermService;
import eulap.eb.validator.inv.RPurchaseOrderValidator;
import eulap.eb.web.dto.PRReferenceDto;
import eulap.eb.web.dto.Pr2PoDto;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class for Inventory Retail - Purchase Order.

 */

@Controller
@RequestMapping("/retailPurchaseOrder")
public class RPurchaseOrderController {
	private static Logger logger = Logger.getLogger(RPurchaseOrderController.class);
	@Autowired
	private RPurchaseOrderService pOrderService;
	@Autowired
	private RPurchaseOrderValidator pOrderValidator;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TermService termService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private DivisionService divisionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{divisionId}/form", method=RequestMethod.GET)
	public String loadPOForm(@PathVariable(value="divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) throws IOException {
		logger.info("Showing the Retail - Purchase Order form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		RPurchaseOrder purchaseOrder = new RPurchaseOrder();
		purchaseOrder.setDivision(divisionService.getDivision(divisionId));
		purchaseOrder.setDivisionId(divisionId);
		if (pId != null) {
			logger.debug("Loading the PO form: "+pId);
			purchaseOrder = pOrderService.getRpurchaseOrder(pId);
		} else {
			Date currentDate = new Date();
			purchaseOrder.setPoDate(currentDate);
			purchaseOrder.setEstDeliveryDate(currentDate);
			purchaseOrder.setrPoItems(new ArrayList<RPurchaseOrderItem>());
			purchaseOrder.setPoLines(new ArrayList<PurchaseOrderLine>());
		}
		return loadForm(purchaseOrder, user, model);
	}

	private String loadForm(RPurchaseOrder purchaseOrder, User user, Model model) {
		purchaseOrder.serializeItems();
		purchaseOrder.serializePOLines();
		purchaseOrder.serializeReferenceDocuments();
		model.addAttribute("terms", termService.getTerms(user));
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(purchaseOrder.getCurrencyId()));
		model.addAttribute("rPurchaseOrder", purchaseOrder);
		if (purchaseOrder.getDivisionId() == null) {
			return "R_PurchaseOrderForm.jsp";
		}
		return "PurchaseOrderForm.jsp";
	}

	@RequestMapping(value="{divisionId}/form", method=RequestMethod.POST)
	public String savePurchaseOrder(@PathVariable(value="divisionId") int divisionId,
			@ModelAttribute("rPurchaseOrder") RPurchaseOrder purchaseOrder,
			BindingResult result, Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		logger.info("Saving the Purchase Order.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		purchaseOrder.deSerializeItems();
		purchaseOrder.deserializePOLines();
		purchaseOrder.deserializeReferenceDocuments();
		synchronized (this) {
			pOrderValidator.validate(purchaseOrder, result, true);
			if (result.hasErrors()) {
				logger.debug("Form has error/s. Reloading the Purchase Order form.");
				return loadForm(purchaseOrder, user, model);
			}
			ebFormServiceHandler.saveForm(purchaseOrder, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", purchaseOrder.getPoNumber());
		model.addAttribute("formId", purchaseOrder.getId());
		model.addAttribute("ebObjectId", purchaseOrder.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/viewForm", method=RequestMethod.GET)
	public String viewPurchaseOrder(@RequestParam(value="pId", required=false) Integer pId, Model model) throws IOException {
		logger.info("Loading the view form of Retail - Purchase Order");
		RPurchaseOrder purchaseOrder = pOrderService.getRpurchaseOrder(pId);
		model.addAttribute("rPurchaseOrder", purchaseOrder);
		if (purchaseOrder.getDivisionId() == null) {
			return "R_PurchaseOrderView.jsp";
		}
		return "PurchaseOrderView.jsp";
	}

	@RequestMapping(value ="/getLatestUC", method = RequestMethod.GET)
	public @ResponseBody String getLatestUC(@RequestParam(value="itemId") int itemId,
			@RequestParam(value="supplierAcctId") int supplierAcctId){
		logger.info("Retrieving the latest unit cost of item id "+itemId+" under supplier account: "+supplierAcctId);
		Double unitCost = pOrderService.getLatestUCPerSupplierAcct(itemId, supplierAcctId);
		return NumberFormatUtil.formatNumber(unitCost.doubleValue(), 10);
	}

	@RequestMapping(value="/prReference", method=RequestMethod.GET)
	public String showPrReference(
			@RequestParam (value="companyId", required=false) Integer companyId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadCompanyList(user, model);
		loadReferences(model, companyId, null, null, null , null, null,
				RequisitionForm.STATUS_UNUSED, PageSetting.START_PAGE, user);
		return "PRReference.jsp";
	}

	@RequestMapping(value="/prReference/reload", method=RequestMethod.GET)
	public String reloadPrReferenceTable (
			@RequestParam (value="companyId", required=false) Integer companyId, 
			@RequestParam (value="fleetProfileId", required=false) Integer fleetProfileId,
			@RequestParam (value="projectId", required=false) Integer projectId, 
			@RequestParam (value="prNumber", required=false)  Integer prNumber, 
			@RequestParam (value="strDateFrom", required=false) String strDateFrom,
			@RequestParam (value="strDateTo", required=false) String strDateTo, 
			@RequestParam (value="status") Integer status,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Date dateFrom = strDateFrom != null ? DateUtil.parseDate(strDateFrom) : null;
		Date dateTo = strDateTo != null ? DateUtil.parseDate(strDateTo) : null; 
		loadReferences(model, companyId, fleetProfileId, prNumber, projectId, dateFrom, dateTo, status, pageNumber, user);
		return "PRReferenceTable.jsp";
	}

	private void loadReferences(Model model, Integer companyId, Integer fleetProfileId,
			Integer prNumber, Integer projectId, Date dateFrom,  Date dateTo, Integer status, Integer pageNumber, User user) {
		logger.info("Loading the requisition forms.");
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		Page<PRReferenceDto> purchaseRequisitions = pOrderService.getPrReferences(user, companyId,
				fleetProfileId, projectId, prNumber, dateFrom, dateTo, status, pageNumber);
		model.addAttribute("purchaseRequisitions", purchaseRequisitions);
	}

	@RequestMapping(value="loadRequests",method=RequestMethod.GET)
	public @ResponseBody String setRequisitionForm (@RequestParam (value="prObjIds", required=true) String prObjIds,
			Model model, HttpSession session) {
		logger.info("setting purchase order form.");
		Pr2PoDto po = pOrderService.convertPrToPo(prObjIds);
		String [] exclude = {"item", "inventoryCost", "receivedStockId", "ignore"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(po, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value="loadRefDocs",method=RequestMethod.GET)
	public String setRefDocs (
			@RequestParam (value="prObjIds", required=true) String prObjIds,
			Model model, HttpSession session) {
		logger.info("setting reference documents.");
		return "ReferenceDocumentTable.jsp";
	}

	private void loadCompanyList(User user, Model model){
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
	}
}
