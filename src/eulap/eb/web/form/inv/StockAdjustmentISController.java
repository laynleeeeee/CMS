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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.StockAdjustmentIsService;
import eulap.eb.validator.inv.StockAdjustmentIsValidator;

/**
 * Controller class for Stock Adjustment OUT - Individual Selection of available stocks.

 *
 */
@Controller
@RequestMapping(value="/stockAdjustmentIS")
public class StockAdjustmentISController {
	private static Logger logger = Logger.getLogger(StockAdjustmentISController.class);
	@Autowired
	private StockAdjustmentIsValidator stockAdjustmentValidator;
	@Autowired
	private StockAdjustmentIsService stockAdjustmentService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}//form", method=RequestMethod.GET)
	public String showForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		StockAdjustment stockAdjustment = new StockAdjustment();
		if(pId != null) {
			logger.info("Editing the Stock Adjustment - IS of id: "+pId);
			stockAdjustment = stockAdjustmentService.getSAIsAndProcessedItems(pId, typeId);
		} else {
			stockAdjustment.setSaDate(new Date());
		}
		stockAdjustment.setTypeId(typeId);
		stockAdjustment.serializeItems();
		loadSelections(model, user, typeId, stockAdjustment);
		return "StockAdjustmentISForm.jsp";
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.POST)
	public String saveForm(@PathVariable("typeId") int typeId,
			@ModelAttribute("stockAdjustmentIS") StockAdjustment stockAdjustment,
			BindingResult result, Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		logger.info("Validating the Stock Adjustment form before saving.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		stockAdjustment.deSerializeItems();
		stockAdjustment.setTypeId(typeId);
		stockAdjustment.setStockAdjustmentClassificationId(typeId);
		synchronized (this) {
			stockAdjustmentValidator.validate(stockAdjustment, result);
			if(result.hasErrors()) {
				logger.info("Form has error/s. Reloading the form.");
				stockAdjustmentService.reloadForm(stockAdjustment);
				loadSelections(model, user, typeId, stockAdjustment);
				return "StockAdjustmentISForm.jsp";
			}
			ebFormServiceHandler.saveForm(stockAdjustment, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", stockAdjustment.getSaNumber());
		model.addAttribute("formId", stockAdjustment.getId());
		logger.info("Successfully saved the Stock Adjustment - IS Form.");
		model.addAttribute("ebObjectId", stockAdjustment.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="{typeId}//view", method=RequestMethod.GET)
	public String showViewForm(@PathVariable("typeId") int typeId, @RequestParam(value="pId") Integer pId, Model model) {
		model.addAttribute("stockAdjustment", stockAdjustmentService.getSAIsWithAvailStocks(pId, typeId));
		model.addAttribute("typeId", typeId);
		return "StockAdjustmentISView.jsp";
	}

	private void loadSelections(Model model, User user, int typeId, StockAdjustment stockAdjustment) {
		model.addAttribute("typeId", typeId);
		stockAdjustmentService.loadCompanies(model, user);
		model.addAttribute("stockAdjustmentIS", stockAdjustment);
	}
}