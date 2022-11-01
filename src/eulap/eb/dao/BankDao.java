package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.Bank;

/**
 * Data access object interface for {@link Bank}

 */
 
public interface BankDao extends Dao<Bank> {

	/**
	 * Get the list of banks
	 * @param bankId The bank id
	 * @return The list of banks
	 */
	List<Bank> getBanks(Integer bankId);
}
