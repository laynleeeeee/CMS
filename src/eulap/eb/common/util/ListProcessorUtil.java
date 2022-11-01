package eulap.eb.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eulap.eb.domain.hibernate.BaseItem;

/**
 * Utility to process the list of items.

 */
public class ListProcessorUtil<T extends BaseItem> {
	private static Logger logger = Logger.getLogger(ListProcessorUtil.class);

	/**
	 * Removes the duplicate item ids and totals the quantity from the list.
	 * @param items The list of items.
	 * @return List of items without duplications.
	 */
	public List<T> removeDuplicate(List<T> items) {
		if(items.isEmpty()) {
			return items;
		}

		Map<Integer, T> itemId2BaseItem = new HashMap<Integer, T>();
		T editedItem = null;
		for (T item : items) {
			if(item.getItemId() == null)
				continue;
			if(itemId2BaseItem.containsKey(item.getItemId())) {
				editedItem = itemId2BaseItem.get(item.getItemId());
				//Total the quantity for the same item id.
				editedItem.setProcessedQty(editedItem.getProcessedQty()+item.getQuantity());
				itemId2BaseItem.put(item.getItemId(), editedItem);
			} else {
				item.setProcessedQty(item.getQuantity());
				itemId2BaseItem.put(item.getItemId(), item);
			}
		}

		List<T> ret = new ArrayList<T>();
		for (Map.Entry<Integer, T> m : itemId2BaseItem.entrySet()) {
			ret.add(m.getValue());
		}
		return ret;
	}

	/**
	 * Collect the form ids from the list.
	 * @param items The list of items.
	 * @return List of form ids.
	 */
	public List<Integer> collectFormIds(List<T> items) {
		return collectIds(items, true);
	}

	/**
	 * Collect the distinct item ids from the list.
	 * @param items The list of items.
	 * @return List of item ids.
	 */
	public List<Integer> collectDisctinctItemIds(List<T> items) {
		return collectIds(items, false);
	}

	private List<Integer> collectIds(List<T> items, boolean isFormId) {
		if(items.isEmpty()) {
			return Collections.emptyList();
		}
		logger.info("Collect the ids from the list.");
		List<Integer> collectedIds = new ArrayList<Integer>();
		Integer id = null;
		for (T item : items) {
			if(isFormId) {
				// Collect the form ids.
				id = item.getId();
				collectedIds.add(id);
			} else {
				// Collect the distinct item ids
				id = item.getItemId();
				if(Collections.frequency(collectedIds, id) == 0) {
					collectedIds.add(id);
				}
			}
			logger.debug("Added id to the list: "+id);
		}
		logger.debug("Collected "+collectedIds.size()+" ids.");
		return collectedIds;
	}
}
