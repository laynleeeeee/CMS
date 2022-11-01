package eulap.eb.service;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elasticbooks.pos.dto.FCashSaleDto;
import com.elasticbooks.pos.dto.FCashSaleItem;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.dto.LoginCredential;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.bitpos.BCashSaleDto;
import eulap.eb.bitpos.BCashSaleItem;
import eulap.eb.common.util.ListProcessorUtil;
import eulap.eb.dao.ArLineSetupDao;
import eulap.eb.dao.CashSaleArLineDao;
import eulap.eb.dao.CashSaleDao;
import eulap.eb.dao.CashSaleItemDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.CsiRFinishedProductDao;
import eulap.eb.dao.CsiRawMaterialDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ProductLineDao;
import eulap.eb.dao.UserDao;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.ArReceiptType;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleArLine;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.CsiFinishedProduct;
import eulap.eb.domain.hibernate.CsiRawMaterial;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.PosMiddlewareSetting;
import eulap.eb.domain.hibernate.ProductLineItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.inventory.FormUnitCosthandler;
import eulap.eb.service.inventory.RItemCostUpdateService;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.web.dto.ItemTransaction;
import eulap.eb.webservice.WebServiceCredential;

/**
 * Class that handles business logic of {@link CashSale} - Processing Type.
 *
 */
@Service
public class CashSaleProcessingService extends BaseWorkflowService implements FormUnitCosthandler {
	private static Logger logger = Logger.getLogger(CashSaleProcessingService.class);
	@Autowired
	private CashSaleDao cashSaleDao;
	@Autowired
	private CashSaleItemDao cashSaleItemDao;
	@Autowired
	private CashSaleArLineDao cashSaleArLineDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ProductLineService productLineService;
	@Autowired
	private CsiRawMaterialDao rawMaterialsDao;
	@Autowired
	private CsiRFinishedProductDao finishedProductDao;
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private PosMiddlewareSettingService middlewareSettingService;
	@Autowired
	private ProductLineDao productLineDao;
	@Autowired
	private ArLineSetupDao arLineSetupDao;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private CashSaleItemService csItemService;

