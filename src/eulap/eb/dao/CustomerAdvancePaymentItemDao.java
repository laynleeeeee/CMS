package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentItem;

/**
 * Data access object of {@link CustomerAdvancePaymentItem}

 *
 */
public interface CustomerAdvancePaymentItemDao extends Dao<CustomerAdvancePaymentItem>{

	/**
	 * Get the list of customer advance payment items.
	 * @param capId The customer advance payment id.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @return The list of customer advance payment items.
	 */
	public List<CustomerAdvancePaymentItem> getCAPItems (Integer capId, Integer itemId, Integer warehouseId);

	/**
	 * Get the total customer advance payment amount of all cash sale items per 
	 * customer advance payment.
	 * @param capId The customer advance payment id.
	 * @return The customer advance payment amount.
	 */
	public double getTotalCAPAmount (Integer capId);
}
