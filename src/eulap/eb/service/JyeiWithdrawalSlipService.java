package eulap.eb.service;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.EmployeeProfileDao;
import eulap.eb.dao.FleetProfileDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.JyeiWithdrawalSlipDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.RequisitionFormDao;
import eulap.eb.dao.SerialItemSetupDao;
import eulap.eb.dao.WarehouseDao;
import eulap.eb.dao.WithdrawalSlipDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.JyeiWithdrawalSlip;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.RequisitionFormItem;
import eulap.eb.domain.hibernate.RequisitionType;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.SerialItemSetup;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.domain.hibernate.WithdrawalSlipItem;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.JyeiFormItemDto;
import eulap.eb.web.dto.JyeiWithdrawalSlipPrintoutDto;
import eulap.eb.web.dto.ResultProperty;
import eulap.eb.web.dto.RfReferenceDto;
import eulap.eb.web.dto.WithdrawalSlipDto;

/**
 * Class that will handle business logic for JYEI Withdrawal Slip.

 */
@Service
public class JyeiWithdrawalSlipService extends WithdrawalSlipService {
	private Logger logger = Logger.getLogger(JyeiWithdrawalSlipService.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private ArCustomerAcctDao arCustomerAcctDao;
	@Autowired
	private WarehouseDao warehouseDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private JyeiWithdrawalSlipDao jyeiWithdrawalSlipDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private FleetProfileDao fleetProfileDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private EmployeeProfileDao employeeProfileDao;
	@Autowired
	private WithdrawalSlipDao withdrawalSlipDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private RequisitionFormDao requisitionFormDao;
	@Autowired
	private RequisitionFormService requisitionFormService;
	@Autowired
	private SerialItemSetupDao siSetupDao;
	@Autowired
	private SerialItemService serializedItemService;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private FleetProfileService fleetProfileService;

	/**
	 * Validation method for JYEI Withdrawal Slip.
	 */
	public void validateWSDto(WithdrawalSlipDto wsDto, Errors errors, boolean isRequestByUserInput) {
		WithdrawalSlip withdrawalSlip = wsDto.getWithdrawalSlip();
		ValidatorUtil.validateCompany(withdrawalSlip.getCompanyId(), companyService, errors, "withdrawalSlip.companyId");

		if(withdrawalSlip.getWarehouseId() == null){
			errors.rejectValue("withdrawalSlip.warehouseId", null, null, ValidatorMessages.getString("WithdrawalSlipService.5"));
		} else if(!warehouseDao.get(withdrawalSlip.getWarehouseId()).isActive()) {
			errors.rejectValue("withdrawalSlip.warehouseId", null, null, ValidatorMessages.getString("WithdrawalSlipService.6"));
		}

		if(!isRequestByUserInput) {
			if(withdrawalSlip.getEmployeeId() == null && (withdrawalSlip.getRequestedBy() == null || withdrawalSlip.getRequestedBy().trim().isEmpty())){
				errors.rejectValue("withdrawalSlip.requestedBy", null, null, ValidatorMessages.getString("WithdrawalSlipService.7"));
			} else if (withdrawalSlip.getRequestedBy() != null && !withdrawalSlip.getRequestedBy().trim().isEmpty() && withdrawalSlip.getEmployeeId() == null){
				errors.rejectValue("withdrawalSlip.requestedBy", null, null, ValidatorMessages.getString("WithdrawalSlipService.8"));
			}
		} else {
			if(withdrawalSlip.getRequesterName() != null && !StringFormatUtil.removeExtraWhiteSpaces(withdrawalSlip.getRequesterName()).isEmpty()) {
				if(StringFormatUtil.removeExtraWhiteSpaces(withdrawalSlip.getRequesterName()).length() > WithdrawalSlip.MAX_REQUESTER_NAME_CHAR) {
					errors.rejectValue("withdrawalSlip.requesterName", null, null, String.format(ValidatorMessages.getString("WithdrawalSlipService.19"),
							WithdrawalSlip.MAX_REQUESTER_NAME_CHAR));
				}
			}
		}

		if(withdrawalSlip.getDate() == null){
			errors.rejectValue("withdrawalSlip.date", null, null, ValidatorMessages.getString("WithdrawalSlipService.9"));
		} else if(!timePeriodService.isOpenDate(withdrawalSlip.getDate())) {
			errors.rejectValue("withdrawalSlip.date", null, null, ValidatorMessages.getString("WithdrawalSlipService.10"));
		}

		if(wsDto.getJyeiWithdrawalSlip().getWithdrawnBy() == "" || wsDto.getJyeiWithdrawalSlip().getWithdrawnBy() == null || wsDto.getJyeiWithdrawalSlip().getWithdrawnBy().isEmpty()) {
			errors.rejectValue("jyeiWithdrawalSlip.withdrawnBy", null, null, ValidatorMessages.getString("WithdrawalSlipService.24"));
		} else if (StringFormatUtil.removeExtraWhiteSpaces(wsDto.getJyeiWithdrawalSlip().getWithdrawnBy()).length() > WithdrawalSlip.MAX_REQUESTER_NAME_CHAR) {
			errors.rejectValue("jyeiWithdrawalSlip.withdrawnBy", null, null, String.format(ValidatorMessages.getString("WithdrawalSlipService.25"),
					WithdrawalSlip.MAX_REQUESTER_NAME_CHAR));
		}

		List<WithdrawalSlipItem> withdrawalSlipItems = withdrawalSlip.getWithdrawalSlipItems();
		if(withdrawalSlip.getWithdrawalSlipItems() != null && !withdrawalSlip.getWithdrawalSlipItems().isEmpty()) {
			int row = 1;
			for (WithdrawalSlipItem slipItem : withdrawalSlipItems) {
				double quantity = slipItem.getQuantity() != null ? slipItem.getQuantity() : 0;
				if(quantity <= 0){
					errors.rejectValue("withdrawalSlip.withdrawalSlipItems", null, null, ValidatorMessages.getString("WithdrawalSlipService.12"));
				} else {
					EBObject poItemObjectId = objectToObjectDao.getOtherReference(slipItem.getEbObjectId(), WithdrawalSlipItem.OR_TYPE_ID);
					Integer referenceObjectId = slipItem.getRefenceObjectId() == null ?
						poItemObjectId != null ? poItemObjectId.getId() : null : slipItem.getRefenceObjectId();
					if(referenceObjectId != null){
						double remQty = NumberFormatUtil.roundOffTo2DecPlaces(
								requisitionFormService.getRemainingRFQty(referenceObjectId, withdrawalSlip.getId(), null, true));
						if(remQty < quantity){
							errors.rejectValue("withdrawalSlip.withdrawalSlipItems", null, null, slipItem.getStockCode()
									+ ValidatorMessages.getString("WithdrawalSlipService.13")
									+ row + ValidatorMessages.getString("WithdrawalSlipService.14"));
						}
					}

					if(withdrawalSlip.getDate() != null && withdrawalSlip.getWarehouseId() != null){
						boolean isWarehouseChanged = false;
						Warehouse origWarehouse = getWarehouseByEbObjectId(withdrawalSlip.getEbObjectId());
						if(origWarehouse != null) {
							isWarehouseChanged = !withdrawalSlip.getWarehouseId().equals(origWarehouse.getId());
						}
						String quantityErrorMsg = ValidationUtil.validateWithdrawnQty(itemService, warehouseService,
								slipItem.getItemId(), isWarehouseChanged, withdrawalSlipItems, withdrawalSlip.getDate(),
								withdrawalSlip.getWarehouseId(), row);
						if(quantityErrorMsg != null) {
							errors.rejectValue("withdrawalSlip.withdrawalSlipItems", null, null, quantityErrorMsg);
						}
					}
				}
				if(slipItem.getStockCode() == null || slipItem.getStockCode().trim().isEmpty()){
					errors.rejectValue("withdrawalSlip.withdrawalSlipItems", null, null, ValidatorMessages.getString("WithdrawalSlipService.15"));
				}
				row++;
			}
		}

		if(withdrawalSlip.getPoNumber() != null && withdrawalSlip.getPoNumber().trim().length() > WithdrawalSlip.MAX_PO_CHARACTER) {
			errors.rejectValue("withdrawalSlip.poNumber", null, null, ValidatorMessages.getString("WithdrawalSlipService.16") + WithdrawalSlip.MAX_PO_CHARACTER
					+ ValidatorMessages.getString("WithdrawalSlipService.17"));
		}
		referenceDocumentService.validateReferences(wsDto.getReferenceDocuments(), errors);
	}

	/**
	 * Saving method for JYEI Withdrawal Slip.
	 * @param wsDto The withdrawal slip DTO.
	 * @param user The current user logged on.
	 */
	public void saveJyeiWithdrawalSlip(WithdrawalSlipDto wsDto, User user) throws InvalidClassException, ClassNotFoundException {
		WithdrawalSlip ws = wsDto.getWithdrawalSlip();
		boolean isNew = ws.getId() == 0;
		String poNum = ws.getPoNumber();
		ws.setPoNumber(poNum.substring(poNum.lastIndexOf(" ") + 1));
		ebFormServiceHandler.saveForm(ws, user);

		JyeiWithdrawalSlip jyeiWithdrawalSlip = wsDto.getJyeiWithdrawalSlip();
		jyeiWithdrawalSlip.setWithdrawalSlipId(ws.getId());
		jyeiWithdrawalSlipDao.saveOrUpdate(jyeiWithdrawalSlip);

		// Set object to object relationship for withdrawal slip item and requisition form item.
		EBObject refReqFormEbObject = objectToObjectDao.getOtherReference(
				ws.getEbObjectId(), JyeiWithdrawalSlip.WITHDRAWAL_SLIP_REQUISITION_FORM_OR_TYPE);
		Integer reqFormRefObjId = ws.getRefenceObjectId() != null ? ws.getRefenceObjectId()
				: refReqFormEbObject != null ? refReqFormEbObject.getId() : null;
		if(isNew) {
			if(reqFormRefObjId != null){
				objectToObjectDao.save(ObjectToObject.getInstanceOf(reqFormRefObjId,
						ws.getEbObjectId(), JyeiWithdrawalSlip.WITHDRAWAL_SLIP_REQUISITION_FORM_OR_TYPE, user, new Date()));
			}
		} else {
			logger.info("Set to inactive the saved serial items.");
			List<SerialItem> sItems = serialItemService.getSerializedItemByRefObjId(
					ws.getEbObjectId(), JyeiWithdrawalSlip.WS_SERIAL_ITEM_OR_TYPE);
			serialItemService.setSerialNumberToInactive(sItems);
		}
		// Save serialized items.
		serialItemService.saveSerializedItems(wsDto.getSerialItems(), ws.getEbObjectId(),
				ws.getWarehouseId(), user, JyeiWithdrawalSlip.WS_SERIAL_ITEM_OR_TYPE, false);
		// Save reference documents.
		referenceDocumentService.saveReferenceDocuments(user, isNew, ws.getEbObjectId(), wsDto.getReferenceDocuments(), true);
	}

	/**
	 * Get the Withdrawal Slip Dto object for viewing or printout.
	 * @param wsId The withdrawal slip id.
	 * @param isForPrintout True if for printout, otherwise, false.
	 * @return The withdrawal slip dto object.
	 */
	public WithdrawalSlipDto getWithdrawalSlipDtoForView(int wsId, boolean isForPrintout) {
		WithdrawalSlipDto withdrawalSlipDto = new WithdrawalSlipDto();
		WithdrawalSlip withdrawalSlip = getWithdrwalSlipForViewingEdit(wsId, true);
		JyeiWithdrawalSlip jyeiWS = jyeiWithdrawalSlipDao.getAllByRefId(
				"withdrawalSlipId", wsId).iterator().next();
		withdrawalSlip.setPoNumber(withdrawalSlip.getPoNumber());
		if(withdrawalSlip.getPoNumber() != null && !withdrawalSlip.getPoNumber().isEmpty()) {
			for(WithdrawalSlipItem wsItem : withdrawalSlip.getWithdrawalSlipItems()) {
				Integer refItemObjectId = objectToObjectDao.getOtherReference(wsItem.getEbObjectId(), WithdrawalSlipItem.OR_TYPE_ID).getId();
				wsItem.setReferenceObjectId(refItemObjectId);
			}
		}
		withdrawalSlipDto.setWithdrawalSlip(withdrawalSlip);
		withdrawalSlipDto.setJyeiWithdrawalSlip(jyeiWS);
		boolean isCancelled = withdrawalSlip.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;
		List<SerialItem> wsSerialItems = serialItemService.getSerializeItemsByRefObjectId(withdrawalSlip.getEbObjectId(),
				withdrawalSlip.getWarehouseId(), JyeiWithdrawalSlip.WS_SERIAL_ITEM_OR_TYPE, isCancelled);
		if(isForPrintout) {
			withdrawalSlipDto.setWsItemDtos(convertItemsForPrintout(withdrawalSlip.getWithdrawalSlipItems(), wsSerialItems));
		} else {
			withdrawalSlipDto.setReferenceDocuments(referenceDocumentService.getReferenceDocuments(
					withdrawalSlip.getEbObjectId()));
			withdrawalSlipDto.setSerialItems(wsSerialItems);
		}
		return withdrawalSlipDto;
	}

	private List<JyeiFormItemDto> convertItemsForPrintout(List<WithdrawalSlipItem> wsItems, List<SerialItem> wsSerialItems) {
		List<JyeiFormItemDto> wsItemDtos = new ArrayList<>();
		if(!wsItems.isEmpty()) {
			for(WithdrawalSlipItem wsItem : wsItems) {
				wsItemDtos.add(JyeiFormItemDto.getInstanceOf(wsItem.getStockCode(),
						wsItem.getItem().getDescription(), "", wsItem.getQuantity(),
						wsItem.getItem().getUnitMeasurement().getName(), 0, 0));
			}
		}
		if(!wsSerialItems.isEmpty()) {
			for(SerialItem wsSerialItem : wsSerialItems) {
				wsItemDtos.add(JyeiFormItemDto.getInstanceOf(wsSerialItem.getStockCode(),
						wsSerialItem.getItem().getDescription(), wsSerialItem.getSerialNumber(),
						wsSerialItem.getQuantity(), wsSerialItem.getUom(), 0, 0));
			}
		}
		return wsItemDtos;
	}

	/**
	 * Get the paged list of jyei withdrawal slip objects filtered by requisition type id, and the search param.
	 * @param requisitionTypeId The requisition type id.
	 * @param searchParam The search parameters.
	 * @param formStatusIds The form statuses.
	 * @param pageSetting The page setting.
	 * @return The paged list of JYEI Withdrawal Slips.
	 */
	public Page<JyeiWithdrawalSlip> getWSByRequisitionTypeId(Integer requisitionTypeId,
			ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting) {
		Page<JyeiWithdrawalSlip> result = jyeiWithdrawalSlipDao.getWSByRequisitionTypeId(requisitionTypeId, searchParam, formStatusIds, pageSetting);
		for (JyeiWithdrawalSlip jyeiWs : result.getData()) {
			WithdrawalSlip withdrawalSlip = jyeiWs.getWithdrawalSlip();
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

			withdrawalSlip.setWsNumber(jyeiWithdrawalSlipDao.generateSequenceNo(withdrawalSlip.getCompanyId(), withdrawalSlip.getTypeId()));
			// Save company object to object.
			Company company = companyDao.get(withdrawalSlip.getCompanyId());
			objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(company.getEbObjectId(),
					wsEbObjectId, WithdrawalSlip.OR_TYPE_COMPANY, user, new Date()));
			// Save warehouse object to object.
			Warehouse warehouse = warehouseDao.get(withdrawalSlip.getWarehouseId());
			objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(warehouse.getEbObjectId(),
					wsEbObjectId, WithdrawalSlip.OR_TYPE_WAREHOUSE, user, new Date()));

			if (withdrawalSlip.getAccountId() != null) {
				// Save account object to object.
				Account account = accountDao.get(withdrawalSlip.getAccountId());
				objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(account.getEbObjectId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_ACCOUNT, user, new Date()));
			}
			if(withdrawalSlip.getFleetId() != null) {
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
			WithdrawalSlip savedWS = withdrawalSlipDao.get(withdrawalSlipId);
			DateUtil.setCreatedDate(withdrawalSlip, savedWS.getCreatedDate());
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
			if (accountEbObject != null) {
				List<ObjectToObject> accountObjectToObjects = objectToObjectDao.getReferenceObjects(accountEbObject.getId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_ACCOUNT);
				if(accountObjectToObjects != null && !accountObjectToObjects.isEmpty()){
					ObjectToObject toObject = accountObjectToObjects.iterator().next();
					Account account = accountDao.get(withdrawalSlip.getAccountId());
					toObject.setFromObjectId(account.getEbObjectId());
					objectToObjectDao.update(toObject);
				}
			}
			// Update Fleet Profile object to object.
			EBObject fpEbObject = objectToObjectDao.getOtherReference(wsEbObjectId, WithdrawalSlip.OR_TYPE_FLEET_PROFILE);
			if(fpEbObject != null){
				List<ObjectToObject> fpObjectToObjects = objectToObjectDao.getReferenceObjects(fpEbObject.getId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_FLEET_PROFILE);
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
				List<ObjectToObject> customerObjectToObjects = objectToObjectDao.getReferenceObjects(customerEbObject.getId(),
						wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER);
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
					List<ObjectToObject> custAcctObjectToObjects = objectToObjectDao.getReferenceObjects(custAcctEbObject.getId(),
							wsEbObjectId, WithdrawalSlip.OR_TYPE_CUSTOMER_ACCOUNT);
					ObjectToObject objectToObject = custAcctObjectToObjects.iterator().next();
					if(withdrawalSlip.getArCustomerId() != null){
						ArCustomerAccount customerAccount = arCustomerAcctDao.getArCustomerAccount(withdrawalSlip.getCompanyId(),
								withdrawalSlip.getArCustomerId());
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
		saveWithdrawalItems(withdrawalSlip.getWithdrawalSlipItems(), user, wsEbObjectId, withdrawalSlip.getWarehouseId(), withdrawalSlip.getDate());
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		WithdrawalSlip withdrawalSlip = withdrawalSlipDao.getByEbObjectId(ebObjectId);
		int pId = withdrawalSlip.getId();
		Integer requisitionTypeId = jyeiWithdrawalSlipDao.getByWithdrawalSlipId(pId).getRequisitionTypeId();
		withdrawalSlip.setTypeId(requisitionTypeId);
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

	private String getFormName(Integer requisitionTypeId, boolean isTitle) {
		String formName = isTitle? "Withdrawal Slip " : "";
		switch (requisitionTypeId) {
			case RequisitionType.RT_TIRE:
				formName += "Tire";
				break;

			case RequisitionType.RT_FUEL:
				formName += "Fuel";
				break;

			case RequisitionType.RT_PMS:
				formName += "PMS";
				break;

			case RequisitionType.RT_ELECTRICAL:
				formName += "Electrical";
				break;

			case RequisitionType.RT_CONSTRUCTION_MATERIAL:
				formName += isTitle ? "Construction Material" : "CM";
				break;

			case RequisitionType.RT_ADMIN:
				formName += "Admin";
				break;

			case RequisitionType.RT_MOTORPOOL:
				formName += "Motorpool";
				break;

			case RequisitionType.RT_OIL:
				formName += "Oil";
				break;

			case RequisitionType.RT_SUBCON:
				formName += isTitle ? "Subcon Settlement" : "SUBCON" ;
				break;

			case RequisitionType.RT_PAKYAWAN:
				formName += "Pakyawan";
				break;
		}
		return formName;
	}

	/**
	 * General search method for JYEI Withdrawal Slip.
	 * @param searchCriteria The search criteria.
	 * @param requisitionTypeId The requisition type id.
	 * @return The list of general search results for jyei withdrawal slip.
	 */
	public List<FormSearchResult> searchJyeiWithdrawalSlips(String searchCriteria, int requisitionTypeId) {
		logger.info("Searching JYEI Withdrawal Slips.");
		Page<JyeiWithdrawalSlip> gWithdrawalSlips = jyeiWithdrawalSlipDao.searchJyeiWithdrawalSlips(
				searchCriteria, requisitionTypeId, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<>();
		String title = null;
		String status = null;
		for(JyeiWithdrawalSlip gWithdrawalSlip : gWithdrawalSlips.getData()) {
			WithdrawalSlip ws = gWithdrawalSlip.getWithdrawalSlip();
			Company c = getCompanyByEbObjectId(ws.getEbObjectId());
			title = getFormName(gWithdrawalSlip.getRequisitionTypeId(), true) + " - " + String.valueOf(ws.getWsNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", c.getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(ws.getDate())));
			status = ws.getFormWorkflow().getCurrentStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(ws.getId(), title, properties));
		}
		return result;
	}

	/**
	 * Get the list of requisition form references
	 * @param user The current user logged
	 * @param companyId The company id
	 * @param fleetId The fleet id
	 * @param projectId The project id
	 * @param rfNumber The requisition form number
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param status The form status, used if the RF form was already
	 * been used as reference form, otherwise UNSUSED
	 * @param reqTypeId The requisition form type id
	 * @param pageSetting The page setting
	 * @param isPakyawanSubconOnly True if retrieve PAKYAWAN and SUBCON
	 * requisition form types only, otherwise false
	 * @return The list of requisition form references
	 */
	public Page<RfReferenceDto> getRequisitionForms(User user, Integer companyId, Integer fleetId,
			Integer projectId, Integer rfNumber, Date dateFrom, Date dateTo, Integer status,
			Integer reqTypeId, int pageNumber, boolean isPakyawanSubconOnly, boolean isExcludePrForms) {
		return requisitionFormDao.getRfReferenceForms(companyId, fleetId, projectId, rfNumber,
				dateFrom, dateTo, status, reqTypeId, true, isPakyawanSubconOnly, isExcludePrForms,
				new PageSetting(pageNumber));
	}

	/**
	 * Convert Requisition Form to Withdrawal Slip DTO.
	 * @param rfId The requisition form id.
	 * @param user The current logged in user.
	 * @return The Withdrawal Slip DTO.
	 */
	public WithdrawalSlipDto getAndConvertRf(Integer rfId, User user) {
		RequisitionForm requisitionForm = requisitionFormService.getById(rfId, true, false);
		WithdrawalSlipDto wsDto = new WithdrawalSlipDto();
		WithdrawalSlip withdrawalSlip = new WithdrawalSlip();
		JyeiWithdrawalSlip jyeiWS = new JyeiWithdrawalSlip();
		withdrawalSlip.setRequesterName(requisitionForm.getRequestedBy());
		withdrawalSlip.setCompanyId(requisitionForm.getCompanyId());
		withdrawalSlip.setRefenceObjectId(requisitionForm.getEbObjectId());
		withdrawalSlip.setPoNumber(requisitionForm.getSequenceNumber().toString());
		withdrawalSlip.setWarehouseId(requisitionForm.getWarehouseId());
		jyeiWS.setRequisitionTypeId(requisitionForm.getRequisitionTypeId());

		List<WithdrawalSlipItem> wsItems = new ArrayList<>();
		List<SerialItem> serializedItems = new ArrayList<>();
		WithdrawalSlipItem wsItem = null;
		SerialItem serialItem = null;
		double remainingQty = 0;
		for(RequisitionFormItem rfItem : requisitionForm.getRequisitionFormItems()) {
			remainingQty = requisitionFormService.getRemainingRFQty(rfItem.getEbObjectId(), null, null, true);
			if (remainingQty > 0) {
				Item item = rfItem.getItem();
				int itemId = item.getId();
				SerialItemSetup serialItemSetup = siSetupDao.getSerialItemSetupByItemId(itemId);
				if(!serialItemSetup.isSerializedItem()) {
					wsItem = new WithdrawalSlipItem();
					wsItem.setItem(item);
					wsItem.setItemId(itemId);
					wsItem.setQuantity(remainingQty);
					wsItem.setUnitCost(rfItem.getUnitCost());
					wsItem.setStockCode(item.getStockCode());
					wsItem.setReferenceObjectId(rfItem.getEbObjectId());
					wsItem.setOrigRefObjectId(rfItem.getEbObjectId());
					wsItems.add(wsItem);
					wsItem = null;
				} else {
					for (int i = 0; i < remainingQty; i++) {
						serialItem = new SerialItem();
						serialItem.setItem(item);
						serialItem.setItemId(item.getId());
						serialItem.setQuantity(1.0);
						serialItem.setUnitCost(rfItem.getUnitCost());
						serialItem.setStockCode(item.getStockCode());
						serialItem.setRefenceObjectId(rfItem.getEbObjectId());
						serialItem.setOrigRefObjectId(rfItem.getEbObjectId());
						serializedItems.add(serialItem);
						serialItem = null;
					}
				}
			}
		}
		withdrawalSlip.setWithdrawalSlipItems(wsItems);
		wsDto.setWithdrawalSlip(withdrawalSlip);
		wsDto.setJyeiWithdrawalSlip(jyeiWS);
		wsDto.setSerialItems(serializedItems);
		return wsDto;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		super.doBeforeSaving(currentWorkflowLog, bindingResult);
		WithdrawalSlip ws = withdrawalSlipDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			List<SerialItem> wsSerialItems = serialItemService.getSerializeItemsByRefObjectId(ws.getEbObjectId(),
					ws.getWarehouseId(), JyeiWithdrawalSlip.WS_SERIAL_ITEM_OR_TYPE, false);
			serialItemService.setSerialNumberToInactive(wsSerialItems);
		}
	}

	private Warehouse getWarehouseByEbObjectId(Integer ebObjectId){
		Warehouse warehouse = null;
		EBObject wEBObject = objectToObjectDao.getOtherReference(ebObjectId, WithdrawalSlip.OR_TYPE_WAREHOUSE);
		if(wEBObject != null){
			warehouse = warehouseDao.getByEbObjectId(wEBObject.getId());
		}
		return warehouse;
	}

	/**
	 * Validate the serial items of withdrawal slip.
	 */
	public void validateWSSerialItems(WithdrawalSlipDto wsDto, Errors errors) throws CloneNotSupportedException {
		WithdrawalSlip withdrawalSlip = wsDto.getWithdrawalSlip();
		List<SerialItem> serialItems = wsDto.getSerialItems();
		if(!serialItems.isEmpty()) {
			serializedItemService.validateSerialItems("siMessage", "siMessage",
					wsDto.getWithdrawalSlip().getWithdrawalSlipItems().isEmpty(),
					false, false, serialItems, errors);
			if(withdrawalSlip.getRefenceObjectId() != null){
				Map<Integer, SerialItem> saleItemHM = new HashMap<>();
				Integer key = 0;
				SerialItem sItem = null;
				for (SerialItem serialItem : serialItems) {
					if(serialItem.getItemId() == null){
						continue;
					}
					key = serialItem.getItemId();
					if(saleItemHM.containsKey(key)){
						sItem = (SerialItem) saleItemHM.get(key).clone();
						sItem.setQuantity(sItem.getQuantity() + serialItem.getQuantity());
						saleItemHM.put(key, sItem);
					} else {
						saleItemHM.put(key, serialItem);
					}
				}
				if(withdrawalSlip.getId() == 0){
					SerialItem serialItem = new SerialItem();
					List<RequisitionFormItem> rfItems =
							requisitionFormService.getRfItems(withdrawalSlip.getRefenceObjectId());
					for (RequisitionFormItem rfItem : rfItems) {
						serialItem = saleItemHM.get(rfItem.getItemId());
						if(serialItem == null){
							continue;
						}
						double availableQty = rfItem.getQuantity()-jyeiWithdrawalSlipDao.getSIAvailableStockFromRf(
								withdrawalSlip.getId(), serialItem.getItemId(), withdrawalSlip.getRefenceObjectId());
						if(serialItem.getQuantity() > (availableQty)){
							errors.rejectValue("siMessage", null, null, String.format(
									ValidatorMessages.getString("WithdrawalSlipService.21"),
									itemDao.get(serialItem.getItemId()).getStockCode()));
						}
					}
				}
			}
		}
	}

	/**
	 * Get printout data of Withdrawal Slip
	 * @param wsDto the withdrawal slip dto
	 * @return the data of the withdrawal slip printout
	 */
	public List<JyeiWithdrawalSlipPrintoutDto> getWsPrintOutDto(WithdrawalSlipDto wsDto){
		JyeiWithdrawalSlipPrintoutDto printOutDto = new JyeiWithdrawalSlipPrintoutDto();
		List<WithdrawalSlipDto> wsDtos = new ArrayList<>();
		wsDto.setFleetPlateNo(getPlateNo(wsDto.getWithdrawalSlip().getFleetId()));
		wsDtos.add(wsDto);
		printOutDto.setJyeiWsPrintoutDto(wsDtos);
		List<JyeiWithdrawalSlipPrintoutDto> wsPrintOutDto = new ArrayList<>();
		wsPrintOutDto.add(printOutDto);
		return wsPrintOutDto;
	}

	/**
	 * Get the plate no of the fleet
	 * @param fleetId the fleet ID
	 * @return the plate No
	 */
	public String getPlateNo (Integer fleetId) {
		if (fleetId != null) {
			FleetProfile fleetProfile = fleetProfileService.getFleetProfile(fleetId);
			return fleetProfile.getPlateNo();
		}
		return "";
	}

	/**
	 * Get Withdrawal Slip by EB Object id
	 * @param ebObjectId the eb object id
	 * @param isComplete true if complete only, else false
	 * @return the list of withdrawal slip
	 */
	public List<WithdrawalSlip> getWsByEbObjectId(Integer ebObjectId, Boolean isComplete){
		return jyeiWithdrawalSlipDao.getWsByEbObjectId(ebObjectId, isComplete);
	}
}
