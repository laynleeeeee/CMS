package eulap.eb.web.form.inv;

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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ItemAddOnTypeService;
import eulap.eb.service.ItemDiscountTypeService;
import eulap.eb.service.ItemService;
import eulap.eb.service.UnitMeasurementService;
import eulap.eb.validator.inv.RetailItemValidator;

/**
 * Controller for retail item.

 *
 */
@Controller
@RequestMapping("/admin/rItems")
public class RItemController {
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemAddOnTypeService itemAddOnTypeService;
	@Autowired
	private ItemDiscountTypeService itemDiscountTypeService;
	@Autowired
	private UnitMeasurementService unitMeasurementService;
	@Autowired
	private RetailItemValidator retailItemValidator;

	private final Logger logger = Logger.getLogger(RItemController.class);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
         DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showRItems(Model model){
		logger.info("Fetching unit of measurements.");
		model.addAttribute("unitMeasurements", unitMeasurementService.getActiveUnitMeasurements());
		logger.info("Fetching item categories.");
		model.addAttribute("itemCategories", itemService.getAllActiveItemCategory());

		model.addAttribute("items", itemService.getRetailItems("", "", null, null, -1, false, 1));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		return "RetailItem";
	}

	@RequestMapping (method = RequestMethod.GET, params={"stockCode", "description", "unitMeasurementId", 
			"itemCategoryId", "status", "pageNumber" })
	public String loadItems (@RequestParam String stockCode,
			@RequestParam String description,
			@RequestParam Integer unitMeasurementId,
			@RequestParam Integer itemCategoryId,
			@RequestParam int status,
			@RequestParam int pageNumber,
			Model model) {
		model.addAttribute("items", itemService.getRetailItems(stockCode, description, 
				unitMeasurementId == -1 ? null : unitMeasurementId, 
						itemCategoryId == -1 ? null : itemCategoryId, 
								status, false, pageNumber));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "RetailItemTable";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form")
	public String addItem(Model model) {
		Item item = new Item();
		item.setActive(true);
		return showItemForm(model, item);
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form", params={"itemId"})
	public String editItem(@RequestParam int itemId, Model model) {
		Item item = itemService.getItem(itemId);
		if (item != null) {
			itemService.getItemDetails(item);
		}
		return showItemForm(model, item);
	}

	public String showItemForm (Model model, Item item) {
		logger.info("Fetching unit of measurements.");
		model.addAttribute("unitMeasurements", unitMeasurementService.getActiveUnitMeasurements());
		logger.info("Fetching unit of measurements.");
		model.addAttribute("itemCategories", itemService.getAllActiveItemCategory());
		logger.info("Fetching the item discount types.");
		model.addAttribute("itemDiscountTypes", itemDiscountTypeService.getItemDiscountTypes());
		logger.debug("Adding item add on types in the model.");
		model.addAttribute("itemAddOnTypes", itemAddOnTypeService.getAllTypes());
		logger.debug("Adding the item in the model.");
		model.addAttribute("item", item);
		return "RetailItemForm";
	}

	@RequestMapping (method = RequestMethod.POST, value = "/form")
	public String saveItem (@ModelAttribute("item") Item item, 
			BindingResult result, Model model, HttpSession session)  {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		itemService.processItemDetails(item);
		retailItemValidator.validate(item, result);
		if (result.hasErrors()) {
			logger.debug("Encountered validation errors.");
			return showItemForm(model, item);
		}
		logger.info("Saving the item : " + item);
		itemService.saveRItem(item, user.getId());
		logger.debug("Successfully saved the item : " + item);
		return "successfullySaved";
	}
}
