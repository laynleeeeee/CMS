package eulap.eb.web.form.inv;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

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
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.AccountSaleISService;
import eulap.eb.service.AccountSaleItemService;
import eulap.eb.service.AccountSalePoService;
import eulap.eb.service.AccountSalesService;
import eulap.eb.service.ArLineService;
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.TermService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.inv.AccountSaleValidator;
import eulap.eb.web.dto.AccountSaleDto;
import eulap.eb.web.dto.FormSearchResult;

/**
 * Controller class for account sales.

 *
 */
@Controller
@RequestMapping ("/accountSale")
public class AccountSaleController {
	private final static String SLIP_TITLE = "ACCOUNT SALE - OUT SLIP";
	private final Logger logger = Logger.getLogger(AccountSaleController.class);
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private AccountSaleItemService accountSaleItemService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TermService termService;
	@Autowired
	private AccountSaleValidator accountSaleValidator;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private ArLineService arLineService;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private AccountSaleISService accountSaleISService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private AccountSalesService accountSalesService;
	@Autowired
	private AccountSalePoService salePoService;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	private String loadAccountSalesForm (ArTransaction arTransaction, Model model, User user) {
		logger.info("Retrieving active companies.");
		List<Company> companies = (List<Company>) companyService.getActiveCompanies(user, null, null, null);
		model.addAttribute("companies", companies);
		logger.trace("Retrieving all of the terms");
		model.addAttribute("terms", termService.getTerms(user));
		arTransaction.serializeAccountSaleItems();
		arTransaction.serializeArLines();
		if(arTransaction.getId() == 0 && companies != null && !companies.isEmpty()) {
			Company company = companies.iterator().next();
			arTransaction.setCompany(company);
			arTransaction.setCompanyId(company.getId());
		}
		model.addAttribute("accountSale", arTransaction);
		if (arTransaction.getTransactionTypeId() == ArTransactionType.TYPE_ACCOUNT_SALES_IS) {
			return "AccountSaleISForm.jsp";
		} else {
			return "AccountSaleForm.jsp";
		}
	}

