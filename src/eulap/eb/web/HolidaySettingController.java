package eulap.eb.web;

import java.util.Date;
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
import eulap.eb.domain.hibernate.HolidaySetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.HolidaySettingService;
import eulap.eb.validator.HolidaySettingValidator;
/**
 * Controller class that will handle request for {@link HolidaySetting}

 *
 */
@Controller
@RequestMapping("/admin/holidaySetting")
public class HolidaySettingController {
	private static final Logger  logger= Logger.getLogger(HolidaySettingController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private HolidaySettingService holidaySettingService;
	@Autowired
	private HolidaySettingValidator holidaySettingValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	/**
	 * Load the Holiday Setting main page.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showMainForm(@RequestParam(value="pId", required=false)
			Integer pId,Model model, HttpSession session){
		List<String> searchStatus = SearchStatus.getSearchStatus();
		HolidaySetting holidaySetting = new HolidaySetting();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadHolidaySettings(null, null, null, null, "All", 1, model);
		model.addAttribute("status", searchStatus);
		loadSelections(model, user, holidaySetting);
		return "HolidaySetting.jsp";
	}

	public String loadForm(Model model, User user, HolidaySetting holidaySetting) {
		loadSelections(model, user, holidaySetting);
		model.addAttribute("holidaySetting", holidaySetting);
		return "HolidaySettingForm.jsp";
	}

	/**
	 * Load the Holiday Setting form
	 */
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		HolidaySetting holidaySetting = new HolidaySetting();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (pId != null) {
			holidaySetting = holidaySettingService.getHolidaySetting(pId);
		} else {
			holidaySetting.setActive(true);
		}
		return loadForm(model, user, holidaySetting);
	}

	/**
	 * Validating and saving of Form.
	 */
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveHolidaySetting(@ModelAttribute("holidaySetting") HolidaySetting holidaySetting,
			BindingResult result, Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		holidaySettingValidator.validate(holidaySetting, result);
		if (result.hasErrors()) {
			logger.debug("Reloading the employee shift Form");
			return loadForm(model, user, holidaySetting);
		}
		holidaySettingService.saveHolidaySetting(user, holidaySetting);
		logger.info("Successfully saved the Holiday Setting.");
		return "successfullySaved";
	}

	/**
	 * Load the list of companies
	 * @param model The model
	 * @param user The current user logged
	 */
	private void loadSelections(Model model, User user, HolidaySetting holidaySetting) {
		Integer companyId = holidaySetting.getCompanyId();
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user,
				(companyId != null ? companyId : 0)));
		model.addAttribute("holidayTypes", holidaySettingService.getAllActiveHolidayType());
	}

	/**
	 * Get the page list of Holiday Setting.
	 * @param companyId The id of the company.
	 * @param name The name of the Holiday Setting.
	 * @param holidayTypeId The holiday type id.
	 * @param date The date of the Holiday.
	 * @param status ALL, ACTIVE, INACTIVE.
	 * @param pageNumber The page number.
	 */
	private void loadHolidaySettings(Integer companyId ,String name,
			Integer holidayTypeId, Date date, String status, int pageNumber, Model model){
		Page<HolidaySetting> holidaySettings = holidaySettingService.getHolidaySettings(companyId, name, holidayTypeId, date,
				status, pageNumber);
		model.addAttribute("holidaySettings", holidaySettings);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	/**
	 * Get the list of Holiday Settings based on specified parameters.
	 * @param companyId The id of the Company.
	 * @param name The name of the Holiday Setting.
	 * @param date The date of Holiday.
	 * @param status ALL, ACTIVE, INACTIVE.
	 * @param pageNumber The page number
	 * @return The list of Holiday Settings.
	 */
	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchHolidaySetting(
			@RequestParam(value="companyId", required = false) Integer companyId,
			@RequestParam(value="name", required = false) String name,
			@RequestParam(value="date", required = false) Date date,
			@RequestParam(value="holidayTypeId", required = false) Integer holidayTypeId,
			@RequestParam(value="status", required = false) String status,
			@RequestParam(value="pageNumber", required = false) int pageNumber, Model model,
			HttpSession session) {
		loadHolidaySettings(companyId, name, holidayTypeId, date, status, pageNumber, model);
		return "HolidaySettingTable.jsp";
	}
}
