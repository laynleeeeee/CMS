package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import eulap.eb.dao.CashSaleItemDao;
import eulap.eb.dao.CashSaleReturnArLineDao;
import eulap.eb.dao.CashSaleReturnDao;
import eulap.eb.dao.CashSaleReturnItemDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleArLine;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnArLine;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.CashSaleReturnDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Class that handles all the business logic of {@link CashSaleReturn}

 *
 */
@Service
public class CashSaleReturnService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(CashSaleReturnService.class);
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private CashSaleReturnDao cashSaleReturnDao;
	@Autowired
	private CashSaleReturnItemDao cashSaleReturnItemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private CashSaleItemDao cashSaleItemDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private CashSaleReturnArLineDao cashSaleReturnArLineDao;
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusDao formStatusDao;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return cashSaleReturnDao.get(id).getFormWorkflow();
	}

	/**
	 * Get the cash sale return object by id.
	 * @param cashSaleReturnId The cash sale return id.
	 * @return The cash sale return object.
	 */
	public CashSaleReturn getCashSaleReturn(Integer cashSaleReturnId) {
		CashSaleReturn cashSaleReturn = cashSaleReturnDao.get(cashSaleReturnId);
		double totalAmount = cashSaleReturnItemDao.getTotalCSRAmount(cashSaleReturnId);
		double totalOtherCharges = getTotalcsrArLinesAmt(cashSaleReturnId);
		cashSaleReturn.setTotalAmount(totalAmount + totalOtherCharges);
		return cashSaleReturn;
	}

	/**
	 * Get the label name for reference cash sales form
	 * @param cashSaleId The cash sale id
	 * @return label name for reference cash sales form
	 */
	public String getReferenceCs(int cashSaleId) {
		CashSale refCs = cashSaleService.getCashSale(cashSaleId);
		return cashSaleService.getCsLabel(refCs.getCashSaleTypeId())
				+ "No. " + refCs.getCsNumber();
	}

	/**
	 * Get the list of cash sale return items
	 * @param csrId The cash sale return id
	 * @return The list of cash sale return items
	 */
	public List<CashSaleReturnItem> getCsReturnItems(int csrId) {
		return cashSaleReturnItemDao.getCashSaleReturnItems(csrId, null, null, false);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		CashSaleReturn cashSaleReturn = (CashSaleReturn) form;
		boolean isNew = cashSaleReturn.getId() == 0;
		Date currentDate = new Date();
		Integer userId = user.getId();
		AuditUtil.addAudit(cashSaleReturn, new Audit(userId, isNew, currentDate));
		// Save the form header here
		processAndSaveCsr(cashSaleReturn, isNew);
		// Save the item lines here
		Integer csrId = cashSaleReturn.getId();
		try {
			allocateAndSaveItems(cashSaleReturn);
			saveOtherCharges(cashSaleReturn.getCashSaleReturnArLines(), csrId, isNew);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		logger.info("Successfully saved the CSR with sequence no.: "+cashSaleReturn.getFormattedCSRNumber());
	}

	/**
	 * Save and reallocate items
	 */
	private void allocateAndSaveItems(CashSaleReturn cashSaleReturn) throws CloneNotSupportedException {
		List<CashSaleReturnItem> csrItems = cashSaleReturn.getCashSaleReturnItems();
		if (csrItems != null && !csrItems.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			Map<AllocatorKey, WeightedAveItemAllocator<CashSaleReturnItem>> itemId2CostAllocator =
					new HashMap<AllocatorKey, WeightedAveItemAllocator<CashSaleReturnItem>>();
			AllocatorKey key = null;
			for (CashSaleReturnItem csrItem : csrItems) {
				if (csrItem.getCashSaleItemId() == null) { // Exchange
					logger.debug("Performing the unit cost allocation for the Cash Sale Return Items.");
					WeightedAveItemAllocator<CashSaleReturnItem> itemAllocator = itemId2CostAllocator.get(csrItem.getItemId());
					if (itemAllocator == null) {
						itemAllocator = new WeightedAveItemAllocator<CashSaleReturnItem>(itemDao, itemService,
								csrItem.getItemId(), csrItem.getWarehouseId(), cashSaleReturn.getDate());
						key = AllocatorKey.getInstanceOf(csrItem.getItemId(), csrItem.getWarehouseId());
						itemId2CostAllocator.put(key, itemAllocator);
					}

					List<CashSaleReturnItem> allocatedCsrItems = itemAllocator.allocateCost(csrItem);
					SaleItemUtil<CashSaleReturnItem> saleItemUtil = new SaleItemUtil<CashSaleReturnItem>();
					allocatedCsrItems =  saleItemUtil.processDiscountAndAmount(allocatedCsrItems, itemDiscountService);
					for (CashSaleReturnItem csri : allocatedCsrItems) {
						logger.debug("Successfully allocated the item id: "+csri.getItemId()
								+" with unit cost: "+csri.getUnitCost());
						csri.setCashSaleReturnId(cashSaleReturn.getId());
						SaleItemUtil.setNullUnitCostToZero(csri);
						toBeSaved.add(csri);
					}
				} else { 
					csrItem.setCashSaleReturnId(cashSaleReturn.getId());
					int cashSaleItemId = csrItem.getCashSaleItemId();

					CashSaleItem csi = cashSaleItemDao.get(cashSaleItemId);	
					int warehouseId = csi.getWarehouseId();
					int cashSaleId = csi.getCashSaleId();
					List<CashSaleItem> csris = cashSaleItemDao.getCashSaleItems(cashSaleId, csi.getItemId(), warehouseId);
					List<CashSaleReturnItem> allocatedSEItems = SaleItemUtil.processSalesReturn(csrItem, csris, itemDiscountService);

					// Process the discount and amount before saving.
					SaleItemUtil<CashSaleReturnItem> saleItemUtil = new SaleItemUtil<CashSaleReturnItem>();
					allocatedSEItems =  saleItemUtil.processDiscountAndAmount(allocatedSEItems, itemDiscountService);
					for (CashSaleReturnItem csri : allocatedSEItems) {
						toBeSaved.add(csri);
					}
				}
			}
			cashSaleReturnItemDao.batchSave(toBeSaved);
		}
	}

	/**
	 * Saving of other charges.
	 * @param csrArLines The csr ar lines.
	 * @param cashSaleReturnId The cash sales return id.
	 * @param isNew True if already exist cash sale return, otherwise false.
	 */
	public void saveOtherCharges(List<CashSaleReturnArLine> csrArLines, int cashSaleReturnId, boolean isNew) {
		if (!isNew) {
			logger.debug("Deleting saved Other Charges.");
			List<CashSaleReturnArLine> toBeDeleted = getCsrArLine(cashSaleReturnId);
			for (CashSaleReturnArLine csa : toBeDeleted) {
				cashSaleReturnArLineDao.delete(csa.getId());
			}
		}

		for (CashSaleReturnArLine csra : csrArLines) {
			csra.setCashSaleReturnId(cashSaleReturnId);
			cashSaleReturnArLineDao.save(csra);
		}
		logger.info("Successfully saved Other Charges of Cash Sale Return "+cashSaleReturnId);
	}

	/**
	 * Get the detailed AR Lines of Cash Sales.
	 * @param cashSaleId The id of the cash sales.
	 * @return The list of AR Lines
	 */
	public List<CashSaleReturnArLine> getDetailedArLines(Integer cashSaleReturnId) {
		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		otherCharges.addAll(getCsrArLine(cashSaleReturnId));
		List<AROtherCharge> processedList = cashSaleService.getDetailOtherCharges(otherCharges);
		List<CashSaleReturnArLine> ret = new ArrayList<CashSaleReturnArLine>();
		for (AROtherCharge oc : processedList) {
			ret.add((CashSaleReturnArLine) oc);
		}
		return ret;
	}


	/**
	 * Save the {@link CashSaleReturn} object and deletes the CSR Items on edit.
	 */
	public void processAndSaveCsr(CashSaleReturn cashSaleReturn, boolean isNew) {
		if (cashSaleReturn.getRefCashSaleReturnId() != null && cashSaleReturn.getRefCashSaleReturnId() == 0){
			cashSaleReturn.setRefCashSaleReturnId(null);
		}
		if (cashSaleReturn.getCashSaleId() != null && cashSaleReturn.getCashSaleId() == 0) {
			cashSaleReturn.setCashSaleId(null);
		}
		if (isNew) {
			int csrNumber = cashSaleReturnDao.generateCsrNumber(cashSaleReturn.getCompanyId(), cashSaleReturn.getCashSaleTypeId());
			logger.info("Generated sequence number: "+csrNumber+" for type: "+cashSaleReturn.getCashSaleTypeId());
			cashSaleReturn.setCsrNumber(csrNumber);
		} else {
			CashSaleReturn savedCSR = getCashSaleReturn(cashSaleReturn.getId());
			DateUtil.setCreatedDate(cashSaleReturn, savedCSR.getCreatedDate());
			// Get the CSR Items
			List<CashSaleReturnItem> savedCSRItems = getCsReturnItems(cashSaleReturn.getId());
			if (!savedCSRItems.isEmpty()) {
				List<Integer> toBeDeletedCSRItems = new ArrayList<Integer>();
				for (CashSaleReturnItem csrItem : savedCSRItems) {
					if (cashSaleReturn.getCashSaleTypeId().equals(CashSaleType.INDIV_SELECTION)) {
						ItemBagQuantity ibq =  itemBagQuantityDao.getByRefId(
								csrItem.getEbObjectId(), ItemBagQuantity.CSR_IS_BAG_QTY);
						if (ibq != null) {
							ibq.setActive(false);
							itemBagQuantityDao.update(ibq);
						}
					}
					toBeDeletedCSRItems.add(csrItem.getId());
				}
				cashSaleReturnItemDao.delete(toBeDeletedCSRItems);
				toBeDeletedCSRItems = null;
			}
		}
		cashSaleReturnDao.saveOrUpdate(cashSaleReturn);
	}

	/**
	 * Convert the cash sale object to cash sale return.
	 * @param cashSale The cash sale object to be converted.
	 * @return The converted cash sale return object.
	 */
	public CashSaleReturn convertCSToCSR (CashSale cashSale) {
		if (cashSale != null) {
			Integer typeId = cashSale.getCashSaleTypeId();
			boolean isIndividualSelection = typeId.equals(CashSaleType.INDIV_SELECTION);
			CashSaleReturn csr = new CashSaleReturn();
			csr.setCashSaleId(cashSale.getId());
			csr.setRefCashSaleReturnId(null);
			csr.setCompanyId(cashSale.getCompanyId());
			csr.setArCustomerId(cashSale.getArCustomerId());
			csr.setArCustomerAccountId(cashSale.getArCustomerAccountId());
			csr.setSalesInvoiceNo(cashSale.getSalesInvoiceNo());
			csr.setCompany(cashSale.getCompany());
			csr.setArCustomer(cashSale.getArCustomer());
			csr.setArCustomerAccount(cashSale.getArCustomerAccount());
			csr.setCashSaleTypeId(cashSale.getCashSaleTypeId());
			csr.setReferenceNo(cashSaleService.getCsLabel(typeId) + cashSale.getCsNumber());
			if(cashSale.getWtAcctSettingId() != null) {
				csr.setWtAcctSetting(cashSale.getWtAcctSetting());
				csr.setWtAcctSettingId(cashSale.getWtAcctSettingId());
				csr.setWtAmount(cashSale.getWtAmount());
			}
			List<CashSaleItem> cashSaleItems = cashSaleService.getCSItems(cashSale.getId());
 			if (cashSaleItems != null && !cashSaleItems.isEmpty()) {
				if (!isIndividualSelection) {
					// Applicable only for FIFO items.
					SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
					cashSaleItems = saleItemUtil.processSaleItemsForViewing(cashSaleItems);
				}
				List<CashSaleReturnItem> cashSaleReturnItems = new ArrayList<CashSaleReturnItem>();
				Item item = null;
				CashSaleReturnItem csri = null;
				for (CashSaleItem csi : cashSaleItems) {
					if (csi.getQuantity() < 0){
						continue;
					}
					if (isIndividualSelection) {
						itemBagQuantityService.setItemBagQty(csi, csi.getEbObjectId(), ItemBagQuantity.CS_IS_BAG_QTY);
					}
					item = csi.getItem();
					csri = new CashSaleReturnItem();
					csri.setStockCode(item.getStockCode());
					csri.setItem(item);
					csri.setItemId(csi.getItemId());
					csri.setWarehouseId(csi.getWarehouseId());
					csri.setQuantity(-csi.getQuantity());
					csri.setSrp(csi.getSrp());
					csri.setOrigSrp(csi.getItemSrp().getSrp());
					csri.setItemSrpId(csi.getItemSrpId());
					csri.setRefQuantity(csi.getQuantity());
					csri.setItemDiscountId(csi.getItemDiscountId());
					csri.setItemAddOnId(csi.getItemAddOnId());
					csri.setDiscount(csi.getDiscount());
					csri.setAmount(csi.getAmount());
					csri.setCashSaleItemId(csi.getId());
					csri.setSalesRefId(csi.getId());
					csri.setUnitCost(csi.getUnitCost());
					csri.setOrigRefObjectId(csi.getEbObjectId());
					csri.setItemBagQuantity(csi.getItemBagQuantity());
					csri.setOrigBagQty(csi.getItemBagQuantity());
					csri.setTaxTypeId(csi.getTaxTypeId());
					csri.setVatAmount(csi.getVatAmount());
					cashSaleReturnItems.add(csri);

					// clear values
					item = null;
					csri = null;
				}
				csr.setCashSaleReturnItems(cashSaleReturnItems);
			}
			logger.debug("Successfully converted the CS to CSR object.");
			List<CashSaleArLine> cashSaleArLines = cashSaleService.getDetailedArLines(cashSale.getId());
			if (cashSaleArLines != null && !cashSaleArLines.isEmpty()){
				List<CashSaleReturnArLine> cashSaleReturnArLines = new ArrayList<CashSaleReturnArLine>();
				CashSaleReturnArLine csrArLine = null;
				for (CashSaleArLine csal : cashSaleArLines) {
					csrArLine = new CashSaleReturnArLine();
					csrArLine.setAmount(-csal.getAmount());
					csrArLine.setArLineSetupName(csal.getArLineSetup().getName());
					csrArLine.setCashSaleALRefId(csal.getId());
					csrArLine.setArLineSetupId(csal.getArLineSetup().getId());
					csrArLine.setUnitOfMeasurementId(csal.getUnitOfMeasurementId());
					csrArLine.setUnitMeasurementName(csal.getUnitMeasurement() != null ?
							csal.getUnitMeasurement().getName() : "");
					csrArLine.setUpAmount(csal.getUpAmount() != null ? -csal.getUpAmount() : 0);
					csrArLine.setQuantity(csal.getQuantity());
					csrArLine.setTaxTypeId(csal.getTaxTypeId());
					csrArLine.setVatAmount(csal.getVatAmount());
					cashSaleReturnArLines.add(csrArLine);
				}
				csr.setCashSaleReturnArLines(cashSaleReturnArLines);
			}
			return csr;
		}
		return null;
	}

	/**
	 * Search Cash Sales Return forms.
	 * @param searchCriteria The search criteria.
	 * @param typeId The type of cash sales.
	 */
	public List<FormSearchResult> search(String searchCriteria, int typeId) {
		logger.info("Searching Cash Sale Returns of type: "+typeId);
		logger.debug("Searching for: "+searchCriteria.trim());
		Page<CashSaleReturn> cashSalesReturn = cashSaleReturnDao.searchCashSaleReturns(searchCriteria.trim(), typeId, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		for (CashSaleReturn csr : cashSalesReturn.getData()) {
			logger.trace("Retrieved CSR No. " + csr.getCsrNumber());
			title = String.valueOf(csr.getFormattedCSRNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", csr.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Customer", csr.getArCustomer().getNumberAndName()));
			properties.add(ResultProperty.getInstance("Customer Account", csr.getArCustomerAccount().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(csr.getDate())));
			status = formStatusDao.get(csr.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(csr.getId(), title, properties));
		}
		return result;
	}

	/**
	 * Get the CSR Label of the form based on the type of the CSR.
	 * @param typeId The id of the Cash sale return type.
	 * @return The label of the form.
	 */
	public String getCsrLabel(int typeId) {
		switch (typeId) {
		case CashSaleType.INDIV_SELECTION:
			return "CSR - IS ";

		default:
			return "CSR ";
		}
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			CashSaleReturn csr = cashSaleReturnDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			if (!csr.getCashSaleTypeId().equals(CashSaleType.INDIV_SELECTION)) { // Not applicable for IS forms.
				List<CashSaleReturnItem> csrItems = getCsReturnItems(csr.getId());
				List<CashSaleReturnItem> returnedItems = SaleItemUtil.filterSaleReturnItems(csrItems, true);
				String errorMessage = "";
				if (cashSaleReturnDao.isExistingInCashSaleReturn(csr.getId())) {
					String asFormName = csr.getCashSaleId() != null
							? "Cash Sale Order" : "Cash Sale Return";
					List<CashSaleReturn> cashSaleReturns = cashSaleReturnDao.getCashReturnSaleUsedInReturns(csr.getId());
					errorMessage += asFormName + " was used as reference in: ";
					for(CashSaleReturn cashSaleReturn : cashSaleReturns) {
						errorMessage += "<br> CSR No. " + cashSaleReturn.getCsrNumber();
					}
					setWorkflowMsg(bindingResult, currentWorkflowLog, errorMessage.toString());
				}
				if (errorMessage != "" && csr.getFormWorkflow().isComplete()){
					for (CashSaleReturnItem csri : returnedItems) {
						// Check existing stocks of the item/s if form will be cancelled.
						double totalQty = ValidationUtil.getTotalQtyPerItem(csri.getItemId(), returnedItems);
						errorMessage = ValidationUtil.validateToBeCancelledItem(itemService, csri.getItemId(),
								csri.getWarehouseId(), csr.getDate(), Math.abs(totalQty));
						if (errorMessage != null && errorMessage != "") {
							setWorkflowMsg(bindingResult, currentWorkflowLog, errorMessage.toString());
							break;
						}
					}
				}
			}
		}
	}

	private void setWorkflowMsg(BindingResult bindingResult, FormWorkflowLog currentWorkflowLog, String errorMessage) {
		bindingResult.reject("workflowMessage", errorMessage);
		currentWorkflowLog.setWorkflowMessage(errorMessage);
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		//do nothing
	}

	/**
	 * Get the cash sale return object by id.
	 * @param cashSaleReturnId The cash sale return id.
	 * @return The cash sale return object.
	 */
	public CashSaleReturn getCashSaleReturnPrintOut(Integer pId) {
		CashSaleReturn cashSaleReturn = cashSaleReturnDao.get(pId);
		if (cashSaleReturn != null){
			double totalAmount = cashSaleReturnItemDao.getTotalCSRAmount(pId);
			cashSaleReturn.setTotalAmount(totalAmount);
		}
		return cashSaleReturn;
	}

	/**
	 * Get the cash sale return ref.
	 * @param companyId The company id.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAccountId The ar customer account id.
	 * @param csNumber The cash sale number.
	 * @param dateFrom The date from.
	 * @param dateTo The date to.
	 * @param status The status.
	 * @param typeId The type id.
	 * @param pageNumber The page number.
	 * @param user The user logged.
	 * @return The cash sale return ref.
	 */
	public Page<CashSaleReturnDto> getCashSaleReturnRef(Integer referenceId, String refType, Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer csNumber, Date dateFrom, Date dateTo, Integer status,
			Integer typeId, Integer pageNumber, User user) {
		return cashSaleReturnItemDao.getCashSaleReturnRef(referenceId, refType, companyId, arCustomerId,
				arCustomerAccountId, csNumber, dateFrom, dateTo, status,
				typeId, user, new PageSetting(pageNumber));
	}

	/**
	 * Add cash sale return items in cash sale return.
	 * @param cashSaleReturn The cash sale return.
	 * @return The cash sale return.
	 */
	public CashSaleReturn convertCSRDtoToCSR(CashSaleReturn cashSaleReturn) {
		if (cashSaleReturn != null) {
			CashSaleReturn csr = new CashSaleReturn();
			csr.setRefCashSaleReturnId(cashSaleReturn.getId());
			csr.setCashSaleId(null);
			csr.setCompanyId(cashSaleReturn.getCompanyId());
			csr.setArCustomerId(cashSaleReturn.getArCustomerId());
			csr.setArCustomerAccountId(cashSaleReturn.getArCustomerAccountId());
			csr.setSalesInvoiceNo(cashSaleReturn.getSalesInvoiceNo());
			csr.setCompany(cashSaleReturn.getCompany());
			csr.setArCustomer(cashSaleReturn.getArCustomer());
			csr.setArCustomerAccount(cashSaleReturn.getArCustomerAccount());
			csr.setReferenceNo("CSR "+cashSaleReturn.getCsrNumber());
			csr.setCashSaleTypeId(cashSaleReturn.getCashSaleTypeId());
			List<CashSaleReturnItem> csrItems = getCsReturnItems(cashSaleReturn.getId());
			if (csrItems != null && !csrItems.isEmpty()) {
				List<CashSaleReturnItem> exchangeItems = SaleItemUtil.<CashSaleReturnItem>filterSaleReturnItems(csrItems, false);
				SaleItemUtil<CashSaleReturnItem> saleItemUtil = new SaleItemUtil<CashSaleReturnItem>();
				exchangeItems = saleItemUtil.processSaleItemsForViewing(exchangeItems);
				List<CashSaleReturnItem> cashSaleReturnItems = new ArrayList<CashSaleReturnItem>();
				for (CashSaleReturnItem csi : exchangeItems) {
					if (csi.getQuantity() < 0){
						continue;
					}
					Item item = csi.getItem();
					CashSaleReturnItem csri = new CashSaleReturnItem();
					csri.setStockCode(item.getStockCode());
					csri.setItem(item);
					csri.setItemId(csi.getItemId());
					csri.setWarehouseId(csi.getWarehouseId());
					csri.setQuantity(csi.getQuantity());
					csri.setSrp(csi.getSrp());
					double origSrp = csi.getItemSrp().getSrp();
					csri.setOrigSrp(origSrp);
					csri.setItemSrpId(csi.getItemSrpId());
					csri.setRefQuantity(csi.getQuantity());
					csri.setItemDiscountId(csi.getItemDiscountId());
					csri.setItemAddOnId(csi.getItemAddOnId());
					csri.setDiscount(csi.getDiscount());
					csri.setAmount(csi.getAmount());
					csri.setRefCashSaleReturnItemId(csi.getId());
					csri.setSalesRefId(csi.getId());
					csri.setUnitCost(csi.getUnitCost());
					csri.setOrigRefObjectId(csi.getEbObjectId());
					cashSaleReturnItems.add(csri);
				}
				csr.setCashSaleReturnItems(cashSaleReturnItems);
			}
			logger.debug("Successfully converted the CS to CSR object.");
			List<CashSaleReturnArLine> csrArLines = getCsrArLine(cashSaleReturn.getId());
			List<CashSaleReturnArLine> returnArLines = new ArrayList<CashSaleReturnArLine>();
			for (CashSaleReturnArLine csrArLine : csrArLines) {
				if (csrArLine.getCashSaleALRefId() == null && csrArLine.getCashSaleRetALRefId() == null){
					csrArLine.setArLineSetupName(csrArLine.getArLineSetup().getName());
					csrArLine.setCashSaleRetALRefId(csrArLine.getId());
					csrArLine.setUnitMeasurementName(csrArLine.getUnitMeasurement() != null ? csrArLine.getUnitMeasurement().getName() : "");
					csrArLine.setAmount(-csrArLine.getAmount());
					returnArLines.add(csrArLine);
				}
			}
			csr.setCashSaleReturnArLines(returnArLines);
			return csr;
		}
		return null;
	}

	/**
	 * Get the list of Cash Sale Return Ar Lines.
	 * @param cashSaleReturnId The cash sales return id.
	 * @return The list of cash sale return ar lines.
	 */
	public List<CashSaleReturnArLine> getCsrArLine(int cashSaleReturnId) {
		return cashSaleReturnArLineDao.getCsrArLine(cashSaleReturnId);
	}

	/**
	 * Compute the total amount of other charges.
	 * @param csrId The id of the cash sale return.
	 * @return The total amount for other charges, otherwise zero.
	 */
	public double getTotalcsrArLinesAmt(int csrId) {
		logger.debug("Computing the total amount in other charges.");
		List<CashSaleReturnArLine> arLines = getCsrArLine(csrId);
		double totalAmt = 0;
		if (arLines.isEmpty()) {
			logger.debug("No Other Charges for cash sale return "+csrId);
			return totalAmt;
		}

		for (CashSaleReturnArLine csa : arLines) {
			totalAmt += csa.getAmount();
		}
		logger.debug("Total amount of other charges is: "+totalAmt);
		return totalAmt;
	}

	/**
	 * Get the total item VAT
	 * @param csrId The cash sale return id
	 * @return The computed total item VAT
	 */
	public double getTotalItemVat(int csrId) {
		double totalItemVat = 0;
		List<CashSaleReturnItem> csrItems = getCsReturnItems(csrId);
		for (CashSaleReturnItem csri : csrItems) {
			double vat = csri.getVatAmount() != null ? csri.getVatAmount() : 0.0;
			if (csri.getQuantity() < 0) {
				vat = vat * -1; // negate VAT for returns
			}
			totalItemVat += vat;
		}

		List<CashSaleReturnArLine> arLines = getCsrArLine(csrId);
		for (CashSaleReturnArLine csrl : arLines) {
			totalItemVat += csrl.getVatAmount() != null ? csrl.getVatAmount() : 0.0;
		}
		return totalItemVat;
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return cashSaleReturnDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		CashSaleReturn csr = cashSaleReturnDao.getByEbObjectId(ebObjectId);
		Integer pId = csr.getId();
		Integer typeId = csr.getCashSaleTypeId();
		FormProperty property = workflowHandler.getProperty(csr.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String formName = typeId == CashSaleType.RETAIL ? "Cash Sale Return" : "Cash Sale Return - IS";

		String latestStatus = csr.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + csr.getCsrNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + csr.getArCustomer().getName())
				.append(" " + csr.getArCustomerAccount().getName())
				.append("<b> DATE : </b>" + DateUtil.formatDate(csr.getDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case CashSaleReturn.CSR_OBJECT_TYPE_ID:
			return cashSaleReturnDao.getByEbObjectId(ebObjectId);
		case CashSaleReturnItem.RETURN_OBJECT_TYPE_ID:
		case CashSaleReturnItem.EXCHANGE_OBJECT_TYPE_ID:
			return cashSaleReturnItemDao.getByEbObjectId(ebObjectId);
		case CashSaleReturnArLine.CSR_AR_LINE_OBJECT_TYPE_ID:
			return cashSaleReturnArLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}
