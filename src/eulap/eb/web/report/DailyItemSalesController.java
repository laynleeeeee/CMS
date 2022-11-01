package eulap.eb.web.report;

import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemCategoryService;
import eulap.eb.service.ItemService;
import eulap.eb.service.WarehouseService;
import eulap.eb.web.dto.DailyItemSale;

/**
 * Daily item sales report controller.

 *
 */
@Controller
@RequestMapping("/dailyItemSales")
public class DailyItemSalesController {
	private static Logger logger = Logger.getLogger(DailyItemSalesController.class);
	private final static String REPORT_TITLE = "ITEM SOLD";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemCategoryService itemCatService;
	@Autowired
	private WarehouseService warehouseService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}
	
	@RequestMapping (method = RequestMethod.GET)
	public String showDailyItemSalesPage (Model model, HttpSession session){
		logger.info("Loading the main page of Daily Item Sales.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		// Get Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		// Set the current date
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		
		logger.info("Successfully loaded the main page of Daily Item Sales.");
		return "DailyItemSales.jsp";
	}
	
	@RequestMapping (value="/generate", method = RequestMethod.GET)
	public String generatePDF(
			@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="stockCode", required=false) String stockCode,
			@RequestParam (value="invoiceNo", required=false) String invoiceNo,
//			@RequestParam (value="date", required=true) Date date,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="itemCategoryId", required = true) Integer itemCategoryId,
			@RequestParam (value="warehouseId", required = true) Integer warehouseId,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating " + formatType + " version of daily item sales.");
		List<DailyItemSale> dailyItemSales =
				itemService.getDailyItemSales(companyId, stockCode, invoiceNo, dateFrom, dateTo, itemCategoryId, warehouseId);
		JRDataSource dataSource = new JRBeanCollectionDataSource(dailyItemSales, false);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		Company company = null;
		if (companyId != null && dateFrom != null && dateTo != null) {
			company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			if (itemCategoryId != -1) {
				ItemCategory category = itemCatService.getItemCategory(itemCategoryId);
				model.addAttribute("itemCategory", category.getName());
			} else {
				model.addAttribute("itemCategory", "ALL");
			}
			if (stockCode != null && !stockCode.trim().isEmpty()) {
				model.addAttribute("stockCode", stockCode);
			} else {
				model.addAttribute("stockCode", "ALL");
			}
			if (invoiceNo != null && !invoiceNo.trim().isEmpty()) {
				model.addAttribute("invoiceNo", invoiceNo);
			} else {
				model.addAttribute("invoiceNo", "ALL");
			}
			if (warehouseId != -1) {
				model.addAttribute("warehouse",
						warehouseService.getWarehouse(warehouseId).getName());
			} else {
				model.addAttribute("warehouse", "ALL");
			}
			model.addAttribute("reportTitle" , REPORT_TITLE);
			model.addAttribute("dateFrom", DateUtil.formatDate(dateFrom));
			model.addAttribute("dateTo", DateUtil.formatDate(dateTo));
		} else {
			logger.error("Company and  date are required");
			throw new RuntimeException("Company and  date are required");
		}
		logger.info("Sucessfully loaded the daily sales");
		return "DailyItemSales.jasper";
	}
	
}
