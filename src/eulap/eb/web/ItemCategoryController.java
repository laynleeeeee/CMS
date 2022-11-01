package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ItemCategoryService;
import eulap.eb.validator.ItemCategoryValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller for YBL Item Category 

 */
@Controller
@RequestMapping ("/admin/itemCategories")
public class ItemCategoryController {
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private ItemCategoryValidator itemCategoryValidator;
	private Logger logger = Logger.getLogger(ItemCategoryController.class);

	@RequestMapping (method = RequestMethod.GET)
	public String showItemCategory(HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Page<ItemCategory> itemCategories =
				itemCategoryService.searchCategories("", SearchStatus.All.name(), 1, user);
		model.addAttribute("itemCategories",itemCategories);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		logger.info("Show item categories");
		return "showItemCategories";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form")
	public String addItemCategoryForm(Model model) {
		ItemCategory itemCategory = new ItemCategory();
		itemCategory.setActive(true);
		return showItemCategoryForm(itemCategory, model);
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form", params = {"itemCategoryId"})
	public String editCategoryForm(@RequestParam int itemCategoryId, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ItemCategory itemCategory = itemCategoryService.itemCategory(itemCategoryId, user);
		return showItemCategoryForm(itemCategory, model);
	}

	private String showItemCategoryForm(ItemCategory itemCategory, Model model){
		itemCategory.serializeAccountSetups();
		model.addAttribute(itemCategory);
		return "showItemCategoryForm";
	}

	@RequestMapping (method = RequestMethod.POST, value = "/form")
	public String saveItemCategory (@ModelAttribute ("itemCategory") ItemCategory itemCategory, 
			BindingResult result, Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		itemCategory.deSerializeAccountSetups();
		itemCategory.setAccountSetups(itemCategoryService.processItemCategoryAcctSetup(itemCategory));
		itemCategoryValidator.validate(itemCategory, result);
		if (result.hasErrors())
			return showItemCategoryForm(itemCategory, model);
		itemCategoryService.saveItemCategory(user, itemCategory);
		logger.info("Successfully saved the item category: "+itemCategory.getName());
		return "successfullySaved";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchCategories(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") int pageNumber, HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Page<ItemCategory> categories = itemCategoryService.searchCategories(name, status, pageNumber, user);
		model.addAttribute("itemCategories", categories);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		logger.info("Search categories at page: "+pageNumber);
		return "showItemCategoryTable";
	}

	@RequestMapping (method = RequestMethod.GET, value="/getAcctCombis")
	public @ResponseBody String getAccCombinations(
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId){
		ItemCategoryAccountSetup itemCatAcctSetup = itemCategoryService.processAcctCombinations(companyId, divisionId);
		JSONObject jsonObject = JSONObject.fromObject(itemCatAcctSetup);
		return jsonObject.toString();
	}
}
