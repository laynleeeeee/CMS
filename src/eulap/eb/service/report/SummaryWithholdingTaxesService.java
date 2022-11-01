package eulap.eb.service.report;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.COCTaxDto;
import net.sf.jasperreports.engine.JRDataSource;
/**
 * Business logic for generating report for Summary of Withholding Taxes.

 */
@Service
public class SummaryWithholdingTaxesService {
	@Autowired
	private APInvoiceDao apDao;

	/**
	 * Generate the datasource of the monthly summary withholding taxes.
	 */
	public JRDataSource generateMonthlySummaryWT(Integer companyId, Integer divisionId, Integer year, Integer month, String wtTaxType) {
		EBJRServiceHandler<COCTaxDto> handler = new MonthlySummaryWTJRHandler(companyId, divisionId ,year, month, wtTaxType, this);
		return new EBDataSource<COCTaxDto>(handler);
	}

	private static class MonthlySummaryWTJRHandler implements EBJRServiceHandler<COCTaxDto> {
		private static Logger logger = Logger.getLogger(MonthlySummaryWTJRHandler.class);
		private final int companyId;
		private final int divisionId;
		private final int month;
		private final int year;
		private final String wtTaxType;
		private SummaryWithholdingTaxesService service;

		private MonthlySummaryWTJRHandler(Integer companyId, Integer divisionId, Integer year, Integer month, String wtTaxType, SummaryWithholdingTaxesService service) {
			this.companyId = companyId;
			this.month = month;
			this.year = year;
			this.service = service;
			this.wtTaxType=wtTaxType;
			this.divisionId=divisionId;
		}

		@Override
		public void close() throws IOException {
			service = null;
		}

		@Override
		public Page<COCTaxDto> nextPage(PageSetting pageSetting) {
			logger.info("Successfully retrieved report data");
			return service.apDao.getWithholdingTaxesSummary(companyId, divisionId, year, month, wtTaxType, pageSetting);
		}
	}

	/**
	 * Generate the datasource of the Quarterly summary withholding taxes.
	 */
	public JRDataSource generateQuarterlySummaryWT(Integer companyId, Integer divisionId, Integer year, Integer quarter) {
		EBJRServiceHandler<COCTaxDto> handler = new QuarterlySummaryWTJRHandler(
			companyId, divisionId, year, quarter, apDao);
		return new EBDataSource<COCTaxDto>(handler);
	}

	private static class QuarterlySummaryWTJRHandler implements EBJRServiceHandler<COCTaxDto> {
		private static Logger logger = Logger.getLogger(QuarterlySummaryWTJRHandler.class);
		private int companyId;
		private int divisionId;
		private int quarter;
		private int year;
		private APInvoiceDao apDao;

		private QuarterlySummaryWTJRHandler(Integer companyId, Integer divisionId, Integer year, Integer quarter, APInvoiceDao apDao) {
			this.companyId = companyId;
			this.quarter = quarter;
			this.year = year;
			this.apDao = apDao;
			this.divisionId=divisionId;
		}

		@Override
		public void close() throws IOException {
			apDao = null;
		}

		@Override
		public Page<COCTaxDto> nextPage(PageSetting pageSetting) {
			Page<COCTaxDto> quarterlyData = apDao.getQuarterlySummaryWT(companyId, divisionId, year, quarter, pageSetting);
			logger.info("Generating page "+pageSetting.getPageNumber()+" of Quarterly Summary.");
			return quarterlyData;
		}
	}
}
