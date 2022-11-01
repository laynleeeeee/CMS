package eulap.eb.web;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBuyingAddOn;
import eulap.eb.domain.hibernate.ItemBuyingDiscount;
import eulap.eb.domain.hibernate.ItemBuyingPrice;
import eulap.eb.service.ItemBuyingService;

/**
 * Controller class to retrieve the buying details of item.

 *
 */
@Controller
@RequestMapping("/getBuyingDetails")
public class GetItemBuyingDetails {
	private static Logger logger = Logger.getLogger(GetItemBuyingDetails.class);
	@Autowired
	private ItemBuyingService buyingService;

	@RequestMapping (value="/items", method = RequestMethod.GET)
	public @ResponseBody String getBuyingItems(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="stockCode", required=false) String stockCode) {
		List<Item> items = buyingService.getBuyingItems(companyId, stockCode);
		if(items.isEmpty()) {
			logger.debug("No items found for stock code: "+stockCode);
			return "No items for buying found.";
		}
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(items, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/item", method = RequestMethod.GET)
	public @ResponseBody String getBuyingItem(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="stockCode", required=false) String stockCode) {
		Item item = buyingService.getBuyingItem(companyId, warehouseId, stockCode);
		if(item == null) {
			logger.debug("No item found for stock code: "+stockCode);
			return "No item for buying found.";
		}
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(item, jConfig);
		logger.info("Retrieved the buying item: "+item.getStockCode()+" with existing stocks: "+item.getExistingStocks());
		return jsonObject.toString();
	}

	@RequestMapping(value="/price", method=RequestMethod.GET)
	public @ResponseBody String getBuyingPrice(@RequestParam(value="itemId") Integer itemId,
			@RequestParam(value="companyId") Integer companyId) {
		logger.info("Retrieving the latest buying price of item: "+itemId);
		ItemBuyingPrice buyingPrice = buyingService.getLatestBPrice(itemId, companyId);
		if(buyingPrice == null) {
			logger.debug("No buying price configured for item id: "
					+itemId+" under company id: "+companyId);
			return "No buying price found.";
		}
		String [] exclude = {"item"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(buyingPrice, jConfig);
		logger.info("Latest buying price of item id "+itemId+" is "+buyingPrice.getBuyingPrice());
		return jsonObject.toString();
	}

	@RequestMapping(value="/addOns", method=RequestMethod.GET)
	public @ResponseBody String getBuyingAddOns(@RequestParam(value="itemId") Integer itemId,
			@RequestParam(value="companyId") Integer companyId) {
		List<ItemBuyingAddOn> addOns = buyingService.getBuyingAddOns(itemId, companyId);
		if(addOns.isEmpty()) {
			logger.debug("No add ons configured for item id: "
					+itemId+" under company id: "+companyId);
			return "No add ons found.";
		}
		logger.info("Retrieved "+addOns.size()+" buying add ons of item: "+itemId);
		String [] exclude = {"item"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(addOns, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/addOn", method=RequestMethod.GET)
	public @ResponseBody String getAddOn(@RequestParam(value="id") Integer buyingAddOnId) {
		logger.info("Retrieving the latest buying price of item: "+buyingAddOnId);
		ItemBuyingAddOn buyingAddOn = buyingService.getBAddOn(buyingAddOnId);
		if(buyingAddOn == null) {
			return "No buying add on found.";
		}
		String [] exclude = {"item"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(buyingAddOn, jConfig);
		logger.info("Retrieved the add on with value: "+buyingAddOn.getValue());
		return jsonObject.toString();
	}

	@RequestMapping(value="/discounts", method=RequestMethod.GET)
	public @ResponseBody String getBuyingDiscounts(@RequestParam(value="itemId") Integer itemId,
			@RequestParam(value="companyId") Integer companyId) {
		List<ItemBuyingDiscount> buyingDiscounts = buyingService.getBuyingDiscounts(itemId, companyId);
		if(buyingDiscounts.isEmpty()) {
			logger.debug("No discounts configured for item id: "
					+itemId+" under company id: "+companyId);
			return "No discounts found.";
		}
		logger.info("Retrieved "+buyingDiscounts.size()+" buying discounts for item: "
				+itemId+" under company id: "+companyId);
		String [] exclude = {"item"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(buyingDiscounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/discount", method=RequestMethod.GET)
	public @ResponseBody String getDiscount(@RequestParam(value="id") Integer buyingDiscId,
			@RequestParam(value="quantity") Double quantity, @RequestParam(value="price") Double price) {
		logger.info("Retrieving the latest buying price of item: "+buyingDiscId);
		double computedDiscount = buyingService.computeDiscount(buyingDiscId, quantity, price);
		return String.valueOf(computedDiscount);
	}
}
