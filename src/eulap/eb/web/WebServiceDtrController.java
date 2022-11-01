package eulap.eb.web;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.common.dto.LoginCredential;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.EmployeeDtrService;
import eulap.eb.service.EmployeeProfileService;
import eulap.eb.service.UserService;
import eulap.eb.web.dto.ActionNoticeDto;
import eulap.eb.web.dto.AvailableLeavesDto;
import eulap.eb.web.dto.DTRDailyShiftScheduleDto;
import eulap.eb.web.dto.DTRMonthlyScheduleDto;
import eulap.eb.web.dto.EmployeeLeaveDto;
import eulap.eb.web.dto.EmployeeShiftDto;
import eulap.eb.web.dto.WebEmployeeProfileDto;

/**
 * Controller class that handled dtr webservice.

 *
 */
@Controller
@RequestMapping("/webservice/employeeDtr")
public class WebServiceDtrController {
	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeDtrService employeeDtrService;
	@Autowired
	private EmployeeProfileService employeeProfileService;

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String updateEmployeeDTR(@RequestParam(value="userName") String userName, 
			@RequestParam(value="password") String password, 
			@RequestParam(value="employeeId") Integer employeeId,
			@RequestParam(value="locationId") Integer locationId,
			@RequestParam(value="logTime") String logTime,
			HttpSession session) throws NoSuchAlgorithmException, ParseException, NullPointerException {
		LoginCredential loginCredential = new LoginCredential();
		loginCredential.setUserName(userName);
		loginCredential.setPassword(password);

		User user = userService.getUser(loginCredential);
		if (user == null) {
			throw new RuntimeException("Invalid credentials.");
		}
		employeeDtrService.save(employeeId, logTime, locationId);
		System.out.println("Succeeded to access with credentials:\n" + "Username: " + userName);
		return "Succeeded to access with credentials:\n" + "Username: " + userName;
	}

	@RequestMapping(value="getTobeCreated", method=RequestMethod.GET)
	public @ResponseBody String getEmployeesDTR(@RequestParam(value="employeeId") Integer employeeId,
			HttpServletResponse response, HttpSession session) throws NoSuchAlgorithmException, ParseException, NullPointerException, IOException {
		List<WebEmployeeProfileDto> employeeProfiles = employeeProfileService.getEmployees(employeeId, null);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(employeeProfiles);
		} finally {
			if (oos != null) {
				oos.close();
			} 
		}
		return "";
	}

	@RequestMapping(value="getTobeUpdated", method=RequestMethod.GET)
	public @ResponseBody String getEmployeesDTRTobeUpdate(@RequestParam(value="employeeId") Integer employeeId,
			@RequestParam(value="updatedDate") String updatedDate,
			HttpServletResponse response, HttpSession session) throws NoSuchAlgorithmException, ParseException, NullPointerException, IOException {
		updatedDate = updatedDate.replaceAll(";", " ");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<WebEmployeeProfileDto> employeeProfiles = employeeProfileService.getEmployees(employeeId, format.parse(updatedDate));
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(employeeProfiles);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
		return "";
	}

	@RequestMapping(value="getEmployeeAvailableLeaves", method=RequestMethod.GET)
	public @ResponseBody String getEmployeeAvailableLeaves (@RequestParam(value="erUpdatedDate") String erUpdatedDate,
			@RequestParam(value="elsUpdatedDate") String elsUpdatedDate,
			HttpServletResponse response, HttpSession session) throws NoSuchAlgorithmException, ParseException, NullPointerException, IOException {
		elsUpdatedDate = elsUpdatedDate.replaceAll(";", " ");
		erUpdatedDate = erUpdatedDate.replaceAll(";", " ");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<AvailableLeavesDto> availableLeavesDtos = employeeDtrService.getLatestAvailableLeaves(erUpdatedDate != null && !erUpdatedDate.equals("null") ? format.parse(erUpdatedDate) : null,
				elsUpdatedDate != null && !elsUpdatedDate.equals("null")? format.parse(elsUpdatedDate) : null);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(availableLeavesDtos);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
		return "";
	}

	@RequestMapping(value="getLatestDailyShifts", method=RequestMethod.GET)
	public @ResponseBody String getLatestDailyShifts(@RequestParam(value="updatedDate") String updatedDate,
			HttpServletResponse response, HttpSession session) throws NoSuchAlgorithmException, ParseException, NullPointerException, IOException {
		updatedDate = updatedDate.replaceAll(";", " ");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<DTRDailyShiftScheduleDto> shiftScheduleDtos = employeeDtrService.getLatestDailyShifts(updatedDate != null && !updatedDate.equals("null") ?
				format.parse(updatedDate) : null);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(shiftScheduleDtos);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
		return "";
	}

	@RequestMapping(value="getActionNotices", method=RequestMethod.GET)
	public @ResponseBody String getActionNotices(@RequestParam(value="updatedDate") String updatedDate,
			HttpServletResponse response, HttpSession session) throws NoSuchAlgorithmException, ParseException, NullPointerException, IOException {
		updatedDate = updatedDate.replaceAll(";", " ");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<ActionNoticeDto> actionNoticeDtos = employeeDtrService.getActionNotices(updatedDate != null && !updatedDate.equals("null") ?
				format.parse(updatedDate) : null);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(actionNoticeDtos);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
		return "";
	}

	@RequestMapping(value="getLatestShifts", method=RequestMethod.GET)
	public @ResponseBody String getLatestShifts(@RequestParam(value="updatedDate") String updatedDate,
			HttpServletResponse response, HttpSession session) throws NoSuchAlgorithmException, ParseException, NullPointerException, IOException {
		updatedDate = updatedDate.replaceAll(";", " ");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<EmployeeShiftDto> employeeShiftDtos = employeeDtrService.getLatestShifts(updatedDate != null && !updatedDate.equals("null") ?
				format.parse(updatedDate) : null);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(employeeShiftDtos);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
		return "";
	}

	@RequestMapping(value="getEmployeeLeaves", method=RequestMethod.GET)
	public @ResponseBody String getEmployeeLeaves(@RequestParam(value="updatedDate") String updatedDate,
			HttpServletResponse response, HttpSession session) throws NoSuchAlgorithmException, ParseException, NullPointerException, IOException {
		updatedDate = updatedDate.replaceAll(";", " ");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<EmployeeLeaveDto> employeeLeaveDtos = employeeDtrService.getEmployeeLeaves(updatedDate != null && !updatedDate.equals("null") ?
				format.parse(updatedDate) : null);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(employeeLeaveDtos);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
		return "";
	}

	@RequestMapping(value="getLatestMonthlyShed", method=RequestMethod.GET)
	public @ResponseBody String getLatestMonthlyShed(@RequestParam(value="updatedDate") String updatedDate,
			@RequestParam(value="updatedPayrollPeriodDate") String updatedPayrollPeriodDate,
			HttpServletResponse response, HttpSession session) throws NoSuchAlgorithmException, ParseException, NullPointerException, IOException {
		updatedDate = updatedDate.replaceAll(";", " ");
		updatedPayrollPeriodDate = updatedPayrollPeriodDate.replaceAll(";", " ");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<DTRMonthlyScheduleDto> shiftScheduleDtos = employeeDtrService.getLatestMonthlySheds(updatedDate != null && !updatedDate.equals("null") ?
				format.parse(updatedDate) : null, updatedPayrollPeriodDate != null && !updatedPayrollPeriodDate.equals("null") ? format.parse(updatedPayrollPeriodDate) : null);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(shiftScheduleDtos);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
		return "";
	}
}
