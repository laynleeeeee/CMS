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
import eulap.eb.domain.hibernate.TypeOfLeave;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AdminTypeOfLeaveService;

/**
 * Controller class for {@link TypeOfLeave}

 *
 */
@Controller
@RequestMapping(value="admin/typeOfLeave")
public class AdminTypeOfLeaveCtrl {
	private final Logger logger = Logger.getLogger(AdminTypeOfLeaveCtrl.class);
	@Autowired
	private AdminTypeOfLeaveService adminTypeOfLeaveService;

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	/**
	 * Shows the type of leave main jsp page.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showMainPage(Model model, HttpSession session) {
		logger.info("Loading type of leave main page.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadLeaveType("", "All", PageSetting.START_PAGE, model, user);
		return "TypeOfLeave.jsp";
	}

	/**
	 * Shows the type of leave form.
	 */
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		TypeOfLeave typeOfLeave = new TypeOfLeave();
		if(pId != null) {
			logger.info("Loading deduction type with id: " + pId);
			typeOfLeave = adminTypeOfLeaveService.getTypeOfLeave(pId);
		} else {
			typeOfLeave.setActive(true);
		}
		model.addAttribute("leaveType", typeOfLeave);
		return "TypeOfLeaveForm.jsp";
	}

	/**
	 * Controller method for saving type of leave.
	 */
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveLeaveType(@ModelAttribute("leaveType") TypeOfLeave typeOfLeave,
			BindingResult result, Model model, HttpSession httpSession) {
		User user = CurrentSessionHandler.getLoggedInUser(httpSession);
		adminTypeOfLeaveService.saveLeave(user, typeOfLeave, result);
		if(result.hasErrors()) {
			model.addAttribute("leaveType", typeOfLeave);
			return "TypeOfLeaveForm.jsp";
		}
		logger.info("Successfuly saved leave type");
		return "successfullySaved";
	}

	/**
	 * Controller method for searching type of leave.
	 */
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchLeaveType(@RequestParam(value="name") String name,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadLeaveType(name, status, pageNumber, model, user);
		return "TypeOfLeaveTable.jsp";
	}

	private void loadLeaveType(String name, String status, Integer pageNumber,
			Model model, User user) {
		Page<TypeOfLeave> leaveTypes = adminTypeOfLeaveService.searchLeaves(user, name, status, pageNumber);
		model.addAttribute("leaveTypes", leaveTypes);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}
}
