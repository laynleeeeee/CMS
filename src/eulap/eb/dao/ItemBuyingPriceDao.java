package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemBuyingPrice;


/**
 * Data access object for {@link ItemBuyingPrice}

 *
 */
public interface ItemBuyingPriceDao extends Dao<ItemBuyingPrice> {

	/**
	 * Get the latest buying price of the item.
	 * @param itemId The unique id of the item.
	 * @param companyId The id of the company.
	 * @return The {@link ItemBuyingPrice} object of the latest price.
	 */
	ItemBuyingPrice getLatestBuyingPrice(int itemId, int companyId);

	/**
	 * Get all the buying prices of the item.
	 * @param itemId The id of the item.
	 * @param isActiveOnly Set to true if retrieve only active buying prices.
	 * @return The list of {@link ItemBuyingPrice} of the item.
	 */
	List<ItemBuyingPrice> getBuyingPrices(int itemId, boolean isActiveOnly);
}
