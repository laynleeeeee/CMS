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
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.validator.PayrollTimePeriodValidator;
import eulap.eb.web.dto.TimePeriodMonth;

/**
 * Controller class for Payroll Time Period module.

 *
 */
@Controller
@RequestMapping("/admin/payrollTimePeriod")
public class PayrollTimePeriodCtrlr {
	private static final Logger logger = Logger.getLogger(PayrollTimePeriodCtrlr.class);
	@Autowired
	private PayrollTimePeriodService pTimePeriodService;
	@Autowired
	private PayrollTimePeriodValidator pTimePeriodValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	/**
	 * Load the main page of the admin settinng
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showMainPageForm(Model model, HttpSession session) {
		logger.info("Load the payroll time period main page");
		loadPayrollTimePeriods(model, "", -1, -1, SearchStatus.All.name(), PageSetting.START_PAGE);
		loadMonthAndYear(model);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		return "PayrollTimePeriod.jsp";
	}

	/**
	 * Load the employee form for adding or editing of employee info
	 */
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		PayrollTimePeriod payrollTimePeriod = new PayrollTimePeriod();
		if(pId != null) {
			logger.info("Loading payroll time period with id: "+pId);
			payrollTimePeriod = pTimePeriodService.getPayrollTimePeriod(pId);
		} else {
			payrollTimePeriod.setYear(DateUtil.getYear(new Date()));
			payrollTimePeriod.setActive(true);
		}
		payrollTimePeriod.serializePayrollTimeSchedule();
		return loadForm(payrollTimePeriod, model);
	}

	/**
	 * Validating and Saving the employee form.
	 */
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String savePayrollTimePeriod(@ModelAttribute("payrollTimePeriod") PayrollTimePeriod payrollTimePeriod,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		payrollTimePeriod.deserializePayrollTimeSchedule();
		payrollTimePeriod.setPayrollTimePeriodSchedules(
				pTimePeriodService.processTimeScheds(payrollTimePeriod.getPayrollTimePeriodSchedules()));
		pTimePeriodValidator.validate(payrollTimePeriod, result);
		if(result.hasErrors()) {
			logger.debug("Reloading the payroll time period form");
			return loadForm(payrollTimePeriod, model);
		}
		pTimePeriodService.savePayrollTimePeriodForm(payrollTimePeriod, user);
		logger.info("Successfully saved the payroll time period form.");
		return "successfullySaved";
	}

	/**
	 * Get the list of payroll time period
	 * @param name The name of the payroll time period
	 * @param pageNumber The page number
	 * @return The list of the payroll time period
	 */
	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchEmployee(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="month", required=false) Integer month,
			@RequestParam(value="year", required=false) Integer year,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model,
			HttpSession session) {
		loadPayrollTimePeriods(model, name, month, year, status, pageNumber);
		return "PayrollTimePeriodTable.jsp";
	}

	/**
	 * Load the list of the payroll time periods
	 * @param name The name of the payroll time periods
	 * @param pageNumber The page number
	 */
	private void loadPayrollTimePeriods(Model model, String name, Integer month, Integer year,
			String status, Integer pageNumber) {
		Page<PayrollTimePeriod> payrollTimePeriods =
				pTimePeriodService.searchPayrollTimePeriods(name, month, year, status, pageNumber);
		model.addAttribute("payrollTimePeriods", payrollTimePeriods);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	private String loadForm (PayrollTimePeriod payrollTimePeriod, Model model) {
		loadMonthAndYear(model);
		model.addAttribute("payrollTimePeriod", payrollTimePeriod);
		return "PayrollTimePeriodForm.jsp";
	}

	private void loadMonthAndYear(Model model) {
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", pTimePeriodService.initYears());
	}
}
