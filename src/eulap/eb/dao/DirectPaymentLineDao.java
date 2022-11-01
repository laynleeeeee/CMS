package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.DirectPaymentLine;

/**
 * Data Access Object for {@link DirectPaymentLine}

 *
 */
public interface DirectPaymentLineDao extends Dao<DirectPaymentLine>{

	/**
	 * Get the list of {@link DirectPaymentLine} by Direct Payment ID.
	 * @param directPaymentId The Direct Payment ID.
	 * @param isActiveOnly Load only the active.
	 * @return the list of {@link DirectPaymentLine}
	 */
	List<DirectPaymentLine> getDirectPaymentLinesByDirectPaymentId(Integer directPaymentId, boolean isActiveOnly);

}
