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
import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AdminDeductionTypeService;

/**
 * Controller class for {@link DeductionType}

 *
 */
@Controller
@RequestMapping(value="admin/deductionType")
public class AdminDeductionTypeCtrl {
	private final Logger logger = Logger.getLogger(AdminDeductionTypeCtrl.class);
	@Autowired
	private AdminDeductionTypeService adminDeductionTypeService;

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	/**
	 * Shows the deduction type main jsp page.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showMainPage(Model model, HttpSession session) {
		logger.info("Loading therapist main jsp page");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadDeductionType("", "All", PageSetting.START_PAGE, model, user);
		return "DeductionType.jsp";
	}

	/**
	 * Shows the deduction type form.
	 */
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		DeductionType deductionType = new DeductionType();
		if(pId != null) {
			logger.info("Loading deduction type with id: " + pId);
			deductionType = adminDeductionTypeService.getDeductionType(pId);
		} else {
			deductionType.setActive(true);
		}
		model.addAttribute("deductionType", deductionType);
		return "DeductionTypeForm.jsp";
	}

	/**
	 * Method for saving deduction type
	 */
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveTherapist(@ModelAttribute("deductionType") DeductionType deductionType,
			BindingResult result, Model model, HttpSession httpSession) {
		User user = CurrentSessionHandler.getLoggedInUser(httpSession);
		adminDeductionTypeService.saveDeductionType(user, deductionType, result);
		if(result.hasErrors()) {
			model.addAttribute("deductionType", deductionType);
			return "DeductionTypeForm.jsp";
		}
		logger.info("Successfuly saved deduction type");
		return "successfullySaved";
	}

	/**
	 * Method for searching deduction type
	 */
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchDeductionType(@RequestParam(value="name") String name,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadDeductionType(name, status, pageNumber, model, user);
		return "DeductionTypeTable.jsp";
	}

	private void loadDeductionType(String name, String status, Integer pageNumber,
			Model model, User user) {
		Page<DeductionType> deductionTypes = adminDeductionTypeService.searchDeductionTypes(user, name, status, pageNumber);
		model.addAttribute("deductionTypes", deductionTypes);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}
}
