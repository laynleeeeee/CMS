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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.SerialItemSetup;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.SerialItemSetupService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemDiscountTypeService;
import eulap.eb.service.ItemService;
import eulap.eb.service.RmItemService;
import eulap.eb.service.UnitMeasurementService;
import eulap.eb.web.dto.SerialItemSetupDto;

/**
 * A controller class that handles special functions
 * for Serial Item Setup

 */

@Controller
@RequestMapping("admin/serialItemSetup")
public class SerialItemSetupController  {
	private static final Logger logger =  Logger.getLogger(SerialItemSetupController.class);
	@Autowired
	private SerialItemSetupService serialItemSetupService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private RmItemService rmItemService;
	@Autowired
	private ItemDiscountTypeService itemDiscountTypeService;
	@Autowired
	private UnitMeasurementService unitMeasurementService;
	@Autowired
	private DivisionService divisionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String loadAllItems(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Loading all the items.");
		loadSelections(model, null);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("items", rmItemService.getAllItemWithDivision(-1, "", "", null, null, -1, 1));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, PageSetting.START_PAGE);
		return "RMSerialItemMain";
	}

	@RequestMapping (value="/search", method = RequestMethod.GET)
	public String searchRItems(@RequestParam(value="stockCode") String stockCode,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam(value="description") String description,
			@RequestParam(value="unitMeasurementId") Integer unitMeasurementId,
			@RequestParam(value="itemCategoryId") Integer itemCategoryId,
			@RequestParam(value="status") int status,
			@RequestParam(value="pageNumber") int pageNumber,
			Model model) {
		model.addAttribute("items", rmItemService.getAllItemWithDivision(divisionId, stockCode, description,
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
		loadSelections(model, rItem.getItemCategoryId());
		serialItemSetupDto.setItem(rItem);
		serialItemSetupDto.setSerialItemSetup(serialItemSetup);
		model.addAttribute("serialItemSetupDto", serialItemSetupDto);
		return "RMSerialItemForm";
	}

	private void loadSelections(Model model, Integer itemCategoryId) {
		model.addAttribute("itemDiscountTypes", itemDiscountTypeService.getItemDiscountTypes());
		model.addAttribute("unitMeasurements", unitMeasurementService.getActiveUnitMeasurements());
		if(itemCategoryId != null) {
			model.addAttribute("itemCategories", serialItemSetupService.getAllWithInactive(itemCategoryId));
		} else {
			model.addAttribute("itemCategories", itemService.getAllActiveItemCategory());
		}
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveRItem(@ModelAttribute("serialItemSetupDto") SerialItemSetupDto serialItemSetupDto,
			BindingResult result, Model model, HttpSession session) {
		logger.info("Processing the retail item to be saved.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Item item = serialItemSetupDto.getItem();
		rmItemService.processItemDetails(item);
		serialItemSetupService.validateRetailItem(serialItemSetupDto, result);
		if (result.hasErrors()) {
			logger.info("Form has/have error/s. Reloading the form.");
			loadSelections(model, item.getItemCategoryId());
			model.addAttribute("serialItemSetupDto", serialItemSetupDto);
			return "RMSerialItemForm";
		}
		logger.info("Saving the item with stock code : " + item.getStockCode());
		serialItemSetupService.saveRetailItem(serialItemSetupDto, user.getId());
		logger.info("Successfully saved/edited the item with stock code: " + item.getStockCodeAndDesc());
		return "successfullySaved";
	}
}