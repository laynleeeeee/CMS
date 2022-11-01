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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ArLineSetupDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.ItemDiscountTypeDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.RequisitionFormDao;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.dao.WorkOrderDao;
import eulap.eb.dao.WorkOrderInstructionDao;
import eulap.eb.dao.WorkOrderItemDao;
import eulap.eb.dao.WorkOrderLineDao;
import eulap.eb.dao.WorkOrderPurchasedItemDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.SalesOrderItem;
import eulap.eb.domain.hibernate.SalesOrderLine;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.domain.hibernate.WorkOrderInstruction;
import eulap.eb.domain.hibernate.WorkOrderItem;
import eulap.eb.domain.hibernate.WorkOrderLine;
import eulap.eb.domain.hibernate.WorkOrderPurchasedItem;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;
import eulap.eb.web.dto.SubWorkOrderDto;
import eulap.eb.web.dto.WorkOrderDto;

/**
 * Service class that will handle requests for {@link WorkOrder}

 */

@Service
public class WorkOrderService extends BaseWorkflowService {
	@Autowired
	private WorkOrderDao workOrderDao;
	@Autowired
	private WorkOrderLineDao workOrderLineDao;
	@Autowired
	private WorkOrderInstructionDao woInstructionDao;
	@Autowired
	private WorkOrderItemDao workOrderItemDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private SalesOrderDao salesOrderDao;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private SalesOrderService salesOrderService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ArLineSetupDao arLineSetupDao;
	@Autowired
	private UnitMeasurementDao uomDao;
	@Autowired
	private ItemDiscountTypeDao itemDiscountTypeDao;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private ItemService itemService;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private WorkOrderPurchasedItemDao wopDao;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private RequisitionFormDao rfDao;

	/**
	 * Get the list of companies
	 * @param user The current logged user object
	 * @param companyId The company id
	 * @return The list of companies
	 */
	public List<Company> getCompanies(User user, int companyId) {
		return (List<Company>) companyService.getCompaniesWithInactives(user, companyId);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		WorkOrder workOrder = (WorkOrder) form;
		boolean isNew = workOrder.getId() == 0;
		AuditUtil.addAudit(workOrder, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			workOrder.setSequenceNumber(workOrderDao.generateSequenceNo(workOrder.getCompanyId()));
		} else {
			List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(
					workOrder.getEbObjectId(), WorkOrder.WO_SERIAL_ITEM_OR_TYPE_ID);
			serialItemService.setSerialNumberToInactive(savedSerialItems);
		}
		workOrderDao.saveOrUpdate(workOrder);

		List<WorkOrderLine> workOrderLines = workOrder.getWoLines();
		for (WorkOrderLine wol : workOrderLines) {
			wol.setWorkOrderId(workOrder.getId());
			workOrderLineDao.saveOrUpdate(wol);
		}

		//Delete saved WorkOrderItem.
		List<WorkOrderItem> savedWoItems = workOrderItemDao.getWorkOrderItems(workOrder.getId());
		if(savedWoItems != null && !savedWoItems.isEmpty()) {
			for(WorkOrderItem woi : savedWoItems) {
				workOrderItemDao.delete(woi);
			}
		}
		List<WorkOrderItem> woItems = workOrder.getWoItems();
		for (WorkOrderItem woi : woItems) {
			woi.setWorkOrderId(workOrder.getId());
			workOrderItemDao.save(woi);
		}

		//Delete saved WorkOrderInstruction.
		List<WorkOrderInstruction> savedWoInstructions = woInstructionDao.getWoInstructions(workOrder.getId());
		if(savedWoInstructions != null && !savedWoInstructions.isEmpty()) {
			for(WorkOrderInstruction woi : savedWoInstructions) {
				woInstructionDao.delete(woi);
			}
		}
		List<WorkOrderInstruction> woInstructions = workOrder.getWoInstructions();
		for (WorkOrderInstruction woi : woInstructions) {
			woi.setWorkOrderId(workOrder.getId());
			woInstructionDao.save(woi);
		}

		List<WorkOrderPurchasedItem> wopItems = workOrder.getWoPurchasedItems();
		for(WorkOrderPurchasedItem wopItem : wopItems) {
			wopItem.setWorkOrderId(workOrder.getId());
			wopDao.saveOrUpdate(wopItem);
		}

		saveRefDocuments(workOrder);
	}

