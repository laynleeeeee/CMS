package eulap.eb.service.report;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.SalesReportDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Service class that will handle business logic for retention costs report generation

 */

@Service
public class SalesReportService {
	@Autowired
	private SalesOrderDao salesOrderDao;

	/**
	 * Get the sales report data.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param salesPersonnelId The sales personnel id.
	 * @param dateFrom The invoice start date filter.
	 * @param dateTo The invoice end date filter.
	 * @param currencyId The currency id.
	 * @param pageSetting The {@link PageSetting}
	 * @return The sales report data.
	 */
	public JRDataSource generateRetentionCostRprt(Integer companyId, Integer divisionId, Integer salesPersonnelId,
			Date dateFrom, Date dateTo, Integer currencyId) {
		EBJRServiceHandler<SalesReportDto> handler = new JRRetentionCostRprtHandler(companyId,
				divisionId, salesPersonnelId, dateFrom, dateTo, currencyId, this);
		return new EBDataSource<SalesReportDto> (handler);
	}

	private static class JRRetentionCostRprtHandler implements EBJRServiceHandler<SalesReportDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer salesPersonnelId;
		private Date dateFrom;
		private Date dateTo;
		private Integer currencyId;
		private SalesReportService salesReportService;

		private JRRetentionCostRprtHandler (Integer companyId, Integer divisionId, Integer salesPersonnelId,
				Date dateFrom, Date dateTo, Integer currencyId, SalesReportService salesReportService){
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.salesPersonnelId = salesPersonnelId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.currencyId = currencyId;
			this.salesReportService = salesReportService;
		}

		@Override
		public void close() throws IOException {
			salesReportService = null;
		}

		@Override
		public Page<SalesReportDto> nextPage(PageSetting pageSetting) {
			return salesReportService.salesOrderDao.generateSalesReport(companyId, divisionId, salesPersonnelId,
					dateFrom, dateTo, currencyId, pageSetting);
		}
	}
}
