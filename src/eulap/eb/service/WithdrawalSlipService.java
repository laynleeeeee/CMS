package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.common.util.ListProcessorUtil;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeProfileDao;
import eulap.eb.dao.FleetProfileDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.RPurchaseOrderDao;
import eulap.eb.dao.WarehouseDao;
import eulap.eb.dao.WithdrawalSlipDao;
import eulap.eb.dao.WithdrawalSlipItemDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RPurchaseOrderItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.domain.hibernate.WithdrawalSlipItem;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.inventory.FormUnitCosthandler;
import eulap.eb.service.inventory.RItemCostUpdateService;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.ItemTransaction;

/**
 * A class that handles the business logic of Withdrawal Slip. 

 *
 */
@Service
public class WithdrawalSlipService extends BaseWorkflowService implements FormUnitCosthandler {
	private Logger logger = Logger.getLogger(WithdrawalSlipService.class);
	@Autowired
	private WithdrawalSlipDao withdrawalSlipDao;
	@Autowired
	private WithdrawalSlipItemDao withdrawalSlipItemDao;
	@Autowired
	private RPurchaseOrderDao purchaseOrderDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private WarehouseDao warehouseDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FleetProfileDao fleetProfileDao;
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private EmployeeProfileDao employeeProfileDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private RPurchaseOrderDao rPurchaseOrderDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ArCustomerAcctDao arCustomerAcctDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusService formStatusService;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return withdrawalSlipDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return withdrawalSlipDao.getByWorkflowId(workflowId);
	}

	@Override
	public boolean isDeleteOOReference() {
		return false;
	}

	/**
	 * Convert PO to Withdrawal Slip.
	 * @param poId The PO ID.
	 * @param user The current user logged.
	 * @return The converted Withdrawal Slip.
	 */
	public WithdrawalSlip getAndConvertPO(Integer poId, User user) {
		RPurchaseOrder purchaseOrder = purchaseOrderDao.get(poId);
		WithdrawalSlip withdrawalSlip = new WithdrawalSlip();
		withdrawalSlip.setEmployeeId(purchaseOrder.getRequestedById());
		withdrawalSlip.setPurchaseOderId(purchaseOrder.getId());
		if(purchaseOrder.getRequestedById() != null) {
			withdrawalSlip.setRequestedBy(employeeDao.get(purchaseOrder.getRequestedById()).getFullName());
		}
		withdrawalSlip.setCompanyId(purchaseOrder.getCompanyId());
		withdrawalSlip.setPoNumber(purchaseOrder.getPoNumber().toString());
		withdrawalSlip.setRefenceObjectId(purchaseOrder.getEbObjectId());
		List<WithdrawalSlipItem> withdrawalSlipItems = new ArrayList<>();
		WithdrawalSlipItem withdrawalSlipItem = new WithdrawalSlipItem();
		Item item = null;
		double remainingQty = 0;
		for (RPurchaseOrderItem poItem : purchaseOrder.getrPoItems()) {
			item = poItem.getItem();
			withdrawalSlipItem = new WithdrawalSlipItem();
			withdrawalSlipItem.setItem(item);
			withdrawalSlipItem.setItemId(item.getId());
			remainingQty = getRemainingQty(poItem.getEbObjectId(), null);
			withdrawalSlipItem.setQuantity(remainingQty);
			withdrawalSlipItem.setUnitCost(poItem.getUnitCost());
			withdrawalSlipItem.setStockCode(item.getStockCode());
			withdrawalSlipItem.setReferenceObjectId(poItem.getEbObjectId());
			withdrawalSlipItem.setOrigRefObjectId(poItem.getEbObjectId());
			withdrawalSlipItems.add(withdrawalSlipItem);
		}
		withdrawalSlip.setWithdrawalSlipItems(withdrawalSlipItems);
		return withdrawalSlip;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		WithdrawalSlip withdrawalSlip = (WithdrawalSlip) form;
		logger.info("Saving the Withdrawal Slip.");
		int withdrawalSlipId = withdrawalSlip.getId();
		boolean isNew = withdrawalSlipId == 0;
		AuditUtil.addAudit(withdrawalSlip, new Audit(user.getId(), isNew, new Date()));

		Integer wsEbObjectId = null;
		if(isNew) {
			wsEbObjectId  = withdrawalSlip.getEbObjectId();

			withdrawalSlip.setWsNumber(withdrawalSlipDao.generateWSNumber(withdrawalSlip.getCompanyId()));
			// Save company object to object.
			Company company = companyDao.get(withdrawalSlip.getCompanyId());
			objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(company.getEbObjectId(),
					wsEbObjectId, WithdrawalSlip.OR_TYPE_COMPANY, user, new Date()));
			// Save warehouse object to object.
			Warehouse warehouse = warehouseDao.get(withdrawalSlip.getWarehouseId());
			objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(warehouse.getEbObjectId(),
					wsEbObjectId, WithdrawalSlip.OR_TYPE_WAREHOUSE, user, new Date()));
			// Save account object to object.
			Account account = accountDao.get(withdrawalSlip.getAccountId());
			objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(account.getEbObjectId(),
					wsEbObjectId, WithdrawalSlip.OR_TYPE_ACCOUNT, user, new Date()));
			if(withdrawalSlip.getFleetId() != null){
				// Save fleet profile object to object.
				FleetProfile fleetProfile = fleetProfileDao.get(withdrawalSlip.getFleetId());
				objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(fleetProfile.getEbObjectId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_FLEET_PROFILE, user, new Date()));
			}
			if(withdrawalSlip.getArCustomerId() != null) {
				// Save ar customer object to object.
				ArCustomer arCustomer = arCustomerDao.get(withdrawalSlip.getArCustomerId());
				objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(arCustomer.getEbObjectId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER, user, new Date()));
				// Save employe profile object to object.
				ArCustomerAccount customerAccount = arCustomerAcctDao.get(withdrawalSlip.getCustomerAcctId());
				objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(customerAccount.getEbObjectId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER_ACCOUNT, user, new Date()));
			}
			if(withdrawalSlip.getEmployeeId() != null) {
				// Save employe profile object to object.
				EmployeeProfile employeeProfile = employeeProfileDao.getByEmployee(withdrawalSlip.getEmployeeId());
				objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(employeeProfile.getEbObjectId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_EMPLOYEE_PROFILE, user, new Date()));
			}
		} else {
			wsEbObjectId  = withdrawalSlip.getEbObjectId();
			// Update company object to object.
			EBObject compEBObject = objectToObjectDao.getOtherReference(wsEbObjectId, WithdrawalSlip.OR_TYPE_COMPANY);
			List<ObjectToObject> compObjectToObjects = objectToObjectDao.getReferenceObjects(compEBObject.getId(), wsEbObjectId, WithdrawalSlip.OR_TYPE_COMPANY);
			if(compObjectToObjects != null && !compObjectToObjects.isEmpty()){
				ObjectToObject toObject = compObjectToObjects.iterator().next();
				Company company = companyDao.get(withdrawalSlip.getCompanyId());
				toObject.setFromObjectId(company.getEbObjectId());
				objectToObjectDao.update(toObject);
			}
			// Update warehouse object to object.
			EBObject warehouseEbObject = objectToObjectDao.getOtherReference(wsEbObjectId, WithdrawalSlip.OR_TYPE_WAREHOUSE);
			List<ObjectToObject> warehouseObjectToObjects = objectToObjectDao.getReferenceObjects(warehouseEbObject.getId(), wsEbObjectId, WithdrawalSlip.OR_TYPE_WAREHOUSE);
			if(warehouseObjectToObjects != null && !warehouseObjectToObjects.isEmpty()){
				ObjectToObject toObject = warehouseObjectToObjects.iterator().next();
				Warehouse warehouse = warehouseDao.get(withdrawalSlip.getWarehouseId());
				toObject.setFromObjectId(warehouse.getEbObjectId());
				objectToObjectDao.update(toObject);
			}
			// Update account object to object.
			EBObject accountEbObject = objectToObjectDao.getOtherReference(wsEbObjectId, WithdrawalSlip.OR_TYPE_ACCOUNT);
			List<ObjectToObject> accountObjectToObjects = objectToObjectDao.getReferenceObjects(accountEbObject.getId(), wsEbObjectId, WithdrawalSlip.OR_TYPE_ACCOUNT);
			if(accountObjectToObjects != null && !accountObjectToObjects.isEmpty()){
				ObjectToObject toObject = accountObjectToObjects.iterator().next();
				Account account = accountDao.get(withdrawalSlip.getAccountId());
				toObject.setFromObjectId(account.getEbObjectId());
				objectToObjectDao.update(toObject);
			}
			// Update Fleet Profile object to object.
			EBObject fpEbObject = objectToObjectDao.getOtherReference(wsEbObjectId, WithdrawalSlip.OR_TYPE_FLEET_PROFILE);
			if(fpEbObject != null){
				List<ObjectToObject> fpObjectToObjects = objectToObjectDao.getReferenceObjects(fpEbObject.getId(), wsEbObjectId, WithdrawalSlip.OR_TYPE_FLEET_PROFILE);
				ObjectToObject toObject = fpObjectToObjects.iterator().next();
				if(withdrawalSlip.getFleetId() != null){
					FleetProfile fleetProfile = fleetProfileDao.get(withdrawalSlip.getFleetId());
					toObject.setFromObjectId(fleetProfile.getEbObjectId());
					objectToObjectDao.update(toObject);
				} else {
					objectToObjectDao.delete(toObject);
				}
			} else if(withdrawalSlip.getFleetId() != null){
				// Save fleet profile object to object.
				FleetProfile fleetProfile = fleetProfileDao.get(withdrawalSlip.getFleetId());
				objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(fleetProfile.getEbObjectId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_FLEET_PROFILE, user, new Date()));
			}
			// Update ar customer object to object.
			EBObject customerEbObject = objectToObjectDao.getOtherReference(wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER);
			if(customerEbObject != null){
				List<ObjectToObject> customerObjectToObjects = objectToObjectDao.getReferenceObjects(customerEbObject.getId(), wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER);
				ObjectToObject toObject = customerObjectToObjects.iterator().next();
				if(withdrawalSlip.getArCustomerId() != null){
					ArCustomer arCustomer = arCustomerDao.get(withdrawalSlip.getArCustomerId());
					toObject.setFromObjectId(arCustomer.getEbObjectId());
					objectToObjectDao.update(toObject);
				} else {
					objectToObjectDao.delete(toObject);
				}
				// Update ar customer account object to object.
				EBObject custAcctEbObject = objectToObjectDao.getOtherReference(wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER_ACCOUNT);
				if(custAcctEbObject != null){
					List<ObjectToObject> custAcctObjectToObjects = objectToObjectDao.getReferenceObjects(custAcctEbObject.getId(), wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER_ACCOUNT);
					ObjectToObject objectToObject = custAcctObjectToObjects.iterator().next();
					if(withdrawalSlip.getArCustomerId() != null){
						ArCustomerAccount customerAccount = arCustomerAcctDao.getArCustomerAccount(withdrawalSlip.getCompanyId(), withdrawalSlip.getArCustomerId());
						objectToObject.setFromObjectId(customerAccount.getEbObjectId());
						objectToObjectDao.update(objectToObject);
					} else {
						objectToObjectDao.delete(objectToObject);
					}
				}
			} else if(withdrawalSlip.getArCustomerId() != null){
				// Save ar customer object to object.
				ArCustomer arCustomer = arCustomerDao.get(withdrawalSlip.getArCustomerId());
				objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(arCustomer.getEbObjectId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER, user, new Date()));
				// Save employe profile object to object.
				ArCustomerAccount customerAccount = arCustomerAcctDao.get(withdrawalSlip.getCustomerAcctId());
				objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(customerAccount.getEbObjectId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER_ACCOUNT, user, new Date()));
			}
			if(withdrawalSlip.getEmployeeId() != null) {
				// Update Employee Profile object to object.
				EBObject empPEbObject = objectToObjectDao.getOtherReference(wsEbObjectId, WithdrawalSlip.OR_TYPE_EMPLOYEE_PROFILE);
				List<ObjectToObject> empPObjectToObjects = objectToObjectDao.getReferenceObjects(empPEbObject.getId(), wsEbObjectId, WithdrawalSlip.OR_TYPE_EMPLOYEE_PROFILE);
				if(empPObjectToObjects != null && !empPObjectToObjects.isEmpty()){
					ObjectToObject toObject = empPObjectToObjects.iterator().next();
					EmployeeProfile employeeProfile = employeeProfileDao.getByEmployee(withdrawalSlip.getEmployeeId());
					toObject.setFromObjectId(employeeProfile.getEbObjectId());
					objectToObjectDao.update(toObject);
				}
			}
		}

		String remarks = withdrawalSlip.getRemarks();
		if(remarks != null) {
			withdrawalSlip.setRemarks(StringFormatUtil.removeExtraWhiteSpaces(remarks));
		}

		String requesterName = withdrawalSlip.getRequesterName();
		if(requesterName != null) {
			withdrawalSlip.setRequesterName(StringFormatUtil.removeExtraWhiteSpaces(requesterName));
		}
		withdrawalSlipDao.saveOrUpdate(withdrawalSlip);
		if(withdrawalSlip.getPurchaseOderId() != null && isNew){
			objectToObjectDao.save(ObjectToObject.getInstanceOf(withdrawalSlip.getRefenceObjectId(),
					wsEbObjectId, WithdrawalSlip.OR_TYPE_WS_PO, user, new Date()));
		}
		saveWithdrawalItems(withdrawalSlip.getWithdrawalSlipItems(), user, wsEbObjectId, withdrawalSlip.getWarehouseId(), withdrawalSlip.getDate());
	}

	protected void saveWithdrawalItems(List<WithdrawalSlipItem> withdrawalSlipItems, User user, int wsEbObjectId, Integer warehouseId, Date date) {
		List<WithdrawalSlipItem> slipItems = getAllActiveWsItems(wsEbObjectId);
		for (WithdrawalSlipItem slipItem : slipItems) {
			slipItem.setActive(false);
			withdrawalSlipItemDao.update(slipItem);
		}

		Map<AllocatorKey, WeightedAveItemAllocator<WithdrawalSlipItem>> itemId2CostAllocator =
				new HashMap<AllocatorKey, WeightedAveItemAllocator<WithdrawalSlipItem>>();
		Date currentDate = new Date();
		for (WithdrawalSlipItem withdrawalSlipItem : withdrawalSlipItems) {
			EBObject poItemObjectId = objectToObjectDao.getOtherReference(withdrawalSlipItem.getEbObjectId(), WithdrawalSlipItem.OR_TYPE_ID);
			Integer poObjectId = withdrawalSlipItem.getRefenceObjectId() != null ? withdrawalSlipItem.getRefenceObjectId() :
				poItemObjectId != null ? poItemObjectId.getId() : null;
			logger.debug("Allocating the unit cost of the WS Item using the FIFO Item Allocator.");
			WeightedAveItemAllocator<WithdrawalSlipItem> itemAllocator = itemId2CostAllocator.get(withdrawalSlipItem.getItemId());
			AllocatorKey key = null;
			if (itemAllocator == null) {
				itemAllocator = new WeightedAveItemAllocator<WithdrawalSlipItem>(itemDao, itemService,
						withdrawalSlipItem.getItemId(), warehouseId, date);
				key = AllocatorKey.getInstanceOf(withdrawalSlipItem.getItemId(), warehouseId);
				itemId2CostAllocator.put(key, itemAllocator);
			}
			logger.debug("Successfully saved the WS Items.");
			List<WithdrawalSlipItem> allocatedCsItems = null;
			try {
				allocatedCsItems = itemAllocator.allocateCost(withdrawalSlipItem);
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
			for (WithdrawalSlipItem withSItem : allocatedCsItems) {
				withSItem.setActive(true);
				AuditUtil.addAudit(withSItem, new Audit(user.getId(), true, currentDate));
				withdrawalSlipItemDao.save(withSItem);
				if(poObjectId != null) {
				objectToObjectDao.save(ObjectToObject.getInstanceOf(poObjectId, withSItem.getEbObjectId(),
						WithdrawalSlipItem.OR_TYPE_ID, user, currentDate));
				}
			}
		}
	}

	private List<WithdrawalSlipItem> getAllActiveWsItems(Integer wsEbObjectId){
		return withdrawalSlipItemDao.getAllActiveWsItems(wsEbObjectId, null);
	}

	/**
	 * Validate withdrawal slip form
	 * @param withdrawalSlip The withdrawal slip object
	 * @param errors The validation error
	 */
	public void validate(WithdrawalSlip withdrawalSlip, Errors errors, boolean isRequestByUserInput) {
		ValidatorUtil.validateCompany(withdrawalSlip.getCompanyId(), companyService, errors, "companyId");

		if(withdrawalSlip.getArCustomerId() == null && withdrawalSlip.getFleetId() == null
				&& (withdrawalSlip.getCustomerName() == null || withdrawalSlip.getCustomerName().trim().isEmpty())
				&& (withdrawalSlip.getFleetName() == null || withdrawalSlip.getFleetName().trim().isEmpty())) {
			errors.rejectValue("fleetId", null, null, ValidatorMessages.getString("WithdrawalSlipService.0"));
		} else {
			if(withdrawalSlip.getArCustomerId() != null &&
					!arCustomerDao.get(withdrawalSlip.getArCustomerId()).isActive()) {
				errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("WithdrawalSlipService.1"));
			} else if (withdrawalSlip.getCustomerName() != null && !withdrawalSlip.getCustomerName().trim().isEmpty() && withdrawalSlip.getArCustomerId() == null){
				errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("WithdrawalSlipService.2"));
			}
			if(withdrawalSlip.getFleetName() != null && !withdrawalSlip.getFleetName().trim().isEmpty() && withdrawalSlip.getFleetId() == null){
				errors.rejectValue("fleetId", null, null, ValidatorMessages.getString("WithdrawalSlipService.3"));
			}
		}

		if(withdrawalSlip.getAccountId() == null) {
			errors.rejectValue("accountId", null, null, ValidatorMessages.getString("WithdrawalSlipService.4"));
		} else {
			ValidatorUtil.checkInactiveAccount(accountService, withdrawalSlip.getAccountId(), "accountId", errors, null);
		}

		if(withdrawalSlip.getWarehouseId() == null){
			errors.rejectValue("warehouseId", null, null, ValidatorMessages.getString("WithdrawalSlipService.5"));
		} else if(!warehouseDao.get(withdrawalSlip.getWarehouseId()).isActive()) {
			errors.rejectValue("warehouseId", null, null, ValidatorMessages.getString("WithdrawalSlipService.6"));
		}

		if(!isRequestByUserInput) {
			if(withdrawalSlip.getEmployeeId() == null && (withdrawalSlip.getRequestedBy() == null || withdrawalSlip.getRequestedBy().trim().isEmpty())){
				errors.rejectValue("requestedBy", null, null, ValidatorMessages.getString("WithdrawalSlipService.7"));
			} else if (withdrawalSlip.getRequestedBy() != null && !withdrawalSlip.getRequestedBy().trim().isEmpty() && withdrawalSlip.getEmployeeId() == null){
				errors.rejectValue("requestedBy", null, null, ValidatorMessages.getString("WithdrawalSlipService.8"));
			}
		} else {
			if(withdrawalSlip.getRequesterName() != null && !StringFormatUtil.removeExtraWhiteSpaces(withdrawalSlip.getRequesterName()).isEmpty()) {
				if(StringFormatUtil.removeExtraWhiteSpaces(withdrawalSlip.getRequesterName()).length() > WithdrawalSlip.MAX_REQUESTER_NAME_CHAR) {
					errors.rejectValue("requesterName", null, null, String.format(ValidatorMessages.getString("WithdrawalSlipService.19"),
							WithdrawalSlip.MAX_REQUESTER_NAME_CHAR));
				}
			}
		}

		if(withdrawalSlip.getDate() == null){
			errors.rejectValue("date", null, null, ValidatorMessages.getString("WithdrawalSlipService.9"));
		} else if(!timePeriodService.isOpenDate(withdrawalSlip.getDate())) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("WithdrawalSlipService.10"));
		}

		List<WithdrawalSlipItem> withdrawalSlipItems = withdrawalSlip.getWithdrawalSlipItems();
		if(withdrawalSlip.getWithdrawalSlipItems() == null || withdrawalSlip.getWithdrawalSlipItems().isEmpty()){
			errors.rejectValue("withdrawalSlipItems", null, null, ValidatorMessages.getString("WithdrawalSlipService.11"));
		} else {
			int row = 1;
			for (WithdrawalSlipItem slipItem : withdrawalSlipItems) {
				double quantity = slipItem.getQuantity() != null ? slipItem.getQuantity() : 0;
				if(quantity <= 0){
					errors.rejectValue("withdrawalSlipItems", null, null, ValidatorMessages.getString("WithdrawalSlipService.12"));
				} else {
					EBObject poItemObjectId = objectToObjectDao.getOtherReference(slipItem.getEbObjectId(), WithdrawalSlipItem.OR_TYPE_ID);
					Integer referenceObjectId = slipItem.getRefenceObjectId() == null ?
						poItemObjectId != null ? poItemObjectId.getId() : null : slipItem.getRefenceObjectId();
					if(referenceObjectId != null){
						double remQty = NumberFormatUtil.roundOffTo2DecPlaces(getRemainingQty(referenceObjectId, withdrawalSlip.getId()));
						if(remQty < quantity){
							errors.rejectValue("withdrawalSlipItems", null, null, slipItem.getStockCode()
									+ ValidatorMessages.getString("WithdrawalSlipService.13")
									+ row + ValidatorMessages.getString("WithdrawalSlipService.14"));
						}
					}

					if(withdrawalSlip.getDate() != null && withdrawalSlip.getWarehouseId() != null){
						String quantityErrorMsg = ValidationUtil.validateWithdrawnQty(itemService, warehouseService,
								slipItem.getItemId(), false, withdrawalSlipItems, withdrawalSlip.getDate(),
								withdrawalSlip.getWarehouseId(), row);
						if(quantityErrorMsg != null) {
							errors.rejectValue("withdrawalSlipItems", null, null, quantityErrorMsg);
						}
					}
				}
				if(slipItem.getStockCode() == null || slipItem.getStockCode().trim().isEmpty()){
					errors.rejectValue("withdrawalSlipItems", null, null, ValidatorMessages.getString("WithdrawalSlipService.15"));
				}
				row++;
			}
		}
		if(withdrawalSlip.getPoNumber() != null && withdrawalSlip.getPoNumber().trim().length() > WithdrawalSlip.MAX_PO_CHARACTER) {
			errors.rejectValue("poNumber", null, null, ValidatorMessages.getString("WithdrawalSlipService.16") + WithdrawalSlip.MAX_PO_CHARACTER
					+ ValidatorMessages.getString("WithdrawalSlipService.17"));
		}

		if(withdrawalSlip.getCustomerAcctId() != null && !arCustomerAcctDao.get(withdrawalSlip.getCustomerAcctId()).isActive()){
			errors.rejectValue("customerAcctId", null, null, ValidatorMessages.getString("WithdrawalSlipService.18"));
		}
		//Validate form status
		FormWorkflow workflow = withdrawalSlip.getId() != 0 ? getFormWorkflow(withdrawalSlip.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
	}

	/**
	 * Get the page of withdrawal slip base on the search param.
	 * @param searchParam The search parametter.
	 * @param formStatusIds The form work flow ids.
	 * @param pageSetting The page settings.
	 * @return The page of withdrawal slip.
	 */
	public Page<WithdrawalSlip> getWithdrawalSlip(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting) {
		Page<WithdrawalSlip> result = withdrawalSlipDao.getWithdrawalSlip(searchParam, formStatusIds, pageSetting);
		for (WithdrawalSlip withdrawalSlip : result.getData()) {
			Integer ebObjectId = withdrawalSlip.getEbObjectId();
			// set company.
			Company company = getCompanyByEbObjectId(ebObjectId);
			withdrawalSlip.setCompanyName(company.getName());
			withdrawalSlip.setCompanyId(company.getId());
			// set fleet Profile
			FleetProfile fleetProfile = getFByEbObjectId(ebObjectId);
			if(fleetProfile != null){
				withdrawalSlip.setFleetName(fleetProfile.getCodeVesselName());
			}
			// Set customer.
			ArCustomer arCustomer = getCustomerByEbObjectId(ebObjectId);
			if(arCustomer != null){
				withdrawalSlip.setCustomerName(arCustomer.getName());
			}
			// Set employee Profile
			EmployeeProfile employeeProfile = getEPByEbObjectId(ebObjectId);
			if(employeeProfile != null){
				withdrawalSlip.setRequestedBy(employeeProfile.getEmployee().getFullName());
			}
		}
		return result;
	}

	public WithdrawalSlip getWithdrwalSlipForViewingEdit(Integer pId, boolean isRequestedByUserInput) {
		WithdrawalSlip withdrawalSlip = withdrawalSlipDao.get(pId);
		Integer ebObjectId = withdrawalSlip.getEbObjectId();
		// Set company.
		Company company = getCompanyByEbObjectId(ebObjectId);
		withdrawalSlip.setCompanyName(company.getName());
		withdrawalSlip.setCompanyId(company.getId());
		// Set warehouse.
		Warehouse warehouse = getWarehouseByEbObjectId(ebObjectId);
		Integer warehouseId = warehouse.getId();
		withdrawalSlip.setWarehouseName(warehouse.getName());
		withdrawalSlip.setWarehouseId(warehouseId);
		//Set account.
		Account account = getAccountByEbObjectId(ebObjectId);
		withdrawalSlip.setAccountName(account.getAccountName());
		withdrawalSlip.setAccountId(account.getId());
		// Set customer.
		ArCustomer arCustomer = getCustomerByEbObjectId(ebObjectId);
		if(arCustomer != null){
			withdrawalSlip.setCustomerName(arCustomer.getName());
			withdrawalSlip.setArCustomerId(arCustomer.getId());
			// Set Ar Customer Account.
			ArCustomerAccount customerAccount = getCustAcctByEbObjectId(ebObjectId);
			withdrawalSlip.setCustomerAcctId(customerAccount.getId());
			withdrawalSlip.setCustomerAccountName(customerAccount.getName());
		}
		// Set fleet.
		FleetProfile fleetProfile = getFByEbObjectId(ebObjectId);
		if(fleetProfile != null){
			withdrawalSlip.setFleetName(fleetProfile.getCodeVesselName());
			withdrawalSlip.setFleetId(fleetProfile.getId());
			withdrawalSlip.setDivisionId(fleetProfile.getDivisionId());
		}
		// Set withdrawal slip items.
		withdrawalSlip.setWithdrawalSlipItems(getWithdrawalItemsItems(ebObjectId, warehouseId));

		EmployeeProfile employeeProfile = null;
		if(!isRequestedByUserInput) {
			// Set the employee profile.
			employeeProfile = getEPByEbObjectId(ebObjectId);
			if(employeeProfile != null) {
				Employee employee  = employeeProfile.getEmployee();
				String middleInitial = employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()
						? employee.getMiddleName().charAt(0) + ". " : "";
				String employeeName = employee.getFirstName() + " " + middleInitial + employee.getLastName();
				withdrawalSlip.setRequestedBy(employeeName);
				withdrawalSlip.setEmployeeId(employeeProfile.getEmployeeId());
			}
		}

		warehouse = null; 
		employeeProfile = null;
		fleetProfile = null;
		arCustomer = null;
		company = null;
		return withdrawalSlip;
	}

	public List<WithdrawalSlipItem> getWithdrawalItemsItems(Integer ebObjectId, Integer warehouseId) {
		List<WithdrawalSlipItem> processedList = getAllActiveWsItems(ebObjectId);
		Double existingStocks = null;
		Map<String, WithdrawalSlipItem> wsHM = new HashMap<String, WithdrawalSlipItem>();
		WithdrawalSlipItem slipItem = null;
		double qty = 0;
		for (WithdrawalSlipItem wsi : processedList) {
			EBObject poObject = objectToObjectDao.getOtherReference(wsi.getEbObjectId(), WithdrawalSlipItem.OR_TYPE_ID);
			// poItem and nonPo are used to distinguish if the item is from PO reference 
			// to avoid merging of same Items if the other one has no PO reference
			String key = poObject == null ? "poItem "+wsi.getItemId() : "nonPo " + wsi.getItemId();
			if(wsHM.containsKey(key)){
				slipItem = wsHM.get(key);
				qty = (slipItem.getQuantity() != null ? slipItem.getQuantity() : 0) +
						(wsi.getQuantity() != null ? wsi.getQuantity() : 0);
				slipItem.setQuantity(qty);
				slipItem.setOrigQty(qty);
				wsHM.put(key, slipItem);
			} else {
				existingStocks = itemService.getItemExistingStocks(wsi.getItemId(), warehouseId, new Date());
				wsi.setStockCode(wsi.getItem().getStockCode());
				wsi.setExistingStocks(existingStocks);
				wsi.setOrigQty(wsi.getQuantity());
				wsHM.put(key, wsi);
			}
		}
		List<WithdrawalSlipItem> withdrawalSlipItems = new ArrayList<>();
		for (Map.Entry<String, WithdrawalSlipItem> disc : wsHM.entrySet()) {
			withdrawalSlipItems.add(disc.getValue());
		}
		return withdrawalSlipItems;
	}

	protected Company getCompanyByEbObjectId(Integer ebObjectId){
		Company company = null;
		EBObject compEBObject = objectToObjectDao.getOtherReference(ebObjectId, WithdrawalSlip.OR_TYPE_COMPANY);
		if(compEBObject != null){
			company = companyDao.getByEbObjectId(compEBObject.getId());
		}
		return company;
	}

	private ArCustomerAccount getCustAcctByEbObjectId(Integer ebObjectId){
		ArCustomerAccount customerAccount = null;
		EBObject custAcctEBObject = objectToObjectDao.getOtherReference(ebObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER_ACCOUNT);
		if(custAcctEBObject != null){
			customerAccount = arCustomerAcctDao.getByEbObjectId(custAcctEBObject.getId());
		}
		return customerAccount;
	}

	private Warehouse getWarehouseByEbObjectId(Integer ebObjectId){
		Warehouse warehouse = null;
		EBObject wEBObject = objectToObjectDao.getOtherReference(ebObjectId, WithdrawalSlip.OR_TYPE_WAREHOUSE);
		if(wEBObject != null){
			warehouse = warehouseDao.getByEbObjectId(wEBObject.getId());
		}
		return warehouse;
	}

	private Account getAccountByEbObjectId(Integer ebObjectId){
		Account account = null;
		EBObject acctEBObject = objectToObjectDao.getOtherReference(ebObjectId, WithdrawalSlip.OR_TYPE_ACCOUNT);
		if(acctEBObject != null){
			account = accountDao.getByEbObjectId(acctEBObject.getId());
		}
		return account;
	}

	protected FleetProfile getFByEbObjectId(Integer ebObjectId){
		FleetProfile fleetProfile = null;
		EBObject fpEBObject = objectToObjectDao.getOtherReference(ebObjectId, WithdrawalSlip.OR_TYPE_FLEET_PROFILE);
		EBObject divEbObject = null;
		Division division = null;
		if(fpEBObject != null){
			fleetProfile = fleetProfileDao.getByEbObjectId(fpEBObject.getId());
			divEbObject = objectToObjectDao.getOtherReference(fleetProfile.getEbObjectId(), FleetProfile.FP_DIVISION_OR_TYPE_ID);
			if (divEbObject != null) {
				fleetProfile.setRefObjectId(divEbObject.getId());
				division = divisionDao.getByEbObjectId(divEbObject.getId());
				fleetProfile.setDivisionId(division.getId());
			}
			divEbObject = null;
		}
		return fleetProfile;
	}

	protected ArCustomer getCustomerByEbObjectId(Integer ebObjectId){
		ArCustomer arCustomer = null;
		EBObject fpEBObject = objectToObjectDao.getOtherReference(ebObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER);
		if(fpEBObject != null){
			arCustomer = arCustomerDao.getByEbObjectId(fpEBObject.getId());
		}
		return arCustomer;
	}

	protected EmployeeProfile getEPByEbObjectId(Integer ebObjectId){
		EmployeeProfile employeeProfile = null;
		EBObject fpEBObject = objectToObjectDao.getOtherReference(ebObjectId, WithdrawalSlip.OR_TYPE_EMPLOYEE_PROFILE);
		if(fpEBObject != null){
			employeeProfile = employeeProfileDao.getByEbObjectId(fpEBObject.getId());
		}
		return employeeProfile;
	}

	/**
	 * Get the PO Remaining QTY by reference object id.
	 * @param refenceObjectId The reference object id.
	 * @param wsId The id of Withdrawal Slip.
	 * @return The remaining quantity.
	 */
	public double getRemainingQty(Integer refenceObjectId, Integer wsId) {
		return rPurchaseOrderDao.getRemainingQty(refenceObjectId, wsId);
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		// Do nothing
	}

	@Override
	public void updateUnitCost(RItemCostUpdateService costUpdateService,
			WeightedAveItemAllocator<ItemTransaction> fifoAllocator, ItemTransaction it, int itemId, int warehouseId,
			Date formDate, boolean isAllocateRpTo) {
		// Do nothing
	}

	@Override
	public void processAllocatedItem(int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems,
			ItemTransaction currentAllocItem) throws CloneNotSupportedException {
		logger.info("Current Allocated transaction for withdrawal slip item :::: "+currentAllocItem);
		WithdrawalSlip withdrawalSlip = withdrawalSlipDao.get(currentAllocItem.getId());
		List<WithdrawalSlipItem> wsItems = withdrawalSlipItemDao.getAllActiveWsItems(withdrawalSlip.getEbObjectId(), itemId);

		ListProcessorUtil<WithdrawalSlipItem> remover = new ListProcessorUtil<WithdrawalSlipItem>();
		List<Integer> formIds = remover.collectFormIds(wsItems);

		logger.debug("Processing the withdrawal slip items. Removing the duplicate items.");
		List<WithdrawalSlipItem> processedItems = summarizeWithdrawalSlipItems(wsItems);
		Double allocQty = currentAllocItem.getQuantity();
		Double qtyToBeWithdrawn = null;
		WithdrawalSlipItem splitItem = null;
		List<Integer> savedWsItemIds = new ArrayList<Integer>();
		for (WithdrawalSlipItem wsi : processedItems) {
			//Create a new instance of Withdrawal Slip Item
			while (currentAllocItem != null) {
				if(qtyToBeWithdrawn == null) {
					qtyToBeWithdrawn = wsi.getQuantity();
				}

				if(allocQty >= qtyToBeWithdrawn) {
					wsi.setUnitCost(currentAllocItem.getUnitCost());
					wsi.setQuantity(qtyToBeWithdrawn);
					// Update WS items
					withdrawalSlipItemDao.saveOrUpdate(wsi);
					savedWsItemIds.add(wsi.getId());
					allocQty = NumberFormatUtil.roundOffNumber((allocQty - qtyToBeWithdrawn), NumberFormatUtil.SIX_DECIMAL_PLACES);
					if(allocQty == 0.0) {
						logger.debug("Current allocated quantity is zero");
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
						logger.info("Current Allocated item: "+currentAllocItem);
					}
					qtyToBeWithdrawn = null;
					break;
				} else {
					if(allocQty > 0) {
						logger.info("allocated qty is greater: "+currentAllocItem);
						splitItem = (WithdrawalSlipItem) wsi.clone();
						splitItem.setId(0);
						splitItem.setQuantity(allocQty);
						splitItem.setUnitCost(currentAllocItem.getUnitCost());
						// Save WS items
						withdrawalSlipItemDao.saveOrUpdate(splitItem);
						qtyToBeWithdrawn = qtyToBeWithdrawn - allocQty;
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
					}
				}
			}
		}

		List<Domain> toBeDeleted = new ArrayList<Domain>();
		int frequency = 0;
		for (Integer id : formIds) {
			//Delete the items that were not updated.
			frequency = Collections.frequency(savedWsItemIds, id);
			if(frequency == 0) {
				logger.info("Deleting withdrawal slip item/s");
				toBeDeleted.add(withdrawalSlipItemDao.get(id));
			}
		}

		if(!toBeDeleted.isEmpty()) {
			logger.info("Deleting withdrawal slip item/s");
			for (Domain tbd : toBeDeleted) {
				logger.debug("Deleting the id: "+tbd);
				withdrawalSlipItemDao.delete(tbd);
			}
		}

		logger.debug("Freeing up memory allocation for withdrawal slip out.");
		remover = null;
		processedItems = null;
		toBeDeleted = null;
		formIds = null;
		savedWsItemIds = null;
		logger.info("Successfully updated the quantity and unit cost of item transaction "
				+ "from Withdrawal slip form.");
	}

	@Override
	public String getFormLabel() {
		return "WS";
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

	private List<WithdrawalSlipItem> summarizeWithdrawalSlipItems(List<WithdrawalSlipItem> wsItems) {
		List<WithdrawalSlipItem> updatedItems = new ArrayList<WithdrawalSlipItem>();
		Map<String, WithdrawalSlipItem> wsItemHm = new HashMap<String, WithdrawalSlipItem>();

		WithdrawalSlipItem editedItem = null;
		String itemKey = null;
		for (WithdrawalSlipItem wsi : wsItems) {
			itemKey = "i" + wsi.getItemId();
			if(wsItemHm.containsKey(itemKey)) {
				editedItem = processEditedItem(wsi, wsItemHm.get(itemKey));
				wsItemHm.put(itemKey, editedItem);
			} else {
				wsItemHm.put(itemKey, wsi);
			}
		}

		for (Map.Entry<String, WithdrawalSlipItem> iHM : wsItemHm.entrySet()) {
			updatedItems.add(iHM.getValue());
		}

		wsItemHm = null;
		editedItem = null;

		Collections.sort(updatedItems, new Comparator<WithdrawalSlipItem>() {
			@Override
			public int compare(WithdrawalSlipItem wsi1, WithdrawalSlipItem wsi2) {
				if (wsi1.getId() < wsi2.getId()) {
					return -1;
				} else if (wsi1.getId() > wsi2.getId()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		return updatedItems;
	}

	private WithdrawalSlipItem processEditedItem(WithdrawalSlipItem wsi, WithdrawalSlipItem editedItem) {
		editedItem.setQuantity(wsi.getQuantity() + editedItem.getQuantity());
		editedItem.setOrigQty((wsi.getOrigQty() != null ? wsi.getOrigQty() : 0)
				+ (editedItem.getOrigQty() != null ? editedItem.getOrigQty() : 0));
		return editedItem;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		WithdrawalSlip withdrawalSlip = withdrawalSlipDao.getByEbObjectId(ebObjectId);
		int pId = withdrawalSlip.getId();
		FormProperty property = workflowHandler.getProperty(withdrawalSlip.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = withdrawalSlip.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Withdrawal Slip - " + withdrawalSlip.getWsNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + withdrawalSlip.getCompanyName())
				.append(" " + withdrawalSlip.getWarehouseName())
				.append(" " + DateUtil.formatDate(withdrawalSlip.getDate()))
				.append(" " + withdrawalSlip.getRequesterName());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case WithdrawalSlip.OBJECT_TYPE:
			return withdrawalSlipDao.getByEbObjectId(ebObjectId);
		case WithdrawalSlipItem.OBJECT_TYPE:
			return withdrawalSlipItemDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}
