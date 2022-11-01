
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
import eulap.eb.service.PettyCashVoucherLiquidationService;
import eulap.eb.service.report.PCVLiquidationRegisterParam;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Controller class that will handle requests for petty cash voucher liquidation register report

 */

@Controller
@RequestMapping("/pcvlRegisterReport")
public class PCVLRegisterReportController {
	private final Logger logger = Logger.getLogger(PCVLRegisterReportController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private PettyCashVoucherLiquidationService pettycashvoucherliquidation;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showPettyCashVoucher (Model model,HttpSession session){
		logger.info("Loading petty cash voucher liquidation");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		model.addAttribute("formStatuses", pettycashvoucherliquidation.getFormStatuses(user));
		return "PCVLiquidationRegisterReport.jsp";
	}

	@RequestMapping(value="/generateReport", method = RequestMethod.GET)
	public String loadReport(
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam(value="custodianId") Integer custodianId,
			@RequestParam(value="requestorName") String requestorName,
			@RequestParam(value="dateFrom") Date dateFrom,
			@RequestParam(value="dateTo") Date dateTo,
			@RequestParam(value="formatType")String formatType,
			@RequestParam(value="transactionStatusId") Integer transactionStatusId,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model,HttpSession session) {
		Company company = companyService.getCompany(companyId);
		PCVLiquidationRegisterParam param = new PCVLiquidationRegisterParam();
		param.setCompanyId(companyId);
		param.setDivisionId(divisionId);
		param.setCustodianId(custodianId);
		param.setRequestorName(requestorName);
		param.setDateFrom(dateFrom);
		param.setDateTo(dateTo);
		param.setTransactionStatusId(transactionStatusId);
		String datefrom = DateUtil.formatDate(dateFrom);
		String dateto = DateUtil.formatDate(dateTo);
		logger.info("Generating Petty Cash Voucher Liquidation Register Report.");
		JRDataSource datasource = pettycashvoucherliquidation.generatePCVLiquidationRegister(param);
		model.addAttribute("datasource", datasource);
		model.addAttribute("format", formatType);
		model.addAttribute( "dateFromTO", datefrom + " to " + dateto);
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyLogo", formatType.equals("pdf")? company.getLogo(): "");
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser,isFirstNameFirst != null ? isFirstNameFirst : false );
		return "PCVLiquidationRegister.jasper";
	}
}