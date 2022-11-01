package eulap.eb.service.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import eulap.common.domain.Domain;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.domain.hibernate.BaseItem;
import eulap.eb.service.ItemService;
import eulap.eb.web.processing.dto.AvailableStock;

/**
 * Allocate the unit cost of the item using Individual Selection method.

 */
public class IndividualSelectionAllocator<T extends InventoryItem> {
	private static final Logger logger = Logger.getLogger(IndividualSelectionAllocator.class);
	private static final int TWO_DECIMALS = 2;
//	private static final int SIX_DECIMALS = 6;
	private Queue<AvailableStock> receivedStocks;
	private AvailableStock currentStockPerCost;
	private String stockCode;
	private final int itemId;

	/**
	 * First in, first out allocation for withdrawals.
	 * @param itemId The id of the item to be allocated.
	 * @param warehouseId The id of the warehouse for the stocks to be deducted.
	 * @param date The form date.
	 */
	public IndividualSelectionAllocator (ItemService itemService,
			int itemId, String stockCode, int warehouseId, boolean isSerialItem) {
		this.itemId = itemId;
		this.stockCode = stockCode;
		receivedStocks = new LinkedList<>();
		logger.info("Allocating the item: "+stockCode+" using the stocks from warehouse: "+warehouseId);
		// Allocate only items from individual selection type.
		List<AvailableStock> unusedStocks = itemService.getAvailableStocks(stockCode, warehouseId, null);
		receivedStocks.addAll(unusedStocks);
		logger.info("Added "+unusedStocks.size()+" available stocks to the list.");
		currentStockPerCost = receivedStocks.poll();
		if (currentStockPerCost != null) {
			logger.info("Current received stock is from "+currentStockPerCost.getSource()+
					" form with quantity "+currentStockPerCost.getQuantity());
			logger.info("Current Received stock details: "+currentStockPerCost);
		}
	}

