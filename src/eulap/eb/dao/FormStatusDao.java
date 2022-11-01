package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FormStatus;

/**
 * An interface class that defines the form status. 

 *
 */
public interface FormStatusDao extends Dao<FormStatus> {
	
	/**
	 * Get the status for payment.
	 * @return Payment statuses.
	 */
	List<FormStatus> getPaymentStatuses ();
}
