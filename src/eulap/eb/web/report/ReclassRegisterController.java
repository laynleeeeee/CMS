package eulap.eb.web.report;

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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ReclassRegisterService;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.fap.FormStatusService;

/**
 * Class that will handle the generation of Reclass Register.

 *
 */
@Controller
@RequestMapping("/reclassRegister")
public class ReclassRegisterController {
	private static Logger logger = Logger.getLogger(ReclassRegisterController.class);
	@Autowired
	private ReclassRegisterService reclassRegisterService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private FormStatusService formStatusService;

	@RequestMapping(value="/generatePDF", method = RequestMethod.GET)
	public String showPI(Model model, HttpSession session){
		logger.info("Showing the search page for Reclass Register.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getDivisions(null, null, true, null));
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		model.addAttribute("statuses", formStatusService.getFormStatuses(user, "Repacking11"));
		return "ReclassRegister.jsp";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String generateReport(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="dateFrom", required=true) Date dateFrom,
			@RequestParam (value="dateTo", required=true) Date dateTo,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="statusId", required=false) Integer statusId,
			Model model, HttpSession session) {
		logger.info("Generating the report.");
		JRDataSource dataSource = reclassRegisterService.generateReclassRegisterDataSource(companyId,
				divisionId, warehouseId, dateFrom, dateTo, statusId);
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("format", formatType);
		// Parameters
		logger.info("Processing the parameters needed for the report.");
		model.addAttribute("warehouseName", "All");
		if(warehouseId != -1) {
			Warehouse warehouse = warehouseService.getWarehouse(warehouseId);
			model.addAttribute("warehouseName", warehouse.getName());
		}
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		String strDate = DateUtil.setUpDate(DateUtil.formatDate(dateFrom), DateUtil.formatDate(dateTo));
		model.addAttribute("dateFromAndTo", strDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, false);
		logger.info("Successfully generated the report in "+formatType+" format.");
		return "ReclassRegister.jasper";
	}
}
