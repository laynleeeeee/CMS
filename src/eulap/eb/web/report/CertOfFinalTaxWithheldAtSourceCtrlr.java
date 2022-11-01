package eulap.eb.web.report;

import java.time.Year;
import java.util.ArrayList;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.report.CertOfFinalTaxWithheldAtSrcService;
import eulap.eb.web.dto.CertificateOfFinalTaxWithheldAtSourceDto;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller for Certificate of final tax withheld at source
 * The entry point of Certificate of final tax withheld at source.

 */

@Controller
@RequestMapping(value="/certOfFinalTaxWithheldAtSrc")
public class CertOfFinalTaxWithheldAtSourceCtrlr {
	private static Logger logger =  Logger.getLogger(CertOfFinalTaxWithheldAtSourceCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CertOfFinalTaxWithheldAtSrcService cftwsService;

	@RequestMapping (value="/showParams",method = RequestMethod.GET)
	public String showCertificateOfFinalTaxWithheldSourcePage (Model model, HttpSession session){
		logger.info("Loading the main page of Item unit cost history per supplier.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		logger.info("Successfully loaded certificate of final tax withheld at source.");
		model.addAttribute("defaultMonth", DateUtil.getMonth(new Date()));
		model.addAttribute("listMonths",TimePeriodMonth.getMonths());
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		return "CertifacteOfFinalTaxWithheldAtSource.jsp";
	}

	@RequestMapping (value="/generatePDF", method = RequestMethod.GET)
	public String generateReport(@RequestParam (value="companyId") int companyId,
			@RequestParam (value="divisionId") int divisionId,
			@RequestParam (value="year") int year,
			@RequestParam (value="fromMonth") int fromMonth,
			@RequestParam (value="toMonth") int toMonth,
			@RequestParam (value="birAtcId") int birAtcId,
			@RequestParam (value="formatType", required = true) String formatType,
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating " + formatType + " version of summary alphalist of withholding taxes.");
		List<CertificateOfFinalTaxWithheldAtSourceDto> cftwsDtos = new ArrayList<CertificateOfFinalTaxWithheldAtSourceDto>();
		cftwsDtos.add(cftwsService.getCertFinalTax(companyId, divisionId, year, fromMonth, toMonth, birAtcId));
		JRDataSource dataSource = new JRBeanCollectionDataSource(cftwsDtos);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("companyTin", company.getTin());
		company = null;
		model.addAttribute("reportTitle", "Certificate of Final Tax Withheld at Source");
		model.addAttribute("fromMonth", DateUtil.getFirstDayOfMonth(DateUtil.getDateByYearAndMonth(year, (fromMonth - 1))));
		model.addAttribute("toMonth", DateUtil.getEndDayOfMonth(DateUtil.getDateByYearAndMonth(year, (toMonth - 1))));
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("preparedBy", user.getFirstName() + " " + user.getLastName());
		ReportUtil.getPrintDetails(model, user, false);
		logger.info("Sucessfully loaded the Certificate of final tax withheld at source report.");
		return "CertificateFinalTaxWithheldAtSource.jasper";
	}
}

