package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.PurchaseRequisitionItemDao;
import eulap.eb.dao.RatioDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.RequisitionClassificationDao;
import eulap.eb.dao.RequisitionFormDao;
import eulap.eb.dao.RequisitionFormItemDao;
import eulap.eb.dao.RequisitionTypeDao;
import eulap.eb.dao.WorkOrderDao;
import eulap.eb.dao.WorkOrderItemDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.PurchaseRequisitionItem;
import eulap.eb.domain.hibernate.Ratio;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.RequisitionClassification;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.RequisitionFormItem;
import eulap.eb.domain.hibernate.RequisitionType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.domain.hibernate.WorkOrderItem;
import eulap.eb.domain.hibernate.WorkOrderLine;
import eulap.eb.service.oo.OODomain;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.RequisitionFormDto;
import eulap.eb.web.dto.ResultProperty;
import eulap.eb.web.dto.UsedRequisitionFormDto;

/**
 * Class that handles the business logic {@code RequesitionForm}

 *
 */
@Service
public class RequisitionFormService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(RequisitionFormService.class);
	@Autowired
	private RequisitionTypeDao requisitionTypeDao;
	@Autowired
	private RequisitionClassificationDao requisitionClassificationDao;
	@Autowired
	private RequisitionFormDao requisitionFormDao;
	@Autowired
	private RequisitionFormItemDao requisitionFormItemDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private RatioDao ratioDao;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private WorkOrderDao workOrderDao;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private WorkOrderItemDao woItemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private PurchaseRequisitionItemDao prItemDao;
	@Autowired
	private JyeiWithdrawalSlipService jyeiWsService;

	/**
	 * Get the name of the requisition type by id.
	 * @param requisitionTypeId The parameter for filtering the requisition type.
	 * @param True if purchase request, otherwise false.
	 * @return The name of the requisition type.
	 */
	public String getRequisitionFormTypeName(int requisitionTypeId, boolean isPurchaseRequest) {
		String name = (isPurchaseRequest ? "Purchase Request" : "Material Requisition");
		return name;
	}

	/**
	 * Get the list of all active requisition types.
	 * @return The list of active requisition types.
	 */
	public List<RequisitionType> getAllActiveRequisitionTypes() {
		return requisitionTypeDao.getAllActive();
	}

	/**
	 * Get the list of classifications but filters the list if its purchase request or requisition form.
	 * @param isPurchaseRequest True if to filter purchase request only, otherwise false.
	 */
	public List<RequisitionClassification> getAllActiveReqClassifications(boolean isPurchaseRequest) {
		return requisitionClassificationDao.getAllActive(isPurchaseRequest);
	}

	/**
	 * Get the list of all active ratios.
	 * @return The list of active ratios.
	 */
	public List<Ratio> getAllActiveRatios() {
		return ratioDao.getAllActive();
	}

	/**
	 * Get the requisition form dto.
	 * @param requisitionFormId
	 * @param isIncludeItems
	 * @param isIncludeDocuments
	 * @return
	 */
	public RequisitionFormDto getRequisitionFormDto (Integer requisitionFormId, boolean isIncludeItems,
			boolean isIncludeDocuments) {
		RequisitionForm requisitionForm = getById(requisitionFormId, isIncludeItems, isIncludeDocuments);
		RequisitionFormDto dto = RequisitionFormDto.getInstanceOf(requisitionForm);
		Integer warehouseId = requisitionForm.getWarehouseId();
		if (warehouseId != null && warehouseId > 0) {
			dto.setWarehouseName(warehouseService.getWarehouse(warehouseId).getName());
		}
		return dto;
	}

	/**
	 * Get requisition form from the database by its id. 
	 * @param requisitionFormId The requisition form id
	 * @param isIncludeItems True if include RF items, otherwise false
	 * @param isIncludeDocuments True if include reference documents, otherwise false
	 * @return The processed requisition form object
	 */
	public RequisitionForm getById(Integer requisitionFormId, boolean isIncludeItems,
			boolean isIncludeDocuments) {
		RequisitionForm requisitionForm = requisitionFormDao.get(requisitionFormId);
		if (requisitionForm == null) {
			throw new RuntimeException("There is no Requisition Form object with id : " + requisitionFormId);
		}
		Integer companyId = requisitionForm.getCompanyId();
		Integer parentObjectId = requisitionForm.getEbObjectId();
		Integer warehouseId = requisitionForm.getWarehouseId() != null ? requisitionForm.getWarehouseId() : -1;
		if (requisitionForm.getWorkOrderId() != null) {
			requisitionForm.setWoNumber(workOrderDao.get(requisitionForm.getWorkOrderId()).getSequenceNumber().toString());
		}
		if (isIncludeItems) {
			List<RequisitionFormItem> rfItems = getRfItems(parentObjectId);
			if (rfItems != null && !rfItems.isEmpty()) {
				Item item = null;
				Date date = new Date();
				for (RequisitionFormItem rfi : rfItems) {
					int itemId = rfi.getItemId();
					item = itemDao.get(itemId);
					rfi.setItem(item);
					rfi.setStockCode(item.getStockCode());
					rfi.setExistingStocks(itemDao.getItemExistingStocks(itemId,
							warehouseId, date, companyId));
					item = null;
				}
			}
			requisitionForm.setRequisitionFormItems(rfItems);
		}
		if (isIncludeDocuments) {
			List<ReferenceDocument> referenceDocuments = referenceDocumentService.getReferenceDocuments(parentObjectId);
			requisitionForm.setReferenceDocuments(referenceDocuments);
		}
		return requisitionForm;
	}

	/**
	 * Get the list of {@code RequisitionFormItem} by the eb object id of the parent object.
	 * @param refObjectId The eb object id of the {@code RequisitionForm}
	 */
	public List<RequisitionFormItem> getRfItems(Integer refObjectId) {
		return requisitionFormItemDao.getAllByParent(refObjectId);
	}

	/**
	 * Validate the requisition form.
	 * @param requisitionFormDto The dto for {@code RequisitionForm}
	 * @param errors The validation errors.
	 */
	public void validateRequisitionForm(RequisitionFormDto requisitionFormDto, Errors errors) {
		RequisitionForm rf = requisitionFormDto.getRequisitionForm();
		Integer companyId = rf.getCompanyId();
		ValidatorUtil.validateCompany(companyId, companyService, errors, "requisitionForm.companyId");

		Integer warehouseId = rf.getWarehouseId();
		if (warehouseId != null && warehouseId > 0) {
			if (!warehouseService.getWarehouse(warehouseId).isActive()) {
				errors.rejectValue("requisitionForm.warehouseId", null, null, ValidatorMessages.getString("RequisitionFormService.18"));
			}
		}

		if (rf.getDate() == null) {
			errors.rejectValue("requisitionForm.date", null, null, ValidatorMessages.getString("RequisitionFormService.0"));
		} else if(!timePeriodService.isOpenDate(rf.getDate())) {
			errors.rejectValue("requisitionForm.date", null, null, ValidatorMessages.getString("RequisitionFormService.1"));
		}

		Integer reqClassificationId = rf.getRequisitionClassificationId();
		boolean isPurchaseRequest = (reqClassificationId != null &&
				reqClassificationId.intValue() == RequisitionClassification.RC_PURCHASE_REQUISITION);

		if (isPurchaseRequest) {
			Integer refRfId = rf.getReqFormRefId();
			if (refRfId == null) {
				errors.rejectValue("requisitionForm.reqFormRefId", null, null, ValidatorMessages.getString("RequisitionFormService.9"));
			}

			List<PurchaseRequisitionItem> prItems = rf.getPurchaseRequisitionItems();
			if (prItems != null && !prItems.isEmpty()) {
				int rowCount = 0;
				for (PurchaseRequisitionItem pri : prItems) {
					rowCount++;
					Integer itemId = pri.getItemId();
					Double quantity = pri.getQuantity();
					if (itemId != null) {
						if(!itemDao.get(itemId).isActive()) {
							errors.rejectValue("errItemsMsg", null, null,
									String.format(ValidatorMessages.getString("RequisitionFormService.12"), rowCount));
						}
					}
					if (quantity == null || quantity <= 0) {
						errors.rejectValue("errItemsMsg", null, null,
								String.format(ValidatorMessages.getString("RequisitionFormService.5"), rowCount));
					} else {
						double remainingQty = getRemainingRFQty(pri.getRefenceObjectId(), null, rf.getId(), false);
						if (quantity > remainingQty) {
							errors.rejectValue("errItemsMsg", null, null,
									String.format(ValidatorMessages.getString("RequisitionFormService.20"), rowCount));
						}
					}
				}
			} else {
				errors.rejectValue("errItemsMsg", null, null, ValidatorMessages.getString("RequisitionFormService.2"));
			}
		} else {
			List<RequisitionFormItem> rfItems = rf.getRequisitionFormItems();
			if (rfItems != null && !rfItems.isEmpty()) {
				int rowCount = 0;
				for (RequisitionFormItem rfItem : rfItems) {
					rowCount++;
					Integer itemId = rfItem.getItemId();
					if(itemId == null) {
						errors.rejectValue("errItemsMsg", null, null, String.format(ValidatorMessages.getString("RequisitionFormService.3"), rowCount));
					} else if(!itemDao.get(itemId).isActive()) {
						errors.rejectValue("errItemsMsg", null, null, String.format(ValidatorMessages.getString("RequisitionFormService.12"), rowCount));
					}

					if (rfItem.getQuantity() == null || rfItem.getQuantity() <= 0) {
						errors.rejectValue("errItemsMsg", null, null, String.format(ValidatorMessages.getString("RequisitionFormService.5"), rowCount));
					} else if (rf.getWorkOrderId() != null 
							&& rfItem.getQuantity() > woItemDao.getRemainingQty(rf.getWorkOrderId(), rfItem.getItemId(), rf.getId())) {
						errors.rejectValue("errItemsMsg", null, null,
								String.format(ValidatorMessages.getString("RequisitionFormService.21"), rowCount));
					}
				}
			} else {
				errors.rejectValue("errItemsMsg", null, null, ValidatorMessages.getString("RequisitionFormService.2"));
			}
		}
		referenceDocumentService.validateReferences(requisitionFormDto.getRequisitionForm().getReferenceDocuments(), errors);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		logger.info("Saving the purchase requisition form.");
		RequisitionForm requisitionForm = (RequisitionForm) form;
		boolean isNew = requisitionForm.getId() == 0;
		boolean isPurchaseRequisition = (requisitionForm.getRequisitionClassificationId() != null &&
				requisitionForm.getRequisitionClassificationId() == RequisitionClassification.RC_PURCHASE_REQUISITION);
		AuditUtil.addAudit(requisitionForm, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			requisitionForm.setSequenceNumber(requisitionFormDao.generateSequenceNumber(requisitionForm.getCompanyId(),
					isPurchaseRequisition, requisitionForm.getRequisitionTypeId()));
		} else {
			RequisitionForm saveRF = requisitionFormDao.get(requisitionForm.getId());
			DateUtil.setCreatedDate(requisitionForm, saveRF.getCreatedDate());

			List<PurchaseRequisitionItem> savePrItems = prItemDao.getAllByRefId(
					PurchaseRequisitionItem.FIELD.purchaseRequisitionId.name(), requisitionForm.getId());
			if (savePrItems != null && !savePrItems.isEmpty()) {
				List<Integer> toBeDeletedIds = new ArrayList<Integer>();
				for (PurchaseRequisitionItem pri : savePrItems) {
					toBeDeletedIds.add(pri.getId());
				}
				prItemDao.delete(toBeDeletedIds);
				toBeDeletedIds = null;
			}
			savePrItems = null;
		}
		requisitionForm.setWarehouseId(requisitionForm.getWarehouseId() > 0
				? requisitionForm.getWarehouseId() : null);
		requisitionFormDao.saveOrUpdate(requisitionForm);

		if (!isPurchaseRequisition) {
			// Saving the list of requisition form items.
			List<RequisitionFormItem> rfItems = requisitionForm.getRequisitionFormItems();
			List<Domain> toBeSavedItems = new ArrayList<Domain>();
			toBeSavedItems.addAll(rfItems);
			requisitionFormItemDao.batchSaveOrUpdate(toBeSavedItems);
			toBeSavedItems = null;
		} else {
			List<PurchaseRequisitionItem> prItems = requisitionForm.getPurchaseRequisitionItems();
			for (PurchaseRequisitionItem pri : prItems) {
				pri.setPurchaseRequisitionId(requisitionForm.getId());
				prItemDao.save(pri);
			}
		}
		// Saving the list of reference documents.
		referenceDocumentService.saveReferenceDocuments(user, isNew, requisitionForm.getEbObjectId(),
				requisitionForm.getReferenceDocuments(), true);
	}

	/**
	 * Search Requisition forms.
	 * @param searchCriteria The search criteria.
	 * @param typeId The type of the requisition forms.
	 */
	public List<FormSearchResult> searchRequisitionForms(String searchCriteria, int typeId, boolean isPurchaseRequest) {
		logger.info("Searching Requisition Form.");
		logger.debug("Searching for: "+searchCriteria.trim());
		Page<RequisitionForm> requisitionForms = 
				requisitionFormDao.searchRequisitionForms(searchCriteria.trim(), typeId, isPurchaseRequest, new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		for (RequisitionForm rf : requisitionForms.getData()) {
			logger.trace("Retrieved Seq No. " + rf.getSequenceNumber());
			title = String.valueOf(rf.getFormattedSequenceNo());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", rf.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(rf.getDate())));
			properties.add(ResultProperty.getInstance("Requested Date", DateUtil.formatDate(rf.getRequestedDate())));
			status = rf.getFormWorkflow().getCurrentStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(rf.getId(), title, properties));
		}
		return result;
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return requisitionFormDao.getByWorkflowId(workflowId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return requisitionFormDao.get(id).getFormWorkflow();
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case RequisitionForm.RF_TIRE_OBJECT_TYPE_ID:
			case RequisitionForm.RF_FUEL_OBJECT_TYPE_ID:
			case RequisitionForm.RF_PMS_OBJECT_TYPE_ID:
			case RequisitionForm.RF_ELECTRICAL_OBJECT_TYPE_ID:
			case RequisitionForm.RF_CM_OBJECT_TYPE_ID:
			case RequisitionForm.RF_ADMIN_OBJECT_TYPE_ID:
			case RequisitionForm.RF_MOTORPOOL_OBJECT_TYPE_ID:
			case RequisitionForm.RF_OIL_OBJECT_TYPE_ID:
			case RequisitionForm.RF_SUBCON_OBJECT_TYPE_ID:
			case RequisitionForm.RF_PAKYAWAN_OBJECT_TYPE_ID:
				return requisitionFormDao.getByEbObjectId(ebObjectId);
			case RequisitionFormItem.OBJECT_TYPE_ID:
				return requisitionFormItemDao.getByEbObjectId(ebObjectId);
			case ReferenceDocument.OBJECT_TYPE_ID:
				return referenceDocumentDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		RequisitionForm requisitionForm = requisitionFormDao.getByEbObjectId(ebObjectId);
		Integer pId = requisitionForm.getId();
		Integer classificationId = requisitionForm.getRequisitionClassificationId();
		FormProperty property = workflowHandler.getProperty(requisitionForm.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = requisitionForm.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = ((classificationId != null && classificationId.intValue() == RequisitionClassification.RC_PURCHASE_REQUISITION)
				? "Purchase Requisition" : "Material Requisition") + " " + requisitionForm.getSequenceNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + DateUtil.formatDate(requisitionForm.getDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	/**
	 * Get the remaining quantity of the referenced Requisition Form.
	 * @param referenceObjectId The eb object id of the referenced Requisition Form.
	 * @param wsId The withdrawal slip id of the WS form that references the requisition form.
	 * @param trId The transfer receipt id of the TR form that references the requisition form.
	 * @param prId The purchase requisition id of the PR form that references the requisition form.
	 * @return The remaining quantity of the referenced requisition form.
	 */
	public double getRemainingRFQty(Integer referenceObjectId, Integer wsId,
			Integer prId, boolean isExcludePr) {
		return requisitionFormDao.getRemainingQuantity(referenceObjectId, wsId, prId, isExcludePr);
	}

	/**
	 * Get the list of requisition forms by job order id
	 * @param jobOrderId The job order id
	 * @param isComplete True if complete status only, else false
	 * @return The list of requisition forms
	 */
	public List<RequisitionForm> getRequisitionFormsByJobOrderId(Integer jobOrderId, Boolean isComplete) {
		return requisitionFormDao.getRequisitionFormsByJobOrderId(jobOrderId, isComplete);
	}

	@Override
	public Integer getObjectTypeId(OODomain ooDomain) {
		RequisitionForm requisitionForm = (RequisitionForm) ooDomain;
		return requisitionForm.getObjectTypeId();
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		RequisitionForm rf = requisitionFormDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			StringBuilder strErrMsg = null;
			StringBuilder strRfErrMsg = new StringBuilder();
			Boolean isUsedRf = false;
			if(rf.getRequisitionClassificationId() == null ||
					rf.getRequisitionClassificationId() != RequisitionClassification.RC_PURCHASE_REQUISITION) {
				List<UsedRequisitionFormDto> usedRFs = requisitionFormDao.getUsedRequisitionForm(rf.getId());
				if(usedRFs != null && !usedRFs.isEmpty()) {
					for(UsedRequisitionFormDto usedRf : usedRFs) {
						isUsedRf = true;
						strRfErrMsg.append("<br> " + usedRf.getRefererForm() + " - " + usedRf.getRefererFormSequenceNo());
					}
				}
				List<WithdrawalSlip> withdrawalSlips = jyeiWsService.getWsByEbObjectId(rf.getEbObjectId(), false);
				if(withdrawalSlips != null && !withdrawalSlips.isEmpty()) {
					for(WithdrawalSlip ws : withdrawalSlips) {
						isUsedRf = true;
						strRfErrMsg.append("<br> " + "Withdrawal Slip - " + ws.getWsNumber());
					}
				}
				if (isUsedRf) {
					strErrMsg = new StringBuilder("Material Requisition form was used as reference in " + strRfErrMsg);
				}
			} else {
				UsedRequisitionFormDto usedPR = requisitionFormDao.getUsedPR(rf.getId());
				if(usedPR != null) { // Validate form cancellation when used Purchase Requisition Form has been used as reference in Purchase Order.
					strErrMsg = new StringBuilder("Purchase requisition form was used as reference in "
							+ usedPR.getRefererForm() +" - " + usedPR.getRefererFormSequenceNo() + ".");
				}
			}
			if(strErrMsg != null) {
				bindingResult.reject("workflowMessage", strErrMsg.toString());
				currentWorkflowLog.setWorkflowMessage(strErrMsg.toString());
			}
		}
	}

	/**
	 * Get Purchase Requisition by reference ID
	 * @param refReqFormId the reference requisition form
	 * @return list of requisition form
	 */
	public List<RequisitionForm> getReqFormByRefId(Integer refReqFormId) {
		return requisitionFormDao.getReqFormByRefId(refReqFormId);
	}

	/**
	 * Get the {@link WorkOrder} reference for {@link RequisitionForm} with {@link RequisitionType} 5.
	 * {@link RequisitionType} 5 = Construction Material.
	 * @param companyId The {@link Company} id.
	 * @param woNumber The {@link WorkOrder} sequence number.
	 * @param arCustomerId The {@link ArCustomer} id.
	 * @param arCustomerAcctId The {@link ArCustomerAccount} id.
	 * @param statusId The status id.
	 * @param pageSetting The {@link PageSetting}.
	 * @return List of {@link WorkOrder}.
	 */
	public Page<WorkOrder> getWorkOrderReferences(Integer companyId, Integer woNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, Integer pageNumber) {
		Page<WorkOrder> workOrders = workOrderDao.getMrWoReferences(companyId, woNumber, arCustomerId, arCustomerAcctId,
				statusId, new PageSetting(pageNumber));
		for (WorkOrder wo : workOrders.getData()) {
			wo.setArCustomer(arCustomerService.getCustomer(wo.getArCustomerId()));
			wo.setArCustomerAccount(arCustomerAcctService.getAccount(wo.getArCustomerAcctId()));
		}
		return workOrders;
	}

	/**
	 * Convert {@link WorkOrder} to {@link RequisitionForm}.
	 * @param workOrderId The {@link WorkOrder} id.
	 * @return The {@link RequisitionFormItem}.
	 */
	public RequisitionForm convertWoToRf(Integer workOrderId) {
		RequisitionForm rf = new RequisitionForm();
		rf.setWorkOrderId(workOrderId);
		rf.setRequisitionTypeId(RequisitionType.RT_ADMIN);
		rf.setWoNumber(workOrderDao.get(workOrderId).getSequenceNumber().toString());
		List<WorkOrderItem> wois = woItemDao.getAllByRefId(WorkOrderLine.FIELD.workOrderId.name(), workOrderId);
		List<RequisitionFormItem> rfis = new ArrayList<RequisitionFormItem>();
		RequisitionFormItem rfi = null;
		for(WorkOrderItem woi : wois) {
			rfi = new RequisitionFormItem();
			rfi.setRefenceObjectId(woi.getEbObjectId());
			rfi.setWarehouseId(woi.getEbObjectId());
			Item item = itemService.getItem(woi.getItemId());
			rfi.setItem(item);
			rfi.setItemId(woi.getItemId());
			rfi.setStockCode(item.getStockCode());
			rfi.setQuantity(woItemDao.getRemainingQty(workOrderId, woi.getItemId(), null));
			rfi.setExistingStocks(itemService.getItemExistingStocks(woi.getItemId(), -1, new Date()));
			rfis.add(rfi);
		}
		rf.setRequisitionFormItems(rfis);
		return rf;
	}

	/**
	 * Get the remaining quantity of the referenced Purchase Requisition Form.
	 * @param referenceObjectId The eb object id of the referenced Purchase Requisition Form.
	 * @param poId The purchase order id.
	 * @return The remaining quantity.
	 */
	public double getRemainingRFQty(Integer referenceObjectId, Integer poId) {
		return requisitionFormDao.getRemainingPRQuantity(referenceObjectId, poId);
	}
}
