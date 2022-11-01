package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eulap.eb.dao.ItemDao;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ProcessingItem;

/**
 * Utility class for classes that extends {@link ProcessingItem}

 * @param <T>
 */
public class PRItemUtil<T extends ProcessingItem> {

	/**
	 * 
	 * @param items The list of processing items.
	 * @return
	 */
	public static <T extends ProcessingItem> boolean hasNoAvailableStocks (List<T> items) {
		if (items != null && !items.isEmpty()) {
			for (T i : items) {
				if (i.getAvailableStocks() == null)
					return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param items The list of processing items.
	 * @return
	 */
	public static <T extends ProcessingItem> boolean hasDuplicateItem (List<T> items) {
		Map<String, T> piHM = new HashMap<String, T>();
		if (items != null && !items.isEmpty()) {
			for (T i : items) {
				if (piHM.containsKey(i.getStockCode())) {
					T item = piHM.get(i.getStockCode());
					if (item.getRefId().equals(i.getRefId())) {
						return true;
					}
				} else {
					piHM.put(i.getStockCode(), i);
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param items The list of processing items.
	 * @return
	 */
	public static <T extends ProcessingItem> boolean hasNoWarehouse (List<T> items) {
		if (items != null && !items.isEmpty()) {
			for (T i : items) {
				if (i.getWarehouseId() == null)
					return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param items The list of processing items.
	 * @return
	 */
	public static <T extends ProcessingItem> void setItemIds (List<T> pItems, ItemDao itemDao) {
		if (pItems != null && !pItems.isEmpty()) {
			for (ProcessingItem bi : pItems) {
				String stockCode = bi.getStockCode();
				if (stockCode != null && !stockCode.trim().isEmpty()) {
					Item item = itemDao.getItemByStockCode(stockCode, null);
					if (item != null) {
						bi.setItemId(item.getId());
					}
				}
			}
			pItems = null;
		}
	}

	/**
	 *
	 * @param items The list of processing items.
	 * @return True if has duplicate stock code in same warehouse, otherwise falase.
	 */
	public static <T extends ProcessingItem> boolean hasDuplicateFifoItem (List<T> items) {
		Map<String, T> piHM = new HashMap<String, T>();
		if (items != null && !items.isEmpty()) {
			for (T i : items) {
				if (piHM.containsKey(i.getStockCode())) {
					T item = piHM.get(i.getStockCode());
					if (item.getWarehouseId().equals(i.getWarehouseId())) {
						return true;
					}
				} else {
					piHM.put(i.getStockCode(), i);
				}
			}
		}
		return false;
	}

	/**
	 * Remove duplicate items.
	 * @param items The list of processing items.
	 * @return Summarized Individual Selection items.
	 */
	public List<T> getSummarizedISItems (List<T> items) {
		List<T> updatedItems = new ArrayList<T>();
		Map<String, T> itemHM = new HashMap<String, T>();

		T editedItem = null;
		String itemKey = null;
		for (T i : items) {
			itemKey = "i"+i.getItemId()+"w"+i.getWarehouseId();
			if(itemHM.containsKey(itemKey)) {
				editedItem = processEditedItem(i, itemHM.get(itemKey));
				itemHM.put(itemKey, editedItem);
			} else {
				itemHM.put(itemKey, i);
			}
		}

		for (Map.Entry<String, T> iHM : itemHM.entrySet()) {
			updatedItems.add(iHM.getValue());
		}

		itemHM = null;
		editedItem = null;

		Collections.sort(updatedItems, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				if (o1.getId() < o2.getId())
					return -1;
				else if (o1.getId() > o2.getId())
					return 1;
				return 0;
			}
		});

		return updatedItems;
	}

	private T processEditedItem(T i, T editedItem) {
		editedItem.setQuantity(i.getQuantity() + editedItem.getQuantity());
		editedItem.setRefQuantity((i.getRefQuantity() != null ? i.getRefQuantity() : 0) +
				(editedItem.getRefQuantity() != null ? editedItem.getRefQuantity() : 0));
		editedItem.setOrigQty((i.getOrigQty() != null ? i.getOrigQty() : 0)  +
				(editedItem.getOrigQty() != null ? editedItem.getOrigQty() : 0));
		return editedItem;
	}
}
