package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ArServiceLine;

/**
 * Data-Access-Object of {@link ArServiceLine}

 */
public interface ArServiceLineDao extends Dao<ArServiceLine>{

	/**
	 * Get the list of AR Service Lines
	 * @param arTransactionId The unique AR transaction id.
	 * @return The list of AR Service Lines.
	 */
	List<ArServiceLine> getArServiceLines (int arTransactionId);

	/**
	 * Get the of AR Service Lines
	 * @param arLineSetupId The unique AR line setup id.
	 * @return The list of AR Service Lines.
	 */
	List<ArServiceLine> getArServiceLinesBySetupId (int arServiceLineSetupId);

	/**
	 * Get the total ar line amount.
	 * @param arTransactionId The transaction id.
	 * @return The total amount.
	 */
	public double getTotalArServiceLineAmount (int arTransactionId);
}
