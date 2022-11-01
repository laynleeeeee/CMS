package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

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
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.domain.hibernate.UserCompanyHead;
import eulap.eb.service.CompanyService;
import eulap.eb.service.UserCompanyService;
import eulap.eb.service.UserService;
import eulap.eb.validator.UserCompanyValidator;

/**
 * Controller class for User Company.

 */
@Controller
@RequestMapping("admin/userCompany")
public class UserCompanyController {
	private final Logger logger = Logger.getLogger(UserCompanyController.class);
	@Autowired
	private UserCompanyService userCompanyService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserCompanyValidator userCompanyValidator;
	@Autowired
	private CompanyService companyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showUserCompanyList (Model model, HttpSession currentSession) {
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadUserCompanies(model, "","",  "All", 1);
		return "userCompanyList";
	}

	private void loadUserCompanies(Model model, String userName, String companyName, String searchStatus, Integer pageNumber) {
		Page<User> users = userCompanyService.getUserCompanies(userName, companyName, searchStatus, pageNumber);
		model.addAttribute("users", users);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showUserCompanyForm(@RequestParam(value="userId", required=false) Integer userId,
			HttpSession session, Model model) {
		UserCompanyHead userCompanyHead = new UserCompanyHead();
		if(userId != null) {
			logger.info("Editing the user company: " + userId);
			User user = userService.getUser(userId);
			userCompanyHead = userCompanyService.getUserCompanyHeadPerUser(userId);
			if (userCompanyHead == null){
				userCompanyHead = new UserCompanyHead();
			}
			userCompanyHead.setUserName(user.getUsername());
			for (UserCompany usrCompany : user.getUserCompanies()) {
				usrCompany.setUserCompanyId(usrCompany.getId());
				usrCompany.setCompanyName(companyService.getCompanyName(usrCompany.getCompanyId()));
			}
			userCompanyHead.setUserCompanies(user.getUserCompanies());
			userCompanyHead.setUsrId(user.getId());
		} else {
			userCompanyHead.setActive(true);
		}
		logger.info("Showing the user company form.");
		return loadUCForm(model, userCompanyHead);
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveUserCompany(@ModelAttribute("userCompanyHead") UserCompanyHead userCompanyHead,
			BindingResult result, HttpSession session, Model model) {
		logger.info("Saving the User Company form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		userCompanyHead.deSerializeUserCompanies();
		userCompanyHead.setUserCompanies(userCompanyService.processUserCompanies(userCompanyHead.getUserCompanies()));
		userCompanyValidator.validate(userCompanyHead, result);
		if(result.hasErrors()) {
			logger.debug("Form has error/s. Reloading the User Company form.");
			return loadUCForm(model, userCompanyHead);
		}

		userCompanyService.save(userCompanyHead, user);
		return "successfullySaved";
	}

	private String loadUCForm (Model model, UserCompanyHead userCompanyHead) {
		userCompanyHead.serializeUserCompanies();
		model.addAttribute("userCompanyHead", userCompanyHead);
		return "userCompanyForm";
	}

	@RequestMapping(value="/byUserName", method = RequestMethod.GET)
	public @ResponseBody String getUsersByUserName (
			@RequestParam (value="userId") Integer userId,
			@RequestParam (value="userName") String userName,
			HttpSession session) {
		logger.info("Retrieving users by user name : " + userName);
		List<User> users = userService.getUsersByUsername(userName, true, 10, userId);
		String[] exclude = {"userCompanies", "userGroup", "company", 
				"accessRight", "serviceLeaseKey", "userAccessRights", 
				"position", "userLoginStatus"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(users, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/byExactUserName", method = RequestMethod.GET)
	public @ResponseBody String getUserByUserName (
			@RequestParam (value="userId") Integer userId,
			@RequestParam (value="userName") String userName,
			HttpSession session) {
		logger.info("Retrieving users by user name : " + userName);
		List<User> users = userService.getUsersByUsername(userName, true, 1, userId);
		String[] exclude = {"userCompanies", "userGroup", "company", 
				"accessRight", "serviceLeaseKey", "userAccessRights", 
				"position", "userLoginStatus"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONObject ret = JSONObject.fromObject(users.iterator().next(), jConfig);
		return ret.toString();
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchUserCompany(
			@RequestParam(value="userName", required=false) String userName,
			@RequestParam(value="companyName", required=false) String companyName,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="page") Integer page,
			HttpSession session, Model model) {
		loadUserCompanies(model, userName, companyName, status, page);
		return "userCompanyTable";
	}
}