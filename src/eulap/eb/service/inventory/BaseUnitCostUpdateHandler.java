package eulap.eb.service.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Domain;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.common.util.ListProcessorUtil;
import eulap.eb.dao.AccountSaleItemDao;
import eulap.eb.dao.CAPDeliveryItemDao;
import eulap.eb.dao.CashSaleItemDao;
import eulap.eb.dao.CashSaleReturnItemDao;
import eulap.eb.dao.RReturnToSupplierItemDao;
import eulap.eb.dao.RTransferReceiptItemDao;
import eulap.eb.dao.RepackingDao;
import eulap.eb.dao.RepackingItemDao;
import eulap.eb.dao.StockAdjustmentItemDao;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.CAPDeliveryItem;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
import eulap.eb.domain.hibernate.RTransferReceiptItem;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.domain.hibernate.RepackingItem;
import eulap.eb.domain.hibernate.SaleItem;
import eulap.eb.domain.hibernate.StockAdjustmentItem;
import eulap.eb.service.ItemDiscountService;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.web.dto.ItemTransaction;

/**
 * Elasticbooks base class that will handle the updating of unit cost of the item
 * every time there is an update of status, cancellation and saving of previous dates. 


 *
 */
@Service
public class BaseUnitCostUpdateHandler {
	private static Logger logger = Logger.getLogger(BaseUnitCostUpdateHandler.class);

	@Autowired
	private RTransferReceiptItemDao trItemDao;
	@Autowired
	private RepackingDao repackingDao;
	@Autowired
	private RepackingItemDao rpItemDao;
	@Autowired
	private CashSaleItemDao csItemDao;
	@Autowired
	private StockAdjustmentItemDao saItemDao;
	@Autowired
	private AccountSaleItemDao accountSaleItemDao;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private CashSaleReturnItemDao csrItemDao;
	@Autowired
	private CAPDeliveryItemDao deliveryItemDao;
	@Autowired
	private RReturnToSupplierItemDao rtsItemDao;
	@Autowired
	private List<FormUnitCosthandler> formUnitCostUpdatehandlers;

