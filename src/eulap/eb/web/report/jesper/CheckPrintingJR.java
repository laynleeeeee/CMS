package eulap.eb.web.report.jesper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.CheckbookTemplate;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.service.CheckbookService;
import eulap.eb.service.CheckbookTemplateService;
import eulap.eb.web.dto.CheckPrintingDto;

/**
 * A class the generate PDF file format for checkPrinting.

 */
@Controller
@RequestMapping ("checkPrinting")
public class CheckPrintingJR {
	Logger logger =  Logger.getLogger(CheckPrintingJR.class);
	@Autowired
	private CheckbookTemplateService checkPrintingService;
	@Autowired
	private CheckbookService checkbookService;
	@Autowired
	private CheckbookTemplateService checkTemplateService;

	@RequestMapping (method = RequestMethod.GET)
	private String checkPrinting (@RequestParam (value="name", required = false) String name,
			@RequestParam (value="amount", required = false) Double amount,
			@RequestParam (value="checkDate", required = false) Date checkDate,
			@RequestParam (value="checkbookTemplateId", required = true) Integer checkbookTemplateId,
			Model model) throws ConfigurationException{
		logger.info("Generating check printing.");
		CheckbookTemplate checkbookTemplate = checkTemplateService.getCheckTemplate(checkbookTemplateId);
		List<CheckPrintingDto> checkPrintingDtos = new ArrayList<CheckPrintingDto>();
		checkPrintingDtos.add(checkPrintingService.setCheckDetails(name, amount,
				checkDate, checkbookTemplate.getMaxCharAmountInWords(), checkbookTemplate.getMaxCharName()));
		JRDataSource dataSource = new JRBeanCollectionDataSource(checkPrintingDtos, false);
		model.addAttribute("format", "pdf");
		model.addAttribute("datasource", dataSource);
		return checkbookTemplate.getViewsPropName();
	}

	@RequestMapping (value="/new", method = RequestMethod.GET)
	private String newCheckPrinting (@RequestParam (value="name", required = false) String name,
			@RequestParam (value="amount", required = false) Double amount,
			@RequestParam (value="checkDate", required = false) Date checkDate,
			@RequestParam (value="checkbookTemplateId", required = true) Integer checkbookTemplateId,
			@RequestParam (value="currencyId", required = false) Integer currencyId,
			Model model) throws ConfigurationException{
		logger.info("Generating check printing.");
		CheckbookTemplate checkbookTemplate = checkTemplateService.getCheckTemplate(checkbookTemplateId);
		List<CheckPrintingDto> checkPrintingDtos = new ArrayList<CheckPrintingDto>();
		boolean removePesos = currencyId != null && currencyId != Currency.DEFUALT_PHP_ID;
		checkPrintingDtos.add(checkPrintingService.setCheckDetails(name, amount,
				checkDate, checkbookTemplate.getMaxCharAmountInWords(), checkbookTemplate.getMaxCharName(), removePesos));
		JRDataSource dataSource = new JRBeanCollectionDataSource(checkPrintingDtos, false);
		model.addAttribute("format", "pdf");
		model.addAttribute("datasource", dataSource);
		model.addAttribute("strDate", DateUtil.formatDate(checkPrintingDtos.get(0).getDate())); //convert date to string.
		return checkbookTemplate.getViewsPropName();
	}
}
