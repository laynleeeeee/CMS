package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountSaleItemDao;
import eulap.eb.dao.ArLineDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.AccountSalesPoItem;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Class that handles the business logic of {@link AccountSaleItem}

 *
 */
@Service
public class AccountSaleItemService {
	private static Logger logger = Logger.getLogger(AccountSaleItemService.class);
	@Autowired
	private AccountSaleItemDao accountSaleItemDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private ArLineDao arLineDao;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private AccountSalePoService accountSalePoService;

	/**
	 * Get all the account sale items.
	 * @return The list of account sale items.
	 */
	public List<AccountSaleItem> getAccountSaleItems() {
		return (List<AccountSaleItem>) accountSaleItemDao.getAll();
	}

	/**
	 * Get account sale items with other charges
	 * @param arTransaction The AR transaction object
	 * @param arTransactionId The AR transaction id
	 * @return The AR transaction object
	 */
	public ArTransaction getAcctSalesWithItems(ArTransaction arTransaction, Integer arTransactionId) {
		//Set account sales items
		arTransaction.setAccountSaleItems(getAccountSaleItems(arTransactionId));
		//Set ar lines/other charges
		arTransaction.setArLines(getOtherCharges(arTransactionId));
		return arTransaction;
	}

	/**
	 * Get the other charges in AR Transaction
	 * @param arTransactionId The AR transaction id
	 */
	public List<ArLine> getOtherCharges(Integer arTransactionId) {
		//Get the list of AR Lines/Other charges.
		List<ArLine> arLines = arLineDao.getArLines(arTransactionId);
		if(!arLines.isEmpty()) {
			List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
			otherCharges.addAll(arLines);
			//Process the list. Set the AR Line setup name and uom.
			List<AROtherCharge> processedList = cashSaleService.getDetailOtherCharges(otherCharges);
			List<ArLine> procesedArLines = new ArrayList<ArLine>();
			for (AROtherCharge oc : processedList) {
				procesedArLines.add((ArLine) oc);
			}
			return procesedArLines;
		} else {
			return null;
		}
	}

	/**
	 * Get the list of account sales by account sale id for viewing.
	 * @param arTransactionId The ar transaction id.
	 * @return The list of account sale items.
	 */
	public List<AccountSaleItem> getAccountSaleItems(Integer arTransactionId) {
		return getAccountSaleItems(arTransactionId, true);
	}

	/**
	 * Get the list of account sales by account sale id.
	 * @param arTransactionId The ar transaction id.
	 * @return The list of account sale items.
	 */
	public List<AccountSaleItem> getAccountSaleItems(Integer arTransactionId, boolean isViewing) {
		SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil <AccountSaleItem>();
		ArTransaction arTransaction = arTransactionDao.get(arTransactionId);
		List<AccountSaleItem> accountSaleItems = 
				accountSaleItemDao.getAccountSaleItems(arTransactionId, null, null);
		double existingStocks = 0;
		double srp = 0;
		double addOn = 0;
		for (AccountSaleItem asItem : accountSaleItems) {
			asItem.setSalesRefId(asItem.getRefAccountSaleItemId());
			existingStocks = itemDao.getItemExistingStocks(asItem.getItemId(), asItem.getWarehouseId(), new Date());
			asItem.setExistingStocks(existingStocks);
			asItem.setOrigQty(asItem.getQuantity());
			asItem.setOrigWarehouseId(asItem.getWarehouseId());
			asItem.setStockCode(asItem.getItem().getStockCode());
			srp = asItem.getSrp();
			addOn = asItem.getItemAddOn() !=null ? asItem.getItemAddOn().getValue() : 0;
			asItem.setOrigSrp(srp-addOn);
		}

		List<AccountSaleItem> processedList = null;
		int transactionTypeId = arTransaction.getTransactionTypeId();
		if (transactionTypeId == ArTransactionType.TYPE_ACCOUNT_SALE) {
			processedList = saleItemUtil.processSaleItemsForViewing(accountSaleItems);
			for (AccountSaleItem asi : processedList) {
				AccountSalesPoItem accountSalesPoItem
					= accountSalePoService.getASPOItemByASOIEBObject(asi.getEbObjectId());
				if(accountSalesPoItem != null) {
					asi.setOrigRefObjectId(accountSalesPoItem.getEbObjectId());
					asi.setReferenceObjectId(accountSalesPoItem.getEbObjectId());
				}
				double origSrp = asi.getItemSrp().getSrp();
				double computeAddOn = SaleItemUtil.computeAddOn(origSrp, asi.getQuantity(), asi.getSrp());
				asi.setAddOn(computeAddOn);
				asi.setOrigSrp(origSrp);
			}
			return processedList;
		} else {
			// for return and exchange
			processedList = new ArrayList<>();
			if(isViewing){
				List<AccountSaleItem> returns =
						SaleItemUtil.<AccountSaleItem>filterSaleReturnItems(accountSaleItems, true);
				for (AccountSaleItem asItem : returns) {
					double origSrp = asItem.getItemSrp().getSrp();
							double computeAddOn = SaleItemUtil.computeAddOn(origSrp, asItem.getQuantity(), asItem.getSrp());
							asItem.setAddOn(computeAddOn);
							asItem.setOrigSrp(origSrp);
							asItem.setRefQuantity (totalRefQty(arTransaction.getAccountSaleId(), asItem.getItemId(), asItem.getWarehouseId()));
				}
				processedList = saleItemUtil.processSaleItemsForViewing(returns);
			}

			List<AccountSaleItem> exchanges =
					SaleItemUtil.<AccountSaleItem>filterSaleReturnItems(accountSaleItems, false);
			for (AccountSaleItem asri : exchanges) {
				double origSrp = asri.getItemSrp().getSrp();
				double computeAddOn = SaleItemUtil.computeAddOn(origSrp, asri.getQuantity(), asri.getSrp());
				asri.setAddOn(computeAddOn);
				asri.setOrigSrp(origSrp);
			}
			processedList.addAll(saleItemUtil.processSaleItemsForViewing(exchanges));
			return processedList;
		}
	}

