package eulap.eb.web.form.inv;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Collection;
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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentType;
import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArReceiptTypeService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.CustomerAdvancePaymentService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.ReceiptMethodService;
import eulap.eb.service.SalesOrderService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.inv.CustomerAdvancePaymentValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class for {@link CustomerAdvancePayment}

 *
 */
@Controller
@RequestMapping("/customerAdvancePayment")
public class CustomerAdvancePaymentController {
	private final Logger logger = Logger.getLogger(CustomerAdvancePaymentController.class);
	@Autowired
	private CustomerAdvancePaymentService capService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArReceiptTypeService arReceiptTypeService;
	@Autowired
	CustomerAdvancePaymentValidator capValidator;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private ReceiptMethodService receiptMethodService;
	@Autowired
	private SalesOrderService salesOrderService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	private String loadCAPForm(CustomerAdvancePayment cap, Model model, User user) {
		cap.serializeItems();
		cap.serializeCapArLines();
		cap.serializeReferenceDocuments();
		cap.serializeCapLines();
		Collection<Company> companies = companyService.getActiveCompanies(user, null, null, null);
		model.addAttribute("companies", companies);
		model.addAttribute("currencies", currencyService.getActiveCurrencies(cap.getCurrencyId()));
		model.addAttribute("capTypes", arReceiptTypeService.getArReceiptTypes());
		// Receipt Method
		List<ReceiptMethod> receiptMethods = receiptMethodService.getReceiptMethods(companies.iterator().next().getId());
		model.addAttribute("receiptMethods", receiptMethods);
		model.addAttribute("customerAdvancePayment", cap);
		if (cap.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.INDIV_SELECTION)) {
			return "CustomerAdvancePaymentIsForm.jsp";
		}
		return "CustomerAdvancePaymentForm.jsp";
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.GET)
	public String showCAPForm (@PathVariable("typeId") int typeId,
			@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws ConfigurationException, IOException {
		CustomerAdvancePayment cap = new CustomerAdvancePayment();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isEdit = false;
		if (pId == null) {
			Date currentDate = new Date();
			cap.setReceiptDate(currentDate);
			cap.setMaturityDate(currentDate);
			Integer divisionId = capService.getDivisionByCapTypeId(typeId);
			cap.setDivisionId(divisionId);
			cap.setDivision(divisionService.getDivision(divisionId));
		} else {
			logger.debug("Retrieving customer advance payment  form by id " + pId);
			cap = capService.getCapWithItems(pId);
			isEdit = workflowServiceHandler.isAllowedToEdit(CustomerAdvancePayment.class.getSimpleName()+typeId,
					user, cap.getFormWorkflow());
		}
		model.addAttribute("isEdit", isEdit);
		cap.setCustomerAdvancePaymentTypeId(typeId);
		return loadCAPForm(cap, model, CurrentSessionHandler.getLoggedInUser(session));
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String submit (@ModelAttribute ("customerAdvancePayment") CustomerAdvancePayment cap, BindingResult result,
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		cap.deserializeItems();
		cap.deserializeCapArLines();
		cap.deserializeReferenceDocuments();
		cap.deserializeCapLines();
		synchronized (this) {
			capValidator.validate(cap, result);
			if (result.hasErrors()) {
				return loadCAPForm(cap, model, user);

			}
			ebFormServiceHandler.saveForm(cap, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", cap.getCapNumber());
		model.addAttribute("ebObjectId", cap.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/{typeId}/viewForm", method=RequestMethod.GET)
	public String viewCap(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model) throws IOException {
		logger.info("Loading the view form of Customer Advance Payment.");
		CustomerAdvancePayment customerAdvancePayment = capService.getCapWithItems(pId);
		model.addAttribute("customerAdvancePayment", customerAdvancePayment);
		return "CustomerAdvancePaymentView.jsp";
	}

	@RequestMapping(value = "/convSOToCAP", method = RequestMethod.GET)
	public @ResponseBody String getBySeqNo (@RequestParam(value="salesOrderId", required=true) int salesOrderId,
			@RequestParam(value="capId", required=false) Integer capId,
			@RequestParam(value="typeId", required=false) Integer typeId) {
		CustomerAdvancePayment cap = capService.convSOToCAP(salesOrderId, typeId, capId);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts", "user", "userCompanies"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(cap, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value = "/showDepositSO", method = RequestMethod.GET)
	public @ResponseBody String showDepositSOForms (@RequestParam(value="seqNo", required=false) String seqNo,
			@RequestParam(value="companyId", required=false) Integer companyId) {
		logger.info("Retrieving the list of Sales Orders for company: "+companyId);
		Collection<SalesOrder> salesOrder = salesOrderService.showDepositSOForms(seqNo, companyId);
		String [] exclude = {"soItems", "soLines"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(salesOrder, jConfig);
		logger.info("Successfully retrieved" + salesOrder.size() + " ar customers");
		salesOrder = null; //Freeing up memory
		return jsonArray.toString();
	}

	@RequestMapping(value="{divisionId}/showSoReferences", method=RequestMethod.GET)
	public String showSoReferenceForm(@PathVariable(value="divisionId") int divisionId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, 0);
		model.addAttribute("companies", companies);
		model.addAttribute("divisionId", divisionId);
		Integer companyId = companies != null ? companies.iterator().next().getId() : null;
		loadSoReferenceForms(companyId, null, null, null, CustomerAdvancePayment.STATUS_UNUSED,
				null, null, null, divisionId, PageSetting.START_PAGE, model);
		return "CapSoReferenceForm.jsp";
	}

	private void loadSoReferenceForms(Integer companyId, Integer arCustomerId, Integer arCustomerAcctId,
			Integer soNumber, Integer statusId, Date dateFrom, Date dateTo, String poNumber, Integer divisionId, 
			Integer pageNumber, Model model) {
		Page<SalesOrder> salesOrders = capService.getCapSalesOrders(companyId, arCustomerId, 
				arCustomerAcctId, soNumber, statusId, dateFrom, dateTo, poNumber, divisionId, pageNumber);
		model.addAttribute("salesOrders", salesOrders);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getSalesOrdeRefs", method=RequestMethod.GET)
	public String getSalesOrderRefs(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="arCustomerId", required=false) Integer arCustomerId,
			@RequestParam (value="arCustomerAcctId", required=false) Integer arCustomerAcctId,
			@RequestParam (value="soNumber", required=false) Integer soNumber,
			@RequestParam (value="bmsNumber", required=false) String bmsNumber,
			@RequestParam (value="poNumber", required=false) String poNumber,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="statusId", required=false) Integer statusId,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		loadSoReferenceForms(companyId, arCustomerId, arCustomerAcctId, soNumber, statusId, 
				dateFrom, dateTo, poNumber, divisionId, pageNumber, model);
		return "CapSoReferenceTable.jsp";
	}
}
