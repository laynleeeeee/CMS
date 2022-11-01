package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.PurchaseRequisitionItemDao;
import eulap.eb.dao.RequisitionFormDao;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.OtherChargesLine;
import eulap.eb.domain.hibernate.PurchaseRequisitionItem;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.RequisitionFormItem;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.web.dto.RequisitionFormDto;
import eulap.eb.web.dto.Rf2PrDto;
import eulap.eb.web.dto.RfReferenceDto;

/**
 * Service class for purchase requisition form.

 *
 */
@Service
public class PurchaseRequisitionService extends RequisitionFormService {
	private static Logger logger = Logger.getLogger(PurchaseRequisitionService.class);
	@Autowired
	private RequisitionFormDao requisitionFormDao;
	@Autowired
	private RequisitionFormService requisitionFormService;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private PurchaseRequisitionItemDao prItemDao;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;

	/**
	 * Get the list of requisition forms for reference.
	 * @param companyId The company id.
	 * @param fleetId The supplier id.
	 * @param projectId The project id.
	 * @param rfNumber The requisition form number.
	 * @param dateFrom The date from.
	 * @param dateTo The date to.
	 * @param status The current status of the requisition form: Used, Unused, or All.
	 * @param reqTypeId The requisition type id.
	 * @param pageNumber The paged number.
	 * @return The paged list of requisition forms available for referencing.
	 */
	public Page<RfReferenceDto> getRequisitionForms(Integer companyId, Integer fleetId,
			Integer projectId, Integer rfNumber, Date dateFrom, Date dateTo, Integer status,
			Integer reqTypeId, int pageNumber) {
		return requisitionFormDao.getRfReferenceForms(companyId, fleetId, projectId, rfNumber,
				dateFrom, dateTo, status, reqTypeId, false, false, false, new PageSetting(pageNumber));
	}

	/**
	 * Convert the requisition form to requisition form.
	 * @param rfid The id of the requisition.
	 */	
	public Rf2PrDto convertRfToPr(int rfId) {
		RequisitionForm requisitionForm = requisitionFormDao.get(rfId);
		if (requisitionForm != null) {
			String rfReferenceNo = requisitionForm.getSequenceNumber().toString();
			boolean hasCustomer = requisitionForm.getArCustomerId() != null;
			boolean hasFleet = requisitionForm.getFleetProfileId() != null;
			List<PurchaseRequisitionItem> requisitionFormItems = initPrItems(rfId, requisitionForm.getRequisitionTypeId(),
					requisitionForm.getWarehouseId(), requisitionForm.getCompanyId(), null);
			return Rf2PrDto.getInstanceOf(rfId, rfReferenceNo, requisitionForm.getFleetProfileId(), requisitionForm.getArCustomerId(),
				(hasCustomer ? requisitionForm.getArCustomer().getName() : null), (hasFleet ? requisitionForm.getFleetProfile().getCodeVesselName() : null),
				DateUtil.formatDate(requisitionForm.getRequestedDate()), requisitionForm.getRemarks(), requisitionForm.getWarehouseId(),
				requisitionFormItems);
		} else {
			logger.error("No requisition form found.");
			throw new RuntimeException("No requisition form found.");
		}
	}

