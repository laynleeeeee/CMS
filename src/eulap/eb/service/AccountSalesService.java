package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import eulap.common.domain.Domain;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.AccountSaleItemDao;
import eulap.eb.dao.ArLineDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.AccountSales;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ItemDiscount;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.oo.ObjectInfo;

/**
 * Handles business logic for Account Sale Order and Account Sale Return Form.


 *
 */
@Service
@Primary
public class AccountSalesService extends ArTransactionService {
	private final Logger logger = Logger.getLogger(AccountSalesService.class);
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private AccountSaleItemDao accountSaleItemDao;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private ArLineDao arLineDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private ObjectToObjectDao toObjectDao;

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		super.preFormSaving(form, workflowName, user);

		ArTransaction arTransaction = (ArTransaction) form;
		int transactionTypeId = arTransaction.getTransactionTypeId();
		SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
		List<AccountSaleItem> processedAsItems = saleItemUtil.processDiscountAndAmount(
				arTransaction.getAccountSaleItems(), itemDiscountService);
		double totalLineAmount = 0;
		double totalVAT = 0;
		for (AccountSaleItem asItem : processedAsItems) {
			asItem.setTransactionTypeId(transactionTypeId);
			totalLineAmount += asItem.getAmount();
			totalVAT += asItem.getVatAmount() != null ? (asItem.getAmount() < 0 ? -asItem.getVatAmount() : asItem.getVatAmount()) : 0;
		}
		List<ArLine> arLines = arTransaction.getArLines();
		for (ArLine oc : arLines) {
			totalLineAmount += oc.getAmount();
			totalVAT += oc.getVatAmount() != null ? oc.getVatAmount() : 0;
		}
		arTransaction.setAccountSaleItems(processedAsItems);
		double wtAmount = (arTransaction.getWtAmount() != null ? arTransaction.getWtAmount() : 0);
		arTransaction.setAmount((NumberFormatUtil.roundOffTo2DecPlaces(totalLineAmount)
				+ NumberFormatUtil.roundOffTo2DecPlaces(totalVAT)) - NumberFormatUtil.roundOffTo2DecPlaces(wtAmount));
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		ArTransaction arTransaction = (ArTransaction) form;
		boolean isNew = arTransaction.getId() == 0;
		int transactionTypeId = arTransaction.getTransactionTypeId();
		arTransaction.setTransactionNumber(getTrLabel(transactionTypeId) + arTransaction.getSequenceNumber());
		// Handle the saving of ar transaction
		super.saveForm(form, workflowName, user);

		if (!isNew) { // Delete the previous saved data.
			List<AccountSaleItem> savedASItems = accountSaleItemDao.getAccountSaleItems(arTransaction.getId(), null, null);
			if (!savedASItems.isEmpty()) {
				List<Integer> toBeDeletedASItems = new ArrayList<Integer>();
				for (AccountSaleItem asItem : savedASItems)
					toBeDeletedASItems.add(asItem.getId());
				accountSaleItemDao.delete(toBeDeletedASItems);
				toBeDeletedASItems = null;
			}
		}

		saveAccountSalesReference(arTransaction, user);