	/**
	 * Get all of the account sale item. 
	 * @param arTransactionId The transaction id. 
	 */
	public List<AccountSaleItem> getAllAccountSaleItems (Integer arTransactionId) {
		List<AccountSaleItem> items = accountSaleItemDao.getAccountSaleItems(arTransactionId, null, null);
		SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
		return saleItemUtil.getSummarisedSaleItems(items);
	}

	public double totalRefQty(Integer arTransactionId, Integer itemId, Integer warehouseId) {
		double totalRefQty = 0.0;
		List<AccountSaleItem> items = accountSaleItemDao.getAccountSaleItems(arTransactionId, itemId, warehouseId);
		for (AccountSaleItem accountSaleItem : items) {
			totalRefQty += accountSaleItem.getQuantity();
		}
		return totalRefQty;
	}

	/**
	 * Get the list of account sales
	 * @param arTransactionId The transaction id.
	 * @return The list of account sales.
	 */
	public List<AccountSaleItem> getASItemPrintOut(Integer arTransactionId, int transactionTypeId) {
		List<AccountSaleItem> accountSaleItems = 
				accountSaleItemDao.getAccountSaleItems(arTransactionId, null, null);
		SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
		List<AccountSaleItem> returns =
				SaleItemUtil.<AccountSaleItem>filterSaleReturnItems(accountSaleItems, true);
		List<AccountSaleItem> processedAccountSaleItems = saleItemUtil.getSummarisedSaleItems(returns);
		List<AccountSaleItem> exchange =
				SaleItemUtil.<AccountSaleItem>filterSaleReturnItems(accountSaleItems, false);
		processedAccountSaleItems.addAll(saleItemUtil.getSummarisedSaleItems(exchange));
		if(processedAccountSaleItems != null && !processedAccountSaleItems.isEmpty()) {
			saleItemUtil.generateSaleItemPrintout(processedAccountSaleItems);
		}
		return processedAccountSaleItems;
	}

	public List<AccountSaleItem> getSummarisedList(Integer arTransactionId) {
		List<AccountSaleItem> accountSaleItems = 
				accountSaleItemDao.getAccountSaleItems(arTransactionId, null, null);
		SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
		List<AccountSaleItem> returns =
				SaleItemUtil.<AccountSaleItem>filterSaleReturnItems(accountSaleItems, true);
		List<AccountSaleItem> processedAccountSaleItems = saleItemUtil.processSaleItemsForViewing(returns);
		List<AccountSaleItem> exchange =
				SaleItemUtil.<AccountSaleItem>filterSaleReturnItems(accountSaleItems, false);
		processedAccountSaleItems.addAll(saleItemUtil.processSaleItemsForViewing(exchange));
		return processedAccountSaleItems;
	}

