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
import eulap.eb.service.PettyCashReplenishmentService;
import eulap.eb.service.report.PCVRRegisterParam;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Controller class that will request for petty cash replenishment report

 */

@Controller
@RequestMapping("/pcvrRegisterReport")
public class PCVRRegisterReportController {
	private final Logger logger = Logger.getLogger(PCVRRegisterReportController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private PettyCashReplenishmentService pcvReplenishmentService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showPettyCashVoucher (Model model,HttpSession session){
		logger.info("Loading petty cash voucher replenishment register report");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, 0));
		return "PCReplenishmentRegisterReport.jsp";
	}

	@RequestMapping(value="/generateReport", method = RequestMethod.GET)
	public String loadReport(@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam(value="custodianId", defaultValue="-1") Integer custodianId,
			@RequestParam(value="pcrNo") Integer pcrNo,
			@RequestParam(value="dateFrom") Date dateFrom,
			@RequestParam(value="dateTo") Date dateTo,
			@RequestParam(value="formatType")String formatType,
			@RequestParam(value="status") Integer status,
			@RequestParam (value="isFirstNameFirst", required=false) Boolean isFirstNameFirst,
			Model model,HttpSession session) {
		PCVRRegisterParam param = new PCVRRegisterParam();
		param.setCompanyId(companyId);
		param.setDivisionId(divisionId);
		param.setCustodianId(custodianId);
		param.setPcrNo(pcrNo);
		param.setDateFrom(dateFrom);
		param.setDateTo(dateTo);
		param.setStatus(status);
		logger.info("Generating Petty Cash Voucher Replenishment Report.");
		JRDataSource datasource = pcvReplenishmentService.generatePCReplenishmentRegister(param);
		model.addAttribute("datasource", datasource);
		model.addAttribute("format", formatType);
		model.addAttribute("reportTitle", "PETTY CASH REPLENISHMENT REGISTER");
		model.addAttribute( "dateFromTo", DateUtil.formatDate(dateFrom) + " to " + DateUtil.formatDate(dateTo));
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyLogo", formatType.equals("pdf")? company.getLogo(): "");
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser,isFirstNameFirst != null ? isFirstNameFirst : false );
		return "PCReplenishmentRegister.jasper";
	}

}