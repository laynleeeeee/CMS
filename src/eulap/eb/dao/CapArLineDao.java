package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CapArLine;

/**
 * Data Access Object for {@link CapArLine}

 *
 */
public interface CapArLineDao extends Dao<CapArLine>{

	/**
	 * Get the list of Customer Advance Payment AR Lines.
	 * @param capId The id of the customer advance payment.
	 * @return The list of Customer Advance Payment AR Lines.
	 */
	List<CapArLine> getCapArLines (int capId);

	/**
	 * Get the total customer advance total ar line amount.
	 * @param capId The customer advance payment id.
	 * @return The total amount.
	 */
	double getTotalCAPLineAmount (int capId);
}