	public void updateUnitCost(RItemCostUpdateService costUpdateService, WeightedAveItemAllocator<ItemTransaction> fifoAllocator, ItemTransaction it, int itemId,
			int warehouseId, Date formDate, boolean isAllocateRpTo) {
		String formName = null;
		List<ItemTransaction> allocatedTransactions = null;
		Queue<ItemTransaction> allocatedItems = null;
		ItemTransaction currentAllocItem = null;
		try {
			formName = it.getForm();
			allocatedTransactions = fifoAllocator.allocateCost(it);
			logger.debug("Allocated size: "+allocatedTransactions);
			logger.debug("Re-computing transactions on "+formName+" form.");
			if(formName.equals(ItemTransaction.ACCT_SALE_FORM_NAME)
					|| formName.equals(ItemTransaction.ACCT_SALE_RETURN_FORM_NAME)
					|| formName.equals(ItemTransaction.ACCT_SALE_WS_FORM_NAME)
					|| formName.equals(ItemTransaction.ACCT_SALE_RET_WS_FORM_NAME)
					|| formName.equals(ItemTransaction.CASH_SALE_FORM_NAME)
					|| formName.equals(ItemTransaction.CASH_SALE_RETURN_FORM_NAME)
					|| formName.equals(ItemTransaction.CAP_DELIVERY_FORM_NAME)) {
				allocatedItems = new LinkedList<ItemTransaction>(allocatedTransactions);
				currentAllocItem = allocatedItems.poll();
				if(formName.equals(ItemTransaction.ACCT_SALE_FORM_NAME)
						|| formName.equals(ItemTransaction.ACCT_SALE_RETURN_FORM_NAME)
						|| formName.equals(ItemTransaction.ACCT_SALE_WS_FORM_NAME)
						|| formName.equals(ItemTransaction.ACCT_SALE_RET_WS_FORM_NAME)) {
					//Account Sales and Account Sales Return items.
					boolean isExchangeItem = false;
					if(formName.equals(ItemTransaction.ACCT_SALE_RETURN_FORM_NAME)
							|| formName.equals(ItemTransaction.ACCT_SALE_RET_WS_FORM_NAME)) {
						//Get only the exchanged items for sale return items.
						isExchangeItem = true;
					}
					updateAcctSaleItemUC(itemId, warehouseId, allocatedItems, currentAllocItem, it.getId(), isExchangeItem);
				} else if(formName.equals(ItemTransaction.CASH_SALE_FORM_NAME)) {
					//Cash Sale items
					updateCsItemUC(itemId, warehouseId, allocatedItems, currentAllocItem, it.getId());
				} else if(formName.equals(ItemTransaction.CASH_SALE_RETURN_FORM_NAME)){
					//Cash Sale Return items.
					updateCsrItemUC(itemId, warehouseId, allocatedItems, currentAllocItem, it.getId());
				} else {
					//Paid in Advance Delivery
					updateDeliveryItemUC(itemId, warehouseId, allocatedItems, currentAllocItem, it.getId());
				}
			} else {
				allocatedItems = new LinkedList<ItemTransaction>(allocatedTransactions);
				currentAllocItem = allocatedItems.poll();
				if(formName.equals(ItemTransaction.RTS_FORM_NAME)) {
					//Allocate RTS Items but will not save the allocation, unit cost is based from RR.
					return;
				} else if(formName.equals(ItemTransaction.STOCK_ADJUSTMENT_FORM_NAME)) {
					//Stock Adjustment items.
					updateSaItemUC(itemId, allocatedItems, currentAllocItem, it.getId());
				} else if(formName.equals(ItemTransaction.TR_FORM_NAME)) {
					//Transfer Receipt items.
					updateTrItemUC(itemId, allocatedItems, currentAllocItem, it.getId());
				} else if(formName.equals(ItemTransaction.REPACKING_FORM_NAME)) {
					//Repacking items.
					updateRepackedItemUC(costUpdateService, itemId, warehouseId, formDate, allocatedItems,
							currentAllocItem, it.getId(), isAllocateRpTo);
				} else if(formName.equals(ItemTransaction.RTS_EB_FORM_NAME)) {
					// RTS - EB items
					updateRTSItemUC(itemId, allocatedItems, currentAllocItem, it.getId());
				} else {
					FormUnitCosthandler formUCHandler = getFormUnitCosthandler(it.getForm());
					if (formUCHandler != null) {
						formUCHandler.processAllocatedItem(itemId, warehouseId, allocatedItems, currentAllocItem);
					}
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private FormUnitCosthandler getFormUnitCosthandler(String formLabel) {
		for (FormUnitCosthandler costHalder : formUnitCostUpdatehandlers) {
			if (costHalder.getFormLabel().equalsIgnoreCase(formLabel)) {
				return costHalder;
			}
		}
		return null;
	}

	/**
	 * Update the Stock Adjustment Items' unit cost and quantity.
	 * @param allocatedItems The list of allocated items.
	 * @param stockAdjustmentId The id of the stock adjustment.
	 */
	private void updateSaItemUC(int itemId, Queue<ItemTransaction> allocatedItems, ItemTransaction currentAllocItem, int stockAdjustmentId) {
		logger.info("Current Allocated transaction for stock adjustment item :::: "+currentAllocItem);
		Double allocQty = currentAllocItem.getQuantity();
		List<StockAdjustmentItem> saItems = saItemDao.getSAItems(stockAdjustmentId, itemId);

		ListProcessorUtil<StockAdjustmentItem> remover = new ListProcessorUtil<StockAdjustmentItem>();
		List<Integer> toBeDeletedIds = remover.collectFormIds(saItems);
		logger.debug("To be deleted ids: "+toBeDeletedIds.size());
		saItemDao.delete(toBeDeletedIds);

		logger.debug("Processing the stock adjustment items. Removing the duplicate items.");
		List<StockAdjustmentItem> processedItems = remover.removeDuplicate(saItems);
		for (StockAdjustmentItem sai : processedItems) {
			//Create a new instance of Stock Adjustment Item
			while (currentAllocItem != null) {
				allocQty = currentAllocItem.getQuantity();
				sai.setId(0);
				sai.setQuantity(-allocQty);
				sai.setUnitCost(currentAllocItem.getUnitCost());
				saItemDao.save(sai); // SAVE
				currentAllocItem = getNextAllocItem(allocatedItems);
				allocQty = getAllocatedQty(currentAllocItem);
				logger.info("Current Allocated item: "+currentAllocItem);
			}
		}

		logger.debug("Freeing up memory allocation for stock adjustment out.");
		remover = null;
		processedItems = null;
		toBeDeletedIds = null;
		logger.info("Successfully updated the quantity and unit cost of item transaction "
				+ "from STOCK ADJUSTMENT form.");
	}

	/**
	 * Update the Quantity and Unit Cost for Transfer Receipt Items.
	 */
	private void updateTrItemUC(int itemId, Queue<ItemTransaction> allocatedItems, ItemTransaction currentAllocItem, int transferReceiptId) {
		logger.info("Current Allocated transaction for transfer receipt item :::: "+currentAllocItem);
		Double allocQty = null;
		List<RTransferReceiptItem> trItems = trItemDao.getRTrItems(transferReceiptId, itemId);

		ListProcessorUtil<RTransferReceiptItem> remover = new ListProcessorUtil<RTransferReceiptItem>();
		List<Integer> toBeDeletedIds = remover.collectFormIds(trItems);
		logger.debug("To be deleted ids: "+toBeDeletedIds.size());
		trItemDao.delete(toBeDeletedIds);

		List<RTransferReceiptItem> processedItems = remover.removeDuplicate(trItems);
		for (RTransferReceiptItem tri : processedItems) {
			allocQty = currentAllocItem.getQuantity();
			while (currentAllocItem != null) {
				tri.setId(0);
				tri.setQuantity(allocQty);
				tri.setUnitCost(currentAllocItem.getUnitCost());
				trItemDao.save(tri); // SAVE
				currentAllocItem = getNextAllocItem(allocatedItems);
				allocQty = getAllocatedQty(currentAllocItem);
				logger.info("Current allocated item for TR form: "+currentAllocItem);
			}
		}

		logger.debug("Freeing up memory allocation for transfer receipt item.");
		remover = null;
		processedItems = null;
		toBeDeletedIds = null;
		logger.info("Successfully updated the quantity and unit cost of "
				+ "item transaction from TRANSFER RECEIPT form.");
	}

	/**
	 * Update the quantity and unit cost of Repacked items.
	 */
	private void updateRepackedItemUC(RItemCostUpdateService costUpdateService, int itemId, int warehouseId, Date fromDate, Queue<ItemTransaction> allocatedItems,
			ItemTransaction currentAllocItem, int repackingId, boolean isAllocateRpTo) {
		logger.info("Current Allocated transaction for Repacked item :::: "+currentAllocItem);
		Double allocQty = getAllocatedQty(currentAllocItem);
		Double qtyToBeWithdrawn = null;
		double totalAmount = 0;
		List<RepackingItem> repackedItems = rpItemDao.getRepackingItems(repackingId, itemId);
		for (RepackingItem rpi : repackedItems) {
			totalAmount = 0;
			while(currentAllocItem != null) {
				if(qtyToBeWithdrawn == null) {
					qtyToBeWithdrawn = rpi.getQuantity();
				}
				if(allocQty >=  qtyToBeWithdrawn) {
					allocQty = NumberFormatUtil.roundOffNumber(allocQty - qtyToBeWithdrawn, NumberFormatUtil.SIX_DECIMAL_PLACES);
					totalAmount += qtyToBeWithdrawn * currentAllocItem.getUnitCost();
					if(allocQty == 0.0) {
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
					}
					qtyToBeWithdrawn = null;
					break;
				} else if(allocQty > 0){
					totalAmount += allocQty * currentAllocItem.getUnitCost();
					qtyToBeWithdrawn = qtyToBeWithdrawn - allocQty;
					currentAllocItem = getNextAllocItem(allocatedItems);
					allocQty = getAllocatedQty(currentAllocItem);
				}
			}

			logger.debug("Set the unit cost and repacked unit cost of the repacked item.");
			rpi.setUnitCost(totalAmount/rpi.getQuantity());
			rpi.setRepackedUnitCost(totalAmount/rpi.getRepackedQuantity());
			rpItemDao.saveOrUpdate(rpi);

			if(isAllocateRpTo) {
				// Update the re-packed items
				int repackedToId = rpi.getToItemId();
				Repacking r = repackingDao.get(rpi.getRepackingId());
				logger.info("Re-allocating repacked to item id: "+repackedToId+", starting on: "+r.getrDate());
				costUpdateService.updateItemUnitCost(repackedToId, warehouseId, r.getrDate(), isAllocateRpTo);
			}

			//Setting the memory allocation to null
			repackedItems = null;
			qtyToBeWithdrawn = null;
			logger.info("Successfully updated the unit cost of item transaction from REPACKING form.");
		}
	}

	/**
	 * Update the Quantity and Unit Cost of Account Sale Items.
	 */
	private void updateAcctSaleItemUC(int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems, ItemTransaction currentAllocItem,
			int arTransactionId, boolean isExchangedItem) throws CloneNotSupportedException {
		logger.info("Current Allocated transaction for Account Sale item :::: "+currentAllocItem);
		List<AccountSaleItem> acctSaleItems = accountSaleItemDao.getAccountSaleItems(arTransactionId, itemId, warehouseId, isExchangedItem);
		List<SaleItem> saleItems = new ArrayList<SaleItem>(acctSaleItems);
		processItems(allocatedItems, currentAllocItem, saleItems);
	}

	/**
	 * Update the Quantity and Unit Cost for Cash Sales Items.
	 */
	private void updateCsItemUC(int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems, ItemTransaction currentAllocItem, int cashSaleId) throws CloneNotSupportedException {
		logger.info("Current Allocated transaction for cash sale item :::: "+currentAllocItem);
		List<CashSaleItem> csItems = csItemDao.getCashSaleItems(cashSaleId, itemId, warehouseId);
		List<SaleItem> saleItems = new ArrayList<SaleItem>(csItems);
		processItems(allocatedItems, currentAllocItem, saleItems);
	}

	/**
	 * Update the Quantity and Unit Cost for Cash Sale Return Items.
	 */
	private void updateCsrItemUC(int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems, ItemTransaction currentAllocItem, int cashSaleReturnId) throws CloneNotSupportedException {
		logger.info("Current Allocated transaction for cash sale return item :::: "+currentAllocItem);
		List<CashSaleReturnItem> csrItems = csrItemDao.getCashSaleReturnItems(cashSaleReturnId, itemId, warehouseId, true);
		List<SaleItem> saleItems = new ArrayList<SaleItem>(csrItems);
		processItems(allocatedItems, currentAllocItem, saleItems);
	}

	/**
	 * Update the Quantity and Unit Cost for CAP Delivery Items.
	 */
	private void updateDeliveryItemUC(int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems, ItemTransaction currentAllocItem, int capDeliveryId) throws CloneNotSupportedException {
		logger.info("Current Allocated transaction for CAP Delivery item :::: "+currentAllocItem);
		List<CAPDeliveryItem> deliveryItems = deliveryItemDao.getDeliveryItems(capDeliveryId, itemId, warehouseId);
		List<SaleItem> saleItems = new ArrayList<SaleItem>(deliveryItems);
		processItems(allocatedItems, currentAllocItem, saleItems);
	}

	private void processItems(Queue<ItemTransaction> allocatedItems, ItemTransaction currentAllocItem,
			List<SaleItem> unprocessedSaleItems) throws CloneNotSupportedException {
		logger.info("Process the list of sale items before allocating.");
		ListProcessorUtil<SaleItem> remover = new ListProcessorUtil<SaleItem>();
		//Collect form ids from the list.
		List<Integer> formIds = remover.collectFormIds(unprocessedSaleItems);
		boolean isReference = false;
		logger.info("Checking the sale items if they are used as reference for returns.");
		if(currentAllocItem.getForm().equals(ItemTransaction.ACCT_SALE_FORM_NAME)) {
			isReference = accountSaleItemDao.isReferenceId(formIds);
		} else if(currentAllocItem.getForm().equals(ItemTransaction.CASH_SALE_FORM_NAME)) {
			isReference = csItemDao.isReferenceId(formIds);
		}

		SaleItemUtil<SaleItem> saleUtil = new SaleItemUtil<SaleItem>();
		List<SaleItem> saleItems = new ArrayList<SaleItem>(unprocessedSaleItems);
		if(!isReference && unprocessedSaleItems.size() > 1) {
			logger.info("Sale items were not used as reference. Removing the split price from the list.");
			//Summarise the list of sale items if the item is not used as reference and if it is more than 1.
			saleItems = saleUtil.getSummarisedSaleItems(unprocessedSaleItems);
		} else {
			logger.info("Sale items are used or are returns.");
			saleItems.addAll(unprocessedSaleItems);
		}

		logger.info("Allocating the processed sale items.");
		updateSaleItemUC(allocatedItems, currentAllocItem, saleUtil, saleItems, formIds, isReference);
	}

	private void updateSaleItemUC(Queue<ItemTransaction> allocatedItems, ItemTransaction currentAllocItem,
			SaleItemUtil<SaleItem> saleUtil, List<SaleItem> saleItems, List<Integer> formIds,
			boolean isReference) throws CloneNotSupportedException {
		String formName = currentAllocItem.getForm();
		logger.info("Updating the quantity and unit cost of the sale items of form: "+formName);
		List<SaleItem> toBeUpdatedItems = null;
		List<Integer> savedIds = new ArrayList<Integer>();
		Double qtyToBeWithdrawn = null;
		double allocQty = currentAllocItem.getQuantity();
		SaleItem splitItem = null;
		for (SaleItem saleItem : saleItems) {
			while(currentAllocItem != null) {
				if(qtyToBeWithdrawn == null) {
					qtyToBeWithdrawn = Math.abs(saleItem.getQuantity());
				}
				if(allocQty >= qtyToBeWithdrawn) {
					logger.debug("Allocated quantity is greater than the quantity from account sale.");
					saleItem.setUnitCost(currentAllocItem.getUnitCost());
					saleItem.setQuantity(qtyToBeWithdrawn);
					saleItem = processDiscAndAmt(saleUtil, toBeUpdatedItems, saleItem);
					// Update sale item.
					accountSaleItemDao.saveOrUpdate(saleItem); // UPDATE
					//Add the saved id.
					savedIds.add(saleItem.getId());
					allocQty = NumberFormatUtil.roundOffNumber((allocQty - qtyToBeWithdrawn), NumberFormatUtil.SIX_DECIMAL_PLACES);
					if(allocQty == 0.0) {
						logger.debug("Current allocated quantity is zero");
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
					}
					qtyToBeWithdrawn = null;
					break;
				} else {
					if(allocQty > 0) {
						logger.info("allocated qty is greater: "+currentAllocItem);
						splitItem =  (SaleItem) saleItem.clone();
						splitItem.setId(0);
						splitItem.setQuantity(allocQty);
						splitItem.setUnitCost(currentAllocItem.getUnitCost());
						splitItem = processDiscAndAmt(saleUtil, toBeUpdatedItems, splitItem);
						accountSaleItemDao.saveOrUpdate(splitItem); // SAVE
						qtyToBeWithdrawn = qtyToBeWithdrawn - allocQty;
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
					}
				}
			}
		}

		//Process the returns of the referenced items and split prices.
		processReturnsAndSplitPrice(isReference, formIds, savedIds, formName);
		logger.info("Successfully updated the quantity and unit cost of the sale item.");
		logger.debug("Freeing up memory allocation for sale item.");
		toBeUpdatedItems = null;
		splitItem = null;
		savedIds = null;
	}

	private void updateRTSItemUC(int itemId, Queue<ItemTransaction> allocatedItems, ItemTransaction currentAllocItem, int apInvoiceId) {
		logger.info("Current Allocated transaction for rts - eb item :::: "+currentAllocItem);
		Double allocQty = currentAllocItem.getQuantity();
		List<RReturnToSupplierItem> rtsItems = rtsItemDao.getRtsItems(apInvoiceId, itemId);

		ListProcessorUtil<RReturnToSupplierItem> remover = new ListProcessorUtil<RReturnToSupplierItem>();
		List<Integer> toBeDeletedIds = remover.collectFormIds(rtsItems);
		logger.debug("To be deleted ids: "+toBeDeletedIds.size());
		rtsItemDao.delete(toBeDeletedIds);

		logger.debug("Processing the rts -eb items. Removing the duplicate items.");
		List<RReturnToSupplierItem> processedItems = remover.removeDuplicate(rtsItems);
		for (RReturnToSupplierItem rtsi : processedItems) {
			//Create a new instance of Stock Adjustment Item
			while (currentAllocItem != null) {
				allocQty = currentAllocItem.getQuantity();
				rtsi.setId(0);
				rtsi.setQuantity(allocQty);
				rtsi.setUnitCost(currentAllocItem.getUnitCost());
				rtsItemDao.save(rtsi); // SAVE
				currentAllocItem = getNextAllocItem(allocatedItems);
				allocQty = getAllocatedQty(currentAllocItem);
				logger.info("Current Allocated item: "+currentAllocItem);
			}
		}

		logger.debug("Freeing up memory allocation for RTS-EB item.");
		remover = null;
		processedItems = null;
		toBeDeletedIds = null;
		logger.info("Successfully updated the quantity and unit cost of item transaction "
				+ "from RTS-EB form.");
	}


	private void processReturnsAndSplitPrice(boolean isReference,
			List<Integer> formIds, List<Integer> savedIds, String formName) {
		if(!isReference) {
			List<Domain> toBeDeleted = new ArrayList<Domain>();
			int frequency = 0;
			for (Integer id : formIds) {
				//Delete the items that were not updated.
				frequency = Collections.frequency(savedIds, id);
				if(frequency == 0) {
					if(formName.equals(ItemTransaction.ACCT_SALE_FORM_NAME)
							|| formName.equals(ItemTransaction.ACCT_SALE_RETURN_FORM_NAME)
							|| formName.equals(ItemTransaction.ACCT_SALE_WS_FORM_NAME)
							|| formName.equals(ItemTransaction.ACCT_SALE_RET_WS_FORM_NAME)) {
						logger.debug("Deleting the account sale item id: "+id);
						toBeDeleted.add(accountSaleItemDao.get(id));
					} else if(formName.equals(ItemTransaction.CASH_SALE_FORM_NAME)) {
						logger.debug("Deleting the cash sale item id: "+id);
						toBeDeleted.add(csItemDao.get(id));
					} else if(formName.equals(ItemTransaction.CASH_SALE_RETURN_FORM_NAME)) {
						logger.debug("Deleting the cash sale return item id: "+id);
						toBeDeleted.add(csrItemDao.get(id));
					} else if(formName.equals(ItemTransaction.CAP_DELIVERY_FORM_NAME)) {
						logger.debug("Deleting the cap delivery id: "+id);
						toBeDeleted.add(deliveryItemDao.get(id));
					}
				}
			}

			if(!toBeDeleted.isEmpty()) {
				logger.info("Deleting sale item ids of form: "+formName);
				for (Domain tbd : toBeDeleted) {
					logger.debug("Deleting the id: "+tbd);
					accountSaleItemDao.delete(tbd);
				}
			}

			//Setting the memory allocation to null
			toBeDeleted = null;
		} else {
			logger.info("Updating the unit cost of the sale item returns.");
			List<Domain> toBeUpdatedReturns = new ArrayList<Domain>();
			SaleItem reference = null;
			List<SaleItem> items = new ArrayList<SaleItem>();
			for (Integer id : formIds) {
				if(formName.equals(ItemTransaction.ACCT_SALE_FORM_NAME)) {
					reference = accountSaleItemDao.get(id);
					items.addAll(accountSaleItemDao.getSalesReturnItem(id));
				} else if(formName.equals(ItemTransaction.CASH_SALE_FORM_NAME)) {
					reference = csItemDao.get(id);
					items.addAll(csrItemDao.getCSaleReturnItems(id));
				}

				//Loop through the returns of the items and update the unit cost.
				for (SaleItem si : items) {
					logger.debug("Updating the sale item returns of form: "+formName);
					if(reference != null) {
						logger.trace("Set the unit cost of the return.");
						si.setUnitCost(reference.getUnitCost());
						toBeUpdatedReturns.add(si);
					}
				}
			}

			if(!toBeUpdatedReturns.isEmpty()) {
				logger.debug("Saving the updated sale items of form: "+formName);
				for (Domain tbu : toBeUpdatedReturns) {
					logger.trace("Updated id: "+tbu.getId());
					accountSaleItemDao.saveOrUpdate(tbu);
				}
			}

			//Setting the memory allocation to null
			toBeUpdatedReturns = null;
		}
	}

	/**
	 * Process the discount and amount of the account sale item.
	 */
	private SaleItem processDiscAndAmt(SaleItemUtil<SaleItem> saleUtil,
			List<SaleItem> toBeUpdatedItems, SaleItem saleItem) {
		logger.info("Processing discount and amount of the sale item id: "+saleItem.getId());
		toBeUpdatedItems = new ArrayList<SaleItem>(1);
		toBeUpdatedItems.add(saleItem);
		return saleUtil.processDiscountAndAmount(toBeUpdatedItems, itemDiscountService).get(0);
	}

	/**
	 * Get the next allocated item from the list.
	 */
	private ItemTransaction getNextAllocItem(Queue<ItemTransaction> allocatedItems) {
		logger.debug("Get the next allocated item transaction on the list.");
		return allocatedItems.poll();
	}

	/**
	 * Get the quantity of the current allocated item transaction.
	 * Returns zero if allocation is null.
	 */
	private double getAllocatedQty(ItemTransaction currentAllocItem) {
		if(currentAllocItem != null) {
			return currentAllocItem.getQuantity();
		}
		logger.debug("Current allocated transaction is null.");
		return 0;
	}
}
