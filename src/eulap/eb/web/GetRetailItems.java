package eulap.eb.web;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.Item;
import eulap.eb.service.ItemService;

/**
 * Controller class to retrieve Items using the stock

 *
 */
@Controller
@RequestMapping("/getRItems")
public class GetRetailItems {
	@Autowired
	private ItemService itemService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getItems(@RequestParam (value="term", required=false) String stockCode) {
		List<Item> items = itemService.getItemsByCodeAndDesc(stockCode);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(items, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/filter", method = RequestMethod.GET)
	public @ResponseBody String getRItems(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="itemCategoryId", required=false) Integer itemCategoryId,
			@RequestParam (value="stockCode", required=false) String stockCode,
			@RequestParam (value="stockCodes", required=false) String stockCodes,
			@RequestParam (value="isShowAll", required=false) Boolean isShowAll,
			@RequestParam (value="isExcludeFG", required=false) Boolean isExcludeFG) {
		List<Item> items = null;
		if (isShowAll == null) {
			items = itemService.getItemsByCodeAndDesc(companyId, divisionId, warehouseId,
					itemCategoryId, stockCode, stockCodes, isExcludeFG);
		} else {
			items = itemService.getRetailItems(companyId, divisionId, warehouseId,
					itemCategoryId, stockCodes, null, isShowAll);
		}
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(items, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/stockCode", method = RequestMethod.GET)
	public @ResponseBody String getItem(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="itemCategoryId", required=false) Integer itemCategoryId,
			@RequestParam (value="stockCode", required=false) String stockCode) {
		List<Item> item = itemService.getItemsByCodeAndDesc(companyId, divisionId, 0,
				itemCategoryId, stockCode, null, null);
		if (item != null && !item.isEmpty()) {
			String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
					"buyingPrices", "buyingAddOns", "buyingDiscounts"};
			JsonConfig jConfig = new JsonConfig();
			jConfig.setExcludes(exclude);
			JSONObject jsonObject = JSONObject.fromObject(item.iterator().next(), jConfig);
			return jsonObject.toString();
		}
		return "";
	}

	@RequestMapping (value="/filterItems", method = RequestMethod.GET)
	public @ResponseBody String getRItemsExcludedWarehouse(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="itemCategoryId", required=false) Integer itemCategoryId,
			@RequestParam (value="stockCode", required=false) String stockCode,
			@RequestParam (value="stockCodes", required=false) String stockCodes) {
		List<Item> items = itemService.getRetailItems(companyId, divisionId, warehouseId,
				itemCategoryId, stockCodes, null, false);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(items, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/items", method = RequestMethod.GET)
	public @ResponseBody String getItemsWithoutExistingStocks(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="stockCode", required=false) String stockCode) {
		List<Item> items = itemService.getRetailItems(stockCode, companyId);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(items, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/mainProds", method = RequestMethod.GET)
	public @ResponseBody String getMainProducts(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="stockCode", required=false) String stockCode,
			@RequestParam (value="isExact", required=false) Boolean isExact,
			@RequestParam (value="isActiveOnly", required=false) Boolean isActiveOnly) {
		isExact = isExact != null ? isExact : false;
		isActiveOnly = isActiveOnly != null ? isActiveOnly : true;
		List<Item> items = itemService.getMainProducts(stockCode, companyId, isExact, isActiveOnly);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(items, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/filter/mixing", method = RequestMethod.GET)
	public @ResponseBody String getMixingItems(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="itemCategoryId", required=false) Integer itemCategoryId,
			@RequestParam (value="stockCode", required=false) String stockCode,
			@RequestParam (value="stockCodes", required=false) String stockCodes,
			@RequestParam (value="isShowAll", required=false) Boolean isShowAll) {
		List<Item> items = null;
		if (isShowAll == null) {
			items = itemService.getItemsByCodeAndDesc(companyId, divisionId, warehouseId,
					itemCategoryId, stockCode, stockCodes, true);
		} else {
			items = itemService.getRetailItems(companyId, divisionId, warehouseId,
					itemCategoryId, stockCodes, null, isShowAll, true, null);
		}
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(items, jConfig);
		return jsonArray.toString();
	}
}
