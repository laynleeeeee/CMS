package eulap.eb.service.report;

import java.io.IOException;
import java.util.Date;
import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.CAPDeliveryDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.ArTransactionRegisterDto;

/**
 * Paid in advance delivery register service.

 *
 */
@Service
public class PaidInAdvanceDeliveryService {
	@Autowired
	private CAPDeliveryDao capDeliveryDao;

	/**
	 * Generate the data source of PIAD Register Report.
	 */
	public JRDataSource generatePAIDRegister(Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Date dateFrom, Date dateTo, Integer statusId) {
		EBJRServiceHandler<ArTransactionRegisterDto> handler = new PIADRegisterJRHandler(
				companyId, arCustomerId, arCustomerAccountId, dateFrom,	dateTo, statusId, capDeliveryDao);
		return new EBDataSource<ArTransactionRegisterDto>(handler);
	}

	private static class PIADRegisterJRHandler implements EBJRServiceHandler<ArTransactionRegisterDto> {
		private static Logger logger = Logger.getLogger(PIADRegisterJRHandler.class);
		private int companyId;
		private int arCustomerId;
		private int arCustomerAccountId;
		private Date dateFrom;
		private Date dateTo;
		private int statusId;
		private CAPDeliveryDao capDeliveryDao;

		private PIADRegisterJRHandler(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
				Date dateFrom, Date dateTo,	Integer statusId, CAPDeliveryDao capDeliveryDao) {
			this.companyId = companyId;
			this.arCustomerId = arCustomerId;
			this.arCustomerAccountId = arCustomerAccountId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.statusId = statusId;
			this.capDeliveryDao = capDeliveryDao;
		}

		@Override
		public void close() throws IOException {
			capDeliveryDao = null;
		}

		@Override
		public Page<ArTransactionRegisterDto> nextPage(PageSetting pageSetting) {
			Page<ArTransactionRegisterDto> capRegisterData = capDeliveryDao.generatePAIDRegister(companyId, arCustomerId,
					arCustomerAccountId, dateFrom, dateTo, statusId, pageSetting);
			if(capRegisterData.getTotalRecords() < 1) {
				logger.info("No CAP transactions found for company id: "+companyId+" for date "+
						DateUtil.formatDate(dateFrom)+" to "+DateUtil.formatDate(dateTo));
				return capRegisterData;
			}
			logger.info("Generating page "+pageSetting.getPageNumber()+" of CAP Register.");
			return capRegisterData;
		}
	}
}
