package eulap.eb.service.report;

import java.io.IOException;
import java.util.Date;

import net.sf.jasperreports.engine.JRDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.RReceivingReportItemDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.ItemUnitCostHistoryPerSupplier;

/**
 * Business logic for generating item unit cost per supplier report.

 *
 */
@Service
public class ItemUnitCostHistoryPerSupplierService {
	@Autowired
	private RReceivingReportItemDao rReceivingReportItemDao;
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	
	public JRDataSource generateItemUnitCostHistoryPerSupplier(int companyId, int divisionId, int itemId, int supplierId,
			int supplierAccountId, Date dateFrom, Date dateTo) {
		EBJRServiceHandler<ItemUnitCostHistoryPerSupplier> handler = 
				new ItemUCHistoryPerSupplierHandler(this, companyId, divisionId, itemId, supplierId, supplierAccountId, dateFrom, dateTo);
		return new EBDataSource<ItemUnitCostHistoryPerSupplier>(handler);
	}
	
	private static class ItemUCHistoryPerSupplierHandler implements EBJRServiceHandler<ItemUnitCostHistoryPerSupplier> {
		private ItemUnitCostHistoryPerSupplierService service;
		private int companyId;
		private int divisionId;
		private int itemId;
		private int supplierId;
		private int supplierAccountId;
		private Date dateFrom; 
		private Date dateTo;
		
		private ItemUCHistoryPerSupplierHandler(ItemUnitCostHistoryPerSupplierService service, int companyId, int divisionId,
				int itemId, int supplierId, int supplierAccountId, Date dateFrom, Date dateTo) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.itemId = itemId;
			this.service = service;
			this.supplierId = supplierId;
			this.supplierAccountId = supplierAccountId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public void close() throws IOException {
			service = null;
		}

		@Override
		public Page<ItemUnitCostHistoryPerSupplier> nextPage(PageSetting pageSetting) {
			return service.rReceivingReportItemDao.generateItemUCHistPerSupplier(companyId, divisionId, itemId,
					supplierId, supplierAccountId, dateFrom, dateTo, pageSetting);
		}
	}

}
