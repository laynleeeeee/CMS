package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ItemVolumeConversion;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemVolumeConversionService;
import eulap.eb.validator.inv.ItemVolumeConversionValidator;


/**
 * Controller class for item Volume Conversion.

 */
@Controller 
@RequestMapping("/admin/volumeConversion")
public class ItemVolumeConversionCtrlr {
	private static Logger logger = Logger.getLogger(ItemVolumeConversionCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemVolumeConversionValidator conversionValidator;
	@Autowired
	private ItemVolumeConversionService volumeConversionService;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showSchoolYears(Model model, HttpSession session){
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		logger.info("Load item volume conversion");
		loadVolumeConversion(model, -1, -1, SearchStatus.All.name(), 1);
		List<String> searchStatuses = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatuses);
		loadCompanies(session, model, 0);
		return "ItemVolumeConversion.jsp";
	}

	private void loadCompanies(HttpSession session, Model model, int companyId) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
	}

	private void loadVolumeConversion(Model model, Integer itemId, Integer companyId, String status, Integer pageNumber) {
		Page<ItemVolumeConversion> volumeConversion = volumeConversionService.getAllItemVolumeConversion(itemId,
				companyId, status, pageNumber);
		model.addAttribute("volumeConversion", volumeConversion);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchSchoolYear(
			@RequestParam(value="itemId", required=false) Integer itemId,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="page") Integer pageNumber,
			HttpSession session, Model model) {
		loadVolumeConversion(model, itemId, companyId, status, pageNumber);
		return "ItemVolumeConversionTable.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showSchoolYearForm(@RequestParam(value="volumeConversionId", required=false) Integer volumeConversionId,
			HttpSession session, Model model) {
		ItemVolumeConversion volumeConversion = new ItemVolumeConversion();
		int companyId = 0;
		if(volumeConversionId != null) {
			logger.info("Editing the Volume Conversion: "+volumeConversionId);
			volumeConversion = volumeConversionService.getItemVolumeConversion(volumeConversionId);
			companyId = volumeConversion.getCompanyId();
		} else {
			volumeConversion.setActive(true);
		}
		logger.info("Showing the Item Volume Conversion form.");
		return loadItemVolumeConversionForm(session, volumeConversion, model, companyId);
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveSchoolYear(@ModelAttribute("volumeConversion") ItemVolumeConversion volumeConversion,
			BindingResult result, HttpSession session, Model model) {
		logger.info("Saving the Item Volume Conversion form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		conversionValidator.validate(volumeConversion, result);
		if(result.hasErrors()) {
			logger.debug("Form has error/s. Reloading the Item Volume Conversion form.");
			return loadItemVolumeConversionForm(session, volumeConversion,
						model, volumeConversion.getCompanyId());
		}
		volumeConversionService.saveVolumeConversion(volumeConversion, user);
		return "successfullySaved";
	}

	private String loadItemVolumeConversionForm (HttpSession session, ItemVolumeConversion volumeConversion,
			Model model, int companyId) {
		model.addAttribute("volumeConversion", volumeConversion);
		loadCompanies(session, model, companyId);
		return "ItemVolumeConversionForm.jsp";
	}
}
