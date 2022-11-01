package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.EmployeeLeaveCredit;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data Access Object of {@link EmployeeLeaveCredit}

 *
 */
public interface EmployeeLeaveCreditDao extends Dao<EmployeeLeaveCredit>{

	/**
	 * Generates Sequence number.
	 * @return The generated Sequence Number of the form.
	 */
	Integer generateSequenceNo();

	/**
	 * Get the paged EmployeeLeaveCredits.
	 * @param param The search parameter.
	 * @return The paged collection of {@link EmployeeLeaveCredit}
	 */
	Page<EmployeeLeaveCredit> getEmployeeLeaveCredits(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the list of Employee Leave Credits by search Criteria.
	 * @param searchCriteria The search criteria.
	 * @param user The logged user.
	 * @return The list employee leave credits.
	 */
	List<EmployeeLeaveCredit> searchEmployeeLeaveCredits(String searchCriteria, User user);

}
