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

import eulap.eb.domain.hibernate.ItemAddOn;
import eulap.eb.service.ItemAddOnService;

/**
 * Controller that gets the item add ons.

 *
 */
@Controller
@RequestMapping("/getItemAddOn")
public class GetItemAddOn {
	@Autowired
	private ItemAddOnService itemAddOnService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getItemAddOns(
			@RequestParam (value="itemId", required=false) Integer itemId,
			@RequestParam (value="companyId", required=false) Integer companyId) {
		List<ItemAddOn> itemAddOns = itemAddOnService.getItemAddOnsByItem(itemId, companyId, true);
		return getJsonArray(itemAddOns);
	}

	@RequestMapping(value="/withInactive", method = RequestMethod.GET)
	public @ResponseBody String getIAddOnsWithSlctdInactiveIAId (
			@RequestParam (value="itemId", required=false) Integer itemId,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="itemAddOnId", required=false) Integer itemAddOnId) {
		List<ItemAddOn> itemAddOns =
				itemAddOnService.getAddOnsWSlctdInactiveAddOnId(itemId, companyId, itemAddOnId);
		return getJsonArray(itemAddOns);
	}

	private String getJsonArray(List<ItemAddOn> itemAddOns) {
		String [] exclude = {"item", "company"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(itemAddOns, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value="/byId")
	public @ResponseBody String getItemAddOn(
			@RequestParam (value="itemAddOnId", required=true) Integer itemAddOnId) {
		ItemAddOn itemAddOn = itemAddOnService.getItemAddOn(itemAddOnId);
		return getJsonObject(itemAddOn);
	}

	@RequestMapping(method = RequestMethod.GET, value="/computeAddOn")
	public @ResponseBody String computeAddOn(@RequestParam (value="itemAddOnId") Integer itemAddOnId,
			@RequestParam (value="srp") double srp, @RequestParam (value="quantity") double quantity,
			@RequestParam (value="taxTypeId", required=false) Integer taxTypeId) {
		ItemAddOn itemAddOn = itemAddOnService.computeAddOnValue(itemAddOnId, srp, quantity, taxTypeId);
		return getJsonObject(itemAddOn);
	}

	private String getJsonObject(ItemAddOn itemAddOn) {
		String [] exclude = {"item", "company"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(itemAddOn, jConfig);
		return jsonObject.toString();
	}
}
