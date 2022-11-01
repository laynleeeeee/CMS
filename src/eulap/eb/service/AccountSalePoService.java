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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountSaleItemDao;
import eulap.eb.dao.AccountSalesDao;
import eulap.eb.dao.AccountSalesPoItemDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.AccountSales;
import eulap.eb.domain.hibernate.AccountSalesPoItem;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * Service class for Account Sales.

 *
 */
@Service
public class AccountSalePoService  extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(AccountSalePoService.class);
	@Autowired
	private AccountSalesPoItemDao salesPoItemDao;
	@Autowired
	private AccountSalesDao accountSalesDao;
	@Autowired
	private AccountSaleItemDao accountSaleItemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private InventoryItemSpecialHandler inventoryItemSpecialHandler;

	/**
	 * Get the Account Sales object.
	 * @param accountSalesId The unique id.
	 * @return The Account Sales object.
	 */
	public AccountSales getAccountSales(int accountSalesId) {
		logger.info("Retrieving the AS using the id: "+accountSalesId);
		AccountSales accountSales = accountSalesDao.get(accountSalesId);
		Double existingStocks = null;
		for (AccountSalesPoItem poi : accountSales.getAsPoItems()) {
			existingStocks = itemService.getItemExistingStocks(poi.getItemId(), poi.getWarehouseId());
			poi.getItem().setExistingStocks(existingStocks);
			poi.setExistingStocks(existingStocks);
			poi.setStockCode(poi.getItem().getStockCode());
			poi.setOrigQty(poi.getQuantity());
		}
		return accountSales;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		logger.info("Retrieving the form workflow of Account Sales: "+id);
		return getAccountSales(id).getFormWorkflow();
	}

	/**
	 * Generate the next available AS Number.
	 * @param companyId The id of the company.
	 * @return The next available AS Number.
	 */
	public int generatePONumber(int companyId) {
		logger.info("Generating the AS Number of company: "+companyId);
		int maxPONumber = accountSalesDao.getMaxPONumber(companyId);
		logger.debug("The current maximum AS number "+maxPONumber);
		logger.info("The generated available AS Number: "+maxPONumber);
		return maxPONumber;
	}

	/**
	 * Get the list of Account Sales Items with existing stocks.
	 * @return The list of AS PO Items.
	 */
	public List<AccountSalesPoItem> getPOItems(AccountSales as) {
		int asId = as.getId();
		logger.info("Retrieve the list of Items of PO Id: "+asId);
		List<AccountSalesPoItem> items = salesPoItemDao.getAllByRefId(AccountSalesPoItem.FIELD.accoutnSaleId.name(), asId);
		for (AccountSalesPoItem poItem : items) {
			Item item =  itemService.getRetailItem(poItem.getItem().getStockCode(), as.getCompanyId(), null);
			poItem.setItem(item);
		}
		return items;
	}

	/**
	 * Save the Account Sales.
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		AccountSales accountSales = (AccountSales) form;
		logger.info("Saving the Account Sales");
		boolean isNew = accountSales.getId() == 0 ? true : false;
		Integer poNumber = null;
		AuditUtil.addAudit(accountSales, new Audit(user.getId(), isNew, new Date()));
		if(isNew) {
			poNumber = generatePONumber(accountSales.getCompanyId());
			accountSales.setPoNumber(poNumber);
		} else {
			AccountSales savedPO = getAccountSales(accountSales.getId());
			DateUtil.setCreatedDate(accountSales, savedPO.getCreatedDate());
		}

		String remarks = accountSales.getRemarks();
		if(remarks != null) {
			accountSales.setRemarks(StringFormatUtil.removeExtraWhiteSpaces(remarks));
		}

		accountSalesDao.saveOrUpdate(accountSales);
		logger.info("Successfully saved Account Sales with PO Number: "+poNumber);
		List<Integer> toBeDeletedPoItemIds = new ArrayList<Integer>();
		List<AccountSalesPoItem> asPoItems = null;
		Integer asId = accountSales.getId();
		if(!isNew) {
			logger.debug("Deleting the saved Account Sales Items.");
			asPoItems = getPOItems(accountSales);
			if(!asPoItems.isEmpty()) {
				for (AccountSalesPoItem item : asPoItems)
					toBeDeletedPoItemIds.add(item.getId());
				salesPoItemDao.delete(toBeDeletedPoItemIds);
				logger.trace("Deleted "+toBeDeletedPoItemIds.size()+" PO Items.");
				logger.debug("Successfully deleted AS PO Items of PO: "+asId);
			}
		}

		List<Domain> toBeSavedASPOItems = new ArrayList<Domain>();
		asPoItems = accountSales.getAsPoItems();
		inventoryItemSpecialHandler.updateItem(asPoItems);
		logger.debug("Saving the AS PO Items.");
		logger.trace("Saving "+asPoItems.size()+" AS PO Items.");
		for (AccountSalesPoItem poItem : asPoItems) {
			if(poItem.getItemId() != null) {
				poItem.setAccoutnSaleId(asId);
				if(poItem.getUnitCost() == null) {
					poItem.setUnitCost(0.0);
				}
				toBeSavedASPOItems.add(poItem);
			}
		}
		salesPoItemDao.batchSave(toBeSavedASPOItems);
		logger.debug("Successfully saved "+toBeSavedASPOItems.size()+" PO Items.");

		logger.warn("=====>>> Freeing up the memory allocation");
		toBeDeletedPoItemIds = null;
		toBeSavedASPOItems = null;
		asPoItems = null;
		poNumber = null;
		remarks = null;
	}

	/**
	 * Get the Account Sales PO Item by account sales order eb object id.
	 * @param ebObjectId The Account Sales Order eb object id.
	 * @return The Account Sales.
	 */
	public AccountSalesPoItem getASPOItemByASOIEBObject(Integer ebObjectId) {
		logger.info("Getting the list of refence document by ebObject id.");
		//Get object to object by ebObjectId of the PO table.
		List<ObjectToObject> objectToObjects = objectToObjectDao.getAllByRefId("toObjectId", ebObjectId);
		AccountSalesPoItem poItem = null;
		EBObject ebObject = new EBObject();
		for (ObjectToObject objectToObject : objectToObjects) {
			ebObject = ebObjectDao.get(objectToObject.getFromObjectId());
			if(ebObject.getObjectTypeId() == AccountSalesPoItem.OBJECT_TYPE_ID){
				poItem = new AccountSalesPoItem();
				poItem = salesPoItemDao.getAllByRefId("ebObjectId",
						objectToObject.getFromObjectId()).iterator().next();
				break;
			}
		}
		return poItem;
	}

	/**
	 * Get the Account Sales by Ar transaction eb object id.
	 * @param ebObjectId The Ar Transaction  eb object id.
	 * @return The Account Sales.
	 */
	public AccountSales getASPOByASOEBObject(Integer ebObjectId) {
		logger.info("Getting the list of refence document by ebObject id.");
		//Get object to object by ebObjectId of the PO table.
		List<ObjectToObject> objectToObjects = objectToObjectDao.getAllByRefId("toObjectId", ebObjectId);
		AccountSales as = null;
		EBObject ebObject = new EBObject();
		for (ObjectToObject objectToObject : objectToObjects) {
			ebObject = ebObjectDao.get(objectToObject.getFromObjectId());
			if(ebObject.getObjectTypeId() == AccountSales.OBJECT_TYPE_ID){
				as = new AccountSales();
				as = accountSalesDao.getAllByRefId("ebObjectId",
						objectToObject.getFromObjectId()).iterator().next();
				break;
			}
		}
		return as;
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return accountSalesDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		AccountSales accountSales = accountSalesDao.getByEbObjectId(ebObjectId);
		Integer pId = accountSales.getId();
		FormProperty property = workflowHandler.getProperty(accountSales.getWorkflowName(), user);

		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printoutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = accountSales.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Sales Order - " + accountSales.getPoNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + accountSales.getArCustomer().getName())
				.append(" " + accountSales.getArCustomerAccount().getName())
				.append("<b> AS DATE : </b>" + DateUtil.formatDate(accountSales.getPoDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printoutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		return accountSalesDao.getByEbObjectId(ebObject.getId());
	}

	/**
	 * Get the list of account sales reference,
	 * @param companyId The company id.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAccountId The customer account id.
	 * @param asNumber The account sales number.
	 * @param dateFrom The date ranged start.
	 * @param dateTo The date ranged end.
	 * @param status The status account sales.
	 * @param user The user current logged.
	 * @return {@link Page<AccountSales>}
	 */
	public Page<AccountSales> getAccountSales(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer asNumber, Date dateFrom, Date dateTo, Integer status, Integer pageNumber, User user) {
		return accountSalesDao.getAccountSales(companyId, arCustomerId, arCustomerAccountId, asNumber,
				dateFrom, dateTo, status, new PageSetting(pageNumber), user);
	}

	/**
	 * Get and convert Account sales to ar transaction.
	 * @param accountSalesId The account sales id.
	 * @return {@link ArTransaction}
	 */
	public ArTransaction getConvrtedARTransactionByASId(Integer accountSalesId) {
		ArTransaction arTransaction = new ArTransaction();
		AccountSales accountSales = getAccountSales(accountSalesId);
		arTransaction.setCustomerAcctId(accountSales.getArCustomerAccountId());
		arTransaction.setCustomerId(accountSales.getArCustomerId());
		arTransaction.setArCustomer(accountSales.getArCustomer());
		arTransaction.setReferenceNo(accountSales.getPoNumber().toString());
		arTransaction.setCompanyId(accountSales.getCompanyId());
		arTransaction.setCompany(accountSales.getCompany());
		arTransaction.setReferenceObjectId(accountSales.getEbObjectId());
		arTransaction.setAccountSaleId(accountSales.getId());
		arTransaction.setTransactionTypeId(ArTransactionType.TYPE_ACCOUNT_SALE);
		List<AccountSalesPoItem> accountSalesPoItems = accountSales.getAsPoItems();
		List<AccountSaleItem> asis = new ArrayList<AccountSaleItem>();
		AccountSaleItem accountSaleItem = null;
		for (AccountSalesPoItem accountSalesPoItem : accountSalesPoItems) {
			accountSaleItem = new AccountSaleItem();
			accountSaleItem.setTransactionTypeId(ArTransactionType.TYPE_ACCOUNT_SALE);
			accountSaleItem.setStockCode(accountSalesPoItem.getStockCode());
			accountSaleItem.setItem(accountSalesPoItem.getItem());
			accountSaleItem.setReferenceObjectId(accountSalesPoItem.getEbObjectId());
			accountSaleItem.setOrigRefObjectId(accountSalesPoItem.getEbObjectId());
			accountSaleItem.setItemId(accountSalesPoItem.getItemId());
			accountSaleItem.setQuantity(accountSalesPoItem.getQuantity());
			accountSaleItem.setWarehouseId(accountSalesPoItem.getWarehouseId());
			asis.add(accountSaleItem);
		}
		arTransaction.setAccountSaleItems(asis);
		return arTransaction;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		AccountSales po = accountSalesDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		logger.debug("Status of Account Sales was updated to: "+currentWorkflowLog.getFormStatusId());
		List<AccountSalesPoItem> purchaseOrderItems = po.getAsPoItems();
		if (po.getEbObjectId() != null) {
			if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
				String errorMessage = "AS is used as reference in AS ";
				ArTransaction arTransaction = null;
				List<AccountSaleItem> accountSaleItems = null;
				for (AccountSalesPoItem rPurchaseOrderItem : purchaseOrderItems) {
					logger.info("Cancelling TO ");
					List<ObjectToObject> objectToObjects =
							objectToObjectDao.getAllByRefId("fromObjectId", rPurchaseOrderItem.getEbObjectId());
					accountSaleItems = new ArrayList<>();
					for (ObjectToObject objectToObject : objectToObjects) {
						//Get the PO by object to object toId.
						accountSaleItems.addAll(accountSaleItemDao.getAllByRefId("ebObjectId",
								objectToObject.getToObjectId()));
					}
					for (AccountSaleItem accountSaleItem : accountSaleItems) {
						arTransaction = arTransactionDao.get(accountSaleItem.getArTransactionId());
						if(arTransaction.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID
								&& arTransaction.getSequenceNumber() != null){
							errorMessage += " " + arTransaction.getSequenceNumber();
							bindingResult.reject("workflowMessage", errorMessage);
							currentWorkflowLog.setWorkflowMessage(errorMessage);
							break;
						}
					}
				}
			}
		}
	}
}
