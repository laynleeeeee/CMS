package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.ItemDiscountType;
import eulap.eb.service.ItemDiscountTypeService;

/**
 * Controller that gets the item discount types.

 *
 */
@Controller
@RequestMapping("/getItemDiscountType")
public class GetItemDiscountType {
	@Autowired
	private ItemDiscountTypeService itemDiscountTypeService;

	@RequestMapping(value = "/all",method = RequestMethod.GET)
	public @ResponseBody String getItemDiscountTypes(
			@RequestParam (value="name", required=false) String name) {
		List<ItemDiscountType> itemDiscountTypes = itemDiscountTypeService.getItemDiscountTypes(name);
		JSONArray jsonArray = JSONArray.fromObject(itemDiscountTypes);
		return jsonArray.toString();
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getDiscountType (@RequestParam (value="name") String name,
			HttpSession session) {
		ItemDiscountType itemDiscountType = itemDiscountTypeService.getItemDiscountType(name);
		JSONObject jsonObject = JSONObject.fromObject(itemDiscountType);
		return itemDiscountType == null ? "No item discount type found" : jsonObject.toString();
	}
}
