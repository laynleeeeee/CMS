package eulap.eb.web;

import java.text.ParseException;
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
import eulap.eb.domain.hibernate.CurrencyRate;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CurrencyRateService;
import eulap.eb.service.CurrencyService;

/**
 * Controller class that will handle request for {@link CurrencyRate}

 */

@Controller
@RequestMapping("admin/currencyRate")
public class CurrencyRateController {
	private final Logger logger = Logger.getLogger(CurrencyRateController.class);
	@Autowired
	private CurrencyRateService currencyRateService;
	@Autowired
	private CurrencyService currencyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(Model model) {
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadSelections(null, model);
		loadCurrencyRates(null, null, "", "", -1, "All", 1, model);
		logger.info("Loading currency rate form.");
		return "CurrencyRate.jsp";
	}

	private void loadSelections(Integer currencyId, Model model) {
		model.addAttribute("currencies", currencyService.getActiveCurrencies(currencyId, true));
	}


	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model) throws ParseException {
		CurrencyRate currencyRate = new CurrencyRate();
		if(pId != null) {
			currencyRate = currencyRateService.getCurencyRate(pId);
		} else {
			Date date = new Date();
			currencyRate.setDate(date);
			currencyRate.setTime(currencyRateService.getMilitaryTime(date));
			currencyRate.setActive(true);
			date = null;
		}
		loadSelections(currencyRate.getCurrencyId(), model);
		model.addAttribute("currencyRate", currencyRate);
		return "CurrencyRateForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String save(@ModelAttribute("currencyRate") CurrencyRate currencyRate,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		currencyRateService.validateForm(currencyRate, result);
		if(result.hasErrors()) {
			logger.debug("Reloading currency rate form.");
			loadSelections(currencyRate.getCurrencyId(), model);
			return "CurrencyRateForm.jsp";
		}
		currencyRateService.saveCurrencyRate(user, currencyRate);
		logger.info("Successfully saved the currency rate.");
		return "successfullySaved";
	}

	private void loadCurrencyRates(Date dateFrom, Date dateTo, String timeFrom, String timeTo, Integer currencyId,
			String status, Integer pageNumber, Model model) {
		Page<CurrencyRate> currencyRates = 
				currencyRateService.searchCurrencyRate(dateFrom, dateTo, timeFrom, timeTo, currencyId, status, pageNumber);
		model.addAttribute("currencyRates", currencyRates);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String search(@RequestParam(value="dateFrom", required=false) Date dateFrom,
			@RequestParam(value="dateTo", required=false) Date dateTo,
			@RequestParam(value="timeFrom", required=false) String timeFrom,
			@RequestParam(value="timeTo", required=false) String timeTo,
			@RequestParam(value="currencyId", required=false) Integer currencyId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model) {
		loadCurrencyRates(dateFrom, dateTo, timeFrom, timeTo, currencyId, status, pageNumber, model);
		return "CurrencyRateTable.jsp";
	}
}
