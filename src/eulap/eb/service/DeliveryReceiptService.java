package eulap.eb.service;

import java.io.IOException;
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
import eulap.eb.common.util.ListProcessorUtil;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.dao.ArLineSetupDao;
import eulap.eb.dao.AuthorityToWithdrawDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DeliveryReceiptDao;
import eulap.eb.dao.DeliveryReceiptItemDao;
import eulap.eb.dao.DeliveryReceiptLineDao;
import eulap.eb.dao.DeliveryReceiptTypeDao;
import eulap.eb.dao.EquipmentUtilizationLineDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ItemDiscountTypeDao;
import eulap.eb.dao.ItemSrpDao;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.dao.SerialItemDao;
import eulap.eb.dao.SerialItemSetupDao;
import eulap.eb.dao.ServiceSettingDao;
import eulap.eb.dao.TermDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.dao.WaybillLineDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.AuthorityToWithdraw;
import eulap.eb.domain.hibernate.AuthorityToWithdrawItem;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.DeliveryReceiptItem;
import eulap.eb.domain.hibernate.DeliveryReceiptLine;
import eulap.eb.domain.hibernate.DeliveryReceiptType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.EquipmentUtilizationLine;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.SalesOrderItem;
import eulap.eb.domain.hibernate.SalesOrderLine;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.SerialItemSetup;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WaybillLine;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.FormUnitCosthandler;
import eulap.eb.service.inventory.RItemCostUpdateService;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.AtwItemDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ItemTransaction;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service class for {@link DeliveryReceipt}

 */

@Service
public class DeliveryReceiptService extends BaseWorkflowService implements FormUnitCosthandler {
	private static Logger logger = Logger.getLogger(DeliveryReceiptService.class);
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DeliveryReceiptDao deliveryReceiptDao;
	@Autowired
	private DeliveryReceiptItemDao deliveryReceiptItemDao;
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private ArCustomerAcctDao arCustomerAcctDao;
	@Autowired
	private TermDao termDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private SerialItemDao serialItemDao;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private SerialItemSetupDao siSetupDao;
	@Autowired
	private ItemSrpDao itemSrpDao;
	@Autowired
	private AuthorityToWithdrawDao atwDao;
	@Autowired
	private AuthorityToWithdrawService atwService;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private SalesOrderService salesOrderService;
	@Autowired
	private DeliveryReceiptLineDao drLineDao;
	@Autowired
	private ArLineSetupDao arLineSetupDao;
	@Autowired
	private UnitMeasurementDao uomDao;
	@Autowired
	private ItemDiscountTypeDao itemDiscountTypeDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private SalesOrderDao salesOrderDao;
	@Autowired
	private WaybillLineDao wbLineDao;
	@Autowired
	private EquipmentUtilizationLineDao utilizationLineDao;
	@Autowired
	private DeliveryReceiptTypeDao drTypeDao;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private SalesPersonnelService salesPersonnelService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private ServiceSettingDao serviceSettingDao;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ArInvoiceDao arInvoiceDao;

	/**
	 * Get delivery receipt by id
	 * @param deliveryReceiptId The delivery receipt id
	 * @return The delivery receipt object
	 */
	public DeliveryReceipt getDeliveryReceipt(Integer deliveryReceiptId, Integer typeId) {
		return deliveryReceiptDao.getDrByTypeId(deliveryReceiptId, typeId);
	}

	/**
	 * Get the delivery receipt for edit, view and printout
	 * @param deliveryReceiptId The form object id
	 * @param isPopulateHeader rue of populate object header details, otherwise false
	 * @param isIncludeItems True of populate item/line details, otherwise false
	 * @return The he delivery receipt object for edit, view and printout
	 */
	public DeliveryReceipt getDeliveryReceipt(int deliveryReceiptId, boolean isPopulateHeader, boolean isItemAndLine, Integer typeId) {
		DeliveryReceipt deliveryReceipt = getDeliveryReceipt(deliveryReceiptId, typeId);
		int drTypeId = deliveryReceipt.getDeliveryReceiptTypeId();
		int refObjectId = deliveryReceipt.getEbObjectId();
		if (isPopulateHeader) {
			deliveryReceipt.setCompany(companyDao.get(deliveryReceipt.getCompanyId()));
			deliveryReceipt.setArCustomer(arCustomerDao.get(deliveryReceipt.getArCustomerId()));
			deliveryReceipt.setArCustomerAccount(arCustomerAcctDao.get(deliveryReceipt.getArCustomerAccountId()));
			deliveryReceipt.setTerm(termDao.get(deliveryReceipt.getTermId()));
			Integer soNumber = salesOrderDao.get(deliveryReceipt.getSalesOrderId()).getSequenceNumber();
			deliveryReceipt.setAtwNumber(soNumber.toString());
			deliveryReceipt.setSoNumber(soNumber.toString());
			if(deliveryReceipt.getSalesPersonnelId() != null) {
				deliveryReceipt.setSalesPersonnelName(
						salesPersonnelService.getSalesPersonnel(deliveryReceipt.getSalesPersonnelId()).getName());
			}
		}
		if (isItemAndLine) {
			UnitMeasurement uom = null;
			ArLineSetup arLineSetup = null;
			if (drTypeId == DeliveryReceiptType.DR_TYPE_ID) {
				// Populate non-serial items
				deliveryReceipt.setNonSerialDrItems(getDrItems(deliveryReceiptId));
				// Populate serial items
				boolean isCancelled = deliveryReceipt.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;
				List<SerialItem> serialItems = serialItemService.getSerializeItemsByRefObjectId(refObjectId,
						null, DeliveryReceipt.DR_SI_OR_TYPE_ID, isCancelled);
				deliveryReceipt.setSerialDrItems(serialItems);
			} else if (drTypeId == DeliveryReceiptType.WAYBILL_DR_TYPE_ID) {
				List<WaybillLine> wbLines = wbLineDao.getAllByRefId(DeliveryReceiptItem.FIELD.deliveryReceiptId.name(),
						deliveryReceiptId);
				for (WaybillLine wbLine : wbLines) {
					arLineSetup = arLineSetupDao.get(wbLine.getArLineSetupId());
					if (arLineSetup != null) {
						wbLine.setArLineSetupName(arLineSetup.getName());
					}
					if (wbLine.getUnitOfMeasurementId() != null) {
						uom = uomDao.get(wbLine.getUnitOfMeasurementId());
						wbLine.setUnitMeasurementName(uom.getName());
					}
					Integer itemDiscountTypeId = wbLine.getDiscountTypeId();
					if (itemDiscountTypeId != null) {
						wbLine.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
						itemDiscountTypeId = null;
					}
				}
				deliveryReceipt.setWbLines(wbLines);
			} else if (drTypeId == DeliveryReceiptType.EQ_UTIL_DR_TYPE_ID) {
				List<EquipmentUtilizationLine> utilizationLines = utilizationLineDao.getAllByRefId(DeliveryReceiptItem.FIELD.deliveryReceiptId.name(),
						deliveryReceiptId);
				for (EquipmentUtilizationLine euLine : utilizationLines) {
					arLineSetup = arLineSetupDao.get(euLine.getArLineSetupId());
					if (arLineSetup != null) {
						euLine.setArLineSetupName(arLineSetup.getName());
					}
					if (euLine.getUnitOfMeasurementId() != null) {
						uom = uomDao.get(euLine.getUnitOfMeasurementId());
						euLine.setUnitMeasurementName(uom.getName());
					}
					Integer itemDiscountTypeId = euLine.getDiscountTypeId();
					if (itemDiscountTypeId != null) {
						euLine.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
						itemDiscountTypeId = null;
					}
				}
				deliveryReceipt.setEuLines(utilizationLines);
			} else if (drTypeId == DeliveryReceiptType.DR_SERVICE_TYPE_ID) {
				deliveryReceipt.setDrLines(getDrLines(deliveryReceiptId));
			} else if(drTypeId >= DeliveryReceiptType.DR_CENTRAL_TYPE_ID) {
				//Dr Items
				deliveryReceipt.setNonSerialDrItems(getDrItems(deliveryReceiptId));
				// Populate serial items
				boolean isCancelled = deliveryReceipt.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;
				List<SerialItem> serialItems = serialItemService.getSerializeItemsByRefObjectId(refObjectId,
						null, DeliveryReceipt.DR_SI_OR_TYPE_ID, isCancelled);
				deliveryReceipt.setSerialDrItems(serialItems);
				//DR Lines
				deliveryReceipt.setDrLines(getDrLines(deliveryReceiptId));
			}
		}
		
		return deliveryReceipt;
	}

