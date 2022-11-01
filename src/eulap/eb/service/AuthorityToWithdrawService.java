package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArLineSetupDao;
import eulap.eb.dao.AuthorityToWithdrawDao;
import eulap.eb.dao.AuthorityToWithdrawItemDao;
import eulap.eb.dao.AuthorityToWithdrawLineDao;
import eulap.eb.dao.DeliveryReceiptDao;
import eulap.eb.dao.DriverDao;
import eulap.eb.dao.FleetProfileDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.ItemDiscountTypeDao;
import eulap.eb.dao.ItemSrpDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.dao.SerialItemDao;
import eulap.eb.dao.SerialItemSetupDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.AuthorityToWithdraw;
import eulap.eb.domain.hibernate.AuthorityToWithdrawItem;
import eulap.eb.domain.hibernate.AuthorityToWithdrawLine;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.Driver;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.SalesOrderItem;
import eulap.eb.domain.hibernate.SalesOrderLine;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.SerialItemSetup;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.AtwItemDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;
import eulap.eb.web.dto.SoToAtwDto;

/**
 * A service class that will handle business logic for {@link AuthorityToWithdraw}

 */

@Service
public class AuthorityToWithdrawService extends BaseWorkflowService {
	@Autowired
	private AuthorityToWithdrawDao atwDao;
	@Autowired
	private AuthorityToWithdrawItemDao atwItemDao;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private SalesOrderDao salesOrderDao;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private SerialItemSetupDao siSetupDao;
	@Autowired
	private SalesOrderService salesOrderService;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private SerialItemDao serialItemDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private ItemSrpDao itemSrpDao;
	@Autowired
	private DeliveryReceiptDao drDao;
	@Autowired
	private DriverDao driverDao;
	@Autowired
	private FleetProfileDao fleetProfileDao;
	@Autowired
	private AuthorityToWithdrawLineDao atwLineDao;
	@Autowired
	private ArLineSetupDao arLineSetupDao;
	@Autowired
	private UnitMeasurementDao uomDao;
	@Autowired
	private ItemDiscountTypeDao itemDiscountTypeDao;

	/**
	 * Get the {@code AuthorityToWithdraw} form object
	 * @param pId The object id
	 * @return The form object
	 */
	public AuthorityToWithdraw getAuthorityToWithdraw(int pId) {
		AuthorityToWithdraw authorityToWithdraw = atwDao.get(pId);
		SalesOrder soReference = salesOrderService.getSalesOrder(authorityToWithdraw.getSalesOrderId(), false);
		authorityToWithdraw.setSoNumber(soReference.getSequenceNumber().toString());
		List<AuthorityToWithdrawItem> atwItems = atwItemDao.getAllByRefId(
				AuthorityToWithdrawItem.FIELD.authorityToWithdrawId.name(), pId);
		Date currentDate = new Date();
		EBObject ebObject = null;
		for (AuthorityToWithdrawItem atwi : atwItems) {
			atwi.setOrigWarehouseId(atwi.getWarehouseId());
			atwi.setStockCode(itemService.getItem(atwi.getItemId()).getStockCode());
			atwi.setExistingStocks(itemService.getItemExistingStocks(atwi.getItemId(),
					atwi.getWarehouseId(), currentDate));
			ebObject = ooLinkHelper.getReferenceObject(atwi.getEbObjectId(),
					AuthorityToWithdraw.ATW_SO_ITEM_OR_TYPE_ID);
			if (ebObject != null) {
				atwi.setRefenceObjectId(ebObject.getId());
				ebObject = null;
			}
		}
		boolean isCancelled = authorityToWithdraw.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;
		List<SerialItem> serialItems = serialItemService.getSerializeItemsByRefObjectId(authorityToWithdraw.getEbObjectId(),
				null, AuthorityToWithdraw.ATW_SERIAL_ITEM_OR_TYPE_ID, isCancelled);
		authorityToWithdraw.setAtwItems(atwItems);
		authorityToWithdraw.setSerialItems(serialItems);
		authorityToWithdraw.setReferenceDocuments(refDocumentService.getReferenceDocuments(authorityToWithdraw.getEbObjectId()));
		if(authorityToWithdraw.getDriverId() != null) {
			Driver driver = driverDao.get(authorityToWithdraw.getDriverId());
			authorityToWithdraw.setDriver(driver);
			authorityToWithdraw.setDriverName(processDriverName(driver));
			driver = null;//Clear memory.
		}
		if(authorityToWithdraw.getFleetProfileId() != null) {
			authorityToWithdraw.setFleetProfile(fleetProfileDao.get(authorityToWithdraw.getFleetProfileId()));
		}

		UnitMeasurement uom = null;
		ArLineSetup arLineSetup = null;
		List<AuthorityToWithdrawLine> atwLines = atwLineDao.getAllByRefId(
				AuthorityToWithdrawLine.FIELD.authorityToWithdrawId.name(), pId);
		for (AuthorityToWithdrawLine atwLine : atwLines) {
			arLineSetup = arLineSetupDao.get(atwLine.getArLineSetupId());
			if (arLineSetup != null) {
				atwLine.setArLineSetupName(arLineSetup.getName());
			}
			if (atwLine.getUnitOfMeasurementId() != null) {
				uom = uomDao.get(atwLine.getUnitOfMeasurementId());
				atwLine.setUnitMeasurementName(uom.getName());
			}
			Integer itemDiscountTypeId = atwLine.getDiscountTypeId();
			if (itemDiscountTypeId != null) {
				atwLine.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
				itemDiscountTypeId = null;
			}
		}
		authorityToWithdraw.setAtwLines(atwLines);
		return authorityToWithdraw;
	}

