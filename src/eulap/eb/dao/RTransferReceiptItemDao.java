package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RTransferReceiptItem;

/**
 * Data Access Object {@link RTransferReceiptItem}

 *
 */
public interface RTransferReceiptItemDao extends Dao<RTransferReceiptItem>{

	/**
	 * Get the list of Retail - TR Items.
	 * @param rTansferReceiptId The unique Retail - TR id.
	 * @param itemId The id of the item.
	 * @return The list of Retail - TR Items.
	 */
	List<RTransferReceiptItem> getRTrItems (int rTansferReceiptId, Integer itemId);
}
