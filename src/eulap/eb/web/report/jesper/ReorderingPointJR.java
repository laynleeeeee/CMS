package eulap.eb.web.report.jesper;

import java.util.Date;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;

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
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemCategoryService;
import eulap.eb.service.ReorderPointService;

/**
 * Class that will handle the generation of Reordering Point Report.


 *
 */
@Controller
@RequestMapping("/reorderingPointPDF")
public class ReorderingPointJR {
	private static Logger logger = Logger.getLogger(ReorderingPointJR.class);
	@Autowired
	private ReorderPointService reorderPointService;
	@Autowired
	private ItemCategoryService categoryService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CompanyService companyService;

	@RequestMapping(value="/showParams", method = RequestMethod.GET)
	public String showPI(Model model, HttpSession session){
		logger.info("Showing the search page for Reordering Point Report.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getDivisions(null, null, true, null));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		model.addAttribute("status", SearchStatus.getSearchStatus());
		return "ReorderPointReport.jsp";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String generateReport(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="categoryId", required=false) Integer categoryId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="asOfDate", required=true) String asOfDate,
			@RequestParam (value="formatType", required=false) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) {
		logger.info("Generating the report.");
		JRDataSource dataSource = reorderPointService.
				generateReorderingPtDataSource(companyId, divisionId, warehouseId, categoryId, status, asOfDate);
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("format", formatType);
		//Parameters
		logger.info("Processing the parameters needed for the report.");
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		String category = "ALL";
		if(categoryId != null) {
			ItemCategory ic = categoryService.getItemCategory(categoryId);
			if(ic != null) {
				category = ic.getName();
			}
		}
		model.addAttribute("category", category);
		model.addAttribute("asOfDate", "As of "+asOfDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		logger.info("Successfully generated the report.");
		return "ReorderPointReport.jasper";
	}
}