		List<AccountSaleItem> asItems = arTransaction.getAccountSaleItems();
		if (asItems != null && !asItems.isEmpty()) {
			List<Domain> toBeSavedItems = new ArrayList<Domain>();
			SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
			Map<AllocatorKey, WeightedAveItemAllocator<AccountSaleItem>> itemId2CostAllocator =
					new HashMap<AllocatorKey, WeightedAveItemAllocator<AccountSaleItem>>();
			logger.debug("Allocating the Account Sales / Account Sales Return Item using the FIFO Cost Allocator.");
			AllocatorKey key = null;
			for (AccountSaleItem asItem : asItems) {
				logger.info("Saving account sale item : " + asItem);
				WeightedAveItemAllocator<AccountSaleItem> itemAllocator = itemId2CostAllocator.get(asItem.getItemId());
				if (itemAllocator == null) {
					itemAllocator = new WeightedAveItemAllocator<AccountSaleItem>(itemDao, itemService,
							asItem.getItemId(), asItem.getWarehouseId(), arTransaction.getTransactionDate());
					key = AllocatorKey.getInstanceOf(asItem.getItemId(), asItem.getWarehouseId());
					itemId2CostAllocator.put(key, itemAllocator);
				}
				try {
					if (transactionTypeId == ArTransactionType.TYPE_ACCOUNT_SALE) {
						List<AccountSaleItem> allocatedAsItems  = itemAllocator.allocateCost(asItem);
						allocatedAsItems = saleItemUtil.processDiscountAndAmount(allocatedAsItems, itemDiscountService);
						for (AccountSaleItem saleItem : allocatedAsItems) {
							logger.debug("Successfully allocated the item id: "+saleItem.getItemId()
									+" with unit cost: "+saleItem.getUnitCost());
							SaleItemUtil.setNullUnitCostToZero(saleItem);
							saleItem.setArTransactionId(arTransaction.getId());
							toBeSavedItems.add(saleItem);
						}
					} else if(transactionTypeId == ArTransactionType.TYPE_SALE_RETURN) {
						if (asItem.getRefAccountSaleItemId() == null) {
							List<AccountSaleItem> allocatedAsrItems = itemAllocator.allocateCost(asItem);
							allocatedAsrItems = saleItemUtil.processDiscountAndAmount(allocatedAsrItems, itemDiscountService);
							for (AccountSaleItem exchangedItem : allocatedAsrItems) {
								logger.debug("Successfully allocated the item id: "+exchangedItem.getItemId()
										+" with unit cost: "+exchangedItem.getUnitCost());
								exchangedItem.setArTransactionId(arTransaction.getId());
								SaleItemUtil.setNullUnitCostToZero(exchangedItem);
								toBeSavedItems.add(exchangedItem);
							}
						} else {
							asItem.setArTransactionId(arTransaction.getId());
							AccountSaleItem asi = accountSaleItemDao.get(asItem.getRefAccountSaleItemId());
							List<AccountSaleItem> asis = accountSaleItemDao.getAccountSaleItems(asi.getArTransactionId(), asi.getItemId(), asi.getWarehouseId());
							List<AccountSaleItem> allocatedSEItems = SaleItemUtil.processSalesReturn(asItem, asis, itemDiscountService);
							for (AccountSaleItem returnedItem : allocatedSEItems) {
								returnedItem.setArTransactionId(arTransaction.getId());
								toBeSavedItems.add(returnedItem);
							}
						}
					}
				} catch (CloneNotSupportedException e) {
					throw new RuntimeException(e);
				}
			}
			accountSaleItemDao.batchSave(toBeSavedItems);
		}
	}

	private void saveAccountSalesReference (ArTransaction arTransaction, User user) {
		if (arTransaction.getReferenceObjectId() != null) {
			toObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(arTransaction.getReferenceObjectId(),
					arTransaction.getEbObjectId(), AccountSales.ACCOUNT_SALES_PO_AR_TRANSACTION_OR_TYPE, user, new Date()));
		}
	}

	private String getTrLabel (int transactionTypeId) {
		switch (transactionTypeId) {
		case ArTransactionType.TYPE_ACCOUNT_SALE:
			return "AS-";
		case ArTransactionType.TYPE_SALE_RETURN:
			return "ASR-";
		}
		return "";
	}

	/**
	 * Converts the account sale ar transaction to account sale return.
	 * @param savedTransaction The account sale transaction.
	 * @return The account sale return object.
	 */
	public ArTransaction conv2NewTransaction (ArTransaction savedTransaction) {
		if (savedTransaction != null) {
			ArTransaction arTransaction = new ArTransaction();
			arTransaction.setAccountSaleId(savedTransaction.getId());
			arTransaction.setCompanyId(savedTransaction.getCompanyId());
			arTransaction.setCompany(savedTransaction.getCompany());
			arTransaction.setCustomerId(savedTransaction.getCustomerId());
			arTransaction.setArCustomer(savedTransaction.getArCustomer());
			arTransaction.setCustomerAcctId(savedTransaction.getCustomerAcctId());
			arTransaction.setArCustomerAccount(savedTransaction.getArCustomerAccount());
			arTransaction.setTermId(savedTransaction.getTermId());
			arTransaction.setTerm(savedTransaction.getTerm());
			if(savedTransaction.getWtAcctSettingId() != null) {
				arTransaction.setWtAcctSettingId(savedTransaction.getWtAcctSettingId());
				arTransaction.setWtAcctSetting(savedTransaction.getWtAcctSetting());
				arTransaction.setWtAmount(savedTransaction.getWtAmount());
			}
			if(savedTransaction.getTransactionTypeId() == ArTransactionType.TYPE_ACCOUNT_SALE) {
				arTransaction.setTransactionTypeId(ArTransactionType.TYPE_SALE_RETURN);
			} else {
				arTransaction.setTransactionTypeId(ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS);
			}
			arTransaction.setTransactionNumber(getTransactionPrefix(savedTransaction.getTransactionTypeId())
					+ " " + savedTransaction.getSequenceNumber());
			List<AccountSaleItem> accountSaleItems = getAcctSaleItems(savedTransaction.getId());
			if (accountSaleItems != null && !accountSaleItems.isEmpty()) {
				if (!savedTransaction.getTransactionTypeId().equals(ArTransactionType.TYPE_ACCOUNT_SALES_IS)) {
					SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
					accountSaleItems = SaleItemUtil.<AccountSaleItem>filterSaleReturnItems(accountSaleItems, false);
					accountSaleItems = saleItemUtil.processSaleItemsForViewing(accountSaleItems);
				}
				List<AccountSaleItem> asis = new ArrayList<AccountSaleItem>();
				for (AccountSaleItem accountSaleItem : accountSaleItems) {
					Item item = accountSaleItem.getItem();
					item.setItemSrps(new ArrayList<ItemSrp>());
					item.setItemDiscounts(new ArrayList<ItemDiscount>());
					AccountSaleItem asi = new AccountSaleItem();
					if (savedTransaction.getTransactionTypeId() == ArTransactionType.TYPE_ACCOUNT_SALE) {
						asi.setTransactionTypeId(ArTransactionType.TYPE_SALE_RETURN);
					} else {
						asi.setTransactionTypeId(ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS);
					}
					asi.setRefAccountSaleItemId(accountSaleItem.getId());
					asi.setSalesRefId(accountSaleItem.getId());
					asi.setStockCode(item.getStockCode());
					asi.setItem(item);
					asi.setItemId(accountSaleItem.getItemId());
					asi.setWarehouseId(accountSaleItem.getWarehouseId());
					asi.setQuantity(-accountSaleItem.getQuantity());
					asi.setSrp(accountSaleItem.getSrp());
					double origSrp = accountSaleItem.getItemSrp().getSrp();
					asi.setOrigSrp(origSrp);
					asi.setItemSrpId(accountSaleItem.getItemSrpId());
					asi.setRefQuantity(accountSaleItem.getQuantity());
					asi.setVatAmount(accountSaleItem.getVatAmount());
					asi.setTaxTypeId(accountSaleItem.getTaxTypeId());
					asi.setTaxType(accountSaleItem.getTaxType());
					asi.setItemDiscountId(accountSaleItem.getItemDiscountId());
					asi.setItemAddOnId(accountSaleItem.getItemAddOnId());
					asi.setDiscount(accountSaleItem.getDiscount());
					asi.setAmount(accountSaleItem.getAmount());
					asi.setUnitCost(accountSaleItem.getUnitCost());
					asi.setOrigRefObjectId(accountSaleItem.getEbObjectId());
					if (savedTransaction.getTransactionTypeId().equals(ArTransactionType.TYPE_ACCOUNT_SALES_IS) 
							|| savedTransaction.getTransactionTypeId().equals(ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS)) {
						itemBagQuantityService.setItemBagQty(asi, accountSaleItem.getEbObjectId(),
								savedTransaction.getTransactionTypeId().equals(ArTransactionType.TYPE_ACCOUNT_SALES_IS) ?
										ItemBagQuantity.AS_IS_BAG_QTY : ItemBagQuantity.ASR_IS_BAG_QTY);
					}
					asi.setOrigBagQty(asi.getItemBagQuantity());
					asis.add(asi);
				}
				arTransaction.setAccountSaleItems(asis);
			}

			if (!savedTransaction.getArLines().isEmpty()){
				List<ArLine> arLines = new ArrayList<>();
				ArLine arLine = null;
				for (ArLine line : savedTransaction.getArLines()) {
					arLine = new ArLine();
					arLine.setArLineSetupName(line.getArLineSetup().getName());
					arLine.setArLineSetupId(line.getArLineSetupId());
					arLine.setUnitOfMeasurementId(line.getUnitOfMeasurementId());
					arLine.setUnitMeasurementName(line.getUnitMeasurement() == null ? "" : line.getUnitMeasurement().getName());
					arLine.setQuantity(line.getQuantity());
					arLine.setUpAmount(-line.getUpAmount());
					arLine.setAmount(line.getAmount());
					arLine.setVatAmount(line.getVatAmount());
					arLine.setTaxTypeId(line.getTaxTypeId());
					arLine.setTaxType(line.getTaxType());
					arLines.add(arLine);
				}
				arTransaction.setArLines(arLines);
			}
			logger.debug("Successfully created a sale return transaction object.");
			return arTransaction;
		}
		return null;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		ObjectInfo arTransObjecInfo = super.getObjectInfo(ebObjectId, user);
		ArTransaction arTransaction = arTransactionDao.getByEbObjectId(ebObjectId);
		String title = "";
		switch (arTransaction.getTransactionTypeId()) {
		case ArTransactionType.TYPE_ACCOUNT_SALE:
			title = "Account Sales - " + arTransaction.getSequenceNumber();
			break;
		case ArTransactionType.TYPE_SALE_RETURN:
			title = "Account Sales Return - " + arTransaction.getSequenceNumber();
			break;
		}
		return ObjectInfo.getInstance(ebObjectId, title,
				arTransObjecInfo.getLatestStatus(),
				arTransObjecInfo.getShortDescription(),
				arTransObjecInfo.getFullDescription(),
				arTransObjecInfo.getPopupLink(),
				arTransObjecInfo.getPrintOutLink());
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case ArTransaction.ACCOUNT_SALE_ORDER_OBJECT_TYPE_ID:
		case ArTransaction.ACCOUNT_SALE_RETURN_OBJECT_TYPE_ID:
			return arTransactionDao.getByEbObjectId(ebObjectId);

		case AccountSaleItem.ACCOUNT_SALE_ITEM_OBJECT_TYPE_ID:
		case AccountSaleItem.ACCOUNT_SALE_RETURN_ITEM_OBJECT_TYPE_ID:
		case AccountSaleItem.ACCOUNT_SALE_EXCHANGE_ITEM_OBJECT_TYPE_ID:
			return accountSaleItemDao.getByEbObjectId(ebObjectId);

		case ArLine.OBJECT_TYPE_ID:
			return arLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the total vat amount.
	 * @param transactionId The transaction id.
	 * @return The total vat amount.
	 */
	public double getTotalVatAmnt(int transactionId) {
		ArTransaction arTransaction = arTransactionDao.get(transactionId);
		double totalVat = 0;
		for (ArLine oc : arTransaction.getArLines()) {
			totalVat += oc.getVatAmount() != null ? oc.getVatAmount() : 0;
		}
		List<AccountSaleItem> accountSaleItems = accountSaleItemDao.getAccountSaleItems(arTransaction.getId(), null, null);
		for (AccountSaleItem asItem : accountSaleItems) {
			totalVat += asItem.getVatAmount() != null ? (asItem.getAmount() < 0 ? -asItem.getVatAmount() : asItem.getVatAmount())  : 0;
		}
		return totalVat;
	}
}
