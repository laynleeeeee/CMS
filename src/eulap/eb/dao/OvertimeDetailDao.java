package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.OvertimeDetail;

/**
 * Data access object for {@link OvertimeDetail}

 *
 */
public interface OvertimeDetailDao extends Dao<OvertimeDetail> {
	/**
	 * Get the overtime detail object by employee request id.
	 * @param employeeRequestId The employee request id.
	 * @return The overtime detail object.
	 */
	OvertimeDetail getOvertimeDetailByRequestId(Integer employeeRequestId);

	/**
	 * Get the overtime detail object by employee and date.
	 * @param employeeId The employee filter.
	 * @param date The date filter.
	 * @return The overtime detail object.
	 */
	OvertimeDetail getByEmployeeAndDate(Integer employeeId, Date date, boolean isComplete);

	/**
	 * Check if the requested overtime for the date exists
	 * @param pId The form id
	 * @param employeeId The employee id
	 * @param overtimeDate The overtime date
	 * @return True if the requested overtime for the date exists, otherwise false
	 */
	boolean hasRequestedOvertime(Integer pId, Integer employeeId, Date overtimeDate);

	/**
	 * Get the list of overtime details object by employee and date.
	 * @param employeeId The employee filter.
	 * @param date The date filter.
	 * @return The overtime detail object.
	 */
	List<OvertimeDetail> getAllByEmployeeAndDate(Integer employeeId, Date date, boolean isComplete);
}
