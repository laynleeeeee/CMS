package eulap.eb.service;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.RepackingDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.ReclassRegisterDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Class that will handle the business logic for generating Reclass Register.

 */
@Service
public class ReclassRegisterService {
	@Autowired
	private RepackingDao repackingDao;
	@Autowired
	private ItemDao itemDao;

	/**
	 * Generate the data source of the Reclass Register.
	 * @param companyId The unique id of the company.
	 * @param warehouseId The warehouse id, set to 1- for all warehouses under the company.
	 * @return The data source for the Reclass Register.
	 */
	public JRDataSource generateReclassRegisterDataSource(Integer companyId, Integer divisionId, Integer warehouseId,
			Date dateFrom, Date dateTo, Integer statusId) {
		EBJRServiceHandler<ReclassRegisterDto> handler = new ReclassRegisterControllerHandler(
				repackingDao, itemDao, companyId, divisionId, warehouseId, dateFrom, dateTo, statusId);
		return new EBDataSource<ReclassRegisterDto>(handler);
	}

	private static class ReclassRegisterControllerHandler implements EBJRServiceHandler<ReclassRegisterDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer warehouseId;
		private Date dateFrom;
		private Date dateTo;
		private Page<ReclassRegisterDto> currentPage;
		private RepackingDao repackingDao;
		private ItemDao itemDao;
		private static Logger logger = Logger.getLogger(ReclassRegisterControllerHandler.class);
		private Integer statusId;

		private ReclassRegisterControllerHandler (RepackingDao repackingDao, ItemDao itemDao,
				Integer companyId, Integer divisionId, Integer warehouseId, Date dateFrom, Date dateTo,
				Integer statusId) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.warehouseId = warehouseId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.repackingDao = repackingDao;
			this.itemDao = itemDao;
			this.statusId = statusId;
		}

		@Override
		public void close() throws IOException {
			currentPage = null;
		}

		@Override
		public Page<ReclassRegisterDto> nextPage(PageSetting pageSetting) {
			Page<ReclassRegisterDto> reclassRegisterData = repackingDao.getReclassRegisterData(companyId, divisionId, warehouseId,
					dateFrom, dateTo, statusId, pageSetting);
			if (!reclassRegisterData.getData().isEmpty()) {
				for (ReclassRegisterDto rrDto : reclassRegisterData.getData()) {
					Date currentDate = new Date();
					rrDto.setFromExistingStocks(itemDao.getItemExistingStocks(rrDto.getFromItemId(), warehouseId, currentDate));
					rrDto.setToExistingStocks(itemDao.getItemExistingStocks(rrDto.getToItemId(), warehouseId, currentDate));
				}
			}
			currentPage = new Page<ReclassRegisterDto>(pageSetting, reclassRegisterData.getData(), reclassRegisterData.getTotalRecords());
			logger.debug("Retrieved "+reclassRegisterData.getDataSize()+" at page "+pageSetting.getPageNumber());
			return currentPage;	
		}
	}
}