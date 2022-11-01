package eulap.eb.web.report;

import java.time.Year;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
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
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.report.MonthlyAlphaPayeesService;
import eulap.eb.web.dto.MonthlyAlphalistPayeesDto;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.jasperreports.engine.JRException;
/**
 * A controller class that handles the monthly alphalist of payees.

 */

@Controller
@RequestMapping("/monthlyalphalistpayees")
public class MonthlyAlphalistofPayeesCtrl {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private MonthlyAlphaPayeesService monthlyalphalistpayees;

	private static Logger logger =  Logger.getLogger(MonthlyAlphalistofPayeesCtrl.class);

	@RequestMapping (method = RequestMethod.GET)
	public String showItemSalesCustomerPage (Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Loading the main page of monthly alphalist of payees.");
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("months",TimePeriodMonth.getMonths());
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		model.addAttribute("defaultMonth", DateUtil.getMonth(new Date()));
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		logger.info("Successfully loaded the main page of Monthly Alphalist of Payees.");
		return "MonthlyAlphalistofPayees.jsp";
	}

	@RequestMapping (value = "/generate", method = RequestMethod.GET)
	public String generateReport(
			@RequestParam (value="companyId") int companyId,
			@RequestParam (value="divisionId") int divisionId,
			@RequestParam (value="year") Integer year,
			@RequestParam (value="currentMonthId") Integer month,
			@RequestParam (value="formatType", required = true) String formatType,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model, HttpSession session) throws JRException, ConfigurationException {
			List<MonthlyAlphalistPayeesDto> annualAWTEDto = monthlyalphalistpayees.getMAPayees(companyId, divisionId, month, month, year);
			User user = CurrentSessionHandler.getLoggedInUser(session);
			Company company = companyService.getCompany(companyId);
			String returnMonth = month < 10 ? "0" + month.toString() : month.toString();
			Integer returnedYear = (year % 100);
			model.addAttribute("datasource", annualAWTEDto);
			model.addAttribute("format", formatType );
			model.addAttribute("birForm", "BIR FORM 1601E - SCHEDULE I");
			model.addAttribute("reportTitle", "MONTHLY ALPHALIST OF PAYEES (MAP)");
			model.addAttribute("rptMonth", DateUtil.getMonthName(month-1));
			model.addAttribute("rptYear", year);
			model.addAttribute("returnPeriod", returnMonth +"/"+ returnedYear.toString());
			model.addAttribute("companyTin", StringFormatUtil.processBirTinTo13Digits(company.getTin()));
			model.addAttribute("companyName", (company.getName().toUpperCase()));
			model.addAttribute("headName", "NOEL S. BARCELONA");
			ReportUtil.getPrintDetails(model, user, isFirstNameFirst != null ? isFirstNameFirst : false);
			return "MonthlyAlphalistPayees.jasper";
	}
}
