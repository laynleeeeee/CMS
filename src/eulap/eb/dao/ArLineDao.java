package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ArLine;

/**
 * Data-Access-Object of {@link ArLine}

 */
public interface ArLineDao extends Dao<ArLine>{

	/**
	 * Get the list of Ar Lines
	 * @param arTransactionId The unique AR transaction id.
	 * @return The list of Ar lines.
	 */
	List<ArLine> getArLines (int arTransactionId);

	/**
	 * Get the of Ar Lines
	 * @param arLineSetupId The unique AR line setup id.
	 * @return The list of Ar lines.
	 */
	List<ArLine> getArLinesBySetupId (int arLineSetupId);

	/**
	 * Get the total ar line amount.
	 * @param arTransactionId The transaction id.
	 * @return The total amount.
	 */
	public double getTotalArLineAmount (int arTransactionId);
}