	private void saveRefDocuments(WorkOrder workOrder) {
		// Delete the saved referenced documents.
		List<ReferenceDocument> toBeDeleteRefDocs = refDocumentService.getReferenceDocuments(workOrder.getEbObjectId());
		for (ReferenceDocument referenceDocument : toBeDeleteRefDocs) {
			referenceDocumentDao.delete(referenceDocument);
		}

		// Saving Reference documents
		List<ReferenceDocument> refDocuments = workOrder.getReferenceDocuments();
		if (refDocuments != null && !refDocuments.isEmpty()) {
			for (ReferenceDocument referenceDocument : refDocuments) {
				referenceDocumentDao.save(referenceDocument);
			}
		}
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return workOrderDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return workOrderDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		WorkOrder workOrder = workOrderDao.getByEbObjectId(ebObjectId);
		int pId = workOrder.getId();
		FormProperty property = workflowHandler.getProperty(workOrder.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = workOrder.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Work Order - " + workOrder.getSequenceNumber();
		shortDescription = new StringBuffer(title + " " + workOrder.getArCustomer().getName());
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus, shortDescription.toString(),
				fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case WorkOrder.OBJECT_TYPE_ID:
				return workOrderDao.getByEbObjectId(ebObjectId);
			case WorkOrderLine.OBJECT_TYPE_ID:
				return workOrderLineDao.getByEbObjectId(ebObjectId);
			case WorkOrderItem.OBJECT_TYPE_ID:
				return workOrderItemDao.getByEbObjectId(ebObjectId);
			case WorkOrderInstruction.OBJECT_TYPE_ID:
				return woInstructionDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the paged list of sales order for SO reference
	 * @param companyId The company id
	 * @param soNumber The sales order sequence number
	 * @param arCustomerId The AR customer id
	 * @param arCustomerAcctId The AR customer account id id
	 * @param statusId The reference form status id (ALL, USED, and UNUSED)
	 * @param pageNumber The page number
	 * @return The paged list of sales order for ATW reference
	 */
	public Page<SalesOrder> getSaleOrderForms(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, Integer pageNumber) {
		Page<SalesOrder> salesOrders = salesOrderDao.getSOServiceReferences(companyId, soNumber, arCustomerId, arCustomerAcctId,
				statusId, new PageSetting(pageNumber));
		for (SalesOrder so : salesOrders.getData()) {
			so.setArCustomer(arCustomerService.getCustomer(so.getArCustomerId()));
			so.setArCustomerAccount(arCustomerAcctService.getAccount(so.getArCustomerAcctId()));
		}
		return salesOrders;
	}

	/**
	 * Get work order object by reference sales order
	 * @param refSalesOrderId The reference sales order id
	 * @return The work order object
	 */
	public WorkOrder convertSOtoWO(Integer refSalesOrderId) {
		WorkOrder workOrder = new WorkOrder();
		SalesOrder salesOrder = salesOrderService.getSalesOrder(refSalesOrderId, true);
		int companyId = salesOrder.getCompanyId();
		if (salesOrder != null) {
			workOrder.setSalesOrderId(salesOrder.getId());
			workOrder.setSoNumber(salesOrder.getSequenceNumber().toString());
			workOrder.setCompanyId(companyId);
			workOrder.setCompany(salesOrder.getCompany());
			workOrder.setArCustomerId(salesOrder.getArCustomerId());
			workOrder.setArCustomer(salesOrder.getArCustomer());
			workOrder.setArCustomerAcctId(salesOrder.getArCustomerAcctId());
			workOrder.setArCustomerAccount(salesOrder.getArCustomerAccount());

			List<WorkOrderLine> workOrderLines = new ArrayList<WorkOrderLine>();
			WorkOrderLine wol = null;
			List<SalesOrderLine> salesOrderLines = salesOrder.getSoLines();
			for (SalesOrderLine sol : salesOrderLines) {
				wol = new WorkOrderLine();
				wol.setRefenceObjectId(sol.getEbObjectId());
//				wol.setArLineSetupId(sol.getArLineSetupId());
//				wol.setArLineSetupName(sol.getArLineSetup().getName());
				wol.setQuantity(sol.getQuantity());
				wol.setUpAmount(sol.getUpAmount());
				wol.setDiscountTypeId(sol.getDiscountTypeId());
				wol.setDiscountValue(sol.getDiscountValue());
				wol.setDiscount(sol.getDiscount());
				wol.setTaxTypeId(sol.getTaxTypeId());
				wol.setVatAmount(sol.getVatAmount());
				Integer uomId = sol.getUnitOfMeasurementId();
				if (uomId != null) {
					wol.setUnitOfMeasurementId(uomId);
					wol.setUnitMeasurementName(sol.getUnitMeasurement().getName());
				}
				wol.setAmount(sol.getAmount());
				workOrderLines.add(wol);
				wol = null;
			}
			salesOrderLines = null;
			workOrder.setWoLines(workOrderLines);

			List<WorkOrderPurchasedItem> woPurchasedItems = new ArrayList<WorkOrderPurchasedItem>();
			Item item = null;
			WorkOrderPurchasedItem woPurchasedItem = null;
			for (SalesOrderItem soi : salesOrder.getSoItems()) {
				item = itemService.getItem(soi.getItemId());
					Double qty = workOrderDao.getPurchasedItemsQty(soi.getSalesOrderId(), soi.getItemId());
					woPurchasedItem = new WorkOrderPurchasedItem();
					woPurchasedItem.setItem(item);
					woPurchasedItem.setItemId(soi.getItemId());
					woPurchasedItem.setStockCode(item.getStockCode());
					woPurchasedItem.setQuantity(qty);
					woPurchasedItem.setRefenceObjectId(soi.getEbObjectId());
					woPurchasedItems.add(woPurchasedItem);
					woPurchasedItem = null;
				item = null;
			}
			workOrder.setWoPurchasedItems(woPurchasedItems);
		}
		return workOrder;
	}

	/**
	 * Validate the work order form
	 * @param workOrder The work order object
	 * @param errors The validation errors
	 */
	public void validateForm(WorkOrder workOrder, Errors errors) {
		ValidatorUtil.validateCompany(workOrder.getCompanyId(), companyService, errors, "companyId");

		Date date = workOrder.getDate();
		if (date == null) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("WorkOrderService.1"));
		} else if (!timePeriodService.isOpenDate(date)) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("WorkOrderService.2"));
		}

		Date targetEndDate = workOrder.getTargetEndDate();
		if (targetEndDate == null) {
			errors.rejectValue("targetEndDate", null, null, ValidatorMessages.getString("WorkOrderService.3"));
		} else if (!timePeriodService.isOpenDate(targetEndDate)) {
			errors.rejectValue("targetEndDate", null, null, ValidatorMessages.getString("WorkOrderService.4"));
		}

		if (workOrder.getSalesOrderId() == null) {
			errors.rejectValue("salesOrderId", null, null, ValidatorMessages.getString("WorkOrderService.5"));
		}

		Integer arCustomerId = workOrder.getArCustomerId();
		if (arCustomerId == null) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("WorkOrderService.6"));
		} else if (!arCustomerService.getCustomer(arCustomerId).isActive()) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("WorkOrderService.7"));
		}

		Integer arCustomerAcctId = workOrder.getArCustomerAcctId();
		if (arCustomerAcctId == null) {
			errors.rejectValue("arCustomerAcctId", null, null, ValidatorMessages.getString("WorkOrderService.8"));
		} else if (!arCustomerAcctService.getAccount(arCustomerAcctId).isActive()) {
			errors.rejectValue("arCustomerAcctId", null, null, ValidatorMessages.getString("WorkOrderService.9"));
		}

		String workDesc = workOrder.getWorkDescription();
		if (workDesc == null || workDesc.isEmpty()) {
			errors.rejectValue("workDescription", null, null, ValidatorMessages.getString("WorkOrderService.10"));
		}

		List<WorkOrderInstruction> woInstructions = workOrder.getWoInstructions();
		if (woInstructions == null || woInstructions.isEmpty()) {
			errors.rejectValue("woInstructions", null, null, ValidatorMessages.getString("WorkOrderService.11"));
		}
	}

	/**
	 * Get work order DTO for form printout
	 * @param pId The work order id
	 * @return The work order DTO for form printout
	 */
	public List<WorkOrderDto> getWorkOrderDtos(Integer pId) {
		List<WorkOrderDto> workOrderDtos = new ArrayList<WorkOrderDto>();
		WorkOrderDto workOrderDto = new WorkOrderDto();
		// Set main work order
		List<WorkOrder> mainWorkOrder = new ArrayList<WorkOrder>();
		mainWorkOrder.add(getWorkOrder(pId, false, false));
		workOrderDto.setMainWorkOrder(mainWorkOrder);
		// Set sub work order
		List<WorkOrder> subWorkOrders = new ArrayList<WorkOrder>();
		List<WorkOrder> savedSubWorkOrders = workOrderDao.getSubWorkOrders(pId);
		for (WorkOrder workOrder : savedSubWorkOrders) {
			subWorkOrders.add(getWorkOrder(workOrder.getId(), false, false));
		}
		workOrderDto.setSubWorkOrders(subWorkOrders);
		workOrderDtos.add(workOrderDto);
		return workOrderDtos;
	}

	/**
	 * Get the work order object
	 * @param pId The work order id
	 * @param isHeaderOnly True if retrieve the object header details only, otherwise false
	 * @return The work order object
	 */
	public WorkOrder getWorkOrder(Integer pId, boolean isHeaderOnly, boolean isView) {
		WorkOrder workOrder = workOrderDao.get(pId);
		SalesOrder soReference = salesOrderService.getSalesOrder(workOrder.getSalesOrderId(), false);
		workOrder.setSoNumber(soReference.getSequenceNumber().toString());
		soReference = null;
		Integer refWorkOrderId = workOrder.getRefWorkOrderId();
		if (refWorkOrderId != null) {
			workOrder.setRefWoNumber(workOrderDao.get(refWorkOrderId).getSequenceNumber().toString());
		}
		if (!isHeaderOnly) {
			UnitMeasurement uom = null;
			ArLineSetup arLineSetup = null;
			EBObject ebObject = null;
			List<WorkOrderLine> woLines = workOrderLineDao.getAllByRefId(
					WorkOrderLine.FIELD.workOrderId.name(), pId);
			for (WorkOrderLine wol : woLines) {
				arLineSetup = arLineSetupDao.get(wol.getArLineSetupId());
				if (arLineSetup != null) {
					wol.setArLineSetupName(arLineSetup.getName());
				}
				if (wol.getUnitOfMeasurementId() != null) {
					uom = uomDao.get(wol.getUnitOfMeasurementId());
					wol.setUnitMeasurementName(uom.getName());
				}
				Integer itemDiscountTypeId = wol.getDiscountTypeId();
				if (itemDiscountTypeId != null) {
					wol.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
					itemDiscountTypeId = null;
				}
				ebObject = ooLinkHelper.getReferenceObject(wol.getEbObjectId(),
						WorkOrderLine.SO_LINE_WO_LINE_OR_TYPE_ID);
				if (ebObject != null) {
					wol.setRefenceObjectId(ebObject.getId());
					ebObject = null;
				}
			}
			workOrder.setWoLines(woLines);

			workOrder.setWoInstructions(woInstructionDao.getAllByRefId(
					WorkOrderLine.FIELD.workOrderId.name(), pId));

			List<WorkOrderItem> workOrderItems = workOrderItemDao.getAllByRefId(
					WorkOrderLine.FIELD.workOrderId.name(), pId);
			for (WorkOrderItem woi : workOrderItems) {
				woi.setStockCode(itemService.getItem(woi.getItemId()).getStockCode());
				woi.setExistingStocks(itemService.getItemExistingStocks(woi.getItemId(), -1, new Date()));
			}
			workOrder.setWoItems(workOrderItems);

			List<WorkOrderPurchasedItem> wopItems = wopDao.getAllByRefId(
					WorkOrderPurchasedItem.FIELD.workOrderId.name(), pId);
			for(WorkOrderPurchasedItem wopItem : wopItems) {
				wopItem.setStockCode(itemService.getItem(wopItem.getItemId()).getStockCode());
				wopItem.setExistingStocks(itemService.getItemExistingStocks(wopItem.getItemId(), -1, new Date()));
			}
			workOrder.setWoPurchasedItems(wopItems);
			workOrder.setReferenceDocuments(refDocumentService.getReferenceDocuments(workOrder.getEbObjectId()));
		}

		if (isView) {
			List<WorkOrder> subWorkOrders = workOrderDao.getSubWorkOrders(pId);
			List<SubWorkOrderDto> subWorkOrderDtos = new ArrayList<SubWorkOrderDto>();
			SubWorkOrderDto sDto = null;
			int workOrderId = 0;
			for (WorkOrder wo : subWorkOrders) {
				sDto = new SubWorkOrderDto();
				workOrderId = wo.getId();
				sDto.setWorkOrderId(workOrderId);
				sDto.setWoNumber("WO - "+wo.getSequenceNumber());
				sDto.setEditUri("/workOrder/form?pId="+workOrderId);
				subWorkOrderDtos.add(sDto);
			}
			workOrder.setSubWorkOrderDtos(subWorkOrderDtos);
		}
		return workOrder;
	}

	/**
	 * Retrieve the list of work order forms for general search
	 * @param searchCriteria The search criteria
	 * @return The list of work order forms for general search
	 */
	public List<FormSearchResult> retrieveForms(String searchCriteria) {
		Page<WorkOrder> workOrders = workOrderDao.retrieveWorkOrders(searchCriteria, new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		ArCustomer arCustomer = null;
		for (WorkOrder wo : workOrders.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String title = wo.getCompany().getCompanyCode() + " " + wo.getSequenceNumber();
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(wo.getDate())));
			arCustomer = arCustomerService.getCustomer(wo.getArCustomerId());
			properties.add(ResultProperty.getInstance("Customer", arCustomer.getName()));
			String status = formStatusDao.get(wo.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(wo.getId(), title, properties));
		}
		return result;
	}

	/**
	 * Get the paged list of work order for sub work order reference
	 * @param companyId The company id
	 * @param woNumber The work order number
	 * @param arCustomerId The customer id
	 * @param arCustomerAcctId The customer account id
	 * @param statusId The status id
	 * @param pageNumber The page number
	 * @return The paged list of work order for sub work order reference
	 */
	public Page<WorkOrder> getWorkOrderReferences(Integer companyId, Integer woNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, Integer pageNumber) {
		Page<WorkOrder> workOrders = workOrderDao.getWorkOrderReferences(companyId, woNumber, arCustomerId, arCustomerAcctId,
				statusId, new PageSetting(pageNumber));
		for (WorkOrder wo : workOrders.getData()) {
			wo.setArCustomer(arCustomerService.getCustomer(wo.getArCustomerId()));
			wo.setArCustomerAccount(arCustomerAcctService.getAccount(wo.getArCustomerAcctId()));
		}
		return workOrders;
	}

	/**
	 * Get the sub work order object
	 * @param refWorkOrderId The reference work order
	 * @return The sub work order object
	 */
	public WorkOrder setSubWorkOrder(Integer refWorkOrderId) {
		WorkOrder subWorkOrder = getWorkOrder(refWorkOrderId, false, false);
		subWorkOrder.setRefWoNumber(subWorkOrder.getSequenceNumber().toString());
		SalesOrder soReference = salesOrderService.getSalesOrder(subWorkOrder.getSalesOrderId(), false);
		subWorkOrder.setSoNumber(soReference.getSequenceNumber().toString());
		soReference = null;
		Date currentDate = new Date();
		subWorkOrder.setDate(currentDate);
		subWorkOrder.setTargetEndDate(currentDate);
		for (WorkOrderLine workOrderLine : subWorkOrder.getWoLines()) {
			workOrderLine.setId(0);
			workOrderLine.setWorkOrderId(0);
			workOrderLine.setRefenceObjectId(workOrderLine.getEbObjectId());
			workOrderLine.setEbObjectId(null);
		}
		subWorkOrder.setWoInstructions(new ArrayList<WorkOrderInstruction>());
		subWorkOrder.setWoItems(new ArrayList<WorkOrderItem>());
		subWorkOrder.setReferenceDocuments(new ArrayList<ReferenceDocument>());
		return subWorkOrder;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			WorkOrder workOrder = workOrderDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			StringBuffer errorMessage = null;
			if(workOrder != null) {
				Integer woId = workOrder.getId();
				if(workOrderDao.isUsedByRf(woId)) {
					List<RequisitionForm> rfs = rfDao.getReqFormByWoId(woId);
					if(!rfs.isEmpty()) {
						errorMessage = new StringBuffer("Work order form has been used by the ff forms:");
						for(RequisitionForm rf : rfs) {
							errorMessage.append("<br>RF-"+rf.getSequenceNumber());
						}
					}
				}
				if (errorMessage != null) {
					bindingResult.reject("workflowMessage", errorMessage.toString());
					currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
				}
			}
		}
	}
}