	private String processDriverName(Driver driver) {
		return driver.getLastName() + ", " + driver.getFirstName()+ " " 
				+ (driver.getMiddleName() != null ? driver.getMiddleName() : "");
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		AuthorityToWithdraw authorityToWithdraw = (AuthorityToWithdraw) form;
		boolean isNew = authorityToWithdraw.getId() == 0;
		AuditUtil.addAudit(authorityToWithdraw, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			authorityToWithdraw.setSequenceNumber(atwDao.generateSequenceNo(authorityToWithdraw.getCompanyId()));
		} else {
			List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(
					authorityToWithdraw.getEbObjectId(), AuthorityToWithdraw.ATW_SERIAL_ITEM_OR_TYPE_ID);
			serialItemService.setSerialNumberToInactive(savedSerialItems);
		}
		String remarks = authorityToWithdraw.getRemarks();
		if (remarks != null && !remarks.isEmpty()) {
			authorityToWithdraw.setRemarks(StringFormatUtil.removeExtraWhiteSpaces(remarks));
		}
		atwDao.saveOrUpdate(authorityToWithdraw);

		List<AuthorityToWithdrawItem> atwItems = authorityToWithdraw.getAtwItems();
		for (AuthorityToWithdrawItem atwi : atwItems) {
			atwi.setAuthorityToWithdrawId(authorityToWithdraw.getId());
			atwItemDao.saveOrUpdate(atwi);
		}

		serialItemService.saveSerializedItems(authorityToWithdraw.getSerialItems(), authorityToWithdraw.getEbObjectId(),
				null, user, AuthorityToWithdraw.ATW_SERIAL_ITEM_OR_TYPE_ID, false);

		List<AuthorityToWithdrawLine> atwLines = authorityToWithdraw.getAtwLines();
		for(AuthorityToWithdrawLine atwLine : atwLines) {
			atwLine.setAuthorityToWithdrawId(authorityToWithdraw.getId());
			atwLineDao.saveOrUpdate(atwLine);
		}
		saveRefDocuments(authorityToWithdraw);
	}

