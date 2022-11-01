package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CAPDeliveryItem;

/**
 * DAO Layer of {@link CAPDeliveryItem} object.

 *
 */
public interface CAPDeliveryItemDao extends Dao<CAPDeliveryItem>{

	/**
	 * Get the list of CAP Delivery Items to be re-allocated.
	 * @param capDeliveryId The unique id of the CAP Delivery.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @return The list of {@link CAPDeliveryItem}
	 */
	public List<CAPDeliveryItem> getDeliveryItems(int capDeliveryId, int itemId, int warehouseId);

	/**
	 * Get the list of CAP Delivery Items by the reference CAP Id.
	 * @param capDeliveryId The id of the CAP Delivery.
	 * @param customerAdvPaymentId The reference id.
	 * @return The list of {@link CAPDeliveryItem}
	 */
	public double getTotalDeliveredAmt(int capDeliveryId, int customerAdvPaymentId);
}
