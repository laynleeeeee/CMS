package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.TaxUtil;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.BaseItem;
import eulap.eb.domain.hibernate.BaseItemDiscount;
import eulap.eb.domain.hibernate.ItemAddOn;
import eulap.eb.domain.hibernate.ItemDiscount;
import eulap.eb.domain.hibernate.ItemDiscountType;
import eulap.eb.domain.hibernate.ProcessingItem;
import eulap.eb.domain.hibernate.SaleItem;
import eulap.eb.domain.hibernate.SalesReturnItem;
import eulap.eb.service.oo.OOChild;

/**
 * Utility service class for sale items.

 * @param <T>
 *
 */
public class SaleItemUtil<T extends SaleItem> {
	private static Logger logger = Logger.getLogger(SaleItemUtil.class);

	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;
	private static final double VAT_VALUE = 1.12;

	/**
	 * Checks if the quantity is null or zero
	 * @param items The list of return items.
	 * @return True if an item has null or zero quantity, otherwise false.
	 */
	public static<T extends BaseItem> boolean hasNoOrZeroQty (List<T> items) {
		for (T t : items) {
			if (t.getQuantity() == null) {
				return true;
			} else if (t.getQuantity() == 0.0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the return item quantity is greater than reference item quantity.
	 * @param items The list of return items.
	 * @return True if all items has quantity greater than the 
	 * reference item quantity , otherwise false.
	 */
	public boolean isOverQtySR (List<T> items){
		for (T t : items) {
			logger.info(t.toString());
			logger.info("Comparing return/exchange quantity with the reference quantity ..." + 
					t.getStockCode() + " : " + t.getQuantity() + " == " + t.getRefQuantity());
			
			// Bug 1372: Validate the returns only and disregard the exchange. 
			if(t.getQuantity() != null) {
				if (t.getQuantity() < 0 && t.getRefQuantity() != null && t.getRefQuantity() != 0) {
					double refQuantity = NumberFormatUtil.roundOffTo2DecPlaces(t.getRefQuantity());
					if (Math.abs(t.getQuantity()) > refQuantity) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if the added item has negative quantity.
	 * Any added item must be considered as an exchange therefore must be positive.
	 * @param items The list of return items
	 * @return True if added item without reference returns negative, otherwise false.
	 */
	public boolean isExchangingNeg (List<T> items) {
		for (T t : items) {
			logger.info("Checks if the added item has a negative quantity : " + t.getQuantity());
			if (t.getRefQuantity() == null) {
				if (t.getQuantity() < 0.0) {
					return true;
				}				
			}
		}
		return false;
	}

	/**
	 * Get the split price items in the list. 
	 * @param itemId the item id that will be the basis in parsing the split price. 
	 * @param discountId The discount id 
	 * @param items The list of the items.
	 * @return The split items in the list. 
	 */
	public static <T extends SaleItem> List<T> getSplitPricedItems (Integer itemId, Integer discountId, List<T> items) {
		List<T> ret = new ArrayList<T>();
		for (T t : items) {
			if (t.getItemId().equals(itemId)) {
				if (t.getItemDiscountId() == null && discountId == null)
					ret.add(t);
				else if (t.getItemDiscountId() == null || discountId == null)
					continue;
				else if (t.getItemDiscountId().equals(discountId)) {
					ret.add(t);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Groups the  sale items by the item id.
	 * Appends 'n' for ITEM_ID and 'd' for ITEM_DISCOUNT_ID. This simply avoids duplicate key replacement in HashMap.
	 * For ex. ITEM_ID = 1, ITEM_DISCOUNT_ID = 1,    ITEM_ADD_ON_ID = NULL
	 *         ITEM_ID = 1, ITEM_DISCOUNT_ID = NULL, ITEM_ADD_ON_ID = 1
	 * First loop, it will go to the else part having a key of d1.
	 * Second loop, it will go to the if part of the condition having a key of n1.
	 * @param items The list of Items to be grouped.
	 * @return The list of sale items without duplicate item ids.
	 */
	public List<T> getSummarisedSaleItems (List<T> items) {
		logger.debug("Group sale items by item id.");
		List<T> updatedItems = new ArrayList<T>();
		Map<String, T> discHM = new HashMap<String, T>();
		Map<String, T> nullDiscHM = new HashMap<String,T>();
		
		T editedItem = null;
		for (T i : items) {
			boolean nullDiscount = i.getItemDiscountId() == null;

			if (nullDiscount) {
				// n for nullDiscount, appends character to avoid HashMap replacement.
				if (nullDiscHM.containsKey("n"+i.getItemId())) {
					editedItem = processEditedItem(i, nullDiscHM.get("n"+i.getItemId()));
					nullDiscHM.put("n"+i.getItemId(), editedItem);
				} else {
					nullDiscHM.put("n"+i.getItemId(), i);
				}
			} else {
				// d for discHM, appends character to avoid HashMap replacement.
				if(discHM.containsKey("d"+i.getItemDiscountId())) {
					editedItem = processEditedItem(i, discHM.get("d"+i.getItemDiscountId()));
					discHM.put("d"+i.getItemDiscountId(), editedItem);
				} else {
					discHM.put("d"+i.getItemDiscountId(), i);
				}
			}
		}
		
		discHM.putAll(nullDiscHM);
		
		for (Map.Entry<String, T> disc : discHM.entrySet()) {
			updatedItems.add(disc.getValue());
		}
		
		nullDiscHM = null;
		discHM = null;
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

	/**
	 * Filter the by return or exchange sale items.
	 * @param returnAndExchangeItems The return and exchange items. 
	 * @param onlyReturnItems true to set the returns only, otherwise false. 
	 * @return The filtered sales return item. 
	 */
	public static <T extends SalesReturnItem> List<T> filterSaleReturnItems (List<T> returnAndExchangeItems,
													boolean onlyReturnItems) {
		List<T> ret = new ArrayList<T>();
		for (T asItem : returnAndExchangeItems) {
			if (onlyReturnItems && asItem.getSalesReferenceId() != null) {
				ret.add(asItem);
			} else if (!onlyReturnItems && asItem.getSalesReferenceId() == null) {
				ret.add(asItem);
			}
		}
		return ret;
	}
	
	/**
	 * Process the item for sales returns. 
	 * This will handle the split price of of the reference sold items. 
	 * @param t The sales return form. 
	 * @param associatedSales sold items. 
	 * @return The process sales and return item. 
	 */
	public static <T extends SaleItem & SalesReturnItem> List<T>
	processSalesReturn (T t, List<? extends SaleItem> associatedSales, ItemDiscountService itemDiscountService) throws CloneNotSupportedException {
		List<? extends SaleItem> processedItems = 
				SaleItemUtil.getSplitPricedItems(t.getItemId(), t.getItemDiscountId(), associatedSales);
		Collections.sort(processedItems, new Comparator<SaleItem>() {
			@Override
			public int compare(SaleItem s1, SaleItem s2) {
				return s2.getId() - s1.getId();
			}
		});
		
		double currentReturn = Math.abs(t.getQuantity());
		
		// Handle split price.
		List<T> ret = new ArrayList<T>();
		for (SaleItem pi : processedItems) {
			if (currentReturn <= pi.getQuantity()) {  // if totalReturn is less than the quantity.
				t.setUnitCost(pi.getUnitCost());
				t.setQuantity(-currentReturn);
				ret.add(t);
				break;
			} else {
				T newRE = (T) t.clone(); // Create new return and exchange for split price. 
				double quantity = pi.getQuantity();
				newRE.setUnitCost(pi.getUnitCost());
				newRE.setQuantity(-quantity);
				currentReturn -= quantity;
				ret.add(newRE);
			}
		}
		SaleItemUtil<T> util = new SaleItemUtil<T>();
		return util.processDiscountAndAmount(ret, itemDiscountService);
	}
	
	private T processEditedItem(T i, T editedItem) {
		editedItem.setQuantity(i.getQuantity() + editedItem.getQuantity());
		editedItem.setRefQuantity((i.getRefQuantity() != null ? i.getRefQuantity() : 0) + 
				(editedItem.getRefQuantity() != null ? editedItem.getRefQuantity() : 0));
		editedItem.setOrigQty((i.getOrigQty() != null ? i.getOrigQty() : 0)  + 
				(editedItem.getOrigQty() != null ? editedItem.getOrigQty() : 0));
		if (i.getDiscount() != null && editedItem.getDiscount() != null) {
			editedItem.setDiscount((i.getDiscount() != null ? i.getDiscount() : 0) + 
					(editedItem.getDiscount() != null ? editedItem.getDiscount() : 0));	
		}
		editedItem.setVatAmount(NumberFormatUtil.roundOffTo2DecPlaces(i.getVatAmount() + editedItem.getVatAmount()));
		editedItem.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(i.getAmount() + editedItem.getAmount()));
		return editedItem;
	}

	/**
	 * Compute the value of the discount based from its type id.
	 */
	public static double computeDiscount (BaseItemDiscount itemDiscount, int discountType, double quantity, double srp, Integer taxTypeId) {
		logger.debug("Computingn the discount of item with type id: "+discountType);
		return computeDiscountOrAddOn(itemDiscount.getValue(), discountType, quantity, srp, taxTypeId);
	}

	/**
	 * Compute the value of the add on based from its type id.
	 */
	public static double computeAddOn(ItemAddOn itemAddOn, double quantity, double srp, Integer taxTypeId) {
		int addOnTypeId = itemAddOn.getItemAddOnTypeId();
		logger.debug("Computing the add on for type: "+addOnTypeId);
		return computeDiscountOrAddOn(itemAddOn.getValue(), addOnTypeId, quantity, srp, taxTypeId);
	}

	/**
	 * Computes the discount or add on amount.
	 * @param value The value of the discount or add on.
	 * @param typeId The type of discount/add on {1=Percentage, 2=Amount, 3=Quantity}
	 * @param quantity The quantity of items to be computed.
	 * @param srp The selling price of the item.
	 * @return The computed value for add on/discount.
	 */
	private static double computeDiscountOrAddOn(double value, int typeId, double quantity, double srp, Integer taxTypeId) {
		double grossAmount = quantity * srp;
		double netOfVat = TaxUtil.isVatable(taxTypeId) ? (NumberFormatUtil.divideWFP(grossAmount, VAT_VALUE)) : grossAmount;
		if (typeId == ItemDiscountType.DISCOUNT_TYPE_AMOUNT) {
			return NumberFormatUtil.roundOffTo2DecPlaces(quantity < 0 ? -value : value);
		} else if (typeId == ItemDiscountType.DISCOUNT_TYPE_PERCENTAGE) {
			return NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(netOfVat, NumberFormatUtil.divideWFP(value, 100)));
		} else if (typeId == ItemDiscountType.DISCOUNT_TYPE_QUANITY) {
			return NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, value));
		}
		throw new RuntimeException("unknown type");
	}

	/**
	 * Process/compute the discount and amount of the list of sale items.
	 * @param items The list of sale items.
	 * @param itemDiscountService The item discount service class.
	 * @return The list of sale items with discount of amount processed.
	 */
	public List<T> processDiscountAndAmount (List<T> items, ItemDiscountService itemDiscountService) {
		if (items != null && !items.isEmpty()) {
			T currentItem = null;
			int cnt = 0;
			for (T item : items) {
				Integer discountId = item.getItemDiscountId();
				double origQuantity = item.getQuantity();
				double quantity = Math.abs(origQuantity);
				double srp = item.getSrp() != null ? item.getSrp() : 0;
				Integer taxTypeId = item.getTaxTypeId();
				if (discountId != null || (item.getDiscount() != null && 
						item.getDiscount().doubleValue() != 0)) {
					ItemDiscount itemDiscount = discountId == null ? null : itemDiscountService.getItemDiscount(discountId);
					Integer discountTypeId = itemDiscount != null ? itemDiscount.getItemDiscountTypeId() : ItemDiscountType.DISCOUNT_TYPE_AMOUNT;
					if (discountTypeId.intValue() == ItemDiscountType.DISCOUNT_TYPE_AMOUNT) {
						if (currentItem == null) {
							currentItem = item;
						} else {
							if (currentItem.getItemId().equals(item.getItemId())) {
								cnt++;
								if (cnt >= 1) {
									item.setDiscount(null);
								} else {
									item.setDiscount(computeDiscount(itemDiscount, discountTypeId, quantity, srp, taxTypeId));
								}
							} else {
								currentItem = item;
								cnt = 0;
							}
						}
					} else {
						item.setDiscount(computeDiscount(itemDiscount, discountTypeId, quantity, srp, taxTypeId));
					}
				}
				double discount = item.getDiscount() != null ? Math.abs(item.getDiscount()) : 0;
				double qtyDiscount = quantity != 0 ? NumberFormatUtil.divideWFP(discount, quantity) : 0;
				srp = srp - qtyDiscount;//Subtract discount before computing vat amount.
				double vat = TaxUtil.isVatable(taxTypeId) ? srp - (NumberFormatUtil.divideWFP(srp, VAT_VALUE)) : 0;
				double computedUnitPrice = srp - vat;
				double netAmount = NumberFormatUtil.multiplyWFP(computedUnitPrice, quantity);
				if (origQuantity < 0) {
					netAmount = -netAmount; // negate net amount for returned items
				}
				item.setVatAmount(NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(vat, quantity))); // re compute VAT amount
				item.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(netAmount));
			}
			logger.debug("Free the current item.");
			currentItem = null;
			return items;
		}
		return new ArrayList<T>();
	}
	
	/**
	 * Compute the total per item and warehouse.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @param items The list of sale items.
	 * @return The total quantity.
	 */
	public static <T extends SaleItem> double getTotalQtyByItemAndWH (int itemId, int warehouseId, List<T> items ) {
		double totalQty = 0;
		for (T i : items) {
			if (i.getItemId() == itemId && i.getWarehouseId() == warehouseId && i.getQuantity() != null) {
				totalQty += i.getQuantity() + (i.getOrigQty() != null ? -i.getOrigQty() : 0);
			}
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalQty);
	}

	/**
	 * Checks if the sale item has no quantity.
	 * @param items The list of sale items.
	 * @return True if all sale items has no quantity, otherwise false.
	 */
	public boolean hasNoQty (List<T> items) {
		for (T t : items) {
			if (t.getQuantity() == null || t.getQuantity().equals(0))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the sale items has same item and discount.
	 * @param items The list of sale items.
	 * @return True if the items has same item and discount, 
	 * otherwise false.
	 */
	public boolean hasSameItemAndDiscount (List<T> items) {
		if (items != null && !items.isEmpty()) {
			Map<Integer, T> itemHM = new HashMap<Integer, T>();
			T addedItem = null;
			for (T i : items) {
				boolean nullDiscount = i.getItemDiscountId() == null;
				if (itemHM.containsKey(i.getItemId())) {
					addedItem = itemHM.get(i.getItemId());
					Integer aDId = addedItem.getItemDiscountId();
					if (aDId == null && nullDiscount) {
						return true;
					} else {
						if (aDId != null && !nullDiscount && (aDId.equals(i.getItemDiscountId()))) {
							return true;
						}
					}
				} else {
					itemHM.put(i.getItemId(), i);
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the sale items has an invalid item.
	 * @param items The list of sale items.
	 * @return True if all stock codes are valid, otherwise false.
	 */
	public static <T extends BaseItem> boolean hasInvalidItem (List<T> items) {
		for (T i : items) {
			if (i.getItemId() == null)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the sale item has no warehouse.
	 * @param items The list of cash sale items.
	 * @return True if any sale items has no warehouse, otherwise false.
	 */
	public static <T extends SaleItem> boolean hasNoWarehouse (List<T> items) {
		for (T i : items) {
			if (i.getWarehouseId() == null)
				return true;
		}
		return false;
	}

	/**
	 * Checks if the sale item has negative quantity.
	 * @param items The list of sale items.
	 * @return True if all sale items has no amount, otherwise false.
	 */
	public static <T extends BaseItem> boolean hasZeroOrNegQty (List<T> items) {
		for (T i : items) {
			if (i.getQuantity() != null) {
				if (i.getQuantity() <= 0.0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Compute the header amount for account sales and account sales return.
	 * @param items The list of sale items.
	 * @return The header amount.
	 */
	public static <T extends SaleItem & SalesReturnItem> double computeHeaderAmt (List<T> items) {
		return computeTotalAmt(items);
	}

	/**
	 * Compute the total amount of the sale items.
	 * @param items The list of sale items.
	 * @return The total amount.
	 */
	public static <T extends SaleItem> double computeTotalAmt (List<T> items) {
		double totalAmount = 0;
		if (items != null && !items.isEmpty()) {
			for (T i : items) {
				totalAmount += i.getAmount() != null ? i.getAmount() : 0;
			}
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalAmount);
	}

	/**
	 * Compute the total other charges.
	 * @param otherCharges The list of other charges of sales, returns zero if empty.
	 * @return The total other charges.
	 */
	public static double computeTotalOtherCharges(List<AROtherCharge> otherCharges) {
		if(otherCharges.isEmpty()) {
			return 0;
		}

		double totalAmt = 0;
		for (AROtherCharge oc : otherCharges) {
			if(oc.getAmount() != null) {
				totalAmt += oc.getAmount();
			}
		}

		return NumberFormatUtil.roundOffTo2DecPlaces(totalAmt);
	}

	/**
	 * Stores the font size to the model, returns 10 if the fontSize is null.
	 * @param fontSize The size of the font.
	 * @param model The model.
	 */
	public static void getFontSize(Integer fontSize, Model model) {
		if(fontSize == null) {
			fontSize = 10; //Default font
		}
		model.addAttribute("fontSize", fontSize);
	}

	/**
	 * Filter the list of items by warehouseId.
	 * @param saleItems The list of items.
	 * @return The filtered items.
	 */
	public List<T> filterItemsByWarehouse(List<T> saleItems, Integer warehouseId) {
		if(!saleItems.isEmpty() && warehouseId != null) {
			logger.info("filtering the list of items using the warehouse id: "+warehouseId);
			List<T> filteredItems = new ArrayList<T>();
			for (T item : saleItems) {
				if(item.getWarehouseId() != null) {
					if(item.getWarehouseId().equals(warehouseId)) {
						filteredItems.add(item);
					}
				}
			}
			return filteredItems;
		}
		return saleItems;
	}

	/**
	 * Get and collect the list of warehouseIds in the list.
	 * @param items The list of items.
	 * @return The list of warehouse ids.
	 */
	public List<Integer> collectWarehouseIds(List<T> items) {
		if(items.isEmpty()) {
			return Collections.emptyList();
		}
		logger.info("Collect the ids from the list.");
		List<Integer> collectedIds = new ArrayList<Integer>();
		Integer id = null;
		for (T item : items) {
			// Collect the distinct warehouse ids
			id = item.getWarehouseId();
			if(Collections.frequency(collectedIds, id) == 0) {
				collectedIds.add(id);
			}
			logger.debug("Added id to the list: "+id);
		}
		logger.debug("Collected "+collectedIds.size()+" ids.");
		return collectedIds;
	}

	/**
	 * Process the list of sale items for viewing.
	 * <br>First filter the items by warehouse then summarise the items to remove duplicate items.
	 * @param saleItems The list of items.
	 * @return The summarised items.
	 */
	public List<T> processSaleItemsForViewing(List<T> saleItems) {
		List<Integer> warehouseIds = collectWarehouseIds(saleItems);
		if(warehouseIds.isEmpty()) {
			return getSummarisedSaleItems(saleItems);
		} else {
			List<T> processedItems = new ArrayList<T>();
			List<T> filteredItems = new ArrayList<T>();
			for (Integer warehouseId : warehouseIds) {
				filteredItems = filterItemsByWarehouse(saleItems, warehouseId);
				processedItems.addAll(getSummarisedSaleItems(filteredItems));
			}
			return processedItems;
		}
	}

	/**
	 * Validate the items if there are duplicate item and discount per warehouse.
	 * @param saleItems The list of sale items.
	 * @return The error message: "Same item and discount is not allowed."
	 *  if there are errors. Otherwise, null.
	 */
	public String validateDuplicateItemAndDisc(List<T> saleItems) {
		boolean hasDuplicateItemAndDisc = false;
		List<Integer> warehouseIds = collectWarehouseIds(saleItems);
		//If there are more than 1 warehouse, filter first the list of items by warehouse id before validating the list.
		if(warehouseIds.size() > 1) {
			for (Integer warehouseId : warehouseIds) {
				List<T> filteredItems = filterItemsByWarehouse(saleItems, warehouseId);
				if (hasSameItemAndDiscount(filteredItems)) {
					hasDuplicateItemAndDisc = true;
				}
			}
		} else {
			if (hasSameItemAndDiscount(saleItems)) {
				hasDuplicateItemAndDisc = true;
			}
		}

		if(hasDuplicateItemAndDisc) {
			return "Same item and discount is not allowed.";
		}
		return null;
	}

	/**
	 * Update the refQauntity. refQauntity is the remaining stocks that can be returned by the customers. 
	 * @param saleItemReturns The actual returned of the the customers
	 * @param returnedStocks The returned stocks.
	 * @param cashSaleItems the sold items. 
	 */
	public static <T extends SaleItem> void updateRefQuantity (List<T> saleItemReturns, List<? extends SaleItem> cashSaleItems,
			List<? extends SaleItem> returnedStocks) {
		for (T saleItemReturn : saleItemReturns) {
			saleItemReturn.setRefQuantity(0.0);
			// Update reference quantity from the actual sold items. 
			updateRefQuantity(saleItemReturn, cashSaleItems);
			// update the reference quantity from the returned stocks
			updateRefQuantity(saleItemReturn, returnedStocks);
		}
	}

	/*
	 * Update the remaining stocks that can be allowed for returns.
	 */
	private static <T extends SaleItem> void updateRefQuantity (T toBeReturnedStock, List<? extends SaleItem> returnedStocksOrSoldItems) {
		for (SaleItem returnedStock : returnedStocksOrSoldItems) {
			if (toBeReturnedStock.getItemId().equals(returnedStock.getItemId())) {
				toBeReturnedStock.setRefQuantity(NumberFormatUtil.roundOffTo2DecPlaces(returnedStock.getQuantity() + toBeReturnedStock.getRefQuantity()));
			}
		}
	}

	/**
	 * Generate the needed parameters for the printout of the sale items.
	 * @param saleItems The list of sale items.
	 * @return The processed list of sale items.
	 */
	public List<T> generateSaleItemPrintout(List<T> saleItems) {
		if (saleItems != null && !saleItems.isEmpty()) {
			for (SaleItem saleItem : saleItems) {
				double quantity = saleItem.getQuantity() != null ? saleItem.getQuantity() : 0;
				double srp = saleItem.getSrp() != null ? saleItem.getSrp() : 0;
				double discount = saleItem.getDiscount() != null ? saleItem.getDiscount() : 0;
				logger.debug("Computing gross amount : " + (NumberFormatUtil.multiplyWFP(quantity, srp)));
				double grossAmount = NumberFormatUtil.multiplyWFP(quantity, srp);
				logger.debug("Computing net amount : " + (grossAmount - discount));
				double netAmount = grossAmount - discount;
				saleItem.setStockCode(saleItem.getItem().getStockCode());
				saleItem.setGrossAmount(NumberFormatUtil.roundOffTo2DecPlaces(grossAmount));
				saleItem.setNetAmount(NumberFormatUtil.roundOffTo2DecPlaces(netAmount));
			}
		}
		return saleItems;
	}

	/**
	 * Process the sale items by including only those that have at least stock code or quantity.
	 * @param items The sale items.
	 * @return The processed sale items.
	 */
	public static <T extends BaseItem> List<T> processSaleItems (List<T> items) {
		List<T> ret = new ArrayList<T>();
		if (items != null && !items.isEmpty()) {
			for (T item : items) {
				String stockCode = item.getStockCode();
				Double quantity = item.getQuantity();
				boolean hasStockCode = stockCode != null && !stockCode.isEmpty();
				boolean hasQty = quantity != null && quantity != 0.0;
				if (hasStockCode || hasQty) 
					ret.add(item);
			}
		}
		items = null;
		return ret;
	}

	/**
	 * Compute the total per item and warehouse.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @param items The list of sale items.
	 * @return The total quantity.
	 */
	public static <T extends ProcessingItem> double getTQtyByItemAndWH (int itemId, int warehouseId, List<T> items ) {
		double totalQty = 0;
		for (T i : items) {
			if (i.getItemId() == itemId && i.getWarehouseId() == warehouseId && i.getQuantity() != null) {
				totalQty += i.getQuantity() + (i.getOrigQty() != null ? -i.getOrigQty() : 0);
			}
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalQty);
	}

	/**
	 * Collect the reference object ids from the list.
	 */
	public static <T extends OOChild> List<Integer> collectRefObjectIds(List<T> children) {
		logger.debug("Collect the reference object ids.");
		List<Integer> collectedIds = new ArrayList<Integer>();
		for (T t : children) {
			Integer id = t.getRefenceObjectId();
			if(id != null) {
				if(Collections.frequency(collectedIds, id) == 0) {
					collectedIds.add(id);
				}
			}
		}
		return collectedIds;
	}

	/**
	 * Compute the total quantity of stocks to be withdrawn
	 *  from the list filtered by the reference object id.
	 */
	public static <T extends OOChild> double getTotalQtyByReferenceId(List<T> children, Integer refObjectId) {
		double totalQty = 0;
		for (T child : children) {
			BaseItem baseItem = (BaseItem) child;
			//The current reference object id.
			Integer currentRefObjectId = child.getRefenceObjectId();
			if(currentRefObjectId != null && refObjectId != null) {
				if(currentRefObjectId.equals(refObjectId)) {
					//The original reference object id before editing of the form.
					Integer origRefObjectId = baseItem.getOrigRefObjectId();
					double origQty = 0;
					if(origRefObjectId != null) { // For editing
						if(origRefObjectId.equals(currentRefObjectId)) {
							//If the reference object id is not changed, get the original quantity before the form was edited.
							origQty = baseItem.getOrigQty() != null ? Math.abs(baseItem.getOrigQty()) : 0;
						}
					}
					totalQty += Math.abs(baseItem.getQuantity()) - origQty;
				}
			}
		}

		return NumberFormatUtil.roundOffTo2DecPlaces(totalQty);
	}

	/**
	 * Computes the add on of the item.
	 */
	public static double computeAddOn(double originalSrp, double quantity, double computedSrp) {
		double totalWoAddOn = NumberFormatUtil.multiplyWFP(originalSrp, quantity);
		double totalWAddOn = NumberFormatUtil.multiplyWFP(computedSrp, quantity);
		return NumberFormatUtil.roundOffTo2DecPlaces(totalWAddOn- totalWoAddOn);
	}

	/**
	 * Set the unit cost to zero when null
	 */
	public static <T extends BaseItem> void setNullUnitCostToZero (T baseItem) {
		if(baseItem.getUnitCost() == null) {
			baseItem.setUnitCost(0.0);
		}
	}

	/**
	 * Compute the total amount of the processing items.
	 * @param items The list of processing items.
	 * @return The total amount.
	 */
	public static <T extends ProcessingItem> double computeTotalPrAmt (List<T> items) {
		double totalAmount = 0;
		if (items != null && !items.isEmpty()) {
			for (T i : items) {
				totalAmount += i.getQuantity() != null ? NumberFormatUtil.multiplyWFP(i.getQuantity(), i.getUnitCost()) : 0;
			}
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalAmount);
	}
}
