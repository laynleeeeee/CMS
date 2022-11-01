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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemService;
import eulap.eb.service.SupplierAccountService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.report.ItemUnitCostHistoryPerSupplierService;

/**
 * Controller for item unit cost history per supplier report

 *
 */
@Controller
@RequestMapping("/itemUnitCostHistoryPerSupplier")
public class ItemUnitCostHistoryPerSupplierJR {
	private static Logger logger =  Logger.getLogger(ItemUnitCostHistoryPerSupplierJR.class);
	private final static String REPORT_TITLE = "ITEM UNIT COST HISTORY PER SUPPLIER";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private SupplierAccountService supplierAccountService;
	@Autowired
	private ItemUnitCostHistoryPerSupplierService service;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}
	
	@RequestMapping (method = RequestMethod.GET)
	public String showItemUnitCostHistoryPerSupplierPage (Model model, HttpSession session){
		logger.info("Loading the main page of Item unit cost history per supplier.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		// Get Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		
		logger.info("Successfully loaded the Item unit cost history per supplier.");
		return "ItemUnitCostHistoryPerSupplier.jsp";
	}
	
	@RequestMapping (value="/generate", method = RequestMethod.GET)
	public String generateReport(
			@RequestParam (value="companyId") int companyId,
			@RequestParam (value="divisionId") int divisionId,
			@RequestParam (value="itemId") int itemId,
			@RequestParam (value="supplierId") int supplierId,
			@RequestParam (value="supplierAccountId") int supplierAccountId,
			@RequestParam (value="dateFrom") Date dateFrom,
			@RequestParam (value="dateTo") Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required = false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating " + formatType + " version of Item Unit Cost History Per Supplier.");
		
		JRDataSource dataSource = 
				service.generateItemUnitCostHistoryPerSupplier(companyId, divisionId, itemId, supplierId, supplierAccountId, dateFrom, dateTo);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		
		model.addAttribute("reportTitle", REPORT_TITLE);
		
		// Set company attributes.
		Company company = companyService.getCompany(companyId);
		if (company != null) {
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
		}
		
		// Set the item attribute.
			Item item = itemService.getItem(itemId);
			model.addAttribute("item", item.getStockCode());
			
		// Set the supplier attribute.
		if (supplierId != -1) {
			Supplier supplier = supplierService.getSupplier(supplierId);
			model.addAttribute("supplier", supplier.getName());
		} else {
			model.addAttribute("supplier", "ALL");
		}
		
		// Set the supplier account attribute.
		if (supplierAccountId != -1) {
			SupplierAccount supplierAccount = supplierAccountService.getSupplierAccount(supplierAccountId);
			model.addAttribute("supplierAccount", supplierAccount.getName());
		} else {
			model.addAttribute("supplierAccount", "ALL");
		}
		
		// Set date range attributes.
		if (dateFrom != null && dateTo != null) {
			model.addAttribute("dateRange", DateUtil.formatDate(dateFrom) + " - " + DateUtil.formatDate(dateTo));
		}

		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("preparedBy", user.getFirstName() + " " + user.getLastName());
		ReportUtil.getPrintDetails(model, user, isFirstNameFirst != null ? isFirstNameFirst : false);
		logger.info("Sucessfully loaded the Item Unit Cost History Per Supplier report.");
		return "ItemUnitCostHistoryPerSupplier.jasper";
	}

}
