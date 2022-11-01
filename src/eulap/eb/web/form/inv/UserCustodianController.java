package eulap.eb.web.form.inv;

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
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCustodian;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.UserCustodianService;

/**
 * Controller class for User Custodian.

 *
 */
@Controller
@RequestMapping ("/admin/userCustodian")
public class UserCustodianController {
	private final Logger logger = Logger.getLogger(UserCustodianController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private UserCustodianService userCustodianService;
	@Autowired
	private DivisionService divisionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showProductList (Model model, HttpSession session) {
		List<String> searchStatus = SearchStatus.getSearchStatus();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		seachUserCustodians(model, -1, -1, "",  "All", 1);
		loadSelections(user, model, null, 0, 0);
		model.addAttribute("status", searchStatus);
		return "UserCustodian.jsp";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchProductline(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="userCustodianName", required=false) String userCustodianName,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber,
			HttpSession session, Model model) {
		seachUserCustodians(model, companyId, divisionId, userCustodianName, status, pageNumber);
		return "UserCustodianTable.jsp";
	}

	private void seachUserCustodians(Model model, Integer companyId, Integer divisionId, String userCustodianName, String searchStatus, int pageNumber) {
	Page<UserCustodian> userCustodians = userCustodianService.searchUserCustodians(companyId, divisionId, userCustodianName, searchStatus, pageNumber);
		model.addAttribute("userCustodians", userCustodians);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showUserCustodianForm(@RequestParam(value="pId", required=false) Integer userCustodianId,
			HttpSession session, Model model) {
		UserCustodian userCustodian = new UserCustodian();
		if(userCustodianId != null) {
			userCustodian = userCustodianService.getUserCustodian(userCustodianId);
		} else {
			userCustodian.setActive(true);
		}
		logger.info("Showing the user company form.");
		return loadUserCustodianForm(model, userCustodian, session);
	}

	private String loadUserCustodianForm(Model model, UserCustodian userCustodian, HttpSession session) {
		userCustodian.serializeUserCustodianLines();
		int divisionId = userCustodian.getDivisionId() == null ? 0 : userCustodian.getDivisionId();
		if(userCustodian.getDivisionId() != null) {
			userCustodian.setDivision(divisionService.getDivision(divisionId));
		}
		model.addAttribute("userCustodian", userCustodian);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user, model, null, 0, divisionId);
		return "UserCustodianForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveUserCustodian(@ModelAttribute("userCustodian") UserCustodian userCustodian,
			BindingResult result, HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		userCustodian.deSerializeUserCustodianLines();
		synchronized (this) {
			userCustodianService.processLines(userCustodian);
			userCustodianService.validate(userCustodian, result);
			if (result.hasErrors()) {
				return loadUserCustodianForm(model, userCustodian, session);
			}
			userCustodianService.saveUserCustodian(userCustodian, user);
		}
		return "successfullySaved";
	}

	private void loadSelections (User user, Model model, UserCustodian userCustodian, int companyId, Integer divisionId) {
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		model.addAttribute("divisions", divisionService.getLastLevelDivisions(divisionId));
	}
}
