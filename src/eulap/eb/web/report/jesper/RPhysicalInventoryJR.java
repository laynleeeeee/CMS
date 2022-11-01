package eulap.eb.web.report.jesper;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.InventoryListingService;
import eulap.eb.service.PhysicalInventoryService;
import eulap.eb.service.WarehouseService;
import net.sf.jasperreports.engine.JRDataSource;
/**
 * Class that will handle the generation of Physical Inventory Worksheet Report.

 *
 */
@Controller
@RequestMapping("/rPhysicalInventoryWorksheetPDF")
public class RPhysicalInventoryJR {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PhysicalInventoryService piService;
	@Autowired
	private InventoryListingService invListingService;
	@Autowired
	private WarehouseService warehouseService;
	private static Logger logger = Logger.getLogger(RPhysicalInventoryJR.class);

	@RequestMapping(value="/showParams", method = RequestMethod.GET)
	public String showPI(Model model, HttpSession session){
		logger.info("Showing the search page for Physical Inventory Worksheet Report.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		//Load Item Categories
		Collection<ItemCategory> categories = piService.getActiveCategories();
		model.addAttribute("categories", categories);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		model.addAttribute("status", SearchStatus.getSearchStatus());
		return "RPhysicalInventoryWorksheet.jsp";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String generateReport(@RequestParam(value="itemCategoryId") Integer itemCategoryId,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false)Integer divisionId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam(value="stockOptionId", required = true)Integer stockOptionId, 
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="workflowStatusId", required = true)Integer workflowStatusId,
			@RequestParam(value="asOfDate", required=true) String asOfDate,
			@RequestParam (value="orderBy", required=false) String orderBy,
			@RequestParam (value="formatType", required=false) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			@RequestParam(value="isIncludeQuantity", required=false)Boolean isIncludeQuantity,
			Model model, HttpSession session) {
		logger.info("Generating the Physical Inventory Worksheet Report for company: "+companyId
				+" with warehouse: "+warehouseId+" as of "+asOfDate);
		JRDataSource dataSource = invListingService.generateInventoryListing(divisionId,itemCategoryId,
				stockOptionId, companyId, warehouseId, status, workflowStatusId, orderBy, asOfDate);
		return generateReport(model, session, dataSource, formatType, companyId, divisionId,
				asOfDate, itemCategoryId, warehouseId, isFirstNameFirst,isIncludeQuantity);
	}

	private String generateReport(Model model, HttpSession session, JRDataSource dataSource, String formatType,
			Integer companyId,Integer divisionId, String asOfDate, Integer itemCategoryId, Integer warehouseId, Boolean isFirstNameFirst,Boolean isIncludeQuantity) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		//Parameters
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("user", user.getFirstName() + " " + user.getLastName());
		model.addAttribute("asOfDate", "As of "+asOfDate);
		model.addAttribute("isIncludeQuantity", isIncludeQuantity);
		String category = "ALL";
		if(itemCategoryId != null)
			category = piService.getCategory(itemCategoryId).getName();
		model.addAttribute("category", category);
		String warehouse = "ALL";
		if(warehouseId != -1)
			warehouse = warehouseService.getWarehouse(warehouseId).getName();
		model.addAttribute("warehouse", warehouse);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		logger.info("Successfully generated the report.");
		return isIncludeQuantity ? "RPhysicalInventoryWorksheet.jasper":"RPhysicalInventoryWorksheetNoQuantity.jasper";
	}
}