	/*
	 * Recursive function that will allocate the cost of the items.
	 * This will handle 3 cases:
	 * 1. Positive received stocks
	 * 2. Negative beginning balance
	 * 3. total withdrawal is greater than the on hand items.
	 */
	private List<T> allocateCost (T item, List<T> splitItems) throws CloneNotSupportedException {
		// Special case for Return to Supplier since we don't need to allocate the items in it.
		// RTS reference the cost from its RR.
		if (item.isIgnore() || currentStockPerCost == null) { // disregard allocation if there are no available stocks.
			logger.info("This item will not be allocated.");
			if(currentStockPerCost == null) {
				logger.warn("No received stocks for this item. Setting unit and inventory cost to null.");
				item.setUnitCost(null);
				item.setInventoryCost(null);
			}
			splitItems.add(item);
			return splitItems;
		}

		double quantity = item.getQuantity();
		//Special case for stock adjusment out. 
		//Currently the quantity is save as NEGATIVE.
		boolean isNegative = quantity < 0;
		quantity = Math.abs(quantity);
		logger.debug("Quantity to be deducted: "+quantity);
		// withdrawn item is less than or equal the current stocks.
		if (quantity <= currentStockPerCost.getQuantity()) {
			logger.debug("Quantity to be withdrawn is less than the current received stock quantity.");
			logger.debug("Current received stock: "+currentStockPerCost);
//			item.setInventoryCost(currentStockPerCost.getInventoryCost());
			item.setUnitCost(currentStockPerCost.getUnitCost());
			item.setReceivedStockId(currentStockPerCost.getEbObjectId());
			double remaining = currentStockPerCost.getQuantity() - quantity;
			remaining = NumberFormatUtil.roundOffNumber(remaining, TWO_DECIMALS);
			if (remaining == 0 && !receivedStocks.isEmpty()) {
				logger.info("Retrieving the next received stock from the list.");
				currentStockPerCost = receivedStocks.poll();
				logger.info("Current received stock is from "+currentStockPerCost.getSource()
						+" form with quantity "+currentStockPerCost.getQuantity());
				logger.info("Current received stock details: "+currentStockPerCost);
			} else {
				currentStockPerCost.setQuantity(remaining);
			}
			logger.debug("Remaining quantity for the current received stock: "+remaining);
			splitItems.add(item);
		// If current stock is zero and there are no available received stocks, set the unit and inventory cost to null.
		} else if (currentStockPerCost.getQuantity() == 0 && receivedStocks.isEmpty()) {
			logger.warn("Received stocks are already exhausted.");
			item.setUnitCost(null);
			item.setInventoryCost(null);
			splitItems.add(item);
			return splitItems;
		} else {
			//Quantity to be withdrawn is greater than the current stock available.
			if (currentStockPerCost.getQuantity() > 0) {
				logger.debug("Item quantity to be allocated is greater than the quantity of the current stock.");
//				item.setInventoryCost(currentStockPerCost.getInventoryCost());
				item.setUnitCost(currentStockPerCost.getUnitCost());
				item.setReceivedStockId(currentStockPerCost.getEbObjectId());
				@SuppressWarnings("unchecked")
				T newItem = (T) item.clone();
				newItem.setQuantity(currentStockPerCost.getQuantity());

				if (isNegative){
					newItem.setQuantity(newItem.getQuantity() * -1);
				}

				if (newItem instanceof Domain) {
					Domain domain = (Domain) newItem;
					domain.setId(0);
				}
				logger.debug("Current received stock can only allocate "+currentStockPerCost.getQuantity());
				logger.warn("Remaining quantity for current received stock is zero.");
				splitItems.add(newItem);

				double remaining = quantity - currentStockPerCost.getQuantity();
				remaining = NumberFormatUtil.roundOffNumber(remaining, TWO_DECIMALS);
				item.setQuantity(remaining);
				if (isNegative){
					item.setQuantity(item.getQuantity() * -1);
				}
				logger.debug("Remaining quantity to be withdrawn is: "+remaining);
				if (receivedStocks.isEmpty()) { // If there are no available received stocks, return the remaining item.
					item.setInventoryCost(null);
					item.setUnitCost(null);
					currentStockPerCost.setQuantity(0.0); // Set to zero, to notify that there are no available stocks.
					logger.warn("Received stocks are all exhausted, quantity is set to zero.");
					splitItems.add(item);
					return splitItems;
				}

				currentStockPerCost = receivedStocks.poll();
				logger.info("Get the next available received stock from the list.");
				// If item is less than or equal to or item is greater than the received stocks
				if (quantity <= currentStockPerCost.getQuantity() ||
						quantity > currentStockPerCost.getQuantity()) {
					// the current available stocks, reallocate the item.
					logger.info("Re-allocating the item.");
					logger.trace("Remaining quantity to be allocated: "+quantity);
					return allocateCost(item, splitItems);
				}
			} else if (currentStockPerCost.getQuantity() <= 0) {
				// Negative existing stocks.
				// Rare case but possible scenario.
				double currentStockQuantity = currentStockPerCost.getQuantity();
				logger.warn("Quantity for current received stock has reached zero or is negative.");
				while (!receivedStocks.isEmpty()) {
					logger.info("Get the next received stock from the list.");
					currentStockPerCost = receivedStocks.poll();
					logger.trace("Current received stock details: "+currentStockPerCost);
					if (currentStockPerCost.getQuantity() > currentStockQuantity) {
						double remainingStockQuantity = currentStockPerCost.getQuantity() + currentStockQuantity;
						remainingStockQuantity = NumberFormatUtil.roundOffNumber(remainingStockQuantity, TWO_DECIMALS);
						currentStockPerCost.setQuantity(remainingStockQuantity);
						logger.trace("Remaining quantity for the current received stock: "+remainingStockQuantity);
						logger.info("Re-allocating the item for the quantity: "+item.getQuantity());
						return allocateCost(item, splitItems); // reallocate item if there are still remaining stocks.
					}
					currentStockQuantity += currentStockPerCost.getQuantity();
				}
			}

			while (!receivedStocks.isEmpty()) {
				logger.info("Get the next received stock from the list.");
				currentStockPerCost = receivedStocks.poll();
				allocateAndCollectItem(item, splitItems);
			}
		}
		return splitItems;
	}

