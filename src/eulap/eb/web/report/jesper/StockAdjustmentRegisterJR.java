package eulap.eb.web.report.jesper;


import java.util.Date;
import java.util.List;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.StockAdjustmentService;
import eulap.eb.service.StockAdjustmentTypeService;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.web.dto.StockAdjustmentDto;

/**
 * Entry point for generating the Stock Adjustment Register report.

 *
 */
@Controller
@RequestMapping("/stockAdjRegisterPDF")
public class StockAdjustmentRegisterJR {
	private static Logger logger = Logger.getLogger(StockAdjustmentRegisterJR.class);
	private static String ALL = "ALL";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private StockAdjustmentTypeService saTypeService;
	@Autowired
	private StockAdjustmentService sAdjustmentService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private FormStatusService formStatusService;

	@RequestMapping(value="showParams", method=RequestMethod.GET)
	public String showParams(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		// Get the workflow statuses from stock in since both in and out have the same workflow
		model.addAttribute("workflowStatuses", formStatusService.getFormStatuses(user, "StockAdjustment5"));
		logger.info("Showing the search page for Stock Adjustment Register Report.");
		return "StockAdjustmentRegister.jsp";
	}

	@RequestMapping(method=RequestMethod.GET)
	public String generateReport(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="warehouseId") int warehouseId,
			@RequestParam(value="divisionId") int divisionId,
			@RequestParam(value="adjustmentTypeId") int adjustmentTypeId,
			@RequestParam(value="dateFrom") String strDateFrom,
			@RequestParam(value="dateTo") String strDateTo,
			@RequestParam(value="formatType") String formatType,
			@RequestParam(value="formStatusId") int formStatusId,
			HttpSession session, Model model) {
		logger.info("Processing the Stock Adjustment Register Report.");

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		logger.debug("Searching transactions for Company: "+company.getName());

		String warehouseName = ALL;
		if(warehouseId != -1) {
			warehouseName = warehouseService.getWarehouse(warehouseId).getName();
		}
		logger.debug("Searching transactions for warehouse: "+warehouseName);
		model.addAttribute("warehouse", warehouseName);

		String adjustmentTypeName = ALL;
		if(adjustmentTypeId != -1) {
			adjustmentTypeName = saTypeService.getSAdjustmentType(adjustmentTypeId).getName();
		}

		String divisionName = ALL;
		if(divisionId != -1) {
			Division division = divisionService.getDivision(divisionId);
			divisionName = division.getName();
		}
		logger.debug("Searching transactions for divisions: "+divisionName);
		model.addAttribute("division", divisionName);

		logger.debug("Searching transactions for stock adjustment type: "+adjustmentTypeId);
		model.addAttribute("adjustmentType", adjustmentTypeName);
		logger.debug("Successfully generated the parameters for the report.");
		List<StockAdjustmentDto> dataSource = sAdjustmentService.getStockAdjustmentRegisterData(companyId,
				warehouseId, divisionId, adjustmentTypeId, strDateFrom, strDateTo, formStatusId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, false);
		logger.debug("Successfully generated the data source for the report.");
		logger.info("Showing the Stock Adjustment Register Report.");
		return "StockAdjustmentRegister.jasper";
	}
}
