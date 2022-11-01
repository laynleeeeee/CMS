package eulap.eb.web;

import java.util.ArrayList;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.PosMiddlewareSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.PosMiddlewareSettingService;
import eulap.eb.service.WarehouseService;
import eulap.eb.validator.inv.PosMiddlewareSettingValidator;

/**
 * The controller for {@link PosMiddlewareSetting}

 *
 */
@Controller
@RequestMapping("admin/posMiddlewareSetting")
public class PosMiddlewareSettingCtrlr {
	private final static Logger logger = Logger.getLogger(PosMiddlewareSettingCtrlr.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private PosMiddlewareSettingService service;
	@Autowired
	private PosMiddlewareSettingValidator validator;

	private static String POS_PG_ATTR = "middlewareSettings";
	private static String POS_FORM_ATTR = "posMiddlewareSetting";

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showPosMiddlewareSetting(HttpSession session, Model model) {
		loadCompanies(new PosMiddlewareSetting(), session, model);
		List<String> searchStatuses = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatuses);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadMiddlewareSettings(null, null, "", SearchStatus.All.name(), 1, user, model);
		return "PosMiddlewareSetting.jsp";
	}

	private void loadMiddlewareSettings (Integer companyId, Integer warehouseId, 
			String customerName, String status, int pageNumber, User user, Model model) {
		Page<PosMiddlewareSetting> settings = service.getMiddlewares(companyId, warehouseId, 
				customerName, status, pageNumber, user);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute(POS_PG_ATTR, settings);
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String search(
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId,
			@RequestParam(value="customerName", required=false) String customerName,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") Integer pageNumber,
			HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadMiddlewareSettings(companyId, warehouseId, customerName, status, pageNumber, user, model);
		return "PosMiddlewareSettingTable.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) {
		PosMiddlewareSetting posMiddlewareSetting = new PosMiddlewareSetting();
		if(pId != null) {
			logger.info("Editing the Setting: "+pId);
			posMiddlewareSetting = service.getById(pId);
		} else {
			posMiddlewareSetting.setActive(true);
		}
		logger.info("Showing the POS Middleware Setting form.");
		return loadForm(posMiddlewareSetting, session, model);
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String save(@ModelAttribute("posMiddlewareSetting") PosMiddlewareSetting posMiddlewareSetting,
			BindingResult result, HttpSession session, Model model) {
		logger.info("Saving the POS Middleware Setting form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		validator.validate(posMiddlewareSetting, result);
		if(result.hasErrors()) {
			return loadForm(posMiddlewareSetting, session, model);
		}
		service.save(user, posMiddlewareSetting);
		return "successfullySaved";
	}

	private String loadForm (PosMiddlewareSetting posMiddlewareSetting, 
			HttpSession session, Model model) {
		loadCompanies(posMiddlewareSetting, session, model);
		model.addAttribute(POS_FORM_ATTR, posMiddlewareSetting);
		return "PosMiddlewareSettingForm.jsp";
	}

	private void loadCompanies(PosMiddlewareSetting posMiddlewareSetting, HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Company> companies = new ArrayList<>();
		if (posMiddlewareSetting.getId() == 0) {
			companies = (List<Company>) companyService.getActiveCompanies(user, "", "", "");
		} else {
			Company company = companyService.getCompany(posMiddlewareSetting.getCompanyId());
			companies.add(company);
		}
		model.addAttribute("companies", companies);
	}
}
