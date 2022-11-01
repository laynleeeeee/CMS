package eulap.eb.web.form.inv;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.service.ItemService;
import eulap.eb.service.inventory.RItemCostUpdateService;

/**
 * Controller class to re-allocate all items from the beginning of the inventory.

 *
 */
@Controller
@RequestMapping ("/reallocateUC")
public class RealloceUnitCostCtrlr {
	@Autowired
	private RItemCostUpdateService itemUCService;
	@Autowired
	private ItemService itemService;
	private static int WAREHOUSE_ID = 1;
	private static Logger logger = Logger.getLogger(RealloceUnitCostCtrlr.class);

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String updateAllItemUnitCost () {
		PageSetting ps = new PageSetting(1);
		Page<Item> allItems = itemService.getAllItems(false, ps);
		Date beginningDate = DateUtil.parseDate("01/01/1900");
		logger.info("====>> Starting the allocation");
		int currentPage = allItems.getCurrentPage();
		logger.info("TOTAL ITEMS to be re allocated: "+allItems.getTotalRecords());
		while(currentPage <= allItems.getLastPage()) {
			if(currentPage > 1) {
				allItems = itemService.getAllItems(false, new PageSetting(currentPage));
			}
			logger.debug("ON PAGE: "+currentPage);

			for (Item item : allItems.getData()) {
				logger.info("CURRENT ITEM: "+item.getId());
				itemUCService.updateItemUnitCost(item.getId(), WAREHOUSE_ID, beginningDate, false);
			}
			currentPage++;
		}
		logger.info("Completed the re-allocation of all items from the beginning.");
		return "Successfully reallocated items.";
	}

	@RequestMapping(method = RequestMethod.GET, value="/perItem")
	public @ResponseBody String updateAllItemUnitCost (@RequestParam(required=true, value="warehouseId") int warehouseId,
			@RequestParam (required=true, value="itemId") String itemIds) {
		Date beginningDate = DateUtil.parseDate("01/01/1900");
		for (String str : itemIds.split(";")) {
			logger.info("====>> Starting the allocation for item id: " + str);
			itemUCService.updateItemUnitCost(Integer.valueOf(str), warehouseId, beginningDate, true);
		}
		logger.info("Completed the re-allocation of all items from the beginning.");
		return "Successfully reallocated items.";
	}
}
