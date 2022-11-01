package eulap.eb.web.report;

import java.util.Date;

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
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.report.SalesOrderRegisterService;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Sales Order Register Controller.

 *
 */
@Controller
@RequestMapping("/salesOrderRegister")
public class SalesOrderRegisterController {
	private static Logger logger = Logger.getLogger(SalesOrderRegisterController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private SalesOrderRegisterService soRegisterService;
	@Autowired
	private DivisionService divisionService;
	private final static String REPORT_TITLE = "SALES ORDER REGISTER";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showSOListing(Model model, HttpSession session) {
		logger.info("Loading the search page of the SO Register Report.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("deliveryStatuses", soRegisterService.getDeliverytStatuses());
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		logger.info("Successfully loaded the search page.");
		return "SalesOrderRegister.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="arCustomerId", required=false) Integer arCustomerId,
			@RequestParam (value="arCustomerAccountId", required=true) Integer arCustomerAccountId,
			@RequestParam (value="soType", required=false) Integer soType,
			@RequestParam (value="soFrom", required=false) Integer soFrom,
			@RequestParam (value="soTo", required=false) Integer soTo,
			@RequestParam (value="popcrNo", required=false) String popcrNo,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="statusId", required=false) Integer statusId,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) {
		logger.info("Generating the SO Register report.");
		logger.debug("Generating the datasource.");
		JRDataSource dataSource = soRegisterService.generateSORegister(companyId, divisionId,arCustomerId,
				arCustomerAccountId, soType, soFrom, soTo, popcrNo, dateFrom, dateTo, statusId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		String strDate = DateUtil.setUpDate(DateUtil.formatDate(dateFrom), DateUtil.formatDate(dateTo));
		model.addAttribute("dateFromAndTo", strDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		logger.info("Successfully generated the SO Register report in "+formatType+" format.");
		return "SoRegisterReport.jasper";
	}
}
