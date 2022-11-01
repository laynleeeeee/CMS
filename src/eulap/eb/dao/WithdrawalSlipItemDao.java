package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.WithdrawalSlipItem;

/**
 * Data Access Object of {@link WithdrawalSlipItem}

 *
 */
public interface WithdrawalSlipItemDao extends Dao<WithdrawalSlipItem>{

	/**
	 * Get all the list of withdrawal slip items.
	 * @param wsEbObjectId The withdrawal slip eb object.
	 * @param itemId thE item id.
	 * @return The list of withdrawal slip items.
	 */
	List<WithdrawalSlipItem> getAllActiveWsItems(Integer wsEbObjectId, Integer itemId);
}
