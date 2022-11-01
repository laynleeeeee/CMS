package eulap.eb.service.report;

import java.io.IOException;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.ApInvoiceAgingDto;

/**
 * Business logic for generating report for the invoice aging.


 */
@Service
public class ApInvoiceAgingService{
	private final Logger logger = Logger.getLogger(ApInvoiceAgingService.class);
	@Autowired
	private APInvoiceDao invoiceDao;

	/**
	 * Generate Jasper Report for Invoice Aging.
	 * @param param The invoice aging parameter.
	 * @return Generated Jasper Report for Invoice Aging.
	 */
	public JRDataSource generateInvoiceAging(ApInvoiceAgingParam param) {
		EBJRServiceHandler<ApInvoiceAgingDto> handler = new JRInvoiceAgingHandler(param, this);
		return new EBDataSource<ApInvoiceAgingDto>(handler);
	}

	private static class JRInvoiceAgingHandler implements EBJRServiceHandler<ApInvoiceAgingDto> {
		private ApInvoiceAgingParam param;
		private ApInvoiceAgingService agingServiceImpl;
		private JRInvoiceAgingHandler (ApInvoiceAgingParam param,
				ApInvoiceAgingService agingServiceImpl){
			this.param = param;
			this.agingServiceImpl = agingServiceImpl;
		}

		@Override
		public void close() throws IOException {
			agingServiceImpl = null;
		}

		@Override
		public Page<ApInvoiceAgingDto> nextPage(PageSetting pageSetting) {
			agingServiceImpl.logger.info("Generating the AP Invoice Aging Report on page "
					+pageSetting.getPageNumber());
			Page<ApInvoiceAgingDto> invoices = agingServiceImpl.invoiceDao.searchInvoiceAging(param, pageSetting);
			return invoices;
		}
	}
}
