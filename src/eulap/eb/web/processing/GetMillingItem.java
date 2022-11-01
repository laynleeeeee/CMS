package eulap.eb.web.processing;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.service.ItemService;

/**
 * Controller class to retrieve Item using the stock

 *
 */
@Controller
@RequestMapping("/getMillingItem")
public class GetMillingItem {
	@Autowired
	private ItemService itemService;

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String getItem(@RequestParam(value="stockCode") String stockCode,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId,
			@RequestParam(value="itemCategoryId", required=false) Integer itemCategoryId) {
		Item item = itemService.getRetailItem(stockCode, companyId, null, warehouseId, itemCategoryId, false);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(item, jsonConfig);
		return item == null ? "No item found" : jsonObject.toString();
	}

	@RequestMapping(value="/withInactive", method=RequestMethod.GET)
	public @ResponseBody String getItem(@RequestParam(value="stockCode") String stockCode,
			@RequestParam(value="companyId", required=false) Integer companyId) {
		Item item = itemService.getRetailItem(stockCode, companyId, null, false, null, false);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(item, jsonConfig);
		return item == null ? "No item found" : jsonObject.toString();
	}

	@RequestMapping(value="existingStock", method=RequestMethod.GET)
	public @ResponseBody String getESByWarehouse(@RequestParam(value="itemId") Integer itemId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId) {
		return NumberFormatUtil.format(itemService.getItemExistingStocks(itemId, warehouseId));
	}
}
