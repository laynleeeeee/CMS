package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.WarehouseDao;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.ReorderPointDto;

/**
 * Class that will handle the business logic for generating Reorder Point report.

 */
@Service
public class ReorderPointService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private WarehouseDao warehouseDao;
	@Autowired
	private InventoryListingService invListingService;

	/**
	 * Generate the data source of the Reordering Point Report.
	 * @param companyId The unique id of the company.
	 * @param warehouseId The warehouse id, set to 1- for all warehouses under the company.
	 * @param categoryId The id of the category of the item.
	 * @param status The status of the item. {All, Active, Inactive}
	 * @param strDate As of date.
	 * @return The data source for the Reordering Point Report.
	 */
	public JRDataSource generateReorderingPtDataSource(Integer companyId, Integer divisionId, Integer warehouseId,
			Integer categoryId, String status, String strDate) {
		EBJRServiceHandler<ReorderPointDto> rInvListingJRHandler = new ReorderPointJrHandler(itemDao, divisionDao, warehouseDao,
				companyId, divisionId, warehouseId, invListingService.getstatusId(status), categoryId, strDate);
		return new EBDataSource<ReorderPointDto>(rInvListingJRHandler);
	}

	public static class ReorderPointJrHandler implements EBJRServiceHandler<ReorderPointDto> {
		private final ItemDao itemDao;
		private final Integer companyId;
		private final Integer divisionId;
		private final Integer warehouseId;
		private final Integer statusId;
		private final Integer categoryId;
		private final Date asOfDate;
		private Page<ReorderPointDto> currentPage;
		private Logger logger = Logger.getLogger(ReorderPointJrHandler.class);

		public ReorderPointJrHandler(ItemDao itemDao, DivisionDao divisionDao, WarehouseDao warehouseDao,
				Integer companyId, Integer divisionId, Integer warehouse, Integer statusId, Integer categoryId, String strDate) {
			this.itemDao = itemDao;
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.warehouseId = warehouse;
			this.statusId = statusId;
			this.categoryId = categoryId;
			this.asOfDate = DateUtil.parseDate(strDate);
		}

		@Override
		public void close() throws IOException {
			currentPage = null;
		}

		@Override
		public Page<ReorderPointDto> nextPage(PageSetting pageSetting) {
			Page<ReorderPointDto> reorderingData = itemDao.getReorderingPointData(companyId, divisionId, warehouseId,
			statusId, categoryId, asOfDate, "sc", -1, true, "", pageSetting);
			currentPage = new Page<ReorderPointDto>(pageSetting, reorderingData.getData(), reorderingData.getTotalRecords());
			logger.debug("Retrieved "+reorderingData.getDataSize()+" at page "+pageSetting.getPageNumber());
			return currentPage;
		}
	}
}
