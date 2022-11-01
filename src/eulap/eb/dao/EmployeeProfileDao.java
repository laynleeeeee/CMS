package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.web.dto.EmployeeFileDocumentDto;

/**
 * Data access object for {@link EmployeeProfile}

 *
 */
public interface EmployeeProfileDao extends Dao<EmployeeProfile>{

	/**
	 * Get the list of employees.
	 * @param companyId The company id.
	 * @param divisionId The employee division.
	 * @param status The employee status.
	 * @param asOfDate As of date.
	 * @param isOrderByLastName true if order by last name, else false
	 * @param pageSetting The page settings.
	 * @return The list of employees in paged format.
	 */
	Page<EmployeeProfile> getEmployeePerBranch(Integer companyId, Integer divisionId, SearchStatus status, 
			Date asOfDate, Boolean isOrderByLastName, PageSetting pageSetting);

	/**
	 * Get the employee profile by employee id.
	 * @param employeeId The employee filter.
	 * @return
	 */
	EmployeeProfile getByEmployee(Integer employeeId);

	/**
	 * Get the list of employees for regularization.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param renewalDate Renewal date of the employee.
	 * @param daysBeforeRenewal Number of days before renewal.
	 * @param isLastNameFirst true if last name first, else false
	 * @param pageSetting The page setting.
	 * @return The list of employees for regularization in paged format.
	 */
	Page<EmployeeProfile> getEmployeeForRegularization(Integer companyId, Integer divisionId,
			Date dateFrom, Date dateTo, Boolean isOrderByLastName,PageSetting pageSetting);

	/**
	 * Generate employee number.
	 * @param companyId The company id.
	 * @return employee number.
	 */
	Integer generateEmpNumber(Integer companyId);

	/**
	 * Get all newly created employee after parameter employeeId.
	 * @param employeeId The base employee id.
	 * @return The list of employee profiles.
	 */
	List<EmployeeProfile> getNewEmployee(Integer employeeId);

	/**
	 * Get the list of newly updated employees.
	 * @param employeeId The base employee Id.
	 * @param updatedDate The updated date.
	 * @return The list of employee profiles.
	 */
	List<EmployeeProfile> getNewlyUpdatedEmployee(Integer employeeId, Date updatedDate);

	/**
	 * Get the list of completed forms/documents by employee.
	 * @param employeeId the employee filter.
	 * @param formTypeId The type of form.
	 * @param pageSetting The page setting.
	 * @return The list of employee forms/documents.
	 */
	Page<EmployeeFileDocumentDto> getEmployeeFilesAndDocs (Integer employeeId, Integer formTypeId, PageSetting pageSetting);

	/**
	 * Check if the rfid is unique.
	 * @param rfid The RFID.
	 * @return True if the RFID is unique, otherwise false.
	 */
	boolean isUniqueRfid(EmployeeProfile employeeProfile);

	/**
	 * Check if employee number is unique.
	 * @param employeeProfile The employee profile object.
	 * @return True if the employee number is unique, otherwise false.
	 */
	boolean isUniqueEmployeeNo(EmployeeProfile employeeProfile);

	/**
	 * Get the list of employees for regularization by as of date.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param asOfDate The as of date.
	 * @param isLastNameFirst true if last name first, else false
	 * @param pageSetting The page setting.
	 * @return The list of employees for regularization in paged format.
	 */
	Page<EmployeeProfile> getEmployeeForRegularizationAsOfDate(Integer companyId, Integer divisionId,
			Date asOfDate, Boolean isOrderByLastName,PageSetting pageSetting);
}