	/**
	 * Checks if the cash sale item has an invalid item.
	 * @param accountSaleItems The list of cash sale items.
	 * @return True if all stock codes are valid, otherwise false.
	 */
	public boolean hasInvalidItem (List<AccountSaleItem> accountSaleItems) {
		for (AccountSaleItem asItem : accountSaleItems) {
			if (asItem.getItemId() == null)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the cash sale item has no warehouse.
	 * @param accountSaleItems The list of cash sale items.
	 * @return True if all cash sale items has warehouse, otherwise false.
	 */
	public boolean hasNoWarehouse (List<AccountSaleItem> accountSaleItems) {
		for (AccountSaleItem asItem : accountSaleItems) {
			if (asItem.getWarehouseId() == null)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the cash sale item has no quantity.
	 * @param cashSaleItems The list of cash sale items.
	 * @return True if all cash sale items has no quantity, otherwise false.
	 */
	public boolean hasNoQty (List<AccountSaleItem> accountSaleItems) {
		for (AccountSaleItem asItem : accountSaleItems) {
			if (asItem.getQuantity() == null) 
				return true;
		}
		return false;
	}
		
	/**
	 * Checks if the cash sale item has negative quantity.
	 * @param cashSaleItems The list of cash sale items.
	 * @return True if all cash sale items has no amount, otherwise false.
	 */
	public boolean hasNegQty (List<AccountSaleItem> accountSaleItems) {
		for (AccountSaleItem asItem : accountSaleItems) {
			if (asItem.getQuantity() != null) {
				if (asItem.getQuantity() <= 0.0)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the list of account sale return items has at least 1 return item.
	 * @param asrItems Account sale return items.
	 * @return True if no return items, otherwise false. 
	 */
	public boolean hasNoReturnItems (List<AccountSaleItem> asrItems) {
		if (asrItems != null && !asrItems.isEmpty()) {
			for (AccountSaleItem asri : asrItems) {
				if (asri.getRefAccountSaleItemId() != null) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks if the list of account sale return items has positive quantity or absolute 
	 * value of quantity is greater than the reference quantity.
	 * @param asrItems Account sale return items.
	 * @return True if has positive quantity, otherwise false. 
	 */
	public boolean hasInvalidReturnItem (List<AccountSaleItem> asrItems) {
		if (asrItems != null && !asrItems.isEmpty()) {
			for (AccountSaleItem asri : asrItems) {
				if (asri.getRefAccountSaleItemId() != null && asri.getQuantity() != null) {
					if (asri.getQuantity() >= 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public List<AccountSaleItem> getASRItemsByReference(int refAccountSaleId) {
		return accountSaleItemDao.getASRItemsByReference(refAccountSaleId);
	}

	/**
	 * Checks if the list of account sale return items has unit cost or none.
	 * @param asrItems The list of account sale return items.
	 * @return True if no unit cost, otherwise false. 
	 */
	public boolean hasNoUnitCost(List<AccountSaleItem> asrItems) {
		if (asrItems != null && !asrItems.isEmpty()) {
			for (AccountSaleItem asri : asrItems) {
				if (asri.getUnitCost() != null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Search Account Sale forms.
	 * @param transactionTypeId The type of Account Sale.
	 * @param searchCriteria The search keyword.
	 * @return The account sales form that matches the search keyword.
	 */
	public List<FormSearchResult> search(Integer transactionTypeId, String searchCriteria) {
		logger.info("Searching Account Sale type: "+transactionTypeId);
		logger.debug("Searching for: "+searchCriteria.trim());
		// Replace comma in searchCriteria with ""
		String searchCriteriaFinal = searchCriteria.replace(",", "");

		Page<ArTransaction> arTransactions = 
				arTransactionDao.searchARTransactions(transactionTypeId, searchCriteriaFinal, new PageSetting(1));  
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		for (ArTransaction transaction : arTransactions.getData()) {
			logger.trace("Retrieved Account Sale No. " + transaction.getSequenceNumber());
			title = String.valueOf(transaction.getFormattedASNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			if (transaction.getCompany() != null)
				properties.add(ResultProperty.getInstance("Company", transaction.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Customer", transaction.getArCustomer().getNumberAndName()));
			properties.add(ResultProperty.getInstance("Customer Account", transaction.getArCustomerAccount().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(transaction.getTransactionDate())));
			if (transaction.getDueDate() != null)
				properties.add(ResultProperty.getInstance("Due Date", DateUtil.formatDate(transaction.getDueDate())));
			status = formStatusDao.get(transaction.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(transaction.getId(), title, properties));
		}
		return result;
	}

	/**
	 * Get the remaining quantity of the sale item.
	 * @param refAcctSaleItemId The id of the Account Sales object.
	 * @return The remaining quantity  to be returned.
	 */
	public double getRemainingQty(int refAcctSaleItemId) {
		return accountSaleItemDao.getRemainingQty(refAcctSaleItemId);
	}

	/**
	 * Get the remaining quantity if the saved sale items
	 * @param saleRefId The sale form reference id
	 * @param itemId The item id
	 * @param warehouseId The warehouse id
	 * @return The remaining quantity
	 */
	public double getRemainingQty(Integer saleRefId, Integer itemId, Integer warehouseId) {
		return accountSaleItemDao.getRemainingQty(saleRefId, itemId, warehouseId);
	}

	/**
	 * Get the list of Account Sale Items
	 * @param arTransactionId The AR Transaction id.
	 * @return The list of Account Sale Items.
	 */
	public List<AccountSaleItem> getAcctSaleItems (Integer arTransactionId) {
		return accountSaleItemDao.getAccountSaleItems(arTransactionId, null, null);
	}
}
