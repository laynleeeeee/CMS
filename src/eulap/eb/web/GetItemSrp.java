package eulap.eb.web;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.service.ItemSrpService;

/**
 * Controller that gets the item discounts.

 *
 */
@Controller
@RequestMapping("/getItemSrp")
public class GetItemSrp {
	@Autowired
	private ItemSrpService itemSrpService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getItemSrp (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="itemId", required=true) Integer itemId,
			@RequestParam (value="divisionId", required=false) Integer divisionId) {
		if (itemSrpService.hasItemSrp(itemId)) {
			ItemSrp itemSrp = itemSrpService.getLatestItemSrp(companyId, itemId, divisionId);
			if (itemSrp == null) {
				return "No item SRP setup for the selected company/division.";
			}
			itemSrp.setSellingPrice(itemSrp.getSrp());
			String [] exclude = {"item"};
			JsonConfig jConfig = new JsonConfig();
			jConfig.setExcludes(exclude);
			JSONObject jsonObject = JSONObject.fromObject(itemSrp, jConfig);
			return jsonObject.toString();
		}
		return "No item SRP setup.";
	}

	@RequestMapping(value="/withRate", method = RequestMethod.GET)
	public @ResponseBody String getItemSrpWithRate (@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="itemId", required=true) Integer itemId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="currencyId", required=false) Integer currencyId) {
		if (itemSrpService.hasItemSrp(itemId)) {
			ItemSrp itemSrp = itemSrpService.getLatestItemSrpWithRate(companyId, itemId, divisionId, currencyId);
			if (itemSrp == null) {
				return "No item SRP setup for the selected company/division.";
			}
			itemSrp.setSellingPrice(itemSrp.getSrp());
			String [] exclude = {"item"};
			JsonConfig jConfig = new JsonConfig();
			jConfig.setExcludes(exclude);
			JSONObject jsonObject = JSONObject.fromObject(itemSrp, jConfig);
			return jsonObject.toString();
		}
		return "No item SRP setup.";
	}
}
