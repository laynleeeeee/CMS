package eulap.eb.service;

import java.io.IOException;
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
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.common.util.TaxUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.dao.ApLineSetupDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.FleetProfileDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.PurchaseOrderLineDao;
import eulap.eb.dao.PurchaseRequisitionItemDao;
import eulap.eb.dao.RPurchaseOrderDao;
import eulap.eb.dao.RPurchaseOrderItemDao;
import eulap.eb.dao.RReceivingReportItemDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.RequisitionFormDao;
import eulap.eb.dao.SupplierAdvPaymentDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PurchaseOrderLine;
import eulap.eb.domain.hibernate.PurchaseRequisitionItem;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RPurchaseOrderItem;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.PRReferenceDto;
import eulap.eb.web.dto.Pr2PoDto;
import eulap.eb.web.dto.PurchaseOrderItemDto;

/**
 * Service class for Retail Purchase Order.

 *
 */
@Service
public class RPurchaseOrderService  extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(RPurchaseOrderService.class);
	@Autowired
	private RPurchaseOrderItemDao purchaseOrderItemDao;
	@Autowired
	private RPurchaseOrderDao purchaseOderDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private APInvoiceDao invoiceDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private RReceivingReportItemDao  rReceivingReportItemDao;
	@Autowired
	private EmployeeService eeService;
	@Autowired
	private RequisitionFormDao requisitionFormDao;
	@Autowired
	private FleetProfileDao fleetProfileDao;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private PurchaseRequisitionItemDao prItemDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ObjectToObjectDao o2oDao;
	@Autowired
	private PurchaseOrderLineDao poLineDao;
	@Autowired
	private ApLineSetupDao apLineSetupDao;
	@Autowired
	private UnitMeasurementDao unitMeasurementDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private ReferenceDocumentDao referenceDocDao;
	@Autowired
	private ApInvoiceLineDao apiLineDao;
	@Autowired
	private SupplierAdvPaymentDao sapDao;

	/**
	 * Get the Retail Purchase Order object.
	 * @param purchaseOrderId The unique id.
	 * @return The Retail Purchase order object.
	 */
	public RPurchaseOrder getRpurchaseOrder(int purchaseOrderId) throws IOException {
		logger.info("Retrieving the Retail PO using the id: "+purchaseOrderId);
		RPurchaseOrder po = getRPurchaseOrder(purchaseOrderId);
		int ebObjectId = po.getEbObjectId();
		List<ObjectToObject> oos = objectToObjectDao.getReferenceObjects(ebObjectId, RPurchaseOrder.PR_PO_OR_TYPE_ID);
		List<Integer> prObjIds = new ArrayList<>();
		for (ObjectToObject oo : oos) {
			prObjIds.add(oo.getFromObjectId());
		}
		Pr2PoDto pr2PoDto = convertPrToPo(prObjIds);
		po.setStrCustomerName(pr2PoDto.getProjectName());
		po.setStrFleetCode(pr2PoDto.getFleetProfileCode());
		String prRefObjIds = po.getPrReference();
		if (prRefObjIds != null && !prRefObjIds.isEmpty()) {
			String arr[] = prRefObjIds.split(";");
			String prReference = "";
			int count = 0;
			for (String str : arr) {
				Integer sequenceNo = requisitionFormDao.getByEbObjectId(Integer.parseInt(str)).getSequenceNumber();
				if(count == 0) {
					prReference += sequenceNo.toString();
				} else {
					prReference += ", " + sequenceNo.toString();
				}
				count++;
			}
			po.setStrPrReferences(prReference);
		}
		if (po.getRequestedById() != null) {
			po.setEmployeeName(eeService.getEmployee(po.getRequestedById()).getFullName());
		}
		double rate = po.getCurrencyRateValue();
		double grandTotal = 0;
		for (RPurchaseOrderItem poi : po.getrPoItems()) {
			if (po.getPrReference() != null) {
				EBObject refSQIObjId = o2oDao.getOtherReference(poi.getEbObjectId(),
						RPurchaseOrderItem.PRI_POI_OR_TYPE_ID);
				if (refSQIObjId != null) {
					PurchaseRequisitionItem pri = prItemDao.getByEbObjectId(refSQIObjId.getId());
					poi.setRefenceObjectId(pri.getEbObjectId());
				}
			}
			Double unitCost = poi.getUnitCost();
			if(TaxUtil.isVatable(poi.getTaxTypeId())) {
				double vatQuantity = NumberFormatUtil.divideWFP(poi.getVatAmount(),  poi.getQuantity());
				unitCost = unitCost + vatQuantity;
			}
			poi.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, rate));
			poi.setStockCode(poi.getItem().getStockCode());
			poi.setVatAmount(CurrencyUtil.convertMonetaryValues(poi.getVatAmount(), rate));
			grandTotal += (NumberFormatUtil.multiplyWFP(poi.getQuantity() , poi.getUnitCost()));
		}
		List<PurchaseOrderLine> savedPOLines = poLineDao.getAllByRefId(
				PurchaseOrderLine.FIELD.purchaseOrderId.name(), po.getId());
		for (PurchaseOrderLine pol : savedPOLines) {
			pol.setApLineSetupName(apLineSetupDao.get(pol.getApLineSetupId()).getName());
			Integer uomId = pol.getUnitOfMeasurementId();
			if (uomId != null) {
				pol.setUnitMeasurementName(unitMeasurementDao.get(uomId).getName());
			}
			pol.setUpAmount(CurrencyUtil.convertMonetaryValues(pol.getUpAmount(), rate));
			pol.setVatAmount(CurrencyUtil.convertMonetaryValues(pol.getVatAmount(), rate));
			double amount = CurrencyUtil.convertMonetaryValues(pol.getAmount(), rate, true);
			pol.setAmount(amount);
			grandTotal += amount;
		}
		po.setGrandTotal(grandTotal);
		po.setPoLines(savedPOLines);
		po.setReferenceDocuments(refDocumentService.processReferenceDocs(po.getEbObjectId()));
		return po;
	}

	/**
	 * Get the purchase order object only
	 * @param purchaseOrderId The purchase order id
	 * @return The purchase order object
	 */
	public RPurchaseOrder getRPurchaseOrder(int purchaseOrderId) {
		return purchaseOderDao.get(purchaseOrderId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		logger.info("Retrieving the form workflow of purchase order: "+id);
		return purchaseOderDao.get(id).getFormWorkflow();
	}

	/**
	 * Generate the next available PO Number.
	 * @param companyId The id of the company.
	 * @return The next available PO Number.
	 */
	public int generatePONumber(int companyId) {
		logger.info("Generating the PO Number of company: "+companyId);
		int maxPONumber = purchaseOderDao.getMaxPONumber(companyId, null);
		logger.debug("The current maximum PO number "+maxPONumber);
		logger.info("The generated available PO Number: "+maxPONumber);
		return maxPONumber;
	}

	/**
	 * Generate the next available PO number.
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @return The next available PO number.
	 */
	public int generatePONumber(int companyId, Integer divisionId) {
		logger.info("Generating the PO Number of company: "+companyId);
		int maxPONumber = purchaseOderDao.getMaxPONumber(companyId, divisionId);
		logger.debug("The current maximum PO number "+maxPONumber);
		logger.info("The generated available PO Number: "+maxPONumber);
		return maxPONumber;
	}

	/**
	 * Get the list of Purchase Order Items with existing stocks.
	 * @param rPurchaseOrderId The unique id of the PO.
	 * @return The list of PO Items.
	 */
	public List<RPurchaseOrderItem> getPOItems(RPurchaseOrder po) {
		int purchaseId = po.getId();
		logger.info("Retrieve the list of items for PO pid: "+purchaseId);
		List<RPurchaseOrderItem> items = purchaseOrderItemDao.getPOItems(purchaseId);
		double rowAmount = 0;
		double rate = po.getCurrencyRateValue();
		for (RPurchaseOrderItem poItem : items) {
			Double unitCost = poItem.getUnitCost();
			if(TaxUtil.isVatable(poItem.getTaxTypeId())) {
				double vatQuantity = NumberFormatUtil.divideWFP(poItem.getVatAmount(), poItem.getQuantity());
				unitCost = unitCost + vatQuantity;
			}
			poItem.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, rate));
			poItem.setVatAmount(CurrencyUtil.convertMonetaryValues(poItem.getVatAmount(), rate));
			rowAmount = (NumberFormatUtil.multiplyWFP(poItem.getUnitCost(), poItem.getQuantity())) + poItem.getVatAmount();
			poItem.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(rowAmount));
			Item item = itemService.getRetailItem(poItem.getItem().getStockCode(), po.getCompanyId(), null);
			if(item != null) {
				poItem.setItem(item);
			}
		}
		return items;
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		RPurchaseOrder rPurchaseOrder = (RPurchaseOrder) form;
		boolean isNew = rPurchaseOrder.getId() == 0;
		if (isNew) {
			rPurchaseOrder.setPoNumber(generatePONumber(rPurchaseOrder.getCompanyId(),
					rPurchaseOrder.getDivisionId()));
		}
	}

	/**
	 * Save the Retail - Purchase Order.
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		RPurchaseOrder rPurchaseOrder = (RPurchaseOrder) form;
		logger.info("Saving the purchase order");
		boolean isNew = rPurchaseOrder.getId() == 0;
		AuditUtil.addAudit(rPurchaseOrder, new Audit(user.getId(), isNew, new Date()));
		Integer poEbObjectId = rPurchaseOrder.getEbObjectId();
		if (isNew) {
			String refObjectIds = rPurchaseOrder.getPrReference();
			if (refObjectIds != null && !refObjectIds.isEmpty()) {
				saveHeader2HeaderReferences(user, refObjectIds,
						poEbObjectId, RPurchaseOrder.PR_PO_OR_TYPE_ID, new Date());
			}
		} else {
			RPurchaseOrder savedPO = purchaseOderDao.get(rPurchaseOrder.getId());
			DateUtil.setCreatedDate(rPurchaseOrder, savedPO.getCreatedDate());
			savedPO = null;
		}
		String remarks = rPurchaseOrder.getRemarks();
		if (remarks != null) {
			rPurchaseOrder.setRemarks(StringFormatUtil.removeExtraWhiteSpaces(remarks));
		}
		String requesterName = rPurchaseOrder.getRequesterName();
		if (requesterName != null) {
			rPurchaseOrder.setRequesterName(StringFormatUtil.removeExtraWhiteSpaces(requesterName));
		}
		double currencyRate = rPurchaseOrder.getCurrencyRateValue() > 0 ? rPurchaseOrder.getCurrencyRateValue() : 1;
		rPurchaseOrder.setCurrencyRateValue(currencyRate);
		purchaseOderDao.saveOrUpdate(rPurchaseOrder);
		logger.info("Successfully saved purchase order id "+rPurchaseOrder.getId());
		List<Integer> toBeDeletedPoItemIds = new ArrayList<Integer>();
		List<RPurchaseOrderItem> rPOItems = null;
		Integer purchaseOrderId = rPurchaseOrder.getId();
		if (!isNew) {
			logger.debug("Deleting the saved purchase order items and lines.");
			rPOItems = getPOItems(rPurchaseOrder);
			if (!rPOItems.isEmpty()) {
				for (RPurchaseOrderItem item : rPOItems) {
					toBeDeletedPoItemIds.add(item.getId());
				}
				purchaseOrderItemDao.delete(toBeDeletedPoItemIds);
				logger.trace("Deleted "+toBeDeletedPoItemIds.size()+" purchase order items.");
				logger.debug("Successfully deleted items of purchase order id "+purchaseOrderId);
			}

			List<PurchaseOrderLine> savedPOLines = poLineDao.getAllByRefId(
					PurchaseOrderLine.FIELD.purchaseOrderId.name(), rPurchaseOrder.getId());
			for (PurchaseOrderLine pol : savedPOLines) {
				poLineDao.delete(pol);
			}
		}
		//Save items
		savePoItems(rPurchaseOrder, currencyRate);

		//Save purchase order lines.
		savePoLines(rPurchaseOrder, currencyRate);

		refDocumentService.saveReferenceDocuments(user, isNew, rPurchaseOrder.getEbObjectId(),
				rPurchaseOrder.getReferenceDocuments(), true);

		logger.warn("=====>>> Freeing up the memory allocation");
		toBeDeletedPoItemIds = null;
		
		rPOItems = null;
		remarks = null;
	}

	private void savePoLines(RPurchaseOrder rPurchaseOrder, double currencyRate) {
		List<PurchaseOrderLine> poLines = rPurchaseOrder.getPoLines();
		for (PurchaseOrderLine pol : poLines) {
			pol.setPurchaseOrderId(rPurchaseOrder.getId());
			OtherChargeUtil.recomputeOCCosts(pol, null, currencyRate);
			poLineDao.save(pol);
		}
	}

	private void savePoItems(RPurchaseOrder rPurchaseOrder, double currencyRate) {
		List<Domain> toBeSavedPOItems = new ArrayList<Domain>();
		List<RPurchaseOrderItem> rPOItems = rPurchaseOrder.getrPoItems();
		logger.trace("Saving "+rPOItems.size()+" purchase order items");
		for (RPurchaseOrderItem poi : rPOItems) {
			poi.setrPurchaseOrderId(rPurchaseOrder.getId());
			double quantity = poi.getQuantity();
			double vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(poi.getVatAmount() != null ? poi.getVatAmount() : 0);
			double vatQuantity = NumberFormatUtil.divideWFP(vatAmount, quantity, 12);
			double unitCost = poi.getUnitCost() != null ? poi.getUnitCost() : 0;
			poi.setUnitCost(CurrencyUtil.convertAmountToPhpRate((unitCost-vatQuantity), currencyRate));
			poi.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			toBeSavedPOItems.add(poi);
		}
		purchaseOrderItemDao.batchSave(toBeSavedPOItems);
		logger.debug("Successfully saved "+toBeSavedPOItems.size()+" purchase order items");
		toBeSavedPOItems = null;
	}

	/**
	 * Get the list of purchase orders.
	 * @param companyId The company id
	 * @param user The current logged user.
	 * @param supplierId The supplier id
	 * @param poNumber The purchase order number
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param status Used, UnUsed and All
	 * @param pageNumber The paged number
	 * @return The page result
	 */
	public Page<RPurchaseOrder> getPurchaseOrders(User user, Integer companyId, Integer supplierId,
			String poNumber, Date dateFrom, Date dateTo, Integer status, int pageNumber) {
		return purchaseOderDao.getPurchaseOrders(user, companyId, supplierId, poNumber, dateFrom, dateTo, status, new PageSetting(pageNumber));
	}

	/**
	 * Get the list of purchase orders.
	 * @param companyId The company id
	 * @param divisionId The division id.
	 * @param user The current logged user.
	 * @param supplierId The supplier id
	 * @param poNumber The purchase order number
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param status Used, UnUsed and All
	 * @param pageNumber The paged number
	 * @return The page result
	 */
	public Page<RPurchaseOrder> getPurchaseOrders(User user, Integer companyId, Integer divisionId, Integer supplierId,
			String poNumber, String bmsNumber, Date dateFrom, Date dateTo, Integer status, int pageNumber) {
		return purchaseOderDao.getPurchaseOrders(user, companyId, divisionId, supplierId, poNumber, bmsNumber, dateFrom, dateTo, status, new PageSetting(pageNumber));
	}

	/**
	 * Get the purcahse order by eb object id.
	 * @param ebObjectId The eb object id.
	 * @return The purchase order.
	 */
	public RPurchaseOrderItem getPOByInvoiceEBObject(Integer ebObjectId) {
		logger.info("Getting the list of refence document by ebObject id.");
		//Get object to object by ebObjectId of the PO table.
		List<ObjectToObject> objectToObjects = objectToObjectDao.getAllByRefId("toObjectId", ebObjectId);
		RPurchaseOrderItem poItem = new RPurchaseOrderItem();
		EBObject ebObject = new EBObject();
		for (ObjectToObject objectToObject : objectToObjects) {
			ebObject = ebObjectDao.get(objectToObject.getFromObjectId());
			if(ebObject.getObjectTypeId() == RPurchaseOrderItem.PO_ITEM_OBJECT_TYPE_ID){
				poItem = purchaseOrderItemDao.getAllByRefId("ebObjectId",
						objectToObject.getFromObjectId()).iterator().next();
			}
		}
		return poItem;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		RPurchaseOrder po = purchaseOderDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		logger.debug("Status of RPurchase Order was updated to: "+currentWorkflowLog.getFormStatusId());
		List<RPurchaseOrderItem> purchaseOrderItems = purchaseOrderItemDao.getPOItems(po.getId());
		List<PurchaseOrderLine> poLines = poLineDao.getPOLines(po.getId());
		if (po.getEbObjectId() != null) {
			if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
				StringBuffer errorMessage = null;
				APInvoice invoice = null;
				List<RReceivingReportItem> rReceivingReportItems = null;
				for (RPurchaseOrderItem rPurchaseOrderItem : purchaseOrderItems) {
					logger.info("Cancelling TO ");
					List<ObjectToObject> objectToObjects =
							objectToObjectDao.getAllByRefId("fromObjectId", rPurchaseOrderItem.getEbObjectId());
					rReceivingReportItems = new ArrayList<>();
					for (ObjectToObject objectToObject : objectToObjects) {
						//Get the PO by object to object toId.
						rReceivingReportItems.addAll(rReceivingReportItemDao.getAllByRefId("ebObjectId",
								objectToObject.getToObjectId()));
					}
					StringBuffer rrNos = new StringBuffer("");
					for (RReceivingReportItem rri : rReceivingReportItems) {
						invoice = invoiceDao.get(rri.getApInvoiceId());
						if (invoice.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID
								&& invoice.getSequenceNumber() != null){
							rrNos.append("<br>RR-"+invoice.getSequenceNumber());
							break;
						}
					}
					if (!rrNos.toString().isEmpty()) {
						errorMessage = new StringBuffer("PO is used as reference in:");
						errorMessage.append(rrNos.toString());
					}
				}
				APInvoice api = null;
				List<ApInvoiceLine> apiLines = null;
				for (PurchaseOrderLine pol : poLines) {
					List<ObjectToObject> otos = objectToObjectDao.getAllByRefId("fromObjectId", pol.getEbObjectId());
					apiLines = new ArrayList<>();
					for (ObjectToObject oto : otos) {
						//Get the PO by object to object toId.
						apiLines.addAll(apiLineDao.getAllByRefId("ebObjectId", oto.getToObjectId()));
					}
					if (errorMessage == null) {
						StringBuffer rrNos = new StringBuffer("");
						for (ApInvoiceLine apiLine : apiLines) {
							api = invoiceDao.get(apiLine.getApInvoiceId());
							if (api.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID
									&& api.getSequenceNumber() != null) {
								rrNos.append("<br>RR-"+api.getSequenceNumber());
							}
						}
						if (!rrNos.toString().isEmpty()) {
							errorMessage = new StringBuffer("PO is used as reference in:");
							errorMessage.append(rrNos.toString());
						}
					}
				}
				if (sapDao.isUsedBySAP(po.getId())) {
					List<SupplierAdvancePayment> saps = sapDao.getPoAdvPayments(null, po.getId());
					if (!saps.isEmpty()) {
						errorMessage = new StringBuffer("PO is used as reference in:");
						for (SupplierAdvancePayment sap : saps) {
							errorMessage.append("<br>SAP-"+sap.getSequenceNumber());
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

	/**
	 * Get the PO Remaining QTY by reference object id.
	 * @param refenceObjectId The reference object id.
	 * @return The remaining quantity.
	 */
	public Double getPORemainingQty(Integer refenceObjectId, Integer itemId) {
		return purchaseOrderItemDao.getRemainingQty(refenceObjectId, itemId);
	}

	/**
	 * Get the latest Unit Cost
	 * @param itemId The item id
	 * @param supplierAcctId The supplier account id
	 * @return the latest unit cost
	 */
	public Double getLatestUCPerSupplierAcct(int itemId, int supplierAcctId){
		return purchaseOderDao.getLatestUCPerSupplierAndItem(itemId, supplierAcctId);
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return purchaseOderDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		RPurchaseOrder purchaseOrder = purchaseOderDao.getByEbObjectId(ebObjectId);
		Integer pId = purchaseOrder.getId();
		FormProperty property = workflowHandler.getProperty(purchaseOrder.getWorkflowName(), user);

		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printoutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = purchaseOrder.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Purchase Order - " + purchaseOrder.getPoNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + purchaseOrder.getSupplier().getName())
				.append(" " + purchaseOrder.getSupplierAccount().getName())
				.append("<b> PO DATE : </b>" + DateUtil.formatDate(purchaseOrder.getPoDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printoutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case RPurchaseOrder.OBJECT_TYPE_ID:
				return purchaseOderDao.getByEbObjectId(ebObjectId);
			case RPurchaseOrderItem.PO_ITEM_OBJECT_TYPE_ID:
				return purchaseOrderItemDao.getByEbObjectId(ebObjectId);
			case ReferenceDocument.OBJECT_TYPE_ID:
				return referenceDocDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the paged list of requisition forms available for references.
	 * @param user The logged user.
	 * @param companyId The if of the company.
	 * @param fleetId The fleet profile id.
	 * @param projectId The customer id.
	 * @param prNumber The sequence number of the purchase requisition.
	 * @param dateFrom The start date.
	 * @param dateTo The end date.
	 * @param status The current status of the requisition form: Used, Unused, or All.
	 * @param pageNumber The page number.
	 * @return The paged list of requisition forms available for referencing.
	 */
	public Page<PRReferenceDto> getPrReferences(User user, Integer companyId, Integer fleetId, Integer projectId,
			Integer prNumber, Date dateFrom, Date dateTo, Integer status, Integer pageNumber) {
		return requisitionFormDao.getPrReferences(user, companyId, fleetId, projectId, prNumber, 
				dateFrom, dateTo, status, new PageSetting(pageNumber));
	}

	/**
	 * Convert the selected purchase requisitions to {@code Pr2PoDto}
	 * @param strPrObjIds The purchase reference eb object ids in string format separated by (;)
	 * @return Instance of {@code Pr2PoDto}
	 */
	public Pr2PoDto convertPrToPo(String strPrObjIds) {
		List<Integer> prObjIds = convert2PrObjectIds(strPrObjIds);
		return convertPrToPo(prObjIds);
	}

	private List<Integer> convert2PrObjectIds(String strPrObjIds) {
		List<Integer> prObjIds = new ArrayList<>();
		String[] objIds = strPrObjIds.split(";");
		for(String s : objIds) {
			prObjIds.add(Integer.parseInt(s));
		}
		return prObjIds;
	}

	private Pr2PoDto convertPrToPo(List<Integer> prObjIds) {
		List<Integer> customerIds = new ArrayList<>();
		List<Integer> fleetProfileIds = new ArrayList<>();
		RequisitionForm pr = null;
		RequisitionForm rfReference = null;
		List<RequisitionForm> rfReferences = new ArrayList<>();
		String strPrNumbersComma = "";
		String strPrNumbers = "";
		String projectName = "";
		String fleetProfileCode = "";
		String remarks = "";
		boolean isNotLast = false;
		int index = 0;
		int prObjectId = 0;
		Integer warehouseId = null;
		RPurchaseOrder po = new RPurchaseOrder();
		po.setrPoItems(new ArrayList<>());
		for (Integer prObjId : prObjIds) {
			isNotLast = index < prObjIds.size() - 1;
			pr = requisitionFormDao.getByEbObjectId(prObjId);
			Integer prWarehouse = pr.getWarehouseId() != null ? pr.getWarehouseId() : -1;
			warehouseId = prObjIds.size() == 1 ? prWarehouse : -1;
			rfReference = requisitionFormDao.get(pr.getReqFormRefId());
			prObjectId = pr.getEbObjectId();
			strPrNumbersComma += pr.getSequenceNumber() + (isNotLast ? ", " : "");
			strPrNumbers += prObjectId + (isNotLast ? ";" : "");
			if (rfReference.getArCustomerId() != null && !customerIds.contains(rfReference.getArCustomerId())) {
				customerIds.add(rfReference.getArCustomerId());
			}
			if (rfReference.getFleetProfileId() != null && !fleetProfileIds.contains(rfReference.getFleetProfileId())) {
				fleetProfileIds.add(rfReference.getFleetProfileId());
			}
			if (pr.getRemarks() != null && !remarks.contains(pr.getRemarks())) {
				remarks += pr.getRemarks() + (isNotLast ? ", " : "") ;
			}
			List<RPurchaseOrderItem> poItems = new ArrayList<>();
			List<PurchaseRequisitionItem> prItems = prItemDao.getAllByRefId(
					PurchaseRequisitionItem.FIELD.purchaseRequisitionId.name(), pr.getId());
			for(PurchaseRequisitionItem prItem : prItems) {
				RPurchaseOrderItem poItem = new RPurchaseOrderItem();
				Item item = prItem.getItem();
				poItem.setStockCode(item.getStockCode());
				poItem.setItemId(prItem.getItemId());
				poItem.setItem(prItem.getItem());
				poItem.setQuantity(prItem.getQuantity());
				poItem.setRefenceObjectId(prItem.getEbObjectId());
				poItems.add(poItem);
			}
			po.getrPoItems().addAll(poItems);
			poItems = null;
			pr = null;
			rfReferences.add(rfReference);
			index++;
		}
		for (Integer c : customerIds) {
			int custIndex = customerIds.indexOf(c);
			isNotLast = custIndex > 0 && custIndex < customerIds.size();
			projectName += (isNotLast ? " / " : "") + arCustomerService.getCustomer(c).getName();
		}
		FleetProfile fp = null;
		for (Integer f : fleetProfileIds) {
			int fleetIndex = fleetProfileIds.indexOf(f);
			isNotLast = fleetIndex > 0 && fleetIndex < fleetProfileIds.size();
			fp = fleetProfileDao.get(f);
			fleetProfileCode  += fp != null ? (isNotLast ? " / " : "") + fp.getCodeVesselName() : "";
		}
		return Pr2PoDto.getInstanceOf(prObjIds, strPrNumbers, strPrNumbersComma, projectName,
				fleetProfileCode, remarks, warehouseId, po);
	}

	/**
	 * Initialize the list purchase order items.
	 * @param strPrObjIds The purchase reference eb object ids in string format separated by (;)
	 * @return List of purchase order items.
	 */
	public List<RPurchaseOrderItem> initPoItems(Integer supplierAccountId, String strPrObjIds, Integer warehouseId) {
		List<Integer> prObjIds = convert2PrObjectIds(strPrObjIds);
		RequisitionForm pr = null;
		List<PurchaseRequisitionItem> prItems = null;
		List<RPurchaseOrderItem> poItems = new ArrayList<>();
		Item item = null;
		RPurchaseOrderItem poItem = null;
		int itemId = 0;
		int companyId = 0;
		for (Integer prObjId : prObjIds) {
			pr = requisitionFormDao.getByEbObjectId(prObjId);
			if (pr == null) {
				throw new RuntimeException("No purchase request found.");
			}
			companyId = pr.getCompanyId();
			prItems = prItemDao.getAllByRefId(PurchaseRequisitionItem.FIELD.purchaseRequisitionId.name(), pr.getId());
			double remainingQty = 0;
			for (PurchaseRequisitionItem pri : prItems) {
				itemId = pri.getItemId();
				remainingQty = purchaseOderDao.getRemainingPrReferenceQty(pri.getEbObjectId(), itemId);
				if (remainingQty > 0) {
					poItem = new RPurchaseOrderItem();
					item = itemService.getItem(itemId);
					poItem.setStockCode(item.getStockCode());
					poItem.setItemId(itemId);
					item.setExistingStocks(itemDao.getItemExistingStocks(itemId,
							(warehouseId != null ? warehouseId : -1), new Date(), companyId));
					poItem.setItem(item);
					poItem.setQuantity(remainingQty);
					poItem.setRefenceObjectId(pri.getEbObjectId());
					poItems.add(poItem);
					poItem = null;
				}
			}
			prItems = null;
		}
		return poItems;
	}

	/**
	 * Get the list of Purchase order items by purchase order id.
	 * @param rPurchaseOrderId The id of the Purchase Order.
	 * @return The list of Purchase order items.
	 */
	public List<RPurchaseOrderItem> getPoItems(Integer rPurchaseOrderId, Integer warehouseId) throws IOException{
		RPurchaseOrder rPurchaseOrder = getRpurchaseOrder(rPurchaseOrderId);
		return getPOItems(rPurchaseOrder, warehouseId);
	}

	private List<RPurchaseOrderItem> getPOItems(RPurchaseOrder po, Integer warehouseId) {
		int purchaseId = po.getId();
		warehouseId = warehouseId != null ? warehouseId : -1;
		List<RPurchaseOrderItem> items = purchaseOrderItemDao.getPOItems(purchaseId);
		EBObject refObject = null;
		Integer refObjectId = null;
		for (RPurchaseOrderItem poItem : items) {
			refObject = objectToObjectDao.getOtherReference(poItem.getEbObjectId(), PRReferenceDto.PRI_POI_OR_TYPE_ID);
			refObject = refObject == null ? objectToObjectDao.getOtherReference(poItem.getEbObjectId(),
					PRReferenceDto.MERGED_PRI_POI_OR_TYPE_ID) : refObject;
			if (refObject != null) {
				refObjectId = refObject.getId();
				poItem.setRefenceObjectId(refObjectId);
				refObjectId = null;
			}
			Item item = itemDao.get(poItem.getItemId());
			item.setExistingStocks(itemDao.getItemExistingStocks(poItem.getItemId(),
					(warehouseId != null ? warehouseId : -1), new Date(), po.getCompanyId()));
			poItem.setItem(item);
			item = null;
		}
		return items;
	}

	private void saveHeader2HeaderReferences(User user, String strRefObjIds, Integer refObjectId, Integer orTypeId, Date date) {
		if (strRefObjIds.contains(",")) {
			strRefObjIds = strRefObjIds.replace(",", "");
		}
		String refObjIds[] = strRefObjIds.split(";");
		if (refObjIds.length > 0) {
			List<Domain> o2os = new ArrayList<>();
			for (String s : refObjIds) {
				o2os.add(ObjectToObject.getInstanceOf(Integer.parseInt(s), refObjectId, orTypeId, user, date));
			}
			objectToObjectDao.batchSave(o2os);
		}
	}

	/**
	 * Get the purchase order object for NSB PO printout
	 * @param purchaseOrderId The purchase order object id
	 * @return The purchase order object for NSB PO printout
	 */
	public RPurchaseOrder getPurchaseOrder(int purchaseOrderId) throws IOException {
		RPurchaseOrder purchaseOrder = getRpurchaseOrder(purchaseOrderId);
		List<PurchaseOrderItemDto> poItemDtos = new ArrayList<PurchaseOrderItemDto>();
		PurchaseOrderItemDto poItemDto = null;
		List<RPurchaseOrderItem> purchaseOrderItems = purchaseOrder.getrPoItems();
		double totalNetOfVAT = 0;
		double totalVAT = 0;
		double unitCost = 0;
		double vat = 0;
		double totalNetintVal = 0.00;
		double quantity = 0;
		for (RPurchaseOrderItem poi : purchaseOrderItems) {
			quantity = poi.getQuantity();
			unitCost = poi.getUnitCost() != null ? poi.getUnitCost() : 0;
			vat = poi.getVatAmount() != null ? NumberFormatUtil.roundOffRuleOfFive(poi.getVatAmount()) : 0;
			totalNetintVal += (NumberFormatUtil.multiplyWFP(quantity, unitCost)) - vat;
			totalNetOfVAT += NumberFormatUtil.roundOffRuleOfFive(totalNetintVal);
			totalVAT += vat;

			poItemDto = new PurchaseOrderItemDto();
			poItemDto.setNotRemarks(true);
			poItemDto.setQuantity(quantity);
			poItemDto.setUomName(poi.getItem().getUnitMeasurement().getName());
			poItemDto.setStockCodeDesc(StringFormatUtil.removeExtraWhiteSpaces(poi.getItem().getStockCode() + " - " + poi.getItem().getDescription()));
			poItemDto.setUnitCost(unitCost);
			poItemDto.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, unitCost)));
			poItemDtos.add(poItemDto);

			// Reset values
			quantity = 0;
			unitCost = 0;
			vat = 0;
			totalNetintVal = 0;
		}
		List<PurchaseOrderLine> purchaseOrderLines = purchaseOrder.getPoLines();
		for (PurchaseOrderLine pol : purchaseOrderLines) {
			unitCost = pol.getUpAmount() != null ? pol.getUpAmount() : 0;
			vat = pol.getVatAmount() != null ? NumberFormatUtil.roundOffRuleOfFive(pol.getVatAmount()) : 0;
			quantity = pol.getQuantity() != null && pol.getQuantity() != 0 ? pol.getQuantity() : 1;
			totalNetintVal += (NumberFormatUtil.multiplyWFP(quantity, unitCost)) - vat;
			totalNetOfVAT += NumberFormatUtil.roundOffRuleOfFive(totalNetintVal);
			totalVAT += vat;

			poItemDto = new PurchaseOrderItemDto();
			poItemDto.setNotRemarks(true);
			poItemDto.setQuantity(quantity);
			poItemDto.setUomName(pol.getUnitMeasurement().getName());
			poItemDto.setStockCodeDesc(StringFormatUtil.removeExtraWhiteSpaces(pol.getApLineSetup().getName()));
			poItemDto.setUnitCost(unitCost);
			poItemDto.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, unitCost)));
			poItemDtos.add(poItemDto);

			// Reset values
			quantity = 0;
			unitCost = 0;
			vat = 0;
			totalNetintVal = 0;
		}

		purchaseOrder.setPoItemDtos(poItemDtos);
		purchaseOrder.setTotalNetOfVAT(totalNetOfVAT);
		purchaseOrder.setTotalVAT(totalVAT);
		return purchaseOrder;
	}

	/**
	 * Determine if the item would exceed the set max ordering point.
	 * @param itemId The item id.
	 * @param poQty The purchase order qty.
	 * @return True if the item will exceed the max ordering point, otherwise false.
	 */
	public boolean hasExceedMaxOrderingPoint(Integer itemId, double poQty, Integer poiId) {
		boolean hasExceedMaxOp = false;
		Integer maxOp = itemDao.get(itemId).getMaxOrderingPoint();
		if(maxOp != null) {
			double notReceivedQty = purchaseOrderItemDao.getNotReceivedPoiQty(itemId, poiId);
			if(maxOp < notReceivedQty + poQty) {
				hasExceedMaxOp = true;
			}
		}
		return hasExceedMaxOp;
	}

	/**
	 * Compute the {@link RPurchaseOrder} grand total.
	 * @param purchaseOrder The {@link RPurchaseOrder}
	 * @return The computed grand total.
	 */
	public double getPoGrandTotal(RPurchaseOrder purchaseOrder) {
		double grandTotal = 0;
		double currencyRate = purchaseOrder.getCurrencyRateValue() > 0.0 ? purchaseOrder.getCurrencyRateValue() : 1.0;
		for (RPurchaseOrderItem poi : purchaseOrder.getrPoItems()) {
			double vatAmount = poi.getVatAmount() != null ? poi.getVatAmount() : 0;
			grandTotal += (NumberFormatUtil.multiplyWFP(poi.getQuantity(), poi.getUnitCost())) + vatAmount;
		}
		List<PurchaseOrderLine> savedPOLines = poLineDao.getAllByRefId
				(PurchaseOrderLine.FIELD.purchaseOrderId.name(), purchaseOrder.getId());
		for (PurchaseOrderLine pol : savedPOLines) {
			grandTotal += NumberFormatUtil.multiplyWFP(pol.getQuantity(), pol.getUpAmount());
		}
		return NumberFormatUtil.divideWFP(grandTotal, currencyRate);
	}
}
