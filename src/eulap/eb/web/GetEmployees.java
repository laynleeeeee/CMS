package eulap.eb.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EmployeeLeaveCreditService;
import eulap.eb.service.EmployeeProfileService;
import eulap.eb.service.EmployeeRequestService;
import eulap.eb.service.EmployeeService;
import eulap.eb.service.PositionService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * A controller class that retrieve the {@link Employee} object/s

 *
 */
@Controller
@RequestMapping("/getEmployees")
public class GetEmployees {
	@Autowired
	private EmployeeLeaveCreditService employeeLeaveCreditService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private EmployeeRequestService employeeRequestService;
	@Autowired
	private EmployeeProfileService employeeProfileService;
	@Autowired
	private EmployeeService employeeService;

	@RequestMapping(method = RequestMethod.GET, value = "/byName")
	public @ResponseBody String getEmployeesByName(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="name", required=false) String name, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Employee> employees = employeeLeaveCreditService.getEmployees(companyId,divisionId, user, name);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(employees, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/bycCompanyAndName")
	public @ResponseBody String getEmployee(@RequestParam(value = "companyId", required = false) Integer companyId,
			@RequestParam(value = "name", required = false) String name){
		List<Employee> employees = employeeRequestService.getEmployees(companyId, name);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(employees, jConfig);
		return jsonArray.toString();
	}

	/**
	 * Get the available leave credits for employee
	 * @param employeeId The employee id
	 * @param typeOfLeaveId The type of leave id
	 * @return The available leave credits for employee
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getAvailableLeave")
	public @ResponseBody String getAvailableLeave(@RequestParam(value = "employeeId", required = false) Integer employeeId,
			@RequestParam(value = "typeOfLeaveId", required = false) Integer typeOfLeaveId,
			@RequestParam(value = "isRequestForm", required = false) Boolean isRequestForm) {
		Double availableLeave = 0.0;
		if(typeOfLeaveId != null && typeOfLeaveId != 0){
			availableLeave = employeeLeaveCreditService.getAvailableLeaves(employeeId, typeOfLeaveId, isRequestForm, false);
		}
		return availableLeave.toString();
	}

	/**
	 * Get the employee position
	 * @param employeeId The employee id
	 * @return The employee position
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getPosition")
	public @ResponseBody String getPosition(@RequestParam(value = "employeeId", required = false) Integer employeeId) {
		Position position = positionService.getEmployeePosition(employeeId);
		JsonConfig jConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(position, jConfig);
		return jsonObject.toString();
	}

	/**
	 * Get the employee division.
	 * @param employeeId The employee id.
	 * @return The employee's division.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getDivision")
	public @ResponseBody String getDivision(@RequestParam(value = "employeeId", required = false) Integer employeeId) {
		Division division = divisionService.getEmployeeDivision(employeeId);
		JsonConfig jConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(division, jConfig);
		return jsonObject.toString();
	}

	/**
	 * Get the employee profile
	 * @param employeeId The employee id.
	 * @return The employee's employee profile.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getHiredDate")
	public @ResponseBody String getHiredDate(@RequestParam(value = "employeeId", required = false) Integer employeeId) {
		EmployeeProfile employeeProfile = employeeProfileService.getEmployeeProfile(employeeId);
		if (employeeProfile != null && employeeProfile.getHiredDate() != null) {
			return DateUtil.formatDate(employeeProfile.getHiredDate());
		}
		return "";
	}

	/**
	 * Calculate the age of the employee
	 * @param birthDate The birth date of the employee
	 * @return The computed age for employee
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/computeAge")
	public @ResponseBody String computeAge(@RequestParam(value = "birthDate", required = false) Date birthDate) {
		Integer age = 0;
		if (birthDate != null) {
			age = DateUtil.computeAge(birthDate);
			if (age < 0) {
				age = 0;
			}
		}
		return age.toString();
	}

	@RequestMapping(method=RequestMethod.GET, value="/fromSettings")
	public @ResponseBody String getEmployees(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="name", required=false) String name,
			HttpSession session){
		List<Employee> employees = employeeService.getEmployeesByName(companyId, name);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(employees, jConfig);
		return jsonArray.toString();
	}
}