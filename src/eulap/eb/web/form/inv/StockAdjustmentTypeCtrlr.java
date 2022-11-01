package eulap.eb.web.form.inv;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.StockAdjustmentType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.StockAdjustmentTypeService;
import eulap.eb.validator.inv.StockAdjustmentTypeValidator;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;

/**
 * Controller class for Stock Adjustment Type.

 *
 */
@Controller
@RequestMapping("/admin/adjustmentType")
public class StockAdjustmentTypeCtrlr {
	private static Logger logger = Logger.getLogger(StockAdjustmentTypeCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private StockAdjustmentTypeService satService;
	@Autowired
	private StockAdjustmentTypeValidator satValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method=RequestMethod.GET)
	public String loadAllSATs(HttpSession session, Model model) {
		logger.info("Showing the main page of Stock Adjustment Type module in admin.");
		model.addAttribute("status", SearchStatus.getSearchStatus());
		loadCompanies(session, model, 0);
		loadAdjustmentTypes(model, -1, "", SearchStatus.All.name(), 1);
		return "StockAdjustmentType.jsp";
	}

	private void loadCompanies(HttpSession session, Model model, int companyId) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchAdjustmentTypes(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="page") Integer pageNumber,
			HttpSession session, Model model) {
		loadAdjustmentTypes(model, companyId, name, status, pageNumber);
		return "StockAdjustmentTypeTable.jsp";
	}

	private void loadAdjustmentTypes(Model model, Integer companyId, String name, String status, Integer pageNumber) {
		Page<StockAdjustmentType> adjustmentTypes = satService.getAllSATypes(companyId, name, status, pageNumber);
		model.addAttribute("adjustmentTypes", adjustmentTypes);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showSATForm(@RequestParam(value="adjustmentTypeId", required=false) Integer adjustmentTypeId,
			HttpSession session, Model model) {
		StockAdjustmentType adjustmentType = new StockAdjustmentType();
		int companyId = 0;
		if (adjustmentTypeId != null) {
			logger.info("Editing the Stock Adjustment type: "+adjustmentTypeId);
			adjustmentType = satService.getSAdjustmentType(adjustmentTypeId);
			companyId = adjustmentType.getAcctCombi().getCompanyId();
		} else {
			adjustmentType.setActive(true);
		}
		model.addAttribute("stockAdjustmentType", adjustmentType);
		loadCompanies(session, model, companyId);
		logger.info("Showing the Stock Adjustment Type form.");
		return "StockAdjustmentTypeForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveSAdjustmentType(@ModelAttribute("stockAdjustmentType") StockAdjustmentType adjustmentType,
			BindingResult result, HttpSession session, Model model) {
		logger.info("Saving the stock adjustment type form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		satValidator.validate(adjustmentType, result);
		if (result.hasErrors()) {
			loadCompanies(session, model, adjustmentType.getCompanyId());
			return "StockAdjustmentTypeForm.jsp";
		}
		satService.saveSAdjustmentType(adjustmentType, user);
		return "successfullySaved";
	}
}
