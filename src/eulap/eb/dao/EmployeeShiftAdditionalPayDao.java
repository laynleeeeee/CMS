package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EmployeeShiftAdditionalPay;

/**
 * Data Access Object of {@link EmployeeShiftAdditionalPayDao}

 *
 */

public interface EmployeeShiftAdditionalPayDao extends Dao<EmployeeShiftAdditionalPay>{

	/**
	 * Get the addition pay by employee shift.
	 * @param employeeShiftId The employee shift id.
	 * @return The additional pay.
	 */
	EmployeeShiftAdditionalPay getByShift (int employeeShiftId);
}
