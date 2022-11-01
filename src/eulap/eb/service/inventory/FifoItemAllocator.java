package eulap.eb.service.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemDao;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.service.ItemService;
import eulap.eb.web.processing.dto.AvailableStock;

/**
 * Allocate the unit cost of the item using First In, First Out method.

 */
public class FifoItemAllocator<T extends InventoryItem> {
	private final Logger logger = Logger.getLogger(FifoItemAllocator.class);
	private static final int SIX_DECIMALS = 6;
	private Queue<ReceivedStock> receivedStocks;
	private ReceivedStock currentStockPerCost;
	private final int itemId;

	/**
	 * First in, first out allocation for withdrawals.
	 * @param itemId The id of the item to be allocated.
	 * @param warehouseId The id of the warehouse for the stocks to be deducted.
	 * @param date The form date.
	 */
	public FifoItemAllocator (ItemDao itemDao, ItemService itemService, int itemId, int warehouseId, Date date) {
		this.itemId = itemId;
		receivedStocks = new LinkedList<ReceivedStock>();
		logger.debug("Allocating the item: "+itemId+" using the stocks from warehouse: "+warehouseId);
		//Get unused stocks as of the date. This is to handle insert transactions.
		// List<ReceivedStock> unusedStocks = new ArrayList<>();
		// unusedStocks.add(itemDao.getAvailableStocks(itemId, companyId, warehouseId, date));
		Date currentDate = new Date();
 		List<ReceivedStock> unusedStocks = itemService.getItemUnusedStocks(itemId, warehouseId, currentDate);
		logger.debug(unusedStocks);
		receivedStocks.addAll(unusedStocks);
		logger.debug("Added "+unusedStocks.size()+" unused stocks to the list, as of "+DateUtil.formatDate(date));
		PageSetting ps = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		//Retrieve and add the received stocks after the date.
		// Collection<ReceivedStock> descRecStocks = new ArrayList<>();
		// descRecStocks.add(itemDao.getReceivedStocksFuture(itemId, companyId, warehouseId, date));
		Collection<ReceivedStock> descRecStocks = itemDao.getItemReceivedStocksAfterDate(itemId, warehouseId, currentDate, ps).getData();
		logger.debug("Retrieved "+descRecStocks.size()+" received stocks after the date "+DateUtil.formatDate(date));
		List<ReceivedStock> orderRecStocks = new ArrayList<ReceivedStock>(descRecStocks);
		Collections.reverse(orderRecStocks);
		logger.trace("Successfully reversed the order of the received stocks.");
		receivedStocks.addAll(orderRecStocks);
		currentStockPerCost = receivedStocks.poll();
		if (currentStockPerCost != null) {
			logger.info("Current received stock is from "+currentStockPerCost.getForm()+
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
	private List<T> allocateCost (T item, List<T> ret) throws CloneNotSupportedException {
		// Special case for Return to Supplier since we don't need to allocate the items in it.
		// RTS reference the cost from its RR.
		if (item.isIgnore() || currentStockPerCost == null) { // disregard allocation if there are no available stocks.
			logger.info("This item will not be allocated.");
			if(currentStockPerCost == null) {
				logger.warn("No received stocks for this item. Setting unit and inventory cost to null.");
				item.setUnitCost(null);
				item.setInventoryCost(null);
			}
			ret.add(item);
			return ret;
		}

		double quantity = item.getQuantity();
		logger.debug("Quantity to be deducted: "+quantity);
		// withdrawn item is less than or equal the current stocks.
		if (quantity <= currentStockPerCost.getQuantity()) {
			logger.debug("Quantity to be withdrawn is less than the current received stock quantity.");
			logger.debug("Current received stock: "+currentStockPerCost);
			item.setInventoryCost(currentStockPerCost.getInventoryCost());
			item.setUnitCost(currentStockPerCost.getUnitCost());
			item.setReceivedStockId(currentStockPerCost.getFormId());
			double remaining = currentStockPerCost.getQuantity() - item.getQuantity();
			remaining = NumberFormatUtil.roundOffNumber(remaining, SIX_DECIMALS);
			if (remaining == 0 && !receivedStocks.isEmpty()) {
				logger.info("Retrieving the next received stock from the list.");
				currentStockPerCost = receivedStocks.poll();
				logger.info("Current received stock is from "+currentStockPerCost.getForm()
						+" form with quantity "+currentStockPerCost.getQuantity());
				logger.info("Current received stock details: "+currentStockPerCost);
			} else {
				currentStockPerCost.setQuantity(remaining);
			}
			logger.debug("Remaining quantity for the current received stock: "+remaining);
			ret.add(item);
		// If current stock is zero and there are no available received stocks, set the unit and inventory cost to null.
		} else if (currentStockPerCost.getQuantity() == 0 && receivedStocks.isEmpty()) {
			logger.warn("Received stocks are already exhausted.");
			item.setUnitCost(null);
			item.setInventoryCost(null);
			ret.add(item);
			return ret;
		} else {
			//Quantity to be withdrawn is greater than the current stock available.
			if (currentStockPerCost.getQuantity() > 0) {
				logger.debug("Item quantity to be allocated is greater than the quantity of the current stock.");
				item.setInventoryCost(currentStockPerCost.getInventoryCost());
				item.setUnitCost(currentStockPerCost.getUnitCost());
				item.setReceivedStockId(currentStockPerCost.getFormId());
				@SuppressWarnings("unchecked")
				T newItem = (T) item.clone();
				newItem.setQuantity(currentStockPerCost.getQuantity());
				if (newItem instanceof Domain) {
					Domain domain = (Domain) newItem;
					domain.setId(0);
				}
				logger.debug("Current received stock can only allocate "+currentStockPerCost.getQuantity());
				logger.warn("Remaining quantity for current received stock is zero.");
				ret.add(newItem);

				double remaining = item.getQuantity() - currentStockPerCost.getQuantity();
				remaining = NumberFormatUtil.roundOffNumber(remaining, SIX_DECIMALS);
				item.setQuantity(remaining);
				logger.debug("Remaining quantity to be withdrawn is: "+remaining);
				if (receivedStocks.isEmpty()) { // If there are no available received stocks, return the remaining item.
					item.setInventoryCost(null);
					item.setUnitCost(null);
					currentStockPerCost.setQuantity(0); // Set to zero, to notify that there are no available stocks.
					logger.warn("Received stocks are all exhausted, quantity is set to zero.");
					ret.add(item);
					return ret;
				}

				currentStockPerCost = receivedStocks.poll();
				logger.info("Get the next available received stock from the list.");
				// If item is less than or equal to or item is greater than the received stocks
				if (item.getQuantity() <= currentStockPerCost.getQuantity() ||
						item.getQuantity() > currentStockPerCost.getQuantity()) {
					// the current available stocks, reallocate the item.
					logger.info("Re-allocating the item.");
					logger.trace("Remaining quantity to be allocated: "+item.getQuantity());
					return allocateCost(item, ret);
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
						remainingStockQuantity = NumberFormatUtil.roundOffTo2DecPlaces(remainingStockQuantity);
						currentStockPerCost.setQuantity(remainingStockQuantity);
						logger.trace("Remaining quantity for the current received stock: "+remainingStockQuantity);
						logger.info("Re-allocating the item for the quantity: "+item.getQuantity());
						return allocateCost(item, ret); // reallocate item if there are still remaining stocks.
					}
					currentStockQuantity += currentStockPerCost.getQuantity();
				}
			}

			while (!receivedStocks.isEmpty()) {
				logger.info("Get the next received stock from the list.");
				currentStockPerCost = receivedStocks.poll();
				allocateAndCollectItem(item, ret);
			}
		}
		return ret;
	}

	/**
	 * Compute and allocate the cost of the item based on the received items. Set
	 * InventoryItem.isIgnore to true to ignore the allocation.
	 * @param item The item to be allocated.
	 * @return The allocated items.
	 * @throws CloneNotSupportedException Throw error if the class does not support cloning.
	 */
	public List<T> allocateCost (T item) throws CloneNotSupportedException {
		logger.info(" =========>>>>> Allocating the item: "+itemId);
		List<T> ret = new ArrayList<T>();
		return allocateCost(item, ret);
	}

	private void allocateAndCollectItem (T item, List<T> collector) throws CloneNotSupportedException {
		double quantityToBeWithdrawn = item.getQuantity() - currentStockPerCost.getQuantity();
		quantityToBeWithdrawn = NumberFormatUtil.roundOffTo2DecPlaces(quantityToBeWithdrawn);
		if (quantityToBeWithdrawn <= currentStockPerCost.getQuantity()) {
			@SuppressWarnings("unchecked")
			T newItem = (T)item.clone();
			logger.info("Split price. Cloning the item.");
			newItem.setQuantity(quantityToBeWithdrawn);
			newItem.setReceivedStockId(currentStockPerCost.getFormId());
			newItem.setInventoryCost(currentStockPerCost.getInventoryCost());
			newItem.setUnitCost(currentStockPerCost.getUnitCost());
			double remaining = currentStockPerCost.getQuantity() - newItem.getQuantity();
			remaining = NumberFormatUtil.roundOffTo2DecPlaces(remaining);
			currentStockPerCost.setQuantity(remaining);
			collector.add(newItem);
			return;
		}
		@SuppressWarnings("unchecked")
		T newItem = (T)item.clone();
		newItem.setQuantity(currentStockPerCost.getQuantity());
		newItem.setReceivedStockId(currentStockPerCost.getFormId());
		newItem.setInventoryCost(currentStockPerCost.getInventoryCost());
		newItem.setUnitCost(currentStockPerCost.getUnitCost());
		quantityToBeWithdrawn -= currentStockPerCost.getQuantity();
		quantityToBeWithdrawn = NumberFormatUtil.roundOffTo2DecPlaces(quantityToBeWithdrawn);
		collector.add(newItem);
	}

	public int getItemId() {
		return itemId;
	}
}
