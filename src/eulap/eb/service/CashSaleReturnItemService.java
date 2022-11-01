package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.CashSaleItemDao;
import eulap.eb.dao.CashSaleReturnDao;
import eulap.eb.dao.CashSaleReturnItemDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnItem;

/**
 * Class that handles the business logic of {@link CashSaleReturnItemService}

 *
 */
@Service
public class CashSaleReturnItemService {
	private static Logger logger = Logger.getLogger(CashSaleReturnItemService.class);
	@Autowired
	private CashSaleReturnService cashSaleReturnService;
	@Autowired
	private CashSaleReturnItemDao cashSaleReturnItemDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private CashSaleItemDao cashSaleItemDao;
	@Autowired
	private CashSaleReturnDao cashSaleReturnDao;

	/**
	 * Get all the cash sales return items.
	 * @return The list of cash sales return items.
	 */
	public List<CashSaleReturnItem> getCashSaleReturnItems() {
		return (List<CashSaleReturnItem>) cashSaleReturnItemDao.getAll();
	}

	/**
	 * Get the list of cash sales return items by cash sales return id.
	 * @param cashSaleReturnId The cash sales return id.
	 * @return The list of cash sales return items.
	 */
	public List<CashSaleReturnItem> getCashSaleReturnItems(Integer cashSaleReturnId) {
		List<CashSaleReturnItem> csrItems = cashSaleReturnService.getCsReturnItems(cashSaleReturnId);
		Double existingStocks = null;
		double srp = 0;
		double addOn = 0;
		for (CashSaleReturnItem csri : csrItems) {
			existingStocks = itemDao.getItemExistingStocks(csri.getItemId(), csri.getWarehouseId(), new Date());
			csri.getItem().setExistingStocks(existingStocks);
			csri.setOrigQty(csri.getQuantity());
			csri.setStockCode(csri.getItem().getStockCode());
			csri.setExistingStocks(existingStocks);
			csri.setOrigWarehouseId(csri.getWarehouseId());
			csri.setSalesRefId(csri.getCashSaleItemId());
			srp = csri.getSrp();
			addOn = csri.getItemAddOn() !=null ? csri.getItemAddOn().getValue() : 0;
			csri.setOrigSrp(srp-addOn);
		}

		SaleItemUtil<CashSaleReturnItem> saleItemUtil = new SaleItemUtil <CashSaleReturnItem>();
		CashSaleReturn cashSaleReturn = cashSaleReturnDao.get(cashSaleReturnId);
		List<CashSaleReturnItem> returnItems = 
				SaleItemUtil.<CashSaleReturnItem>filterSaleReturnItems(csrItems, true);
		for (CashSaleReturnItem csrItem : returnItems) {
			if(cashSaleReturn.getCashSaleId() != null){
				csrItem.setRefQuantity (totalRefQty(cashSaleReturn.getCashSaleId(), csrItem.getItemId(), csrItem.getWarehouseId()));
			} else if (cashSaleReturn.getRefCashSaleReturnId() != null){
				csrItem.setRefQuantity (totalCSRRefQty(cashSaleReturn.getRefCashSaleReturnId(), csrItem.getItemId(), csrItem.getWarehouseId(), true));
			}
			double origSrp = csrItem.getItemSrp().getSrp();
			double computeAddOn = SaleItemUtil.computeAddOn(origSrp, csrItem.getQuantity(), csrItem.getSrp());
			csrItem.setAddOn(computeAddOn);
			csrItem.setOrigSrp(origSrp);
		}
		List<CashSaleReturnItem> processedCSRIs = saleItemUtil.processSaleItemsForViewing(returnItems);

		List<CashSaleReturnItem> exchangeItems = SaleItemUtil.<CashSaleReturnItem>filterSaleReturnItems(csrItems, false);
		for (CashSaleReturnItem csri : exchangeItems) {
			double origSrp = csri.getItemSrp().getSrp();
			double computeAddOn = SaleItemUtil.computeAddOn(origSrp, csri.getQuantity(), csri.getSrp());
			csri.setAddOn(computeAddOn);
			csri.setOrigSrp(origSrp);
		}
		processedCSRIs.addAll(saleItemUtil.processSaleItemsForViewing(exchangeItems));
		return processedCSRIs;
	}

