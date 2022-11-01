package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EmployeeLeaveCreditLine;
import eulap.eb.web.dto.AvailableLeavesDto;

/**
 *  Data Access Object of {@link EmployeeLeaveCreditLine}

 *
 */
public interface EmployeeLeaveCreditLineDao extends Dao<EmployeeLeaveCreditLine>{

	/**
	 * Get the Available Leaves of the Employee
	 * @param employeeId The unique identifier of the Employee
	 * @param typeOfLeaveId The unique identifier of the Type of the Leave.
	 * @param isForPrintOut True if get all the available balance for printout, otherwise false
	 * @return The number of the available leaves.
	 */
	Double getAvailableLeaves(Integer employeeId, Integer typeOfLeaveId,
			boolean isForPrintOut, boolean isLeaveEarned);

	/**
	 * Get the list of EmployeeLeaveCreditLine by eb object id.
	 * @param ebObjectId The Eb object Id.
	 * @return The list of EmployeeLeaveCreditLine.
	 */
	List<EmployeeLeaveCreditLine> getEmployeeLeaveCreditLineByEbObject(Integer ebObjectId, Integer employeeId);

	/**
	 * Get the list of latest employee available leaves.
	 * @param erUpdatedDate The updated date of employee request.
	 * @param elsUpdatedDate The updated date of leave credits.
	 * @return The list of available leaves.
	 */
	List<AvailableLeavesDto> getEmpAvailableLeaves(Date erUpdatedDate, Date elsUpdatedDate);
}
