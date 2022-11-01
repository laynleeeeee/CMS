package eulap.eb.service.inventory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Domain;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.ItemDao;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.BaseItem;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.domain.hibernate.RepackingItem;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.service.AccountSaleItemService;
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.CashSaleItemService;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.ItemService;
import eulap.eb.service.RepackingService;
import eulap.eb.service.StockAdjustmentService;

/**
 * This will loop to all transaction from the begging of encoding to the current
 * date. Will run through date so that we will be sore that everything will be
 * handles. Allocation run through all items one by one.
 * 

 * 
 */
@Service
public class CostAllocatorHandler {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private CashSaleItemService csItemService;
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private AccountSaleItemService accountSaleItemService;
	@Autowired
	private RepackingService repackingService;
	@Autowired
	private StockAdjustmentService stockAdjustmentService;
	private static Logger logger = Logger.getLogger(CostAllocatorHandler.class);

	/**
	 * Will start the allocation. Make sure that there are no transaction being
	 * process while running this method.
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public boolean doAllocation(int companyId, int warehouseId, Date startDate,
			AllocatorStatusHandler statusHandler, int pageNumber)
			throws CloneNotSupportedException {
		Calendar currentDate = Calendar.getInstance();
		boolean hasNext = false;
		//int pageNumber = 1;
		List<Item> processedRepacked = null;
		//while (!isCompleted) {
			// Handle first the re-packed items
	
			if (processedRepacked == null) {
				processedRepacked = new ArrayList<Item>();
				Page<RepackingItem> repackedItems = repackingService.getAllRepackedItems();
				for (RepackingItem ri : repackedItems.getData()) {
					Item item = ri.getFromItem();
					if (!item.isActive())
						continue;
					if (!processedRepacked.contains(item)) {
						if (pageNumber == 0) {
							boolean isSuccessful = processTransactions(item, companyId, warehouseId, startDate, currentDate, statusHandler, processedRepacked);
							if (isSuccessful){
								statusHandler.completed(item, "Done");
							}
						}
						processedRepacked.add(item);
					}
				}
				hasNext = true;
			}
		if (pageNumber != 0) {
		// Get the status of the first batch of the item.
			// Two item per page so that user will see the changes 
			Page<Item> page = itemService.getRetailItems("", "", null, null, 1, false,  pageNumber++, 10);
			for (Item item : page.getData()) {
				boolean isSuccessful = processTransactions(item, companyId, warehouseId, startDate, currentDate, statusHandler, processedRepacked);
				if (isSuccessful){
					statusHandler.completed(item, "Done");
				}
			}
	
			if (page.getLastPage() < pageNumber) {
				hasNext = false;
			}
		}
		return hasNext;
	}

	private boolean processTransactions(Item item, int companyId, int warehouseId, Date startDate, Calendar endDate,
				AllocatorStatusHandler statusHandler, List<Item> processRepacked) throws CloneNotSupportedException {
		// Oldest date of the item transaction.
		boolean isSuccess = true;
		if (processRepacked.contains(item))
			return true;
		statusHandler.progressReport(item, "Oldest transaction is " + startDate);
		Calendar transactionDate = Calendar.getInstance();
		logger.info("item ############################################################# :  "
				+ item.getDescription());
		transactionDate.setTime(startDate);
		while (transactionDate.before(endDate)) {
			logger.info("date : " + transactionDate.getTime());	
			Calendar prevDay = Calendar.getInstance();
			prevDay.setTime(transactionDate.getTime());
			prevDay.set(Calendar.DAY_OF_MONTH, prevDay.get(Calendar.DAY_OF_MONTH) - 1);
			ItemAllocator<InventoryItem> allocator = new WeightedAverageAllocator<InventoryItem>(itemService, item.getId(),
					companyId, warehouseId, prevDay.getTime());
			Page<CashSale> cashSales = cashSaleService.getTransactionPerDate(item.getId(), warehouseId, 
															transactionDate.getTime());

			List<Domain> inventoryTransactions = new ArrayList<Domain>();
			// Cash Sale
			for (CashSale cs : cashSales.getData()) {
				List<CashSaleItem> csItems = csItemService.getAllCashSaleItems(cs.getId());
				cs.setCashSaleItems(csItems);
				inventoryTransactions.add(cs);
			}

			// Account Sale
			Page<ArTransaction> transactions =
						arTransactionService.getTransactionsByDate(item.getId(), warehouseId,
																	transactionDate.getTime());
			for (ArTransaction transaction : transactions.getData()) {
				List<AccountSaleItem> asItems = accountSaleItemService.getAllAccountSaleItems(transaction.getId());
				transaction.setAccountSaleItems(asItems);
				inventoryTransactions.add(transaction);
			}

			// Re-packing
			Page<Repacking> repackingTransactions =
					repackingService.getRepackingTransactions(item.getId(), warehouseId,
																transactionDate.getTime());
			inventoryTransactions.addAll(repackingTransactions.getData());

			// withdrawn or negative Stock adjustment

			Page<StockAdjustment> withdrawnAdjustment =
					stockAdjustmentService.getWithdrawnStockAdjustment(item.getId(), warehouseId,
																	transactionDate.getTime());
			inventoryTransactions.addAll(withdrawnAdjustment.getData());
			boolean allocationSuccessfull = processAllocation(statusHandler, item, allocator, inventoryTransactions);
			if (isSuccess)
				isSuccess = allocationSuccessfull;
			transactionDate.set(Calendar.DAY_OF_MONTH, transactionDate.get(Calendar.DAY_OF_MONTH) + 1);
		}
		return isSuccess;
	}

	private List<? extends InventoryItem> checkAndMergeSplitted (List<? extends InventoryItem> baseItems) {
		// merge splitted item
		List<Domain> tobeDeleted = new ArrayList<Domain>();
		List<InventoryItem> newList = new ArrayList<InventoryItem>();
		for (InventoryItem item : baseItems) {
			if (!(item instanceof BaseItem))
				continue;
			BaseItem baseItem = (BaseItem) item;
			boolean isSplitPrice =  false;
			for (InventoryItem itemCompare : baseItems) {
				if (baseItem.equals(itemCompare))
					continue;
				if (!(itemCompare instanceof BaseItem))
					continue;
				BaseItem bICompare = (BaseItem) itemCompare;
				if (baseItem.isSplitWithUnitCost(bICompare)) {
					isSplitPrice = true;
					item.setQuantity(item.getQuantity() + bICompare.getQuantity()); // Merge the splitted price.
					tobeDeleted.add(bICompare);
					itemDao.delete(bICompare);
				}
			}
			if (!isSplitPrice)
				newList.add(item);
		}
		return newList;
	}
	
	private boolean processAllocation(AllocatorStatusHandler statusHandler, Item item,
			ItemAllocator<InventoryItem> allocator,
			List<Domain> withdrawalTransactions)
			throws CloneNotSupportedException {
		boolean isSuccess = true;
		// Sort by created data.
		Collections.sort(withdrawalTransactions, new Comparator<Domain>() {
			@Override
			public int compare(Domain item, Domain item2) {
				boolean isBefore = item.getCreatedDate().before(
						item2.getCreatedDate());
				return isBefore ? -1 : 1;
			}
		});
		
		List<Domain> toBeSaved = new ArrayList<Domain>();
		for (Domain transaction : withdrawalTransactions) {
			List<? extends InventoryItem> baseItems = null;
			if (transaction instanceof InventoryTransaction) {
				InventoryTransaction it = (InventoryTransaction) transaction;
				baseItems = it.getInventoryItems();
			} else if (transaction instanceof Repacking) {
				Repacking r = (Repacking) transaction;
				baseItems = r.getrItems();
			}
			baseItems = checkAndMergeSplitted(baseItems);
			for (InventoryItem inventoryItem : baseItems) {
				if (inventoryItem.getItemId() != item.getId())
					continue;
				for (InventoryItem ii : allocator.allocateCost(inventoryItem)) {
					if (ii instanceof RepackingItem) {
						RepackingItem ri = (RepackingItem) ii;
						boolean isSuccessRepacked = reallocateRepacked(statusHandler, ri);
						if (isSuccess)
							isSuccess = isSuccessRepacked;
					} else {
						if (ii.getUnitCost() == null || ii.getUnitCost() == 0) {
							statusHandler.error(item,"Error while allocating this item. Needed manual intervention");
							if (isSuccess)
								isSuccess = false;
						}
					}
					toBeSaved.add((Domain) ii);
				}
			}
		}
		itemDao.batchSaveOrUpdate(toBeSaved);
		return isSuccess;
	}

	// Re-allocate the re-packed items.
	private boolean reallocateRepacked(AllocatorStatusHandler statusHandler, RepackingItem ri) {
		double fromQuantity = ri.getQuantity();
		if (ri.getUnitCost() == null || ri.getUnitCost() == 0) {
			statusHandler.error(ri.getFromItem(), "Error while allocating this item. Needed manual intervention");
			logger.info("+++++++++++++++++++++++++++++++++++++++ " + ri.getItemId());
			return false;
		}
		double toQauntity = ri.getRepackedQuantity();
		// Total unit cost of the item to be re-packed.
		double totalUnitCost = fromQuantity * ri.getUnitCost();
		// Unit cost of re-packed items.
		double repackedUnitCost = totalUnitCost / toQauntity;
		ri.setRepackedUnitCost(NumberFormatUtil.roundOffTo2DecPlaces(repackedUnitCost));
		return true;
	}

	public interface AllocatorStatusHandler {
		/**
		 * Report the progress of the item to be allocated with unit cost.
		 * 
		 * @param item
		 *            The item that will be allocated.
		 * @param str
		 *            progress message
		 */
		void progressReport(Item item, String str);
		
		/**
		 * Report an error while allocating unit cost.
		 * @param item
		 * @param it
		 * @param message
		 */
		void error (Item item, String message);

		/**
		 * Called once the operation is completed.
		 * 
		 * @param message
		 */
		void completed(Item item, String message);
	}
}
