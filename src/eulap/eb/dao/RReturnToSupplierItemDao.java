package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
/**
 * Data access object of {@link RReturnToSupplierItem}

 */
public interface RReturnToSupplierItemDao extends Dao<RReturnToSupplierItem>{

	/**
	 * Get the Retail - RTS Items.
	 * @param apInvoiceId The id of the AP Invoice.
	 * @return The list of Retail - RTS Items.
	 */
	List<RReturnToSupplierItem> getRtsItems(int apInvoiceId);

	/**
	 * Get the Retail - RTS Items by invoice and item.
	 * @param apInvoiceId The id of the AP Invoice.
	 * @param itemId The item id.
	 * @return The list of Retail - RTS Items.
	 */
	List<RReturnToSupplierItem> getRtsItems(int apInvoiceId, int itemId);

	/**
	 * Get the list of Retail - RTS Items.
	 * @param isCompleted Set to true if only completed RTS forms, otherwise false.
	 * @param rrItemId The id of the RR Item id.
	 * @return The list of Retail - RTS Items.
	 */
	List<RReturnToSupplierItem> getRtsItemsByRrItem(boolean isCompleted, int rrItemId);
}