	public void processRefDoc(DeliveryReceipt deliveryReceipt) throws IOException {
		deliveryReceipt.setReferenceDocuments(refDocumentService.processReferenceDocs(deliveryReceipt.getEbObjectId()));
	}

	private List<DeliveryReceiptItem> getDrItems(Integer deliveryReceiptId) {
		SaleItemUtil<DeliveryReceiptItem> saleItemUtil = new SaleItemUtil<DeliveryReceiptItem>();
		List<DeliveryReceiptItem> drItems = saleItemUtil.getSummarisedSaleItems(deliveryReceiptItemDao.getAllByRefId(
				DeliveryReceiptItem.FIELD.deliveryReceiptId.name(), deliveryReceiptId));
		EBObject ebObject = null;
		Date currentDate = new Date();
		for (DeliveryReceiptItem dri : drItems) {
			dri.setOrigWarehouseId(dri.getWarehouseId());
			dri.setOrigQty(dri.getQuantity());
			dri.setStockCode(itemService.getItem(dri.getItemId()).getStockCode());
			dri.setExistingStocks(itemService.getItemExistingStocks(dri.getItemId(),
					dri.getWarehouseId(), currentDate));
			ebObject = ooLinkHelper.getReferenceObject(dri.getEbObjectId(),
					DeliveryReceipt.ATWI_DRI_OR_TYPE_ID);
			if (ebObject != null) {
				dri.setRefenceObjectId(ebObject.getId());
				ebObject = null;
			}
		}
		return drItems;
	}

