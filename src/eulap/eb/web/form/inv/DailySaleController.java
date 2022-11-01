package eulap.eb.web.form.inv;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.CompanyService;
import eulap.eb.web.dto.DailySaleDto;


/**
 * Daily sales report controller

 */
@Controller
@RequestMapping("/dailySales")
public class DailySaleController {
	private static Logger logger = Logger.getLogger(DailySaleController.class);
	private final static String REPORT_TITLE = "DAILY CASH SALES";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CashSaleService cashSaleService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String showDailySaleMainPage (Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		logger.info("Loading daily cash sales summary main page.");
		return "DailySale.jsp";
	}
	
	@RequestMapping (value="/generate", method = RequestMethod.GET)
	public String generatePDF(@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="salesInvoiceNo", required=true) String salesInvoiceNo,
			@RequestParam (value="customerName", required=true) String customerName,
			@RequestParam (value="date", required=true) Date date,
			@RequestParam (value="formatType", required = true) String formatType, 
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating " + formatType + " version of daily sales.");
		List<DailySaleDto> dailySales = cashSaleService.generateDailySales(companyId, salesInvoiceNo, customerName, date);
		JRDataSource dataSource = new JRBeanCollectionDataSource(dailySales, false);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		Company company = null;
		String strDate = "";
		if (companyId != null && date != null) {
			company = companyService.getCompany(companyId);
			model.addAttribute("companyLogo", company.getLogo());
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , REPORT_TITLE);
			model.addAttribute("salesInvoiceNo", salesInvoiceNo);
			model.addAttribute("customerName", customerName);
			strDate = DateUtil.formatDate(date);
			model.addAttribute("date", strDate);
		} else {
			logger.error("Company and  date are required");
			throw new RuntimeException("Company and date are required");
		}
		logger.info("Sucessfully loaded the daily sales");
		return "DailySale.jasper";
	}
	
}
