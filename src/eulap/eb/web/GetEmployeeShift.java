package eulap.eb.web;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.service.EmployeeShiftService;

/**
 * Get employee shift Controller

 *
 */
@Controller
@RequestMapping("/getEmployeeShifts")
public class GetEmployeeShift {
	@Autowired
	private EmployeeShiftService employeeShiftService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getEmployeeShift (@RequestParam(value="companyId", required = false) Integer companyId,
			@RequestParam(value="employeeShiftId", required = false) Integer employeeShiftId) {
		List<EmployeeShift> employeeShifts = employeeShiftService.getEmployeeShifts(companyId, employeeShiftId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(employeeShifts, jConfig);
		return jsonArray.toString();
	}
}
