package eulap.eb.web;

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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.FrequencyReportService;
import eulap.eb.web.dto.FrequencyReportDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller class for Frequency Report.

 *
 */
@Controller
@RequestMapping("frequencyReport")
public class FrequencyReportController {
	private static Logger logger = Logger.getLogger(FrequencyReportController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private FrequencyReportService frequencyReportService;

	@RequestMapping(method = RequestMethod.GET)
	public String showReportForm(HttpSession session, Model model) {
		logger.info("Loading frequency report form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("divisions", divisionService.getActiveDivisions(0));
		return "FrequencyReport.jsp";
	}

	@RequestMapping(value="/generate", method = RequestMethod.GET)
	public String generateReport(@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="typeId")Integer typeId,
			@RequestParam(value="divisionId")Integer divisionId,
			@RequestParam(value="status", required=false) int status,
			@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam(value="format", required=false) String format,
			@RequestParam(value="isFirstNameFirst", required=true) boolean isFirstNameFirst,
			@RequestParam(value="isOrderByLastName", required=false, defaultValue="false") boolean isOrderByLastName,
			Model model, HttpSession session) throws Exception {
		getCommonParam(companyId, divisionId, typeId, model, dateFrom, dateTo, format, status, isFirstNameFirst, isOrderByLastName);
		if (typeId == FrequencyReportDto.FREQUENCY_TYPE_ABSENCE) {
			return "AbsenceFrequencyReport.jasper";
		}
		return "FrequencyReport.jasper";
	}

	private void getCommonParam(Integer companyId, Integer divisionId, Integer typeId, Model model,
			Date dateFrom, Date dateTo, String format, Integer status, boolean isFirstNameFirst,
			boolean isOrderByLastName) throws Exception {
		JRDataSource dataSource = null;
		List<FrequencyReportDto> frequencyReportDtos = null;
		if (typeId.equals(FrequencyReportDto.FREQUENCY_TYPE_ABSENCE)) {
			frequencyReportDtos = frequencyReportService.getAbsentEmployees(companyId,
					divisionId, dateFrom, dateTo, status, isFirstNameFirst, isOrderByLastName);
		} else {
			frequencyReportDtos = frequencyReportService.getFrequencyReport(companyId,
					divisionId, dateFrom, dateTo, status, typeId, isFirstNameFirst);
		}
		dataSource = new JRBeanCollectionDataSource(frequencyReportDtos);
		model.addAttribute("datasource", dataSource);

		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		if (company.getTin() != null) {
			model.addAttribute("companyTin", company.getTin());
		}
		if (company.getLogo() != null) {
			model.addAttribute("companyLogo", company.getLogo());
		}
		company = null;

		String title = null;
		switch(typeId) {
			case 1: title = "LATE FREQUENCY REPORT";
				break;
			case 2: title = "OVERSTAY FREQUENCY REPORT";
				break;
			case 3: title = "UNDERTIME FREQUENCY REPORT";
				break;
			case 4: title = "ABSENCE FREQUENCY REPORT";
				break;
		}
		model.addAttribute("reportTitle", title);
		model.addAttribute("dateFrom", dateFrom);
		model.addAttribute("dateTo", dateTo);
		model.addAttribute("format", format);
	}
}
