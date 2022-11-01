package eulap.eb.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.service.ItemService;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class to retrieve Item using the stock

 *
 */
@Controller
@RequestMapping("/getItem")
public class GetRetailItem {
	@Autowired
	private ItemService itemService;

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String getItem(@RequestParam(value="stockCode") String stockCode,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId,
			@RequestParam(value="itemCategoryId", required=false) Integer itemCategoryId,
			@RequestParam (value="isExcludeFG", required=false) Boolean isExcludeFG) {
		Item item = itemService.getRetailItem(stockCode, companyId, divisionId, warehouseId, itemCategoryId, isExcludeFG);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(item, jsonConfig);
		return item == null ? "No item found" : jsonObject.toString();
	}

	@RequestMapping(value="/withInactive", method=RequestMethod.GET)
	public @ResponseBody String getItem(@RequestParam(value="stockCode") String stockCode,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="isExcludeFG", required=false) Boolean isExcludeFG) {
		Integer wId = warehouseId == null ? -1 : warehouseId;
		Item item = itemService.getRetailItem(stockCode, companyId, wId, false, divisionId, isExcludeFG);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(item, jsonConfig);
		return item == null ? "No item found" : jsonObject.toString();
	}

	@RequestMapping(value="existingStockBySC", method=RequestMethod.GET)
	public @ResponseBody String getESByWarehouseAndSC(@RequestParam(value="stockCode") String stockCode,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId) {
		if (stockCode != null) {
			Integer wId = warehouseId == null ? -1 : warehouseId;
			Item item = itemService.getRetailItem(stockCode, companyId, wId);
			if (item != null) {
				return NumberFormatUtil.format(item.getExistingStocks());
			}
		}
		return "0.00";
	}

	@RequestMapping(value="availableStocks", method=RequestMethod.GET)
	public @ResponseBody String getAvailStocksByWarehouse(@RequestParam(value="stockCode") String stockCode,
			@RequestParam(value="warehouseId") Integer warehouseId) {
		Item item = itemService.getItemByStockCode(stockCode);
		if(item != null) {
			double availStocks = itemService.getTotalAvailStocks(item.getStockCode(), warehouseId);
			item.setExistingStocks(availStocks);
			String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
					"buyingPrices", "buyingAddOns", "buyingDiscounts"};
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(exclude);
			JSONObject jsonObject = JSONObject.fromObject(item, jsonConfig);
			return jsonObject.toString();
		}
		return "No item found";
	}

	@RequestMapping(value="/existingStocks", method=RequestMethod.GET)
	public @ResponseBody String getExistingStocks(@RequestParam(value="itemId") Integer itemId,
			@RequestParam(value="warehouseId") Integer warehouseId) {
		Double availStocks = itemService.getItemExistingStocks(itemId, warehouseId);
		return availStocks.toString();
	}

	@RequestMapping(value="/getWeightedAverageWithRate", method=RequestMethod.GET)
	public @ResponseBody String getWeigtedAverageWithRate(@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="warehouseId") Integer warehouseId,
			@RequestParam(value="itemId") Integer itemId,
			@RequestParam(value="currencyId", required=false) Integer currencyId) {
		Double wac = itemService.getItemWeightedAverage(companyId, warehouseId, itemId, currencyId);
		return wac != null ? wac.toString() : "0.00";
	}
}
