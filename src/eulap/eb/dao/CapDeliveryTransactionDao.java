package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CapDeliveryTransaction;

/**
 * Data Access Object of {@link CapDeliveryTransaction}

 *
 */
public interface CapDeliveryTransactionDao extends Dao<CapDeliveryTransaction>{

	/**
	 * Get the CapDeliveryTransaction by CAP DELIVERY ID.
	 * @param capDeliveryId The CAP DELIVERY ID.
	 * @return The CapDeliveryTransaction object.
	 */
	CapDeliveryTransaction getCapdTransaction( Integer capDeliveryId);

}
