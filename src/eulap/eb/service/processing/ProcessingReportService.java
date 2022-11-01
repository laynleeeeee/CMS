package eulap.eb.service.processing;

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
import eulap.eb.dao.ArLineSetupDao;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.PrByProductDao;
import eulap.eb.dao.PrMainProductDao;
import eulap.eb.dao.PrOtherChargeDao;
import eulap.eb.dao.PrOtherMaterialsItemDao;
import eulap.eb.dao.PrRawMaterialsItemDao;
import eulap.eb.dao.ProcessingReportDao;
import eulap.eb.dao.ProcessingReportTypeDao;
import eulap.eb.dao.RReceivingReportItemDao;
import eulap.eb.dao.RReceivingReportRmItemDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.BaseItem;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PrByProduct;
import eulap.eb.domain.hibernate.PrMainProduct;
import eulap.eb.domain.hibernate.PrOtherCharge;
import eulap.eb.domain.hibernate.PrOtherMaterialsItem;
import eulap.eb.domain.hibernate.PrRawMaterialsItem;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.domain.hibernate.ProcessingReportType;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RReceivingReportRmItem;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.EbObjectService;
import eulap.eb.service.ItemBagQuantityService;
import eulap.eb.service.ItemService;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.UomConversionService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.oo.OOChild;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.AvblStocksAndBagsDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;
import eulap.eb.web.processing.dto.MillingRecoveryReport;
import eulap.eb.web.processing.dto.ProcessingReportPrintout;
import eulap.eb.web.processing.dto.Recovery;

/**
 * Class that handles business logic of {@link ProcessingReport}

 *
 */
