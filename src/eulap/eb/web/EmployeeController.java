package eulap.eb.web;

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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeStatus;
import eulap.eb.domain.hibernate.EmployeeType;
import eulap.eb.domain.hibernate.SalaryType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EmployeeService;
import eulap.eb.service.SalaryTypeService;
import eulap.eb.validator.EmployeeValidator;

/**
 * Controller class that will handle request for {@link Employee} admin setting

 *
 */

@Controller
@RequestMapping("/admin/employee")
public class EmployeeController {
	private static final Logger logger = Logger.getLogger(EmployeeController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeValidator employeeValidator;
	@Autowired
	private SalaryTypeService salaryTypeService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	/**
	 * Load the employee main page form
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showMainPageForm(Model model, HttpSession session) {
		logger.info("Load the Employee admin setting main page.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		model.addAttribute("divisions", divisionService.getActiveDivisions(0));
		loadEmployees(model, "", null, -1, user, SearchStatus.All.name(), PageSetting.START_PAGE);
		return "Employee.jsp";
	}

	/**
	 * Load the employee form for adding or editing of employee info
	 */
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Employee employee = new Employee();
		if(pId != null) {
			logger.info("Loading employee with id: "+pId);
			employee = employeeService.getEmployeeForEditing(pId);
		} else {
			employee.setActive(true);
		}
		loadSelections(model, user, employee);
		model.addAttribute("employee", employee);
		return "EmployeeForm.jsp";
	}

	/**
	 * Validating and Saving the employee form.
	 */
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveEmployee(@ModelAttribute("employee") Employee employee,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		employeeValidator.validate(employee, result);
		if(result.hasErrors()) {
			logger.debug("Reloading the Employee Form");
			loadSelections(model, user, employee);
			return "EmployeeForm.jsp";
		}
		employeeService.saveEmployee(user, employee);
		logger.info("Successfully saved the Employee.");
		return "successfullySaved";
	}

	/**
	 * Search the employee
	 * @param name The name of the employee
	 * @param position The position of the employee
	 * @param pageNumber The page number
	 * @return The list employee in a paged table format
	 */
	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchEmployee(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="position", required=false) String position,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadEmployees(model, name, position, divisionId, user, status, pageNumber);
		return "EmployeeTable.jsp";
	}

	/**
	 * Load the list of employees
	 * @param name The name of the employee
	 * @param user The user currently log.
	 * @param positionId The positionId of the employee
	 * @param pageNumber The page number
	 * @return The list of employee in a paged format
	 */
	private void loadEmployees(Model model, String name, String position, int divisionId, 
			User user, String status,Integer pageNumber) {
		Page<Employee> employees = 
				employeeService.searchEmployees(name, position, divisionId, user, status, pageNumber);
		model.addAttribute("employees", employees);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	/**
	 * Load the list of companies, employee types and shift
	 * @param model The model
	 * @param user The current user logged
	 */
	public void loadSelections(Model model, User user, Employee employee) {
		Integer companyId = employee.getCompanyId() != null ? employee.getCompanyId() : 0;
		Integer divisionId = employee.getDivisionId() != null ? employee.getDivisionId() : 0;
		Integer employeeTypeId = employee.getEmployeeTypeId() != null ? employee.getEmployeeTypeId() : 0;
		Integer employeeStatusId = employee.getEmployeeStatusId() != null ? employee.getEmployeeStatusId() : 0;

		//Company
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, companyId);
		model.addAttribute("companies", companies);

		//Division
		Collection<Division> divisions = divisionService.getActiveDivisions(divisionId);
		model.addAttribute("divisions", divisions);

		//Employee type
		List<EmployeeType> employeeTypes = employeeService.getEmployeeTypes(employeeTypeId);
		model.addAttribute("employeeTypes", employeeTypes);

		//Employee status
		List<EmployeeStatus> employeeStatuses = employeeService.getEmployeeStatuses(employeeStatusId);
		model.addAttribute("employeeStatuses", employeeStatuses);

		//Salary Types
		List<SalaryType> salaryTypes = salaryTypeService.getAllActive();
		model.addAttribute("salaryTypes", salaryTypes);
	}
}
