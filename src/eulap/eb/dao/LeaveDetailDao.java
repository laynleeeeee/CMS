package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.LeaveDetail;

/**
 * Data access object for {@link LeaveDetail}

 *
 */

public interface LeaveDetailDao extends Dao<LeaveDetail> {

	/**
	 * Get the leave detail object by employee request id
	 * @param employeeRequestId The employee request id
	 * @return
	 */
	LeaveDetail getLeaveDetailByRequestId(Integer employeeRequestId);

	/**
	 * Check if the employee has leave.
	 * @param employeeId The employee filter.
	 * @param date The date filter.
	 * @return True if has approved leave, other false.
	 */
	boolean hasLeave(Integer employeeId, Date date);

	/**
	 * Get the leave detail by employee and date.
	 * @param employeeId The employee filter.
	 * @param date The date filter.
	 * @return The leave detail object.
	 */
	LeaveDetail getByEmployeeAndDate(Integer employeeId, Date date);

	/**
	 * Get the list of the latest employee leaves.
	 * @param updatedDate The updated date.
	 * @return The list of employee leaves.
	 */
	List<LeaveDetail> getEmployeeLeavesByFormDate(Date updatedDate);
}
