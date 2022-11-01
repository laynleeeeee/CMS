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
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CurrencyService;

/**
 * Controller class that will handle request for {@link Currency}

 */

@Controller
@RequestMapping("admin/currency")
public class CurrencyController {
	private final Logger logger = Logger.getLogger(CurrencyController.class);
	@Autowired
	private CurrencyService currencyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(Model model) {
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadCurrency("", "", "", "All", 1, model);
		logger.info("Loading currency form.");
		return "Currency.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model) {
		Currency currency = new Currency();
		if(pId != null) {
			currency = currencyService.getCurency(pId);
			logger.info("Loading currency : "+currency.getName());
		} else {
			currency.setActive(true);
		}
		model.addAttribute("currency", currency);
		return "CurrencyForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String save(@ModelAttribute("currency") Currency currency,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		currencyService.validateForm(currency, result);
		if(result.hasErrors()) {
			logger.debug("Reloading currency form.");
			return "CurrencyForm.jsp";
		}
		currencyService.saveCurrency(user, currency);
		logger.info("Successfully saved the currency.");
		return "successfullySaved";
	}

	private void loadCurrency(String name, String description, String sign,  String status,  
			Integer pageNumber, Model model) {
		Page<Currency> currencies = 
				currencyService.searchCurrency(name, description, sign, status, pageNumber);
		model.addAttribute("currencies", currencies);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String search(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="description", required=false) String description,
			@RequestParam(value="sign", required=false) String sign,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model) {
		loadCurrency(name, description, sign, status, pageNumber, model);
		return "CurrencyTable.jsp";
	}
}
