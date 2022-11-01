package eulap.eb.web;

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
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.SerialItemSetup;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ItemDiscountTypeService;
import eulap.eb.service.ItemService;
import eulap.eb.service.RmItemService;
import eulap.eb.service.SerialItemSetupService;
import eulap.eb.service.UnitMeasurementService;
import eulap.eb.web.dto.SerialItemSetupDto;

/**
 * Controller for Retail Item for Rice Mill.

 *
 */
@Controller
@RequestMapping("/admin/rmItems")
public class RmItemController {
	private static Logger logger = Logger.getLogger(RmItemController.class);
	@Autowired
	private ItemService itemService;
	@Autowired
	private RmItemService rmItemService;
	@Autowired
	private ItemDiscountTypeService itemDiscountTypeService;
	@Autowired
	private UnitMeasurementService unitMeasurementService;
	@Autowired
	private SerialItemSetupService serialItemSetupService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String loadAllItems(Model model) {
		logger.info("Loading all the items.");
		loadSelections(model, null, null, null);
		model.addAttribute("items", rmItemService.getAllItems("", "", null, null, -1, 1));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, PageSetting.START_PAGE);
		return "RMSerialItemMain";
	}

	@RequestMapping (value="/search", method = RequestMethod.GET)
	public String searchRItems(@RequestParam(value="stockCode") String stockCode,
			@RequestParam(value="description") String description,
			@RequestParam(value="unitMeasurementId") Integer unitMeasurementId,
			@RequestParam(value="itemCategoryId") Integer itemCategoryId,
			@RequestParam(value="status") int status,
			@RequestParam(value="pageNumber") int pageNumber,
			Model model) {
		model.addAttribute("items", rmItemService.getAllItems(stockCode, description,
				unitMeasurementId == -1 ? null : unitMeasurementId,
				itemCategoryId == -1 ? null : itemCategoryId, status, pageNumber));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "RMSerialItemTable";
	}

	@RequestMapping (value="/form", method=RequestMethod.GET)
	public String getRItem(@RequestParam(value="itemId", required=false) Integer itemId, Model model) {
		SerialItemSetupDto serialItemSetupDto = new SerialItemSetupDto();
		Item rItem = new Item();
		SerialItemSetup serialItemSetup = new SerialItemSetup();
		if(itemId != null) {
			logger.info("Edting the item with id: "+itemId);
			rItem = rmItemService.getRItemWithBuyingDetails(itemId);
			serialItemSetup = serialItemSetupService.getSerialItemSetup(itemId);
		} else {
			logger.info("Showing the add form of the retail item.");
			rItem.setActive(true);
		}
		loadSelections(model, rItem.getItemCategoryId(), rItem.getItemVatTypeId(), rItem.getUnitMeasurementId());
		serialItemSetupDto.setItem(rItem);
		serialItemSetupDto.setSerialItemSetup(serialItemSetup);
		model.addAttribute("serialItemSetupDto", serialItemSetupDto);
		return "RMSerialItemForm";
	}

	private void loadSelections(Model model, Integer itemCategoryId, Integer itemVatTypeId, Integer uomId) {
		model.addAttribute("itemDiscountTypes", itemDiscountTypeService.getItemDiscountTypes());
		model.addAttribute("unitMeasurements", unitMeasurementService.getActiveUnitMeasurements(uomId));
		model.addAttribute("vatTypes", serialItemSetupService.getItemVatTypes(itemVatTypeId));
		model.addAttribute("itemCategories", serialItemSetupService.getAllWithInactive(itemCategoryId));
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveRItem(@ModelAttribute("serialItemSetupDto") SerialItemSetupDto serialItemSetupDto,
			BindingResult result, Model model, HttpSession session) {
		logger.info("Processing the retail item to be saved.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Item item = serialItemSetupDto.getItem();
		itemService.processItemDetails(item);
		serialItemSetupService.validateRetailItem(serialItemSetupDto, result);
		if (result.hasErrors()) {
			logger.info("Form has/have error/s. Reloading the form.");
			loadSelections(model, item.getItemCategoryId(), item.getItemVatTypeId(), item.getUnitMeasurementId());
			model.addAttribute("serialItemSetupDto", serialItemSetupDto);
			return "RMSerialItemForm";
		}
		logger.info("Saving the item with stock code : " + item.getStockCode());
		serialItemSetupService.saveRetailItem(serialItemSetupDto, user.getId());
		logger.info("Successfully saved/edited the item with stock code: " + item.getStockCodeAndDesc());
		return "successfullySaved";
	}

	@RequestMapping(value="/generateBarcode", method=RequestMethod.GET)
	public @ResponseBody String generateBarcode(
			@RequestParam (value="itemCategoryId", required=false) Integer itemCategoryId) {
		return serialItemSetupService.generateBarcode(itemCategoryId);
	}

	@RequestMapping(value="/printBarcode", method=RequestMethod.GET)
	public String printBarcode(@RequestParam (value="barcode", required=false) String barcode,
			Model model) {
		model.addAttribute("format", "pdf");
		model.addAttribute("barcode", barcode);
		return "Barcode.jasper";
	}
}
