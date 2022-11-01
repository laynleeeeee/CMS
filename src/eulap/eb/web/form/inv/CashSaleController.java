package eulap.eb.web.form.inv;

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
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArReceiptTypeService;
import eulap.eb.service.CashSaleISService;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.inv.CashSaleValidator;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.json.JSONArray;

/**
 * Controller class for Cash sales.

 */
@Controller
@RequestMapping("/cashSale")
public class CashSaleController {
	private final Logger logger = Logger.getLogger(CashSaleController.class);
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private CashSaleValidator cashSaleValidator;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArReceiptTypeService arReceiptTypeService;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private CashSaleISService cashSaleISService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	private String loadCashSalesForm(CashSale cashSale, Model model, User user) {
		logger.info("Retrieving active companies.");
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		logger.info("Retrieving cash sales types.");
		model.addAttribute("cashSalesTypes", arReceiptTypeService.getArReceiptTypes());
		logger.info("Putting cashSales in a model.");
		cashSale.serializeItems();
		cashSale.serializeCSArLines();
		model.addAttribute("cashSale", cashSale);
		if(cashSale.getCashSaleTypeId() == CashSaleType.INDIV_SELECTION){
			return "CashSaleISForm.jsp";
		}
		return "CashSaleFormV2.jsp";
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.GET)
	public String showCashSalesForm (@PathVariable("typeId") int typeId,
			@RequestParam (value="pId", required = false) Integer pId, Model model, HttpSession session) throws ConfigurationException {
		logger.info("Loading the CS form of type: "+typeId);
		CashSale cashSale = new CashSale();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isEdit = false;
		if (pId == null) {
			Date currentDate = new Date();
			cashSale.setReceiptDate(currentDate);
			cashSale.setMaturityDate(currentDate);
		} else if(typeId == CashSaleType.INDIV_SELECTION) {
			logger.debug("Retrieving cash sale for individual selection using id: "+pId);
			cashSale = cashSaleService.getCsWithItemsAndOC(pId);
		} else {
			logger.debug("Retrieving cash sale  form by id "+pId);
			cashSale = cashSaleService.getCashSaleWithItems(pId);
			isEdit = workflowServiceHandler.isAllowedToEdit(CashSale.class.getSimpleName()+typeId,
					user, cashSale.getFormWorkflow());
		}
		model.addAttribute("isEdit", isEdit);
		cashSale.setCashSaleTypeId(typeId); //Set the Cash Sale Type id.
		return loadCashSalesForm(cashSale, model, user);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable("typeId") int typeId,
			@ModelAttribute ("cashSale") CashSale cashSale, BindingResult result, 
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		cashSale.deserializeItems();
		cashSale.deserializeCSArLines();
		synchronized (this) {
			cashSaleValidator.validate(cashSale, result);
			if (result.hasErrors()) {
				return loadCashSalesForm(cashSale, model, user);
			}
			ebFormServiceHandler.saveForm(cashSale, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", cashSale.getCsNumber());
		model.addAttribute("ebObjectId", cashSale.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/form/{typeId}/viewForm", method=RequestMethod.GET)
	public String viewPurchaseOrder(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model) {
		CashSale cashSale = null;
		logger.info("Loading the view form of Cash Sale");
		if(typeId == CashSaleType.INDIV_SELECTION) {
			cashSale = cashSaleISService.getCashSaleISWithItems(pId);
			model.addAttribute("cashSale", cashSale);
			return "CashSaleISView.jsp";
		} else {
			cashSale = cashSaleService.getCashSaleWithItems(pId);
			model.addAttribute("cashSale", cashSale);
			return "CashSaleView.jsp";
		}
	}

	@RequestMapping(value="/{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchRPurchaseOrder(@PathVariable("typeId") int typeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = cashSaleService.searchCashSales(criteria, typeId);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