	private void saveRefDocuments(AuthorityToWithdraw authorityToWithdraw) {
		// Delete the saved referenced documents.
		List<ReferenceDocument> toBeDeleteRefDocs = refDocumentService.getReferenceDocuments(authorityToWithdraw.getEbObjectId());
		for (ReferenceDocument referenceDocument : toBeDeleteRefDocs) {
			referenceDocumentDao.delete(referenceDocument);
		}

		// Saving Reference documents
		List<ReferenceDocument> refDocuments = authorityToWithdraw.getReferenceDocuments();
		if (refDocuments != null && !refDocuments.isEmpty()) {
			for (ReferenceDocument referenceDocument : refDocuments) {
				referenceDocumentDao.save(referenceDocument);
			}
		}
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return atwDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return atwDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		AuthorityToWithdraw authorityToWithdraw = atwDao.getByEbObjectId(ebObjectId);
		int pId = authorityToWithdraw.getId();
		FormProperty property = workflowHandler.getProperty(authorityToWithdraw.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = authorityToWithdraw.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Authority To Withdraw - " + authorityToWithdraw.getSequenceNumber();
		shortDescription = new StringBuffer(title + " " + authorityToWithdraw.getArCustomer().getName());
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus, shortDescription.toString(),
				fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case AuthorityToWithdraw.OBJECT_TYPE_ID:
				return atwDao.getByEbObjectId(ebObjectId);
			case AuthorityToWithdrawItem.OBJECT_TYPE_ID:
				return atwItemDao.getByEbObjectId(ebObjectId);
			case SerialItem.OBJECT_TYPE_ID:
				return serialItemDao.getByEbObjectId(ebObjectId);
			case ReferenceDocument.OBJECT_TYPE_ID:
				return atwItemDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the paged list of sales order for ATW reference
	 * @param companyId The company id
	 * @param soNumber The sales order sequence number
	 * @param arCustomerId The AR customer id
	 * @param arCustomerAcctId The AR customer account id id
	 * @param statusId The reference form status id (ALL, USED, and UNUSED)
	 * @param pageSetting The page setting
	 * @return The paged list of sales order for ATW reference
	 */
	public Page<SalesOrder> getSaleOrderForms(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, Integer pageNumber) {
		Page<SalesOrder> salesOrders = salesOrderDao.getSaleOrderReferences(companyId, soNumber, arCustomerId, arCustomerAcctId,
				statusId, new PageSetting(pageNumber));
		for (SalesOrder so : salesOrders.getData()) {
			so.setArCustomer(arCustomerService.getCustomer(so.getArCustomerId()));
			so.setArCustomerAccount(arCustomerAcctService.getAccount(so.getArCustomerAcctId()));
		}
		return salesOrders;
	}

	/**
	 * Convert reference sales order form for ATW
	 * @param salesOrderId The sales order id
	 * @return The ATW form object
	 */
	public SoToAtwDto convertSOtoATW(Integer salesOrderId) {
		SoToAtwDto soToAtwDto = new SoToAtwDto();
		List<AuthorityToWithdrawItem> atwItems = new ArrayList<AuthorityToWithdrawItem>();
		List<SerialItem> serialItems = new ArrayList<SerialItem>();
		SalesOrder salesOrder = salesOrderService.getSalesOrder(salesOrderId, true);
		Integer companyId = salesOrder.getCompanyId();
		Integer divisionId = salesOrder.getDivisionId();
		soToAtwDto.setSalesOrder(salesOrder);
		SerialItemSetup siSetup = null;
		SerialItem serialItem = null;
		Item item = null;
		ItemSrp itemSrp = null;
		AuthorityToWithdrawItem atwItem = null;
		for (SalesOrderItem soi : salesOrder.getSoItems()) {
			item = itemService.getItem(soi.getItemId());
			siSetup = siSetupDao.getSerialItemSetupByItemId(soi.getItemId());
			itemSrp = itemSrpDao.getLatestItemSrp(companyId, soi.getItemId(), divisionId);
			if (siSetup != null && siSetup.isSerializedItem()) {
				double qtyDiscount = (soi.getDiscount() == null ? 0 : soi.getDiscount()) / soi.getQuantity();
				double vat = soi.getVatAmount() != null ? soi.getVatAmount() : 0;
				double qtyVat = vat / soi.getQuantity(); 
				Double qty = atwDao.getRemainingSiQty(salesOrder.getId(), soi.getItemId());
				for (int i = 0; i < qty; i++) {
					serialItem = new SerialItem();
					serialItem.setItemId(soi.getItemId());
					serialItem.setItem(item);
					serialItem.setStockCode(item.getStockCode());
					serialItem.setQuantity(1.0);
					serialItem.setItemSrpId(itemSrp.getId());
					serialItem.setSrp(soi.getGrossAmount());
					serialItem.setDiscount(qtyDiscount);
					serialItem.setTaxTypeId(soi.getTaxTypeId());
					serialItem.setVatAmount(qtyVat);
					serialItem.setRefenceObjectId(soi.getEbObjectId());
					serialItem.setItemDiscountTypeId(soi.getItemDiscountTypeId());
					serialItem.setDiscountValue(soi.getDiscountValue());
					serialItem.setActive(true);
					serialItems.add(serialItem);
					serialItem = null;
				}
			} else {
				Double qty = atwDao.getRemainingAtwItemQty(salesOrder.getId(), soi.getItemId());
				if(qty > 0) {
					atwItem = new AuthorityToWithdrawItem();
					atwItem.setItem(item);
					atwItem.setItemId(soi.getItemId());
					atwItem.setStockCode(item.getStockCode());
					atwItem.setQuantity(qty);
					atwItem.setRefenceObjectId(soi.getEbObjectId());
					atwItems.add(atwItem);
					atwItem = null;
				}
			}
			item = null;
			siSetup = null;
			itemSrp = null;
		}
		List<AuthorityToWithdrawLine> atwLines = new ArrayList<AuthorityToWithdrawLine>();
		AuthorityToWithdrawLine atwLine = null;
		for(SalesOrderLine sol : salesOrder.getSoLines()) {
			atwLine = new AuthorityToWithdrawLine();
			atwLine.setRefenceObjectId(sol.getEbObjectId());
//			atwLine.setArLineSetupId(sol.getArLineSetupId());
//			atwLine.setArLineSetupName(sol.getArLineSetup().getName());
			atwLine.setQuantity(sol.getQuantity());
			atwLine.setUpAmount(sol.getUpAmount());
			atwLine.setDiscountTypeId(sol.getDiscountTypeId());
			atwLine.setDiscountValue(sol.getDiscountValue());
			atwLine.setDiscount(sol.getDiscount());
			atwLine.setTaxTypeId(sol.getTaxTypeId());
			atwLine.setVatAmount(sol.getVatAmount());
			Integer uomId = sol.getUnitOfMeasurementId();
			if (uomId != null) {
				atwLine.setUnitOfMeasurementId(uomId);
				atwLine.setUnitMeasurementName(sol.getUnitMeasurement().getName());
			}
			atwLine.setAmount(sol.getAmount());
			atwLines.add(atwLine);
			atwLine = null;
		}
		soToAtwDto.setAtwLines(atwLines);
		soToAtwDto.setSerialItems(serialItems);
		soToAtwDto.setAtwItems(atwItems);
		serialItems = null;
		atwItems = null;
		return soToAtwDto;
	}

	/**
	 * Retrieve the page list of {@code AuthorityToWithdraw} for general search
	 * @param searchCriteria The search criteria
	 * @return The page list of ATW for general search
	 */
	public List<FormSearchResult> retrieveForms(String searchCriteria) {
		Page<AuthorityToWithdraw> atws = atwDao.retrieveAuthorityToWithdraws(searchCriteria, new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		ArCustomer arCustomer = null;
		for (AuthorityToWithdraw atw : atws.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String title = atw.getCompany().getCompanyCode() + " " + atw.getSequenceNumber();
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(atw.getDate())));
			arCustomer = arCustomerService.getCustomer(atw.getArCustomerId());
			properties.add(ResultProperty.getInstance("Customer", arCustomer.getName()));
			String status = formStatusDao.get(atw.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(atw.getId(), title, properties));
		}
		return result;
	}

	/**
	 * Validate ATW form
	 * @param authorityToWithdraw The ATW object
	 * @param errors The validation errors
	 */
	public void validateForm(AuthorityToWithdraw authorityToWithdraw, Errors errors) {
		ValidatorUtil.validateCompany(authorityToWithdraw.getCompanyId(), companyService, errors, "companyId");

		Date date = authorityToWithdraw.getDate();
		if (date == null) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.1"));
		} else if (!timePeriodService.isOpenDate(date)) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.2"));
		}