	/**
	 * Compute and allocate the cost of the item based on the received items. Set
	 * InventoryItem.isIgnore to true to ignore the allocation.
	 * @param item The item to be allocated.
	 * @return The allocated items.
	 * @throws CloneNotSupportedException Throw error if the class does not support cloning.
	 */
	public List<T> allocateCost (T item) throws CloneNotSupportedException {
		logger.info(" =========>>>>> Allocating the item: "+stockCode);
		List<T> ret = new ArrayList<T>();
		List<T> splitItems = allocateCost(item, ret);
		return splitItems;
	}

	private void allocateAndCollectItem (T item, List<T> collector) throws CloneNotSupportedException {
		double quantityToBeWithdrawn = item.getQuantity() - currentStockPerCost.getQuantity();
		quantityToBeWithdrawn = NumberFormatUtil.roundOffNumber(quantityToBeWithdrawn, TWO_DECIMALS);
		if (quantityToBeWithdrawn <= currentStockPerCost.getQuantity()) {
			@SuppressWarnings("unchecked")
			T newItem = (T)item.clone();
			logger.info("Split price. Cloning the item.");
			newItem.setQuantity(quantityToBeWithdrawn);
			newItem.setReceivedStockId(currentStockPerCost.getEbObjectId());
//			newItem.setInventoryCost(currentStockPerCost.getInventoryCost());
			newItem.setUnitCost(NumberFormatUtil.roundOffTo2DecPlaces(currentStockPerCost.getUnitCost()));
			double remaining = currentStockPerCost.getQuantity() - newItem.getQuantity();
			remaining = NumberFormatUtil.roundOffNumber(remaining, TWO_DECIMALS);
			currentStockPerCost.setQuantity(remaining);
			collector.add(newItem);
			return;
		}
		@SuppressWarnings("unchecked")
		T newItem = (T)item.clone();
		newItem.setQuantity(currentStockPerCost.getQuantity());
		newItem.setReceivedStockId(currentStockPerCost.getEbObjectId());
//		newItem.setInventoryCost(currentStockPerCost.getInventoryCost());
		newItem.setUnitCost(NumberFormatUtil.roundOffNumber(currentStockPerCost.getUnitCost(), TWO_DECIMALS));
		quantityToBeWithdrawn -= currentStockPerCost.getQuantity();
		quantityToBeWithdrawn = NumberFormatUtil.roundOffNumber(quantityToBeWithdrawn, TWO_DECIMALS);
		collector.add(newItem);
	}

	public int getItemId() {
		return itemId;
	}

	/**
	 * An interface for special parameter for this allocator

	 *
	 */
	public interface ISAllocatorHandler {
		/**
		 * Get the associated warehouse of this item.
		 */
		int getWarehouse (BaseItem item);
		/**
		 * This can be used by the consumer/caller for a special handling when the reference object is identified.
		 */
		void handleAllocatedItems (BaseItem item, Integer objectId);
	}

	/**
	 * Allocate the base items by FIFO.
	 */
	public static void allocateFIFO (List<? extends BaseItem> items, ItemService itemService, ISAllocatorHandler handler) throws CloneNotSupportedException {
		Map<Integer, IndividualSelectionAllocator<BaseItem>> itemId2CostAllocator =
				new HashMap<Integer, IndividualSelectionAllocator<BaseItem>>();

		for (BaseItem item : items) {
			IndividualSelectionAllocator<BaseItem> itemAllocator = itemId2CostAllocator
					.get(item.getItemId());
			if (itemAllocator == null) {
				int warehouseId = handler.getWarehouse(item);
				itemAllocator = new IndividualSelectionAllocator<>(itemService,
						item.getItemId(), item.getStockCode().trim(),
						warehouseId, false);
			}

			List<BaseItem> allocatedTrItems = itemAllocator.allocateCost(item);
			for (BaseItem allocRm : allocatedTrItems) {
				logger.debug("Allocated items: "+allocRm);
				handler.handleAllocatedItems(allocRm, allocRm.getReceivedStockId());
				logger.debug("REF OBJECT ID: " + allocRm.getReceivedStockId());
			}

		}
	}
}