	/**
	 * This will show the account sales form. 
	 * @param pId The unique id of the account sales form. Null for new account sales.
	 * @param model The current model.
	 * @param session The current session. 
	 * @return The view of this controller
	 * @throws ConfigurationException 
	 */
	@RequestMapping (value="{typeId}/form", method = RequestMethod.GET)
	public String showAccountSales (@PathVariable(value="typeId") int typeId,
			@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws ConfigurationException {
		logger.info("Loading the Account sales form of type: "+typeId);
		ArTransaction arTransaction = new ArTransaction();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isEdit = false;
		if (pId == null) {
			arTransaction.setTransactionDate(new Date ());
		} else if (typeId == ArTransactionType.TYPE_ACCOUNT_SALES_IS) {
			logger.debug("Retrieving account sales form by id "+pId);
			arTransaction = accountSaleISService.getArTransItemsAndOC(pId);
		} else {
			logger.debug("Retrieving account sales form by id "+pId);
			arTransaction = arTransactionService.getARTransaction(pId);
			arTransaction = accountSaleItemService.getAcctSalesWithItems(arTransaction, pId);
			isEdit = workflowServiceHandler.isAllowedToEdit(ArTransaction.class.getSimpleName()+typeId,
					user, arTransaction.getFormWorkflow());
		}
		model.addAttribute("isEdit", isEdit);
		arTransaction.setTransactionTypeId(typeId);
		return loadAccountSalesForm(arTransaction, model, user);
	}

	@RequestMapping(value="/availableBalance", method = RequestMethod.GET)
	public @ResponseBody String getAvailableBalance(@RequestParam(required=true, value="arCustomerId")
		Integer arCustomerId) {
		return arTransactionService.computeAvailableBalance(arCustomerId, 0) + "";
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable("typeId") int transactionTypeId,
			@ModelAttribute ("accountSale") ArTransaction arTransaction, BindingResult result,
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		arTransaction.setTransactionTypeId(transactionTypeId);
		arTransaction.deserializeAccountSalesItems();
		arTransaction.deserializeArLines();
		synchronized (this) {
			accountSaleValidator.validate(arTransaction, result);
			if (result.hasErrors())  {
				logger.info("Form has error/s. Reloading the form.");
				return loadAccountSalesForm(arTransaction, model, user);
			}
			ebFormServiceHandler.saveForm(arTransaction, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", arTransaction.getSequenceNumber());
		model.addAttribute("formId", arTransaction.getId());
		model.addAttribute("ebObjectId", arTransaction.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/form/{typeId}/viewForm", method=RequestMethod.GET)
	public String viewAccountSale(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model) {
		logger.info("Loading the view form of Account Sale");
		ArTransaction arTransaction = null;
		if (typeId == ArTransactionType.TYPE_ACCOUNT_SALES_IS) {
			arTransaction = accountSaleISService.getArTransItemsAndOC(pId);
			model.addAttribute("accountSale", arTransaction);
			return "AccountSaleISView.jsp";
		} else {
			arTransaction = arTransactionService.getARTransaction(pId);
			arTransaction.setAccountSaleItems(accountSaleItemService.getAccountSaleItems(pId));
			model.addAttribute("accountSale", arTransaction);
			return "AccountSaleView.jsp";
		}
	}

	@RequestMapping(value="{typeId}/pdf", method = RequestMethod.GET)
	public String showReport (@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true)Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session){
		if (pId != null) {
			logger.info("Loading account sale pdf format");
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(pId, typeId));
			model.addAttribute("datasource", dataSource);
			getCommonParam(model, pId, fontSize, "pdf", session);
		} else {
			logger.error("Account sale id required.");
			throw new RuntimeException("Account sale id is required.");
		}
		if (typeId == ArTransactionType.TYPE_ACCOUNT_SALES_IS) {
			return "AccountSaleFormIS.jasper";
		}
		logger.info("Sucessfully loaded account sale pdf.");
		return "AccountSaleForm.jasper";
	}

	@RequestMapping(value="{typeId}/html", method = RequestMethod.GET)
	private String showReportHtml (@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true)Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session){
		if (pId != null) {
			logger.info("Loading account sale pdf format");
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(pId, typeId));
			model.addAttribute("datasource", dataSource);
			getCommonParam(model, pId, fontSize, "html", session);
		} else {
			logger.error("Account sale id required.");
			throw new RuntimeException("Account sale id is required.");
		}
		logger.info("Sucessfully loaded account sale pdf.");
		return "AccountSaleFormHTML.jasper";
	}

	private List<AccountSaleDto> getDataSource(int pId, Integer typeId) {
		List<AccountSaleDto> datasource = new ArrayList<AccountSaleDto>();
		List<AccountSaleItem> asItems = null;
		if (typeId.equals(ArTransactionType.TYPE_ACCOUNT_SALES_IS)) {
				asItems = accountSaleISService.getAcctSaleItems(pId);
				SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
				asItems = saleItemUtil.generateSaleItemPrintout(asItems);
		} else {
			asItems = accountSaleItemService.getASItemPrintOut(pId, ArTransactionType.TYPE_ACCOUNT_SALE);
		}
		List<ArLine> arLines = arLineService.getArLines(pId);
		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		otherCharges.addAll(arLines);
		List<AROtherCharge> processedList = cashSaleService.getDetailOtherCharges(otherCharges);
		List<ArLine> processedArLines = new ArrayList<ArLine>();
		for (AROtherCharge oc : processedList) {
			processedArLines.add((ArLine) oc);
		}
		datasource.add(AccountSaleDto.getInstanceOf(asItems, processedArLines));
		return datasource;
	}

	@RequestMapping(value="{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchAccountSale(@PathVariable("typeId") Integer transactionTypeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria){
		List<FormSearchResult> result = accountSaleItemService.search(transactionTypeId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	private void getCommonParam(Model model, Integer transactionId, Integer fontSize, String format, HttpSession session) {
		ArTransaction transaction =  arTransactionService.getARTransaction(transactionId);
		model.addAttribute("format", format);
		Integer typeId = transaction.getTransactionTypeId();
		String prinoutTitle = "ACCOUNT SALES";
		model.addAttribute("reportTitle" , prinoutTitle);
		if (format == "pdf") {
			model.addAttribute("companyLogo", transaction.getCompany().getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ transaction.getCompany().getLogo());
		}
		model.addAttribute("companyName", transaction.getCompany().getName());
		model.addAttribute("companyAddress", transaction.getCompany().getAddress());

		model.addAttribute("slipTitle", SLIP_TITLE);
		model.addAttribute("customer", transaction.getArCustomer().getName());
		model.addAttribute("customerAcct", transaction.getArCustomerAccount().getName());
		String address =  transaction.getArCustomer().getAddress();
		model.addAttribute("address", address == null ? "" : address);
		model.addAttribute("term", transaction.getTerm().getName());
		model.addAttribute("dueDate", transaction.getDueDate());
		model.addAttribute("date", transaction.getTransactionDate());
		model.addAttribute("accountSalesNo", transaction.getFormattedASNumber());
		model.addAttribute("remarks", StringFormatUtil.removeLineBreaks(transaction.getDescription()));
		double totalAmount = transaction.getAmount();
		double totalVat = accountSalesService.getTotalVatAmnt(transactionId);
		model.addAttribute("subTotal", totalAmount-totalVat);
		model.addAttribute("totalVat", totalVat);
		model.addAttribute("totalAmount", totalAmount);
		WithholdingTaxAcctSetting wtax = transaction.getWtAcctSetting();
		if (wtax != null) {
			model.addAttribute("wtAmount", transaction.getWtAmount() != null ? transaction.getWtAmount() : 0.0);
			model.addAttribute("wtAcctSetting", wtax.getName());
		}
		String formLabel = arTransactionService.getTransactionPrefix(typeId);
		model.addAttribute("formLabel", formLabel);
		SaleItemUtil.getFontSize(fontSize, model);

		FormWorkflow workflowLog = transaction.getFormWorkflow();
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				String name = log.getCreated().getLastName() + ", " + log.getCreated().getFirstName();
				model.addAttribute("createdBy", name);
				model.addAttribute("creatorPosition", log.getCreated().getPosition().getName());
			} else if (log.getFormStatusId() == FormStatus.VALIDATED_ID) {
				String name = log.getCreated().getLastName() + ", " + log.getCreated().getFirstName();
				model.addAttribute("validatedBy", name);
				model.addAttribute("validatorPosition", log.getCreated().getPosition().getName());
			}
		}
	}

	@RequestMapping(value="asReference", method=RequestMethod.GET)
	public String showASReference(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Retrieving active companies.");
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		loadAcctSalesReferences(model, PageSetting.START_PAGE, null, null,
				null, null, null, null, ArTransaction.STATUS_UNUSED, user);
		logger.info("Successfully retrieved account sales.");
		return "ASPOReference.jsp";
	}

	@RequestMapping(value="asReference/reload", method=RequestMethod.GET)
	public String showCsReferenceTable (@RequestParam (value="companyId") Integer companyId, 
			@RequestParam (value="arCustomerId", required=false) Integer arCustomerId, 
			@RequestParam (value="arCustomerAccountId", required=false) Integer arCustomerAccountId,
			@RequestParam (value="asNumber", required=false) Integer asNumber, 
			@RequestParam (value="dateFrom", required=false) String dateFrom, 
			@RequestParam (value="dateTo", required=false) String dateTo, 
			@RequestParam (value="status") Integer status, 
			@RequestParam (value="pageNumber") Integer pageNumber, 
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadAcctSalesReferences(model, pageNumber, companyId, arCustomerId,
				arCustomerAccountId, asNumber, dateFrom, dateTo, status, user);
		return "ASPoReferenceTable.jsp";
	}

	private void loadAcctSalesReferences(Model model, Integer pageNumber,
			Integer companyId, Integer arCustomerId, Integer arCustomerAccountId, Integer asNumber,
			String dateFrom, String dateTo, Integer status, User user) {
		logger.info("Retrieving Account Sales references.");
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("accountSales", salePoService.getAccountSales(companyId, arCustomerId, arCustomerAccountId,
				asNumber, DateUtil.parseDate(dateFrom), DateUtil.parseDate(dateTo), status, pageNumber, user));
	}

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String setAccountSaleReturn (
			@RequestParam (value="accountSalesId", required=true) Integer accountSalesId,
			Model model, HttpSession session) {
		logger.info("setting account sale return.");
		ArTransaction arTransaction = salePoService.getConvrtedARTransactionByASId(accountSalesId);
		String [] exclude = {"paymentStatus", "item"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(arTransaction, jsonConfig);
		return arTransaction == null ? "No transaction found" : jsonObject.toString();
	}
}