	private List<DeliveryReceiptLine> getDrLines(Integer deliveryReceiptId) {
		List<DeliveryReceiptLine> drLines = drLineDao.getAllByRefId(
				DeliveryReceiptLine.FIELD.deliveryReceiptId.name(), deliveryReceiptId);
		UnitMeasurement uom = null;
		EBObject ebObject = null;
		for (DeliveryReceiptLine drl : drLines) {
			ServiceSetting serviceSetting = serviceSettingDao.get(drl.getServiceSettingId());
			if (serviceSetting != null) {
				drl.setServiceSettingName(serviceSetting.getName());
			}
			if (drl.getUnitOfMeasurementId() != null) {
				uom = uomDao.get(drl.getUnitOfMeasurementId());
				drl.setUnitMeasurementName(uom.getName());
			}
			Integer itemDiscountTypeId = drl.getDiscountTypeId();
			if (itemDiscountTypeId != null) {
				drl.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
				itemDiscountTypeId = null;
			}
			ebObject = ooLinkHelper.getReferenceObject(drl.getEbObjectId(),
					DeliveryReceipt.ATWI_DRI_OR_TYPE_ID);
			if (ebObject != null) {
				drl.setRefenceObjectId(ebObject.getId());
				ebObject = null;
			}
		}
		return drLines;
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		DeliveryReceipt dr = (DeliveryReceipt) form;
		boolean isNew = dr.getId() == 0;
		if (isNew) {
			dr.setSequenceNo(deliveryReceiptDao.generateSeqNo(dr.getCompanyId(), dr.getDeliveryReceiptTypeId()));
		} else {
			List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(
					dr.getEbObjectId(), DeliveryReceipt.DR_SI_OR_TYPE_ID);
			serialItemService.setSerialNumberToInactive(savedSerialItems);
			//Delete saved delivery receipt items.
			List<DeliveryReceiptItem> savedDrItems = deliveryReceiptItemDao.getAllByRefId(
					DeliveryReceiptItem.FIELD.deliveryReceiptId.name(), dr.getId());
			List<Integer> toBeDeletedIds = new ArrayList<Integer>();
			for (DeliveryReceiptItem drItem : savedDrItems) {
				toBeDeletedIds.add(drItem.getId());
			}
			deliveryReceiptItemDao.delete(toBeDeletedIds);
			//Delete saved delivery receipt lines
			List<DeliveryReceiptLine> savedDrLines = drLineDao.getAllByRefId(
					DeliveryReceiptLine.FIELD.deliveryReceiptId.name(), dr.getId());
			for (DeliveryReceiptLine drLine : savedDrLines) {
				drLineDao.delete(drLine);
			}
			savedSerialItems = null;
			savedDrItems = null;
			savedDrLines = null;
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		DeliveryReceipt dr = (DeliveryReceipt) form;
		boolean isNew = dr.getId() == 0;
		AuditUtil.addAudit(dr, new Audit(user.getId(), isNew, new Date()));
		String remarks = dr.getRemarks();
		if (remarks != null && !remarks.isEmpty()) {
			dr.setRemarks(remarks);
		}
		Integer parentObjectId = dr.getEbObjectId();
		deliveryReceiptDao.saveOrUpdate(dr);

		List<DeliveryReceiptItem> nonSerialDrItems = dr.getNonSerialDrItems();
		if (nonSerialDrItems != null) {
			SaleItemUtil<DeliveryReceiptItem> saleItemUtil = new SaleItemUtil<DeliveryReceiptItem>();
			Map<AllocatorKey, WeightedAveItemAllocator<DeliveryReceiptItem>> itemId2CostAllocator =
					new HashMap<AllocatorKey, WeightedAveItemAllocator<DeliveryReceiptItem>>();
			AllocatorKey key = null;
			List<Domain> toBeSavedDris = new ArrayList<Domain>();
			List<DeliveryReceiptItem> allocatedDrItems = null;
			for (DeliveryReceiptItem dri : nonSerialDrItems) {
				if (dri.getItemId() != null) {
					WeightedAveItemAllocator<DeliveryReceiptItem> itemAllocator = itemId2CostAllocator.get(dri.getItemId());
					if (itemAllocator == null) {
						itemAllocator = new WeightedAveItemAllocator<DeliveryReceiptItem>(itemDao, itemService,
								dri.getItemId(), dri.getWarehouseId(), dr.getDate());
						key = AllocatorKey.getInstanceOf(dri.getItemId(), dri.getWarehouseId());
						itemId2CostAllocator.put(key, itemAllocator);
					}
					allocatedDrItems = new ArrayList<DeliveryReceiptItem>();
					try {
						allocatedDrItems = itemAllocator.allocateCost(dri);
						allocatedDrItems = saleItemUtil.processDiscountAndAmount(allocatedDrItems, itemDiscountService);
						for (DeliveryReceiptItem pdri : allocatedDrItems) {
							pdri.setDeliveryReceiptId(dr.getId());
							toBeSavedDris.add(pdri);
						}
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
			deliveryReceiptItemDao.batchSave(toBeSavedDris);
		}

		if (dr.getSerialDrItems() != null) {
			serialItemService.saveSerializedItems(dr.getSerialDrItems(), dr.getEbObjectId(),
					null, user, DeliveryReceipt.DR_SI_OR_TYPE_ID, false);
		}

		List<Domain> toBeSavedDrLines = new ArrayList<Domain>();
		List<DeliveryReceiptLine> drLines = dr.getDrLines();
		if (drLines != null) {
			for (DeliveryReceiptLine drl : drLines) {
				drl.setDeliveryReceiptId(dr.getId());
				String description = drl.getDescription();
				if (description != null && !description.isEmpty()) {
					drl.setDescription(StringFormatUtil.removeExtraWhiteSpaces(description));
				}
				toBeSavedDrLines.add(drl);
			}
			drLineDao.batchSave(toBeSavedDrLines);
		}

		//Save reference documents
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				dr.getReferenceDocuments(), true);
	}

	/**
	 * Validate the delivery receipt object before saving.
	 * @param dr The delivery receipt object to be validated.
	 * @param errors The binding result.
	 */
	public void validate(DeliveryReceipt dr, Errors errors) {
		if (dr.getCompanyId() == null) {
			//Company is Required
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("DeliveryReceiptService.1"));
		} else if (!companyDao.get(dr.getCompanyId()).isActive()) {
			//Company is inactive.
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("DeliveryReceiptService.2"));
		}
		if(dr.getDivisionId() == null ) {
			//Division is required.
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("DeliveryReceiptService.20"));
		} else if(!divisionService.getDivision(dr.getDivisionId()).isActive()) {
			//Division is inactive.
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("DeliveryReceiptService.21"));
		}
		if (dr.getArCustomerId() == null) {
			//Customer is required.
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("DeliveryReceiptService.3"));
		} else if (!arCustomerDao.get(dr.getArCustomerId()).isActive()) {
			//Customer is inactive.
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("DeliveryReceiptService.4"));
		}
		if (dr.getArCustomerAccountId() == null) {
			//Customer account is required.
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("DeliveryReceiptService.5"));
		} else if (!arCustomerAcctDao.get(dr.getArCustomerAccountId()).isActive()) {
			//Customer account is inactive.
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("DeliveryReceiptService.6"));
		}
		if (dr.getTermId() == null) {
			//Term is requried.
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("DeliveryReceiptService.7"));
		} else if (!termDao.get(dr.getTermId()).isActive()) {
			//Term is inactive.
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("DeliveryReceiptService.8"));
		}

		Date date = dr.getDate();
		if (date == null) {
			//Date is required.
			errors.rejectValue("date", null, null, ValidatorMessages.getString("DeliveryReceiptService.9"));
		} else if (!timePeriodService.isOpenDate(date)) {
			//Date should be in an open time period.
			errors.rejectValue("date", null, null, ValidatorMessages.getString("DeliveryReceiptService.10"));
		}

		if (dr.getDueDate() == null) {
			//Due date is required.
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("DeliveryReceiptService.11"));
		}

		Integer salesPersonnelId = dr.getSalesPersonnelId();
		if(salesPersonnelId == null || salesPersonnelId.equals(0.00) ) {
			errors.rejectValue("salesPersonnelId", null, null, ValidatorMessages.getString("DeliveryReceiptService.26"));
		} else if(!salesPersonnelService.getSalesPersonnel(salesPersonnelId).isActive()) {
			//Inactive sales personnel
			errors.rejectValue("salesPersonnelId", null, null, ValidatorMessages.getString("DeliveryReceiptService.15"));
		}

		if(dr.getDrRefNumber() != null && !dr.getDrRefNumber().trim().isEmpty()
				&& dr.getDrRefNumber().length() > DeliveryReceipt.MAX_DR_REF_NO) {
			errors.rejectValue("drRefNumber", null, null,
					String.format(ValidatorMessages.getString("DeliveryReceiptService.16"), DeliveryReceipt.MAX_DR_REF_NO));
		}

		boolean hasDrLines = dr.getDrLines() != null && !dr.getDrLines().isEmpty();
		List<SerialItem> serialItems = dr.getSerialDrItems();
		List<DeliveryReceiptItem> drItems = dr.getNonSerialDrItems();
		boolean hasSerialItems = serialItems != null && !serialItems.isEmpty();
		boolean hasDrItems = drItems != null && !drItems.isEmpty();
		if(!hasSerialItems && !hasDrItems && !hasDrLines) {
			errors.rejectValue("commonErrorMsg", null, null, ValidatorMessages.getString("DeliveryReceiptService.12"));
		} else {
			if (drItems != null && !drItems.isEmpty()) {
				int row = 0;
				for (DeliveryReceiptItem dri : drItems) {
					row++;
					Integer itemId = dri.getItemId();
					if (itemId == null) {
						errors.rejectValue("nonSerialErrMsg", null, null, 
								String.format(ValidatorMessages.getString("AuthorityToWithdrawService.9"), row));
					} else if (!itemService.getItem(itemId).isActive()) {
						errors.rejectValue("nonSerialErrMsg", null, null,
								String.format(ValidatorMessages.getString("AuthorityToWithdrawService.10"),
										dri.getStockCode(), row));
					}
					Double quantity = dri.getQuantity();
					Integer warehouseId = dri.getWarehouseId();
					if (warehouseId != null && date != null) {
						boolean isWarehouseChanged = dri.getOrigWarehouseId() != null ? !dri.getOrigWarehouseId().equals(dri.getWarehouseId()) : false;
						String quantityErrorMsg = ValidationUtil.validateWithdrawnQty(itemService, warehouseService,
								dri.getItemId(), isWarehouseChanged, drItems, dr.getDate(),
								dri.getWarehouseId(), row);
						if(quantityErrorMsg != null) {
							errors.rejectValue("nonSerialErrMsg", null, null, quantityErrorMsg);
						}
					}
					if (quantity == null) {
						errors.rejectValue("nonSerialErrMsg", null, null, 
								String.format(ValidatorMessages.getString("AuthorityToWithdrawService.11"), row));
					} else if (quantity <= 0) {
						errors.rejectValue("nonSerialErrMsg", null, null, 
								String.format(ValidatorMessages.getString("AuthorityToWithdrawService.12"), row));
					} else if (quantity > getRemainingQty(dr.getId(), dri.getRefenceObjectId())) {
						errors.rejectValue("nonSerialErrMsg", null, null, 
								String.format(ValidatorMessages.getString("AuthorityToWithdrawService.14"),
										itemService.getItem(dri.getItemId()).getStockCode(), row));
					}
					if(warehouseId == null) {
						//Warehouse is required in row %d.
						errors.rejectValue("nonSerialErrMsg", null, null, 
								String.format(ValidatorMessages.getString("DeliveryReceiptService.24"), row));
					} else if(!warehouseService.getWarehouse(warehouseId).isActive()) {
						//Inactive warehouse in row %d.
						errors.rejectValue("nonSerialErrMsg", null, null, 
								String.format(ValidatorMessages.getString("DeliveryReceiptService.25"), row));
					}
				}
			}
			if(hasSerialItems) {
				serialItemService.validateSerialItems("nonSerialErrMsg", "serialErrMsg",
						!hasDrItems, false, false, serialItems, errors);
			}
			
			if(hasDrLines) {
				int row = 0;
				for(DeliveryReceiptLine drLine : dr.getDrLines()) {
					double quantity = drLine.getQuantity() != null ? drLine.getQuantity() : 0;
					if (quantity > 0) {
						double remainingQty = drLineDao.getRemainingQty(dr.getId(), drLine.getRefenceObjectId());
						if (quantity > remainingQty) {
							errors.rejectValue("drLines", null, null, 
									String.format(ValidatorMessages.getString("DeliveryReceiptService.27"), remainingQty, row));
						}
					}
					row++;
					if(drLine.getServiceSettingId() == null) {
						//Invalid serivce in row %d.
						errors.rejectValue("drLines", null, null, 
								String.format(ValidatorMessages.getString("DeliveryReceiptService.22"), row));
					} else if(!serviceSettingDao.get(drLine.getServiceSettingId()).isActive()) {
						//Service is inactive in row %d.
						errors.rejectValue("drLines", null, null, 
								String.format(ValidatorMessages.getString("DeliveryReceiptService.23"), row));
					}
				}
			}
		}
		refDocumentService.validateReferences(dr.getReferenceDocuments(), errors);
	}

	private double getRemainingQty(Integer drId, Integer referenceObjectId) {
		return deliveryReceiptItemDao.getRemainingRefItemQty(drId, referenceObjectId);
	}


	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return deliveryReceiptDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return deliveryReceiptDao.getByWorkflowId(workflowId);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID 
				&& currentWorkflowLog.getComment() != null && !currentWorkflowLog.getComment().isEmpty()) {
			DeliveryReceipt dr = deliveryReceiptDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			String errorMessage = "Delivery Receipt form has been used by the ff AR Invoice forms:";
			String arInvoiceNumber = "";
			if (dr != null) {
				Integer drId = dr.getId();
				List<ArInvoice> arInvoices = arInvoiceDao.getARIsByDRId(drId);
				if (!arInvoices.isEmpty()) {
					for (ArInvoice ari : arInvoices) {
						arInvoiceNumber += "<br>" + ari.getSequenceNo();
					}
				}
				if (!arInvoiceNumber.isEmpty()) {
					errorMessage += arInvoiceNumber;
					bindingResult.reject("workflowMessage", errorMessage);
					currentWorkflowLog.setWorkflowMessage(errorMessage);
				} else {
					List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(
							dr.getEbObjectId(), DeliveryReceipt.DR_SI_OR_TYPE_ID);
					serialItemService.setSerialNumberToInactive(savedSerialItems);
				}
			}
		}
	}

	/**
	 * Search the {@link DeliveryReceipt}
	 * @param searchCriteria The search Criteria
	 * @return The List of searched DeliveryReceipts.
	 */
	public List<FormSearchResult> searchDeliveryReceipts(Integer typeId, String searchCriteria) {
		Page<DeliveryReceipt> deliveryReceipts = deliveryReceiptDao.searchDeliveryReceipts(typeId, searchCriteria, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		Company company = null;
		ArCustomer customer = null;
		ArCustomerAccount custAcct = null;
		Term term = null;
		for(DeliveryReceipt deliveryReceipt : deliveryReceipts.getData()) {
			company = companyDao.get(deliveryReceipt.getCompanyId());
			customer = arCustomerDao.get(deliveryReceipt.getArCustomerId());
			custAcct = arCustomerAcctDao.get(deliveryReceipt.getArCustomerAccountId());
			term = termDao.get(deliveryReceipt.getTermId());
			title = String.valueOf(company.getCompanyCode() + " " + deliveryReceipt.getSequenceNo());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", company.getName()));
			properties.add(ResultProperty.getInstance("Customer", customer.getName()));
			properties.add(ResultProperty.getInstance("Customer Account", custAcct.getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(deliveryReceipt.getDate())));
			properties.add(ResultProperty.getInstance("Term", term.getName()));
			status = deliveryReceipt.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(deliveryReceipt.getId(), title, properties));
		}
		title = null;
		status = null;
		company = null;
		customer = null;
		custAcct = null;
		term = null;
		return result;
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		// Do nothing
	}

	@Override
	public void updateUnitCost(RItemCostUpdateService costUpdateService, WeightedAveItemAllocator<ItemTransaction> fifoAllocator,
			ItemTransaction it, int itemId, int warehouseId, Date formDate, boolean isAllocateRpTo) {
		// Do nothing
	}

	@Override
	public void processAllocatedItem(int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems,
			ItemTransaction currentAllocItem) throws CloneNotSupportedException {
		List<DeliveryReceiptItem> deliveryReceiptItems = deliveryReceiptItemDao.getAllByRefId(
				DeliveryReceiptItem.FIELD.deliveryReceiptId.name(), currentAllocItem.getId());
		logger.info("Processing the unit cost allocation for delivery items.");
		ListProcessorUtil<DeliveryReceiptItem> remover = new ListProcessorUtil<DeliveryReceiptItem>();
		List<Integer> formIds = remover.collectFormIds(deliveryReceiptItems);
		List<DeliveryReceiptItem> processedItems = summarizeDrItems(deliveryReceiptItems);
		Double allocQty = currentAllocItem.getQuantity();
		Double qtyToBeWithdrawn = null;
		DeliveryReceiptItem splitItem = null;
		List<Integer> savedDrItems = new ArrayList<Integer>();
		List<DeliveryReceiptItem> toBeUpdatedItems = null;
		SaleItemUtil<DeliveryReceiptItem> saleUtil = new SaleItemUtil<DeliveryReceiptItem>();
		for (DeliveryReceiptItem dri : processedItems) {
			// Create a new instance of DR item
			while (currentAllocItem != null) {
				if (qtyToBeWithdrawn == null) {
					qtyToBeWithdrawn = dri.getQuantity();
				}
				if (allocQty >= qtyToBeWithdrawn) {
					dri.setUnitCost(currentAllocItem.getUnitCost());
					dri.setQuantity(qtyToBeWithdrawn);
					dri = processDiscAndAmt(saleUtil, toBeUpdatedItems, dri);
					// Update DR items
					deliveryReceiptItemDao.saveOrUpdate(dri);
					savedDrItems.add(dri.getId());
					allocQty = NumberFormatUtil.roundOffNumber((allocQty - qtyToBeWithdrawn), NumberFormatUtil.SIX_DECIMAL_PLACES);
					if (allocQty == 0.0) {
						logger.debug("Current allocated quantity is zero");
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
						logger.info("Current allocated item "+currentAllocItem);
					}
					qtyToBeWithdrawn = null;
					break;
				} else {
					if (allocQty > 0) {
						logger.info("Allocated quantity is greater than "+currentAllocItem);
						splitItem = (DeliveryReceiptItem) dri.clone();
						splitItem.setId(0);
						splitItem.setQuantity(allocQty);
						splitItem.setUnitCost(currentAllocItem.getUnitCost());
						splitItem = processDiscAndAmt(saleUtil, toBeUpdatedItems, splitItem);
						// Save DR items
						deliveryReceiptItemDao.saveOrUpdate(splitItem);
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
			// Delete the items that were not updated.
			frequency = Collections.frequency(savedDrItems, id);
			if (frequency == 0) {
				logger.info("Deleting delivery receipt item/s");
				toBeDeleted.add(deliveryReceiptItemDao.get(id));
			}
		}

		if (!toBeDeleted.isEmpty()) {
			logger.info("Deleting delivery receipt item/s");
			for (Domain tbd : toBeDeleted) {
				logger.debug("Deleting the id: "+tbd);
				deliveryReceiptItemDao.delete(tbd);
			}
		}

		remover = null;
		processedItems = null;
		toBeDeleted = null;
		formIds = null;
		savedDrItems = null;
		logger.info("Successfully updated the quantity and unit cost of item transaction from Processing Report form.");
	}

	/**
	 * Process the sale item discount and amount
	 */
	private DeliveryReceiptItem processDiscAndAmt(SaleItemUtil<DeliveryReceiptItem> saleUtil,
			List<DeliveryReceiptItem> toBeUpdatedItems, DeliveryReceiptItem drItem) {
		logger.info("Processing discount and amount of the delivery receipt item id: "+drItem.getId());
		toBeUpdatedItems = new ArrayList<DeliveryReceiptItem>(1);
		toBeUpdatedItems.add(drItem);
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
		if (currentAllocItem != null) {
			return currentAllocItem.getQuantity();
		}
		logger.debug("Current allocated transaction is null.");
		return 0;
	}

	private List<DeliveryReceiptItem> summarizeDrItems(List<DeliveryReceiptItem> drItems) {
		List<DeliveryReceiptItem> updatedItems = new ArrayList<DeliveryReceiptItem>();
		Map<String, DeliveryReceiptItem> drItemHm = new HashMap<String, DeliveryReceiptItem>();

		DeliveryReceiptItem editedItem = null;
		String itemKey = null;
		for (DeliveryReceiptItem dri : drItems) {
			itemKey = "i" + dri.getItemId();
			if (drItemHm.containsKey(itemKey)) {
				editedItem = processEditedItem(dri, drItemHm.get(itemKey));
				drItemHm.put(itemKey, editedItem);
			} else {
				drItemHm.put(itemKey, dri);
			}
		}

		for (Map.Entry<String, DeliveryReceiptItem> iHM : drItemHm.entrySet()) {
			updatedItems.add(iHM.getValue());
		}

		drItemHm = null;
		editedItem = null;

		Collections.sort(updatedItems, new Comparator<DeliveryReceiptItem>() {
			@Override
			public int compare(DeliveryReceiptItem dri1, DeliveryReceiptItem dri2) {
				if (dri1.getId() < dri2.getId()) {
					return -1;
				} else if (dri1.getId() > dri2.getId()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		return updatedItems;
	}

	private DeliveryReceiptItem processEditedItem(DeliveryReceiptItem dri, DeliveryReceiptItem editedItem) {
		editedItem.setQuantity(dri.getQuantity() + editedItem.getQuantity());
		editedItem.setOrigQty((dri.getOrigQty() != null ? dri.getOrigQty() : 0)
				+ (editedItem.getOrigQty() != null ? editedItem.getOrigQty() : 0));
		editedItem.setAmount((editedItem.getQuantity() != null ? editedItem.getQuantity(): 0)
				* (editedItem.getSrp() != null ? editedItem.getSrp(): 0));
		return editedItem;
	}

	@Override
	public String getFormLabel() {
		return DeliveryReceipt.DELIVERY_RECEIPT_FORM_NAME;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		DeliveryReceipt deliveryReceipt = deliveryReceiptDao.getByEbObjectId(ebObjectId);
		Integer pId = deliveryReceipt.getId();

		FormProperty property = workflowHandler.getProperty(deliveryReceipt.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId="+ pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = deliveryReceipt.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = getFormName(deliveryReceipt.getDeliveryReceiptTypeId()) + " - " + deliveryReceipt.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + companyDao.get(deliveryReceipt.getCompanyId()).getName())
				.append(" " + arCustomerDao.get(deliveryReceipt.getArCustomerId()).getName())
				.append("<b> DATE : </b>" + DateUtil.formatDate(deliveryReceipt.getDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case AuthorityToWithdraw.OBJECT_TYPE_ID:
				return atwDao.getByEbObjectId(ebObjectId);
			case DeliveryReceiptItem.OBJECT_TYPE_ID:
				return deliveryReceiptItemDao.getByEbObjectId(ebObjectId);
			case SerialItem.OBJECT_TYPE_ID:
				return serialItemDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	private String getFormName(Integer drTypeId) {
		return drTypeDao.get(drTypeId).getName();
	}

	/**
	 * Get the paged list for {@code AuthorityToWithdraw} for DR form referencing
	 * @param companyId The company id
	 * @param arCustomerId The customer id
	 * @param arCustomerAccountId The customer account id
	 * @param atwNumber The ATW number
	 * @param statusId The ATW form status
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param pageNumber The page number
	 * @param drTypeId The delivery receipt type id
	 * @return The paged list for {@code AuthorityToWithdraw} for DR form referencing
	 */
	public Page<AuthorityToWithdraw> getAuthorityToWithdraws(Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer atwNumber, Integer statusId, Date dateFrom, Date dateTo,
			Integer pageNumber, Integer drTypeId) {
		Page<AuthorityToWithdraw> authorityToWithdraws = atwDao.getATWReferences(companyId, arCustomerId,
				arCustomerAccountId, atwNumber, statusId, dateFrom, dateTo, new PageSetting(pageNumber),
				drTypeId);
		for (AuthorityToWithdraw atw : authorityToWithdraws.getData()) {
			atw.setArCustomer(arCustomerDao.get(atw.getArCustomerId()));
			atw.setArCustomerAccount(arCustomerAcctDao.get(atw.getArCustomerAcctId()));
		}
		return authorityToWithdraws;
	}

	/**
	 * Get the list of {@link SalesOrder} reference for {@link DeliveryReceipt}. 
	 * @param companyId The company id.
	 * @param arCustomerId The ar customer id.
	 * @param arCustomerAcctId The ar customer account id.
	 * @param soNumber The sales order sequence number.
	 * @param statusId The status id.
	 * @param dateFrom The start date filter.
	 * @param dateTo The end date filter.
	 * @param pageSetting The pagenumber.
	 * @param drTypeId The dr type id.
	 * @return The list of {@link SalesOrder} reference.
	 */
	public Page<SalesOrder> getSalesOrderRefs(Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer soNumber, Integer statusId, Date dateFrom, Date dateTo,
			Integer pageNumber, Integer drTypeId, String poNumber, Integer divisionId) {
		Page<SalesOrder> salesOrders = salesOrderDao.getDrSalesOrders(companyId, arCustomerId,
				arCustomerAccountId, soNumber, statusId, dateFrom, dateTo,
				new PageSetting(pageNumber), drTypeId, poNumber, divisionId);
		for (SalesOrder so : salesOrders.getData()) {
			so.setArCustomer(arCustomerDao.get(so.getArCustomerId()));
			so.setArCustomerAccount(arCustomerAcctDao.get(so.getArCustomerAcctId()));
		}
		return salesOrders;
	}


	/**
	 * Convert ATW to DR
	 * @param atwRefId
	 * @return {@link DeliveryReceipt}
	 */
	public DeliveryReceipt convertATWtoDR(Integer atwRefId) {
		DeliveryReceipt deliveryReceipt = new DeliveryReceipt();
		AuthorityToWithdraw atw = atwService.getAuthorityToWithdraw(atwRefId);
		if (atw != null) {
			deliveryReceipt.setAuthorityToWithdrawId(atw.getId());
			deliveryReceipt.setAtwNumber(atw.getSequenceNumber().toString());
			int companyId = atw.getCompanyId();
			deliveryReceipt.setCompanyId(companyId);
			deliveryReceipt.setCompany(atw.getCompany());
			deliveryReceipt.setArCustomerId(atw.getArCustomerId());
			deliveryReceipt.setArCustomer(atw.getArCustomer());
			deliveryReceipt.setArCustomerAccountId(atw.getArCustomerAcctId());
			deliveryReceipt.setArCustomerAccount(atw.getArCustomerAccount());
			deliveryReceipt.setTermId(atw.getArCustomerAccount().getTermId());
			Term term = atw.getArCustomerAccount().getTerm();
			deliveryReceipt.setTerm(term);
			deliveryReceipt.setRemarks(atw.getShipTo());
			List<SerialItem> drSerialItems = new ArrayList<SerialItem>();
			SerialItem drsi = null;
			Item item = null;
			for (SerialItem si : atw.getSerialItems()) {
				drsi = new SerialItem();
				item = itemService.getItem(si.getItemId());
				drsi.setRefenceObjectId(si.getEbObjectId());
				drsi.setItem(item);
				drsi.setItemId(item.getId());
				drsi.setStockCode(item.getStockCode());
				drsi.setWarehouseId(si.getWarehouseId());
				drsi.setSerialNumber(si.getSerialNumber());
				drsi.setQuantity(si.getQuantity());
				drsi.setItemSrpId(si.getItemSrpId());
				drsi.setOrigSrp(si.getSrp());
				drsi.setSrp(si.getSrp());
				drsi.setDiscount(si.getDiscount());
				drsi.setTaxTypeId(si.getTaxTypeId());
				drsi.setVatAmount(si.getVatAmount());
				drsi.setItemDiscountTypeId(si.getItemDiscountTypeId());
				drsi.setDiscountValue(si.getDiscountValue());
				drsi.setActive(si.isActive());
				drSerialItems.add(drsi);
				drsi = null;
				item = null;
			}
			deliveryReceipt.setSerialDrItems(drSerialItems);
			Integer divisionId = deliveryReceipt.getDivisionId();
			List<DeliveryReceiptItem> drItems = new ArrayList<DeliveryReceiptItem>();
			DeliveryReceiptItem dri = null;
			ItemSrp itemSrp = null;
			AtwItemDto dto = null;
			for (AuthorityToWithdrawItem atwi : atw.getAtwItems()) {
				dri = new DeliveryReceiptItem();
				item = itemService.getItem(atwi.getItemId());
				itemSrp = itemSrpDao.getLatestItemSrp(companyId, item.getId(), divisionId);
				dto = atwService.getAtwItemByRefItemObjectId(atwi.getEbObjectId());
				dri.setRefenceObjectId(atwi.getEbObjectId());
				dri.setItem(item);
				dri.setItemId(item.getId());
				dri.setStockCode(item.getStockCode());
				dri.setWarehouseId(atwi.getWarehouseId());
				dri.setItemSrpId(itemSrp.getId());
				dri.setQuantity(atwi.getQuantity());
				dri.setOrigSrp(dto.getSrp());
				dri.setSrp(dto.getSrp());
				dri.setDiscount(dto.getDiscount());
				dri.setTaxTypeId(dto.getTaxTypeId());
				dri.setVatAmount(dto.getVatAmount());
				dri.setItemDiscountTypeId(dto.getItemDiscountTypeId());
				dri.setDiscountValue(dto.getDiscountValue());
				drItems.add(dri);
				dto = null;
				dri = null;
				item = null;
				itemSrp = null;
			}
			deliveryReceipt.setNonSerialDrItems(drItems);

			List<SalesOrderLine> soLines = salesOrderService.getSOLines(atw.getSalesOrderId());
			List<DeliveryReceiptLine> drLines = new ArrayList<DeliveryReceiptLine>();
			DeliveryReceiptLine drLine = null;
			for (SalesOrderLine sol : soLines) {
				drLine = new DeliveryReceiptLine();
				drLine.setRefenceObjectId(sol.getEbObjectId());
				drLine.setServiceSettingId(sol.getServiceSettingId());
				drLine.setServiceSettingName(sol.getServiceSetting().getName());
				drLine.setQuantity(sol.getQuantity());
				drLine.setUpAmount(sol.getUpAmount());
				drLine.setDiscountTypeId(sol.getDiscountTypeId());
				drLine.setDiscountValue(sol.getDiscountValue());
				drLine.setDiscount(sol.getDiscount());
				drLine.setTaxTypeId(sol.getTaxTypeId());
				drLine.setVatAmount(sol.getVatAmount());
				Integer uomId = sol.getUnitOfMeasurementId();
				if (uomId != null) {
					drLine.setUnitOfMeasurementId(uomId);
					drLine.setUnitMeasurementName(sol.getUnitMeasurement().getName());
				}
				drLine.setAmount(sol.getAmount());
				drLines.add(drLine);
				drLine = null;
			}
			soLines = null;
			deliveryReceipt.setDrLines(drLines);
		}
		return deliveryReceipt;
	}

	/**
	 * Convert sales order to delivery receipt object
	 * @param salesOrderId The sales order id
	 * @return The delivery receipt object
	 */
	public DeliveryReceipt convertSOtoDR(int salesOrderId, int drTypeId) {
		DeliveryReceipt deliveryReceipt = new DeliveryReceipt();
		SalesOrder salesOrder = salesOrderDao.get(salesOrderId);
		salesOrder.setSoItems(salesOrderService.getSOItems(salesOrderId, false));
		salesOrder.setSoLines(salesOrderService.getSOLines(salesOrderId));
		if (salesOrder != null) {
			deliveryReceipt.setSalesOrderId(salesOrder.getId());
			deliveryReceipt.setAtwNumber(salesOrder.getSequenceNumber().toString());
			int companyId = salesOrder.getCompanyId();
			deliveryReceipt.setCompanyId(companyId);
			deliveryReceipt.setCompany(salesOrder.getCompany());
			deliveryReceipt.setArCustomerId(salesOrder.getArCustomerId());
			deliveryReceipt.setArCustomer(salesOrder.getArCustomer());
			deliveryReceipt.setArCustomerAccountId(salesOrder.getArCustomerAcctId());
			deliveryReceipt.setArCustomerAccount(salesOrder.getArCustomerAccount());
			deliveryReceipt.setTermId(salesOrder.getTermId());
			deliveryReceipt.setTerm(salesOrder.getTerm());
			deliveryReceipt.setRemarks(salesOrder.getShipTo());
			deliveryReceipt.setCurrencyId(salesOrder.getCurrencyId());
			deliveryReceipt.setCurrencyRateId(salesOrder.getCurrencyRateId());
			deliveryReceipt.setCurrencyRateValue(salesOrder.getCurrencyRateValue());
			deliveryReceipt.setDivision(salesOrder.getDivision());
			deliveryReceipt.setPoNumber(salesOrder.getPoNumber());
			deliveryReceipt.setWtAcctSettingId(salesOrder.getWtAcctSettingId());
			deliveryReceipt.setWtAmount(salesOrder.getWtAmount());
			deliveryReceipt.setWtVatAmount(salesOrder.getWtVatAmount());
			//Items
			SerialItemSetup siSetup = null;
			SerialItem serialItem = null;
			Item item = null;
			ItemSrp itemSrp = null;
			List<SerialItem> serialItems = new ArrayList<SerialItem>();
			DeliveryReceiptItem drItem = null;
			List<DeliveryReceiptItem> drItems = new ArrayList<DeliveryReceiptItem>();
			int soId = salesOrder.getId();
			Integer divisionId = salesOrder.getDivisionId();
			for (SalesOrderItem soi : salesOrder.getSoItems()) {
				item = itemService.getItem(soi.getItemId());
				siSetup = siSetupDao.getSerialItemSetupByItemId(soi.getItemId());
				itemSrp = itemSrpDao.getLatestItemSrp(companyId, soi.getItemId(), divisionId);
				if (siSetup != null && siSetup.isSerializedItem()) {
					double qtyDiscount = (soi.getDiscount() == null ? 0 : soi.getDiscount()) / soi.getQuantity();
					double vat = soi.getVatAmount() != null ? soi.getVatAmount() : 0;
					double qtyVat = vat / soi.getQuantity(); 
					double remainingQty = deliveryReceiptItemDao.getSerialItemRemainingQty(soId, item.getId());
					for (int i = 0; i < remainingQty; i++) {
						serialItem = new SerialItem();
						serialItem.setItemId(soi.getItemId());
						serialItem.setItem(item);
						serialItem.setStockCode(item.getStockCode());
						serialItem.setQuantity(1.0);
						if (itemSrp != null) {
							serialItem.setItemSrpId(itemSrp.getId());
						}
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
					double remainingQty = deliveryReceiptItemDao.getNonSerialItemRemainingQty(soId, item.getId());
					if (remainingQty > 0) {
						drItem = new DeliveryReceiptItem();
						drItem.setItem(item);
						drItem.setItemId(soi.getItemId());
						drItem.setStockCode(item.getStockCode());
						drItem.setQuantity(remainingQty);
						drItem.setRefenceObjectId(soi.getEbObjectId());
						drItem.setSrp(soi.getGrossAmount());
						if (itemSrp != null) {
							drItem.setItemSrpId(itemSrp.getId());
						}
						drItem.setDiscount(soi.getDiscount());
						drItem.setTaxTypeId(soi.getTaxTypeId());
						drItem.setVatAmount(soi.getVatAmount());
						drItem.setItemDiscountTypeId(soi.getItemDiscountTypeId());
						drItem.setDiscountValue(soi.getDiscountValue());
						drItems.add(drItem);
						drItem = null;
					}
				}
				item = null;
				siSetup = null;
				itemSrp = null;
			}
			deliveryReceipt.setSerialDrItems(serialItems);
			deliveryReceipt.setNonSerialDrItems(drItems);
			//Lines
			List<DeliveryReceiptLine> drLines = new ArrayList<DeliveryReceiptLine>();
			DeliveryReceiptLine drLine = null;
			for (SalesOrderLine sol : salesOrder.getSoLines()) {
				double remainingQty = drLineDao.getRemainingQty(null, sol.getEbObjectId());
				if (remainingQty > 0) {
					drLine = new DeliveryReceiptLine();
					drLine.setRefenceObjectId(sol.getEbObjectId());
					drLine.setServiceSettingId(sol.getServiceSettingId());
					drLine.setServiceSettingName(sol.getServiceSetting().getName());
					drLine.setQuantity(remainingQty);
					drLine.setUpAmount(sol.getUpAmount());
					drLine.setDiscountTypeId(sol.getDiscountTypeId());
					drLine.setDiscountValue(sol.getDiscountValue());
					drLine.setDiscount(sol.getDiscount());
					drLine.setTaxTypeId(sol.getTaxTypeId());
					drLine.setVatAmount(sol.getVatAmount());
					drLine.setDescription(sol.getDescription());
					if(sol.getQuantity() != null) {
						drLine.setPercentile(computePercentage(remainingQty, sol.getQuantity()));
					}
					Integer uomId = sol.getUnitOfMeasurementId();
					if (uomId != null) {
						drLine.setUnitOfMeasurementId(uomId);
						drLine.setUnitMeasurementName(sol.getUnitMeasurement().getName());
					}
					drLine.setAmount(sol.getAmount());
					drLines.add(drLine);
					drLine = null;
				}
			}
			deliveryReceipt.setDrLines(drLines);
		}
		return deliveryReceipt;
	}

	/**
	 * Compute the percentage. 
	 * @param divisor The divisor.
	 * @param dividend The dividend.
	 * @return The computed percentage.
	 */
	private Double computePercentage(double dividend, double divisor) {
		return (dividend / divisor) * 100;
	}

	/**
	 * Get the list of sales order with sales order trucking line data.
	 * @param companyId The company id.
	 * @param soNumber The sales order sequence number.
	 * @param arCustomerId The ar customer id.
	 * @param arCustomerAcctId The ar customer account id.
	 * @param statusId The form current status id.
	 * @param pageSetting The {@link PageSetting} object. 
	 * @return List of {@link SalesOrder}.
	 */
	public Page<SalesOrder> getSOTruckingReferences(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, Integer pageNumber, Integer typeId) {
		Page<SalesOrder> salesOrders =salesOrderDao.getSOTruckingReferences(companyId, soNumber, arCustomerId,
				arCustomerAcctId, statusId, new PageSetting(pageNumber), typeId);
		for(SalesOrder so : salesOrders.getData()) {
			so.setArCustomer(arCustomerDao.get(so.getArCustomerId()));
		}
		return salesOrders;
	}

	/**
	 * Process sales order object to delivery receipt.
	 * @param soId The sales order id.
	 * @param typeId Dr type.
	 * @return The processed {@link DeliveryReceipt.}
	 */
	public DeliveryReceipt convertSO(Integer soId, Integer typeId) {
		DeliveryReceipt dr = new DeliveryReceipt();
		SalesOrder so = salesOrderDao.get(soId);
		if (so != null) {
			//Header
			dr.setSalesOrderId(so.getId());
			dr.setCompanyId(so.getCompanyId());
			dr.setCompany(so.getCompany());
			dr.setArCustomerId(so.getArCustomerId());
			dr.setArCustomer(so.getArCustomer());
			ArCustomerAccount customerAcct = so.getArCustomerAccount();
			dr.setArCustomerAccountId(so.getArCustomerAcctId());
			dr.setArCustomerAccount(customerAcct);
			dr.setTermId(customerAcct.getTermId());
			dr.setTerm(customerAcct.getTerm());
			dr.setRemarks(so.getShipTo());
			dr.setSoNumber(so.getSequenceNumber().toString());
//			Disabling this since WAYBILL and EQUIPMENT UTIL
//			if (typeId.equals(DeliveryReceiptType.WAYBILL_DR_TYPE_ID)) {
//				//Trucking lines
//				List<SalesOrderTruckingLine> sotLines = salesOrderService.getSOTLines(soId);
//				List<WaybillLine> wbLines = new ArrayList<WaybillLine>();
//				WaybillLine wbLine = null;
//				for(SalesOrderTruckingLine sotLine : sotLines) {
//					wbLine = new WaybillLine();
//					wbLine.setRefenceObjectId(sotLine.getEbObjectId());
//					wbLine.setArLineSetupId(sotLine.getArLineSetupId());
//					wbLine.setArLineSetupName(sotLine.getArLineSetup().getName());
//					wbLine.setQuantity(sotLine.getQuantity());
//					wbLine.setUpAmount(sotLine.getUpAmount());
//					wbLine.setDiscountTypeId(sotLine.getDiscountTypeId());
//					wbLine.setDiscountValue(sotLine.getDiscountValue());
//					wbLine.setDiscount(sotLine.getDiscount());
//					wbLine.setTaxTypeId(sotLine.getTaxTypeId());
//					wbLine.setVatAmount(sotLine.getVatAmount());
//					Integer uomId = sotLine.getUnitOfMeasurementId();
//					if (uomId != null) {
//						wbLine.setUnitOfMeasurementId(uomId);
//						wbLine.setUnitMeasurementName(sotLine.getUnitMeasurement().getName());
//					}
//					wbLine.setAmount(sotLine.getAmount());
//					wbLines.add(wbLine);
//					wbLine = null;
//				}
//				dr.setWbLines(wbLines);
//			} else if (typeId.equals(DeliveryReceiptType.EQ_UTIL_DR_TYPE_ID)) {
//				//Equipment lines
//				List<SalesOrderEquipmentLine> equipmentLines = salesOrderService.getSOELines(soId);
//				List<EquipmentUtilizationLine> utilizationLines = new ArrayList<EquipmentUtilizationLine>();
//				EquipmentUtilizationLine utilizationLine = null;
//				for(SalesOrderEquipmentLine equipmentLine : equipmentLines) {
//					utilizationLine = new EquipmentUtilizationLine();
//					utilizationLine.setRefenceObjectId(equipmentLine.getEbObjectId());
//					utilizationLine.setArLineSetupId(equipmentLine.getArLineSetupId());
//					utilizationLine.setArLineSetupName(equipmentLine.getArLineSetup().getName());
//					utilizationLine.setQuantity(equipmentLine.getQuantity());
//					utilizationLine.setUpAmount(equipmentLine.getUpAmount());
//					utilizationLine.setDiscountTypeId(equipmentLine.getDiscountTypeId());
//					utilizationLine.setDiscountValue(equipmentLine.getDiscountValue());
//					utilizationLine.setDiscount(equipmentLine.getDiscount());
//					utilizationLine.setTaxTypeId(equipmentLine.getTaxTypeId());
//					utilizationLine.setVatAmount(equipmentLine.getVatAmount());
//					Integer uomId = equipmentLine.getUnitOfMeasurementId();
//					if (uomId != null) {
//						utilizationLine.setUnitOfMeasurementId(uomId);
//						utilizationLine.setUnitMeasurementName(equipmentLine.getUnitMeasurement().getName());
//					}
//					utilizationLine.setAmount(equipmentLine.getAmount());
//					utilizationLines.add(utilizationLine);
//					utilizationLine = null;
//				}
//				dr.setEuLines(utilizationLines);
//			}
		}
		return dr;
	}

	/**
	 * Get the DR division id by DR type id.
	 * @param typeId The DR type id.
	 * @return The division id.
	 */
	public int getDivisionByDrTypeId(int typeId) {
		int divisionId = 0;
		switch (typeId) {
		case DeliveryReceiptType.DR_CENTRAL_TYPE_ID:
			divisionId = 1;
			break;
		case DeliveryReceiptType.DR_NSB3_TYPE_ID:
			divisionId = 2;
			break;
		case DeliveryReceiptType.DR_NSB4_TYPE_ID:
			divisionId = 3;
			break;
		case DeliveryReceiptType.DR_NSB5_TYPE_ID:
			divisionId = 4;
			break;
		case DeliveryReceiptType.DR_NSB8_TYPE_ID:
			divisionId = 5;
			break;
		case DeliveryReceiptType.DR_NSB8A_TYPE_ID:
			divisionId = 6;
			break;
		}
		return divisionId;
	}

	/**
	 * Validate delivery receipt receiving details.
	 * @param deliveryReceipt The {@link DeliveryReceipt}.
	 * @param errors The {@link Error}
	 */
	public void validateStatusLogs(DeliveryReceipt deliveryReceipt, Errors errors) {
		if(deliveryReceipt.getDateReceived() == null) {
			//Received date is required.
			errors.rejectValue("dateReceived", null, null, ValidatorMessages.getString("DeliveryReceiptService.17"));
		}
		if(deliveryReceipt.getReceiver() == null || deliveryReceipt.getReceiver().trim().isEmpty()) {
			//Received by is required.
			errors.rejectValue("receiver", null, null, ValidatorMessages.getString("DeliveryReceiptService.18"));
		} else if(deliveryReceipt.getReceiver().length() > DeliveryReceipt.MAX_RECEIVER) {
			//Received by should not exceed %d characters.
			errors.rejectValue("receiver", null, null,
					String.format(ValidatorMessages.getString("DeliveryReceiptService.19"), DeliveryReceipt.MAX_RECEIVER));
		}
	}

	/**
	 * Save delivery receipt with receiving details.
	 * @param deliveryReceipt The {@link DeliveryReceipt}.
	 * @param user The {@link User}.
	 */
	public void saveDrReceivingDetails(DeliveryReceipt deliveryReceipt, User user) {
		DeliveryReceipt savedDr = deliveryReceiptDao.get(deliveryReceipt.getId());
		//Set receiving details
		savedDr.setDateReceived(deliveryReceipt.getDateReceived());
		savedDr.setReceiver(StringFormatUtil.removeExtraWhiteSpaces(deliveryReceipt.getReceiver()));
		AuditUtil.addAudit(savedDr, new Audit(user.getId(), true, new Date()));
		//Save
		deliveryReceiptDao.saveOrUpdate(savedDr);
	}
}
