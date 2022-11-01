package eulap.eb.web.form.inv;

import java.io.InvalidClassException;
import java.util.ArrayList;
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
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.AccountSaleISService;
import eulap.eb.service.AccountSaleItemService;
import eulap.eb.service.AccountSalesService;
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.TermService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.inv.SaleReturnValidator;
import eulap.eb.web.dto.AccountSaleDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class for Account Sales Return.

 *
 */
@Controller
@RequestMapping ("/saleReturn")
public class SaleReturnController {
	private final Logger logger = Logger.getLogger(SaleReturnController.class);
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private AccountSaleItemService accountSaleItemService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TermService termService;
	@Autowired
	private SaleReturnValidator saleReturnValidator;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private AccountSaleISService accountSaleISService;
	@Autowired
	private AccountSalesService accountSalesService;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	private String loadSalesReturnForm (ArTransaction arTransaction, Model model, User user) {
		logger.info("Retrieving active companies.");
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		logger.trace("Retrieving all of the terms");
		model.addAttribute("terms", termService.getTerms(user));
		arTransaction.serializeAccountSaleItems();
		arTransaction.serializeArLines();
		model.addAttribute("salesReturn", arTransaction);
		if (arTransaction.getTransactionTypeId() == ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS) {
			return "AccountSaleReturnISForm.jsp";
		} else {
			return "SaleReturnFormV2.jsp";
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
	public String showAccountSales (@PathVariable(value="typeId") int transactionTypeId,
			@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws ConfigurationException {
		logger.info("Loading the Account Sales Return form of type: "+transactionTypeId);
		ArTransaction arTransaction = new ArTransaction();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isEdit = false;
		if (pId == null) {
			arTransaction.setTransactionDate(new Date ());
		} else {
			logger.debug("Retrieving sale return form by id "+pId);
			arTransaction = arTransactionService.getARTransaction(pId);
			if (transactionTypeId == ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS) {
				arTransaction.setAccountSaleItems(accountSaleISService.getASRItems(pId));
			} else {
				arTransaction = accountSaleItemService.getAcctSalesWithItems(arTransaction, pId);
				isEdit = workflowServiceHandler.isAllowedToEdit(arTransaction.getWorkflowName(),
						user, arTransaction.getFormWorkflow());
			}
			setTransactionNo(arTransaction);
		}
		model.addAttribute("isEdit", isEdit);
		arTransaction.setTransactionTypeId(transactionTypeId);
		return loadSalesReturnForm(arTransaction, model, user);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable("typeId") int transactionTypeId,
			@ModelAttribute ("salesReturn") ArTransaction arTransaction, BindingResult result,
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		arTransaction.deserializeAccountSalesItems();
		arTransaction.deserializeArLines();
		synchronized (this) {
			saleReturnValidator.validate(arTransaction, result);
			if (result.hasErrors()) {
				return loadSalesReturnForm(arTransaction, model, user);
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
	public String viewAccountSale(@PathVariable(value="typeId") Integer typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model) {
		logger.info("Loading the view form of Sales return.");
		ArTransaction arTransaction = arTransactionService.getARTransaction(pId);
		if (typeId == ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS) {
			arTransaction.setAccountSaleItems(accountSaleISService.getASRItems(pId));
		} else {
			arTransaction.setAccountSaleItems(accountSaleItemService.getAccountSaleItems(pId));
		}
		setTransactionNo(arTransaction);
		model.addAttribute("accountSale", arTransaction);
		if (typeId == ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS) {
			return "AccountSaleReturnISView.jsp";
		}
		return "SaleReturnView.jsp";
	}

	private void setTransactionNo(ArTransaction arTransaction) {
		arTransaction.setTransactionNumber(arTransactionService
				.getReferenceNo(arTransaction.getAccountSaleItems()));
	}

	@RequestMapping(value="{typeId}/asReference", method=RequestMethod.GET)
	public String showCsReference(@PathVariable(value="typeId") int transactionTypeId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Retrieving active companies.");
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		loadAcctSalesReferences(model, transactionTypeId, PageSetting.START_PAGE, null, null,
				null, null, null, null, ArTransaction.STATUS_UNUSED, user);
		logger.info("Successfully retrieved account sales.");
		return "ASReference.jsp";
	}

	@RequestMapping(value="{typeId}/asReference/reload", method=RequestMethod.GET)
	public String showCsReferenceTable (
			@PathVariable(value="typeId") int transactionTypeId,
			@RequestParam (value="companyId") Integer companyId, 
			@RequestParam (value="arCustomerId", required=false) Integer arCustomerId, 
			@RequestParam (value="arCustomerAccountId", required=false) Integer arCustomerAccountId,
			@RequestParam (value="asNumber", required=false) Integer asNumber, 
			@RequestParam (value="dateFrom", required=false) String dateFrom, 
			@RequestParam (value="dateTo", required=false) String dateTo, 
			@RequestParam (value="status") Integer status, 
			@RequestParam (value="pageNumber") Integer pageNumber, 
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadAcctSalesReferences(model, transactionTypeId, pageNumber, companyId, arCustomerId,
				arCustomerAccountId, asNumber, dateFrom, dateTo, status, user);
		return "ASReferenceTable.jsp";
	}

	private void loadAcctSalesReferences(Model model, Integer transactionTypeId, Integer pageNumber,
			Integer companyId, Integer arCustomerId, Integer arCustomerAccountId, Integer asNumber,
			String dateFrom, String dateTo, Integer status, User user) {
		logger.info("Retrieving Account Sales references.");
		model.addAttribute("transactionTypeId", transactionTypeId);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute("accountSales", arTransactionService.getAccountSales(companyId, arCustomerId, arCustomerAccountId,
				asNumber, DateUtil.parseDate(dateFrom),DateUtil.parseDate(dateTo), status, transactionTypeId, pageNumber, user));
	}

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String setAccountSaleReturn (@RequestParam (value="arTransactionId", required=true) Integer arTransactionId,
			Model model, HttpSession session) {
		// Setting cash sale return.
		logger.info("setting account sale return.");
		ArTransaction oldArt = arTransactionService.getARTransaction(arTransactionId);
		ArTransaction arTransaction = null;
		if (oldArt != null) {
			 arTransaction = accountSalesService.conv2NewTransaction(oldArt);
		}
		String [] exclude = {"paymentStatus", "item"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(arTransaction, jsonConfig);
		return arTransaction == null ? "No transaction found" : jsonObject.toString();
	}

	public void getCommonParams(Model model, Integer pId, Integer fontSize, JRDataSource dataSource,
			ArTransaction transaction, String format, HttpSession session) {
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format);

		if (transaction == null && pId != null) {
			transaction = arTransactionService.getARTransaction(pId);
		} else {
			// To prevent LazyInitializationException.
			List<AccountSaleItem> asItems =
					accountSaleItemService.getASItemPrintOut(pId, transaction.getTransactionTypeId());
			model.addAttribute("referenceNo", arTransactionService.getReferenceNo(asItems));
		}

		String printOutTitle = null;
		if (transaction.getTransactionTypeId().equals(ArTransactionType.TYPE_SALE_RETURN) ||
				transaction.getTransactionTypeId().equals(ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS)) {
			printOutTitle = "ACCOUNT SALES RETURN";
		} else {
			printOutTitle = "ACCOUNT SALES RETURN - WHOLESALE";
		}
		model.addAttribute("reportTitle" , printOutTitle);
		model.addAttribute("companyLogo", transaction.getCompany().getLogo());
		if (format == "pdf") {
			model.addAttribute("companyLogo", transaction.getCompany().getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ transaction.getCompany().getLogo());
		}
		model.addAttribute("companyName", transaction.getCompany().getName());
		model.addAttribute("companyAddress", transaction.getCompany().getAddress());

		model.addAttribute("customer", transaction.getArCustomer().getName());
		model.addAttribute("customerAcct", transaction.getArCustomerAccount().getName());
		String address =  transaction.getArCustomer().getAddress();
		model.addAttribute("address", address == null ? "" : address);
		model.addAttribute("term", transaction.getTerm().getName());
		model.addAttribute("dueDate", transaction.getDueDate());
		model.addAttribute("date", transaction.getTransactionDate());
		model.addAttribute("accountSalesNo", transaction.getFormattedASNumber());
		String formLabel = arTransactionService.getTransactionPrefix(transaction.getTransactionTypeId());
		model.addAttribute("formLabel", formLabel);
		String remarks = transaction.getDescription();
		model.addAttribute("remarks", remarks == null ? "" : remarks);
		double totalAmount = transaction.getAmount();
		double totalVat = accountSalesService.getTotalVatAmnt(pId);
		WithholdingTaxAcctSetting wtax = transaction.getWtAcctSetting();
		double wtAmount = 0;
		if (wtax != null) {
			wtAmount = transaction.getWtAmount() != null ? transaction.getWtAmount() : 0.0;
			model.addAttribute("wtAmount", wtAmount);
			model.addAttribute("wtAcctSetting", wtax.getName());
		}
		model.addAttribute("subTotal", totalAmount-totalVat+wtAmount);
		model.addAttribute("totalVat", totalVat);
		model.addAttribute("totalAmount", totalAmount);
		setArLines(pId, model);
		SaleItemUtil.getFontSize(fontSize, model);

		String formattedAsrNo = arTransactionService.getTransactionPrefix(transaction.getTransactionTypeId(), transaction);
		model.addAttribute("sequenceNo", formattedAsrNo);

		FormWorkflow workflowLog = transaction.getFormWorkflow();
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				String name = log.getCreated().getFirstName() + " " + log.getCreated().getLastName();
				model.addAttribute("createdBy", name);
				model.addAttribute("creatorPosition", log.getCreated().getPosition().getName());
			} else if (log.getFormStatusId() == FormStatus.VALIDATED_ID) {
				String name = log.getCreated().getFirstName() + " " + log.getCreated().getLastName();
				model.addAttribute("validatedBy", name);
				model.addAttribute("validatorPosition", log.getCreated().getPosition().getName());
			}
		}
	}

	/**
	 * Set the AR lines and the total AR line amount in to the printout
	 * @param pId The AR transaction id
	 * @param model The model
	 */
	private void setArLines(Integer pId, Model model) {
		List<ArLine> processedArLines = accountSaleItemService.getOtherCharges(pId);
		Double totalArLineAmt = 0.0;
		if (processedArLines != null && !processedArLines.isEmpty()) {
			for (ArLine arLine : processedArLines) {
				totalArLineAmt += arLine.getAmount();
			}
		}
		model.addAttribute("totalArLineAmt", totalArLineAmt);
	}

	@RequestMapping(value="/{typeId}/pdf", method = RequestMethod.GET)
	private String showReport (@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=true)Integer pId,
			Model model, HttpSession session) {
		if (pId != null) {
			logger.info("Loading account sale pdf format");
			ArTransaction transaction = arTransactionService.getARTransaction(pId);
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(pId, typeId));
			getCommonParams(model, pId, null, dataSource, transaction, "pdf", session);
		} else {
			logger.error("Account sale return id required.");
			throw new RuntimeException("Account sale return id is required.");
		}
		logger.info("Sucessfully loaded account sale return pdf.");
		if (typeId == ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS) {
			return "AccountSaleReturnFormIS.jasper";
		}
		return "AccountSaleReturnForm.jasper";
	}

	@RequestMapping(value="/{typeId}/html", method = RequestMethod.GET)
	private String showReportHTML (@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId", required=true)Integer pId,
			Model model, HttpSession session) {
		if (pId != null) {
			logger.info("Loading account sale pdf format");
			ArTransaction transaction = arTransactionService.getARTransaction(pId);
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(pId, typeId));
			getCommonParams(model, pId, null, dataSource, transaction, "html", session);
		} else {
			logger.error("Account sale return id required.");
			throw new RuntimeException("Account sale return id is required.");
		}
		logger.info("Sucessfully loaded account sale return pdf.");
		return "AccountSaleReturnFormHTML.jasper";
	}

	private List<AccountSaleDto> getDataSource(int pId, int typeId) {
		List<AccountSaleDto> datasource = new ArrayList<AccountSaleDto>();
		List<AccountSaleItem> asItems = null;
		if (typeId == ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS) {
			asItems = accountSaleISService.getASRItems(pId);
			SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<>();
			asItems = saleItemUtil.generateSaleItemPrintout(asItems);
		} else {
			asItems = accountSaleItemService.getASItemPrintOut(pId, typeId);
		}
		List<ArLine> processedList = accountSaleItemService.getOtherCharges(pId);
		datasource.add(AccountSaleDto.getInstanceOf(asItems, processedList));
		return datasource;
	}
}
