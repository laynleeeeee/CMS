package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.eb.dao.AccountSaleItemDao;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.ArLineDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;

/**
 * Class that handles business logic for Account Sale - IS

 */
@Service
public class AccountSaleISService extends BaseWorkflowService {
	private final Logger logger = Logger.getLogger(AccountSaleISService.class);
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private AccountSaleItemDao accountSaleItemDao;
	@Autowired
	private ArLineDao arLineDao;
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private ArTransactionService transactionService;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private EbObjectService ebObjectService;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return arTransactionDao.get(id).getFormWorkflow();
	}

	/**
	 * Generate the sequence number of the AR Transaction.
	 * @param companyId The id of the company.
	 * @param transactionTypeId The id of the AR Transaction Type.
	 * @return The generated sequence number.
	 */
	public Integer generateSeqNo(Integer companyId, Integer transactionTypeId) {
		return arTransactionDao.genSequenceNo(companyId, transactionTypeId);
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		ArTransaction arTransaction = (ArTransaction) form;
		AccountSaleItem refSaleItem = null;
		for(AccountSaleItem asi : arTransaction.getAccountSaleItems()) {
			if(asi.getRefAccountSaleItemId() != null) {
				refSaleItem = accountSaleItemDao.get(asi.getRefAccountSaleItemId());
				asi.setSrp(refSaleItem.getSrp());
				asi.setUnitCost(refSaleItem.getUnitCost());
				asi.setItemSrpId(refSaleItem.getItemSrpId());
				asi.setItemAddOnId(refSaleItem.getItemAddOnId());
				asi.setItemDiscountId(refSaleItem.getItemDiscountId());
			}
		}
		SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<>();
		List<AccountSaleItem> processedAsItems = saleItemUtil.processDiscountAndAmount(arTransaction.getAccountSaleItems(),
				itemDiscountService);
		for (AccountSaleItem asi : processedAsItems) {
			asi.setTransactionTypeId(arTransaction.getTransactionTypeId());
		}
		arTransaction.setAccountSaleItems(processedAsItems);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		ArTransaction arTransaction = (ArTransaction) form;
		boolean isNew = arTransaction.getId() == 0;
		Date currentDate = new Date();
		AuditUtil.addAudit(arTransaction, new Audit(user.getId(), isNew, currentDate));
		if (isNew) {
			Integer asNumber = generateSeqNo(arTransaction.getCompanyId(), arTransaction.getTransactionTypeId());
			arTransaction.setSequenceNumber(asNumber);
		} else {
			ArTransaction savedArTransaction = transactionService.getARTransaction(arTransaction.getId());
			DateUtil.setCreatedDate(arTransaction, savedArTransaction.getCreatedDate());
			// Get the AS Items
			List<AccountSaleItem> savedASItems = getAcctSaleItems(arTransaction.getId());
			if (!savedASItems.isEmpty()) {
				List<Integer> toBeDeletedASItems = new ArrayList<Integer>();
				for (AccountSaleItem asItem : savedASItems) {
					ItemBagQuantity ibq =  itemBagQuantityDao.getByRefId(asItem.getEbObjectId(), ItemBagQuantity.AS_IS_BAG_QTY);
					if (ibq != null) {
						ibq.setActive(false);
						itemBagQuantityDao.update(ibq);
					}
					toBeDeletedASItems.add(asItem.getId());
				}
				accountSaleItemDao.delete(toBeDeletedASItems);
				toBeDeletedASItems = null;
			}
		}

		arTransaction.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		arTransaction.setDescription(arTransaction.getDescription().trim());

		//Process the Account Sales - IS
		List<AccountSaleItem> asItems = arTransaction.getAccountSaleItems();
		List<AccountSaleItem> toBeSavedItems = new ArrayList<AccountSaleItem>();
		double totalSales = 0;
		int transactionType = arTransaction.getTransactionTypeId();
		if (asItems != null && !asItems.isEmpty()) {
			if(transactionType == ArTransactionType.TYPE_ACCOUNT_SALES_IS) {
				for (AccountSaleItem saleItem : asItems) {
					saleItem.setArTransactionId(arTransaction.getId());
					totalSales += saleItem.getAmount();
					toBeSavedItems.add(saleItem);
				}
			} else if(transactionType == ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS) {
				for (AccountSaleItem saleItem : asItems) {
					saleItem.setArTransactionId(arTransaction.getId());
					totalSales += saleItem.getAmount();
					toBeSavedItems.add(saleItem);
				}
			}
		} else {
			toBeSavedItems.addAll(asItems);
		}
		if(transactionType == ArTransactionType.TYPE_ACCOUNT_SALES_IS) {
			//Computing the total charges and add it to the amount of the ar transaction.
			double totalCharges = getTotalCharges(arTransaction.getArLines());
			// Recompute total amount in the header. ;
			logger.info("Computed total sales: "+totalSales);
			logger.info("Computed total charges: "+totalCharges);

			double totalAmount = totalSales + totalCharges;
			arTransaction.setAmount(totalAmount);

			arTransaction.setTransactionNumber("AS-IS " + arTransaction.getSequenceNumber());
		} else {
			arTransaction.setTransactionNumber("ASR-IS " + arTransaction.getSequenceNumber());
		}

		arTransactionDao.saveOrUpdate(arTransaction);
		saveAcctSaleItems(arTransaction.getId(), toBeSavedItems);

		saveItemBagQty(asItems, transactionType == ArTransactionType.TYPE_ACCOUNT_SALES_IS ?
				ItemBagQuantity.AS_IS_BAG_QTY : ItemBagQuantity.ASR_IS_BAG_QTY, user, currentDate);

		processArLines(isNew, arTransaction.getId(), arTransaction.getArLines());
		logger.info("Successfully saved the Account Sales with transaction number: "+arTransaction.getTransactionNumber());
	}

	/**
	 * Save the Account Sale Items.
	 * @param arTransactionId The id of the ar transaction.
	 * @param asItems The account sale items to be saved.
	 */
	public void saveAcctSaleItems(int arTransactionId, List<AccountSaleItem> asItems) {
		logger.info("Saving the Account Sale Items.");
		List<Domain> toBeSavedDomains = new ArrayList<Domain>(); //Batch save the Account Sales and Returned Items.
		for (AccountSaleItem asi : asItems) {
			if (asi.getAmount() == null) {
				asi.setAmount(0.0);
			}
			SaleItemUtil.setNullUnitCostToZero(asi);
			asi.setArTransactionId(arTransactionId);
			toBeSavedDomains.add(asi);
		}
		accountSaleItemDao.batchSave(toBeSavedDomains);
	}

	private void saveItemBagQty(List<AccountSaleItem> asItems, Integer orTypeId, User user, Date currentDate) {
		List<Domain> toBeSavedIbqs = new ArrayList<>();
		List<Domain> o2os = new ArrayList<>();
		for(AccountSaleItem asItem : asItems) {
			if(asItem.getItemBagQuantity() != null) {
				int ibqObjectId = ebObjectService.saveAndGetEbObjectId(user.getId(), ItemBagQuantity.OBJECT_TYPE_ID, currentDate);
				ItemBagQuantity ibq = ItemBagQuantity.getInstanceOf(asItem.getItemId(), ibqObjectId, asItem.getItemBagQuantity());
				ibq.setCreatedBy(user.getId());
				AuditUtil.addAudit(ibq, new Audit(user.getId(), true, currentDate));
				toBeSavedIbqs.add(ibq);
				o2os.add(ObjectToObject.getInstanceOf(asItem.getEbObjectId(),
						ibqObjectId, orTypeId, user, currentDate));
			}
		}
		if (!toBeSavedIbqs.isEmpty()) {
			itemBagQuantityDao.batchSave(toBeSavedIbqs);
		}
		if (!o2os.isEmpty()) {
			objectToObjectDao.batchSave(o2os);
		}
	}

	public void processArLines(boolean isNew, Integer arTransactionId, List<ArLine> arLines) {
		logger.debug("Processing AR Lines.");
		List<Integer> toBeDeleted = new ArrayList<Integer>();
		if (!isNew) {
			ArTransaction oldART = arTransactionDao.get(arTransactionId);
			List<ArLine> oldARLines = oldART.getArLines();
			for (ArLine arLine : oldARLines) 
				toBeDeleted.add(arLine.getId());
			arLineDao.delete(toBeDeleted);
			toBeDeleted = null;
		}
		if (arLines != null && !arLines.isEmpty()) {
			for (ArLine arLine : arLines) {
				arLine.setId(0);
				arLine.setaRTransactionId(arTransactionId);
				arLineDao.saveOrUpdate(arLine);
			}
		}
		logger.info("Successfully processed and saved ar lines.");
	}

	public double getTotalCharges(List<ArLine> otherCharges) {
		double totalCharges = 0;
		for (ArLine oc : otherCharges) {
			totalCharges += oc.getAmount();
		}
		return totalCharges;
	}

	public ArTransaction getTransaction(Integer arTransactionId) {
		return arTransactionDao.get(arTransactionId);
	}

	/**
	 * Get the list of account sale items by the id of the parent object.
	 * @param arTransactionId The id of the parent object.
	 * @return The list of account sales items.
	 */
	public List<AccountSaleItem> getAcctSaleItems(Integer arTransactionId) {
		List<AccountSaleItem> asItems = accountSaleItemDao.getAccountSaleItems(arTransactionId, null, null);
		for(AccountSaleItem asi : asItems) {
			addASIReference(asi);
		}
		return asItems;
	}

	private void addASIReference(AccountSaleItem asi) {
		itemBagQuantityService.setItemBagQty(asi, asi.getEbObjectId(), ItemBagQuantity.AS_IS_BAG_QTY);
		EBObject otherReference = objectToObjectDao.getOtherReference(asi.getEbObjectId(), AccountSaleItem.ASI_OR_TYPE_ID);
		if (otherReference == null) {
			throw new RuntimeException("There was an error occured previously when saving object to object reference for account sale.");
		}
		asi.setReferenceObjectId(otherReference.getId());
		asi.setStockCode(asi.getItem().getStockCode());
	}

	/**
	 * Get the account sale item with the needed reference objects.
	 * @param csItemId The id of the account sale item object.
	 * @return The account sale item.
	 */
	public AccountSaleItem getASItemWithReference (Integer asItemId) {
		AccountSaleItem asi = accountSaleItemDao.get(asItemId);
		addASIReference(asi);
		return asi;
	}

	public ArTransaction getArTransItemsAndOC(Integer arTransactionId) {
		ArTransaction arTransaction = getTransaction(arTransactionId);
		List<AccountSaleItem> accountSaleItems = getAcctSaleItems(arTransactionId);
		EBObject refObject = null;
		for (AccountSaleItem asi : accountSaleItems) {
			asi.setOrigQty(asi.getQuantity());
			asi.setStockCode(asi.getItem().getStockCode());
			refObject = ooLinkHelper.getReferenceObject(asi.getEbObjectId(), 5); //Object Type id for Account Sales Item
			asi.setOrigRefObjectId(refObject.getId());
			asi.setReferenceObjectId(refObject.getId());
			double origSrp = asi.getItemSrp().getSrp();
			asi.setOrigSrp(origSrp);
		}
		arTransaction.setAccountSaleItems(accountSaleItems);
		arTransaction.setArLines(getDetailedArLines(arTransactionId));
		//Set Available balance for customer
		ArCustomer arCustomer = arCustomerDao.get(arTransaction.getCustomerId());
		if (arCustomer != null) {
			if (arCustomer.getMaxAmount() != null && arCustomer.getMaxAmount() != 0) {
				Integer arCustomerId = arCustomer.getId();
				arTransaction.setAvailableBalance(arCustomer.getMaxAmount() - getTotalBalance(arCustomerId));
			} else {
				arTransaction.setAvailableBalance(0.0);
			}
		}
		return arTransaction;
	}

	public double getTotalBalance (Integer arCustomerId) {
		return accountSaleItemDao.getTotalBalance(arCustomerId, 0);
	}

	public List<ArLine> getArLines(Integer acctSaleId) {
		return arLineDao.getArLines(acctSaleId);
	}

	public List<ArLine> getDetailedArLines(Integer acctSaleId) {
		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		otherCharges.addAll(getArLines(acctSaleId));
		List<AROtherCharge> processedList = getDetailOtherCharges(otherCharges);
		List<ArLine> ret = new ArrayList<ArLine>();
		for (AROtherCharge oc : processedList) {
			ret.add((ArLine) oc);
		}
		return ret;
	}

	/**
	 * Get the detailed Other Charges and set the AR Line Setup name and UOM name.
	 * @return The list of Other Charges.
	 */
	public List<AROtherCharge> getDetailOtherCharges(List<AROtherCharge> otherCharges) {
		if(otherCharges.isEmpty()) {
			logger.debug("No AR Lines/Other Charges to be processed.");
			return otherCharges;
		}
		List<AROtherCharge> ret = new ArrayList<AROtherCharge>();
		for (AROtherCharge oc : otherCharges) {
			logger.debug("Setting the AR Line setup name and UOM name to AR Line: "+oc.getId());
			oc.setArLineSetupName(oc.getArLineSetup().getName());
			if(oc.getUnitOfMeasurementId() != null) {
				oc.setUnitMeasurementName(oc.getUnitMeasurement().getName());
			}
			ret.add(oc);
		}
		logger.debug("Successfully retrieved "+ret.size()+" AR Lines.");
		return ret;
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return arTransactionDao.getByEbObjectId(ebObjectId);
	}

	public List<AccountSaleItem> getASRItems(int arTransactionId) {
		List<AccountSaleItem> asrItems = accountSaleItemDao.getAllByRefId("arTransactionId", arTransactionId);
		AccountSaleItem refASItem = null;
		EBObject ebObject = null;
		double srp = 0;
		double addOn = 0;
		for (AccountSaleItem asi : asrItems) {
			asi.setStockCode(asi.getItem().getStockCode());
			asi.setOrigQty(asi.getQuantity());
			// Set item bag quantity
			itemBagQuantityService.setItemBagQty(asi, asi.getEbObjectId(), ItemBagQuantity.ASR_IS_BAG_QTY);

			Integer asItemId = asi.getRefAccountSaleItemId();
			if(asItemId != null) {
				refASItem = accountSaleItemDao.get(asItemId);
				itemBagQuantityService.setItemBagQty(refASItem, refASItem.getEbObjectId(), ItemBagQuantity.AS_IS_BAG_QTY);
				asi.setOrigBagQty(refASItem.getItemBagQuantity());
				asi.setSalesRefId(asItemId);
				asi.setOrigRefObjectId(refASItem.getEbObjectId());
				asi.setReferenceObjectId(refASItem.getEbObjectId());
				srp = refASItem.getSrp();
				addOn = refASItem.getItemAddOn() !=null ? refASItem.getItemAddOn().getValue() : 0;
			} else {
				ebObject = ooLinkHelper.getReferenceObject(asi.getEbObjectId(), 7);
				asi.setReferenceObjectId(ebObject.getId());
				asi.setOrigRefObjectId(ebObject.getId());
				srp = asi.getSrp();
				addOn = asi.getItemAddOn() != null ? asi.getItemAddOn().getValue() : 0;
			}
			asi.setOrigSrp(srp-addOn);
		}
		return asrItems;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		transactionService.doBeforeSaving(currentWorkflowLog, bindingResult);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return arTransactionDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		ObjectInfo arTransObjectInfo = transactionService.getObjectInfo(ebObjectId, user);
		StringBuilder shortDesc = new StringBuilder(arTransObjectInfo.getShortDescription());
		String title = "";
		ArTransaction arTransaction = arTransactionDao.getByEbObjectId(ebObjectId);
		boolean isAsIS = arTransaction.getTransactionTypeId() == ArTransactionType.TYPE_ACCOUNT_SALES_IS;
		int sequenceNo = arTransaction.getSequenceNumber();
		if(isAsIS) {
			shortDesc.append("<b> DUE DATE : </b>" + DateUtil.formatDate(arTransaction.getDueDate()));
			title = "Account Sale - IS - " + sequenceNo;
		} else {
			title = "Account Sale Return - IS - " + sequenceNo;
		}
		return ObjectInfo.getInstance(ebObjectId, title,
				arTransObjectInfo.getLatestStatus(),
				isAsIS ? shortDesc.toString() : arTransObjectInfo.getShortDescription(),
				arTransObjectInfo.getFullDescription(),
				arTransObjectInfo.getPopupLink(),
				arTransObjectInfo.getPrintOutLink());
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case ArTransaction.ACCOUNT_SALE_IS_OBJECT_TYPE_ID:
		case ArTransaction.ACCOUNT_SALE_RETURN_IS_OBJECT_TYPE_ID:
			return arTransactionDao.getByEbObjectId(ebObjectId);

		case AccountSaleItem.ACCOUNT_SALE_ITEM_IS_OBJECT_TYPE_ID:
		case AccountSaleItem.ACCOUNT_SALE_RETURN_ITEM_IS_OBJECT_TYPE_ID:
		case AccountSaleItem.ACCOUNT_SALE_EXCHANGE_ITEM_IS_OBJECT_TYPE_ID:
			return accountSaleItemDao.getByEbObjectId(ebObjectId);

		case ArLine.OBJECT_TYPE_ID:
			return arLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}
