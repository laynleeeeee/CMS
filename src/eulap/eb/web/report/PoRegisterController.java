package eulap.eb.web.report;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import eulap.eb.service.PoRegisterService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * A controller class the handles the purchase order register report.

 */

@Controller
@RequestMapping("poRegister")
public class PoRegisterController {
	private static Logger logger = Logger.getLogger(PoRegisterController.class);
	private final static String REPORT_TITLE = "PURCHASE ORDER REGISTER";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PoRegisterService poRegisterService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showReceiptRegister(Model model, HttpSession session) throws ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("formStatuses", poRegisterService.getFormStatuses(user));
		model.addAttribute("terms", poRegisterService.getTerms(user));
		return "PoRegisterReport.jsp";
	}

	@RequestMapping (value="/generateReport", method=RequestMethod.GET)
	public String generateReport (@RequestParam (value="companyId", required=false) int companyId,
			@RequestParam (value="divisionId", required=true) Integer divisionId,
			@RequestParam (value="supplierId", required=false) Integer supplierId,
			@RequestParam (value="supplierAccountId", required=false) Integer supplierAccountId,
			@RequestParam (value="termId", required=false) Integer termId,
			@RequestParam (value="poDateFrom", required=false) Date poDateFrom,
			@RequestParam (value="poDateTo", required=false) Date poDateTo,
			@RequestParam (value="rrDateFrom", required=false) Date rrDateFrom,
			@RequestParam (value="rrDateTo", required=false) Date rrDateTo,
			@RequestParam (value="statusId", required=false) Integer statusId,
			@RequestParam (value="deliveryStatus", required=false) String deliveryStatus,
			@RequestParam (value="formatType", required=false) String formatType,
			Model model, HttpSession session) {
		logger.info("Generating the Purchase Order Register Report.");
		JRDataSource dataSource = new JRBeanCollectionDataSource(poRegisterService.getPoRegisterData(companyId,
				divisionId, supplierId, supplierAccountId, termId, poDateFrom, poDateTo, rrDateFrom, rrDateTo,
				statusId, deliveryStatus));
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		String strDate = "";
		if (poDateFrom != null && poDateFrom != null) {
			String startDate = DateUtil.formatDate(poDateFrom);
			String endDate = DateUtil.formatDate(poDateTo);
			strDate = DateUtil.setUpDate(startDate, endDate);
		}
		model.addAttribute("dateFromAndTo", strDate);
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		return "PoRegisterReportCancellationRemarks.jasper";
	}
}
