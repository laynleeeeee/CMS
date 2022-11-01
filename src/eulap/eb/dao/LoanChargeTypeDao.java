package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.LoanChargeType;

/**
 * Data access object class for {@link LoanChargeType}

 */

public interface LoanChargeTypeDao extends Dao<LoanChargeType> {

	/**
	 * Get all the list of active loan detail type id
	 * @param loanDetailTypeId The loan detail type id
	 * @return The list of active loan detail type id
	 */
	List<LoanChargeType> getAllActiveLoanChargeTypes(Integer loanDetailTypeId);

}
