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
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AvailableStocksRptService;
import eulap.eb.service.CompanyService;

/**
 * Controller class for Available Stocks Report.

 *
 */
@Controller
@RequestMapping("/availableStocksPDF")
public class AvailableStocksReportCtrlr {
	private static Logger logger = Logger.getLogger(AvailableStocksReportCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private AvailableStocksRptService availableStocksRptService;

	private static final int AVAILABLE_STOCK = 2;
	@RequestMapping(value="/showParams", method=RequestMethod.GET)
	public String showParams(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		logger.info("Showing the parameters to generate the Available Stocks Report.");
		return "AvailableStocksReport.jsp";
	}

	@RequestMapping(method=RequestMethod.GET)
	public String generateReport(
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="itemCategoryId", required=false) Integer itemCategoryId,
			@RequestParam(value="stockCode", required=true) String stockCode,
			@RequestParam(value="orderBy", required=true) String orderBy,
			@RequestParam(value="asOfDate", required=true) String asOfDate,
			@RequestParam (value="formatType", required=false) String formatType,
			@RequestParam (value="reportType", required=false) Integer reportType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) {
		logger.info("Generating the "+formatType+" version of Available Stocks Report.");
		JRDataSource dataSource = availableStocksRptService.
				generateAvailableStocksRpt(companyId, warehouseId, itemCategoryId, stockCode, orderBy, asOfDate);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		availableStocksRptService.generateParams(model, companyId, itemCategoryId, warehouseId, asOfDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, isFirstNameFirst != null ? isFirstNameFirst : false);
		logger.info("Sucessfully generated the Available Stocks Report.");
		if (reportType == null || reportType == AVAILABLE_STOCK)
			return "AvailableStocksReport.jasper";
		else 
			return "InventoryWorksheet.jasper";
	}
}
