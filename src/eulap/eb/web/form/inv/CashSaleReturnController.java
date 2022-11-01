package eulap.eb.web.form.inv;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnArLine;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CashSaleReturnIsService;
import eulap.eb.service.CashSaleReturnItemService;
import eulap.eb.service.CashSaleReturnService;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.inv.CashSaleReturnValidator;
import eulap.eb.web.dto.CashSaleReturnDto;
import eulap.eb.web.dto.FormSearchResult;

/**
 * Controller for {@link CashSaleReturn}

 *
 */
@Controller
@RequestMapping("/cashSaleReturn")
public class CashSaleReturnController {
	private final Logger logger = Logger.getLogger(CashSaleReturnController.class);
	@Autowired
	private CashSaleReturnService cashSaleReturnService;
	@Autowired
	private CashSaleReturnItemService cashSaleReturnItemService;
	@Autowired
	private CashSaleReturnValidator cashSaleReturnValidator;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private CashSaleReturnIsService cashSaleReturnIsService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	private String loadCashSalesReturnForm(CashSaleReturn cashSaleReturn, Model model, User user) {
		logger.info("Retrieving active companies.");
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		logger.info("Putting cashSalesReturn in a model.");
		cashSaleReturn.serializeItems();
		cashSaleReturn.serializeCSRArLines();
		model.addAttribute("cashSaleReturn", cashSaleReturn);
		if(cashSaleReturn.getCashSaleTypeId().equals(CashSaleType.INDIV_SELECTION)) {
			return "CashSaleReturnIsForm.jsp";
		}
		return "CashSaleReturnFormV2.jsp";
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.GET)
	public String showCashSalesForm (@PathVariable(value="typeId") int typeId,
			@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws ConfigurationException {
		logger.info("Loading the CSR form of type: "+typeId);
		CashSaleReturn cashSaleReturn = null;
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isEdit = false;
		if (pId == null) {
			cashSaleReturn = new CashSaleReturn();
			cashSaleReturn.setDate(new Date());
		} else {
			logger.debug("Editing the cash sale return form by id "+pId);
			cashSaleReturn = cashSaleReturnService.getCashSaleReturn(pId);
			if(typeId == CashSaleType.INDIV_SELECTION) {
				cashSaleReturn.setCashSaleReturnItems(cashSaleReturnIsService.getCsrItems(pId));
				cashSaleReturn.setCashSaleReturnArLines(new ArrayList<CashSaleReturnArLine>());
			} else {
				cashSaleReturn.setCashSaleReturnItems(
						cashSaleReturnItemService.getCashSaleReturnItems(pId));
				isEdit = workflowServiceHandler.isAllowedToEdit(CashSaleReturn.class.getSimpleName()+typeId,
						user, cashSaleReturn.getFormWorkflow());
				cashSaleReturn.setCashSaleReturnArLines(cashSaleReturnService.getDetailedArLines(pId));
			}
			setRefNo(cashSaleReturn);
		}
		cashSaleReturn.setCashSaleTypeId(typeId); //Set the type id.
		model.addAttribute("isEdit", isEdit);
		return loadCashSalesReturnForm(cashSaleReturn, model, CurrentSessionHandler.getLoggedInUser(session));
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable(value="typeId") int typeId,
			@ModelAttribute ("cashSaleReturn") CashSaleReturn cashSaleReturn, BindingResult result, 
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		cashSaleReturn.setCashSaleTypeId(typeId);
		cashSaleReturn.deserializeItems();
		cashSaleReturn.deserializeCSRArLines();
		synchronized (this) {
			cashSaleReturnValidator.validate(cashSaleReturn, result);
			if(result.hasErrors()) {
				logger.info("Form has error/s. Reloading the form.");
				return loadCashSalesReturnForm(cashSaleReturn, model, user);
			}
			ebFormServiceHandler.saveForm(cashSaleReturn, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", cashSaleReturn.getCsrNumber());
		model.addAttribute("ebObjectId", cashSaleReturn.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/form/{typeId}/viewForm", method=RequestMethod.GET)
	public String viewCsrForm(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId") Integer pId, Model model) {
		logger.info("Loading the view form of Cash Sale Return");
		CashSaleReturn cashSaleReturn = cashSaleReturnService.getCashSaleReturn(pId);
		if(typeId == CashSaleType.INDIV_SELECTION) {
			cashSaleReturn.setCashSaleReturnItems(cashSaleReturnIsService.getCsrItems(pId));
			cashSaleReturn.setCashSaleReturnArLines(new ArrayList<CashSaleReturnArLine>());
		} else {
			cashSaleReturn.setCashSaleReturnItems(cashSaleReturnItemService.getCashSaleReturnItems(pId));
			cashSaleReturn.setCashSaleReturnArLines(cashSaleReturnService.getCsrArLine(pId));
		}

		setRefNo(cashSaleReturn);
		model.addAttribute("cashSaleReturn", cashSaleReturn);
		return "CashSaleReturnView.jsp";
	}

	private void setRefNo(CashSaleReturn cashSaleReturn) {
		if(cashSaleReturn.getCashSaleId() != null) {
			cashSaleReturn.setReferenceNo(cashSaleReturnService
					.getReferenceCs(cashSaleReturn.getCashSaleId()));
		} else if (cashSaleReturn.getRefCashSaleReturnId() != null) {
			CashSaleReturn saleReturn = cashSaleReturnService.getCashSaleReturn(cashSaleReturn.getRefCashSaleReturnId());
			cashSaleReturn.setReferenceNo("CSR No. " + saleReturn.getCsrNumber());
		}
	}

	@RequestMapping(value="{typeId}/csReference", method=RequestMethod.GET)
	public String showCsReference(@PathVariable(value="typeId") Integer typeId, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Retrieving active companies.");
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		loadReferences(model, typeId, PageSetting.START_PAGE, null, null, null, null, null, null, CashSale.STATUS_UNUSED, user);
		return "CSReference.jsp";
	}

	@RequestMapping(value="{typeId}/csReference/reload", method=RequestMethod.GET)
	public String showCsReferenceTable (@PathVariable(value="typeId") Integer typeId,
			@RequestParam (value="companyId") Integer companyId, 
			@RequestParam (value="arCustomerId", required=false)  Integer arCustomerId, 
			@RequestParam (value="arCustomerAccountId", required=false) Integer arCustomerAccountId,
			@RequestParam (value="csNumber", required=false) Integer csNumber, 
			@RequestParam (value="dateFrom", required=false) String dateFrom, 
			@RequestParam (value="dateTo", required=false) String dateTo, 
			@RequestParam (value="status") Integer status, 
			@RequestParam (value="pageNumber") Integer pageNumber, 
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadReferences(model, typeId, pageNumber, companyId, arCustomerId,arCustomerAccountId,
				csNumber, dateFrom, dateTo, status, user);
		return "CSReferenceTable.jsp";
	}

	private void loadReferences(Model model, Integer typeId, Integer pageNumber, Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer csNumber, String dateFrom, String dateTo, Integer status, User user) {
		logger.info("Loading the Cash Sales reference of type: "+typeId);
		model.addAttribute("cashSaleTypeId", typeId);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		Page<CashSaleReturnDto> saleReturnDto = cashSaleReturnService.getCashSaleReturnRef(null, null, companyId, arCustomerId, arCustomerAccountId, 
				csNumber, DateUtil.parseDate(dateFrom), DateUtil.parseDate(dateTo), status, typeId, pageNumber, user);
		model.addAttribute("cashSales", saleReturnDto);
	}

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String setCashSaleReturn (@RequestParam (value="cashSaleId", required=true) Integer cashSaleId,
			Model model, HttpSession session) {
		// Setting cash sale return.
		logger.info("setting cash sale return.");
		CashSale cashSale = cashSaleService.getCashSale(cashSaleId);
		CashSaleReturn csr = null;
		if (cashSale != null) {
			csr = cashSaleReturnService.convertCSToCSR(cashSale);
		}
		String [] exclude = {"item"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(csr, jsonConfig);
		return csr == null ? "No Cash sale found" : jsonObject.toString();
	}

	@RequestMapping(value="{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchCSR(@PathVariable(value="typeId") Integer typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = cashSaleReturnService.search(criteria, typeId);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="loadReturnItems",method=RequestMethod.GET)
	public @ResponseBody String setCashSaleReturn2 (@RequestParam (value="cashSaleId", required=true) Integer cashSaleId,
			@RequestParam (value="typeId", required=true) Integer typeId,
			@RequestParam (value="refType", required=true) String refType,
			Model model, HttpSession session) {
		logger.info("setting cash sale return.");
		CashSaleReturn csr = new CashSaleReturn();
		if(refType.equals("CSR")){
			CashSaleReturn cashSaleReturn = cashSaleReturnService.getCashSaleReturn(cashSaleId);
			csr = cashSaleReturnService.convertCSRDtoToCSR(cashSaleReturn);
		} else if (refType.equals("CS")) {
			CashSale cashSale = cashSaleService.getCashSale(cashSaleId);
			if (cashSale != null) {
				csr = cashSaleReturnService.convertCSToCSR(cashSale);
			}
		}
		String [] exclude = {"item","user","cashSale"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(csr, jsonConfig);
		return jsonObject.toString();
	}
}