	/**
	 * Initialize the list of {@code RequisitionFormItem} from requisition form
	 * @param rfId The requisition form id
	 * @param requisitionTypeId The requisition type id
	 * @param isNew True if the form is a new form, otherwise false
	 * @param warehouseId The warehouse id
	 * @return The processed list of requisition items
	 */
	public List<PurchaseRequisitionItem> initPrItems(int rfId, int requisitionTypeId,
			Integer warehouseId, Integer companyId, Integer prId) {
		warehouseId = warehouseId != null ? warehouseId : -1;
		List<PurchaseRequisitionItem> requisitionFormItems = new ArrayList<PurchaseRequisitionItem>();
		RequisitionFormDto rfDto = requisitionFormService.getRequisitionFormDto(rfId, true, false);
		if (rfDto != null) {
			List<RequisitionFormItem> rfItems = rfDto.getRequisitionForm().getRequisitionFormItems();
			PurchaseRequisitionItem prItem = null;
			double remainingQty = 0;
			Date currentDate = new Date();
			Item item = null;
			Integer itemId = null;
			for (RequisitionFormItem rfi : rfItems) {
				remainingQty = requisitionFormService.getRemainingRFQty(rfi.getEbObjectId(),
						null, prId, false);
				if (remainingQty > 0) {
					prItem = new PurchaseRequisitionItem();
					itemId = rfi.getItemId();
					item = itemDao.get(itemId);
					prItem.setStockCode(item.getStockCode());
					prItem.setItem(item);
					prItem.setItemId(itemId);
					prItem.setQuantity(remainingQty);
					prItem.setExistingStocks(itemDao.getItemExistingStocks(rfi.getItemId(),
							warehouseId, currentDate, companyId));
					prItem.setRefenceObjectId(rfi.getEbObjectId());
					requisitionFormItems.add(prItem);
					prItem = null;
				}
			}
		} else {
			logger.error("No requisition form found.");
			throw new RuntimeException("No requisition form found.");
		}
		return requisitionFormItems;
	}

	/**
	 * Initialize the list of {@code OtherChargesLine} from requisition form.
	 * @param rfId The requisition form id.
	 */
	public List<OtherChargesLine> initOCLines(int rfId) {
		RequisitionFormDto rfDto = requisitionFormService.getRequisitionFormDto(rfId, false, false);
		if (rfDto == null) {
			logger.error("No requisition form found.");
			throw new RuntimeException("No requisition form found.");
		}
		return rfDto.getRequisitionForm().getOtherChargesLines();
	}

	/**
	 * Initialize the list of {@code ReferenceDocument} from requisition form.
	 * @param rfId The requisition form id.
	 */
	public List<ReferenceDocument> initRefDocs(int rfId) {
		RequisitionFormDto rfDto = requisitionFormService.getRequisitionFormDto(rfId, false, true);
		if (rfDto == null) {
			logger.error("No requisition form found.");
			throw new RuntimeException("No requisition form found.");
		}
		return rfDto.getRequisitionForm().getReferenceDocuments();
	}

	/**
	 * Get the requisition form - purchase requisition
	 * @param rfId The form id
	 * @return The requisition form - purchase requisition object
	 */
	public RequisitionForm getPurchaseRequisition(Integer rfId) {
		RequisitionForm purchaseRequisition = requisitionFormDao.get(rfId);
		List<PurchaseRequisitionItem> prItems = prItemDao.getAllByRefId(
				PurchaseRequisitionItem.FIELD.purchaseRequisitionId.name(), rfId);
		EBObject refObject = null;
		Integer refObjectId = null;
		Date currentDate = new Date();
		for (PurchaseRequisitionItem pri : prItems) {
			refObject = ooLinkHelper.getReferenceObject(pri.getEbObjectId(),
					PurchaseRequisitionItem.RFI_PRI_OR_TYPE_ID);
			if (refObject != null) {
				refObjectId = refObject.getId();
				pri.setOrigRefObjectId(refObjectId);
				pri.setRefenceObjectId(refObjectId);
				refObjectId = null;
			}
			pri.setStockCode(itemDao.get(pri.getItemId()).getStockCode());
			int warehouseId = purchaseRequisition.getWarehouseId() != null ? purchaseRequisition.getWarehouseId() : -1;
			pri.setExistingStocks(itemDao.getItemExistingStocks(pri.getItemId(),
					warehouseId, currentDate, purchaseRequisition.getCompanyId()));
			refObject = null;
		}
		purchaseRequisition.setPurchaseRequisitionItems(prItems);
		List<ReferenceDocument> referenceDocuments = referenceDocumentService.getReferenceDocuments(
				purchaseRequisition.getEbObjectId());
		if (referenceDocuments != null && !referenceDocuments.isEmpty()) {
			purchaseRequisition.setReferenceDocuments(referenceDocuments);
		}
		return purchaseRequisition;
	}
}
