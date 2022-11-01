package eulap.eb.web.report;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
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
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.QrtrlyValAddedTaxDeclrtnService;
import eulap.eb.web.dto.ValueAddedTaxSummaryDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * The entry point of quarterly alphalist of payees form.

 */

@Controller
@RequestMapping(value="/quarterlyValueAddedTaxDeclaration")
public class QrtrlyValAddedTaxDeclrtnCtrlr {
	private static final Logger LOGGER = Logger.getLogger(UnliquidatedPCVAgingCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private QrtrlyValAddedTaxDeclrtnService qrtrlyValAddedTaxDeclrtnService;

	@RequestMapping (method = RequestMethod.GET)
	public String showParams(HttpSession session, Model model) {
		LOGGER.info("Show the parameters of the report.");
		model.addAttribute("companies", companyService.getCompanies(CurrentSessionHandler.getLoggedInUser(session)));
		model.addAttribute("divisions", divisionService.getActiveDivsions(CurrentSessionHandler.getLoggedInUser(session), 0));
		model.addAttribute("quarter", Arrays.asList("Jan - Mar", "Apr - Jun", "Jul - Sep" , "Oct - Dec"));
		model.addAttribute("years", DateUtil.rangeYears(1999, Year.now().getValue()));
		model.addAttribute("defaultYear", DateUtil.getYear(new Date()));
		return "QuarterlyValueAddedTaxDeclaration.jsp";
	}

	@RequestMapping(value = "/generatePDF", method=RequestMethod.GET)
	public String generatePDF(@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId") int divisionId,
			@RequestParam (value="quarter", required=true) int quarter,
			@RequestParam (value="year") Integer year,
			@RequestParam (value="formatType", required = true) String formatType,
			HttpSession session, Model model) {
		LOGGER.info("Generating the Quarterly Value-Added Tax Declaration.");
		List<ValueAddedTaxSummaryDto> inputVATSummaryDtos = new ArrayList<ValueAddedTaxSummaryDto>();
		ValueAddedTaxSummaryDto vatSummaryDto = qrtrlyValAddedTaxDeclrtnService.getQrtlyVATSummaryDto(companyId, divisionId, year, quarter, -1);
		inputVATSummaryDtos.add(vatSummaryDto);
		JRDataSource dataSource = new JRBeanCollectionDataSource(inputVATSummaryDtos);
		LOGGER.debug("Generating the data source.");
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType);
		model.addAttribute("reportTitle", "QUARTERLY VALUE ADDED TAX DECLARATION");
		String[] arrSplit =  QrtrlyValAddedTaxDeclrtnService.getQuarterFullName(quarter).split(" - ");
		model.addAttribute("quarterDate", arrSplit[0].toUpperCase() + " - " +
				arrSplit[1].toUpperCase() + " " + year.toString());
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", formatType.equals("pdf") ? company.getLogo() : "");
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		LOGGER.debug("Searching transactions for Company: "+company.getName());
		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser, false);
		LOGGER.info("Successfully generated the report");
		return "QuarterlyValueAddedTaxDeclaration.jasper";
	}
}
