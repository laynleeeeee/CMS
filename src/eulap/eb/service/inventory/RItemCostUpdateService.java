package eulap.eb.service.inventory;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemDao;
import eulap.eb.service.ItemService;
import eulap.eb.web.dto.ItemTransaction;

/**
 * Business logic that will update the allocation of quantity and unit cost
 * of the when forms are inserted/cancelled.

 *
 */
@Service
public class RItemCostUpdateService {
	private static Logger logger = Logger.getLogger(RItemCostUpdateService.class);
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private BaseUnitCostUpdateHandler unitCostUpdateHandler;
	/**
	 * Update Item unit cost and quantity.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @param formDate The start date of the re-allocation.
	 */
	public void updateItemUnitCost(int itemId, int warehouseId, Date formDate) {
		updateItemUnitCost(itemId, warehouseId, formDate, true);
	}

	/**
	 * Update Item unit cost and quantity.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @param isAllocateRpTo Set to true if allocate Repacking To items as well, otherwise false.
	 * @param formDate The form date.
	 */
	public void updateItemUnitCost(int itemId, int warehouseId, Date formDate, boolean isAllocateRpTo) {
		logger.info("Updating the unit cost of the forms that will be affected by the insert/cancel forms.");
		PageSetting ps = new PageSetting(1);
		Page<ItemTransaction> itemTransactions = itemDao.getItemTransaction(itemId, warehouseId, formDate, ps);
		if(itemTransactions.getData().isEmpty()) {
			logger.info("No recomputations for unit cost for item id: "+itemId+" on warehouse: "+warehouseId);
			return;
		}
		WeightedAveItemAllocator<ItemTransaction> fifoAllocator = new WeightedAveItemAllocator<ItemTransaction>(itemDao, itemService, itemId, warehouseId, formDate);
		int currentPage = ps.getPageNumber();
		logger.info("Recomputing "+itemTransactions.getTotalRecords()+" transactions for item id: "+itemId);
		while(currentPage <= itemTransactions.getLastPage()) {
			logger.debug("Re-allocate item transactions on page: "+currentPage);
			if(currentPage > 1) {
				itemTransactions = itemDao.getItemTransaction(itemId, warehouseId, formDate, new PageSetting(currentPage));
			}

			if(!itemTransactions.getData().isEmpty()) {
				for (ItemTransaction it : itemTransactions.getData()) {
					it.setQuantity(Math.abs(it.getQuantity()));
					// Allocate Transactions
					unitCostUpdateHandler.updateUnitCost(this, fifoAllocator, it, itemId, warehouseId, formDate, isAllocateRpTo);
				}
				currentPage++;
			}
		}
	}

	
}
