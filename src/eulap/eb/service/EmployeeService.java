package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeSalaryDetailDao;
import eulap.eb.dao.EmployeeStatusDao;
import eulap.eb.dao.EmployeeTypeDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeSalaryDetail;
import eulap.eb.domain.hibernate.EmployeeStatus;
import eulap.eb.domain.hibernate.EmployeeType;
import eulap.eb.domain.hibernate.User;

/**
 * Service class that will handle business logic for {@link Employee}

 *
 */

@Service
public class EmployeeService {
	private static final Logger logger = Logger.getLogger(EmployeeService.class);
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private EmployeeSalaryDetailDao salaryDetailDao;
	@Autowired
	private EmployeeTypeDao employeeTypeDao;
	@Autowired
	private EmployeeStatusDao employeeStatusDao;

	/**
	 * Get the employee by id
	 * @param employeeId The employee id
	 * @return The employee object
	 */
	public Employee getEmployee(Integer employeeId) {
		return employeeDao.get(employeeId);
	}

	/**
	 * Get the employee salary detail by employee id
	 * @param employeeId The employee id
	 * @return The employee salary detail object
	 */
	public EmployeeSalaryDetail getSalaryDetailByEmployeeId(Integer employeeId) {
		return salaryDetailDao.getSalaryDetailByEmployeeId(employeeId);
	}

	/**
	 * Get employee and set the salary detail by employee id
	 * for editing the employee form
	 * @param employeeId The employee id
	 * @return The employee object;
	 */
	public Employee getEmployeeForEditing(Integer employeeId) {
		Employee employee = getEmployee(employeeId);
		employee.setSalaryDetail(getSalaryDetailByEmployeeId(employeeId));
		return employee;
	}

	/**
	 * Save the employee
	 * @param user The current user logged
	 * @param employee The employee object
	 */
	public Employee saveEmployee(User user, Employee employee) {
		logger.debug("Saving the customer type.");
		boolean isNewRecord = employee.getId() == 0; 
		AuditUtil.addAudit(employee, new Audit(user.getId(), isNewRecord, new Date()));
		employee.setFirstName(StringFormatUtil.removeExtraWhiteSpaces(employee.getFirstName(), null));
		employee.setMiddleName(StringFormatUtil.removeExtraWhiteSpaces(employee.getMiddleName(), null));
		employee.setLastName(StringFormatUtil.removeExtraWhiteSpaces(employee.getLastName(), null));
		employee.setContactNo(StringFormatUtil.removeExtraWhiteSpaces(employee.getContactNo(), null));
		employee.setAddress(StringFormatUtil.removeExtraWhiteSpaces(employee.getAddress(), null));
		employee.setEmailAddress(StringFormatUtil.removeExtraWhiteSpaces(employee.getEmailAddress(), null));
		employee.setEmployeeNo(StringFormatUtil.removeExtraWhiteSpaces(employee.getEmployeeNo(), null));
		employeeDao.saveOrUpdate(employee);

		EmployeeSalaryDetail salaryDetail = employee.getSalaryDetail();
		AuditUtil.addAudit(salaryDetail, new Audit(user.getId(), isNewRecord, new Date()));
		salaryDetail.setEmployeeId(employee.getId());
		salaryDetailDao.saveOrUpdate(salaryDetail);

		logger.info("Successfully saved the employee.");
		return employee;
	}

