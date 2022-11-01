package eulap.eb.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.AvblStocksAndBagsDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Business logic for Available Stocks Report

 *
 */
@Service
public class AvailableStocksRptService {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;

	/**
	 * Generate the parameters for Available Stocks Report.
	 */
	public void generateParams(Model model, Integer companyId,
			Integer itemCategoryId, Integer warehouseId, String asOfDate) {
		Company company = companyService.getCompany(companyId);
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());

		model.addAttribute("asOfDate", "As of "+asOfDate);
		String itemCategory = itemCategoryId == null ? "ALL"
				: itemCategoryService.getItemCategory(itemCategoryId).getName();
		model.addAttribute("itemCategory", itemCategory);
		String warehouse = warehouseService.getWarehouse(warehouseId).getName();
		model.addAttribute("warehouse", warehouse);
	}

	/**
	 * Generate the data source for Available Stocks Report.
	 */
	public JRDataSource generateAvailableStocksRpt(Integer companyId, Integer warehouseId,
			Integer itemCategoryId, String stockCode, String orderBy, String strDate) {
		EBJRServiceHandler<AvblStocksAndBagsDto> asRptHandler = new AvailableStocksRptHandler(itemBagQuantityDao, companyId,
				warehouseId, itemCategoryId, stockCode, orderBy, strDate);
		return new EBDataSource<>(asRptHandler);
	}

	public static class AvailableStocksRptHandler implements EBJRServiceHandler<AvblStocksAndBagsDto> {
		private final ItemBagQuantityDao itemBagQuantityDao;
		private final Integer companyId;
		private final Integer warehouseId;
		private final Integer itemCategoryId;
		private final String stockCode;
		private final String orderBy;
		private final Date asOfDate;

		private AvailableStocksRptHandler(ItemBagQuantityDao itemBagQuantityDao, Integer companyId, Integer warehouseId,
				Integer itemCategoryId, String stockCode, String orderBy, String strDate) {
			this.itemBagQuantityDao = itemBagQuantityDao;
			this.companyId = companyId;
			this.warehouseId = warehouseId;
			this.asOfDate = DateUtil.parseDate(strDate);
			this.itemCategoryId = itemCategoryId;
			this.stockCode = stockCode;
			this.orderBy = orderBy;
		}

		@Override
		public void close() throws IOException {
			//Do nothing
		}

		@Override
		public Page<AvblStocksAndBagsDto> nextPage(PageSetting pageSetting) {
			return itemBagQuantityDao.getAvblBagsAndStocksRpt(companyId, null, warehouseId, null, null,
					itemCategoryId, stockCode, asOfDate, orderBy, pageSetting);
		}
	}
}
