package eulap.eb.web.report.jesper;

import java.util.Date;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemCategoryService;
import eulap.eb.service.ItemService;
import eulap.eb.service.StockcardPerItemService;
import eulap.eb.service.WarehouseService;

/**
 * Generates the Stockcard per item report.

 *
 */
@Controller
@RequestMapping("rStockcardPDF")
public class RStockcardPerItemJR {
	private static Logger logger = Logger.getLogger(RStockcardPerItemJR.class);
	@Autowired
	private ItemService itemService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private StockcardPerItemService stockcardService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private DivisionService divisionService;;

	@RequestMapping(method=RequestMethod.GET)
	public String generateReport(@RequestParam(value="itemId") int itemId,
			@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId", required=true) Integer divisionId,
			@RequestParam(value="warehouseId") int warehouseId,
			@RequestParam(value="dateFrom") String strDateFrom,
			@RequestParam(value="dateTo") String strDateTo,
			@RequestParam(value="companyName") String companyName,
			@RequestParam(value="typeId") int typeId,
			@RequestParam(value="formatType") String formatType,
			HttpSession session, Model model) {
		JRDataSource dataSource = stockcardService.generateStockcardDatasource(itemId, companyId, divisionId,
				warehouseId, strDateFrom, strDateTo, companyName, typeId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);

		logger.debug("Successfully generated the data source for the report.");

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());

		Item item = itemService.getItem(itemId);
		model.addAttribute("stockCode", item.getStockCode());
		model.addAttribute("description", item.getDescription());
		model.addAttribute("uom", item.getUnitMeasurement().getName());

		Warehouse warehouse = warehouseService.getWarehouse(warehouseId);
		model.addAttribute("warehouse", warehouse.getName());
		logger.debug("Successfully generated the parameters.");
		logger.debug("Showing the Stockcard per item report.");
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		if(typeId == 1) {
			logger.info("Generating the Stockcard Per Item Report for item: "+itemId);
			return "RStockcardPerItem.jasper";
		}
		logger.info("Generating the Bincard Per Item Report for item: "+itemId);
		return "RBincardPerItem.jasper";
	}

	@RequestMapping(value="{typeId}/showParams", method=RequestMethod.GET)
	public String showParams(@PathVariable int typeId, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("categories", itemCategoryService.getActiveItemCategories());
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		model.addAttribute("typeId", typeId);
		return "RStockcardPerItem.jsp";
	}
}
