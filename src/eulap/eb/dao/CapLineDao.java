package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentLine;

/**
 * Data access object of {@link CustomerAdvancePaymentLine}

 *
 */
public interface CapLineDao extends Dao<CustomerAdvancePaymentLine>{
	/**
	 * Get the list of {@link CustomerAdvancePaymentLine}.
	 * @param capId The {@link CustomerAdvancePayment} id.
	 * @return The list of {@link CustomerAdvancePaymentLine} with formatted reference number.
	 */
	List<CustomerAdvancePaymentLine> getCapLines(Integer capId);
}
