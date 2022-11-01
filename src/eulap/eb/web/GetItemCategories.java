package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ItemCategoryService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller that gets the {@link ItemCategory}

 *
 */
@Controller
@RequestMapping("/getItemCategories")
public class GetItemCategories {
	@Autowired
	private ItemCategoryService itemCategoryService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getItemCategories(@RequestParam (value="term", required=false) String name,
			@RequestParam (value="companyId") int companyId,
			@RequestParam (value="divisionId", required = false) Integer divisionId){
		List<ItemCategory> itemCategories = itemCategoryService.getItemCategoriesByCompany(name, companyId, divisionId);
		JSONArray jsonArray = JSONArray.fromObject(itemCategories, excludeAcctSetups());
		return jsonArray.toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/perCategory")
	public @ResponseBody String getItemCategory(@RequestParam (value="term", required=false) String term,
			@RequestParam (value="companyId") int companyId,
			@RequestParam (value="divisionId", required = false) Integer divisionId) {
		ItemCategory itemCategory = itemCategoryService.getItemCategoryByName(term, companyId, divisionId);
		JSONObject jsonObj = JSONObject.fromObject(itemCategory, excludeAcctSetups());
		return jsonObj.toString();
	}

	private JsonConfig excludeAcctSetups() {
		JsonConfig jConfig = new JsonConfig();
		String [] exclude = {"accountSetups"};
		jConfig.setExcludes(exclude);
		return jConfig;
	}

	@RequestMapping (value="/byName", method = RequestMethod.GET)
	public @ResponseBody String getItemCategoriesByName(@RequestParam (value="categoryName", required=false) String categoryName,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<ItemCategory> itemCategories = itemCategoryService.getByName(categoryName, user);
		JSONArray jsonArray = JSONArray.fromObject(itemCategories, excludeAcctSetups());
		return jsonArray.toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/exactName")
	public @ResponseBody String getItemCategory(@RequestParam (value="categoryName", required=false) String categoryName) {
		ItemCategory itemCategory = itemCategoryService.getItemCategoryByExactName(categoryName);
		JSONObject jsonObj = JSONObject.fromObject(itemCategory, excludeAcctSetups());
		return jsonObj.toString();
	}
}
