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

import eulap.eb.domain.hibernate.ItemDiscount;
import eulap.eb.service.ItemDiscountService;

/**
 * Controller that gets the item discounts.

 *
 */
@Controller
@RequestMapping("/getItemDiscount")
public class GetItemDiscount {
	@Autowired
	private ItemDiscountService itemDiscountService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getItemDiscounts(
			@RequestParam (value="itemId", required=false) Integer itemId) {
		List<ItemDiscount> itemDiscounts = itemDiscountService.getItemDiscountsByItem(itemId, true);
		JSONArray jsonArray = JSONArray.fromObject(itemDiscounts);
		return jsonArray.toString();
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getItemDiscount(
			@RequestParam (value="itemDiscountId", required=true) Integer itemDiscountId) {
		ItemDiscount itemDiscount = itemDiscountService.getItemDiscount(itemDiscountId);
		String [] exclude = {"item", "ItemDiscountType"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(itemDiscount, jConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value="/filter", method = RequestMethod.GET)
	public @ResponseBody String getItemDiscounts(
			@RequestParam (value="itemId") Integer itemId, 
			@RequestParam (value="companyId") Integer companyId) {
		List<ItemDiscount> itemDiscounts = 
				itemDiscountService.getIDsByItemIdCodeAndCompany(itemId, companyId);
		String [] exclude = {"item"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(itemDiscounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/withInactive", method = RequestMethod.GET)
	public @ResponseBody String getDiscountsByItemIdAndCompanyId (
			@RequestParam (value="itemId", required=false) Integer itemId,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="itemDiscountId", required=false) Integer itemDiscountId) {
		List<ItemDiscount> itemDiscounts =
				itemDiscountService.getDiscountsWithSlctdDiscId(itemId, companyId, itemDiscountId);
		String [] exclude = {"item", "itemDiscountType", "company"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(itemDiscounts, jConfig);
		return jsonArray.toString();
	}
}
