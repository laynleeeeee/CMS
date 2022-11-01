package eulap.eb.validator.inv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.RPurchaseOrderDao;
import eulap.eb.dao.RPurchaseOrderItemDao;
import eulap.eb.dao.SerialItemDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.ApLineSetup;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.RPurchaseOrderItem;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.service.ApLineSetupService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemService;
import eulap.eb.service.RReceivingReportService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.SerialItemService;
import eulap.eb.service.SupplierAccountService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
/**
 * A class that handles the validation of R_ReceivingReport.

 */
@Service
public class RReceivingReportValidator implements Validator{
	private static Logger logger = Logger.getLogger(RReceivingReportValidator.class);
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private RReceivingReportService rrService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private RPurchaseOrderItemDao purchaseOrderItemDao;
	@Autowired
	private SerialItemDao serialItemDao;
	@Autowired
	private RPurchaseOrderDao purchaseOrderDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private ApInvoiceLineDao aplDao;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private SupplierAccountService supplierAccountService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ApLineSetupService aplSetupService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private ReferenceDocumentService refDocumentService;

	@Override
	public boolean supports(Class<?> clazz) {
		return APInvoice.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		validate(obj, errors, "", true);
	}

	public void validate(Object obj, Errors errors, String fieldPrepend, boolean isValidatedRRItems) {
		APInvoice apInvoice = (APInvoice) obj;
		boolean isExistingInvNo = false;
		logger.info("Performing receiving report validation process");

		refDocumentService.validateReferences(apInvoice.getReferenceDocuments(), errors);
		RReceivingReport rr = apInvoice.getReceivingReport();
		ValidatorUtil.validateCompany(rr.getCompanyId(), companyService,
				errors, fieldPrepend+"receivingReport.companyId");

		logger.debug("Validating the Warehouse id: "+rr.getWarehouseId());
		if(rr.getWarehouseId() == null || rr.getWarehouseId() == -1) {
			errors.rejectValue(fieldPrepend+"receivingReport.warehouseId", null, null, ValidatorMessages.getString("RReceivingReportValidator.0"));
		} else if(!warehouseService.getWarehouse(rr.getWarehouseId()).isActive()) {
			errors.rejectValue(fieldPrepend+"receivingReport.warehouseId", null, null, ValidatorMessages.getString("RReceivingReportValidator.34"));
		}

		logger.debug("Validating the RR Date: "+apInvoice.getGlDate());
		if(apInvoice.getGlDate() == null)
			errors.rejectValue(fieldPrepend+"glDate", null, null, ValidatorMessages.getString("RReceivingReportValidator.1"));
		else if(!timePeriodService.isOpenDate(apInvoice.getGlDate()))
			errors.rejectValue(fieldPrepend+"glDate", null, null, ValidatorMessages.getString("RReceivingReportValidator.2"));

		String bms = rr.getBmsNumber();
		if(bms == null || !bms.trim().isEmpty()) {
			if(bms.length() > APInvoice.MAX_CHAR_BMS) {
				errors.rejectValue("receivingReport.bmsNumber", null, null, String.format(ValidatorMessages.getString("RReceivingReportValidator.26"),
						APInvoice.MAX_CHAR_BMS));
			}
		}

		String supplierInvNo = apInvoice.getInvoiceNumber();
		logger.debug("Validating the Supplier Invoice No.: "+supplierInvNo);

		if(supplierInvNo != null && !supplierInvNo.trim().isEmpty()) {
			isExistingInvNo = true;
			if(supplierInvNo.trim().length() > APInvoice.MAX_CHAR_INVOICE_NO) {
				errors.rejectValue(fieldPrepend+"invoiceNumber", null, null,
					String.format(ValidatorMessages.getString("RReceivingReportValidator.6"), APInvoice.MAX_CHAR_INVOICE_NO));
			}
		}

		if(apInvoice.getReceivingReport().getDivisionId() == null) {
			errors.rejectValue(fieldPrepend+"divisionId", null, null, ValidatorMessages.getString("RReceivingReportValidator.28"));
		}else if(!divisionService.getDivision(apInvoice.getReceivingReport().getDivisionId()).isActive()) {
			errors.rejectValue(fieldPrepend+"divisionId", null, null, ValidatorMessages.getString("RReceivingReportValidator.29"));
		}

		logger.debug("Validating the supplier id: "+apInvoice.getSupplierId());
		if(apInvoice.getSupplierId() == null || apInvoice.getSupplierId() == -1) {
			errors.rejectValue(fieldPrepend+"supplierId", null, null, ValidatorMessages.getString("RReceivingReportValidator.7"));
		} else if(!supplierService.getSupplier(apInvoice.getSupplierId()).isActive()) {
			errors.rejectValue(fieldPrepend+"supplierId", null, null, ValidatorMessages.getString("RReceivingReportValidator.30"));
		}

		logger.debug("Validating the supplier account id: "+apInvoice.getSupplierAccountId());
		if(apInvoice.getSupplierAccountId() == null || apInvoice.getSupplierAccountId() == -1) {
			errors.rejectValue(fieldPrepend+"supplierAccountId", null, null, ValidatorMessages.getString("RReceivingReportValidator.8"));
		} else if(!supplierAccountService.getSupplierAcct(apInvoice.getSupplierAccountId()).isActive()) {
			errors.rejectValue(fieldPrepend+"supplierAccountId", null, null, ValidatorMessages.getString("RReceivingReportValidator.31"));
		}

		logger.debug("Validating the DR No.: "+rr.getDeliveryReceiptNo());
		if ((rr.getDeliveryReceiptNo() == null || rr.getDeliveryReceiptNo().trim().isEmpty()) && !isExistingInvNo) {
			errors.rejectValue(fieldPrepend+"receivingReport.deliveryReceiptNo", null, null,
					ValidatorMessages.getString("RReceivingReportValidator.9"));
		} else if(rr.getDeliveryReceiptNo().length() > APInvoice.MAX_CHAR_INVOICE_NO) {
			errors.rejectValue(fieldPrepend+"receivingReport.deliveryReceiptNo", null, null,
					String.format(ValidatorMessages.getString("RReceivingReportValidator.10"), APInvoice.MAX_CHAR_INVOICE_NO));
		}

		Integer row = 0;
		List<RReceivingReportItem> rritems = apInvoice.getRrItems();
		logger.info("Validation receiving report items");
		double totalAmount = 0;
		for (RReceivingReportItem rri : rritems) {
			row++;
			if(rri.getItemId() != null && rri.getItemId() != 0) {
				//Quantity
				if(rri.getQuantity() == null || rri.getQuantity() < 0) {
					errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReceivingReportValidator.11"));
					break;
				} else if(rri.getQuantity() == 0) {
					errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReceivingReportValidator.12"));
					break;
				}
				//Unit Cost
				if(rri.getUnitCost() != null && rri.getUnitCost() < 0) {
					errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReceivingReportValidator.13"));
					break;
				}
				totalAmount += rri.getUnitCost() != null ? rri.getUnitCost() : 0;
			}else if(rri.getQuantity() != null && rri.getQuantity() > 0) {
				errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReceivingReportValidator.14")+row+ValidatorMessages.getString("RReceivingReportValidator.15"));
				break;
			}else if(rri.getItemId() == null && !rri.getStockCode().trim().isEmpty()) {
				errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReceivingReportValidator.16"));
				break;
			}
		}

		evalRrPoItems(rritems, rr.getPoNumber(), errors, fieldPrepend);

		List<SerialItem> serialItems = rr.getSerialItems();
		boolean hasRrItems = rritems != null && !rritems.isEmpty();
		boolean hasSerialItems = serialItems != null && !serialItems.isEmpty();
		boolean hasApLines = apInvoice.getApInvoiceLines() != null && !apInvoice.getApInvoiceLines().isEmpty();
		if(!hasRrItems && !hasSerialItems && !hasApLines){
			errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReceivingReportValidator.24"));
		} else if(hasRrItems || hasSerialItems) {
			serialItemService.validateSerialItems("apInvoice.rrItems", "receivingReport.siMessage",
					!hasRrItems, true, true, serialItems, errors);
			if(hasSerialItems) {
				Item item = null;
				for(SerialItem si : serialItems) {
					row++;
					item = itemService.getItem(si.getItemId());
					if(!item.isActive()) {
						errors.rejectValue(fieldPrepend+"receivingReport.siMessage", null, null, String.format(
								ValidatorMessages.getString("RReceivingReportValidator.32"), row));
					}
					totalAmount += si.getUnitCost() != null ? si.getUnitCost() : 0;
				}
			}
		} else {
			Integer purchaseOrderId = apInvoice.getPurchaseOderId();
			if(purchaseOrderId != null) {
				Map<Integer, SerialItem> saleItemHM = new HashMap<>();
				Integer key = 0;
				SerialItem sItem = null;
				for (SerialItem serialItem : serialItems) {
					if(serialItem.getItemId() == null){
						continue;
					}
					key = serialItem.getItemId();
					if(saleItemHM.containsKey(key)){
						try {
							sItem = (SerialItem) saleItemHM.get(key).clone();
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
						sItem.setQuantity(sItem.getQuantity() + serialItem.getQuantity());
						saleItemHM.put(key, sItem);
					} else {
						saleItemHM.put(key, serialItem);
					}
				}
				if(apInvoice.getId() == 0){
					SerialItem serialItem = new SerialItem();
					List<RPurchaseOrderItem> purchaseOrderItems =
							purchaseOrderItemDao.getPOItems(purchaseOrderId);
					for (RPurchaseOrderItem rPurchaseOrderItem : purchaseOrderItems) {
						serialItem = saleItemHM.get(rPurchaseOrderItem.getItemId());
						if(serialItem == null){
							continue;
						}
						double serialItemQty = serialItem.getQuantity() > rPurchaseOrderItem.getQuantity()
								? rPurchaseOrderItem.getQuantity() : serialItem.getQuantity();
						double availableQty = rPurchaseOrderItem.getQuantity()-serialItemDao.getAvailableStockFromPo(
								apInvoice.getId(), serialItem.getItemId(), purchaseOrderId);
						if(serialItemQty > (availableQty)){
							errors.rejectValue("receivingReport.siMessage", null, null, String.format(ValidatorMessages.getString(
									"RReceivingReportValidator.25"), itemDao.get(serialItem.getItemId()).getStockCode(),
									purchaseOrderDao.get(purchaseOrderId).getPoNumber()));
						}
					}
				}
			}
		}

		logger.info("Freeing up memory allocation.");
		supplierInvNo = null;
		rr = null;

		List<ApInvoiceLine> otherCharges = apInvoice.getApInvoiceLines();
		if (otherCharges != null && !otherCharges.isEmpty()) {
			int rowCount = 0;
			for (ApInvoiceLine oc : otherCharges) {
				rowCount++;
				Integer apLineSetupId = oc.getApLineSetupId();
				String apLineName = oc.getApLineSetupName();
				if (apLineSetupId != null || apLineName != null) {
					if (apLineSetupId == null) {
						if (apLineName != null && !apLineName.isEmpty()) {
							errors.rejectValue(fieldPrepend+"apInvoiceLines", null, null,
									String.format(ValidatorMessages.getString("RReceivingReportValidator.23"), rowCount));
						} else {
							errors.rejectValue(fieldPrepend+"apInvoiceLines", null, null,
									String.format(ValidatorMessages.getString("RReceivingReportValidator.22"), rowCount));
						}
					}
					if(!aplSetupService.getApLineSetup(apLineSetupId).isActive()) {
						errors.rejectValue(fieldPrepend+"apInvoiceLines", null, null,
								String.format(ValidatorMessages.getString("RReceivingReportValidator.33"), rowCount));
					}
				}
				if(oc.getQuantity() != null || oc.getPercentile() != null || oc.getUnitOfMeasurementId() != null) {
					if(apLineSetupId == null) {
						errors.rejectValue(fieldPrepend+"apInvoiceLines", null, null,
								String.format(ValidatorMessages.getString("RReceivingReportValidator.35"), rowCount));
					}
				}
				if(apLineSetupId != null && oc.getQuantity() != null) {
					Double remQty = aplDao.getRemainingQty(oc.getRefenceObjectId(), apLineSetupId, oc.getApInvoiceId());
					if(remQty - oc.getQuantity() < 0) {
						errors.rejectValue(fieldPrepend+"apInvoiceLines", null, null, 
								String.format(ValidatorMessages.getString("RReceivingReportValidator.27"), oc.getApLineSetupName(),rowCount));
					}
				}
				totalAmount += oc.getUpAmount() != null ? oc.getUpAmount() : 0;
			}
		}
		if(totalAmount == 0) {
			//Total transaction amount should be greater than zero.
			errors.rejectValue(fieldPrepend+"receivingReport.commonErrMsg", null, null, ValidatorMessages.getString("RReceivingReportValidator.36"));
		}

		// Validate form status
		FormWorkflow workflow = apInvoice.getId() != 0 ? rrService.getFormWorkflow(apInvoice.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatusCancelled(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}
	}

	private void evalRrPoItems (List<RReceivingReportItem> rrItems, String poNumber, Errors errors, String fieldPrepend) {
		if (rrItems != null && !rrItems.isEmpty()) {
			int row = 1;
			for (RReceivingReportItem rri : rrItems) {
				if(rri.getItemId() != null) {
					double quantity = rri.getQuantity() != null ? rri.getQuantity() : 0;
					Integer referenceObjectId = rri.getRefenceObjectId();
					Item item = itemService.getItem(rri.getItemId());
					if(!item.isActive()) {
						errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, String.format(
								ValidatorMessages.getString("RReceivingReportValidator.32"), row));
					}
					// Validate only remaining quantity if there is a reference item.
					if(referenceObjectId != null) {
						double remQty = rrService.getRemainingQty(referenceObjectId, rri.getItemId());
						remQty += (rri.getId() != 0 ? rrService.getRrItem(rri.getId()).getQuantity() : 0);
						if (quantity > remQty) {
							errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, item.getStockCode() + ValidatorMessages.getString("RReceivingReportValidator.18") + row + ValidatorMessages.getString("RReceivingReportValidator.19"));
						}
						row++;
					}
				}
			}
		}
	}
}
