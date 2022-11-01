package eulap.eb.web.report.jesper;

import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemCategoryService;
import eulap.eb.service.ItemService;
import eulap.eb.service.report.ItemSalesCustomerService;

/**
 * Controller for Item sales by customer report.

 *
 */
@Controller
@RequestMapping("/itemSalesCustomer")
public class ItemSalesCustomerJR {
	private static Logger logger =  Logger.getLogger(ItemSalesCustomerJR.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemSalesCustomerService iscService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}
	
	@RequestMapping (method = RequestMethod.GET)
	public String showItemSalesCustomerPage (Model model, HttpSession session){
		logger.info("Loading the main page of Item Sales By Customer.");
		loadDefaultAttribute(model, session);
		logger.info("Successfully loaded the main page of Item Sales By Customer.");
		return "ItemSalesCustomer.jsp";
	}
	
	@RequestMapping (method = RequestMethod.GET, value= "/customerVolume")
	public String showCustomerVolume (Model model, HttpSession session){
		logger.info("Loading Customer Volume.");
		loadDefaultAttribute(model, session);
		logger.info("Successfully processed,");
		return "CustomerPurchasedVolume.jsp";
	}
	
	private void loadDefaultAttribute (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		// Get Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
	}
	
	@RequestMapping (method = RequestMethod.GET, value="/belongsToCategory")
	public @ResponseBody String belongsToItemCategory (
			@RequestParam (value="itemId") int itemId, 
			@RequestParam (value="itemCategoryId") int itemCategoryId) {
		return itemService.belongsToCategory(itemId, itemCategoryId) + "";
	}

	@RequestMapping (value="/generate", method = RequestMethod.GET)
	public String generateReport(@RequestParam (value="companyId") int companyId,
			@RequestParam (value="arCustomerId") int arCustomerId,
			@RequestParam (value="arCustomerAcctId", required=false) int arCustomerAcctId,
			@RequestParam (value="itemCategoryId", required=false) int itemCategoryId,
			@RequestParam (value="itemId", required=false) int itemId,
			@RequestParam (value="dateFrom") Date dateFrom,
			@RequestParam (value="dateTo") Date dateTo,
			@RequestParam (value="isWithAmount", defaultValue="true") boolean isWithAmount,
			@RequestParam (value="isExcludeReturns", defaultValue="false") boolean isExcludeReturns,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			@RequestParam (value="divisionId", required = true) Integer divisionId,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		// CurrentSessionHandler.validateUserCompanyAccessRight(user, companyId); // generate report without user company
		logger.info("Generating " + formatType + " version of Item Sales By Customer.");
		JRDataSource dataSource = iscService.generateItemSalesCustomer(companyId, divisionId, arCustomerId, arCustomerAcctId, itemCategoryId, itemId, dateFrom, dateTo, isExcludeReturns, isWithAmount);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		
		// Set company attributes.
		Company company = companyService.getCompany(companyId);
		if (company != null) {
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
		}
		// Set customer attributes.
		if (arCustomerId == -1) {
			model.addAttribute("customer", "ALL");
		} else {
			ArCustomer customer = arCustomerService.getCustomer(arCustomerId);
			if (customer != null) {
				model.addAttribute("customer", customer.getNumberAndName());
			}
		}
		
		// Set customer account attributes.
		if (arCustomerAcctId == -1) {
			model.addAttribute("customerAccount", "ALL");
		} else {
			ArCustomerAccount customerAccount = arCustomerAcctService.getAccount(arCustomerAcctId);
			if (customerAccount != null) {
				model.addAttribute("customerAccount", customerAccount.getName());
			}
		}
		
		// Set item category attributes.
		if (itemCategoryId == -1) {
			model.addAttribute("itemCategory", "ALL");
		} else {
			ItemCategory itemCategory = itemCategoryService.itemCategory(itemCategoryId);
			if (itemCategory != null) {
				model.addAttribute("itemCategory", itemCategory.getName());
			}
		}
		
		// Set item attributes.
		if (itemId == -1) {
			model.addAttribute("item", "ALL");
		} else {
			Item item = itemService.getItem(itemId);
			if (item != null) {
				model.addAttribute("item", item.getStockCode());
			}
		}
		
		// Set date range attributes.
		if (dateFrom != null && dateTo != null) {
			model.addAttribute("date", DateUtil.formatDate(dateFrom) + " - " + DateUtil.formatDate(dateTo));
		} 

		ReportUtil.getPrintDetails(model, user, isFirstNameFirst != null ? isFirstNameFirst : false);
		logger.info("Sucessfully loaded the item sales by customer report.");
		if (isWithAmount) {
			model.addAttribute("reportTitle", "ITEM SOLD TO CUSTOMER REPORT");
			return "ItemSalesCustomer.jasper";
		}

		model.addAttribute("reportTitle", "CUSTOMER'S VOLUME REPORT");
		return "CustomerPurchasedVolume.jasper";
	}
}