	private double totalRefQty(Integer cashSaleId, Integer itemId, Integer warehouseId) {
		double totalRefQty = 0.0;
		List<CashSaleItem> items = cashSaleItemDao.getCashSaleItems(cashSaleId, itemId, warehouseId);
		for (CashSaleItem csItem : items) {
			totalRefQty += csItem.getQuantity();
		}
		return totalRefQty;
	}

	private double totalCSRRefQty(Integer cashSaleReturnId, Integer itemId, Integer warehouseId, boolean isExchangedItems) {
		double totalRefQty = 0.0;
		List<CashSaleReturnItem> items = cashSaleReturnItemDao.getCashSaleReturnItems(cashSaleReturnId, itemId, warehouseId, isExchangedItems);
		for (CashSaleReturnItem csItem : items) {
			totalRefQty += csItem.getQuantity();
		}
		return totalRefQty;
	}

	/**
	 * Process the cash sale items.
	 * @param cashSaleReturnItems The list of cash sale items.
	 * @return The process cash sale items.
	 */
	public List<CashSaleReturnItem> processCashSaleReturnItems(List<CashSaleReturnItem> cashSaleReturnItems) {
		List<CashSaleReturnItem> ret = new ArrayList<CashSaleReturnItem>();
		if (cashSaleReturnItems != null && !cashSaleReturnItems.isEmpty()) {
			for (CashSaleReturnItem csrItem : cashSaleReturnItems) {
				String stockCode = csrItem.getStockCode();
				Double quantity = csrItem.getQuantity();
				boolean hasStockCode = stockCode != null && !stockCode.isEmpty();
				boolean hasQty = quantity != null && quantity != 0.0;
				if (hasStockCode || hasQty) 
					ret.add(csrItem);
			}
		}
		return ret;
	}

	/**
	 * Checks if the cash sale item has an invalid item.
	 * @param cashSaleReturnItems The list of cash sale items.
	 * @return True if all stock codes are valid, otherwise false.
	 */
	public boolean hasInvalidItem (List<CashSaleReturnItem> cashSaleReturnItems) {
		for (CashSaleReturnItem csrItem : cashSaleReturnItems) {
			if (csrItem.getItemId() == null)
				return true;
		}
		return false;
	}

	/**
	 * Checks if the cash sale item has no warehouse.
	 * @param cashSaleReturnItems The list of cash sale items.
	 * @return True if all cash sale items has warehouse, otherwise false.
	 */
	public boolean hasNoWarehouse (List<CashSaleReturnItem> cashSaleReturnItems) {
		for (CashSaleReturnItem csrItem : cashSaleReturnItems) {
			if (csrItem.getWarehouseId() == null)
				return true;
		}
		return false;
	}

	/**
	 * Checks if the cash sale item has no amount.
	 * @param cashSaleReturnItems The list of cash sale items.
	 * @return True if all cash sale items has no amount, otherwise false.
	 */
	public boolean hasNoAmount (List<CashSaleReturnItem> cashSaleReturnItems) {
		for (CashSaleReturnItem csrItem : cashSaleReturnItems) {
			if (csrItem.getAmount() == null)
				return true;
		}
		return false;
	}

	/**
	 * Checks if the cash sale item has no quantity.
	 * @param cashSaleReturnItems The list of cash sale items.
	 * @return True if all cash sale items has no quantity, otherwise false.
	 */
	public boolean hasNoQty (List<CashSaleReturnItem> cashSaleReturnItems) {
		for (CashSaleReturnItem csrItem : cashSaleReturnItems) {
			if (csrItem.getQuantity() == null || csrItem.getQuantity() == 0.0)
				return true;
		}
		return false;
	}

	/**
	 * Get the list of cash sales return items by cash sales id.
	 * @param cashSaleReturnId The cash sales return id.
	 * @return The list of cash sales return items.
	 */
	public List<CashSaleReturnItem> getCSRItemsPrintOut(Integer cashSaleReturnId) {
		logger.info("Processing the list of CSR Items for the printout.");
		List<CashSaleReturnItem> csrItems = cashSaleReturnService.getCsReturnItems(cashSaleReturnId);
		SaleItemUtil<CashSaleReturnItem> saleItemUtil = new SaleItemUtil <CashSaleReturnItem>();
		List<CashSaleReturnItem> returnItems = 
				SaleItemUtil.<CashSaleReturnItem>filterSaleReturnItems(csrItems, true);
		List<CashSaleReturnItem> processedCSRIs = saleItemUtil.getSummarisedSaleItems(returnItems);
		List<CashSaleReturnItem> exchangeItems = SaleItemUtil.<CashSaleReturnItem>filterSaleReturnItems(csrItems, false);
		processedCSRIs.addAll(saleItemUtil.getSummarisedSaleItems(exchangeItems));
		processedCSRIs = saleItemUtil.generateSaleItemPrintout(processedCSRIs);
		return processedCSRIs;
	}

