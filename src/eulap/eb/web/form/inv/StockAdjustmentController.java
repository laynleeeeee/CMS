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
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.StockAdjustmentService;
import eulap.eb.validator.inv.StockAdjustmentValidator;

/**
 * Controller class for Stock Adjustment.


 *
 */

@Controller
@RequestMapping("/stockAdjustment")
public class StockAdjustmentController {
	private static Logger logger = Logger.getLogger(StockAdjustmentController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private StockAdjustmentValidator adjustmentValidator;
	@Autowired
	private StockAdjustmentService adjustmentService;
	@Autowired
	private EBFormServiceHandler eBFormServiceHandler;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.GET)
	public String showSAForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) {
		logger.info("Loading the Stock Adjustment form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		StockAdjustment stockAdjustment = new StockAdjustment();
		if(pId != null) {
			logger.debug("Loading the stock adjustment form: "+pId);
			stockAdjustment = adjustmentService.getProcessedStockAdjustment(pId, typeId);
		} else {
			stockAdjustment.setSaDate(new Date());
		}
		stockAdjustment.serializeItems();
		model.addAttribute("stockAdjustment", stockAdjustment);
		loadSelections(model, user, typeId);
		return "StockAdjustmentForm.jsp";
	}

	private void loadSelections(Model model, User user, int typeId) {
		model.addAttribute("typeId", typeId);
		model.addAttribute("companies", companyService.getCompanies(user));
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.POST)
	public String saveSAForm(@PathVariable("typeId") int typeId,
			@ModelAttribute("stockAdjustment") StockAdjustment stockAdjustment,
			BindingResult result, Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		stockAdjustment.deSerializeItems();
		stockAdjustment.setTypeId(typeId);
		stockAdjustment.setStockAdjustmentClassificationId(typeId);
		synchronized (this) {
			adjustmentValidator.validate(stockAdjustment, result);
			if(result.hasErrors()) {
				logger.debug("Form has error/s. Reloading the Stock Adjustment form.");
				loadSelections(model, user, typeId);
				return "StockAdjustmentForm.jsp";
			}
			eBFormServiceHandler.saveForm(stockAdjustment, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", stockAdjustment.getSaNumber());
		model.addAttribute("formId", stockAdjustment.getId());
		logger.info("Successfully saved the Stock Adjustment Form.");
		model.addAttribute("ebObjectId", stockAdjustment.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="{typeId}/viewForm", method=RequestMethod.GET)
	public String vewStockAdjustmentForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model) {
		logger.info("Showing the view form of Stock Adjustment.");
		StockAdjustment stockAdjustment = adjustmentService.getProcessedStockAdjustment(pId, typeId);
		model.addAttribute("stockAdjustment", stockAdjustment);
		model.addAttribute("typeId", typeId);
		return "StockAdjustmentView.jsp";
	}
}
