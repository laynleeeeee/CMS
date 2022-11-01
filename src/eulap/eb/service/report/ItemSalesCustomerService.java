package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.CustomerPurchaseVolumeDto;
import eulap.eb.web.dto.ItemSalesCustomer;

/**
 * Business logic for generating Item Sales Customer report.

 *
 */
@Service
public class ItemSalesCustomerService {
	@Autowired
	private ItemDao itemDao;

	public JRDataSource generateItemSalesCustomer(int companyId,Integer divisionId, int customerId, int customerAccountId, 
			int itemCategoryId, int itemId, Date dateFrom, Date dateTo, boolean isExcludeReturns, boolean isWithAmount) {
		if (isWithAmount) {
			EBJRServiceHandler<ItemSalesCustomer> handler = new JRISCHandler(itemDao, companyId, divisionId, customerId, 
					customerAccountId, itemCategoryId, itemId, dateFrom, dateTo, isExcludeReturns, isWithAmount);
			return new EBDataSource<ItemSalesCustomer>(handler);
		} else {
			EBJRServiceHandler<CustomerPurchaseVolumeDto> handler = new JRCustomerPurchaseHandler(this, companyId, divisionId, customerId, 
					customerAccountId, itemCategoryId, itemId, dateFrom, dateTo, isExcludeReturns);
			return new EBDataSource<CustomerPurchaseVolumeDto>(handler);
		}
	}

	private static class JRISCHandler implements EBJRServiceHandler<ItemSalesCustomer> {
		private ItemDao itemDao;
		private final int companyId; 
		private final int customerId; 
		private final int customerAccountId; 
		private final int itemCategoryId; 
		private final int itemId; 
		private final Date dateFrom; 
		private final Date dateTo;
		private final boolean isExcludeReturns;
		private final boolean isWithAmount;
		private final int divisionId;

		private JRISCHandler (ItemDao itemDao, int companyId,int divisionId, int customerId, int customerAccountId, 
				int itemCategoryId, int itemId, Date dateFrom, Date dateTo, boolean isExcludeReturns, boolean isWithAmount) {
			this.itemDao = itemDao;
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.customerId = customerId;
			this.customerAccountId = customerAccountId;
			this.itemCategoryId = itemCategoryId;
			this.itemId = itemId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.isExcludeReturns = isExcludeReturns;
			this.isWithAmount = isWithAmount;
		}
		
		@Override
		public void close() throws IOException {
			itemDao = null;
		}

		@Override
		public Page<ItemSalesCustomer> nextPage(PageSetting pageSetting) {
			if (isWithAmount) {
				return itemDao.getItemSalesCustomer(companyId, divisionId, customerId, customerAccountId,
						itemCategoryId, itemId, dateFrom, dateTo, pageSetting);
			}
			return itemDao.getItemSoldByCustomer(companyId, divisionId, customerId, customerAccountId,
					itemCategoryId, itemId, dateFrom, dateTo, isExcludeReturns, null, pageSetting);
		}
	}

	private static class JRCustomerPurchaseHandler implements EBJRServiceHandler<CustomerPurchaseVolumeDto> {
		private ItemSalesCustomerService salesCustomerService;
		private final int companyId;
		private final int divisionId;
		private final int customerId;
		private final int customerAccountId;
		private final int itemCategoryId;
		private final int itemId;
		private final Date dateFrom;
		private final Date dateTo;
		private final boolean isExcludeReturns;

		private JRCustomerPurchaseHandler (ItemSalesCustomerService salesCustomerService, int companyId, int divisionId, int customerId, int customerAccountId, 
				int itemCategoryId, int itemId, Date dateFrom, Date dateTo, boolean isExcludeReturns) {
			this.salesCustomerService = salesCustomerService;
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.customerId = customerId;
			this.customerAccountId = customerAccountId;
			this.itemCategoryId = itemCategoryId;
			this.itemId = itemId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.isExcludeReturns = isExcludeReturns;
		}

		@Override
		public void close() throws IOException {
			salesCustomerService = null;
		}

		@Override
		public Page<CustomerPurchaseVolumeDto> nextPage(PageSetting pageSetting) {
			List<CustomerPurchaseVolumeDto> purchaseVolumeDtos = new ArrayList<CustomerPurchaseVolumeDto>();
			Page<ItemSalesCustomer> itemSaleCustomerResult = salesCustomerService.itemDao.getItemSoldByCustomer(companyId, divisionId, customerId,
					customerAccountId, itemCategoryId, itemId, dateFrom, dateTo, isExcludeReturns, null, pageSetting);
			CustomerPurchaseVolumeDto purchaseVolumeDto = new CustomerPurchaseVolumeDto();
			purchaseVolumeDto.setItemSalesCustomers((List<ItemSalesCustomer>) itemSaleCustomerResult.getData());
			return new Page<CustomerPurchaseVolumeDto>(pageSetting, purchaseVolumeDtos, itemSaleCustomerResult.getTotalRecords());
		}
	}
}
