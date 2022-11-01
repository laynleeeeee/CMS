package eulap.eb.web;

import java.io.IOException;
import java.util.Collection;
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
import eulap.eb.dao.CivilStatusDao;
import eulap.eb.dao.GenderDao;
import eulap.eb.domain.hibernate.CivilStatus;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Driver;
import eulap.eb.domain.hibernate.Gender;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DriverService;

/**
 * Controller class for {@link Driver}

 *
 */
@Controller
@RequestMapping("/admin/driver")
public class DriverController {
	private static final Logger logger = Logger.getLogger(DriverController.class);
	@Autowired
	private DriverService driverService;
	@Autowired
	private GenderDao genderDao;
	@Autowired
	private CivilStatusDao civilStatusDao;
	@Autowired
	private CompanyService companyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	/**
	 * Load the Driver main page.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showMain(Model model, HttpSession session) {
		logger.info("Load the Driver admin setting main page.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadSelections(model, user, null, false);
		loadDrivers(model, "", -1, SearchStatus.All.name(), PageSetting.START_PAGE);
		return "Driver.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Driver driver = new Driver();
		if(pId != null) {
			logger.info("Loading driver with id: "+pId);
			driver = driverService.getDriver(pId);
		} else {
			driver.setActive(true);
		}
		loadSelections(model, user, driver, true);
		model.addAttribute("driver", driver);
		return "DriverForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveDriver(@ModelAttribute("driver") Driver driver,
			BindingResult result, Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		driverService.validateDriver(driver, result);
		if(result.hasErrors()) {
			logger.debug("Reloading the Form");
			loadSelections(model, user, driver, true);
			return "DriverForm.jsp";
		}
		driverService.saveDriver(driver, user, result);
		return "successfullySaved";
	}

	private void loadDrivers(Model model, String name, Integer companyId, String status,Integer pageNumber) {
		Page<Driver> drivers = 
				driverService.searchDrivers(name, companyId, status, pageNumber);
		model.addAttribute("drivers", drivers);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchDriver(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model,
			HttpSession session) {
		logger.info("Searching drivers with the following search criterias: companyId: "
			+companyId+" name: "+name+" status: "+status);
		loadDrivers(model, name, companyId, status, pageNumber);
		return "DriverTable.jsp";
	}

	private void loadSelections(Model model, User user, Driver driver, boolean isForForm) {
		if(isForForm) {
			Integer companyId = driver.getCompanyId() != null ? driver.getCompanyId() : 0;
			Integer genderId = driver.getGenderId() != null ? driver.getGenderId() : 0;
			Integer civilStatus = driver.getCivilStatusId() != null ? driver.getCivilStatusId() : 0;

			//Company
			Collection<Company> companies = companyService.getCompaniesWithInactives(user, companyId);
			model.addAttribute("companies", companies);
			//Gender
			List<Gender> genders = genderDao.getAllWithInactive(genderId);
			model.addAttribute("genders", genders);
			//Civil Status
			List<CivilStatus> civilStatuses = civilStatusDao.getAllWithInactive(civilStatus);
			model.addAttribute("civilStatuses", civilStatuses);
		} else {
			model.addAttribute("searchSlctCompanies", companyService.getActiveCompanies(user, null, null, null));
		}
	}
}
