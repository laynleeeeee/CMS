package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.AcArLine;

/**
 * Data Access Object for {@link AcArLine}

 *
 */
public interface AcArLineDao extends Dao<AcArLine>{

	/**
	 * Get the AR line of AR receipt by arReceiptId
	 * @param arReceiptId The AR receipt id.
	 * @return The AR line of AR receipt
	 */
	List<AcArLine> getArReceipt(int arReceiptId);
}
