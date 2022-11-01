package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object for {@link EmployeeRequest}

 *
 */
public interface EmployeeRequestDao extends Dao<EmployeeRequest> {

	/**
	 * Generates sequence number for request for leave.
	 * @return The sequence number of the form.
	 */
	Integer generateSequenceNo(Integer typeId);

	/**
	 * Get the paged list of requests per type.
	 * @param typeId The type id.
	 * @param searchParam The search parameter.
	 * @param statuses Form status ids.
	 * @return The paged list of request per type.
	 */
	Page<EmployeeRequest> getRequests(int typeId, ApprovalSearchParam searchParam, List<Integer> statuses,
			PageSetting pageSetting);

	/**
	 * get the list of employee request based on the search criteria
	 * @param typeId The type id
	 * @param searchCriteria The search criteria
	 * @param user The user logged
	 * @return The list of employee request
	 */
	List<EmployeeRequest> searchEmployeeRequest(Integer typeId, String searchCriteria, User user);
}