	/**
	 * Get the total cash sale amount of all cash sale items per 
	 * cash sale.
	 * @param cashSaleReturnId The cash sale id.
	 * @return The cash sale amount.
	 */
	public double getTotalCSRAmount (Integer cashSaleReturnId) {
		return cashSaleReturnItemDao.getTotalCSRAmount(cashSaleReturnId);
	}

	/**
	 * Checks if the list of cash sale return items has at least 1 return item.
	 * @param csrItems Cash sale return items.
	 * @return True if no return items, otherwise false. 
	 */
	public boolean hasNoReturnItems (List<CashSaleReturnItem> csrItems) {
		if (csrItems != null && !csrItems.isEmpty()) {
			for (CashSaleReturnItem csri : csrItems) {
				if (csri.getCashSaleItemId() != null || csri.getRefCashSaleReturnItemId() != null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the list of cash sale return items has positive quantity or absolute 
	 * value of quantity is greater than the reference quantity.
	 * @param csrItems Cash sale return items.
	 * @return True if has positive quantity, otherwise false. 
	 */
	public boolean hasInvalidReturnItem (List<CashSaleReturnItem> csrItems) {
		if (csrItems != null && !csrItems.isEmpty()) {
			for (CashSaleReturnItem csri : csrItems) {
				if ((csri.getCashSaleItemId() != null || csri.getRefCashSaleReturnItemId() != null)
						&& csri.getQuantity() != null) {
					if (csri.getQuantity() >= 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get the list of cash sale return items by cash sale reference.
	 * @param referenceId The cash sale item id.
	 * @return The list of returns of the reference.
	 */
	public List<CashSaleReturnItem> getCSRItemsByReference(int cashSaleId) {
		return cashSaleReturnItemDao.getCSRItemsByReference(cashSaleId);
	}

	/**
	 * Get all of the cash sale item line.
	 * @param cashSaleReturnId the cash sale return id.
	 */
	public List<CashSaleReturnItem> getAllCashSaleReturnItems(Integer cashSaleReturnId, boolean isSummariseItems) {
		List<CashSaleReturnItem> items = cashSaleReturnItemDao.getCashSaleReturnItems(cashSaleReturnId, null, null, true);
		for (CashSaleReturnItem item : items) {
			item.setOrigQty(item.getQuantity());
		}

		if(!isSummariseItems) {
			return items;
		}
		SaleItemUtil<CashSaleReturnItem> saleItemUtil = new SaleItemUtil<CashSaleReturnItem>();
		return saleItemUtil.getSummarisedSaleItems(items);
	}

	/**
	 * Get the list of cash sale return items by cash sale return reference.
	 * @param referenceId The reference cash sale return id.
	 * @param cashSaleReturnId The cash sale return id.
	 * @return The list of returns of the reference csr.
	 */
	public List<CashSaleReturnItem> getCSRItemsByReferenceCSR(int referenceId, int cashSaleReturnId) {
		return cashSaleReturnItemDao.getCSRItemsByReferenceCSR(referenceId, cashSaleReturnId);
	}

	/**
	 * Compute the remaining quantity of the sale item
	 * @param referenceId The reference cash sale/cash sale return id.
	 * @param itemId The item id
	 * @param warehouseId The warehouse id
	 * @param isCSAsReference True if the reference form used is cash sales, otherwise false
	 * @return The remaining stocks.
	 */
	public double getRemainingQty(int referenceId, int itemId, int warehouseId, boolean isCSAsReference) {
		return cashSaleReturnItemDao.getRemainingQty(referenceId, itemId, warehouseId, isCSAsReference);
	}

	/**
	 * Get the list of Cash Sale Return Items by cash sale return id.
	 * @param csrId The cash sale return id.
	 * @return The list of cash sale return items.
	 */
	public List<CashSaleReturnItem> getCSRItems(Integer csrId){
		return cashSaleReturnItemDao.getCashSaleReturnItems(csrId, null, null, true);
	}

}