@Service
public class ProcessingReportService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(ProcessingReportService.class);
	@Autowired
	private ProcessingReportDao processingReportDao;
	@Autowired
	private PrRawMaterialsItemDao prRawMaterialsItemDao;
	@Autowired
	private PrOtherMaterialsItemDao prOtherMaterialsItemDao;
	@Autowired
	private PrOtherChargeDao prOtherChargeDao;
	@Autowired
	private PrMainProductDao prMainProductDao;
	@Autowired
	private PrByProductDao prByProductDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private RReceivingReportItemDao rrItemDao;
	@Autowired
	private RReceivingReportRmItemDao rrmItemDao;
	@Autowired
	private ArLineSetupDao arLineSetupDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private UomConversionService uomConversionService;
	@Autowired
	private UnitMeasurementDao unitMeasurementDao;
	@Autowired
	private ProcessingReportTypeDao processingTypeDao;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private ItemService itemService;
	@Autowired
	private EbObjectService ebObjectService;
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;

	/**
	 * Get the processing report object.
	 * @param processingReportId The object id.
	 * @param isMainObjectOnly True to get the main/header object only while false
	 * to get all the related objects. 
	 * @return The processing report object.
	 */
	public ProcessingReport getProcessingReport (int processingReportId, boolean isMainObjectOnly) {
		ProcessingReport processingReport = processingReportDao.get(processingReportId);
		if (isMainObjectOnly) {
			return processingReport;
		}

		processingReport.setPrRawMaterialsItems(getPrRawMaterials(processingReportId));
		processingReport.setPrOtherMaterialsItems(getOtherMaterials(processingReportId));
		processingReport.setPrOtherCharges(getPrOtherCharges(processingReportId));
		processingReport.setPrMainProducts(prMainProductDao.getAllByRefId("processingReportId", processingReportId));
		processingReport.setPrByProducts(prByProductDao.getAllByRefId("processingReportId", processingReportId));

		setRMItemsValues(processingReport.getPrRawMaterialsItems(), processingReport.getCompanyId(), false);
		setOMItemsValues(processingReport.getPrOtherMaterialsItems(), processingReport.getCompanyId(), false);
		setMPValues(processingReport.getPrMainProducts(), false);
		setBPValues(processingReport.getPrByProducts(), false);

		return processingReport;
	}

	/**
	 * Get the list of {@link PrOtherCharge} of Processing Report form.
	 * @param processingReportId The id of the processing report.
	 * @return The list of Other Charges.
	 */
	private List<PrOtherCharge> getPrOtherCharges(Integer processingReportId) {
		List<PrOtherCharge> otherCharges = prOtherChargeDao.getAllByRefId("processingReportId", processingReportId);
		if(!otherCharges.isEmpty()) {
			for (PrOtherCharge poc : otherCharges) {
				poc.setArLineSetupName(poc.getArLineSetup().getName());
				if(poc.getUnitOfMeasurementId() != null) {
					poc.setUnitMeasurementName(poc.getUnitMeasurement().getName());
				}
			}
		}
		return otherCharges;
	}

	/**
	 * Get the list of {@link PrRawMaterialsItem} of Processing Report form
	 * @param processingReportId The id of the processing report.
	 * @return The list of Raw Materials used.
	 */
	private List<PrRawMaterialsItem> getPrRawMaterials(Integer processingReportId) {
		EBObject ebObject = null;
		List<PrRawMaterialsItem> prRawMaterials = prRawMaterialsItemDao.getAllByRefId("processingReportId", processingReportId);
		for (PrRawMaterialsItem rawMaterial : prRawMaterials) {
			ebObject = getRefObject(rawMaterial.getEbObjectId());
			rawMaterial.setOrigRefObjectId(ebObject.getId());
			rawMaterial.setReferenceObjectId(ebObject.getId());
			rawMaterial.setOrigQty(rawMaterial.getQuantity());
		}
		return prRawMaterials;
	}

	/**
	 * Get the list of {@link PrOtherMaterialsItem} of the Processing Report form.
	 * @param processingReportId The id of the processing report.
	 * @return The list of Other Materials used.
	 */
	private List<PrOtherMaterialsItem> getOtherMaterials(Integer processingReportId) {
		EBObject ebObject = null;
		List<PrOtherMaterialsItem> prOtherMaterials = prOtherMaterialsItemDao.getAllByRefId("processingReportId", processingReportId);
		for (PrOtherMaterialsItem otherMaterial : prOtherMaterials) {
			ebObject = getRefObject(otherMaterial.getEbObjectId());
			otherMaterial.setOrigRefObjectId(ebObject.getId());
			otherMaterial.setReferenceObjectId(ebObject.getId());
			otherMaterial.setOrigQty(otherMaterial.getQuantity());
		}
		return prOtherMaterials;
	}

	/**
	 * Get the reference EB Object using its id.
	 * @param ebObjectId The id of the EB Object.
	 * @return The {@link EBObject}
	 */
	private EBObject getRefObject(Integer ebObjectId) {
		return ooLinkHelper.getReferenceObject(ebObjectId, 2);
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		// do nothing.
	}

	/**
	 * Saved the processing report main object and the associated objects.
	 * @param form The object to be saved in the database.
	 * @param user The logged user.
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		logger.info("Saving processing report.");
		ProcessingReport processingReport = (ProcessingReport) form;
		int processingReportId =  processingReport.getId();
		boolean isNew = processingReportId == 0;
		Date currentDate = new Date();
		AuditUtil.addAudit(processingReport, new Audit(user.getId(), isNew, new Date ()));
		if (isNew) {
			int seqNo = processingReportDao.generateSeqNo(processingReport.getProcessingReportTypeId(), processingReport.getCompanyId());
			logger.info("Successufully generated sequence no: "+seqNo+" for processing type: "+processingReport.getProcessingReportTypeId());
			processingReport.setSequenceNo(seqNo);
		} else {
			ProcessingReport savedPR = processingReportDao.get(processingReportId);
			DateUtil.setCreatedDate(processingReport, savedPR.getCreatedDate());
		}
		logger.info("Saving the processing report header/main object.");
		processingReportDao.saveOrUpdate(processingReport);
		logger.info("Done saving the processing report header/main object.");

		logger.info("Saving the processing report associated objects.");
		saveRMItems(processingReport, user, currentDate);
		saveOMItems(processingReport, user, currentDate);
		saveOtherCharges(processingReport);
		saveMainProducts(processingReport, user, currentDate);
		saveByProducts(processingReport, user, currentDate);
		logger.info("Done saving the processing report associated objects.");
		logger.info("Done saving processing report.");
	}

	private List<Domain> processItemBagQty(Integer itemId, Integer orTypeId, Integer fromObjectId, 
			Double bagQty, User user, Date currentDate) {
		List<Domain> toBeSaved = new ArrayList<>();
		int ibqObjectId = ebObjectService.saveAndGetEbObjectId(user.getId(), ItemBagQuantity.OBJECT_TYPE_ID, currentDate);
		ItemBagQuantity ibq = ItemBagQuantity.getInstanceOf(itemId, ibqObjectId, bagQty);
		AuditUtil.addAudit(ibq, new Audit(user.getId(), true, currentDate));
		toBeSaved.add(ibq);
		toBeSaved.add(ObjectToObject.getInstanceOf(fromObjectId,
				ibqObjectId, orTypeId, user, currentDate));
		return toBeSaved;
	}

	private void saveRMItems (ProcessingReport processingReport, User user, Date currentDate){
		logger.info("Saving save raw material items");
		int processingReportId = processingReport.getId();
		// Retrieve the saved items and delete. 
		List<PrRawMaterialsItem> savedRMItems = 
				prRawMaterialsItemDao.getAllByRefId("processingReportId", processingReportId);
		if (savedRMItems != null && !savedRMItems.isEmpty()) {
			for (PrRawMaterialsItem rmi : savedRMItems) {
				prRawMaterialsItemDao.delete(rmi);
			}
		}
		savedRMItems = null;

		// Save the items.
		List<PrRawMaterialsItem> rawMaterialsItems = processingReport.getPrRawMaterialsItems();
		if (rawMaterialsItems != null && !rawMaterialsItems.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<Domain>();
			for (PrRawMaterialsItem rmi : rawMaterialsItems) {
				rmi.setId(0);
				rmi.setProcessingReportId(processingReportId);
				SaleItemUtil.setNullUnitCostToZero(rmi);
				toBeSaved.add(rmi);
				toBeSaved.addAll(processItemBagQty(rmi.getItemId(), ItemBagQuantity.PR_MAIN_RAW_MAT_BAG_QTY,
						rmi.getEbObjectId(), rmi.getItemBagQuantity(), user, currentDate));
			}
			prRawMaterialsItemDao.batchSave(toBeSaved);
			toBeSaved = null;
		}
		rawMaterialsItems = null;
		logger.info("Sucessfully saved raw material items.");
	}

	private void saveOMItems (ProcessingReport processingReport, User user, Date currentDate) {
		logger.info("Saving other material items.");
		int processingReportId = processingReport.getId();
		// Retrieve the saved items and delete. 
		List<PrOtherMaterialsItem> savedOMItems = 
				prOtherMaterialsItemDao.getAllByRefId("processingReportId", processingReportId);
		if (savedOMItems != null && !savedOMItems.isEmpty()) {
			for (PrOtherMaterialsItem omi : savedOMItems) {
				prOtherMaterialsItemDao.delete(omi);
			}
		}
		savedOMItems = null;

		// Save the items.
		List<PrOtherMaterialsItem> otherMaterialsItems = processingReport.getPrOtherMaterialsItems();
		if (otherMaterialsItems != null && !otherMaterialsItems.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<Domain>();
			for (PrOtherMaterialsItem omi : otherMaterialsItems) {
				omi.setId(0);
				omi.setProcessingReportId(processingReportId);
				SaleItemUtil.setNullUnitCostToZero(omi);
				toBeSaved.add(omi);
				toBeSaved.addAll(processItemBagQty(omi.getItemId(), ItemBagQuantity.PR_OTHER_MAT_BAG_QTY,
						omi.getEbObjectId(), omi.getItemBagQuantity(), user, currentDate));
			}
			prOtherMaterialsItemDao.batchSave(toBeSaved);
			toBeSaved = null;
		}
		otherMaterialsItems = null;
		logger.info("Sucessfully saved other material items.");
	}

	private void saveOtherCharges (ProcessingReport processingReport) {
		logger.info("Saving other charges.");
		int processingReportId = processingReport.getId();
		// Retrieve the saved charges and delete. 
		List<PrOtherCharge> savedOtherCharges = 
				prOtherChargeDao.getAllByRefId("processingReportId", processingReportId);
		if (savedOtherCharges != null && !savedOtherCharges.isEmpty()) {
			for (PrOtherCharge oc : savedOtherCharges) {
				prOtherChargeDao.delete(oc);
			}
		}
		savedOtherCharges = null;

		// Save the other charges.
		List<PrOtherCharge> otherCharges = processingReport.getPrOtherCharges();
		if (otherCharges != null && !otherCharges.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<Domain>();
			for (PrOtherCharge ot : otherCharges) {
				ot.setId(0);
				ot.setProcessingReportId(processingReportId);
				toBeSaved.add(ot);
			}
			prOtherChargeDao.batchSave(toBeSaved);
			toBeSaved = null;
		}
		otherCharges = null;
		logger.info("Sucessfully saved other charges.");
	}

	private void saveMainProducts (ProcessingReport processingReport, User user, Date currentDate) {
		logger.info("Saving main products.");
		int processingReportId = processingReport.getId();
		// Retrieve the saved items and delete. 
		List<PrMainProduct> savedMProducts = 
				prMainProductDao.getAllByRefId("processingReportId", processingReportId);
		if (savedMProducts != null && !savedMProducts.isEmpty()) {
			for (PrMainProduct mp : savedMProducts) {
				prMainProductDao.delete(mp);
			}
		}
		savedMProducts = null;

		// Save the items.
		List<PrMainProduct> mainProducts = processingReport.getPrMainProducts();
		if (mainProducts != null && !mainProducts.isEmpty()) {
			computeAndSaveMPUnitCost(processingReport, user, currentDate);
		}
		mainProducts = null;
		logger.info("Sucessfully saved main products.");
	}

	private void saveByProducts (ProcessingReport processingReport, User user, Date currentDate) {
		logger.info("Saving by products.");
		int processingReportId = processingReport.getId();
		// Retrieve the saved items and delete. 
		List<PrByProduct> savedBProducts = 
				prByProductDao.getAllByRefId("processingReportId", processingReportId);
		if (savedBProducts != null && !savedBProducts.isEmpty()) {
			for (PrByProduct bp : savedBProducts) {
				prByProductDao.delete(bp);
			}
		}
		savedBProducts = null;

		// Save the items.
		List<PrByProduct> byProducts = processingReport.getPrByProducts();
		if (byProducts != null && !byProducts.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<Domain>();
			for (PrByProduct bp : byProducts) {
				bp.setId(0);
				bp.setProcessingReportId(processingReportId);
				bp.setUnitCost(0.0);
				toBeSaved.add(bp);
				toBeSaved.addAll(processItemBagQty(bp.getItemId(), ItemBagQuantity.PR_BY_PRODUCT_BAG_QTY,
						bp.getEbObjectId(), bp.getItemBagQuantity(), user, currentDate));
			}
			prByProductDao.batchSave(toBeSaved);
			toBeSaved = null;
		}
		byProducts = null;
		logger.info("Sucessfully saved by products.");
	}

	/**
	 * Removes the empty lines.
	 * @param processingReport The processing report object.
	 */
	public void processPRItems (ProcessingReport processingReport, boolean isSaving) {
		// Process the associated item objects.
		List<PrRawMaterialsItem> prRawMaterialsItems =
				SaleItemUtil.processSaleItems(processingReport.getPrRawMaterialsItems());
		List<PrOtherMaterialsItem> prOtherMaterialsItems =
				SaleItemUtil.processSaleItems(processingReport.getPrOtherMaterialsItems());
		List<PrMainProduct> prMainProducts = 
				SaleItemUtil.processSaleItems(processingReport.getPrMainProducts());
		List<PrByProduct> prByProducts =
				SaleItemUtil.processSaleItems(processingReport.getPrByProducts());
		// Set the item ids.
		PRItemUtil.setItemIds(prRawMaterialsItems, itemDao);
		PRItemUtil.setItemIds(prOtherMaterialsItems, itemDao);
		PRItemUtil.setItemIds(prMainProducts, itemDao);
		PRItemUtil.setItemIds(prByProducts, itemDao);
		// Set the associated item objects.
		processingReport.setPrRawMaterialsItems(prRawMaterialsItems);
		processingReport.setPrOtherMaterialsItems(prOtherMaterialsItems);
		processingReport.setPrMainProducts(prMainProducts);
		processingReport.setPrByProducts(prByProducts);
		// Set the values for ref ids and unit cost.
		setRMItemsValues(prRawMaterialsItems, processingReport.getCompanyId(), isSaving);
		setOMItemsValues(prOtherMaterialsItems, processingReport.getCompanyId(), isSaving);
		setMPValues(prMainProducts, isSaving);
		setBPValues(prByProducts, isSaving);
		// Free up the memory
		prRawMaterialsItems = null;
		prOtherMaterialsItems = null;
		prMainProducts = null;
		prByProducts = null;
	}

	private void setRMItemsValues (List<PrRawMaterialsItem> prRawMaterialsItems, Integer companyId, boolean isSaving) {
		if (prRawMaterialsItems != null && !prRawMaterialsItems.isEmpty()) {
			for (PrRawMaterialsItem rmi : prRawMaterialsItems) {
				rmi.setStockCode(itemDao.get(rmi.getItemId()).getStockCode());
				Integer referenceObjectId = rmi.getRefenceObjectId();
				if (referenceObjectId == null && rmi.getEbObjectId() != null) {
					EBObject ebObject = objectToObjectDao.getOtherReference(rmi.getEbObjectId(), 
							ORType.RAW_TO_PROCESSED_OR_TYPE_ID);
					referenceObjectId = ebObject.getId();
				}

				if(rmi.getEbObjectId() != null && !isSaving) {
					itemBagQuantityService.setItemBagQty(rmi, rmi.getEbObjectId(), ItemBagQuantity.PR_MAIN_RAW_MAT_BAG_QTY);
				}

				rmi.setRefId(referenceObjectId);
				rmi.setReferenceObjectId(referenceObjectId);
				AvblStocksAndBagsDto avblStocksAndBagsDto = itemBagQuantityDao.getRefAvailableBags(companyId, referenceObjectId, rmi.getItemId(), rmi.getWarehouseId());
				if (avblStocksAndBagsDto != null) {
					rmi.setAvailableStocks(avblStocksAndBagsDto.getTotalStocks());
					rmi.setSource(avblStocksAndBagsDto.getSource());
				}
			}
		}
	}

	private void setOMItemsValues (List<PrOtherMaterialsItem> prOtherMaterialsItems, Integer companyId, boolean isSaving) {
		if (prOtherMaterialsItems != null && !prOtherMaterialsItems.isEmpty()) {
			for (PrOtherMaterialsItem omi : prOtherMaterialsItems) {
				omi.setStockCode(itemDao.get(omi.getItemId()).getStockCode());
				Integer referenceObjectId = omi.getRefenceObjectId();
				if (referenceObjectId == null && omi.getEbObjectId() != null) {
					EBObject ebObject = objectToObjectDao.getOtherReference(omi.getEbObjectId(), 
							ORType.RAW_TO_PROCESSED_OR_TYPE_ID);
					referenceObjectId = ebObject.getId();
				}

				if(omi.getEbObjectId() != null && !isSaving) {
					itemBagQuantityService.setItemBagQty(omi, omi.getEbObjectId(), ItemBagQuantity.PR_OTHER_MAT_BAG_QTY);
				}

				omi.setRefId(referenceObjectId);
				omi.setReferenceObjectId(referenceObjectId);
				AvblStocksAndBagsDto avblStocksAndBagsDto = itemBagQuantityDao.getRefAvailableBags(companyId, referenceObjectId, omi.getItemId(), omi.getWarehouseId());
				if (avblStocksAndBagsDto != null) {
					omi.setAvailableStocks(avblStocksAndBagsDto.getTotalStocks());
					omi.setSource(avblStocksAndBagsDto.getSource());
				}
			}
		}
	}

	private void setMPValues (List<PrMainProduct> prMainProducts, boolean isSaving) {
		if (prMainProducts != null && !prMainProducts.isEmpty()) {
			for (PrMainProduct mp : prMainProducts) {
				mp.setStockCode(itemDao.get(mp.getItemId()).getStockCode());
				Integer referenceObjectId = mp.getRefenceObjectId();
				if (referenceObjectId == null && mp.getEbObjectId() != null) {
					EBObject ebObject = objectToObjectDao.getOtherReference(mp.getEbObjectId(), 
							ORType.PARENT_OR_TYPE_ID);
					referenceObjectId = ebObject.getId();
				}

				if(mp.getEbObjectId() != null && !isSaving) {
					itemBagQuantityService.setItemBagQty(mp, mp.getEbObjectId(), ItemBagQuantity.PR_OTHER_MAT_BAG_QTY);
				}

				mp.setRefId(referenceObjectId);
				mp.setReferenceObjectId(referenceObjectId);
			}
		}
	}

	private void setBPValues (List<PrByProduct> prByProducts, boolean isSaving) {
		if (prByProducts != null && !prByProducts.isEmpty()) {
			for (PrByProduct bp : prByProducts) {
				bp.setStockCode(itemDao.get(bp.getItemId()).getStockCode());
				Integer referenceObjectId = bp.getRefenceObjectId();
				if (referenceObjectId == null && bp.getEbObjectId() != null) {
					EBObject ebObject = objectToObjectDao.getOtherReference(bp.getEbObjectId(), 
							ORType.PARENT_OR_TYPE_ID);
					referenceObjectId = ebObject.getId();
				}

				if(bp.getEbObjectId() != null && !isSaving) {
					itemBagQuantityService.setItemBagQty(bp, bp.getEbObjectId(), ItemBagQuantity.PR_OTHER_MAT_BAG_QTY);
				}

				bp.setRefId(referenceObjectId);
				bp.setReferenceObjectId(referenceObjectId);
			}
		}
	}

	private static class PRItemUtil<T extends BaseItem> {
		private static <T extends BaseItem> void setItemIds (List<T> baseItems, ItemDao itemDao) {
			if (baseItems != null && !baseItems.isEmpty()) {
				for (BaseItem bi : baseItems) {
					String stockCode = bi.getStockCode();
					if (stockCode != null && !stockCode.trim().isEmpty()) {
						Item item = itemDao.getItemByStockCode(stockCode, null);
						if (item != null) {
							bi.setItemId(item.getId());
						}
					}
				}
				baseItems = null;
			}
		}
	}

	/**
	 * Search processing report forms.
	 * @param processingTypeId The type Id of Processing Report
	 * @param searchCriteria The search criteria.
	 */
	public List<FormSearchResult> searchProcessingReports(int processingTypeId, String searchCriteria) {
		logger.info("Searching Processing Report.");
		logger.debug("Searching for: "+searchCriteria.trim());
		Page<ProcessingReport> processingReports = 
				processingReportDao.searchProcessingReports(searchCriteria.trim(), processingTypeId, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		for (ProcessingReport pr : processingReports.getData()) {
			logger.trace("Retrieved Sequence No. " + pr.getSequenceNo());
			title = String.valueOf(pr.getFormattedPRNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", pr.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(pr.getDate())));
			status = pr.getFormWorkflow().getCurrentStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(pr.getId(), title, properties));
		}
		return result;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return processingReportDao.get(id).getFormWorkflow();
	}

	/**
	 * Get the unit cost by eb object id.
	 * @param ebObjectId The eb object id.
	 * @return The unit cost.
	 */
	public double getUCByEBObjectId (int ebObjectId) {
		RReceivingReportRmItem rrmItem = rrmItemDao.getByEbObjectId(ebObjectId);
		if (rrmItem != null) {
			RReceivingReportItem rrItem = rrItemDao.get(rrmItem.getrReceivingReportItemId());
			return rrItem.getUnitCost();
		}

		PrRawMaterialsItem prRawMaterialsItem = prRawMaterialsItemDao.getByEbObjectId(ebObjectId);
		if (prRawMaterialsItem != null) {
			return prRawMaterialsItem.getUnitCost();
		}

		PrOtherMaterialsItem prOtherMaterialsItem = prOtherMaterialsItemDao.getByEbObjectId(ebObjectId);
		if (prOtherMaterialsItem != null) {
			return prOtherMaterialsItem.getUnitCost();
		}

		PrMainProduct prMainProduct = prMainProductDao.getByEbObjectId(ebObjectId);
		if (prMainProduct != null) {
			return prMainProduct.getUnitCost();
		}

		return 0;
	}

	/**
	 * Compute the total main product unit cost and save the finished products.
	 * @param processingReport The processing report object.
	 */
	public void computeAndSaveMPUnitCost (ProcessingReport processingReport, User user, Date currentDate) {
		double totalAmount = 0;
		double totalMPQty = 0; // Total quantity of the Main Product/s

		List<PrRawMaterialsItem> prRawMaterialsItems = processingReport.getPrRawMaterialsItems();
		List<PrOtherMaterialsItem> prOtherMaterialsItems = processingReport.getPrOtherMaterialsItems();
		List<PrOtherCharge> prOtherCharges = processingReport.getPrOtherCharges();
		List<PrMainProduct> prMainProducts = processingReport.getPrMainProducts();

		// Add all pr raw material item unit cost to the total amount.
		if (prRawMaterialsItems != null && !prRawMaterialsItems.isEmpty()) {
			for (PrRawMaterialsItem rmi : prRawMaterialsItems) {
				totalAmount += (rmi.getUnitCost() * rmi.getQuantity());
			}
		}

		// Add all pr other material item unit cost to the total amount.
		if (prOtherMaterialsItems != null && !prOtherMaterialsItems.isEmpty()) {
			for (PrOtherMaterialsItem omi : prOtherMaterialsItems) {
				totalAmount += (omi.getUnitCost() * omi.getQuantity());
			}
		}

		// Add all pr other charge amount to the total amount.
		if (prOtherCharges != null && !prOtherCharges.isEmpty()) {
			for (PrOtherCharge oc : prOtherCharges) {
				totalAmount += oc.getAmount();
			}
		}

		int fromUomId = 0;
		int toUomId = unitMeasurementDao.getKilo().getId();
		// Add all main product quantity.
		if (prMainProducts != null && !prMainProducts.isEmpty()) {
			for (PrMainProduct  mp : prMainProducts) {
				// Compute the quantity to Kilo based on the UOM conversion table.
				mp.setItem(itemDao.get(mp.getItemId()));
				fromUomId = mp.getItem().getUnitMeasurementId();
				double convertedQtyPerKg = convertQuantity(fromUomId, toUomId, mp.getQuantity());
				mp.setConvertedQtyPerKg(convertedQtyPerKg);
				totalMPQty += convertedQtyPerKg;
			}
		}

		double computedUcPerKg = totalAmount / totalMPQty;
		logger.debug("Computed Unit Cost of finished products per kilo: "+computedUcPerKg);
		// Save Finished products
		List<Domain> toBeSaved = new ArrayList<Domain>();
		for (PrMainProduct mainProd : prMainProducts) {
			mainProd.setProcessingReportId(processingReport.getId());
			double amount = computedUcPerKg * mainProd.getConvertedQtyPerKg();
			mainProd.setUnitCost(amount / mainProd.getQuantity());
			toBeSaved.add(mainProd);
			toBeSaved.addAll(processItemBagQty(mainProd.getItemId(), ItemBagQuantity.PR_MAIN_PRODUCT_BAG_QTY,
					mainProd.getEbObjectId(), mainProd.getItemBagQuantity(), user, currentDate));
		}
		prMainProductDao.batchSave(toBeSaved);
	}

	public List<Recovery> getRecoveries (int processingReportId) {
		logger.info("Getting the recoveries for " + processingReportId);
		List<Recovery> recoveries = new ArrayList<>();
		List<PrRawMaterialsItem> prRawMaterialsItems = 
				prRawMaterialsItemDao.getAllByRefId("processingReportId", processingReportId);
		List<PrMainProduct> prMainProducts = 
				prMainProductDao.getAllByRefId("processingReportId", processingReportId);
		int toUnitMeasurementId = unitMeasurementDao.getKilo().getId();
		// Add the raw materials.
		if (prRawMaterialsItems != null && !prRawMaterialsItems.isEmpty()) {
			for (PrRawMaterialsItem prItem : prRawMaterialsItems) {
				Item item =  itemDao.get(prItem.getItemId());
				int fromUnitMeasurementId = item.getUnitMeasurementId();
				double convQuantity = 
						convertQuantity(fromUnitMeasurementId, toUnitMeasurementId, prItem.getQuantity());
				Recovery recovery = Recovery.getInstanceOf(item, convQuantity, false);
				recoveries.add(recovery);
			}
		}
		// Free up the memory
		prRawMaterialsItems = null;

		// Add the main products / outputs.
		if (prMainProducts != null && !prMainProducts.isEmpty()) {
			for (PrMainProduct prMp : prMainProducts) {
				Item item = itemDao.get(prMp.getItemId());
				int fromUnitMeasurementId = item.getUnitMeasurementId();
				double convQuantity = 
						convertQuantity(fromUnitMeasurementId, toUnitMeasurementId, prMp.getQuantity());
				Recovery recovery = Recovery.getInstanceOf(item, convQuantity, true);
				recoveries.add(recovery);
			}
		}
		// Free up the memory
		prMainProducts = null;

		logger.info("Done getting the recoveries for " + processingReportId);
		return recoveries;
	}

	private double convertQuantity (int fromUnitMeasurementId, int toUnitMeasurementId, double qty) {
		// If the main raw materials and main products has the 
		// same unit of measurement, there must be no conversion.
		if (fromUnitMeasurementId != toUnitMeasurementId) {
			 qty = uomConversionService.convert(fromUnitMeasurementId, toUnitMeasurementId, qty);
		}
		return qty;
	}

	/**
	 * Get the recovery percentage.
	 */
	public double getRecoveryPercentage (List<Recovery> recoveries) {
		logger.info("Computing the recovery percentage.");
		double totalMaterials = 0;
		double totalMainProducts = 0;
		if (recoveries != null && !recoveries.isEmpty()) {
			for (Recovery recovery : recoveries) {
				if (recovery.isMainProduct()) {
					totalMainProducts += recovery.getQuantity();
				} else {
					totalMaterials += recovery.getQuantity();
				}
			}
		}
		logger.info("Done computing the recovery percentage.");
		return (totalMainProducts / totalMaterials) * 100;
	}

	/**
	 * Get the recovery percentage.
	 */
	public double getRecoveryPercentage (int processingReportId) {
		return getRecoveryPercentage (getRecoveries(processingReportId));
	}

	/**
	 * Get the milling recovery report.
	 * @param processingReportId The processing report id.
	 * @return The list of milling recovery report objects.
	 */
	public List<MillingRecoveryReport> getMillingRecoveryReports (int processingReportId) {
		List<MillingRecoveryReport> millingRecoveryReports = new ArrayList<>();
		List<PrRawMaterialsItem> prRawMaterialsItems = 
				prRawMaterialsItemDao.getAllByRefId("processingReportId", processingReportId);
		List<PrOtherMaterialsItem> prOtherMaterialsItems = 
				prOtherMaterialsItemDao.getAllByRefId("processingReportId", processingReportId);
		List<PrOtherCharge> prOtherCharges = 
				prOtherChargeDao.getAllByRefId("processingReportId", processingReportId);
		// Main Raw Materials
		logger.info("Processing/adding the main raw materials.");
		if (prRawMaterialsItems != null && !prRawMaterialsItems.isEmpty()) {
			for (PrRawMaterialsItem prItem : prRawMaterialsItems) {
				Item item = itemDao.get(prItem.getItemId());
				if (item != null) {
					UnitMeasurement um = unitMeasurementDao.get(item.getUnitMeasurementId());
					if(prItem.getEbObjectId() != null) {
						itemBagQuantityService.setItemBagQty(prItem, prItem.getEbObjectId(), ItemBagQuantity.PR_MAIN_RAW_MAT_BAG_QTY);
					}
					MillingRecoveryReport mrr = MillingRecoveryReport.getInstanceOf(item.getStockCode(), 
							item.getDescription(), um.getName(), prItem.getItemBagQuantity(), prItem.getQuantity(), 
							prItem.getUnitCost(), prItem.getQuantity() * prItem.getUnitCost());
					millingRecoveryReports.add(mrr);
				}
			}
		}
		logger.info("Done processing/adding the main raw materials.");

		// Other Raw Materials
		logger.info("Processing/adding the other raw materials.");
		if (prOtherMaterialsItems != null && !prOtherMaterialsItems.isEmpty()) {
			for (PrOtherMaterialsItem prItem : prOtherMaterialsItems) {
				Item item = itemDao.get(prItem.getItemId());
				if (item != null) {
					UnitMeasurement um = unitMeasurementDao.get(item.getUnitMeasurementId());
					if(prItem.getEbObjectId() != null) {
						itemBagQuantityService.setItemBagQty(prItem, prItem.getEbObjectId(), ItemBagQuantity.PR_OTHER_MAT_BAG_QTY);
					}
					MillingRecoveryReport mrr = MillingRecoveryReport.getInstanceOf(item.getStockCode(),
							item.getDescription(), um.getName(), prItem.getItemBagQuantity(), prItem.getQuantity(),
							prItem.getUnitCost(), prItem.getQuantity() * prItem.getUnitCost());
					millingRecoveryReports.add(mrr);
				}
			}
		}
		logger.info("Done processing/adding the other raw materials.");

		// Other Charges
		logger.info("Processing/adding the other charges.");
		if (prOtherCharges != null && !prOtherCharges.isEmpty()) {
			for (PrOtherCharge prOtherCharge : prOtherCharges) {
				ArLineSetup arLineSetup = arLineSetupDao.get(prOtherCharge.getArLineSetupId());
				if (arLineSetup != null) {
					UnitMeasurement um = null;
					if (prOtherCharge.getUnitOfMeasurementId() != null) {
						um = unitMeasurementDao.get(prOtherCharge.getUnitOfMeasurementId());
					}
					String uom = um != null ? um.getName() : "";
					double quantity = prOtherCharge.getQuantity() != null ?
							prOtherCharge.getQuantity() : 0;
					MillingRecoveryReport mrr = MillingRecoveryReport.getInstanceOf(arLineSetup.getName(), "", uom, null,
							quantity, 0.0, prOtherCharge.getAmount());
					millingRecoveryReports.add(mrr);
				}
			}
		}
		logger.info("Done processing/adding the other charges.");

		logger.info("Free up the memory.");
		prRawMaterialsItems = null;
		prOtherMaterialsItems = null;
		prOtherCharges = null;
		logger.info("Finished the free up of memory");
		return millingRecoveryReports;
	}

	public List<PrMainProduct> getMainProductsPrintout (int processingReportId) {
		List<PrMainProduct> prMainProducts = 
				prMainProductDao.getAllByRefId("processingReportId", processingReportId);
		if (prMainProducts != null && !prMainProducts.isEmpty()) {
			for (PrMainProduct prmp : prMainProducts) {
				Item item = itemDao.get(prmp.getItemId());
				UnitMeasurement um = unitMeasurementDao.get(item.getUnitMeasurementId());
				item.setUnitMeasurement(um);
				prmp.setItem(item);
				if(prmp.getEbObjectId() != null) {
					itemBagQuantityService.setItemBagQty(prmp, prmp.getEbObjectId(), ItemBagQuantity.PR_MAIN_PRODUCT_BAG_QTY);
				}
			}
		}
		return prMainProducts;
	}

	public List<PrByProduct> getByProductsPrintout (int processingReportId) {
		List<PrByProduct> prByProducts = 
				prByProductDao.getAllByRefId("processingReportId", processingReportId);
		if (prByProducts != null && !prByProducts.isEmpty()) {
			for (PrByProduct prbp : prByProducts) {
				Item item = itemDao.get(prbp.getItemId());
				UnitMeasurement um = unitMeasurementDao.get(item.getUnitMeasurementId());
				item.setUnitMeasurement(um);
				prbp.setItem(item);
				if(prbp.getEbObjectId() != null) {
					itemBagQuantityService.setItemBagQty(prbp, prbp.getEbObjectId(), ItemBagQuantity.PR_BY_PRODUCT_BAG_QTY);
				}
			}
		}
		return prByProducts;
	}

	public List<ProcessingReportPrintout> getPRPrintout (ProcessingReport pr) {
		List<ProcessingReportPrintout> ret = new ArrayList<>();
		int processingReportId = pr.getId();
		List<PrMainProduct> prMainProducts = getMainProductsPrintout(processingReportId);
		if(pr.getProcessingReportTypeId().equals(ProcessingReportType.MILLING_REPORT)) {
			List<Recovery> recoveries = getRecoveries(processingReportId);
			List<MillingRecoveryReport> millingRecoveryReports = getMillingRecoveryReports(processingReportId);
			List<PrByProduct> prByProducts = getByProductsPrintout(processingReportId);
			ProcessingReportPrintout prp = ProcessingReportPrintout.getInstanceOf(recoveries, 
					millingRecoveryReports, prMainProducts, prByProducts);
			ret.add(prp);
		} else {
			ProcessingReportPrintout printout = new ProcessingReportPrintout();
			printout.setPrRawMaterials(getPrRawMaterials(processingReportId));
			setRMItemsValues(printout.getPrRawMaterials(), pr.getCompanyId(), false);
			printout.setOtherFees(getPrOtherCharges(processingReportId));
			printout.setPrMainProducts(prMainProducts);
			ret.add(printout);
		}
		return ret;
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return processingReportDao.getByEbObjectId(ebObjectId);
	}

	/**
	 * Get the Processing Type object using its unique id.
	 * @param processingTypeId The id of the processing report type.
	 * @return The {@link ProcessingReportType}.
	 */
	public ProcessingReportType getProcessingType(int processingTypeId) {
		return processingTypeDao.get(processingTypeId);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		ProcessingReport processingReport = processingReportDao.getByWorkflowId(
				currentWorkflowLog.getFormWorkflowId());
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID
				&& processingReport.getFormWorkflow().isComplete()) {
			String errorMessage = null;
			//PR Main Product
			List<PrMainProduct> mainProducts =
					prMainProductDao.getAllByRefId("processingReportId", processingReport.getId());

			//PR By Product
			List<PrByProduct> byProducts =
					prByProductDao.getAllByRefId("processingReportId", processingReport.getId());

			if(!mainProducts.isEmpty() || !byProducts.isEmpty()) {
				List<PrItem> prItems = setPrItems(mainProducts, byProducts);
				errorMessage = ValidationUtil.validateToBeCancelledRefForm(itemService, prItems);
			}

			if(errorMessage != null) {
				bindingResult.reject("workflowMessage", errorMessage);
				currentWorkflowLog.setWorkflowMessage(errorMessage);
			}
		}
	}

	private List<PrItem> setPrItems(List<PrMainProduct> mainProducts, List<PrByProduct> byProducts){
		List<PrItem> prItems = new ArrayList<>();
		PrItem prItem = null;
		for (PrMainProduct t : mainProducts) {
			prItem = new PrItem();
			prItem.setEBObjecctId(t.getEbObjectId());
			prItem.setStockCode(t.getItem().getStockCode());
			prItems.add(prItem);
		}
		for (PrByProduct t : byProducts) {
			prItem = new PrItem();
			prItem.setEBObjecctId(t.getEbObjectId());
			prItem.setStockCode(t.getItem().getStockCode());
			prItems.add(prItem);
		}
		return prItems;
	}

	private class PrItem implements OOChild{
		private Integer eBObjecctId;
		private String stockCode;

		public void setEBObjecctId(Integer eBObjecctId) {
			this.eBObjecctId = eBObjecctId;
		}

		@Override
		public EBObject getEbObject() {
			return null;
		}

		@Override
		public void setEbObjectId(Integer eBObjecctId) {
			this.eBObjecctId = eBObjecctId;
		}

		@Override
		public Integer getObjectTypeId() {
			return null;
		}

		@Override
		public Integer getEbObjectId() {
			return eBObjecctId;
		}

		@Override
		public Integer getRefenceObjectId() {
			return null;
		}

		public void setStockCode(String stockCode) {
			this.stockCode = stockCode;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("PrItem [eBObjecctId=").append(eBObjecctId)
					.append(", stockCode=").append(stockCode).append("]");
			return builder.toString();
		}
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return processingReportDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		String formName = "";

		ProcessingReport processingReport = processingReportDao.getByEbObjectId(ebObjectId);
		Integer pId = processingReport.getId();
		int typeId = processingReport.getProcessingReportTypeId();
		switch (typeId) {
			case ProcessingReportType.MILLING_ORDER:
				formName = "Processing - Milling Order";
				break;

			case ProcessingReportType.MILLING_REPORT:
				formName = "Processing - Milling Report";
				break;

			case ProcessingReportType.PASS_IN:
				formName = "Processing - Pass In";
				break;

			case ProcessingReportType.PASS_OUT:
				formName = "Processing - Pass Out";
				break;
		}
		FormProperty property = workflowHandler.getProperty(processingReport.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId="+ pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = processingReport.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + processingReport.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + processingReport.getCompany().getNumberAndName())
				.append(" " + processingReport.getRefNumber())
				.append("<b> DATE : </b>" + DateUtil.formatDate(processingReport.getDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case ProcessingReport.PR_MILLING_REPORT_OBJECT_TYPE_ID:
		case ProcessingReport.PR_MILLING_ORDER_OBJECT_TYPE_ID:
		case ProcessingReport.PR_PASS_IN_OBJECT_TYPE_ID:
		case ProcessingReport.PR_PASS_OUT_OBJECT_TYPE_ID:
			return processingReportDao.getByEbObjectId(ebObjectId);
		case PrRawMaterialsItem.OBJECT_TYPE_ID:
			return prRawMaterialsItemDao.getByEbObjectId(ebObjectId);
		case PrOtherMaterialsItem.OBJECT_TYPE_ID:
			return prOtherMaterialsItemDao.getByEbObjectId(ebObjectId);
		case PrOtherCharge.OBJECT_TYPE_ID:
			return prOtherChargeDao.getByEbObjectId(ebObjectId);
		case PrMainProduct.OBJECT_TYPE_ID:
			return prMainProductDao.getByEbObjectId(ebObjectId);
		case PrByProduct.OBJECT_TYPE_ID:
			return prByProductDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}