	private final static String SENIOR_DISCOUNT_AR_LINE = "SENIOR DISCOUNT";
	private final static String SALES_DISCOUNT_AR_LINE = "SALES DISCOUNT";
	private final static String TAX_AR_LINE = "TAX";
	private final static double SENIOR_DISCOUNT = 0.20;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return cashSaleDao.get(id).getFormWorkflow();
	}

	/**
	 * Get the cash sale object by id.
	 * @param cashSaleId The cash sale id.
	 * @return The cash sale object.
	 */
	public CashSale getCashSale(Integer cashSaleId) {
		return cashSaleDao.get(cashSaleId);
	}

	/**
	 * Initialize the raw materials given the main product and the associated product lines for Cash Sales - Processing.
	 * @param mainProducts The list of main products.
	 * @return The list of raw material items for mixing report.
	 */
	public CashSale processRawMaterialsAndFinishedProds(CashSale cashSale) {
		// Initialize the list of domains to be processed.
		List<CsiRawMaterial> rawMaterials = new ArrayList<>();
		List<CsiFinishedProduct> finishedProducts = new ArrayList<>();
		List<CashSaleItem> processedMainProducts = new ArrayList<>();
		List<CashSaleItem> mainProducts = cashSale.getCashSaleItems();
		List<Integer> processedItemIds = new ArrayList<>();
		Map<String, CsiRawMaterial> stockCode2RawMat = new HashMap<String, CsiRawMaterial>();
		if (mainProducts != null && !mainProducts.isEmpty()) {
			List<ProductLineItem> productLineItems = null;
			CsiRawMaterial csiRawMaterial = null;
			CsiRawMaterial editCsiRawMaterial = null;
			CsiFinishedProduct csiFinishedProduct = null;
			for (CashSaleItem mainProd : mainProducts) {
				int itemId = mainProd.getItemId();
				if (processedItemIds.contains(itemId)) {
					continue;
				}
				processedItemIds.add(itemId);
				int warehouseId = mainProd.getWarehouseId();
				double mainProdQty = mainProd.getQuantity() != null ? mainProd.getQuantity() : 0;
				mainProd.setQuantity(mainProdQty);
				// Get the Raw Materials of the finished products
				productLineItems = productLineService.getRawMaterials(itemId);
				logger.debug("Retrieved "+productLineItems.size()+" Raw Materials for item id: "+itemId);
				Integer productLineId = null;
				for (ProductLineItem pliRawMaterial : productLineItems) {
					productLineId = pliRawMaterial.getProductLineId();
					// Build the raw materials.
					// Group raw materials based on stock code.
					// There are existing record with more than one instance of item per product line.
					// Fixing this problem by group the stock code before allocating the unit cost. 
					// See bug 2864 for more details.
					Integer rmItemId = pliRawMaterial.getItemId();
					String key = "i" + rmItemId;
					double quantity = mainProdQty * pliRawMaterial.getQuantity();
					if (stockCode2RawMat.containsKey(key)) {
						editCsiRawMaterial = stockCode2RawMat.get(key);
						editCsiRawMaterial.setQuantity(editCsiRawMaterial.getQuantity() + quantity);
						stockCode2RawMat.put(key, editCsiRawMaterial);
						editCsiRawMaterial = null;
					} else {
						csiRawMaterial = new CsiRawMaterial();
						csiRawMaterial.setItemId(rmItemId);
						csiRawMaterial.setStockCode(pliRawMaterial.getItem().getStockCode());
						csiRawMaterial.setQuantity(quantity);
						csiRawMaterial.setProductLineId(productLineId);
						csiRawMaterial.setWarehouseId(warehouseId);
						stockCode2RawMat.put(key, csiRawMaterial);
						csiRawMaterial = null;
					}
				}
				mainProd.setQuantity(mainProdQty);
				mainProd.setProductLineId(productLineId);
				processedMainProducts.add(mainProd);

				// Build the finished products.
				csiFinishedProduct = new CsiFinishedProduct();
				csiFinishedProduct.setProductLineId(productLineId);
				csiFinishedProduct.setQuantity(mainProdQty);
				csiFinishedProduct.setWarehouseId(warehouseId);
				csiFinishedProduct.setItemId(itemId);
				finishedProducts.add(csiFinishedProduct);
			}
			// Free the used main products.
			mainProducts = null;
		}
		for (Map.Entry<String, CsiRawMaterial> csiRm : stockCode2RawMat.entrySet()) {
			rawMaterials.add(csiRm.getValue());
		}
		cashSale.setRawMaterials(rawMaterials);
		logger.debug("Total raw materials processed "+rawMaterials.size());
		cashSale.setCashSaleItems(processedMainProducts);
		cashSale.setFinishedProducts(finishedProducts);
		logger.debug("Total cash sale item/finished product processed "+finishedProducts.size());
		return cashSale;
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		CashSale cashSale = (CashSale) form;
		cashSale.setCashSaleTypeId(CashSaleType.PROCESSING);
		cashSale.setArReceiptTypeId(ArReceiptType.TYPE_CASH);
		Date receiptDate = cashSale.getReceiptDate();
		cashSale.setMaturityDate(receiptDate);
		cashSale.setRawMaterials(getAndAllocateRMIUnitCost(cashSale.getRawMaterials(), receiptDate));
	}

	private List<CsiRawMaterial> getAndAllocateRMIUnitCost(List<CsiRawMaterial> csiRawMaterials, Date receiptDate) {
		Map<AllocatorKey, WeightedAveItemAllocator<CsiRawMaterial>> itemId2CostAllocator =
				new HashMap<AllocatorKey, WeightedAveItemAllocator<CsiRawMaterial>>();
		AllocatorKey key = null;
		List<CsiRawMaterial> processedItems = new ArrayList<CsiRawMaterial>();
		for (CsiRawMaterial rm : csiRawMaterials) {
			WeightedAveItemAllocator<CsiRawMaterial> itemAllocator = itemId2CostAllocator.get(rm.getItemId());
			if (itemAllocator == null) {
				itemAllocator = new WeightedAveItemAllocator<CsiRawMaterial>(itemDao, itemService,
						rm.getItemId(), rm.getWarehouseId(), receiptDate);
				key = AllocatorKey.getInstanceOf(rm.getItemId(), rm.getWarehouseId());
				itemId2CostAllocator.put(key, itemAllocator);
			}
			try {
				List<CsiRawMaterial> allocatedRMs = itemAllocator.allocateCost(rm);
				for (CsiRawMaterial rmi : allocatedRMs) {
					SaleItemUtil.setNullUnitCostToZero(rmi);
					processedItems.add(rmi);
				}
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		return processedItems;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		logger.info("Saving Cash Sales - Processing Type");
		CashSale cashSale = (CashSale) form;
		boolean isNew = cashSale.getId() == 0;
		AuditUtil.addAudit(cashSale, new Audit(user.getId(), isNew, new Date ()));
		if (isNew) {
			int csNumber = cashSaleDao.generateCsNumber(cashSale.getCompanyId(), CashSaleType.PROCESSING);
			cashSale.setCsNumber(csNumber);
		} else {
			CashSale savedCS = getCashSale(cashSale.getId());
			DateUtil.setCreatedDate(cashSale, savedCS.getCreatedDate());
			deleteItems(cashSale.getId());
			savedCS = null;
		}
		String salesInvoiceNo = cashSale.getSalesInvoiceNo();
		if (salesInvoiceNo != null) {
			cashSale.setSalesInvoiceNo(StringFormatUtil.removeExtraWhiteSpaces(salesInvoiceNo));
		}
		cashSaleDao.saveOrUpdate(cashSale);
		logger.info("Save Finished Products and Raw Materials.");

		int csId = cashSale.getId();
		double unitCost = 0;
		double discount = 0;
		List<CashSaleItem> cashSaleItems = cashSale.getCashSaleItems();
		List<CsiRawMaterial> rawMaterials = cashSale.getRawMaterials();
		// Save raw materials
		for (CsiRawMaterial rm : rawMaterials) {
			rm.setCashSaleId(csId);
			rawMaterialsDao.saveOrUpdate(rm);
		}

		// Save finished product
		List<ProductLineItem> productLineItems = null;
		for (CsiFinishedProduct fp : cashSale.getFinishedProducts()) {
			productLineItems = productLineService.getRawMaterials(fp.getItemId());
			unitCost = computeFinishedProdCost(rawMaterials, productLineItems);
			fp.setUnitCost(unitCost);
			fp.setCashSaleId(csId);
			finishedProductDao.save(fp);
			for (CashSaleItem csi : cashSaleItems) {
				if (fp.getProductLineId().equals(csi.getProductLineId())) {
					csi.setCashSaleId(csId);
					csi.setReferenceObjectId(fp.getEbObjectId());
					csi.setUnitCost(unitCost);
					discount = csi.getDiscount() != null ? csi.getDiscount() : 0.0;
					csi.setAmount((csi.getQuantity() * csi.getSrp()) - discount);
					cashSaleItemDao.save(csi);
				}
			}
			discount = 0;
			productLineItems = null;
		}
		// Save the AR Lines.
		cashSaleService.saveOtherCharges(cashSale.getCashSaleArLines(), cashSale.getId(), isNew);
		logger.info("Successfully saved cash sales - processing id "+csId);

		rawMaterials = null;
		cashSaleItems = null;
	}

	private double computeFinishedProdCost(List<CsiRawMaterial> rawMaterials,
			List<ProductLineItem> productLineItems) {
		double unitCost = 0;
		for (ProductLineItem pli : productLineItems) {
			for (CsiRawMaterial rm : rawMaterials) {
				if (rm.getItemId().intValue() == pli.getItemId().intValue()) {
					unitCost += pli.getQuantity() * (rm.getUnitCost() != null ? rm.getUnitCost() : 0);
				}
			}
		}
		return unitCost;
	}

	/**
	 * Delete the saved items data.
	 * @param cashSaleId
	 */
	private List<Domain> deleteItems(int cashSaleId) {
		List<Domain> deletedDomains = new ArrayList<>();
		// Delete CS Items
		List<CashSaleItem> savedCSItems = cashSaleItemDao.getCashSaleItems(cashSaleId, null, null);
		if (!savedCSItems.isEmpty()) {
			for (CashSaleItem csItem : savedCSItems) {
				cashSaleItemDao.delete(csItem);
				deletedDomains.add(csItem);
			}
		}

		// Delete Raw Materials saved.
		List<CsiRawMaterial> rawMaterials = getRawMaterials(cashSaleId);
		for (CsiRawMaterial rm : rawMaterials) {
			rawMaterialsDao.delete(rm);
			deletedDomains.add(rm);
		}

		// Delete Finished Products saved.
		List<CsiFinishedProduct> finishedProducts = getFinishedProducts(cashSaleId);
		for (CsiFinishedProduct fp : finishedProducts) {
			finishedProductDao.delete(fp);
			deletedDomains.add(fp);
		}
		return deletedDomains;
	}

	/**
	 * Get the list of Raw Materials.
	 * @param cashSaleId The id of the Cash Sales - Processing form.
	 * @return The list of Raw Materials.
	 */
	private List<CsiRawMaterial> getRawMaterials(int cashSaleId) {
		return rawMaterialsDao.getAllByRefId(CsiRawMaterial.FIELD.cashSaleId.name(), cashSaleId);
	}

	/**
	 * Get the list of Finished Products.
	 * @param cashSaleId The id of the Cash Sales - Processing form.
	 * @return The list of Finished Products.
	 */
	private List<CsiFinishedProduct> getFinishedProducts(int cashSaleId) {
		return finishedProductDao.getAllByRefId(CsiFinishedProduct.FIELD.cashSaleId.name(), cashSaleId);
	}

	/**
	 * Get the Cash Sales object for viewing.
	 * @param id The id of the cash sales.
	 * @return The Cash sales object.
	 */
	public CashSale getCsProcessingAndItems(int id) {
		CashSale cashSale = cashSaleDao.get(id);
		List<CashSaleItem> csItems = csItemService.getAllCashSaleItems(id, false);
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		List<CashSaleItem> processedList = saleItemUtil.processSaleItemsForViewing(csItems);
		Integer warehouseId = null;
		String warehouseName = null;
		double discount = 0;
		for (CashSaleItem csi : processedList) {
			csi.setOrigQty(csi.getQuantity());
			csi.setOrigSrp(csi.getSrp());
			csi.setStockCode(csi.getItem().getStockCode());
			if (warehouseId == null) {
				warehouseName = csi.getWarehouse().getName();
				warehouseId = csi.getWarehouseId();
			}
			discount = csi.getDiscount() != null ? csi.getDiscount() : 0;
			csi.setAmount((csi.getQuantity() * csi.getSrp()) - discount);
		}
		cashSale.setWarehouseId(warehouseId);
		cashSale.setWarehouseName(warehouseName);
		cashSale.setCashSaleItems(processedList);
		cashSale.setCashSaleArLines(cashSaleService.getDetailedArLines(id));
		return cashSale;
	}

	/**
	 * Update the the user update status object.
	 * @param request The http servlet request object.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException 
	 */
	public List<CashSale> convert2FCashsales(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		return convert2CashSales(request, response, true);
	}

	/**
	 * Update the the user update status object.
	 * @param request The http servlet request object.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException 
	 */
	public List<CashSale> convert2BCashsales(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		return convert2CashSales(request, response, false);
	}

	private List<CashSale> convert2CashSales (HttpServletRequest request, 
			HttpServletResponse response,
			boolean isFloreant) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(request.getInputStream());
		WebServiceCredential webCredential = null;
		if (isFloreant) {
			com.elasticbooks.pos.dto.WebServiceCredential floreantCredential = 
					(com.elasticbooks.pos.dto.WebServiceCredential) in.readObject();
			webCredential = new WebServiceCredential();
			webCredential.setUserName(floreantCredential.getUserName());
			webCredential.setPassword(floreantCredential.getPassword());
		} else {
			webCredential = (WebServiceCredential) in.readObject();
		}
		LoginCredential credential = new LoginCredential();
		credential.setUserName(webCredential.getUserName());
		credential.setPassword(webCredential.getPassword());
		if (!userService.isUniqueUserName(credential.getUserName())) {
			boolean isEmpty = credential.getPassword() == null || credential.getPassword().isEmpty();
			if (isEmpty || !userDao.validateUser(credential))  {
				throw new RuntimeException("Status " + HttpServletResponse.SC_FORBIDDEN + ": Invalid credential.");
			}
		} else {
			throw new RuntimeException("Status " + HttpServletResponse.SC_FORBIDDEN + ": Invalid credential.");
		}
		User user = userService.getUser(credential);
		Integer companyId = (Integer) in.readObject();
		Company company = companyDao.get(companyId);
		if (company == null) {
			throw new RuntimeException("Status " + HttpServletResponse.SC_FORBIDDEN + ": Invalid company.");
		}
		if (isFloreant) {
			Integer warehouseId = (Integer) in.readObject();
			List<FCashSaleDto> cashSaleDtos = (List<FCashSaleDto>) in.readObject();
			in.close();
			return processFCashSales(companyId, cashSaleDtos, user, warehouseId);
		} else {
			List<BCashSaleDto> cashSaleDtos = (List<BCashSaleDto>) in.readObject();
			in.close();
			return processBCashSale(companyId, cashSaleDtos);
		}
	}

	private List<CashSale> processFCashSales (Integer companyId, List<FCashSaleDto> cashSaleDtos, User user, Integer warehouseId) throws InvalidClassException, ClassNotFoundException {
		List<CashSale> cashSales = new ArrayList<>();
		if (cashSaleDtos != null && !cashSaleDtos.isEmpty()) {
			CashSale cs = null;
			List<CashSaleArLine> cashSaleArLines = null;
			CashSaleArLine arLine= null;
			for (FCashSaleDto fcs : cashSaleDtos) {
				if (hasExistingSI(fcs.getSalesInvoiceCode() + fcs.getTicketId() + "")) {
					continue;
				}
				// Convert the FCashSaleDto to CashSale object.
				cs = new CashSale();
				arLine= new CashSaleArLine();
				cashSaleArLines = new ArrayList<>();
				if (fcs.getDiscount() != null && fcs.getDiscount() != 0){
					arLine.setAmount(-fcs.getDiscount());
					ArLineSetup setup = arLineSetupDao.getALSetupByNameAndCompany(SALES_DISCOUNT_AR_LINE, companyId, null);
					arLine.setArLineSetupId(setup.getId());
					cashSaleArLines.add(arLine);
					arLine= new CashSaleArLine();
					arLine.setAmount(-fcs.getTax());
					ArLineSetup setupTax = arLineSetupDao.getALSetupByNameAndCompany(TAX_AR_LINE, companyId, null);
					arLine.setArLineSetupId(setupTax.getId());
					cashSaleArLines.add(arLine);
					cs.setCashSaleArLines(cashSaleArLines);
				}
				cs.setUser(user);
				cs.setCashSaleTypeId(CashSaleType.PROCESSING);
				cs.setReceiptDate(fcs.getReceiptDate());
				cs.setMaturityDate(fcs.getMaturityDate());
				PosMiddlewareSetting setting = middlewareSettingService.getByCompanyAndWarehouse(companyId, warehouseId);
				cs.setWarehouseId(setting.getWarehouseId());
				cs.setCompanyId(companyId);
				cs.setArCustomerId(setting.getArCustomerId());
				cs.setArCustomerAccountId(setting.getArCustomerAccountId());
				cs.setSalesInvoiceNo(fcs.getSalesInvoiceCode() + fcs.getTicketId() + "");
				cs.setCash(fcs.getCash());
				// Processs cash sale items.
				 List<FCashSaleItem> fCashSaleItems = fcs.getfCashSaleItems();
				 if (fCashSaleItems != null && !fCashSaleItems.isEmpty()) {
					 List<CashSaleItem> cashSaleItems = new ArrayList<>();
					 CashSaleItem csi = null;
					 for (FCashSaleItem fcsi : fCashSaleItems) {
						 csi = new CashSaleItem();
						 Item item = itemService.getByDescription(fcsi.getDescription());
						 csi.setQuantity(fcsi.getQuantity());
						 csi.setSrp(fcsi.getSrp() / fcsi.getQuantity());
						 csi.setDiscount(fcsi.getDiscount());
						 csi.setAmount(fcsi.getAmount());
						 if(item == null){
							 csi.setStockCode(fcsi.getDescription());
							 cashSaleItems.add(csi);
							 continue;
						 }
						 csi.setItem(item);
						 csi.setStockCode(item.getStockCode());
						 csi.setItemId(item.getId());
						 csi.setWarehouseId(setting.getWarehouseId());
						 cashSaleItems.add(csi);
					 }
					 cs.setCashSaleItems(cashSaleItems);
					 cashSales.add(cs);
				 }
			}
		}
		return cashSales;
	}

	private List<CashSale> processBCashSale (Integer companyId, List<BCashSaleDto> cashSaleDtos) throws InvalidClassException, ClassNotFoundException {
		List<CashSale> cashSales = new ArrayList<>();
		CashSale cs = null;
		if (cashSaleDtos != null && !cashSaleDtos.isEmpty()) {
			for (BCashSaleDto cashSaleDto : cashSaleDtos) {
				if (hasExistingSI(cashSaleDto.getOrNumber() + "")) {
					continue;
				}
				// Convert the BCashSaleDto to CashSale object.
				cs = new CashSale();
				Integer userId = userService.getUserIdByUsername(cashSaleDto.getUsername());
				User user = userDao.get(userId);
				cs.setUser(user);
				cs.setCashSaleTypeId(CashSaleType.PROCESSING);
				cs.setReceiptDate(cashSaleDto.getReceiptDate());
				cs.setMaturityDate(cashSaleDto.getMaturityDate());
				PosMiddlewareSetting setting = middlewareSettingService.getByCompany(companyId);
				cs.setWarehouseId(setting.getWarehouseId());
				cs.setCompanyId(companyId);
				cs.setArCustomerId(setting.getArCustomerId());
				cs.setArCustomerAccountId(setting.getArCustomerAccountId());
				cs.setSalesInvoiceNo(cashSaleDto.getOrNumber() + "");
				cs.setCash(cashSaleDto.getCash());
				// Processs cash sale items.
				double totalSeniorDiscount = 0;
				double totalSalesDiscount = 0;
				List<BCashSaleItem> bCashSaleItems = cashSaleDto.getbCashSaleItems();
				if (bCashSaleItems != null && !bCashSaleItems.isEmpty()) {
					List<CashSaleItem> cashSaleItems = new ArrayList<>();
					CashSaleItem csi = null;
					for (BCashSaleItem bcsi : bCashSaleItems) {
						csi = new CashSaleItem();
						Item item = itemService.getItemByStockCode(bcsi.getStockCode());
						if (item == null) {
							/*System.out.println("STOCK CODE: " + bcsi.getStockCode() );*/
							continue;
						}
						csi.setItem(item);
						csi.setItemId(item.getId());
						csi.setStockCode(item.getStockCode());
						csi.setQuantity(bcsi.getQuantity());
						csi.setAddOn(bcsi.getAddOn());
						csi.setSrp(bcsi.getSrp() + (bcsi.getAddOn() != null ? bcsi.getAddOn() : 0));
						csi.setAmount((bcsi.getSrp() * bcsi.getQuantity()));
						csi.setWarehouseId(setting.getWarehouseId());
						if(bcsi.getDiscount() != null && bcsi.getDiscount() != 0){
							if (bcsi.getDiscount() == SENIOR_DISCOUNT) {
								totalSeniorDiscount += csi.getAmount() * bcsi.getDiscount();
							} else {
								totalSalesDiscount += csi.getAmount() * bcsi.getDiscount();
							}
						}
						cashSaleItems.add(csi);
					}
					cs.setCashSaleItems(cashSaleItems);
				}
				if (totalSeniorDiscount != 0 || totalSalesDiscount != 0) {
					List<CashSaleArLine> csArLines = new ArrayList<>();
					CashSaleArLine csLine = null;
					ArLineSetup setup = null;
					if (totalSeniorDiscount != 0) {
						csLine = new CashSaleArLine();
						setup = arLineSetupDao.getALSetupByNameAndCompany(SENIOR_DISCOUNT_AR_LINE, companyId, null);
						csLine.setArLineSetupId(setup.getId());
						csLine.setAmount(-totalSeniorDiscount);
						csArLines.add(csLine);
					}
					if (totalSalesDiscount != 0) {
						csLine = new CashSaleArLine();
						setup = arLineSetupDao.getALSetupByNameAndCompany(SALES_DISCOUNT_AR_LINE, companyId, null);
						csLine.setArLineSetupId(setup.getId());
						csLine.setAmount(-totalSalesDiscount);
						csArLines.add(csLine);
					}
					cs.setCashSaleArLines(csArLines);
				}
				cashSales.add(cs);
			}
		}
		return cashSales;
	}

	private boolean hasExistingSI(String salesInvoiceNo) {
		return cashSaleDao.hasExistingSI(salesInvoiceNo);
	}

	/**
	 * Check if there is/are item that has no finished product.
	 * @param cashSale The object containing the list of finished products.
	 * @return The string with the message.
	 */
	public String checkPLConfig (CashSale cashSale) {
		String msg = null;
		List<CashSaleItem> cashSaleItems = cashSale.getCashSaleItems();
		if (cashSaleItems != null && !cashSaleItems.isEmpty()) {
			StringBuilder itemsWithNoSetup = null;
			Item item = null;
			for (CashSaleItem csi : cashSaleItems) {
				boolean hasConfig = productLineDao.hasConfig(csi.getItemId());
				if (!hasConfig) {
					if (itemsWithNoSetup == null) {
						itemsWithNoSetup = new StringBuilder();
					}
					item = itemService.getItem(csi.getItemId());
					itemsWithNoSetup.append(item.getDescription() + "\n") ;
				}
			}
			if (itemsWithNoSetup != null) {
				msg = "No product line setup for the following item/s: \n" + 
						itemsWithNoSetup.toString();
			}
		}
		return msg;
	}

	/**
	 * Check if the credential is valid.
	 * @param request The http servelet request.
	 * @param response The http servelet response.
	 * @return True if valid user, otherwise false.
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Boolean isValidCredential (HttpServletRequest request, 
			HttpServletResponse response) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(request.getInputStream());
		WebServiceCredential webCredential = webCredential = (WebServiceCredential) in.readObject();

		LoginCredential credential = new LoginCredential();
		credential.setUserName(webCredential.getUserName());
		credential.setPassword(webCredential.getPassword());
		if (!userService.isUniqueUserName(credential.getUserName())) {
			boolean isEmpty = credential.getPassword() == null || credential.getPassword().isEmpty();
			if (isEmpty || !userDao.validateUser(credential))  {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Get the total cash sales amount of all cash sales items date.
	 * @param date The cash sales receipt date.
	 * @return The cash sales amount.
	 */
	public double getTotalAmountByDate(Date date) {
		return cashSaleItemDao.getTotalAmountCSIByDate(date);
	}

	/**
	 * Get the total cash sale amount of all ar lines.
	 * @param date The cash sale receipt date.
	 * @return The cash sale amount.
	 */
	public double getTotalAmountCSAByDate(Date date) {
		return cashSaleArLineDao.getTotalAmountCSAByDate(date);
	}

	/**
	 * Evaluates if the Item has Product Line Setup.
	 * @param itemId The id of the item.
	 * @return True if has setup, otherwise false.
	 */
	public boolean hasProductLine(Integer itemId) {
		return productLineDao.hasConfig(itemId);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return cashSaleDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		return null;
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		return null;
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		// Do nothing
	}

	@Override
	public void updateUnitCost(RItemCostUpdateService costUpdateService,
			WeightedAveItemAllocator<ItemTransaction> fifoAllocator, ItemTransaction it, int itemId, int warehouseId,
			Date formDate, boolean isAllocateRpTo) {
		// Do nothing
	}

	@Override
	public void processAllocatedItem(int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems,
			ItemTransaction currentAllocItem) throws CloneNotSupportedException {
		int pId = currentAllocItem.getId();
		List<CsiRawMaterial> rawMaterialsItems = rawMaterialsDao.getCsRawMaterialItems(pId, itemId);
		ListProcessorUtil<CsiRawMaterial> remover = new ListProcessorUtil<CsiRawMaterial>();
		List<Integer> formIds = remover.collectFormIds(rawMaterialsItems);
		List<CsiRawMaterial> processedItems = summarizeRawMaterials(rawMaterialsItems);
		Double allocQty = currentAllocItem.getQuantity();
		Double qtyToBeWithdrawn = null;
		CsiRawMaterial splitItem = null;
		List<Integer> savedFormIds = new ArrayList<Integer>();
		for (CsiRawMaterial rrm : processedItems) {
			while (currentAllocItem != null) {
				if (qtyToBeWithdrawn == null) {
					qtyToBeWithdrawn = rrm.getQuantity();
				}
				if (allocQty >= qtyToBeWithdrawn) {
					rrm.setUnitCost(currentAllocItem.getUnitCost());
					rrm.setQuantity(qtyToBeWithdrawn);
					// update raw material
					rawMaterialsDao.saveOrUpdate(rrm);
					savedFormIds.add(rrm.getId());
					allocQty = NumberFormatUtil.roundOffNumber((allocQty - qtyToBeWithdrawn),
							NumberFormatUtil.SIX_DECIMAL_PLACES);
					if (allocQty == 0.0) {
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
					}
					qtyToBeWithdrawn = null;
					break;
				} else {
					if (allocQty > 0) {
						splitItem = (CsiRawMaterial) rrm.clone();
						splitItem.setId(0);
						splitItem.setQuantity(allocQty);
						splitItem.setUnitCost(currentAllocItem.getUnitCost());
						// save raw material
						rawMaterialsDao.saveOrUpdate(splitItem);
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
			frequency = Collections.frequency(savedFormIds, id);
			if (frequency == 0) {
				toBeDeleted.add(rawMaterialsDao.get(id));
			}
		}

		if (!toBeDeleted.isEmpty()) {
			for (Domain tbd : toBeDeleted) {
				rawMaterialsDao.delete(tbd);
			}
		}

		rawMaterialsItems = null;
		remover = null;
		processedItems = null;
		toBeDeleted = null;
		formIds = null;
		savedFormIds = null;

		recomputeMainProductCost(pId);
	}

	private void recomputeMainProductCost(int pId) {
		List<ProductLineItem> productLineItems = null;
		List<CashSaleItem> cashSaleItems = cashSaleItemDao.getCashSaleItems(pId, null, null);
		List<CsiFinishedProduct> finishedProducts = finishedProductDao.getAllByRefId("cashSaleId", pId);
		List<CsiRawMaterial> rawMaterials = null;
		double unitCost = 0;
		for (CsiFinishedProduct fpi : finishedProducts) {
			productLineItems = productLineService.getRawMaterials(fpi.getItemId());
			rawMaterials = getRawMaterials(pId);
			unitCost = computeFinishedProdCost(rawMaterials, productLineItems);
			fpi.setUnitCost(unitCost);
			finishedProductDao.update(fpi);

			// Update the associated CS item
			for (CashSaleItem csi : cashSaleItems) {
				if (csi.getItemId().equals(fpi.getItemId())) {
					csi.setUnitCost(unitCost);
					cashSaleDao.update(csi);
				}
			}
		}
		cashSaleItems = null;
		finishedProducts = null;
	}

	/**
	 * Recompute total finished product cost
	 * @param companyId The company id
	 */
	public void recomputeFinishedProductCost(int companyId) {
		List<CashSale> cashSales = cashSaleDao.getCashSales(companyId, CashSaleType.PROCESSING);
		if (cashSales != null && !cashSales.isEmpty()) {
			int pId = 0;
			List<CsiRawMaterial> toBeSavedRawMaterials = null;
			for (CashSale cashSale : cashSales) {
				logger.info("Processing cash sale number: "+cashSale.getCsNumber());
				pId = cashSale.getId();
				cashSale.setCashSaleItems(cashSaleItemDao.getCashSaleItems(pId, null, null));
				toBeSavedRawMaterials = processRawMaterials(cashSale);
				saveRawMaterials(pId, toBeSavedRawMaterials);
				// Re compute main product cost
				recomputeMainProductCost(cashSale, toBeSavedRawMaterials);
				// Free up memory
				toBeSavedRawMaterials = null;
				logger.info("Done processing cash sale number: "+cashSale.getCsNumber());
			}
		}
	}

	private void saveRawMaterials(int pId, List<CsiRawMaterial> toBeSavedRawMaterials) {
		List<Integer> toBeDeletedIds = new ArrayList<Integer>();
		List<CsiRawMaterial> savedRawMaterials = getRawMaterials(pId);
		for (CsiRawMaterial rm : savedRawMaterials) {
			toBeDeletedIds.add(rm.getId());
		}
		rawMaterialsDao.delete(toBeDeletedIds);
		toBeDeletedIds = null;
		savedRawMaterials = null;

		// Save cash sale item raw material
		for (CsiRawMaterial rm : toBeSavedRawMaterials) {
			rm.setCashSaleId(pId);
			rawMaterialsDao.saveOrUpdate(rm);
		}
	}

	private void recomputeMainProductCost(CashSale cashSale, List<CsiRawMaterial> savedRawMaterials) {
		List<ProductLineItem> productLineItems = null;
		List<CashSaleItem> cashSaleItems = cashSale.getCashSaleItems();
		List<CsiFinishedProduct> finishedProducts = getFinishedProducts(cashSale.getId());
		for (CashSaleItem csi : cashSaleItems) {
			productLineItems = productLineService.getRawMaterials(csi.getItemId());
			csi.setUnitCost(computeFinishedProdCost(savedRawMaterials, productLineItems));
			cashSaleDao.update(csi);
			for (CsiFinishedProduct fpi : finishedProducts) {
				if (csi.getItemId().intValue() == fpi.getItemId().intValue()) {
					fpi.setUnitCost(csi.getUnitCost());
					finishedProductDao.update(fpi);
				}
			}
			productLineItems = null;
		}
		cashSaleItems = null;
		finishedProducts = null;
	}

	private List<CsiRawMaterial> processRawMaterials(CashSale cashSale) {
		cashSale = processRawMaterialsAndFinishedProds(cashSale);
		int companyId = cashSale.getCompanyId();
		for (CsiRawMaterial rmi : cashSale.getRawMaterials()) {
			rmi.setUnitCost(itemDao.getLatestItemUnitCost(companyId,
					rmi.getWarehouseId(), rmi.getItemId()));
		}
		return cashSale.getRawMaterials();
	}

	private ItemTransaction getNextAllocItem(Queue<ItemTransaction> allocatedItems) {
		return allocatedItems.poll();
	}

	private double getAllocatedQty(ItemTransaction currentAllocItem) {
		if (currentAllocItem != null) {
			return currentAllocItem.getQuantity();
		}
		return 0;
	}

	private List<CsiRawMaterial> summarizeRawMaterials(List<CsiRawMaterial> repackingRawMaterials) {
		List<CsiRawMaterial> updatedItems = new ArrayList<CsiRawMaterial>();
		Map<String, CsiRawMaterial> rmHm = new HashMap<String, CsiRawMaterial>();

		CsiRawMaterial editedItem = null;
		String itemKey = null;
		for (CsiRawMaterial rrm : repackingRawMaterials) {
			itemKey = "i" + rrm.getItemId();
			if(rmHm.containsKey(itemKey)) {
				editedItem = processEditedItem(rrm, rmHm.get(itemKey));
				rmHm.put(itemKey, editedItem);
			} else {
				rmHm.put(itemKey, rrm);
			}
		}

		for (Map.Entry<String, CsiRawMaterial> iHM : rmHm.entrySet()) {
			updatedItems.add(iHM.getValue());
		}

		rmHm = null;
		editedItem = null;

		Collections.sort(updatedItems, new Comparator<CsiRawMaterial>() {
			@Override
			public int compare(CsiRawMaterial rrm1, CsiRawMaterial rrm2) {
				if (rrm1.getId() < rrm2.getId()) {
					return -1;
				} else if (rrm1.getId() > rrm2.getId()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return updatedItems;
	}

	private CsiRawMaterial processEditedItem(CsiRawMaterial rrm, CsiRawMaterial editedItem) {
		editedItem.setQuantity(rrm.getQuantity() + editedItem.getQuantity());
		editedItem.setOrigQty((rrm.getOrigQty() != null ? rrm.getOrigQty() : 0)
				+ (editedItem.getOrigQty() != null ? editedItem.getOrigQty() : 0));
		return editedItem;
	}

	@Override
	public String getFormLabel() {
		return "CS-P";
	}
}