package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.LoanChargeTypeDao;
import eulap.eb.domain.hibernate.LoanChargeType;

/**
 * Service class that will handle business logic for {@link LoanChargeType}

 */

@Service
public class LoanChargeTypeService {
	@Autowired
	private LoanChargeTypeDao loanChargeTypeDao;

	/**
	 * Get all the list of active loan charge type id
	 * @param loanChargeTypeId The loan charge type id
	 * @return The list of active loan charge type id
	 */
	public List<LoanChargeType> getAllActiveLoanChargeTypes(Integer loanChargeTypeId) {
		return loanChargeTypeDao.getAllActiveLoanChargeTypes(loanChargeTypeId);
	}

}