		if (authorityToWithdraw.getSalesOrderId() == null) {
			errors.rejectValue("salesOrderId", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.3"));
		}

		Integer arCustomerId = authorityToWithdraw.getArCustomerId();
		if (arCustomerId == null) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.4"));
		} else if (!arCustomerService.getCustomer(arCustomerId).isActive()) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.5"));
		}

		Integer arCustomerAcctId = authorityToWithdraw.getArCustomerAcctId();
		if (arCustomerAcctId == null) {
			errors.rejectValue("arCustomerAcctId", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.6"));
		} else if (!arCustomerAcctService.getAccount(arCustomerAcctId).isActive()) {
			errors.rejectValue("arCustomerAcctId", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.7"));
		}

		if(authorityToWithdraw.getFleetProfileId() == null 
				&& !authorityToWithdraw.getFleetProfile().getPlateNo().trim().isEmpty()) {
			errors.rejectValue("fleetProfileId", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.15"));
		}

		if(authorityToWithdraw.getDriverId() == null && !authorityToWithdraw.getDriverName().trim().isEmpty()) {
			errors.rejectValue("driverName", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.16"));
		}

		if(authorityToWithdraw.getDriverId() != null && !driverDao.get(authorityToWithdraw.getDriverId()).isActive()) {
			errors.rejectValue("driverName", null, null, ValidatorMessages.getString("AuthorityToWithdrawService.17"));
		}

		List<AuthorityToWithdrawItem> atwItems = authorityToWithdraw.getAtwItems();
		List<SerialItem> serialItems = authorityToWithdraw.getSerialItems();
		if (serialItems != null && !serialItems.isEmpty()) {
			serialItemService.validateSerialItems("atwItems", "serialItems", atwItems.isEmpty(),
					false, false, serialItems, errors);
		}

		if (atwItems != null && !atwItems.isEmpty()) {
			int row = 0;
			for (AuthorityToWithdrawItem atwi : atwItems) {
				row++;
				Integer itemId = atwi.getItemId();
				if (itemId == null) {
					errors.rejectValue("atwItems", null, null, 
							String.format(ValidatorMessages.getString("AuthorityToWithdrawService.9"), row));
				} else if (!itemService.getItem(itemId).isActive()) {
					errors.rejectValue("atwItems", null, null,
							String.format(ValidatorMessages.getString("AuthorityToWithdrawService.10"),
									atwi.getStockCode(), row));
				}
				Double quantity = atwi.getQuantity();
				double existingStocks = 0;
				Integer warehouseId = atwi.getWarehouseId();
				String warehouseName = null;
				if (warehouseId != null && date != null) {
					existingStocks = NumberFormatUtil.roundOffTo2DecPlaces(itemService.getItemExistingStocks(itemId, warehouseId, date));
					warehouseName = warehouseService.getWarehouse(warehouseId).getName();
				}
				if (quantity == null) {
					errors.rejectValue("atwItems", null, null, 
							String.format(ValidatorMessages.getString("AuthorityToWithdrawService.11"), row));
				} else if (quantity <= 0) {
					errors.rejectValue("atwItems", null, null, 
							String.format(ValidatorMessages.getString("AuthorityToWithdrawService.12"), row));
				} else if (existingStocks < quantity && date != null) {
					errors.rejectValue("atwItems", null, null, 
							String.format(ValidatorMessages.getString("AuthorityToWithdrawService.13"), warehouseName,
									DateUtil.formatDate(date), existingStocks, row));
				} else if(quantity > getRemainingQty(atwi.getId(), atwi.getRefenceObjectId())) {
					errors.rejectValue("atwItems", null, null, 
							String.format(ValidatorMessages.getString("AuthorityToWithdrawService.14"),
									itemService.getItem(atwi.getItemId()).getStockCode(), row));
				}
			}
		}
	}

	/**
	 * Get {@code AuthorityToWithdrawItem} object by item reference object id
	 * @param refItemObjectId The item reference object id
	 * @return The authority to withdraw item object
	 */
	public AtwItemDto getAtwItemByRefItemObjectId(Integer refItemObjectId) {
		return atwItemDao.getAtwItemByRefItemObjectId(refItemObjectId);
	}

	private double getRemainingQty(Integer authorityToWithdrawItemId, Integer referenceObjectId) {
		return atwItemDao.getRemainingQty(authorityToWithdrawItemId, referenceObjectId);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			AuthorityToWithdraw atw = atwDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			StringBuffer errorMessage = null;
			if (atw != null) {
				Integer atwId = atw.getId();
				if (atwDao.isUsedByDR(atwId)) {
					List<DeliveryReceipt> drs = drDao.getDRsByATWId(atwId);
					if (!drs.isEmpty()) {
						errorMessage = new StringBuffer("Authority to Withdraw form has been used by the ff forms:");
						for( DeliveryReceipt dr : drs) {
							errorMessage.append("<br>DR-"+dr.getSequenceNo());
						}
					}
				}
				if (errorMessage != null) {
					bindingResult.reject("workflowMessage", errorMessage.toString());
					currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
				} else {
					List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(
							atw.getEbObjectId(), AuthorityToWithdraw.ATW_SERIAL_ITEM_OR_TYPE_ID);
					serialItemService.setSerialNumberToInactive(savedSerialItems);
				}
			}
		}
	}
}
