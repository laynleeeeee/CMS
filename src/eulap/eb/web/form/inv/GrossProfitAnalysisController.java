package eulap.eb.web.form.inv;

import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

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
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemCategoryService;
import eulap.eb.service.ItemService;
import eulap.eb.web.dto.GrossProfitAnalysis;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller for gross profit analysis.

 *
 */
@Controller
@RequestMapping("/grossProfitAnalysis")
public class GrossProfitAnalysisController {
	private static Logger logger = Logger.getLogger(GrossProfitAnalysisController.class);
	private final static String REPORT_TITLE = "GROSS PROFIT ANALYSIS";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private DivisionService divisionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String generateReport (Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user,model);
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		return "GrossProfitAnalysis.jsp";
	}

	private void loadSelections(User user, Model model) {
		// Get Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		// Get Active Divisions
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		// Get Item Categories
		List<ItemCategory> itemCategories = itemCategoryService.getActiveItemCategories();
		model.addAttribute("itemCategories", itemCategories);
	}

	@RequestMapping (value="/generate", method = RequestMethod.GET)
	public String generatePDF(
			@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="itemCategoryId", required=true) Integer itemCategoryId,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating " + formatType + " version of daily sales.");
		itemCategoryId = itemCategoryId != null ? itemCategoryId : -1;
		List<GrossProfitAnalysis> dailyCashCollection =
				itemService.getGrossProfitAnalysis(companyId, divisionId, itemCategoryId, dateFrom, dateTo);
		JRDataSource dataSource = new JRBeanCollectionDataSource(dailyCashCollection, false);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		Company company = null;
		if (companyId != null && dateFrom != null && dateTo != null) {
			company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , REPORT_TITLE);
			model.addAttribute("dateRange", DateUtil.formatDate(dateFrom) + " - " +
					DateUtil.formatDate(dateTo));
			if (itemCategoryId != null) {
				ItemCategory ic = itemCategoryService.itemCategory(itemCategoryId);
				if (ic != null) {
					model.addAttribute("itemCategory", ic.getName());
				} else {
					model.addAttribute("itemCategory", "ALL");
				}
			} else {
				model.addAttribute("itemCategory", "ALL");
			}
		} else {
			logger.error("Company , date from and to are required");
			throw new RuntimeException("Company , date from and to are required");
		}
		logger.info("Sucessfully loaded the gross profit analysis");
		return "GrossProfitAnalysis.jasper";
	}
}
