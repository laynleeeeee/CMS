package eulap.eb.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.SerialItemSetup;
import eulap.eb.service.SerialItemService;
import eulap.eb.service.SerialItemSetupService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class for retrieving serialized and non serialized item/s

 */

@Controller
@RequestMapping(value="getRetailSerialItem")
public class GetRetailSerialItem {
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private SerialItemSetupService serialItemSetupService;

	@RequestMapping (value="/getSerialNumber", method = RequestMethod.GET)
	public @ResponseBody String getItemSerialNumber (
			@RequestParam (value="serialNumber", required=false) String serialNumber,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="stockCode", required=false) String stockCode,
			@RequestParam (value="isExact", required=false) boolean isExact,
			@RequestParam (value="referenceObjectId", required=false) Integer referenceObjectId,
			@RequestParam (value="divisionId", required=false) Integer divisionId) {
		List<SerialItem> serialItems = serialItemService.getSerializeItems(serialNumber, companyId,
				warehouseId, stockCode, isExact, referenceObjectId, divisionId);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(serialItems, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/getRetailItems", method = RequestMethod.GET)
	public @ResponseBody String getRetailSerialItems (
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="itemCategoryId", required=false) Integer itemCategoryId,
			@RequestParam (value="stockCode", required=false) String stockCode,
			@RequestParam (value="isSerialized", required=false) boolean isSerialized,
			@RequestParam (value="isActive", required=false) boolean isActive,
			@RequestParam (value="divisionId", required=false) Integer divisionId) {
		List<SerialItemSetup> serialItemSetup = serialItemSetupService.getRetailItems(companyId,
				warehouseId, itemCategoryId, stockCode, isSerialized, isActive, divisionId);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(serialItemSetup, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String getItem(@RequestParam(value="stockCode") String stockCode,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="isSerialized", required=false) boolean isSerialized,
			@RequestParam (value="isActiveOnly", required=false) boolean isActiveOnly,
			@RequestParam (value="divisionId", required=false) Integer divisionId) {
		SerialItemSetup item = serialItemSetupService.getRetailItem(stockCode, companyId,
				warehouseId, isSerialized, isActiveOnly, divisionId);
		String [] exclude = {"itemCategory", "itemSrps", "itemDiscounts", "itemAddOns",
				"buyingPrices", "buyingAddOns", "buyingDiscounts"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(item, jsonConfig);
		return item == null ? "No item found" : jsonObject.toString();
	}
}