	/**
	 * Search employee
	 * @param name The name of the employee
	 * @param position The positionId of the employee
	 * @param divisionId The unique id of division.
	 * @param user The user currently log.
	 * @param pageNumber The page number
	 * @return The list of employee in a paged format
	 */
	public Page<Employee> searchEmployees(String name, String position, int divisionId, User user, String status,Integer pageNumber) {
		SearchStatus searchStatus = status != null ? SearchStatus.getInstanceOf(status) : null;
		Page<Employee> result = employeeDao.searchEmployees(name, position, divisionId, user, searchStatus,
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		for (Employee employee : result.getData()) {
			employee.setSalaryDetail(getSalaryDetailByEmployeeId(employee.getId()));
		}
		return result;
	}

	/**
	 * Get the list of active employee types
	 * @return The list of active employee types
	 */
	public List<EmployeeType> getAllActiveEmployeeTypes() {
		return employeeTypeDao.getAllActive();
		
	}

	/**
	 * Get the list of active employee status.
	 * @return The list of active employee status.
	 */
	public List<EmployeeStatus> getAllActiveEmployeeStatus () {
		return employeeStatusDao.getAllActive();
	}

	/**
	 * Check if biometric id is unique.
	 * @param employee The employee.
	 * @return True if biometric id is unique, otherwise false.
	 */
	public boolean isUniqueBiometricId(Employee employee) {
		return employeeDao.isUniqueBiometricId(employee);
	}

	/**
	 * Get the list of employee types and the selected employee type if inactive.
	 * @return The list of employee types.
	 */
	public List<EmployeeType> getEmployeeTypes(Integer employeeTypeId) {
		List<EmployeeType> employeeTypes = getAllActiveEmployeeTypes();
		if(employeeTypeId != 0) {
			Collection<Integer> employeeTypeIdIds = new ArrayList<Integer>();
			for (EmployeeType employeeType : employeeTypes) {
				employeeTypeIdIds.add(employeeType.getId());
			}
			if(!employeeTypeIdIds.contains(employeeTypeId)) {
				employeeTypes.add(employeeTypeDao.get(employeeTypeId));
			}
		}
		return employeeTypes;
	}

	/**
	 * Get the list of employee status and the selected employee status if inactive.
	 * @return The list of employee status.
	 */
	public List<EmployeeStatus> getEmployeeStatuses(Integer employeeStatusId) {
		List<EmployeeStatus> employeeStatus = getAllActiveEmployeeStatus();
		if(employeeStatusId != 0) {
			Collection<Integer> employeeTypeIdIds = new ArrayList<Integer>();
			for (EmployeeStatus employeeStat : employeeStatus) {
				employeeTypeIdIds.add(employeeStat.getId());
			}
			if(!employeeTypeIdIds.contains(employeeStatusId)) {
				employeeStatus.add(employeeStatusDao.get(employeeStatusId));
			}
		}
		return employeeStatus;
	}

	/**
	 * Get the employee type per employee type id.
	 * @param employeeTypeId The employee type id.
	 * @return The employee type.
	 */
	public EmployeeType getEmployeeType(Integer employeeTypeId) {
		return employeeTypeDao.get(employeeTypeId);
	}

	/**
	 * Get the employee status per employee status id.
	 * @param employeeStatusId The employee status id.
	 * @return The employee status.
	 */
	public EmployeeStatus getEmployeeStatus(Integer employeeStatusId) {
		return employeeStatusDao.get(employeeStatusId);
	}

	/**
	 * Check the employee no if it's already use.
	 * @param employee The employee.
	 * @return True if the employee no is unique, otherwise false.
	 */
	public boolean isUniqueEmployeeNo(Employee employee) {
		return employeeDao.isUniqueEmployeeNo(employee);
	}

	/**
	 * Evaluates whether the complete name of the employee is existing.
	 * @param companyId The company id.
	 * @param completeName The complete name of the employee.
	 * @return True if unique, otherwise false.
	 */
	public Boolean isUniqueEmployeeName(Integer companyId, String completeName) {
		return employeeDao.isUniqueEmployeeName(companyId, completeName);
	}

	/**
	 * Get the list of employees by name criteria
	 * @param companyId The company id
	 * @param name The name criteria
	 * @return The list of employees
	 */
	public List<Employee> getEmployeesByName(Integer companyId, String name) {
		return employeeDao.getEmployeesByName(companyId, name);
	}
}
