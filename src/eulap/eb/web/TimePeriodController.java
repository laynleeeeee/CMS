package eulap.eb.web;

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
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.TimePeriodService;
import eulap.eb.validator.TimePeriodValidator;

/**
 * Controller of Time Period module.

 */
@Controller
@RequestMapping("/admin/timePeriod")
public class TimePeriodController {
	private static String PERIOD_STATUS_ATTRIBUTE_NAME = "periodStatus";
	private static final String TIME_PERIOD_ATTRIBUTE_NAME = "timePeriods";
	private final Logger logger = Logger.getLogger(TimePeriodController.class);
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private TimePeriodValidator timePeriodValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showTimePeriods(Model model) {
		Page<TimePeriod> getAllTimePeriods = timePeriodService.getAllTimePeriods();
		model.addAttribute(TIME_PERIOD_ATTRIBUTE_NAME, getAllTimePeriods);
		model.addAttribute(PERIOD_STATUS_ATTRIBUTE_NAME, timePeriodService.getAllPeriodStatuses());
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		logger.info("Show all the time periods");
		return "showTimePeriod";
	}

	@RequestMapping(method = RequestMethod.GET, params = {"name", "periodStatusId", "startDate", "endDate", "pageNumber"})
	public String searchTimePeriods(@RequestParam String name, @RequestParam int periodStatusId,
			@RequestParam String startDate, @RequestParam String endDate,
			@RequestParam int pageNumber, Model model) {
		Page<TimePeriod> timePeriods = timePeriodService.searchTimePeriods(name, periodStatusId, startDate, endDate, pageNumber);
		model.addAttribute(TIME_PERIOD_ATTRIBUTE_NAME, timePeriods);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		logger.info("Search Time Periods.");
		return "showTimePeriodTable";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String addTimePeriod(Model model) {
		TimePeriod timePeriod = new TimePeriod();
		logger.info("Add a time period.");
		return showTimePeriodForm(timePeriod, model);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET, params = {"timePeriodId"})
	public String editTimePeriod(@RequestParam int timePeriodId, Model model) {
		TimePeriod getTimePeriod = timePeriodService.getPeriodById(timePeriodId);
		logger.info("Editing time period:" + timePeriodId);
		return showTimePeriodForm(getTimePeriod, model);
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String saveTimePeriod(@ModelAttribute("timePeriod") TimePeriod timePeriod,
			BindingResult result, HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		timePeriodValidator.validate(timePeriod, result);
		if (result.hasErrors())
			return showTimePeriodForm(timePeriod, model);
		timePeriodService.saveTimePeriod(timePeriod, user);
		logger.info("Saved time period: " + timePeriod.getName());
		return "successfullySaved";
	}

	private String showTimePeriodForm(TimePeriod timePeriod, Model model) {
		model.addAttribute("periodStatus", timePeriodService.getAllPeriodStatuses());
		model.addAttribute(timePeriod);
		return "showTimePeriodForm";
	}
}