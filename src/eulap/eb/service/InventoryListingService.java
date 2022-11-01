package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ItemDao;
import eulap.eb.service.inventory.ReceivedStock;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.PhysicalInventory;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Class that will handle the business logic for Inventory Listing report.

 */
@Service
public class InventoryListingService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemService itemService;

	/**
	 * Generate the Inventory Listing report using the computed unused stocks of the item.
	 * <br> Computes the unused stocks of the item based from the existing stocks.
	 */
	public JRDataSource generateInvListingFromUnusedStocks(Integer itemCategoryId,
			Integer stockOptionId, Integer companyId, Integer warehouseId, String status, String strDate) {
		EBJRServiceHandler<PhysicalInventory> rInvListingJRHandler = new RInvListingFromUnusedStocksHandler(itemDao, itemService,
				itemCategoryId, stockOptionId, companyId, warehouseId, getstatusId(status), strDate);
		return new EBDataSource<PhysicalInventory>(rInvListingJRHandler);
	}

	/**
	 * Get the status id.
	 * @param status The status.
	 * @return The id of the status.
	 */
	public Integer getstatusId(String status) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Integer statusId = null;
		switch (searchStatus) {
			case All : 
				statusId = -1;
				break;
			case Active : 
				statusId = 1;
				break;
			case Inactive :
				statusId = 0;
				break;
		}
		return statusId;
	}

	public static class RInvListingFromUnusedStocksHandler implements EBJRServiceHandler<PhysicalInventory> {
		private final Logger logger = Logger.getLogger(RInvListingFromUnusedStocksHandler.class);
		private final ItemDao itemDao;
		private final ItemService itemService;
		private final Integer itemCategoryId;
		private final Integer stockOptionId;
		private final Integer companyId;
		private final Integer warehouseId;
		private final Integer statusId;
		private Date asOfDate;
		private Page<PhysicalInventory> currentPage;

		private RInvListingFromUnusedStocksHandler(ItemDao itemDao, ItemService itemService, Integer itemCategoryId,
				Integer stockOptionId, Integer companyId, Integer warehouseId, Integer statusId, String strDate) {
			this.itemDao = itemDao;
			this.itemService = itemService;
			this.itemCategoryId = itemCategoryId;
			this.stockOptionId = stockOptionId;
			this.companyId = companyId;
			this.warehouseId = warehouseId;
			this.statusId = statusId;
			this.asOfDate = DateUtil.parseDate(strDate);
		}

		@Override
		public void close() throws IOException {

		}

		@Override
		public Page<PhysicalInventory> nextPage(PageSetting pageSetting) {
			List<PhysicalInventory> returnRpt = new ArrayList<PhysicalInventory>();
			Integer categoryId = itemCategoryId;
			if(itemCategoryId == -1)
				categoryId = 0;
			Page<PhysicalInventory> searchResult = itemDao.generateInvListingFromUnusedStocks(categoryId, companyId,
					warehouseId, stockOptionId, asOfDate, statusId, pageSetting);
			if(!searchResult.getData().isEmpty()) {
				PhysicalInventory pi = null;
				PhysicalInventory splitPrice = null;
				Map<Double, PhysicalInventory> invListingMap = null;
				List<ReceivedStock> receivedStocks = null;
				logger.debug("Current page : "+searchResult.getCurrentPage()+" .... Total Records: "+searchResult.getTotalRecords());
				for (PhysicalInventory il : searchResult.getData()) {
					logger.debug("Current item: "+il);

					invListingMap = new HashMap<Double, PhysicalInventory> ();
					receivedStocks = itemService.getItemUnusedStocks(il.getItemId(), warehouseId, asOfDate);
					logger.debug("Retrieved "+receivedStocks.size()+" Received Stocks for item: "+il.getItemId());
					if(!receivedStocks.isEmpty()) {
						for (ReceivedStock rs : receivedStocks) {
							Double unitCost = rs.getUnitCost();
							Double quantity = rs.getQuantity();
							pi = invListingMap.get(unitCost);
							if(pi != null) {
								pi.setQuantity(pi.getQuantity()+quantity);
								pi.setAmount(pi.getQuantity()*unitCost);
								continue;
							}
							try {
								splitPrice = (PhysicalInventory) il.clone();
								splitPrice.setQuantity(quantity);
								splitPrice.setUnitCost(unitCost);
								splitPrice.setAmount(quantity*unitCost);
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}

							invListingMap.put(unitCost, splitPrice);
							returnRpt.add(splitPrice);
						}
					} else {
						//For items without Received stocks, add only if user selects ALL.
						if(stockOptionId == -1)
							returnRpt.add(il);
					}
				}
			}

			logger.info("Successfully generated the Inventory Listing of Company: "+companyId);
			currentPage = new Page<PhysicalInventory>(pageSetting, returnRpt, searchResult.getTotalRecords());
			return currentPage;
		}
	}

	/**
	 * Generate the Inventory Listing report.
	 */
	public JRDataSource generateInventoryListing(Integer divisionId, Integer itemCategoryId, Integer stockOptionId,
			Integer companyId, Integer warehouseId, String status, int workflowStatusId, String orderBy, String strDate) {
		EBJRServiceHandler<PhysicalInventory> rInvListingJRHandler = new RInvListingJRServiceHandler(divisionId, itemDao,
				itemCategoryId, stockOptionId, companyId, warehouseId, getstatusId(status), workflowStatusId, orderBy, strDate);
		return new EBDataSource<PhysicalInventory>(rInvListingJRHandler);
	}

	public static class RInvListingJRServiceHandler implements EBJRServiceHandler<PhysicalInventory> {
		private final Logger logger = Logger.getLogger(RInvListingJRServiceHandler.class);
		private final Integer divisionId;
		private ItemDao itemDao;
		private final Integer itemCategoryId;
		private final Integer stockOptionId;
		private final Integer companyId;
		private final Integer warehouseId;
		private final Integer statusId;
		private final int workflowStatusId;
		private final int orderBy;
		private Date asOfDate;
		private Page<PhysicalInventory> currentPage;

		private RInvListingJRServiceHandler(Integer divisionId, ItemDao itemDao, Integer itemCategoryId, Integer stockOptionId,
				Integer companyId, Integer warehouseId, Integer statusId, int workflowStatusId, String strOrderBy, String strDate) {
			this.divisionId = divisionId;
			this.itemDao = itemDao;
			this.stockOptionId = stockOptionId;
			this.companyId = companyId;
			this.statusId = statusId;
			this.workflowStatusId = workflowStatusId;
			this.orderBy = strOrderBy.equals("sc") ? 1 : 0;
			this.itemCategoryId = itemCategoryId == null ? -1 : itemCategoryId;
			this.warehouseId = warehouseId == PhysicalInventory.ALL_OPTION ? -1 : warehouseId;
			this.asOfDate = DateUtil.parseDate(strDate);
		}

		@Override
		public Page<PhysicalInventory> nextPage(PageSetting pageSetting) {
			Page<PhysicalInventory> searchResult = itemDao.generateInventoryListingData(divisionId, itemCategoryId, companyId,
					warehouseId, stockOptionId, asOfDate, statusId, workflowStatusId, orderBy, pageSetting);
			logger.debug("Current page : "+searchResult.getCurrentPage()+" .... Total Records: "
					+searchResult.getTotalRecords()+" ordered by: "+orderBy);
			currentPage = new Page<PhysicalInventory>(pageSetting, searchResult.getData(), searchResult.getTotalRecords());
			return currentPage;
		}

		@Override
		public void close() throws IOException {
			currentPage = null;
			itemDao = null;
		}
	}
}