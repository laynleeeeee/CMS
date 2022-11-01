package eulap.eb.web.form.inv;

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
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArReceiptTypeService;
import eulap.eb.service.CashSaleProcessingService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.validator.inv.CashSaleProcessingValidator;

/**
 * Controller class for Cash Sales - Processing.

 */
@Controller
@RequestMapping("/csProcessing")
public class CashSaleProcessingController {
	private final Logger logger = Logger.getLogger(CashSaleProcessingController.class);
	@Autowired
	private CashSaleProcessingValidator csProcessingValidator;
	@Autowired
	private CashSaleProcessingService csProcessingService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private ArReceiptTypeService arReceiptTypeService;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	private String loadCashSalesForm(CashSale cashSale, Model model, User user) {
		cashSale.setCashSaleTypeId(CashSaleType.PROCESSING);
		logger.info("Retrieving active companies.");
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		cashSale.serializeItems();
		logger.info("Retrieving cash sales types.");
		model.addAttribute("cashSalesTypes", arReceiptTypeService.getArReceiptTypes());
		model.addAttribute("csProcessing", cashSale);
		return "CashSaleProcessingForm.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String showCashSalesForm (@RequestParam (value="pId", required = false) Integer pId, Model model, HttpSession session) {
		logger.info("Loading the CS form of type: "+CashSaleType.PROCESSING);
		CashSale cashSale = new CashSale();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isEdit = false;
		if (pId == null) {
			Date currentDate = new Date();
			cashSale.setReceiptDate(currentDate);
		} else {
			cashSale = csProcessingService.getCsProcessingAndItems(pId);
			logger.debug("Retrieving cash sale form by id "+pId);
		}
		model.addAttribute("isEdit", isEdit);
		return loadCashSalesForm(cashSale, model, user);
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String submit (@ModelAttribute ("csProcessing") CashSale cashSale, BindingResult result, 
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		cashSale.deserializeItems();
		synchronized (this) {
			cashSale = csProcessingService.processRawMaterialsAndFinishedProds(cashSale);
			csProcessingValidator.validate(cashSale, result);
			if (result.hasErrors()) {
				return loadCashSalesForm(cashSale, model, user);
			}
			cashSale.setCashSaleTypeId(CashSaleType.PROCESSING);
			ebFormServiceHandler.saveForm(cashSale, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", cashSale.getCsNumber());
		model.addAttribute("formId", cashSale.getId());
		model.addAttribute("ebObjectId", cashSale.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String viewCsProcessingForm(@RequestParam(value="pId", required=false) Integer pId, Model model) {
		CashSale cashSale = csProcessingService.getCsProcessingAndItems(pId);
		logger.info("Loading the view form of Cash Sale");
		model.addAttribute("cashSale", cashSale);
		return "CashSaleProcessingView.jsp";
	}

	@RequestMapping(method = RequestMethod.GET, value="/recomputeCost")
	public @ResponseBody String recomputeFinishedProductCost (@RequestParam(value="companyId") String companyIds) {
		logger.info("Recomputing cash sale - finished product costs");
		for (String companyId : companyIds.split(";")) {
			csProcessingService.recomputeFinishedProductCost(Integer.valueOf(companyId));
		}
		logger.info("Successfully recompute cash sale - processing finished product costs");
		return "Successfully recompute cash sale - processing finished product costs";
	}
}
